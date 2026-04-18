package net.ooder.skill.cli.driver.impl;

import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.skill.cli.driver.ConfigDriver;

import java.util.*;

public class RealConfigDriver implements ConfigDriver {
    
    private final CliRouter cliRouter;
    private final Map<String, Object> localCache = new HashMap<>();
    
    public RealConfigDriver(CliRouter cliRouter) {
        this.cliRouter = cliRouter;
    }
    
    @Override
    public String getDriverId() {
        return "real-config-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Real Config Driver (agent-sdk-cli)";
    }
    
    @Override
    public boolean isAvailable() {
        return cliRouter != null;
    }
    
    @Override
    public Map<String, Object> getAllConfig() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"list", "--format", "json"});
        context.setOutputFormat("json");
        
        CommandResult result = cliRouter.execute("config", context);
        
        if (result.isSuccess() && result.getData() instanceof Map) {
            return (Map<String, Object>) result.getData();
        }
        
        return new HashMap<>(localCache);
    }
    
    @Override
    public Object getConfig(String key) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"get", key});
        
        CommandResult result = cliRouter.execute("config", context);
        
        if (result.isSuccess()) {
            return result.getData();
        }
        
        return localCache.get(key);
    }
    
    @Override
    public boolean setConfig(String key, Object value) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"set", key, String.valueOf(value)});
        
        CommandResult result = cliRouter.execute("config", context);
        
        if (result.isSuccess()) {
            localCache.put(key, value);
            return true;
        }
        
        localCache.put(key, value);
        return true;
    }
    
    @Override
    public boolean removeConfig(String key) {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"remove", key});
        
        CommandResult result = cliRouter.execute("config", context);
        
        localCache.remove(key);
        return true;
    }
    
    @Override
    public boolean reload() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"reload"});
        
        CommandResult result = cliRouter.execute("config", context);
        return result.isSuccess();
    }
    
    @Override
    public boolean save() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"save"});
        
        CommandResult result = cliRouter.execute("config", context);
        return result.isSuccess();
    }
    
    @Override
    public String getConfigPath() {
        CommandContext context = new CommandContext();
        context.setArgs(new String[]{"path"});
        
        CommandResult result = cliRouter.execute("config", context);
        
        if (result.isSuccess()) {
            return String.valueOf(result.getData());
        }
        
        return "cli-config.yml";
    }
}
