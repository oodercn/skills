package net.ooder.skill.bpm.engine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import net.ooder.skill.bpm.model.ActivityDef;
import net.ooder.skill.bpm.model.ActivityInst;
import net.ooder.skill.bpm.model.ProcessDef;
import net.ooder.skill.bpm.model.ProcessInst;

public class ProcessEngine {

    public ProcessInst startProcess(String processDefId, Map<String, Object> variables, String starterId) {
        ProcessInst inst = new ProcessInst();
        inst.setProcessDefId(processDefId);
        inst.setStarterId(starterId);
        inst.setStartTime(LocalDateTime.now());
        inst.setCreateTime(LocalDateTime.now());
        inst.setStatus("RUNNING");
        if (variables != null) {
            inst.getProcessVariables().putAll(variables);
        }
        return inst;
    }

    public void completeActivity(String activityInstId, Map<String, Object> formData, String opinion, String userId) {
    }

    public void claimActivity(String activityInstId, String userId) {
    }

    public void releaseActivity(String activityInstId, String userId) {
    }

    public void suspendProcess(String processInstId) {
    }

    public void resumeProcess(String processInstId) {
    }

    public void terminateProcess(String processInstId, String reason) {
    }

    public ActivityInst findNextActivity(ProcessInst processInst, ActivityInst completedActivity) {
        return null;
    }

    public List<ActivityDef> findNextActivities(ProcessDef processDef, String currentActivityId, Map<String, Object> variables) {
        return null;
    }

    public boolean evaluateCondition(String condition, Map<String, Object> variables) {
        return true;
    }

    public ProcessDef parseXpdl(String xpdlContent) {
        return null;
    }

    public String generateXpdl(ProcessDef processDef) {
        return null;
    }
}
