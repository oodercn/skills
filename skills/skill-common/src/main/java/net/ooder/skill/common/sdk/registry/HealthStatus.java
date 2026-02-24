package net.ooder.skill.common.sdk.registry;

/**
 * Skill 健康状态枚举
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public enum HealthStatus {

    /**
     * 未知状态
     */
    UNKNOWN,

    /**
     * 初始化中
     */
    INITIALIZING,

    /**
     * 已注册但未启动
     */
    REGISTERED,

    /**
     * 启动中
     */
    STARTING,

    /**
     * 运行中且健康
     */
    HEALTHY,

    /**
     * 运行中但不健康
     */
    UNHEALTHY,

    /**
     * 停止中
     */
    STOPPING,

    /**
     * 已停止
     */
    STOPPED,

    /**
     * 故障
     */
    FAILED,

    /**
     * 更新中
     */
    UPDATING,

    /**
     * 卸载中
     */
    UNINSTALLING,

    /**
     * 已卸载
     */
    UNINSTALLED
}
