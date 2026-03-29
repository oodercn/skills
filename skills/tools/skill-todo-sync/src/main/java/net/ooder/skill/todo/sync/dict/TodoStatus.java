package net.ooder.skill.todo.sync.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "todo_status", name = "待办状态")
public enum TodoStatus implements DictItem {
    PENDING("PENDING", "待处理", "待办待处理", "ri-time-line", 1),
    IN_PROGRESS("IN_PROGRESS", "进行中", "待办进行中", "ri-loader-line", 2),
    COMPLETED("COMPLETED", "已完成", "待办已完成", "ri-checkbox-circle-line", 3),
    CANCELLED("CANCELLED", "已取消", "待办已取消", "ri-close-circle-line", 4),
    OVERDUE("OVERDUE", "已逾期", "待办已逾期", "ri-error-warning-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    TodoStatus(String code, String name, String description, String icon, int sort) {
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
