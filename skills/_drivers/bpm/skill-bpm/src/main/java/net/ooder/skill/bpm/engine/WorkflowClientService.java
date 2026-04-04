package net.ooder.skill.bpm.engine;

import java.util.List;
import java.util.Map;

import net.ooder.skill.bpm.model.ActivityInst;
import net.ooder.skill.bpm.model.ProcessDef;
import net.ooder.skill.bpm.model.ProcessInst;

public class WorkflowClientService {

    private ProcessEngine processEngine;

    public WorkflowClientService() {
        this.processEngine = new ProcessEngine();
    }

    public ProcessInst startProcess(String processDefId, Map<String, Object> variables, String starterId) {
        return processEngine.startProcess(processDefId, variables, starterId);
    }

    public void completeActivity(String activityInstId, Map<String, Object> formData, String opinion, String userId) {
        processEngine.completeActivity(activityInstId, formData, opinion, userId);
    }

    public void claimActivity(String activityInstId, String userId) {
        processEngine.claimActivity(activityInstId, userId);
    }

    public void releaseActivity(String activityInstId, String userId) {
        processEngine.releaseActivity(activityInstId, userId);
    }

    public void suspendProcess(String processInstId) {
        processEngine.suspendProcess(processInstId);
    }

    public void resumeProcess(String processInstId) {
        processEngine.resumeProcess(processInstId);
    }

    public void terminateProcess(String processInstId, String reason) {
        processEngine.terminateProcess(processInstId, reason);
    }

    public List<ActivityInst> getMyTasks(String userId) {
        return null;
    }

    public List<ActivityInst> getMyCompletedTasks(String userId) {
        return null;
    }

    public List<ProcessInst> getMyProcesses(String userId) {
        return null;
    }

    public List<ProcessInst> getMyStartedProcesses(String userId) {
        return null;
    }

    public ProcessInst getProcessInstance(String processInstId) {
        return null;
    }

    public ActivityInst getActivityInstance(String activityInstId) {
        return null;
    }

    public ProcessDef getProcessDefinition(String processDefId) {
        return null;
    }

    public List<ProcessDef> getProcessDefinitions(String category) {
        return null;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
