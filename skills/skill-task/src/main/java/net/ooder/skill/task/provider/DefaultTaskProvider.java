package net.ooder.skill.task.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.task.TaskProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultTaskProvider implements TaskProvider {
    
    private final Map<String, TaskResult> tasks = new ConcurrentHashMap<>();
    private final Map<String, List<String>> dependencies = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return "default";
    }
    
    @Override
    public List<String> getSupportedTypes() {
        return Arrays.asList("extraction", "import", "export", "sync", "batch", "custom");
    }
    
    @Override
    public TaskResult createTask(TaskRequest request) {
        log.info("Create task: name={}, type={}", request.getName(), request.getType());
        
        String taskId = request.getTaskId() != null ? request.getTaskId() : UUID.randomUUID().toString();
        
        TaskResult result = new TaskResult();
        result.setSuccess(true);
        result.setTaskId(taskId);
        result.setName(request.getName());
        result.setType(request.getType());
        result.setStatus("pending");
        result.setProgress(0);
        result.setInput(request.getInput());
        result.setRetryCount(0);
        result.setCreatedAt(System.currentTimeMillis());
        
        tasks.put(taskId, result);
        
        if (request.getDependsOn() != null) {
            dependencies.put(taskId, new ArrayList<>(request.getDependsOn()));
        }
        
        return result;
    }
    
    @Override
    public TaskResult getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    @Override
    public List<TaskResult> listTasks(String status, String type, int page, int pageSize) {
        List<TaskResult> filteredTasks = tasks.values().stream()
                .filter(task -> status == null || status.isEmpty() || status.equals(task.getStatus()))
                .filter(task -> type == null || type.isEmpty() || type.equals(task.getType()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .collect(Collectors.toList());
        
        int from = page * pageSize;
        int to = Math.min(from + pageSize, filteredTasks.size());
        
        return from < filteredTasks.size() 
                ? filteredTasks.subList(from, to) 
                : new ArrayList<>();
    }
    
    @Override
    public TaskResult updateTask(String taskId, Map<String, Object> updates) {
        log.info("Update task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task == null) {
            TaskResult result = new TaskResult();
            result.setSuccess(false);
            result.setErrorCode("TASK_NOT_FOUND");
            result.setErrorMessage("Task not found: " + taskId);
            return result;
        }
        
        if (updates.containsKey("progress")) {
            task.setProgress((Integer) updates.get("progress"));
        }
        if (updates.containsKey("status")) {
            task.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("output")) {
            task.setOutput((Map<String, Object>) updates.get("output"));
        }
        
        return task;
    }
    
    @Override
    public boolean deleteTask(String taskId) {
        log.info("Delete task: taskId={}", taskId);
        dependencies.remove(taskId);
        return tasks.remove(taskId) != null;
    }
    
    @Override
    public TaskResult startTask(String taskId) {
        log.info("Start task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task == null) {
            TaskResult result = new TaskResult();
            result.setSuccess(false);
            result.setErrorCode("TASK_NOT_FOUND");
            return result;
        }
        
        List<String> deps = dependencies.get(taskId);
        if (deps != null) {
            for (String depId : deps) {
                TaskResult depTask = tasks.get(depId);
                if (depTask != null && !"completed".equals(depTask.getStatus())) {
                    TaskResult result = new TaskResult();
                    result.setSuccess(false);
                    result.setErrorCode("DEPENDENCY_NOT_MET");
                    result.setErrorMessage("Dependency task not completed: " + depId);
                    return result;
                }
            }
        }
        
        task.setStatus("running");
        task.setStartedAt(System.currentTimeMillis());
        
        return task;
    }
    
    @Override
    public TaskResult pauseTask(String taskId) {
        log.info("Pause task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task != null) {
            task.setStatus("paused");
        }
        
        return task;
    }
    
    @Override
    public TaskResult resumeTask(String taskId) {
        log.info("Resume task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task != null) {
            task.setStatus("running");
        }
        
        return task;
    }
    
    @Override
    public TaskResult cancelTask(String taskId) {
        log.info("Cancel task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task != null) {
            task.setStatus("cancelled");
            task.setCompletedAt(System.currentTimeMillis());
        }
        
        return task;
    }
    
    @Override
    public TaskResult retryTask(String taskId) {
        log.info("Retry task: taskId={}", taskId);
        
        TaskResult task = tasks.get(taskId);
        if (task != null) {
            task.setStatus("pending");
            task.setRetryCount(task.getRetryCount() + 1);
            task.setProgress(0);
            task.setError(null);
        }
        
        return task;
    }
    
    @Override
    public TaskProgress getProgress(String taskId) {
        TaskResult task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        
        TaskProgress progress = new TaskProgress();
        progress.setTaskId(taskId);
        progress.setPercentage(task.getProgress());
        progress.setMessage("Processing...");
        progress.setProcessedItems(task.getProgress());
        progress.setTotalItems(100);
        
        return progress;
    }
    
    @Override
    public List<TaskResult> getDependencies(String taskId) {
        List<String> depIds = dependencies.get(taskId);
        if (depIds == null) {
            return new ArrayList<>();
        }
        
        List<TaskResult> result = new ArrayList<>();
        for (String depId : depIds) {
            TaskResult task = tasks.get(depId);
            if (task != null) {
                result.add(task);
            }
        }
        
        return result;
    }
    
    @Override
    public TaskResult addDependency(String taskId, String dependsOnTaskId) {
        log.info("Add dependency: {} -> {}", taskId, dependsOnTaskId);
        
        dependencies.computeIfAbsent(taskId, k -> new ArrayList<>()).add(dependsOnTaskId);
        
        return tasks.get(taskId);
    }
    
    @Override
    public TaskResult removeDependency(String taskId, String dependsOnTaskId) {
        log.info("Remove dependency: {} -> {}", taskId, dependsOnTaskId);
        
        List<String> deps = dependencies.get(taskId);
        if (deps != null) {
            deps.remove(dependsOnTaskId);
        }
        
        return tasks.get(taskId);
    }
}
