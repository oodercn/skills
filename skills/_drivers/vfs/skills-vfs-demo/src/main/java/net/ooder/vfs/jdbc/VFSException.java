package net.ooder.vfs.jdbc;

public class VFSException extends RuntimeException {
    
    public VFSException(String message) {
        super(message);
    }
    
    public VFSException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VFSException(Throwable cause) {
        super(cause);
    }
}
