package net.ooder.mvp.skill.scene.config;

import net.ooder.scene.core.security.AuditService;
import net.ooder.scene.core.security.AuditLog;
import net.ooder.scene.core.security.AuditLogQuery;
import net.ooder.scene.core.security.OperationResult;
import net.ooder.scene.core.security.AuditExportResult;
import net.ooder.scene.core.security.UserOperationStats;
import net.ooder.scene.core.security.ResourceAccessStats;
import net.ooder.scene.core.security.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAuditService implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(InMemoryAuditService.class);
    private static final int MAX_LOGS = 10000;
    
    private final Map<String, AuditLog> auditLogs = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private int logCounter = 0;

    public InMemoryAuditService() {
        initSampleData();
    }

    private void initSampleData() {
        String[] operations = {"invoke", "create", "update", "delete", "query", "export"};
        String[] resources = {"scene", "agent", "skill", "llm", "permission", "key"};
        String[] userIds = {"user-1", "user-2", "user-3", "user-4", "user-5"};
        
        for (int i = 0; i < 50; i++) {
            AuditLog logEntry = new AuditLog();
            logEntry.setLogId("audit-" + System.currentTimeMillis() + "-" + i);
            logEntry.setTimestamp(System.currentTimeMillis() - i * 3600000L);
            logEntry.setUserId(userIds[i % userIds.length]);
            logEntry.setOperation(operations[i % operations.length]);
            logEntry.setResource(resources[i % resources.length]);
            logEntry.setResourceId(resources[i % resources.length] + "-" + (i % 10));
            logEntry.setResult(i % 5 == 0 ? OperationResult.FAILURE : OperationResult.SUCCESS);
            logEntry.setIpAddress("192.168.1." + (i % 255));
            
            Map<String, Object> details = new HashMap<>();
            details.put("description", "Sample audit log entry " + i);
            logEntry.setDetails(details);
            
            auditLogs.put(logEntry.getLogId(), logEntry);
        }
        log.info("[InMemoryAuditService] Initialized with {} sample audit logs", auditLogs.size());
    }

    @Override
    public void logOperation(OperationContext context, String operation, String resource, 
            String resourceId, OperationResult result, Map<String, Object> details) {
        log.debug("[logOperation] operation={}, resource={}, resourceId={}, result={}", 
            operation, resource, resourceId, result);
        
        AuditLog logEntry = new AuditLog();
        logEntry.setLogId(generateLogId());
        logEntry.setTimestamp(context != null && context.getTimestamp() > 0 ? 
            context.getTimestamp() : System.currentTimeMillis());
        logEntry.setUserId(context != null ? context.getUserId() : null);
        logEntry.setOperation(operation);
        logEntry.setResource(resource);
        logEntry.setResourceId(resourceId);
        logEntry.setResult(result != null ? result : OperationResult.SUCCESS);
        logEntry.setDetails(details != null ? details : new HashMap<>());
        if (context != null) {
            logEntry.setIpAddress(context.getIpAddress());
        }
        
        synchronized (lock) {
            auditLogs.put(logEntry.getLogId(), logEntry);
            
            while (auditLogs.size() > MAX_LOGS) {
                Optional<String> oldestKey = auditLogs.entrySet().stream()
                    .min(Comparator.comparingLong(e -> e.getValue().getTimestamp()))
                    .map(Map.Entry::getKey);
                oldestKey.ifPresent(auditLogs::remove);
            }
        }
        
        log.debug("[logOperation] Audit log saved: {}", logEntry.getLogId());
    }

    @Override
    public CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query) {
        log.debug("[queryLogs] Querying audit logs");
        
        List<AuditLog> result = auditLogs.values().stream()
            .filter(logEntry -> query == null || matchesQuery(logEntry, query))
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
        
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<AuditExportResult> exportLogs(AuditLogQuery query) {
        log.debug("[exportLogs] Exporting audit logs");
        
        try {
            List<AuditLog> logs = queryLogs(query).join();
            
            AuditExportResult result = new AuditExportResult();
            result.setRecords(logs);
            result.setFormat("csv");
            result.setRecordCount(logs.size());
            result.setExportId("export-" + System.currentTimeMillis());
            result.setTimestamp(System.currentTimeMillis());
            result.setStatus("COMPLETED");
            
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("[exportLogs] Failed to export logs: {}", e.getMessage());
            AuditExportResult errorResult = new AuditExportResult();
            errorResult.setStatus("ERROR");
            return CompletableFuture.completedFuture(errorResult);
        }
    }

    @Override
    public CompletableFuture<UserOperationStats> getUserStats(String userId, long startTime, long endTime) {
        log.debug("[getUserStats] Getting stats for user: {}", userId);
        
        List<AuditLog> userLogs = auditLogs.values().stream()
            .filter(logEntry -> userId == null || userId.equals(logEntry.getUserId()))
            .filter(logEntry -> logEntry.getTimestamp() >= startTime && logEntry.getTimestamp() <= endTime)
            .collect(Collectors.toList());
        
        UserOperationStats stats = new UserOperationStats();
        stats.setUserId(userId);
        stats.setTotalOperations(userLogs.size());
        
        long successCount = userLogs.stream()
            .filter(l -> l.getResult() == OperationResult.SUCCESS)
            .count();
        stats.setSuccessCount(successCount);
        stats.setFailureCount(userLogs.size() - (int) successCount);
        
        return CompletableFuture.completedFuture(stats);
    }

    @Override
    public CompletableFuture<ResourceAccessStats> getResourceStats(String resourceType, String resourceId) {
        log.debug("[getResourceStats] Getting stats for resource: {}/{}", resourceType, resourceId);
        
        List<AuditLog> resourceLogs = auditLogs.values().stream()
            .filter(logEntry -> resourceType == null || resourceType.equals(logEntry.getResource()))
            .filter(logEntry -> resourceId == null || resourceId.equals(logEntry.getResourceId()))
            .collect(Collectors.toList());
        
        ResourceAccessStats stats = new ResourceAccessStats();
        stats.setResourceType(resourceType);
        stats.setResourceId(resourceId);
        stats.setTotalAccesses(resourceLogs.size());
        
        Map<String, Long> operationCounts = resourceLogs.stream()
            .collect(Collectors.groupingBy(
                l -> l.getOperation() != null ? l.getOperation() : "unknown",
                Collectors.counting()
            ));
        stats.setOperationCounts(operationCounts);
        
        Map<String, Long> resultCounts = resourceLogs.stream()
            .collect(Collectors.groupingBy(
                l -> l.getResult() != null ? l.getResult().name() : "UNKNOWN",
                Collectors.counting()
            ));
        stats.setResultCounts(resultCounts);
        
        return CompletableFuture.completedFuture(stats);
    }
    
    private String generateLogId() {
        synchronized (lock) {
            return "audit-" + System.currentTimeMillis() + "-" + (++logCounter);
        }
    }
    
    private boolean matchesQuery(AuditLog logEntry, AuditLogQuery query) {
        if (query.getUserId() != null && !query.getUserId().equals(logEntry.getUserId())) {
            return false;
        }
        if (query.getOperation() != null && !query.getOperation().equals(logEntry.getOperation())) {
            return false;
        }
        if (query.getResource() != null && !query.getResource().equals(logEntry.getResource())) {
            return false;
        }
        if (query.getStartTime() > 0 && logEntry.getTimestamp() < query.getStartTime()) {
            return false;
        }
        if (query.getEndTime() > 0 && logEntry.getTimestamp() > query.getEndTime()) {
            return false;
        }
        if (query.getResult() != null && logEntry.getResult() != query.getResult()) {
            return false;
        }
        return true;
    }
}
