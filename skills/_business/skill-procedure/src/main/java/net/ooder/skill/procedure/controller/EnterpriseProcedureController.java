package net.ooder.skill.procedure.controller;

import net.ooder.skill.procedure.dto.*;
import net.ooder.skill.procedure.model.ResultModel;
import net.ooder.skill.procedure.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/enterprise-procedures")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class EnterpriseProcedureController {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseProcedureController.class);

    private final Map<String, EnterpriseProcedureDTO> procedureStore = new HashMap<>();

    public EnterpriseProcedureController() {
        initSampleData();
    }

    private void initSampleData() {
        EnterpriseProcedureDTO proc1 = new EnterpriseProcedureDTO();
        proc1.setId("proc-001");
        proc1.setName("员工入职流程");
        proc1.setDescription("新员工入职标准流程");
        proc1.setCategory("hr");
        proc1.setStatus("active");
        proc1.setVersion("1.0");
        proc1.setCreateTime(System.currentTimeMillis());
        procedureStore.put("proc-001", proc1);

        EnterpriseProcedureDTO proc2 = new EnterpriseProcedureDTO();
        proc2.setId("proc-002");
        proc2.setName("请假审批流程");
        proc2.setDescription("员工请假审批流程");
        proc2.setCategory("hr");
        proc2.setStatus("active");
        proc2.setVersion("1.0");
        proc2.setCreateTime(System.currentTimeMillis());
        procedureStore.put("proc-002", proc2);
    }

    @GetMapping
    public ResultModel<PageResult<EnterpriseProcedureDTO>> listProcedures(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        log.info("[EnterpriseProcedureController] List procedures - pageNum: {}, pageSize: {}", pageNum, pageSize);
        
        List<EnterpriseProcedureDTO> allProcedures = new ArrayList<>(procedureStore.values());
        
        if (category != null && !category.isEmpty()) {
            allProcedures.removeIf(p -> !category.equals(p.getCategory()));
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            allProcedures.removeIf(p -> {
                String name = p.getName();
                return name == null || !name.toLowerCase().contains(lowerKeyword);
            });
        }
        
        int total = allProcedures.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<EnterpriseProcedureDTO> pageData = start < total ? allProcedures.subList(start, end) : new ArrayList<>();
        
        PageResult<EnterpriseProcedureDTO> result = new PageResult<>();
        result.setData(pageData);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        
        return ResultModel.success(result);
    }

    @GetMapping("/{id}")
    public ResultModel<EnterpriseProcedureDTO> getProcedure(@PathVariable String id) {
        log.info("[EnterpriseProcedureController] Get procedure: {}", id);
        
        EnterpriseProcedureDTO procedure = procedureStore.get(id);
        if (procedure == null) {
            return ResultModel.notFound("Procedure not found: " + id);
        }
        
        return ResultModel.success(procedure);
    }

    @PostMapping
    public ResultModel<EnterpriseProcedureDTO> createProcedure(@RequestBody EnterpriseProcedureDTO procedure) {
        log.info("[EnterpriseProcedureController] Create procedure: {}", procedure.getName());
        
        String id = "proc-" + UUID.randomUUID().toString().substring(0, 8);
        procedure.setId(id);
        procedure.setCreateTime(System.currentTimeMillis());
        procedureStore.put(id, procedure);
        
        return ResultModel.success(procedure);
    }

    @PutMapping("/{id}")
    public ResultModel<EnterpriseProcedureDTO> updateProcedure(@PathVariable String id, @RequestBody EnterpriseProcedureDTO procedure) {
        log.info("[EnterpriseProcedureController] Update procedure: {}", id);
        
        if (!procedureStore.containsKey(id)) {
            return ResultModel.notFound("Procedure not found: " + id);
        }
        
        procedure.setId(id);
        procedure.setUpdateTime(System.currentTimeMillis());
        procedureStore.put(id, procedure);
        
        return ResultModel.success(procedure);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteProcedure(@PathVariable String id) {
        log.info("[EnterpriseProcedureController] Delete procedure: {}", id);
        
        if (!procedureStore.containsKey(id)) {
            return ResultModel.notFound("Procedure not found: " + id);
        }
        
        procedureStore.remove(id);
        return ResultModel.success(true);
    }

    @GetMapping("/categories")
    public ResultModel<List<CategoryDTO>> getCategories() {
        log.info("[EnterpriseProcedureController] Get categories");
        
        List<CategoryDTO> categories = new ArrayList<>();
        
        CategoryDTO cat1 = new CategoryDTO();
        cat1.setId("hr");
        cat1.setName("人力资源");
        categories.add(cat1);
        
        CategoryDTO cat2 = new CategoryDTO();
        cat2.setId("finance");
        cat2.setName("财务");
        categories.add(cat2);
        
        CategoryDTO cat3 = new CategoryDTO();
        cat3.setId("operation");
        cat3.setName("运营");
        categories.add(cat3);
        
        return ResultModel.success(categories);
    }

    @GetMapping("/sources")
    public ResultModel<List<CategoryDTO>> getSources() {
        log.info("[EnterpriseProcedureController] Get sources");
        
        List<CategoryDTO> sources = new ArrayList<>();
        
        CategoryDTO source1 = new CategoryDTO();
        source1.setId("internal");
        source1.setName("内部流程");
        sources.add(source1);
        
        CategoryDTO source2 = new CategoryDTO();
        source2.setId("external");
        source2.setName("外部导入");
        sources.add(source2);
        
        return ResultModel.success(sources);
    }

    @PostMapping("/llm-preview")
    public ResultModel<LlmPreviewResultDTO> llmPreview(@RequestBody LlmPreviewResultDTO request) {
        log.info("[EnterpriseProcedureController] LLM preview");
        
        LlmPreviewResultDTO result = new LlmPreviewResultDTO();
        result.setPreview("Generated procedure preview");
        result.setSuggestedSteps(5);
        
        return ResultModel.success(result);
    }

    @PostMapping("/llm-assist")
    public ResultModel<LlmPreviewResultDTO> llmAssist(@RequestBody LlmPreviewResultDTO request) {
        log.info("[EnterpriseProcedureController] LLM assist");
        
        LlmPreviewResultDTO result = new LlmPreviewResultDTO();
        result.setSuggestions(Arrays.asList(
            "建议添加审批节点",
            "建议添加通知步骤",
            "建议设置超时处理"
        ));
        
        return ResultModel.success(result);
    }
}
