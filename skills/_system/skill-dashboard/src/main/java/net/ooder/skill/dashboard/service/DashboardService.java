package net.ooder.skill.dashboard.service;

import java.util.Map;

public interface DashboardService {
    
    Map<String, Object> getStats();
    
    Map<String, Object> getExecutionStats();
    
    Map<String, Object> getMarketStats();
    
    Map<String, Object> getSystemStats();
}
