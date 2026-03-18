package net.ooder.skill.protocol.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.common.Result;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProtocolProviderImpl {
    
    private boolean initialized = false;
    private boolean running = false;
    
    private final Map<String, ProtocolHandlerInfo> handlers = new ConcurrentHashMap<>();
    
    public String getProviderName() {
        return "skill-protocol";
    }
    
    public String getVersion() {
        return "2.3.1";
    }
    
    public void initialize() {
        this.initialized = true;
        registerDefaultHandlers();
        log.info("ProtocolProvider initialized");
    }
    
    public void start() {
        this.running = true;
        log.info("ProtocolProvider started");
    }
    
    public void stop() {
        this.running = false;
        log.info("ProtocolProvider stopped");
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    private void registerDefaultHandlers() {
        registerHandler("mcp", "Model Context Protocol", "net.ooder.protocol.mcp.McpHandler", Arrays.asList("mcp://", "model-context://"));
        registerHandler("http", "HTTP Protocol", "net.ooder.protocol.http.HttpHandler", Arrays.asList("http://", "https://"));
        registerHandler("mqtt", "MQTT Protocol", "net.ooder.protocol.mqtt.MqttHandler", Arrays.asList("mqtt://", "mqtts://"));
        registerHandler("coap", "CoAP Protocol", "net.ooder.protocol.coap.CoapHandler", Arrays.asList("coap://", "coaps://"));
        registerHandler("ws", "WebSocket Protocol", "net.ooder.protocol.ws.WebSocketHandler", Arrays.asList("ws://", "wss://"));
    }
    
    private void registerHandler(String type, String name, String handlerClass, List<String> schemes) {
        ProtocolHandlerInfo handler = new ProtocolHandlerInfo();
        handler.setHandlerId(UUID.randomUUID().toString());
        handler.setType(type);
        handler.setName(name);
        handler.setHandlerClass(handlerClass);
        handler.setSchemes(schemes);
        handler.setEnabled(true);
        handler.setRegisteredAt(System.currentTimeMillis());
        handler.setMetadata(new HashMap<>());
        handlers.put(type, handler);
    }
    
    public Result<List<ProtocolHandlerInfo>> getProtocolHandlers() {
        List<ProtocolHandlerInfo> handlerList = new ArrayList<>(handlers.values());
        log.debug("Retrieved {} protocol handlers", handlerList.size());
        return Result.success(handlerList);
    }
    
    public Result<ProtocolHandlerInfo> registerProtocolHandler(Map<String, Object> handlerData) {
        try {
            String type = (String) handlerData.get("type");
            if (type == null || type.isEmpty()) {
                return Result.error("Handler type is required");
            }
            
            if (handlers.containsKey(type)) {
                return Result.error("Handler already exists: " + type);
            }
            
            ProtocolHandlerInfo handler = new ProtocolHandlerInfo();
            handler.setHandlerId(UUID.randomUUID().toString());
            handler.setType(type);
            handler.setName((String) handlerData.getOrDefault("name", type));
            handler.setHandlerClass((String) handlerData.get("handlerClass"));
            handler.setSchemes((List<String>) handlerData.getOrDefault("schemes", new ArrayList<>()));
            handler.setEnabled((Boolean) handlerData.getOrDefault("enabled", true));
            handler.setRegisteredAt(System.currentTimeMillis());
            handler.setMetadata((Map<String, Object>) handlerData.getOrDefault("metadata", new HashMap<>()));
            
            handlers.put(type, handler);
            
            log.info("Registered protocol handler: {} ({})", handler.getName(), type);
            return Result.success(handler);
        } catch (Exception e) {
            log.error("Failed to register protocol handler", e);
            return Result.error("Failed to register protocol handler: " + e.getMessage());
        }
    }
    
    public Result<Boolean> removeProtocolHandler(String handlerType) {
        ProtocolHandlerInfo removed = handlers.remove(handlerType);
        if (removed == null) {
            return Result.error("Handler not found: " + handlerType);
        }
        
        log.info("Removed protocol handler: {}", handlerType);
        return Result.success(true);
    }
    
    public Result<Map<String, Object>> handleProtocolCommand(Map<String, Object> commandData) {
        try {
            String protocol = (String) commandData.get("protocol");
            String command = (String) commandData.get("command");
            Map<String, Object> params = (Map<String, Object>) commandData.getOrDefault("params", new HashMap<>());
            
            if (protocol == null || protocol.isEmpty()) {
                return Result.error("Protocol is required");
            }
            
            ProtocolHandlerInfo handler = handlers.get(protocol);
            if (handler == null) {
                return Result.error("Handler not found for protocol: " + protocol);
            }
            
            if (!handler.isEnabled()) {
                return Result.error("Handler is disabled: " + protocol);
            }
            
            Map<String, Object> output = new HashMap<>();
            output.put("commandId", UUID.randomUUID().toString());
            output.put("protocol", protocol);
            output.put("command", command);
            output.put("success", true);
            output.put("status", "completed");
            output.put("message", "Command executed successfully");
            output.put("params", params);
            output.put("timestamp", System.currentTimeMillis());
            
            log.info("Protocol command executed: {} - {}", protocol, command);
            return Result.success(output);
        } catch (Exception e) {
            log.error("Failed to handle protocol command", e);
            return Result.error("Failed to handle protocol command: " + e.getMessage());
        }
    }
    
    public Result<Boolean> refreshProtocolHandlers() {
        try {
            handlers.clear();
            registerDefaultHandlers();
            
            log.info("Protocol handlers refreshed");
            return Result.success(true);
        } catch (Exception e) {
            log.error("Failed to refresh protocol handlers", e);
            return Result.error("Failed to refresh protocol handlers: " + e.getMessage());
        }
    }
    
    public ProtocolHandlerInfo getHandler(String type) {
        return handlers.get(type);
    }
    
    public boolean hasHandler(String type) {
        return handlers.containsKey(type);
    }
    
    @lombok.Data
    public static class ProtocolHandlerInfo {
        private String handlerId;
        private String type;
        private String name;
        private String handlerClass;
        private List<String> schemes;
        private boolean enabled;
        private long registeredAt;
        private Map<String, Object> metadata;
    }
}
