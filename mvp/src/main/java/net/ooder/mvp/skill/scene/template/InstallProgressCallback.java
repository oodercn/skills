package net.ooder.mvp.skill.scene.template;

public interface InstallProgressCallback {
    
    void onInstallStart(String templateId, int totalSkills);
    
    void onSkillStart(String skillId, int current, int total);
    
    void onSkillProgress(String skillId, String phase, int progress);
    
    void onSkillComplete(String skillId, boolean success, String message);
    
    void onDependencyStart(String parentSkillId, String dependencyId);
    
    void onDependencyComplete(String parentSkillId, String dependencyId, boolean success);
    
    void onInstallComplete(String templateId, boolean success, String message);
    
    void onRollbackStart(String templateId, java.util.List<String> installedSkills);
    
    void onRollbackComplete(String templateId, boolean success);
}
