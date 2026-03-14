package net.ooder.skill.scene.dto.report;

import lombok.Data;
import java.util.List;

@Data
public class EmailFetchRequestDTO {
    private String userId;
    private String sceneGroupId;
    private Long startTime;
    private Long endTime;
    private Integer limit;
}
