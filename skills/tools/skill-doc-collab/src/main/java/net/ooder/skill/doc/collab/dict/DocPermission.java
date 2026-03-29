package net.ooder.skill.doc.collab.dict;

import net.ooder.api.dict.DictItem;
import net.ooder.api.dict.Dict;

@Dict(code = "doc_permission", name = "文档权限")
public enum DocPermission implements DictItem {
    VIEW("VIEW", "只读", "只能查看", "ri-eye-line", 1),
    COMMENT("COMMENT", "评论", "可评论", "ri-chat-1-line", 2),
    EDIT("EDIT", "编辑", "可编辑", "ri-edit-line", 3),
    MANAGE("MANAGE", "管理", "可管理", "ri-settings-4-line", 4),
    OWNER("OWNER", "所有者", "文档所有者", "ri-user-star-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    DocPermission(String code, String name, String description, String icon, int sort) {
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
