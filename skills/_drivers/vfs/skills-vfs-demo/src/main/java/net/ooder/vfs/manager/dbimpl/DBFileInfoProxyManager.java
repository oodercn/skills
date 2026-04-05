package net.ooder.vfs.manager.dbimpl;

import com.ds.annotation.RoleType;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.common.ConfigCode;
import com.ds.vfs.FileInfo;
import com.ds.vfs.VFSException;
import com.ds.vfs.manager.FileInfoProxyManager;
import com.ds.vfs.manager.inner.EIFileInfo;
import com.ds.vfs.proxy.FileInfoProxy;
import com.ds.vfs.proxy.VFSListProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBFileInfoProxyManager extends DBBaseManager implements FileInfoProxyManager {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileInfoProxyManager.class);

    static Map<ConfigCode, FileInfoProxyManager> managerMap = new HashMap<ConfigCode, FileInfoProxyManager>();

    private OrgConfig config;

    private ConfigCode subSystemId;

    public static FileInfoProxyManager getInstance(ConfigCode sysCode) {
        FileInfoProxyManager manager = managerMap.get(sysCode);
        if (manager == null) {
            manager = new DBFileInfoProxyManager(sysCode);
            managerMap.put(sysCode, manager);
        }
        return manager;
    }

    public DBFileInfoProxyManager(ConfigCode subSystemId) {

        this.config = OrgConfig.getInstance(OrgConstants.VFSCONFIG_KEY);
        this.subSystemId = subSystemId;
    }

    public List<FileInfo> getFileInfoProxyList(List<EIFileInfo> fileInfoList) {
        return new VFSListProxy(fileInfoList);

    }

    public FileInfo loadFileInfo(EIFileInfo fileInfo) {
        FileInfoProxy fileInfoProxy = new FileInfoProxy(fileInfo, subSystemId);
        return loadFromDb(fileInfoProxy);
    }

    FileInfoProxy loadFromDb(FileInfoProxy proxy) {
        try {

            if (!proxy.isInitialized()) {
                return proxy;
            }

            if (config.isSupportPersonRole()) {
                loadRoles(proxy);
            } else {
                proxy.setRoleIdCache_is_initialized(true);
            }

            proxy.setRoleIdCache_is_initialized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }

    public synchronized void loadRoles(FileInfoProxy proxy) throws VFSException {
        ResultSet rs = null;
        Query query = null;
        SqlClause sqlClause = null;
        Map columnMap = null;
        String strSql = null;

        query = config.getQuery("File-BrowserRight");
        if (query != null) {
            sqlClause = query.getSqlClause("basic");
            PreparedStatement ps = null;
            Connection c = null;
            columnMap = sqlClause.getColumnMappings();
            strSql = buildSql(sqlClause, proxy.getID());
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

    private String buildSql(SqlClause sqlClause, String ID) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("fileId")).getColumn() + "='" + ID + "'";
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("fileId")).getColumn() + "='" + ID + "'";
        }
        return strSql;
    }

}
