package net.ooder.skill.scene.dto.dailyreport;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class AnalyzeRequestDTO {
    
    @NotBlank(message = "鍦烘櫙缁処D涓嶈兘涓虹┖")
    private String sceneGroupId;
    
    private List<Map<String, Object>> reports;
    
    private String analyzeType;
    
    public AnalyzeRequestDTO() {}
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public List<Map<String, Object>> getReports() {
        return reports;
    }
    
    public void setReports(List<Map<String, Object>> reports) {
        this.reports = reports;
    }
    
    public String getAnalyzeType() {
        return analyzeType;
    }
    
    public void setAnalyzeType(String analyzeType) {
        this.analyzeType = analyzeType;
    }
}
