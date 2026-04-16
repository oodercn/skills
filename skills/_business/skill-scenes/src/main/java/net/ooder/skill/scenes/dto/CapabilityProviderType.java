package net.ooder.skill.scenes.dto;

import net.ooder.skill.dict.dto.Dict;
import net.ooder.skill.dict.dto.DictItem;

@Dict(code = "capability_provider_type", name = "能力提供者类型", description = "能力提供者的类型")
public enum CapabilityProviderType implements DictItem {
    
    PLATFORM("PLATFORM", "平台", "平台提供的能力", "ri-government-line", 1),
    AGENT("AGENT", "Agent", "Agent提供的能力", "ri-robot-line", 2),
    EXTERNAL("EXTERNAL", "外部", "外部提供的能力", "ri-external-link-line", 3);

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
