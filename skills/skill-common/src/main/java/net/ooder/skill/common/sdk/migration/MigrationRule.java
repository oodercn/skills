package net.ooder.skill.common.sdk.migration;

import lombok.Builder;
import lombok.Data;

/**
 * 迁移规则
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Data
@Builder
public class MigrationRule {

    /**
     * 源 Skill ID
     */
    private String sourceSkill;

    /**
     * 目标 Skill ID
     */
    private String targetSkill;

    /**
     * 触发条件
     */
    private MigrationCondition triggerCondition;

    /**
     * 阈值（根据条件类型）
     */
    private int threshold;

    /**
     * 优先级
     */
    private MigrationPriority priority;

    /**
     * 是否自动执行
     */
    @Builder.Default
    private boolean autoExecute = false;

    /**
     * 迁移脚本路径
     */
    private String migrationScriptPath;
}
