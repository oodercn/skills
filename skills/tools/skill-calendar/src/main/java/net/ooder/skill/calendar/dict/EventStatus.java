package net.ooder.skill.calendar.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "event_status", name = "日程状态")
public enum EventStatus implements DictItem {
    TENTATIVE("TENTATIVE", "待定", "暂定日程", "ri-question-line", 1),
    CONFIRMED("CONFIRMED", "已确认", "已确认日程", "ri-check-line", 2),
    CANCELLED("CANCELLED", "已取消", "已取消日程", "ri-close-line", 3),
    COMPLETED("COMPLETED", "已完成", "已完成日程", "ri-checkbox-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    EventStatus(String code, String name, String description, String icon, int sort) {
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
