package net.ooder.skill.a2ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.ooder.sdk.api.OoderSDK;
import net.ooder.sdk.api.agent.EndAgent;
import net.ooder.sdk.infra.config.SDKConfiguration;
import net.ooder.sdk.infra.lifecycle.LifecycleManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(
        scanBasePackages = "net.ooder.skill.a2ui",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@RestController
@RequestMapping("/api")
public class A2UISkillApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(A2UISkillApplication.class);

    private OoderSDK sdk;
    private EndAgent endAgent;

    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        SpringApplication.run(A2UISkillApplication.class, args);
    }

    @Bean
    public SDKConfiguration sdkConfiguration() {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("skill-a2ui-001");
        config.setAgentName("A2UI Skill");
        config.setAgentType("skill");
        config.setEndpoint("http://localhost:8081");
        config.setUdpPort(9081);
        config.setHeartbeatInterval(30000);
        config.setSkillRootPath(System.getProperty("java.io.tmpdir") + "/ooder-skill-a2ui-" + System.currentTimeMillis());
        return config;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("A2UI Skill Starting with SDK 0.7.1...");

        LifecycleManager.getInstance().reset();

        sdk = OoderSDK.builder()
            .configuration(sdkConfiguration())
            .build();
        sdk.initialize();
        sdk.start();

        endAgent = sdk.createEndAgent();
        endAgent.start();

        log.info("A2UI Skill started successfully");
        log.info("Skill ID: {}", endAgent.getAgentId());
        log.info("Skill Name: {}", endAgent.getAgentName());
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("skillId", "skill-a2ui");
        info.put("name", "A2UI Skill");
        info.put("version", "0.7.1");
        info.put("description", "A2UI图转代码技能");
        info.put("sceneId", "ui-generation");
        info.put("capabilities", new String[]{"generate-ui", "preview-ui", "create-view"});
        return info;
    }

    @PostMapping("/generate")
    public Map<String, Object> generateUI(@RequestBody Map<String, Object> request) {
        log.info("Generating UI: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("message", "UI generated successfully");
        result.put("data", request);
        return result;
    }

    @PostMapping("/preview")
    public Map<String, Object> previewUI(@RequestBody Map<String, Object> request) {
        log.info("Previewing UI: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("previewUrl", "/preview/" + System.currentTimeMillis());
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<String, Object>();
        health.put("status", "UP");
        health.put("skillId", "skill-a2ui");
        if (endAgent != null) {
            health.put("agentHealthy", endAgent.isHealthy());
        }
        return health;
    }
}
