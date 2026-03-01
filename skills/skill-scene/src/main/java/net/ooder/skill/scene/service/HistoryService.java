package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.history.HistoryDTO;
import net.ooder.skill.scene.dto.history.HistoryStatisticsDTO;

public interface HistoryService {
    
    PageResult<HistoryDTO> listMyHistory(String userId, Integer days, String category, 
            String status, String keyword, int pageNum, int pageSize);
    
    HistoryStatisticsDTO getStatistics(String userId, Integer days);
    
    boolean rerunScene(String userId, String sceneGroupId);
    
    byte[] exportHistory(String userId, Integer days, String category, String status);
}
