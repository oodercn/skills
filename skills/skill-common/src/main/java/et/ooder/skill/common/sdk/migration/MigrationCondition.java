package net.ooder.skill.common.sdk.migration;

/**
 * 迁移条件枚举
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public enum MigrationCondition {

    /**
     * 数据大小超过阈值
     */
    DATA_SIZE_THRESHOLD,

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE,

    /**
     * 性能下降
     */
    PERFORMANCE_DEGRADATION,

    /**
     * 手动触发
     */
    MANUAL_TRIGGER,

    /**
     * 版本过期
     */
    VERSION_DEPRECATED,

    /**
     * 安全漏洞
     */
    SECURITY_VULNERABILITY
}
