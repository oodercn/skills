package net.ooder.vfs.manager.inner;

import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.FolderManager;
import net.ooder.vfs.manager.dbimpl.JdbcFolderManager;

import java.io.Serializable;
import java.util.*;

public class DBFolder implements EIFolder, Cacheable, Serializable, Comparable<EIFolder> {
    private static final Log logger = LogFactory.getLog("vfs", DBFolder.class);
    private static final long serialVersionUID = 1L;
    private String ID, name, sysid, path = "";
    private int hit;
    private String parentId, personId;
    private FolderType folderType = FolderType.folder;
    private long size;
    private int orderNum, recycle;
    private FolderState state = FolderState.tested;
    private String descrition;
    private long createTime;
    public boolean initialized = false;
    public int index;
    public long updateTime;
    public String activityInstId, activityInstHistoryId;
    private Set<String> fileIdList;
    private boolean fileIdList_is_initialized;
    private Set<String> childNameList, childIdList;
    private boolean childIdList_is_initialized;
    boolean extProperty_is_initialized, isModified = false;
    private FolderManager folderManager;

    public DBFolder() {
        this.ID = UUID.randomUUID().toString();
        this.folderManager = JdbcFolderManager.getInstance();
        isModified = false;
    }

    public void addFileInfo(String fileId) {
        prepareFiles();
        if (fileIdList == null) { fileIdList = new LinkedHashSet<>(); }
        if (!fileIdList.contains(fileId)) { fileIdList.add(fileId); }
    }
    public void addFileInfo(EIFileInfo info) {
        addFileInfo(info.getID());
    }
    public void removeFileInfo(String fileId) {
        prepareFiles();
        if (fileIdList != null && fileIdList.contains(fileId)) { fileIdList.remove(fileId); }
    }
    public void addChildrenID(String fId) {
        this.prepareChildren();
        if (childIdList == null) { childIdList = new LinkedHashSet<>(); }
        if (!childIdList.contains(fId)) { childIdList.add(fId); }
    }
    synchronized void addChildName(String name) {
        this.prepareChildren();
        if (!this.getChildNameList().contains(name)) { this.getChildNameList().add(name); }
    }
    public synchronized void addChildren(EIFolder eiFloder) {
        if (eiFloder != null) { addChildrenID(eiFloder.getID()); addChildName(eiFloder.getName()); }
    }
    public String getName() { return name; }
    public void setName(String _name) {
        if ((_name != null && this.name == null) || (_name != null && !_name.equals(this.name))) { isModified = true; }
        this.name = _name;
    }
    public String getPath() {
        if (this.path == null || this.path.equals("")) {
            if (this.getFolderType().equals(FolderType.disk)) { return this.name + "/" + path; }
            else if (this.getParent() != null) { path = this.getParent().getPath(); }
            String folderName = this.getName();
            if (folderName == null) { folderName = this.getID(); }
            if (path.endsWith("/")) { path = path + folderName; }
            else { path = path + "/" + folderName; }
            this.path = path + "/";
        }
        return path;
    }
    public void setPath(String path) { this.path = path; }
    public int getHit() { return hit; }
    public void setHit(int _hit) { if (_hit != hit) { isModified = true; } this.hit = _hit; }

    public List<EIFolder> getChildrenList() {
        prepareChildren();
        if (childIdList == null) { return new ArrayList<EIFolder>(); }
        List<String> childIds = new ArrayList(); childIds.addAll(childIdList);
        List<EIFolder> childList = new ArrayList<EIFolder>();
        for (String childId : childIds) {
            try {
                if (childId != null) {
                    EIFolder folder = VFSRoManager.getInstance().getFolderByID(childId);
                    if (folder != null && !childId.equals(this.getID())) { childList.add(folder); }
                }
            } catch (VFSFolderNotFoundException e) { e.printStackTrace(); }
        }
        return childList;
    }

    private void prepareChildren() {
        if (!childIdList_is_initialized) {
            try { folderManager.loadChildren(this); } catch (VFSException e) { e.printStackTrace(); }
        }
    }

    public List<EIFileInfo> getFileList() {
        prepareFiles();
        if (fileIdList == null) { return new ArrayList<EIFileInfo>(); }
        List<EIFileInfo> fileList = new ArrayList<EIFileInfo>();
        List<String> fileIds = new ArrayList<String>(); fileIds.addAll(getFileIdList());
        for (int i = 0, n = fileIds.size(); i < n; i++) {
            if (fileIds.get(i) != null) {
                EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByID(fileIds.get(i));
                if (fileInfo != null) { fileList.add(fileInfo); }
            }
        }
        return fileList;
    }

