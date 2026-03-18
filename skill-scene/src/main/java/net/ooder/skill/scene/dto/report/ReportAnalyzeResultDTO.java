package net.ooder.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportAnalyzeResultDTO {
    private String sceneGroupId;
    private String analysisSummary;
    private List<String> keyFindings;
    private List<String> suggestions;
}
