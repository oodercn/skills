package net.ooder.skill.scenes.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "net.ooder.scene.group.template",
    "net.ooder.skill.scenes"
})
public class TemplateManagementConfig {
}
