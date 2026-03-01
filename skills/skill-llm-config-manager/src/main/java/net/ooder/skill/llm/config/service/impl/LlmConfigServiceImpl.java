package net.ooder.skill.llm.config.service.impl;

import net.ooder.skill.llm.config.model.LlmConfig;
import net.ooder.skill.llm.config.model.ResolvedConfig;
import net.ooder.skill.llm.config.service.LlmConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LlmConfigServiceImpl implements LlmConfigService {
    
    private static final Logger log = LoggerFactory.getLogger(LlmConfigServiceImpl.class);
    
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_KEY = "ooder-llm-config-key-32byte";
    
    private final Map<String, LlmConfig> configStore = new ConcurrentHashMap<>();
    private SecretKeySpec secretKey;
    
    @PostConstruct
    public void init() {
        byte[] keyBytes = Arrays.copyOf(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), 32);
        secretKey = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
        
        initDefaultConfigs();
        log.info("LlmConfigService initialized with {} configs", configStore.size());
    }
    
    private void initDefaultConfigs() {
        LlmConfig enterpriseConfig = new LlmConfig();
        enterpriseConfig.setId("enterprise-default");
        enterpriseConfig.setLevel(LlmConfig.ConfigLevel.ENTERPRISE);
        enterpriseConfig.setScopeId("default");
        enterpriseConfig.setProviderType("qianwen");
        enterpriseConfig.setModel("qwen-plus");
        enterpriseConfig.setEnabled(true);
        enterpriseConfig.setCreatedAt(System.currentTimeMillis());
        enterpriseConfig.setCreatedBy("system");
        
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("max_tokens", 2048);
        enterpriseConfig.setOptions(options);
        
        configStore.put(enterpriseConfig.getId(), enterpriseConfig);
    }
    
    @Override
    public LlmConfig createConfig(LlmConfig config) {
        if (config.getId() == null || config.getId().isEmpty()) {
            config.setId(UUID.randomUUID().toString());
        }
        
        config.setCreatedAt(System.currentTimeMillis());
        config.setUpdatedAt(System.currentTimeMillis());
        
        if (config.getProviderConfig() != null && config.getProviderConfig().containsKey("apiKey")) {
            String apiKey = (String) config.getProviderConfig().get("apiKey");
            if (apiKey != null && !apiKey.startsWith("enc:")) {
                config.getProviderConfig().put("apiKey", encryptApiKey(apiKey));
            }
        }
        
        configStore.put(config.getId(), config);
        log.info("Created LLM config: id={}, level={}, provider={}", 
            config.getId(), config.getLevel(), config.getProviderType());
        
        return config;
    }
    
    @Override
    public LlmConfig updateConfig(String id, LlmConfig config) {
        LlmConfig existing = configStore.get(id);
        if (existing == null) {
            throw new RuntimeException("Config not found: " + id);
        }
        
        config.setId(id);
        config.setCreatedAt(existing.getCreatedAt());
        config.setUpdatedAt(System.currentTimeMillis());
        
        if (config.getProviderConfig() != null && config.getProviderConfig().containsKey("apiKey")) {
            String apiKey = (String) config.getProviderConfig().get("apiKey");
            if (apiKey != null && !apiKey.startsWith("enc:")) {
                config.getProviderConfig().put("apiKey", encryptApiKey(apiKey));
            }
        }
        
        configStore.put(id, config);
        log.info("Updated LLM config: id={}", id);
        
        return config;
    }
    
    @Override
    public void deleteConfig(String id) {
        LlmConfig removed = configStore.remove(id);
        if (removed != null) {
            log.info("Deleted LLM config: id={}", id);
        }
    }
    
    @Override
    public LlmConfig getConfig(String id) {
        return configStore.get(id);
    }
    
    @Override
    public LlmConfig getConfigByLevelAndScope(LlmConfig.ConfigLevel level, String scopeId) {
        return configStore.values().stream()
            .filter(c -> c.getLevel() == level && Objects.equals(c.getScopeId(), scopeId))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public List<LlmConfig> getConfigsByLevel(LlmConfig.ConfigLevel level) {
        return configStore.values().stream()
            .filter(c -> c.getLevel() == level)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<LlmConfig> getAllConfigs() {
        return new ArrayList<>(configStore.values());
    }
    
    @Override
    public ResolvedConfig resolveConfig(String userId, String sceneId, String departmentId) {
        return resolveConfigWithPriority(userId, sceneId, departmentId, null);
    }
    
    @Override
    public ResolvedConfig resolveConfigWithPriority(String userId, String sceneId, String departmentId, Map<String, Object> context) {
        List<LlmConfig> candidates = new ArrayList<>();
        
        if (userId != null) {
            LlmConfig personal = getConfigByLevelAndScope(LlmConfig.ConfigLevel.PERSONAL, userId);
            if (personal != null && personal.isEnabled()) {
                candidates.add(personal);
            }
        }
        
        if (sceneId != null) {
            LlmConfig scene = getConfigByLevelAndScope(LlmConfig.ConfigLevel.SCENE, sceneId);
            if (scene != null && scene.isEnabled()) {
                candidates.add(scene);
            }
        }
        
        if (departmentId != null) {
            LlmConfig dept = getConfigByLevelAndScope(LlmConfig.ConfigLevel.DEPARTMENT, departmentId);
            if (dept != null && dept.isEnabled()) {
                candidates.add(dept);
            }
        }
        
        LlmConfig enterprise = getConfigByLevelAndScope(LlmConfig.ConfigLevel.ENTERPRISE, "default");
        if (enterprise != null && enterprise.isEnabled()) {
            candidates.add(enterprise);
        }
        
        if (candidates.isEmpty()) {
            log.warn("No LLM config found, using defaults");
            return createDefaultResolvedConfig();
        }
        
        candidates.sort((a, b) -> Integer.compare(
            a.getLevel().ordinal(),
            b.getLevel().ordinal()
        ));
        
        LlmConfig selected = candidates.get(0);
        return convertToResolvedConfig(selected);
    }
    
    private ResolvedConfig convertToResolvedConfig(LlmConfig config) {
        ResolvedConfig resolved = new ResolvedConfig();
        resolved.setProviderType(config.getProviderType());
        resolved.setModel(config.getModel());
        resolved.setOptions(config.getOptions());
        resolved.setPriority(config.getLevel().ordinal());
        resolved.setSource(new ResolvedConfig.ConfigSource(config.getLevel(), config.getScopeId()));
        
        if (config.getProviderConfig() != null) {
            Map<String, Object> providerConfig = new HashMap<>(config.getProviderConfig());
            
            if (providerConfig.containsKey("apiKey")) {
                String encryptedKey = (String) providerConfig.get("apiKey");
                if (encryptedKey != null && encryptedKey.startsWith("enc:")) {
                    resolved.setApiKey(decryptApiKey(encryptedKey));
                    providerConfig.remove("apiKey");
                }
            }
            
            if (providerConfig.containsKey("baseUrl")) {
                resolved.setBaseUrl((String) providerConfig.get("baseUrl"));
            }
            
            resolved.setProviderConfig(providerConfig);
        }
        
        return resolved;
    }
    
    private ResolvedConfig createDefaultResolvedConfig() {
        ResolvedConfig resolved = new ResolvedConfig();
        resolved.setProviderType("qianwen");
        resolved.setModel("qwen-plus");
        resolved.setPriority(999);
        resolved.setSource(new ResolvedConfig.ConfigSource(LlmConfig.ConfigLevel.ENTERPRISE, "default"));
        
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("max_tokens", 2048);
        resolved.setOptions(options);
        
        return resolved;
    }
    
    @Override
    public String encryptApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return apiKey;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(apiKey.getBytes(StandardCharsets.UTF_8));
            return "enc:" + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Failed to encrypt API key", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    @Override
    public String decryptApiKey(String encryptedKey) {
        if (encryptedKey == null || !encryptedKey.startsWith("enc:")) {
            return encryptedKey;
        }
        
        try {
            String base64 = encryptedKey.substring(4);
            byte[] decoded = Base64.getDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to decrypt API key", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    @Override
    public boolean validateConfig(LlmConfig config) {
        if (config.getProviderType() == null || config.getProviderType().isEmpty()) {
            return false;
        }
        
        if (config.getModel() == null || config.getModel().isEmpty()) {
            return false;
        }
        
        if (config.getLevel() == null) {
            return false;
        }
        
        return true;
    }
}
