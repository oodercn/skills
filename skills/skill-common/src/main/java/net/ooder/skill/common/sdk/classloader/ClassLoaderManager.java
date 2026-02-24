package net.ooder.skill.common.sdk.classloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassLoader 管理器
 * 管理所有 Skill 的 ClassLoader 生命周期
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class ClassLoaderManager {

    /**
     * Skill ClassLoader 缓存: skillId -> SkillClassLoader
     */
    private final Map<String, SkillClassLoader> classLoaderCache = new ConcurrentHashMap<>();

    /**
     * 父 ClassLoader
     */
    private ClassLoader parentClassLoader;

    @PostConstruct
    public void init() {
        this.parentClassLoader = Thread.currentThread().getContextClassLoader();
        log.info("ClassLoaderManager initialized with parent: {}", parentClassLoader);
    }

    /**
     * 创建 Skill ClassLoader
     *
     * @param skillId Skill ID
     * @param jarPath JAR 文件路径
     * @return SkillClassLoader
     */
    public SkillClassLoader createClassLoader(String skillId, Path jarPath) {
        if (classLoaderCache.containsKey(skillId)) {
            log.warn("ClassLoader already exists for skill: {}, returning existing", skillId);
            return classLoaderCache.get(skillId);
        }

        SkillClassLoader classLoader = new SkillClassLoader(skillId, jarPath, parentClassLoader);
        classLoaderCache.put(skillId, classLoader);
        
        log.info("ClassLoader created for skill: {}", skillId);
        return classLoader;
    }

    /**
     * 获取 Skill ClassLoader
     *
     * @param skillId Skill ID
     * @return SkillClassLoader
     */
    public SkillClassLoader getClassLoader(String skillId) {
        return classLoaderCache.get(skillId);
    }

    /**
     * 检查是否存在 ClassLoader
     *
     * @param skillId Skill ID
     * @return true if exists  
     */
    public boolean hasClassLoader(String skillId) {
        return classLoaderCache.containsKey(skillId);
    }

    /**
     * 移除 ClassLoader
     *
     * @param skillId Skill ID
     */
    public void removeClassLoader(String skillId) {
        SkillClassLoader classLoader = classLoaderCache.remove(skillId);
        if (classLoader != null) {
            try {
                classLoader.close();
                log.info("ClassLoader removed for skill: {}", skillId);
            } catch (IOException e) {
                log.error("Failed to close ClassLoader for skill: {}", skillId, e);
            }
        }
    }

    /**
     * 获取所有 ClassLoader 数量
     *
     * @return count
     */
    public int getClassLoaderCount() {
        return classLoaderCache.size();
    }

    /**
     * 获取所有 Skill IDs
     *
     * @return list of skillIds
     */
    public java.util.List<String> getAllSkillIds() {
        return java.util.List.copyOf(classLoaderCache.keySet());
    }

    /**
     * 清理所有 ClassLoader
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up all ClassLoaders...");
        
        for (Map.Entry<String, SkillClassLoader> entry : classLoaderCache.entrySet()) {
            String skillId = entry.getKey();
            SkillClassLoader classLoader = entry.getValue();
            
            try {
                classLoader.close();
                log.info("ClassLoader closed for skill: {}", skillId);
            } catch (IOException e) {
                log.error("Failed to close ClassLoader for skill: {}", skillId, e);
            }
        }
        
        classLoaderCache.clear();
        log.info("All ClassLoaders cleaned up");
    }

    /**
     * 获取统计信息
     *
     * @return ClassLoaderStatistics
     */
    public ClassLoaderStatistics getStatistics() {
        int total = classLoaderCache.size();
        int active = 0;
        int closed = 0;
        
        for (SkillClassLoader classLoader : classLoaderCache.values()) {
            if (classLoader.isClosed()) {
                closed++;
            } else {
                active++;
            }
        }
        
        return ClassLoaderStatistics.builder()
            .totalClassLoaders(total)
            .activeClassLoaders(active)
            .closedClassLoaders(closed)
            .build();
    }
}
