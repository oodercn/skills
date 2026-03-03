package net.ooder.skill.test.controller;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skill.test.service.DocumentIndexService;
import net.ooder.skill.test.service.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/kb")
public class KnowledgeBaseController {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);
    
    @Autowired(required = false)
    private SkillService skillService;
    
    @Autowired(required = false)
    private CapabilityRegistry capabilityRegistry;
    
    @Autowired
    private DocumentIndexService documentIndexService;
    
    @Autowired
    private LLMService llmService;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listKnowledgeBases() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        
        List<Map<String, Object>> kbs = new ArrayList<>();
        
        List<Map<String, Object>> storedKbs = documentIndexService.listKnowledgeBases();
        for (Map<String, Object> kb : storedKbs) {
            Map<String, Object> kbCopy = new HashMap<>(kb);
            String kbId = (String) kb.get("id");
            int docCount = documentIndexService.getDocumentCount(kbId);
            kbCopy.put("docCount", docCount);
            kbs.add(kbCopy);
        }
        
        if (skillService != null) {
            try {
                log.info("[listKnowledgeBases] Using SkillService to list installed skills");
                CompletableFuture<List<InstalledSkill>> future = skillService.listInstalledSkills();
                List<InstalledSkill> skills = future.get();
                
                Set<String> existingIds = storedKbs.stream()
                    .map(kb -> (String) kb.get("id"))
                    .collect(java.util.stream.Collectors.toSet());
                
                for (InstalledSkill skill : skills) {
                    if (!existingIds.contains(skill.getSkillId())) {
                        Map<String, Object> kb = new HashMap<>();
                        kb.put("id", skill.getSkillId());
                        kb.put("name", skill.getName());
                        kb.put("version", skill.getVersion());
                        kb.put("status", skill.getStatus());
                        kb.put("installPath", skill.getInstallPath());
                        kb.put("invokeCount", skill.getInvokeCount());
                        kb.put("docCount", documentIndexService.getDocumentCount(skill.getSkillId()));
                        kbs.add(kb);
                    }
                }
                log.info("[listKnowledgeBases] Found {} installed skills", skills.size());
            } catch (Exception e) {
                log.error("[listKnowledgeBases] Failed to list skills", e);
                result.put("error", e.getMessage());
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("kbs", kbs);
        data.put("skillServiceReady", skillService != null);
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createKnowledgeBase(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String description = (String) request.get("description");
        
        log.info("[createKnowledgeBase] Name: {}, Description: {}", name, description);
        
        String id = documentIndexService.createKnowledgeBase(name, description);
        
        Map<String, Object> kb = documentIndexService.getKnowledgeBase(id);
        
        log.info("[createKnowledgeBase] Saved knowledge base: {}", id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", kb);
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteKnowledgeBase(@PathVariable String id) {
        log.info("[deleteKnowledgeBase] id: {}", id);
        
        boolean deleted = documentIndexService.deleteKnowledgeBase(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        if (deleted) {
            result.put("message", "知识库已删除");
            result.put("id", id);
        } else {
            result.put("message", "知识库不存在");
            result.put("id", id);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getKnowledgeBase(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> kb = documentIndexService.getKnowledgeBase(id);
        if (kb != null) {
            Map<String, Object> kbCopy = new HashMap<>(kb);
            kbCopy.put("docCount", documentIndexService.getDocumentCount(id));
            result.put("status", "success");
            result.put("data", kbCopy);
        } else {
            result.put("status", "error");
            result.put("message", "知识库不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> listDocuments(@RequestParam(required = false) String kbId) {
        log.info("[listDocuments] kbId: {}", kbId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        
        List<Map<String, Object>> docs = new ArrayList<>();
        
        if (kbId != null) {
            List<DocumentIndexService.DocumentMeta> metas = documentIndexService.getDocumentsByKb(kbId);
            for (DocumentIndexService.DocumentMeta meta : metas) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("id", meta.getDocId());
                doc.put("name", meta.getFileName());
                doc.put("size", formatSize(meta.getFileSize()));
                doc.put("status", meta.getStatus());
                doc.put("uploadTime", meta.getCreateTime().toString());
                docs.add(doc);
            }
        } else {
            List<Map<String, Object>> allKbs = documentIndexService.listKnowledgeBases();
            for (Map<String, Object> kb : allKbs) {
                String kbId2 = (String) kb.get("id");
                List<DocumentIndexService.DocumentMeta> metas = documentIndexService.getDocumentsByKb(kbId2);
                for (DocumentIndexService.DocumentMeta meta : metas) {
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("id", meta.getDocId());
                    doc.put("name", meta.getFileName());
                    doc.put("size", formatSize(meta.getFileSize()));
                    doc.put("status", meta.getStatus());
                    doc.put("uploadTime", meta.getCreateTime().toString());
                    doc.put("kbId", meta.getKbId());
                    docs.add(doc);
                }
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("docs", docs);
        data.put("count", docs.size());
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam String kbId,
            @RequestParam("file") MultipartFile file) {
        
        log.info("[uploadDocument] kbId: {}, fileName: {}", kbId, file.getOriginalFilename());
        
        Map<String, Object> kb = documentIndexService.getKnowledgeBase(kbId);
        if (kb == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "知识库不存在");
            return ResponseEntity.ok(result);
        }
        
        try {
            byte[] content = file.getBytes();
            String docId = documentIndexService.storeDocument(kbId, file.getOriginalFilename(), content);
            
            if (docId != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "success");
                result.put("docId", docId);
                result.put("fileName", file.getOriginalFilename());
                result.put("size", formatSize(content.length));
                result.put("kbId", kbId);
                log.info("[uploadDocument] Document uploaded successfully: {}", docId);
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "error");
                result.put("message", "文档存储失败");
                return ResponseEntity.ok(result);
            }
        } catch (IOException e) {
            log.error("[uploadDocument] Failed to upload", e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @DeleteMapping("/docs/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable String id) {
        log.info("[deleteDocument] id: {}", id);
        
        boolean deleted = documentIndexService.deleteDocument(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", deleted ? "文档已删除" : "文档不存在");
        result.put("id", id);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/docs/{id}/content")
    public ResponseEntity<Map<String, Object>> getDocumentContent(@PathVariable String id) {
        log.info("[getDocumentContent] id: {}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        DocumentIndexService.DocumentMeta meta = documentIndexService.getDocumentMeta(id);
        if (meta == null) {
            result.put("status", "error");
            result.put("message", "文档不存在");
            return ResponseEntity.ok(result);
        }
        
        try {
            java.nio.file.Path docPath = java.nio.file.Paths.get(meta.getFilePath());
            if (!java.nio.file.Files.exists(docPath)) {
                result.put("status", "error");
                result.put("message", "文件不存在");
                return ResponseEntity.ok(result);
            }
            
            String content = new String(java.nio.file.Files.readAllBytes(docPath), java.nio.charset.StandardCharsets.UTF_8);
            
            result.put("status", "success");
            result.put("data", new HashMap<String, Object>() {{
                put("id", meta.getDocId());
                put("name", meta.getFileName());
                put("content", content);
                put("size", meta.getFileSize());
                put("kbId", meta.getKbId());
            }});
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[getDocumentContent] Failed to read document", e);
            result.put("status", "error");
            result.put("message", "读取文档失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        Integer topK = (Integer) request.getOrDefault("topK", 10);
        Double threshold = (Double) request.getOrDefault("threshold", 0.7);
        
        log.info("[search] Query: {}, topK: {}, threshold: {}", query, topK, threshold);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        
        List<Map<String, Object>> searchResults = new ArrayList<>();
        
        if (query != null && !query.isEmpty()) {
            List<DocumentIndexService.SearchResult> results = documentIndexService.search(query, topK, threshold);
            
            for (DocumentIndexService.SearchResult sr : results) {
                DocumentIndexService.DocumentMeta meta = documentIndexService.getDocumentMeta(sr.getDocId());
                if (meta != null) {
                    Map<String, Object> r = new HashMap<>();
                    r.put("docId", sr.getDocId());
                    r.put("title", meta.getFileName());
                    r.put("score", sr.getScore());
                    r.put("source", meta.getFileName());
                    r.put("kbId", meta.getKbId());
                    searchResults.add(r);
                }
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("results", searchResults);
        data.put("total", searchResults.size());
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/qa")
    public ResponseEntity<Map<String, Object>> questionAnswer(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        String kbId = (String) request.get("kbId");
        Integer topK = (Integer) request.getOrDefault("topK", 10);
        Double threshold = (Double) request.getOrDefault("threshold", 0.7);
        
        log.info("[qa] Question: {}, kbId: {}", question, kbId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        
        List<DocumentIndexService.SearchResult> searchResults = documentIndexService.search(question, topK, threshold);
        
        List<Map<String, Object>> sources = new ArrayList<>();
        for (DocumentIndexService.SearchResult sr : searchResults) {
            DocumentIndexService.DocumentMeta meta = documentIndexService.getDocumentMeta(sr.getDocId());
            if (meta != null) {
                Map<String, Object> s = new HashMap<>();
                s.put("docId", sr.getDocId());
                s.put("title", meta.getFileName());
                s.put("score", sr.getScore());
                sources.add(s);
            }
        }
        
        Map<String, Object> llmStatus = llmService.checkDependencies();
        boolean llmReady = (Boolean) llmStatus.get("ready");
        
        String answer;
        if (llmReady) {
            answer = llmService.generateAnswer(question, sources);
        } else {
            answer = "根据知识库检索结果，关于\"" + question + "\"的相关信息如下：\n\n";
            if (!sources.isEmpty()) {
                answer += "找到 " + sources.size() + " 个相关文档：\n";
                for (Map<String, Object> s : sources) {
                    answer += "- " + s.get("title") + " (相关度: " + String.format("%.0f%%", ((Double)s.get("score")) * 100) + ")\n";
                }
            } else {
                answer += "未找到相关文档。";
            }
            answer += "\n\n注意: LLM服务未配置，无法生成智能回答。";
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("answer", answer);
        data.put("sources", sources);
        data.put("llmReady", llmReady);
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/llm/status")
    public ResponseEntity<Map<String, Object>> getLLMStatus() {
        return ResponseEntity.ok(llmService.checkDependencies());
    }
    
    @GetMapping("/discover")
    public ResponseEntity<Map<String, Object>> discoverSkills() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        
        List<Map<String, Object>> skills = new ArrayList<>();
        
        if (skillService != null) {
            try {
                log.info("[discoverSkills] Discovering all skills");
                CompletableFuture<List<SkillPackage>> future = skillService.discoverAllSkills();
                List<SkillPackage> packages = future.get();
                
                for (SkillPackage pkg : packages) {
                    Map<String, Object> skill = new HashMap<>();
                    skill.put("skillId", pkg.getSkillId());
                    skill.put("name", pkg.getName());
                    skill.put("description", pkg.getDescription());
                    skill.put("version", pkg.getVersion());
                    skill.put("category", pkg.getCategory());
                    skill.put("sceneId", pkg.getSceneId());
                    skills.add(skill);
                }
                log.info("[discoverSkills] Discovered {} skills", packages.size());
            } catch (Exception e) {
                log.error("[discoverSkills] Failed to discover skills", e);
                result.put("error", e.getMessage());
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("skills", skills);
        data.put("count", skills.size());
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/install/{skillId}")
    public ResponseEntity<Map<String, Object>> installSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        
        if (skillService != null) {
            try {
                log.info("[installSkill] Installing skill: {}", skillId);
                CompletableFuture<net.ooder.skills.api.InstallResult> future = skillService.installSkill(skillId);
                net.ooder.skills.api.InstallResult installResult = future.get();
                
                result.put("status", installResult.isSuccess() ? "success" : "failed");
                result.put("skillId", skillId);
                result.put("error", installResult.getError());
                result.put("installPath", installResult.getInstallPath());
                
                log.info("[installSkill] Install result: {}", installResult.isSuccess());
            } catch (Exception e) {
                log.error("[installSkill] Install failed", e);
                result.put("status", "error");
                result.put("error", e.getMessage());
            }
        } else {
            result.put("status", "error");
            result.put("error", "SkillService not available");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/uninstall/{skillId}")
    public ResponseEntity<Map<String, Object>> uninstallSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        
        if (skillService != null) {
            try {
                log.info("[uninstallSkill] Uninstalling skill: {}", skillId);
                CompletableFuture<net.ooder.skills.api.UninstallResult> future = skillService.uninstallSkill(skillId);
                net.ooder.skills.api.UninstallResult uninstallResult = future.get();
                
                result.put("status", uninstallResult.isSuccess() ? "success" : "failed");
                result.put("skillId", skillId);
                result.put("error", uninstallResult.getError());
                
                log.info("[uninstallSkill] Uninstall result: {}", uninstallResult.isSuccess());
            } catch (Exception e) {
                log.error("[uninstallSkill] Uninstall failed", e);
                result.put("status", "error");
                result.put("error", e.getMessage());
            }
        } else {
            result.put("status", "error");
            result.put("error", "SkillService not available");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sdk/status")
    public ResponseEntity<Map<String, Object>> getSdkStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("skillServiceReady", skillService != null);
        status.put("capabilityRegistryReady", capabilityRegistry != null);
        status.put("knowledgeBaseCount", documentIndexService.listKnowledgeBases().size());
        status.put("documentIndexReady", true);
        status.put("llmStatus", llmService.checkDependencies());
        
        if (skillService != null) {
            status.put("skillRootPath", skillService.getSkillRootPath());
        }
        
        return ResponseEntity.ok(status);
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
    }
}
