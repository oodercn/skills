package net.ooder.skill.scenes.dto;

import net.ooder.skill.dict.dto.Dict;
import net.ooder.skill.dict.dto.DictItem;

@Dict(code = "capability_binding_status", name = "能力绑定状态", description = "能力绑定的状态")
public enum CapabilityBindingStatus implements DictItem {
    
    ACTIVE("ACTIVE", "活跃", "能力绑定活跃", "ri-check-line", 1),
    INACTIVE("INACTIVE", "非活跃", "能力绑定非活跃", "ri-forbid-line", 2),
    ERROR("ERROR", "错误", "能力绑定错误", "ri-error-warning-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    CapabilityBindingStatus(String code, String name, String description, String icon, int sort) {
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
