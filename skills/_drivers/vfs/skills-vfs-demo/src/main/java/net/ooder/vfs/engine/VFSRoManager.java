package net.ooder.vfs.engine;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.vfs.*;
import net.ooder.vfs.manager.*;
import net.ooder.vfs.manager.inner.*;
import net.ooder.vfs.manager.dbimpl.JdbcFileVersionManager;
import net.ooder.vfs.manager.dbimpl.JdbcFileViewManager;

import java.util.*;

public class VFSRoManager {

    private static ConfigCode configKey = OrgConstants.VFSCONFIG_KEY;

    protected static Log log = LogFactory.getLog(configKey.getType(), VFSRoManager.class);

    public static final String THREAD_LOCK = "Thread Lock";

    private OrgConfig config;

    private Cache<String, EIFileInfo> fileCache;
    private Cache<String, EIFolder> folderCache;
    private Cache<String, FileVersion> fileVersionCache;
    private Cache<String, FileView> fileViewCache;
    private Cache<String, FileLink> fileLinkCache;
    private Cache<String, FileCopy> fileCopyCache;
    private Cache<String, String> filePathCache;

    private boolean cacheEnabled = true;
    private boolean cacheLoad = false;

    private FileInfoManager fileInfoManager;
    private FolderManager folderManager;
    private FileVersionManager fileVersonManager;
    private FileViewManager fileViewManager;
    private FileLinkManager fileLinkManager;
    private FileCopyManager fileCopyManager;

    private static VFSRoManager vfsRoManager;

    private VFSRoManager() {
        this.config = OrgConfig.getInstance(OrgConstants.VFSCONFIG_KEY);
    }

    public static VFSRoManager getInstance() {
        if (vfsRoManager == null) {
            synchronized (THREAD_LOCK) {
                if (vfsRoManager == null) {
                    vfsRoManager = new VFSRoManager();
                }
            }
        }
        return vfsRoManager;
    }

    public synchronized void init() {
        initCache();
    }

    private void initCache() {
        folderCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "folderCache", -1, -1);
        fileCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileCache", -1, -1);
        fileVersionCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileVersionCache", -1, -1);
        fileViewCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileViewCache");
        fileCopyCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileCopyCache");
        fileLinkCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileLinkCache");
        filePathCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY_DISK, "filePathCache", -1, -1);
    }

    public EIFileInfo getFileInfoByID(String fileId) {
        if (fileId == null) return null;
        synchronized (fileId.intern()) {
            if (!cacheEnabled) {
                return fileInfoManager.loadById(fileId);
            }
            EIFileInfo fileInfo = (EIFileInfo) fileCache.get(fileId);
            return fileInfo;
        }
    }

    public EIFolder getFolderByID(String folderId) throws VFSFolderNotFoundException {
        if (folderId == null) return null;
        synchronized (folderId.intern()) {
            if (!cacheEnabled) {
                return folderManager.loadById(folderId);
            }
            DBFolder folder = (DBFolder) this.getFolderCache().get(folderId);
            return folder;
        }
    }

    public Cache<String, EIFileInfo> getFileCache() {
        return fileCache;
    }

    public Cache<String, EIFolder> getFolderCache() {
        return folderCache;
    }

    public Cache<String, FileVersion> getFileVersionCache() {
        return fileVersionCache;
    }

    public Cache<String, FileView> getFileViewCache() {
        return fileViewCache;
    }

    public Cache<String, FileLink> getFileLinkCache() {
        return fileLinkCache;
    }

    public Cache<String, FileCopy> getFileCopyCache() {
        return fileCopyCache;
    }

    public Cache getFilePathCache() {
        return filePathCache;
    }

    public void setFilePathCache(Cache filePathCache) {
        this.filePathCache = filePathCache;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public FileVersion getFileVersionByID(String versionId) {
        if (versionId == null) return null;
        try { return getFileVersionCache().get(versionId); }
        catch (Exception e) { return null; }
    }

    public FileView getFileViewByID(String viewId) {
        if (viewId == null) return null;
        try { return getFileViewCache().get(viewId); }
        catch (Exception e) { return null; }
    }

    public FileLink getFileLinkByID(String linkId) {
        if (linkId == null) return null;
        try { return getFileLinkCache().get(linkId); }
        catch (Exception e) { return null; }
    }

    public FileObject getFileObjectByID(String objectId) {
        return null;
    }

    public FileView createFileView(DBFileVersion version, String fileObjectId, Integer fileIndex) {
        try { return JdbcFileViewManager.getInstance().createView(version.getVersionID(), fileIndex); }
        catch (Exception e) { log.error("createFileView failed", e); return null; }
    }

    public void updateView(FileView view) {
    }

    public DBFileVersion createFileVersion(DBFileInfo fileInfo) {
        try { return JdbcFileVersionManager.getInstance().createFileVersion(fileInfo); }
        catch (Exception e) { log.error("createFileVersion failed", e); return null; }
    }

    public EIFileInfo createFile(EIFolder parentFolder, String name, String descrition, String createPersonId) {
        try { return folderManager.createFile(parentFolder, name); }
        catch (Exception e) { log.error("createFile failed", e); return null; }
    }

    public EIFolder createFolder(DBFolder parentFolder, String name, String descrition, String createPersonId) throws VFSFolderNotFoundException {
        return folderManager.createFolder(parentFolder, name, descrition, createPersonId);
    }

    public void removeFolder(String folderId) throws VFSFolderNotFoundException {
        folderManager.remove(folderId);
    }

    public void commitFolder(EIFolder folder) throws VFSException {
        folderManager.commit(folder);
    }
}
