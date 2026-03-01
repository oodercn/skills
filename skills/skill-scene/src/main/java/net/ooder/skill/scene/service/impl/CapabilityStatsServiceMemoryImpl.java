package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.stats.CapabilityStatsDTO;
import net.ooder.skill.scene.dto.stats.CapabilityRankDTO;
import net.ooder.skill.scene.service.CapabilityStatsService;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CapabilityStatsServiceMemoryImpl implements CapabilityStatsService {

    @Override
    public CapabilityStatsDTO getOverviewStats() {
        CapabilityStatsDTO stats = new CapabilityStatsDTO();
        stats.setTotalCapabilities(28);
        stats.setActiveCapabilities(24);
        stats.setTotalInvocations(1547);
        stats.setSuccessInvocations(1489);
        stats.setFailedInvocations(58);
        stats.setAvgResponseTime(127.5);
        stats.setLastInvokeTime(System.currentTimeMillis() - 300000);
        return stats;
    }

    @Override
    public List<CapabilityRankDTO> getTopCapabilities(int limit) {
        List<CapabilityRankDTO> list = new ArrayList<>();
        
        CapabilityRankDTO c1 = new CapabilityRankDTO();
        c1.setCapabilityId("cap-001");
        c1.setName("日志收集器");
        c1.setType("SERVICE");
        c1.setInvokeCount(523);
        c1.setSuccessCount(518);
        c1.setAvgResponseTime(45.2);
        c1.setSuccessRate(99.0);
        list.add(c1);
        
        CapabilityRankDTO c2 = new CapabilityRankDTO();
        c2.setCapabilityId("cap-002");
        c2.setName("数据分析器");
        c2.setType("AI");
        c2.setInvokeCount(412);
        c2.setSuccessCount(398);
        c2.setAvgResponseTime(234.5);
        c2.setSuccessRate(96.6);
        list.add(c2);
        
        CapabilityRankDTO c3 = new CapabilityRankDTO();
        c3.setCapabilityId("cap-003");
        c3.setName("通知推送");
        c3.setType("COMMUNICATION");
        c3.setInvokeCount(387);
        c3.setSuccessCount(382);
        c3.setAvgResponseTime(89.3);
        c3.setSuccessRate(98.7);
        list.add(c3);
        
        CapabilityRankDTO c4 = new CapabilityRankDTO();
        c4.setCapabilityId("cap-004");
        c4.setName("文件处理器");
        c4.setType("STORAGE");
        c4.setInvokeCount(225);
        c4.setSuccessCount(191);
        c4.setAvgResponseTime(156.8);
        c4.setSuccessRate(84.9);
        list.add(c4);
        
        return list.size() > limit ? list.subList(0, limit) : list;
    }

    @Override
    public List<CapabilityRankDTO> getCapabilityRank(String sortBy, int limit) {
        return getTopCapabilities(limit);
    }

    @Override
    public List<String> getRecentErrors(int limit) {
        List<String> errors = new ArrayList<>();
        errors.add("2026-03-01 17:25:32 [ERROR] cap-004 文件处理器 - 连接超时");
        errors.add("2026-03-01 17:20:15 [ERROR] cap-007 数据同步器 - 权限不足");
        errors.add("2026-03-01 17:15:08 [ERROR] cap-004 文件处理器 - 磁盘空间不足");
        errors.add("2026-03-01 17:10:22 [ERROR] cap-009 日志分析器 - 内存溢出");
        errors.add("2026-03-01 17:05:45 [ERROR] cap-004 文件处理器 - 文件不存在");
        return errors.size() > limit ? errors.subList(0, limit) : errors;
    }

    @Override
    public List<Object> getRecentLogs(int limit) {
        List<Object> logs = new ArrayList<>();
        logs.add(createLog("INFO", "cap-001", "日志收集器执行成功", System.currentTimeMillis() - 60000));
        logs.add(createLog("INFO", "cap-002", "数据分析器执行成功", System.currentTimeMillis() - 120000));
        logs.add(createLog("WARN", "cap-003", "通知推送延迟", System.currentTimeMillis() - 180000));
        logs.add(createLog("ERROR", "cap-004", "文件处理器连接超时", System.currentTimeMillis() - 240000));
        logs.add(createLog("INFO", "cap-005", "任务调度器执行成功", System.currentTimeMillis() - 300000));
        return logs.size() > limit ? logs.subList(0, limit) : logs;
    }
    
    private Map<String, Object> createLog(String level, String capId, String message, long time) {
        Map<String, Object> log = new HashMap<>();
        log.put("level", level);
        log.put("capabilityId", capId);
        log.put("message", message);
        log.put("time", time);
        return log;
    }
}
