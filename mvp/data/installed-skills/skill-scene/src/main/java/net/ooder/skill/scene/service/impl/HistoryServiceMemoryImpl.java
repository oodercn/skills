package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.history.HistoryDTO;
import net.ooder.skill.scene.dto.history.HistoryStatisticsDTO;
import net.ooder.skill.scene.service.HistoryService;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HistoryServiceMemoryImpl implements HistoryService {

    private final Map<String, List<HistoryDTO>> userHistory = new ConcurrentHashMap<>();

    public HistoryServiceMemoryImpl() {
        initMockData();
    }

    private void initMockData() {
        String userId = "current-user";
        List<HistoryDTO> history = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        HistoryDTO h1 = new HistoryDTO();
        h1.setExecutionId("exec-001");
        h1.setSceneGroupId("sg-dev-log");
        h1.setSceneGroupName("研发部日志汇报 - 第12周");
        h1.setCategory("business");
        h1.setStatus("success");
        h1.setParticipantCount(5);
        h1.setDuration(900000);
        h1.setStartTime(now - 86400000);
        h1.setEndTime(now - 86400000 + 900000);
        h1.setTriggerType("schedule");
        history.add(h1);
        
        HistoryDTO h2 = new HistoryDTO();
        h2.setExecutionId("exec-002");
        h2.setSceneGroupId("sg-weekly-report");
        h2.setSceneGroupName("项目周报汇总 - 第12周");
        h2.setCategory("collaboration");
        h2.setStatus("success");
        h2.setParticipantCount(8);
        h2.setDuration(1320000);
        h2.setStartTime(now - 86400000 * 2);
        h2.setEndTime(now - 86400000 * 2 + 1320000);
        h2.setTriggerType("manual");
        history.add(h2);
        
        HistoryDTO h3 = new HistoryDTO();
        h3.setExecutionId("exec-003");
        h3.setSceneGroupId("sg-project-alpha");
        h3.setSceneGroupName("项目Alpha协作组 - 需求评审");
        h3.setCategory("business");
        h3.setStatus("partial");
        h3.setParticipantCount(6);
        h3.setDuration(3600000);
        h3.setStartTime(now - 86400000 * 3);
        h3.setEndTime(now - 86400000 * 3 + 3600000);
        h3.setTriggerType("manual");
        h3.setErrorMessage("2人未完成提交");
        history.add(h3);
        
        HistoryDTO h4 = new HistoryDTO();
        h4.setExecutionId("exec-004");
        h4.setSceneGroupId("sg-hr-team");
        h4.setSceneGroupName("HR团队组 - 月度考勤统计");
        h4.setCategory("governance");
        h4.setStatus("success");
        h4.setParticipantCount(4);
        h4.setDuration(1800000);
        h4.setStartTime(now - 86400000 * 5);
        h4.setEndTime(now - 86400000 * 5 + 1800000);
        h4.setTriggerType("schedule");
        history.add(h4);
        
        HistoryDTO h5 = new HistoryDTO();
        h5.setExecutionId("exec-005");
        h5.setSceneGroupId("sg-iot-monitor");
        h5.setSceneGroupName("物联网设备监控 - 日报");
        h5.setCategory("iot");
        h5.setStatus("failed");
        h5.setParticipantCount(3);
        h5.setDuration(600000);
        h5.setStartTime(now - 86400000 * 7);
        h5.setEndTime(now - 86400000 * 7 + 600000);
        h5.setTriggerType("schedule");
        h5.setErrorMessage("设备连接超时");
        history.add(h5);
        
        userHistory.put(userId, history);
    }

    @Override
    public PageResult<HistoryDTO> listMyHistory(String userId, Integer days, String category, 
            String status, String keyword, int pageNum, int pageSize) {
        
        List<HistoryDTO> history = userHistory.getOrDefault(userId, new ArrayList<>());
        
        if (days != null && days > 0) {
            long cutoff = System.currentTimeMillis() - (days * 86400000L);
            history = history.stream()
                .filter(h -> h.getStartTime() >= cutoff)
                .collect(Collectors.toList());
        }
        
        if (category != null && !category.isEmpty()) {
            history = history.stream()
                .filter(h -> category.equals(h.getCategory()))
                .collect(Collectors.toList());
        }
        
        if (status != null && !status.isEmpty()) {
            history = history.stream()
                .filter(h -> status.equals(h.getStatus()))
                .collect(Collectors.toList());
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            history = history.stream()
                .filter(h -> h.getSceneGroupName() != null && 
                    h.getSceneGroupName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
        }
        
        history.sort((a, b) -> Long.compare(b.getStartTime(), a.getStartTime()));
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, history.size());
        
        List<HistoryDTO> pagedHistory = start < history.size() 
            ? history.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<HistoryDTO> result = new PageResult<>();
        result.setList(pagedHistory);
        result.setTotal(history.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public HistoryStatisticsDTO getStatistics(String userId, Integer days) {
        List<HistoryDTO> history = userHistory.getOrDefault(userId, new ArrayList<>());
        
        if (days != null && days > 0) {
            long cutoff = System.currentTimeMillis() - (days * 86400000L);
            history = history.stream()
                .filter(h -> h.getStartTime() >= cutoff)
                .collect(Collectors.toList());
        }
        
        int totalScenes = history.size();
        int participateCount = history.stream().mapToInt(HistoryDTO::getParticipantCount).sum();
        int successCount = (int) history.stream().filter(h -> "success".equals(h.getStatus())).count();
        int successRate = totalScenes > 0 ? (successCount * 100 / totalScenes) : 0;
        long totalDuration = history.stream().mapToLong(HistoryDTO::getDuration).sum();
        int avgDuration = totalScenes > 0 ? (int) (totalDuration / totalScenes / 60000) : 0;
        
        return new HistoryStatisticsDTO(totalScenes, participateCount, successRate, avgDuration);
    }

    @Override
    public boolean rerunScene(String userId, String sceneGroupId) {
        return true;
    }

    @Override
    public byte[] exportHistory(String userId, Integer days, String category, String status) {
        List<HistoryDTO> history = userHistory.getOrDefault(userId, new ArrayList<>());
        
        StringBuilder sb = new StringBuilder();
        sb.append("执行ID,场景组ID,场景名称,分类,状态,参与人数,耗时(分钟),开始时间,结束时间,触发类型,错误信息\n");
        
        for (HistoryDTO h : history) {
            sb.append(h.getExecutionId()).append(",");
            sb.append(h.getSceneGroupId()).append(",");
            sb.append(h.getSceneGroupName()).append(",");
            sb.append(h.getCategory()).append(",");
            sb.append(h.getStatus()).append(",");
            sb.append(h.getParticipantCount()).append(",");
            sb.append(h.getDuration() / 60000).append(",");
            sb.append(h.getStartTime() != null ? new Date(h.getStartTime()) : "").append(",");
            sb.append(h.getEndTime() != null ? new Date(h.getEndTime()) : "").append(",");
            sb.append(h.getTriggerType()).append(",");
            sb.append(h.getErrorMessage() != null ? h.getErrorMessage() : "").append("\n");
        }
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
