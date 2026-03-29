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
public class ParsedCommandDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originalText;
    private String platform;
    private String command;
    private List<String> args;
    private Map<String, Object> options;
    private Double confidence;
    private List<String> alternatives;
    private String intent;
    private Map<String, Object> entities;
}
