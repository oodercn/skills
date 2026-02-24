package net.ooder.skill.common.sdk.driver;

/**
 * 驱动 Skill 接口
 * 定义基础设施驱动的标准接口
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public interface DriverSkill {

    /**
     * 获取驱动类型
     *
     * @return driver type (e.g., "vfs", "org", "msg", "database")
     */
    String getDriverType();

    /**
     * 获取驱动版本
     *
     * @return version
     */
    String getVersion();

    /**
     * 初始化驱动
     */
    void initialize();

    /**
     * 检查驱动是否健康
     *
     * @return true if healthy
     */
    boolean isHealthy();

    /**
     * 尝试恢复驱动
     */
    void recover();

    /**
     * 关闭驱动
     */
    void shutdown();
}
