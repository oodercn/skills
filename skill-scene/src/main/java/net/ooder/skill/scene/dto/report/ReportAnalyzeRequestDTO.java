package net.ooder.skill.scene.dto.report;

import lombok.Data;

@Data
public class ReportAnalyzeRequestDTO {
    private String sceneGroupId;
    private String date;
    private String analyzeType;
}
