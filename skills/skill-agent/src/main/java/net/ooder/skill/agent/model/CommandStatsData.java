package net.ooder.skill.agent.model;

import lombok.Data;

@Data
public class CommandStatsData {
    private int totalCommands;
    private int successfulCommands;
    private int failedCommands;
}
