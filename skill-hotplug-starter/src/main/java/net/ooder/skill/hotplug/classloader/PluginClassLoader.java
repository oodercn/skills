package net.ooder.skill.hotplug.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Skill专用类加载器
 * 实现类隔离和双亲委派机制的灵活控制
 */
public class PluginClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(PluginClassLoader.class);

    // 父类加载器（通常是ApplicationClassLoader）
    private final ClassLoader parent;

    // Skill ID
    private final String skillId;

    // 已加载的类缓存
    private final ConcurrentMap<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    // 已加载的资源缓存
    private final ConcurrentMap<String, URL> resourceCache = new ConcurrentHashMap<>();

    // 父类优先的包前缀列表
    private static final String[] PARENT_FIRST_PACKAGES = {
            "java.",
            "javax.",
            "sun.",
            "com.sun.",
            "org.xml.",
            "org.w3c.",
            "org.ietf.jgss",
            "org.omg.",
            "com.oracle.",
            "jdk.",
            "net.ooder.core.",
            "net.ooder.agent.",
            "org.springframework.",
            "org.slf4j.",
            "org.apache.commons.logging.",
            "org.apache.logging.log4j.",
            "ch.qos.logback.",
            "com.fasterxml.jackson.",
            "io.micrometer.",
            "reactor.",
            "io.netty."
    };

    // 父类优先的类列表
    private static final String[] PARENT_FIRST_CLASSES = {
            "net.ooder.skill.hotplug.SkillLifecycle",
            "net.ooder.skill.hotplug.PluginContext",
            "net.ooder.skill.hotplug.model.SkillPackage",
            "net.ooder.skill.hotplug.config.SkillConfiguration"
    };

    public PluginClassLoader(String skillId, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.skillId = skillId;
        this.parent = parent;
        logger.debug("Created PluginClassLoader for skill: {}", skillId);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 检查已加载的类
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }

        // 同步加载
        synchronized (getClassLoadingLock(name)) {
            // 双重检查
            clazz = loadedClasses.get(name);
            if (clazz != null) {
                return clazz;
            }

            try {
                // 判断是否应该由父类加载器加载
                if (shouldLoadFromParent(name)) {
                    clazz = loadFromParent(name);
                    if (clazz != null) {
                        loadedClasses.put(name, clazz);
                        if (resolve) {
                            resolveClass(clazz);
                        }
                        return clazz;
                    }
                }

                // 尝试自己加载
                clazz = findClass(name);
                if (clazz != null) {
                    loadedClasses.put(name, clazz);
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    logger.debug("Loaded class from plugin [{}]: {}", skillId, name);
                    return clazz;
                }

            } catch (ClassNotFoundException e) {
                // 如果自己加载失败，尝试父类加载器
                clazz = loadFromParent(name);
                if (clazz != null) {
                    loadedClasses.put(name, clazz);
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
                throw e;
            }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * 从父类加载器加载
     */
    private Class<?> loadFromParent(String name) {
        try {
            if (parent != null) {
                return parent.loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            // 忽略，继续尝试
        }
        return null;
    }

    /**
     * 判断是否应该从父类加载器加载
     */
    private boolean shouldLoadFromParent(String name) {
        // 检查包前缀
        for (String prefix : PARENT_FIRST_PACKAGES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }

        // 检查具体类
        for (String className : PARENT_FIRST_CLASSES) {
            if (name.equals(className)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public URL findResource(String name) {
        // 先检查缓存
        URL resource = resourceCache.get(name);
        if (resource != null) {
            return resource;
        }

        // 查找资源
        resource = super.findResource(name);
        if (resource != null) {
            resourceCache.put(name, resource);
        }

        return resource;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        return super.findResources(name);
    }

    /**
     * 获取已加载的类数量
     */
    public int getLoadedClassCount() {
        return loadedClasses.size();
    }

    /**
     * 获取Skill ID
     */
    public String getSkillId() {
        return skillId;
    }

    /**
     * 获取已加载的类名列表
     */
    public java.util.Set<String> getLoadedClassNames() {
        return new java.util.HashSet<>(loadedClasses.keySet());
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        logger.debug("Cleaning up PluginClassLoader for skill: {}", skillId);
        loadedClasses.clear();
        resourceCache.clear();
    }

    @Override
    public void close() throws IOException {
        cleanup();
        super.close();
    }

    @Override
    public String toString() {
        return "PluginClassLoader[" + skillId + "]";
    }
}
