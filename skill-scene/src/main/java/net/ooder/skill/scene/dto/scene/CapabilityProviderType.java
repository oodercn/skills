package net.ooder.skill.scene.dto.scene;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "capability_provider_type", name = "能力提供者类型", description = "能力提供者的类型")
public enum CapabilityProviderType implements DictItem {
    
    SKILL("SKILL", "技能", "可安装的技能包", "ri-flashlight-line", 1),
    AGENT("AGENT", "Agent", "智能代理", "ri-robot-line", 2),
    SUPER_AGENT("SUPER_AGENT", "超级Agent", "超级智能代理", "ri-robot-2-line", 3),
    DEVICE("DEVICE", "设备", "物联网设备", "ri-device-line", 4),
    PLATFORM("PLATFORM", "平台", "平台服务", "ri-cloud-line", 5),
    CROSS_SCENE("CROSS_SCENE", "跨场景", "跨场景能力", "ri-git-merge-line", 6);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CapabilityProviderType(String code, String name, String description, String icon, int sort) {
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
