package net.ooder.skill.common.sdk.scene;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 场景配置
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Data
@Builder
public class SceneConfiguration {

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 所需的 Skills
     */
    private List<String> requiredSkills;

    /**
     * 可选的 Skills
     */
    private List<String> optionalSkills;

    /**
     * 配置参数
     */
    private Map<String, String> parameters;

    /**
     * 是否自动启动
     */
    @Builder.Default
    private boolean autoStart = true;

    /**
     * 健康检查间隔（秒）
     */
    @Builder.Default
    private int healthCheckInterval = 30;
}
