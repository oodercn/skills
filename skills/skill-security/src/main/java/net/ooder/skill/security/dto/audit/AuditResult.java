package net.ooder.skill.security.dto.audit;

import net.ooder.skill.security.dto.dict.Dict;
import net.ooder.skill.security.dto.dict.DictItem;

@Dict(code = "audit_result", name = "审计结果", description = "安全审计操作结果")
public enum AuditResult implements DictItem {
    
    SUCCESS("SUCCESS", "成功", "操作成功", "ri-checkbox-circle-line", 1),
    FAILURE("FAILURE", "失败", "操作失败", "ri-error-warning-line", 2),
    DENIED("DENIED", "拒绝", "操作被拒绝", "ri-forbid-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    AuditResult(String code, String name, String description, String icon, int sort) {
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
