package net.ooder.skill.mqtt.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT服务端配�? */
public class MqttServerConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String serverId = "mqtt-broker";
    private String host = "0.0.0.0";
    private int port = 1883;
    private int websocketPort = 8083;
    private boolean websocketEnabled = true;
    private boolean sslEnabled = false;
    private String sslKeystorePath;
    private String sslKeystorePassword;
    
    private int maxConnections = 10000;
    private int maxMessageSize = 65536;
    private int connectionTimeout = 30000;
    private int keepAliveInterval = 60;
    private boolean allowAnonymous = false;
    
    private String username;
    private String password;
    
    private boolean persistentEnabled = true;
    private String dataDirectory = "./data/mqtt";
    
    private Map<String, Object> extendedConfig = new HashMap<String, Object>();
    
    public MqttServerConfig() {
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getServerId() {
        return serverId;
    }
    
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getWebsocketPort() {
        return websocketPort;
    }
    
    public void setWebsocketPort(int websocketPort) {
        this.websocketPort = websocketPort;
    }
    
    public boolean isWebsocketEnabled() {
        return websocketEnabled;
    }
    
    public void setWebsocketEnabled(boolean websocketEnabled) {
        this.websocketEnabled = websocketEnabled;
    }
    
    public boolean isSslEnabled() {
        return sslEnabled;
    }
    
    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }
    
    public String getSslKeystorePath() {
        return sslKeystorePath;
    }
    
    public void setSslKeystorePath(String sslKeystorePath) {
        this.sslKeystorePath = sslKeystorePath;
    }
    
    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }
    
    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getMaxMessageSize() {
        return maxMessageSize;
    }
    
    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }
    
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }
    
    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isPersistentEnabled() {
        return persistentEnabled;
    }
    
    public void setPersistentEnabled(boolean persistentEnabled) {
        this.persistentEnabled = persistentEnabled;
    }
    
    public String getDataDirectory() {
        return dataDirectory;
    }
    
    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
    
    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }
    
    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }
    
    public static class Builder {
        private MqttServerConfig config = new MqttServerConfig();
        
        public Builder serverId(String serverId) {
            config.setServerId(serverId);
            return this;
        }
        
        public Builder host(String host) {
            config.setHost(host);
            return this;
        }
        
        public Builder port(int port) {
            config.setPort(port);
            return this;
        }
        
        public Builder websocketPort(int websocketPort) {
            config.setWebsocketPort(websocketPort);
            return this;
        }
        
        public Builder websocketEnabled(boolean enabled) {
            config.setWebsocketEnabled(enabled);
            return this;
        }
        
        public Builder sslEnabled(boolean enabled) {
            config.setSslEnabled(enabled);
            return this;
        }
        
        public Builder maxConnections(int maxConnections) {
            config.setMaxConnections(maxConnections);
            return this;
        }
        
        public Builder allowAnonymous(boolean allow) {
            config.setAllowAnonymous(allow);
            return this;
        }
        
        public Builder auth(String username, String password) {
            config.setUsername(username);
            config.setPassword(password);
            return this;
        }
        
        public Builder persistent(boolean enabled, String dataDir) {
            config.setPersistentEnabled(enabled);
            config.setDataDirectory(dataDir);
            return this;
        }
        
        public Builder extendedConfig(Map<String, Object> extended) {
            config.setExtendedConfig(extended);
            return this;
        }
        
        public MqttServerConfig build() {
            return config;
        }
    }
}
