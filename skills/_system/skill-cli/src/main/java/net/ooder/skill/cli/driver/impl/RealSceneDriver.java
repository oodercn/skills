package net.ooder.skill.cli.driver.impl;

import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.skill.cli.driver.SceneDriver;
import net.ooder.skill.cli.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class RealSceneDriver implements SceneDriver {
    
    private final CliRouter cliRouter;
    
    public RealSceneDriver(CliRouter cliRouter) {
        this.cliRouter = cliRouter;
    }
    
    @Override
    public String getDriverId() {
        return "real-scene-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Real Scene Driver (agent-sdk-cli)";
    }
    
    @Override
    public boolean isAvailable() {
        return cliRouter != null;
    }
    
    @Override
    public CreateSceneResult create(String sceneGroupId, Map<String, Object> config) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"create", "--scene-group", sceneGroupId});
        
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                context.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        
        CommandResult result = cliRouter.execute("scene", context);
        
        if (result.isSuccess()) {
            String sceneId = extractSceneId(result);
            return CreateSceneResult.success(sceneId);
        }
        return CreateSceneResult.failure(result.getMessage());
    }
    
    @Override
    public boolean destroy(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"destroy", sceneId});
        
        CommandResult result = cliRouter.execute("scene", context);
        return result.isSuccess();
    }
    
    @Override
    public boolean start(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"start", sceneId});
        
        CommandResult result = cliRouter.execute("scene", context);
        return result.isSuccess();
    }
    
    @Override
    public boolean stop(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"stop", sceneId});
        
        CommandResult result = cliRouter.execute("scene", context);
        return result.isSuccess();
    }
    
    @Override
    public List<SceneEntity> getAllScenes() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("scene", context);
        
        if (result.isSuccess() && result.getData() instanceof List) {
            return convertToSceneEntities((List<?>) result.getData());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public SceneEntity getScene(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"show", sceneId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("scene", context);
        
        if (result.isSuccess() && result.getData() instanceof Map) {
            return convertToSceneEntity((Map<String, Object>) result.getData());
        }
        
        return null;
    }
    
    @Override
    public SceneStatus getStatus(String sceneId) {
        SceneEntity entity = getScene(sceneId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public Map<String, Object> getVariables(String sceneId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"vars", sceneId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("scene", context);
        
        if (result.isSuccess() && result.getData() instanceof Map) {
            return (Map<String, Object>) result.getData();
        }
        
        return new HashMap<>();
    }
    
    @Override
    public boolean setVariable(String sceneId, String key, Object value) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"var", "set", sceneId, key, String.valueOf(value)});
        
        CommandResult result = cliRouter.execute("scene", context);
        return result.isSuccess();
    }
    
    @Override
    public void refresh() {
    }
    
    private String extractSceneId(CommandResult result) {
        if (result.getData() instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) result.getData();
            Object id = data.get("sceneId");
            return id != null ? id.toString() : UUID.randomUUID().toString().substring(0, 8);
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    @SuppressWarnings("unchecked")
    private List<SceneEntity> convertToSceneEntities(List<?> data) {
        List<SceneEntity> entities = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof Map) {
                entities.add(convertToSceneEntity((Map<String, Object>) item));
            }
        }
        return entities;
    }
    
    private SceneEntity convertToSceneEntity(Map<String, Object> data) {
        SceneEntity entity = new SceneEntity();
        entity.setSceneId((String) data.get("sceneId"));
        entity.setSceneGroupId((String) data.get("sceneGroupId"));
        entity.setName((String) data.get("name"));
        
        String statusStr = (String) data.get("status");
        if (statusStr != null) {
            entity.setStatus(SceneStatus.fromValue(statusStr));
        }
        
        return entity;
    }
}
