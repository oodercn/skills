package net.ooder.skill.capability.controller;

import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/discovery")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryController.class);

    @PostMapping("/local")
    public ResultModel<DiscoveryResult> discoverLocal() {
        log.info("[discoverLocal] Discovering capabilities from local");
        
        DiscoveryResult result = new DiscoveryResult();
        result.setMethod("LOCAL");
        result.setCapabilities(new ArrayList<>());
        result.setTotal(0);
        
        return ResultModel.success(result);
    }

    @PostMapping("/github")
    public ResultModel<DiscoveryResult> discoverFromGitHub(@RequestBody Map<String, Object> config) {
        log.info("[discoverFromGitHub] Discovering capabilities from GitHub: {}", config.get("repoUrl"));
        
        DiscoveryResult result = new DiscoveryResult();
        result.setMethod("GITHUB");
        result.setRepoUrl((String) config.get("repoUrl"));
        result.setCapabilities(new ArrayList<>());
        result.setTotal(0);
        
        return ResultModel.success(result);
    }

    @PostMapping("/gitee")
    public ResultModel<DiscoveryResult> discoverFromGitee(@RequestBody Map<String, Object> config) {
        log.info("[discoverFromGitee] Discovering capabilities from Gitee: {}", config.get("repoUrl"));
        
        DiscoveryResult result = new DiscoveryResult();
        result.setMethod("GITEE");
        result.setRepoUrl((String) config.get("repoUrl"));
        result.setCapabilities(new ArrayList<>());
        result.setTotal(0);
        
        return ResultModel.success(result);
    }

    @PostMapping("/install")
    public ResultModel<InstallResult> installCapability(@RequestBody Map<String, Object> request) {
        log.info("[installCapability] Installing capability: {}", request.get("capabilityId"));
        
        InstallResult result = new InstallResult();
        result.setSuccess(true);
        result.setMessage("Capability installed successfully");
        result.setCapabilityId((String) request.get("capabilityId"));
        
        return ResultModel.success(result);
    }

    @GetMapping("/statistics")
    public ResultModel<Statistics> getStatistics() {
        log.info("[getStatistics] Getting discovery statistics");
        
        Statistics stats = new Statistics();
        stats.setTotalCapabilities(0);
        stats.setInstalledCapabilities(0);
        stats.setActiveCapabilities(0);
        
        return ResultModel.success(stats);
    }

    public static class DiscoveryResult {
        private String method;
        private String repoUrl;
        private List<Object> capabilities;
        private int total;

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getRepoUrl() { return repoUrl; }
        public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
        public List<Object> getCapabilities() { return capabilities; }
        public void setCapabilities(List<Object> capabilities) { this.capabilities = capabilities; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }

    public static class InstallResult {
        private boolean success;
        private String message;
        private String capabilityId;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    }

    public static class Statistics {
        private int totalCapabilities;
        private int installedCapabilities;
        private int activeCapabilities;

        public int getTotalCapabilities() { return totalCapabilities; }
        public void setTotalCapabilities(int totalCapabilities) { this.totalCapabilities = totalCapabilities; }
        public int getInstalledCapabilities() { return installedCapabilities; }
        public void setInstalledCapabilities(int installedCapabilities) { this.installedCapabilities = installedCapabilities; }
        public int getActiveCapabilities() { return activeCapabilities; }
        public void setActiveCapabilities(int activeCapabilities) { this.activeCapabilities = activeCapabilities; }
    }
}
