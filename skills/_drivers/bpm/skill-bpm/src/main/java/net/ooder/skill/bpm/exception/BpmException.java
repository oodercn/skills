package net.ooder.skill.bpm.exception;

public class BpmException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final int NOT_LOGINED_ERROR = 401;
    public static final int PROCESS_NOT_FOUND = 404;
    public static final int ACTIVITY_NOT_FOUND = 405;
    public static final int INVALID_STATUS = 400;
    public static final int PERMISSION_DENIED = 403;
    public static final int PROCESS_DEFINITION_ERROR = 500;
    public static final int PROCESS_EXECUTION_ERROR = 501;

    private int errorCode;

    public BpmException(String message) {
        super(message);
        this.errorCode = 500;
    }

    public BpmException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BpmException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 500;
    }

    public BpmException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
