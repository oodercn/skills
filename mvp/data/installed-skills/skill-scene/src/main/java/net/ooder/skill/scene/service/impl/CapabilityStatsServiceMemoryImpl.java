package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.dto.history.HistoryDTO;
import net.ooder.skill.scene.dto.stats.CapabilityStatsDTO;
import net.ooder.skill.scene.dto.stats.CapabilityRankDTO;
import net.ooder.skill.scene.service.CapabilityStatsService;
import net.ooder.skill.scene.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CapabilityStatsServiceMemoryImpl implements CapabilityStatsService {

    @Autowired
    private CapabilityService capabilityService;
    
    @Autowired
    private HistoryService historyService;

    @Override
    public CapabilityStatsDTO getOverviewStats() {
        CapabilityStatsDTO stats = new CapabilityStatsDTO();
        
        List<Capability> capabilities = capabilityService.findAll();
        stats.setTotalCapabilities(capabilities.size());
        
        int activeCount = 0;
        for (Capability cap : capabilities) {
            if (cap.getStatus() != null && "REGISTERED".equals(cap.getStatus().name())) {
                activeCount++;
            }
        }
        stats.setActiveCapabilities(activeCount);
        
        net.ooder.skill.scene.dto.PageResult<HistoryDTO> historyResult = 
            historyService.listMyHistory("current-user", 30, null, null, null, 1, 1000);
        List<HistoryDTO> historyList = historyResult.getList();
        
        int totalInvocations = historyList.size();
        int successInvocations = 0;
        int failedInvocations = 0;
        long totalDuration = 0;
        
        for (HistoryDTO h : historyList) {
            if ("success".equals(h.getStatus())) {
                successInvocations++;
            } else if ("failed".equals(h.getStatus())) {
                failedInvocations++;
            }
            totalDuration += h.getDuration();
        }
        
        stats.setTotalInvocations(totalInvocations);
        stats.setSuccessInvocations(successInvocations);
        stats.setFailedInvocations(failedInvocations);
        
        double avgResponseTime = totalInvocations > 0 
            ? (double) totalDuration / totalInvocations / 1000.0 
            : 0;
        stats.setAvgResponseTime(avgResponseTime);
        
        if (!historyList.isEmpty()) {
            historyList.sort((a, b) -> Long.compare(b.getStartTime(), a.getStartTime()));
            stats.setLastInvokeTime(historyList.get(0).getStartTime());
        } else {
            stats.setLastInvokeTime(System.currentTimeMillis());
        }
        
        return stats;
    }

    @Override
    public List<CapabilityRankDTO> getTopCapabilities(int limit) {
        List<Capability> capabilities = capabilityService.findAll();
        List<CapabilityRankDTO> result = new ArrayList<>();
        
        Map<String, Integer> invokeCounts = new HashMap<>();
        Map<String, Integer> successCounts = new HashMap<>();
        Map<String, Long> totalDurations = new HashMap<>();
        
        net.ooder.skill.scene.dto.PageResult<HistoryDTO> historyResult = 
            historyService.listMyHistory("current-user", 30, null, null, null, 1, 1000);
        List<HistoryDTO> historyList = historyResult.getList();
        
        for (HistoryDTO h : historyList) {
            String sceneGroupId = h.getSceneGroupId();
            invokeCounts.merge(sceneGroupId, 1, Integer::sum);
            if ("success".equals(h.getStatus())) {
                successCounts.merge(sceneGroupId, 1, Integer::sum);
            }
            if (h.getDuration() > 0) {
                totalDurations.merge(sceneGroupId, h.getDuration(), Long::sum);
            }
        }
        
        for (Capability cap : capabilities) {
            CapabilityRankDTO rank = new CapabilityRankDTO();
            rank.setCapabilityId(cap.getCapabilityId());
            rank.setName(cap.getName());
            rank.setType(cap.getType() != null ? cap.getType().name() : "SERVICE");
            
            String capId = cap.getCapabilityId();
            int invokeCount = invokeCounts.getOrDefault(capId, 0);
            int successCount = successCounts.getOrDefault(capId, 0);
            long totalDuration = totalDurations.getOrDefault(capId, 0L);
            
            rank.setInvokeCount(invokeCount);
            rank.setSuccessCount(successCount);
            rank.setAvgResponseTime(invokeCount > 0 ? (double) totalDuration / invokeCount / 1000.0 : 0);
            rank.setSuccessRate(invokeCount > 0 ? (double) successCount / invokeCount * 100 : 0);
            
            result.add(rank);
        }
        
        result.sort((a, b) -> Integer.compare(b.getInvokeCount(), a.getInvokeCount()));
        
        return result.size() > limit ? result.subList(0, limit) : result;
    }

    @Override
    public List<CapabilityRankDTO> getCapabilityRank(String sortBy, int limit) {
        return getTopCapabilities(limit);
    }

    @Override
    public List<String> getRecentErrors(int limit) {
        List<String> errors = new ArrayList<>();
        
        net.ooder.skill.scene.dto.PageResult<HistoryDTO> historyResult = 
            historyService.listMyHistory("current-user", 7, null, "failed", null, 1, limit);
        List<HistoryDTO> historyList = historyResult.getList();
        
        for (HistoryDTO h : historyList) {
            if (h.getErrorMessage() != null && !h.getErrorMessage().isEmpty()) {
                String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(h.getStartTime()));
                String errorLine = timeStr + " [ERROR] " + h.getSceneGroupId() + " " + 
                    h.getSceneGroupName() + " - " + h.getErrorMessage();
                errors.add(errorLine);
            }
        }
        
        if (errors.isEmpty()) {
            errors.add(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + 
                " [INFO] 暂无错误记录");
        }
        
        return errors.size() > limit ? errors.subList(0, limit) : errors;
    }

    @Override
    public List<Object> getRecentLogs(int limit) {
        List<Object> logs = new ArrayList<>();
        
        net.ooder.skill.scene.dto.PageResult<HistoryDTO> historyResult = 
            historyService.listMyHistory("current-user", 7, null, null, null, 1, limit);
        List<HistoryDTO> historyList = historyResult.getList();
        
        for (HistoryDTO h : historyList) {
            Map<String, Object> log = new HashMap<>();
            String level = "success".equals(h.getStatus()) ? "INFO" : 
                          "failed".equals(h.getStatus()) ? "ERROR" : 
                          "partial".equals(h.getStatus()) ? "WARN" : "INFO";
            log.put("level", level);
            log.put("capabilityId", h.getSceneGroupId());
            log.put("message", h.getSceneGroupName() + 
                (h.getErrorMessage() != null ? " - " + h.getErrorMessage() : ""));
            log.put("time", h.getStartTime());
            logs.add(log);
        }
        
        return logs.size() > limit ? logs.subList(0, limit) : logs;
    }
}