    public Set<String> getFileIdList() {
        prepareFiles();
        if (fileIdList == null) { fileIdList = new LinkedHashSet<String>(); }
        return fileIdList;
    }

    private void prepareFiles() {
        try {
            if (!fileIdList_is_initialized) { folderManager.loadFiles(this); }
        } catch (VFSException e) { e.printStackTrace(); }
    }

    public int compareTo(EIFolder o) { return 0; }
    public String getUuid() { return ID; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(name) + CacheSizes.sizeOfString(path);
        size += CacheSizes.sizeOfString(activityInstId) + CacheSizes.sizeOfString(sysid);
        size += CacheSizes.sizeOfString(activityInstHistoryId) + CacheSizes.sizeOfString(parentId);
        size += CacheSizes.sizeOfString(personId) + CacheSizes.sizeOfString(descrition) + CacheSizes.sizeOfString(ID);
        size += CacheSizes.sizeOfObject(index) + CacheSizes.sizeOfObject(hit);
        size += CacheSizes.sizeOfObject(fileIdList) + CacheSizes.sizeOfObject(childIdList) + CacheSizes.sizeOfObject(childNameList);
        return size;
    }

    public String getID() { return ID; }

    public List<EIFolder> getChildrenRecursivelyList() {
        List<String> recursiveChildIdList = new ArrayList<String>();
        recursiveChildIdList = getChildIdsRecursivelyList(recursiveChildIdList, this);
        List<EIFolder> allChildList = new ArrayList<EIFolder>();
        for (int i = 0, n = recursiveChildIdList.size(); i < n; i++) {
            try { allChildList.add(VFSRoManager.getInstance().getFolderByID((String) recursiveChildIdList.get(i))); }
            catch (VFSFolderNotFoundException e) { e.printStackTrace(); }
        }
        return allChildList;
    }

    private List<String> getChildIdsRecursivelyList(List<String> list, EIFolder folder) {
        if (!folder.getChildIdList().isEmpty()) {
            List<EIFolder> folderList = folder.getChildrenList();
            for (int i = 0; i < folderList.size(); i++) {
                list.add(folderList.get(i).getID());
                list = getChildIdsRecursivelyList(list, folderList.get(i));
            }
        }
        return list;
    }

    public EIFolder getParent() {
        if (this.getFolderType().equals(FolderType.disk)) { return null; }
        try { return VFSRoManager.getInstance().getFolderByID(parentId); } catch (Exception ex) { return null; }
    }

    public List<EIFolder> getAllParent() {
        LinkedList<EIFolder> result = new LinkedList<EIFolder>();
        EIFolder parent = this.getParent();
        if (parent != null) { result.addLast(parent); }
        while (parent != null) {
            EIFolder p = parent.getParent();
            if (p == null) break;
            if (p != null) { result.addFirst(p); if (p.getID().equals(p.getParentId())) break; }
            parent = p;
        }
        return result;
    }

    public Set<String> getFileIdListRecursively() {
        Set<String> fileSet = new LinkedHashSet<String>();
        for (String fid : getFileIdList()) { fileSet.add(fid); }
        for (EIFolder folder : getChildrenRecursivelyList()) {
            if (folder == null) continue;
            for (String cfid : folder.getFileIdList()) { fileSet.add(cfid); }
        }
        return fileSet;
    }

    public Set<String> getChildIdList() {
        prepareChildren();
        if (childIdList == null) { childIdList = new LinkedHashSet<String>(); }
        Set<String> childIds = new LinkedHashSet<>(); childIds.addAll(childIdList);
        Map<String, String> nameMap = new HashMap<String, String>();
        for (String cid : childIds) {
            try {
                if (VFSRoManager.getInstance().getFolderByID(cid) != null) {
                    String nm = VFSRoManager.getInstance().getFolderByID(cid).getName();
                    if (nameMap.containsKey(nm)) { nameMap.put(nm, cid); }
                    else { childIdList.remove(nameMap.get(nm)); nameMap.put(nm, cid); }
                }
            } catch (VFSFolderNotFoundException e) { e.printStackTrace(); }
        }
        return childIdList;
    }

    public List<EIFileInfo> getFileListRecursively() {
        Set<String> fids = getFileIdListRecursively();
        if (fids == null) { return new ArrayList<EIFileInfo>(); }
        List<EIFileInfo> fl = new ArrayList<EIFileInfo>();
        for (String fid : fids) { fl.add(VFSRoManager.getInstance().getFileInfoByID(fid)); }
        return fl;
    }

