package net.ooder.skill.project.knowledge.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "project_status", name = "项目状态", description = "项目的生命周期状态")
public enum ProjectStatus implements DictItem {
    
    PLANNING("PLANNING", "规划中", "项目规划阶段", "ri-planning-line", 1),
    ACTIVE("ACTIVE", "进行中", "项目正在进行", "ri-play-circle-line", 2),
    COMPLETED("COMPLETED", "已完成", "项目已完成", "ri-checkbox-circle-line", 3),
    ARCHIVED("ARCHIVED", "已归档", "项目已归档", "ri-archive-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ProjectStatus(String code, String name, String description, String icon, int sort) {
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
