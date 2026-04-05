package net.ooder.vfs.manager.dbimpl;

import com.ds.annotation.EsbBeanAnnotation;
import com.ds.common.ContextType;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.vfs.FileVersion;
import com.ds.vfs.VFSException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.manager.FileVersionManager;
import com.ds.vfs.manager.inner.DBFileVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@EsbBeanAnnotation(dataType = ContextType.Function)
public class DBFileVersionManager extends DBBaseManager implements FileVersionManager {
    static FileVersionManager manager;
    protected static Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileVersionManager.class);

    public static FileVersionManager getInstance() {
        if (manager == null) { manager = new DBFileVersionManager(); }
        return manager;
    }

    public DBFileVersionManager() { super(); }

    public Integer loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE_VERSION";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }

        ExecutorService service = VFSRoManager.getInstance().getInitPoolservice("VFS_FILE_VERSION", LoadAllVersion.class);
        while ((start + pageSize) < size) { service.submit(new LoadAllVersion(start, start + pageSize)); start = start + pageSize; }
        service.submit(new LoadAllVersion(start, size));
        logger.info("initVfs  load FileVersion " + (System.currentTimeMillis() - System.currentTimeMillis()));
        return size;
    }

    class LoadAllVersion implements Runnable {
        private Integer start; private Integer end;
        public LoadAllVersion(Integer start, Integer end) { this.start = start; this.end = end; }
        @Override
        public void run() { loadVersion(start, end); }
        private void loadVersion(Integer start, Integer end) {
            ResultSet rs = null; SqlClause sqlClause; String strSql; PreparedStatement ps = null; Connection c = null;
            try {
                sqlClause = config.getQuery("FileVersion").getSqlClause("basic"); strSql = buildSql(sqlClause, start, end);
                c = getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = ps.executeQuery();
                while (rs.next()) {
                    DBFileVersion version = decodeRow(rs, true);
                    VFSRoManager.getInstance().getFileVersionCache().put(version.getVersionID(), version);
                    if (version.getFileId() != null) {
                        EIFileInfo fileInfo = VFSRoManager.getInstance().getFileInfoByID(version.getFileId());
                        if (fileInfo != null) { fileInfo.addFileVersion(version.getVersionID()); VFSRoManager.getInstance().getFileCache().put(fileInfo.getID(), fileInfo); }
                    }
                }
                rs.close();
            } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        }
    }

    public DBFileVersion loadById(String versionId) throws VFSException {
        return loadData(versionId);
    }

    public DBFileVersion createFileVersion(EIFileInfo info) throws VFSException {
        DBFileVersion version = new DBFileVersion();
        version.setFileId(info.getID());
        version.setPersonId(info.getPersonId());
        version.setID(UUID.randomUUID().toString()); version.setName(info.getName());
        version.setCreateTime(System.currentTimeMillis());
        save(version); return version;
    }

    public void commit(DBFileVersion fileVersion) throws VFSException {
        if (fileVersion.isModified()) { delete(fileVersion.getVersionID()); insert(fileVersion); fileVersion.setModified(false); }
    }

    public FileVersion save(DBFileVersion fileVersion) throws VFSException {
        insert(fileVersion); return fileVersion;
    }

    public List<FileVersion> getVersionByObjectId(String fileObjectId) {
        // TODO: implement
        return null;
    }

    public FileVersion loadByIndex(String fileId, Integer index) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("FileVersion").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, fileId, index);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { return decodeRow(rs, true); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return null;
    }

    public boolean delete(String ID) throws VFSException {
        Query q = config.getQuery("FileVersion"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where "); _sql.append(cm.get("versionId")).append("='" + ID + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
        return true;
    }

    private DBFileVersion decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc = config.getQuery("FileVersion").getSqlClause("basic"); Map cm = sc.getColumnMappings(); DBFileVersion v = new DBFileVersion();
        String id = getString(rs, cm.get("versionId")); v.setID(id);
        if (all != null && all) {
            String vid = getString(rs, cm.get("versionId"));
            String fid = getString(rs, cm.get("fileId"));
            int idx = getInt(rs, cm.get("index"));
            String pid = getString(rs, cm.get("personId"));
            long ct = getLong(rs, cm.get("createTime"));
            String foid = getString(rs, cm.get("fileObjectId"));
            v.setVersionID(vid); v.setFileId(fid); v.setIndex(idx); v.setPersonId(pid); v.setCreateTime(ct); v.setFileObjectId(foid);
            v.setModified(false);
        }
        return v;
    }

    private DBFileVersion loadData(String ID) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("FileVersion").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, ID);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { DBFileVersion v = decodeRow(rs, true); VFSRoManager.getInstance().getFileVersionCache().put(v.getVersionID(), v); return v; } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return null;
    }

    private void insert(FileVersion fv) throws VFSException {
        Query q = config.getQuery("FileVersion"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("versionId")).append(","); dc++;
        _sql.append(cm.get("fileId")).append(","); dc++; _sql.append(cm.get("index")).append(","); dc++;
        _sql.append(cm.get("personId")).append(","); dc++; _sql.append(cm.get("createTime")).append(","); dc++;
        _sql.append(cm.get("fileObjectId")).append(","); dc++;
        _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,"); _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, fv.getVersionID()); ps.setString(++dc, ((DBFileVersion)fv).getFileId());
            ps.setInt(++dc, ((DBFileVersion)fv).getIndex()); ps.setString(++dc, ((DBFileVersion)fv).getPersonId());
            ps.setLong(++dc, ((DBFileVersion)fv).getCreateTime()); ps.setString(++dc, ((DBFileVersion)fv).getFileObjectId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private String buildSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("versionId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("versionId")).getColumn() + "='" + ID + "'"; }
        return s;
    }

    private String buildSql(SqlClause sc, String fId, Integer index) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("fileId")).getColumn() + "='" + fId + "' AND " + ((ColumnMapping)cm.get("index")).getColumn() + "=" + index; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("fileId")).getColumn() + "='" + fId + "' AND " + ((ColumnMapping)cm.get("index")).getColumn() + "=" + index; }
        return s;
    }
}
