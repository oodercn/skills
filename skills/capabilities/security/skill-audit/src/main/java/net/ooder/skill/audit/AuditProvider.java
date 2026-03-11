package net.ooder.skill.audit;

import java.util.List;
import java.util.Map;

public interface AuditProvider {
    
    String getProviderType();
    
    AuditResult log(AuditLogRequest request);
    
    AuditResult logBatch(List<AuditLogRequest> requests);
    
    AuditQueryResult query(AuditQueryRequest request);
    
    AuditLogResult getLog(String logId);
    
    long count(AuditQueryRequest request);
    
    boolean export(AuditQueryRequest request, String format, String outputPath);
    
    List<String> getAuditTypes();
    
    List<String> getAuditActions();
    
    public static class AuditLogRequest {
        private String logId;
        private String type;
        private String action;
        private String userId;
        private String userName;
        private String resourceType;
        private String resourceId;
        private String resourceName;
        private String ip;
        private String userAgent;
        private String status;
        private String detail;
        private Map<String, Object> before;
        private Map<String, Object> after;
        private long timestamp;
        
        public String getLogId() { return logId; }
        public void setLogId(String logId) { this.logId = logId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        public String getResourceName() { return resourceName; }
        public void setResourceName(String resourceName) { this.resourceName = resourceName; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public Map<String, Object> getBefore() { return before; }
        public void setBefore(Map<String, Object> before) { this.before = before; }
        public Map<String, Object> getAfter() { return after; }
        public void setAfter(Map<String, Object> after) { this.after = after; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    public static class AuditResult {
        private boolean success;
        private String logId;
        private String status;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getLogId() { return logId; }
        public void setLogId(String logId) { this.logId = logId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class AuditQueryRequest {
        private String type;
        private String action;
        private String userId;
        private String resourceType;
        private String resourceId;
        private String status;
        private String ip;
        private long startTime;
        private long endTime;
        private String keyword;
        private int page;
        private int pageSize;
        private String sortBy;
        private String sortOrder;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    }
    
    public static class AuditQueryResult {
        private boolean success;
        private long total;
        private List<AuditLogResult> logs;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public List<AuditLogResult> getLogs() { return logs; }
        public void setLogs(List<AuditLogResult> logs) { this.logs = logs; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class AuditLogResult {
        private String logId;
        private String type;
        private String action;
        private String userId;
        private String userName;
        private String resourceType;
        private String resourceId;
        private String resourceName;
        private String ip;
        private String userAgent;
        private String status;
        private String detail;
        private Map<String, Object> before;
        private Map<String, Object> after;
        private long timestamp;
        
        public String getLogId() { return logId; }
        public void setLogId(String logId) { this.logId = logId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        public String getResourceName() { return resourceName; }
        public void setResourceName(String resourceName) { this.resourceName = resourceName; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public Map<String, Object> getBefore() { return before; }
        public void setBefore(Map<String, Object> before) { this.before = before; }
        public Map<String, Object> getAfter() { return after; }
        public void setAfter(Map<String, Object> after) { this.after = after; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
