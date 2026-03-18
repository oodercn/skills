package net.ooder.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportAggregateResultDTO {
    private String sceneGroupId;
    private Integer totalCount;
    private Integer submittedCount;
    private List<String> allWorkItems;
    private String summary;
}
