package net.ooder.skill.template.controller;

import net.ooder.skill.template.model.ResultModel;
import net.ooder.skill.template.dto.DeployResultDTO;
import net.ooder.skill.template.dto.InstallResultDTO;
import net.ooder.skill.template.dto.SceneTemplateDTO;
import net.ooder.skill.template.dto.HealthCheckResultDTO;
import net.ooder.skill.template.dto.DependencyStatusDTO;
import net.ooder.skill.template.dto.AutoInstallResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/templates")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class SceneTemplateController {

    private static final Logger log = LoggerFactory.getLogger(SceneTemplateController.class);

    @Autowired(required = false)
    private SceneTemplateService templateService;

    @GetMapping
    public ResultModel<List<SceneTemplateDTO>> listTemplates() {
        log.info("[listTemplates] Listing all templates");
        if (templateService == null) {
            return ResultModel.success(List.of());
        }
        List<SceneTemplateDTO> templates = templateService.listTemplates();
        return ResultModel.success(templates);
    }

    @GetMapping("/{templateId}")
    public ResultModel<SceneTemplateDTO> getTemplate(@PathVariable String templateId) {
        log.info("[getTemplate] Getting template: {}", templateId);
        if (templateService == null) {
            return ResultModel.notFound("Template service not available");
        }
        SceneTemplateDTO template = templateService.getTemplate(templateId);
        if (template == null) {
            return ResultModel.notFound("Template not found: " + templateId);
        }
        return ResultModel.success(template);
    }

    @PostMapping("/{templateId}/deploy")
    public ResultModel<DeployResultDTO> deployTemplate(
            @PathVariable String templateId,
            @RequestBody(required = false) DeployRequest request) {
        
        log.info("[deployTemplate] Deploying template: {}", templateId);
        
        if (templateService == null) {
            return ResultModel.error(500, "Template service not available");
        }
        
        try {
            DeployResultDTO result = templateService.deployTemplate(templateId);
            
            if (result.isSuccess()) {
                log.info("[deployTemplate] Template {} deployed successfully, sceneId: {}", 
                    templateId, result.getSceneId());
                return ResultModel.success(result);
            } else {
                log.warn("[deployTemplate] Failed to deploy template {}: {}", templateId, result.getMessage());
                return ResultModel.error(500, result.getMessage());
            }
        } catch (Exception e) {
            log.error("[deployTemplate] Error deploying template: {}", templateId, e);
            return ResultModel.error(500, "Failed to deploy template: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{templateId}/deploy/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter deployTemplateWithProgress(@PathVariable String templateId) {
        log.info("[deployTemplateWithProgress] Starting SSE deployment for template: {}", templateId);
        if (templateService == null) {
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event().name("error").data("Template service not available"));
                emitter.completeWithError(new RuntimeException("Template service not available"));
            } catch (Exception e) {
                log.error("Error sending error", e);
            }
            return emitter;
        }
        return templateService.deployTemplateWithProgress(templateId);
    }

    @PostMapping("/{templateId}/install")
    public ResultModel<InstallResultDTO> installTemplate(@PathVariable String templateId) {
        log.info("[installTemplate] Installing template dependencies: {}", templateId);
        
        if (templateService == null) {
            return ResultModel.error(500, "Template service not available");
        }
        
        try {
            InstallResultDTO result = templateService.installTemplateDependencies(templateId);
            
            if (result.isSuccess()) {
                log.info("[installTemplate] Template {} dependencies installed successfully", templateId);
                return ResultModel.success(result);
            } else {
                log.warn("[installTemplate] Failed to install template {} dependencies: {}", templateId, result.getMessage());
                return ResultModel.error(500, result.getMessage());
            }
        } catch (Exception e) {
            log.error("[installTemplate] Error installing template dependencies: {}", templateId, e);
            return ResultModel.error(500, "Failed to install dependencies: " + e.getMessage());
        }
    }

    @GetMapping("/{templateId}/dependencies/health")
    public ResultModel<HealthCheckResultDTO> checkDependenciesHealth(@PathVariable String templateId) {
        log.info("[checkDependenciesHealth] Checking dependencies health for template: {}", templateId);
        
        if (templateService == null) {
            return ResultModel.error(500, "Template service not available");
        }
        
        try {
            HealthCheckResultDTO result = templateService.checkDependenciesHealth(templateId);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[checkDependenciesHealth] Error checking dependencies for template: {}", templateId, e);
            return ResultModel.error(500, "Failed to check dependencies: " + e.getMessage());
        }
    }

    @GetMapping("/{templateId}/dependencies/missing")
    public ResultModel<List<DependencyStatusDTO>> getMissingDependencies(@PathVariable String templateId) {
        log.info("[getMissingDependencies] Getting missing dependencies for template: {}", templateId);
        
        if (templateService == null) {
            return ResultModel.success(List.of());
        }
        
        try {
            List<DependencyStatusDTO> missing = templateService.getMissingDependencies(templateId);
            return ResultModel.success(missing);
        } catch (Exception e) {
            log.error("[getMissingDependencies] Error getting missing dependencies for template: {}", templateId, e);
            return ResultModel.error(500, "Failed to get missing dependencies: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/dependencies/auto-install")
    public ResultModel<AutoInstallResultDTO> autoInstallDependencies(
            @PathVariable String templateId,
            @RequestParam(required = false, defaultValue = "false") boolean includeOptional) {
        log.info("[autoInstallDependencies] Auto installing dependencies for template: {}, includeOptional: {}", 
            templateId, includeOptional);
        
        if (templateService == null) {
            return ResultModel.error(500, "Template service not available");
        }
        
        try {
            AutoInstallResultDTO result = templateService.autoInstallDependencies(templateId, includeOptional);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[autoInstallDependencies] Error auto installing dependencies for template: {}", templateId, e);
            return ResultModel.error(500, "Failed to auto install dependencies: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/dependencies/install-missing")
    public ResultModel<AutoInstallResultDTO> installMissingRequired(@PathVariable String templateId) {
        log.info("[installMissingRequired] Installing missing required dependencies for template: {}", templateId);
        
        if (templateService == null) {
            return ResultModel.error(500, "Template service not available");
        }
        
        try {
            AutoInstallResultDTO result = templateService.installMissingRequired(templateId);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[installMissingRequired] Error installing missing required dependencies for template: {}", templateId, e);
            return ResultModel.error(500, "Failed to install missing required dependencies: " + e.getMessage());
        }
    }

    public static class DeployRequest {
        private String name;
        private Map<String, Object> overrides;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Map<String, Object> getOverrides() { return overrides; }
        public void setOverrides(Map<String, Object> overrides) { this.overrides = overrides; }
    }
}
