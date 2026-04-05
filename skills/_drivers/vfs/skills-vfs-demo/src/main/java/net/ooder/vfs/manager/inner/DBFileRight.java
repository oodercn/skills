package net.ooder.vfs.manager.inner;

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.manager.dbimpl.DBBaseManager;

import java.io.Serializable;

public class DBFileRight extends DBBaseManager implements Cacheable, Serializable, Comparable<DBFileRight> {

    private static final long serialVersionUID = 3645562902002279482L;
    private String fileId;
    private String roleId;
    private int type;
    private String colRight;

    private ConfigCode subSystemId;
    private OrgConfig config;

    public DBFileRight(ConfigCode subSystemId) {
        if (subSystemId == null) {
            subSystemId = OrgConstants.VFSCONFIG_KEY;
        }
        this.subSystemId = subSystemId;
        this.config = OrgConfig.getInstance(subSystemId);
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColRight() {
        return colRight;
    }

    public int compareTo(DBFileRight arg0) {
        return 0;
    }

    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(fileId);
        size += CacheSizes.sizeOfString(roleId);
        size += CacheSizes.sizeOfString(colRight);
        size += CacheSizes.sizeOfString(subSystemId.getType());
        return size;
    }

}
