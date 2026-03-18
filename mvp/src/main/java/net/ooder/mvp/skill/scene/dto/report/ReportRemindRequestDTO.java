package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportRemindRequestDTO {
    private String sceneGroupId;
    private List<String> userIds;
    private String message;
}
