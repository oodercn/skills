package net.ooder.skill.onboarding.assistant.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "learning_stage_status", name = "学习阶段状态", description = "学习阶段的执行状态")
public enum LearningStageStatus implements DictItem {
    
    PENDING("PENDING", "待开始", "阶段未开始", "ri-time-line", 1),
    IN_PROGRESS("IN_PROGRESS", "进行中", "正在学习", "ri-play-circle-line", 2),
    PAUSED("PAUSED", "已暂停", "学习暂停", "ri-pause-circle-line", 3),
    COMPLETED("COMPLETED", "已完成", "阶段完成", "ri-checkbox-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    LearningStageStatus(String code, String name, String description, String icon, int sort) {
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
