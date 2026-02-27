package net.ooder.skill.share.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.skill.share.api.SkillShareApi;
import net.ooder.skill.share.model.ReceivedSkill;
import net.ooder.skill.share.model.SharedSkill;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Skill鍒嗕韩鎺у埗鍣? * SDK 2.3 杩佺Щ鐗堟湰
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/skills/share")
@RequiredArgsConstructor
public class SkillShareController {
    
    private final SkillShareApi skillShareApi;
    
    /**
     * 鍒嗕韩Skill
     */
    @PostMapping
    public Result<SharedSkill> shareSkill(@RequestBody Map<String, Object> skillData) {
        log.info("Sharing skill: {}", skillData.get("skillName"));
        return skillShareApi.shareSkill(skillData);
    }
    
    /**
     * 鎺ユ敹Skill
     */
    @PostMapping("/{shareId}/receive")
    public Result<ReceivedSkill> receiveSkill(
            @PathVariable String shareId,
            @RequestParam String receivedBy) {
        log.info("Receiving skill: {} by {}", shareId, receivedBy);
        return skillShareApi.receiveSkill(shareId, receivedBy);
    }
    
    /**
     * 鑾峰彇鍒嗕韩鐨凷kill
     */
    @GetMapping("/shared/{shareId}")
    public Result<SharedSkill> getSharedSkill(@PathVariable String shareId) {
        return skillShareApi.getSharedSkill(shareId);
    }
    
    /**
     * 鑾峰彇鎺ユ敹鐨凷kill
     */
    @GetMapping("/received/{receiveId}")
    public Result<ReceivedSkill> getReceivedSkill(@PathVariable String receiveId) {
        return skillShareApi.getReceivedSkill(receiveId);
    }
    
    /**
     * 鍒楀嚭鍒嗕韩鐨凷kills
     */
    @GetMapping("/shared")
    public Result<List<SharedSkill>> listSharedSkills(@RequestParam String sharedBy) {
        return skillShareApi.listSharedSkills(sharedBy);
    }
    
    /**
     * 鍒楀嚭鎺ユ敹鐨凷kills
     */
    @GetMapping("/received")
    public Result<List<ReceivedSkill>> listReceivedSkills(@RequestParam String receivedBy) {
        return skillShareApi.listReceivedSkills(receivedBy);
    }
    
    /**
     * 鎾ら攢鍒嗕韩
     */
    @DeleteMapping("/shared/{shareId}")
    public Result<Boolean> revokeShare(@PathVariable String shareId) {
        log.info("Revoking share: {}", shareId);
        return skillShareApi.revokeShare(shareId);
    }
    
    /**
     * 鍒犻櫎鎺ユ敹鐨凷kill
     */
    @DeleteMapping("/received/{receiveId}")
    public Result<Boolean> deleteReceivedSkill(@PathVariable String receiveId) {
        log.info("Deleting received skill: {}", receiveId);
        return skillShareApi.deleteReceivedSkill(receiveId);
    }
    
    /**
     * 鏇存柊鍒嗕韩鏉冮檺
     */
    @PutMapping("/shared/{shareId}/permissions")
    public Result<SharedSkill> updateSharePermissions(
            @PathVariable String shareId,
            @RequestBody List<String> permissions) {
        log.info("Updating share permissions: {}", shareId);
        return skillShareApi.updateSharePermissions(shareId, permissions);
    }
    
    /**
     * 鍋ュ悍妫€鏌?     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("skill-share is healthy");
    }
}
