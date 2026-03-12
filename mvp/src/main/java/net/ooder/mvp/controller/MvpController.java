package net.ooder.mvp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mvp")
public class MvpController {

    @Value("${spring.application.name:mvp-core}")
    private String applicationName;

    @Value("${spring.profiles.active:micro}")
    private String activeProfile;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("profile", activeProfile);
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", applicationName);
        info.put("version", "2.3.1");
        info.put("profile", activeProfile);
        
        Map<String, String> capabilities = new HashMap<>();
        capabilities.put("auth", "/api/v1/auth");
        capabilities.put("org", "/api/v1/org");
        capabilities.put("config", "/api/v1/system");
        capabilities.put("capability", "/api/v1/capabilities");
        info.put("capabilities", capabilities);
        
        return info;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", activeProfile);
        
        Map<String, Boolean> features = new HashMap<>();
        features.put("auth", true);
        features.put("org", true);
        features.put("config", true);
        features.put("capability", true);
        features.put("llm", "small".equals(activeProfile) || "large".equals(activeProfile) || "enterprise".equals(activeProfile));
        features.put("knowledge", "large".equals(activeProfile) || "enterprise".equals(activeProfile));
        features.put("audit", "large".equals(activeProfile) || "enterprise".equals(activeProfile));
        profile.put("features", features);
        
        return profile;
    }
}
