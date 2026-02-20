package net.ooder.skill.mqtt.server;

import net.ooder.skill.mqtt.context.MqttContext;

import java.util.Map;

/**
 * MQTT服务端接�? * 
 * <p>提供轻量级MQTT服务端能力，作为降级方案�?/p>
 * 
 * <h3>核心功能�?/h3>
 * <ul>
 *   <li>Broker生命周期管理</li>
 *   <li>连接管理</li>
 *   <li>消息路由</li>
 *   <li>订阅管理</li>
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
