package net.ooder.skill.scene.dto.discovery;

import java.util.List;

public class DiscoveryResultDTO {
    
    private String method;
    
    private String repoUrl;
    
    private String branch;
    
    private List<CapabilityDTO> capabilities;
    
    private Long scanTime;
    
    private List<RepositoryDTO> repositories;
    
    private String errorMessage;
    
    private boolean fromCache;

    public DiscoveryResultDTO() {}

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<CapabilityDTO> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityDTO> capabilities) {
        this.capabilities = capabilities;
    }

    public Long getScanTime() {
        return scanTime;
    }

    public void setScanTime(Long scanTime) {
        this.scanTime = scanTime;
    }

    public List<RepositoryDTO> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryDTO> repositories) {
        this.repositories = repositories;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
}
