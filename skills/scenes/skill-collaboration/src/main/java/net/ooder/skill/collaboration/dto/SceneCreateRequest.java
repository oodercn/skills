package net.ooder.skill.collaboration.dto;

import lombok.Data;
import java.util.List;

@Data
public class SceneCreateRequest {
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private List<String> skillIds;
    private List<String> memberIds;
}
