package net.ooder.vfs.manager.dbimpl;

import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.vfs.FileLink;
import com.ds.vfs.VFSException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.manager.FileLinkManager;
import com.ds.vfs.manager.inner.DBFileLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DBFileLinkManager extends DBBaseManager implements FileLinkManager {
    static FileLinkManager manager;
    protected static Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileLinkManager.class);

    public static FileLinkManager getInstance() {
        if (manager == null) { manager = new DBFileLinkManager(); }
        return manager;
    }

    public DBFileLinkManager() { super(); }

    public DBFileLink createLink() { return new DBFileLink(); }

    public void save(DBFileLink link) throws VFSException {
        insert(link);
    }

    public void delete(String linkId) throws VFSException {
        Query q = config.getQuery("File-Link"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where "); _sql.append(cm.get("linkId")).append("='" + linkId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public Integer loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE_LINK";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery(); if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        while ((start + pageSize) < size) { loadLink(start, start + pageSize); start = start + pageSize; }
        loadLink(start, size);
        logger.info("initVfs  load FileLink " + (System.currentTimeMillis() - System.currentTimeMillis()));
        return size;
    }

    private void loadLink(Integer start, Integer end) {
        ResultSet rs = null; SqlClause sc; String strSql; PreparedStatement ps = null; Connection c = null;
        try {
            sc = config.getQuery("File-Link").getSqlClause("basic"); strSql = buildSql(sc, start, end);
            c = getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            while (rs.next()) { DBFileLink link = decodeRow(rs, true); VFSRoManager.getInstance().getFileLinkCache().put(link.getID(), link); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
    }

    public DBFileLink loadById(String objId) throws VFSException {
        return loadData(objId);
    }

    private DBFileLink decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc = config.getQuery("File-Link").getSqlClause("basic"); Map cm = sc.getColumnMappings(); DBFileLink fl = new DBFileLink();
        fl.setID(getString(rs, cm.get("linkId")));
        if (all != null && all) {
            fl.setFileId(getString(rs, cm.get("fileId"))); fl.setPersonId(getString(rs, cm.get("personId")));
            fl.setCreateTime(getLong(rs, cm.get("createTime"))); fl.setRight(getString(rs, cm.get("right")));
            fl.setState(getString(rs, cm.get("state")));
        }
        return fl;
    }

    private DBFileLink loadData(String ID) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("File-Link").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, ID);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { DBFileLink f = decodeRow(rs, true); VFSRoManager.getInstance().getFileLinkCache().put(f.getID(), f); return f; } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return null;
    }

    private void insert(DBFileLink fl) throws VFSException {
        Query q = config.getQuery("File-Link"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("linkId")).append(","); dc++; _sql.append(cm.get("fileId")).append(",");
        dc++; _sql.append(cm.get("personId")).append(","); dc++; _sql.append(cm.get("right")).append(",");
        dc++; _sql.append(cm.get("state")).append(","); dc++; _sql.append(cm.get("createTime")).append(",");
        dc++; _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, fl.getID()); ps.setString(++dc, fl.getFileId()); ps.setString(++dc, fl.getPersonId());
            ps.setString(++dc, fl.getRight()); ps.setString(++dc, fl.getState()); ps.setLong(++dc, fl.getCreateTime());
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private String buildSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("linkId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("linkId")).getColumn() + "='" + ID + "'"; }
        return s;
    }
}
