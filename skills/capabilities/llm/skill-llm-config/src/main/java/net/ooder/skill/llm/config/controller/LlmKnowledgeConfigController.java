package net.ooder.skill.llm.config.controller;

import net.ooder.skill.llm.config.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm-knowledge-config")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmKnowledgeConfigController {

    private static final Logger log = LoggerFactory.getLogger(LlmKnowledgeConfigController.class);

    private final Map<String, List<Map<String, Object>>> synonymsStore = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> interfacesStore = new HashMap<>();
    private final List<Map<String, Object>> dictionariesStore = new ArrayList<>();
    private Map<String, Object> promptTemplateStore = getDefaultPromptTemplate();

    private Map<String, Object> getDefaultPromptTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("id", "default");
        template.put("name", "知识增强回答模板");
        template.put("content", "你是一个专业的AI助手。请根据以下知识库内容回答用户问题：\n\n{knowledge}\n\n用户问题：{question}\n\n请提供准确、有帮助的回答。如果知识库中没有相关信息，请如实告知。");
        template.put("description", "用于知识检索后的回答生成");
        return template;
    }

    @GetMapping("/dictionaries")
    public ResultModel<List<Map<String, Object>>> getDictionaries() {
        log.info("[LlmKnowledgeConfigController] Get dictionaries");
        return ResultModel.success(dictionariesStore);
    }

    @PostMapping("/dictionaries")
    public ResultModel<Map<String, Object>> addDictionary(@RequestBody Map<String, Object> term) {
        log.info("[LlmKnowledgeConfigController] Add dictionary term: {}", term.get("term"));
        String termId = UUID.randomUUID().toString();
        term.put("termId", termId);
        term.put("createTime", new Date().toString());
        dictionariesStore.add(term);
        return ResultModel.success(term);
    }

    @DeleteMapping("/dictionaries/{termId}")
    public ResultModel<Void> deleteDictionary(@PathVariable String termId) {
        log.info("[LlmKnowledgeConfigController] Delete dictionary term: {}", termId);
        dictionariesStore.removeIf(term -> termId.equals(term.get("termId")));
        return ResultModel.success(null);
    }

    @GetMapping("/prompt-templates/default")
    public ResultModel<Map<String, Object>> getDefaultPromptTemplateEndpoint() {
        log.info("[LlmKnowledgeConfigController] Get default prompt template");
        return ResultModel.success(promptTemplateStore);
    }

    @PostMapping("/prompt-templates")
    public ResultModel<Map<String, Object>> savePromptTemplate(@RequestBody Map<String, Object> template) {
        log.info("[LlmKnowledgeConfigController] Save prompt template: {}", template.get("name"));
        promptTemplateStore.putAll(template);
        promptTemplateStore.put("updateTime", new Date().toString());
        return ResultModel.success(promptTemplateStore);
    }

    @GetMapping("/synonyms")
    public ResultModel<List<Map<String, Object>>> getSynonyms() {
        log.info("[LlmKnowledgeConfigController] Get synonyms");
        List<Map<String, Object>> allSynonyms = new ArrayList<>();
        for (List<Map<String, Object>> list : synonymsStore.values()) {
            allSynonyms.addAll(list);
        }
        return ResultModel.success(allSynonyms);
    }

    @PostMapping("/synonyms")
    public ResultModel<Map<String, Object>> addSynonym(@RequestBody Map<String, Object> synonym) {
        log.info("[LlmKnowledgeConfigController] Add synonym");
        String id = UUID.randomUUID().toString();
        synonym.put("id", id);
        synonym.put("createTime", new Date().toString());
        synonymsStore.computeIfAbsent("default", k -> new ArrayList<>()).add(synonym);
        return ResultModel.success(synonym);
    }

    @GetMapping("/interfaces")
    public ResultModel<List<Map<String, Object>>> getInterfaces() {
        log.info("[LlmKnowledgeConfigController] Get interfaces");
        List<Map<String, Object>> allInterfaces = new ArrayList<>();
        for (List<Map<String, Object>> list : interfacesStore.values()) {
            allInterfaces.addAll(list);
        }
        return ResultModel.success(allInterfaces);
    }

    @PostMapping("/interfaces")
    public ResultModel<Map<String, Object>> addInterface(@RequestBody Map<String, Object> interface_) {
        log.info("[LlmKnowledgeConfigController] Add interface");
        String id = UUID.randomUUID().toString();
        interface_.put("id", id);
        interface_.put("createTime", new Date().toString());
        interfacesStore.computeIfAbsent("default", k -> new ArrayList<>()).add(interface_);
        return ResultModel.success(interface_);
    }
}
