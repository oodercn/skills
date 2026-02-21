package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.dto.*;
import net.ooder.skill.security.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final Map<String, SecurityPolicy> policies = new ConcurrentHashMap<>();
    private final Map<String, AccessControl> acls = new ConcurrentHashMap<>();
    private final Map<String, ThreatInfo> threats = new ConcurrentHashMap<>();
    private final AtomicBoolean firewallEnabled = new AtomicBoolean(true);
    private final AtomicLong totalScans = new AtomicLong(0);
    private final AtomicLong threatsDetected = new AtomicLong(0);
    private final AtomicLong threatsResolved = new AtomicLong(0);
    private final AtomicLong blockedConnections = new AtomicLong(0);
    private final AtomicLong auditLogs = new AtomicLong(0);

    public SecurityServiceImpl() {
        initDefaultPolicies();
    }

    private void initDefaultPolicies() {
        SecurityPolicy defaultPolicy = new SecurityPolicy();
        defaultPolicy.setPolicyId("policy-default-001");
        defaultPolicy.setPolicyName("Default Security Policy");
        defaultPolicy.setPolicyType("general");
        defaultPolicy.setDescription("Default security policy for basic protection");
        defaultPolicy.setStatus("enabled");
        defaultPolicy.setPriority(100);
        defaultPolicy.setAction("allow");
        policies.put(defaultPolicy.getPolicyId(), defaultPolicy);
    }

    @Override
    public SecurityStatus getStatus() {
        SecurityStatus status = new SecurityStatus();
        status.setActivePolicies((int) policies.values().stream().filter(p -> "enabled".equals(p.getStatus())).count());
        status.setTotalPolicies(policies.size());
        status.setRecentAlerts((int) threats.values().stream().filter(t -> "open".equals(t.getStatus())).count());
        status.setBlockedAttempts(blockedConnections.get());
        status.setThreatScore(calculateThreatScore());
        status.setFirewallEnabled(firewallEnabled.get());
        status.setAuditEnabled(true);
        return status;
    }

    private double calculateThreatScore() {
        long openThreats = threats.values().stream().filter(t -> "open".equals(t.getStatus())).count();
        return Math.min(100.0, openThreats * 10.0);
    }

    @Override
    public SecurityStats getStats() {
        SecurityStats stats = new SecurityStats();
        stats.setTotalScans(totalScans.get());
        stats.setThreatsDetected(threatsDetected.get());
        stats.setThreatsResolved(threatsResolved.get());
        stats.setBlockedConnections(blockedConnections.get());
        stats.setAuditLogs(auditLogs.get());
        stats.setAverageScanTime(150.0);
        return stats;
    }

    @Override
    public List<SecurityPolicy> listPolicies() {
        return new ArrayList<>(policies.values());
    }

    @Override
    public SecurityPolicy getPolicy(String policyId) {
        return policies.get(policyId);
    }

    @Override
    public SecurityPolicy createPolicy(SecurityPolicy policy) {
        if (policy.getPolicyId() == null || policy.getPolicyId().isEmpty()) {
            policy.setPolicyId("policy-" + UUID.randomUUID().toString().substring(0, 8));
        }
        policy.setCreatedAt(System.currentTimeMillis());
        policy.setUpdatedAt(System.currentTimeMillis());
        policies.put(policy.getPolicyId(), policy);
        auditLogs.incrementAndGet();
        return policy;
    }

    @Override
    public boolean enablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setStatus("enabled");
            policy.setUpdatedAt(System.currentTimeMillis());
            auditLogs.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean disablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setStatus("disabled");
            policy.setUpdatedAt(System.currentTimeMillis());
            auditLogs.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePolicy(String policyId) {
        if (policies.remove(policyId) != null) {
            auditLogs.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public PageResult<AccessControl> listAcls(int page, int size) {
        List<AccessControl> allAcls = new ArrayList<>(acls.values());
        int start = page * size;
        int end = Math.min(start + size, allAcls.size());
        List<AccessControl> pageItems = start < allAcls.size() ? allAcls.subList(start, end) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allAcls.size());
    }

    @Override
    public AccessControl createAcl(AccessControl acl) {
        if (acl.getAclId() == null || acl.getAclId().isEmpty()) {
            acl.setAclId("acl-" + UUID.randomUUID().toString().substring(0, 8));
        }
        acl.setGrantedAt(System.currentTimeMillis());
        acls.put(acl.getAclId(), acl);
        auditLogs.incrementAndGet();
        return acl;
    }

    @Override
    public boolean deleteAcl(String aclId) {
        if (acls.remove(aclId) != null) {
            auditLogs.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public PageResult<ThreatInfo> listThreats(int page, int size) {
        List<ThreatInfo> allThreats = new ArrayList<>(threats.values());
        int start = page * size;
        int end = Math.min(start + size, allThreats.size());
        List<ThreatInfo> pageItems = start < allThreats.size() ? allThreats.subList(start, end) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allThreats.size());
    }

    @Override
    public boolean resolveThreat(String threatId) {
        ThreatInfo threat = threats.get(threatId);
        if (threat != null) {
            threat.setStatus("resolved");
            threat.setResolvedAt(System.currentTimeMillis());
            threatsResolved.incrementAndGet();
            auditLogs.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean runSecurityScan() {
        totalScans.incrementAndGet();
        auditLogs.incrementAndGet();
        return true;
    }

    @Override
    public boolean toggleFirewall() {
        boolean newValue = !firewallEnabled.get();
        firewallEnabled.set(newValue);
        auditLogs.incrementAndGet();
        return newValue;
    }
}
