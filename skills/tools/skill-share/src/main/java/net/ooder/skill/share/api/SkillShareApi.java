package net.ooder.skill.share.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import net.ooder.skill.share.model.ReceivedSkill;
import net.ooder.skill.share.model.SharedSkill;

import java.util.List;
import java.util.Map;

/**
 * Skill鍒嗕韩API鎺ュ彛
 * 瀵瑰簲鏃х増SkillShareProvider
 */
public interface SkillShareApi {
    
    /**
     * 鑾峰彇API鍚嶇О
     */
    String getApiName();
    
    /**
     * 鑾峰彇鐗堟湰
     */
    String getVersion();
    
    /**
     * 鍒濆鍖?     */
    void initialize(SkillContext context);
    
    /**
     * 鍚姩
     */
    void start();
    
    /**
     * 鍋滄
     */
    void stop();
    
    /**
     * 鏄惁宸插垵濮嬪寲
     */
    boolean isInitialized();
    
    /**
     * 鏄惁杩愯涓?     */
    boolean isRunning();
    
    /**
     * 鍒嗕韩Skill
     */
    Result<SharedSkill> shareSkill(Map<String, Object> skillData);
    
    /**
     * 鎺ユ敹Skill
     */
    Result<ReceivedSkill> receiveSkill(String shareId, String receivedBy);
    
    /**
     * 鑾峰彇鍒嗕韩鐨凷kill
     */
    Result<SharedSkill> getSharedSkill(String shareId);
    
    /**
     * 鑾峰彇鎺ユ敹鐨凷kill
     */
    Result<ReceivedSkill> getReceivedSkill(String receiveId);
    
    /**
     * 鍒楀嚭鎵€鏈夊垎浜殑Skill
     */
    Result<List<SharedSkill>> listSharedSkills(String sharedBy);
    
    /**
     * 鍒楀嚭鎵€鏈夋帴鏀剁殑Skill
     */
    Result<List<ReceivedSkill>> listReceivedSkills(String receivedBy);
    
    /**
     * 鎾ら攢鍒嗕韩
     */
    Result<Boolean> revokeShare(String shareId);
    
    /**
     * 鍒犻櫎鎺ユ敹鐨凷kill
     */
    Result<Boolean> deleteReceivedSkill(String receiveId);
    
    /**
     * 鏇存柊鍒嗕韩鏉冮檺
     */
    Result<SharedSkill> updateSharePermissions(String shareId, List<String> permissions);
}
