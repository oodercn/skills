package net.ooder.skill.traesolo;

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

@SpringBootApplication(
        scanBasePackages = "net.ooder.skill.traesolo",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@RestController
@RequestMapping("/api")
public class TraeSoloSkillApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TraeSoloSkillApplication.class);

    private OoderSDK sdk;
    private EndAgent endAgent;

    public static void main(String[] args) {
        System.setProperty("server.port", "8085");
        SpringApplication.run(TraeSoloSkillApplication.class, args);
    }

    @Bean
    public SDKConfiguration sdkConfiguration() {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("skill-trae-solo-001");
        config.setAgentName("Trae Solo Skill");
        config.setAgentType("skill");
        config.setEndpoint("http://localhost:8085");
        config.setUdpPort(9085);
        config.setHeartbeatInterval(30000);
        config.setSkillRootPath(System.getProperty("java.io.tmpdir") + "/ooder-skill-trae-solo-" + System.currentTimeMillis());
        return config;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Trae Solo Skill Starting with SDK 0.7.1...");

        LifecycleManager.getInstance().reset();

        sdk = OoderSDK.builder()
            .configuration(sdkConfiguration())
            .build();
        sdk.initialize();
        sdk.start();

        endAgent = sdk.createEndAgent();
        endAgent.start();

        log.info("Trae Solo Skill started successfully");
        log.info("Skill ID: {}", endAgent.getAgentId());
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("skillId", "skill-trae-solo");
        info.put("name", "Trae Solo Service");
        info.put("version", "0.7.1");
        info.put("description", "连接实用功能，运行结果通过A2UI节点展示");
        info.put("sceneId", "utility");
        info.put("capabilities", new String[]{"execute-task", "get-info"});
        return info;
    }

    @PostMapping("/execute")
    public Map<String, Object> executeTask(@RequestBody Map<String, Object> request) {
        log.info("Executing task: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("taskId", "task-" + System.currentTimeMillis());
        result.put("status", "completed");
        result.put("result", "Task executed successfully");
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<String, Object>();
        health.put("status", "UP");
        health.put("skillId", "skill-trae-solo");
        if (endAgent != null) {
            health.put("agentHealthy", endAgent.isHealthy());
        }
        return health;
    }
}
