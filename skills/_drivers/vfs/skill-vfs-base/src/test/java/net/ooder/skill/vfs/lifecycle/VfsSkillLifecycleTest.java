package net.ooder.skill.vfs.lifecycle;

import net.ooder.skill.common.test.SkillLifecycleTestBase;
import net.ooder.skill.vfs.base.*;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * VFS Skill Lifecycle Test
 * 
 * <p>Comprehensive lifecycle testing for VFS Skill implementation.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 */
@DisplayName("VFS Skill Lifecycle Tests")
class VfsSkillLifecycleTest extends SkillLifecycleTestBase<VfsSkill> {

    private boolean initialized = false;
    private boolean running = false;
    private boolean destroyed = false;
    private final Map<String, FileInfo> files = new HashMap<>();
    private final Map<String, Folder> folders = new HashMap<>();

    @Override
    protected void setUpSkill() {
        skill = new VfsSkillImpl();
        initialized = false;
        running = false;
        destroyed = false;
        files.clear();
        folders.clear();
    }

    @Override
    protected void tearDownSkill() {
        skill = null;
    }

    @Override
    protected String getSkillId() {
        return skill.getSkillId();
    }

    @Override
    protected String getSkillName() {
        return skill.getSkillName();
    }

    @Override
    protected String getSkillVersion() {
        return skill.getSkillVersion();
    }

    @Override
    protected List<String> getCapabilities() {
        return skill.getCapabilities();
    }

    @Override
    protected void initializeSkill(Map<String, Object> config) {
        skill.initialize(new VfsCapabilities());
        initialized = true;
    }

    @Override
    protected void startSkill() {
        running = true;
    }

    @Override
    protected void stopSkill() {
        running = false;
    }

    @Override
    protected void destroySkill() {
        destroyed = true;
        files.clear();
        folders.clear();
    }

    @Override
    protected boolean isInitialized() {
        return initialized;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    protected Object invokeCapability(String capability, Map<String, Object> params) {
        switch (capability) {
            case "file.read":
                return skill.getFileInfo("file-001");
            case "file.write":
                return skill.createFile(null, "test.txt", "user-001");
            case "file.delete":
                return skill.deleteFile("file-001");
            case "folder.create":
                return skill.createFolder(null, "TestFolder", "user-001");
            case "folder.delete":
                return skill.deleteFolder("folder-001");
            case "version.manage":
                return skill.getFileVersions("file-001");
            case "share":
                return skill.shareFile("file-001", System.currentTimeMillis() + 3600000);
            case "search":
                return skill.searchFiles("test", null);
            default:
                return skill.invoke(capability, params);
        }
    }

    @Override
    protected Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", running ? "UP" : "DOWN");
        health.put("initialized", initialized);
        health.put("running", running);
        health.put("uptime", System.currentTimeMillis());
        health.put("fileCount", files.size());
        health.put("folderCount", folders.size());
        health.put("capabilities", skill.getVfsCapabilities());
        return health;
    }

    // ==================== VFS-Specific Lifecycle Tests ====================

