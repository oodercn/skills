package net.ooder.vfs;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.ConfigCode;
import java.util.List;
import java.util.Map;

public interface VFSManager {

    @MethodChinaName(cname = "初始化子系统", display = false)
    public void init(ConfigCode subSystemId);

    @MethodChinaName(cname = "获取所有顶级文件夹")
    public List<Folder> getTopFolder();

    @MethodChinaName(cname = "根据文件夹path获取文件")
    public Folder getFolderByPath(String path);

    @MethodChinaName(cname = "创建文件")
    public FileInfo createFile(String path, String personId) throws VFSException;

    @MethodChinaName(cname = "创建版本")
    public FileVersion createFileVersion(String path, String hash, String personId) throws VFSException;

    @MethodChinaName(cname = "创建文件夹")
    public Folder mkDir(String path) throws VFSException;

    public Folder mkDir(String path, String personId) throws VFSException;

    @MethodChinaName(cname = "根据文件夹ID获取文件")
    public Folder getFolderByID(String folderId);

    @MethodChinaName(cname = "根据文件ID获取文件")
    public FileInfo getFileInfoByID(String fileId);

    @MethodChinaName(cname = "根据逻辑地址获取文件信息")
    public FileInfo getFileInfoByPath(String path);

    @MethodChinaName(cname = "清空文件夹")
    public void cleanFolder(String path);

    @MethodChinaName(cname = "清空文件")
    public void cleanFile(String path);

    @MethodChinaName(cname = "恢复文件")
    public void resotreDeletedFile(String path);

    @MethodChinaName(cname = "恢复文件夹")
    public void resotreDeletedFolder(String path);

    @MethodChinaName(cname = "移动文件夹")
    public void moveFolder(String path, String newpath);

    @MethodChinaName(cname = "移动文件")
    public void moveFile(String path, String newpath);

    @MethodChinaName(cname = "获取文件实体")
    public FileObject getFileObjectByHash(String hash);

    @MethodChinaName(cname = "获取文件实体")
    public FileObject getFileObjectById(String id);

    @MethodChinaName(cname = "获取文件副本")
    public FileCopy getFileCopyById(String id);

    @MethodChinaName(cname = "获取文件版本")
    public FileVersion getVersionById(String versionId) throws VFSException;

    @MethodChinaName(cname = "添加文件权限")
    public boolean addFileRight(String fileId, String roleId, int type);

    @MethodChinaName(cname = "删除文件权限")
    public void deleteFileRight(String fileId, String roleId, int type);

    @MethodChinaName(cname = "添加文件夹权限")
    public boolean addFolderRight(String folderId, String roleId, int type);

    @MethodChinaName(cname = "删除文件夹权限")
    public void deleteFolderRight(String folderId, String roleId, int type);

    @MethodChinaName(cname = "排除文件权限")
    public boolean addFileDiabled(String fileId, String personId);

    @MethodChinaName(cname = "排除文件夹权限")
    public boolean addFolderDiabled(String folderId, String personId, String fileId);

    public void deleteFile(String... fileIds) throws VFSException;

    public void deleteFileVersion(String versionId) throws VFSException;

    public void deleteFolder(String folderId) throws VFSFolderNotFoundException;

    public String commit(String userId, String path, String hash, long length, Map context) throws VFSException;

    public void commitFolder(Folder folder);

    public void updateFolderInfo(Folder folder);

    public void copyView(List<FileView> views, FileVersion newVersion);

    public void copyFolder(String folderId, String newFolderId, List<String> limitFileIds) throws VFSException;

    public void copyFolder(String folderId, String newFolderId) throws VFSException;

    public void copyFolder(Folder pfolder, Folder pNewFolder) throws VFSException;

    public String copyFile(FileInfo EFileInfo, Folder newFolder) throws VFSException;

    public String getSubSystemId();

    public List<FileInfo> getPersonDeletedFile(String userId) throws VFSException, VFSFolderNotFoundException;

    public List<Folder> getPersonDeletedFolder(String userId) throws VFSException;

    public FileInfo getDeletedFile(String fileId);

    public Folder getDeletedFolder(String folderId);

    public List<FileVersion> getVersionByHash(String hash);

    public void updateFileVersion(FileVersion fileVersion);

    public void removeFileInfo(String fileId);

    public FileLink getFileLinkByID(String linkId);

    public List<FileInfo> getChiledFileList(String id);

    public List<Folder> getChildrenFolderRecursivelyList(String id);

    public List<Folder> getChildrenFolderList(String id);

    public List<FileInfo> getChiledFileRecursivelyList(String id);

    public FileView getFileViewByID(String fileViewId);

    public void updateFileViewInfo(FileView view);
}
