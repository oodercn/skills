package net.ooder.mvp;

import net.ooder.mvp.skill.scene.config.GiteeDiscoveryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableConfigurationProperties(GiteeDiscoveryProperties.class)
@ComponentScan(
    basePackages = {
        "net.ooder.mvp",
        "net.ooder.mvp.skill.scene",
        "net.ooder.skill.common",
        "net.ooder.skill.capability",
        "net.ooder.skill.llm"
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.skill\\.capability\\.controller\\.CapabilityStatsController"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.mvp\\.skill\\.scene\\.SceneSkillApplication"
        )
    }
)
public class MvpCoreApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MvpCoreApplication.class, args);
    }
}
