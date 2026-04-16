package net.ooder.skill.share.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 鎺ユ敹鐨凷kill
 */
@Data
public class ReceivedSkill {
    
    /**
     * 鎺ユ敹ID
     */
    private String receiveId;
    
    /**
     * 鍒嗕韩ID
     */
    private String shareId;
    
    /**
     * Skill ID
     */
    private String skillId;
    
    /**
     * Skill鍚嶇О
     */
    private String skillName;
    
    /**
     * Skill鐗堟湰
     */
    private String skillVersion;
    
    /**
     * 鎻忚堪
     */
    private String description;
    
    /**
     * 鍒嗕韩鑰?     */
    private String sharedBy;
    
    /**
     * 鎺ユ敹鑰?     */
    private String receivedBy;
    
    /**
     * 鎺ユ敹鏃堕棿
     */
    private Long receivedAt;
    
    /**
     * 杩囨湡鏃堕棿
     */
    private Long expiresAt;
    
    /**
     * 鐘舵€?     */
    private String status;
    
    /**
     * 鏉冮檺鍒楄〃
     */
    private List<String> permissions;
    
    /**
     * 鍏冩暟鎹?     */
    private Map<String, Object> metadata;
}
