package net.ooder.skill.audit.dto;

public enum AuditEventType {
    
    USER_LOGIN("USER_LOGIN", "用户登录"),
    USER_LOGOUT("USER_LOGOUT", "用户登出"),
    USER_CREATE("USER_CREATE", "创建用户"),
    USER_UPDATE("USER_UPDATE", "更新用户"),
    USER_DELETE("USER_DELETE", "删除用户"),
    ROLE_CREATE("ROLE_CREATE", "创建角色"),
    ROLE_UPDATE("ROLE_UPDATE", "更新角色"),
    ROLE_DELETE("ROLE_DELETE", "删除角色"),
    ROLE_ASSIGN("ROLE_ASSIGN", "分配角色"),
    PERMISSION_GRANT("PERMISSION_GRANT", "授予权限"),
    PERMISSION_REVOKE("PERMISSION_REVOKE", "撤销权限"),
    SCENE_CREATE("SCENE_CREATE", "创建场景"),
    SCENE_UPDATE("SCENE_UPDATE", "更新场景"),
    SCENE_DELETE("SCENE_DELETE", "删除场景"),
    SCENE_START("SCENE_START", "启动场景"),
    SCENE_STOP("SCENE_STOP", "停止场景"),
    CAPABILITY_INVOKE("CAPABILITY_INVOKE", "能力调用"),
    CAPABILITY_ENABLE("CAPABILITY_ENABLE", "启用能力"),
    CAPABILITY_DISABLE("CAPABILITY_DISABLE", "禁用能力"),
    LLM_CONFIG_CHANGE("LLM_CONFIG_CHANGE", "LLM配置变更"),
    KNOWLEDGE_IMPORT("KNOWLEDGE_IMPORT", "知识导入"),
    KNOWLEDGE_DELETE("KNOWLEDGE_DELETE", "知识删除"),
    SYSTEM_CONFIG_CHANGE("SYSTEM_CONFIG_CHANGE", "系统配置变更"),
    DATA_EXPORT("DATA_EXPORT", "数据导出"),
    DATA_IMPORT("DATA_IMPORT", "数据导入");

    private final String code;
    private final String description;

    AuditEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static AuditEventType fromCode(String code) {
        if (code == null) return null;
        for (AuditEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
