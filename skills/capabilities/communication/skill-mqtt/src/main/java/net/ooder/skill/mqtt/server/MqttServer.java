package net.ooder.skill.mqtt.server;

import net.ooder.skill.mqtt.context.MqttContext;

import java.util.Map;

/**
 * MQTT閺堝秴濮熺粩顖涘复閸? * 
 * <p>閹绘劒绶垫潪濠氬櫤缁绢湏QTT閺堝秴濮熺粩顖濆厴閸旀冻绱濇担婊€璐熼梽宥囬獓閺傝顢嶉妴?/p>
 * 
 * <h3>閺嶇绺鹃崝鐔诲厴閿?/h3>
 * <ul>
 *   <li>Broker閻㈢喎鎳￠崨銊︽埂缁狅紕鎮?/li>
 *   <li>鏉╃偞甯寸粻锛勬倞</li>
 *   <li>濞戝牊浼呯捄顖滄暠</li>
 *   <li>鐠併垽妲勭粻锛勬倞</li>
 * </ul>
 */
public interface MqttServer {
    
    String SERVER_ID = "mqtt-server";
    
    String getServerId();
    
    ServerStatus getStatus();
    
    void start() throws MqttServerException;
    
    void stop() throws MqttServerException;
    
    void initialize(MqttServerConfig config) throws MqttServerException;
    
    MqttServerConfig getConfig();
    
    void onConnect(MqttContext context) throws MqttServerException;
    
    void onDisconnect(String sessionId);
    
    boolean isRunning();
    
    int getConnectedCount();
    
    Map<String, Object> getStatistics();
    
    enum ServerStatus {
        CREATED,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED,
        ERROR
    }
}
