package net.ooder.nexus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import java.io.File;

/**
 * 静态资源配置类
 * 
 * 支持以下资源路径：
 * - /console/** -> classpath:/static/console/
 * - /console/skills/** -> file:./skills/ (Nexus-UI Skill 静态资源)
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/console/**")
                .addResourceLocations("classpath:/static/console/")
                .setCachePeriod(0);
        
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/console/css/")
                .setCachePeriod(0);
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/console/js/")
                .setCachePeriod(0);
        
        String skillsLocation = new File(skillsPath).toURI().toString();
        registry.addResourceHandler("/console/skills/**")
                .addResourceLocations(skillsLocation)
                .setCachePeriod(0);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/console/")
                .setViewName("forward:/console/index.html");
        
        registry.addViewController("/")
                .setViewName("forward:/console/index.html");
        
        registry.addViewController("/index.html")
                .setViewName("forward:/console/index.html");
    }
}
