package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.*;
import net.ooder.skill.scene.service.SceneGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scene-groups")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SceneGroupController extends BaseController {

    private final SceneGroupService sceneGroupService;

    @Autowired
    public SceneGroupController(SceneGroupService sceneGroupService) {
        this.sceneGroupService = sceneGroupService;
    }

    @PostMapping
    public ResultModel<SceneGroupDTO> create(@RequestBody CreateSceneGroupRequest request) {
        long startTime = System.currentTimeMillis();
        logRequestStart("create", request);

        try {
            SceneGroupDTO result = sceneGroupService.create(request.getTemplateId(), request.getConfig());
            logRequestEnd("create", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("create", e);
            return ResultModel.error(500, "创建场景组失败: " + e.getMessage());
        }
    }

    @PutMapping("/{sceneGroupId}")
    public ResultModel<SceneGroupDTO> update(
            @PathVariable String sceneGroupId,
            @RequestBody UpdateSceneGroupRequest request) {
        long startTime = System.currentTimeMillis();
        logRequestStart("update", sceneGroupId);

        try {
            SceneGroupDTO result = sceneGroupService.update(sceneGroupId, request.getConfig());
            if (result == null) {
                logRequestEnd("update", "Not found", System.currentTimeMillis() - startTime);
                return ResultModel.notFound("场景组不存在");
            }
            logRequestEnd("update", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("update", e);
            return ResultModel.error(500, "更新场景组失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ResultModel<PageResult<SceneGroupDTO>> listAll(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String templateId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listAll", "pageNum=" + pageNum);

        try {
            PageResult<SceneGroupDTO> result = templateId != null && !templateId.isEmpty()
                ? sceneGroupService.listByTemplate(templateId, pageNum, pageSize)
                : sceneGroupService.listAll(pageNum, pageSize);
            logRequestEnd("listAll", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listAll", e);
            return ResultModel.error(500, "获取场景组列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sceneGroupId}")
    public ResultModel<SceneGroupDTO> get(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("get", sceneGroupId);

        try {
            SceneGroupDTO result = sceneGroupService.get(sceneGroupId);
            if (result == null) {
                logRequestEnd("get", "Not found", System.currentTimeMillis() - startTime);
                return ResultModel.notFound("场景组不存在");
            }
            logRequestEnd("get", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("get", e);
            return ResultModel.error(500, "获取场景组失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{sceneGroupId}")
    public ResultModel<Boolean> destroy(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("destroy", sceneGroupId);

        try {
            boolean result = sceneGroupService.destroy(sceneGroupId);
            logRequestEnd("destroy", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("destroy", e);
            return ResultModel.error(500, "销毁场景组失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/activate")
    public ResultModel<Boolean> activate(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("activate", sceneGroupId);

        try {
            boolean result = sceneGroupService.activate(sceneGroupId);
            logRequestEnd("activate", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("activate", e);
            return ResultModel.error(500, "激活场景组失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/deactivate")
    public ResultModel<Boolean> deactivate(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("deactivate", sceneGroupId);

        try {
            boolean result = sceneGroupService.deactivate(sceneGroupId);
            logRequestEnd("deactivate", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("deactivate", e);
            return ResultModel.error(500, "停用场景组失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/participants")
    public ResultModel<Boolean> join(@PathVariable String sceneGroupId, @RequestBody SceneParticipantDTO participant) {
        long startTime = System.currentTimeMillis();
        logRequestStart("join", sceneGroupId);

        try {
            boolean result = sceneGroupService.join(sceneGroupId, participant);
            logRequestEnd("join", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("join", e);
            return ResultModel.error(500, "加入场景失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sceneGroupId}/participants")
    public ResultModel<PageResult<SceneParticipantDTO>> listParticipants(
            @PathVariable String sceneGroupId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listParticipants", sceneGroupId);

        try {
            PageResult<SceneParticipantDTO> result = sceneGroupService.listParticipants(sceneGroupId, pageNum, pageSize);
            logRequestEnd("listParticipants", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listParticipants", e);
            return ResultModel.error(500, "获取参与者列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sceneGroupId}/participants/{participantId}")
    public ResultModel<SceneParticipantDTO> getParticipant(
            @PathVariable String sceneGroupId,
            @PathVariable String participantId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("getParticipant", participantId);

        try {
            SceneParticipantDTO result = sceneGroupService.getParticipant(sceneGroupId, participantId);
            if (result == null) {
                return ResultModel.notFound("参与者不存在");
            }
            logRequestEnd("getParticipant", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("getParticipant", e);
            return ResultModel.error(500, "获取参与者失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{sceneGroupId}/participants/{participantId}")
    public ResultModel<Boolean> leave(
            @PathVariable String sceneGroupId,
            @PathVariable String participantId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("leave", participantId);

        try {
            boolean result = sceneGroupService.leave(sceneGroupId, participantId);
            logRequestEnd("leave", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("leave", e);
            return ResultModel.error(500, "离开场景失败: " + e.getMessage());
        }
    }

    @PutMapping("/{sceneGroupId}/participants/{participantId}/role")
    public ResultModel<Boolean> changeRole(
            @PathVariable String sceneGroupId,
            @PathVariable String participantId,
            @RequestBody ChangeRoleRequest request) {
        long startTime = System.currentTimeMillis();
        logRequestStart("changeRole", participantId);

        try {
            boolean result = sceneGroupService.changeRole(sceneGroupId, participantId, request.getNewRole());
            logRequestEnd("changeRole", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("changeRole", e);
            return ResultModel.error(500, "变更角色失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/capabilities")
    public ResultModel<Boolean> bindCapability(@PathVariable String sceneGroupId, @RequestBody CapabilityBindingDTO binding) {
        long startTime = System.currentTimeMillis();
        logRequestStart("bindCapability", sceneGroupId);

        try {
            boolean result = sceneGroupService.bindCapability(sceneGroupId, binding);
            logRequestEnd("bindCapability", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("bindCapability", e);
            return ResultModel.error(500, "绑定能力失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sceneGroupId}/capabilities")
    public ResultModel<PageResult<CapabilityBindingDTO>> listCapabilityBindings(
            @PathVariable String sceneGroupId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listCapabilityBindings", sceneGroupId);

        try {
            PageResult<CapabilityBindingDTO> result = sceneGroupService.listCapabilityBindings(sceneGroupId, pageNum, pageSize);
            logRequestEnd("listCapabilityBindings", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listCapabilityBindings", e);
            return ResultModel.error(500, "获取能力绑定列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{sceneGroupId}/capabilities/{bindingId}")
    public ResultModel<Boolean> unbindCapability(
            @PathVariable String sceneGroupId,
            @PathVariable String bindingId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("unbindCapability", bindingId);

        try {
            boolean result = sceneGroupService.unbindCapability(sceneGroupId, bindingId);
            logRequestEnd("unbindCapability", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("unbindCapability", e);
            return ResultModel.error(500, "解绑能力失败: " + e.getMessage());
        }
    }

    @PutMapping("/{sceneGroupId}/capabilities/{bindingId}")
    public ResultModel<Boolean> updateCapabilityBinding(
            @PathVariable String sceneGroupId,
            @PathVariable String bindingId,
            @RequestBody CapabilityBindingDTO binding) {
        long startTime = System.currentTimeMillis();
        logRequestStart("updateCapabilityBinding", bindingId);

        try {
            boolean result = sceneGroupService.updateCapabilityBinding(sceneGroupId, bindingId, binding);
            logRequestEnd("updateCapabilityBinding", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("updateCapabilityBinding", e);
            return ResultModel.error(500, "更新能力绑定失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sceneGroupId}/snapshots")
    public ResultModel<List<SceneSnapshotDTO>> listSnapshots(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listSnapshots", sceneGroupId);

        try {
            List<SceneSnapshotDTO> result = sceneGroupService.listSnapshots(sceneGroupId);
            logRequestEnd("listSnapshots", result != null ? result.size() : 0, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listSnapshots", e);
            return ResultModel.error(500, "获取快照列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/snapshots")
    public ResultModel<SceneSnapshotDTO> createSnapshot(@PathVariable String sceneGroupId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("createSnapshot", sceneGroupId);

        try {
            SceneSnapshotDTO result = sceneGroupService.createSnapshot(sceneGroupId);
            logRequestEnd("createSnapshot", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("createSnapshot", e);
            return ResultModel.error(500, "创建快照失败: " + e.getMessage());
        }
    }

    @PostMapping("/{sceneGroupId}/snapshots/{snapshotId}/restore")
    public ResultModel<Boolean> restoreSnapshot(
            @PathVariable String sceneGroupId,
            @PathVariable String snapshotId,
            @RequestBody SceneSnapshotDTO snapshot) {
        long startTime = System.currentTimeMillis();
        logRequestStart("restoreSnapshot", snapshotId);

        try {
            boolean result = sceneGroupService.restoreSnapshot(sceneGroupId, snapshot);
            logRequestEnd("restoreSnapshot", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("restoreSnapshot", e);
            return ResultModel.error(500, "恢复快照失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{sceneGroupId}/snapshots/{snapshotId}")
    public ResultModel<Boolean> deleteSnapshot(
            @PathVariable String sceneGroupId,
            @PathVariable String snapshotId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("deleteSnapshot", snapshotId);

        try {
            boolean result = sceneGroupService.deleteSnapshot(sceneGroupId, snapshotId);
            logRequestEnd("deleteSnapshot", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("deleteSnapshot", e);
            return ResultModel.error(500, "删除快照失败: " + e.getMessage());
        }
    }

    @GetMapping("/my/created")
    public ResultModel<PageResult<SceneGroupDTO>> listMyCreated(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listMyCreated", "pageNum=" + pageNum);

        try {
            String currentUserId = "current-user";
            PageResult<SceneGroupDTO> result = sceneGroupService.listByCreator(currentUserId, pageNum, pageSize);
            logRequestEnd("listMyCreated", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listMyCreated", e);
            return ResultModel.error(500, "获取我创建的场景失败: " + e.getMessage());
        }
    }

    @GetMapping("/my/participated")
    public ResultModel<PageResult<SceneGroupDTO>> listMyParticipated(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listMyParticipated", "pageNum=" + pageNum);

        try {
            String currentUserId = "current-user";
            PageResult<SceneGroupDTO> result = sceneGroupService.listByParticipant(currentUserId, pageNum, pageSize);
            logRequestEnd("listMyParticipated", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listMyParticipated", e);
            return ResultModel.error(500, "获取我参与的场景失败: " + e.getMessage());
        }
    }

    public static class CreateSceneGroupRequest {
        private String templateId;
        private SceneGroupConfigDTO config;

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public SceneGroupConfigDTO getConfig() { return config; }
        public void setConfig(SceneGroupConfigDTO config) { this.config = config; }
    }

    public static class UpdateSceneGroupRequest {
        private SceneGroupConfigDTO config;

        public SceneGroupConfigDTO getConfig() { return config; }
        public void setConfig(SceneGroupConfigDTO config) { this.config = config; }
    }

    public static class ChangeRoleRequest {
        private String newRole;

        public String getNewRole() { return newRole; }
        public void setNewRole(String newRole) { this.newRole = newRole; }
    }

    @PostMapping("/{sceneGroupId}/knowledge-bases")
    public ResultModel<Boolean> bindKnowledgeBase(
            @PathVariable String sceneGroupId,
            @RequestBody KnowledgeBindingDTO binding) {
        long startTime = System.currentTimeMillis();
        logRequestStart("bindKnowledgeBase", sceneGroupId);

        try {
            boolean result = sceneGroupService.bindKnowledgeBase(sceneGroupId, binding);
            logRequestEnd("bindKnowledgeBase", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("bindKnowledgeBase", e);
            return ResultModel.error(500, "绑定知识库失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{sceneGroupId}/knowledge-bases/{kbId}")
    public ResultModel<Boolean> unbindKnowledgeBase(
            @PathVariable String sceneGroupId,
            @PathVariable String kbId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("unbindKnowledgeBase", kbId);

        try {
            boolean result = sceneGroupService.unbindKnowledgeBase(sceneGroupId, kbId);
            logRequestEnd("unbindKnowledgeBase", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("unbindKnowledgeBase", e);
            return ResultModel.error(500, "解绑知识库失败: " + e.getMessage());
        }
    }
}
