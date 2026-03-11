package net.ooder.skill.knowledge.share.service;

import net.ooder.skill.knowledge.share.enums.PermissionType;
import net.ooder.skill.knowledge.share.enums.ShareStatus;
import net.ooder.skill.knowledge.share.enums.CollaborationStatus;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KnowledgeShareService {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeShareService.class);
    
    @Value("${share.default-expire-days:7}")
    private int defaultExpireDays;
    
    @Value("${share.max-access-count:100}")
    private int defaultMaxAccessCount;
    
    @Autowired(required = false)
    private KnowledgeBaseService knowledgeBaseService;
    
    private final Map<String, Share> shareStore = new ConcurrentHashMap<>();
    private final Map<String, Permission> permissionStore = new ConcurrentHashMap<>();
    private final Map<String, AccessLog> accessLogStore = new ConcurrentHashMap<>();
    private final Map<String, Collaboration> collaborationStore = new ConcurrentHashMap<>();
    
    public Permission grantPermission(String kbId, String userId, PermissionType permissionType, String grantedBy) {
        log.info("Granting permission: kbId={}, userId={}, type={}", kbId, userId, permissionType);
        
        String permissionId = "perm-" + System.currentTimeMillis();
        Permission permission = new Permission();
        permission.setPermissionId(permissionId);
        permission.setKbId(kbId);
        permission.setUserId(userId);
        permission.setPermissionType(permissionType);
        permission.setGrantedBy(grantedBy);
        permission.setGrantedAt(new Date());
        
        permissionStore.put(permissionId, permission);
        
        return permission;
    }
    
    public boolean revokePermission(String permissionId) {
        log.info("Revoking permission: permissionId={}", permissionId);
        
        Permission permission = permissionStore.remove(permissionId);
        return permission != null;
    }
    
    public List<Permission> listPermissions(String kbId) {
        return permissionStore.values().stream()
            .filter(p -> kbId.equals(p.getKbId()))
            .collect(Collectors.toList());
    }
    
    public Permission checkPermission(String kbId, String userId) {
        return permissionStore.values().stream()
            .filter(p -> kbId.equals(p.getKbId()) && userId.equals(p.getUserId()))
            .findFirst()
            .orElse(null);
    }
    
    public Share createShare(String kbId, String ownerId, Integer expireDays, String password, Integer maxAccessCount) {
        log.info("Creating share: kbId={}, ownerId={}", kbId, ownerId);
        
        int days = expireDays != null ? expireDays : defaultExpireDays;
        int maxCount = maxAccessCount != null ? maxAccessCount : defaultMaxAccessCount;
        
        String shareId = "share-" + System.currentTimeMillis();
        String shareCode = generateShareCode();
        
        Share share = new Share();
        share.setShareId(shareId);
        share.setShareCode(shareCode);
        share.setKbId(kbId);
        share.setOwnerId(ownerId);
        share.setPassword(password);
        share.setExpiresAt(new Date(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000));
        share.setMaxAccessCount(maxCount);
        share.setCurrentCount(0);
        share.setStatus(ShareStatus.VALID);
        share.setCreatedAt(new Date());
        
        shareStore.put(shareId, share);
        
        return share;
    }
    
    public Share getShare(String shareId) {
        return shareStore.get(shareId);
    }
    
    public Share getShareByCode(String shareCode) {
        return shareStore.values().stream()
            .filter(s -> shareCode.equals(s.getShareCode()))
            .findFirst()
            .orElse(null);
    }
    
    public List<Share> listShares(String kbId, String status) {
        return shareStore.values().stream()
            .filter(s -> kbId == null || kbId.equals(s.getKbId()))
            .filter(s -> status == null || status.equals(s.getStatus().getCode()))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }
    
    public Share validateShare(String shareCode, String password, String visitorId) {
        log.info("Validating share: shareCode={}", shareCode);
        
        Share share = getShareByCode(shareCode);
        
        if (share == null) {
            return null;
        }
        
        if (share.getStatus() == ShareStatus.REVOKED) {
            return null;
        }
        
        if (share.getExpiresAt() != null && share.getExpiresAt().before(new Date())) {
            share.setStatus(ShareStatus.EXPIRED);
            return null;
        }
        
        if (share.getMaxAccessCount() != null && share.getCurrentCount() >= share.getMaxAccessCount()) {
            return null;
        }
        
        if (share.getPassword() != null && !share.getPassword().equals(password)) {
            return null;
        }
        
        share.setCurrentCount(share.getCurrentCount() + 1);
        share.setStatus(ShareStatus.ACTIVE);
        
        recordAccess(share.getShareId(), visitorId, "VIEW");
        
        return share;
    }
    
    public Share revokeShare(String shareId) {
        log.info("Revoking share: shareId={}", shareId);
        
        Share share = shareStore.get(shareId);
        if (share != null) {
            share.setStatus(ShareStatus.REVOKED);
        }
        return share;
    }
    
    public boolean deleteShare(String shareId) {
        log.info("Deleting share: shareId={}", shareId);
        
        Share removed = shareStore.remove(shareId);
        accessLogStore.entrySet().removeIf(e -> shareId.equals(e.getValue().getShareId()));
        
        return removed != null;
    }
    
    public void recordAccess(String shareId, String visitorId, String action) {
        String logId = "log-" + System.currentTimeMillis();
        AccessLog log = new AccessLog();
        log.setLogId(logId);
        log.setShareId(shareId);
        log.setVisitorId(visitorId);
        log.setAction(action);
        log.setAccessTime(new Date());
        
        accessLogStore.put(logId, log);
    }
    
    public List<AccessLog> getAccessLogs(String shareId) {
        return accessLogStore.values().stream()
            .filter(l -> shareId.equals(l.getShareId()))
            .sorted((a, b) -> b.getAccessTime().compareTo(a.getAccessTime()))
            .collect(Collectors.toList());
    }
    
    public Collaboration startCollaboration(String kbId, String initiatorId) {
        log.info("Starting collaboration: kbId={}", kbId);
        
        Collaboration collaboration = collaborationStore.values().stream()
            .filter(c -> kbId.equals(c.getKbId()) && c.getStatus() == CollaborationStatus.ACTIVE)
            .findFirst()
            .orElse(null);
        
        if (collaboration != null) {
            return collaboration;
        }
        
        String collabId = "collab-" + System.currentTimeMillis();
        collaboration = new Collaboration();
        collaboration.setCollaborationId(collabId);
        collaboration.setKbId(kbId);
        collaboration.setInitiatorId(initiatorId);
        collaboration.setStatus(CollaborationStatus.ACTIVE);
        collaboration.setParticipants(new ArrayList<>());
        collaboration.getParticipants().add(initiatorId);
        collaboration.setStartedAt(new Date());
        
        collaborationStore.put(collabId, collaboration);
        
        return collaboration;
    }
    
    public Collaboration stopCollaboration(String kbId) {
        log.info("Stopping collaboration: kbId={}", kbId);
        
        Collaboration collaboration = collaborationStore.values().stream()
            .filter(c -> kbId.equals(c.getKbId()) && c.getStatus() == CollaborationStatus.ACTIVE)
            .findFirst()
            .orElse(null);
        
        if (collaboration != null) {
            collaboration.setStatus(CollaborationStatus.IDLE);
            collaboration.setStoppedAt(new Date());
        }
        
        return collaboration;
    }
    
    public Collaboration getCollaboration(String kbId) {
        return collaborationStore.values().stream()
            .filter(c -> kbId.equals(c.getKbId()))
            .findFirst()
            .orElse(null);
    }
    
    private String generateShareCode() {
        return "SHARE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static class Permission {
        private String permissionId;
        private String kbId;
        private String userId;
        private PermissionType permissionType;
        private String grantedBy;
        private Date grantedAt;
        
        public String getPermissionId() { return permissionId; }
        public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public PermissionType getPermissionType() { return permissionType; }
        public void setPermissionType(PermissionType permissionType) { this.permissionType = permissionType; }
        public String getGrantedBy() { return grantedBy; }
        public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
        public Date getGrantedAt() { return grantedAt; }
        public void setGrantedAt(Date grantedAt) { this.grantedAt = grantedAt; }
    }
    
    public static class Share {
        private String shareId;
        private String shareCode;
        private String kbId;
        private String ownerId;
        private String password;
        private Date expiresAt;
        private Integer maxAccessCount;
        private int currentCount;
        private ShareStatus status;
        private Date createdAt;
        
        public String getShareId() { return shareId; }
        public void setShareId(String shareId) { this.shareId = shareId; }
        public String getShareCode() { return shareCode; }
        public void setShareCode(String shareCode) { this.shareCode = shareCode; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Date getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
        public Integer getMaxAccessCount() { return maxAccessCount; }
        public void setMaxAccessCount(Integer maxAccessCount) { this.maxAccessCount = maxAccessCount; }
        public int getCurrentCount() { return currentCount; }
        public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }
        public ShareStatus getStatus() { return status; }
        public void setStatus(ShareStatus status) { this.status = status; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
    
    public static class AccessLog {
        private String logId;
        private String shareId;
        private String visitorId;
        private String visitorIp;
        private String action;
        private Date accessTime;
        
        public String getLogId() { return logId; }
        public void setLogId(String logId) { this.logId = logId; }
        public String getShareId() { return shareId; }
        public void setShareId(String shareId) { this.shareId = shareId; }
        public String getVisitorId() { return visitorId; }
        public void setVisitorId(String visitorId) { this.visitorId = visitorId; }
        public String getVisitorIp() { return visitorIp; }
        public void setVisitorIp(String visitorIp) { this.visitorIp = visitorIp; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Date getAccessTime() { return accessTime; }
        public void setAccessTime(Date accessTime) { this.accessTime = accessTime; }
    }
    
    public static class Collaboration {
        private String collaborationId;
        private String kbId;
        private String initiatorId;
        private CollaborationStatus status;
        private List<String> participants;
        private Date startedAt;
        private Date stoppedAt;
        
        public String getCollaborationId() { return collaborationId; }
        public void setCollaborationId(String collaborationId) { this.collaborationId = collaborationId; }
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getInitiatorId() { return initiatorId; }
        public void setInitiatorId(String initiatorId) { this.initiatorId = initiatorId; }
        public CollaborationStatus getStatus() { return status; }
        public void setStatus(CollaborationStatus status) { this.status = status; }
        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
        public Date getStartedAt() { return startedAt; }
        public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
        public Date getStoppedAt() { return stoppedAt; }
        public void setStoppedAt(Date stoppedAt) { this.stoppedAt = stoppedAt; }
    }
}
