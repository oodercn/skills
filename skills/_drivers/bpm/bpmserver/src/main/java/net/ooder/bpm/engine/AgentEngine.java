package net.ooder.bpm.engine;

import net.ooder.bpm.enums.agent.AgentConditionEnums;
import net.ooder.bpm.enums.agent.AgentCtx;
import net.ooder.bpm.enums.agent.AgentGroupEnums;
import net.ooder.common.Filter;
import net.ooder.common.ReturnType;

import java.util.List;
import java.util.Map;

public interface AgentEngine {

    Filter getProcessDefListFilter(Map<AgentCtx, Object> ctx) throws BPMException;

    Filter getProcessInstListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException;

    Filter getActivityInstListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException;

    Filter getActivityInstHistoryListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType createProcessInst(String processInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType createProcessInst(String processInstId, String initType, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType startProcessInst(String processInstId, String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    List<String> getAgentCandidate(String activityDefId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType startActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType hasAgentToStartProcess(String processInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType changeAgent(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType routeTo(String activityInstId, String activityDefId, Map<AgentCtx, Object> ctx) throws BPMException;

    boolean canRouteBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<AgentCtx, Object> ctx) throws BPMException;

    boolean canTakeBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType tackBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    boolean canSignReceive(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType signReceive(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType combineActivityInsts(String[] activityInstIds, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType suspendActivityInst(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType resumeActivityInst(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType suspendProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType resumeProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType abortProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType completeProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType deleteProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<AgentCtx, Object> ctx) throws BPMException;

    ActivityDefAgent getActivityDefAgentAttribute(String activityDefId) throws BPMException;

    List<String> getActivityInstAgentAttribute(String activityInstId, AgentGroupEnums attName, Map<AgentCtx, Object> ctx) throws BPMException;

    List<String> getActivityInstHistoryAgentAttribute(String activityInstHistoryId, AgentGroupEnums attName, Map<AgentCtx, Object> ctx) throws BPMException;

    boolean queryPermissionToActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    List<AgentGroupEnums> queryAllPermissionToActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType invokeAgent(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    void terminateAgent(String activityInstId) throws BPMException;

    String getAgentStatus(String activityInstId) throws BPMException;

    ReturnType escalateToCoordinator(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException;

    ReturnType delegateToAgent(String activityInstId, String targetAgentId, Map<AgentCtx, Object> ctx) throws BPMException;
}
