package net.ooder.skill.platform.bind.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "platform_type", name = "平台类型")
public enum PlatformType implements DictItem {
    DINGTALK("DINGTALK", "钉钉", "钉钉平台", "ri-message-2-line", 1),
    FEISHU("FEISHU", "飞书", "飞书平台", "ri-message-3-line", 2),
    WECOM("WECOM", "企业微信", "企业微信平台", "ri-wechat-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    PlatformType(String code, String name, String description, String icon, int sort) {
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
