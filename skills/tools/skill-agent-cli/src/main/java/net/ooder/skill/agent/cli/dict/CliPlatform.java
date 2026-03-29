package net.ooder.skill.agent.cli.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "cli_platform", name = "CLI平台")
public enum CliPlatform implements DictItem {
    DINGTALK("DINGTALK", "钉钉CLI", "钉钉命令行工具", "ri-message-2-line", 1),
    FEISHU("FEISHU", "飞书CLI", "飞书命令行工具", "ri-message-3-line", 2),
    WECOM("WECOM", "企业微信CLI", "企业微信命令行工具", "ri-wechat-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CliPlatform(String code, String name, String description, String icon, int sort) {
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
