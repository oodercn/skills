package net.ooder.skill.scenes.dto;

import net.ooder.skill.dict.dto.Dict;
import net.ooder.skill.dict.dto.DictItem;

@Dict(code = "participant_type", name = "参与者类型", description = "场景参与者的类型")
public enum ParticipantType implements DictItem {
    
    USER("USER", "用户", "普通用户参与者", "ri-user-line", 1),
    AGENT("AGENT", "智能体", "AI智能体参与者", "ri-robot-line", 2),
    SUPER_AGENT("SUPER_AGENT", "超级智能体", "超级AI智能体参与者", "ri-robot-2-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ParticipantType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public int getSort() {
        return sort;
    }
}
