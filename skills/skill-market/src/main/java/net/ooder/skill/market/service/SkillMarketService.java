package net.ooder.skill.market.service;

import net.ooder.skill.market.dto.*;

import java.util.List;

public interface SkillMarketService {
    List<SkillPackage> listSkills();
    PageResult<SkillPackage> searchSkills(SearchRequest request);
    SkillPackage getSkill(String skillId);
    InstallResult installSkill(String skillId, String version);
    InstallResult uninstallSkill(String skillId);
    InstallResult updateSkill(String skillId);
    AuthStatus getAuthStatus(String skillId);
    
    SdkConfig getSdkConfig();
    SdkConfig updateSdkConfig(SdkConfig config);
    SdkStatus getSdkStatus();
    boolean switchMode(String mode);
}
