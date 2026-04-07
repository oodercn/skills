package net.ooder.skill.workflow.core;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.common.Filter;
import net.ooder.config.ListResultModel;

import java.util.List;
import java.util.Map;

public interface BpmCoreService {

    boolean isAvailable();

    String getSystemCode();

    ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter) throws BPMException;

    ProcessDefVersion getProcessDefVersion(String processDefVersionId) throws BPMException;

    ProcessDef getProcessDef(String processDefId) throws BPMException;

    ActivityDef getActivityDef(String activityDefId) throws BPMException;

    ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException;

    ProcessInst newProcess(String processDefId, String name, String urgency, Map<net.ooder.bpm.enums.right.RightCtx, Object> ctx) throws BPMException;

    ProcessInst startProcess(String processDefVersionId, String processName, String urgency, Map<String, Object> formValues) throws BPMException;

    ProcessInst getProcessInst(String processInstId) throws BPMException;

    ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnum, Filter filter) throws BPMException;

    void completeProcessInst(String processInstId) throws BPMException;

    void suspendProcessInst(String processInstId) throws BPMException;

    void resumeProcessInst(String processInstId) throws BPMException;

    void abortProcessInst(String processInstId) throws BPMException;

    void deleteProcessInst(String processInstId) throws BPMException;

    List<ActivityInst> getWaitedWorkList(int page, int size, String processDefId, Long startTime, Long endTime, String titleKeyword) throws BPMException;

    List<ActivityInst> getMyWorkList(int page, int size, String processDefId, Long startTime, Long endTime, String titleKeyword) throws BPMException;

    List<ActivityInst> getCompletedWorkList(int page, int size, String processDefId, Long startTime, Long endTime) throws BPMException;

    List<ActivityInst> getReadWorkList(int page, int size, String processDefId, Long startTime, Long endTime) throws BPMException;

    List<ActivityInst> getDraftList(int page, int size, String processDefId) throws BPMException;

    void signReceive(String activityInstId) throws BPMException;

    void routeTo(String activityInstId, List<String> nextActivityDefIds, List<String> performerIds, List<String> readerIds) throws BPMException;

    void routeBack(String activityInstId, String historyId) throws BPMException;

    void takeBack(String activityInstId) throws BPMException;

    void endRead(String activityInstId) throws BPMException;

    void endTask(String activityInstId) throws BPMException;

    List<RouteDef> getNextRoutes(String activityInstId) throws BPMException;

    boolean canTakeBack(String activityInstId) throws BPMException;

    boolean canRouteBack(String activityInstId) throws BPMException;

    boolean canSignReceive(String activityInstId) throws BPMException;

    boolean canEndRead(String activityInstId) throws BPMException;

    void copyTo(String activityInstId, List<String> readerIds) throws BPMException;

    DataMap getActivityInstFormValues(String activityInstId) throws BPMException;

    void saveActivityInstFormValues(String activityInstId, Map<String, Object> formValues) throws BPMException;

    DataMap getProcessInstFormValues(String processInstId) throws BPMException;

    void saveProcessInstFormValues(String processInstId, Map<String, Object> formValues) throws BPMException;

    Object getActivityMainFormDef(String activityDefId) throws BPMException;

    Object getAllActivityDataFormDef(String activityDefId) throws BPMException;

    Object getProcessDefForm(String processDefVersionId) throws BPMException;

    List<ActivityInstHistory> getActivityInstHistoryListByActivityInst(String activityInstId) throws BPMException;

    List<ActivityInstHistory> getLastActivityInstHistoryListByActivityInst(String activityInstId) throws BPMException;

    ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId) throws BPMException;

    List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstId) throws BPMException;

    Map<String, Object> getDashboardOverview() throws BPMException;
}
