package net.ooder.skills.update;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.api.skill.Skill;
import net.ooder.sdk.api.skill.SkillContext;
import net.ooder.sdk.update.SkillUpdater;
import net.ooder.sdk.update.UpdateInfo;
import net.ooder.sdk.update.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 鏇存柊妫€鏌?Skill
 *
 * 瀹氭椂妫€鏌?Skills 鏇存柊锛屾彁渚涙洿鏂扮鐞嗘湇鍔? *
 * @author Skills Team
 * @version 1.0.0
 * @since 2026-02-24
 */
@Slf4j
@Component
@Skill(
        id = "skill-update-checker",
        name = "Update Checker Skill",
        version = "1.0.0",
        description = "Checks for skill updates and manages update process"
)
public class UpdateCheckerSkill {

    @Autowired
    private SkillUpdater skillUpdater;

    @Autowired
    private UpdateNotificationService notificationService;

    /**
     * 鑷姩妫€鏌ュ紑�?     */
    private boolean autoCheckEnabled = true;

    /**
     * 鑷姩鏇存柊寮€鍏筹紙浠呭 PATCH 鐗堟湰锛?     */
    private boolean autoUpdateEnabled = false;

    @PostConstruct
    public void init() {
        log.info("UpdateCheckerSkill initialized");
    }

    /**
     * 瀹氭椂妫€鏌ユ洿鏂帮紙姣忓皬鏃讹�?
     */
    @Scheduled(fixedRate = 3600000)
    public void scheduledUpdateCheck() {
        if (!autoCheckEnabled) {
            return;
        }

        log.info("Running scheduled update check...");
        checkForUpdates();
    }

    /**
     * 妫€鏌ユ墍鏈?Skills 鐨勬洿鏂?     *
     * @return 鍙敤鏇存柊鍒楄�?
     */
    public List<UpdateInfo> checkForUpdates() {
        try {
            List<UpdateInfo> updates = skillUpdater.checkForUpdates();

            if (updates.isEmpty()) {
                log.info("No updates available");
                return updates;
            }

            log.info("Found {} available updates", updates.size());

            // 閫氱煡鐢ㄦ埛
            for (UpdateInfo update : updates) {
                notificationService.notifyUpdateAvailable(update);

                // 鑷姩鏇存柊 PATCH 鐗堟�?
                if (autoUpdateEnabled && isPatchUpdate(update)) {
                    performAutoUpdate(update);
                }
            }

            return updates;

        } catch (Exception e) {
            log.error("Failed to check for updates", e);
            return List.of();
        }
    }

    /**
     * 妫€鏌ョ壒�?Skill 鐨勬洿鏂?     *
     * @param skillId Skill ID
     * @return 鏇存柊淇℃伅锛堝鏋滄湁�?     */
    public UpdateInfo checkForSkillUpdate(String skillId) {
        try {
            List<UpdateInfo> updates = skillUpdater.checkForUpdates();

            return updates.stream()
                    .filter(u -> u.getSkillId().equals(skillId))
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            log.error("Failed to check update for skill: {}", skillId, e);
            return null;
        }
    }

    /**
     * 鎵ц鏇存�?
     *
     * @param skillId     Skill ID
     * @param newVersion  鏂扮増鏈?     * @param strategy    鏇存柊绛栫暐
     * @return 鏇存柊缁撴灉
     */
    public UpdateResult performUpdate(String skillId, String newVersion, UpdateStrategy strategy) {
        log.info("Performing update for skill: {} to version: {} with strategy: {}",
                skillId, newVersion, strategy);

        try {
            UpdateResult result;

            switch (strategy) {
                case IMMEDIATE:
                    result = skillUpdater.performUpdate(skillId, newVersion, null);
                    break;
                case SCHEDULED:
                    result = scheduleUpdate(skillId, newVersion);
                    break;
                case BLUE_GREEN:
                    result = performBlueGreenUpdate(skillId, newVersion);
                    break;
                default:
                    result = skillUpdater.performUpdate(skillId, newVersion, null);
            }

            if (result.isSuccess()) {
                notificationService.notifyUpdateSuccess(result);
            } else {
                notificationService.notifyUpdateFailed(result);
            }

            return result;

        } catch (Exception e) {
            log.error("Update failed for skill: {}", skillId, e);
            return UpdateResult.failed(skillId, "Update error: " + e.getMessage());
        }
    }

