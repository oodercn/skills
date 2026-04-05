package net.ooder.vfs.engine;

import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.vfs.*;
import net.ooder.vfs.manager.*;

import java.util.List;

public class DbVFSManager {

    private static final Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DbVFSManager.class);

    private OrgConfig config;

    private FileInfoManager fileInfoManager;
    private FolderManager folderManager;
    private FileVersionManager fileVersonManager;
    private FileViewManager fileViewManager;
    private FileLinkManager fileLinkManager;
    private FileCopyManager fileCopyManager;

    public DbVFSManager() {
        this.config = OrgConfig.getInstance(OrgConstants.VFSCONFIG_KEY);
    }

    public FileInfoManager getFileInfoManager() {
        return fileInfoManager;
    }

    public void setFileInfoManager(FileInfoManager fileInfoManager) {
        this.fileInfoManager = fileInfoManager;
    }

    public FolderManager getFolderManager() {
        return folderManager;
    }

    public void setFolderManager(FolderManager folderManager) {
        this.folderManager = folderManager;
    }

    public FileVersionManager getFileVersonManager() {
        return fileVersonManager;
    }

    public void setFileVersonManager(FileVersionManager fileVersonManager) {
        this.fileVersonManager = fileVersonManager;
    }

    public FileViewManager getFileViewManager() {
        return fileViewManager;
    }

    public void setFileViewManager(FileViewManager fileViewManager) {
        this.fileViewManager = fileViewManager;
    }

    public FileLinkManager getFileLinkManager() {
        return fileLinkManager;
    }

    public void setFileLinkManager(FileLinkManager fileLinkManager) {
        this.fileLinkManager = fileLinkManager;
    }

    public FileCopyManager getFileCopyManager() {
        return fileCopyManager;
    }

    public void setFileCopyManager(FileCopyManager fileCopyManager) {
        this.fileCopyManager = fileCopyManager;
    }
}
