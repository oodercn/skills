package net.ooder.skill.llm.monitor.controller;

import net.ooder.skill.llm.monitor.model.ResultModel;
import net.ooder.skill.llm.monitor.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm/monitor")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class LlmMonitorController {

    private static final Logger log = LoggerFactory.getLogger(LlmMonitorController.class);
    
    @Autowired(required = false)
    private LlmMonitorService monitorService;

    @GetMapping("/stats")
    public ResultModel<LlmMonitorStatsDTO> getStats(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String providerId) {
        log.info("[getStats] timeRange: {}, providerId: {}", timeRange, providerId);
        
        if (monitorService == null) {
            return ResultModel.success(new LlmMonitorStatsDTO());
        }
        
        Map<String, Object> rawStats = monitorService.getStats(timeRange, providerId);
        LlmMonitorStatsDTO stats = new LlmMonitorStatsDTO();
        if (rawStats != null) {
            stats.setDetails(rawStats);
        }
        return ResultModel.success(stats);
    }
    
    @GetMapping("/logs")
    public ResultModel<PageResult<LlmCallLogDTO>> getLogs(
            @RequestParam(required = false) String providerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[getLogs] providerId: {}, status: {}, pageNum: {}, pageSize: {}", providerId, status, pageNum, pageSize);
        
        if (monitorService == null) {
            return ResultModel.success(new PageResult<>());
        }
        
        PageResult<LlmCallLogDTO> result = monitorService.getLogs(providerId, status, model, pageNum, pageSize);
        return ResultModel.success(result);
    }
    
    @GetMapping("/logs/{logId}")
    public ResultModel<LlmCallLogDTO> getLogDetail(@PathVariable String logId) {
        log.info("[getLogDetail] logId: {}", logId);
        
        if (monitorService == null) {
            return ResultModel.notFound("Monitor service not available");
        }
        
        LlmCallLogDTO logEntry = monitorService.getLogById(logId);
        if (logEntry != null) {
            return ResultModel.success(logEntry);
        }
        
        return ResultModel.notFound("Log not found: " + logId);
    }
    
    @GetMapping("/provider-stats")
    public ResultModel<List<ProviderStatsDTO>> getProviderStats() {
        log.info("[getProviderStats] request start");
        
        if (monitorService == null) {
            return ResultModel.success(List.of());
        }
        
        List<ProviderStatsDTO> result = monitorService.getProviderStats();
        return ResultModel.success(result);
    }
    
    @GetMapping("/sessions")
    public ResultModel<List<LlmSessionDTO>> getSessions(
            @RequestParam(required = false) String providerId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[getSessions] providerId: {}, pageNum: {}, pageSize: {}", providerId, pageNum, pageSize);
        
        List<LlmSessionDTO> sessions = new ArrayList<>();
        return ResultModel.success(sessions);
    }
    
    @GetMapping("/logs/session/{sessionId}")
    public ResultModel<List<LlmCallLogDTO>> getSessionLogs(@PathVariable String sessionId) {
        log.info("[getSessionLogs] sessionId: {}", sessionId);
        
        List<LlmCallLogDTO> logs = new ArrayList<>();
        return ResultModel.success(logs);
    }
    
    @GetMapping("/engine/status")
    public ResultModel<EngineStatusDTO> getEngineStatus() {
        log.info("[getEngineStatus] request start");
        
        EngineStatusDTO status = new EngineStatusDTO();
        
        if (monitorService != null) {
            Map<String, Object> rawStatus = monitorService.getEngineStatus();
            status.setSdkAvailable(Boolean.TRUE.equals(rawStatus.get("sdkAvailable")));
            status.setMonitorEnabled(Boolean.TRUE.equals(rawStatus.get("monitorEnabled")));
            status.setDataSource(rawStatus.get("dataSource") != null ? rawStatus.get("dataSource").toString() : "available");
            status.setExtensions(rawStatus);
        } else {
            status.setSdkAvailable(false);
            status.setMonitorEnabled(false);
            status.setDataSource("unavailable");
        }
        
        return ResultModel.success(status);
    }
    
    @DeleteMapping("/logs")
    public ResultModel<Boolean> clearLogs() {
        log.info("[clearLogs] request start");
        
        if (monitorService == null) {
            return ResultModel.success(true);
        }
        
        monitorService.clearLogs();
        return ResultModel.success(true);
    }
    
    @GetMapping("/stats/company/{companyId}")
    public ResultModel<CompanyLlmStatsDTO> getCompanyStats(
            @PathVariable String companyId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        log.info("[getCompanyStats] companyId: {}", companyId);
        
        if (monitorService == null) {
            return ResultModel.success(new CompanyLlmStatsDTO());
        }
        
        StatsTimeRange range = buildTimeRange(startTime, endTime);
        CompanyLlmStatsDTO stats = monitorService.getCompanyStats(companyId, range);
        return ResultModel.success(stats);
    }
    
    @GetMapping("/stats/department/{departmentId}")
    public ResultModel<DepartmentLlmStatsDTO> getDepartmentStats(
            @PathVariable String departmentId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        log.info("[getDepartmentStats] departmentId: {}", departmentId);
        
        if (monitorService == null) {
            return ResultModel.success(new DepartmentLlmStatsDTO());
        }
        
        StatsTimeRange range = buildTimeRange(startTime, endTime);
        DepartmentLlmStatsDTO stats = monitorService.getDepartmentStats(departmentId, range);
        return ResultModel.success(stats);
    }
    
    @GetMapping("/stats/user/{userId}")
    public ResultModel<UserLlmStatsDTO> getUserStats(
            @PathVariable String userId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        log.info("[getUserStats] userId: {}", userId);
        
        if (monitorService == null) {
            return ResultModel.success(new UserLlmStatsDTO());
        }
        
        StatsTimeRange range = buildTimeRange(startTime, endTime);
        UserLlmStatsDTO stats = monitorService.getUserStats(userId, range);
        return ResultModel.success(stats);
    }
    
    @GetMapping("/stats/module/{moduleId}")
    public ResultModel<ModuleLlmStatsDTO> getModuleStats(
            @PathVariable String moduleId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        log.info("[getModuleStats] moduleId: {}, userId: {}", moduleId, userId);
        
        if (monitorService == null) {
            return ResultModel.success(new ModuleLlmStatsDTO());
        }
        
        StatsTimeRange range = buildTimeRange(startTime, endTime);
        ModuleLlmStatsDTO stats = monitorService.getModuleStats(moduleId, userId, range);
        return ResultModel.success(stats);
    }
    
    @GetMapping("/ranking/departments")
    public ResultModel<List<DepartmentLlmStatsDTO>> getDepartmentRanking(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "10") int topN,
            @RequestParam(defaultValue = "calls") String orderBy) {
        log.info("[getDepartmentRanking] companyId: {}, topN: {}, orderBy: {}", companyId, topN, orderBy);
        
        if (monitorService == null) {
            return ResultModel.success(List.of());
        }
        
        List<DepartmentLlmStatsDTO> ranking = monitorService.getDepartmentRanking(companyId, topN, orderBy);
        return ResultModel.success(ranking);
    }
    
    @GetMapping("/ranking/users")
    public ResultModel<List<UserLlmStatsDTO>> getUserRanking(
            @RequestParam(required = false) String departmentId,
            @RequestParam(defaultValue = "10") int topN,
            @RequestParam(defaultValue = "calls") String orderBy) {
        log.info("[getUserRanking] departmentId: {}, topN: {}, orderBy: {}", departmentId, topN, orderBy);
        
        if (monitorService == null) {
            return ResultModel.success(List.of());
        }
        
        List<UserLlmStatsDTO> ranking = monitorService.getUserRanking(departmentId, topN, orderBy);
        return ResultModel.success(ranking);
    }
    
    @GetMapping("/ranking/modules")
    public ResultModel<List<ModuleLlmStatsDTO>> getModuleRanking(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "10") int topN,
            @RequestParam(defaultValue = "calls") String orderBy) {
        log.info("[getModuleRanking] userId: {}, topN: {}, orderBy: {}", userId, topN, orderBy);
        
        if (monitorService == null) {
            return ResultModel.success(List.of());
        }
        
        List<ModuleLlmStatsDTO> ranking = monitorService.getModuleRanking(userId, topN, orderBy);
        return ResultModel.success(ranking);
    }
    
    @GetMapping("/overall-stats")
    public ResultModel<OverallStatsDTO> getOverallStats(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        log.info("[getOverallStats] companyId: {}", companyId);
        
        if (monitorService == null) {
            return ResultModel.success(new OverallStatsDTO());
        }
        
        StatsTimeRange range = buildTimeRange(startTime, endTime);
        OverallStatsDTO stats = monitorService.getOverallStats(companyId, range);
        return ResultModel.success(stats);
    }
    
    private StatsTimeRange buildTimeRange(Long startTime, Long endTime) {
        if (startTime == null && endTime == null) {
            return null;
        }
        
        long end = endTime != null ? endTime : System.currentTimeMillis();
        long start = startTime != null ? startTime : end - 7 * 24 * 60 * 60 * 1000L;
        
        return new StatsTimeRange(start, end);
    }
}
