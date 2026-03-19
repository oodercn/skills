package net.ooder.skill.knowledge.share.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "share_status", name = "分享状态", description = "分享链接的状态")
public enum ShareStatus implements DictItem {
    
    VALID("VALID", "有效", "分享链接已创建，未开始使用", "ri-checkbox-circle-line", 1),
    ACTIVE("ACTIVE", "活跃", "分享链接正在被访问", "ri-play-circle-line", 2),
    EXPIRED("EXPIRED", "已过期", "分享链接已超过有效期", "ri-time-line", 3),
    REVOKED("REVOKED", "已撤销", "分享链接已被管理员撤销", "ri-close-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ShareStatus(String code, String name, String description, String icon, int sort) {
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
