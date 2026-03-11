package net.ooder.skill.document.assistant.controller;

import net.ooder.skill.document.assistant.dto.DocumentResponse;
import net.ooder.skill.document.assistant.dto.KbManageResponse;
import net.ooder.skill.document.assistant.dto.KnowledgeBaseResponse;
import net.ooder.skill.document.assistant.dto.OperationResponse;
import net.ooder.skill.document.assistant.dto.QueryDocumentRequest;
import net.ooder.skill.document.assistant.dto.QueryDocumentResponse;
import net.ooder.skill.document.assistant.dto.QueryHistoryResponse;
import net.ooder.skill.document.assistant.dto.SubmitFeedbackRequest;
import net.ooder.skill.document.assistant.dto.UploadDocumentResponse;
import net.ooder.skill.document.assistant.service.DocumentAssistantService;
import net.ooder.skill.document.assistant.service.DocumentAssistantService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/document-assistant")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class DocumentAssistantController {
    
    @Autowired
    private DocumentAssistantService documentAssistantService;
    
    @PostMapping("/query")
    public ResponseEntity<QueryDocumentResponse> queryDocument(@RequestBody QueryDocumentRequest request) {
        String kbId = request.getKbId();
        String query = request.getQuery();
        String userId = request.getUserId();
        Integer topK = request.getTopK() != null ? request.getTopK() : 5;
        
        QueryResult result = documentAssistantService.queryDocument(kbId, query, topK);
        
        if (userId != null) {
            documentAssistantService.recordQueryHistory(userId, kbId, query, 
                result.getAnswer(), result.getSources(), result.getConfidence());
        }
        
        QueryDocumentResponse response = new QueryDocumentResponse();
        response.setAnswer(result.getAnswer());
        response.setSources(result.getSources());
        response.setConfidence(result.getConfidence());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<UploadDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("kbId") String kbId,
            @RequestParam(value = "autoProcess", defaultValue = "true") boolean autoProcess) throws IOException {
        
        UploadResult result = documentAssistantService.uploadDocument(userId, kbId, file, autoProcess, null);
        
        UploadDocumentResponse response = new UploadDocumentResponse();
        response.setDocId(result.getDocId());
        response.setStatus(result.getStatus());
        response.setProcessStatus(result.getProcessStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/kb")
    public ResponseEntity<KbManageResponse> createKnowledgeBase(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam(required = false) String visibility) {
        
        KbManageResult result = documentAssistantService.createKnowledgeBase(userId, name, visibility);
        
        KbManageResponse response = new KbManageResponse();
        response.setKbId(result.getKbId());
        response.setStatus(result.getStatus());
        response.setName(result.getName());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/kb")
    public ResponseEntity<List<KnowledgeBaseResponse>> listKnowledgeBases(@RequestParam String userId) {
        List<KnowledgeBase> list = documentAssistantService.listKnowledgeBases(userId);
        List<KnowledgeBaseResponse> responseList = list.stream()
            .map(this::toKnowledgeBaseResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
    
    @GetMapping("/kb/{kbId}")
    public ResponseEntity<KnowledgeBaseResponse> getKnowledgeBase(@PathVariable String kbId) {
        KnowledgeBase kb = documentAssistantService.getKnowledgeBase(kbId);
        
        if (kb == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toKnowledgeBaseResponse(kb));
    }
    
    @PutMapping("/kb/{kbId}")
    public ResponseEntity<KbManageResponse> updateKnowledgeBase(
            @PathVariable String kbId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String visibility) {
        
        KbManageResult result = documentAssistantService.updateKnowledgeBase(kbId, name, visibility);
        
        KbManageResponse response = new KbManageResponse();
        response.setKbId(result.getKbId());
        response.setStatus(result.getStatus());
        response.setName(result.getName());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/kb/{kbId}")
    public ResponseEntity<OperationResponse> deleteKnowledgeBase(@PathVariable String kbId) {
        documentAssistantService.deleteKnowledgeBase(kbId);
        
        OperationResponse response = new OperationResponse();
        response.setStatus("deleted");
        response.setId(kbId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/kb/{kbId}/archive")
    public ResponseEntity<KbManageResponse> archiveKnowledgeBase(@PathVariable String kbId) {
        KbManageResult result = documentAssistantService.archiveKnowledgeBase(kbId);
        
        KbManageResponse response = new KbManageResponse();
        response.setKbId(result.getKbId());
        response.setStatus(result.getStatus());
        response.setName(result.getName());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/kb/{kbId}/documents")
    public ResponseEntity<List<DocumentResponse>> listDocuments(@PathVariable String kbId) {
        List<Document> list = documentAssistantService.listDocuments(kbId);
        List<DocumentResponse> responseList = list.stream()
            .map(this::toDocumentResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
    
    @GetMapping("/documents/{docId}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable String docId) {
        Document doc = documentAssistantService.getDocument(docId);
        
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toDocumentResponse(doc));
    }
    
    @DeleteMapping("/documents/{docId}")
    public ResponseEntity<OperationResponse> deleteDocument(@PathVariable String docId) {
        documentAssistantService.deleteDocument(docId);
        
        OperationResponse response = new OperationResponse();
        response.setStatus("deleted");
        response.setId(docId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/documents/{docId}/reprocess")
    public ResponseEntity<OperationResponse> reprocessDocument(@PathVariable String docId) {
        documentAssistantService.reprocessDocument(docId);
        
        OperationResponse response = new OperationResponse();
        response.setStatus("reprocessing");
        response.setId(docId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<OperationResponse> submitFeedback(@RequestBody SubmitFeedbackRequest request) {
        String userId = request.getUserId();
        String queryId = request.getQueryId();
        String query = request.getQuery();
        String answer = request.getAnswer();
        Boolean helpful = request.getHelpful();
        String comment = request.getComment();
        
        documentAssistantService.submitFeedback(userId, queryId, query, answer, 
            helpful != null ? helpful : false, comment);
        
        OperationResponse response = new OperationResponse();
        response.setStatus("submitted");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<QueryHistoryResponse>> getQueryHistory(
            @RequestParam String userId,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        
        List<QueryHistory> history = documentAssistantService.getQueryHistory(userId, limit);
        List<QueryHistoryResponse> responseList = history.stream()
            .map(this::toQueryHistoryResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseList);
    }
    
    private KnowledgeBaseResponse toKnowledgeBaseResponse(KnowledgeBase kb) {
        KnowledgeBaseResponse response = new KnowledgeBaseResponse();
        response.setKbId(kb.getKbId());
        response.setName(kb.getName());
        response.setOwnerId(kb.getOwnerId());
        response.setVisibility(kb.getVisibility());
        response.setCreatedAt(kb.getCreatedAt());
        return response;
    }
    
    private DocumentResponse toDocumentResponse(Document doc) {
        DocumentResponse response = new DocumentResponse();
        response.setDocId(doc.getDocId());
        response.setTitle(doc.getTitle());
        response.setContent(doc.getContent());
        response.setKbId(doc.getKbId());
        response.setSource(doc.getSource());
        response.setMetadata(doc.getMetadata());
        response.setCreatedAt(doc.getCreatedAt());
        return response;
    }
    
    private QueryHistoryResponse toQueryHistoryResponse(QueryHistory history) {
        QueryHistoryResponse response = new QueryHistoryResponse();
        response.setQueryId(history.getQueryId());
        response.setUserId(history.getUserId());
        response.setKbId(history.getKbId());
        response.setQuery(history.getQuery());
        response.setAnswer(history.getAnswer());
        response.setConfidence(history.getConfidence());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}
