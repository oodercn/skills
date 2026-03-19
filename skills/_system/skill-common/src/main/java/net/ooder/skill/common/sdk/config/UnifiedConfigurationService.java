package net.ooder.skill.common.sdk.config;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * 缁熶竴閰嶇疆鏈嶅姟
 *
 * 涓?Skills 鎻愪緵缁熶竴鐨勯厤缃鐞嗚兘鍔? * 鏀寔鏈湴鏂囦欢銆佺幆澧冨彉閲忋€佺郴缁熷睘鎬х瓑澶氱閰嶇疆婧? *
 * @author Skills Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Service
public class UnifiedConfigurationService {

    private Path configRoot;
    private final Map<String, Configuration> configCache = new ConcurrentHashMap<>();
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();
    private WatchService watchService;

    @PostConstruct
    public void init() {
        try {
            configRoot = Paths.get(System.getProperty("user.home"), ".ooder", "config");
            Files.createDirectories(configRoot);
            startFileWatcher();
            log.info("UnifiedConfigurationService initialized, config root: {}", configRoot);
        } catch (Exception e) {
            log.error("Failed to initialize configuration service", e);
        }
    }

    public Configuration getConfiguration(String configId) {
        Configuration cached = configCache.get(configId);
        if (cached != null) {
            return cached;
        }
        Configuration config = loadFromFile(configId);
        if (config != null) {
            configCache.put(configId, config);
            return config;
        }
        return Configuration.builder()
                .configId(configId)
                .build();
    }

    public String getString(String configId, String key, String defaultValue) {
        Configuration config = getConfiguration(configId);
        return config.getString(key, defaultValue);
    }

    public int getInt(String configId, String key, int defaultValue) {
        Configuration config = getConfiguration(configId);
        return config.getInt(key, defaultValue);
    }

    public boolean getBoolean(String configId, String key, boolean defaultValue) {
        Configuration config = getConfiguration(configId);
        return config.getBoolean(key, defaultValue);
    }

    public void saveConfiguration(String configId, Configuration configuration) {
        try {
            Path configPath = getConfigPath(configId);
            String yamlContent = toYaml(configuration);
            writeString(configPath, yamlContent);
            configCache.put(configId, configuration);
            notifyListeners(configId, configuration);
            log.info("Configuration saved: {}", configId);
        } catch (IOException e) {
            log.error("Failed to save configuration: {}", configId, e);
            throw new RuntimeException("Failed to save configuration", e);
        }
    }

    public void setValue(String configId, String key, String value) {
        Configuration config = getConfiguration(configId);
        config.setValue(key, value);
        saveConfiguration(configId, config);
    }

    public void addListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String configId, Configuration configuration) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChanged(configId, configuration);
            } catch (Exception e) {
                log.error("Config change listener failed for: {}", configId, e);
            }
        }
    }

    private void startFileWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            configRoot.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            Thread watcherThread = new Thread(this::watchConfigFiles);
            watcherThread.setDaemon(true);
            watcherThread.setName("config-file-watcher");
            watcherThread.start();

            log.info("Config file watcher started");
        } catch (IOException e) {
            log.error("Failed to start file watcher", e);
        }
    }

    private void watchConfigFiles() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path fileName = (Path) event.context();
                    if (fileName.toString().endsWith(".yaml") || fileName.toString().endsWith(".yml")) {
                        String configId = fileName.toString().replace(".yaml", "").replace(".yml", "");
                        log.info("Config file changed: {}", configId);
                        Configuration config = loadFromFile(configId);
                        if (config != null) {
                            configCache.put(configId, config);
                            notifyListeners(configId, config);
                        }
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Path getConfigPath(String configId) {
        return configRoot.resolve(configId + ".yaml");
    }

    private Configuration loadFromFile(String configId) {
        try {
            Path configPath = getConfigPath(configId);
            if (!Files.exists(configPath)) {
                return null;
            }
            String content = readString(configPath);
            return fromYaml(content, configId);
        } catch (IOException e) {
            log.error("Failed to load configuration: {}", configId, e);
            return null;
        }
    }

    private String toYaml(Configuration configuration) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Configuration: ").append(configuration.getConfigId()).append("\n");
        Map<String, String> values = configuration.getValues();
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }

    private Configuration fromYaml(String content, String configId) {
        Configuration config = Configuration.builder()
                .configId(configId)
                .build();

        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                config.setValue(key, value);
            }
        }
        return config;
    }

    private void writeString(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    private String readString(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Data
    @Builder
    public static class Configuration {
        private String configId;
        private String version;
        private String description;

        @Builder.Default
        private Map<String, String> values = new ConcurrentHashMap<>();

        public String getString(String key, String defaultValue) {
            return values != null ? values.getOrDefault(key, defaultValue) : defaultValue;
        }

        public int getInt(String key, int defaultValue) {
            try {
                String value = values != null ? values.get(key) : null;
                return value != null ? Integer.parseInt(value) : defaultValue;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            String value = values != null ? values.get(key) : null;
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        }

        public void setValue(String key, String value) {
            if (values == null) {
                values = new ConcurrentHashMap<>();
            }
            values.put(key, value);
        }
    }

    public interface ConfigChangeListener {
        void onConfigChanged(String configId, Configuration configuration);
    }
}
