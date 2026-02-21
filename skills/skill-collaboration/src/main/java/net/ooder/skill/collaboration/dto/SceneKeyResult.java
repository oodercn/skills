package net.ooder.skill.collaboration.dto;

import lombok.Data;

@Data
public class SceneKeyResult {
    private String sceneId;
    private String sceneKey;
    private Long createTime;
    private Long expireTime;
}
