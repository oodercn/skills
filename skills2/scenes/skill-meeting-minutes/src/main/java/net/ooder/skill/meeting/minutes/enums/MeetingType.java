package net.ooder.skill.meeting.minutes.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "meeting_type", name = "会议类型", description = "会议的类型分类")
public enum MeetingType implements DictItem {
    
    REGULAR("REGULAR", "例会", "定期召开的常规会议", "ri-calendar-line", 1),
    PROJECT("PROJECT", "项目会", "项目相关会议", "ri-folder-line", 2),
    DECISION("DECISION", "决策会", "重要决策会议", "ri-checkbox-line", 3),
    BRAINSTORM("BRAINSTORM", "头脑风暴", "创意讨论会议", "ri-lightbulb-line", 4),
    REVIEW("REVIEW", "评审会", "方案/代码评审会议", "ri-eye-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    MeetingType(String code, String name, String description, String icon, int sort) {
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
