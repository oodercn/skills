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
public class CliCommandDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commandId;
    private String platform;
    private String command;
    private List<String> args;
    private Map<String, Object> options;
    private String naturalLanguage;
    private String status;
    private String output;
    private String error;
    private Integer exitCode;
    private Long duration;
    private String createTime;
    private String startTime;
    private String endTime;
    private String userId;
}
