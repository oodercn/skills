package net.ooder.vfs.jdbc.cache;

import net.ooder.vfs.FileCopy;
import net.ooder.vfs.FileLink;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.manager.inner.EIFolder;

import java.util.Map;

public interface VfsCacheProvider {

    Map<String, EIFolder> getFolderCache();

    Map<String, EIFileInfo> getFileCache();

    Map<String, FileVersion> getFileVersionCache();

    Map<String, FileView> getFileViewCache();

    Map<String, FileLink> getFileLinkCache();

    Map<String, FileCopy> getFileCopyCache();
}
