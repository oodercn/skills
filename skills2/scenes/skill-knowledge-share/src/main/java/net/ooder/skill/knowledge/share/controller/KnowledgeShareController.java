package net.ooder.skill.knowledge.share.controller;

import net.ooder.skill.knowledge.share.dto.AccessLogResponse;
import net.ooder.skill.knowledge.share.dto.ApiResponse;
import net.ooder.skill.knowledge.share.dto.CollaborationResponse;
import net.ooder.skill.knowledge.share.dto.CreateShareRequest;
import net.ooder.skill.knowledge.share.dto.OperationResultResponse;
import net.ooder.skill.knowledge.share.dto.PermissionCheckResponse;
import net.ooder.skill.knowledge.share.dto.PermissionResponse;
import net.ooder.skill.knowledge.share.dto.ShareResponse;
import net.ooder.skill.knowledge.share.dto.VerifyShareResponse;
import net.ooder.skill.knowledge.share.service.KnowledgeShareService;
import net.ooder.skill.knowledge.share.service.KnowledgeShareService.*;
import net.ooder.skill.knowledge.share.enums.PermissionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/knowledge-share")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class KnowledgeShareController {
    
    @Autowired
    private KnowledgeShareService knowledgeShareService;
    
    @PostMapping("/permission/grant")
    public ResponseEntity<ApiResponse<PermissionResponse>> grantPermission(
            @RequestParam String kbId,
            @RequestParam String userId,
            @RequestParam String permissionType,
            @RequestParam String grantedBy) {
        
        PermissionType permType = PermissionType.valueOf(permissionType);
        Permission permission = knowledgeShareService.grantPermission(kbId, userId, permType, grantedBy);
        
        return ResponseEntity.ok(new ApiResponse<>(toPermissionResponse(permission)));
    }
    
    @GetMapping("/permission")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> listPermissions(@RequestParam String kbId) {
        List<Permission> permissions = knowledgeShareService.listPermissions(kbId);
        List<PermissionResponse> list = permissions.stream()
            .map(this::toPermissionResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(list));
    }
    
    @GetMapping("/permission/check")
    public ResponseEntity<ApiResponse<PermissionCheckResponse>> checkPermission(
            @RequestParam String kbId,
            @RequestParam String userId) {
        
        Permission permission = knowledgeShareService.checkPermission(kbId, userId);
        
        PermissionCheckResponse response = new PermissionCheckResponse();
        if (permission != null) {
            response.setHasPermission(true);
            response.setPermissionType(permission.getPermissionType().getCode());
            response.setPermissionName(permission.getPermissionType().getName());
        } else {
            response.setHasPermission(false);
        }
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    @DeleteMapping("/permission/{permissionId}")
    public ResponseEntity<ApiResponse<OperationResultResponse>> revokePermission(@PathVariable String permissionId) {
        boolean removed = knowledgeShareService.revokePermission(permissionId);
        
        OperationResultResponse response = new OperationResultResponse();
        response.setRemoved(removed);
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    @PostMapping("/share")
    public ResponseEntity<ApiResponse<ShareResponse>> createShare(@RequestBody CreateShareRequest request) {
        String kbId = request.getKbId();
        String ownerId = request.getOwnerId();
        Integer expireDays = request.getExpireDays();
        String password = request.getPassword();
        Integer maxAccessCount = request.getMaxAccessCount();
        
        Share share = knowledgeShareService.createShare(kbId, ownerId, expireDays, password, maxAccessCount);
        
        return ResponseEntity.ok(new ApiResponse<>(toShareResponse(share)));
    }
    
    @GetMapping("/share")
    public ResponseEntity<ApiResponse<List<ShareResponse>>> listShares(
            @RequestParam(required = false) String kbId,
            @RequestParam(required = false) String status) {
        
        List<Share> shares = knowledgeShareService.listShares(kbId, status);
        List<ShareResponse> list = shares.stream()
            .map(this::toShareResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponse<>(list));
    }
    
    @GetMapping("/share/{shareId}")
    public ResponseEntity<ApiResponse<ShareResponse>> getShare(@PathVariable String shareId) {
        Share share = knowledgeShareService.getShare(shareId);
        return ResponseEntity.ok(new ApiResponse<>(toShareResponse(share)));
    }
    
    @GetMapping("/share/{shareCode}/verify")
    public ResponseEntity<ApiResponse<VerifyShareResponse>> verifyShare(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password,
            @RequestParam String visitorId) {
        
        Share share = knowledgeShareService.validateShare(shareCode, password, visitorId);
        
        VerifyShareResponse response = new VerifyShareResponse();
        if (share != null) {
            response.setValid(true);
            response.setKbId(share.getKbId());
            response.setShareId(share.getShareId());
        } else {
            response.setValid(false);
            response.setMessage("分享链接无效或已过期");
        }
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    @PutMapping("/share/{shareId}/revoke")
    public ResponseEntity<ApiResponse<ShareResponse>> revokeShare(@PathVariable String shareId) {
        Share share = knowledgeShareService.revokeShare(shareId);
        return ResponseEntity.ok(new ApiResponse<>(toShareResponse(share)));
    }
    
    @DeleteMapping("/share/{shareId}")
    public ResponseEntity<ApiResponse<OperationResultResponse>> deleteShare(@PathVariable String shareId) {
        boolean removed = knowledgeShareService.deleteShare(shareId);
        
        OperationResultResponse response = new OperationResultResponse();
        response.setRemoved(removed);
        
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    
    @GetMapping("/share/{shareId}/logs")
    public ResponseEntity<ApiResponse<List<AccessLogResponse>>> getAccessLogs(@PathVariable String shareId) {
        List<AccessLog> logs = knowledgeShareService.getAccessLogs(shareId);
        List<AccessLogResponse> list = logs.stream()
            .map(this::toAccessLogResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(list));
    }
    
    @PostMapping("/collaboration")
    public ResponseEntity<ApiResponse<CollaborationResponse>> startCollaboration(
            @RequestParam String kbId,
            @RequestParam String initiatorId) {
        
        Collaboration collaboration = knowledgeShareService.startCollaboration(kbId, initiatorId);
        return ResponseEntity.ok(new ApiResponse<>(toCollaborationResponse(collaboration)));
    }
    
    @GetMapping("/collaboration/{kbId}")
    public ResponseEntity<ApiResponse<CollaborationResponse>> getCollaboration(@PathVariable String kbId) {
        Collaboration collaboration = knowledgeShareService.getCollaboration(kbId);
        return ResponseEntity.ok(new ApiResponse<>(toCollaborationResponse(collaboration)));
    }
    
    @PutMapping("/collaboration/{kbId}/stop")
    public ResponseEntity<ApiResponse<CollaborationResponse>> stopCollaboration(@PathVariable String kbId) {
        Collaboration collaboration = knowledgeShareService.stopCollaboration(kbId);
        return ResponseEntity.ok(new ApiResponse<>(toCollaborationResponse(collaboration)));
    }
    
    private PermissionResponse toPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setPermissionId(permission.getPermissionId());
        response.setKbId(permission.getKbId());
        response.setUserId(permission.getUserId());
        response.setPermissionType(permission.getPermissionType().getCode());
        response.setPermissionName(permission.getPermissionType().getName());
        response.setGrantedBy(permission.getGrantedBy());
        response.setGrantedAt(permission.getGrantedAt());
        return response;
    }
    
    private ShareResponse toShareResponse(Share share) {
        if (share == null) return null;
        ShareResponse response = new ShareResponse();
        response.setShareId(share.getShareId());
        response.setShareCode(share.getShareCode());
        response.setKbId(share.getKbId());
        response.setOwnerId(share.getOwnerId());
        response.setExpiresAt(share.getExpiresAt());
        response.setMaxAccessCount(share.getMaxAccessCount());
        response.setCurrentCount(share.getCurrentCount());
        response.setStatus(share.getStatus().getCode());
        response.setStatusName(share.getStatus().getName());
        response.setCreatedAt(share.getCreatedAt());
        return response;
    }
    
    private AccessLogResponse toAccessLogResponse(AccessLog log) {
        AccessLogResponse response = new AccessLogResponse();
        response.setLogId(log.getLogId());
        response.setShareId(log.getShareId());
        response.setVisitorId(log.getVisitorId());
        response.setVisitorIp(log.getVisitorIp());
        response.setAction(log.getAction());
        response.setAccessTime(log.getAccessTime());
        return response;
    }
    
    private CollaborationResponse toCollaborationResponse(Collaboration collaboration) {
        if (collaboration == null) return null;
        CollaborationResponse response = new CollaborationResponse();
        response.setCollaborationId(collaboration.getCollaborationId());
        response.setKbId(collaboration.getKbId());
        response.setInitiatorId(collaboration.getInitiatorId());
        response.setStatus(collaboration.getStatus().getCode());
        response.setStatusName(collaboration.getStatus().getName());
        response.setParticipants(collaboration.getParticipants());
        response.setStartedAt(collaboration.getStartedAt());
        response.setStoppedAt(collaboration.getStoppedAt());
        return response;
    }
}
