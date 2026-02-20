package net.ooder.skill.mqtt.provider;

import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT服务提供者工�? */
public class MqttProviderFactory {
    
    private static final Logger log = LoggerFactory.getLogger(MqttProviderFactory.class);
    
    private static final MqttProviderFactory INSTANCE = new MqttProviderFactory();
    
    private final Map<String, MqttServiceProvider> providers = new ConcurrentHashMap<String, MqttServiceProvider>();
    private volatile boolean initialized = false;
    
    private MqttProviderFactory() {
    }
    
    public static MqttProviderFactory getInstance() {
        return INSTANCE;
    }
    
    public void initialize() {
        if (initialized) {
            return;
        }
        
        synchronized (this) {
            if (initialized) {
                return;
            }
            
            registerDefaultProviders();
            initialized = true;
            log.info("MqttProviderFactory initialized with {} providers", providers.size());
        }
    }
    
    public void register(MqttServiceProvider provider) {
        providers.put(provider.getProviderId(), provider);
        log.info("Registered MQTT provider: {} ({})", provider.getProviderName(), provider.getProviderId());
    }
    
    public void unregister(String providerId) {
        providers.remove(providerId);
        log.info("Unregistered MQTT provider: {}", providerId);
    }
    
    public MqttServiceProvider getProvider(String providerId) {
        return providers.get(providerId);
    }
    
    public MqttServer createServer(String providerId, MqttServerConfig config) {
        MqttServiceProvider provider = providers.get(providerId);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not found: " + providerId);
        }
        return provider.createServer(config);
    }
    
    public MqttServer createDefaultServer(MqttServerConfig config) {
        List<MqttServiceProvider> availableProviders = getAvailableProviders();
        
        if (availableProviders.isEmpty()) {
            log.warn("No available MQTT providers, using lightweight fallback");
            return new LightweightMqttProvider().createServer(config);
        }
        
        Collections.sort(availableProviders, new Comparator<MqttServiceProvider>() {
            @Override
            public int compare(MqttServiceProvider p1, MqttServiceProvider p2) {
                return Integer.compare(p1.getPriority(), p2.getPriority());
            }
        });
        
        MqttServiceProvider selected = availableProviders.get(0);
        log.info("Selected MQTT provider: {} (priority={})", selected.getProviderName(), selected.getPriority());
        
        return selected.createServer(config);
    }
    
    public List<MqttServiceProvider> getAvailableProviders() {
        List<MqttServiceProvider> available = new ArrayList<MqttServiceProvider>();
        for (MqttServiceProvider provider : providers.values()) {
            if (provider.isAvailable()) {
                available.add(provider);
            }
        }
        return available;
    }
    
    public List<MqttServiceProvider> getProvidersByType(MqttServiceProvider.ProviderType type) {
        List<MqttServiceProvider> result = new ArrayList<MqttServiceProvider>();
        for (MqttServiceProvider provider : providers.values()) {
            if (provider.getProviderType() == type) {
                result.add(provider);
            }
        }
        return result;
    }
    
    public List<MqttServiceProvider> getAllProviders() {
        return new ArrayList<MqttServiceProvider>(providers.values());
    }
    
    public int getProviderCount() {
        return providers.size();
    }
    
    private void registerDefaultProviders() {
        register(new LightweightMqttProvider());
        register(new EmqxEnterpriseProvider());
        register(new MosquittoEnterpriseProvider());
        register(new AliyunIoTProvider());
        register(new TencentIoTProvider());
    }
    
    public void clear() {
        providers.clear();
        initialized = false;
    }
}
