package net.ooder.skill.tenant.service;

import net.ooder.skill.tenant.entity.Tenant;
import net.ooder.skill.tenant.entity.TenantMember;
import java.util.List;
import java.util.Map;

public interface TenantService {

    Tenant createTenant(Tenant tenant, String ownerId);

    List<Tenant> listTenants(String userId);

    Tenant getTenant(String tenantId);

    Tenant updateTenant(String tenantId, Tenant updates);

    void deleteTenant(String tenantId);

    Tenant switchTenant(String tenantId, String userId);

    Map<String, Object> getQuota(String tenantId);

    Map<String, Object> updateQuota(String tenantId, Map<String, Object> quota);

    List<TenantMember> listMembers(String tenantId);

    TenantMember addMember(String tenantId, String userId, String role);

    void removeMember(String tenantId, String userId);
}
