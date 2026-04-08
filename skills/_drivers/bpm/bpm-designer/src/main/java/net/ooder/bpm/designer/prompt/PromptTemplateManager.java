package net.ooder.bpm.designer.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PromptTemplateManager {
    
    private static final Logger log = LoggerFactory.getLogger(PromptTemplateManager.class);
    
    private final Map<String, PromptTemplate> templates = new HashMap<>();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @PostConstruct
    public void init() {
        loadTemplates();
    }
    
    private void loadTemplates() {
        try {
            Resource[] resources = resolver.getResources("classpath:prompts/*.yaml");
            
            for (Resource resource : resources) {
                try {
                    PromptTemplate template = yamlMapper.readValue(
                        resource.getInputStream(), 
                        PromptTemplate.class
                    );
                    
                    if (template.getName() != null) {
                        templates.put(template.getName(), template);
                        log.info("Loaded prompt template: {}", template.getName());
                    }
                } catch (IOException e) {
                    log.warn("Failed to load prompt template from {}: {}", 
                        resource.getFilename(), e.getMessage());
                }
            }
            
            loadDefaultTemplates();
            
        } catch (IOException e) {
            log.warn("Failed to scan prompt templates: {}", e.getMessage());
            loadDefaultTemplates();
        }
    }
    
    private void loadDefaultTemplates() {
        if (!templates.containsKey("performer-derivation")) {
            PromptTemplate template = new PromptTemplate();
            template.setName("performer-derivation");
            template.setDescription("办理人推导Prompt模板");
            template.setSystemPrompt(buildDefaultPerformerSystemPrompt());
            template.setUserPromptTemplate(buildDefaultPerformerUserPrompt());
            templates.put("performer-derivation", template);
            log.info("Loaded default template: performer-derivation");
        }
        
        if (!templates.containsKey("capability-matching")) {
            PromptTemplate template = new PromptTemplate();
            template.setName("capability-matching");
            template.setDescription("能力匹配Prompt模板");
            template.setSystemPrompt(buildDefaultCapabilitySystemPrompt());
            template.setUserPromptTemplate(buildDefaultCapabilityUserPrompt());
            templates.put("capability-matching", template);
            log.info("Loaded default template: capability-matching");
        }
        
        if (!templates.containsKey("form-matching")) {
            PromptTemplate template = new PromptTemplate();
            template.setName("form-matching");
            template.setDescription("表单匹配Prompt模板");
            template.setSystemPrompt(buildDefaultFormSystemPrompt());
            template.setUserPromptTemplate(buildDefaultFormUserPrompt());
            templates.put("form-matching", template);
            log.info("Loaded default template: form-matching");
        }
    }
    
    public PromptTemplate getTemplate(String name) {
        return templates.get(name);
    }
    
    public String buildPrompt(String templateName, Map<String, Object> context) {
        PromptTemplate template = templates.get(templateName);
        if (template == null) {
            log.warn("Template not found: {}", templateName);
            return "";
        }
        return template.render(context);
    }
    
    public String buildFullPrompt(String templateName, Map<String, Object> context) {
        PromptTemplate template = templates.get(templateName);
        if (template == null) {
            log.warn("Template not found: {}", templateName);
            return "";
        }
        return template.renderFull(context);
    }
    
    public String getSystemPrompt(String templateName) {
        PromptTemplate template = templates.get(templateName);
        return template != null ? template.getSystemPrompt() : null;
    }
    
    public String getUserPrompt(String templateName, Map<String, Object> context) {
        PromptTemplate template = templates.get(templateName);
        return template != null ? template.render(context) : null;
    }
    
    public void registerTemplate(String name, PromptTemplate template) {
        templates.put(name, template);
    }
    
    public Map<String, PromptTemplate> getAllTemplates() {
        return new HashMap<>(templates);
    }
    
    private String buildDefaultPerformerSystemPrompt() {
        return """
            你是一个BPM流程设计专家，负责根据活动描述推导合适的办理人。
            
            你可以调用以下函数获取信息：
            - get_organization_tree: 获取组织架构
            - get_users_by_role: 按角色获取用户
            - search_users: 语义搜索用户
            - get_department_members: 获取部门成员
            - get_department_leader: 获取部门负责人
            
            推导规则：
            1. 首先理解活动描述中的角色需求
            2. 调用相关函数获取候选人信息
            3. 根据业务规则筛选合适的办理人
            4. 返回推导结果和推理过程
            
            输出格式要求：
            {
              "assigneeType": "ROLE|USER|DEPT",
              "assigneeId": "角色或用户或部门ID",
              "assigneeName": "显示名称",
              "reasoning": "推导理由"
            }
            """;
    }
    
    private String buildDefaultPerformerUserPrompt() {
        return """
            当前流程: {{processName}}
            当前活动: {{activityName}}
            活动描述: {{activityDesc}}
            
            请根据以上信息推导合适的办理人配置。
            """;
    }
    
    private String buildDefaultCapabilitySystemPrompt() {
        return """
            你是一个能力匹配专家，负责根据活动描述匹配合适的能力。
            
            能力匹配规则：
            1. 分析活动描述中的动词和名词，识别所需能力
            2. 根据能力描述进行语义匹配
            3. 考虑能力的参数和返回值是否满足需求
            4. 返回匹配度排序和能力绑定配置
            
            匹配维度：
            - 语义相似度: 活动描述与能力描述的语义相似程度
            - 功能匹配度: 活动需求与能力功能的匹配程度
            - 参数兼容性: 活动输入输出与能力参数的兼容程度
            
            输出格式要求：
            {
              "matches": [
                {
                  "capId": "能力ID",
                  "capName": "能力名称",
                  "matchScore": 0.95,
                  "matchReason": "匹配原因"
                }
              ],
              "reasoning": "整体匹配说明"
            }
            """;
    }
    
    private String buildDefaultCapabilityUserPrompt() {
        return """
            当前流程: {{processName}}
            当前活动: {{activityName}}
            活动描述: {{activityDesc}}
            
            可用能力列表:
            {{capabilities}}
            
            请匹配最合适的能力，并给出匹配理由。
            """;
    }
    
    private String buildDefaultFormSystemPrompt() {
        return """
            你是一个表单匹配专家，负责根据活动描述匹配合适的表单。
            
            表单匹配规则：
            1. 分析活动描述，识别需要收集的数据
            2. 根据表单字段进行语义匹配
            3. 考虑表单的适用场景和业务领域
            4. 返回匹配度排序和表单配置
            
            如果没有合适的现有表单，可以建议生成新表单。
            
            输出格式要求：
            {
              "matches": [
                {
                  "formId": "表单ID",
                  "formName": "表单名称",
                  "matchScore": 0.95,
                  "matchReason": "匹配原因"
                }
              ],
              "suggestedSchema": {
                "formName": "建议表单名称",
                "fields": [...]
              },
              "reasoning": "整体匹配说明"
            }
            """;
    }
    
    private String buildDefaultFormUserPrompt() {
        return """
            当前流程: {{processName}}
            当前活动: {{activityName}}
            活动描述: {{activityDesc}}
            
            可用表单列表:
            {{forms}}
            
            请匹配最合适的表单，或建议生成新表单。
            """;
    }
}
