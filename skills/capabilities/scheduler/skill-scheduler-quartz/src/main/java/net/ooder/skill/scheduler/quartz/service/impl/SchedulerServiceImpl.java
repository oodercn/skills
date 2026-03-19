package net.ooder.skill.scheduler.quartz.service.impl;

import net.ooder.skill.common.storage.JsonStorage;
import net.ooder.skill.scheduler.quartz.dto.*;
import net.ooder.skill.scheduler.quartz.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Value("${scheduler.data-path:./data/scheduler}")
    private String dataPath;

    private JsonStorage storage;
    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();
    private final Map<String, List<TaskExecution>> executionHistory = new ConcurrentHashMap<>();

    private static final String TASKS_KEY = "tasks";
    private static final String HISTORY_KEY = "history";

    @PostConstruct
    public void init() {
        storage = new JsonStorage(dataPath);
        
        List<TaskInfo> savedTasks = storage.loadList(TASKS_KEY, TaskInfo.class);
        if (savedTasks != null) {
            for (TaskInfo task : savedTasks) {
                tasks.put(task.getTaskId(), task);
            }
        }
        
        Map<String, List<TaskExecution>> savedHistory = storage.load(HISTORY_KEY, Map.class);
        if (savedHistory != null) {
            executionHistory.putAll(savedHistory);
        }
        
        log.info("Loaded {} tasks from storage", tasks.size());
    }

    @Override
    public String schedule(String taskName, String cronExpression, Map<String, Object> taskData, Map<String, Object> options) {
        String taskId = "task-" + UUID.randomUUID().toString().substring(0, 8);

        TaskInfo task = new TaskInfo();
        task.setTaskId(taskId);
        task.setTaskName(taskName);
        task.setCronExpression(cronExpression);
        task.setStatus("scheduled");
        task.setLastExecutionTime(0L);
        task.setNextExecutionTime(calculateNextExecutionTime(cronExpression));
        task.setExecutionCount(0);
        task.setCreateTime(System.currentTimeMillis());

        Map<String, Object> data = new HashMap<>();
        if (taskData != null) {
            data.putAll(taskData);
        }
        if (options != null) {
            data.putAll(options);
        }
        task.setTaskData(data);

        tasks.put(taskId, task);
        executionHistory.put(taskId, new ArrayList<>());
        
        saveTasks();
        
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
        saveTasks();
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
        saveTasks();
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
        saveTasks();
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
        execution.setExecutionId("exec-" + UUID.randomUUID().toString().substring(0, 8));
        execution.setTaskId(taskId);
        execution.setStartTime(System.currentTimeMillis());
        execution.setEndTime(System.currentTimeMillis() + 100);
        execution.setStatus("success");
        execution.setResult("Triggered manually");

        List<TaskExecution> history = executionHistory.get(taskId);
        if (history != null) {
            history.add(0, execution);
            saveHistory();
        }

        task.setLastExecutionTime(execution.getStartTime());
        task.setExecutionCount(task.getExecutionCount() + 1);
        task.setNextExecutionTime(calculateNextExecutionTime(task.getCronExpression()));
        saveTasks();

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
        saveTasks();

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

    private void saveTasks() {
        storage.save(TASKS_KEY, new ArrayList<>(tasks.values()));
    }

    private void saveHistory() {
        storage.save(HISTORY_KEY, executionHistory);
    }
}
