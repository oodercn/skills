package net.ooder.vfs.jdbc.cache;

import net.ooder.vfs.FileCopy;
import net.ooder.vfs.FileLink;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.manager.inner.EIFolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultVfsCacheProvider implements VfsCacheProvider {

    private final Map<String, EIFolder> folderCache = new ConcurrentHashMap<>();
    private final Map<String, EIFileInfo> fileCache = new ConcurrentHashMap<>();
    private final Map<String, FileVersion> fileVersionCache = new ConcurrentHashMap<>();
    private final Map<String, FileView> fileViewCache = new ConcurrentHashMap<>();
    private final Map<String, FileLink> fileLinkCache = new ConcurrentHashMap<>();
    private final Map<String, FileCopy> fileCopyCache = new ConcurrentHashMap<>();

    @Override
    public Map<String, EIFolder> getFolderCache() { return folderCache; }

    @Override
    public Map<String, EIFileInfo> getFileCache() { return fileCache; }

    @Override
    public Map<String, FileVersion> getFileVersionCache() { return fileVersionCache; }

    @Override
    public Map<String, FileView> getFileViewCache() { return fileViewCache; }

    @Override
    public Map<String, FileLink> getFileLinkCache() { return fileLinkCache; }

    @Override
    public Map<String, FileCopy> getFileCopyCache() { return fileCopyCache; }
}
