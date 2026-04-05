package net.ooder.vfs.client.impl;

import com.ds.annotation.EsbBeanAnnotation;
import com.ds.common.ContextType;
import com.ds.common.JDSConstants;
import com.ds.common.JDSException;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.config.ConnectInfo;
import com.ds.engine.ConnectInfo;
import com.ds.server.JDSSessionHandle;
import com.ds.server.JDSServer;
import com.ds.server.JDSClientService;
import com.ds.vfs.VFSException;
import com.ds.vfs.engine.VFSServer;
import com.ds.vfs.manager.inner.EIFileInfo;
import com.ds.vfs.manager.inner.EIFolder;
import com.ds.vfs.service.VFSClient;

@EsbBeanAnnotation(dataType = ContextType.Function)
public class VFSClientServiceImpl implements VFSClient {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, VFSClientServiceImpl.class);

    private JDSClientService clientService;
    private String systemCode;
    private String personId;
    private String account;
    private String password;

    public VFSClientServiceImpl() {
    }

    @Override
    public void connect(ConnectInfo connectInfo) throws JDSException {
        this.personId = connectInfo.getUserId();
        this.account = connectInfo.getAccount();
        this.password = connectInfo.getPassword();
    }

    @Override
    public void setClientService(JDSClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public JDSClientService getClientService() {
        return this.clientService;
    }

    @Override
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Override
    public String getSystemCode() {
        return this.systemCode;
    }

    @Override
    public Folder mkDir(String path) throws VFSException {
        try {
            return (Folder) VFSRoManager.getInstance().mkDir(path, this.personId);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public FileInfo createFile(String path, String name) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByPath(path);
            EIFileInfo fileInfo = VFSRoManager.getInstance().createFile(folder, name, name, this.personId);
            return (FileInfo) fileInfo;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public Folder getFolderByPath(String path) throws VFSException {
        try {
            return (Folder) VFSRoManager.getInstance().getFolderByPath(path);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public FileInfo getFileByPath(String path) throws VFSException {
        try {
            return (FileInfo) VFSRoManager.getInstance().getFileInfoByPath(path);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void delete(String path) throws VFSException {
        try {
            EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByPath(path);
            if (fileInfo != null) {
                VFSRoManager.getInstance().deleteFileIds(fileInfo.getID());
            }
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public Folder getFolderByID(String folderId) throws VFSException {
        try {
            return (Folder) VFSRoManager.getInstance().getFolderByID(folderId);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public FileInfo getFileInfoByID(String fileId) throws VFSException {
        return (FileInfo) VFSRoManager.getInstance().getFileInfoByID(fileId);
    }

    @Override
    public FileVersion getVersionById(String versionId) throws VFSException {
        return VFSRoManager.getInstance().getFileVersionByID(versionId);
    }

    @Override
    public FileVersion createFileVersion(String path, String fileHash) throws VFSException {
        try {
            EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByPath(path);
            if (fileInfo != null) {
                return VFSRoManager.getInstance().createFileVersion(fileInfo);
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void updateFileInfo(String path, String name, String description) throws VFSException {
        try {
            EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByPath(path);
            if (fileInfo != null) {
                if (name != null) {
                    fileInfo.setName(name);
                }
                if (description != null) {
                    fileInfo.setDescrition(description);
                }
                fileInfo.setModified(true);
                VFSRoManager.getInstance().commitFolder(fileInfo.getFolder());
            }
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void updateFolderInfo(String path, String name, String description, FolderType type) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByPath(path);
            if (folder != null) {
                if (name != null) {
                    folder.setName(name);
                }
                if (description != null) {
                    folder.setDescrition(description);
                }
                if (type != null) {
                    folder.setFolderType(type);
                }
                folder.setModified(true);
                VFSRoManager.getInstance().updateFolderInfo(folder);
            }
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void updateFolderState(String path, FolderState state) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByPath(path);
            if (folder != null) {
                folder.setState(state);
                folder.setModified(true);
                VFSRoManager.getInstance().updateFolderInfo(folder);
            }
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void copyFolder(String sourcePath, String targetPath) throws VFSException {
    }

    @Override
    public void copyFolder(String sourcePath, String targetPath, boolean overwrite) throws VFSException {
    }

    @Override
    public void copyFile(String sourcePath, String targetPath) throws VFSException {
    }

    @Override
    public FileVersion getVersionByPath(String path) throws VFSException {
        try {
            EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByPath(path);
            if (fileInfo != null) {
                return fileInfo.getCurrentVersion();
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public List<Folder> getChildrenFolderList(String folderId) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByID(folderId);
            if (folder != null) {
                return (List<Folder>) folder.getChildrenList();
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public List<FileInfo> getChiledFileList(String folderId) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByID(folderId);
            if (folder != null) {
                return (List<FileInfo>) folder.getFileList();
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void deleteFolder(String folderId) throws VFSException {
        try {
            VFSRoManager.getInstance().deleteFolderIds(folderId);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void deleteFile(List<String> fileIds) throws VFSException {
        VFSRoManager.getInstance().deleteFileIds(fileIds.toArray(new String[0]));
    }

    @Override
    public FileCopy getFileCopyById(String id) throws VFSException {
        return VFSRoManager.getInstance().getFileCopyById(id);
    }

    @Override
    public FileView getFileViewByID(String viewId) throws VFSException {
        return VFSRoManager.getInstance().getFileViewByID(viewId);
    }

    @Override
    public FileLink getFileLinkByID(String linkId) throws VFSException {
        return VFSRoManager.getInstance().getFileLinkByID(linkId);
    }

    @Override
    public List<FileInfo> getPersonDeletedFile(String userId) throws VFSException {
        return (List<FileInfo>) VFSRoManager.getInstance().getPersonDeletedFile(userId);
    }

    @Override
    public List<Folder> getPersonDeletedFolder(String userId) throws VFSException {
        return (List<Folder>) VFSRoManager.getInstance().getPersonDeletedFolder(userId);
    }

    @Override
    public FileInfo getDeletedFile(String fileId) throws VFSException {
        return (FileInfo) VFSRoManager.getInstance().getFileInfoByID(fileId);
    }

    @Override
    public Folder getDeletedFolder(String folderId) throws VFSException {
        try {
            return (Folder) VFSRoManager.getInstance().getFolderByID(folderId);
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public List<FileVersion> getVersionByHash(String hash) throws VFSException {
        return VFSRoManager.getInstance().getVersionByHash(hash);
    }

    @Override
    public void removeFileInfo(String fileId) throws VFSException {
        VFSRoManager.getInstance().removeFileInfo(fileId);
    }

    @Override
    public List<Folder> getChildrenFolderRecursivelyList(String folderId) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByID(folderId);
            if (folder != null) {
                return (List<Folder>) folder.getChildrenRecursivelyList();
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public List<FileInfo> getChiledFileRecursivelyList(String folderId) throws VFSException {
        try {
            EIFolder folder = VFSRoManager.getInstance().getFolderByID(folderId);
            if (folder != null) {
                return (List<FileInfo>) folder.getFileListRecursively();
            }
            return null;
        } catch (VFSFolderNotFoundException e) {
            throw new VFSException(e);
        }
    }

    @Override
    public void copyView(List<FileView> views, FileVersion newVersion) throws VFSException {
        VFSRoManager.getInstance().copyView(views, newVersion);
    }

    @Override
    public void updateFileVersionInfo(String fileVersionId, String hash) throws VFSException {
        FileVersion version = VFSRoManager.getInstance().getFileVersionByID(fileVersionId);
        if (version != null) {
            version.setFileObjectId(hash);
            VFSRoManager.getInstance().updateVersion(version);
        }
    }

    @Override
    public void updateFileViewInfo(FileView view) throws VFSException {
        VFSRoManager.getInstance().updateView(view);
    }
}
