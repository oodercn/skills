package net.ooder.skill.onboarding.assistant.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "question_category", name = "问题分类", description = "培训问题的分类")
public enum QuestionCategory implements DictItem {
    
    POLICY("POLICY", "制度政策", "公司制度、政策相关问题", "ri-file-list-line", 1),
    PROCESS("PROCESS", "流程规范", "工作流程、规范相关问题", "ri-flow-line", 2),
    BENEFITS("BENEFITS", "福利待遇", "薪资福利相关问题", "ri-money-line", 3),
    TECHNICAL("TECHNICAL", "技术问题", "岗位技术相关问题", "ri-code-line", 4),
    GENERAL("GENERAL", "通用问题", "其他通用问题", "ri-question-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    QuestionCategory(String code, String name, String description, String icon, int sort) {
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
