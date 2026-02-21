package net.ooder.skill.security.service;

import net.ooder.skill.security.dto.*;

import java.util.List;

public interface SecurityService {
    SecurityStatus getStatus();
    SecurityStats getStats();
    List<SecurityPolicy> listPolicies();
    SecurityPolicy getPolicy(String policyId);
    SecurityPolicy createPolicy(SecurityPolicy policy);
    boolean enablePolicy(String policyId);
    boolean disablePolicy(String policyId);
    boolean deletePolicy(String policyId);
    PageResult<AccessControl> listAcls(int page, int size);
    AccessControl createAcl(AccessControl acl);
    boolean deleteAcl(String aclId);
    PageResult<ThreatInfo> listThreats(int page, int size);
    boolean resolveThreat(String threatId);
    boolean runSecurityScan();
    boolean toggleFirewall();
}
