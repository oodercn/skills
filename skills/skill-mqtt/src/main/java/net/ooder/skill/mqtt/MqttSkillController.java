package net.ooder.skill.mqtt;

import net.ooder.skill.mqtt.server.*;
import net.ooder.skill.mqtt.provider.*;
import net.ooder.skill.mqtt.handler.*;
import net.ooder.skill.mqtt.message.*;
import net.ooder.skill.mqtt.context.*;
import net.ooder.skill.mqtt.protocol.*;
import net.ooder.skill.mqtt.spec.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.*;

@RestController
@RequestMapping("/api/mqtt")
@ConditionalOnProperty(name = "mqtt.broker.enabled", havingValue = "true", matchIfMissing = true)
public class MqttSkillController {
    
    private MqttServer mqttServer;
    private MqttProviderFactory providerFactory;
    
    public MqttSkillController() {
        providerFactory = MqttProviderFactory.getInstance();
        providerFactory.initialize();
    }
    
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("skillId", "skill-mqtt");
        info.put("version", "0.7.1");
        info.put("status", mqttServer != null ? mqttServer.getStatus().name() : "STOPPED");
        info.put("running", mqttServer != null && mqttServer.isRunning());
        return info;
    }
    
    @GetMapping("/broker/status")
    public Map<String, Object> getBrokerStatus() {
        Map<String, Object> status = new HashMap<>();
        if (mqttServer != null) {
            status.put("serverId", mqttServer.getServerId());
            status.put("status", mqttServer.getStatus().name());
            status.put("running", mqttServer.isRunning());
            status.put("connectedCount", mqttServer.getConnectedCount());
            status.put("statistics", mqttServer.getStatistics());
        } else {
            status.put("status", "NOT_INITIALIZED");
            status.put("running", false);
        }
        return status;
    }
    
    @PostMapping("/broker/start")
    public Map<String, Object> startBroker(@RequestBody(required = false) Map<String, Object> config) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (mqttServer == null) {
                String providerId = config != null ? (String) config.getOrDefault("providerId", "lightweight-mqtt") : "lightweight-mqtt";
                MqttServerConfig serverConfig = buildConfig(config);
                mqttServer = providerFactory.createServer(providerId, serverConfig);
            }
            mqttServer.start();
            result.put("success", true);
            result.put("serverId", mqttServer.getServerId());
            result.put("status", mqttServer.getStatus().name());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/broker/stop")
    public Map<String, Object> stopBroker() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (mqttServer != null) {
                mqttServer.stop();
                result.put("success", true);
                result.put("status", mqttServer.getStatus().name());
            } else {
                result.put("success", false);
                result.put("error", "Broker not initialized");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/publish")
    public Map<String, Object> publish(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String topic = (String) request.get("topic");
        String payload = (String) request.get("payload");
        Integer qos = (Integer) request.getOrDefault("qos", 1);
        
        if (topic == null || payload == null) {
            result.put("success", false);
            result.put("error", "topic and payload are required");
            return result;
        }
        
        MqttMessage message = MqttMessage.create(topic, payload);
        message.setQos(qos);
        
        result.put("success", true);
        result.put("messageId", message.getMessageId());
        result.put("topic", topic);
        return result;
    }
    
    @PostMapping("/subscribe")
    public Map<String, Object> subscribe(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String topic = (String) request.get("topic");
        Integer qos = (Integer) request.getOrDefault("qos", 1);
        
        if (topic == null) {
            result.put("success", false);
            result.put("error", "topic is required");
            return result;
        }
        
        result.put("success", true);
        result.put("topic", topic);
        result.put("qos", qos);
        return result;
    }
    
    @PostMapping("/p2p")
    public Map<String, Object> sendP2P(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String from = (String) request.get("from");
        String to = (String) request.get("to");
        String content = (String) request.get("content");
        
        if (from == null || to == null || content == null) {
            result.put("success", false);
            result.put("error", "from, to and content are required");
            return result;
        }
        
        ImMessage imMsg = ImMessage.create(from, to, content);
        String topic = MqttTopicSpec.p2pTopic(to);
        
        result.put("success", true);
        result.put("msgId", imMsg.getMsgId());
        result.put("topic", topic);
        return result;
    }
    
    @PostMapping("/topic")
    public Map<String, Object> broadcastTopic(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String topicName = (String) request.get("topic");
        String body = (String) request.get("body");
        
        if (topicName == null || body == null) {
            result.put("success", false);
            result.put("error", "topic and body are required");
            return result;
        }
        
        TopicMessage topicMsg = TopicMessage.create(topicName, body);
        String fullTopic = MqttTopicSpec.topicPath(topicName);
        
        result.put("success", true);
        result.put("topic", fullTopic);
        return result;
    }
    
    @PostMapping("/command")
    public Map<String, Object> sendCommand(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String deviceType = (String) request.get("deviceType");
        String deviceId = (String) request.get("deviceId");
        String command = (String) request.get("command");
        
        if (deviceType == null || deviceId == null || command == null) {
            result.put("success", false);
            result.put("error", "deviceType, deviceId and command are required");
            return result;
        }
        
        CommandMessage cmdMsg = CommandMessage.create(command);
        cmdMsg.setTargetType(deviceType);
        cmdMsg.setTargetId(deviceId);
        
        String topic = MqttTopicSpec.commandRequestTopic(deviceType, deviceId);
        
        result.put("success", true);
        result.put("commandId", cmdMsg.getCommandId());
        result.put("topic", topic);
        return result;
    }
    
    @GetMapping("/providers")
    public Map<String, Object> listProviders() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> providers = new ArrayList<>();
        
        for (MqttServiceProvider provider : providerFactory.getAllProviders()) {
            Map<String, Object> info = new HashMap<>();
            info.put("providerId", provider.getProviderId());
            info.put("providerName", provider.getProviderName());
            info.put("providerType", provider.getProviderType().name());
            info.put("available", provider.isAvailable());
            info.put("priority", provider.getPriority());
            providers.add(info);
        }
        
        result.put("providers", providers);
        return result;
    }
    
    @PostMapping("/providers/switch")
    public Map<String, Object> switchProvider(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        String providerId = (String) request.get("providerId");
        
        if (providerId == null) {
            result.put("success", false);
            result.put("error", "providerId is required");
            return result;
        }
        
        MqttServiceProvider provider = providerFactory.getProvider(providerId);
        if (provider == null) {
            result.put("success", false);
            result.put("error", "Provider not found: " + providerId);
            return result;
        }
        
        boolean wasRunning = mqttServer != null && mqttServer.isRunning();
        if (wasRunning) {
            try {
                mqttServer.stop();
            } catch (Exception e) {
            }
        }
        
        mqttServer = providerFactory.createServer(providerId, buildConfig(null));
        
        if (wasRunning) {
            try {
                mqttServer.start();
            } catch (Exception e) {
            }
        }
        
        result.put("success", true);
        result.put("providerId", providerId);
        result.put("providerName", provider.getProviderName());
        return result;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("brokerRunning", mqttServer != null && mqttServer.isRunning());
        return health;
    }
    
    private MqttServerConfig buildConfig(Map<String, Object> config) {
        MqttServerConfig.Builder builder = MqttServerConfig.builder()
            .serverId("mqtt-skill-" + System.currentTimeMillis())
            .port(1883)
            .websocketPort(8083)
            .maxConnections(10000)
            .allowAnonymous(false);
        
        if (config != null) {
            if (config.containsKey("port")) {
                builder.port(((Number) config.get("port")).intValue());
            }
            if (config.containsKey("websocketPort")) {
                builder.websocketPort(((Number) config.get("websocketPort")).intValue());
            }
            if (config.containsKey("maxConnections")) {
                builder.maxConnections(((Number) config.get("maxConnections")).intValue());
            }
        }
        
        return builder.build();
    }
}
