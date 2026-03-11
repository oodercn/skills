package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * EMQX娴间椒绗熺痪顪換TT閺堝秴濮熼幓鎰返閼?- 閼奉亜缂撻弬瑙勵攳
 * 
 * <p>EMQX閺勵垯绔村▎鎯х磻濠ф劗娈戞妯烩偓褑鍏楳QTT Broker閿涘矂鈧倻鏁ゆ禍搴°亣鐟欏嫭膩IoT闁劎璁查妴?/p>
 * 
 * <h3>閻楀湱鍋ｉ敍?/h3>
 * <ul>
 *   <li>妤傛ê鑻熼崣鎴ｇ箾閹恒儲鏁幐渚婄礄閻у彞绔剧痪褝绱?/li>
 *   <li>闂嗗棛鍏㈤柈銊ц閼宠棄濮?/li>
 *   <li>娑撴澘鐦滈惃鍕礂鐠侇喗鏁幐渚婄礄MQTT 5.0, CoAP, LwM2M閿?/li>
 *   <li>鐟欏嫬鍨鏇熸惛閸滃本鏆熼幑顔剿夐幒?/li>
 * </ul>
 */
public class EmqxEnterpriseProvider implements MqttServiceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(EmqxEnterpriseProvider.class);
    
    public static final String PROVIDER_ID = "emqx-enterprise";
    public static final String PROVIDER_NAME = "EMQX Enterprise MQTT Broker";
    
    private String brokerUrl = "tcp://localhost:1883";
    private String dashboardUrl = "http://localhost:18083";
    private String apiUsername = "admin";
    private String apiPassword = "public";
    
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
        log.info("EMQX Enterprise provider creates server connection to: {}", brokerUrl);
        return new EmqxServerAdapter(config);
    }
    
    @Override
    public boolean isAvailable() {
        return checkEmqxConnection();
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new HashMap<String, Object>();
        caps.put("maxConnections", 1000000);
        caps.put("websocket", true);
        caps.put("ssl", true);
        caps.put("cluster", true);
        caps.put("persistence", true);
        caps.put("qos", new int[]{0, 1, 2});
        caps.put("retained", true);
        caps.put("lastWill", true);
        caps.put("mqtt5", true);
        caps.put("sharedSubscription", true);
        caps.put("ruleEngine", true);
        caps.put("dataBridge", true);
        return caps;
    }
    
    @Override
    public int getPriority() {
        return 50;
    }
    
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
    
    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }
    
    public void setApiCredentials(String username, String password) {
        this.apiUsername = username;
        this.apiPassword = password;
    }
    
    private boolean checkEmqxConnection() {
        return true;
    }
    
    private static class EmqxServerAdapter implements MqttServer {
        private final MqttServerConfig config;
        private ServerStatus status = ServerStatus.CREATED;
        
        EmqxServerAdapter(MqttServerConfig config) {
            this.config = config;
        }
        
        @Override
        public String getServerId() {
            return "emqx-adapter";
        }
        
        @Override
        public ServerStatus getStatus() {
            return status;
        }
        
        @Override
        public void start() {
            status = ServerStatus.RUNNING;
            log.info("EMQX adapter started");
        }
        
        @Override
        public void stop() {
            status = ServerStatus.STOPPED;
            log.info("EMQX adapter stopped");
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
