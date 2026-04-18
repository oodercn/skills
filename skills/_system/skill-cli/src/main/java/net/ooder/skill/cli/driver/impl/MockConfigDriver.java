package net.ooder.skill.cli.driver.impl;

import net.ooder.skill.cli.driver.ConfigDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockConfigDriver implements ConfigDriver {
    
    private final Map<String, Object> config = new ConcurrentHashMap<>();
    
    public MockConfigDriver() {
        initMockData();
    }
    
    private void initMockData() {
        config.put("cli.output.format", "text");
        config.put("cli.auto.refresh", true);
        config.put("cli.refresh.interval", 3000);
        config.put("llm.default.model", "deepseek-chat");
        config.put("knowledge.chunk.size", 500);
    }
    
    @Override
    public String getDriverId() {
        return "mock-config-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Mock Config Driver";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public Map<String, Object> getAllConfig() {
        return new HashMap<>(config);
    }
    
    @Override
    public Object getConfig(String key) {
        return config.get(key);
    }
    
    @Override
    public boolean setConfig(String key, Object value) {
        config.put(key, value);
        return true;
    }
    
    @Override
    public boolean removeConfig(String key) {
        return config.remove(key) != null;
    }
    
    @Override
    public boolean reload() {
        return true;
    }
    
    @Override
    public boolean save() {
        return true;
    }
    
    @Override
    public String getConfigPath() {
        return "mock://config/cli.properties";
    }
}
