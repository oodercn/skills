package net.ooder.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportSubmitRequestDTO {
    private String sceneGroupId;
    private String userId;
    private String userName;
    private List<String> workItems;
    private List<String> planItems;
    private String issues;
    private List<AttachmentDTO> attachments;

    @Data
    public static class AttachmentDTO {
        private String name;
        private Long size;
        private String type;
        private String url;
    }
}
