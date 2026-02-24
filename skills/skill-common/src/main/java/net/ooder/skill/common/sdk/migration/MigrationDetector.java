package net.ooder.skill.common.sdk.migration;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.common.sdk.registry.SkillInfo;
import net.ooder.skill.common.sdk.registry.SkillRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 迁移检测器
 * 自动检测 Skill 降级/升级需求
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class MigrationDetector {

    @Autowired
    private SkillRegistry skillRegistry;

    /**
     * 迁移规则缓存: sourceSkill -> MigrationRule
     */
    private final Map<String, MigrationRule> migrationRules = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("MigrationDetector initialized");
        loadMigrationRules();
    }

    /**
     * 加载迁移规则
     */
    private void loadMigrationRules() {
        // VFS Local -> VFS Database
        migrationRules.put("skill-vfs-local", MigrationRule.builder()
            .sourceSkill("skill-vfs-local")
            .targetSkill("skill-vfs-database")
            .triggerCondition(MigrationCondition.DATA_SIZE_THRESHOLD)
            .threshold(1000) // 1000个文件触发迁移
            .priority(MigrationPriority.RECOMMENDED)
            .build());

        // Org Dingding -> Org Local (降级)
        migrationRules.put("skill-org-dingding", MigrationRule.builder()
            .sourceSkill("skill-org-dingding")
            .targetSkill("skill-org-local")
            .triggerCondition(MigrationCondition.SERVICE_UNAVAILABLE)
            .priority(MigrationPriority.HIGH)
            .build());

        log.info("Loaded {} migration rules", migrationRules.size());
    }

    /**
     * 检测所有可能的迁移
     *
     * @return List of MigrationOpportunity
     */
    public List<MigrationOpportunity> detectAll() {
        List<MigrationOpportunity> opportunities = new ArrayList<>();

        for (MigrationRule rule : migrationRules.values()) {
            if (skillRegistry.hasSkill(rule.getSourceSkill())) {
                MigrationOpportunity opportunity = detectForRule(rule);
                if (opportunity != null) {
                    opportunities.add(opportunity);
                }
            }
        }

        return opportunities;
    }

    /**
     * 针对特定 Skill 检测迁移
     *
     * @param skillId Skill ID
     * @return MigrationOpportunity
     */
    public MigrationOpportunity detectForSkill(String skillId) {
        MigrationRule rule = migrationRules.get(skillId);
        if (rule == null) {
            return null;
        }

        return detectForRule(rule);
    }

    /**
     * 根据规则检测迁移
     */
    private MigrationOpportunity detectForRule(MigrationRule rule) {
        String sourceSkill = rule.getSourceSkill();
        String targetSkill = rule.getTargetSkill();

        // 检查目标 Skill 是否已安装
        if (skillRegistry.hasSkill(targetSkill)) {
            return null; // 已安装目标 Skill，无需迁移
        }

        // 评估迁移条件
        MigrationAssessment assessment = assessCondition(rule);

        if (assessment.isShouldMigrate()) {
            return MigrationOpportunity.builder()
                .sourceSkill(sourceSkill)
                .targetSkill(targetSkill)
                .reason(assessment.getReason())
                .priority(rule.getPriority())
                .estimatedImpact(assessment.getImpact())
                .build();
        }

        return null;
    }

    /**
     * 评估迁移条件
     */
    private MigrationAssessment assessCondition(MigrationRule rule) {
        switch (rule.getTriggerCondition()) {
            case DATA_SIZE_THRESHOLD:
                return assessDataSizeThreshold(rule);
            case SERVICE_UNAVAILABLE:
                return assessServiceAvailability(rule);
            case PERFORMANCE_DEGRADATION:
                return assessPerformance(rule);
            case MANUAL_TRIGGER:
                return MigrationAssessment.builder()
                    .shouldMigrate(false)
                    .reason("Manual migration required")
                    .build();
            default:
                return MigrationAssessment.builder()
                    .shouldMigrate(false)
                    .reason("Unknown condition")
                    .build();
        }
    }

    /**
     * 评估数据大小阈值
     */
    private MigrationAssessment assessDataSizeThreshold(MigrationRule rule) {
        String sourceSkill = rule.getSourceSkill();
        int threshold = rule.getThreshold();

        // TODO: 实际检测数据大小
        int currentSize = estimateDataSize(sourceSkill);

        if (currentSize > threshold) {
            return MigrationAssessment.builder()
                .shouldMigrate(true)
                .reason(String.format("Data size (%d) exceeds threshold (%d)", currentSize, threshold))
                .impact(MigrationImpact.MEDIUM)
                .build();
        }

        return MigrationAssessment.builder()
            .shouldMigrate(false)
            .reason("Data size within threshold")
            .build();
    }

    /**
     * 评估服务可用性
     */
    private MigrationAssessment assessServiceAvailability(MigrationRule rule) {
        String sourceSkill = rule.getSourceSkill();

        // TODO: 实际检测服务可用性
        boolean isAvailable = checkServiceAvailability(sourceSkill);

        if (!isAvailable) {
            return MigrationAssessment.builder()
                .shouldMigrate(true)
                .reason("Source service unavailable, fallback required")
                .impact(MigrationImpact.HIGH)
                .build();
        }

        return MigrationAssessment.builder()
            .shouldMigrate(false)
            .reason("Service available")
            .build();
    }

    /**
     * 评估性能
     */
    private MigrationAssessment assessPerformance(MigrationRule rule) {
        String sourceSkill = rule.getSourceSkill();

        // TODO: 实际检测性能指标
        double performanceScore = measurePerformance(sourceSkill);

        if (performanceScore < 0.5) {
            return MigrationAssessment.builder()
                .shouldMigrate(true)
                .reason("Performance degraded: " + performanceScore)
                .impact(MigrationImpact.MEDIUM)
                .build();
        }

        return MigrationAssessment.builder()
            .shouldMigrate(false)
            .reason("Performance acceptable")
            .build();
    }

    /**
     * 估计数据大小
     */
    private int estimateDataSize(String skillId) {
        // TODO: 实现实际数据大小估计
        return 0;
    }

    /**
     * 检查服务可用性
     */
    private boolean checkServiceAvailability(String skillId) {
        // TODO: 实现实际可用性检查
        return true;
    }

    /**
     * 测量性能
     */
    private double measurePerformance(String skillId) {
        // TODO: 实现实际性能测量
        return 1.0;
    }

    /**
     * 添加迁移规则
     */
    public void addMigrationRule(MigrationRule rule) {
        migrationRules.put(rule.getSourceSkill(), rule);
        log.info("Migration rule added: {} -> {}", rule.getSourceSkill(), rule.getTargetSkill());
    }

    /**
     * 移除迁移规则
     */
    public void removeMigrationRule(String sourceSkill) {
        migrationRules.remove(sourceSkill);
        log.info("Migration rule removed for: {}", sourceSkill);
    }
}
