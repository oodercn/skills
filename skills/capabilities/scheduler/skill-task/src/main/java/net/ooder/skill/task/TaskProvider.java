package net.ooder.skill.task;

import java.util.List;
import java.util.Map;

public interface TaskProvider {
    
    String getProviderType();
    
    List<String> getSupportedTypes();
    
    TaskResult createTask(TaskRequest request);
    
    TaskResult getTask(String taskId);
    
    List<TaskResult> listTasks(String status, String type, int page, int pageSize);
    
    TaskResult updateTask(String taskId, Map<String, Object> updates);
    
    boolean deleteTask(String taskId);
    
    TaskResult startTask(String taskId);
    
    TaskResult pauseTask(String taskId);
    
    TaskResult resumeTask(String taskId);
    
    TaskResult cancelTask(String taskId);
    
    TaskResult retryTask(String taskId);
    
    TaskProgress getProgress(String taskId);
    
    List<TaskResult> getDependencies(String taskId);
    
    TaskResult addDependency(String taskId, String dependsOnTaskId);
    
    TaskResult removeDependency(String taskId, String dependsOnTaskId);
    
    public static class TaskRequest {
        private String taskId;
        private String name;
        private String type;
        private String description;
        private Map<String, Object> input;
        private Map<String, Object> config;
        private int priority;
        private long timeout;
        private int maxRetries;
        private List<String> dependsOn;
        private Map<String, Object> metadata;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getInput() { return input; }
        public void setInput(Map<String, Object> input) { this.input = input; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        public List<String> getDependsOn() { return dependsOn; }
        public void setDependsOn(List<String> dependsOn) { this.dependsOn = dependsOn; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class TaskResult {
        private boolean success;
        private String taskId;
        private String name;
        private String type;
        private String status;
        private int progress;
        private Map<String, Object> input;
        private Map<String, Object> output;
        private Map<String, Object> error;
        private int retryCount;
        private long createdAt;
        private long startedAt;
        private long completedAt;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public Map<String, Object> getInput() { return input; }
        public void setInput(Map<String, Object> input) { this.input = input; }
        public Map<String, Object> getOutput() { return output; }
        public void setOutput(Map<String, Object> output) { this.output = output; }
        public Map<String, Object> getError() { return error; }
        public void setError(Map<String, Object> error) { this.error = error; }
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        public long getStartedAt() { return startedAt; }
        public void setStartedAt(long startedAt) { this.startedAt = startedAt; }
        public long getCompletedAt() { return completedAt; }
        public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class TaskProgress {
        private String taskId;
        private int percentage;
        private String message;
        private long processedItems;
        private long totalItems;
        private long estimatedTimeRemaining;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getProcessedItems() { return processedItems; }
        public void setProcessedItems(long processedItems) { this.processedItems = processedItems; }
        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
        public long getEstimatedTimeRemaining() { return estimatedTimeRemaining; }
        public void setEstimatedTimeRemaining(long estimatedTimeRemaining) { this.estimatedTimeRemaining = estimatedTimeRemaining; }
    }
}
