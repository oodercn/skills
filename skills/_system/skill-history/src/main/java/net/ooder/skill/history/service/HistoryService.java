package net.ooder.skill.history.service;

import net.ooder.skill.history.dto.HistoryDTO;
import net.ooder.skill.history.dto.HistoryStatisticsDTO;

import java.util.Map;

public interface HistoryService {

    PageResult<HistoryDTO> listMyHistory(String userId, Integer days, String category,
            String status, String keyword, int pageNum, int pageSize);

    HistoryDTO getExecutionDetail(String executionId, String userId);

    HistoryStatisticsDTO getStatistics(String userId, Integer days);

    boolean rerunScene(String userId, String sceneGroupId);

    byte[] exportHistory(String userId, Integer days, String category, String status);

    class PageResult<T> {
        private java.util.List<T> data;
        private int total;
        private int pageNum;
        private int pageSize;

        public PageResult(java.util.List<T> data, int total, int pageNum, int pageSize) {
            this.data = data; this.total = total; this.pageNum = pageNum; this.pageSize = pageSize;
        }
        public java.util.List<T> getData() { return data; }
        public int getTotal() { return total; }
        public int getPageNum() { return pageNum; }
        public int getPageSize() { return pageSize; }
    }
}
