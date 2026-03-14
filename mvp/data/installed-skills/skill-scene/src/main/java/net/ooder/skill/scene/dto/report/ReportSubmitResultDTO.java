package net.ooder.skill.scene.dto.report;

import lombok.Data;

@Data
public class ReportSubmitResultDTO {
    private String reportId;
    private String status;
    private String message;
}
