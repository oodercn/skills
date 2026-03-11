package net.ooder.skill.onboarding.assistant.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "stage_type", name = "阶段类型", description = "学习阶段的类型分类")
public enum StageType implements DictItem {
    
    ORIENTATION("ORIENTATION", "入职引导", "公司文化、制度介绍", "ri-home-line", 1),
    POLICY("POLICY", "制度学习", "公司规章制度学习", "ri-file-list-line", 2),
    SKILL("SKILL", "技能培训", "岗位技能培训", "ri-tools-line", 3),
    PROJECT("PROJECT", "项目实践", "实际项目参与", "ri-folder-line", 4),
    ASSESSMENT("ASSESSMENT", "考核评估", "学习效果评估", "ri-checkbox-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    StageType(String code, String name, String description, String icon, int sort) {
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
