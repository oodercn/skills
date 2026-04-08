package net.ooder.bpm.designer.cache;

import com.alibaba.fastjson2.JSON;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class CacheKeyGenerator {
    
    private static final String PREFIX = "bpm:designer:";
    
    public static String performerDerivation(String tenantId, String processId, String activityDesc) {
        String content = String.format("pd:%s:%s:%s", tenantId, processId, hash(activityDesc));
        return PREFIX + content;
    }
    
    public static String capabilityMatching(String tenantId, String keyword, String category) {
        String content = String.format("cm:%s:%s:%s", tenantId, hash(keyword), category != null ? category : "all");
        return PREFIX + content;
    }
    
    public static String formMatching(String tenantId, String keyword, String formType) {
        String content = String.format("fm:%s:%s:%s", tenantId, hash(keyword), formType != null ? formType : "all");
        return PREFIX + content;
    }
    
    public static String organizationTree(String tenantId, String orgType) {
        return PREFIX + String.format("org:%s:%s", tenantId, orgType);
    }
    
    public static String sceneTemplates(String tenantId) {
        return PREFIX + String.format("scene:%s:templates", tenantId);
    }
    
    public static String llmResponse(String model, String prompt) {
        return PREFIX + String.format("llm:%s:%s", model, hash(prompt));
    }
    
    public static String functionResult(String functionName, Map<String, Object> args) {
        String argsHash = hash(JSON.toJSONString(args));
        return PREFIX + String.format("func:%s:%s", functionName, argsHash);
    }
    
    private static String hash(String input) {
        if (input == null || input.isEmpty()) {
            return "empty";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }
}
