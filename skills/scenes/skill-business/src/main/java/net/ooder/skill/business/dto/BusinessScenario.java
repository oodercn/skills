package net.ooder.skill.business.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BusinessScenario {
    private String scenarioId;
    private String name;
    private String description;
    private String type;
    private String status;
    private List<String> steps;
    private Map<String, Object> config;
    private String creatorId;
    private Long createTime;
    private Long updateTime;
}
