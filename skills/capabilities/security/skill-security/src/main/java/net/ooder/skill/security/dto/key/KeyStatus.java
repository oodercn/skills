package net.ooder.skill.security.dto.key;

import net.ooder.skill.security.dto.dict.Dict;
import net.ooder.skill.security.dto.dict.DictItem;

@Dict(code = "key_status", name = "密钥状态", description = "密钥生命周期状态")
public enum KeyStatus implements DictItem {
    
    ACTIVE("ACTIVE", "激活", "密钥可用", "ri-checkbox-circle-line", 1),
    INACTIVE("INACTIVE", "未激活", "密钥未启用", "ri-pause-circle-line", 2),
    EXPIRED("EXPIRED", "已过期", "密钥已过期", "ri-time-line", 3),
    REVOKED("REVOKED", "已撤销", "密钥已撤销", "ri-close-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    KeyStatus(String code, String name, String description, String icon, int sort) {
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
