package net.ooder.skill.llm.config.controller;

import net.ooder.skill.llm.config.dto.*;
import net.ooder.skill.llm.config.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm-config")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class LlmConfigController {

    private static final Logger log = LoggerFactory.getLogger(LlmConfigController.class);

    @GetMapping
    public ResultModel<List<LlmConfigDTO>> listConfigs() {
        log.info("[LlmConfigController] listConfigs");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/all")
    public ResultModel<List<LlmConfigDTO>> listAllConfigs() {
        log.info("[LlmConfigController] listAllConfigs");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResultModel<LlmConfigDTO> getConfig(@PathVariable String id) {
        log.info("[LlmConfigController] getConfig: {}", id);
        LlmConfigDTO dto = new LlmConfigDTO();
        dto.setId(id);
        return ResultModel.success(dto);
    }

    @PostMapping
    public ResultModel<LlmConfigDTO> createConfig(@RequestBody LlmConfigDTO request) {
        log.info("[LlmConfigController] createConfig: {}", request.getName());
        request.setId(UUID.randomUUID().toString());
        request.setCreatedAt(System.currentTimeMillis());
        return ResultModel.success(request);
    }

    @PutMapping("/{id}")
    public ResultModel<LlmConfigDTO> updateConfig(@PathVariable String id, @RequestBody LlmConfigDTO request) {
        log.info("[LlmConfigController] updateConfig: {}", id);
        request.setId(id);
        return ResultModel.success(request);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Void> deleteConfig(@PathVariable String id) {
        log.info("[LlmConfigController] deleteConfig: {}", id);
        return ResultModel.success(null);
    }

    @GetMapping("/providers")
    public ResultModel<List<LlmProviderConfigDTO>> getAvailableProviders() {
        log.info("[LlmConfigController] getAvailableProviders");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/providers/info")
    public ResultModel<List<LlmProviderMeta>> getProvidersInfo() {
        log.info("[LlmConfigController] getProvidersInfo");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/templates")
    public ResultModel<List<LlmConfigTemplateDTO>> getConfigTemplates() {
        log.info("[LlmConfigController] getConfigTemplates");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/{id}/usage-stats")
    public ResultModel<LlmUsageStatsDTO> getUsageStats(@PathVariable String id) {
        log.info("[LlmConfigController] getUsageStats: {}", id);
        return ResultModel.success(new LlmUsageStatsDTO());
    }
}
