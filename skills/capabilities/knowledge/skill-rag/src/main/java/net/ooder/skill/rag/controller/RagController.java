package net.ooder.skill.rag.controller;

import net.ooder.skill.rag.model.CustomPromptRequest;
import net.ooder.skill.rag.model.PromptRequest;
import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RagResult;
import net.ooder.skill.rag.model.RegisterKbRequest;
import net.ooder.skill.rag.model.ResultModel;
import net.ooder.skill.rag.service.RagEngine;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {
    
    private final RagEngine ragEngine;
    
    public RagController(RagEngine ragEngine) {
        this.ragEngine = ragEngine;
    }
    
    @PostMapping("/retrieve")
    public ResultModel<RagResult> retrieve(@RequestBody RagContext context) {
        return ResultModel.success(ragEngine.retrieve(context));
    }
    
    @PostMapping("/prompt")
    public ResultModel<Map<String, Object>> buildPrompt(@RequestBody PromptRequest request) {
        RagContext context = new RagContext();
        context.setQuery(request.getQuery());
        context.setKbIds(request.getKbIds());
        context.setTopK(request.getTopK() != null ? request.getTopK() : 5);
        context.setScoreThreshold(request.getThreshold() != null ? request.getThreshold() : 0.3);
        context.setStrategy(request.getStrategy() != null ? request.getStrategy() : "HYBRID");
        
        RagResult result = ragEngine.retrieve(context);
        String prompt = ragEngine.buildPrompt(context, result);
        
        Map<String, Object> data = new HashMap<>();
        data.put("prompt", prompt);
        data.put("context", result.getCombinedContext());
        data.put("documentCount", result.getTotalRetrieved());
        
        return ResultModel.success(data);
    }
    
    @PostMapping("/prompt/custom")
    public ResultModel<Map<String, String>> buildCustomPrompt(@RequestBody CustomPromptRequest request) {
        String prompt = ragEngine.buildPromptWithContext(
                request.getQuery(), 
                request.getContext(), 
                request.getSystemPrompt());
        
        Map<String, String> result = new HashMap<>();
        result.put("prompt", prompt);
        return ResultModel.success(result);
    }
    
    @PostMapping("/kb/{kbId}/register")
    public ResultModel<Boolean> registerKb(
            @PathVariable String kbId,
            @RequestBody RegisterKbRequest request) {
        ragEngine.registerKnowledgeBase(kbId, request.getEndpoint());
        return ResultModel.success("Knowledge base registered", true);
    }
    
    @DeleteMapping("/kb/{kbId}")
    public ResultModel<Boolean> unregisterKb(@PathVariable String kbId) {
        ragEngine.unregisterKnowledgeBase(kbId);
        return ResultModel.success("Knowledge base unregistered", true);
    }
    
    @GetMapping("/health")
    public ResultModel<Map<String, String>> health() {
        Map<String, String> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", "skill-rag");
        return ResultModel.success(healthData);
    }
}
