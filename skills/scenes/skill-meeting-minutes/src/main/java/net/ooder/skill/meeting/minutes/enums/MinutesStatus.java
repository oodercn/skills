package net.ooder.skill.meeting.minutes.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "minutes_status", name = "会议纪要状态", description = "会议纪要的生命周期状态")
public enum MinutesStatus implements DictItem {
    
    DRAFT("DRAFT", "草稿", "会议纪要草稿状态，内容待确认", "ri-draft-line", 1),
    COMPLETED("COMPLETED", "完成", "会议纪要已完成，可导出归档", "ri-checkbox-circle-line", 2),
    ARCHIVED("ARCHIVED", "已归档", "会议纪要已归档到知识库", "ri-archive-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    MinutesStatus(String code, String name, String description, String icon, int sort) {
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
