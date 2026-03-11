package net.ooder.skill.security.provider;

import net.ooder.skill.security.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityProviderImpl {
    
    private boolean initialized = false;
    private boolean running = false;
    private boolean firewallEnabled = true;
    
    private final Map<String, SecurityPolicy> policies = new HashMap<>();
    private final Map<String, AccessControl> acls = new HashMap<>();
    private final Map<String, ThreatInfo> threats = new HashMap<>();
    
    public String getProviderName() {
        return "skill-security";
    }
    
    public String getVersion() {
        return "2.3";
    }
    
    public void initialize(Map<String, Object> context) {
        this.initialized = true;
    }
    
    public void start() {
        this.running = true;
    }
    
    public void stop() {
        this.running = false;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public SecurityStatus getStatus() {
        SecurityStatus status = new SecurityStatus();
        status.setStatus("SECURE");
        status.setFirewallEnabled(firewallEnabled);
        status.setActivePolicies(policies.size());
        status.setActiveThreats((int) threats.values().stream()
                .filter(t -> "active".equals(t.getStatus()))
                .count());
        status.setLastScanTime(System.currentTimeMillis());
        return status;
    }
    
    public SecurityStats getStats() {
        SecurityStats stats = new SecurityStats();
        stats.setTotalPolicies(policies.size());
        stats.setActivePolicies((int) policies.values().stream()
                .filter(p -> p.isEnabled())
                .count());
        stats.setTotalAcls(acls.size());
        stats.setTotalThreats(threats.size());
        stats.setResolvedThreats((int) threats.values().stream()
                .filter(t -> "resolved".equals(t.getStatus()))
                .count());
        return stats;
    }
    
    public List<SecurityPolicy> listPolicies() {
        return new ArrayList<>(policies.values());
    }
    
    public SecurityPolicy getPolicy(String policyId) {
        return policies.get(policyId);
    }
    
    public SecurityPolicy createPolicy(SecurityPolicy policy) {
        if (policy.getPolicyId() == null) {
            policy.setPolicyId(UUID.randomUUID().toString());
        }
        policies.put(policy.getPolicyId(), policy);
        return policy;
    }
    
    public boolean updatePolicy(SecurityPolicy policy) {
        if (policies.containsKey(policy.getPolicyId())) {
            policies.put(policy.getPolicyId(), policy);
            return true;
        }
        return false;
    }
    
    public boolean deletePolicy(String policyId) {
        return policies.remove(policyId) != null;
    }
    
    public boolean enablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setEnabled(true);
            return true;
        }
        return false;
    }
    
    public boolean disablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setEnabled(false);
            return true;
        }
        return false;
    }
    
    public List<AccessControl> listAcls() {
        return new ArrayList<>(acls.values());
    }
    
    public AccessControl createAcl(AccessControl acl) {
        if (acl.getAclId() == null) {
            acl.setAclId(UUID.randomUUID().toString());
        }
        acls.put(acl.getAclId(), acl);
        return acl;
    }
    
    public boolean deleteAcl(String aclId) {
        return acls.remove(aclId) != null;
    }
    
    public boolean checkPermission(String userId, String resource, String action) {
        return acls.values().stream()
                .anyMatch(acl -> acl.getUserId().equals(userId) 
                        && acl.getResource().equals(resource) 
                        && acl.getAction().equals(action));
    }
    
    public List<ThreatInfo> listThreats(String status) {
        if (status == null || status.isEmpty()) {
            return new ArrayList<>(threats.values());
        }
        List<ThreatInfo> result = new ArrayList<>();
        for (ThreatInfo t : threats.values()) {
            if (status.equals(t.getStatus())) {
                result.add(t);
            }
        }
        return result;
    }
    
    public ThreatInfo getThreat(String threatId) {
        return threats.get(threatId);
    }
    
    public boolean resolveThreat(String threatId, String resolution) {
        ThreatInfo threat = threats.get(threatId);
        if (threat != null) {
            threat.setStatus("resolved");
            threat.setResolution(resolution);
            threat.setResolvedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    public boolean enableFirewall() {
        firewallEnabled = true;
        return true;
    }
    
    public boolean disableFirewall() {
        firewallEnabled = false;
        return true;
    }
    
    public FirewallStatus getFirewallStatus() {
        FirewallStatus status = new FirewallStatus();
        status.setEnabled(firewallEnabled);
        status.setActiveRules(0);
        status.setBlockedConnections(0);
        status.setAllowedConnections(0);
        status.setLastUpdated(System.currentTimeMillis());
        return status;
    }
    
    public boolean isFirewallEnabled() {
        return firewallEnabled;
    }
}
