package net.ooder.skill.share.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import net.ooder.skill.share.model.ReceivedSkill;
import net.ooder.skill.share.model.SharedSkill;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Skill鍒嗕韩API瀹炵幇
 * 瀵瑰簲鏃х増SkillShareProviderImpl
 */
@Slf4j
@Component
public class SkillShareApiImpl implements SkillShareApi {
    
    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    
    private final Map<String, SharedSkill> sharedSkills = new ConcurrentHashMap<>();
    private final Map<String, ReceivedSkill> receivedSkills = new ConcurrentHashMap<>();
    
    @Override
    public String getApiName() {
        return "skill-share";
    }
    
    @Override
    public String getVersion() {
        return "2.3.0";
    }
    
    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("SkillShareApi initialized with context: {}", context);
    }
    
    @Override
    public void start() {
        this.running = true;
        log.info("SkillShareApi started");
    }
    
    @Override
    public void stop() {
        this.running = false;
        log.info("SkillShareApi stopped");
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public Result<SharedSkill> shareSkill(Map<String, Object> skillData) {
        try {
            String shareId = UUID.randomUUID().toString();
            
            SharedSkill skill = new SharedSkill();
            skill.setShareId(shareId);
            skill.setSkillId((String) skillData.get("skillId"));
            skill.setSkillName((String) skillData.getOrDefault("skillName", "Unnamed Skill"));
            skill.setSkillVersion((String) skillData.getOrDefault("skillVersion", "1.0.0"));
            skill.setDescription((String) skillData.get("description"));
            skill.setSharedBy((String) skillData.get("sharedBy"));
            skill.setSharedWith((List<String>) skillData.getOrDefault("sharedWith", new ArrayList<>()));
            skill.setPermissions((List<String>) skillData.getOrDefault("permissions", Arrays.asList("read", "execute")));
            skill.setSharedAt(System.currentTimeMillis());
            skill.setExpiresAt(skillData.containsKey("expiresAt") 
                    ? ((Number) skillData.get("expiresAt")).longValue() 
                    : System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            skill.setStatus("active");
            skill.setMetadata((Map<String, Object>) skillData.getOrDefault("metadata", new HashMap<>()));
            
            sharedSkills.put(shareId, skill);
            
            log.info("Skill shared: {} ({})", skill.getSkillName(), shareId);
            return Result.success(skill);
        } catch (Exception e) {
            log.error("Failed to share skill", e);
            return Result.error("Failed to share skill: " + e.getMessage());
        }
    }
    
    @Override
    public Result<ReceivedSkill> receiveSkill(String shareId, String receivedBy) {
        SharedSkill sharedSkill = sharedSkills.get(shareId);
        if (sharedSkill == null) {
            return Result.error("Share not found: " + shareId);
        }
        
        if (!"active".equals(sharedSkill.getStatus())) {
            return Result.error("Share is not active: " + shareId);
        }
        
        if (sharedSkill.getExpiresAt() != null && sharedSkill.getExpiresAt() < System.currentTimeMillis()) {
            return Result.error("Share has expired: " + shareId);
        }
        
        ReceivedSkill receivedSkill = new ReceivedSkill();
        receivedSkill.setReceiveId(UUID.randomUUID().toString());
        receivedSkill.setShareId(shareId);
        receivedSkill.setSkillId(sharedSkill.getSkillId());
        receivedSkill.setSkillName(sharedSkill.getSkillName());
        receivedSkill.setSkillVersion(sharedSkill.getSkillVersion());
        receivedSkill.setDescription(sharedSkill.getDescription());
        receivedSkill.setSharedBy(sharedSkill.getSharedBy());
        receivedSkill.setReceivedBy(receivedBy);
        receivedSkill.setReceivedAt(System.currentTimeMillis());
        receivedSkill.setExpiresAt(sharedSkill.getExpiresAt());
        receivedSkill.setStatus("received");
        receivedSkill.setPermissions(sharedSkill.getPermissions());
        receivedSkill.setMetadata(sharedSkill.getMetadata());
        
        receivedSkills.put(receivedSkill.getReceiveId(), receivedSkill);
        
        log.info("Skill received: {} by {}", shareId, receivedBy);
        return Result.success(receivedSkill);
    }
    
    @Override
    public Result<SharedSkill> getSharedSkill(String shareId) {
        SharedSkill skill = sharedSkills.get(shareId);
        if (skill == null) {
            return Result.error("Shared skill not found: " + shareId);
        }
        return Result.success(skill);
    }
    
    @Override
    public Result<ReceivedSkill> getReceivedSkill(String receiveId) {
        ReceivedSkill skill = receivedSkills.get(receiveId);
        if (skill == null) {
            return Result.error("Received skill not found: " + receiveId);
        }
        return Result.success(skill);
    }
    
    @Override
    public Result<List<SharedSkill>> listSharedSkills(String sharedBy) {
        List<SharedSkill> skills = sharedSkills.values().stream()
                .filter(skill -> skill.getSharedBy().equals(sharedBy))
                .collect(Collectors.toList());
        
        skills.forEach(skill -> {
            if (skill.getExpiresAt() != null && skill.getExpiresAt() < System.currentTimeMillis()) {
                skill.setStatus("expired");
            }
        });
        
        log.debug("Retrieved {} shared skills for user: {}", skills.size(), sharedBy);
        return Result.success(skills);
    }
    
    @Override
    public Result<List<ReceivedSkill>> listReceivedSkills(String receivedBy) {
        List<ReceivedSkill> skills = receivedSkills.values().stream()
                .filter(skill -> skill.getReceivedBy().equals(receivedBy))
                .collect(Collectors.toList());
        
        skills.forEach(skill -> {
            if (skill.getExpiresAt() != null && skill.getExpiresAt() < System.currentTimeMillis()) {
                skill.setStatus("expired");
            }
        });
        
        log.debug("Retrieved {} received skills for user: {}", skills.size(), receivedBy);
        return Result.success(skills);
    }
    
    @Override
    public Result<Boolean> revokeShare(String shareId) {
        SharedSkill skill = sharedSkills.remove(shareId);
        if (skill == null) {
            return Result.error("Share not found: " + shareId);
        }
        
        log.info("Share revoked: {}", shareId);
        return Result.success(true);
    }
    
    @Override
    public Result<Boolean> deleteReceivedSkill(String receiveId) {
        ReceivedSkill skill = receivedSkills.remove(receiveId);
        if (skill == null) {
            return Result.error("Received skill not found: " + receiveId);
        }
        
        log.info("Received skill deleted: {}", receiveId);
        return Result.success(true);
    }
    
    @Override
    public Result<SharedSkill> updateSharePermissions(String shareId, List<String> permissions) {
        SharedSkill skill = sharedSkills.get(shareId);
        if (skill == null) {
            return Result.error("Share not found: " + shareId);
        }
        
        skill.setPermissions(permissions);
        log.info("Share permissions updated: {}", shareId);
        return Result.success(skill);
    }
}
