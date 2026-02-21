package net.ooder.skill.scheduler.quartz;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.SchedulerProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QuartzSchedulerProvider implements SchedulerProvider {
    
    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();
    private final Map<String, List<TaskExecution>> executionHistory = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return "quartz";
    }
    
    @Override
    public String schedule(String taskName, String cronExpression, Object taskData, Map<String, Object> options) {
        String taskId = UUID.randomUUID().toString();
        
        TaskInfo task = new TaskInfo();
        task.setTaskId(taskId);
        task.setTaskName(taskName);
        task.setCronExpression(cronExpression);
        task.setStatus("scheduled");
        task.setLastExecutionTime(0);
        task.setNextExecutionTime(calculateNextExecutionTime(cronExpression));
        task.setExecutionCount(0);
        
        Map<String, Object> data = new HashMap<>();
        data.put("data", taskData);
        if (options != null) {
            data.putAll(options);
        }
        task.setTaskData(data);
        
        tasks.put(taskId, task);
        executionHistory.put(taskId, new ArrayList<>());
        
        log.info("Scheduled task: {} ({}) with cron: {}", taskName, taskId, cronExpression);
        return taskId;
    }
    
    @Override
    public boolean cancel(String taskId) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        task.setStatus("cancelled");
        log.info("Cancelled task: {}", taskId);
        return true;
    }
    
    @Override
    public boolean pause(String taskId) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        task.setStatus("paused");
        log.info("Paused task: {}", taskId);
        return true;
    }
    
    @Override
    public boolean resume(String taskId) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        task.setStatus("scheduled");
        task.setNextExecutionTime(calculateNextExecutionTime(task.getCronExpression()));
        log.info("Resumed task: {}", taskId);
        return true;
    }
    
    @Override
    public TaskInfo getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    @Override
    public TaskListResult listTasks(String status, int page, int pageSize) {
        List<TaskInfo> filteredTasks = new ArrayList<>();
        
        for (TaskInfo task : tasks.values()) {
            if (status == null || status.isEmpty() || status.equals(task.getStatus())) {
                filteredTasks.add(task);
            }
        }
        
        int total = filteredTasks.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<TaskInfo> pageTasks = start < total ? filteredTasks.subList(start, end) : new ArrayList<>();
        
        TaskListResult result = new TaskListResult(pageTasks, total);
        result.setPage(page);
        result.setPageSize(pageSize);
        
        return result;
    }
    
    @Override
    public boolean triggerNow(String taskId) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        TaskExecution execution = new TaskExecution();
        execution.setExecutionId(UUID.randomUUID().toString());
        execution.setTaskId(taskId);
        execution.setStartTime(System.currentTimeMillis());
        execution.setEndTime(System.currentTimeMillis() + 100);
        execution.setStatus("success");
        execution.setResult("Triggered manually");
        
        List<TaskExecution> history = executionHistory.get(taskId);
        if (history != null) {
            history.add(0, execution);
        }
        
        task.setLastExecutionTime(execution.getStartTime());
        task.setExecutionCount(task.getExecutionCount() + 1);
        task.setNextExecutionTime(calculateNextExecutionTime(task.getCronExpression()));
        
        log.info("Triggered task: {}", taskId);
        return true;
    }
    
    @Override
    public boolean updateCron(String taskId, String cronExpression) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        task.setCronExpression(cronExpression);
        task.setNextExecutionTime(calculateNextExecutionTime(cronExpression));
        
        log.info("Updated cron for task: {} -> {}", taskId, cronExpression);
        return true;
    }
    
    @Override
    public List<TaskExecution> getExecutionHistory(String taskId, int limit) {
        List<TaskExecution> history = executionHistory.get(taskId);
        if (history == null) {
            return new ArrayList<>();
        }
        
        return history.size() <= limit ? new ArrayList<>(history) : new ArrayList<>(history.subList(0, limit));
    }
    
    private long calculateNextExecutionTime(String cronExpression) {
        return System.currentTimeMillis() + 60000;
    }
}
