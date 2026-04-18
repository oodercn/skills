package net.ooder.skill.cli.driver.impl;

import net.ooder.skill.cli.driver.SkillDriver;
import net.ooder.skill.cli.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class MockSkillDriver implements SkillDriver {
    
    private final Map<String, SkillEntity> skills = new HashMap<>();
    
    public MockSkillDriver() {
        initMockData();
    }
    
    private void initMockData() {
        SkillEntity skill1 = new SkillEntity();
        skill1.setSkillId("skill-llm-deepseek");
        skill1.setName("DeepSeek LLM");
        skill1.setVersion("3.0.3");
        skill1.setDescription("DeepSeek LLM Driver");
        skill1.setStatus(SkillStatus.RUNNING);
        skill1.setCategory("llm");
        skill1.setInstalledAt(LocalDateTime.now().minusDays(5));
        skills.put(skill1.getSkillId(), skill1);
        
        SkillEntity skill2 = new SkillEntity();
        skill2.setSkillId("skill-knowledge");
        skill2.setName("Knowledge Base");
        skill2.setVersion("3.0.3");
        skill2.setDescription("Knowledge Base Management");
        skill2.setStatus(SkillStatus.RUNNING);
        skill2.setCategory("knowledge");
        skill2.setInstalledAt(LocalDateTime.now().minusDays(3));
        skills.put(skill2.getSkillId(), skill2);
    }
    
    @Override
    public String getDriverId() {
        return "mock-skill-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Mock Skill Driver";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public SkillEntity install(String source, Map<String, Object> config) {
        String skillId = "skill-" + UUID.randomUUID().toString().substring(0, 8);
        SkillEntity entity = new SkillEntity();
        entity.setSkillId(skillId);
        entity.setName(source);
        entity.setVersion("1.0.0");
        entity.setStatus(SkillStatus.INSTALLED);
        entity.setInstalledAt(LocalDateTime.now());
        entity.setSource(source);
        skills.put(skillId, entity);
        return entity;
    }
    
    @Override
    public UninstallResult uninstall(String skillId, boolean force) {
        if (skills.remove(skillId) != null) {
            return UninstallResult.success(skillId);
        }
        return UninstallResult.failure("Skill not found: " + skillId);
    }
    
    @Override
    public StartResult start(String skillId, Map<String, Object> params) {
        SkillEntity entity = skills.get(skillId);
        if (entity != null) {
            entity.setStatus(SkillStatus.RUNNING);
            entity.setStartedAt(LocalDateTime.now());
            return StartResult.success(skillId);
        }
        return StartResult.failure("Skill not found: " + skillId);
    }
    
    @Override
    public StopResult stop(String skillId, boolean force) {
        SkillEntity entity = skills.get(skillId);
        if (entity != null) {
            entity.setStatus(SkillStatus.STOPPED);
            return StopResult.success(skillId);
        }
        return StopResult.failure("Skill not found: " + skillId);
    }
    
    @Override
    public List<SkillEntity> getAllSkills() {
        return new ArrayList<>(skills.values());
    }
    
    @Override
    public SkillEntity getSkill(String skillId) {
        return skills.get(skillId);
    }
    
    @Override
    public SkillStatus getStatus(String skillId) {
        SkillEntity entity = skills.get(skillId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public Object invoke(String skillId, String capabilityId, Map<String, Object> params) {
        return Map.of("result", "mock-invoke", "skillId", skillId, "capabilityId", capabilityId);
    }
    
    @Override
    public void refresh() {
    }
}
