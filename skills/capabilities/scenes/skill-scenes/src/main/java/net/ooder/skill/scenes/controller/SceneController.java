package net.ooder.skill.scenes.controller;

import net.ooder.skill.scenes.dto.SceneDTO;
import net.ooder.skill.scenes.dto.SceneCapabilityDTO;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.model.ResultModel;
import net.ooder.skill.scenes.service.SceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scenes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SceneController {

    private static final Logger log = LoggerFactory.getLogger(SceneController.class);

    @Autowired
    private SceneService sceneService;

    @PostMapping("/list")
    public ResultModel<PageResult<SceneDTO>> listScenes(@RequestBody Map<String, Object> request) {
        int pageNum = request.containsKey("pageNum") ? (int) request.get("pageNum") : 1;
        int pageSize = request.containsKey("pageSize") ? (int) request.get("pageSize") : 20;
        String status = request.containsKey("status") ? (String) request.get("status") : null;
        log.info("[SceneController] List scenes called - pageNum: {}, pageSize: {}, status: {}", pageNum, pageSize, status);
        PageResult<SceneDTO> result = sceneService.listScenes(status, pageNum, pageSize);
        return ResultModel.success(result);
    }

    @PostMapping("/create")
    public ResultModel<SceneDTO> createScene(@RequestBody SceneDTO scene) {
        log.info("[SceneController] Create scene called: {}", scene.getName());
        SceneDTO created = sceneService.create(scene);
        return ResultModel.success(created);
    }

    @PostMapping("/get")
    public ResultModel<SceneDTO> getScene(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] Get scene called: {}", sceneId);
        SceneDTO scene = sceneService.get(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping("/delete")
    public ResultModel<Boolean> deleteScene(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] Delete scene called: {}", sceneId);
        boolean result = sceneService.delete(sceneId);
        if (!result) {
            return ResultModel.error("Failed to delete scene or scene not found");
        }
        return ResultModel.success(true);
    }

    @PostMapping("/activate")
    public ResultModel<SceneDTO> activateScene(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] Activate scene called: {}", sceneId);
        SceneDTO scene = sceneService.activate(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping("/deactivate")
    public ResultModel<SceneDTO> deactivateScene(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] Deactivate scene called: {}", sceneId);
        SceneDTO scene = sceneService.deactivate(sceneId);
        if (scene == null) {
            return ResultModel.notFound("Scene not found: " + sceneId);
        }
        return ResultModel.success(scene);
    }

    @PostMapping("/capabilities/list")
    public ResultModel<List<SceneCapabilityDTO>> listCapabilities(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] List capabilities called for scene: {}", sceneId);
        List<SceneCapabilityDTO> caps = sceneService.listCapabilities(sceneId);
        return ResultModel.success(caps);
    }

    @PostMapping("/capabilities/add")
    public ResultModel<SceneCapabilityDTO> addCapability(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        String capId = (String) request.get("capId");
        log.info("[SceneController] Add capability {} to scene {}", capId, sceneId);
        SceneCapabilityDTO cap = sceneService.addCapability(sceneId, capId);
        return ResultModel.success(cap);
    }

    @PostMapping("/capabilities/remove")
    public ResultModel<Boolean> removeCapability(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        String capId = (String) request.get("capId");
        log.info("[SceneController] Remove capability {} from scene {}", capId, sceneId);
        boolean result = sceneService.removeCapability(sceneId, capId);
        return ResultModel.success(result);
    }

    @PostMapping("/collaborative/list")
    public ResultModel<List<String>> listCollaborators(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] List collaborators for scene: {}", sceneId);
        List<String> users = sceneService.listCollaborativeUsers(sceneId);
        return ResultModel.success(users);
    }

    @PostMapping("/collaborative/add")
    public ResultModel<Boolean> addCollaborator(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        String userId = (String) request.get("userId");
        log.info("[SceneController] Add collaborator {} to scene {}", userId, sceneId);
        boolean result = sceneService.addCollaborativeUser(sceneId, userId);
        return ResultModel.success(result);
    }

    @PostMapping("/collaborative/remove")
    public ResultModel<Boolean> removeCollaborator(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        String userId = (String) request.get("userId");
        log.info("[SceneController] Remove collaborator {} from scene {}", userId, sceneId);
        boolean result = sceneService.removeCollaborativeUser(sceneId, userId);
        return ResultModel.success(result);
    }

    @PostMapping("/snapshot/create")
    public ResultModel<Map<String, Object>> createSnapshot(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        log.info("[SceneController] Create snapshot for scene: {}", sceneId);
        String snapshotId = sceneService.createSnapshot(sceneId);
        Map<String, Object> result = Map.of("snapshotId", snapshotId);
        return ResultModel.success(result);
    }

    @PostMapping("/snapshot/list")
    public ResultModel<List<Map<String, Object>>> listSnapshots(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        int pageNum = request.containsKey("pageNum") ? (int) request.get("pageNum") : 1;
        int pageSize = request.containsKey("pageSize") ? (int) request.get("pageSize") : 20;
        log.info("[SceneController] List snapshots for scene: {}", sceneId);
        List<Map<String, Object>> snapshots = sceneService.listSnapshots(sceneId, pageNum, pageSize);
        return ResultModel.success(snapshots);
    }

    @PostMapping("/snapshot/restore")
    public ResultModel<Boolean> restoreSnapshot(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        String snapshotId = (String) request.get("snapshotId");
        log.info("[SceneController] Restore snapshot {} for scene {}", snapshotId, sceneId);
        boolean result = sceneService.restoreSnapshot(sceneId, snapshotId);
        if (!result) {
            return ResultModel.error("Failed to restore snapshot");
        }
        return ResultModel.success(true);
    }

    @PostMapping("/logs")
    public ResultModel<List<Map<String, Object>>> getLogs(@RequestBody Map<String, Object> request) {
        String sceneId = (String) request.get("sceneId");
        int pageNum = request.containsKey("pageNum") ? (int) request.get("pageNum") : 1;
        int pageSize = request.containsKey("pageSize") ? (int) request.get("pageSize") : 20;
        log.info("[SceneController] Get logs for scene: {}", sceneId);
        List<Map<String, Object>> logs = sceneService.getLogs(sceneId, pageNum, pageSize);
        return ResultModel.success(logs);
    }
}