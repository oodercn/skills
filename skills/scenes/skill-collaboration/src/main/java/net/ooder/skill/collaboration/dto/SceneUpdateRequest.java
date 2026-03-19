package net.ooder.skill.collaboration.dto;

import lombok.Data;
import java.util.List;

@Data
public class SceneUpdateRequest {
    private String name;
    private String description;
    private List<String> skillIds;
}
