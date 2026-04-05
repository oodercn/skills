package net.ooder.vfs.manager;

import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.manager.inner.DBFileVersion;
import net.ooder.vfs.manager.inner.EIFileInfo;

import java.util.List;

public interface FileVersionManager {

    public List<DBFileVersion> loadAll(Integer pageSize);

    public DBFileVersion loadById(String versionId) throws VFSException;

    public DBFileVersion createFileVersion(EIFileInfo info);

    public void commit(DBFileVersion fileVersion) throws VFSException;

    public FileVersion save(DBFileVersion fileVersion);

    public List<FileVersion> getVersionByObjectId(String fileObjectId);

    public FileVersion loadByIndex(String fileId, Integer index);

    public List<FileView> loadView(DBFileVersion version) throws VFSException ;

    public boolean delete(String ID);

}
