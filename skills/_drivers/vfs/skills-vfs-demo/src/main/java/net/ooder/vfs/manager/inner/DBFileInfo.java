package net.ooder.vfs.manager.inner;

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.vfs.*;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.FileInfoManager;
import net.ooder.vfs.manager.dbimpl.JdbcFileInfoManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBFileInfo implements EIFileInfo, Cacheable, Serializable, Comparable<DBFileInfo> {
    private static final long serialVersionUID = 7870681403705988152L;
    protected static Log log = LogFactory.getLog("vfs", DBFileInfo.class);
    private String ID;
    private String name;
    private String path = "";
    private int isRecycled;
    private int isLocked;
    private int fileType;
    private String personId;
    private long createTime;
    private String descrition;
    private String folderId;

    private String processInstId;
    private String activityInstId;
    private String histroyId;
    private String right;
    private String oldFolderId;

    public boolean initialized = false;

    private List<String> fileIdVersionList;
    public boolean fileIdVersionList_is_initialized;
    private List<String> fileIdLinkList;
    boolean fileIdLinkList_is_initialized;

    boolean extProperty_is_initialized;

    private boolean reLoadPath = false;
    public long updateTime;
    boolean isModified = false;
    private FileInfoManager fileManager;

    public DBFileInfo() {
        this.setModified(false);
        this.fileManager = JdbcFileInfoManager.getInstance();
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.setModified(true);
        if (this.initialized) { reLoadPath = true; getPath(); }
    }

    public int compareTo(DBFileInfo o) { return 0; }

    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfString(ID);
        size += CacheSizes.sizeOfString(path);
        size += CacheSizes.sizeOfObject(isLocked);
        size += CacheSizes.sizeOfObject(fileType);
        size += CacheSizes.sizeOfObject(isRecycled);
        size += CacheSizes.sizeOfObject(personId);
        size += CacheSizes.sizeOfObject(createTime);
        size += CacheSizes.sizeOfString(folderId);
        size += CacheSizes.sizeOfString(descrition);
        size += CacheSizes.sizeOfString(processInstId);
        size += CacheSizes.sizeOfString(activityInstId);
        size += CacheSizes.sizeOfString(histroyId);
        size += CacheSizes.sizeOfList(fileIdVersionList);
        size += CacheSizes.sizeOfObject(fileIdLinkList);
        return size;
    }

    public String getID() { return ID; }

    public void setFileId(String fileId) { this.ID = fileId; }

    public void addFileVersion(String versionFileId) {
        prepareFileVersion();
        if (fileIdVersionList == null) { fileIdVersionList = new ArrayList(); }
        if (!fileIdVersionList.contains(versionFileId)) { this.fileIdVersionList.add(versionFileId); }
        fileIdVersionList_is_initialized = true;
    }

    public void addFileLink(String linkId) {
        if (fileIdLinkList == null) { fileIdLinkList = new ArrayList(); }
        this.fileIdLinkList.add(linkId);
    }

    public String getPath() {
        String parentPath = "";
        if (this.getFolder() != null) { parentPath = this.getFolder().getPath(); } else { return null; }
        if (parentPath.endsWith("/")) { path = parentPath + this.getName(); }
        else { path = parentPath + "/" + this.getName(); }
        return path;
    }

    public void setPath(String path) { this.path = path; }

    private void prepareFileVersion() {
        if (!fileIdVersionList_is_initialized) {
            try { fileManager.loadVersion(this); } catch (VFSException e) { e.printStackTrace(); }
        }
    }

    public List<FileVersion> getVersionList() {
        prepareFileVersion();
        if (fileIdVersionList == null) { return new ArrayList(); }
        List<FileVersion> versionList = new ArrayList<FileVersion>();
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(fileIdVersionList);
        for (int i = 0, n = arrayList.size(); i < n; i++) {
            try {
                FileVersion version = cacheManager.getFileVersionByID((String) arrayList.get(i));
                if (version != null) { versionList.add(version); }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        Collections.sort(versionList, new Comparator<FileVersion>() {
            public int compare(FileVersion o1, FileVersion o2) { return o2.getIndex() - o1.getIndex(); }
        });
        return versionList;
    }

    public String getPersonId() { return this.personId; }
    public void setPersonId(String personId) { this.setModified(true); this.personId = personId; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public void setID(String iD) { ID = iD; }
    public void setFolderId(String folderId) { this.folderId = folderId; }
    public void setProcessInstId(String processInstId) { this.processInstId = processInstId; }
    public void setActivityInstId(String activityInstId) { this.activityInstId = activityInstId; }

    public int hashCode() {
        final int prime = 31; int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DBFileInfo other = (DBFileInfo) obj;
        if (ID == null) { if (other.ID != null) return false; } else if (!ID.equals(other.ID)) return false;
        return true;
    }

    public synchronized FileVersion getCurrentVersion() {
        List<FileVersion> versionList = getVersionList();
        if (!versionList.isEmpty()) {
            Collections.sort(versionList, new Comparator<FileVersion>() {
                public int compare(FileVersion o1, FileVersion o2) { return o2.getIndex() - o1.getIndex(); }
            });
            return versionList.get(0);
        }
        FileVersion version = this.createFileVersion();
        versionList.add(version); return version;
    }

    public String getCurrentVersonFileHash() {
        FileVersion version = getCurrentVersion();
        if (version != null) { return version.getFileObjectId(); }
        return null;
    }

    public Long getCurrentVersonFileLength() {
        FileVersion version = getCurrentVersion();
        if (version != null && version.getFileObject() != null) { return version.getFileObject().getLength(); }
        return 0L;
    }

    public Long getCurrentVersonFileCreateTime() {
        FileVersion version = getCurrentVersion();
        if (version != null && version.getFileObject() != null) { return version.getFileObject().getCreateTime(); }
        return 0L;
    }

    public EIFolder getFolder() {
        EIFolder folder = null;
        try { folder = VFSRoManager.getInstance().getFolderByID(folderId); } catch (VFSFolderNotFoundException e) { e.printStackTrace(); }
        return folder;
    }

    public String getFolderId() { return folderId; }
    public String getProcessInstId() { return processInstId; }
    public String getActivityInstId() { return activityInstId; }
    public List<String> getVersionIds() {
        prepareFileVersion();
        if (fileIdVersionList == null) { fileIdVersionList = new ArrayList<String>(); }
        return fileIdVersionList;
    }

    public List<FileView> getCurrentViews() {
        FileVersion verson = getCurrentVersion();
        if (verson != null) { return verson.getViews(); }
        return null;
    }

    private void prepareFileLinks() {
        if (!fileIdLinkList_is_initialized) {
            try { fileManager.loadLinks(this); } catch (VFSException e) { e.printStackTrace(); }
        }
    }

    public List<FileLink> getLinks() {
        prepareFileLinks();
        if (fileIdLinkList == null) { return new ArrayList(); }
        List linkList = new ArrayList();
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        for (int i = 0, n = fileIdLinkList.size(); i < n; i++) {
            try { linkList.add(cacheManager.getFileLinkByID((String) fileIdLinkList.get(i))); } catch (Exception ex) {}
        }
        return linkList;
    }

    public int getFileType() { return fileType; }
    public void setFileType(int fileType) { this.fileType = fileType; }
    public String getCurrentVersonId() {
        if (getCurrentVersion() != null) { return getCurrentVersion().getVersionID(); }
        return null;
    }
    public int getIsRecycled() { return isRecycled; }
    public void setIsRecycled(int isRecycled) { this.isRecycled = isRecycled; }
    public String getDescrition() { return descrition; }
    public void setDescrition(String descrition) { this.setModified(true); this.descrition = descrition; }
    public int getIsLocked() { return isLocked; }
    public void setIsLocked(int isLocked) { this.setModified(true); this.isLocked = isLocked; }
    public void setRight(String right) { this.setModified(true); this.right = right; }
    public String getRight() { return right; }

    public synchronized FileVersion createFileVersion() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        DBFileVersion version = cacheManager.createFileVersion(this);
        version.setFileId(this.ID);
        version.setCreateTime(System.currentTimeMillis());
        return version;
    }

    public String getSysid() { return this.getFolder().getSysid(); }

    public MD5InputStream getCurrentVersonInputStream() {
        FileVersion version = (FileVersion) getCurrentVersion();
        MD5InputStream inputStream = version.getInputStream();
        synchronized (version) {
            int k = 0;
            while (inputStream == null && k < this.getVersionList().size() - 1) {
                k = k + 1;
                FileVersion versionproxy = (FileVersion) this.getVersionList().get(k);
                inputStream = versionproxy.getInputStream();
            }
        }
        return inputStream;
    }

    public MD5OutputStream getCurrentVersonOutputStream() {
        FileVersion version = (FileVersion) getCurrentVersion();
        MD5OutputStream outStream = version.getOutputStream();
        synchronized (version) {
            if (outStream == null && version.getIndex() > 1) {
                FileVersion versionproxy = (FileVersion) this.getVersionList().get(version.getIndex() - 1);
                outStream = versionproxy.getOutputStream();
            }
        }
        return outStream;
    }

    public String getHistroyId() { return histroyId; }
    public void setHistroytId(String histroyId) { this.setModified(true); this.histroyId = histroyId; }
    public void setUpdateTime(long updateTime) { this.setModified(true); this.updateTime = updateTime; }
    public long getUpdateTime() { return updateTime; }
    public boolean isModified() { return isModified; }
    public void setModified(boolean isModified) { this.isModified = isModified; }
    public void setOldFolderId(String oldFolderId) { this.oldFolderId = oldFolderId; }
    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean initialized) { this.initialized = initialized; }
    public boolean isFileIdLinkList_is_initialized() { return fileIdLinkList_is_initialized; }
    public void setFileIdLinkList_is_initialized(boolean v) { this.fileIdLinkList_is_initialized = v; }
    public boolean isFileIdVersionList_is_initialized() { return fileIdVersionList_is_initialized; }
    public void setFileIdVersionList_is_initialized(boolean v) { this.fileIdVersionList_is_initialized = v; }
    public List<String> getFileIdLinkList() { prepareFileLinks(); if (fileIdLinkList == null) { return new ArrayList(); } return fileIdLinkList; }
    public void setFileIdLinkList(List<String> l) { this.fileIdLinkList = l; }
    public List<String> getFileIdVersionList() { if (fileIdVersionList == null) { fileIdVersionList = new ArrayList(); } return fileIdVersionList; }
    public void setFileIdVersionList(List<String> l) { this.fileIdVersionList = l; }
    public void writeTo(OutputStream outstream) throws IOException { DBFileVersion version = (DBFileVersion) this.getCurrentVersion(); version.writeTo(outstream); }
    @Override
    public String toString() { return this.name; }
}
