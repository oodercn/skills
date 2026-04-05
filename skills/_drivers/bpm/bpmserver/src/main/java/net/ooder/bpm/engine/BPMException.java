package net.ooder.bpm.engine;

public class BPMException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public static final int PROCESSDEFINITIONERROR = 1001;
    public static final int PROCESSINSTANCEERROR = 1002;
    public static final int ACTIVITYDEFINITIONERROR = 1003;
    public static final int ACTIVITYINSTANCEERROR = 1004;
    public static final int ROUTEDEFINITIONERROR = 1005;
    public static final int ROUTEINSTANCEERROR = 1006;
    public static final int ATTRIBUTEERROR = 1007;
    
    public static final int GETPROCESSDEFLISTERROR = 1010;
    public static final int GETPROCESSINSTLISTERROR = 1020;
    public static final int GETACTIVITYINSTLISTERROR = 1030;
    public static final int CREATEPROCESSINSTANCEERROR = 1040;
    public static final int STARTPROCESSINSTANCEERROR = 1050;
    public static final int NEWPROCESSINSTANCEERROR = 1060;
    public static final int TRANSACTIONBEGINERROR = 1070;
    public static final int TRANSACTIONCOMMITERROR = 1071;
    public static final int TRANSACTIONROLLBACKERROR = 1072;
    public static final int NOTLOGINEDERROR = 1080;
    
    private int errorCode;
    
    public BPMException(String message) {
        super(message);
    }
    
    public BPMException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BPMException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public BPMException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BPMException(Exception e) {
        super(e);
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}
