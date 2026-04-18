package net.ooder.skill.cli.api;

import java.util.Map;

public interface SkillCliExtension {
    
    String getSkillId();
    
    String getCommand();
    
    String getDescription();
    
    String getCategory();
    
    CliResult execute(String[] args, SceneContext context);
    
    default boolean isEnabled() {
        return true;
    }
    
    default Map<String, String> getOptions() {
        return Map.of();
    }
}
