package net.ooder.skill.knowledge.local.service.impl;

import net.ooder.skill.knowledge.local.model.IntentClassification;
import net.ooder.skill.knowledge.local.model.IntentType;
import net.ooder.skill.knowledge.local.model.SuggestedAction;
import net.ooder.skill.knowledge.local.service.IntentClassifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntentClassifierImpl implements IntentClassifier {
    
    private static final Logger log = LoggerFactory.getLogger(IntentClassifierImpl.class);
    
    private final Map<String, IntentPattern> patterns = new LinkedHashMap<>();
    
    public IntentClassifierImpl() {
        initPatterns();
    }
    
    private void initPatterns() {
        addPattern(IntentType.DATA_QUERY, 
            Arrays.asList("查询", "显示", "列出", "搜索", "查找", "获取", "找", "看"));
        addPattern(IntentType.CREATE_ACTION, 
            Arrays.asList("创建", "新建", "添加", "增加", "建立", "生成"));
        addPattern(IntentType.FORM_ASSIST, 
            Arrays.asList("填写", "设置", "设为", "加上", "填", "修改", "更改"));
        addPattern(IntentType.DOC_SEARCH, 
            Arrays.asList("怎么", "如何", "什么", "文档", "说明", "帮助文档", "使用方法"));
        addPattern(IntentType.SYSTEM_HELP, 
            Arrays.asList("帮助", "使用指南", "教程", "说明", "介绍"));
    }
    
    private void addPattern(IntentType type, List<String> keywords) {
        patterns.put(type.getCode(), new IntentPattern(type, keywords));
    }
    
    @Override
    public IntentClassification classify(String text) {
        if (text == null || text.trim().isEmpty()) {
            return createUnknownResult();
        }
        
        String normalizedText = text.trim().toLowerCase();
        
        for (Map.Entry<String, IntentPattern> entry : patterns.entrySet()) {
            IntentPattern pattern = entry.getValue();
            for (String keyword : pattern.getKeywords()) {
                if (normalizedText.contains(keyword.toLowerCase())) {
                    IntentClassification result = new IntentClassification();
                    result.setType(pattern.getType());
                    result.setConfidence(0.85);
                    result.setMatchedKeyword(keyword);
                    
                    SuggestedAction action = createSuggestedAction(pattern.getType(), text);
                    result.setSuggestedAction(action);
                    
                    log.debug("Classified text '{}' as {} with keyword '{}'", 
                        text, pattern.getType(), keyword);
                    return result;
                }
            }
        }
        
        return createUnknownResult();
    }
    
    @Override
    public IntentClassification classifyWithContext(String text, Map<String, Object> context) {
        IntentClassification base = classify(text);
        
        if (context != null && context.containsKey("pageType")) {
            String pageType = (String) context.get("pageType");
            base.setTarget(inferTarget(pageType, text));
        }
        
        return base;
    }
    
    private IntentClassification createUnknownResult() {
        IntentClassification result = new IntentClassification();
        result.setType(IntentType.UNKNOWN);
        result.setConfidence(0.0);
        return result;
    }
    
    private SuggestedAction createSuggestedAction(IntentType type, String text) {
        SuggestedAction action = new SuggestedAction();
        action.setType(type.getCode());
        
        switch (type) {
            case DATA_QUERY:
                action.setType("QUERY");
                break;
            case CREATE_ACTION:
                action.setType("NAVIGATE");
                break;
            case FORM_ASSIST:
                action.setType("FORM_UPDATE");
                break;
            case DOC_SEARCH:
                action.setType("SEARCH");
                break;
            case SYSTEM_HELP:
                action.setType("HELP");
                break;
            default:
                action.setType("UNKNOWN");
        }
        
        return action;
    }
    
    private String inferTarget(String pageType, String text) {
        if (text.contains("场景")) return "Scene";
        if (text.contains("模板")) return "Template";
        if (text.contains("能力")) return "Capability";
        if (text.contains("任务")) return "Task";
        if (text.contains("用户")) return "User";
        return null;
    }
    
    private static class IntentPattern {
        private final IntentType type;
        private final List<String> keywords;
        
        IntentPattern(IntentType type, List<String> keywords) {
            this.type = type;
            this.keywords = keywords;
        }
        
        IntentType getType() { return type; }
        List<String> getKeywords() { return keywords; }
    }
}
