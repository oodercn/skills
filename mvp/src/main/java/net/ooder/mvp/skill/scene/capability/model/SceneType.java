package net.ooder.mvp.skill.scene.capability.model;

import net.ooder.mvp.skill.scene.dto.dict.Dict;
import net.ooder.mvp.skill.scene.dto.dict.DictItem;

@Dict(code = "scene_type", name = "场景类型", description = "场景技能的运行类型")
public enum SceneType implements DictItem {

    AUTO("AUTO", "自驱场景", "自动运行，hasSelfDrive=true", "ri-robot-line", 1),
    TRIGGER("TRIGGER", "触发场景", "需要触发，hasSelfDrive=false", "ri-hand-coin-line", 2);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SceneType(String code, String name, String description, String icon, int sort) {
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

    public static SceneType fromCode(String code) {
        for (SceneType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return TRIGGER;
    }
}
