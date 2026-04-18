package net.ooder.skill.cli.driver;

import java.util.Map;

public interface ConfigDriver {
    
    String getDriverId();
    
    String getDriverName();
    
    boolean isAvailable();
    
    Map<String, Object> getAllConfig();
    
    Object getConfig(String key);
    
    boolean setConfig(String key, Object value);
    
    boolean removeConfig(String key);
    
    boolean reload();
    
    boolean save();
    
    String getConfigPath();
}
