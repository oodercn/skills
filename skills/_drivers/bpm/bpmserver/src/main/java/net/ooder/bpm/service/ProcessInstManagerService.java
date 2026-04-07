package net.ooder.bpm.service;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProcessInstManagerService {

    private static final Logger log = LoggerFactory.getLogger(ProcessInstManagerService.class);

    @Transactional
    public Map<String, Object> newProcess(String processDefId, String processInstName, String urgency, String userId) {
        try {
            EIProcessDefVersion activeVersion = EIProcessDefVersionManager.getInstance().getActiveProcessDefVersion(processDefId);
            if (activeVersion == null) {
                throw new RuntimeException("流程定义没有激活版本: " + processDefId);
            }
            
            String versionId = activeVersion.getProcessDefVersionId();
            List<EIActivityDef> startActivities = new ArrayList<>();
            List<EIActivityDef> activities = EIActivityDefManager.getInstance().loadByProcessDefVersionId(versionId);
            for (EIActivityDef activity : activities) {
                if ("POSITION_START".equals(activity.getPosition())) {
                    startActivities.add(activity);
                }
            }
            
            if (startActivities.isEmpty()) {
                throw new RuntimeException("流程没有起始活动: " + processDefId);
            }
            
            EIActivityDef startActivity = startActivities.get(0);
            
            EIProcessInst processInst = EIProcessInstManager.getInstance().createProcessInstance();
            processInst.setProcessDefId(processDefId);
            processInst.setProcessDefVersionId(versionId);
            processInst.setName(processInstName);
            processInst.setUrgency(urgency);
            processInst.setState("running");
            processInst.setStartTime(new Date());
            processInst.setRunStatus("running");
            
            processInst = EIProcessInstManager.getInstance().save(processInst);
            String processInstId = processInst.getProcessInstId();
            
            EIActivityInst activityInst = EIActivityInstManager.getInstance().createActivityInstance();
            activityInst.setProcessInstId(processInstId);
            activityInst.setProcessDefId(processDefId);
            activityInst.setActivityDefId(startActivity.getActivityDefId());
            activityInst.setState("running");
            activityInst.setArrivedTime(new Date());
            activityInst.setReceiveMethod("SEND");
            activityInst.setDealMethod("NORMAL");
            activityInst.setRunStatus("RUNSTATUS_NORMAL");
            
            activityInst = EIActivityInstManager.getInstance().save(activityInst);
            String activityInstId = activityInst.getActivityInstId();
            
            EIActivityInstHistory history = EIActivityInstHistoryManager.getInstance().saveActivityInstAsHistory(activityInst);
            history.setStartTime(new Date());
            
            EIActivityInstHistoryManager.getInstance().save(history);
            
            log.info("Created process instance: {} with start activity: {} by user: {}", processInstId, activityInstId, userId);
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("processInstId", processInstId);
            result.put("activityInstId", activityInstId);
            result.put("activityDefId", startActivity.getActivityDefId());
            result.put("activityName", startActivity.getName());
            result.put("processDefId", processDefId);
            result.put("versionId", versionId);
            result.put("state", "running");
            result.put("createTime", System.currentTimeMillis());
            result.put("initiator", userId);
            
            return result;
        } catch (BPMException e) {
            log.error("Failed to create process instance: {}", e.getMessage());
            throw new RuntimeException("创建流程实例失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getProcessInst(String processInstId) {
        try {
            EIProcessInst processInst = EIProcessInstManager.getInstance().loadByKey(processInstId);
            if (processInst == null) {
                return null;
            }
            return convertProcessInst(processInst);
        } catch (BPMException e) {
            log.error("Failed to get process instance: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getProcessInstList() {
        try {
            List<EIProcessInst> processInsts = EIProcessInstManager.getInstance().loadAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIProcessInst processInst : processInsts) {
                result.add(convertProcessInst(processInst));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get process instance list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getActivityInst(String activityInstId) {
        try {
            EIActivityInst activityInst = EIActivityInstManager.getInstance().loadByKey(activityInstId);
            if (activityInst == null) {
                return null;
            }
            return convertActivityInst(activityInst);
        } catch (BPMException e) {
            log.error("Failed to get activity instance: {}", e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getActivityInstsByProcessInst(String processInstId) {
        try {
            List<EIActivityInst> activityInsts = EIActivityInstManager.getInstance().loadByProcessInstId(processInstId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (EIActivityInst activityInst : activityInsts) {
                result.add(convertActivityInst(activityInst));
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get activity instances: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getActivityInstHistoryByProcessInst(String processInstId) {
        try {
            EIActivityInstHistory[] histories = EIActivityInstHistoryManager.getInstance().loadByProcessInstId(processInstId);
            List<Map<String, Object>> result = new ArrayList<>();
            if (histories != null) {
                for (EIActivityInstHistory history : histories) {
                    result.add(convertActivityInstHistory(history));
                }
            }
            return result;
        } catch (BPMException e) {
            log.error("Failed to get activity instance history: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    public Map<String, Object> routeTo(String activityInstId, String targetActivityDefId, String userId) {
        try {
            EIActivityInst currentActivity = EIActivityInstManager.getInstance().loadByKey(activityInstId);
            if (currentActivity == null) {
                throw new RuntimeException("活动实例不存在: " + activityInstId);
            }
            
            String processInstId = currentActivity.getProcessInstId();
            String processDefId = currentActivity.getProcessDefId();
            
            currentActivity.setState("completed");
            currentActivity.setStartTime(new Date());
            EIActivityInstManager.getInstance().save(currentActivity);
            
            EIActivityInstHistory history = EIActivityInstHistoryManager.getInstance().saveActivityInstAsHistory(currentActivity);
            history.setEndTime(new Date());
            EIActivityInstHistoryManager.getInstance().save(history);
            
            EIActivityDef targetActivityDef = EIActivityDefManager.getInstance().loadByKey(targetActivityDefId);
            if (targetActivityDef == null) {
                throw new RuntimeException("目标活动定义不存在: " + targetActivityDefId);
            }
            
            EIActivityInst newActivityInst = EIActivityInstManager.getInstance().createActivityInstance();
            newActivityInst.setProcessInstId(processInstId);
            newActivityInst.setProcessDefId(processDefId);
            newActivityInst.setActivityDefId(targetActivityDefId);
            newActivityInst.setState("running");
            newActivityInst.setArrivedTime(new Date());
            newActivityInst.setReceiveMethod("SEND");
            newActivityInst.setDealMethod("NORMAL");
            newActivityInst.setRunStatus("RUNSTATUS_NORMAL");
            
            newActivityInst = EIActivityInstManager.getInstance().save(newActivityInst);
            String newActivityInstId = newActivityInst.getActivityInstId();
            
            EIRouteInst routeInst = EIRouteInstManager.getInstance().createRouteInst();
            routeInst.setProcessInstId(processInstId);
            routeInst.setFromActivityId(history.getActivityHistoryId());
            routeInst.setToActivityId(newActivityInstId);
            routeInst.setRouteTime(new Date());
            
            EIRouteInstManager.getInstance().save(routeInst);
            
            log.info("Routed from {} to {} for process {} by user {}", activityInstId, newActivityInstId, processInstId, userId);
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("fromActivityInstId", activityInstId);
            result.put("toActivityInstId", newActivityInstId);
            result.put("historyId", history.getActivityHistoryId());
            result.put("routeInstId", routeInst.getRouteInstId());
            result.put("processInstId", processInstId);
            result.put("routedBy", userId);
            
            return result;
        } catch (BPMException e) {
            log.error("Failed to route: {}", e.getMessage());
            throw new RuntimeException("路由失败: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, Object> endTask(String activityInstId, String userId) {
        try {
            EIActivityInst currentActivity = EIActivityInstManager.getInstance().loadByKey(activityInstId);
            if (currentActivity == null) {
                throw new RuntimeException("活动实例不存在: " + activityInstId);
            }
            
            currentActivity.setState("completed");
            currentActivity.setStartTime(new Date());
            EIActivityInstManager.getInstance().save(currentActivity);
            
            EIActivityInstHistory history = EIActivityInstHistoryManager.getInstance().saveActivityInstAsHistory(currentActivity);
            history.setEndTime(new Date());
            EIActivityInstHistoryManager.getInstance().save(history);
            
            log.info("Ended task: {} by user: {}", activityInstId, userId);
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("activityInstId", activityInstId);
            result.put("historyId", history.getActivityHistoryId());
            result.put("state", "completed");
            result.put("completedBy", userId);
            
            return result;
        } catch (BPMException e) {
            log.error("Failed to end task: {}", e.getMessage());
            throw new RuntimeException("结束任务失败: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, Object> completeProcessInst(String processInstId) {
        try {
            EIProcessInst processInst = EIProcessInstManager.getInstance().loadByKey(processInstId);
            if (processInst == null) {
                throw new RuntimeException("流程实例不存在: " + processInstId);
            }
            
            processInst.setState("completed");
            processInst.setEndTime(new Date());
            EIProcessInstManager.getInstance().save(processInst);
            
            log.info("Completed process instance: {}", processInstId);
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("processInstId", processInstId);
            result.put("state", "completed");
            result.put("endTime", System.currentTimeMillis());
            
            return result;
        } catch (BPMException e) {
            log.error("Failed to complete process instance: {}", e.getMessage());
            throw new RuntimeException("完成流程失败: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> convertProcessInst(EIProcessInst processInst) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("processInstId", processInst.getProcessInstId());
        map.put("processDefId", processInst.getProcessDefId());
        map.put("processDefVersionId", processInst.getProcessDefVersionId());
        map.put("name", processInst.getName());
        map.put("urgency", processInst.getUrgency());
        map.put("state", processInst.getState());
        map.put("startTime", processInst.getStartTime());
        map.put("endTime", processInst.getEndTime());
        map.put("runStatus", processInst.getRunStatus());
        return map;
    }

    private Map<String, Object> convertActivityInst(EIActivityInst activityInst) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("activityInstId", activityInst.getActivityInstId());
        map.put("processInstId", activityInst.getProcessInstId());
        map.put("processDefId", activityInst.getProcessDefId());
        map.put("activityDefId", activityInst.getActivityDefId());
        map.put("state", activityInst.getState());
        map.put("urgency", activityInst.getUrgency());
        map.put("arrivedTime", activityInst.getArrivedTime());
        map.put("limitTime", activityInst.getLimitTime());
        map.put("startTime", activityInst.getStartTime());
        map.put("receiveMethod", activityInst.getReceiveMethod());
        map.put("dealMethod", activityInst.getDealMethod());
        map.put("runStatus", activityInst.getRunStatus());
        return map;
    }

    private Map<String, Object> convertActivityInstHistory(EIActivityInstHistory history) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("activityInstHistoryId", history.getActivityHistoryId());
        map.put("processInstId", history.getProcessInstId());
        map.put("activityDefId", history.getActivityDefId());
        map.put("activityInstId", history.getActivityInstId());
        map.put("startTime", history.getStartTime());
        map.put("endTime", history.getEndTime());
        map.put("runStatus", history.getRunStatus());
        return map;
    }
}
