package net.ooder.bpm.designer.controller;

import net.ooder.bpm.designer.model.ApiResponse;
import net.ooder.bpm.designer.model.ProcessDef;
import net.ooder.bpm.designer.service.DesignerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpm")
@CrossOrigin(origins = "*")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @GetMapping("/process/{processId}/version/{version}")
    public ApiResponse<ProcessDef> getProcess(
            @PathVariable String processId,
            @PathVariable String version) {
        try {
            ProcessDef process = designerService.getProcess(processId, version);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(404, "Process not found: " + e.getMessage());
        }
    }

    @GetMapping("/process/{processId}/version/latest")
    public ApiResponse<ProcessDef> getProcessLatestVersion(@PathVariable String processId) {
        try {
            ProcessDef process = designerService.getProcess(processId, "latest");
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(404, "Process not found: " + e.getMessage());
        }
    }

    @GetMapping("/process")
    public ApiResponse<List<ProcessDef>> getProcessList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<ProcessDef> processes = designerService.getProcessList(category, status, page, size);
            return ApiResponse.success(processes);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get process list: " + e.getMessage());
        }
    }

    @PostMapping("/process")
    public ApiResponse<ProcessDef> createProcess(@RequestBody ProcessDef processDef) {
        try {
            ProcessDef created = designerService.saveProcess(processDef);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to create process: " + e.getMessage());
        }
    }

    @PutMapping("/process/{processId}")
    public ApiResponse<ProcessDef> updateProcess(
            @PathVariable String processId,
            @RequestBody ProcessDef processDef) {
        try {
            processDef.setProcessDefId(processId);
            ProcessDef updated = designerService.saveProcess(processDef);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to update process: " + e.getMessage());
        }
    }

    @DeleteMapping("/process/{processId}")
    public ApiResponse<Void> deleteProcess(@PathVariable String processId) {
        try {
            designerService.deleteProcess(processId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to delete process: " + e.getMessage());
        }
    }

    @GetMapping("/process/{processId}/export/yaml")
    public ApiResponse<String> exportYaml(@PathVariable String processId) {
        try {
            String yaml = designerService.exportYaml(processId);
            return ApiResponse.success(yaml);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to export YAML: " + e.getMessage());
        }
    }

    @PostMapping("/process/import/yaml")
    public ApiResponse<ProcessDef> importYaml(@RequestBody Map<String, String> request) {
        try {
            String yaml = request.get("yaml");
            ProcessDef process = designerService.importYaml(yaml);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to import YAML: " + e.getMessage());
        }
    }

    @GetMapping("/process/tree")
    public ApiResponse<List<Map<String, Object>>> getProcessTree() {
        try {
            return ApiResponse.success(designerService.getProcessTree());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get process tree: " + e.getMessage());
        }
    }

    @PostMapping("/process/{processId}/activity")
    public ApiResponse<ProcessDef> addActivity(
            @PathVariable String processId,
            @RequestBody Map<String, Object> activityDef) {
        try {
            ProcessDef process = designerService.addActivity(processId, activityDef);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to add activity: " + e.getMessage());
        }
    }

    @PutMapping("/process/{processId}/activity/{activityId}")
    public ApiResponse<ProcessDef> updateActivity(
            @PathVariable String processId,
            @PathVariable String activityId,
            @RequestBody Map<String, Object> activityDef) {
        try {
            ProcessDef process = designerService.updateActivity(processId, activityId, activityDef);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to update activity: " + e.getMessage());
        }
    }

    @DeleteMapping("/process/{processId}/activity/{activityId}")
    public ApiResponse<ProcessDef> removeActivity(
            @PathVariable String processId,
            @PathVariable String activityId) {
        try {
            ProcessDef process = designerService.removeActivity(processId, activityId);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to remove activity: " + e.getMessage());
        }
    }

    @PostMapping("/process/{processId}/route")
    public ApiResponse<ProcessDef> addRoute(
            @PathVariable String processId,
            @RequestBody Map<String, Object> routeDef) {
        try {
            ProcessDef process = designerService.addRoute(processId, routeDef);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to add route: " + e.getMessage());
        }
    }

    @PutMapping("/process/{processId}/route/{routeId}")
    public ApiResponse<ProcessDef> updateRoute(
            @PathVariable String processId,
            @PathVariable String routeId,
            @RequestBody Map<String, Object> routeDef) {
        try {
            ProcessDef process = designerService.updateRoute(processId, routeId, routeDef);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to update route: " + e.getMessage());
        }
    }

    @DeleteMapping("/process/{processId}/route/{routeId}")
    public ApiResponse<ProcessDef> removeRoute(
            @PathVariable String processId,
            @PathVariable String routeId) {
        try {
            ProcessDef process = designerService.removeRoute(processId, routeId);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to remove route: " + e.getMessage());
        }
    }

    @GetMapping("/capabilities")
    public ApiResponse<List<String>> getCapabilities() {
        return ApiResponse.success(designerService.getCapabilities());
    }

    @GetMapping("/enums/{enumType}")
    public ApiResponse<List<Map<String, String>>> getEnumOptions(@PathVariable String enumType) {
        return ApiResponse.success(designerService.getEnumOptions(enumType));
    }
}
