package net.ooder.skill.scene.dto.discovery;

import java.util.ArrayList;
import java.util.List;

public class InstallResultDTO {
    
    private String skillId;
    private String status;
    private String message;
    private Long installTime;
    private List<CapabilityDTO> capabilities;
    private List<String> installedDependencies;
    private List<String> existingDependencies;
    private List<String> failedDependencies;

    public InstallResultDTO() {
        this.installedDependencies = new ArrayList<>();
        this.existingDependencies = new ArrayList<>();
        this.failedDependencies = new ArrayList<>();
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(Long installTime) {
        this.installTime = installTime;
    }

    public List<CapabilityDTO> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityDTO> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getInstalledDependencies() {
        return installedDependencies;
    }

    public void setInstalledDependencies(List<String> installedDependencies) {
        this.installedDependencies = installedDependencies != null ? installedDependencies : new ArrayList<>();
    }

    public List<String> getExistingDependencies() {
        return existingDependencies;
    }

    public void setExistingDependencies(List<String> existingDependencies) {
        this.existingDependencies = existingDependencies != null ? existingDependencies : new ArrayList<>();
    }

    public List<String> getFailedDependencies() {
        return failedDependencies;
    }

    public void setFailedDependencies(List<String> failedDependencies) {
        this.failedDependencies = failedDependencies != null ? failedDependencies : new ArrayList<>();
    }
}
