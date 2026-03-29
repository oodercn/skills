package net.ooder.skill.agent.cli.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CliSkillDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private String name;
    private String description;
    private String platform;
    private String category;
    private List<String> commands;
    private Map<String, Object> parameters;
    private List<String> examples;
    private String icon;
}
