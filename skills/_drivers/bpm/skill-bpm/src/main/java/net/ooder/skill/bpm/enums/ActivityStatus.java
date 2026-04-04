package net.ooder.skill.bpm.enums;

import net.ooder.skill.common.dict.DictItem;
import net.ooder.skill.common.dict.Dict;

@Dict(code = "activity_status", name = "活动状态", description = "流程活动的状态")
public enum ActivityStatus implements DictItem {

    PENDING("PENDING", "待处理", "活动待处理状态", "ri-time-line", 1),
    RUNNING("RUNNING", "运行中", "活动运行中", "ri-play-line", 2),
    COMPLETED("COMPLETED", "已完成", "活动已完成", "ri-check-line", 3),
    CANCELLED("CANCELLED", "已取消", "活动已取消", "ri-close-line", 4),
    SUSPENDED("SUSPENDED", "已暂停", "活动已暂停", "ri-pause-line", 5),
    ERROR("ERROR", "异常", "活动异常", "ri-error-warning-line", 6);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ActivityStatus(String code, String name, String description, String icon, int sort) {
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
