package net.ooder.vfs.manager;

import net.ooder.vfs.FileView;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.manager.inner.DBFileView;

public interface FileViewManager  {

    public DBFileView createView(String versionId, Integer fileIndex) ;

    public Integer loadAll(Integer pageSize);

    public DBFileView loadById(String viewId) ;

    public void commit(DBFileView view) throws VFSException;

    public FileView save(DBFileView view) ;

    public void delete(String viewId) throws VFSException ;

}
