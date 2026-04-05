package net.ooder.vfs.manager.inner;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileLink;
import net.ooder.vfs.engine.VFSRoManager;

import java.io.Serializable;

public class DBFileLink implements FileLink, Cacheable, Serializable, Comparable<FileLink> {
    private static final long serialVersionUID = 8286699801512920515L;
    private String linkId;
    private String fileId;
    private String personId;
    private long createTime;
    private ConfigCode subSystemId = OrgConstants.VFSCONFIG_KEY;
    private String right;
    private String state;
    @JSONField(serialize = false)
    private OrgConfig config;

    public DBFileLink() {

        this.config = OrgConfig.getInstance(subSystemId);
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public int compareTo(DBFileInfo arg0) {
        return 0;
    }


    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(linkId);
        size += CacheSizes.sizeOfString(fileId);
        size += CacheSizes.sizeOfString(personId);
        size += CacheSizes.sizeOfObject(createTime);
        size += CacheSizes.sizeOfObject(right);
        return size;
    }

    public int compareTo(FileLink o) {
        return 0;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getPersonId() {
        return this.personId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setID(String linkId) {
        this.linkId = linkId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        EIFileInfo fileInfo = cacheManager.getFileInfoByID(fileId);

        return fileInfo.getName();
    }

    public String getID() {
        return this.linkId;
    }

    public ConfigCode getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(ConfigCode subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;

    }
}
