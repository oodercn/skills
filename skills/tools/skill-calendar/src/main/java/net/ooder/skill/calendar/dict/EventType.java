package net.ooder.skill.calendar.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "event_type", name = "日程类型")
public enum EventType implements DictItem {
    MEETING("MEETING", "会议", "会议日程", "ri-team-line", 1),
    TASK("TASK", "任务", "任务日程", "ri-task-line", 2),
    REMINDER("REMINDER", "提醒", "提醒日程", "ri-alarm-line", 3),
    APPOINTMENT("APPOINTMENT", "预约", "预约日程", "ri-calendar-check-line", 4),
    PERSONAL("PERSONAL", "个人", "个人日程", "ri-user-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    EventType(String code, String name, String description, String icon, int sort) {
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