    public String getPersonId() { return personId; }
    public FolderType getFolderType() { return folderType; }
    public void setFolderType(FolderType t) { if ((t != null && this.folderType == null) || (t != null && !t.equals(this.folderType))) { isModified = true; } this.folderType = t; }
    public void setPersonId(String p) { if ((p != null && this.personId == null) || (p != null && !p.equals(this.personId))) { isModified = true; } this.personId = p; }
    public String getActivityInstId() { return activityInstId; }
    public void setActivityInstId(String a) { if ((a != null && this.activityInstId == null) || (a != null && !a.equals(this.activityInstId))) { isModified = true; } this.activityInstId = a; }
    public String getActivityInstHistoryId() { return activityInstHistoryId; }
    public void setActivityInstHistoryId(String a) { if ((a != null && this.activityInstHistoryId == null) || (a != null && !a.equals(this.activityInstHistoryId))) { isModified = true; } this.activityInstHistoryId = a; }
    public String getSysid() { String tp = this.getPath(); tp = tp.substring(0, tp.indexOf("/")); return tp; }
    public void setSysid(String s) { if ((s != null && this.sysid == null) || (s != null && !s.equals(this.sysid))) { isModified = true; } this.sysid = s; }
    public void setID(String uid) { this.ID = uid; }
    public long getFolderSize() { return size; }
    public void setFolderSize(long s) { this.size = s; }
    public int getOrderNum() { isModified = true; return orderNum; }
    public void setOrderNum(int on) { isModified = true; this.orderNum = on; }
    public void setRecycle(int r) { isModified = true; this.recycle = r; }
    public int getRecycle() { return recycle; }
    public void setState(FolderState s) { isModified = true; this.state = s; }
    public FolderState getState() { return state; }
    public String getDescrition() { return descrition; }
    public void setDescrition(String d) { if ((d != null && this.descrition == null) || (d != null && !d.equals(this.descrition))) { isModified = true; } this.descrition = d; }
    public long getCreateTime() { if (createTime == 0L) { createTime = System.currentTimeMillis(); } return createTime; }
    public void setCreateTime(long ct) { this.createTime = ct; }
    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean b) { this.initialized = b; this.setModified(false); }
    public EIFileInfo createFile(String name, String descrition, String createPersonId) { return folderManager.createFile(this, name); }
    @Override
    public EIFolder createChildFolder(String name, String descrition, String createPersonId) throws VFSFolderNotFoundException {
        if (createPersonId == null) { createPersonId = this.getPersonId(); }
        return folderManager.createFolder(this, name, descrition, createPersonId);
    }
    public EIFileInfo createFile(String name, String createPersonId) { return createFile(name, name, createPersonId); }
    public EIFolder createChildFolder(String name, String createPersonId) throws VFSFolderNotFoundException { return createChildFolder(name, name, createPersonId); }
    public void setIndex(int idx) { this.index = idx; }
    public int getIndex() { return index; }
    public void setUpdateTime(long ut) { if (this.updateTime != ut) { isModified = true; } this.updateTime = ut; }
    public long getUpdateTime() { return updateTime; }
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object != null && object instanceof EIFileInfo) {
            EIFileInfo _folder = (EIFileInfo) object;
            if (ID.equals(_folder.getID())) return true;
            if (name != null && name.equals(_folder.getName())) return this.getPath().equals(_folder.getPath());
            return false;
        } else return false;
    }
    public boolean isModified() { return isModified; }
    public void setModified(boolean m) { this.isModified = m; }
    public Set<String> getChildNameList() {
        if (childNameList == null) { childNameList = new LinkedHashSet<>(); for (EIFolder eiFolder : this.getChildrenList()) { if (!childNameList.contains(eiFolder.getName())) { childNameList.add(eiFolder.getName()); } } }
        return childNameList;
    }
    public void setChildNameList(Set<String> cnl) { this.childNameList = cnl; }
    public boolean isChildIdList_is_initialized() { return childIdList_is_initialized; }
    public void setChildIdList_is_initialized(boolean b) { this.childIdList_is_initialized = b; }
    public boolean isFileIdList_is_initialized() { return fileIdList_is_initialized; }
    public void setFileIdList_is_initialized(boolean b) { this.fileIdList_is_initialized = b; }
    public void setFileIdList(Set<String> fl) { this.fileIdList = fl; }
    public void setChildIdList(Set<String> cl) { this.childIdList = cl; }
    @Override
    public String toString() { return this.getName(); }
}
