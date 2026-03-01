package net.ooder.skill.document.controller;

import net.ooder.skill.document.model.ChunkRequest;
import net.ooder.skill.document.model.ContentRequest;
import net.ooder.skill.document.model.Document;
import net.ooder.skill.document.model.ProcessingResult;
import net.ooder.skill.document.model.ResultModel;
import net.ooder.skill.document.service.DocumentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/document")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class DocumentController {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private DocumentProcessor documentProcessor;

    @PostMapping("/process")
    public ResultModel<ProcessingResult> processDocument(@RequestBody Document document) {
        log.info("Process document request: id={}", document.getId());
        
        try {
            ProcessingResult processingResult = documentProcessor.process(document);
            return ResultModel.success(processingResult.isSuccess() ? "Success" : "Processing failed", processingResult);
            
        } catch (Exception e) {
            log.error("Failed to process document", e);
            return ResultModel.error("Failed to process document: " + e.getMessage());
        }
    }
    
    @PostMapping("/chunk")
    public ResultModel<Map<String, Object>> chunkText(@RequestBody ChunkRequest request) {
        log.info("Chunk text request");
        
        try {
            DocumentProcessor.ChunkConfig config = new DocumentProcessor.ChunkConfig();
            config.setChunkSize(request.getChunkSize());
            config.setChunkOverlap(request.getChunkOverlap());
            
            List<?> chunks = documentProcessor.chunk(request.getContent(), config);
            
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("chunkSize", request.getChunkSize());
            configMap.put("chunkOverlap", request.getChunkOverlap());
            
            Map<String, Object> data = new HashMap<>();
            data.put("chunks", chunks);
            data.put("totalChunks", chunks.size());
            data.put("config", configMap);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to chunk text", e);
            return ResultModel.error("Failed to chunk text: " + e.getMessage());
        }
    }
    
    @PostMapping("/extract-title")
    public ResultModel<Map<String, Object>> extractTitle(@RequestBody ContentRequest request) {
        try {
            String title = documentProcessor.extractTitle(request.getContent());
            
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to extract title", e);
            return ResultModel.error("Failed to extract title: " + e.getMessage());
        }
    }
    
    @PostMapping("/extract-keywords")
    public ResultModel<Map<String, Object>> extractKeywords(@RequestBody ContentRequest request) {
        try {
            List<String> keywords = documentProcessor.extractKeywords(request.getContent());
            
            Map<String, Object> data = new HashMap<>();
            data.put("keywords", keywords);
            data.put("count", keywords.size());
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to extract keywords", e);
            return ResultModel.error("Failed to extract keywords: " + e.getMessage());
        }
    }
    
    @PostMapping("/tokens/estimate")
    public ResultModel<Map<String, Object>> estimateTokens(@RequestBody ContentRequest request) {
        try {
            String content = request.getContent();
            int tokens = documentProcessor.estimateTokens(content);
            
            Map<String, Object> data = new HashMap<>();
            data.put("tokens", tokens);
            data.put("chars", content != null ? content.length() : 0);
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to estimate tokens", e);
            return ResultModel.error("Failed to estimate tokens: " + e.getMessage());
        }
    }
}
