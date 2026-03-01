package net.ooder.nexus.common.exceptions;

public class NexusException extends RuntimeException {
    
    private String errorCode;
    private String moduleName;
    
    public NexusException(String message) {
        super(message);
    }
    
    public NexusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NexusException(String errorCode, String message, String moduleName) {
        super(message);
        this.errorCode = errorCode;
        this.moduleName = moduleName;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getModuleName() {
        return moduleName;
    }
}
