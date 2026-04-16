package net.ooder.skill.audit.dto;

public enum AuditResultType {
    
    SUCCESS("SUCCESS", "成功"),
    FAILURE("FAILURE", "失败"),
    PARTIAL("PARTIAL", "部分成功"),
    PENDING("PENDING", "待处理"),
    TIMEOUT("TIMEOUT", "超时"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    AuditResultType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static AuditResultType fromCode(String code) {
        if (code == null) return null;
        for (AuditResultType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isFailure() {
        return this == FAILURE;
    }
}
