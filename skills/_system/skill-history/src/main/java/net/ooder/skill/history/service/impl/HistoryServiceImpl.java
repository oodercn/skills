package net.ooder.skill.history.service.impl;

import net.ooder.skill.history.dto.HistoryDTO;
import net.ooder.skill.history.dto.HistoryStatisticsDTO;
import net.ooder.skill.history.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HistoryServiceImpl implements HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryServiceImpl.class);

    private final List<HistoryDTO> historyRecords = new ArrayList<>();

    public HistoryServiceImpl() {
        initTestData();
    }

    private void initTestData() {
        for (int i = 1; i <= 5; i++) {
            HistoryDTO history = new HistoryDTO();
            history.setExecutionId("exec-" + i);
            history.setSceneGroupId("scene-group-" + i);
            history.setSceneName("测试场景 " + i);
            history.setCategory("daily-report");
            history.setStatus(i % 2 == 0 ? "completed" : "failed");
            history.setStartTime(System.currentTimeMillis() - i * 3600000);
            history.setEndTime(System.currentTimeMillis() - i * 3600000 + 300000);
            history.setDuration(300000);
            history.setTriggerType("manual");
            history.setTotalSteps(3);
            history.setCompletedSteps(i % 2 == 0 ? 3 : 2);
            historyRecords.add(history);
        }
        log.info("HistoryServiceImpl initialized with {} records", historyRecords.size());
    }

    @Override
    public PageResult<HistoryDTO> listMyHistory(String userId, Integer days, String category,
            String status, String keyword, int pageNum, int pageSize) {

        List<HistoryDTO> filtered = new ArrayList<>(historyRecords);

        if (category != null && !category.isEmpty()) {
            filtered = filtered.stream().filter(h -> category.equals(h.getCategory())).toList();
        }
        if (status != null && !status.isEmpty()) {
            filtered = filtered.stream().filter(h -> status.equals(h.getStatus())).toList();
        }
        if (keyword != null && !keyword.isEmpty()) {
            filtered = filtered.stream().filter(h -> h.getSceneName().contains(keyword)).toList();
        }

        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<HistoryDTO> pageData = new ArrayList<>();
        if (start < total) {
            pageData = filtered.subList(start, end);
        }

        return new PageResult<>(pageData, total, pageNum, pageSize);
    }

    @Override
    public HistoryDTO getExecutionDetail(String executionId, String userId) {
        return historyRecords.stream()
            .filter(h -> executionId.equals(h.getExecutionId()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public HistoryStatisticsDTO getStatistics(String userId, Integer days) {
        HistoryStatisticsDTO stats = new HistoryStatisticsDTO();

        int total = historyRecords.size();
        int success = (int) historyRecords.stream().filter(h -> "completed".equals(h.getStatus())).count();

        stats.setTotalExecutions(total);
        stats.setSuccessCount(success);
        stats.setFailureCount(total - success);
        stats.setSuccessRate(total > 0 ? (double) success / total * 100 : 0);
        stats.setAvgDuration(250000);
        stats.setTodayExecutions(2);
        stats.setWeeklyExecutions(5);
        stats.setMonthlyExecutions(total);

        return stats;
    }

    @Override
    public boolean rerunScene(String userId, String sceneGroupId) {
        log.info("Rerunning scene {} for user {}", sceneGroupId, userId);
        return true;
    }

    @Override
    public byte[] exportHistory(String userId, Integer days, String category, String status) {
        StringBuilder csv = new StringBuilder();
        csv.append("执行ID,场景名称,分类,状态,开始时间,结束时间,耗时(ms)\n");

        for (HistoryDTO h : historyRecords) {
            csv.append(String.format("%s,%s,%s,%s,%d,%d,%d\n",
                h.getExecutionId(), h.getSceneName(), h.getCategory(),
                h.getStatus(), h.getStartTime(), h.getEndTime(), h.getDuration()));
        }

        return csv.toString().getBytes();
    }
}
