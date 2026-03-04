package net.ooder.skill.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/console/**")
                .addResourceLocations("classpath:/static/console/")
                .setCachePeriod(0);
        
        String skillsLocation = new File(skillsPath).toURI().toString();
        registry.addResourceHandler("/console/skills/**")
                .addResourceLocations(skillsLocation)
                .setCachePeriod(0);
        
        registry.addResourceHandler("/skills/**")
                .addResourceLocations(skillsLocation)
                .setCachePeriod(0);
        
        registry.addResourceHandler("/console/skill-*/**")
                .addResourceLocations(skillsLocation)
                .setCachePeriod(0);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/console/")
                .setViewName("forward:/console/index.html");
        
        registry.addViewController("/")
                .setViewName("forward:/console/index.html");
        
        registry.addViewController("/console/skill-knowledge-qa/")
                .setViewName("forward:/console/skills/skill-knowledge-qa/ui/pages/index.html");
        
        registry.addViewController("/console/skill-knowledge-qa")
                .setViewName("forward:/console/skills/skill-knowledge-qa/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-chat/")
                .setViewName("forward:/console/skills/skill-llm-chat/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-chat")
                .setViewName("forward:/console/skills/skill-llm-chat/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-baidu/")
                .setViewName("forward:/console/skills/skill-llm-baidu/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-baidu")
                .setViewName("forward:/console/skills/skill-llm-baidu/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-deepseek/")
                .setViewName("forward:/console/skills/skill-llm-deepseek/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-deepseek")
                .setViewName("forward:/console/skills/skill-llm-deepseek/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-management/")
                .setViewName("forward:/console/skills/skill-llm-management-ui/ui/pages/index.html");
        
        registry.addViewController("/console/skill-llm-management")
                .setViewName("forward:/console/skills/skill-llm-management-ui/ui/pages/index.html");
    }
}
