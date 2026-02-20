package net.ooder.skill.mqtt.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT上下�?- 封装MQTT连接上下文信�? */
public class MqttContext {
    
    private String sessionId;
    private String clientId;
    private String clientIp;
    private String username;
    private String userId;
    private String systemCode;
    private long connectTime;
    private long lastActiveTime;
    private boolean authenticated;
    private boolean cleanSession;
    private int keepAlive;
    
    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private Map<String, Integer> subscriptions = new ConcurrentHashMap<String, Integer>();
    
    public MqttContext() {
        this.connectTime = System.currentTimeMillis();
        this.lastActiveTime = this.connectTime;
    }
    
    public MqttContext(String sessionId, String clientId) {
        this();
        this.sessionId = sessionId;
        this.clientId = clientId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSystemCode() {
        return systemCode;
    }
    
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
    
    public long getConnectTime() {
        return connectTime;
    }
    
    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }
    
    public long getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
    
    public boolean isCleanSession() {
        return cleanSession;
    }
    
    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }
    
    public int getKeepAlive() {
        return keepAlive;
    }
    
    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    public Map<String, Integer> getSubscriptions() {
        return subscriptions;
    }
    
    public void addSubscription(String topic, int qos) {
        subscriptions.put(topic, qos);
    }
    
    public void removeSubscription(String topic) {
        subscriptions.remove(topic);
    }
    
    public boolean hasSubscription(String topic) {
        return subscriptions.containsKey(topic);
    }
    
    public void updateActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }
    
    public void clear() {
        attributes.clear();
        subscriptions.clear();
    }
}
