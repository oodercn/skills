package net.ooder.vfs.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.RoleType;
import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.common.cache.CacheSizes;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.OrgManagerFactory;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.VFSManager;
import net.ooder.vfs.engine.DbVFSManager;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.FolderProxyManager;
import net.ooder.vfs.manager.dbimpl.JdbcFolderProxyManager;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.manager.inner.EIFolder;

import java.io.Serializable;
import java.util.*;

public class FolderProxy implements Folder, Serializable, Comparable<Folder> {
    private static final long serialVersionUID = 1L;
    @JSONField(name = "ID")
    private String ID;

    private String subSystemId;

    @JSONField(serialize = false)
    private Map<RoleType, List<String>> roleIdCache = new HashMap<RoleType, List<String>>();


    @JSONField(serialize = false)
    boolean roleIdCache_is_initialized;

    @JSONField(serialize = false)
    boolean extProperty_is_initialized;

    @JSONField(serialize = false)
    boolean isModified = false;


    @JSONField(serialize = false)
    private FolderProxyManager manager;

    @JSONField(serialize = false)
    private EIFolder eiFolder;

    public FolderProxy(EIFolder eiFolder, ConfigCode subSystemId) {
        if (subSystemId == null) {
            subSystemId = OrgConstants.VFSCONFIG_KEY;
        }
        this.eiFolder = eiFolder;
        this.ID = eiFolder.getID();
        this.subSystemId = subSystemId.getType();
        this.manager = JdbcFolderProxyManager.getInstance(subSystemId);
    }

    public String getName() {
        return this.getFolder().getName();
    }

    public void setName(String name) {
        this.getFolder().setName(name);

    }

    public String getPath() {
        String path = this.getFolder().getPath();
        return path;

    }

    public Integer getHit() {
        return this.getFolder().getHit();
    }

    public void setHit(Integer hit) {
        this.getFolder().setHit(hit);
    }

    public int compareTo(Folder o) {
        if (this.getPath().startsWith(o.getPath())) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getUuid() {
        return this.getFolder().getID();
    }

    public String getParentId() {
        return this.getFolder().getParentId();
    }

    public void setParentId(String parentId) {
        this.getFolder().setParentId(parentId);
    }

    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;

        size += CacheSizes.sizeOfMap(roleIdCache);
        return size;
    }

    public String getID() {
        return ID;
    }

    public void addRole(RoleType type, String roleId) {
        List<String> roleIdList = this.roleIdCache.get(type);
        if (roleIdList == null) {
            roleIdList = new ArrayList();
            roleIdCache_is_initialized = true;
        }
        roleIdList.add(roleId);
        roleIdCache.put(type, roleIdList);
    }


    @JSONField(serialize = false)
    public Folder getParent() {

        try {
            VFSManager vfsManager = new DbVFSManager();
            return (Folder) vfsManager.getFolderByID(this.getParentId());
        } catch (Exception ex) {
            return null;
        }
    }

    public String getPersonId() {
        return this.getFolder().getPersonId();
    }

    public FolderType getFolderType() {
        return this.getFolder().getFolderType();
    }

    public void setFolderType(FolderType folderType) {
        this.getFolder().setFolderType(folderType);
    }

    public void setPersonId(String personId) {
        this.getFolder().setPersonId(personId);
    }

    public void setID(String uid) {
        this.ID = uid;
    }

    public Long getFolderSize() {
        return this.getFolder().getFolderSize();
    }

    public void setFolderSize(Long size) {
        this.getFolder().setFolderSize(size);
    }

    public int getOrderNum() {
        return this.getFolder().getOrderNum();
    }

    public void setOrderNum(int orderNum) {
        this.getFolder().setOrderNum(orderNum);
    }

    public int getRecycle() {
        return this.getFolder().getRecycle();
    }

    public void setState(FolderState state) {
        this.getFolder().setState(state);
    }

    public FolderState getState() {
        return this.getFolder().getState();
    }

    public String getDescrition() {
        return this.getFolder().getDescrition();
    }

    public void setDescrition(String descrition) {
        this.getFolder().setDescrition(descrition);
    }

    public String  getSystemCode() {
        return this.subSystemId;
    }

    public void setSystemCode(String sysCode) {

        this.subSystemId = sysCode;
    }

    public void setIndex(int index) {
        this.getFolder().setIndex(index);
    }

    public int getIndex() {
        return this.getFolder().getIndex();
    }

    public void setUpdateTime(Long updateTime) {
        this.getFolder().setUpdateTime(updateTime);
    }

