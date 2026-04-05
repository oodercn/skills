package net.ooder.vfs.manager.dbimpl;

import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.vfs.FileView;
import com.ds.vfs.VFSException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.manager.FileViewManager;
import com.ds.vfs.manager.inner.DBFileView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DBFileViewManager extends DBBaseManager implements FileViewManager {
    static FileViewManager manager;
    protected static Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileViewManager.class);

    public static FileViewManager getInstance() {
        if (manager == null) { manager = new DBFileViewManager(); }
        return manager;
    }

    public DBFileViewManager() { super(); }

    public Integer loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE_VIEW";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery(); if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        while ((start + pageSize) < size) { loadView(start, start + pageSize); start = start + pageSize; }
        loadView(start, size);
        logger.info("initVfs  load FileView " + (System.currentTimeMillis() - System.currentTimeMillis()));
        return size;
    }

    private void loadView(Integer start, Integer end) {
        ResultSet rs = null; SqlClause sc; String strSql; PreparedStatement ps = null; Connection c = null;
        try {
            sc = config.getQuery("FileView").getSqlClause("basic"); strSql = buildSql(sc, start, end);
            c = getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            while (rs.next()) {
                DBFileView view = decodeRow(rs, true); VFSRoManager.getInstance().getFileViewCache().put(view.getViewId(), view);
                if (view.getVersionId() != null) {
                    DBFileVersion version = (DBFileVersion) VFSRoManager.getInstance().getFileVersionByID(view.getVersionId());
                    if (version != null) { version.addFileView(view.getViewId()); VFSRoManager.getInstance().getFileVersionCache().put(version.getVersionID(), version); }
                }
            }
            rs.close();
        } catch (SQLException | VFSException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
    }

    public DBFileView loadById(String viewId) throws VFSException {
        return loadData(viewId);
    }

    public DBFileView createView(String versionId, Integer fileIndex) {
        DBFileView view = new DBFileView();
        view.setID(UUID.randomUUID().toString()); view.setVersionId(versionId); view.setFileIndex(fileIndex);
        save(view); return view;
    }

    public void commit(DBFileView view) throws VFSException {
        if (view.isModified()) { delete(view.getViewId()); insert(view); view.setModified(false); }
    }

    public FileView save(DBFileView view) throws VFSException {
        insert(view); return view;
    }

    public void delete(String viewId) throws VFSException {
        Query q = config.getQuery("FileView"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where "); _sql.append(cm.get("viewId")).append("='" + viewId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private DBFileView decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc = config.getQuery("FileView").getSqlClause("basic"); Map cm = sc.getColumnMappings(); DBFileView v = new DBFileView();
        String id = getString(rs, cm.get("viewId")); v.setID(id);
        if (all != null && all) {
            v.setViewId(getString(rs, cm.get("viewId"))); v.setFileIndex(getInt(rs, cm.get("fileIndex")));
            v.setVersionId(getString(rs, cm.get("versionId"))); v.setFileType(getInt(rs, cm.get("fileType")));
            v.setFileObjectId(getString(rs, cm.get("fileObjectId"))).setName(getString(rs, cm.get("name")));
            v.setModified(false);
        }
        return v;
    }

    private DBFileView loadData(String ID) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("FileView").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, ID);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { DBFileView v = decodeRow(rs, true); VFSRoManager.getInstance().getFileViewCache().put(v.getViewId(), v); return v; } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return null;
    }

    private void insert(FileView fv) throws VFSException {
        Query q = config.getQuery("FileView"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("viewId")).append(","); dc++; _sql.append(cm.get("fileIndex")).append(",");
        dc++; _sql.append(cm.get("versionId")).append(","); dc++; _sql.append(cm.get("fileType")).append(",");
        dc++; _sql.append(cm.get("fileObjectId")).append(","); dc++;
        _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, ((DBFileView)fv).getViewId()); ps.setInt(++dc, ((DBFileView)fv).getFileIndex());
            ps.setString(++dc, ((DBFileView)fv).getVersionId()); ps.setInt(++dc, ((DBFileView)fv).getFileType());
            ps.setString(++dc, ((DBFileView)fv).getFileObjectId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private String buildSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("viewId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("viewId")).getColumn() + "='" + ID + "'"; }
        return s;
    }
}
