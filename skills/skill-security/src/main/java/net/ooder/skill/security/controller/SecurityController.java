package net.ooder.skill.security.controller;

import net.ooder.skill.security.dto.*;
import net.ooder.skill.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/status")
    public ResponseEntity<SecurityStatus> getStatus() {
        return ResponseEntity.ok(securityService.getStatus());
    }

    @GetMapping("/stats")
    public ResponseEntity<SecurityStats> getStats() {
        return ResponseEntity.ok(securityService.getStats());
    }

    @GetMapping("/policies")
    public ResponseEntity<List<SecurityPolicy>> listPolicies() {
        return ResponseEntity.ok(securityService.listPolicies());
    }

    @PostMapping("/policies")
    public ResponseEntity<SecurityPolicy> createPolicy(@RequestBody SecurityPolicy policy) {
        return ResponseEntity.ok(securityService.createPolicy(policy));
    }

    @GetMapping("/policies/{policyId}")
    public ResponseEntity<SecurityPolicy> getPolicy(@PathVariable String policyId) {
        SecurityPolicy policy = securityService.getPolicy(policyId);
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(policy);
    }

    @PostMapping("/policies/{policyId}/enable")
    public ResponseEntity<Boolean> enablePolicy(@PathVariable String policyId) {
        return ResponseEntity.ok(securityService.enablePolicy(policyId));
    }

    @PostMapping("/policies/{policyId}/disable")
    public ResponseEntity<Boolean> disablePolicy(@PathVariable String policyId) {
        return ResponseEntity.ok(securityService.disablePolicy(policyId));
    }

    @DeleteMapping("/policies/{policyId}")
    public ResponseEntity<Boolean> deletePolicy(@PathVariable String policyId) {
        return ResponseEntity.ok(securityService.deletePolicy(policyId));
    }

    @GetMapping("/acls")
    public ResponseEntity<PageResult<AccessControl>> listAcls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(securityService.listAcls(page, size));
    }

    @PostMapping("/acls")
    public ResponseEntity<AccessControl> createAcl(@RequestBody AccessControl acl) {
        return ResponseEntity.ok(securityService.createAcl(acl));
    }

    @DeleteMapping("/acls/{aclId}")
    public ResponseEntity<Boolean> deleteAcl(@PathVariable String aclId) {
        return ResponseEntity.ok(securityService.deleteAcl(aclId));
    }

    @GetMapping("/threats")
    public ResponseEntity<PageResult<ThreatInfo>> listThreats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(securityService.listThreats(page, size));
    }

    @PostMapping("/threats/{threatId}/resolve")
    public ResponseEntity<Boolean> resolveThreat(@PathVariable String threatId) {
        return ResponseEntity.ok(securityService.resolveThreat(threatId));
    }

    @PostMapping("/scan")
    public ResponseEntity<Boolean> runSecurityScan() {
        return ResponseEntity.ok(securityService.runSecurityScan());
    }

    @PostMapping("/firewall/toggle")
    public ResponseEntity<Boolean> toggleFirewall() {
        return ResponseEntity.ok(securityService.toggleFirewall());
    }
}
