package net.ooder.skill.share.controller;

import net.ooder.skill.share.dto.ShareCapabilityRequest;
import net.ooder.skill.share.dto.DelegateCapabilityRequest;
import net.ooder.skill.share.model.ShareRecord;
import net.ooder.skill.share.model.DelegateRecord;
import net.ooder.skill.share.service.CapabilityShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/capability")
public class CapabilityShareController {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityShareController.class);
    
    private CapabilityShareService shareService;

    public CapabilityShareController() {
    }

    public CapabilityShareController(CapabilityShareService shareService) {
        this.shareService = shareService;
    }

    public void setShareService(CapabilityShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping("/share")
    public Map<String, Object> shareCapability(@RequestBody ShareCapabilityRequest request) {
        log.info("[shareCapability] Sharing capability: {}", request.getSkillId());
        
        Map<String, Object> result = new HashMap<>();
        try {
            ShareRecord record = shareService.shareCapability(request);
            result.put("status", "success");
            result.put("data", record);
            result.put("message", "能力分享成功");
        } catch (Exception e) {
            log.error("[shareCapability] Failed to share capability", e);
            result.put("status", "error");
            result.put("message", "分享失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/delegate")
    public Map<String, Object> delegateCapability(@RequestBody DelegateCapabilityRequest request) {
        log.info("[delegateCapability] Delegating capability: {}", request.getSkillId());
        
        Map<String, Object> result = new HashMap<>();
        try {
            DelegateRecord record = shareService.delegateCapability(request);
            result.put("status", "success");
            result.put("data", record);
            result.put("message", "能力委派成功");
        } catch (Exception e) {
            log.error("[delegateCapability] Failed to delegate capability", e);
            result.put("status", "error");
            result.put("message", "委派失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/shared")
    public Map<String, Object> getSharedCapabilities(@RequestParam String userId) {
        log.info("[getSharedCapabilities] Getting shared capabilities for user: {}", userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<ShareRecord> records = shareService.getSharedCapabilities(userId);
            result.put("status", "success");
            result.put("data", records);
            result.put("total", records.size());
        } catch (Exception e) {
            log.error("[getSharedCapabilities] Failed to get shared capabilities", e);
            result.put("status", "error");
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/delegated")
    public Map<String, Object> getDelegatedCapabilities(@RequestParam String userId) {
        log.info("[getDelegatedCapabilities] Getting delegated capabilities for user: {}", userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<DelegateRecord> records = shareService.getDelegatedCapabilities(userId);
            result.put("status", "success");
            result.put("data", records);
            result.put("total", records.size());
        } catch (Exception e) {
            log.error("[getDelegatedCapabilities] Failed to get delegated capabilities", e);
            result.put("status", "error");
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/received")
    public Map<String, Object> getReceivedCapabilities(@RequestParam String userId) {
        log.info("[getReceivedCapabilities] Getting received capabilities for user: {}", userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<ShareRecord> records = shareService.getReceivedCapabilities(userId);
            result.put("status", "success");
            result.put("data", records);
            result.put("total", records.size());
        } catch (Exception e) {
            log.error("[getReceivedCapabilities] Failed to get received capabilities", e);
            result.put("status", "error");
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/share/{shareId}")
    public Map<String, Object> getShareDetail(@PathVariable String shareId) {
        log.info("[getShareDetail] Getting share detail: {}", shareId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            ShareRecord record = shareService.getShareDetail(shareId);
            if (record != null) {
                result.put("status", "success");
                result.put("data", record);
            } else {
                result.put("status", "error");
                result.put("message", "分享记录不存在");
            }
        } catch (Exception e) {
            log.error("[getShareDetail] Failed to get share detail", e);
            result.put("status", "error");
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/share/{shareId}")
    public Map<String, Object> cancelShare(@PathVariable String shareId, @RequestParam String userId) {
        log.info("[cancelShare] Cancelling share: {} by user: {}", shareId, userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = shareService.cancelShare(shareId, userId);
            if (success) {
                result.put("status", "success");
                result.put("message", "分享已取消");
            } else {
                result.put("status", "error");
                result.put("message", "取消失败，无权限或分享不存在");
            }
        } catch (Exception e) {
            log.error("[cancelShare] Failed to cancel share", e);
            result.put("status", "error");
            result.put("message", "取消失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/share/code/{shareCode}")
    public Map<String, Object> getShareByCode(@PathVariable String shareCode) {
        log.info("[getShareByCode] Getting share by code: {}", shareCode);
        
        Map<String, Object> result = new HashMap<>();
        try {
            ShareRecord record = shareService.getShareByCode(shareCode);
            if (record != null) {
                result.put("status", "success");
                result.put("data", record);
            } else {
                result.put("status", "error");
                result.put("message", "分享码无效或已过期");
            }
        } catch (Exception e) {
            log.error("[getShareByCode] Failed to get share by code", e);
            result.put("status", "error");
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/share/validate")
    public Map<String, Object> validateShare(@RequestBody Map<String, String> params) {
        String shareCode = params.get("shareCode");
        String password = params.get("password");
        
        log.info("[validateShare] Validating share code: {}", shareCode);
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean valid = shareService.validateShare(shareCode, password);
            if (valid) {
                result.put("status", "success");
                result.put("message", "分享码验证通过");
            } else {
                result.put("status", "error");
                result.put("message", "分享码无效、已过期或密码错误");
            }
        } catch (Exception e) {
            log.error("[validateShare] Failed to validate share", e);
            result.put("status", "error");
            result.put("message", "验证失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/received/{shareId}/accept")
    public Map<String, Object> acceptShare(@PathVariable String shareId, @RequestParam String userId) {
        log.info("[acceptShare] Accepting share: {} by user: {}", shareId, userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = shareService.acceptShare(shareId, userId);
            if (success) {
                result.put("status", "success");
                result.put("message", "已接受分享");
            } else {
                result.put("status", "error");
                result.put("message", "接受失败，分享不存在或无权限");
            }
        } catch (Exception e) {
            log.error("[acceptShare] Failed to accept share", e);
            result.put("status", "error");
            result.put("message", "接受失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/received/{shareId}/reject")
    public Map<String, Object> rejectShare(@PathVariable String shareId, 
                                           @RequestParam String userId,
                                           @RequestParam(required = false) String reason) {
        log.info("[rejectShare] Rejecting share: {} by user: {}", shareId, userId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = shareService.rejectShare(shareId, userId, reason);
            if (success) {
                result.put("status", "success");
                result.put("message", "已拒绝分享");
            } else {
                result.put("status", "error");
                result.put("message", "拒绝失败");
            }
        } catch (Exception e) {
            log.error("[rejectShare] Failed to reject share", e);
            result.put("status", "error");
            result.put("message", "拒绝失败: " + e.getMessage());
        }
        return result;
    }
}
