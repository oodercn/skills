package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.llm.ModuleApiRegistry;
import net.ooder.skill.scene.llm.SceneActionExecutor;
import net.ooder.skill.scene.llm.SceneActionExecutor.ActionRequest;
import net.ooder.skill.scene.llm.SceneActionExecutor.ActionResult;
import net.ooder.skill.scene.llm.SceneActionExecutor.ScriptRequest;
import net.ooder.skill.scene.llm.SceneActionExecutor.ScriptResult;
import net.ooder.skill.scene.llm.assistant.ScriptGenerationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/llm")
public class LlmScriptController {

    private static final Logger log = LoggerFactory.getLogger(LlmScriptController.class);

    private final ModuleApiRegistry moduleApiRegistry;
    private final SceneActionExecutor actionExecutor;

    @Autowired
    public LlmScriptController(ModuleApiRegistry moduleApiRegistry, 
                               SceneActionExecutor actionExecutor) {
        this.moduleApiRegistry = moduleApiRegistry;
        this.actionExecutor = actionExecutor;
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeAction(@RequestBody Map<String, Object> request) {
        log.info("[LlmScriptController] Received execute request: {}", request);

        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setAction((String) request.get("action"));
        actionRequest.setModule((String) request.get("module"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        if (params != null) {
            actionRequest.setParams(params);
        }
        
        Boolean requireConfirm = (Boolean) request.get("requireConfirm");
        if (requireConfirm != null) {
            actionRequest.setRequireConfirm(requireConfirm);
        }
        
        Boolean syncContext = (Boolean) request.get("syncContext");
        if (syncContext != null) {
            actionRequest.setSyncContext(syncContext);
        }

        ActionResult result = actionExecutor.execute(actionRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("needConfirm", result.isNeedConfirm());
        
        if (result.isNeedConfirm()) {
            response.put("confirmMessage", result.getConfirmMessage());
            Map<String, Object> pendingAction = new HashMap<>();
            pendingAction.put("action", actionRequest.getAction());
            pendingAction.put("module", actionRequest.getModule());
            pendingAction.put("params", actionRequest.getParams());
            response.put("pendingAction", pendingAction);
        } else if (result.isSuccess()) {
            response.put("result", result.getResult());
        } else {
            response.put("error", result.getError());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/execute-script")
    public ResponseEntity<Map<String, Object>> executeScript(@RequestBody Map<String, Object> request) {
        log.info("[LlmScriptController] Received script execution request");

        ScriptRequest scriptRequest = new ScriptRequest();
        scriptRequest.setScript((String) request.get("script"));
        scriptRequest.setScriptType((String) request.getOrDefault("scriptType", "mvel"));
        scriptRequest.setModule((String) request.get("module"));
        
        Boolean requireConfirm = (Boolean) request.get("requireConfirm");
        if (requireConfirm != null) {
            scriptRequest.setRequireConfirm(requireConfirm);
        }
        
        Boolean syncContext = (Boolean) request.get("syncContext");
        if (syncContext != null) {
            scriptRequest.setSyncContext(syncContext);
        }

        ScriptResult result = actionExecutor.executeScript(scriptRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("needConfirm", result.isNeedConfirm());
        
        if (result.isNeedConfirm()) {
            response.put("confirmMessage", result.getConfirmMessage());
            Map<String, Object> pendingScript = new HashMap<>();
            pendingScript.put("script", scriptRequest.getScript());
            pendingScript.put("scriptType", scriptRequest.getScriptType());
            pendingScript.put("module", scriptRequest.getModule());
            response.put("pendingScript", pendingScript);
        } else if (result.isSuccess()) {
            response.put("result", result.getResult());
        } else {
            response.put("error", result.getError());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-script")
    public ResponseEntity<Map<String, Object>> generateScript(@RequestBody Map<String, Object> request) {
        log.info("[LlmScriptController] Received script generation request");

        String intent = (String) request.get("intent");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.get("context");
        if (context == null) {
            context = new HashMap<>();
        }

        ScriptGenerationResultDto result = actionExecutor.generateScriptForIntent(intent, context);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        
        if (result.isSuccess()) {
            response.put("script", result.getScript());
            response.put("scriptType", result.getScriptType());
            response.put("module", result.getModule());
            response.put("explanation", result.getExplanation());
        } else {
            response.put("error", result.getError());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmAction(@RequestBody Map<String, Object> request) {
        log.info("[LlmScriptController] Received confirm request");

        String action = (String) request.get("action");
        String module = (String) request.get("module");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.get("params");

        moduleApiRegistry.setCurrentModule(module);

        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setAction(action);
        actionRequest.setModule(module);
        if (params != null) {
            actionRequest.setParams(params);
        }
        actionRequest.setRequireConfirm(false);
        actionRequest.setSyncContext(true);

        ActionResult result = actionExecutor.execute(actionRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        
        if (result.isSuccess()) {
            response.put("result", result.getResult());
        } else {
            response.put("error", result.getError());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/module/set")
    public ResponseEntity<Map<String, Object>> setCurrentModule(@RequestBody Map<String, String> request) {
        String module = request.get("module");
        
        if (module == null || module.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Module name is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        moduleApiRegistry.setCurrentModule(module);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("currentModule", module);
        response.put("availableApis", moduleApiRegistry.getAvailableApis(module));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/module/current")
    public ResponseEntity<Map<String, Object>> getCurrentModule() {
        String currentModule = moduleApiRegistry.getCurrentModule();
        Set<String> availableApis = moduleApiRegistry.getCurrentAvailableApis();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("currentModule", currentModule);
        response.put("availableApis", availableApis);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/module/{module}/apis")
    public ResponseEntity<Map<String, Object>> getModuleApis(@PathVariable String module) {
        Set<String> apis = moduleApiRegistry.getAvailableApis(module);
        Map<String, Object> moduleInfo = moduleApiRegistry.getModuleInfo(module);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("module", module);
        response.put("apis", apis);
        response.put("info", moduleInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/modules")
    public ResponseEntity<Map<String, Object>> getAllModules() {
        List<Map<String, Object>> modules = moduleApiRegistry.getAllModulesInfo();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("modules", modules);
        return ResponseEntity.ok(response);
    }
}
