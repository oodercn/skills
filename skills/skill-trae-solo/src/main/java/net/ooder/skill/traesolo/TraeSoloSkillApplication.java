package net.ooder.skill.traesolo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication(
        scanBasePackages = "net.ooder.skill.traesolo",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@RestController
@RequestMapping("/api")
public class TraeSoloSkillApplication {

    private static final Logger log = LoggerFactory.getLogger(TraeSoloSkillApplication.class);

    public static void main(String[] args) {
        System.setProperty("server.port", "8085");
        SpringApplication.run(TraeSoloSkillApplication.class, args);
        log.info("Trae Solo Skill started successfully on port 8085");
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<String, Object>();
        info.put("skillId", "skill-trae-solo");
        info.put("name", "Trae Solo Service");
        info.put("version", "0.7.3");
        info.put("description", "项目管理与协作工具集");
        info.put("sceneId", "project-management");
        info.put("capabilities", new String[]{
            "project-navigation",
            "task-management",
            "notification"
        });
        return info;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new LinkedHashMap<String, Object>();
        health.put("status", "UP");
        health.put("skillId", "skill-trae-solo");
        return health;
    }
}
