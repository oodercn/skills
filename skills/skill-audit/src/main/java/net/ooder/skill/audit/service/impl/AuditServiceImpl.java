package net.ooder.skill.audit.service.impl;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 审计日志服务实现类
 * 
 * <p>提供审计日志的核心业务逻辑实现，使用内存存储（ConcurrentHashMap）作为临时存储方案。
 * 生产环境建议替换为持久化存储（如数据库、Elasticsearch等）。</p>
 * 
 * <h3>实现特点：</h3>
 * <ul>
 *   <li>线程安全：使用ConcurrentHashMap保证并发安全</li>
 *   <li>内存存储：适合开发测试，生产环境需替换</li>
 *   <li>流式处理：使用Java 8 Stream API进行数据过滤和统计</li>
 * </ul>
 * 
 * <h3>扩展建议：</h3>
 * <ul>
 *   <li>替换为数据库存储（MySQL、PostgreSQL等）</li>
 *   <li>集成Elasticsearch实现高效全文检索</li>
 *   <li>添加日志归档和清理策略</li>
 *   <li>实现日志加密和防篡改机制</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@Service
public class AuditServiceImpl implements AuditService {

    /**
     * 审计日志存储
     * 
     * <p>使用ConcurrentHashMap实现线程安全的内存存储。
     * Key为日志ID，Value为审计日志对象。</p>
     * 
     * <p>注意：这是临时存储方案，服务重启后数据会丢失。
     * 生产环境应替换为持久化存储。</p>
     */
    private final Map<String, AuditLog> logs = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     * 
     * <p>实现说明：</p>
     * <ul>
     *   <li>自动生成日志ID（格式：audit-{UUID前8位}）</li>
     *   <li>自动设置时间戳（如果未设置）</li>
     *   <li>使用ConcurrentHashMap保证线程安全</li>
     * </ul>
     */
    @Override
    public AuditLog record(AuditLog log) {
        if (log.getLogId() == null || log.getLogId().isEmpty()) {
            log.setLogId("audit-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (log.getTimestamp() == 0) {
            log.setTimestamp(System.currentTimeMillis());
        }
        logs.put(log.getLogId(), log);
        return log;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLog getById(String logId) {
        return logs.get(logId);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>实现说明：</p>
     * <ul>
     *   <li>使用Stream API进行多条件过滤</li>
     *   <li>支持按时间戳升序/降序排序</li>
     *   <li>支持分页查询</li>
     * </ul>
     * 
     * <p>性能考虑：当前实现为内存过滤，大数据量时建议使用索引优化。</p>
     */
    @Override
    public AuditQueryResult query(AuditQueryRequest request) {
        List<AuditLog> filtered = logs.values().stream()
                .filter(log -> request.getUserId() == null || request.getUserId().equals(log.getUserId()))
                .filter(log -> request.getAction() == null || request.getAction().equals(log.getAction()))
                .filter(log -> request.getResourceType() == null || request.getResourceType().equals(log.getResourceType()))
                .filter(log -> request.getResourceId() == null || request.getResourceId().equals(log.getResourceId()))
                .filter(log -> request.getResult() == null || request.getResult().equals(log.getResult()))
                .filter(log -> request.getStartTime() == null || log.getTimestamp() >= request.getStartTime())
                .filter(log -> request.getEndTime() == null || log.getTimestamp() <= request.getEndTime())
                .sorted((a, b) -> "asc".equals(request.getSortOrder()) 
                        ? Long.compare(a.getTimestamp(), b.getTimestamp())
                        : Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
        
        long total = filtered.size();
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), filtered.size());
        List<AuditLog> pageItems = start < filtered.size() 
                ? filtered.subList(start, end) 
                : new ArrayList<>();
        
        return new AuditQueryResult(pageItems, request.getPage(), request.getSize(), total);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>统计维度：</p>
     * <ul>
     *   <li>总量统计：总日志数、今日日志数</li>
     *   <li>结果统计：成功次数、失败次数</li>
     *   <li>分布统计：操作类型分布、资源类型分布、用户活跃度</li>
     * </ul>
     */
    @Override
    public AuditStatistics getStatistics(Long startTime, Long endTime) {
        AuditStatistics stats = new AuditStatistics();
        
        List<AuditLog> filtered = logs.values().stream()
                .filter(log -> startTime == null || log.getTimestamp() >= startTime)
                .filter(log -> endTime == null || log.getTimestamp() <= endTime)
                .collect(Collectors.toList());
        
        stats.setTotalLogs(filtered.size());
        
        long todayStart = getTodayStart();
        stats.setTodayLogs(filtered.stream()
                .filter(log -> log.getTimestamp() >= todayStart)
                .count());
        
        stats.setSuccessCount(filtered.stream()
                .filter(log -> "success".equals(log.getResult()))
                .count());
        
        stats.setFailureCount(filtered.stream()
                .filter(log -> "failure".equals(log.getResult()))
                .count());
        
        Map<String, Long> actionCounts = new HashMap<>();
        Map<String, Long> resourceTypeCounts = new HashMap<>();
        Map<String, Long> userCounts = new HashMap<>();
        
        for (AuditLog log : filtered) {
            String action = log.getAction() != null ? log.getAction() : "unknown";
            String resourceType = log.getResourceType() != null ? log.getResourceType() : "unknown";
            String userId = log.getUserId() != null ? log.getUserId() : "anonymous";
            
            actionCounts.merge(action, 1L, Long::sum);
            resourceTypeCounts.merge(resourceType, 1L, Long::sum);
            userCounts.merge(userId, 1L, Long::sum);
        }
        
        stats.setActionCounts(actionCounts);
        stats.setResourceTypeCounts(resourceTypeCounts);
        stats.setUserCounts(userCounts);
        stats.setStartTime(startTime != null ? startTime : 0);
        stats.setEndTime(endTime != null ? endTime : System.currentTimeMillis());
        
        return stats;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>支持的导出格式：</p>
     * <ul>
     *   <li>JSON：结构化数据格式，适合程序处理</li>
     *   <li>CSV：表格格式，适合Excel打开查看</li>
     * </ul>
     */
    @Override
    public byte[] export(AuditQueryRequest request, String format) {
        AuditQueryResult result = query(request);
        StringBuilder sb = new StringBuilder();
        
        if ("csv".equalsIgnoreCase(format)) {
            sb.append("LogId,UserId,Action,ResourceType,ResourceId,Result,IpAddress,Timestamp\n");
            for (AuditLog log : result.getItems()) {
                sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%d\n",
                        log.getLogId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getResourceType(),
                        log.getResourceId(),
                        log.getResult(),
                        log.getIpAddress(),
                        log.getTimestamp()));
            }
        } else {
            sb.append("[\n");
            for (int i = 0; i < result.getItems().size(); i++) {
                AuditLog log = result.getItems().get(i);
                sb.append(String.format("  {\"logId\":\"%s\",\"userId\":\"%s\",\"action\":\"%s\",\"timestamp\":%d}",
                        log.getLogId(), log.getUserId(), log.getAction(), log.getTimestamp()));
                if (i < result.getItems().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
        }
        
        return sb.toString().getBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditQueryResult getByUserId(String userId, int page, int size) {
        AuditQueryRequest request = new AuditQueryRequest();
        request.setUserId(userId);
        request.setPage(page);
        request.setSize(size);
        return query(request);
    }
    
    /**
     * 获取今日零点时间戳
     * 
     * <p>用于统计今日日志数量，计算从当天00:00:00开始的时间戳。</p>
     * 
     * @return 今日零点的毫秒时间戳
     */
    private long getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
