package net.ooder.skill.cli.config;

import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.api.ExtensionRegistry;
import net.ooder.skill.cli.driver.ConfigDriver;
import net.ooder.skill.cli.driver.SceneDriver;
import net.ooder.skill.cli.driver.SkillDriver;
import net.ooder.skill.cli.driver.TaskDriver;
import net.ooder.skill.cli.driver.impl.MockConfigDriver;
import net.ooder.skill.cli.driver.impl.MockSceneDriver;
import net.ooder.skill.cli.driver.impl.MockSkillDriver;
import net.ooder.skill.cli.driver.impl.MockTaskDriver;
import net.ooder.skill.cli.driver.impl.RealConfigDriver;
import net.ooder.skill.cli.driver.impl.RealSceneDriver;
import net.ooder.skill.cli.driver.impl.RealSkillDriver;
import net.ooder.skill.cli.driver.impl.RealTaskDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(CliProperties.class)
public class CliAutoConfiguration {
    
    @Autowired(required = false)
    private CliRouter cliRouter;
    
    @Autowired(required = false)
    private ExtensionRegistry extensionRegistry;
    
    @Bean
    @ConditionalOnMissingBean
    public CliProperties cliProperties() {
        return new CliProperties();
    }
    
    @Bean
    @ConditionalOnBean(CliRouter.class)
    @ConditionalOnProperty(name = "skill.cli.use-real-driver", havingValue = "true", matchIfMissing = true)
    public SkillDriver realSkillDriver() {
        return new RealSkillDriver(cliRouter, extensionRegistry);
    }
    
    @Bean
    @ConditionalOnMissingBean(SkillDriver.class)
    public SkillDriver mockSkillDriver() {
        return new MockSkillDriver();
    }
    
    @Bean
    @ConditionalOnBean(CliRouter.class)
    @ConditionalOnProperty(name = "skill.cli.use-real-driver", havingValue = "true", matchIfMissing = true)
    public SceneDriver realSceneDriver() {
        return new RealSceneDriver(cliRouter);
    }
    
    @Bean
    @ConditionalOnMissingBean(SceneDriver.class)
    public SceneDriver mockSceneDriver() {
        return new MockSceneDriver();
    }
    
    @Bean
    @ConditionalOnBean(CliRouter.class)
    @ConditionalOnProperty(name = "skill.cli.use-real-driver", havingValue = "true", matchIfMissing = true)
    public TaskDriver realTaskDriver() {
        return new RealTaskDriver(cliRouter);
    }
    
    @Bean
    @ConditionalOnMissingBean(TaskDriver.class)
    public TaskDriver mockTaskDriver() {
        return new MockTaskDriver();
    }
    
    @Bean
    @ConditionalOnBean(CliRouter.class)
    @ConditionalOnProperty(name = "skill.cli.use-real-driver", havingValue = "true", matchIfMissing = true)
    public ConfigDriver realConfigDriver() {
        return new RealConfigDriver(cliRouter);
    }
    
    @Bean
    @ConditionalOnMissingBean(ConfigDriver.class)
    public ConfigDriver mockConfigDriver() {
        return new MockConfigDriver();
    }
}
