package net.ooder.mvp.skill.scene.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SceneTemplateLoader {

    private static final Logger log = LoggerFactory.getLogger(SceneTemplateLoader.class);

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    private final Map<String, SceneTemplate> templates = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadTemplates() {
        log.info("[loadTemplates] Loading scene templates from YAML files...");
        
        try {
            Resource[] resources = resolver.getResources("classpath:templates/*.yaml");
            log.info("[loadTemplates] Found {} template files", resources.length);
            
            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    SceneTemplate template = yamlMapper.readValue(is, SceneTemplate.class);
                    if (template != null && template.getId() != null) {
                        templates.put(template.getId(), template);
                        log.info("[loadTemplates] Loaded template: {} - {}", template.getId(), template.getName());
                    }
                } catch (IOException e) {
                    log.error("[loadTemplates] Failed to load template from: {}", resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            log.warn("[loadTemplates] No templates directory found or error scanning: {}", e.getMessage());
        }
    }

    public List<SceneTemplate> getAllTemplates() {
        return new ArrayList<>(templates.values());
    }

    public SceneTemplate getTemplate(String templateId) {
        return templates.get(templateId);
    }

    public void registerTemplate(SceneTemplate template) {
        if (template != null && template.getId() != null) {
            templates.put(template.getId(), template);
            log.info("[registerTemplate] Registered template: {}", template.getId());
        }
    }

    public void unregisterTemplate(String templateId) {
        templates.remove(templateId);
        log.info("[unregisterTemplate] Unregistered template: {}", templateId);
    }

    public int getTemplateCount() {
        return templates.size();
    }
}
