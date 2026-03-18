package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scene-groups/{sceneGroupId}/knowledge")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SceneKnowledgeController {

    private static final Logger log = LoggerFactory.getLogger(SceneKnowledgeController.class);
    
    private Map<String, List<KnowledgeBinding>> knowledgeBindings = new HashMap<String, List<KnowledgeBinding>>();
    private Map<String, KnowledgeConfig> knowledgeConfigs = new HashMap<String, KnowledgeConfig>();
    
    public SceneKnowledgeController() {
        initMockData();
    }
    
    private void initMockData() {
        List<KnowledgeBinding> bindings = new ArrayList<KnowledgeBinding>();
        
        KnowledgeBinding kb1 = new KnowledgeBinding();
        kb1.setKbId("kb-001");
        kb1.setKbName("公司制度知识库");
        kb1.setLayer("GENERAL");
        bindings.add(kb1);
        
        KnowledgeBinding kb2 = new KnowledgeBinding();
        kb2.setKbId("kb-002");
        kb2.setKbName("HR专业知识库");
        kb2.setLayer("PROFESSIONAL");
        bindings.add(kb2);
        
        knowledgeBindings.put("sg-1772887335550", bindings);
        
        KnowledgeConfig config = new KnowledgeConfig();
        config.setTopK(5);
        config.setThreshold(0.7);
        config.setCrossLayerSearch(true);
        knowledgeConfigs.put("sg-1772887335550", config);
    }

    @GetMapping
    public ResultModel<List<KnowledgeBinding>> listKnowledgeBindings(@PathVariable String sceneGroupId) {
        log.info("[listKnowledgeBindings] sceneGroupId: {}", sceneGroupId);
        
        List<KnowledgeBinding> bindings = knowledgeBindings.get(sceneGroupId);
        if (bindings == null) {
            bindings = new ArrayList<KnowledgeBinding>();
        }
        
        return ResultModel.success(bindings);
    }
    
    @PostMapping
    public ResultModel<KnowledgeBinding> bindKnowledge(
            @PathVariable String sceneGroupId,
            @RequestBody KnowledgeBindingRequest request) {
        log.info("[bindKnowledge] sceneGroupId: {}, kbId: {}, layer: {}", sceneGroupId, request.getKbId(), request.getLayer());
        
        List<KnowledgeBinding> bindings = knowledgeBindings.get(sceneGroupId);
        if (bindings == null) {
            bindings = new ArrayList<KnowledgeBinding>();
            knowledgeBindings.put(sceneGroupId, bindings);
        }
        
        for (KnowledgeBinding b : bindings) {
            if (b.getKbId().equals(request.getKbId()) && b.getLayer().equals(request.getLayer())) {
                return ResultModel.error(400, "知识库已绑定到此层");
            }
        }
        
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setKbId(request.getKbId());
        binding.setKbName(request.getKbId());
        binding.setLayer(request.getLayer());
        bindings.add(binding);
        
        return ResultModel.success(binding);
    }
    
    @DeleteMapping("/{kbId}")
    public ResultModel<Boolean> unbindKnowledge(
            @PathVariable String sceneGroupId,
            @PathVariable String kbId,
            @RequestParam String layer) {
        log.info("[unbindKnowledge] sceneGroupId: {}, kbId: {}, layer: {}", sceneGroupId, kbId, layer);
        
        List<KnowledgeBinding> bindings = knowledgeBindings.get(sceneGroupId);
        if (bindings == null) {
            return ResultModel.notFound("Knowledge binding not found");
        }
        
        boolean removed = bindings.removeIf(b -> b.getKbId().equals(kbId) && b.getLayer().equals(layer));
        
        return ResultModel.success(removed);
    }
    
    @GetMapping("/config")
    public ResultModel<KnowledgeConfig> getKnowledgeConfig(@PathVariable String sceneGroupId) {
        log.info("[getKnowledgeConfig] sceneGroupId: {}", sceneGroupId);
        
        KnowledgeConfig config = knowledgeConfigs.get(sceneGroupId);
        if (config == null) {
            config = new KnowledgeConfig();
        }
        
        return ResultModel.success(config);
    }
    
    @PutMapping("/config")
    public ResultModel<KnowledgeConfig> updateKnowledgeConfig(
            @PathVariable String sceneGroupId,
            @RequestBody KnowledgeConfig config) {
        log.info("[updateKnowledgeConfig] sceneGroupId: {}", sceneGroupId);
        
        knowledgeConfigs.put(sceneGroupId, config);
        
        return ResultModel.success(config);
    }
    
    public static class KnowledgeBinding {
        private String kbId;
        private String kbName;
        private String layer;
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getKbName() { return kbName; }
        public void setKbName(String kbName) { this.kbName = kbName; }
        public String getLayer() { return layer; }
        public void setLayer(String layer) { this.layer = layer; }
    }
    
    public static class KnowledgeBindingRequest {
        private String kbId;
        private String layer;
        
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getLayer() { return layer; }
        public void setLayer(String layer) { this.layer = layer; }
    }
    
    public static class KnowledgeConfig {
        private int topK = 5;
        private double threshold = 0.7;
        private boolean crossLayerSearch = true;
        
        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }
        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
        public boolean isCrossLayerSearch() { return crossLayerSearch; }
        public void setCrossLayerSearch(boolean crossLayerSearch) { this.crossLayerSearch = crossLayerSearch; }
    }
}
