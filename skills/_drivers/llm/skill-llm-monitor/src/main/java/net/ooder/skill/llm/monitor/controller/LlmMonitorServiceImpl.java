package net.ooder.skill.llm.monitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import net.ooder.skill.llm.monitor.dto.*;

@Service
public class LlmMonitorServiceImpl implements LlmMonitorService {

    private static final Logger log = LoggerFactory.getLogger(LlmMonitorServiceImpl.class);
    
    private final List<LlmCallLogDTO> callLogs = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, LlmCallLogDTO> logById = new HashMap<>();

    @Override
    public Map<String, Object> getStats(String timeRange, String providerId) {
        log.info("[getStats] timeRange: {}, providerId: {}", timeRange, providerId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", callLogs.size());
        stats.put("successCalls", callLogs.stream().filter(l -> "success".equals(l.getStatus())).count());
        stats.put("failedCalls", callLogs.stream().filter(l -> "failed".equals(l.getStatus())).count());
        stats.put("totalTokens", callLogs.stream().mapToLong(l -> l.getTotalTokens() != null ? l.getTotalTokens() : 0).sum());
        stats.put("totalCost", callLogs.stream().mapToDouble(l -> l.getCost() != null ? l.getCost() : 0.0).sum());
        stats.put("avgLatency", callLogs.isEmpty() ? 0 : callLogs.stream().mapToLong(l -> l.getLatency() != null ? l.getLatency() : 0).average().orElse(0));
        return stats;
    }

    @Override
    public PageResult<LlmCallLogDTO> getLogs(String providerId, String status, String model, int pageNum, int pageSize) {
        log.info("[getLogs] providerId: {}, status: {}, model: {}, pageNum: {}, pageSize: {}", providerId, status, model, pageNum, pageSize);
        
        List<LlmCallLogDTO> filtered = new ArrayList<>(callLogs);
        if (providerId != null && !providerId.isEmpty()) {
            filtered.removeIf(l -> !providerId.equals(l.getProviderId()));
        }
        if (status != null && !status.isEmpty()) {
            filtered.removeIf(l -> !status.equals(l.getStatus()));
        }
        if (model != null && !model.isEmpty()) {
            filtered.removeIf(l -> !model.equals(l.getModel()));
        }
        
        Collections.reverse(filtered);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        
        PageResult<LlmCallLogDTO> result = new PageResult<>();
        result.setList(start < filtered.size() ? filtered.subList(start, end) : Collections.emptyList());
        result.setTotal(filtered.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public LlmCallLogDTO getLogById(String logId) {
        return logById.get(logId);
    }

    @Override
    public List<ProviderStatsDTO> getProviderStats() {
        log.info("[getProviderStats]");
        List<ProviderStatsDTO> stats = new ArrayList<>();
        Map<String, List<LlmCallLogDTO>> byProvider = new HashMap<>();
        for (LlmCallLogDTO log : callLogs) {
            byProvider.computeIfAbsent(log.getProviderId(), k -> new ArrayList<>()).add(log);
        }
        for (Map.Entry<String, List<LlmCallLogDTO>> entry : byProvider.entrySet()) {
            ProviderStatsDTO dto = new ProviderStatsDTO();
            dto.setProviderId(entry.getKey());
            dto.setTotalCalls(entry.getValue().size());
            dto.setSuccessCalls((int) entry.getValue().stream().filter(l -> "success".equals(l.getStatus())).count());
            dto.setFailedCalls((int) entry.getValue().stream().filter(l -> "failed".equals(l.getStatus())).count());
            dto.setTotalTokens(entry.getValue().stream().mapToLong(l -> l.getTotalTokens() != null ? l.getTotalTokens() : 0).sum());
            stats.add(dto);
        }
        return stats;
    }

    @Override
    public Map<String, Object> getEngineStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("sdkAvailable", true);
        status.put("monitorEnabled", true);
        status.put("dataSource", "memory");
        status.put("logCount", callLogs.size());
        return status;
    }

    @Override
    public void clearLogs() {
        callLogs.clear();
        logById.clear();
        log.info("[clearLogs] All logs cleared");
    }

    @Override
    public CompanyLlmStatsDTO getCompanyStats(String companyId, StatsTimeRange range) {
        CompanyLlmStatsDTO dto = new CompanyLlmStatsDTO();
        dto.setCompanyId(companyId);
        dto.setTotalCalls(callLogs.size());
        return dto;
    }

    @Override
    public DepartmentLlmStatsDTO getDepartmentStats(String departmentId, StatsTimeRange range) {
        DepartmentLlmStatsDTO dto = new DepartmentLlmStatsDTO();
        dto.setDepartmentId(departmentId);
        dto.setTotalCalls(callLogs.size());
        return dto;
    }

    @Override
    public UserLlmStatsDTO getUserStats(String userId, StatsTimeRange range) {
        UserLlmStatsDTO dto = new UserLlmStatsDTO();
        dto.setUserId(userId);
        dto.setTotalCalls(callLogs.size());
        return dto;
    }

    @Override
    public ModuleLlmStatsDTO getModuleStats(String moduleId, String userId, StatsTimeRange range) {
        ModuleLlmStatsDTO dto = new ModuleLlmStatsDTO();
        dto.setModuleId(moduleId);
        dto.setTotalCalls(callLogs.size());
        return dto;
    }

    @Override
    public List<DepartmentLlmStatsDTO> getDepartmentRanking(String companyId, int topN, String orderBy) {
        return Collections.emptyList();
    }

    @Override
    public List<UserLlmStatsDTO> getUserRanking(String departmentId, int topN, String orderBy) {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleLlmStatsDTO> getModuleRanking(String userId, int topN, String orderBy) {
        return Collections.emptyList();
    }

    @Override
    public OverallStatsDTO getOverallStats(String companyId, StatsTimeRange range) {
        OverallStatsDTO dto = new OverallStatsDTO();
        dto.setTotalCalls(callLogs.size());
        dto.setTotalTokens(callLogs.stream().mapToLong(l -> l.getTotalTokens() != null ? l.getTotalTokens() : 0).sum());
        dto.setTotalCost(callLogs.stream().mapToDouble(l -> l.getCost() != null ? l.getCost() : 0.0).sum());
        return dto;
    }
}
