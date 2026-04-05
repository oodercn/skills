package net.ooder.vfs.manager.dbimpl;

import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.vfs.FileCopy;
import com.ds.vfs.VFSException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.manager.FileCopyManager;
import com.ds.vfs.manager.inner.DBFileCopy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DBFileCopyManager extends DBBaseManager implements FileCopyManager {
    static FileCopyManager manager;
    protected static Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileCopyManager.class);

    public static FileCopyManager getInstance() {
        if (manager == null) { manager = new DBFileCopyManager(); }
        return manager;
    }

    public DBFileCopyManager() { super(); }

    public FileCopy createFileCopy() { return new DBFileCopy(); }

    public void delete(String copyId) throws VFSException {
        Query q = config.getQuery("File-Copy"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where "); _sql.append(cm.get("copyId")).append("='" + copyId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void save(FileCopy copy) throws VFSException {
        insert((DBFileCopy)copy);
    }

    public Integer loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE_COPY";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery(); if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        while ((start + pageSize) < size) { loadCopy(start, start + pageSize); start = start + pageSize; }
        loadCopy(start, size);
        logger.info("initVfs  load FileCopy " + (System.currentTimeMillis() - System.currentTimeMillis()));
        return size;
    }

    private void loadCopy(Integer start, Integer end) {
        ResultSet rs = null; SqlClause sc; String strSql; PreparedStatement ps = null; Connection c = null;
        try {
            sc = config.getQuery("File-Copy").getSqlClause("basic"); strSql = buildSql(sc, start, end);
            c = getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            while (rs.next()) { DBFileCopy copy = decodeRow(rs, true); VFSRoManager.getInstance().getFileCopyCache().put(copy.getID(), copy); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
    }

    public FileCopy loadById(String copyId) throws VFSException {
        return loadData(copyId);
    }

    private FileCopy decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc = config.getQuery("File-Copy").getSqlClause("basic"); Map cm = sc.getColumnMappings(); DBFileCopy fc = new DBFileCopy();
        fc.setID(getString(rs, cm.get("copyId")));
        if (all != null && all) {
            fc.setVersionId(getString(rs, cm.get("versionId"))); fc.setName(getString(rs, cm.get("name")));
            fc.setState(getInt(rs, cm.get("state"))); fc.setCreateTime(getLong(rs, cm.get("createTime")));
            fc.setMaxRight(getInt(rs, cm.get("right"))); fc.setPersonId(getString(rs, cm.get("personId")));
            fc.setFolderId(getString(rs, cm.get("folderId"))); fc.setFileId(getString(rs, cm.get("fileId")));
        }
        return fc;
    }

    private FileCopy loadData(String ID) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("File-Copy").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, ID);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { DBFileCopy f = decodeRow(rs, true); VFSRoManager.getInstance().getFileCopyCache().put(f.getID(), f); return f; } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return null;
    }

    private void insert(DBFileCopy fc) throws VFSException {
        Query q = config.getQuery("File-Copy"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("copyId")).append(","); dc++; _sql.append(cm.get("versionId")).append(",");
        dc++; _sql.append(cm.get("name")).append(","); dc++; _sql.append(cm.get("state")).append(",");
        dc++; _sql.append(cm.get("right")).append(","); dc++; _sql.append(cm.get("personId")).append(",");
        dc++; _sql.append(cm.get("folderId")).append(","); dc++; _sql.append(cm.get("fileId")).append(",");
        dc++; _sql.append(cm.get("createTime")).append(","); dc++;
        _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, fc.getID()); ps.setString(++dc, fc.getVersionId()); ps.setString(++dc, fc.getName());
            ps.setInt(++dc, fc.getState()); ps.setInt(++dc, fc.getMaxRight()); ps.setString(++dc, fc.getPersonId());
            ps.setString(++dc, fc.getFolderId()); ps.setString(++dc, fc.getFileId()); ps.setLong(++dc, fc.getCreateTime());
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private String buildSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("copyId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("copyId")).getColumn() + "='" + ID + "'"; }
        return s;
    }
}
