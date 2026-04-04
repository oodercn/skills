package net.ooder.skill.bpm.enums;

import net.ooder.skill.common.dict.DictItem;
import net.ooder.skill.common.dict.Dict;

@Dict(code = "process_status", name = "流程状态", description = "流程实例的状态")
public enum ProcessStatus implements DictItem {

    DRAFT("DRAFT", "草稿", "流程定义草稿状态", "ri-draft-line", 1),
    PUBLISHED("PUBLISHED", "已发布", "流程定义已发布", "ri-send-plane-line", 2),
    RUNNING("RUNNING", "运行中", "流程实例运行中", "ri-play-circle-line", 3),
    SUSPENDED("SUSPENDED", "已暂停", "流程实例已暂停", "ri-pause-circle-line", 4),
    COMPLETED("COMPLETED", "已完成", "流程实例已完成", "ri-check-line", 5),
    TERMINATED("TERMINATED", "已终止", "流程实例已终止", "ri-stop-circle-line", 6),
    ERROR("ERROR", "异常", "流程实例异常", "ri-error-warning-line", 7);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ProcessStatus(String code, String name, String description, String icon, int sort) {
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
