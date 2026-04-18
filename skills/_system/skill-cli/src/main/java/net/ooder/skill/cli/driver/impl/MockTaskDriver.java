package net.ooder.skill.cli.driver.impl;

import net.ooder.skill.cli.driver.TaskDriver;
import net.ooder.skill.cli.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MockTaskDriver implements TaskDriver {
    
    private final Map<String, TaskEntity> tasks = new HashMap<>();
    
    public MockTaskDriver() {
        initMockData();
    }
    
    private void initMockData() {
        TaskEntity task1 = new TaskEntity();
        task1.setTaskId("task-001");
        task1.setSceneId("scene-001");
        task1.setSkillId("skill-llm-deepseek");
        task1.setCapabilityId("chat");
        task1.setStatus(TaskStatus.COMPLETED);
        task1.setSubmittedAt(LocalDateTime.now().minusMinutes(30));
        task1.setStartedAt(LocalDateTime.now().minusMinutes(30));
        task1.setCompletedAt(LocalDateTime.now().minusMinutes(29));
        task1.setResult(Map.of("response", "Hello, how can I help you?"));
        tasks.put(task1.getTaskId(), task1);
        
        TaskEntity task2 = new TaskEntity();
        task2.setTaskId("task-002");
        task2.setSceneId("scene-001");
        task2.setSkillId("skill-knowledge");
        task2.setCapabilityId("search");
        task2.setStatus(TaskStatus.RUNNING);
        task2.setSubmittedAt(LocalDateTime.now().minusMinutes(5));
        task2.setStartedAt(LocalDateTime.now().minusMinutes(5));
        tasks.put(task2.getTaskId(), task2);
    }
    
    @Override
    public String getDriverId() {
        return "mock-task-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Mock Task Driver";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public SubmitResult submit(String sceneId, String skillId, String capabilityId, 
                               Map<String, Object> params) {
        String taskId = "task-" + UUID.randomUUID().toString().substring(0, 8);
        TaskEntity entity = new TaskEntity();
        entity.setTaskId(taskId);
        entity.setSceneId(sceneId);
        entity.setSkillId(skillId);
        entity.setCapabilityId(capabilityId);
        entity.setParams(params);
        entity.setStatus(TaskStatus.PENDING);
        entity.setSubmittedAt(LocalDateTime.now());
        tasks.put(taskId, entity);
        return SubmitResult.success(taskId);
    }
    
    @Override
    public boolean cancel(String taskId) {
        TaskEntity entity = tasks.get(taskId);
        if (entity != null && entity.getStatus() == TaskStatus.RUNNING) {
            entity.setStatus(TaskStatus.CANCELLED);
            entity.setCompletedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean pause(String taskId) {
        TaskEntity entity = tasks.get(taskId);
        if (entity != null && entity.getStatus() == TaskStatus.RUNNING) {
            entity.setStatus(TaskStatus.PAUSED);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean resume(String taskId) {
        TaskEntity entity = tasks.get(taskId);
        if (entity != null && entity.getStatus() == TaskStatus.PAUSED) {
            entity.setStatus(TaskStatus.RUNNING);
            return true;
        }
        return false;
    }
    
    @Override
    public TaskEntity getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    @Override
    public TaskStatus getStatus(String taskId) {
        TaskEntity entity = tasks.get(taskId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public List<TaskEntity> getTasksByScene(String sceneId) {
        return tasks.values().stream()
                .filter(t -> sceneId.equals(t.getSceneId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskEntity> getTasksBySkill(String skillId) {
        return tasks.values().stream()
                .filter(t -> skillId.equals(t.getSkillId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskEntity> getActiveTasks() {
        return tasks.values().stream()
                .filter(t -> t.getStatus() == TaskStatus.RUNNING || 
                             t.getStatus() == TaskStatus.PENDING ||
                             t.getStatus() == TaskStatus.PAUSED)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getResult(String taskId) {
        TaskEntity entity = tasks.get(taskId);
        return entity != null ? entity.getResult() : null;
    }
    
    @Override
    public void refresh() {
    }
}
