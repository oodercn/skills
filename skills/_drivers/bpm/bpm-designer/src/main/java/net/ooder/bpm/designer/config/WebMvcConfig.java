package net.ooder.bpm.designer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/static/designer/index.html");
        registry.addViewController("/designer").setViewName("forward:/static/designer/index.html");
        registry.addViewController("/designer/").setViewName("forward:/static/designer/index.html");
    }
}
