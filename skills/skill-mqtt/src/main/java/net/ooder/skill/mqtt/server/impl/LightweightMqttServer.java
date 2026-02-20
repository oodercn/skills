package net.ooder.skill.mqtt.server.impl;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.context.MqttSessionManager;
import net.ooder.skill.mqtt.handler.MqttHandlerChain;
import net.ooder.skill.mqtt.handler.MqttMessageHandler;
import net.ooder.skill.mqtt.message.MqttMessage;
import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import net.ooder.skill.mqtt.server.MqttServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轻量级MQTT服务端实�?- 降级方案
 * 
 * <p>基于内存的轻量级MQTT Broker实现，适用于：</p>
 * <ul>
 *   <li>开发测试环�?/li>
 *   <li>小规模部�?/li>
 *   <li>作为企业级Broker的降级方�?/li>
 * </ul>
 */
public class LightweightMqttServer implements MqttServer {
    
    private static final Logger log = LoggerFactory.getLogger(LightweightMqttServer.class);
    
    private static volatile LightweightMqttServer instance;
    private static final Object LOCK = new Object();
    
    private String serverId;
    private ServerStatus status = ServerStatus.CREATED;
    private MqttServerConfig config;
    
    private final MqttSessionManager sessionManager = MqttSessionManager.getInstance();
    private final MqttHandlerChain handlerChain = new MqttHandlerChain();
    private final Map<String, MqttContext> connections = new ConcurrentHashMap<String, MqttContext>();
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    
    private volatile boolean running = false;
    private long startTime;
    
    private LightweightMqttServer() {
        this.serverId = "lightweight-mqtt-" + System.currentTimeMillis();
        initDefaultHandlers();
    }
    
    public static LightweightMqttServer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new LightweightMqttServer();
                }
            }
        }
        return instance;
    }
    
    @Override
    public String getServerId() {
        return serverId;
    }
    
    @Override
    public ServerStatus getStatus() {
        return status;
    }
    
    @Override
    public void initialize(MqttServerConfig config) throws MqttServerException {
        if (status != ServerStatus.CREATED && status != ServerStatus.STOPPED) {
            throw new MqttServerException("Server already initialized", MqttServerException.CONFIG_ERROR);
        }
        
        this.config = config;
        this.serverId = config.getServerId();
        
        log.info("LightweightMqttServer initialized: serverId={}, port={}", serverId, config.getPort());
    }
    
    @Override
    public void start() throws MqttServerException {
        if (status == ServerStatus.RUNNING) {
            log.warn("Server already running");
            return;
        }
        
        status = ServerStatus.STARTING;
        
        try {
            if (config == null) {
                config = MqttServerConfig.builder().build();
            }
            
            startTime = System.currentTimeMillis();
            running = true;
            status = ServerStatus.RUNNING;
            
            log.info("LightweightMqttServer started: serverId={}, port={}, websocketPort={}", 
                serverId, config.getPort(), config.getWebsocketPort());
            
        } catch (Exception e) {
            status = ServerStatus.ERROR;
            throw new MqttServerException("Failed to start server", e, MqttServerException.CONFIG_ERROR);
        }
    }
    
    @Override
    public void stop() throws MqttServerException {
        if (status != ServerStatus.RUNNING) {
            return;
        }
        
        status = ServerStatus.STOPPING;
        running = false;
        
        try {
            for (MqttContext context : connections.values()) {
                onDisconnect(context.getSessionId());
            }
            
            connections.clear();
            sessionManager.clearAll();
            
            status = ServerStatus.STOPPED;
            log.info("LightweightMqttServer stopped: serverId={}", serverId);
            
        } catch (Exception e) {
            status = ServerStatus.ERROR;
            throw new MqttServerException("Failed to stop server", e);
        }
    }
    
    @Override
    public MqttServerConfig getConfig() {
        return config;
    }
    
    @Override
    public void onConnect(MqttContext context) throws MqttServerException {
        if (!running) {
            throw new MqttServerException("Server not running", MqttServerException.CONNECTION_ERROR);
        }
        
        if (connectionCount.get() >= config.getMaxConnections()) {
            throw new MqttServerException("Max connections reached", MqttServerException.CONNECTION_ERROR);
        }
        
        String sessionId = context.getSessionId();
        connections.put(sessionId, context);
        sessionManager.createSession(sessionId, context.getClientId());
        
        connectionCount.incrementAndGet();
        
        log.info("Client connected: sessionId={}, clientId={}, ip={}", 
            sessionId, context.getClientId(), context.getClientIp());
    }
    
    @Override
    public void onDisconnect(String sessionId) {
        MqttContext context = connections.remove(sessionId);
        if (context != null) {
            context.clear();
            sessionManager.removeSession(sessionId);
            connectionCount.decrementAndGet();
            
            log.info("Client disconnected: sessionId={}, clientId={}", sessionId, context.getClientId());
        }
    }
    
    @Override
    public boolean isRunning() {
        return running && status == ServerStatus.RUNNING;
    }
    
    @Override
    public int getConnectedCount() {
        return connectionCount.get();
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("serverId", serverId);
        stats.put("status", status.name());
        stats.put("running", running);
        stats.put("uptime", System.currentTimeMillis() - startTime);
        stats.put("connections", connectionCount.get());
        stats.put("maxConnections", config != null ? config.getMaxConnections() : 0);
        stats.put("handlers", handlerChain.getHandlerCount());
        return stats;
    }
    
    public void addHandler(MqttMessageHandler handler) {
        handlerChain.addHandler(handler);
    }
    
    public void removeHandler(String handlerId) {
        handlerChain.removeHandler(handlerId);
    }
    
    public void dispatchMessage(MqttContext context, MqttMessage message) {
        handlerChain.dispatch(context, message);
    }
    
    public MqttContext getConnection(String sessionId) {
        return connections.get(sessionId);
    }
    
    private void initDefaultHandlers() {
        handlerChain.addHandler(new net.ooder.skill.mqtt.handler.TopicMessageHandler());
        handlerChain.addHandler(new net.ooder.skill.mqtt.handler.ImMessageHandler());
        handlerChain.addHandler(new net.ooder.skill.mqtt.handler.CommandMessageHandler());
    }
}
