package net.ooder.skill.management.model;

public class SkillException extends Exception {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private ErrorCode errorCode;

    public enum ErrorCode {
        SKILL_NOT_FOUND,
        PARAMETER_ERROR,
        EXECUTION_EXCEPTION,
        INITIALIZATION_ERROR,
        CONFIGURATION_ERROR,
        DEPENDENCY_ERROR,
        PERMISSION_DENIED,
        TIMEOUT_ERROR,
        STATE_ERROR
    }

    public SkillException(String skillId, String message) {
        super(message);
        this.skillId = skillId;
        this.errorCode = ErrorCode.EXECUTION_EXCEPTION;
    }

    public SkillException(String skillId, String message, ErrorCode errorCode) {
        super(message);
        this.skillId = skillId;
        this.errorCode = errorCode;
    }

    public SkillException(String skillId, String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.skillId = skillId;
        this.errorCode = errorCode;
    }

    public String getSkillId() {
        return skillId;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
