package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.dto.*;
import net.ooder.skill.security.service.SecurityService;

import net.ooder.sdk.api.security.SecurityApi;
import net.ooder.sdk.api.security.SecurityApi.SecurityPolicy;
import net.ooder.sdk.api.security.SecurityApi.AuditLog;
import net.ooder.sdk.api.security.SecurityApi.SecurityEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private SecurityApi securityApi;
    
    private final Map<String, AccessControl> acls = new ConcurrentHashMap<>();
    private final Map<String, ThreatInfo> threats = new ConcurrentHashMap<>();
    private final AtomicBoolean firewallEnabled = new AtomicBoolean(true);
    private final AtomicLong totalScans = new AtomicLong(0);
    private final AtomicLong threatsDetected = new AtomicLong(0);
    private final AtomicLong threatsResolved = new AtomicLong(0);
    private final AtomicLong blockedConnections = new AtomicLong(0);
    private SecurityConfig config = new SecurityConfig();

    public SecurityServiceImpl() {
        initDefaultPolicies();
    }

    private void initDefaultPolicies() {
        SecurityPolicy defaultPolicy = new SecurityPolicy();
        defaultPolicy.setPolicyId("policy-default-001");
        defaultPolicy.setName("Default Security Policy");
        defaultPolicy.setDescription("Default security policy for basic protection");
        defaultPolicy.setEnabled(true);
        defaultPolicy.setResource("*");
        defaultPolicy.setAction("*");
        defaultPolicy.setAllowedRoles(Arrays.asList("admin", "user"));
        securityApi.createPolicy(defaultPolicy);
    }

    @Override
    public SecurityStatus getStatus() {
        List<SecurityPolicy> policies = securityApi.getPolicies();
        
        SecurityStatus status = new SecurityStatus();
        status.setActivePolicies((int) policies.stream().filter(SecurityPolicy::isEnabled).count());
        status.setTotalPolicies(policies.size());
        status.setRecentAlerts((int) threats.values().stream().filter(t -> "open".equals(t.getStatus())).count());
        status.setBlockedAttempts((int) blockedConnections.get());
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
        stats.setResolvedThreats(threatsResolved.get());
        stats.setBlockedConnections(blockedConnections.get());
        
        AuditLog auditLog = securityApi.getAuditLog(null, 100);
        stats.setAuditLogs(auditLog != null ? auditLog.getTotalCount() : 0);
        stats.setAverageScanTime(150.0);
        return stats;
    }
    
    @Override
    public SecurityConfig getConfig() {
        return config;
    }
    
    @Override
    public void saveConfig(SecurityConfig newConfig) {
        this.config = newConfig;
        logSecurityEvent("CONFIG_UPDATE", "security-config", "update");
    }

    @Override
    public List<SecurityPolicy> listPolicies() {
        return securityApi.getPolicies();
    }

    @Override
    public SecurityPolicy getPolicy(String policyId) {
        return securityApi.getPolicies().stream()
            .filter(p -> policyId.equals(p.getPolicyId()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public SecurityPolicy createPolicy(SecurityPolicy policy) {
        if (policy.getPolicyId() == null || policy.getPolicyId().isEmpty()) {
            policy.setPolicyId("policy-" + UUID.randomUUID().toString().substring(0, 8));
        }
        securityApi.createPolicy(policy);
        logSecurityEvent("POLICY_CREATE", policy.getPolicyId(), "create");
        return policy;
    }

    @Override
    public boolean enablePolicy(String policyId) {
        SecurityPolicy policy = getPolicy(policyId);
        if (policy != null) {
            policy.setEnabled(true);
            securityApi.updatePolicy(policyId, policy);
            logSecurityEvent("POLICY_ENABLE", policyId, "enable");
            return true;
        }
        return false;
    }

    @Override
    public boolean disablePolicy(String policyId) {
        SecurityPolicy policy = getPolicy(policyId);
        if (policy != null) {
            policy.setEnabled(false);
            securityApi.updatePolicy(policyId, policy);
            logSecurityEvent("POLICY_DISABLE", policyId, "disable");
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePolicy(String policyId) {
        securityApi.deletePolicy(policyId);
        logSecurityEvent("POLICY_DELETE", policyId, "delete");
        return true;
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
        logSecurityEvent("ACL_CREATE", acl.getAclId(), "create");
        return acl;
    }

    @Override
    public boolean deleteAcl(String aclId) {
        if (acls.remove(aclId) != null) {
            logSecurityEvent("ACL_DELETE", aclId, "delete");
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
            logSecurityEvent("THREAT_RESOLVE", threatId, "resolve");
            return true;
        }
        return false;
    }

    @Override
    public boolean runSecurityScan() {
        totalScans.incrementAndGet();
        logSecurityEvent("SECURITY_SCAN", "system", "scan");
        return true;
    }

    @Override
    public boolean toggleFirewall() {
        boolean newValue = !firewallEnabled.get();
        firewallEnabled.set(newValue);
        logSecurityEvent("FIREWALL_TOGGLE", "firewall", newValue ? "enable" : "disable");
        return newValue;
    }
    
    private void logSecurityEvent(String eventType, String resource, String action) {
        SecurityEvent event = new SecurityEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setResource(resource);
        event.setAction(action);
        event.setResult("success");
        event.setTimestamp(Instant.now());
        securityApi.logSecurityEvent(event);
    }
}
