package net.ooder.skill.audit.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.audit.AuditProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultAuditProvider implements AuditProvider {
    
    private final Map<String, AuditLogResult> logs = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return "default";
    }
    
    @Override
    public AuditResult log(AuditLogRequest request) {
        log.info("Audit log: type={}, action={}, userId={}", request.getType(), request.getAction(), request.getUserId());
        
        String logId = request.getLogId() != null ? request.getLogId() : UUID.randomUUID().toString();
        
        AuditLogResult logResult = new AuditLogResult();
        logResult.setLogId(logId);
        logResult.setType(request.getType());
        logResult.setAction(request.getAction());
        logResult.setUserId(request.getUserId());
        logResult.setUserName(request.getUserName());
        logResult.setResourceType(request.getResourceType());
        logResult.setResourceId(request.getResourceId());
        logResult.setResourceName(request.getResourceName());
        logResult.setIp(request.getIp());
        logResult.setUserAgent(request.getUserAgent());
        logResult.setStatus(request.getStatus());
        logResult.setDetail(request.getDetail());
        logResult.setBefore(request.getBefore());
        logResult.setAfter(request.getAfter());
        logResult.setTimestamp(request.getTimestamp() > 0 ? request.getTimestamp() : System.currentTimeMillis());
        
        logs.put(logId, logResult);
        
        AuditResult result = new AuditResult();
        result.setSuccess(true);
        result.setLogId(logId);
        result.setStatus("logged");
        
        return result;
    }
    
    @Override
    public AuditResult logBatch(List<AuditLogRequest> requests) {
        log.info("Audit log batch: count={}", requests.size());
        
        for (AuditLogRequest request : requests) {
            log(request);
        }
        
        AuditResult result = new AuditResult();
        result.setSuccess(true);
        result.setStatus("logged");
        
        return result;
    }
    
    @Override
    public AuditQueryResult query(AuditQueryRequest request) {
        log.info("Query audit logs: type={}, userId={}", request.getType(), request.getUserId());
        
        List<AuditLogResult> filteredLogs = logs.values().stream()
                .filter(log -> request.getType() == null || request.getType().equals(log.getType()))
                .filter(log -> request.getAction() == null || request.getAction().equals(log.getAction()))
                .filter(log -> request.getUserId() == null || request.getUserId().equals(log.getUserId()))
                .filter(log -> request.getResourceType() == null || request.getResourceType().equals(log.getResourceType()))
                .filter(log -> request.getResourceId() == null || request.getResourceId().equals(log.getResourceId()))
                .filter(log -> request.getStatus() == null || request.getStatus().equals(log.getStatus()))
                .filter(log -> request.getIp() == null || request.getIp().equals(log.getIp()))
                .filter(log -> request.getStartTime() <= 0 || log.getTimestamp() >= request.getStartTime())
                .filter(log -> request.getEndTime() <= 0 || log.getTimestamp() <= request.getEndTime())
                .collect(Collectors.toList());
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            filteredLogs = filteredLogs.stream()
                    .filter(log -> 
                        (log.getUserName() != null && log.getUserName().toLowerCase().contains(keyword)) ||
                        (log.getResourceName() != null && log.getResourceName().toLowerCase().contains(keyword)) ||
                        (log.getDetail() != null && log.getDetail().toLowerCase().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }
        
        filteredLogs.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        
        int page = request.getPage() > 0 ? request.getPage() : 1;
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 20;
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, filteredLogs.size());
        
        List<AuditLogResult> pagedLogs = from < filteredLogs.size() 
                ? filteredLogs.subList(from, to) 
                : new ArrayList<>();
        
        AuditQueryResult result = new AuditQueryResult();
        result.setSuccess(true);
        result.setTotal(filteredLogs.size());
        result.setLogs(pagedLogs);
        
        return result;
    }
    
    @Override
    public AuditLogResult getLog(String logId) {
        return logs.get(logId);
    }
    
    @Override
    public long count(AuditQueryRequest request) {
        return logs.values().stream()
                .filter(log -> request.getType() == null || request.getType().equals(log.getType()))
                .filter(log -> request.getUserId() == null || request.getUserId().equals(log.getUserId()))
                .count();
    }
    
    @Override
    public boolean export(AuditQueryRequest request, String format, String outputPath) {
        log.info("Export audit logs: format={}, path={}", format, outputPath);
        return true;
    }
    
    @Override
    public List<String> getAuditTypes() {
        return Arrays.asList("user", "resource", "system", "security", "data");
    }
    
    @Override
    public List<String> getAuditActions() {
        return Arrays.asList("create", "update", "delete", "read", "login", "logout", "export", "import");
    }
}
