package net.ooder.vfs.manager;

import net.ooder.common.JDSException;
import net.ooder.common.Condition;
import net.ooder.common.Filter;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFileNotFoundException;
import net.ooder.vfs.manager.inner.DBFileInfo;
import net.ooder.vfs.manager.inner.DBFileRight;
import net.ooder.vfs.manager.inner.EIFileInfo;

import java.util.List;

public interface FileInfoManager  {

    public void delete(String fileId) throws VFSException ;

    public void commit(EIFileInfo fileInfo, boolean update) throws VFSException ;

    public  EIFileInfo save(EIFileInfo fileInfo) ;

    List<DBFileInfo> loadAll(Integer pageSize);

    public EIFileInfo loadById(String fileId) ;

    public List<EIFileInfo> searchFile(Condition condition, Filter filter) throws JDSException ;

    public List<EIFileInfo> getPersonDeletedFile(String personId) throws VFSException, VFSFileNotFoundException ;

    public void loadLinks(DBFileInfo fileInfo) throws VFSException ;

    public void deleteFileIds(String... fileIds) throws VFSException;

    public List<FileVersion> loadVersion(EIFileInfo fileInfo) throws VFSException ;

    public void addRight(DBFileRight right) throws VFSException;

    public void deleteRight(String fileId, String roleId) throws VFSException ;

    void prepareCache(List v);
}
