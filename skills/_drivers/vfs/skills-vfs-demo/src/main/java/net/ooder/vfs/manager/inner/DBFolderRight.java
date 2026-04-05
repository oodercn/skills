package net.ooder.vfs.manager.inner;

import net.ooder.common.cache.Cacheable;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.manager.dbimpl.DBBaseManager;

import java.io.Serializable;

public class DBFolderRight extends DBBaseManager implements Cacheable, Serializable, Comparable<DBFolderRight> {

    private static final long serialVersionUID = 3645562902002279482L;
    private String folderId;
    private String roleId;
    private int type;
    private String colRight;

    private ConfigCode subSystemId;
    private OrgConfig config;

    public DBFolderRight(ConfigCode subSystemId) {
        if (subSystemId == null) {
            subSystemId = OrgConstants.VFSCONFIG_KEY;
        }
        this.subSystemId = subSystemId;
        this.config = OrgConfig.getInstance(subSystemId);
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
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

    public int compareTo(DBFolderRight arg0) {
        return 0;
    }

    public int getCachedSize() {
        return 0;
    }



}
