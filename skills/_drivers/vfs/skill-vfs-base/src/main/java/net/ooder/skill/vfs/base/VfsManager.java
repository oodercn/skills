package net.ooder.skill.vfs.base;

import java.io.InputStream;
import java.util.List;

/**
 * VfsManager VFS绠＄悊鍣ㄦ帴鍙? * 
 * @author Ooder Team
 * @version 2.3
 */
public interface VfsManager {

    FileInfo getFileInfoByID(String fileId);

    FileInfo createFile(String folderId, String name);

    boolean deleteFile(String fileId);

    List<FileInfo> listFiles(String folderId);

    Folder getFolderByID(String folderId);

    Folder createFolder(String parentId, String name);

    boolean deleteFolder(String folderId);

    List<Folder> listFolders(String parentId);

    InputStream downloadFile(String fileId);

    FileInfo uploadFile(String folderId, String name, InputStream content);
}
