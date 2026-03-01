package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.SceneTemplateDTO;
import net.ooder.skill.scene.dto.scene.CapabilityDefDTO;
import net.ooder.skill.scene.dto.scene.RoleDefinitionDTO;
import net.ooder.skill.scene.service.SceneTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scene-templates")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SceneTemplateController extends BaseController {

    private final SceneTemplateService templateService;

    @Autowired
    public SceneTemplateController(SceneTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResultModel<SceneTemplateDTO> create(@RequestBody SceneTemplateDTO template) {
        long startTime = System.currentTimeMillis();
        logRequestStart("create", template);

        try {
            SceneTemplateDTO result = templateService.create(template);
            logRequestEnd("create", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("create", e);
            return ResultModel.error(500, "创建场景模板失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ResultModel<PageResult<SceneTemplateDTO>> listAll(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category) {
        long startTime = System.currentTimeMillis();
        logRequestStart("listAll", "pageNum=" + pageNum + ", pageSize=" + pageSize);

        try {
            PageResult<SceneTemplateDTO> result = category != null && !category.isEmpty()
                ? templateService.listByCategory(category, pageNum, pageSize)
                : templateService.listAll(pageNum, pageSize);
            logRequestEnd("listAll", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("listAll", e);
            return ResultModel.error(500, "获取场景模板列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{templateId}")
    public ResultModel<SceneTemplateDTO> get(@PathVariable String templateId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("get", templateId);

        try {
            SceneTemplateDTO result = templateService.get(templateId);
            if (result == null) {
                logRequestEnd("get", "Not found", System.currentTimeMillis() - startTime);
                return ResultModel.notFound("场景模板不存在");
            }
            logRequestEnd("get", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("get", e);
            return ResultModel.error(500, "获取场景模板失败: " + e.getMessage());
        }
    }

    @PutMapping("/{templateId}")
    public ResultModel<SceneTemplateDTO> update(@PathVariable String templateId, @RequestBody SceneTemplateDTO template) {
        long startTime = System.currentTimeMillis();
        logRequestStart("update", templateId);

        try {
            template.setTemplateId(templateId);
            SceneTemplateDTO existing = templateService.get(templateId);
            if (existing == null) {
                return ResultModel.notFound("场景模板不存在");
            }
            template.setUpdateTime(System.currentTimeMillis());
            logRequestEnd("update", template, System.currentTimeMillis() - startTime);
            return ResultModel.success(template);
        } catch (Exception e) {
            logRequestError("update", e);
            return ResultModel.error(500, "更新场景模板失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{templateId}")
    public ResultModel<Boolean> delete(@PathVariable String templateId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("delete", templateId);

        try {
            boolean result = templateService.delete(templateId);
            logRequestEnd("delete", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("delete", e);
            return ResultModel.error(500, "删除场景模板失败: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/activate")
    public ResultModel<Boolean> activate(@PathVariable String templateId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("activate", templateId);

        try {
            boolean result = templateService.activate(templateId);
            logRequestEnd("activate", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("activate", e);
            return ResultModel.error(500, "激活场景模板失败: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/deactivate")
    public ResultModel<Boolean> deactivate(@PathVariable String templateId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("deactivate", templateId);

        try {
            boolean result = templateService.deactivate(templateId);
            logRequestEnd("deactivate", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("deactivate", e);
            return ResultModel.error(500, "停用场景模板失败: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/capabilities")
    public ResultModel<Boolean> addCapability(@PathVariable String templateId, @RequestBody CapabilityDefDTO capability) {
        long startTime = System.currentTimeMillis();
        logRequestStart("addCapability", templateId);

        try {
            boolean result = templateService.addCapability(templateId, capability);
            logRequestEnd("addCapability", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("addCapability", e);
            return ResultModel.error(500, "添加能力失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{templateId}/capabilities/{capId}")
    public ResultModel<Boolean> removeCapability(@PathVariable String templateId, @PathVariable String capId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("removeCapability", templateId + "/" + capId);

        try {
            boolean result = templateService.removeCapability(templateId, capId);
            logRequestEnd("removeCapability", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("removeCapability", e);
            return ResultModel.error(500, "移除能力失败: " + e.getMessage());
        }
    }

    @PostMapping("/{templateId}/roles")
    public ResultModel<Boolean> addRole(@PathVariable String templateId, @RequestBody RoleDefinitionDTO role) {
        long startTime = System.currentTimeMillis();
        logRequestStart("addRole", templateId);

        try {
            boolean result = templateService.addRole(templateId, role);
            logRequestEnd("addRole", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("addRole", e);
            return ResultModel.error(500, "添加角色失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{templateId}/roles/{roleId}")
    public ResultModel<Boolean> removeRole(@PathVariable String templateId, @PathVariable String roleId) {
        long startTime = System.currentTimeMillis();
        logRequestStart("removeRole", templateId + "/" + roleId);

        try {
            boolean result = templateService.removeRole(templateId, roleId);
            logRequestEnd("removeRole", result, System.currentTimeMillis() - startTime);
            return ResultModel.success(result);
        } catch (Exception e) {
            logRequestError("removeRole", e);
            return ResultModel.error(500, "移除角色失败: " + e.getMessage());
        }
    }
}
