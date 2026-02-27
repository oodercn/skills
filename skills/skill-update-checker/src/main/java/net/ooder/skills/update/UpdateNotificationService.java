package net.ooder.skills.update;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.update.UpdateInfo;
import net.ooder.sdk.update.UpdateResult;
import org.springframework.stereotype.Service;

/**
 * 鏇存柊閫氱煡鏈嶅姟
 *
 * 澶勭悊鏇存柊鐩稿叧鐨勯€氱煡閫昏緫
 */
@Slf4j
@Service
public class UpdateNotificationService {

    /**
     * 閫氱煡鏇存柊鍙敤
     */
    public void notifyUpdateAvailable(UpdateInfo update) {
        log.info("[NOTIFICATION] Update available: {} {} -> {}",
                update.getSkillId(),
                update.getCurrentVersion(),
                update.getNewVersion());

        // TODO: 瀹炵幇瀹為檯鐨勯€氱煡鏈哄埗锛圵ebSocket銆佹秷鎭槦鍒楃瓑锛?    }

    /**
     * 閫氱煡鏇存柊鎴愬姛
     */
    public void notifyUpdateSuccess(UpdateResult result) {
        log.info("[NOTIFICATION] Update success: {} -> {}",
                result.getSkillId(),
                result.getNewVersion());
    }

    /**
     * 閫氱煡鏇存柊澶辫触
     */
    public void notifyUpdateFailed(UpdateResult result) {
        log.error("[NOTIFICATION] Update failed: {} - {}",
                result.getSkillId(),
                result.getErrorMessage());
    }

    /**
     * 閫氱煡鍥炴粴鎴愬姛
     */
    public void notifyRollbackSuccess(String skillId) {
        log.info("[NOTIFICATION] Rollback success: {}", skillId);
    }

    /**
     * 閫氱煡鍥炴粴澶辫触
     */
    public void notifyRollbackFailed(String skillId, String errorMessage) {
        log.error("[NOTIFICATION] Rollback failed: {} - {}", skillId, errorMessage);
    }
}
