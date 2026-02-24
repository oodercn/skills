package net.ooder.skill.common.sdk.classloader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarFile;

/**
 * Skill 专用 ClassLoader
 * 实现类隔离和资源隔离
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
public class SkillClassLoader extends URLClassLoader {

    private final String skillId;
    private final Path jarPath;
    private volatile boolean closed = false;

    /**
     * 父 ClassLoader
     */
    private final ClassLoader parent;

    public SkillClassLoader(String skillId, Path jarPath, ClassLoader parent) {
        super(new URL[]{}, parent);
        this.skillId = skillId;
        this.jarPath = jarPath;
        this.parent = parent;
        
        try {
            addURL(jarPath.toUri().toURL());
            log.info("SkillClassLoader created for skill: {} from {}", skillId, jarPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SkillClassLoader for skill: " + skillId, e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 先尝试从当前 ClassLoader 加载
        try {
            return findClass(name);
        } catch (ClassNotFoundException e) {
            // 如果找不到，委托给父 ClassLoader
            return super.loadClass(name);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 先检查是否已加载
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }

        // 尝试从当前 ClassLoader 加载
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            // 如果找不到，让父 ClassLoader 尝试
            if (parent != null) {
                return parent.loadClass(name);
            }
            throw e;
        }
    }

    @Override
    public URL getResource(String name) {
        // 先尝试从当前 ClassLoader 获取
        URL resource = findResource(name);
        if (resource != null) {
            return resource;
        }
        
        // 如果找不到，委托给父 ClassLoader
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // 先尝试从当前 ClassLoader 获取
        Enumeration<URL> resources = findResources(name);
        
        // 合并父 ClassLoader 的资源
        Enumeration<URL> parentResources = super.getResources(name);
        
        return new Enumeration<URL>() {
            private boolean first = true;
            
            @Override
            public boolean hasMoreElements() {
                if (first) {
                    return resources.hasMoreElements() || parentResources.hasMoreElements();
                }
                return parentResources.hasMoreElements();
            }
            
            @Override
            public URL nextElement() {
                if (first && resources.hasMoreElements()) {
                    return resources.nextElement();
                }
                first = false;
                return parentResources.nextElement();
            }
        };
    }

    /**
     * 获取 Skill ID
     */
    public String getSkillId() {
        return skillId;
    }

    /**
     * 获取 JAR 路径
     */
    public Path getJarPath() {
        return jarPath;
    }

    /**
     * 检查是否已关闭
     */
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        
        synchronized (this) {
            if (closed) {
                return;
            }
            
            log.info("Closing SkillClassLoader for skill: {}", skillId);
            
            // 清除缓存
            clearCache();
            
            // 关闭父 ClassLoader
            super.close();
            
            closed = true;
            log.info("SkillClassLoader closed for skill: {}", skillId);
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        // 清除类缓存
        // 注意：Java 不提供直接清除类缓存的 API
        // 依赖 GC 回收
        
        log.debug("Cache cleared for SkillClassLoader: {}", skillId);
    }

    /**
     * 获取加载的类数量（估计）
     */
    public int getLoadedClassCount() {
        // 注意：这只是一个估计值
        return 0; // TODO: 实现统计
    }

    @Override
    public String toString() {
        return "SkillClassLoader{" +
            "skillId='" + skillId + '\'' +
            ", jarPath=" + jarPath +
            ", closed=" + closed +
            '}';
    }
}
