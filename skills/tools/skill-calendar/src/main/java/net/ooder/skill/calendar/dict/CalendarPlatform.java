package net.ooder.skill.calendar.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "calendar_platform", name = "日历平台")
public enum CalendarPlatform implements DictItem {
    DINGTALK("DINGTALK", "钉钉日历", "钉钉日程", "ri-message-2-line", 1),
    FEISHU("FEISHU", "飞书日历", "飞书日程", "ri-message-3-line", 2),
    WECOM("WECOM", "企业微信日历", "企业微信日程", "ri-wechat-line", 3),
    LOCAL("LOCAL", "本地日历", "本地日程", "ri-calendar-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CalendarPlatform(String code, String name, String description, String icon, int sort) {
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
