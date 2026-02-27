package net.ooder.skill.hotplug;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 热插拔配置属性
 */
@Component
@ConfigurationProperties(prefix = "ooder.skill.hotplug")
public class HotPlugProperties {

    /**
     * 插件目录
     */
    private String pluginDirectory = "./plugins";

    /**
     * 是否启用热插拔
     */
    private boolean enabled = true;

    /**
     * 是否自动加载插件目录中的插件
     */
    private boolean autoLoad = true;

    /**
     * 类加载器缓存大小
     */
    private int classLoaderCacheSize = 100;

    /**
     * 插件超时时间（秒）
     */
    private int pluginTimeout = 30;

    /**
     * 是否启用插件隔离
     */
    private boolean isolationEnabled = true;

    // Getters and Setters

    public String getPluginDirectory() {
        return pluginDirectory;
    }

    public void setPluginDirectory(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public int getClassLoaderCacheSize() {
        return classLoaderCacheSize;
    }

    public void setClassLoaderCacheSize(int classLoaderCacheSize) {
        this.classLoaderCacheSize = classLoaderCacheSize;
    }

    public int getPluginTimeout() {
        return pluginTimeout;
    }

    public void setPluginTimeout(int pluginTimeout) {
        this.pluginTimeout = pluginTimeout;
    }

    public boolean isIsolationEnabled() {
        return isolationEnabled;
    }

    public void setIsolationEnabled(boolean isolationEnabled) {
        this.isolationEnabled = isolationEnabled;
    }
}