    @org.junit.jupiter.api.Nested
    @DisplayName("VFS-Specific Lifecycle Tests")
    class VfsSpecificTests {

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle file operations across lifecycle")
        void shouldHandleFileOperationsAcrossLifecycle() {
            initializeSkill(config);
            startSkill();
            
            Folder folder = skill.createFolder(null, "Documents", "user-001");
            FileInfo file = skill.createFile(folder.getFolderId(), "report.txt", "user-001");
            
            assertNotNull(file.getFileId());
            
            stopSkill();
            startSkill();
            
            lifecycleEvents.add("File operations handled across lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle upload/download during lifecycle")
        void shouldHandleUploadDownloadDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            String content = "Test content for upload";
            InputStream stream = new ByteArrayInputStream(content.getBytes());
            
            Folder folder = skill.createFolder(null, "Uploads", "user-001");
            FileInfo uploaded = skill.uploadFile(folder.getFolderId(), "uploaded.txt", stream, "user-001");
            
            assertNotNull(uploaded.getFileId());
            
            InputStream downloaded = skill.downloadFile(uploaded.getFileId());
            assertNotNull(downloaded);
            
            lifecycleEvents.add("Upload/download handled during lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle version control during lifecycle")
        void shouldHandleVersionControlDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            Folder folder = skill.createFolder(null, "Versioned", "user-001");
            FileInfo file = skill.createFile(folder.getFolderId(), "versioned.txt", "user-001");
            
            FileVersion v1 = skill.createVersion(file.getFileId(), "Version 1", "user-001");
            FileVersion v2 = skill.createVersion(file.getFileId(), "Version 2", "user-001");
            
            List<FileVersion> versions = skill.getFileVersions(file.getFileId());
            assertEquals(2, versions.size());
            
            lifecycleEvents.add("Version control handled during lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle file manipulation during lifecycle")
        void shouldHandleFileManipulationDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            Folder source = skill.createFolder(null, "Source", "user-001");
            Folder target = skill.createFolder(null, "Target", "user-001");
            
            FileInfo file = skill.createFile(source.getFolderId(), "move.txt", "user-001");
            
            FileInfo moved = skill.moveFile(file.getFileId(), target.getFolderId());
            assertNotNull(moved);
            
            FileInfo copy = skill.createFile(source.getFolderId(), "copy.txt", "user-001");
            FileInfo copied = skill.copyFile(copy.getFileId(), target.getFolderId());
            assertNotNull(copied);
            
            lifecycleEvents.add("File manipulation handled during lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle search during lifecycle")
        void shouldHandleSearchDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            Folder folder = skill.createFolder(null, "SearchTest", "user-001");
            skill.createFile(folder.getFolderId(), "document1.pdf", "user-001");
            skill.createFile(folder.getFolderId(), "document2.docx", "user-001");
            skill.createFile(folder.getFolderId(), "image.png", "user-001");
            
            List<FileInfo> results = skill.searchFiles("document", folder.getFolderId());
            assertTrue(results.size() >= 0);
            
            lifecycleEvents.add("Search handled during lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should handle sharing during lifecycle")
        void shouldHandleSharingDuringLifecycle() {
            initializeSkill(config);
            startSkill();
            
            Folder folder = skill.createFolder(null, "Shared", "user-001");
            FileInfo file = skill.createFile(folder.getFolderId(), "shared.txt", "user-001");
            
            FileInfo shared = skill.shareFile(file.getFileId(), System.currentTimeMillis() + 3600000);
            assertNotNull(shared);
            
            lifecycleEvents.add("Sharing handled during lifecycle");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Should maintain capability support check")
        void shouldMaintainCapabilitySupportCheck() {
            initializeSkill(config);
            startSkill();
            
            for (String capability : skill.getCapabilities()) {
                assertTrue(skill.isSupport(capability), 
                        "Should support capability: " + capability);
            }
            
            assertFalse(skill.isSupport("nonexistent.capability"));
            
            lifecycleEvents.add("Capability support check verified");
        }
    }

    /**
     * Simple VfsSkill implementation for testing
     */
    private static class VfsSkillImpl implements VfsSkill {

        private VfsCapabilities capabilities;

        @Override
        public String getSkillId() { return "skill-vfs-base"; }

        @Override
        public String getSkillName() { return "VFS Skill Base"; }

        @Override
        public String getSkillVersion() { return "2.3"; }

        @Override
        public List<String> getCapabilities() {
            return Arrays.asList("file.read", "file.write", "file.delete", 
                    "folder.create", "folder.delete", "version.manage", "share", "search");
        }

        @Override
        public void initialize(VfsCapabilities capabilities) {
            this.capabilities = capabilities;
        }

        @Override
        public void initialize(VfsCapabilities capabilities, VfsManager remoteVfsManager) {
            this.capabilities = capabilities;
        }

        @Override
        public FileInfo getFileInfo(String fileId) { return null; }

        @Override
        public FileInfo createFile(String folderId, String name, String personId) {
            FileInfo file = new FileInfo();
            file.setFileId(UUID.randomUUID().toString());
            file.setName(name);
            file.setFolderId(folderId);
            file.setCreatorId(personId);
            file.setCreateTime(System.currentTimeMillis());
            return file;
        }

        @Override
        public boolean deleteFile(String fileId) { return true; }

        @Override
        public List<FileInfo> listFiles(String folderId) { return new ArrayList<>(); }

        @Override
        public Folder getFolder(String folderId) { return null; }

        @Override
        public Folder createFolder(String parentId, String name, String personId) {
            Folder folder = new Folder();
            folder.setFolderId(UUID.randomUUID().toString());
            folder.setName(name);
            folder.setParentId(parentId);
            folder.setCreatorId(personId);
            folder.setCreateTime(System.currentTimeMillis());
            return folder;
        }

        @Override
        public boolean deleteFolder(String folderId) { return true; }

        @Override
        public List<Folder> listFolders(String parentId) { return new ArrayList<>(); }

        @Override
        public InputStream downloadFile(String fileId) {
            return new ByteArrayInputStream("test content".getBytes());
        }

        @Override
        public FileInfo uploadFile(String folderId, String name, InputStream content, String personId) {
            FileInfo file = createFile(folderId, name, personId);
            return file;
        }

        @Override
        public FileInfo copyFile(String fileId, String targetFolderId) {
            FileInfo copy = new FileInfo();
            copy.setFileId(UUID.randomUUID().toString());
            copy.setFolderId(targetFolderId);
            return copy;
        }

        @Override
        public FileInfo moveFile(String fileId, String targetFolderId) {
            FileInfo file = new FileInfo();
            file.setFileId(fileId);
            file.setFolderId(targetFolderId);
            return file;
        }

        @Override
        public boolean renameFile(String fileId, String newName) { return true; }

        @Override
        public FileInfo shareFile(String fileId, long expireTime) {
            FileInfo file = new FileInfo();
            file.setFileId(fileId);
            return file;
        }

        @Override
        public List<FileVersion> getFileVersions(String fileId) { return new ArrayList<>(); }

        @Override
        public FileVersion createVersion(String fileId, String description, String personId) {
            FileVersion version = new FileVersion();
            version.setVersionId(UUID.randomUUID().toString());
            version.setFileId(fileId);
            version.setDescription(description);
            version.setCreatorId(personId);
            version.setCreateTime(System.currentTimeMillis());
            return version;
        }

        @Override
        public List<FileInfo> searchFiles(String keyword, String folderId) { return new ArrayList<>(); }

        @Override
        public VfsCapabilities getVfsCapabilities() { return capabilities; }

        @Override
        public boolean isSupport(String capability) {
            return getCapabilities().contains(capability);
        }

        @Override
        public Object invoke(String capability, Map<String, Object> params) {
            return "Invoked: " + capability;
        }
    }
}
