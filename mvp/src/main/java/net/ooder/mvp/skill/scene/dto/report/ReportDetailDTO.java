package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class ReportDetailDTO {
    private String reportId;
    private String sceneGroupId;
    private String userId;
    private String userName;
    private String date;
    private List<String> workItems;
    private List<String> planItems;
    private String issues;
    private List<ReportSubmitRequestDTO.AttachmentDTO> attachments;
    private Long submitTime;
    private String status;
}
