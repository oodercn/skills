package net.ooder.skill.todo.sync.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "todo_priority", name = "待办优先级")
public enum TodoPriority implements DictItem {
    LOW("LOW", "低", "低优先级", "ri-arrow-down-line", 1),
    MEDIUM("MEDIUM", "中", "中优先级", "ri-minus-line", 2),
    HIGH("HIGH", "高", "高优先级", "ri-arrow-up-line", 3),
    URGENT("URGENT", "紧急", "紧急优先级", "ri-alarm-warning-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    TodoPriority(String code, String name, String description, String icon, int sort) {
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
