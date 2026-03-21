package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.knowledge.EmbeddingService;
import net.ooder.mvp.skill.scene.knowledge.EmbeddingService.EmbeddingModel;
import net.ooder.mvp.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/embedding")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmbeddingConfigController {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingConfigController.class);
    
    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/models")
    public ResultModel<List<EmbeddingModel>> listModels() {
        log.info("[listModels] request start");
        List<EmbeddingModel> models = embeddingService.listModels();
        return ResultModel.success(models);
    }
    
    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getConfig() {
        log.info("[getConfig] request start");
        Map<String, Object> config = new HashMap<>();
        config.put("currentModel", embeddingService.getCurrentModel());
        EmbeddingModel model = embeddingService.getModel(embeddingService.getCurrentModel());
        if (model != null) {
            config.put("dimensions", model.getDimensions());
            config.put("provider", model.getProvider());
        }
        config.put("defaultChunkSize", 500);
        config.put("defaultChunkOverlap", 50);
        return ResultModel.success(config);
    }
    
    @PutMapping("/config")
    public ResultModel<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> request) {
        log.info("[updateConfig] request: {}", request);
        
        if (request.containsKey("currentModel")) {
            String modelId = (String) request.get("currentModel");
            embeddingService.setCurrentModel(modelId);
        }
        
        return getConfig();
    }
    
    @PostMapping("/test")
    public ResultModel<Map<String, Object>> testEmbedding(@RequestBody Map<String, Object> request) {
        String modelId = (String) request.getOrDefault("modelId", embeddingService.getCurrentModel());
        String text = (String) request.get("text");
        
        log.info("[testEmbedding] modelId: {}, text length: {}", modelId, text != null ? text.length() : 0);
        
        if (text == null || text.trim().isEmpty()) {
            return ResultModel.error(400, "Text is required");
        }
        
        Map<String, Object> result = embeddingService.testEmbedding(modelId, text);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            int dimensions = (Integer) result.get("dimensions");
            List<Double> mockVector = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < Math.min(10, dimensions); i++) {
                mockVector.add(random.nextDouble() * 2 - 1);
            }
            result.put("sampleVector", mockVector);
            result.put("textLength", text.length());
        }
        
        return ResultModel.success(result);
    }
    
    @GetMapping("/models/{modelId}")
    public ResultModel<EmbeddingModel> getModel(@PathVariable String modelId) {
        log.info("[getModel] modelId: {}", modelId);
        EmbeddingModel model = embeddingService.getModel(modelId);
        if (model == null) {
            return ResultModel.notFound("Model not found: " + modelId);
        }
        return ResultModel.success(model);
    }
    
    @PostMapping("/models")
    public ResultModel<EmbeddingModel> registerModel(@RequestBody Map<String, Object> request) {
        String modelId = (String) request.get("modelId");
        String displayName = (String) request.get("displayName");
        int dimensions = request.containsKey("dimensions") ? ((Number) request.get("dimensions")).intValue() : 1536;
        String provider = (String) request.getOrDefault("provider", "custom");
        boolean configured = Boolean.TRUE.equals(request.get("configured"));
        
        log.info("[registerModel] modelId: {}, displayName: {}", modelId, displayName);
        
        embeddingService.registerModel(modelId, displayName, dimensions, provider, configured);
        EmbeddingModel model = embeddingService.getModel(modelId);
        return ResultModel.success(model);
    }
    
    @PutMapping("/models/{modelId}/configure")
    public ResultModel<Boolean> configureModel(@PathVariable String modelId, @RequestBody Map<String, Object> request) {
        boolean configured = Boolean.TRUE.equals(request.get("configured"));
        log.info("[configureModel] modelId: {}, configured: {}", modelId, configured);
        embeddingService.configureModel(modelId, configured);
        return ResultModel.success(true);
    }
}
