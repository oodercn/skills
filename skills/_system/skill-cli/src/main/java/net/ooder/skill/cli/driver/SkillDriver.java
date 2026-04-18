package net.ooder.skill.cli.driver;

import net.ooder.skill.cli.model.SkillEntity;
import net.ooder.skill.cli.model.SkillStatus;
import net.ooder.skill.cli.model.InstallResult;
import net.ooder.skill.cli.model.UninstallResult;
import net.ooder.skill.cli.model.StartResult;
import net.ooder.skill.cli.model.StopResult;

import java.util.List;
import java.util.Map;

public interface SkillDriver {
    
    String getDriverId();
    
    String getDriverName();
    
    boolean isAvailable();
    
    SkillEntity install(String source, Map<String, Object> config);
    
    UninstallResult uninstall(String skillId, boolean force);
    
    StartResult start(String skillId, Map<String, Object> params);
    
    StopResult stop(String skillId, boolean force);
    
    List<SkillEntity> getAllSkills();
    
    SkillEntity getSkill(String skillId);
    
    SkillStatus getStatus(String skillId);
    
    Object invoke(String skillId, String capabilityId, Map<String, Object> params);
    
    void refresh();
}
