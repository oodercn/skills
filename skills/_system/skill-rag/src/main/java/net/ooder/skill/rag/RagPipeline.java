package net.ooder.skill.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RagPipeline {

    private static final Logger log = LoggerFactory.getLogger(RagPipeline.class);

    @Autowired(required = false)
    private KnowledgeService knowledgeService;

    @Autowired(required = false)
    private DictService dictService;

    @Autowired
    private KnowledgeClassifierService classifierService;

    public KnowledgeDocument ingestBusinessData(BusinessDataIngestRequest request) {
        log.info("[RagPipeline] Ingesting business data: type={}", request.getDataType());

        String category = classifierService.classify(
            request.getTitle() + "\n" + request.getContent()
        );

        if (knowledgeService != null) {
            KnowledgeDocument doc = knowledgeService.uploadDocument(
                request.getSourceUserId() != null ? request.getSourceUserId() : "system",
                request.getTitle(),
                request.getContent(),
                request.getDataType()
            );
            log.info("[RagPipeline] Ingested as docId={}, category={}", doc.getDocId(), category);

            if (request.isSyncToDict() && dictService != null) {
                syncToDictionary(doc, category);
            }

            return doc;
        }

        log.warn("[RagPipeline] KnowledgeService not available, returning mock document");
        return new KnowledgeDocument();
    }

    public RagKnowledgeConfig buildKnowledgeConfig(String sceneGroupId, String query) {
        log.info("[RagPipeline] Building knowledge config for query: {}", query);

        RagKnowledgeConfig config = new RagKnowledgeConfig();

        if (knowledgeService != null && query != null) {
            try {
                List<KnowledgeDocument> relatedDocs = searchRelated(query, 5);

                StringBuilder contextBuilder = new StringBuilder();
                for (KnowledgeDocument doc : relatedDocs) {
                    contextBuilder.append("### ").append(doc.getTitle()).append("\n");
                    contextBuilder.append(doc.getContent()).append("\n\n");
                }
                config.setKnowledgeContext(contextBuilder.toString());
            } catch (Exception e) {
                log.warn("[RagPipeline] Search failed, using empty context: {}", e.getMessage());
                config.setKnowledgeContext("");
            }
        }

        if (dictService != null) {
            try {
                List<DictItem> dictItems = dictService.getDictItems(sceneGroupId != null ? sceneGroupId : "default");
                List<Map<String, String>> entries = new ArrayList<>();
                if (dictItems != null) {
                    for (DictItem item : dictItems) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("key", item.getCode());
                        entry.put("value", item.getName());
                        entries.add(entry);
                    }
                }
                config.setDictItems(entries);
            } catch (Exception e) {
                log.warn("[RagPipeline] Dict lookup failed: {}", e.getMessage());
                config.setDictItems(new ArrayList<>());
            }
        }

        config.setSystemPromptTemplate(buildRagPromptTemplate(config));

        return config;
    }

    public String enhancePromptWithRAG(String userQuery, String sceneGroupId,
                                         List<String> knowledgeBaseIds) {
        if (knowledgeService == null || userQuery == null) {
            return null;
        }

        try {
            List<KnowledgeDocument> docs = searchRelated(userQuery, 3);
            if (docs.isEmpty()) return null;

            StringBuilder ragContext = new StringBuilder();
            ragContext.append("\n## 参考资料\n");
            ragContext.append("以下是与用户问题相关的参考资料，请基于这些资料回答：\n\n");

            for (int i = 0; i < docs.size(); i++) {
                ragContext.append("[").append(i + 1).append("] **")
                         .append(docs.get(i).getTitle()).append("**\n");
                ragContext.append(docs.get(i).getContent()).append("\n\n");
            }

            return ragContext.toString();
        } catch (Exception e) {
            log.warn("[RagPipeline] RAG enhancement failed: {}", e.getMessage());
            return null;
        }
    }

    public List<KnowledgeDocument> searchRelated(String query, int limit) {
        if (knowledgeService == null) {
            return new ArrayList<>();
        }
        try {
            return knowledgeService.search(query, limit);
        } catch (Exception e) {
            log.warn("[RagPipeline] search failed: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private void syncToDictionary(KnowledgeDocument doc, String category) {
        try {
            List<DictEntity> entities = classifierService.extractEntities(doc.getContent());
            if (dictService != null && !entities.isEmpty()) {
                dictService.refreshCache();
            }
        } catch (Exception e) {
            log.warn("[RagPipeline] Failed to sync to dictionary: {}", e.getMessage());
        }
    }

    private String buildRagPromptTemplate(RagKnowledgeConfig config) {
        return """
            你是一个智能助手。在回答问题时，请参考以下知识和字典数据：

            {{knowledge_context}}

            ## 可用字典数据
            {{dict_items}}

            请基于以上资料给出准确、专业的回答。
            """;
    }

    public static class BusinessDataIngestRequest {
        private String title;
        private String content;
        private String dataType;
        private String sourceUserId;
        private boolean syncToDict;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public String getSourceUserId() { return sourceUserId; }
        public void setSourceUserId(String sourceUserId) { this.sourceUserId = sourceUserId; }
        public boolean isSyncToDict() { return syncToDict; }
        public void setSyncToDict(boolean syncToDict) { this.syncToDict = syncToDict; }
    }

    public record DictEntity(String category, String key, String value, String description) {}

    public static class RagKnowledgeConfig {
        private String knowledgeContext;
        private List<Map<String, String>> dictItems;
        private String systemPromptTemplate;

        public String getKnowledgeContext() { return knowledgeContext; }
        public void setKnowledgeContext(String knowledgeContext) { this.knowledgeContext = knowledgeContext; }
        public List<Map<String, String>> getDictItems() { return dictItems; }
        public void setDictItems(List<Map<String, String>> dictItems) { this.dictItems = dictItems; }
        public String getSystemPromptTemplate() { return systemPromptTemplate; }
        public void setSystemPromptTemplate(String systemPromptTemplate) { this.systemPromptTemplate = systemPromptTemplate; }
    }
}
