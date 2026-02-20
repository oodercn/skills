package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT消息处理器链
 */
public class MqttHandlerChain {
    
    private static final Logger log = LoggerFactory.getLogger(MqttHandlerChain.class);
    
    private final List<MqttMessageHandler> handlers = new CopyOnWriteArrayList<MqttMessageHandler>();
    private volatile boolean sorted = false;
    
    public void addHandler(MqttMessageHandler handler) {
        handlers.add(handler);
        sorted = false;
        log.info("Added message handler: {}", handler.getHandlerId());
    }
    
    public void removeHandler(String handlerId) {
        handlers.removeIf(h -> h.getHandlerId().equals(handlerId));
        log.info("Removed message handler: {}", handlerId);
    }
    
    public void dispatch(MqttContext context, MqttMessage message) {
        ensureSorted();
        
        boolean handled = false;
        for (MqttMessageHandler handler : handlers) {
            try {
                if (handler.canHandle(message.getTopic())) {
                    handler.handle(context, message);
                    handled = true;
                    log.debug("Message handled by: {}", handler.getHandlerId());
                }
            } catch (Exception e) {
                log.error("Handler {} failed: {}", handler.getHandlerId(), e.getMessage());
            }
        }
        
        if (!handled) {
            log.warn("No handler found for topic: {}", message.getTopic());
        }
    }
    
    public List<MqttMessageHandler> getHandlers() {
        ensureSorted();
        return new ArrayList<MqttMessageHandler>(handlers);
    }
    
    public MqttMessageHandler getHandler(String handlerId) {
        for (MqttMessageHandler handler : handlers) {
            if (handler.getHandlerId().equals(handlerId)) {
                return handler;
            }
        }
        return null;
    }
    
    public int getHandlerCount() {
        return handlers.size();
    }
    
    public void clear() {
        handlers.clear();
        sorted = false;
    }
    
    private void ensureSorted() {
        if (!sorted) {
            synchronized (this) {
                if (!sorted) {
                    Collections.sort(handlers, new Comparator<MqttMessageHandler>() {
                        @Override
                        public int compare(MqttMessageHandler h1, MqttMessageHandler h2) {
                            return Integer.compare(h1.getOrder(), h2.getOrder());
                        }
                    });
                    sorted = true;
                }
            }
        }
    }
    
    public static MqttHandlerChain createDefault() {
        MqttHandlerChain chain = new MqttHandlerChain();
        chain.addHandler(new TopicMessageHandler());
        chain.addHandler(new ImMessageHandler());
        chain.addHandler(new CommandMessageHandler());
        return chain;
    }
}
