package net.ooder.skill.scenes.controller;

import net.ooder.skill.scenes.dto.*;
import net.ooder.skill.scenes.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scene-groups")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class SceneGroupController {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupController.class);

    @GetMapping("/my/created")
    public ResultModel<List<SceneGroupDTO>> getMyCreatedGroups() {
        log.info("[SceneGroupController] getMyCreatedGroups");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/my/led")
    public ResultModel<List<SceneGroupDTO>> getMyLedGroups() {
        log.info("[SceneGroupController] getMyLedGroups");
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/my/participated")
    public ResultModel<List<SceneGroupDTO>> getMyParticipatedGroups() {
        log.info("[SceneGroupController] getMyParticipatedGroups");
        return ResultModel.success(new ArrayList<>());
    }

    @PostMapping
    public ResultModel<SceneGroupDTO> createGroup(@RequestBody SceneGroupDTO request) {
        log.info("[SceneGroupController] createGroup: {}", request.getName());
        request.setId(UUID.randomUUID().toString());
        request.setStatus("DRAFT");
        request.setCreatedAt(System.currentTimeMillis());
        return ResultModel.success(request);
    }

    @GetMapping("/{id}")
    public ResultModel<SceneGroupDTO> getGroup(@PathVariable String id) {
        log.info("[SceneGroupController] getGroup: {}", id);
        SceneGroupDTO dto = new SceneGroupDTO();
        dto.setId(id);
        return ResultModel.success(dto);
    }

    @PutMapping("/{id}")
    public ResultModel<SceneGroupDTO> updateGroup(@PathVariable String id, @RequestBody SceneGroupDTO request) {
        log.info("[SceneGroupController] updateGroup: {}", id);
        request.setId(id);
        return ResultModel.success(request);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Void> deleteGroup(@PathVariable String id) {
        log.info("[SceneGroupController] deleteGroup: {}", id);
        return ResultModel.success(null);
    }

    @PostMapping("/{id}/activate")
    public ResultModel<Void> activateGroup(@PathVariable String id) {
        log.info("[SceneGroupController] activateGroup: {}", id);
        return ResultModel.success(null);
    }

    @PostMapping("/{id}/deactivate")
    public ResultModel<Void> deactivateGroup(@PathVariable String id) {
        log.info("[SceneGroupController] deactivateGroup: {}", id);
        return ResultModel.success(null);
    }

    @GetMapping("/{id}/capabilities")
    public ResultModel<List<SceneCapabilityDTO>> getGroupCapabilities(@PathVariable String id) {
        log.info("[SceneGroupController] getGroupCapabilities: {}", id);
        return ResultModel.success(new ArrayList<>());
    }

    @PostMapping("/{id}/capabilities")
    public ResultModel<SceneCapabilityDTO> addCapability(@PathVariable String id, @RequestBody SceneCapabilityDTO request) {
        log.info("[SceneGroupController] addCapability: {}", id);
        return ResultModel.success(request);
    }

    @GetMapping("/{id}/participants")
    public ResultModel<List<SceneParticipantDTO>> getParticipants(@PathVariable String id) {
        log.info("[SceneGroupController] getParticipants: {}", id);
        return ResultModel.success(new ArrayList<>());
    }

    @PostMapping("/{id}/participants")
    public ResultModel<SceneParticipantDTO> addParticipant(@PathVariable String id, @RequestBody SceneParticipantDTO request) {
        log.info("[SceneGroupController] addParticipant: {}", id);
        return ResultModel.success(request);
    }

    @GetMapping("/{id}/knowledge")
    public ResultModel<List<KnowledgeBindingDTO>> getKnowledgeBases(@PathVariable String id) {
        log.info("[SceneGroupController] getKnowledgeBases: {}", id);
        return ResultModel.success(new ArrayList<>());
    }

    @PostMapping("/{id}/knowledge")
    public ResultModel<KnowledgeBindingDTO> addKnowledgeBase(@PathVariable String id, @RequestBody KnowledgeBindingDTO request) {
        log.info("[SceneGroupController] addKnowledgeBase: {}", id);
        return ResultModel.success(request);
    }

    @GetMapping("/{id}/llm/config")
    public ResultModel<SceneLlmConfigDTO> getLlmConfig(@PathVariable String id) {
        log.info("[SceneGroupController] getLlmConfig: {}", id);
        return ResultModel.success(new SceneLlmConfigDTO());
    }

    @PostMapping("/{id}/llm/config")
    public ResultModel<SceneLlmConfigDTO> updateLlmConfig(@PathVariable String id, @RequestBody SceneLlmConfigDTO request) {
        log.info("[SceneGroupController] updateLlmConfig: {}", id);
        return ResultModel.success(request);
    }
}
