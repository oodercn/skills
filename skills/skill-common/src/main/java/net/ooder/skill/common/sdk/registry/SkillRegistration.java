package net.ooder.skill.common.sdk.registry;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Skill 注册信息
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Data
@Builder
public class SkillRegistration {

    /**
     * Skill ID
     */
    private String skillId;

    /**
     * Skill 版本
     */
    private String version;

    /**
     * Skill 名称
     */
    private String name;

    /**
     * Skill 描述
     */
    private String description;

    /**
     * Skill 类型
     */
    private SkillType type;

    /**
     * JAR 文件路径
     */
    private Path jarPath;

    /**
     * 主类全名
     */
    private String mainClass;

    /**
     * 依赖的 Skills
     */
    private List<SkillDependency> dependencies;

    /**
     * 提供的能力
     */
    private List<String> capabilities;

    /**
     * 配置参数
     */
    private Map<String, String> config;

    /**
     * 注册时间
     */
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * 安装目录
     */
    private Path installDir;

    /**
     * ClassLoader 引用
     */
    private transient ClassLoader classLoader;

    /**
     * 是否为系统 Skill
     */
    @Builder.Default
    private boolean systemSkill = false;

    /**
     * 是否自动启动
     */
    @Builder.Default
    private boolean autoStart = true;

    /**
     * 更新注册信息
     */
    public void update(SkillRegistration newRegistration) {
        this.version = newRegistration.getVersion();
        this.jarPath = newRegistration.getJarPath();
        this.dependencies = newRegistration.getDependencies();
        this.capabilities = newRegistration.getCapabilities();
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
