package net.ooder.mvp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

import net.ooder.mvp.interceptor.SetupInterceptor;

import java.io.File;
import java.util.Properties;
import java.io.FileInputStream;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private SetupInterceptor setupInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(setupInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/setup/**",
                    "/api/v1/setup/**",
                    "/api/v1/plugin/**",
                    "/actuator/**",
                    "/error",
                    "/favicon.svg",
                    "/console/**",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/console/**")
                .addResourceLocations("classpath:/static/console/");
        registry.addResourceHandler("/setup/**")
                .addResourceLocations("classpath:/static/setup/");
        
        addInstalledSkillResourceHandlers(registry);
    }
    
    private void addInstalledSkillResourceHandlers(ResourceHandlerRegistry registry) {
        File registryFile = new File("data/installed-skills/registry.properties");
        if (!registryFile.exists()) {
            return;
        }
        
        try {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(registryFile)) {
                props.load(fis);
            }
            
            java.util.Set<String> skillIds = new java.util.HashSet<>();
            for (String key : props.stringPropertyNames()) {
                int dotIndex = key.indexOf('.');
                if (dotIndex > 0) {
                    skillIds.add(key.substring(0, dotIndex));
                }
            }
            
            for (String skillId : skillIds) {
                String skillPath = props.getProperty(skillId + ".path");
                if (skillPath != null) {
                    File skillDir = new File(skillPath);
                    if (skillDir.exists()) {
                        File staticDir = new File(skillDir, "src/main/resources/static");
                        if (staticDir.exists()) {
                            File consoleDir = new File(staticDir, "console");
                            if (consoleDir.exists()) {
                                registry.addResourceHandler("/console/skills/" + skillId + "/**")
                                        .addResourceLocations("file:" + consoleDir.getAbsolutePath() + "/");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("[MvcConfig] Failed to load skill resource handlers: " + e.getMessage());
        }
    }
    
    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addRedirectViewController("/console", "/console/index.html");
        registry.addRedirectViewController("/console/", "/console/index.html");
    }
}
