package net.ooder.skill.scene.dto.report;

import lombok.Data;

@Data
public class AiGenerateRequestDTO {
    private String userId;
    private String sceneGroupId;
    private String context;
    private String generateType;
}
