package net.ooder.skill.security.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityProviderImpl implements SecurityProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    private boolean firewallEnabled = true;
    
    private final Map<String, SecurityPolicy> policies = new HashMap<>();
    private final Map<String, AccessControl> acls = new HashMap<>();
    private final Map<String, ThreatInfo> threats = new HashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-security";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }
    
    @Override
    public void start() {
        this.running = true;
    }
    
    @Override
    public void stop() {
        this.running = false;
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
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
    
    @Override
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
        if (policy.getPolicyId() == null) {
            policy.setPolicyId(UUID.randomUUID().toString());
        }
        policies.put(policy.getPolicyId(), policy);
        return policy;
    }
    
    @Override
    public boolean updatePolicy(SecurityPolicy policy) {
        if (policies.containsKey(policy.getPolicyId())) {
            policies.put(policy.getPolicyId(), policy);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean deletePolicy(String policyId) {
        return policies.remove(policyId) != null;
    }
    
    @Override
    public boolean enablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setEnabled(true);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean disablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy != null) {
            policy.setEnabled(false);
            return true;
        }
        return false;
    }
    
    @Override
    public PageResult<AccessControl> listAcls(int page, int size) {
        List<AccessControl> allAcls = new ArrayList<>(acls.values());
        int start = page * size;
        int end = Math.min(start + size, allAcls.size());
        List<AccessControl> pageAcls = start < allAcls.size() 
                ? allAcls.subList(start, end) 
                : new ArrayList<>();
        return new PageResult<>(pageAcls, allAcls.size(), page, size);
    }
    
    @Override
    public AccessControl createAcl(AccessControl acl) {
        if (acl.getAclId() == null) {
            acl.setAclId(UUID.randomUUID().toString());
        }
        acls.put(acl.getAclId(), acl);
        return acl;
    }
    
    @Override
    public boolean deleteAcl(String aclId) {
        return acls.remove(aclId) != null;
    }
    
    @Override
    public boolean checkPermission(String userId, String resource, String action) {
        return acls.values().stream()
                .anyMatch(acl -> acl.getUserId().equals(userId) 
                        && acl.getResource().equals(resource) 
                        && acl.getAction().equals(action));
    }
    
    @Override
    public PageResult<ThreatInfo> listThreats(int page, int size) {
        List<ThreatInfo> allThreats = new ArrayList<>(threats.values());
        int start = page * size;
        int end = Math.min(start + size, allThreats.size());
        List<ThreatInfo> pageThreats = start < allThreats.size() 
                ? allThreats.subList(start, end) 
                : new ArrayList<>();
        return new PageResult<>(pageThreats, allThreats.size(), page, size);
    }
    
    @Override
    public ThreatInfo getThreat(String threatId) {
        return threats.get(threatId);
    }
    
    @Override
    public boolean resolveThreat(String threatId) {
        ThreatInfo threat = threats.get(threatId);
        if (threat != null) {
            threat.setStatus("resolved");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean runSecurityScan() {
        return true;
    }
    
    @Override
    public boolean toggleFirewall() {
        firewallEnabled = !firewallEnabled;
        return firewallEnabled;
    }
    
    @Override
    public boolean isFirewallEnabled() {
        return firewallEnabled;
    }
}
