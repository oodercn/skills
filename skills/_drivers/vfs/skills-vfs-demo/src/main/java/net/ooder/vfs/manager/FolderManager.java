package net.ooder.vfs.manager;

import net.ooder.common.JDSException;
import net.ooder.common.Condition;
import net.ooder.common.Filter;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.manager.inner.*;

import java.util.List;

public interface FolderManager {

    public List<String> loadTopFolder(ConfigCode sysCode);

    public List<DBFolder> loadAll(Integer pageSize);

    public void reLoad(DBFolder folder, Boolean hasLoadBase, Boolean hasLoadChiled, Boolean hasLoadFiles);

    public List<EIFolder> searchFolder(Condition condition, Filter filter) throws JDSException;

    public EIFolder loadById(String folderId) throws VFSFolderNotFoundException;

    public List<EIFolder> loadChildren(DBFolder folder) throws VFSException;

    public List<EIFileInfo> loadFiles(DBFolder folder) throws VFSException;

    public List<EIFolder> loadByWhere(String where) throws JDSException ;

    public void remove(String folderId) throws VFSFolderNotFoundException;

    public void commit(EIFolder folder) throws VFSException;

    public void deleteFolderIds(String... folderIds) throws VFSException;

    public EIFolder createFolder(DBFolder pfolder, String name, String descrition, String personId) throws VFSFolderNotFoundException;

    public EIFileInfo createFile(EIFolder pfolder, String name);

    public void delete(String folderId);

    public List<EIFolder> getDeletedChildrenList(String folderID) throws VFSException;

    public List<EIFileInfo> getDeletedFileList(String folderID) throws VFSException;

    public void deleteRight(String folderId, String roleId) throws VFSException;

    public void addRight(DBFolderRight right) throws VFSException;

    public void prepareCache(List ids) ;

    public List<EIFolder> getPersonDeletedFolder(String personId) throws VFSException;

}
