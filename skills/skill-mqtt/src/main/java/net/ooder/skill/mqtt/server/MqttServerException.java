package net.ooder.skill.mqtt.server;

/**
 * MQTT服务端异�? */
public class MqttServerException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public static final int CONNECTION_ERROR = 1001;
    public static final int AUTHENTICATION_ERROR = 1002;
    public static final int TOPIC_ERROR = 1003;
    public static final int MESSAGE_ERROR = 1004;
    public static final int SUBSCRIPTION_ERROR = 1005;
    public static final int SESSION_ERROR = 1006;
    public static final int CONFIG_ERROR = 1007;
    
    private int errorCode;
    
    public MqttServerException(String message) {
        super(message);
    }
    
    public MqttServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public MqttServerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MqttServerException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}
