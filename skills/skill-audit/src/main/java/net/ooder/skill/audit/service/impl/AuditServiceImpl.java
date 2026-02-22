package net.ooder.skill.audit.service.impl;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements AuditService {

    private final Map<String, AuditLog> logs = new ConcurrentHashMap<>();

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

    @Override
    public AuditLog getById(String logId) {
        return logs.get(logId);
    }

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

    @Override
    public AuditQueryResult getByUserId(String userId, int page, int size) {
        AuditQueryRequest request = new AuditQueryRequest();
        request.setUserId(userId);
        request.setPage(page);
        request.setSize(size);
        return query(request);
    }
    
    private long getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
