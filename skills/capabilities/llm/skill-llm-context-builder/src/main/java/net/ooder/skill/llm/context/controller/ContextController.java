package net.ooder.skill.llm.context.controller;

import net.ooder.skill.llm.context.model.BuiltContext;
import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.model.ResultModel;
import net.ooder.skill.llm.context.model.TextRequest;
import net.ooder.skill.llm.context.service.ContextBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/llm/context")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class ContextController {
    
    private static final Logger log = LoggerFactory.getLogger(ContextController.class);
    
    @Autowired
    private ContextBuilder contextBuilder;

    @PostMapping("/build")
    public ResultModel<BuiltContext> buildContext(@RequestBody ContextRequest request) {
        log.info("Build context request: userId={}, sceneId={}", request.getUserId(), request.getSceneId());
        
        try {
            BuiltContext context = contextBuilder.build(request);
            return ResultModel.success(context);
            
        } catch (Exception e) {
            log.error("Failed to build context", e);
            return ResultModel.error("Failed to build context: " + e.getMessage());
        }
    }
    
    @PostMapping("/prompt")
    public ResultModel<Map<String, Object>> buildPrompt(@RequestBody ContextRequest request) {
        log.info("Build prompt request: userId={}, sceneId={}", request.getUserId(), request.getSceneId());
        
        try {
            BuiltContext context = contextBuilder.build(request);
            String prompt = context.toPrompt();
            
            Map<String, Object> data = new HashMap<>();
            data.put("prompt", prompt);
            data.put("totalTokens", context.getTotalTokens());
            data.put("truncated", context.isTruncated());
            data.put("sectionCount", context.getSections() != null ? context.getSections().size() : 0);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to build prompt", e);
            return ResultModel.error("Failed to build prompt: " + e.getMessage());
        }
    }
    
    @PostMapping("/tokens/count")
    public ResultModel<Map<String, Object>> countTokens(@RequestBody TextRequest request) {
        try {
            String text = request.getText();
            int tokens = contextBuilder.countTokens(text);
            
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            data.put("tokens", tokens);
            data.put("chars", text != null ? text.length() : 0);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to count tokens", e);
            return ResultModel.error("Failed to count tokens: " + e.getMessage());
        }
    }
    
    @PostMapping("/truncate")
    public ResultModel<Map<String, Object>> truncate(@RequestBody TextRequest request) {
        try {
            String text = request.getText();
            int maxTokens = request.getMaxTokens() != null ? request.getMaxTokens() : 4096;
            
            String truncated = contextBuilder.truncateToTokenLimit(text, maxTokens);
            int tokens = contextBuilder.countTokens(truncated);
            
            Map<String, Object> data = new HashMap<>();
            data.put("original", text);
            data.put("truncated", truncated);
            data.put("tokens", tokens);
            data.put("maxTokens", maxTokens);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to truncate", e);
            return ResultModel.error("Failed to truncate: " + e.getMessage());
        }
    }
}
