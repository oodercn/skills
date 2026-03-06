package net.ooder.skill.scene.capability.model;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "scene_skill_category", name = "场景技能分类", description = "场景技能三大分类体系 v2.3")
public enum SceneSkillCategory implements DictItem {
    
    ABS("ABS", "自驱业务场景", 
        "Auto Business Scene - 标准1✓ + 标准2✓ + 标准3✓ + 标准4✓，具备完整自驱能力和业务语义", 
        "ri-layout-grid-line", 1),
    
    ASS("ASS", "自驱系统场景", 
        "Auto System Scene - 标准1✓ + 标准2✓ + 标准3✓ + 标准4✗，具备自驱能力但无业务语义", 
        "ri-settings-4-line", 2),
    
    TBS("TBS", "触发业务场景", 
        "Trigger Business Scene - 标准1✓ + 标准2✓ + 标准3✗ + 标准4✓，需要外部触发的业务场景", 
        "ri-hand-coin-line", 3),
    
    PENDING("PENDING", "待定分类", 
        "业务语义评分3-7分，需人工判定分类", 
        "ri-question-line", 4),
    
    NOT_SCENE_SKILL("NOT_SCENE_SKILL", "非场景技能", 
        "不满足标准1或标准2，非场景技能类型", 
        "ri-close-circle-line", 98),
    
    INVALID("INVALID", "无效配置", 
        "配置无效，无法分类", 
        "ri-error-warning-line", 99);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SceneSkillCategory(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public int getSort() {
        return sort;
    }

    public boolean hasMainFirst() {
        return this == ABS || this == ASS;
    }

    public boolean hasBusinessSemantics() {
        return this == ABS || this == TBS;
    }

    public boolean isAutoTrigger() {
        return this == ABS || this == ASS;
    }

    public boolean needsManualTrigger() {
        return this == TBS;
    }

    public boolean isPublicVisible() {
        return this == ABS || this == TBS;
    }

    public boolean isInternalVisible() {
        return this == ASS;
    }

    public boolean supportsPause() {
        return this == ABS || this == TBS;
    }

    public boolean supportsArchive() {
        return this == ABS || this == TBS;
    }

    public boolean isOneTimeExecution() {
        return this == TBS;
    }

    public boolean isContinuousExecution() {
        return this == ASS;
    }
}
