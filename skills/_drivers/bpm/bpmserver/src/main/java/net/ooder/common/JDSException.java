package net.ooder.common;

public class JDSException extends Exception {
    private static final long serialVersionUID = 1L;

    public static final int LOADRIGHTENGINEERROR = 2001;
    public static final int LOADFILEENGINEERROR = 2002;
    public static final int NOTLOGINEDERROR = 2003;
    public static final int LOADADMINSERVICEERROR = 2004;
    public static final int SERVERNOTSTARTEDERROR = 2005;
    public static final int UNSUPPORTCOREPROCESSEVENTERROR = 2006;
    public static final int UNSUPPORTCOREACTIVITYEVENTERROR = 2007;

    private int errorCode;

    public JDSException() {
        super();
    }

    public JDSException(String message) {
        super(message);
    }

    public JDSException(String message, Throwable cause) {
        super(message, cause);
    }

    public JDSException(Throwable cause) {
        super(cause);
    }

    public JDSException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public JDSException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
