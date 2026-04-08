package net.ooder.bpm.designer.controller;

import net.ooder.bpm.designer.function.DesignerFunctionDefinition;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import net.ooder.bpm.designer.model.ApiResponse;
import net.ooder.bpm.designer.model.dto.*;
import net.ooder.bpm.designer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpm/designer/derivation")
public class DesignerDerivationController {
    
    @Autowired
    private PerformerDerivationService performerDerivationService;
    
    @Autowired
    private CapabilityMatchingService capabilityMatchingService;
    
    @Autowired
    private FormMatchingService formMatchingService;
    
    @Autowired
    private PanelRenderService panelRenderService;
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @PostMapping("/performer")
    public ResponseEntity<ApiResponse<PerformerDerivationResultDTO>> derivePerformer(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            PerformerDerivationResultDTO result = performerDerivationService.derive(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("推导失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/performer/search")
    public ResponseEntity<ApiResponse<List<PerformerDerivationResultDTO.PerformerCandidate>>> searchCandidates(
            @RequestParam String query,
            @RequestParam(required = false) Map<String, Object> filters) {
        try {
            List<PerformerDerivationResultDTO.PerformerCandidate> candidates = 
                performerDerivationService.searchCandidates(query, filters);
            return ResponseEntity.ok(ApiResponse.success(candidates));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/capability")
    public ResponseEntity<ApiResponse<CapabilityMatchingResultDTO>> matchCapability(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            CapabilityMatchingResultDTO result = capabilityMatchingService.match(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("匹配失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/capability/smart")
    public ResponseEntity<ApiResponse<CapabilityMatchingResultDTO>> smartMatchCapability(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            CapabilityMatchingResultDTO result = capabilityMatchingService.smartMatch(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("智能匹配失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/form")
    public ResponseEntity<ApiResponse<FormMatchingResultDTO>> matchForm(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            FormMatchingResultDTO result = formMatchingService.match(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("匹配失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/form/smart")
    public ResponseEntity<ApiResponse<FormMatchingResultDTO>> smartMatchForm(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            FormMatchingResultDTO result = formMatchingService.smartMatch(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("智能匹配失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/form/generate")
    public ResponseEntity<ApiResponse<FormMatchingResultDTO.FormSchema>> generateFormSchema(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            FormMatchingResultDTO.FormSchema schema = formMatchingService.generateSchema(
                context, request.getActivityDesc());
            return ResponseEntity.ok(ApiResponse.success(schema));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("生成失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/panel/performer")
    public ResponseEntity<ApiResponse<PanelRenderDataDTO>> buildPerformerPanel(
            @RequestBody PerformerDerivationResultDTO result) {
        try {
            PanelRenderDataDTO panel = panelRenderService.buildPerformerPanel(result);
            return ResponseEntity.ok(ApiResponse.success(panel));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("构建面板失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/panel/capability")
    public ResponseEntity<ApiResponse<PanelRenderDataDTO>> buildCapabilityPanel(
            @RequestBody CapabilityMatchingResultDTO result) {
        try {
            PanelRenderDataDTO panel = panelRenderService.buildCapabilityPanel(result);
            return ResponseEntity.ok(ApiResponse.success(panel));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("构建面板失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/panel/form")
    public ResponseEntity<ApiResponse<PanelRenderDataDTO>> buildFormPanel(
            @RequestBody FormMatchingResultDTO result) {
        try {
            PanelRenderDataDTO panel = panelRenderService.buildFormPanel(result);
            return ResponseEntity.ok(ApiResponse.success(panel));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("构建面板失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/panel/activity")
    public ResponseEntity<ApiResponse<PanelRenderDataDTO>> buildActivityPanel(
            @RequestBody ActivityDerivationRequest request) {
        try {
            PanelRenderDataDTO panel = panelRenderService.buildActivityPanel(
                request.getPerformerResult(),
                request.getCapabilityResult(),
                request.getFormResult()
            );
            return ResponseEntity.ok(ApiResponse.success(panel));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("构建面板失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/full")
    public ResponseEntity<ApiResponse<FullDerivationResult>> fullDerivation(
            @RequestBody DerivationRequest request) {
        try {
            DesignerContextDTO context = buildContext(request);
            
            PerformerDerivationResultDTO performerResult = 
                performerDerivationService.derive(context, request.getActivityDesc());
            
            CapabilityMatchingResultDTO capabilityResult = 
                capabilityMatchingService.match(context, request.getActivityDesc());
            
            FormMatchingResultDTO formResult = 
                formMatchingService.match(context, request.getActivityDesc());
            
            PanelRenderDataDTO panel = panelRenderService.buildActivityPanel(
                performerResult, capabilityResult, formResult);
            
            FullDerivationResult result = new FullDerivationResult();
            result.setPerformerResult(performerResult);
            result.setCapabilityResult(capabilityResult);
            result.setFormResult(formResult);
            result.setPanel(panel);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("完整推导失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/functions")
    public ResponseEntity<ApiResponse<List<DesignerFunctionDefinition>>> getAvailableFunctions() {
        try {
            List<DesignerFunctionDefinition> functions = functionRegistry.getAllFunctions();
            return ResponseEntity.ok(ApiResponse.success(functions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取函数列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/functions/schemas")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFunctionSchemas() {
        try {
            List<Map<String, Object>> schemas = functionRegistry.getOpenAISchemas();
            return ResponseEntity.ok(ApiResponse.success(schemas));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取函数Schema失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/functions/category/{category}")
    public ResponseEntity<ApiResponse<List<DesignerFunctionDefinition>>> getFunctionsByCategory(
            @PathVariable String category) {
        try {
            DesignerFunctionDefinition.FunctionCategory funcCategory = 
                DesignerFunctionDefinition.FunctionCategory.valueOf(category.toUpperCase());
            List<DesignerFunctionDefinition> functions = 
                functionRegistry.getFunctionsByCategory(funcCategory);
            return ResponseEntity.ok(ApiResponse.success(functions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取函数列表失败: " + e.getMessage()));
        }
    }
    
    private DesignerContextDTO buildContext(DerivationRequest request) {
        return DesignerContextDTO.create()
            .sessionId(request.getSessionId())
            .userId(request.getUserId())
            .userName(request.getUserName())
            .currentProcess(request.getCurrentProcess())
            .currentActivity(request.getCurrentActivity())
            .build();
    }
    
    public static class DerivationRequest {
        private String sessionId;
        private String userId;
        private String userName;
        private String activityDesc;
        private ProcessDefDTO currentProcess;
        private ActivityDefDTO currentActivity;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getActivityDesc() { return activityDesc; }
        public void setActivityDesc(String activityDesc) { this.activityDesc = activityDesc; }
        public ProcessDefDTO getCurrentProcess() { return currentProcess; }
        public void setCurrentProcess(ProcessDefDTO currentProcess) { this.currentProcess = currentProcess; }
        public ActivityDefDTO getCurrentActivity() { return currentActivity; }
        public void setCurrentActivity(ActivityDefDTO currentActivity) { this.currentActivity = currentActivity; }
    }
    
    public static class ActivityDerivationRequest {
        private PerformerDerivationResultDTO performerResult;
        private CapabilityMatchingResultDTO capabilityResult;
        private FormMatchingResultDTO formResult;
        
        public PerformerDerivationResultDTO getPerformerResult() { return performerResult; }
        public void setPerformerResult(PerformerDerivationResultDTO performerResult) { this.performerResult = performerResult; }
        public CapabilityMatchingResultDTO getCapabilityResult() { return capabilityResult; }
        public void setCapabilityResult(CapabilityMatchingResultDTO capabilityResult) { this.capabilityResult = capabilityResult; }
        public FormMatchingResultDTO getFormResult() { return formResult; }
        public void setFormResult(FormMatchingResultDTO formResult) { this.formResult = formResult; }
    }
    
    public static class FullDerivationResult {
        private PerformerDerivationResultDTO performerResult;
        private CapabilityMatchingResultDTO capabilityResult;
        private FormMatchingResultDTO formResult;
        private PanelRenderDataDTO panel;
        
        public PerformerDerivationResultDTO getPerformerResult() { return performerResult; }
        public void setPerformerResult(PerformerDerivationResultDTO performerResult) { this.performerResult = performerResult; }
        public CapabilityMatchingResultDTO getCapabilityResult() { return capabilityResult; }
        public void setCapabilityResult(CapabilityMatchingResultDTO capabilityResult) { this.capabilityResult = capabilityResult; }
        public FormMatchingResultDTO getFormResult() { return formResult; }
        public void setFormResult(FormMatchingResultDTO formResult) { this.formResult = formResult; }
        public PanelRenderDataDTO getPanel() { return panel; }
        public void setPanel(PanelRenderDataDTO panel) { this.panel = panel; }
    }
}
