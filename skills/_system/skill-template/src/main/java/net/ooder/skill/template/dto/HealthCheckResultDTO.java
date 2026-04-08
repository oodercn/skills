package net.ooder.skill.template.dto;

import java.util.List;

public class HealthCheckResultDTO {
    
    private String templateId;
    private boolean allHealthy;
    private int totalCount;
    private int healthyCount;
    private int missingCount;
    private int errorCount;
    private List<DependencyStatusDTO> dependencies;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public boolean isAllHealthy() { return allHealthy; }
    public void setAllHealthy(boolean allHealthy) { this.allHealthy = allHealthy; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getHealthyCount() { return healthyCount; }
    public void setHealthyCount(int healthyCount) { this.healthyCount = healthyCount; }
    public int getMissingCount() { return missingCount; }
    public void setMissingCount(int missingCount) { this.missingCount = missingCount; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public List<DependencyStatusDTO> getDependencies() { return dependencies; }
    public void setDependencies(List<DependencyStatusDTO> dependencies) { this.dependencies = dependencies; }
}
