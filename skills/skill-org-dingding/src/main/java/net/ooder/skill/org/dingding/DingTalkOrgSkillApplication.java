package net.ooder.skill.org.dingding;

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
        scanBasePackages = "net.ooder.skill.org.dingding",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@RestController
@RequestMapping("/api")
public class DingTalkOrgSkillApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DingTalkOrgSkillApplication.class);

    private OoderSDK sdk;
    private EndAgent endAgent;

    public static void main(String[] args) {
        System.setProperty("server.port", "8082");
        SpringApplication.run(DingTalkOrgSkillApplication.class, args);
    }

    @Bean
    public SDKConfiguration sdkConfiguration() {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("skill-org-dingding-001");
        config.setAgentName("DingTalk Organization Skill");
        config.setAgentType("skill");
        config.setEndpoint("http://localhost:8082");
        config.setUdpPort(9082);
        config.setHeartbeatInterval(30000);
        config.setSkillRootPath(System.getProperty("java.io.tmpdir") + "/ooder-skill-dingding-" + System.currentTimeMillis());
        return config;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("DingTalk Organization Skill Starting with SDK 0.7.1...");

        LifecycleManager.getInstance().reset();

        sdk = OoderSDK.builder()
            .configuration(sdkConfiguration())
            .build();
        sdk.initialize();
        sdk.start();

        endAgent = sdk.createEndAgent();
        endAgent.start();

        log.info("DingTalk Organization Skill started successfully");
        log.info("Skill ID: {}", endAgent.getAgentId());
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("skillId", "skill-org-dingding");
        info.put("name", "DingTalk Organization Service");
        info.put("version", "0.7.1");
        info.put("description", "钉钉组织数据集成服务");
        info.put("sceneId", "auth");
        info.put("capabilities", new String[]{"org-data-read", "user-auth"});
        return info;
    }

    @GetMapping("/org/tree")
    public Map<String, Object> getOrgTree() {
        log.info("Getting organization tree");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("data", new HashMap<String, Object>());
        return result;
    }

    @PostMapping("/auth/verify")
    public Map<String, Object> verifyAuth(@RequestBody Map<String, Object> request) {
        log.info("Verifying auth: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("userId", "user-" + System.currentTimeMillis());
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<String, Object>();
        health.put("status", "UP");
        health.put("skillId", "skill-org-dingding");
        if (endAgent != null) {
            health.put("agentHealthy", endAgent.isHealthy());
        }
        return health;
    }
}
