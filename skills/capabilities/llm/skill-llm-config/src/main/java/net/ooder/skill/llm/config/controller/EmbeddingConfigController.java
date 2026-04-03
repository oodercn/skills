package net.ooder.skill.llm.config.controller;

import net.ooder.skill.llm.config.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/embedding")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmbeddingConfigController {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingConfigController.class);

    private String currentModel = "text-embedding-ada-002";
    private int dimensions = 1536;
    private int defaultChunkSize = 500;
    private int defaultChunkOverlap = 50;

    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getConfig() {
        log.info("[EmbeddingConfigController] Get embedding config");
        
        Map<String, Object> config = new HashMap<>();
        config.put("currentModel", currentModel);
        config.put("dimensions", dimensions);
        config.put("defaultChunkSize", defaultChunkSize);
        config.put("defaultChunkOverlap", defaultChunkOverlap);
        config.put("models", getAvailableModels());
        
        return ResultModel.success(config);
    }

    @PutMapping("/config")
    public ResultModel<Void> updateConfig(@RequestBody Map<String, Object> config) {
        log.info("[EmbeddingConfigController] Update embedding config: {}", config);
        
        if (config.containsKey("currentModel")) {
            currentModel = (String) config.get("currentModel");
            dimensions = getModelDimensions(currentModel);
        }
        if (config.containsKey("defaultChunkSize")) {
            defaultChunkSize = ((Number) config.get("defaultChunkSize")).intValue();
        }
        if (config.containsKey("defaultChunkOverlap")) {
            defaultChunkOverlap = ((Number) config.get("defaultChunkOverlap")).intValue();
        }
        
        return ResultModel.success(null);
    }

    @PostMapping("/test")
    public ResultModel<Map<String, Object>> testEmbedding(@RequestBody Map<String, Object> request) {
        String modelId = (String) request.getOrDefault("modelId", currentModel);
        String text = (String) request.get("text");
        
        log.info("[EmbeddingConfigController] Test embedding: modelId={}, textLength={}", modelId, text != null ? text.length() : 0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("modelId", modelId);
        result.put("dimensions", getModelDimensions(modelId));
        result.put("textLength", text != null ? text.length() : 0);
        
        double[] sampleVector = new double[10];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sampleVector[i] = random.nextDouble() * 2 - 1;
        }
        result.put("sampleVector", sampleVector);
        result.put("elapsed", random.nextInt(200) + 50);
        
        return ResultModel.success(result);
    }

    @GetMapping("/models")
    public ResultModel<List<Map<String, Object>>> getModels() {
        log.info("[EmbeddingConfigController] Get available models");
        return ResultModel.success(getAvailableModels());
    }

    private List<Map<String, Object>> getAvailableModels() {
        List<Map<String, Object>> models = new ArrayList<>();
        
        Map<String, Object> model1 = new HashMap<>();
        model1.put("modelId", "text-embedding-ada-002");
        model1.put("displayName", "OpenAI Ada-002");
        model1.put("dimensions", 1536);
        model1.put("provider", "openai");
        model1.put("configured", true);
        models.add(model1);
        
        Map<String, Object> model2 = new HashMap<>();
        model2.put("modelId", "text-embedding-3-small");
        model2.put("displayName", "OpenAI Embedding 3 Small");
        model2.put("dimensions", 1536);
        model2.put("provider", "openai");
        model2.put("configured", true);
        models.add(model2);
        
        Map<String, Object> model3 = new HashMap<>();
        model3.put("modelId", "text-embedding-3-large");
        model3.put("displayName", "OpenAI Embedding 3 Large");
        model3.put("dimensions", 3072);
        model3.put("provider", "openai");
        model3.put("configured", false);
        models.add(model3);
        
        Map<String, Object> model4 = new HashMap<>();
        model4.put("modelId", "text-embedding-v2");
        model4.put("displayName", "通义千问 Embedding");
        model4.put("dimensions", 1536);
        model4.put("provider", "qianwen");
        model4.put("configured", true);
        models.add(model4);
        
        Map<String, Object> model5 = new HashMap<>();
        model5.put("modelId", "bge-large-zh");
        model5.put("displayName", "BGE Large Chinese");
        model5.put("dimensions", 1024);
        model5.put("provider", "local");
        model5.put("configured", false);
        models.add(model5);
        
        return models;
    }

    private int getModelDimensions(String modelId) {
        switch (modelId) {
            case "text-embedding-3-large":
                return 3072;
            case "bge-large-zh":
                return 1024;
            default:
                return 1536;
        }
    }
}
