package net.ooder.skill.scene.tool;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.*;

public class FilterCapabilitiesTool implements Tool {
    
    @Override
    public String getId() {
        return "filter_capabilities";
    }
    
    @Override
    public String getName() {
        return "filter_capabilities";
    }
    
    @Override
    public String getDescription() {
        return "筛选已发现的能力列表。可以按关键词、类型、是否已安装等条件筛选。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        Map<String, Object> keywordProp = new LinkedHashMap<String, Object>();
        keywordProp.put("type", "string");
        keywordProp.put("description", "搜索关键词");
        properties.put("keyword", keywordProp);
        
        Map<String, Object> typeProp = new LinkedHashMap<String, Object>();
        typeProp.put("type", "string");
        typeProp.put("description", "能力类型：SERVICE, AI, COMMUNICATION, STORAGE, SKILL, CUSTOM");
        typeProp.put("enum", Arrays.asList("SERVICE", "AI", "COMMUNICATION", "STORAGE", "SKILL", "CUSTOM"));
        properties.put("type", typeProp);
        
        Map<String, Object> installedProp = new LinkedHashMap<String, Object>();
        installedProp.put("type", "boolean");
        installedProp.put("description", "是否只显示已安装的能力");
        properties.put("installed", installedProp);
        
        schema.put("properties", properties);
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String keyword = (String) parameters.get("keyword");
        String type = (String) parameters.get("type");
        Boolean installed = (Boolean) parameters.getOrDefault("installed", false);
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("action", "filterCapabilities");
        result.put("keyword", keyword != null ? keyword : "");
        result.put("type", type != null ? type : "");
        result.put("installed", installed);
        
        return ToolResult.success(result);
    }
    
    @Override
    public String getCategory() {
        return "discovery";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("filter", "search", "capability");
    }
}
