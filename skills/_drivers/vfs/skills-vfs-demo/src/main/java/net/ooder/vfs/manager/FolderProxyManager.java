package net.ooder.vfs.manager;

import net.ooder.vfs.Folder;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.manager.inner.EIFolder;
import net.ooder.vfs.proxy.FolderProxy;

import java.util.List;

public interface FolderProxyManager {

    public FolderProxy loadFolder(EIFolder folder);

    public List<Folder> getFolderProxyList(List<EIFolder> folderList);

    void loadRoles(FolderProxy proxy) throws VFSException;

    public void delete(FolderProxy folder);

    public void commit(FolderProxy folder) throws VFSException;


}
