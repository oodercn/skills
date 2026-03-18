package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.audit.AuditEventType;
import net.ooder.mvp.skill.scene.dto.audit.AuditLogDTO;
import net.ooder.mvp.skill.scene.dto.audit.AuditResultType;
import net.ooder.mvp.skill.scene.dto.audit.AuditStatsDTO;
import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.service.AuditService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuditServiceMemoryImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceMemoryImpl.class);
    
    private final Map<String, AuditLogDTO> logStore = new ConcurrentHashMap<>();
    
    public AuditServiceMemoryImpl() {
        initSampleData();
    }
    
    private void initSampleData() {
        long now = System.currentTimeMillis();
        
        addSampleLog("audit-001", AuditEventType.SCENE_START, AuditResultType.SUCCESS, 
            "user-001", null, "scene", "scene-001", "启动场景", now - 3600000);
        addSampleLog("audit-002", AuditEventType.CAPABILITY_INVOKE, AuditResultType.SUCCESS, 
            "user-001", "agent-001", "capability", "cap-email", "调用邮件通知能力", now - 3500000);
        addSampleLog("audit-003", AuditEventType.CAPABILITY_INVOKE, AuditResultType.FAILURE, 
            "user-001", "agent-001", "capability", "cap-sms", "调用短信通知能力失败", now - 3400000);
        addSampleLog("audit-004", AuditEventType.KEY_MANAGEMENT, AuditResultType.SUCCESS, 
            "user-001", null, "key", "key-001", "创建密钥", now - 3000000);
        addSampleLog("audit-005", AuditEventType.PERMISSION_CHANGE, AuditResultType.DENIED, 
            "user-002", null, "permission", "perm-001", "权限变更被拒绝", now - 2500000);
        addSampleLog("audit-006", AuditEventType.AGENT_INVOKE, AuditResultType.SUCCESS, 
            "user-001", "agent-002", "agent", "agent-super-001", "调用超级Agent", now - 2000000);
        addSampleLog("audit-007", AuditEventType.LLM_CALL, AuditResultType.SUCCESS, 
            "user-001", "agent-001", "llm", "llm-gpt4", "调用GPT-4模型", now - 1500000);
        addSampleLog("audit-008", AuditEventType.SCENE_STOP, AuditResultType.SUCCESS, 
            "user-001", null, "scene", "scene-001", "停止场景", now - 1000000);
        addSampleLog("audit-009", AuditEventType.DATA_ACCESS, AuditResultType.SUCCESS, 
            "user-001", "agent-001", "data", "data-report-001", "访问报表数据", now - 500000);
        addSampleLog("audit-010", AuditEventType.CONFIG_CHANGE, AuditResultType.SUCCESS, 
            "admin", null, "config", "config-audit", "更新审计配置", now - 100000);
    }
    
    private void addSampleLog(String recordId, AuditEventType eventType, AuditResultType result,
            String userId, String agentId, String resourceType, String resourceId, 
            String action, long timestamp) {
        AuditLogDTO log = new AuditLogDTO();
        log.setRecordId(recordId);
        log.setEventType(eventType);
        log.setResult(result);
        log.setUserId(userId);
        log.setAgentId(agentId);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setAction(action);
        log.setDetail(action + "详情信息");
        log.setTimestamp(timestamp);
        log.setIpAddress("192.168.1." + (int)(Math.random() * 255));
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "system");
        metadata.put("version", "1.0");
        log.setMetadata(metadata);
        
        logStore.put(recordId, log);
    }

    @Override
    public PageResult<AuditLogDTO> listLogs(String eventType, String result, String userId, 
            String resourceId, Long startTime, Long endTime, int pageNum, int pageSize) {
        
        List<AuditLogDTO> filtered = logStore.values().stream()
            .filter(log -> eventType == null || eventType.isEmpty() || 
                (log.getEventType() != null && log.getEventType().getCode().equals(eventType)))
            .filter(log -> result == null || result.isEmpty() || 
                (log.getResult() != null && log.getResult().getCode().equals(result)))
            .filter(log -> userId == null || userId.isEmpty() || userId.equals(log.getUserId()))
            .filter(log -> resourceId == null || resourceId.isEmpty() || resourceId.equals(log.getResourceId()))
            .filter(log -> startTime == null || log.getTimestamp() >= startTime)
            .filter(log -> endTime == null || log.getTimestamp() <= endTime)
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
        
        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<AuditLogDTO> pageData = start < total ? filtered.subList(start, end) : new ArrayList<>();
        
        PageResult<AuditLogDTO> pageResult = new PageResult<>();
        pageResult.setList(pageData);
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((total + pageSize - 1) / pageSize);
        
        return pageResult;
    }

    @Override
    public AuditLogDTO getLogById(String recordId) {
        return logStore.get(recordId);
    }

    @Override
    public AuditStatsDTO getStats() {
        AuditStatsDTO stats = new AuditStatsDTO();
        
        List<AuditLogDTO> allLogs = new ArrayList<>(logStore.values());
        
        stats.setTotalEvents(allLogs.size());
        
        long successCount = allLogs.stream()
            .filter(log -> log.getResult() == AuditResultType.SUCCESS)
            .count();
        stats.setSuccessCount(successCount);
        
        long failureCount = allLogs.stream()
            .filter(log -> log.getResult() == AuditResultType.FAILURE)
            .count();
        stats.setFailureCount(failureCount);
        
        long deniedCount = allLogs.stream()
            .filter(log -> log.getResult() == AuditResultType.DENIED)
            .count();
        stats.setDeniedCount(deniedCount);
        
        long now = System.currentTimeMillis();
        long todayStart = now - (now % 86400000);
        long weekStart = now - 7 * 86400000;
        long monthStart = now - 30L * 86400000;
        
        long todayCount = allLogs.stream()
            .filter(log -> log.getTimestamp() >= todayStart)
            .count();
        stats.setTodayCount(todayCount);
        
        long weekCount = allLogs.stream()
            .filter(log -> log.getTimestamp() >= weekStart)
            .count();
        stats.setWeekCount(weekCount);
        
        long monthCount = allLogs.stream()
            .filter(log -> log.getTimestamp() >= monthStart)
            .count();
        stats.setMonthCount(monthCount);
        
        return stats;
    }

    @Override
    public void logEvent(AuditLogDTO log) {
        if (log.getRecordId() == null || log.getRecordId().isEmpty()) {
            log.setRecordId("audit-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (log.getTimestamp() == 0) {
            log.setTimestamp(System.currentTimeMillis());
        }
        
        logStore.put(log.getRecordId(), log);
        logger.info("Audit log recorded: {} - {} - {}", 
            log.getEventType(), log.getAction(), log.getResult());
    }

    @Override
    public void exportLogs(String eventType, String result, String userId, 
            String resourceId, Long startTime, Long endTime) {
        logger.info("Exporting audit logs with filters: eventType={}, result={}, userId={}, resourceId={}", 
            eventType, result, userId, resourceId);
    }
}
