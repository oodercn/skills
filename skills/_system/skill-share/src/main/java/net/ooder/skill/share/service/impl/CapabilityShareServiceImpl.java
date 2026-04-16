package net.ooder.skill.share.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.scene.todo.InvitationTodoRequest;
import net.ooder.scene.todo.DelegationTodoRequest;
import net.ooder.scene.todo.TodoDTO;
import net.ooder.scene.todo.TodoService;
import net.ooder.skill.share.dto.ShareCapabilityRequest;
import net.ooder.skill.share.dto.DelegateCapabilityRequest;
import net.ooder.skill.share.model.ShareRecord;
import net.ooder.skill.share.model.DelegateRecord;
import net.ooder.skill.share.service.CapabilityShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityShareServiceImpl implements CapabilityShareService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityShareServiceImpl.class);
    
    private final String shareDir;
    private final String delegatedDir;
    private final String receivedDir;
    private final int shareCodeLength;
    private final TodoService todoService;
    private final ObjectMapper objectMapper;
    
    private final Map<String, ShareRecord> shareRecords = new ConcurrentHashMap<>();
    private final Map<String, DelegateRecord> delegateRecords = new ConcurrentHashMap<>();
    private final Map<String, ShareRecord> shareCodeIndex = new ConcurrentHashMap<>();

    public CapabilityShareServiceImpl(String baseDir, TodoService todoService) {
        this.shareDir = Paths.get(baseDir, ".ooder", "shared").toString();
        this.delegatedDir = Paths.get(baseDir, ".ooder", "delegated").toString();
        this.receivedDir = Paths.get(baseDir, ".ooder", "received").toString();
        this.shareCodeLength = 8;
        this.todoService = todoService;
        this.objectMapper = new ObjectMapper();
        
        initDirectories();
        loadRecords();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(Paths.get(shareDir));
            Files.createDirectories(Paths.get(delegatedDir));
            Files.createDirectories(Paths.get(receivedDir));
            Files.createDirectories(Paths.get(receivedDir, "from-share"));
            Files.createDirectories(Paths.get(receivedDir, "from-delegate"));
        } catch (IOException e) {
            log.error("Failed to create directories", e);
        }
    }

    private void loadRecords() {
        loadShareRecords();
        loadDelegateRecords();
    }

    private void loadShareRecords() {
        File dir = new File(shareDir);
        if (!dir.exists()) return;
        
        File[] files = dir.listFiles((d, name) -> name.endsWith("-share.json"));
        if (files == null) return;
        
        for (File file : files) {
            try {
                ShareRecord record = objectMapper.readValue(file, ShareRecord.class);
                shareRecords.put(record.getShareId(), record);
                shareCodeIndex.put(record.getShareCode(), record);
            } catch (IOException e) {
                log.error("Failed to load share record: {}", file.getName(), e);
            }
        }
    }

    private void loadDelegateRecords() {
        File dir = new File(delegatedDir);
        if (!dir.exists()) return;
        
        File[] files = dir.listFiles((d, name) -> name.endsWith("-delegate.json"));
        if (files == null) return;
        
        for (File file : files) {
            try {
                DelegateRecord record = objectMapper.readValue(file, DelegateRecord.class);
                delegateRecords.put(record.getDelegateId(), record);
            } catch (IOException e) {
                log.error("Failed to load delegate record: {}", file.getName(), e);
            }
        }
    }

    @Override
    public ShareRecord shareCapability(ShareCapabilityRequest request) {
        log.info("[shareCapability] Sharing capability: {} from {} to {}", 
                request.getSkillId(), request.getFromUserId(), request.getToUserIds());
        
        ShareRecord record = new ShareRecord();
        record.setShareId(generateId("share"));
        record.setShareCode(generateShareCode());
        record.setSkillId(request.getSkillId());
        record.setSkillName(request.getSkillName());
        record.setSkillVersion(request.getSkillVersion());
        record.setFromUserId(request.getFromUserId());
        record.setFromUserName(request.getFromUserName());
        record.setToUserIds(request.getToUserIds());
        record.setToUserNames(request.getToUserNames());
        record.setMessage(request.getMessage());
        record.setPassword(request.getPassword());
        record.setExpiresAt(request.getExpiresAt());
        record.setMaxAccessCount(request.getMaxAccessCount());
        record.setSourcePath(request.getSourcePath());
        record.setCreatedAt(System.currentTimeMillis());
        record.setExtra(request.getExtra());
        
        saveShareRecord(record);
        
        shareRecords.put(record.getShareId(), record);
        shareCodeIndex.put(record.getShareCode(), record);
        
        if (todoService != null) {
            createShareTodos(record);
        }
        
        log.info("[shareCapability] Share created: shareId={}, shareCode={}", 
                record.getShareId(), record.getShareCode());
        
        return record;
    }

    @Override
    public DelegateRecord delegateCapability(DelegateCapabilityRequest request) {
        log.info("[delegateCapability] Delegating capability: {} from {} to {}", 
                request.getSkillId(), request.getFromUserId(), request.getToUserIds());
        
        DelegateRecord record = new DelegateRecord();
        record.setDelegateId(generateId("delegate"));
        record.setSkillId(request.getSkillId());
        record.setSkillName(request.getSkillName());
        record.setSkillVersion(request.getSkillVersion());
        record.setFromUserId(request.getFromUserId());
        record.setFromUserName(request.getFromUserName());
        record.setToUserIds(request.getToUserIds());
        record.setToUserNames(request.getToUserNames());
        record.setMessage(request.getMessage());
        record.setDeadline(request.getDeadline());
        record.setPriority(request.getPriority());
        record.setSourcePath(request.getSourcePath());
        record.setCreatedAt(System.currentTimeMillis());
        record.setExtra(request.getExtra());
        
        saveDelegateRecord(record);
        
        delegateRecords.put(record.getDelegateId(), record);
        
        if (todoService != null) {
            createDelegateTodos(record);
        }
        
        log.info("[delegateCapability] Delegation created: delegateId={}", record.getDelegateId());
        
        return record;
    }

    @Override
    public List<ShareRecord> getSharedCapabilities(String userId) {
        return shareRecords.values().stream()
                .filter(r -> userId.equals(r.getFromUserId()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    @Override
    public List<DelegateRecord> getDelegatedCapabilities(String userId) {
        return delegateRecords.values().stream()
                .filter(r -> userId.equals(r.getFromUserId()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    @Override
    public List<ShareRecord> getReceivedCapabilities(String userId) {
        return shareRecords.values().stream()
                .filter(r -> r.getToUserIds() != null && r.getToUserIds().contains(userId))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .toList();
    }

    @Override
    public ShareRecord getShareDetail(String shareId) {
        return shareRecords.get(shareId);
    }

    @Override
    public ShareRecord getShareByCode(String shareCode) {
        return shareCodeIndex.get(shareCode);
    }

    @Override
    public boolean validateShare(String shareCode, String password) {
        ShareRecord record = shareCodeIndex.get(shareCode);
        if (record == null) {
            return false;
        }
        
        if (!record.isActive()) {
            return false;
        }
        
        if (record.isPasswordProtected()) {
            return record.getPassword() != null && record.getPassword().equals(password);
        }
        
        return true;
    }

    @Override
    public boolean acceptShare(String shareId, String userId) {
        ShareRecord record = shareRecords.get(shareId);
        if (record == null) {
            log.warn("[acceptShare] Share not found: {}", shareId);
            return false;
        }
        
        if (!record.isActive()) {
            log.warn("[acceptShare] Share is not active: {}", shareId);
            return false;
        }
        
        if (record.getToUserIds() == null || !record.getToUserIds().contains(userId)) {
            log.warn("[acceptShare] User {} is not in the recipient list", userId);
            return false;
        }
        
        record.setAccessCount(record.getAccessCount() + 1);
        saveShareRecord(record);
        
        log.info("[acceptShare] Share accepted: shareId={}, userId={}", shareId, userId);
        
        return true;
    }

    @Override
    public boolean rejectShare(String shareId, String userId, String reason) {
        ShareRecord record = shareRecords.get(shareId);
        if (record == null) {
            return false;
        }
        
        log.info("[rejectShare] Share rejected: shareId={}, userId={}, reason={}", shareId, userId, reason);
        
        return true;
    }

    @Override
    public boolean cancelShare(String shareId, String userId) {
        ShareRecord record = shareRecords.get(shareId);
        if (record == null) {
            return false;
        }
        
        if (!userId.equals(record.getFromUserId())) {
            log.warn("[cancelShare] User {} is not the owner of share {}", userId, shareId);
            return false;
        }
        
        record.setStatus("cancelled");
        saveShareRecord(record);
        
        log.info("[cancelShare] Share cancelled: shareId={}", shareId);
        
        return true;
    }

    @Override
    public boolean cancelDelegate(String delegateId, String userId) {
        DelegateRecord record = delegateRecords.get(delegateId);
        if (record == null) {
            return false;
        }
        
        if (!userId.equals(record.getFromUserId())) {
            log.warn("[cancelDelegate] User {} is not the owner of delegation {}", userId, delegateId);
            return false;
        }
        
        record.setStatus("cancelled");
        saveDelegateRecord(record);
        
        log.info("[cancelDelegate] Delegation cancelled: delegateId={}", delegateId);
        
        return true;
    }

    private void createShareTodos(ShareRecord record) {
        if (record.getToUserIds() == null) return;
        
        for (int i = 0; i < record.getToUserIds().size(); i++) {
            String toUserId = record.getToUserIds().get(i);
            String toUserName = record.getToUserNames() != null && i < record.getToUserNames().size() 
                    ? record.getToUserNames().get(i) : null;
            
            InvitationTodoRequest todoRequest = new InvitationTodoRequest();
            todoRequest.setSceneGroupId("capability-share");
            todoRequest.setSceneGroupName("能力分享");
            todoRequest.setFromUserId(record.getFromUserId());
            todoRequest.setFromUserName(record.getFromUserName());
            todoRequest.setToUserId(toUserId);
            todoRequest.setToUserName(toUserName);
            todoRequest.setTitle("收到能力分享: " + record.getSkillName());
            todoRequest.setDescription(record.getMessage());
            todoRequest.setDeadline(record.getExpiresAt());
            
            Map<String, Object> extra = new HashMap<>();
            extra.put("shareId", record.getShareId());
            extra.put("shareCode", record.getShareCode());
            extra.put("skillId", record.getSkillId());
            extra.put("skillName", record.getSkillName());
            todoRequest.setExtra(extra);
            
            try {
                TodoDTO todo = todoService.createInvitationTodo(todoRequest);
                log.info("[createShareTodos] Todo created: todoId={}, toUserId={}", todo.getId(), toUserId);
            } catch (Exception e) {
                log.error("[createShareTodos] Failed to create todo for user: {}", toUserId, e);
            }
        }
    }

    private void createDelegateTodos(DelegateRecord record) {
        if (record.getToUserIds() == null) return;
        
        for (int i = 0; i < record.getToUserIds().size(); i++) {
            String toUserId = record.getToUserIds().get(i);
            String toUserName = record.getToUserNames() != null && i < record.getToUserNames().size() 
                    ? record.getToUserNames().get(i) : null;
            
            DelegationTodoRequest todoRequest = new DelegationTodoRequest();
            todoRequest.setSceneGroupId("capability-delegate");
            todoRequest.setSceneGroupName("能力委派");
            todoRequest.setFromUserId(record.getFromUserId());
            todoRequest.setFromUserName(record.getFromUserName());
            todoRequest.setToUserId(toUserId);
            todoRequest.setToUserName(toUserName);
            todoRequest.setTitle("收到能力委派: " + record.getSkillName());
            todoRequest.setDescription(record.getMessage());
            todoRequest.setDeadline(record.getDeadline());
            todoRequest.setPriority(record.getPriority());
            
            Map<String, Object> extra = new HashMap<>();
            extra.put("delegateId", record.getDelegateId());
            extra.put("skillId", record.getSkillId());
            extra.put("skillName", record.getSkillName());
            todoRequest.setExtra(extra);
            
            try {
                TodoDTO todo = todoService.createDelegationTodo(todoRequest);
                log.info("[createDelegateTodos] Todo created: todoId={}, toUserId={}", todo.getId(), toUserId);
            } catch (Exception e) {
                log.error("[createDelegateTodos] Failed to create todo for user: {}", toUserId, e);
            }
        }
    }

    private void saveShareRecord(ShareRecord record) {
        try {
            Path path = Paths.get(shareDir, record.getShareId() + "-share.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), record);
        } catch (IOException e) {
            log.error("Failed to save share record: {}", record.getShareId(), e);
        }
    }

    private void saveDelegateRecord(DelegateRecord record) {
        try {
            Path path = Paths.get(delegatedDir, record.getDelegateId() + "-delegate.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), record);
        } catch (IOException e) {
            log.error("Failed to save delegate record: {}", record.getDelegateId(), e);
        }
    }

    private String generateId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString((int)(Math.random() * 0xFFFF));
    }

    private String generateShareCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < shareCodeLength; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
