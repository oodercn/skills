package net.ooder.mvp.skill.scene.tool;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.*;

public class StartScanTool implements Tool {
    
    @Override
    public String getId() {
        return "start_scan";
    }
    
    @Override
    public String getName() {
        return "start_scan";
    }
    
    @Override
    public String getDescription() {
        return "开始扫描发现能力。支持多种扫描方式：AUTO(自动检测)、LOCAL_FS(本地文件系统)、SKILL_CENTER(能力中心)、GITHUB、GITEE等。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        Map<String, Object> methodProp = new LinkedHashMap<String, Object>();
        methodProp.put("type", "string");
        methodProp.put("description", "扫描方式：AUTO, LOCAL_FS, SKILL_CENTER, GITHUB, GITEE, GIT_REPOSITORY, UDP_BROADCAST, MDNS_DNS_SD, DHT_KADEMLIA");
        methodProp.put("enum", Arrays.asList("AUTO", "LOCAL_FS", "SKILL_CENTER", "GITHUB", "GITEE", "GIT_REPOSITORY", "UDP_BROADCAST", "MDNS_DNS_SD", "DHT_KADEMLIA"));
        properties.put("method", methodProp);
        
        Map<String, Object> forceRefreshProp = new LinkedHashMap<String, Object>();
        forceRefreshProp.put("type", "boolean");
        forceRefreshProp.put("description", "是否强制刷新（清除缓存）");
        properties.put("forceRefresh", forceRefreshProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("method"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String method = (String) parameters.getOrDefault("method", "AUTO");
        Boolean forceRefresh = (Boolean) parameters.getOrDefault("forceRefresh", false);
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("action", "startScan");
        result.put("method", method);
        result.put("forceRefresh", forceRefresh);
        result.put("message", "扫描已启动，请等待结果");
        
        return ToolResult.success(result);
    }
    
    @Override
    public String getCategory() {
        return "discovery";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("scan", "discovery", "capability");
    }
}
