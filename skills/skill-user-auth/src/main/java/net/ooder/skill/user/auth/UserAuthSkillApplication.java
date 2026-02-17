package net.ooder.skill.user.auth;

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
        scanBasePackages = "net.ooder.skill.user.auth",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@RestController
@RequestMapping("/api")
public class UserAuthSkillApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserAuthSkillApplication.class);

    private OoderSDK sdk;
    private EndAgent endAgent;

    public static void main(String[] args) {
        System.setProperty("server.port", "8084");
        SpringApplication.run(UserAuthSkillApplication.class, args);
    }

    @Bean
    public SDKConfiguration sdkConfiguration() {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("skill-user-auth-001");
        config.setAgentName("User Auth Skill");
        config.setAgentType("skill");
        config.setEndpoint("http://localhost:8084");
        config.setUdpPort(9084);
        config.setHeartbeatInterval(30000);
        config.setSkillRootPath(System.getProperty("java.io.tmpdir") + "/ooder-skill-user-auth-" + System.currentTimeMillis());
        return config;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("User Auth Skill Starting with SDK 0.7.1...");

        LifecycleManager.getInstance().reset();

        sdk = OoderSDK.builder()
            .configuration(sdkConfiguration())
            .build();
        sdk.initialize();
        sdk.start();

        endAgent = sdk.createEndAgent();
        endAgent.start();

        log.info("User Auth Skill started successfully");
        log.info("Skill ID: {}", endAgent.getAgentId());
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("skillId", "skill-user-auth");
        info.put("name", "User Authentication Service");
        info.put("version", "0.7.1");
        info.put("description", "用户认证服务");
        info.put("sceneId", "auth");
        info.put("capabilities", new String[]{"user-auth", "token-validate", "session-manage"});
        return info;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> request) {
        log.info("User login: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("token", "token-" + System.currentTimeMillis());
        result.put("userId", "user-001");
        return result;
    }

    @PostMapping("/validate")
    public Map<String, Object> validateToken(@RequestBody Map<String, Object> request) {
        log.info("Validating token: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("valid", true);
        result.put("userId", "user-001");
        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, Object> request) {
        log.info("User logout: {}", request);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<String, Object>();
        health.put("status", "UP");
        health.put("skillId", "skill-user-auth");
        if (endAgent != null) {
            health.put("agentHealthy", endAgent.isHealthy());
        }
        return health;
    }
}
