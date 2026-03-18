package net.ooder.mvp.skill.scene.dto.report;

import lombok.Data;

@Data
public class ReportHistoryDTO {
    private String reportId;
    private String date;
    private String status;
    private String summary;
}
