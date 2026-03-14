package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.stats.CapabilityStatsDTO;
import net.ooder.skill.scene.dto.stats.CapabilityRankDTO;

import java.util.List;

public interface CapabilityStatsService {
    
    CapabilityStatsDTO getOverviewStats();
    
    List<CapabilityRankDTO> getTopCapabilities(int limit);
    
    List<CapabilityRankDTO> getCapabilityRank(String sortBy, int limit);
    
    List<String> getRecentErrors(int limit);
    
    List<Object> getRecentLogs(int limit);
}
