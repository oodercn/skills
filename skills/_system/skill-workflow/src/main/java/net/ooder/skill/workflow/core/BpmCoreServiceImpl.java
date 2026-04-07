package net.ooder.skill.workflow.core;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.Filter;
import net.ooder.config.ListResultModel;
import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.workflow.listener.BpmEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BpmCoreServiceImpl implements BpmCoreService {

    private static final Logger log = LoggerFactory.getLogger(BpmCoreServiceImpl.class);

    @Autowired(required = false)
    private WorkflowClientService bpmClient;

    @Autowired(required = false)
    private BpmEventListener eventListener;

    @Override
    public boolean isAvailable() {
        return bpmClient != null;
    }

    @Override
    public String getSystemCode() {
        if (!isAvailable()) return "UNKNOWN";
        try {
            return bpmClient.getSystemCode();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private void checkAvailable() throws BPMException {
        if (!isAvailable()) {
            throw new BPMException("BPM engine not available", -1);
        }
    }

    @Override
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessDefVersionList(condition, filter, BpmRightContext.build());
    }

    @Override
    public ProcessDefVersion getProcessDefVersion(String processDefVersionId) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessDefVersion(processDefVersionId);
    }

    @Override
    public ProcessDef getProcessDef(String processDefId) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessDef(processDefId);
    }

    @Override
    public ActivityDef getActivityDef(String activityDefId) throws BPMException {
        checkAvailable();
        return bpmClient.getActivityDef(activityDefId);
    }

    @Override
    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException {
        checkAvailable();
        return bpmClient.getFirstActivityDefInProcess(processDefVersionId);
    }

    @Override
    public ProcessInst newProcess(String processDefId, String name, String urgency, Map<RightCtx, Object> ctx) throws BPMException {
        checkAvailable();
        return bpmClient.newProcess(processDefId, name, urgency, ctx != null ? ctx : BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_start_process", resourceType = "ProcessInst", logParams = true)
    public ProcessInst startProcess(String processDefVersionId, String processName, String urgency, Map<String, Object> formValues) throws BPMException {
        checkAvailable();
        ProcessDefVersion version = bpmClient.getProcessDefVersion(processDefVersionId);
        Map<RightCtx, Object> ctx = BpmRightContext.build();
        ProcessInst inst = bpmClient.newProcess(version.getProcessDefId(), processName, urgency, ctx);
        log.info("[startProcess] processInstId={}, name={}", inst.getProcessInstId(), processName);
        if (formValues != null && !formValues.isEmpty()) {
            List<ActivityInst> actList = inst.getActivityInstList();
            if (actList != null && !actList.isEmpty()) {
                ActivityInst firstAct = actList.get(0);
                DataMap formMap = firstAct.getFormValues();
                if (formMap != null) {
                    formMap.putAll(formValues);
                    bpmClient.updateActivityInstFormValues(firstAct.getActivityInstId(), formMap);
                }
            }
        }
        if (eventListener != null) {
            log.info("[startProcess] Process started: processInstId={}, defId={}, name={}",
                inst.getProcessInstId(), processDefVersionId, processName);
        }
        return inst;
    }

    @Override
    public ProcessInst getProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessInst(processInstId);
    }

    @Override
    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnum, Filter filter) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessInstList(condition, conditionEnum, filter, BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_complete_process", resourceType = "ProcessInst")
    public void completeProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        bpmClient.completeProcessInst(processInstId, BpmRightContext.build());
        log.info("[completeProcessInst] {}", processInstId);
    }

    @Override
    @Auditable(action = "bpm_suspend_process", resourceType = "ProcessInst")
    public void suspendProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        bpmClient.suspendProcessInst(processInstId, BpmRightContext.build());
    }

    @Override
    public void resumeProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        bpmClient.resumeProcessInst(processInstId, BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_abort_process", resourceType = "ProcessInst")
    public void abortProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        bpmClient.abortProcessInst(processInstId, BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_delete_process", resourceType = "ProcessInst")
    public void deleteProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        bpmClient.deleteProcessInst(processInstId, BpmRightContext.build());
    }

    @Override
    public List<ActivityInst> getWaitedWorkList(int page, int size, String processDefId, Long startTime, Long endTime, String titleKeyword) throws BPMException {
        checkAvailable();
        BPMCondition condition = BpmConditionHelper.buildActivityCondition(
            RightConditionEnums.CONDITION_WAITEDWORK, processDefId, startTime, endTime, titleKeyword);
        ListResultModel<List<ActivityInst>> result = bpmClient.getActivityInstList(
            condition, RightConditionEnums.CONDITION_WAITEDWORK, null, BpmRightContext.build());
        return extractPageData(result, page, size);
    }

    @Override
    public List<ActivityInst> getMyWorkList(int page, int size, String processDefId, Long startTime, Long endTime, String titleKeyword) throws BPMException {
        checkAvailable();
        BPMCondition condition = BpmConditionHelper.buildActivityCondition(
            RightConditionEnums.CONDITION_CURRENTWORK, processDefId, startTime, endTime, titleKeyword);
        ListResultModel<List<ActivityInst>> result = bpmClient.getActivityInstList(
            condition, RightConditionEnums.CONDITION_CURRENTWORK, null, BpmRightContext.build());
        return extractPageData(result, page, size);
    }

    @Override
    public List<ActivityInst> getCompletedWorkList(int page, int size, String processDefId, Long startTime, Long endTime) throws BPMException {
        checkAvailable();
        BPMCondition condition = BpmConditionHelper.buildActivityCondition(
            RightConditionEnums.CONDITION_COMPLETEDWORK, processDefId, startTime, endTime, null);
        ListResultModel<List<ActivityInst>> result = bpmClient.getActivityInstList(
            condition, RightConditionEnums.CONDITION_COMPLETEDWORK, null, BpmRightContext.build());
        return extractPageData(result, page, size);
    }

    @Override
    public List<ActivityInst> getReadWorkList(int page, int size, String processDefId, Long startTime, Long endTime) throws BPMException {
        checkAvailable();
        BPMCondition condition = BpmConditionHelper.buildActivityCondition(
            RightConditionEnums.CONDITION_READ, processDefId, startTime, endTime, null);
        ListResultModel<List<ActivityInst>> result = bpmClient.getActivityInstList(
            condition, RightConditionEnums.CONDITION_READ, null, BpmRightContext.build());
        return extractPageData(result, page, size);
    }

    @Override
    public List<ActivityInst> getDraftList(int page, int size, String processDefId) throws BPMException {
        checkAvailable();
        BPMCondition condition = BpmConditionHelper.buildActivityCondition(
            RightConditionEnums.CONDITION_CURRENTWORK_NOTSTART, processDefId, null, null, null);
        ListResultModel<List<ActivityInst>> result = bpmClient.getActivityInstList(
            condition, RightConditionEnums.CONDITION_CURRENTWORK_NOTSTART, null, BpmRightContext.build());
        return extractPageData(result, page, size);
    }

    @Override
    @Auditable(action = "bpm_sign_receive", resourceType = "ActivityInst")
    public void signReceive(String activityInstId) throws BPMException {
        checkAvailable();
        bpmClient.signReceive(activityInstId, BpmRightContext.build());
        log.info("[signReceive] {}", activityInstId);
    }

    @Override
    @Auditable(action = "bpm_route_to", resourceType = "ActivityInst", logParams = true)
    public void routeTo(String activityInstId, List<String> nextActivityDefIds, List<String> performerIds, List<String> readerIds) throws BPMException {
        checkAvailable();
        if (nextActivityDefIds == null || nextActivityDefIds.isEmpty()) {
            List<RouteDef> routes = bpmClient.getNextRoutes(activityInstId, null, null, BpmRightContext.build());
            nextActivityDefIds = new ArrayList<>();
            for (RouteDef r : routes) {
                nextActivityDefIds.add(r.getToActivityDefId());
            }
        }
        List<Map<RightCtx, Object>> ctxs = new ArrayList<>();
        for (String nextActId : nextActivityDefIds) {
            Map<RightCtx, Object> ctx = BpmRightContext.buildForRoute(bpmClient, activityInstId, nextActId, performerIds, readerIds);
            ctxs.add(ctx);
        }
        bpmClient.routeTo(activityInstId, nextActivityDefIds, ctxs);
        log.info("[routeTo] activityInstId={}, nextRoutes={}, performers={}", activityInstId, nextActivityDefIds, performerIds);
    }

    @Override
    @Auditable(action = "bpm_route_back", resourceType = "ActivityInst")
    public void routeBack(String activityInstId, String historyId) throws BPMException {
        checkAvailable();
        if (historyId == null || historyId.isEmpty()) {
            ActivityInst inst = bpmClient.getActivityInst(activityInstId);
            List<ActivityInstHistory> histories = inst.getRouteBackActivityHistoryInstList();
            if (histories != null && !histories.isEmpty()) {
                historyId = histories.get(0).getActivityHistoryId();
            } else {
                throw new BPMException("No route-back history available for " + activityInstId, -1);
            }
        }
        bpmClient.routeBack(activityInstId, historyId, BpmRightContext.build());
        log.info("[routeBack] {} -> {}", activityInstId, historyId);
    }

    @Override
    @Auditable(action = "bpm_take_back", resourceType = "ActivityInst")
    public void takeBack(String activityInstId) throws BPMException {
        checkAvailable();
        bpmClient.takeBack(activityInstId, BpmRightContext.build());
        log.info("[takeBack] {}", activityInstId);
    }

    @Override
    public void endRead(String activityInstId) throws BPMException {
        checkAvailable();
        bpmClient.endRead(activityInstId, BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_end_task", resourceType = "ActivityInst")
    public void endTask(String activityInstId) throws BPMException {
        checkAvailable();
        bpmClient.endTask(activityInstId, BpmRightContext.build());
        log.info("[endTask] {}", activityInstId);
    }

    @Override
    public List<RouteDef> getNextRoutes(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getNextRoutes(activityInstId, null, null, BpmRightContext.build());
    }

    @Override
    public boolean canTakeBack(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.canTakeBack(activityInstId, BpmRightContext.build());
    }

    @Override
    public boolean canRouteBack(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.canRouteBack(activityInstId, BpmRightContext.build());
    }

    @Override
    public boolean canSignReceive(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.canSignReceive(activityInstId, BpmRightContext.build());
    }

    @Override
    public boolean canEndRead(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.canEndRead(activityInstId, BpmRightContext.build());
    }

    @Override
    @Auditable(action = "bpm_copy_to", resourceType = "ActivityInst", logParams = true)
    public void copyTo(String activityInstId, List<String> readerIds) throws BPMException {
        checkAvailable();
        bpmClient.copyTo(activityInstId, readerIds != null ? readerIds : Collections.emptyList());
        log.info("[copyTo] {} -> readers={}", activityInstId, readerIds);
    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getActivityInstFormValues(activityInstId);
    }

    @Override
    public void saveActivityInstFormValues(String activityInstId, Map<String, Object> formValues) throws BPMException {
        checkAvailable();
        ActivityInst inst = bpmClient.getActivityInst(activityInstId);
        if (inst != null) {
            DataMap formMap = inst.getFormValues();
            if (formMap != null && formValues != null) {
                formMap.putAll(formValues);
                bpmClient.updateActivityInstFormValues(activityInstId, formMap);
            }
        }
    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getProcessInstFormValues(processInstId);
    }

    @Override
    public void saveProcessInstFormValues(String processInstId, Map<String, Object> formValues) throws BPMException {
        checkAvailable();
        DataMap formMap = bpmClient.getProcessInstFormValues(processInstId);
        if (formMap != null && formValues != null) {
            formMap.putAll(formValues);
            bpmClient.updateProcessInstFormValues(processInstId, formMap);
        }
    }

    @Override
    public Object getActivityMainFormDef(String activityDefId) throws BPMException {
        checkAvailable();
        return invokeMethod(bpmClient, "getActivityMainFormDef", activityDefId);
    }

    @Override
    public Object getAllActivityDataFormDef(String activityDefId) throws BPMException {
        checkAvailable();
        return invokeMethod(bpmClient, "getAllActivityDataFormDef", activityDefId);
    }

    @Override
    public Object getProcessDefForm(String processDefVersionId) throws BPMException {
        checkAvailable();
        return invokeMethod(bpmClient, "getProcessDefForm", processDefVersionId);
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByActivityInst(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getActivityInstHistoryListByActvityInst(activityInstId, BpmRightContext.build());
    }

    @Override
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActivityInst(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getLastActivityInstHistoryListByActvityInst(activityInstId, BpmRightContext.build());
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getActivityInstHistoryListByProcessInst(processInstId, BpmRightContext.build());
    }

    @Override
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstId) throws BPMException {
        checkAvailable();
        return bpmClient.getRouteBackActivityHistoryInstList(activityInstId, null, BpmRightContext.build());
    }

    @Override
    public Map<String, Object> getDashboardOverview() throws BPMException {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("bpmAvailable", isAvailable());
        if (!isAvailable()) {
            overview.put("waitedCount", 0);
            overview.put("myWorkCount", 0);
            overview.put("completedCount", 0);
            overview.put("readCount", 0);
            overview.put("draftCount", 0);
            overview.put("recentWaited", Collections.emptyList());
            return overview;
        }
        try {
            overview.put("waitedCount", getWaitedWorkList(1, 1, null, null, null, null).size());
            overview.put("myWorkCount", getMyWorkList(1, 1, null, null, null, null).size());
            overview.put("completedCount", getCompletedWorkList(1, 1, null, null, null).size());
            overview.put("readCount", getReadWorkList(1, 1, null, null, null).size());
            overview.put("draftCount", getDraftList(1, 1, null).size());
            overview.put("recentWaited", getWaitedWorkList(1, 5, null, null, null, null));
        } catch (BPMException e) {
            log.warn("[getDashboardOverview] error: {}", e.getMessage());
            overview.put("error", e.getMessage());
        }
        return overview;
    }

    private <T> List<T> extractPageData(ListResultModel<List<T>> result, int page, int size) {
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            return Collections.emptyList();
        }
        List<T> allData = result.getData();
        int fromIndex = (page - 1) * size;
        if (fromIndex >= allData.size()) return Collections.emptyList();
        int toIndex = Math.min(fromIndex + size, allData.size());
        return allData.subList(fromIndex, toIndex);
    }

    private Object invokeMethod(Object target, String methodName, Object... args) throws BPMException {
        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) paramTypes[i] = args[i].getClass();
            java.lang.reflect.Method method = target.getClass().getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (NoSuchMethodException e) {
            throw new BPMException("Method not found: " + methodName, -1);
        } catch (Exception e) {
            throw new BPMException("Invoke error: " + methodName + " -> " + e.getMessage(), -1);
        }
    }
}
