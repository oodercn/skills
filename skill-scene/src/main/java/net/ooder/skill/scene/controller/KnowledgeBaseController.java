package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.knowledge.KnowledgeBaseDTO;
import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/knowledge-bases")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class KnowledgeBaseController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);
    
    private Map<String, KnowledgeBaseDTO> knowledgeBases = new HashMap<String, KnowledgeBaseDTO>();
    
    public KnowledgeBaseController() {
        initMockData();
    }
    
    private void initMockData() {
        KnowledgeBaseDTO kb1 = new KnowledgeBaseDTO();
        kb1.setKbId("kb-001");
        kb1.setName("公司制度知识库");
        kb1.setDescription("公司规章制度、流程规范");
        kb1.setOwnerId("user-001");
        kb1.setVisibility("public");
        kb1.setEmbeddingModel("text-embedding-ada-002");
        kb1.setChunkSize(500);
        kb1.setChunkOverlap(50);
        kb1.setDocumentCount(128);
        kb1.setCreateTime(System.currentTimeMillis() - 86400000L * 30);
        kb1.setUpdateTime(System.currentTimeMillis() - 86400000L);
        kb1.setTags(Arrays.asList("制度", "流程"));
        
        KnowledgeBaseDTO.IndexStatusDTO indexStatus1 = new KnowledgeBaseDTO.IndexStatusDTO();
        indexStatus1.setStatus("completed");
        indexStatus1.setProgress(100);
        indexStatus1.setLastIndexTime(System.currentTimeMillis() - 3600000L);
        kb1.setIndexStatus(indexStatus1);
        
        KnowledgeBaseDTO.KnowledgeLayerConfig layerConfig1 = new KnowledgeBaseDTO.KnowledgeLayerConfig();
        layerConfig1.setLayer("GENERAL");
        layerConfig1.setPriority(0);
        layerConfig1.setEnabled(true);
        kb1.setLayerConfig(layerConfig1);
        
        knowledgeBases.put(kb1.getKbId(), kb1);
        
        KnowledgeBaseDTO kb2 = new KnowledgeBaseDTO();
        kb2.setKbId("kb-002");
        kb2.setName("HR专业知识库");
        kb2.setDescription("人力资源相关政策文档");
        kb2.setOwnerId("user-001");
        kb2.setVisibility("team");
        kb2.setEmbeddingModel("text-embedding-ada-002");
        kb2.setChunkSize(500);
        kb2.setChunkOverlap(50);
        kb2.setDocumentCount(56);
        kb2.setCreateTime(System.currentTimeMillis() - 86400000L * 20);
        kb2.setUpdateTime(System.currentTimeMillis() - 86400000L * 2);
        kb2.setTags(Arrays.asList("HR", "人力"));
        
        KnowledgeBaseDTO.IndexStatusDTO indexStatus2 = new KnowledgeBaseDTO.IndexStatusDTO();
        indexStatus2.setStatus("completed");
        indexStatus2.setProgress(100);
        indexStatus2.setLastIndexTime(System.currentTimeMillis() - 7200000L);
        kb2.setIndexStatus(indexStatus2);
        
        KnowledgeBaseDTO.KnowledgeLayerConfig layerConfig2 = new KnowledgeBaseDTO.KnowledgeLayerConfig();
        layerConfig2.setLayer("PROFESSIONAL");
        layerConfig2.setPriority(1);
        layerConfig2.setEnabled(true);
        kb2.setLayerConfig(layerConfig2);
        
        knowledgeBases.put(kb2.getKbId(), kb2);
        
        KnowledgeBaseDTO kb3 = new KnowledgeBaseDTO();
        kb3.setKbId("kb-003");
        kb3.setName("招聘场景知识库");
        kb3.setDescription("招聘流程相关文档");
        kb3.setOwnerId("user-001");
        kb3.setVisibility("private");
        kb3.setEmbeddingModel("text-embedding-ada-002");
        kb3.setChunkSize(500);
        kb3.setChunkOverlap(50);
        kb3.setDocumentCount(23);
        kb3.setCreateTime(System.currentTimeMillis() - 86400000L * 10);
        kb3.setUpdateTime(System.currentTimeMillis() - 86400000L * 3);
        kb3.setTags(Arrays.asList("招聘"));
        
        KnowledgeBaseDTO.IndexStatusDTO indexStatus3 = new KnowledgeBaseDTO.IndexStatusDTO();
        indexStatus3.setStatus("indexing");
        indexStatus3.setProgress(65);
        indexStatus3.setLastIndexTime(System.currentTimeMillis());
        kb3.setIndexStatus(indexStatus3);
        
        KnowledgeBaseDTO.KnowledgeLayerConfig layerConfig3 = new KnowledgeBaseDTO.KnowledgeLayerConfig();
        layerConfig3.setLayer("SCENE");
        layerConfig3.setPriority(2);
        layerConfig3.setEnabled(true);
        kb3.setLayerConfig(layerConfig3);
        
        knowledgeBases.put(kb3.getKbId(), kb3);
    }

    @GetMapping
    public ResultModel<List<KnowledgeBaseDTO>> listKnowledgeBases() {
        log.info("[listKnowledgeBases] request start");
        return ResultModel.success(new ArrayList<KnowledgeBaseDTO>(knowledgeBases.values()));
    }
    
    @GetMapping("/{kbId}")
    public ResultModel<KnowledgeBaseDTO> getKnowledgeBase(@PathVariable String kbId) {
        log.info("[getKnowledgeBase] kbId: {}", kbId);
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.notFound("Knowledge base not found: " + kbId);
        }
        return ResultModel.success(kb);
    }
    
    @PostMapping
    public ResultModel<KnowledgeBaseDTO> createKnowledgeBase(@RequestBody KnowledgeBaseDTO request) {
        log.info("[createKnowledgeBase] name: {}", request.getName());
        
        String kbId = "kb-" + System.currentTimeMillis();
        request.setKbId(kbId);
        request.setCreateTime(System.currentTimeMillis());
        request.setUpdateTime(System.currentTimeMillis());
        request.setDocumentCount(0);
        
        KnowledgeBaseDTO.IndexStatusDTO indexStatus = new KnowledgeBaseDTO.IndexStatusDTO();
        indexStatus.setStatus("pending");
        indexStatus.setProgress(0);
        request.setIndexStatus(indexStatus);
        
        knowledgeBases.put(kbId, request);
        
        return ResultModel.success(request);
    }
    
    @PutMapping("/{kbId}")
    public ResultModel<KnowledgeBaseDTO> updateKnowledgeBase(@PathVariable String kbId, @RequestBody KnowledgeBaseDTO request) {
        log.info("[updateKnowledgeBase] kbId: {}", kbId);
        
        KnowledgeBaseDTO existing = knowledgeBases.get(kbId);
        if (existing == null) {
            return ResultModel.notFound("Knowledge base not found: " + kbId);
        }
        
        request.setKbId(kbId);
        request.setUpdateTime(System.currentTimeMillis());
        request.setCreateTime(existing.getCreateTime());
        knowledgeBases.put(kbId, request);
        
        return ResultModel.success(request);
    }
    
    @DeleteMapping("/{kbId}")
    public ResultModel<Boolean> deleteKnowledgeBase(@PathVariable String kbId) {
        log.info("[deleteKnowledgeBase] kbId: {}", kbId);
        
        KnowledgeBaseDTO removed = knowledgeBases.remove(kbId);
        if (removed == null) {
            return ResultModel.notFound("Knowledge base not found: " + kbId);
        }
        
        return ResultModel.success(true);
    }
    
    @PostMapping("/{kbId}/rebuild-index")
    public ResultModel<Boolean> rebuildIndex(@PathVariable String kbId) {
        log.info("[rebuildIndex] kbId: {}", kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.notFound("Knowledge base not found: " + kbId);
        }
        
        KnowledgeBaseDTO.IndexStatusDTO indexStatus = kb.getIndexStatus();
        if (indexStatus == null) {
            indexStatus = new KnowledgeBaseDTO.IndexStatusDTO();
            kb.setIndexStatus(indexStatus);
        }
        indexStatus.setStatus("indexing");
        indexStatus.setProgress(0);
        
        return ResultModel.success(true);
    }
    
    @GetMapping("/layer/{layer}")
    public ResultModel<List<KnowledgeBaseDTO>> listByLayer(@PathVariable String layer) {
        log.info("[listByLayer] layer: {}", layer);
        
        List<KnowledgeBaseDTO> result = new ArrayList<KnowledgeBaseDTO>();
        for (KnowledgeBaseDTO kb : knowledgeBases.values()) {
            if (kb.getLayerConfig() != null && layer.equals(kb.getLayerConfig().getLayer())) {
                result.add(kb);
            }
        }
        
        return ResultModel.success(result);
    }
}
