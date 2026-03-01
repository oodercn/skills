package net.ooder.skill.knowledge.local.service.impl;

import net.ooder.skill.knowledge.local.model.IntentInfo;
import net.ooder.skill.knowledge.local.model.ResolvedTerm;
import net.ooder.skill.knowledge.local.model.TermMapping;
import net.ooder.skill.knowledge.local.model.TermResolution;
import net.ooder.skill.knowledge.local.service.TermMappingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TermMappingServiceImpl implements TermMappingService {
    
    private static final Logger log = LoggerFactory.getLogger(TermMappingServiceImpl.class);
    
    @Value("${knowledge.term.builtin:true}")
    private boolean loadBuiltin;
    
    @Value("${knowledge.term.userDefined:true}")
    private boolean loadUserDefined;
    
    @Value("${knowledge.term.userTermPath:./data/user-terms.json}")
    private String userTermPath;
    
    private final Map<String, TermMapping> termMap = new ConcurrentHashMap<>();
    private final Map<String, List<TermMapping>> domainMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        if (loadBuiltin) {
            loadBuiltinTerms();
        }
        if (loadUserDefined) {
            loadUserTerms();
        }
        log.info("Loaded {} term mappings", termMap.size());
    }
    
    @Override
    public void loadBuiltinTerms() {
        Map<String, TermMapping> builtin = getBuiltinTerms();
        for (Map.Entry<String, TermMapping> entry : builtin.entrySet()) {
            registerMapping(entry.getValue());
        }
        log.info("Loaded {} builtin terms", builtin.size());
    }
    
    private Map<String, TermMapping> getBuiltinTerms() {
        Map<String, TermMapping> terms = new LinkedHashMap<>();
        
        terms.put("日志汇报", createMapping("日志汇报", "SceneType.LOG_REPORT", "场景类型", "scene"));
        terms.put("日志", createMapping("日志", "Log", "日志对象", "data"));
        terms.put("汇报", createMapping("汇报", "Report", "汇报对象", "data"));
        terms.put("场景模板", createMapping("场景模板", "Template", "模板对象", "scene"));
        terms.put("模板", createMapping("模板", "Template", "模板对象", "scene"));
        terms.put("能力单元", createMapping("能力单元", "Capability", "能力对象", "capability"));
        terms.put("能力", createMapping("能力", "Capability", "能力对象", "capability"));
        terms.put("参与者", createMapping("参与者", "Participant", "参与者对象", "scene"));
        terms.put("工作流", createMapping("工作流", "Workflow", "工作流对象", "scene"));
        terms.put("审批", createMapping("审批", "Approval", "审批流程", "business"));
        terms.put("任务", createMapping("任务", "Task", "任务对象", "business"));
        terms.put("用户", createMapping("用户", "User", "用户对象", "system"));
        terms.put("角色", createMapping("角色", "Role", "角色对象", "system"));
        terms.put("权限", createMapping("权限", "Permission", "权限对象", "system"));
        terms.put("部门", createMapping("部门", "Department", "部门对象", "org"));
        terms.put("组织", createMapping("组织", "Organization", "组织对象", "org"));
        terms.put("配置", createMapping("配置", "Config", "配置对象", "system"));
        terms.put("设置", createMapping("设置", "Settings", "设置对象", "system"));
        terms.put("消息", createMapping("消息", "Message", "消息对象", "communication"));
        terms.put("通知", createMapping("通知", "Notification", "通知对象", "communication"));
        terms.put("文件", createMapping("文件", "File", "文件对象", "storage"));
        terms.put("存储", createMapping("存储", "Storage", "存储对象", "storage"));
        terms.put("场景", createMapping("场景", "Scene", "场景对象", "scene"));
        terms.put("场景组", createMapping("场景组", "SceneGroup", "场景组对象", "scene"));
        terms.put("步骤", createMapping("步骤", "Step", "步骤对象", "workflow"));
        terms.put("触发器", createMapping("触发器", "Trigger", "触发器对象", "workflow"));
        terms.put("定时", createMapping("定时", "Schedule", "定时任务", "workflow"));
        terms.put("提醒", createMapping("提醒", "Reminder", "提醒对象", "notification"));
        terms.put("智能", createMapping("智能", "Intelligence", "智能能力", "llm"));
        terms.put("助手", createMapping("助手", "Assistant", "助手对象", "llm"));
        terms.put("LLM", createMapping("LLM", "LLM", "大语言模型", "llm"));
        terms.put("知识库", createMapping("知识库", "KnowledgeBase", "知识库对象", "knowledge"));
        terms.put("文档", createMapping("文档", "Document", "文档对象", "knowledge"));
        terms.put("搜索", createMapping("搜索", "Search", "搜索操作", "action"));
        terms.put("查询", createMapping("查询", "Query", "查询操作", "action"));
        terms.put("创建", createMapping("创建", "Create", "创建操作", "action"));
        terms.put("删除", createMapping("删除", "Delete", "删除操作", "action"));
        terms.put("更新", createMapping("更新", "Update", "更新操作", "action"));
        terms.put("编辑", createMapping("编辑", "Edit", "编辑操作", "action"));
        
        return terms;
    }
    
    private TermMapping createMapping(String term, String systemConcept, String type, String domain) {
        TermMapping mapping = new TermMapping(term, systemConcept, type);
        mapping.setDomain(domain);
        mapping.setConfidence(1.0);
        return mapping;
    }
    
    @Override
    public void loadUserTerms() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("terms/user-terms.json");
            if (is == null) {
                log.debug("No user terms file found");
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            String json = sb.toString();
            List<TermMapping> userTerms = parseUserTerms(json);
            for (TermMapping term : userTerms) {
                registerMapping(term);
            }
            
            log.info("Loaded {} user terms", userTerms.size());
            
        } catch (Exception e) {
            log.warn("Failed to load user terms: {}", e.getMessage());
        }
    }
    
    private List<TermMapping> parseUserTerms(String json) {
        List<TermMapping> terms = new ArrayList<>();
        
        if (json == null || json.trim().isEmpty()) {
            return terms;
        }
        
        return terms;
    }
    
    private void registerMapping(TermMapping mapping) {
        termMap.put(mapping.getTerm(), mapping);
        
        String domain = mapping.getDomain();
        if (domain != null) {
            domainMap.computeIfAbsent(domain, k -> new ArrayList<>()).add(mapping);
        }
    }
    
    @Override
    public TermResolution resolveTerm(String text, Map<String, Object> context) {
        TermResolution resolution = new TermResolution();
        resolution.setOriginalText(text);
        resolution.setContext(context);
        
        List<ResolvedTerm> resolved = new ArrayList<>();
        String remainingText = text;
        
        List<String> sortedTerms = new ArrayList<>(termMap.keySet());
        sortedTerms.sort((a, b) -> Integer.compare(b.length(), a.length()));
        
        for (String term : sortedTerms) {
            if (remainingText.contains(term)) {
                TermMapping mapping = termMap.get(term);
                ResolvedTerm rt = new ResolvedTerm();
                rt.setTerm(term);
                rt.setMappedTo(mapping.getSystemConcept());
                rt.setMappedType(mapping.getType());
                rt.setConfidence(mapping.getConfidence());
                rt.setDescription(mapping.getDescription());
                resolved.add(rt);
                
                remainingText = remainingText.replace(term, "");
            }
        }
        
        resolution.setResolvedTerms(resolved);
        
        if (!resolved.isEmpty()) {
            IntentInfo intent = inferIntent(text, resolved);
            resolution.setIntent(intent);
        }
        
        return resolution;
    }
    
    private IntentInfo inferIntent(String text, List<ResolvedTerm> resolvedTerms) {
        IntentInfo intent = new IntentInfo();
        
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("创建") || lowerText.contains("新建") || lowerText.contains("添加")) {
            intent.setType("CREATE_ACTION");
        } else if (lowerText.contains("查询") || lowerText.contains("搜索") || lowerText.contains("显示")) {
            intent.setType("DATA_QUERY");
        } else if (lowerText.contains("填写") || lowerText.contains("设置") || lowerText.contains("修改")) {
            intent.setType("FORM_ASSIST");
        } else if (lowerText.contains("怎么") || lowerText.contains("如何") || lowerText.contains("什么")) {
            intent.setType("DOC_SEARCH");
        } else {
            intent.setType("UNKNOWN");
        }
        
        for (ResolvedTerm rt : resolvedTerms) {
            if ("scene".equals(getDomain(rt.getMappedTo()))) {
                intent.setTarget("Scene");
                break;
            } else if ("capability".equals(getDomain(rt.getMappedTo()))) {
                intent.setTarget("Capability");
                break;
            }
        }
        
        return intent;
    }
    
    private String getDomain(String systemConcept) {
        for (TermMapping mapping : termMap.values()) {
            if (mapping.getSystemConcept().equals(systemConcept)) {
                return mapping.getDomain();
            }
        }
        return null;
    }
    
    @Override
    public void registerTermMapping(String term, String systemConcept, net.ooder.skill.knowledge.local.model.TermMappingDTO options) {
        TermMapping mapping = new TermMapping(term, systemConcept, options.getType());
        mapping.setDomain("user");
        mapping.setConfidence(0.9);
        registerMapping(mapping);
        log.info("Registered term mapping: {} -> {}", term, systemConcept);
    }
    
    @Override
    public List<TermMapping> getTermMappings(String domain) {
        if (domain == null || domain.isEmpty()) {
            return new ArrayList<>(termMap.values());
        }
        return domainMap.getOrDefault(domain, new ArrayList<>());
    }
    
    @Override
    public TermMapping getMappingByTerm(String term) {
        return termMap.get(term);
    }
}
