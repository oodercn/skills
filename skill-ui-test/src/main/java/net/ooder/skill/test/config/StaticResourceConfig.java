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
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/console/")
                .setViewName("forward:/console/index.html");
        
        registry.addViewController("/")
                .setViewName("forward:/console/index.html");
    }
}
