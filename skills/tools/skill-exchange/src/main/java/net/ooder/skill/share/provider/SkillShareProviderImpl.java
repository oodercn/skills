package net.ooder.skill.share.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.SkillShareProvider;
import net.ooder.scene.provider.model.share.SharedSkill;
import net.ooder.scene.provider.model.share.ReceivedSkill;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SkillShareProviderImpl implements SkillShareProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, SharedSkill> sharedSkills = new ConcurrentHashMap<>();
    private final Map<String, ReceivedSkill> receivedSkills = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-share";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
        log.info("SkillShareProvider initialized");
    }
    
    @Override
    public void start() {
        this.running = true;
        log.info("SkillShareProvider started");
    }
    
    @Override
    public void stop() {
        this.running = false;
        log.info("SkillShareProvider stopped");
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
    public Result<List<SharedSkill>> getSharedSkills() {
        List<SharedSkill> skills = new ArrayList<>(sharedSkills.values());
        
        skills.removeIf(skill -> {
            if (skill.getExpiresAt() != null && skill.getExpiresAt() < System.currentTimeMillis()) {
                skill.setStatus("expired");
                return false;
            }
            return false;
        });
        
        log.debug("Retrieved {} shared skills", skills.size());
        return Result.success(skills);
    }
    
    @Override
    public Result<List<ReceivedSkill>> getReceivedSkills() {
        List<ReceivedSkill> skills = new ArrayList<>(receivedSkills.values());
        
        skills.forEach(skill -> {
            if (skill.getExpiresAt() != null && skill.getExpiresAt() < System.currentTimeMillis()) {
                skill.setStatus("expired");
            }
        });
        
        log.debug("Retrieved {} received skills", skills.size());
        return Result.success(skills);
    }
    
    @Override
    public Result<Boolean> cancelShare(String shareId) {
        SharedSkill skill = sharedSkills.remove(shareId);
        if (skill == null) {
            return Result.error("Share not found: " + shareId);
        }
        
        log.info("Share cancelled: {}", shareId);
        return Result.success(true);
    }
    
    @Override
    public Result<Boolean> acceptShare(String shareId) {
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
        receivedSkill.setReceivedAt(System.currentTimeMillis());
        receivedSkill.setExpiresAt(sharedSkill.getExpiresAt());
        receivedSkill.setStatus("accepted");
        receivedSkill.setPermissions(sharedSkill.getPermissions());
        receivedSkill.setMetadata(sharedSkill.getMetadata());
        
        receivedSkills.put(receivedSkill.getReceiveId(), receivedSkill);
        
        log.info("Share accepted: {} by {}", shareId, sharedSkill.getSkillName());
        return Result.success(true);
    }
    
    @Override
    public Result<Boolean> rejectShare(String shareId) {
        SharedSkill sharedSkill = sharedSkills.get(shareId);
        if (sharedSkill == null) {
            return Result.error("Share not found: " + shareId);
        }
        
        log.info("Share rejected: {}", shareId);
        return Result.success(true);
    }
    
    public SharedSkill getSharedSkill(String shareId) {
        return sharedSkills.get(shareId);
    }
    
    public ReceivedSkill getReceivedSkill(String receiveId) {
        return receivedSkills.get(receiveId);
    }
}
