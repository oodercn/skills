package net.ooder.skill.tenant.controller;

import net.ooder.skill.tenant.context.TenantContext;
import net.ooder.skill.tenant.entity.Tenant;
import net.ooder.skill.tenant.entity.TenantMember;
import net.ooder.skill.tenant.model.*;
import net.ooder.skill.tenant.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tenants")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantController {

    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResultModel<List<Tenant>> listTenants(@RequestParam(required = false) String userId) {
        log.info("[TenantController] listTenants: userId={}", userId);
        try {
            String effectiveUserId = userId != null ? userId : TenantContext.getUserId();
            List<Tenant> tenants = tenantService.listTenants(effectiveUserId);
            return ResultModel.success(tenants);
        } catch (Exception e) {
            log.error("[TenantController] listTenants failed", e);
            return ResultModel.error("获取租户列表失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ResultModel<Tenant> createTenant(@RequestBody TenantCreateRequest request) {
        log.info("[TenantController] createTenant: name={}", request.getName());
        try {
            Tenant tenant = Tenant.builder()
                    .name(request.getName())
                    .code(request.getCode())
                    .description(request.getDescription())
                    .planType(request.getPlanType() != null ? request.getPlanType() : "FREE")
                    .maxUsers(request.getMaxUsers())
                    .maxStorageMB(request.getMaxStorageMB())
                    .domain(request.getDomain())
                    .createdBy(TenantContext.getUserId())
                    .build();

            Tenant created = tenantService.createTenant(tenant, TenantContext.getUserId());
            return ResultModel.success(created, "租户创建成功");
        } catch (Exception e) {
            log.error("[TenantController] createTenant failed", e);
            return ResultModel.error("创建租户失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResultModel<Tenant> getTenant(@PathVariable String id) {
        log.info("[TenantController] getTenant: id={}", id);
        try {
            Tenant tenant = tenantService.getTenant(id);
            return ResultModel.success(tenant);
        } catch (Exception e) {
            log.error("[TenantController] getTenant failed", e);
            return ResultModel.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResultModel<Tenant> updateTenant(@PathVariable String id, @RequestBody TenantUpdateRequest request) {
        log.info("[TenantController] updateTenant: id={}", id);
        try {
            Tenant updates = new Tenant();
            updates.setName(request.getName());
            updates.setDescription(request.getDescription());
            updates.setLogoUrl(request.getLogoUrl());
            updates.setDomain(request.getDomain());
            updates.setMaxUsers(request.getMaxUsers());
            updates.setMaxStorageMB(request.getMaxStorageMB());
            updates.setPlanType(request.getPlanType());
            updates.setStatus(request.getStatus());

            Tenant updated = tenantService.updateTenant(id, updates);
            return ResultModel.success(updated, "更新成功");
        } catch (Exception e) {
            log.error("[TenantController] updateTenant failed", e);
            return ResultModel.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResultModel<Void> deleteTenant(@PathVariable String id) {
        log.info("[TenantController] deleteTenant: id={}", id);
        try {
            tenantService.deleteTenant(id);
            return ResultModel.success(null, "租户已停用");
        } catch (Exception e) {
            log.error("[TenantController] deleteTenant failed", e);
            return ResultModel.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/current")
    public ResultModel<Tenant> getCurrentTenant() {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null) {
                return ResultModel.success(null, "未选择租户");
            }
            Tenant tenant = tenantService.getTenant(tenantId);
            return ResultModel.success(tenant);
        } catch (Exception e) {
            return ResultModel.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/switch")
    public ResultModel<Tenant> switchTenant(@PathVariable String id) {
        log.info("[TenantController] switchTenant: id={}", id);
        try {
            Tenant tenant = tenantService.switchTenant(id, TenantContext.getUserId());
            return ResultModel.success(tenant, "已切换到租户: " + tenant.getName());
        } catch (Exception e) {
            log.error("[TenantController] switchTenant failed", e);
            return ResultModel.error("切换失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/quota")
    public ResultModel<Map<String, Object>> getQuota(@PathVariable String id) {
        try {
            Map<String, Object> quota = tenantService.getQuota(id);
            return ResultModel.success(quota);
        } catch (Exception e) {
            return ResultModel.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/quota")
    public ResultModel<Map<String, Object>> updateQuota(@PathVariable String id, @RequestBody TenantQuotaDTO dto) {
        try {
            Map<String, Object> result = tenantService.updateQuota(id, dto.getQuota());
            return ResultModel.success(result, "配额已更新");
        } catch (Exception e) {
            return ResultModel.error("更新配额失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/members")
    public ResultModel<List<TenantMember>> listMembers(@PathVariable String id) {
        try {
            List<TenantMember> members = tenantService.listMembers(id);
            return ResultModel.success(members);
        } catch (Exception e) {
            return ResultModel.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/members")
    public ResultModel<TenantMember> addMember(@PathVariable String id, @RequestBody TenantMemberDTO dto) {
        log.info("[TenantController] addMember: tenant={}, user={}, role={}", id, dto.getUserId(), dto.getRole());
        try {
            String role = dto.getRole() != null ? dto.getRole() : "MEMBER";
            TenantMember member = tenantService.addMember(id, dto.getUserId(), role.toUpperCase());
            return ResultModel.success(member, "成员添加成功");
        } catch (Exception e) {
            log.error("[TenantController] addMember failed", e);
            return ResultModel.error("添加成员失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResultModel<Void> removeMember(@PathVariable String id, @PathVariable String userId) {
        log.info("[TenantController] removeMember: tenant={}, user={}", id, userId);
        try {
            tenantService.removeMember(id, userId);
            return ResultModel.success(null, "成员已移除");
        } catch (Exception e) {
            return ResultModel.error("移除成员失败: " + e.getMessage());
        }
    }
}
