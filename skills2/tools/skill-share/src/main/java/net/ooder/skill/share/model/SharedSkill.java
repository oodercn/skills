package net.ooder.skill.share.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 鍒嗕韩鐨凷kill
 */
@Data
public class SharedSkill {
    
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
     * 鍒嗕韩缁欒皝
     */
    private List<String> sharedWith;
    
    /**
     * 鏉冮檺鍒楄〃
     */
    private List<String> permissions;
    
    /**
     * 鍒嗕韩鏃堕棿
     */
    private Long sharedAt;
    
    /**
     * 杩囨湡鏃堕棿
     */
    private Long expiresAt;
    
    /**
     * 鐘舵€?     */
    private String status;
    
    /**
     * 鍏冩暟鎹?     */
    private Map<String, Object> metadata;
}
