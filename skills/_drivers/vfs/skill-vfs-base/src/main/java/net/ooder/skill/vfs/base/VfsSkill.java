package net.ooder.skill.vfs.base;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Virtual File System Skill Interface
 * 
 * <p>Provides comprehensive virtual file system capabilities including:</p>
 * <ul>
 *   <li>File and folder management</li>
 *   <li>File upload and download</li>
 *   <li>Version control</li>
 *   <li>File sharing</li>
 *   <li>Search functionality</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @Autowired
 * private VfsSkill vfsSkill;
 * 
 * // Create a folder
 * Folder folder = vfsSkill.createFolder(null, "My Documents", "user123");
 * 
 * // Upload a file
 * FileInfo file = vfsSkill.uploadFile(folder.getFolderId(), "report.pdf", inputStream, "user123");
 * 
 * // Download a file
 * InputStream content = vfsSkill.downloadFile(file.getFileId());
 * }</pre>
 * 
 * <h3>Capabilities:</h3>
 * <ul>
 *   <li>{@code file.read} - Read file content</li>
 *   <li>{@code file.write} - Create/update files</li>
 *   <li>{@code file.delete} - Delete files</li>
 *   <li>{@code folder.create} - Create folders</li>
 *   <li>{@code folder.delete} - Delete folders</li>
 *   <li>{@code version.manage} - Version control</li>
 *   <li>{@code share} - File sharing</li>
 *   <li>{@code search} - File search</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 * @see FileInfo
 * @see Folder
 * @see FileVersion
 * @see VfsCapabilities
 */
public interface VfsSkill {

    // ==================== Skill Metadata ====================

    /**
     * Get the unique skill identifier
     * 
     * @return the skill ID, e.g., "skill-vfs-local"
     */
    String getSkillId();

    /**
     * Get the skill display name
     * 
     * @return the skill name, e.g., "Local VFS Skill"
     */
    String getSkillName();

    /**
     * Get the skill version
     * 
     * @return the version string, e.g., "2.3"
     */
    String getSkillVersion();

    /**
     * Get the list of supported capabilities
     * 
     * @return list of capability identifiers
     */
    List<String> getCapabilities();

    // ==================== Initialization ====================

    /**
     * Initialize the VFS skill with capabilities configuration
     * 
     * @param capabilities the VFS capabilities configuration
     */
    void initialize(VfsCapabilities capabilities);

    /**
     * Initialize the VFS skill with capabilities and remote manager
     * 
     * @param capabilities the VFS capabilities configuration
     * @param remoteVfsManager the remote VFS manager for distributed storage
     */
    void initialize(VfsCapabilities capabilities, VfsManager remoteVfsManager);

    // ==================== File Operations ====================

    /**
     * Get file information by ID
     * 
     * @param fileId the file ID
     * @return the file information, or null if not found
     */
    FileInfo getFileInfo(String fileId);

    /**
     * Create a new file in a folder
     * 
     * @param folderId the parent folder ID (null for root)
     * @param name the file name
     * @param personId the creator's person ID
     * @return the created file information
     */
    FileInfo createFile(String folderId, String name, String personId);

    /**
     * Delete a file
     * 
     * @param fileId the file ID to delete
     * @return true if deleted successfully
     */
    boolean deleteFile(String fileId);

    /**
     * List files in a folder
     * 
     * @param folderId the folder ID (null for root)
     * @return list of files in the folder
     */
    List<FileInfo> listFiles(String folderId);

    // ==================== Folder Operations ====================

    /**
     * Get folder information by ID
     * 
     * @param folderId the folder ID
     * @return the folder information, or null if not found
     */
    Folder getFolder(String folderId);

    /**
     * Create a new folder
     * 
     * @param parentId the parent folder ID (null for root)
     * @param name the folder name
     * @param personId the creator's person ID
     * @return the created folder
     */
    Folder createFolder(String parentId, String name, String personId);

    /**
     * Delete a folder and all its contents
     * 
     * @param folderId the folder ID to delete
     * @return true if deleted successfully
     */
    boolean deleteFolder(String folderId);

    /**
     * List subfolders in a folder
     * 
     * @param parentId the parent folder ID (null for root)
     * @return list of subfolders
     */
    List<Folder> listFolders(String parentId);

    // ==================== Upload/Download ====================

    /**
     * Download a file's content
     * 
     * @param fileId the file ID to download
     * @return input stream of the file content
     * @throws VfsException if file not found or access denied
     */
    InputStream downloadFile(String fileId);

    /**
     * Upload content to a new file
     * 
     * @param folderId the parent folder ID
     * @param name the file name
     * @param content the file content stream
     * @param personId the uploader's person ID
     * @return the created file information
     */
    FileInfo uploadFile(String folderId, String name, InputStream content, String personId);

    // ==================== File Manipulation ====================

    /**
     * Copy a file to another folder
     * 
     * @param fileId the source file ID
     * @param targetFolderId the target folder ID
     * @return the copied file information
     */
    FileInfo copyFile(String fileId, String targetFolderId);

    /**
     * Move a file to another folder
     * 
     * @param fileId the file ID to move
     * @param targetFolderId the target folder ID
     * @return the moved file information
     */
    FileInfo moveFile(String fileId, String targetFolderId);

    /**
     * Rename a file
     * 
     * @param fileId the file ID to rename
     * @param newName the new file name
     * @return true if renamed successfully
     */
    boolean renameFile(String fileId, String newName);

    /**
     * Share a file with an expiration time
     * 
     * @param fileId the file ID to share
     * @param expireTime the expiration timestamp in milliseconds
     * @return the shared file information with share link
     */
    FileInfo shareFile(String fileId, long expireTime);

    // ==================== Version Control ====================

    /**
     * Get all versions of a file
     * 
     * @param fileId the file ID
     * @return list of file versions
     */
    List<FileVersion> getFileVersions(String fileId);

    /**
     * Create a new version of a file
     * 
     * @param fileId the file ID
     * @param description the version description
     * @param personId the creator's person ID
     * @return the created version
     */
    FileVersion createVersion(String fileId, String description, String personId);

    // ==================== Search ====================

    /**
     * Search for files by keyword
     * 
     * @param keyword the search keyword
     * @param folderId the folder to search in (null for all)
     * @return list of matching files
     */
    List<FileInfo> searchFiles(String keyword, String folderId);

    // ==================== Capability Support ====================

    /**
     * Get the VFS capabilities configuration
     * 
     * @return the capabilities configuration
     */
    VfsCapabilities getVfsCapabilities();

    /**
     * Check if a capability is supported
     * 
     * @param capability the capability identifier
     * @return true if supported
     */
    boolean isSupport(String capability);

    /**
     * Invoke a capability with parameters
     * 
     * @param capability the capability identifier
     * @param params the invocation parameters
     * @return the invocation result
     */
    Object invoke(String capability, Map<String, Object> params);
}
