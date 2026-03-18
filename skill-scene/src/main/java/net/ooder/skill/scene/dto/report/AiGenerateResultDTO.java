package net.ooder.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class AiGenerateResultDTO {
    private String userId;
    private List<String> workItems;
    private List<String> planItems;
    private String issuesSuggestion;
}
