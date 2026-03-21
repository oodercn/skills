package net.ooder.mvp.skill.scene.llm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/knowledge-bases")
public class KnowledgeBaseBindingController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseBindingController.class);

    @GetMapping("/{kbId}/bindings")
    public Map<String, Object> getKnowledgeBaseBindings(@PathVariable String kbId) {
        log.debug("[KbBinding] Getting bindings for knowledge base: {}", kbId);
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kbId", kbId);
        
        List<Map<String, Object>> bindings = new ArrayList<>();
        
        bindings.addAll(getSkillBindings(kbId));
        bindings.addAll(getSceneBindings(kbId));
        bindings.addAll(getPromptBindings(kbId));
        
        result.put("bindings", bindings);
        result.put("totalBindings", bindings.size());
        
        return result;
    }
    
    @GetMapping("/{kbId}/bindings/skills")
    public Map<String, Object> getSkillBindingsForKb(@PathVariable String kbId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kbId", kbId);
        result.put("bindings", getSkillBindings(kbId));
        return result;
    }
    
    @GetMapping("/{kbId}/bindings/scenes")
    public Map<String, Object> getSceneBindingsForKb(@PathVariable String kbId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kbId", kbId);
        result.put("bindings", getSceneBindings(kbId));
        return result;
    }

    private List<Map<String, Object>> getSkillBindings(String kbId) {
        List<Map<String, Object>> bindings = new ArrayList<>();
        
        if ("kb-skill-prompts".equals(kbId) || "kb-global-menu".equals(kbId)) {
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("type", "skill");
            binding.put("id", "skill-scene");
            binding.put("name", "场景技能");
            binding.put("purpose", "kb-skill-prompts".equals(kbId) ? "提示语检索" : "菜单导航");
            binding.put("layer", "SKILL");
            binding.put("priority", 1);
            bindings.add(binding);
        }
        
        if ("kb-skill-docs".equals(kbId)) {
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("type", "skill");
            binding.put("id", "skill-knowledge");
            binding.put("name", "知识库技能");
            binding.put("purpose", "RAG增强");
            binding.put("layer", "SKILL");
            binding.put("priority", 2);
            bindings.add(binding);
        }
        
        return bindings;
    }

    private List<Map<String, Object>> getSceneBindings(String kbId) {
        List<Map<String, Object>> bindings = new ArrayList<>();
        
        if ("kb-scene-docs".equals(kbId) || "kb-skill-prompts".equals(kbId)) {
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("type", "scene");
            binding.put("id", "scene-discovery");
            binding.put("name", "能力发现场景");
            binding.put("purpose", "RAG增强");
            binding.put("layer", "SCENE");
            binding.put("priority", 2);
            bindings.add(binding);
        }
        
        if ("kb-platform-guide".equals(kbId)) {
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("type", "scene");
            binding.put("id", "scene-activation");
            binding.put("name", "场景激活");
            binding.put("purpose", "平台指南检索");
            binding.put("layer", "GENERAL");
            binding.put("priority", 1);
            bindings.add(binding);
        }
        
        return bindings;
    }

    private List<Map<String, Object>> getPromptBindings(String kbId) {
        List<Map<String, Object>> bindings = new ArrayList<>();
        
        if ("kb-skill-prompts".equals(kbId)) {
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("type", "prompt");
            binding.put("id", "system-prompt");
            binding.put("name", "系统提示词");
            binding.put("purpose", "SystemPrompt检索");
            binding.put("layer", "SKILL");
            bindings.add(binding);
            
            Map<String, Object> roleBinding = new LinkedHashMap<>();
            roleBinding.put("type", "prompt");
            roleBinding.put("id", "role-discovery-assistant");
            roleBinding.put("name", "发现助手角色提示词");
            roleBinding.put("purpose", "角色Prompt检索");
            roleBinding.put("layer", "SKILL");
            bindings.add(roleBinding);
        }
        
        return bindings;
    }
}
