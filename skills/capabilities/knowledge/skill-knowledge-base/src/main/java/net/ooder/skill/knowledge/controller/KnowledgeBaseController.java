package net.ooder.skill.knowledge.controller;

import net.ooder.skill.knowledge.model.KnowledgeBase;
import net.ooder.skill.knowledge.model.KbDocument;
import net.ooder.skill.knowledge.model.MultiSearchRequest;
import net.ooder.skill.knowledge.model.ResultModel;
import net.ooder.skill.knowledge.model.SearchRequest;
import net.ooder.skill.knowledge.model.SearchResult;
import net.ooder.skill.knowledge.model.StatusRequest;
import net.ooder.skill.knowledge.service.KnowledgeBaseService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kb")
public class KnowledgeBaseController {
    
    private final KnowledgeBaseService kbService;
    
    public KnowledgeBaseController(KnowledgeBaseService kbService) {
        this.kbService = kbService;
    }
    
    @PostMapping
    public ResultModel<KnowledgeBase> create(@RequestBody KnowledgeBase kb) {
        return ResultModel.success(kbService.create(kb));
    }
    
    @GetMapping("/{id}")
    public ResultModel<KnowledgeBase> getById(@PathVariable String id) {
        return kbService.findById(id)
                .map(ResultModel::success)
                .orElse(ResultModel.notFound("Knowledge base not found"));
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResultModel<List<KnowledgeBase>> getByOwner(@PathVariable String ownerId) {
        return ResultModel.success(kbService.findByOwner(ownerId));
    }
    
    @GetMapping("/public")
    public ResultModel<List<KnowledgeBase>> getPublic() {
        return ResultModel.success(kbService.findPublicBases());
    }
    
    @GetMapping("/type/{type}")
    public ResultModel<List<KnowledgeBase>> getByType(@PathVariable String type) {
        return ResultModel.success(kbService.findByType(type));
    }
    
    @PutMapping("/{id}")
    public ResultModel<KnowledgeBase> update(@PathVariable String id, @RequestBody KnowledgeBase kb) {
        kb.setId(id);
        return ResultModel.success(kbService.update(kb));
    }
    
    @DeleteMapping("/{id}")
    public ResultModel<Boolean> delete(@PathVariable String id) {
        kbService.delete(id);
        return ResultModel.success("Deleted successfully", true);
    }
    
    @PostMapping("/{kbId}/documents")
    public ResultModel<KbDocument> addDocument(
            @PathVariable String kbId,
            @RequestBody KbDocument document) {
        return ResultModel.success(kbService.addDocument(kbId, document));
    }
    
    @GetMapping("/{kbId}/documents")
    public ResultModel<List<KbDocument>> listDocuments(@PathVariable String kbId) {
        return ResultModel.success(kbService.listDocuments(kbId));
    }
    
    @GetMapping("/{kbId}/documents/{docId}")
    public ResultModel<KbDocument> getDocument(
            @PathVariable String kbId,
            @PathVariable String docId) {
        return kbService.findDocument(kbId, docId)
                .map(ResultModel::success)
                .orElse(ResultModel.notFound("Document not found"));
    }
    
    @PutMapping("/{kbId}/documents/{docId}")
    public ResultModel<KbDocument> updateDocument(
            @PathVariable String kbId,
            @PathVariable String docId,
            @RequestBody KbDocument document) {
        document.setId(docId);
        return ResultModel.success(kbService.updateDocument(kbId, document));
    }
    
    @DeleteMapping("/{kbId}/documents/{docId}")
    public ResultModel<Boolean> removeDocument(
            @PathVariable String kbId,
            @PathVariable String docId) {
        kbService.removeDocument(kbId, docId);
        return ResultModel.success("Deleted successfully", true);
    }
    
    @PostMapping("/{kbId}/search")
    public ResultModel<List<SearchResult>> search(
            @PathVariable String kbId,
            @RequestBody SearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 5;
        double threshold = request.getThreshold() != null ? request.getThreshold() : 0.0;
        return ResultModel.success(kbService.search(kbId, request.getQuery(), topK, threshold));
    }
    
    @PostMapping("/search/multi")
    public ResultModel<List<SearchResult>> searchMulti(@RequestBody MultiSearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 5;
        double threshold = request.getThreshold() != null ? request.getThreshold() : 0.0;
        return ResultModel.success(kbService.searchMulti(request.getKbIds(), request.getQuery(), topK, threshold));
    }
    
    @PostMapping("/{kbId}/reindex")
    public ResultModel<Boolean> rebuildIndex(@PathVariable String kbId) {
        kbService.rebuildIndex(kbId);
        return ResultModel.success("Index rebuilt successfully", true);
    }
    
    @PutMapping("/{kbId}/status")
    public ResultModel<KnowledgeBase> updateStatus(
            @PathVariable String kbId,
            @RequestBody StatusRequest request) {
        return ResultModel.success(kbService.updateStatus(kbId, request.getStatus()));
    }
    
    @GetMapping("/{kbId}/stats")
    public ResultModel<Map<String, Object>> getStats(@PathVariable String kbId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("documentCount", kbService.getDocumentCount(kbId));
        stats.put("chunkCount", kbService.getTotalChunkCount(kbId));
        return ResultModel.success(stats);
    }
}
