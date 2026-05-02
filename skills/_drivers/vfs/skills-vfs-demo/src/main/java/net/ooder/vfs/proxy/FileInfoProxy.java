package net.ooder.vfs.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.RoleType;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.org.OrgManager;
import net.ooder.org.Role;
import net.ooder.org.RoleNotFoundException;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.OrgManagerFactory;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.*;
import net.ooder.vfs.engine.DbVFSManager;
import net.ooder.vfs.manager.FileInfoProxyManager;
import net.ooder.vfs.manager.dbimpl.JdbcFileInfoProxyManager;
import net.ooder.vfs.manager.inner.EIFileInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

public class FileInfoProxy implements FileInfo, Serializable, Comparable<FileInfoProxy> {

    private static final Long serialVersionUID = 7870681403705988152L;
    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), FileInfoProxy.class);


    private String ID;

    private ConfigCode configCode = OrgConstants.VFSCONFIG_KEY;

    @JSONField(serialize = false)

    private OrgConfig config;
    @JSONField(serialize = false)
    public boolean initialized = false;
    private String oldFolderId;

    @JSONField(serialize = false)
    private Map<RoleType, List<String>> roleIdCache = new HashMap<RoleType, List<String>>();

    @JSONField(serialize = false)
    boolean roleIdCache_is_initialized;

    @JSONField(serialize = false)
    boolean extProperty_is_initialized;

    @JSONField(serialize = false)
    private boolean reLoadPath = false;
    public Long updateTime;
    boolean isModified = false;

    @JSONField(serialize = false)
    private EIFileInfo fileInfo;
    @JSONField(serialize = false)
    private FileInfoProxyManager manager;

    public FileInfoProxy(EIFileInfo fileInfo, ConfigCode configCode) {
        if (configCode == null) {
            configCode = OrgConstants.VFSCONFIG_KEY;
        }
        this.configCode = configCode;
        this.ID = fileInfo.getID();
        this.fileInfo = fileInfo;
        this.config = OrgConfig.getInstance(configCode);
        this.manager = JdbcFileInfoProxyManager.getInstance(configCode);

    }

    public String getName() {
        return fileInfo.getName();
    }

    public void setName(String name) {
        fileInfo.setName(name);
        if (this.initialized) {
            reLoadPath = true;
            getPath();
        }
    }

    public int compareTo(FileInfoProxy o) {
        return 0;
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

    public void setFileId(String fileId) {
        this.ID = fileId;
    }

    public void addFileVersion(String versionFileId) {
        fileInfo.addFileVersion(versionFileId);
    }

    public void addFileLink(String linkId) {
        fileInfo.addFileLink(linkId);
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

    private void prepareRoles() {
        if (!roleIdCache_is_initialized && config.isSupportPersonRole()) {

            try {
                manager.loadRoles(this);
            } catch (Exception e) {
                log.error("error occured when get role for person '" + ID + "'!", e);
            }
        }
    }

    public String getPath() {
        return fileInfo.getPath();
    }

    public void setPath(String path) {
        fileInfo.setPath(path);
    }


    @JSONField(serialize = false)
    public List<Role> getRoleList(RoleType type) {
        prepareRoles();

        List<Role> personRoleList = new ArrayList();
        OrgManager cacheManager = OrgManagerFactory.getOrgManager(configCode);
        List<String> roleIdList = this.roleIdCache.get(type);

        for (int i = 0, n = roleIdList.size(); i < n; i++) {
            try {
                personRoleList.add(cacheManager.getPersonRoleByID((String) roleIdList.get(i)));
            } catch (RoleNotFoundException ex) {
            }
        }
        return personRoleList;
    }

    public String getPersonId() {
        return fileInfo.getPersonId();
    }

    public void setPersonId(String personId) {
        fileInfo.setPersonId(personId);
    }

    public Long getCreateTime() {
        return fileInfo.getCreateTime();
    }

    public void setCreateTime(Long createTime) {
        fileInfo.setCreateTime(createTime);
    }

    public void setID(String iD) {
        ID = iD;
        fileInfo.setID(iD);
    }

    public void setFolderId(String folderId) {
        fileInfo.setFolderId(folderId);
    }

    public void setSysid(String sysid) {

    }

    public void setActivtyInstId(String activtyInstId) {
        fileInfo.setActivityInstId(activtyInstId);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileInfoProxy other = (FileInfoProxy) obj;
        if (ID == null) {
            if (other.ID != null)
                return false;
        } else if (!ID.equals(other.ID))
            return false;
        return true;
    }

    @JSONField(serialize = false)
    public FileVersion getCurrentVersion() {

        return fileInfo.getCurrentVersion();
    }

    public String getCurrentVersonFileHash() {

        return fileInfo.getCurrentVersonFileHash();
    }

    @JSONField(serialize = false)
    public Long getCurrentVersonFileLength() {

        return fileInfo.getCurrentVersonFileLength();
    }

    @JSONField(serialize = false)
    public Long getCurrentVersonFileCreateTime() {

        return fileInfo.getCurrentVersonFileCreateTime();
    }


    @JSONField(serialize = false)
    public Folder getFolder() {
        VFSManager vfsManager = new DbVFSManager();
        Folder folder = (Folder) vfsManager.getFolderByID(fileInfo.getFolderId());
        return folder;
    }

    public String getFolderId() {
        return fileInfo.getFolderId();
    }

    @JSONField(serialize = false)
    public String getSysid() {
        return fileInfo.getSysid();
    }

    @JSONField(serialize = false)
    public String getActivityInstId() {
        return fileInfo.getActivityInstId();
    }

    public Set<String> getVersionIds() {
        Set<String> versionIds = new LinkedHashSet<String>();
        for (FileVersion version : fileInfo.getVersionList()) {
            versionIds.add(version.getVersionID());
        }
        return versionIds;
    }


    @JSONField(serialize = false)
    public List<FileView> getCurrentViews() {

        return fileInfo.getCurrentViews();
    }


    @JSONField(serialize = false)
    public List<FileLink> getLinks() {

        return fileInfo.getLinks();
    }

    public Integer getFileType() {
        return fileInfo.getFileType();
    }

    public void setFileType(int fileType) {
        fileInfo.setFileType(fileType);
    }

    public String getCurrentVersonId() {

        return fileInfo.getCurrentVersonId();
    }


    @JSONField(serialize = false)
    public int getIsRecycled() {
        return fileInfo.getIsRecycled();
    }

    public void resotre() {
        fileInfo.setIsRecycled(1);
    }

    public String getDescrition() {
        return fileInfo.getDescrition();
    }

    public void setDescrition(String descrition) {
        fileInfo.setDescrition(descrition);
    }


    @JSONField(serialize = false)
    public int getIsLocked() {
        return fileInfo.getIsLocked();
    }

    public void setIsLocked(int isLocked) {
        fileInfo.setIsLocked(isLocked);
    }

    public void setOldFolderId(String oldFolderId) {
        this.oldFolderId = oldFolderId;
    }


    public String getOldFolderId() {
        return oldFolderId;
    }

    public void setRight(String right) {
        fileInfo.setRight(right);
    }


    @JSONField(serialize = false)
    public String getRight() {
        return fileInfo.getRight();
    }


    @JSONField(serialize = false)
    public FileVersion createFileVersion() {
        return fileInfo.createFileVersion();
    }


    @JSONField(serialize = false)
    public MD5InputStream getCurrentVersonInputStream() {

        return fileInfo.getCurrentVersonInputStream();
    }


    @JSONField(serialize = false)
    public MD5OutputStream getCurrentVersonOutputStream() {

        return fileInfo.getCurrentVersonOutputStream();
    }


    @JSONField(serialize = false)
    public String getHistroyId() {
        return fileInfo.getHistroyId();
    }

    public void setHistroytId(String histroyId) {
        fileInfo.setHistroytId(histroyId);
    }

    public void setUpdateTime(Long updateTime) {
        fileInfo.setUpdateTime(updateTime);
    }

    public Long getUpdateTime() {
        return fileInfo.getUpdateTime();
    }


    @JSONField(serialize = false)
    public List<String> getRoleIdList(RoleType type) {
        prepareRoles();
        List<String> roleIdList = this.roleIdCache.get(type);
        return roleIdList;
    }

    @Override

    public Set<String> getCurrentViewIds() {
        Set<String> viewIds = new LinkedHashSet<String>();
        List<FileView> views = fileInfo.getCurrentViews();
        for (FileView view : views) {
            viewIds.add(view.getID());
        }
        return viewIds;
    }

    @Override
    public Set<String> getLinkIds() {

        Set<String> fileLinkIds = new LinkedHashSet<String>();
        List<FileLink> links = fileInfo.getLinks();
        for (FileLink link : links) {
            fileLinkIds.add(link.getID());
        }
        return fileLinkIds;
    }


    public boolean isModified() {
        return fileInfo.isModified();
    }

    public void setModified(boolean isModified) {
        this.setModified(isModified);
    }


    @JSONField(serialize = false)
    public List<FileVersion> getVersionList() {

        return fileInfo.getVersionList();
    }


    public ConfigCode getSystemCode() {
        return configCode;
    }


    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }


    @JSONField(serialize = false)
    public String getSmbTempPath() {
        String filePath = null;
        if (this.getCurrentVersion() != null && this.getCurrentVersion().getFileObject() != null) {
            filePath = this.getCurrentVersion().getFileObject().getPath();
        }

        return filePath;
    }


    @JSONField(serialize = false)
    public void writeTo(OutputStream outstream) throws IOException {
        fileInfo.writeTo(outstream);

    }

    @Override
    public String toString() {

        return fileInfo.getPath();
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

}
