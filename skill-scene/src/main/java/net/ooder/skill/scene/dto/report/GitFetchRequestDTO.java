package net.ooder.skill.scene.dto.report;

import lombok.Data;

@Data
public class GitFetchRequestDTO {
    private String userId;
    private String sceneGroupId;
    private String repoUrl;
    private String branch;
    private Long startTime;
    private Long endTime;
    private Integer limit;
}
