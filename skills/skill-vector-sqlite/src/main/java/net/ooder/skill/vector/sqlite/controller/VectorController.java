package net.ooder.skill.vector.sqlite.controller;

import net.ooder.skill.vector.sqlite.EmbeddingService;
import net.ooder.skill.vector.sqlite.VectorStore;
import net.ooder.skill.vector.sqlite.model.AddVectorRequest;
import net.ooder.skill.vector.sqlite.model.VectorSearchRequest;
import net.ooder.skill.vector.sqlite.model.EmbedRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/vectors")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class VectorController {
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> addVector(@RequestBody AddVectorRequest request) {
        String id = request.getId() != null ? request.getId() : UUID.randomUUID().toString();
        String text = request.getText();
        
        Map<String, Object> metadata = request.getMetadata();
        
        double[] embedding;
        if (request.getEmbedding() != null) {
            List<Double> embeddingList = request.getEmbedding();
            embedding = new double[embeddingList.size()];
            for (int i = 0; i < embeddingList.size(); i++) {
                embedding[i] = embeddingList.get(i).doubleValue();
            }
        } else if (text != null) {
            embedding = embeddingService.embed(text);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Either 'embedding' or 'text' is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        vectorStore.addVector(id, embedding, metadata != null ? metadata : new HashMap<String, Object>());
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "created");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> addVectors(@RequestBody List<AddVectorRequest> requests) {
        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        
        for (AddVectorRequest request : requests) {
            String id = request.getId() != null ? request.getId() : UUID.randomUUID().toString();
            String text = request.getText();
            
            Map<String, Object> metadata = request.getMetadata();
            
            double[] embedding;
            if (request.getEmbedding() != null) {
                List<Double> embeddingList = request.getEmbedding();
                embedding = new double[embeddingList.size()];
                for (int i = 0; i < embeddingList.size(); i++) {
                    embedding[i] = embeddingList.get(i).doubleValue();
                }
            } else if (text != null) {
                embedding = embeddingService.embed(text);
            } else {
                continue;
            }
            
            entries.add(new VectorStore.VectorEntry(id, embedding, metadata != null ? metadata : new HashMap<String, Object>()));
        }
        
        vectorStore.addVectors(entries);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", entries.size());
        response.put("status", "created");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVector(@PathVariable String id) {
        VectorStore.VectorEntry entry = vectorStore.getVector(id);
        
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", entry.getId());
        result.put("embedding", entry.getEmbedding());
        result.put("metadata", entry.getMetadata());
        result.put("createdAt", entry.getCreatedAt());
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVector(@PathVariable String id) {
        vectorStore.deleteVector(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "deleted");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestBody VectorSearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 10;
        
        List<VectorStore.SearchResult> results;
        
        if (request.getEmbedding() != null) {
            List<Double> embeddingList = request.getEmbedding();
            double[] queryEmbedding = new double[embeddingList.size()];
            for (int i = 0; i < embeddingList.size(); i++) {
                queryEmbedding[i] = embeddingList.get(i).doubleValue();
            }
            
            Map<String, Object> filter = request.getFilter();
            
            if (filter != null && !filter.isEmpty()) {
                results = vectorStore.searchSimilarWithFilter(queryEmbedding, topK, filter);
            } else {
                results = vectorStore.searchSimilar(queryEmbedding, topK);
            }
        } else if (request.getText() != null) {
            String text = request.getText();
            double[] queryEmbedding = embeddingService.embed(text);
            
            Map<String, Object> filter = request.getFilter();
            
            if (filter != null && !filter.isEmpty()) {
                results = vectorStore.searchSimilarWithFilter(queryEmbedding, topK, filter);
            } else {
                results = vectorStore.searchSimilar(queryEmbedding, topK);
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (VectorStore.SearchResult result : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", result.getId());
            item.put("score", result.getScore());
            item.put("metadata", result.getMetadata());
            response.add(item);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", vectorStore.getCount());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clear() {
        vectorStore.clear();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "cleared");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/embed")
    public ResponseEntity<Map<String, Object>> embed(@RequestBody EmbedRequest request) {
        String text = request.getText();
        
        if (text == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "text is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        double[] embedding = embeddingService.embed(text);
        
        List<Double> embeddingList = new ArrayList<>();
        for (double v : embedding) {
            embeddingList.add(v);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("embedding", embeddingList);
        response.put("dimension", embedding.length);
        response.put("model", embeddingService.getModelName());
        return ResponseEntity.ok(response);
    }
}
