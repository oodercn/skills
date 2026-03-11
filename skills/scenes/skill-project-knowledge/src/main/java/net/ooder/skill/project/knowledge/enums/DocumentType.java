package net.ooder.skill.project.knowledge.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "document_type", name = "文档类型", description = "项目文档的类型分类")
public enum DocumentType implements DictItem {
    
    REQUIREMENT("REQUIREMENT", "需求文档", "产品需求规格说明", "ri-file-list-line", 1),
    DESIGN("DESIGN", "设计文档", "系统设计、架构设计", "ri-layout-line", 2),
    TEST("TEST", "测试文档", "测试用例、测试报告", "ri-bug-line", 3),
    MEETING("MEETING", "会议纪要", "会议记录和决议", "ri-team-line", 4),
    SUMMARY("SUMMARY", "项目总结", "项目总结和复盘", "ri-file-text-line", 5),
    MANUAL("MANUAL", "操作手册", "用户手册、操作指南", "ri-book-line", 6),
    OTHER("OTHER", "其他文档", "其他类型文档", "ri-file-line", 7);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    DocumentType(String code, String name, String description, String icon, int sort) {
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
