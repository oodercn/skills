package net.ooder.skills.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstallResultWithDependencies implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String status;
    private String error;
    private String message;
    private String skillId;
    private long duration;
    private List<String> installedDependencies;
    private List<String> existingDependencies;
    private List<String> failedDependencies;

    public InstallResultWithDependencies() {
        this.installedDependencies = new ArrayList<String>();
        this.existingDependencies = new ArrayList<String>();
        this.failedDependencies = new ArrayList<String>();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<String> getInstalledDependencies() {
        return installedDependencies;
    }

    public void setInstalledDependencies(List<String> installedDependencies) {
        this.installedDependencies = installedDependencies;
    }

    public List<String> getExistingDependencies() {
        return existingDependencies;
    }

    public void setExistingDependencies(List<String> existingDependencies) {
        this.existingDependencies = existingDependencies;
    }

    public List<String> getFailedDependencies() {
        return failedDependencies;
    }

    public void setFailedDependencies(List<String> failedDependencies) {
        this.failedDependencies = failedDependencies;
    }
}
