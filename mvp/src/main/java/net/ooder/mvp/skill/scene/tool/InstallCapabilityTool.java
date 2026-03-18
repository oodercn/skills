package net.ooder.mvp.skill.scene.tool;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.*;

public class InstallCapabilityTool implements Tool {
    
    @Override
    public String getId() {
        return "install_capability";
    }
    
    @Override
    public String getName() {
        return "install_capability";
    }
    
    @Override
    public String getDescription() {
        return "安装选中的能力。需要先选择能力才能安装。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        Map<String, Object> capabilityIdProp = new LinkedHashMap<String, Object>();
        capabilityIdProp.put("type", "string");
        capabilityIdProp.put("description", "要安装的能力ID");
        properties.put("capabilityId", capabilityIdProp);
        
        Map<String, Object> pushTypeProp = new LinkedHashMap<String, Object>();
        pushTypeProp.put("type", "string");
        pushTypeProp.put("description", "推送类型：SHARE(分享), INVITE(邀请), DELEGATE(委派)");
        pushTypeProp.put("enum", Arrays.asList("SHARE", "INVITE", "DELEGATE"));
        properties.put("pushType", pushTypeProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("capabilityId"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String capabilityId = (String) parameters.get("capabilityId");
        String pushType = (String) parameters.getOrDefault("pushType", "SHARE");
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("action", "startInstall");
        result.put("capabilityId", capabilityId);
        result.put("pushType", pushType);
        result.put("message", "安装已开始");
        
        return ToolResult.success(result);
    }
    
    @Override
    public String getCategory() {
        return "install";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("install", "capability", "setup");
    }
}
