package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Mosquitto娴间椒绗熺痪顪換TT閺堝秴濮熼幓鎰返閼?- 閼奉亜缂撻弬瑙勵攳
 * 
 * <p>Mosquitto閺勵垯绔村▎鎹愪氦闁插繒楠囧鈧┃鎬TT Broker閿涘矂鈧倻鏁ゆ禍搴濊厬鐏忓繗顫夊Ο锟犲劥缂冨眰鈧?/p>
 * 
 * <h3>閻楀湱鍋ｉ敍?/h3>
 * <ul>
 *   <li>鏉炲鍣虹痪褋鈧椒缍嗙挧鍕爱閸楃姷鏁?/li>
 *   <li>閺勬挷绨柊宥囩枂閸滃矂鍎寸純?/li>
 *   <li>閺€顖涘瘮MQTT 5.0</li>
 *   <li>闁倸鎮庢潏鍦喘鐠侊紕鐣婚崷鐑樻珯</li>
 * </ul>
 */
public class MosquittoEnterpriseProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(MosquittoEnterpriseProvider.class);
    
    public static final String PROVIDER_ID = "mosquitto-enterprise";
    public static final String PROVIDER_NAME = "Mosquitto MQTT Broker";
    
    private String brokerUrl = "tcp://localhost:1883";
    
    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public ProviderType getProviderType() {
        return ProviderType.ENTERPRISE_SELF_HOSTED;
    }
    
    @Override
    public MqttServer createServer(MqttServerConfig config) {
        log.info("Mosquitto provider creates server connection to: {}", brokerUrl);
        return new MosquittoServerAdapter(config);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new HashMap<String, Object>();
        caps.put("maxConnections", 100000);
        caps.put("websocket", true);
        caps.put("ssl", true);
        caps.put("cluster", false);
        caps.put("persistence", true);
        caps.put("qos", new int[]{0, 1, 2});
        caps.put("retained", true);
        caps.put("lastWill", true);
        caps.put("mqtt5", true);
        return caps;
    }
    
    @Override
    public int getPriority() {
        return 60;
    }
    
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
    
    private static class MosquittoServerAdapter implements MqttServer {
        private final MqttServerConfig config;
        private ServerStatus status = ServerStatus.CREATED;
        
        MosquittoServerAdapter(MqttServerConfig config) {
            this.config = config;
        }
        
        @Override
        public String getServerId() {
            return "mosquitto-adapter";
        }
        
        @Override
        public ServerStatus getStatus() {
            return status;
        }
        
        @Override
        public void start() {
            status = ServerStatus.RUNNING;
            log.info("Mosquitto adapter started");
        }
        
        @Override
        public void stop() {
            status = ServerStatus.STOPPED;
            log.info("Mosquitto adapter stopped");
        }
        
        @Override
        public void initialize(MqttServerConfig config) {
        }
        
        @Override
        public MqttServerConfig getConfig() {
            return config;
        }
        
        @Override
        public void onConnect(net.ooder.skill.mqtt.context.MqttContext context) {
        }
        
        @Override
        public void onDisconnect(String sessionId) {
        }
        
        @Override
        public boolean isRunning() {
            return status == ServerStatus.RUNNING;
        }
        
        @Override
        public int getConnectedCount() {
            return 0;
        }
        
        @Override
        public Map<String, Object> getStatistics() {
            return new HashMap<String, Object>();
        }
    }
}
