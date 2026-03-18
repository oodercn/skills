package net.ooder.skill.collaboration.dto;

import lombok.Data;
import java.util.List;

@Data
public class CollaborationScene {
    private String sceneId;
    private String name;
    private String description;
    private String ownerId;
    private List<SceneMember> members;
    private List<String> skillIds;
    private SceneStatus status;
    private String sceneKey;
    private Long createTime;
    private Long updateTime;
}
