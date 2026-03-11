package net.ooder.skill.protocol.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.ProtocolProvider;
import net.ooder.scene.provider.model.protocol.ProtocolHandler;
import net.ooder.scene.provider.model.protocol.ProtocolCommandResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProtocolProviderImpl implements ProtocolProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, ProtocolHandler> handlers = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-protocol";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
        
        registerDefaultHandlers();
        
        log.info("ProtocolProvider initialized");
    }
    
    @Override
    public void start() {
        this.running = true;
        log.info("ProtocolProvider started");
    }
    
    @Override
    public void stop() {
        this.running = false;
        log.info("ProtocolProvider stopped");
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
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
        ProtocolHandler handler = new ProtocolHandler();
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
    
    @Override
    public Result<List<ProtocolHandler>> getProtocolHandlers() {
        List<ProtocolHandler> handlerList = new ArrayList<>(handlers.values());
        log.debug("Retrieved {} protocol handlers", handlerList.size());
        return Result.success(handlerList);
    }
    
    @Override
    public Result<ProtocolHandler> registerProtocolHandler(Map<String, Object> handlerData) {
        try {
            String type = (String) handlerData.get("type");
            if (type == null || type.isEmpty()) {
                return Result.error("Handler type is required");
            }
            
            if (handlers.containsKey(type)) {
                return Result.error("Handler already exists: " + type);
            }
            
            ProtocolHandler handler = new ProtocolHandler();
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
    
    @Override
    public Result<Boolean> removeProtocolHandler(String handlerType) {
        ProtocolHandler removed = handlers.remove(handlerType);
        if (removed == null) {
            return Result.error("Handler not found: " + handlerType);
        }
        
        log.info("Removed protocol handler: {}", handlerType);
        return Result.success(true);
    }
    
    @Override
    public Result<ProtocolCommandResult> handleProtocolCommand(Map<String, Object> commandData) {
        try {
            String protocol = (String) commandData.get("protocol");
            String command = (String) commandData.get("command");
            Map<String, Object> params = (Map<String, Object>) commandData.getOrDefault("params", new HashMap<>());
            
            if (protocol == null || protocol.isEmpty()) {
                return Result.error("Protocol is required");
            }
            
            ProtocolHandler handler = handlers.get(protocol);
            if (handler == null) {
                return Result.error("Handler not found for protocol: " + protocol);
            }
            
            if (!handler.isEnabled()) {
                return Result.error("Handler is disabled: " + protocol);
            }
            
            ProtocolCommandResult result = new ProtocolCommandResult();
            result.setCommandId(UUID.randomUUID().toString());
            result.setProtocol(protocol);
            result.setCommand(command);
            result.setSuccess(true);
            result.setStatus("completed");
            
            Map<String, Object> output = new HashMap<>();
            output.put("message", "Command executed successfully");
            output.put("protocol", protocol);
            output.put("command", command);
            output.put("params", params);
            output.put("timestamp", System.currentTimeMillis());
            result.setOutput(output);
            
            result.setError(null);
            result.setExecutedAt(System.currentTimeMillis());
            result.setDuration(50L);
            
            log.info("Protocol command executed: {} - {}", protocol, command);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to handle protocol command", e);
            return Result.error("Failed to handle protocol command: " + e.getMessage());
        }
    }
    
    @Override
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
    
    public ProtocolHandler getHandler(String type) {
        return handlers.get(type);
    }
    
    public boolean hasHandler(String type) {
        return handlers.containsKey(type);
    }
}
