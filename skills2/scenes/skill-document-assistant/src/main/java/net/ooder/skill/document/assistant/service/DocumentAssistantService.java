package net.ooder.skill.document.assistant.service;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.rag.RagApi;
import net.ooder.scene.skill.rag.RagContext;
import net.ooder.scene.skill.rag.RagResult;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.DocumentCreateRequest;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseCreateRequest;
import net.ooder.scene.skill.knowledge.KnowledgeBaseUpdateRequest;
import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.MessageRequest;
import net.ooder.scene.skill.conversation.MessageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentAssistantService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentAssistantService.class);
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private RagApi ragPipeline;
    
    @Autowired(required = false)
    private ConversationService conversationService;
    
    private final Map<String, List<QueryHistory>> queryHistoryMap = new ConcurrentHashMap<>();
    private final Map<String, List<Feedback>> feedbackMap = new ConcurrentHashMap<>();
    
    public QueryResult queryDocument(String kbId, String query, Integer topK) {
        log.info("Querying document: kbId={}, query={}", kbId, query);
        
        int k = topK != null ? topK : 5;
        
        RagContext context = RagContext.builder()
            .kbId(kbId)
            .query(query)
            .topK(k)
            .threshold(0.7f)
            .build();
        
        RagResult ragResult = ragPipeline.retrieve(context);
        
        String answer = ragPipeline.generate(query, ragResult);
        
        List<SourceReference> sources = new ArrayList<>();
        if (ragResult.getChunks() != null) {
            for (var chunk : ragResult.getChunks()) {
                sources.add(new SourceReference(
                    chunk.getDocId(),
                    chunk.getDocTitle(),
                    chunk.getContent(),
                    chunk.getScore()
                ));
            }
        }
        
        float confidence = ragResult.getChunks() != null && !ragResult.getChunks().isEmpty()
            ? ragResult.getChunks().get(0).getScore()
            : 0.0f;
        
        return new QueryResult(answer, sources, confidence);
    }
    
    public UploadResult uploadDocument(String userId, String kbId, MultipartFile file, 
                                        boolean autoProcess, Map<String, Object> metadata) throws IOException {
        log.info("Uploading document: userId={}, kbId={}, filename={}", userId, kbId, file.getOriginalFilename());
        
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setTitle(file.getOriginalFilename());
        request.setContent(new String(file.getBytes()));
        request.setSource(Document.SOURCE_FILE);
        request.setMetadata(metadata != null ? metadata : new HashMap<>());
        request.getMetadata().put("filename", file.getOriginalFilename());
        request.getMetadata().put("contentType", file.getContentType());
        request.getMetadata().put("size", file.getSize());
        request.getMetadata().put("status", "PENDING");
        
        Document doc = knowledgeBaseService.addDocument(kbId, request);
        
        if (autoProcess) {
            processDocumentAsync(doc);
        }
        
        return new UploadResult(doc.getDocId(), "uploaded", autoProcess ? "processing" : "pending");
    }
    
    @org.springframework.scheduling.annotation.Async
    protected void processDocumentAsync(Document doc) {
        log.info("Processing document async: docId={}", doc.getDocId());
        knowledgeBaseService.rebuildIndex(doc.getKbId());
    }
    
    public KbManageResult createKnowledgeBase(String userId, String name, String visibility) {
        log.info("Creating knowledge base: userId={}, name={}", userId, name);
        
        KnowledgeBaseCreateRequest request = KnowledgeBaseCreateRequest.builder()
            .name(name)
            .ownerId(userId)
            .visibility(visibility != null ? visibility : KnowledgeBase.VISIBILITY_PRIVATE)
            .build();
        
        KnowledgeBase kb = knowledgeBaseService.create(request);
        
        return new KbManageResult(kb.getKbId(), "created", kb.getName());
    }
    
    public KbManageResult updateKnowledgeBase(String kbId, String name, String visibility) {
        log.info("Updating knowledge base: kbId={}, name={}", kbId, name);
        
        KnowledgeBaseUpdateRequest request = KnowledgeBaseUpdateRequest.builder()
            .name(name)
            .visibility(visibility)
            .build();
        
        KnowledgeBase kb = knowledgeBaseService.update(kbId, request);
        
        return new KbManageResult(kb.getKbId(), "updated", kb.getName());
    }
    
    public KbManageResult archiveKnowledgeBase(String kbId) {
        log.info("Archiving knowledge base: kbId={}", kbId);
        
        KnowledgeBaseUpdateRequest request = KnowledgeBaseUpdateRequest.builder()
            .status("ARCHIVED")
            .build();
        
        KnowledgeBase kb = knowledgeBaseService.update(kbId, request);
        
        return new KbManageResult(kb.getKbId(), "archived", kb.getName());
    }
    
    public List<KnowledgeBase> listKnowledgeBases(String userId) {
        return knowledgeBaseService.listByOwner(userId);
    }
    
    public KnowledgeBase getKnowledgeBase(String kbId) {
        return knowledgeBaseService.get(kbId);
    }
    
    public void deleteKnowledgeBase(String kbId) {
        log.info("Deleting knowledge base: kbId={}", kbId);
        knowledgeBaseService.delete(kbId);
    }
    
    public List<Document> listDocuments(String kbId) {
        log.info("Listing documents: kbId={}", kbId);
        return knowledgeBaseService.listDocuments(kbId);
    }
    
    public Document getDocument(String docId) {
        log.info("Getting document: docId={}", docId);
        return knowledgeBaseService.getDocument(docId);
    }
    
    public void deleteDocument(String docId) {
        log.info("Deleting document: docId={}", docId);
        knowledgeBaseService.deleteDocument(docId);
    }
    
    public void reprocessDocument(String docId) {
        log.info("Reprocessing document: docId={}", docId);
        Document doc = knowledgeBaseService.getDocument(docId);
        if (doc != null) {
            processDocumentAsync(doc);
        }
    }
    
    public void submitFeedback(String userId, String queryId, String query, String answer, 
                                boolean helpful, String comment) {
        log.info("Submitting feedback: userId={}, queryId={}, helpful={}", userId, queryId, helpful);
        
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(UUID.randomUUID().toString());
        feedback.setUserId(userId);
        feedback.setQueryId(queryId);
        feedback.setQuery(query);
        feedback.setAnswer(answer);
        feedback.setHelpful(helpful);
        feedback.setComment(comment);
        feedback.setCreatedAt(new Date());
        
        feedbackMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(feedback);
    }
    
    public List<QueryHistory> getQueryHistory(String userId, int limit) {
        log.info("Getting query history: userId={}, limit={}", userId, limit);
        
        List<QueryHistory> history = queryHistoryMap.getOrDefault(userId, new ArrayList<>());
        
        if (history.size() > limit) {
            return history.subList(0, limit);
        }
        
        return history;
    }
    
    public void recordQueryHistory(String userId, String kbId, String query, String answer, 
                                    List<SourceReference> sources, float confidence) {
        QueryHistory history = new QueryHistory();
        history.setHistoryId(UUID.randomUUID().toString());
        history.setUserId(userId);
        history.setKbId(kbId);
        history.setQuery(query);
        history.setAnswer(answer);
        history.setSources(sources);
        history.setConfidence(confidence);
        history.setCreatedAt(new Date());
        
        queryHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(0, history);
    }
    
    public static class QueryResult {
        private String answer;
        private List<SourceReference> sources;
        private float confidence;
        
        public QueryResult(String answer, List<SourceReference> sources, float confidence) {
            this.answer = answer;
            this.sources = sources;
            this.confidence = confidence;
        }
        
        public String getAnswer() { return answer; }
        public List<SourceReference> getSources() { return sources; }
        public float getConfidence() { return confidence; }
    }
    
    public static class SourceReference {
        private String docId;
        private String title;
        private String content;
        private float score;
        
        public SourceReference(String docId, String title, String content, float score) {
            this.docId = docId;
            this.title = title;
            this.content = content;
            this.score = score;
        }
        
        public String getDocId() { return docId; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public float getScore() { return score; }
    }
    
    public static class UploadResult {
        private String docId;
        private String status;
        private String processStatus;
        
        public UploadResult(String docId, String status, String processStatus) {
            this.docId = docId;
            this.status = status;
            this.processStatus = processStatus;
        }
        
        public String getDocId() { return docId; }
        public String getStatus() { return status; }
        public String getProcessStatus() { return processStatus; }
    }
    
    public static class KbManageResult {
        private String kbId;
        private String status;
        private String name;
        
        public KbManageResult(String kbId, String status, String name) {
            this.kbId = kbId;
            this.status = status;
            this.name = name;
        }
        
        public String getKbId() { return kbId; }
        public String getStatus() { return status; }
        public String getName() { return name; }
    }
    
    public static class QueryHistory {
        private String historyId;
        private String userId;
        private String kbId;
        private String query;
        private String answer;
        private List<SourceReference> sources;
        private float confidence;
        private Date createdAt;
        
        public String getHistoryId() { return historyId; }
        public void setHistoryId(String historyId) { this.historyId = historyId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public List<SourceReference> getSources() { return sources; }
        public void setSources(List<SourceReference> sources) { this.sources = sources; }
        public float getConfidence() { return confidence; }
        public void setConfidence(float confidence) { this.confidence = confidence; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
    
    public static class Feedback {
        private String feedbackId;
        private String userId;
        private String queryId;
        private String query;
        private String answer;
        private boolean helpful;
        private String comment;
        private Date createdAt;
        
        public String getFeedbackId() { return feedbackId; }
        public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getQueryId() { return queryId; }
        public void setQueryId(String queryId) { this.queryId = queryId; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public boolean isHelpful() { return helpful; }
        public void setHelpful(boolean helpful) { this.helpful = helpful; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
}
