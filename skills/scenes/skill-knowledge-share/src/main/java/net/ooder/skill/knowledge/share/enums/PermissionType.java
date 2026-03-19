package net.ooder.skill.knowledge.share.enums;

import net.ooder.scene.skill.dict.Dict;
import net.ooder.scene.skill.dict.DictItem;

@Dict(code = "permission_type", name = "权限类型", description = "知识库访问权限级别")
public enum PermissionType implements DictItem {
    
    READ("READ", "只读", "仅可查看知识库内容", "ri-eye-line", 1),
    WRITE("WRITE", "读写", "可查看和编辑知识库内容", "ri-edit-line", 2),
    ADMIN("ADMIN", "管理", "可管理权限和分享", "ri-admin-line", 3),
    OWNER("OWNER", "所有者", "知识库所有者，拥有全部权限", "ri-key-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    PermissionType(String code, String name, String description, String icon, int sort) {
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