    public Long getUpdateTime() {
        return this.getFolder().getUpdateTime();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object != null && object instanceof Folder) {
            Folder _folder = (Folder) object;
            if (ID.equals(_folder.getID())) {
                return true;
            }
            if (this.getName() != null && this.getName().equals(_folder.getName())) {
                return this.getPath().equals(_folder.getPath());
            }
            return false;
        } else {
            return false;
        }
    }

    private boolean isModified() {
        return isModified;
    }


    @JSONField(serialize = false)
    private Set<String> getChildNameList() {
        return this.getFolder().getChildNameList();
    }

    public void addChildren(Folder cfolder) {
        try {
            this.getFolder().addChildren(VFSRoManager.getInstance().getFolderByID(this.getID()));
        } catch (VFSFolderNotFoundException e) {
            e.printStackTrace();
        }
    }


    @JSONField(serialize = false)
    public Folder createChildFolder(String name, String createPersonId) {
        EIFolder dbfolder = null;
        try {
            dbfolder = this.getFolder().createChildFolder(name, createPersonId);
        } catch (VFSFolderNotFoundException e) {
            e.printStackTrace();
        }
        VFSManager vfsManager = new DbVFSManager();
        Folder cfolder = vfsManager.getFolderByID(dbfolder.getID());
        return cfolder;
    }

    @Override
    public Folder createChildFolder(String name, String descrition, String createPersonId) {
        EIFolder dbfolder = null;
        try {
            dbfolder = this.getFolder().createChildFolder(name, descrition, createPersonId);
        } catch (VFSFolderNotFoundException e) {
            e.printStackTrace();
        }
        VFSManager vfsManager = new DbVFSManager();
        Folder cfolder = vfsManager.getFolderByID(dbfolder.getID());
        return cfolder;
    }


    @JSONField(serialize = false)
    public FileInfo createFile(String name, String createPersonId) {

        EIFileInfo dbFileInfo = this.getFolder().createFile(name, createPersonId);
        VFSManager vfsManager = new DbVFSManager();
        return vfsManager.getFileInfoByID(dbFileInfo.getID());
    }

    @Override
    public FileInfo createFile(String name, String descrition, String createPersonId) {

        EIFileInfo dbFileInfo = this.getFolder().createFile(name, createPersonId);
        VFSManager vfsManager = new DbVFSManager();
        return vfsManager.getFileInfoByID(dbFileInfo.getID());
    }


    @JSONField(serialize = false)
    public List<Folder> getAllParent() {

        List<EIFolder> dbfolderList = this.getFolder().getAllParent();
        List<Folder> folderList = new ArrayList();
        VFSManager vfsManager = new DbVFSManager();

        for (int i = 0, n = dbfolderList.size(); i < n; i++) {
            folderList.add(vfsManager.getFolderByID((String) dbfolderList.get(i).getID()));
        }

        return folderList;
    }


    private Set<String> getChildIdList() {
        return this.getFolder().getChildIdList();
    }


    @JSONField(serialize = false)
    public List<Folder> getChildrenList() {
        List<EIFolder> dbfolderList = this.getFolder().getChildrenList();
        List<Folder> folderList = new ArrayList();
        VFSManager vfsManager = new DbVFSManager();

        for (int i = 0, n = dbfolderList.size(); i < n; i++) {
            folderList.add(vfsManager.getFolderByID((String) dbfolderList.get(i).getID()));
        }
        return folderList;
    }


    @JSONField(serialize = false)
    public List<Folder> getChildrenRecursivelyList() {

        List<EIFolder> dbfolderList = this.getFolder().getChildrenRecursivelyList();
        List<Folder> folderList = new ArrayList();
        VFSManager vfsManager = new DbVFSManager();

        for (int i = 0, n = dbfolderList.size(); i < n; i++) {
            folderList.add(vfsManager.getFolderByID((String) dbfolderList.get(i).getID()));
        }

        return folderList;
    }

    public long getCreateTime() {
        return this.getFolder().getCreateTime();
    }


    public Set<String> getFileIdList() {

        return this.getFolder().getFileIdList();
    }


    @JSONField(serialize = false)
    Set<String> getFileIdListRecursively() {

        return this.getFolder().getFileIdListRecursively();
    }


    @JSONField(serialize = false)
    public List<FileInfo> getFileList() {
        Set<String> fileIdList = this.getFolder().getFileIdList();
        List<FileInfo> fileList = new ArrayList();
        VFSManager cacheManager = new DbVFSManager();
        for (String id : fileIdList) {
            FileInfo fileInfo = cacheManager.getFileInfoByID(id);
            if (fileInfo != null) {
                fileList.add(fileInfo);
            }

        }
        return fileList;
    }


    @JSONField(serialize = false)
    public List<FileInfo> getFileListRecursively() {
        Set<String> fileIdList = this.getFolder().getFileIdListRecursively();
        List<FileInfo> fileList = new ArrayList();
        VFSManager cacheManager = new DbVFSManager();
        for (String fileId : fileIdList) {
            fileList.add(cacheManager.getFileInfoByID(fileId));
        }

        return fileList;
    }


    @JSONField(serialize = false)
    public void resotre() {
        this.getFolder().setRecycle(1);
    }

    public void removeChildFile(FileInfo fileInfo) {
        this.getFolder().getFileIdList().remove(fileInfo.getID());

    }

    public void removeChildFolder(Folder cfolder) {
        this.getFolder().getChildIdList().remove(this.getID());

    }

    public void addFile(FileInfo fileInfo) {
        EIFileInfo eiFileInfo = VFSRoManager.getInstance().getFileInfoByID(fileInfo.getID());
        this.getFolder().addFileInfo(eiFileInfo);
    }


    @JSONField(serialize = false)
    public EIFolder getFolder() {
        return this.eiFolder;
    }

    @Override
    public String toString() {
        return this.getFolder().getPath();
    }


    @JSONField(serialize = false)
    public boolean isRoleIdCache_is_initialized() {
        return roleIdCache_is_initialized;
    }

    public void setRoleIdCache_is_initialized(boolean roleIdCache_is_initialized) {
        this.roleIdCache_is_initialized = roleIdCache_is_initialized;
    }


    @JSONField(serialize = false)
    public Map<RoleType, List<String>> getRoleIdCache() {
        return roleIdCache;
    }

    public void setRoleIdCache(Map<RoleType, List<String>> roleIdCache) {
        this.roleIdCache = roleIdCache;
    }


    @Override
    public Set<String> getChildrenIdList() {
        return this.getFolder().getChildIdList();
    }


}
