package net.ooder.skill.rag.impl;

import net.ooder.skill.rag.KnowledgeClassifierService;
import net.ooder.skill.rag.RagPipeline;
import net.ooder.spi.facade.SpiServices;
import net.ooder.spi.llm.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LlmClassifierServiceImpl implements KnowledgeClassifierService {

    private static final Logger log = LoggerFactory.getLogger(LlmClassifierServiceImpl.class);

    private static final Pattern ENTITY_PATTERN = Pattern.compile(
        "([\\u4e00-\\u9fa5a-zA-Z]{2,10})[：:](.{1,50}?)(?:[；;，,]|$)"
    );
    private static final Pattern TAG_PATTERN = Pattern.compile("#([\\u4e00-\\u9fa5a-zA-Z_]{2,8})#");

    private LlmProvider getLlmProvider() {
        return SpiServices.llm().orElse(null);
    }

    private static final Map<String, String> CATEGORY_KEYWORDS = Map.ofEntries(
        Map.entry("技术文档", "技术 API 接口 文档 开发 编程"),
        Map.entry("业务流程", "流程 审批 流转 工单"),
        Map.entry("知识问答", "问题 答案 FAQ 常见"),
        Map.entry("会议纪要", "会议 议题 决议 纪要"),
        Map.entry("项目资料", "项目 需求 方案 报告"),
        Map.entry("培训材料", "培训 教程 指南 手册")
    );

    @Override
    public String classify(String text) {
        if (text == null || text.isBlank()) return "未分类";

        String lowerText = text.toLowerCase();

        for (Map.Entry<String, String> entry : CATEGORY_KEYWORDS.entrySet()) {
            String[] keywords = entry.getValue().split("\\s+");
            for (String kw : keywords) {
                if (lowerText.contains(kw.toLowerCase())) {
                    return entry.getKey();
                }
            }
        }

        LlmProvider llmProvider = getLlmProvider();
        if (llmProvider != null && llmProvider.isAvailable()) {
            try {
                String prompt = "请将以下文本分类到最合适的类别中（技术文档/业务流程/知识问答/会议纪要/项目资料/培训材料/其他）。只返回类别名称。\n\n" + text.substring(0, Math.min(500, text.length()));
                
                net.ooder.spi.llm.model.LlmRequest request = new net.ooder.spi.llm.model.LlmRequest();
                request.setModelId("default");
                request.setStream(false);
                request.setMessages(List.of(
                    new net.ooder.spi.llm.model.LlmRequest.Message() {{
                        setRole("system");
                        setContent("你是一个文本分类助手，请根据文本内容判断其所属类别。");
                    }},
                    new net.ooder.spi.llm.model.LlmRequest.Message() {{
                        setRole("user");
                        setContent(prompt);
                    }}
                ));
                
                var response = llmProvider.chat(request);
                
                if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                    String llmCategory = response.getChoices().get(0).getMessage().getContent().trim();
                    for (String known : CATEGORY_KEYWORDS.keySet()) {
                        if (llmCategory.contains(known)) return known;
                    }
                }
            } catch (Exception e) {
                log.debug("[LlmClassifier] Fallback to keyword matching: {}", e.getMessage());
            }
        }

        return "其他";
    }

    @Override
    public List<String> extractTags(String text) {
        Set<String> tags = new LinkedHashSet<>();

        Matcher m = TAG_PATTERN.matcher(text);
        while (m.find()) {
            tags.add(m.group(1));
        }

        for (Map.Entry<String, String> entry : CATEGORY_KEYWORDS.entrySet()) {
            String[] keywords = entry.getValue().split("\\s+");
            for (String kw : keywords) {
                if (text.toLowerCase().contains(kw.toLowerCase())) {
                    tags.add(kw);
                }
            }
        }

        return new ArrayList<>(tags);
    }

    @Override
    public List<RagPipeline.DictEntity> extractEntities(String text) {
        List<RagPipeline.DictEntity> entities = new ArrayList<>();

        Matcher m = ENTITY_PATTERN.matcher(text);
        while (m.find()) {
            String key = m.group(1).trim();
            String value = m.group(2).trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                entities.add(new RagPipeline.DictEntity("auto_extracted", key, value, ""));
            }
        }

        return entities;
    }
}
