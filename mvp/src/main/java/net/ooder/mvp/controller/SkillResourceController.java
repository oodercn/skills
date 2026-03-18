package net.ooder.mvp.controller;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/console/skills")
public class SkillResourceController {

    private static final Logger log = LoggerFactory.getLogger(SkillResourceController.class);

    @Autowired
    private PluginManager pluginManager;

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();
    static {
        CONTENT_TYPES.put("html", "text/html;charset=UTF-8");
        CONTENT_TYPES.put("css", "text/css;charset=UTF-8");
        CONTENT_TYPES.put("js", "application/javascript;charset=UTF-8");
        CONTENT_TYPES.put("json", "application/json;charset=UTF-8");
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("svg", "image/svg+xml");
        CONTENT_TYPES.put("ico", "image/x-icon");
        CONTENT_TYPES.put("woff", "font/woff");
        CONTENT_TYPES.put("woff2", "font/woff2");
        CONTENT_TYPES.put("ttf", "font/ttf");
        CONTENT_TYPES.put("eot", "application/vnd.ms-fontobject");
    }

    private Properties registryProps;

    @GetMapping("/{skillId}/**")
    public ResponseEntity<byte[]> getSkillResource(
            @PathVariable String skillId,
            HttpServletRequest request) {
        
        String requestURI = request.getRequestURI();
        String prefix = "/console/skills/" + skillId + "/";
        String resourcePath = requestURI.substring(prefix.length());
        
        if (resourcePath.isEmpty()) {
            resourcePath = "index.html";
        }
        
        log.debug("Request for skill resource: skillId={}, path={}", skillId, resourcePath);
        
        return getResource(skillId, "static/console/" + resourcePath);
    }

    private ResponseEntity<byte[]> getResource(String skillId, String resourcePath) {
        log.debug("Loading resource: {}/{}", skillId, resourcePath);

        try {
            PluginClassLoader classLoader = pluginManager.getClassLoader(skillId);
            if (classLoader != null) {
                URL resourceUrl = classLoader.getResource(resourcePath);
                if (resourceUrl != null) {
                    try (InputStream is = resourceUrl.openStream()) {
                        return readResource(resourcePath, is);
                    }
                }
            }

            ResponseEntity<byte[]> sourceResource = loadFromSourceDirectory(skillId, resourcePath);
            if (sourceResource != null) {
                return sourceResource;
            }

            log.warn("Resource not found: {}/{}", skillId, resourcePath);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Failed to load resource: {}/{}", skillId, resourcePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<byte[]> loadFromSourceDirectory(String skillId, String resourcePath) {
        if (registryProps == null) {
            loadRegistry();
        }
        
        if (registryProps == null) {
            return null;
        }
        
        String skillPath = registryProps.getProperty(skillId + ".path");
        if (skillPath == null) {
            log.debug("Skill path not found in registry: {}", skillId);
            return null;
        }
        
        File resourceFile = new File(skillPath, "src/main/resources/" + resourcePath);
        if (!resourceFile.exists()) {
            log.debug("Resource file not found: {}", resourceFile.getAbsolutePath());
            return null;
        }
        
        try (FileInputStream fis = new FileInputStream(resourceFile)) {
            log.debug("Loading resource from source: {}", resourceFile.getAbsolutePath());
            return readResource(resourcePath, fis);
        } catch (Exception e) {
            log.error("Failed to load resource from source: {}", resourceFile.getAbsolutePath(), e);
            return null;
        }
    }

    private void loadRegistry() {
        File registryFile = new File("data/installed-skills/registry.properties");
        if (!registryFile.exists()) {
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(registryFile)) {
            registryProps = new Properties();
            registryProps.load(fis);
            log.debug("Loaded registry with {} entries", registryProps.size());
        } catch (Exception e) {
            log.error("Failed to load registry", e);
        }
    }

    private ResponseEntity<byte[]> readResource(String resourcePath, InputStream is) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] content = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        String contentType = getContentType(resourcePath);
        headers.setContentType(MediaType.parseMediaType(contentType));

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    private String getContentType(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            String ext = path.substring(dotIndex + 1).toLowerCase();
            String contentType = CONTENT_TYPES.get(ext);
            if (contentType != null) {
                return contentType;
            }
        }
        return "application/octet-stream";
    }
}
