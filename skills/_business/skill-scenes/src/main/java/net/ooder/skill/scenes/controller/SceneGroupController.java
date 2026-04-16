package net.ooder.skill.scenes.controller;

import jakarta.annotation.Resource;
import net.ooder.skill.scenes.dto.*;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.model.ResultModel;
import net.ooder.skill.scenes.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scene-groups")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class SceneGroupController {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupController.class);

    @Resource
    private SceneGroupService sceneGroupService;

    @GetMapping
    public ResultModel<PageResult<SceneGroupDTO>> getAllGroups(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get all groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        if (sceneGroupService == null) {
            return ResultModel.success(PageResult.empty());
        }
        PageResult<SceneGroupDTO> result = sceneGroupService.getAllGroups(pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/my/created")
    public ResultModel<PageResult<SceneGroupDTO>> getMyCreatedGroups(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get my created groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        PageResult<SceneGroupDTO> result = sceneGroupService.getMyCreatedGroups(pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/my/led")
    public ResultModel<PageResult<SceneGroupDTO>> getMyLedGroups(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get my led groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        PageResult<SceneGroupDTO> result = sceneGroupService.getMyLedGroups(pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/my/participated")
    public ResultModel<PageResult<SceneGroupDTO>> getMyParticipatedGroups(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get my participated groups - pageNum: {}, pageSize: {}", pageNum, pageSize);
        PageResult<SceneGroupDTO> result = sceneGroupService.getMyParticipatedGroups(pageNum, pageSize);
        return ResultModel.success(result);
    }

    @PostMapping
    public ResultModel<SceneGroupDTO> createGroup(@RequestBody SceneGroupDTO group) {
        log.info("[SceneGroupController] Create group: {}", group.getName());
        SceneGroupDTO created = sceneGroupService.create(group);
        return ResultModel.success(created);
    }

    @GetMapping("/{id}")
    public ResultModel<SceneGroupDTO> getGroup(@PathVariable String id) {
        log.info("[SceneGroupController] Get group: {}", id);
        SceneGroupDTO group = sceneGroupService.get(id);
        if (group == null) {
            return ResultModel.notFound("Group not found: " + id);
        }
        return ResultModel.success(group);
    }

    @PutMapping("/{id}")
    public ResultModel<SceneGroupDTO> updateGroup(@PathVariable String id, @RequestBody SceneGroupDTO group) {
        log.info("[SceneGroupController] Update group: {}", id);
        group.setSceneGroupId(id);
        SceneGroupDTO updated = sceneGroupService.update(group);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteGroup(@PathVariable String id) {
        log.info("[SceneGroupController] Delete group: {}", id);
        boolean result = sceneGroupService.delete(id);
        if (!result) {
            return ResultModel.error("Failed to delete group or group not found");
        }
        return ResultModel.success(true);
    }

    @PostMapping("/{id}/activate")
    public ResultModel<SceneGroupDTO> activateGroup(@PathVariable String id) {
        log.info("[SceneGroupController] Activate group: {}", id);
        SceneGroupDTO group = sceneGroupService.activate(id);
        if (group == null) {
            return ResultModel.notFound("Group not found: " + id);
        }
        return ResultModel.success(group);
    }

    @PostMapping("/{id}/deactivate")
    public ResultModel<SceneGroupDTO> deactivateGroup(@PathVariable String id) {
        log.info("[SceneGroupController] Deactivate group: {}", id);
        SceneGroupDTO group = sceneGroupService.deactivate(id);
        if (group == null) {
            return ResultModel.notFound("Group not found: " + id);
        }
        return ResultModel.success(group);
    }

    @GetMapping("/{id}/capabilities")
    public ResultModel<List<SceneGroupCapabilityDTO>> getGroupCapabilities(@PathVariable String id) {
        log.info("[SceneGroupController] Get capabilities for group: {}", id);
        List<SceneGroupCapabilityDTO> caps = sceneGroupService.getCapabilities(id);
        return ResultModel.success(caps);
    }

    @PostMapping("/{id}/capabilities")
    public ResultModel<SceneGroupCapabilityDTO> addCapability(@PathVariable String id, @RequestBody AddCapabilityRequest request) {
        String capId = request.getCapId();
        log.info("[SceneGroupController] Add capability {} to group {}", capId, id);
        SceneGroupCapabilityDTO result = sceneGroupService.addCapability(id, capId);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/capabilities/{capId}")
    public ResultModel<Boolean> removeCapability(@PathVariable String id, @PathVariable String capId) {
        log.info("[SceneGroupController] Remove capability {} from group {}", capId, id);
        boolean result = sceneGroupService.removeCapability(id, capId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/participants")
    public ResultModel<List<SceneParticipantDTO>> getParticipants(@PathVariable String id) {
        log.info("[SceneGroupController] Get participants for group: {}", id);
        List<SceneParticipantDTO> participants = sceneGroupService.getParticipants(id);
        return ResultModel.success(participants);
    }

    @PostMapping("/{id}/participants")
    public ResultModel<SceneParticipantDTO> addParticipant(@PathVariable String id, @RequestBody AddParticipantRequest request) {
        String userId = request.getUserId();
        String role = request.getRole();
        log.info("[SceneGroupController] Add participant {} to group {} with role {}", userId, id, role);
        SceneParticipantDTO result = sceneGroupService.addParticipant(id, userId, role);
        return ResultModel.success(result);
    }

    @PutMapping("/{id}/participants/{participantId}/role")
    public ResultModel<Boolean> updateParticipantRole(@PathVariable String id, @PathVariable String participantId, @RequestBody UpdateRoleRequest request) {
        String role = request.getRole();
        log.info("[SceneGroupController] Update participant {} role to {} in group {}", participantId, role, id);
        boolean result = sceneGroupService.updateParticipantRole(id, participantId, role);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/participants/{participantId}")
    public ResultModel<Boolean> removeParticipant(@PathVariable String id, @PathVariable String participantId) {
        log.info("[SceneGroupController] Remove participant {} from group {}", participantId, id);
        boolean result = sceneGroupService.removeParticipant(id, participantId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/snapshots")
    public ResultModel<List<SceneSnapshotDTO>> getSnapshots(@PathVariable String id) {
        log.info("[SceneGroupController] Get snapshots for group: {}", id);
        List<SceneSnapshotDTO> snapshots = sceneGroupService.getSnapshots(id);
        return ResultModel.success(snapshots);
    }

    @PostMapping("/{id}/snapshots")
    public ResultModel<SceneSnapshotDTO> createSnapshot(@PathVariable String id, @RequestBody CreateSnapshotRequest request) {
        String name = request.getName();
        log.info("[SceneGroupController] Create snapshot for group {} with name {}", id, name);
        SceneSnapshotDTO snapshot = sceneGroupService.createSnapshot(id, name);
        return ResultModel.success(snapshot);
    }

    @PostMapping("/{id}/snapshots/{snapshotId}/restore")
    public ResultModel<Boolean> restoreSnapshot(@PathVariable String id, @PathVariable String snapshotId) {
        log.info("[SceneGroupController] Restore snapshot {} for group {}", snapshotId, id);
        boolean result = sceneGroupService.restoreSnapshot(id, snapshotId);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/snapshots/{snapshotId}")
    public ResultModel<Boolean> deleteSnapshot(@PathVariable String id, @PathVariable String snapshotId) {
        log.info("[SceneGroupController] Delete snapshot {} for group {}", snapshotId, id);
        boolean result = sceneGroupService.deleteSnapshot(id, snapshotId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/knowledge")
    public ResultModel<List<KnowledgeBindingDTO>> getKnowledgeBases(@PathVariable String id) {
        log.info("[SceneGroupController] Get knowledge bases for group: {}", id);
        List<KnowledgeBindingDTO> kbs = sceneGroupService.getKnowledgeBases(id);
        return ResultModel.success(kbs);
    }

    @PostMapping("/{id}/knowledge")
    public ResultModel<Boolean> addKnowledgeBase(@PathVariable String id, @RequestBody KnowledgeBindingDTO binding) {
        log.info("[SceneGroupController] Add knowledge base {} to group {}", binding.getKbId(), id);
        boolean result = sceneGroupService.bindKnowledgeBase(id, binding);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/knowledge/{kbId}")
    public ResultModel<Boolean> removeKnowledgeBase(@PathVariable String id, @PathVariable String kbId) {
        log.info("[SceneGroupController] Remove knowledge base {} from group {}", kbId, id);
        boolean result = sceneGroupService.unbindKnowledgeBase(id, kbId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/llm/config")
    public ResultModel<SceneLlmConfigDTO> getLlmConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Get LLM config for group: {}", id);
        SceneLlmConfigDTO config = sceneGroupService.getLlmConfig(id);
        return ResultModel.success(config);
    }

    @PostMapping("/{id}/llm/config")
    public ResultModel<Boolean> updateLlmConfig(@PathVariable String id, @RequestBody SceneLlmConfigDTO config) {
        log.info("[SceneGroupController] Update LLM config for group: {}", id);
        boolean result = sceneGroupService.setLlmConfig(id, config);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/event-log")
    public ResultModel<PageResult<SceneGroupEventLogDTO>> getEventLog(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get event log for group: {}", id);
        PageResult<SceneGroupEventLogDTO> logs = sceneGroupService.getEventLog(id, pageNum, pageSize);
        return ResultModel.success(logs);
    }

    @PostMapping("/{id}/workflow/start")
    public ResultModel<WorkflowResultDTO> startWorkflow(@PathVariable String id, @RequestBody StartWorkflowRequest request) {
        String workflowId = request.getWorkflowId();
        log.info("[SceneGroupController] Start workflow {} for group {}", workflowId, id);
        WorkflowResultDTO result = sceneGroupService.startWorkflow(id, workflowId);
        return ResultModel.success(result);
    }

    @PostMapping("/from-fusion")
    public ResultModel<SceneGroupDTO> createFromFusion(@RequestBody CreateFromFusionRequest request) {
        String fusionId = request.getFusionId();
        log.info("[SceneGroupController] Create group from fusion: {}", fusionId);
        SceneGroupDTO group = sceneGroupService.createFromFusion(fusionId);
        return ResultModel.success(group);
    }

    @GetMapping("/{id}/capabilities/{capId}")
    public ResultModel<SceneGroupCapabilityDTO> getCapability(@PathVariable String id, @PathVariable String capId) {
        log.info("[SceneGroupController] Get capability {} for group {}", capId, id);
        SceneGroupCapabilityDTO cap = sceneGroupService.getCapability(id, capId);
        if (cap == null) {
            return ResultModel.notFound("Capability not found: " + capId);
        }
        return ResultModel.success(cap);
    }

    @GetMapping("/{id}/knowledge/config")
    public ResultModel<SceneKnowledgeConfigDTO> getKnowledgeConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Get knowledge config for group: {}", id);
        SceneKnowledgeConfigDTO config = sceneGroupService.getKnowledgeConfig(id);
        return ResultModel.success(config);
    }

    @PostMapping("/{id}/knowledge/config")
    public ResultModel<SceneKnowledgeConfigDTO> updateKnowledgeConfig(@PathVariable String id, @RequestBody SceneKnowledgeConfigDTO config) {
        log.info("[SceneGroupController] Update knowledge config for group: {}", id);
        SceneKnowledgeConfigDTO result = sceneGroupService.updateKnowledgeConfig(id, config);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/llm/providers/{providerId}/models")
    public ResultModel<List<LlmModelDTO>> getLlmProviderModels(@PathVariable String id, @PathVariable String providerId) {
        log.info("[SceneGroupController] Get LLM provider {} models for group {}", providerId, id);
        List<LlmModelDTO> models = sceneGroupService.getLlmProviderModels(id, providerId);
        return ResultModel.success(models);
    }

    @PostMapping("/{id}/llm/reset")
    public ResultModel<Boolean> resetLlmConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Reset LLM config for group: {}", id);
        boolean result = sceneGroupService.resetLlmConfig(id);
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/{action}")
    public ResultModel<ActionResultDTO> executeAction(@PathVariable String id, @PathVariable String action, @RequestBody ActionRequest request) {
        log.info("[SceneGroupController] Execute action {} for group {}", action, id);
        ActionResultDTO result = sceneGroupService.executeAction(id, action);
        return ResultModel.success(result);
    }

    @GetMapping("/my/led/members")
    public ResultModel<List<MemberDTO>> getMyLedMembers() {
        log.info("[SceneGroupController] Get my led members");
        List<MemberDTO> members = sceneGroupService.getMyLedMembers();
        return ResultModel.success(members);
    }
}
