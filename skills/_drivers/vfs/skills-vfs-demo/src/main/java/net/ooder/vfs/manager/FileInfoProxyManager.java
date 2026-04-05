package net.ooder.vfs.manager;

import net.ooder.vfs.FileInfo;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.proxy.FileInfoProxy;

import java.util.List;


public interface FileInfoProxyManager  {

    public List<FileInfo> getFileInfoProxyList(List<EIFileInfo> fileInfoList) ;

    public FileInfo loadFileInfo(EIFileInfo fileInfo);

    void loadRoles(FileInfoProxy  fileInfo)throws VFSException;
}
