package net.ooder.skill.agent.model;

import lombok.Data;

@Data
public class TestCommandResult {
    private String command;
    private boolean success;
    private String output;
    private String error;
    private long duration;
    private long timestamp;
}
