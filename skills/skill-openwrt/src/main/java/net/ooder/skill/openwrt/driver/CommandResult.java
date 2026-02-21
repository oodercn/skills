package net.ooder.skill.openwrt.driver;

import lombok.Data;

@Data
public class CommandResult {
    private int exitCode;
    private String stdout;
    private String stderr;
    private long duration;
}
