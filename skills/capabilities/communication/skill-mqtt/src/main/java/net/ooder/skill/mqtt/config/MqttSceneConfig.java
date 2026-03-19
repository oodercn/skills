package net.ooder.skill.mqtt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttSceneConfig {

    private boolean enabled = true;
    private String provider = "lightweight-mqtt";
    private BrokerConfig broker = new BrokerConfig();
    private SceneConfig scene = new SceneConfig();
    private DiscoveryConfig discovery = new DiscoveryConfig();

    public static class BrokerConfig {
        private int port = 1883;
        private int websocketPort = 8083;
        private int maxConnections = 10000;
        private boolean allowAnonymous = false;
        private String username;
        private String password;
        private boolean websocketEnabled = true;
        private boolean sslEnabled = false;

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public int getWebsocketPort() { return websocketPort; }
        public void setWebsocketPort(int websocketPort) { this.websocketPort = websocketPort; }
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        public boolean isAllowAnonymous() { return allowAnonymous; }
        public void setAllowAnonymous(boolean allowAnonymous) { this.allowAnonymous = allowAnonymous; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public boolean isWebsocketEnabled() { return websocketEnabled; }
        public void setWebsocketEnabled(boolean websocketEnabled) { this.websocketEnabled = websocketEnabled; }
        public boolean isSslEnabled() { return sslEnabled; }
        public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }
    }

    public static class SceneConfig {
        private String sceneId;
        private String sceneType;
        private String orgId;
        private String topicPrefix = "ooder";
        private boolean autoConfigure = true;
        private Map<String, Object> sceneParams = new HashMap<>();

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSceneType() { return sceneType; }
        public void setSceneType(String sceneType) { this.sceneType = sceneType; }
        public String getOrgId() { return orgId; }
        public void setOrgId(String orgId) { this.orgId = orgId; }
        public String getTopicPrefix() { return topicPrefix; }
        public void setTopicPrefix(String topicPrefix) { this.topicPrefix = topicPrefix; }
        public boolean isAutoConfigure() { return autoConfigure; }
        public void setAutoConfigure(boolean autoConfigure) { this.autoConfigure = autoConfigure; }
        public Map<String, Object> getSceneParams() { return sceneParams; }
        public void setSceneParams(Map<String, Object> sceneParams) { this.sceneParams = sceneParams; }
    }

    public static class DiscoveryConfig {
        private boolean enabled = true;
        private String registryUrl = "https://github.com/ooderCN/skills";
        private String giteeMirror = "https://gitee.com/ooderCN/skills";
        private String indexPath = "skill-index.yaml";
        private int cacheTimeout = 3600;
        private boolean autoInstall = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getRegistryUrl() { return registryUrl; }
        public void setRegistryUrl(String registryUrl) { this.registryUrl = registryUrl; }
        public String getGiteeMirror() { return giteeMirror; }
        public void setGiteeMirror(String giteeMirror) { this.giteeMirror = giteeMirror; }
        public String getIndexPath() { return indexPath; }
        public void setIndexPath(String indexPath) { this.indexPath = indexPath; }
        public int getCacheTimeout() { return cacheTimeout; }
        public void setCacheTimeout(int cacheTimeout) { this.cacheTimeout = cacheTimeout; }
        public boolean isAutoInstall() { return autoInstall; }
        public void setAutoInstall(boolean autoInstall) { this.autoInstall = autoInstall; }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public BrokerConfig getBroker() { return broker; }
    public void setBroker(BrokerConfig broker) { this.broker = broker; }
    public SceneConfig getScene() { return scene; }
    public void setScene(SceneConfig scene) { this.scene = scene; }
    public DiscoveryConfig getDiscovery() { return discovery; }
    public void setDiscovery(DiscoveryConfig discovery) { this.discovery = discovery; }
}
