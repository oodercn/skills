package net.ooder.skill.vfs.base;

import java.util.HashSet;
import java.util.Set;

/**
 * VfsCapabilities VFS鑳藉姏閰嶇疆
 * 
 * @author Ooder Team
 * @version 2.3
 */
public class VfsCapabilities {

    private boolean supportFileRead = true;
    private boolean supportFileWrite = true;
    private boolean supportFileDelete = true;
    private boolean supportFileCopy = true;
    private boolean supportFileMove = true;
    private boolean supportFileRename = true;
    private boolean supportStreamUpload = true;
    private boolean supportStreamDownload = true;
    private boolean supportFileVersion = false;
    private boolean supportFileShare = false;
    private boolean supportFileSearch = false;
    private boolean supportFolderCreate = true;
    private boolean supportFolderDelete = true;
    private boolean supportFolderList = true;

    private String providerType = "local";
    private String basePath;
    private String endpoint;

    public static VfsCapabilities forLocal() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("local");
        caps.setSupportFileVersion(true);
        caps.setSupportFileSearch(true);
        return caps;
    }

    public static VfsCapabilities forMinio() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("minio");
        caps.setSupportFileVersion(true);
        caps.setSupportFileShare(true);
        return caps;
    }

    public static VfsCapabilities forS3() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("s3");
        caps.setSupportFileVersion(true);
        caps.setSupportFileShare(true);
        return caps;
    }

    public Set<String> getUnsupportedCapabilities() {
        Set<String> unsupported = new HashSet<String>();
        if (!supportFileVersion) unsupported.add("file.version");
        if (!supportFileShare) unsupported.add("file.share");
        if (!supportFileSearch) unsupported.add("file.search");
        return unsupported;
    }

    public boolean requiresFallback() {
        return !supportFileVersion || !supportFileShare || !supportFileSearch;
    }

    public boolean isSupportFileRead() {
        return supportFileRead;
    }

    public void setSupportFileRead(boolean supportFileRead) {
        this.supportFileRead = supportFileRead;
    }

    public boolean isSupportFileWrite() {
        return supportFileWrite;
    }

    public void setSupportFileWrite(boolean supportFileWrite) {
        this.supportFileWrite = supportFileWrite;
    }

    public boolean isSupportFileDelete() {
        return supportFileDelete;
    }

    public void setSupportFileDelete(boolean supportFileDelete) {
        this.supportFileDelete = supportFileDelete;
    }

    public boolean isSupportFileCopy() {
        return supportFileCopy;
    }

    public void setSupportFileCopy(boolean supportFileCopy) {
        this.supportFileCopy = supportFileCopy;
    }

    public boolean isSupportFileMove() {
        return supportFileMove;
    }

    public void setSupportFileMove(boolean supportFileMove) {
        this.supportFileMove = supportFileMove;
    }

    public boolean isSupportFileRename() {
        return supportFileRename;
    }

    public void setSupportFileRename(boolean supportFileRename) {
        this.supportFileRename = supportFileRename;
    }

    public boolean isSupportStreamUpload() {
        return supportStreamUpload;
    }

    public void setSupportStreamUpload(boolean supportStreamUpload) {
        this.supportStreamUpload = supportStreamUpload;
    }

    public boolean isSupportStreamDownload() {
        return supportStreamDownload;
    }

    public void setSupportStreamDownload(boolean supportStreamDownload) {
        this.supportStreamDownload = supportStreamDownload;
    }

    public boolean isSupportFileVersion() {
        return supportFileVersion;
    }

    public void setSupportFileVersion(boolean supportFileVersion) {
        this.supportFileVersion = supportFileVersion;
    }

    public boolean isSupportFileShare() {
        return supportFileShare;
    }

    public void setSupportFileShare(boolean supportFileShare) {
        this.supportFileShare = supportFileShare;
    }

    public boolean isSupportFileSearch() {
        return supportFileSearch;
    }

    public void setSupportFileSearch(boolean supportFileSearch) {
        this.supportFileSearch = supportFileSearch;
    }

    public boolean isSupportFolderCreate() {
        return supportFolderCreate;
    }

    public void setSupportFolderCreate(boolean supportFolderCreate) {
        this.supportFolderCreate = supportFolderCreate;
    }

    public boolean isSupportFolderDelete() {
        return supportFolderDelete;
    }

    public void setSupportFolderDelete(boolean supportFolderDelete) {
        this.supportFolderDelete = supportFolderDelete;
    }

    public boolean isSupportFolderList() {
        return supportFolderList;
    }

    public void setSupportFolderList(boolean supportFolderList) {
        this.supportFolderList = supportFolderList;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
