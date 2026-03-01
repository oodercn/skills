package net.ooder.skill.management.lifecycle;

import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillDefinition.SkillStatus;

import java.util.Map;

public interface SkillLifecycleListener {

    void onSkillDiscovered(String skillId, SkillDefinition skill);

    void onSkillMetadataLoaded(String skillId, Map<String, Object> metadata);

    void onSkillLoading(String skillId);

    void onSkillLoaded(String skillId, SkillDefinition skill);

    void onSkillLoadFailed(String skillId, Throwable error);

    void onSkillInitializing(String skillId);

    void onSkillInitialized(String skillId);

    void onSkillInitFailed(String skillId, Throwable error);

    void onSkillStarting(String skillId);

    void onSkillStarted(String skillId);

    void onSkillStartFailed(String skillId, Throwable error);

    void onSkillStatusChanged(String skillId, SkillStatus oldStatus, SkillStatus newStatus);

    void onSkillIdle(String skillId);

    void onSkillRunning(String skillId);

    void onSkillPaused(String skillId);

    void onSkillStopping(String skillId);

    void onSkillStopped(String skillId);

    void onSkillUnloading(String skillId);

    void onSkillUnloaded(String skillId);

    void onSkillError(String skillId, Throwable error);

    void onSkillRecovered(String skillId);
}
