package net.ooder.vfs.manager.inner;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.cache.CacheSizes;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileCopy;
import net.ooder.vfs.manager.dbimpl.DBBaseManager;

public class DBFileCopy extends DBBaseManager implements FileCopy {

    private String ID;
    private String versionId;
    private String name;
    private int state;
    private long createTime;
    private int right;
    private String personId;
    private String folderId;
    private String fileId;

    private ConfigCode subSystemId = OrgConstants.VFSCONFIG_KEY;

    public DBFileCopy() {

        this.config = OrgConfig.getInstance(subSystemId);
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @JSONField(serialize = false)
    public ConfigCode getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(ConfigCode subSystemId) {
        this.subSystemId = subSystemId;
    }


    public void setMaxRight(int maxRight) {
        this.right = maxRight;
    }

    public int getMaxRight() {
        return this.right;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonId() {
        return this.personId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;

        size += CacheSizes.sizeOfString(ID);
        size += CacheSizes.sizeOfString(versionId);
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfObject(createTime);
        size += CacheSizes.sizeOfObject(state);
        size += CacheSizes.sizeOfObject(right);

        size += CacheSizes.sizeOfObject(personId);
        size += CacheSizes.sizeOfObject(folderId);
        size += CacheSizes.sizeOfObject(fileId);
        return size;

    }

}
