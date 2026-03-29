package net.ooder.skill.agent.cli.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "cli_command_status", name = "CLI命令状态")
public enum CliCommandStatus implements DictItem {
    PENDING("PENDING", "待执行", "命令待执行", "ri-time-line", 1),
    RUNNING("RUNNING", "执行中", "命令执行中", "ri-loader-line", 2),
    SUCCESS("SUCCESS", "执行成功", "命令执行成功", "ri-checkbox-circle-line", 3),
    FAILED("FAILED", "执行失败", "命令执行失败", "ri-close-circle-line", 4),
    TIMEOUT("TIMEOUT", "执行超时", "命令执行超时", "ri-timer-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CliCommandStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
