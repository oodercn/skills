package net.ooder.skill.security.dto.audit;

import net.ooder.skill.security.dto.dict.Dict;
import net.ooder.skill.security.dto.dict.DictItem;

@Dict(code = "audit_event_type", name = "审计事件类型", description = "安全审计事件类型")
public enum AuditEventType implements DictItem {
    
    KEY_CREATE("KEY_CREATE", "密钥创建", "创建新密钥", "ri-add-circle-line", 1),
    KEY_USE("KEY_USE", "密钥使用", "使用密钥", "ri-key-2-line", 2),
    KEY_ROTATE("KEY_ROTATE", "密钥轮换", "轮换密钥", "ri-refresh-line", 3),
    KEY_REVOKE("KEY_REVOKE", "密钥撤销", "撤销密钥", "ri-forbid-line", 4),
    PERMISSION_GRANT("PERMISSION_GRANT", "权限授予", "授予权限", "ri-user-add-line", 5),
    PERMISSION_REVOKE("PERMISSION_REVOKE", "权限撤销", "撤销权限", "ri-user-unfollow-line", 6),
    PERMISSION_CHECK("PERMISSION_CHECK", "权限检查", "检查权限", "ri-shield-check-line", 7),
    AGENT_AUTH("AGENT_AUTH", "Agent认证", "Agent身份认证", "ri-robot-line", 8),
    AGENT_COMM("AGENT_COMM", "Agent通讯", "Agent间通讯", "ri-message-3-line", 9),
    AGENT_ACCESS("AGENT_ACCESS", "Agent访问", "Agent资源访问", "ri-login-box-line", 10),
    SCENE_CREATE("SCENE_CREATE", "场景创建", "创建场景", "ri-apps-line", 11),
    SCENE_START("SCENE_START", "场景启动", "启动场景", "ri-play-circle-line", 12),
    SCENE_END("SCENE_END", "场景结束", "结束场景", "ri-stop-circle-line", 13),
    LLM_CALL("LLM_CALL", "LLM调用", "调用大语言模型", "ri-chat-ai-line", 14),
    LLM_KEY_USE("LLM_KEY_USE", "LLM密钥使用", "使用LLM密钥", "ri-key-line", 15);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    AuditEventType(String code, String name, String description, String icon, int sort) {
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
