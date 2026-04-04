package net.ooder.skill.bpm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import net.ooder.skill.bpm.model.ActivityInst;
import net.ooder.skill.bpm.model.ProcessInst;

public class ProcessInstService {

    private final Map<String, ProcessInst> processInstCache = new ConcurrentHashMap<>();
    private final Map<String, ActivityInst> activityInstCache = new ConcurrentHashMap<>();

    public ProcessInst createProcessInst(ProcessInst processInst) {
        if (processInst.getId() == null || processInst.getId().isEmpty()) {
            processInst.setId(generateProcessInstId());
        }
        processInst.setCreateTime(LocalDateTime.now());
        processInst.setUpdateTime(LocalDateTime.now());
        processInstCache.put(processInst.getId(), processInst);
        return processInst;
    }

    public ProcessInst getProcessInst(String processInstId) {
        return processInstCache.get(processInstId);
    }

    public List<ProcessInst> listProcessInsts() {
        return new ArrayList<>(processInstCache.values());
    }

    public List<ProcessInst> listProcessInstsByStarter(String starterId) {
        return processInstCache.values().stream()
                .filter(p -> starterId.equals(p.getStarterId()))
                .collect(Collectors.toList());
    }

    public List<ProcessInst> listRunningProcessInsts() {
        return processInstCache.values().stream()
                .filter(p -> "RUNNING".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    public List<ProcessInst> listCompletedProcessInsts() {
        return processInstCache.values().stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    public ProcessInst updateProcessInst(ProcessInst processInst) {
        processInst.setUpdateTime(LocalDateTime.now());
        processInstCache.put(processInst.getId(), processInst);
        return processInst;
    }

    public ProcessInst completeProcessInst(String processInstId) {
        ProcessInst inst = processInstCache.get(processInstId);
        if (inst != null) {
            inst.setStatus("COMPLETED");
            inst.setEndTime(LocalDateTime.now());
            inst.setUpdateTime(LocalDateTime.now());
        }
        return inst;
    }

    public ProcessInst terminateProcessInst(String processInstId, String reason) {
        ProcessInst inst = processInstCache.get(processInstId);
        if (inst != null) {
            inst.setStatus("TERMINATED");
            inst.setEndTime(LocalDateTime.now());
            inst.setUpdateTime(LocalDateTime.now());
        }
        return inst;
    }

    public ActivityInst createActivityInst(ActivityInst activityInst) {
        if (activityInst.getId() == null || activityInst.getId().isEmpty()) {
            activityInst.setId(generateActivityInstId());
        }
        activityInst.setCreateTime(LocalDateTime.now());
        activityInst.setUpdateTime(LocalDateTime.now());
        activityInstCache.put(activityInst.getId(), activityInst);
        return activityInst;
    }

    public ActivityInst getActivityInst(String activityInstId) {
        return activityInstCache.get(activityInstId);
    }

    public List<ActivityInst> listActivityInstsByProcess(String processInstId) {
        return activityInstCache.values().stream()
                .filter(a -> processInstId.equals(a.getProcessInstId()))
                .collect(Collectors.toList());
    }

    public List<ActivityInst> listPendingActivitiesByUser(String userId) {
        return activityInstCache.values().stream()
                .filter(a -> "PENDING".equals(a.getStatus()) && 
                        (userId.equals(a.getAssigneeId()) || 
                         (a.getPerformerId() != null && a.getPerformerId().contains(userId))))
                .collect(Collectors.toList());
    }

    public List<ActivityInst> listCompletedActivitiesByUser(String userId) {
        return activityInstCache.values().stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) && userId.equals(a.getAssigneeId()))
                .collect(Collectors.toList());
    }

    public ActivityInst completeActivityInst(String activityInstId, String userId, String result) {
        ActivityInst inst = activityInstCache.get(activityInstId);
        if (inst != null) {
            inst.setStatus("COMPLETED");
            inst.setAssigneeId(userId);
            inst.setResult(result);
            inst.setEndTime(LocalDateTime.now());
            inst.setCompleteTime(LocalDateTime.now());
            inst.setUpdateTime(LocalDateTime.now());
        }
        return inst;
    }

    public ActivityInst claimActivityInst(String activityInstId, String userId) {
        ActivityInst inst = activityInstCache.get(activityInstId);
        if (inst != null && "PENDING".equals(inst.getStatus())) {
            inst.setAssigneeId(userId);
            inst.setClaimTime(LocalDateTime.now());
            inst.setUpdateTime(LocalDateTime.now());
        }
        return inst;
    }

    private String generateProcessInstId() {
        return "PI_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    private String generateActivityInstId() {
        return "AI_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}
