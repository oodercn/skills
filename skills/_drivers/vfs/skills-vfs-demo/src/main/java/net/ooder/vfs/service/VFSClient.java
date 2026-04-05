package net.ooder.vfs.service;

import com.ds.common.ConfigCode;
import com.ds.common.JDSException;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.config.ConnectInfo;
import com.ds.server.JDSClientService;
import com.ds.vfs.*;
import com.ds.annotation.MethodChinaName;

import java.util.List;

public interface VFSClient {

    @MethodChinaName(cname = "连接")
    void connect(ConnectInfo connectInfo) throws JDSException;

    @MethodChinaName(cname = "设置客户端服务")
    void setClientService(JDSClientService clientService);

    @MethodChinaName(cname = "获取客户端服务")
    JDSClientService getClientService();

    @MethodChinaName(cname = "设置系统编码")
    void setSystemCode(String systemCode);

    @MethodChinaName(cname = "获取系统编码")
    String getSystemCode();

    @MethodChinaName(cname = "创建文件夹")
    Folder mkDir(String path) throws VFSException;

    @MethodChinaName(cname = "创建文件")
    FileInfo createFile(String path, String name) throws VFSException;

    @MethodChinaName(cname = "根据路径获取文件夹")
    Folder getFolderByPath(String path) throws VFSException;

    @MethodChinaName(cname = "根据路径获取文件")
    FileInfo getFileByPath(String path) throws VFSException;

    @MethodChinaName(cname = "删除文件/文件夹")
    void delete(String path) throws VFSException;

    @MethodChinaName(cname = "根据ID获取文件夹")
    Folder getFolderByID(String folderId) throws VFSException;

    @MethodChinaName(cname = "根据ID获取文件")
    FileInfo getFileInfoByID(String fileId) throws VFSException;

    @MethodChinaName(cname = "根据ID获取版本")
    FileVersion getVersionById(String versionId) throws VFSException;

    @MethodChinaName(cname = "创建版本")
    FileVersion createFileVersion(String path, String fileHash) throws VFSException;

    @MethodChinaName(cname = "更新文件信息")
    void updateFileInfo(String path, String name, String description) throws VFSException;

    @MethodChinaName(cname = "更新文件夹信息")
    void updateFolderInfo(String path, String name, String description, FolderType type) throws VFSException;

    @MethodChinaName(cname = "更新文件夹状态")
    void updateFolderState(String path, FolderState state) throws VFSException;

    @MethodChinaName(cname = "复制文件夹")
    void copyFolder(String sourcePath, String targetPath) throws VFSException;

    @MethodChinaName(cname = "复制文件夹(覆盖)")
    void copyFolder(String sourcePath, String targetPath, boolean overwrite) throws VFSException;

    @MethodChinaName(cname = "复制文件")
    void copyFile(String sourcePath, String targetPath) throws VFSException;

    @MethodChinaName(cname = "根据路径获取版本")
    FileVersion getVersionByPath(String path) throws VFSException;

    @MethodChinaName(cname = "获取子文件夹列表")
    List<Folder> getChildrenFolderList(String folderId) throws VFSException;

    @MethodChinaName(cname = "获取子文件列表")
    List<FileInfo> getChiledFileList(String folderId) throws VFSException;

    @MethodChinaName(cname = "删除文件夹")
    void deleteFolder(String folderId) throws VFSException;

    @MethodChinaName(cname = "删除多个文件")
    void deleteFile(List<String> fileIds) throws VFSException;

    @MethodChinaName(cname = "获取文件副本")
    FileCopy getFileCopyById(String id) throws VFSException;

    @MethodChinaName(cname = "获取文件视图")
    FileView getFileViewByID(String viewId) throws VFSException;

    @MethodChinaName(cname = "获取文件链接")
    FileLink getFileLinkByID(String linkId) throws VFSException;

    @MethodChinaName(cname = "获取用户已删除文件")
    List<FileInfo> getPersonDeletedFile(String userId) throws VFSException;

    @MethodChinaName(cname = "获取用户已删除文件夹")
    List<Folder> getPersonDeletedFolder(String userId) throws VFSException;

    @MethodChinaName(cname = "获取已删除文件")
    FileInfo getDeletedFile(String fileId) throws VFSException;

    @MethodChinaName(cname = "获取已删除文件夹")
    Folder getDeletedFolder(String folderId) throws VFSException;

    @MethodChinaName(cname = "根据hash获取版本列表")
    List<FileVersion> getVersionByHash(String hash) throws VFSException;

    @MethodChinaName(cname = "移除文件信息")
    void removeFileInfo(String fileId) throws VFSException;

    @MethodChinaName(cname = "获取递归子文件夹列表")
    List<Folder> getChildrenFolderRecursivelyList(String folderId) throws VFSException;

    @MethodChinaName(cname = "获取递归子文件列表")
    List<FileInfo> getChiledFileRecursivelyList(String folderId) throws VFSException;

    @MethodChinaName(cname = "复制视图")
    void copyView(List<FileView> views, FileVersion newVersion) throws VFSException;

    @MethodChinaName(cname = "更新版本信息")
    void updateFileVersionInfo(String fileVersionId, String hash) throws VFSException;

    @MethodChinaName(cname = "更新视图信息")
    void updateFileViewInfo(FileView view) throws VFSException;
}
