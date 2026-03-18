package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportNotifyRequestDTO {
    private String sceneGroupId;
    private List<String> userIds;
    private String title;
    private String content;
    private String notifyType;
}
