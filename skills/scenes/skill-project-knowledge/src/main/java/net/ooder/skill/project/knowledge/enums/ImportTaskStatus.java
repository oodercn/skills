package net.ooder.skill.project.knowledge.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "import_task_status", name = "导入任务状态", description = "文档导入任务的状态")
public enum ImportTaskStatus implements DictItem {
    
    PENDING("PENDING", "待处理", "任务已创建，等待处理", "ri-time-line", 1),
    PROCESSING("PROCESSING", "处理中", "正在处理文档", "ri-loader-line", 2),
    COMPLETED("COMPLETED", "已完成", "处理完成", "ri-checkbox-circle-line", 3),
    FAILED("FAILED", "失败", "处理失败", "ri-error-warning-line", 4),
    CANCELLED("CANCELLED", "已取消", "任务已取消", "ri-close-circle-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ImportTaskStatus(String code, String name, String description, String icon, int sort) {
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
