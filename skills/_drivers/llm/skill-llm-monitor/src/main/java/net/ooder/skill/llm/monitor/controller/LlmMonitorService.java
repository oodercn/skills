package net.ooder.skill.llm.monitor.controller;

import java.util.*;
import net.ooder.skill.llm.monitor.dto.*;

public interface LlmMonitorService {
    
    Map<String, Object> getStats(String timeRange, String providerId);
    
    PageResult<LlmCallLogDTO> getLogs(String providerId, String status, String model, int pageNum, int pageSize);
    
    LlmCallLogDTO getLogById(String logId);
    
    List<ProviderStatsDTO> getProviderStats();
    
    Map<String, Object> getEngineStatus();
    
    void clearLogs();
    
    CompanyLlmStatsDTO getCompanyStats(String companyId, StatsTimeRange range);
    
    DepartmentLlmStatsDTO getDepartmentStats(String departmentId, StatsTimeRange range);
    
    UserLlmStatsDTO getUserStats(String userId, StatsTimeRange range);
    
    ModuleLlmStatsDTO getModuleStats(String moduleId, String userId, StatsTimeRange range);
    
    List<DepartmentLlmStatsDTO> getDepartmentRanking(String companyId, int topN, String orderBy);
    
    List<UserLlmStatsDTO> getUserRanking(String departmentId, int topN, String orderBy);
    
    List<ModuleLlmStatsDTO> getModuleRanking(String userId, int topN, String orderBy);
    
    OverallStatsDTO getOverallStats(String companyId, StatsTimeRange range);
}
