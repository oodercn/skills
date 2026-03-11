package net.ooder.skill.vector.sqlite.controller;

import net.ooder.scene.skill.vector.EmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.SearchResult;
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
        
        float[] embedding;
        if (request.getEmbedding() != null) {
            List<Double> embeddingList = request.getEmbedding();
            embedding = new float[embeddingList.size()];
            for (int i = 0; i < embeddingList.size(); i++) {
                embedding[i] = embeddingList.get(i).floatValue();
            }
        } else if (text != null) {
            embedding = embeddingService.embed(text);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Either 'embedding' or 'text' is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        vectorStore.insert(id, embedding, metadata != null ? metadata : new HashMap<String, Object>());
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "created");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> addVectors(@RequestBody List<AddVectorRequest> requests) {
        List<VectorData> entries = new ArrayList<>();
        
        for (AddVectorRequest request : requests) {
            String id = request.getId() != null ? request.getId() : UUID.randomUUID().toString();
            String text = request.getText();
            
            Map<String, Object> metadata = request.getMetadata();
            
            float[] embedding;
            if (request.getEmbedding() != null) {
                List<Double> embeddingList = request.getEmbedding();
                embedding = new float[embeddingList.size()];
                for (int i = 0; i < embeddingList.size(); i++) {
                    embedding[i] = embeddingList.get(i).floatValue();
                }
            } else if (text != null) {
                embedding = embeddingService.embed(text);
            } else {
                continue;
            }
            
            entries.add(new VectorData(id, embedding, metadata != null ? metadata : new HashMap<String, Object>()));
        }
        
        vectorStore.batchInsert(entries);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", entries.size());
        response.put("status", "created");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVector(@PathVariable String id) {
        vectorStore.delete(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "deleted");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestBody VectorSearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 10;
        
        List<SearchResult> results;
        
        if (request.getEmbedding() != null) {
            List<Double> embeddingList = request.getEmbedding();
            float[] queryEmbedding = new float[embeddingList.size()];
            for (int i = 0; i < embeddingList.size(); i++) {
                queryEmbedding[i] = embeddingList.get(i).floatValue();
            }
            
            Map<String, Object> filter = request.getFilter();
            
            results = vectorStore.search(queryEmbedding, topK, filter);
        } else if (request.getText() != null) {
            String text = request.getText();
            float[] queryEmbedding = embeddingService.embed(text);
            
            Map<String, Object> filter = request.getFilter();
            
            results = vectorStore.search(queryEmbedding, topK, filter);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (SearchResult result : results) {
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
        response.put("count", vectorStore.count());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dimension")
    public ResponseEntity<Map<String, Object>> getDimension() {
        Map<String, Object> response = new HashMap<>();
        response.put("dimension", vectorStore.getDimension());
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
        
        float[] embedding = embeddingService.embed(text);
        
        List<Float> embeddingList = new ArrayList<>();
        for (float v : embedding) {
            embeddingList.add(v);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("embedding", embeddingList);
        response.put("dimension", embedding.length);
        response.put("model", embeddingService.getModel());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/embed/batch")
    public ResponseEntity<Map<String, Object>> embedBatch(@RequestBody List<String> texts) {
        List<float[]> embeddings = embeddingService.embedBatch(texts);
        
        List<List<Float>> embeddingLists = new ArrayList<>();
        for (float[] embedding : embeddings) {
            List<Float> list = new ArrayList<>();
            for (float v : embedding) {
                list.add(v);
            }
            embeddingLists.add(list);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("embeddings", embeddingLists);
        response.put("count", embeddings.size());
        response.put("dimension", embeddingService.getDimension());
        response.put("model", embeddingService.getModel());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/similarity")
    public ResponseEntity<Map<String, Object>> similarity(
            @RequestParam String text1,
            @RequestParam String text2) {
        
        float[] embedding1 = embeddingService.embed(text1);
        float[] embedding2 = embeddingService.embed(text2);
        
        float similarity = embeddingService.cosineSimilarity(embedding1, embedding2);
        float distance = embeddingService.euclideanDistance(embedding1, embedding2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("cosineSimilarity", similarity);
        response.put("euclideanDistance", distance);
        return ResponseEntity.ok(response);
    }
}
