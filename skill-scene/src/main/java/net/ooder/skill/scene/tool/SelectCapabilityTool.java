package net.ooder.skill.scene.tool;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.*;

public class SelectCapabilityTool implements Tool {
    
    @Override
    public String getId() {
        return "select_capability";
    }
    
    @Override
    public String getName() {
        return "select_capability";
    }
    
    @Override
    public String getDescription() {
        return "选择一个能力进行查看详情或安装。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        Map<String, Object> capabilityIdProp = new LinkedHashMap<String, Object>();
        capabilityIdProp.put("type", "string");
        capabilityIdProp.put("description", "能力ID");
        properties.put("capabilityId", capabilityIdProp);
        
        Map<String, Object> capabilityNameProp = new LinkedHashMap<String, Object>();
        capabilityNameProp.put("type", "string");
        capabilityNameProp.put("description", "能力名称");
        properties.put("capabilityName", capabilityNameProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("capabilityId"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String capabilityId = (String) parameters.get("capabilityId");
        String capabilityName = (String) parameters.get("capabilityName");
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("action", "selectCapability");
        result.put("capabilityId", capabilityId);
        result.put("capabilityName", capabilityName != null ? capabilityName : "");
        
        return ToolResult.success(result);
    }
    
    @Override
    public String getCategory() {
        return "discovery";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("select", "capability", "detail");
    }
}
