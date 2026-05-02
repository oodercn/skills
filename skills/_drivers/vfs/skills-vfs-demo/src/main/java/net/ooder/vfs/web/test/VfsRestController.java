package net.ooder.vfs.web.test;

import net.ooder.vfs.event.VfsEventPublisher;
import net.ooder.vfs.event.VfsFileEvent;
import net.ooder.vfs.event.VfsFolderEvent;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.VFSException;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/vfs")
public class VfsRestController {
    
    private static final Logger log = LoggerFactory.getLogger(VfsRestController.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager jdbcManager;
    private final VfsEventPublisher eventPublisher;
    private final AtomicLong idCounter = new AtomicLong(System.currentTimeMillis());
    
    public VfsRestController(JdbcTemplate jdbcTemplate, JdbcManager jdbcManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcManager = jdbcManager;
        this.eventPublisher = VfsEventPublisher.getInstance();
    }
    
    private String generateId(String prefix) {
        return prefix + "-" + idCounter.incrementAndGet();
    }
    
    @PostMapping("/folders")
    public ResponseEntity<Map<String, Object>> createFolder(@RequestBody Map<String, Object> request) {
        try {
            String folderId = generateId("folder");
            String name = (String) request.getOrDefault("name", "New Folder");
            String parentId = (String) request.getOrDefault("parentId", "folder-root");
            String personId = (String) request.getOrDefault("personId", "user-1");
            long now = System.currentTimeMillis();
            
            return LockManager.executeWithLock(folderId, () -> {
                try {
                    String parentPath = "/";
                    if (!"folder-root".equals(parentId)) {
                        Map<String, Object> parent = jdbcTemplate.queryForObject(
                            "SELECT PATH FROM VFS_FOLDER WHERE FOLDER_ID = ?",
                            new Object[]{parentId},
                            (rs, rowNum) -> Map.of("path", rs.getString("PATH")));
                        parentPath = parent != null ? parent.get("path") + "/" : "/";
                    }
                    String path = parentPath + name;
                    
                    jdbcTemplate.update(
                        "INSERT INTO VFS_FOLDER (FOLDER_ID, NAME, PARENT_ID, PERSON_ID, CREATE_TIME, UPDATE_TIME, PATH) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        folderId, name, parentId, personId, now, now, path);
                    
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("folderId", folderId);
                    result.put("name", name);
                    result.put("parentId", parentId);
                    result.put("path", path);
                    result.put("createTime", now);
                    eventPublisher.publishFolderEvent("VfsRestController", folderId, name, parentId, VfsFolderEvent.FolderAction.CREATED);
                    return ResponseEntity.ok(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to create folder", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/folders/{folderId}")
    public ResponseEntity<Map<String, Object>> getFolder(@PathVariable("folderId") String folderId) {
        try {
            Map<String, Object> folder = jdbcTemplate.queryForObject(
                "SELECT * FROM VFS_FOLDER WHERE FOLDER_ID = ?",
                new Object[]{folderId},
                (rs, rowNum) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("folderId", rs.getString("FOLDER_ID"));
                    map.put("name", rs.getString("NAME"));
                    map.put("parentId", rs.getString("PARENT_ID"));
                    map.put("personId", rs.getString("PERSON_ID"));
                    map.put("createTime", rs.getLong("CREATE_TIME"));
                    map.put("updateTime", rs.getLong("UPDATE_TIME"));
                    map.put("path", rs.getString("PATH"));
                    map.put("size", rs.getInt("SIZE"));
                    return map;
                });
            
            if (folder == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(folder);
        } catch (Exception e) {
            log.error("Failed to get folder: {}", folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/folders/{folderId}/children")
    public ResponseEntity<List<Map<String, Object>>> getFolderChildren(@PathVariable("folderId") String folderId) {
        try {
            List<Map<String, Object>> folders = jdbcTemplate.queryForList(
                "SELECT FOLDER_ID, NAME, PARENT_ID, PATH, CREATE_TIME FROM VFS_FOLDER WHERE PARENT_ID = ? ORDER BY NAME",
                new Object[]{folderId},
                (rs, rowNum) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("folderId", rs.getString("FOLDER_ID"));
                    map.put("name", rs.getString("NAME"));
                    map.put("parentId", rs.getString("PARENT_ID"));
                    map.put("path", rs.getString("PATH"));
                    map.put("createTime", rs.getLong("CREATE_TIME"));
                    return map;
                });
            
            List<Map<String, Object>> files = jdbcTemplate.queryForList(
                "SELECT FILE_ID, NAME, FILE_TYPE, HASH, LENGTH, CREATE_TIME FROM VFS_FILE WHERE FOLDER_ID = ? ORDER BY NAME",
                new Object[]{folderId},
                (rs, rowNum) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("fileId", rs.getString("FILE_ID"));
                    map.put("name", rs.getString("NAME"));
                    map.put("fileType", rs.getInt("FILE_TYPE"));
                    map.put("hash", rs.getString("HASH"));
                    map.put("length", rs.getLong("LENGTH"));
                    map.put("createTime", rs.getLong("CREATE_TIME"));
                    return map;
                });
            
            List<Map<String, Object>> result = new ArrayList<>();
            result.addAll(folders);
            result.addAll(files);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to get children of folder: {}", folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<Map<String, Object>> deleteFolder(@PathVariable("folderId") String folderId) {
        try {
            if ("folder-root".equals(folderId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete root folder"));
            }
            
            return LockManager.executeWithLock(folderId, () -> {
                try {
                    int deleted = jdbcTemplate.update("DELETE FROM VFS_FOLDER WHERE FOLDER_ID = ?", folderId);
                    jdbcTemplate.update("DELETE FROM VFS_FILE WHERE FOLDER_ID = ?", folderId);
                    eventPublisher.publishFolderEvent("VfsRestController", folderId, "", "", VfsFolderEvent.FolderAction.DELETED);
                    return ResponseEntity.ok(Map.of("deleted", deleted));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to delete folder: {}", folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/files")
    public ResponseEntity<Map<String, Object>> createFile(@RequestBody Map<String, Object> request) {
        try {
            String fileId = generateId("file");
            String name = (String) request.getOrDefault("name", "New File");
            String folderId = (String) request.getOrDefault("folderId", "folder-root");
            String personId = (String) request.getOrDefault("personId", "user-1");
            int fileType = request.containsKey("fileType") ? ((Number) request.get("fileType")).intValue() : 1;
            String hash = (String) request.getOrDefault("hash", "");
            long length = request.containsKey("length") ? ((Number) request.get("length")).longValue() : 0;
            long now = System.currentTimeMillis();
            
            return LockManager.executeWithLock(fileId, () -> {
                try {
                    jdbcManager.executeInTransaction(() -> {
                        jdbcTemplate.update(
                            "INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME, HASH, LENGTH) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            fileId, name, folderId, fileType, personId, now, now, hash, length);
                        jdbcTemplate.update(
                            "UPDATE VFS_FOLDER SET SIZE = SIZE + 1, UPDATE_TIME = ? WHERE FOLDER_ID = ?",
                            now, folderId);
                        return null;
                    });
                    
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("fileId", fileId);
                    result.put("name", name);
                    result.put("folderId", folderId);
                    result.put("fileType", fileType);
                    result.put("hash", hash);
                    result.put("length", length);
                    eventPublisher.publishFileEvent("VfsRestController", fileId, name, folderId, VfsFileEvent.FileAction.CREATED, "", length);
                    result.put("createTime", now);
                    return ResponseEntity.ok(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to create file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/files/{fileId}")
    public ResponseEntity<Map<String, Object>> getFile(@PathVariable("fileId") String fileId) {
        try {
            Map<String, Object> file = jdbcTemplate.queryForObject(
                "SELECT * FROM VFS_FILE WHERE FILE_ID = ?",
                new Object[]{fileId},
                (rs, rowNum) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("fileId", rs.getString("FILE_ID"));
                    map.put("name", rs.getString("NAME"));
                    map.put("folderId", rs.getString("FOLDER_ID"));
                    map.put("fileType", rs.getInt("FILE_TYPE"));
                    map.put("personId", rs.getString("PERSON_ID"));
                    map.put("hash", rs.getString("HASH"));
                    map.put("length", rs.getLong("LENGTH"));
                    map.put("createTime", rs.getLong("CREATE_TIME"));
                    map.put("updateTime", rs.getLong("UPDATE_TIME"));
                    map.put("isRecycled", rs.getInt("IS_RECYCLED"));
                    map.put("isLocked", rs.getInt("IS_LOCKED"));
                    return map;
                });
            
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            log.error("Failed to get file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/files/{fileId}")
    public ResponseEntity<Map<String, Object>> updateFile(@PathVariable("fileId") String fileId, @RequestBody Map<String, Object> request) {
        try {
            return LockManager.executeWithLock(fileId, () -> {
                try {
                    long now = System.currentTimeMillis();
                    String name = (String) request.get("name");
                    
                    if (name != null) {
                        jdbcTemplate.update("UPDATE VFS_FILE SET NAME = ?, UPDATE_TIME = ? WHERE FILE_ID = ?", name, now, fileId);
                        eventPublisher.publishFileEvent("VfsRestController", fileId, name, "", VfsFileEvent.FileAction.UPDATED);
                    }
                    
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("fileId", fileId);
                    result.put("updateTime", now);
                    return ResponseEntity.ok(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to update file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable("fileId") String fileId) {
        try {
            return LockManager.executeWithLock(fileId, () -> {
                try {
                    int deleted = jdbcTemplate.update("DELETE FROM VFS_FILE WHERE FILE_ID = ?", fileId);
                    eventPublisher.publishFileEvent("VfsRestController", fileId, "", "", VfsFileEvent.FileAction.DELETED);
                    return ResponseEntity.ok(Map.of("deleted", deleted));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/files/{fileId}/move")
    public ResponseEntity<Map<String, Object>> moveFile(@PathVariable("fileId") String fileId, @RequestBody Map<String, Object> request) {
        try {
            String targetFolderId = (String) request.get("targetFolderId");
            if (targetFolderId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "targetFolderId is required"));
            }
            
            return LockManager.executeWithLock(fileId, () -> {
                try {
                    long now = System.currentTimeMillis();
                    jdbcManager.executeInTransaction(() -> {
                        jdbcTemplate.update("UPDATE VFS_FILE SET FOLDER_ID = ?, UPDATE_TIME = ? WHERE FILE_ID = ?",
                            targetFolderId, now, fileId);
                        eventPublisher.publishFileEvent("VfsRestController", fileId, "", targetFolderId, VfsFileEvent.FileAction.MOVED);
                        return null;
                    });
                    
                    return ResponseEntity.ok(Map.of("fileId", fileId, "targetFolderId", targetFolderId, "moved", true));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to move file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/files/{fileId}/versions")
    public ResponseEntity<List<Map<String, Object>>> getFileVersions(@PathVariable("fileId") String fileId) {
        try {
            List<Map<String, Object>> versions = jdbcTemplate.queryForList(
                "SELECT * FROM VFS_FILE_VERSION WHERE FILE_ID = ? ORDER BY CREATE_TIME DESC",
                new Object[]{fileId},
                (rs, rowNum) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("versionId", rs.getString("VERSION_ID"));
                    map.put("fileId", rs.getString("FILE_ID"));
                    map.put("name", rs.getString("NAME"));
                    map.put("personId", rs.getString("PERSON_ID"));
                    map.put("createTime", rs.getLong("CREATE_TIME"));
                    map.put("hash", rs.getString("HASH"));
                    map.put("length", rs.getLong("LENGTH"));
                    return map;
                });
            return ResponseEntity.ok(versions);
        } catch (Exception e) {
            log.error("Failed to get versions for file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            int folderCount = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FOLDER");
            int fileCount = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE");
            int versionCount = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE_VERSION");
            int activeConnections = jdbcManager.getActiveConnectionCount();
            int lockCount = LockManager.getLockCount();
            int activeLockCount = LockManager.getActiveLockCount();
            boolean inTransaction = jdbcManager.isInTransaction();
            
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("folderCount", folderCount);
            stats.put("fileCount", fileCount);
            stats.put("versionCount", versionCount);
            stats.put("activeConnections", activeConnections);
            stats.put("lockCount", lockCount);
            stats.put("activeLockCount", activeLockCount);
            stats.put("inTransaction", inTransaction);
            stats.put("dialect", jdbcTemplate.getDialect() != null ? jdbcTemplate.getDialect().getName() : "unknown");
            stats.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "vfs-web-test");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
    
    @ExceptionHandler(VFSException.class)
    public ResponseEntity<Map<String, Object>> handleVfsException(VFSException e) {
        log.error("VFS exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", e.getMessage(), "type", "VFSException"));
    }
}
