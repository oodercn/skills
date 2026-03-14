package net.ooder.skill.scene.capability.model;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "skill_form", name = "技能形态", description = "技能的形态分类")
public enum SkillForm implements DictItem {

    SCENE("SCENE", "场景应用", "容器型技能，可包含子技能", "ri-layout-grid-line", 1),
    PROVIDER("PROVIDER", "能力服务", "提供基础能力的技能", "ri-cpu-line", 2),
    DRIVER("DRIVER", "驱动适配", "驱动场景运行的技能", "ri-steering-line", 3),
    INTERNAL("INTERNAL", "内部服务", "系统内部使用的技能", "ri-settings-3-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SkillForm(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getIcon() { return icon; }

    @Override
    public int getSort() { return sort; }

    public static SkillForm fromCode(String code) {
        for (SkillForm form : values()) {
            if (form.code.equalsIgnoreCase(code)) {
                return form;
            }
        }
        return PROVIDER;
    }
}
