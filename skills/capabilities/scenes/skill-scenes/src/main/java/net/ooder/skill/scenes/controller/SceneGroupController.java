package net.ooder.skill.scenes.controller;

import net.ooder.skill.scenes.dto.SceneDTO;
import net.ooder.skill.scenes.dto.SceneGroupDTO;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.model.ResultModel;
import net.ooder.skill.scenes.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scene-groups")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SceneGroupController {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupController.class);

    @Autowired
    private SceneGroupService sceneGroupService;

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
        group.setId(id);
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
    public ResultModel<List<Map<String, Object>>> getGroupCapabilities(@PathVariable String id) {
        log.info("[SceneGroupController] Get capabilities for group: {}", id);
        List<Map<String, Object>> caps = sceneGroupService.getCapabilities(id);
        return ResultModel.success(caps);
    }

    @PostMapping("/{id}/capabilities")
    public ResultModel<Map<String, Object>> addCapability(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String capId = (String) request.get("capId");
        log.info("[SceneGroupController] Add capability {} to group {}", capId, id);
        Map<String, Object> result = sceneGroupService.addCapability(id, capId);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/capabilities/{capId}")
    public ResultModel<Boolean> removeCapability(@PathVariable String id, @PathVariable String capId) {
        log.info("[SceneGroupController] Remove capability {} from group {}", capId, id);
        boolean result = sceneGroupService.removeCapability(id, capId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/participants")
    public ResultModel<List<Map<String, Object>>> getParticipants(@PathVariable String id) {
        log.info("[SceneGroupController] Get participants for group: {}", id);
        List<Map<String, Object>> participants = sceneGroupService.getParticipants(id);
        return ResultModel.success(participants);
    }

    @PostMapping("/{id}/participants")
    public ResultModel<Map<String, Object>> addParticipant(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String role = (String) request.get("role");
        log.info("[SceneGroupController] Add participant {} to group {} with role {}", userId, id, role);
        Map<String, Object> result = sceneGroupService.addParticipant(id, userId, role);
        return ResultModel.success(result);
    }

    @PutMapping("/{id}/participants/{participantId}/role")
    public ResultModel<Boolean> updateParticipantRole(@PathVariable String id, @PathVariable String participantId, @RequestBody Map<String, Object> request) {
        String role = (String) request.get("role");
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
    public ResultModel<List<Map<String, Object>>> getSnapshots(@PathVariable String id) {
        log.info("[SceneGroupController] Get snapshots for group: {}", id);
        List<Map<String, Object>> snapshots = sceneGroupService.getSnapshots(id);
        return ResultModel.success(snapshots);
    }

    @PostMapping("/{id}/snapshots")
    public ResultModel<Map<String, Object>> createSnapshot(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        log.info("[SceneGroupController] Create snapshot for group {} with name {}", id, name);
        Map<String, Object> snapshot = sceneGroupService.createSnapshot(id, name);
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
    public ResultModel<List<Map<String, Object>>> getKnowledgeBases(@PathVariable String id) {
        log.info("[SceneGroupController] Get knowledge bases for group: {}", id);
        List<Map<String, Object>> kbs = sceneGroupService.getKnowledgeBases(id);
        return ResultModel.success(kbs);
    }

    @PostMapping("/{id}/knowledge")
    public ResultModel<Map<String, Object>> addKnowledgeBase(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String kbId = (String) request.get("kbId");
        log.info("[SceneGroupController] Add knowledge base {} to group {}", kbId, id);
        Map<String, Object> result = sceneGroupService.addKnowledgeBase(id, kbId);
        return ResultModel.success(result);
    }

    @DeleteMapping("/{id}/knowledge/{kbId}")
    public ResultModel<Boolean> removeKnowledgeBase(@PathVariable String id, @PathVariable String kbId) {
        log.info("[SceneGroupController] Remove knowledge base {} from group {}", kbId, id);
        boolean result = sceneGroupService.removeKnowledgeBase(id, kbId);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/llm/config")
    public ResultModel<Map<String, Object>> getLlmConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Get LLM config for group: {}", id);
        Map<String, Object> config = sceneGroupService.getLlmConfig(id);
        return ResultModel.success(config);
    }

    @PostMapping("/{id}/llm/config")
    public ResultModel<Map<String, Object>> updateLlmConfig(@PathVariable String id, @RequestBody Map<String, Object> config) {
        log.info("[SceneGroupController] Update LLM config for group: {}", id);
        Map<String, Object> result = sceneGroupService.updateLlmConfig(id, config);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/event-log")
    public ResultModel<PageResult<Map<String, Object>>> getEventLog(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[SceneGroupController] Get event log for group: {}", id);
        PageResult<Map<String, Object>> logs = sceneGroupService.getEventLog(id, pageNum, pageSize);
        return ResultModel.success(logs);
    }

    @PostMapping("/{id}/workflow/start")
    public ResultModel<Map<String, Object>> startWorkflow(@PathVariable String id, @RequestBody Map<String, Object> request) {
        String workflowId = (String) request.get("workflowId");
        log.info("[SceneGroupController] Start workflow {} for group {}", workflowId, id);
        Map<String, Object> result = sceneGroupService.startWorkflow(id, workflowId, request);
        return ResultModel.success(result);
    }

    @PostMapping("/from-fusion")
    public ResultModel<SceneGroupDTO> createFromFusion(@RequestBody Map<String, Object> request) {
        String fusionId = (String) request.get("fusionId");
        log.info("[SceneGroupController] Create group from fusion: {}", fusionId);
        SceneGroupDTO group = sceneGroupService.createFromFusion(fusionId, request);
        return ResultModel.success(group);
    }

    @GetMapping("/{id}/capabilities/{capId}")
    public ResultModel<Map<String, Object>> getCapability(@PathVariable String id, @PathVariable String capId) {
        log.info("[SceneGroupController] Get capability {} for group {}", capId, id);
        Map<String, Object> cap = sceneGroupService.getCapability(id, capId);
        if (cap == null) {
            return ResultModel.notFound("Capability not found: " + capId);
        }
        return ResultModel.success(cap);
    }

    @GetMapping("/{id}/knowledge/config")
    public ResultModel<Map<String, Object>> getKnowledgeConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Get knowledge config for group: {}", id);
        Map<String, Object> config = sceneGroupService.getKnowledgeConfig(id);
        return ResultModel.success(config);
    }

    @PostMapping("/{id}/knowledge/config")
    public ResultModel<Map<String, Object>> updateKnowledgeConfig(@PathVariable String id, @RequestBody Map<String, Object> config) {
        log.info("[SceneGroupController] Update knowledge config for group: {}", id);
        Map<String, Object> result = sceneGroupService.updateKnowledgeConfig(id, config);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/llm/providers/{providerId}/models")
    public ResultModel<List<Map<String, Object>>> getLlmProviderModels(@PathVariable String id, @PathVariable String providerId) {
        log.info("[SceneGroupController] Get LLM provider {} models for group {}", providerId, id);
        List<Map<String, Object>> models = sceneGroupService.getLlmProviderModels(id, providerId);
        return ResultModel.success(models);
    }

    @PostMapping("/{id}/llm/reset")
    public ResultModel<Boolean> resetLlmConfig(@PathVariable String id) {
        log.info("[SceneGroupController] Reset LLM config for group: {}", id);
        boolean result = sceneGroupService.resetLlmConfig(id);
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/{action}")
    public ResultModel<Map<String, Object>> executeAction(@PathVariable String id, @PathVariable String action, @RequestBody Map<String, Object> params) {
        log.info("[SceneGroupController] Execute action {} for group {}", action, id);
        Map<String, Object> result = sceneGroupService.executeAction(id, action, params);
        return ResultModel.success(result);
    }

    @GetMapping("/my/led/members")
    public ResultModel<List<Map<String, Object>>> getMyLedMembers() {
        log.info("[SceneGroupController] Get my led members");
        List<Map<String, Object>> members = sceneGroupService.getMyLedMembers();
        return ResultModel.success(members);
    }
}