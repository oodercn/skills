package net.ooder.skill.security.lifecycle;

import net.ooder.skill.common.test.SkillLifecycleTestBase;
import net.ooder.skill.security.api.SecurityApi;
import net.ooder.skill.security.api.SecurityApiImpl;
import net.ooder.skill.security.dto.*;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

/**
 * Security Skill Lifecycle Test
 * 
 * <p>Comprehensive lifecycle testing for Security Skill implementation.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 */
@DisplayName("Security Skill Lifecycle Tests")
class SecuritySkillLifecycleTest extends SkillLifecycleTestBase<SecurityApi> {

    private boolean destroyed = false;

    @Override
    protected void setUpSkill() {
        skill = new SecurityApiImpl();
        destroyed = false;
    }

    @Override
    protected void tearDownSkill() {
        skill = null;
        destroyed = false;
    }

    @Override
    protected String getSkillId() {
        return skill.getApiName();
    }

    @Override
    protected String getSkillName() {
        return "Security Skill";
    }

    @Override
    protected String getSkillVersion() {
        return skill.getVersion();
    }

    @Override
    protected List<String> getCapabilities() {
        return Arrays.asList(
                "policy.manage",
                "acl.manage",
                "threat.detect",
                "firewall.manage",
                "audit.log"
        );
    }

    @Override
    protected void initializeSkill(Map<String, Object> config) {
        skill.initialize(config);
    }

    @Override
    protected void startSkill() {
        skill.start();
    }

    @Override
    protected void stopSkill() {
        skill.stop();
    }

    @Override
    protected void destroySkill() {
        destroyed = true;
    }

    @Override
    protected boolean isInitialized() {
        return skill.isInitialized();
    }

    @Override
    protected boolean isRunning() {
        return skill.isRunning();
    }

    @Override
    protected Object invokeCapability(String capability, Map<String, Object> params) {
        switch (capability) {
            case "policy.manage":
                SecurityPolicy policy = new SecurityPolicy();
                policy.setPolicyName("Test Policy");
                return skill.createPolicy(policy);
            case "acl.manage":
                AccessControl acl = new AccessControl();
                acl.setUserId("user-001");
                return skill.createAcl(acl);
            case "threat.detect":
                ThreatInfo threat = new ThreatInfo();
                threat.setType("intrusion");
                return skill.reportThreat(threat);
            case "firewall.manage":
                return skill.getFirewallStatus();
            case "audit.log":
                return skill.getAuditStats();
            default:
                return skill.getStatus();
        }
    }

    @Override
    protected Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", isRunning() ? "UP" : "DOWN");
        health.put("initialized", isInitialized());
        health.put("running", isRunning());
        health.put("uptime", System.currentTimeMillis());
        health.put("securityStatus", skill.getStatus());
        return health;
    }

    // ==================== Security-Specific Lifecycle Tests ====================

    @org.junit.jupiter.api.Nested
    @DisplayName("Security-Specific Lifecycle Tests")
    class SecuritySpecificTests {

        @org.junit.jupiter.api.Test
        @DisplayName("Should maintain security state across lifecycle")
        void shouldMaintainSecurityStateAcrossLifecycle() {
            initializeSkill(config);
            startSkill();
            
            SecurityPolicy policy = new SecurityPolicy();
            policy.setPolicyName("Persistent Policy");
            SecurityPolicy created = skill.createPolicy(policy);
            
            stopSkill();
            startSkill();
            
            SecurityPolicy retrieved = skill.getPolicy(created.getPolicyId());
            assertNotNull(retrieved, "Policy should persist across restart");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle security events during lifecycle")
        void shouldHandleSecurityEventsDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            ThreatInfo threat = new ThreatInfo();
            threat.setType("malware");
            threat.setSeverity("high");
            ThreatInfo reported = skill.reportThreat(threat);
            
            SecurityStatus status = skill.getStatus();
            assertTrue(status.getActiveThreats() > 0, "Should have active threats");
            
            skill.resolveThreat(reported.getThreatId(), "Resolved");
            
            status = skill.getStatus();
            lifecycleEvents.add("Security event handled: " + reported.getThreatId());
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should manage firewall state during lifecycle")
        void shouldManageFirewallStateDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            skill.enableFirewall();
            assertTrue(skill.getFirewallStatus().isEnabled());
            
            FirewallRule rule = new FirewallRule();
            rule.setName("Test Rule");
            rule.setAction("deny");
            FirewallRule added = skill.addFirewallRule(rule);
            
            stopSkill();
            startSkill();
            
            lifecycleEvents.add("Firewall state managed across lifecycle");
        }
    }
}
