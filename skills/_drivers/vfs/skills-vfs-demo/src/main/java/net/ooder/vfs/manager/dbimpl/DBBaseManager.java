package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.CommonConfig;
import net.ooder.common.database.C3P0ConnectionProvider;
import net.ooder.common.database.ConnectionManager;
import net.ooder.common.database.ConnectionManagerFactory;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.conf.Query.ColumnMapping;
import net.ooder.org.conf.Query.SqlClause;
import net.ooder.vfs.VFSConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DBBaseManager {
    protected OrgConfig config;


    public DBBaseManager() {
        super();
        this.config = OrgConfig.getInstance(OrgConstants.VFSCONFIG_KEY);
    }


    protected String getString(ResultSet rs, Object column) {
        if (column != null) {
            try {
                return rs.getString(((ColumnMapping) column).getColumnAlias());
            } catch (SQLException ex) {
                return null;
            }
        }
        return null;
    }

    
    protected long getLong(ResultSet rs, Object column) {
        if (column != null) {
            try {
                return rs.getLong(((ColumnMapping) column).getColumnAlias());
            } catch (SQLException ex) {
                return 0l;
            }
        }
        return 0l;
    }

    
    protected int getInt(ResultSet rs, Object column) {
        if (column != null) {
            try {
                return rs.getInt(((ColumnMapping) column).getColumnAlias());
            } catch (SQLException ex) {
                return 0;
            }
        }
        return 0;
    }

    public DbManager getManager() {
        return DbManager.getInstance();
    }

    public void freeConnection(Connection c) {
        getManager().releaseConnection(c);
    }

    public Connection getConnection() throws SQLException {
        return getManager().getConnection();
    }

    
    public String generationUUID() {
        String _uuid = UUID.randomUUID().toString();
        return _uuid.replaceAll("-", "");
    }


    public String buildSql(SqlClause sqlClause, Integer start, Integer end) {
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        String strOrder = sqlClause.getOrderClause();
        String tableName = sqlClause.getTableName();

        final ConnectionManager connectionManager = ConnectionManagerFactory.getInstance().getConnectionManager(VFSConstants.CONFIG_KEY);
        final C3P0ConnectionProvider connectionProvider = (C3P0ConnectionProvider) connectionManager.getConnectionProvider();

        if (connectionProvider.getProviderConfig().getDriver().toLowerCase().indexOf("mysql")  ==-1) {
            if (strWhere != null && !strWhere.equals("")) {
                if (strWhere.trim().toLowerCase().startsWith("where")){
                    strWhere=strWhere.trim().substring("where".length());
                }
                strSql =   "select * from (select rownum r,"+tableName+".* from "+tableName+" where rownum<="+end+") e where e.r>"+start+" and  "+strWhere;
            } else {
                strSql = "select * from (select rownum r,"+tableName+".* from "+tableName+" where rownum<="+end+") e where e.r>"+start+" ";
            }
        }

        if (strOrder != null && !strOrder.equals("")) {
            strSql = strSql + " " + strOrder;
        }

        if (connectionProvider.getProviderConfig().getDriver().toLowerCase().indexOf("mysql") > -1) {
            strSql = strSql + " limit  " + start + "," + end;
        }

        return strSql;
    }
}