    /**
     * 鎵归噺鏇存柊
     *
     * @param updates 鏇存柊鍒楄�?
     * @return 鏇存柊缁撴灉鍒楄�?
     */
    public List<UpdateResult> performBatchUpdate(List<UpdateInfo> updates) {
        log.info("Performing batch update for {} skills", updates.size());

        List<CompletableFuture<UpdateResult>> futures = updates.stream()
                .map(update -> CompletableFuture.supplyAsync(() ->
                        performUpdate(update.getSkillId(), update.getNewVersion(), UpdateStrategy.IMMEDIATE)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    /**
     * 鍥炴粴鏇存柊
     *
     * @param skillId Skill ID
     * @return 鍥炴粴缁撴灉
     */
    public UpdateResult rollbackUpdate(String skillId) {
        log.info("Rolling back update for skill: {}", skillId);

        try {
            UpdateResult result = skillUpdater.rollback(skillId);

            if (result.isSuccess()) {
                notificationService.notifyRollbackSuccess(skillId);
            } else {
                notificationService.notifyRollbackFailed(skillId, result.getErrorMessage());
            }

            return result;

        } catch (Exception e) {
            log.error("Rollback failed for skill: {}", skillId, e);
            return UpdateResult.failed(skillId, "Rollback error: " + e.getMessage());
        }
    }

    /**
     * 鑾峰彇鏇存柊鍘嗗�?
     *
     * @param skillId Skill ID
     * @return 鏇存柊鍘嗗彶鍒楄�?
     */
    public List<UpdateResult> getUpdateHistory(String skillId) {
        return skillUpdater.getUpdateHistory(skillId);
    }

    // ============================================================
    // 閰嶇疆鏂规硶
    // ============================================================

    /**
     * 璁剧疆鑷姩妫€鏌ュ紑鍏?     */
    public void setAutoCheckEnabled(boolean enabled) {
        this.autoCheckEnabled = enabled;
        log.info("Auto check {}", enabled ? "enabled" : "disabled");
    }

    /**
     * 璁剧疆鑷姩鏇存柊寮€鍏?     */
    public void setAutoUpdateEnabled(boolean enabled) {
        this.autoUpdateEnabled = enabled;
        log.info("Auto update {}", enabled ? "enabled" : "disabled");
    }

    // ============================================================
    // 绉佹湁鏂规硶
    // ============================================================

    private boolean isPatchUpdate(UpdateInfo update) {
        String current = update.getCurrentVersion();
        String next = update.getNewVersion();

        String[] currentParts = current.split("\\.");
        String[] nextParts = next.split("\\.");

        return currentParts.length >= 2 && nextParts.length >= 2
                && currentParts[0].equals(nextParts[0])
                && currentParts[1].equals(nextParts[1]);
    }

    private void performAutoUpdate(UpdateInfo update) {
        log.info("Auto-updating skill: {} from {} to {}",
                update.getSkillId(), update.getCurrentVersion(), update.getNewVersion());

        CompletableFuture.runAsync(() ->
                performUpdate(update.getSkillId(), update.getNewVersion(), UpdateStrategy.IMMEDIATE));
    }

    private UpdateResult scheduleUpdate(String skillId, String newVersion) {
        // TODO: 瀹炵幇瀹氭椂鏇存柊閫昏�?
        log.info("Scheduled update for skill: {} to version: {}", skillId, newVersion);
        return UpdateResult.success(skillId, newVersion);
    }

    private UpdateResult performBlueGreenUpdate(String skillId, String newVersion) {
        // TODO: 瀹炵幇钃濈豢閮ㄧ讲鏇存柊
        log.info("Blue-green update for skill: {} to version: {}", skillId, newVersion);
        return skillUpdater.performUpdate(skillId, newVersion, null);
    }

    // ============================================================
    // 鏇存柊绛栫暐鏋氫�?
    // ============================================================

    public enum UpdateStrategy {
        IMMEDIATE,      // 绔嬪嵆鏇存柊
        SCHEDULED,      // 瀹氭椂鏇存柊
        BLUE_GREEN      // 钃濈豢閮ㄧ讲鏇存�?
    }
}
