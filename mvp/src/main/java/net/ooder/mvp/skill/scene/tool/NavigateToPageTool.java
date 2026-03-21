package net.ooder.mvp.skill.scene.tool;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.*;

public class NavigateToPageTool implements Tool {
    
    private static final Map<String, PageMapping> PAGE_MAPPINGS = new LinkedHashMap<>();
    
    static {
        PAGE_MAPPINGS.put("knowledge-center", new PageMapping("knowledge-center", "知识资料库", "/console/pages/knowledge-center.html", "skill-knowledge"));
        PAGE_MAPPINGS.put("knowledge-overview", new PageMapping("knowledge-overview", "知识概览", "/console/pages/knowledge-center.html", "skill-knowledge"));
        PAGE_MAPPINGS.put("capability-discovery", new PageMapping("capability-discovery", "能力发现", "/console/pages/capability-discovery.html", "skill-discovery"));
        PAGE_MAPPINGS.put("capability-install", new PageMapping("capability-install", "能力安装", "/console/pages/install.html", "skill-install"));
        PAGE_MAPPINGS.put("capability-activation", new PageMapping("capability-activation", "能力激活", "/console/pages/capability-activation.html", "skill-scene"));
        PAGE_MAPPINGS.put("scene-group", new PageMapping("scene-group", "场景组管理", "/console/pages/scene-group.html", "skill-scene"));
        PAGE_MAPPINGS.put("scene-group-detail", new PageMapping("scene-group-detail", "场景组详情", "/console/pages/scene-group-detail.html", "skill-scene"));
        PAGE_MAPPINGS.put("llm-config", new PageMapping("llm-config", "LLM配置", "/console/pages/llm-config.html", "skill-scene"));
        PAGE_MAPPINGS.put("llm-knowledge-config", new PageMapping("llm-knowledge-config", "知识库配置", "/console/pages/llm-knowledge-config.html", "skill-knowledge"));
        PAGE_MAPPINGS.put("dashboard", new PageMapping("dashboard", "控制台", "/console/pages/dashboard.html", "skill-scene"));
    }
    
    @Override
    public String getId() {
        return "navigate_to_page";
    }
    
    @Override
    public String getName() {
        return "navigate_to_page";
    }
    
    @Override
    public String getDescription() {
        return "导航到指定页面。当用户请求打开某个功能模块或页面时使用。支持通过页面ID或关键词匹配。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        Map<String, Object> pageProp = new LinkedHashMap<String, Object>();
        pageProp.put("type", "string");
        pageProp.put("description", "目标页面ID或关键词。可用页面: knowledge-center(知识资料库), capability-discovery(能力发现), capability-install(能力安装), capability-activation(能力激活), scene-group(场景组), llm-config(LLM配置), dashboard(控制台)");
        properties.put("page", pageProp);
        
        Map<String, Object> keywordProp = new LinkedHashMap<String, Object>();
        keywordProp.put("type", "string");
        keywordProp.put("description", "搜索关键词，用于模糊匹配页面名称");
        properties.put("keyword", keywordProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList());
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String pageId = (String) parameters.get("page");
        String keyword = (String) parameters.get("keyword");
        
        PageMapping targetPage = null;
        
        if (pageId != null && !pageId.isEmpty()) {
            targetPage = PAGE_MAPPINGS.get(pageId);
            
            if (targetPage == null) {
                targetPage = findByKeyword(pageId);
            }
        } else if (keyword != null && !keyword.isEmpty()) {
            targetPage = findByKeyword(keyword);
        }
        
        if (targetPage == null) {
            Map<String, Object> errorResult = new LinkedHashMap<String, Object>();
            errorResult.put("success", false);
            errorResult.put("error", "page_not_found");
            errorResult.put("message", "未找到匹配的页面: " + (pageId != null ? pageId : keyword));
            errorResult.put("availablePages", getAvailablePages());
            return ToolResult.failure("未找到匹配的页面", "page_not_found");
        }
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("action", "navigate");
        result.put("pageId", targetPage.pageId);
        result.put("pageName", targetPage.pageName);
        result.put("pagePath", targetPage.pagePath);
        result.put("skillId", targetPage.skillId);
        result.put("message", "正在导航到: " + targetPage.pageName);
        
        return ToolResult.success(result);
    }
    
    @Override
    public String getCategory() {
        return "navigation";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("navigation", "page", "redirect");
    }
    
    private PageMapping findByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        
        for (PageMapping mapping : PAGE_MAPPINGS.values()) {
            if (mapping.pageName.toLowerCase().contains(lowerKeyword)) {
                return mapping;
            }
            if (mapping.pageId.toLowerCase().contains(lowerKeyword)) {
                return mapping;
            }
        }
        
        if (lowerKeyword.contains("知识") || lowerKeyword.contains("文档") || lowerKeyword.contains("向量")) {
            return PAGE_MAPPINGS.get("knowledge-center");
        }
        if (lowerKeyword.contains("发现") || lowerKeyword.contains("能力")) {
            return PAGE_MAPPINGS.get("capability-discovery");
        }
        if (lowerKeyword.contains("安装") || lowerKeyword.contains("下载")) {
            return PAGE_MAPPINGS.get("capability-install");
        }
        if (lowerKeyword.contains("激活") || lowerKeyword.contains("启用")) {
            return PAGE_MAPPINGS.get("capability-activation");
        }
        if (lowerKeyword.contains("场景") || lowerKeyword.contains("scene")) {
            return PAGE_MAPPINGS.get("scene-group");
        }
        if (lowerKeyword.contains("配置") || lowerKeyword.contains("llm") || lowerKeyword.contains("模型")) {
            return PAGE_MAPPINGS.get("llm-config");
        }
        if (lowerKeyword.contains("控制台") || lowerKeyword.contains("首页") || lowerKeyword.contains("dashboard")) {
            return PAGE_MAPPINGS.get("dashboard");
        }
        
        return null;
    }
    
    private List<Map<String, String>> getAvailablePages() {
        List<Map<String, String>> pages = new ArrayList<>();
        for (PageMapping mapping : PAGE_MAPPINGS.values()) {
            Map<String, String> pageInfo = new LinkedHashMap<>();
            pageInfo.put("pageId", mapping.pageId);
            pageInfo.put("pageName", mapping.pageName);
            pages.add(pageInfo);
        }
        return pages;
    }
    
    private static class PageMapping {
        String pageId;
        String pageName;
        String pagePath;
        String skillId;
        
        PageMapping(String pageId, String pageName, String pagePath, String skillId) {
            this.pageId = pageId;
            this.pageName = pageName;
            this.pagePath = pagePath;
            this.skillId = skillId;
        }
    }
}
