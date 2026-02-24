package net.ooder.skill.common.sdk.migration;

/**
 * 迁移优先级
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public enum MigrationPriority {

    /**
     * 紧急 - 立即执行
     */
    CRITICAL,

    /**
     * 高 - 24小时内执行
     */
    HIGH,

    /**
     * 中 - 一周内执行
     */
    MEDIUM,

    /**
     * 低 - 建议执行
     */
    LOW,

    /**
     * 推荐 - 可选执行
     */
    RECOMMENDED
}
