package net.ooder.skill.security.controller;

import net.ooder.skill.security.dto.PolicyDTO;
import net.ooder.skill.security.dto.SecurityConfigDTO;
import net.ooder.skill.security.dto.SecurityStatsDTO;
import net.ooder.skill.security.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/security")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    private final Map<String, SecurityConfigDTO> configStore = new ConcurrentHashMap<>();
    private final Map<String, PolicyDTO> policyStore = new ConcurrentHashMap<>();

    public SecurityController() {
        SecurityConfigDTO defaultConfig = new SecurityConfigDTO();
        defaultConfig.setEnabled(true);
        defaultConfig.setEncryption("AES256");
        defaultConfig.setLastUpdated(System.currentTimeMillis());
        configStore.put("default", defaultConfig);
    }

    @GetMapping("/config")
    public ResultModel<SecurityConfigDTO> getConfig() {
        log.info("[SecurityController] Get security config");
        SecurityConfigDTO config = configStore.get("default");
        if (config == null) {
            config = new SecurityConfigDTO();
            config.setEnabled(true);
            config.setEncryption("AES256");
        }
        return ResultModel.success(config);
    }

    @PutMapping("/config")
    public ResultModel<SecurityConfigDTO> updateConfig(@RequestBody SecurityConfigDTO config) {
        log.info("[SecurityController] Update security config: {}", config);
        config.setLastUpdated(System.currentTimeMillis());
        configStore.put("default", config);
        return ResultModel.success(config);
    }

    @GetMapping("/policies")
    public ResultModel<List<PolicyDTO>> getPolicies() {
        log.info("[SecurityController] Get all policies");
        return ResultModel.success(new ArrayList<>(policyStore.values()));
    }

    @PostMapping("/policies")
    public ResultModel<PolicyDTO> createPolicy(@RequestBody PolicyDTO policy) {
        log.info("[SecurityController] Create policy: {}", policy.getName());
        if (policy.getPolicyId() == null || policy.getPolicyId().isEmpty()) {
            policy.setPolicyId(UUID.randomUUID().toString());
        }
        policy.setCreatedAt(System.currentTimeMillis());
        policy.setUpdatedAt(System.currentTimeMillis());
        if (policy.getStatus() == null) {
            policy.setStatus("active");
        }
        policyStore.put(policy.getPolicyId(), policy);
        return ResultModel.success(policy);
    }

    @GetMapping("/policies/{policyId}")
    public ResultModel<PolicyDTO> getPolicy(@PathVariable String policyId) {
        log.info("[SecurityController] Get policy: {}", policyId);
        PolicyDTO policy = policyStore.get(policyId);
        if (policy == null) {
            return ResultModel.notFound("Policy not found: " + policyId);
        }
        return ResultModel.success(policy);
    }

    @PutMapping("/policies/{policyId}")
    public ResultModel<PolicyDTO> updatePolicy(@PathVariable String policyId, @RequestBody PolicyDTO policy) {
        log.info("[SecurityController] Update policy: {}", policyId);
        PolicyDTO existing = policyStore.get(policyId);
        if (existing == null) {
            return ResultModel.notFound("Policy not found: " + policyId);
        }
        policy.setPolicyId(policyId);
        policy.setCreatedAt(existing.getCreatedAt());
        policy.setUpdatedAt(System.currentTimeMillis());
        policyStore.put(policyId, policy);
        return ResultModel.success(policy);
    }

    @DeleteMapping("/policies/{policyId}")
    public ResultModel<Boolean> deletePolicy(@PathVariable String policyId) {
        log.info("[SecurityController] Delete policy: {}", policyId);
        PolicyDTO removed = policyStore.remove(policyId);
        if (removed == null) {
            return ResultModel.notFound("Policy not found: " + policyId);
        }
        return ResultModel.success(true);
    }

    @GetMapping("/stats")
    public ResultModel<SecurityStatsDTO> getStats() {
        log.info("[SecurityController] Get security stats");
        SecurityStatsDTO stats = new SecurityStatsDTO();
        stats.setTotalPolicies(policyStore.size());
        stats.setActivePolicies(policyStore.values().stream().filter(p -> "active".equals(p.getStatus())).count());
        stats.setEnabled(configStore.get("default") != null && configStore.get("default").isEnabled());
        stats.setTimestamp(System.currentTimeMillis());
        return ResultModel.success(stats);
    }
}