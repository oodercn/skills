package net.ooder.vfs.manager.dbimpl;

import com.ds.annotation.RoleType;
import com.ds.common.CommonConfig;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.common.ConfigCode;
import com.ds.vfs.Folder;
import com.ds.vfs.VFSException;
import com.ds.vfs.VFSFolderNotFoundException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.ext.FileBrowserRightType;
import com.ds.vfs.manager.FolderProxyManager;
import com.ds.vfs.manager.inner.EIFolder;
import com.ds.vfs.proxy.FolderProxy;
import com.ds.vfs.proxy.VFSListProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBFolderProxyManager extends DBBaseManager implements FolderProxyManager {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFolderProxyManager.class);

    static Map<ConfigCode, DBFolderProxyManager> managerMap = new HashMap<ConfigCode, DBFolderProxyManager>();

    private OrgConfig config;

    private boolean cacheEnabled;

    private ConfigCode subSystemId;


    public static DBFolderProxyManager getInstance(ConfigCode sysCode) {
        DBFolderProxyManager manager = managerMap.get(sysCode);
        if (manager == null) {
            manager = new DBFolderProxyManager(sysCode);
            managerMap.put(sysCode, manager);
        }
        return manager;
    }
    public  DBFolderProxyManager() {
        this(ConfigCode.vfs);
    }
    public  DBFolderProxyManager(ConfigCode subSystemId) {
        String enabled = CommonConfig.getValue(OrgConstants.VFSCONFIG_KEY + ".cache.enabled");
        this.cacheEnabled = Boolean.valueOf(enabled).booleanValue();
        this.config = OrgConfig.getInstance(OrgConstants.VFSCONFIG_KEY);
        this.subSystemId = subSystemId;
    }

    public FolderProxy loadFolder(EIFolder folder) {
        FolderProxy proxy = new FolderProxy(folder, subSystemId);
        return proxy;
    }

    public List<Folder> getFolderProxyList(List<EIFolder> folderList) {
        return new VFSListProxy(folderList);

    }

    private FolderProxy loadFromDb(FolderProxy proxy) throws VFSException {
        this.loadRoles(proxy);
        return proxy;

    }

    public synchronized void loadRoles(FolderProxy proxy) throws VFSException {
        ResultSet rs = null;
        Query query = null;
        SqlClause sqlClause = null;
        Map columnMap = null;
        String strSql = null;

        query = config.getQuery("Folder-BrowserRight");
        if (query != null) {
            sqlClause = query.getSqlClause("basic");
            if (sqlClause != null) {
                sqlClause = query.getSqlClause("basic");
                PreparedStatement ps = null;
                Connection c = null;
                columnMap = sqlClause.getColumnMappings();
                strSql = buildFileBrowserRightSql(sqlClause, FileBrowserRightType.Org, proxy.getID());
                try {
                    c = this.getConnection();
                    ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                    if (ps.execute()) {
                        proxy.setRoleIdCache_is_initialized(true);
                        proxy.getRoleIdCache().clear();
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            proxy.addRole(RoleType.fromType(this.getString(rs, columnMap.get("type"))), getString(rs, columnMap.get("roleId")));

                        }
                    }
                } catch (SQLException e) {
                    throw new VFSException(e);
                } finally {
                    getManager().close(ps, rs);
                    freeConnection(c);
                }
            }
        }
    }

    private String buildFileBrowserRightSql(SqlClause sqlClause, FileBrowserRightType type, String ID) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("type")).getColumn() + "=" + type.ordinal() + " AND " + ((ColumnMapping) columnMap.get("folderId")).getColumn() + "='" + ID + "'";
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("type")).getColumn() + "=" + type.ordinal() + " AND " + ((ColumnMapping) columnMap.get("folderId")).getColumn() + "='" + ID + "'";
        }
        return strSql;
    }

    //
    public void delete(FolderProxy folder) {
        VFSRoManager roManager = VFSRoManager.getInstance();
        try {
            roManager.removeFolder(folder.getID());
        } catch (VFSFolderNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void commit(FolderProxy folder) throws VFSException {
        EIFolder eifolder = null;
        try {
            eifolder = VFSRoManager.getInstance().getFolderByID(folder.getID());
        } catch (VFSFolderNotFoundException e) {
            e.printStackTrace();
        }
        VFSRoManager.getInstance().commitFolder(eifolder);
    }

    private String buildGetRightChildrenSql(SqlClause sqlClause, String ID) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("parentId")).getColumn() + "='" + ID + "'" + " AND" + ((ColumnMapping) columnMap.get("foldeId"));
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("parentId")).getColumn() + "='" + ID + "'";
        }
        return strSql;
    }

    private String buildSql(SqlClause sqlClause, String ID) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("folderId")).getColumn() + "='" + ID + "'";
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("folderId")).getColumn() + "='" + ID + "'";
        }
        return strSql;
    }

}
