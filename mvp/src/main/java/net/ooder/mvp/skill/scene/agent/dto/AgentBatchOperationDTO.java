package net.ooder.mvp.skill.scene.agent.dto;

import java.util.List;
import java.util.Map;

public class AgentBatchOperationDTO {
    private String operationId;
    private String operationType;
    private List<String> agentIds;
    private Map<String, Object> parameters;
    private long createdAt;
    private String status;
    private int totalCount;
    private int successCount;
    private int failedCount;
    private List<OperationResult> results;

    public static final String OP_ENABLE = "enable";
    public static final String OP_DISABLE = "disable";
    public static final String OP_RESTART = "restart";
    public static final String OP_UPDATE_CONFIG = "update_config";
    public static final String OP_CLEAR_CACHE = "clear_cache";
    public static final String OP_HEALTH_CHECK = "health_check";

    public static class OperationResult {
        private String agentId;
        private boolean success;
        private String message;
        private Map<String, Object> data;

        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public List<String> getAgentIds() { return agentIds; }
    public void setAgentIds(List<String> agentIds) { this.agentIds = agentIds; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
    public List<OperationResult> getResults() { return results; }
    public void setResults(List<OperationResult> results) { this.results = results; }
}
