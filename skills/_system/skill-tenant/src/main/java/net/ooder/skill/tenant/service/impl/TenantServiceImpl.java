package net.ooder.skill.tenant.service.impl;

import net.ooder.skill.tenant.context.TenantContext;
import net.ooder.skill.tenant.entity.Tenant;
import net.ooder.skill.tenant.entity.TenantMember;
import net.ooder.skill.tenant.repository.TenantMemberRepository;
import net.ooder.skill.tenant.repository.TenantRepository;
import net.ooder.skill.tenant.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TenantServiceImpl implements TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    private final TenantRepository tenantRepository;
    private final TenantMemberRepository memberRepository;

    public TenantServiceImpl(TenantRepository tenantRepository, TenantMemberRepository memberRepository) {
        this.tenantRepository = tenantRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant, String ownerId) {
        log.info("[TenantService] Creating tenant: name={}, owner={}", tenant.getName(), ownerId);
        tenant.setCreatedBy(ownerId);
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant saved = tenantRepository.save(tenant);

        TenantMember owner = new TenantMember();
        owner.setTenantId(saved.getId());
        owner.setUserId(ownerId);
        owner.setRole(TenantMember.Role.OWNER.getCode());
        owner.setInvitedBy("system");
        memberRepository.save(owner);

        log.info("[TenantService] Created tenant: id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }

    @Override
    public List<Tenant> listTenants(String userId) {
        log.debug("[TenantService] Listing tenants for user: {}", userId);
        List<TenantMember> memberships = memberRepository.findByUserId(userId);
        Set<String> activeTenantIds = new HashSet<>();
        for (TenantMember m : memberships) {
            if ("ACTIVE".equals(m.getStatus())) {
                activeTenantIds.add(m.getTenantId());
            }
        }

        if (activeTenantIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tenant> result = tenantRepository.findAllById(activeTenantIds);
        result.removeIf(t -> !"ACTIVE".equals(t.getStatus()));
        return result;
    }

    @Override
    public Tenant getTenant(String tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("租户不存在: " + tenantId));
    }

    @Override
    @Transactional
    public Tenant updateTenant(String tenantId, Tenant updates) {
        log.info("[TenantService] Updating tenant: {}", tenantId);
        Tenant existing = getTenant(tenantId);

        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getLogoUrl() != null) existing.setLogoUrl(updates.getLogoUrl());
        if (updates.getDomain() != null) existing.setDomain(updates.getDomain());
        if (updates.getMaxUsers() != null) existing.setMaxUsers(updates.getMaxUsers());
        if (updates.getMaxStorageMB() != null) existing.setMaxStorageMB(updates.getMaxStorageMB());
        if (updates.getPlanType() != null) existing.setPlanType(updates.getPlanType());

        return tenantRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTenant(String tenantId) {
        log.info("[TenantService] Deleting tenant: {}", tenantId);
        Tenant tenant = getTenant(tenantId);
        tenant.setStatus("DELETED");
        tenantRepository.save(tenant);
        memberRepository.findByTenantIdAndStatus(tenantId, "ACTIVE")
                .forEach(m -> { m.setStatus("LEFT"); memberRepository.save(m); });
    }

    @Override
    public Tenant switchTenant(String tenantId, String userId) {
        log.info("[TenantService] Switching to tenant: {}, user={}", tenantId, userId);

        var membershipOpt = memberRepository.findByTenantIdAndUserId(tenantId, userId);
        if (membershipOpt.isEmpty() || !"ACTIVE".equals(membershipOpt.get().getStatus())) {
            throw new RuntimeException("无权访问该租户: " + tenantId);
        }

        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        return getTenant(tenantId);
    }

    @Override
    public Map<String, Object> getQuota(String tenantId) {
        Tenant tenant = getTenant(tenantId);
        long activeMembers = memberRepository.countByTenantIdAndStatus(tenantId, "ACTIVE");

        Map<String, Object> quota = new LinkedHashMap<>();
        quota.put("maxUsers", tenant.getMaxUsers() != null ? tenant.getMaxUsers() : 50);
        quota.put("currentUsers", activeMembers);
        quota.put("maxStorageMB", tenant.getMaxStorageMB() != null ? tenant.getMaxStorageMB() : 10240L);
        quota.put("planType", tenant.getPlanType() != null ? tenant.getPlanType() : "FREE");
        quota.put("status", tenant.getStatus());
        quota.put("activeMembers", activeMembers);

        return quota;
    }

    @Override
    @Transactional
    public Map<String, Object> updateQuota(String tenantId, Map<String, Object> quota) {
        log.info("[TenantService] Updating quota for tenant: {}", tenantId);
        Tenant tenant = getTenant(tenantId);
        if (quota.containsKey("maxUsers")) tenant.setMaxUsers(((Number) quota.get("maxUsers")).intValue());
        if (quota.containsKey("maxStorageMB")) tenant.setMaxStorageMB(((Number) quota.get("maxStorageMB")).longValue());
        if (quota.containsKey("planType")) tenant.setPlanType((String) quota.get("planType"));
        tenantRepository.save(tenant);
        return getQuota(tenantId);
    }

    @Override
    public List<TenantMember> listMembers(String tenantId) {
        return memberRepository.findByTenantIdAndStatus(tenantId, "ACTIVE");
    }

    @Override
    @Transactional
    public TenantMember addMember(String tenantId, String userId, String role) {
        log.info("[TenantService] Adding member: tenant={}, user={}, role={}", tenantId, userId, role);
        getTenant(tenantId);

        if (memberRepository.existsByTenantIdAndUserId(tenantId, userId)) {
            throw new RuntimeException("该用户已是租户成员: " + userId);
        }

        TenantMember member = new TenantMember();
        member.setTenantId(tenantId);
        member.setUserId(userId);
        member.setRole(role);
        member.setInvitedBy(TenantContext.getUserId());

        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public void removeMember(String tenantId, String userId) {
        log.info("[TenantService] Removing member: tenant={}, user={}", tenantId, userId);
        var opt = memberRepository.findByTenantIdAndUserId(tenantId, userId);
        if (opt.isPresent()) {
            TenantMember m = opt.get();
            m.setStatus("REMOVED");
            memberRepository.save(m);
        }
    }
}
