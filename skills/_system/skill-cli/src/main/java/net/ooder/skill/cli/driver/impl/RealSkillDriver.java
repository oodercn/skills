package net.ooder.skill.cli.driver.impl;

import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.api.ExtensionRegistry;
import net.ooder.skill.cli.driver.SkillDriver;
import net.ooder.skill.cli.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class RealSkillDriver implements SkillDriver {
    
    private final CliRouter cliRouter;
    private final ExtensionRegistry extensionRegistry;
    
    public RealSkillDriver(CliRouter cliRouter, ExtensionRegistry extensionRegistry) {
        this.cliRouter = cliRouter;
        this.extensionRegistry = extensionRegistry;
    }
    
    @Override
    public String getDriverId() {
        return "real-skill-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Real Skill Driver (agent-sdk-cli)";
    }
    
    @Override
    public boolean isAvailable() {
        return cliRouter != null;
    }
    
    @Override
    public SkillEntity install(String source, Map<String, Object> config) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"install", source});
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess()) {
            SkillEntity entity = new SkillEntity();
            entity.setSkillId(extractSkillId(result));
            entity.setSource(source);
            entity.setStatus(SkillStatus.INSTALLED);
            entity.setInstalledAt(LocalDateTime.now());
            return entity;
        }
        
        throw new RuntimeException("Install failed: " + result.getMessage());
    }
    
    @Override
    public UninstallResult uninstall(String skillId, boolean force) {
        CommandContext context = new CommandContext();
        List<String> args = new ArrayList<>();
        args.add("uninstall");
        args.add(skillId);
        if (force) {
            args.add("--force");
        }
        context.setArgs(args.toArray(new String[0]));
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess()) {
            return UninstallResult.success(skillId);
        }
        return UninstallResult.failure(result.getMessage());
    }
    
    @Override
    public StartResult start(String skillId, Map<String, Object> params) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"start", skillId});
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess()) {
            return StartResult.success(skillId);
        }
        return StartResult.failure(result.getMessage());
    }
    
    @Override
    public StopResult stop(String skillId, boolean force) {
        CommandContext context = new CommandContext();
        List<String> args = new ArrayList<>();
        args.add("stop");
        args.add(skillId);
        if (force) {
            args.add("--force");
        }
        context.setArgs(args.toArray(new String[0]));
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess()) {
            return StopResult.success(skillId);
        }
        return StopResult.failure(result.getMessage());
    }
    
    @Override
    public List<SkillEntity> getAllSkills() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess() && result.getData() instanceof List) {
            return convertToSkillEntities((List<?>) result.getData());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public SkillEntity getSkill(String skillId) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"show", skillId, "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess() && result.getData() instanceof Map) {
            return convertToSkillEntity((Map<String, Object>) result.getData());
        }
        
        return null;
    }
    
    @Override
    public SkillStatus getStatus(String skillId) {
        SkillEntity entity = getSkill(skillId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public Object invoke(String skillId, String capabilityId, Map<String, Object> params) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"invoke", skillId, capabilityId});
        
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                context.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        
        CommandResult result = cliRouter.execute("skill", context);
        
        if (result.isSuccess()) {
            return result.getData();
        }
        
        throw new RuntimeException("Invoke failed: " + result.getMessage());
    }
    
    @Override
    public void refresh() {
    }
    
    private String extractSkillId(CommandResult result) {
        if (result.getData() instanceof Map) {
            Map<?, ?> data = (Map<?, ?>) result.getData();
            Object id = data.get("skillId");
            return id != null ? id.toString() : UUID.randomUUID().toString().substring(0, 8);
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    @SuppressWarnings("unchecked")
    private List<SkillEntity> convertToSkillEntities(List<?> data) {
        List<SkillEntity> entities = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof Map) {
                entities.add(convertToSkillEntity((Map<String, Object>) item));
            }
        }
        return entities;
    }
    
    private SkillEntity convertToSkillEntity(Map<String, Object> data) {
        SkillEntity entity = new SkillEntity();
        entity.setSkillId((String) data.get("skillId"));
        entity.setName((String) data.get("name"));
        entity.setVersion((String) data.get("version"));
        entity.setDescription((String) data.get("description"));
        entity.setAuthor((String) data.get("author"));
        entity.setCategory((String) data.get("category"));
        
        String statusStr = (String) data.get("status");
        if (statusStr != null) {
            entity.setStatus(SkillStatus.fromValue(statusStr));
        }
        
        return entity;
    }
}
