package net.ooder.nexus.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.nexus.domain.task.model.DataExtractTask;
import net.ooder.nexus.service.DataExtractTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据抽取任务服务实现�?
 */
@Service
public class DataExtractTaskServiceImpl implements DataExtractTaskService {
    
    private static final Logger log = LoggerFactory.getLogger(DataExtractTaskServiceImpl.class);
    private static final String DATA_DIR = "./storage/tasks";
    private static final String TASKS_FILE = "extract-tasks.json";
    
    private final Map<String, DataExtractTask> taskCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final Path storagePath;
    
    public DataExtractTaskServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.storagePath = Paths.get(DATA_DIR, TASKS_FILE);
    }
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            loadTasks();
            log.info("数据抽取任务服务初始化完成，共加�?{} 个任�?, taskCache.size());
        } catch (IOException e) {
            log.error("初始化数据抽取任务服务失�?, e);
        }
    }
    
    /**
     * 从文件加载任�?
     */
    private void loadTasks() {
        if (!Files.exists(storagePath)) {
            return;
        }
        
        try {
            String json = new String(Files.readAllBytes(storagePath), StandardCharsets.UTF_8);
            List<DataExtractTask> tasks = objectMapper.readValue(json, new TypeReference<List<DataExtractTask>>() {});
            tasks.forEach(task -> taskCache.put(task.getId(), task));
        } catch (IOException e) {
            log.error("加载任务数据失败", e);
        }
    }
    
    /**
     * 保存任务到文�?
     */
    private void saveTasks() {
        try {
            List<DataExtractTask> tasks = new ArrayList<>(taskCache.values());
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks);
            Files.write(storagePath, json.getBytes(StandardCharsets.UTF_8), 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("保存任务数据失败", e);
        }
    }
    
    @Override
    public DataExtractTask createTask(DataExtractTask task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
        }
        
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setStatus(DataExtractTask.TaskStatus.PENDING);
        
        taskCache.put(task.getId(), task);
        saveTasks();
        
        log.info("创建数据抽取任务: {}", task.getName());
        return task;
    }
    
    @Override
    public DataExtractTask updateTask(String id, DataExtractTask task) {
        DataExtractTask existingTask = taskCache.get(id);
        if (existingTask == null) {
            log.warn("任务不存�? {}", id);
            return null;
        }
        
        task.setId(id);
        task.setCreateTime(existingTask.getCreateTime());
        task.setUpdateTime(LocalDateTime.now());
        task.setExecuteCount(existingTask.getExecuteCount());
        task.setSuccessCount(existingTask.getSuccessCount());
        task.setFailCount(existingTask.getFailCount());
        task.setLastExecuteTime(existingTask.getLastExecuteTime());
        
        taskCache.put(id, task);
        saveTasks();
        
        log.info("更新数据抽取任务: {}", task.getName());
        return task;
    }
    
    @Override
    public boolean deleteTask(String id) {
        DataExtractTask removed = taskCache.remove(id);
        if (removed != null) {
            saveTasks();
            log.info("删除数据抽取任务: {}", removed.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public DataExtractTask getTaskById(String id) {
        return taskCache.get(id);
    }
    
    @Override
    public List<DataExtractTask> getAllTasks() {
        return new ArrayList<>(taskCache.values());
    }
    
    @Override
    public List<DataExtractTask> getEnabledTasks() {
        return taskCache.values().stream()
                .filter(DataExtractTask::isEnabled)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean executeTask(String id) {
        DataExtractTask task = taskCache.get(id);
        if (task == null) {
            log.warn("任务不存�? {}", id);
            return false;
        }
        
        // 模拟执行任务
        task.setStatus(DataExtractTask.TaskStatus.RUNNING);
        task.setLastExecuteTime(LocalDateTime.now());
        task.incrementExecuteCount();
        saveTasks();
        
        log.info("开始执行数据抽取任�? {}", task.getName());
        
        // 异步执行实际任务逻辑
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 模拟执行时间
                
                // 模拟任务执行成功
                task.setStatus(DataExtractTask.TaskStatus.COMPLETED);
                task.incrementSuccessCount();
                task.setLastErrorMessage(null);
                
                log.info("数据抽取任务执行成功: {}", task.getName());
            } catch (Exception e) {
                task.setStatus(DataExtractTask.TaskStatus.FAILED);
                task.incrementFailCount();
                task.setLastErrorMessage(e.getMessage());
                
                log.error("数据抽取任务执行失败: {}", task.getName(), e);
            }
            
            task.setUpdateTime(LocalDateTime.now());
            saveTasks();
        }).start();
        
        return true;
    }
    
    @Override
    public boolean enableTask(String id) {
        DataExtractTask task = taskCache.get(id);
        if (task != null) {
            task.setEnabled(true);
            task.setUpdateTime(LocalDateTime.now());
            saveTasks();
            log.info("启用数据抽取任务: {}", task.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean disableTask(String id) {
        DataExtractTask task = taskCache.get(id);
        if (task != null) {
            task.setEnabled(false);
            task.setUpdateTime(LocalDateTime.now());
            saveTasks();
            log.info("禁用数据抽取任务: {}", task.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public TaskStats getTaskStats() {
        TaskStats stats = new TaskStats();
        stats.setTotalCount(taskCache.size());
        stats.setEnabledCount((int) taskCache.values().stream().filter(DataExtractTask::isEnabled).count());
        stats.setRunningCount((int) taskCache.values().stream()
                .filter(t -> t.getStatus() == DataExtractTask.TaskStatus.RUNNING).count());
        stats.setCompletedCount((int) taskCache.values().stream()
                .filter(t -> t.getStatus() == DataExtractTask.TaskStatus.COMPLETED).count());
        stats.setFailedCount((int) taskCache.values().stream()
                .filter(t -> t.getStatus() == DataExtractTask.TaskStatus.FAILED).count());
        return stats;
    }
}
