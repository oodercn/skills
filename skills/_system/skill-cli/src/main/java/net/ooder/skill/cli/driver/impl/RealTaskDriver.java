package net.ooder.skill.cli.driver.impl;

import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.skill.cli.driver.TaskDriver;
import net.ooder.skill.cli.model.*;

import java.util.*;

public class RealTaskDriver implements TaskDriver {
    
    private final CliRouter cliRouter;
    
    public RealTaskDriver(CliRouter cliRouter) {
        this.cliRouter = cliRouter;
    }
    
    @Override
    public String getDriverId() {
        return "real-task-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Real Task Driver (agent-sdk-cli)";
    }
    
    @Override
    public boolean isAvailable() {
        return cliRouter != null;
    }
    
    @Override
    public SubmitResult submit(String sceneId, String skillId, String capabilityId, 
                               Map<String, Object> params) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"submit", "--scene", sceneId, "--skill", skillId, capabilityId});
        
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                context.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        
        CommandResult result = cliRouter.execute("task", context);
        
        if (result.isSuccess()) {
            String taskId = extractTaskId(result);
            return SubmitResult.success(taskId);
        }
        return SubmitResult.failure(result.getMessage());
    }
    
    @Override
    public boolean cancel(String taskId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"cancel", taskId});
        
        CommandResult result = cliRouter.execute("task", context);
        return result.isSuccess();
    }
    
    @Override
    public boolean pause(String taskId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"pause", taskId});
        
        CommandResult result = cliRouter.execute("task", context);
        return result.isSuccess();
    }
    
    @Override
    public boolean resume(String taskId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"resume", taskId});
        
        CommandResult result = cliRouter.execute("task", context);
        return result.isSuccess();
    }
    
    @Override
    public TaskEntity getTask(String taskId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"show", taskId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("task", context);
        
        if (result.isSuccess() && result.getData() instanceof Map) {
            return convertToTaskEntity((Map<String, Object>) result.getData());
        }
        
        return null;
    }
    
    @Override
    public TaskStatus getStatus(String taskId) {
        TaskEntity entity = getTask(taskId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public List<TaskEntity> getTasksByScene(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--scene", sceneId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("task", context);
        
        if (result.isSuccess() && result.getData() instanceof List) {
            return convertToTaskEntities((List<?>) result.getData());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public List<TaskEntity> getTasksBySkill(String skillId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--skill", skillId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("task", context);
        
        if (result.isSuccess() && result.getData() instanceof List) {
            return convertToTaskEntities((List<?>) result.getData());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public List<TaskEntity> getActiveTasks() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--active", "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("task", context);
        
        if (result.isSuccess() && result.getData() instanceof List) {
            return convertToTaskEntities((List<?>) result.getData());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public Map<String, Object> getResult(String taskId) {
        TaskEntity entity = getTask(taskId);
        return entity != null ? entity.getResult() : null;
    }
    
    @Override
    public void refresh() {
    }
    
    private String extractTaskId(CommandResult result) {
        if (result.getData() instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) result.getData();
            Object id = data.get("taskId");
            return id != null ? id.toString() : UUID.randomUUID().toString().substring(0, 8);
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    @SuppressWarnings("unchecked")
    private List<TaskEntity> convertToTaskEntities(List<?> data) {
        List<TaskEntity> entities = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof Map) {
                entities.add(convertToTaskEntity((Map<String, Object>) item));
            }
        }
        return entities;
    }
    
    private TaskEntity convertToTaskEntity(Map<String, Object> data) {
        TaskEntity entity = new TaskEntity();
        entity.setTaskId((String) data.get("taskId"));
        entity.setSceneId((String) data.get("sceneId"));
        entity.setSkillId((String) data.get("skillId"));
        entity.setCapabilityId((String) data.get("capabilityId"));
        
        String statusStr = (String) data.get("status");
        if (statusStr != null) {
            entity.setStatus(TaskStatus.fromValue(statusStr));
        }
        
        Object result = data.get("result");
        if (result instanceof Map) {
            entity.setResult((Map<String, Object>) result);
        }
        
        entity.setError((String) data.get("error"));
        
        return entity;
    }
}
