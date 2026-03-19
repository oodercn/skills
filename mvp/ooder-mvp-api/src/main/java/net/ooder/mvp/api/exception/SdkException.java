package net.ooder.mvp.api.exception;

public class SdkException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;
    
    public SdkException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 500;
    }
    
    public SdkException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() { return errorCode; }
    public int getHttpStatus() { return httpStatus; }
}
