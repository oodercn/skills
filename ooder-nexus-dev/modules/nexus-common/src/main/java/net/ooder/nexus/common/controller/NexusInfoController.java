package net.ooder.nexus.common.controller;

import net.ooder.nexus.common.config.NexusProperties;
import net.ooder.nexus.protocol.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nexus")
public class NexusInfoController {
    
    @Autowired
    private NexusProperties nexusProperties;
    
    @Value("${spring.application.name:nexus}")
    private String applicationName;
    
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", applicationName);
        info.put("product", nexusProperties.getProduct());
        info.put("features", buildFeatures());
        info.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(info);
    }
    
    @GetMapping("/features")
    public ApiResponse<Map<String, Boolean>> getFeatures() {
        return ApiResponse.success(buildFeatures());
    }
    
    private Map<String, Boolean> buildFeatures() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("p2p", nexusProperties.getP2p().isEnabled());
        features.put("push", nexusProperties.getPush().isEnabled());
        features.put("llm", nexusProperties.getLlm().isEnabled());
        features.put("skill.dynamic", nexusProperties.getSkill().isDynamic());
        features.put("skill.k8s", nexusProperties.getSkill().getK8s().isEnabled());
        features.put("skill.hosting", nexusProperties.getSkill().getHosting().isEnabled());
        return features;
    }
    
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("product", nexusProperties.getProduct().getName());
        health.put("version", nexusProperties.getProduct().getVersion());
        health.put("uptime", System.currentTimeMillis());
        return ApiResponse.success(health);
    }
}
