package net.ooder.skill.common.sdk.driver;

/**
 * 驱动未找到异常
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(String message) {
        super(message);
    }

    public DriverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
