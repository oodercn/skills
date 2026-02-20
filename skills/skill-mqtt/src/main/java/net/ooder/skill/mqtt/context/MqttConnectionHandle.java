package net.ooder.skill.mqtt.context;

/**
 * MQTT连接句柄接口
 */
public interface MqttConnectionHandle {
    
    void connect(MqttContext context) throws Exception;
    
    void disconnect();
    
    boolean isConnected();
    
    void send(String message) throws Exception;
    
    void send(String topic, byte[] payload, int qos, boolean retained) throws Exception;
    
    void broadcast(String topic, String message) throws Exception;
    
    MqttContext getContext();
    
    String getSessionId();
}
