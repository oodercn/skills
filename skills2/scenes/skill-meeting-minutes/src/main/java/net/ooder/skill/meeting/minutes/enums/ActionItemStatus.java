package net.ooder.skill.meeting.minutes.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "action_item_status", name = "行动项状态", description = "行动项的执行状态")
public enum ActionItemStatus implements DictItem {
    
    PENDING("PENDING", "待办", "行动项待处理", "ri-time-line", 1),
    IN_PROGRESS("IN_PROGRESS", "进行中", "行动项正在执行", "ri-loader-line", 2),
    COMPLETED("COMPLETED", "已完成", "行动项已完成", "ri-checkbox-circle-line", 3),
    CANCELLED("CANCELLED", "已取消", "行动项已取消", "ri-close-circle-line", 4),
    OVERDUE("OVERDUE", "已逾期", "行动项已逾期", "ri-error-warning-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ActionItemStatus(String code, String name, String description, String icon, int sort) {
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
