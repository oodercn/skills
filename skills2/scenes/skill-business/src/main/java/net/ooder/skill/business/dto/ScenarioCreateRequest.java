package net.ooder.skill.business.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ScenarioCreateRequest {
    private String name;
    private String description;
    private String type;
    private List<String> steps;
    private Map<String, Object> config;
    private String creatorId;
}
