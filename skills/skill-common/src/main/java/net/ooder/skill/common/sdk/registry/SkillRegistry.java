package net.ooder.skill.common.sdk.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Skill 注册中心
 * 管理所有已安装 Skill 的元数据和状态
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class SkillRegistry {

    /**
     * Skill 注册表: skillId -> SkillRegistration
     */
    private final Map<String, SkillRegistration> registry = new ConcurrentHashMap<>();

    /**
     * Skill 健康状态: skillId -> HealthStatus
     */
    private final Map<String, HealthStatus> healthStatusMap = new ConcurrentHashMap<>();

    /**
     * Skill 版本信息: skillId -> version
     */
    private final Map<String, String> versionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("SkillRegistry initialized");
        // 从持久化存储加载已注册的 Skills
        loadFromStorage();
    }

    /**
     * 注册 Skill
     *
     * @param skillId        Skill ID
     * @param registration   Skill 注册信息
     */
    public void register(String skillId, SkillRegistration registration) {
        if (skillId == null || registration == null) {
            throw new IllegalArgumentException("skillId and registration cannot be null");
        }

        if (registry.containsKey(skillId)) {
            log.warn("Skill already registered: {}, will be overwritten", skillId);
        }

        registry.put(skillId, registration);
        versionMap.put(skillId, registration.getVersion());
        healthStatusMap.put(skillId, HealthStatus.INITIALIZING);

        log.info("Skill registered: {} v{}", skillId, registration.getVersion());
    }

    /**
     * 注销 Skill
     *
     * @param skillId Skill ID
     */
    public void unregister(String skillId) {
        if (skillId == null) {
            return;
        }

        SkillRegistration removed = registry.remove(skillId);
        if (removed != null) {
            versionMap.remove(skillId);
            healthStatusMap.remove(skillId);
            log.info("Skill unregistered: {}", skillId);
        }
    }

    /**
     * 获取 Skill 注册信息
     *
     * @param skillId Skill ID
     * @return SkillRegistration
     */
    public SkillRegistration getRegistration(String skillId) {
        return registry.get(skillId);
    }

    /**
     * 获取所有已注册的 Skill ID
     *
     * @return List of skillIds
     */
    public List<String> getAllSkillIds() {
        return List.copyOf(registry.keySet());
    }

    /**
     * 检查 Skill 是否已注册
     *
     * @param skillId Skill ID
     * @return true if registered
     */
    public boolean hasSkill(String skillId) {
        return skillId != null && registry.containsKey(skillId);
    }

    /**
     * 更新 Skill 健康状态
     *
     * @param skillId Skill ID
     * @param status  HealthStatus
     */
    public void updateHealth(String skillId, HealthStatus status) {
        if (skillId == null || status == null) {
            return;
        }

        HealthStatus oldStatus = healthStatusMap.put(skillId, status);
        if (oldStatus != status) {
            log.debug("Skill health status changed: {} {} -> {}", 
                skillId, oldStatus, status);
        }
    }

    /**
     * 获取 Skill 健康状态
     *
     * @param skillId Skill ID
     * @return HealthStatus
     */
    public HealthStatus getHealthStatus(String skillId) {
        return healthStatusMap.getOrDefault(skillId, HealthStatus.UNKNOWN);
    }

    /**
     * 获取 Skill 版本
     *
     * @param skillId Skill ID
     * @return version
     */
    public String getVersion(String skillId) {
        return versionMap.get(skillId);
    }

    /**
     * 获取所有健康状态为 HEALTHY 的 Skills
     *
     * @return List of skillIds
     */
    public List<String> getHealthySkills() {
        return healthStatusMap.entrySet().stream()
            .filter(e -> e.getValue() == HealthStatus.HEALTHY)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有健康状态为 UNHEALTHY 或 FAILED 的 Skills
     *
     * @return List of skillIds
     */
    public List<String> getUnhealthySkills() {
        return healthStatusMap.entrySet().stream()
            .filter(e -> e.getValue() == HealthStatus.UNHEALTHY 
                || e.getValue() == HealthStatus.FAILED)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * 从持久化存储加载
     */
    private void loadFromStorage() {
        // TODO: 实现从 JsonStorage 或数据库加载
        log.info("Loading registered skills from storage...");
    }

    /**
     * 保存到持久化存储
     */
    public void saveToStorage() {
        // TODO: 实现保存到 JsonStorage 或数据库
        log.info("Saving registered skills to storage...");
    }

    /**
     * 获取注册表统计信息
     *
     * @return RegistryStatistics
     */
    public RegistryStatistics getStatistics() {
        long total = registry.size();
        long healthy = getHealthySkills().size();
        long unhealthy = getUnhealthySkills().size();
        long initializing = healthStatusMap.values().stream()
            .filter(s -> s == HealthStatus.INITIALIZING)
            .count();

        return RegistryStatistics.builder()
            .totalSkills(total)
            .healthySkills(healthy)
            .unhealthySkills(unhealthy)
            .initializingSkills(initializing)
            .build();
    }
}
