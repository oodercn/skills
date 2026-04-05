package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.*;
import net.ooder.common.cache.Cache;
import net.ooder.common.database.DBAgent;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.conf.Query.ColumnMapping;
import net.ooder.org.conf.Query.SqlClause;
import net.ooder.vfs.VFSConstants;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.FileInfoManager;
import net.ooder.vfs.manager.inner.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class DBFileInfoManager extends DBBaseManager implements FileInfoManager {
    static FileInfoManager manager;
    protected static Log log = LogFactory.getLog("vfs", DBFileInfoManager.class);
    public static final String THREAD_LOCK = "Thread Lock";

    public static FileInfoManager getInstance() {
        synchronized (THREAD_LOCK) {
            if (manager == null) { manager = new DBFileInfoManager(); }
        } return manager;
    }

    public DBFileInfoManager() { super(); }

    public void delete(String fileId) throws VFSException {
        Connection c = null; PreparedStatement ps = null;
        Query query = config.getQuery("File");
        SqlClause sqlClause = query.getSqlClause("basic");
        Map columnMap = sqlClause.getColumnMappings();
        StringBuffer _sql = new StringBuffer(sqlClause.getDeleteClause() + " where ");
        _sql.append(columnMap.get("fileId")).append("='" + fileId + "'");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void commit(EIFileInfo fileInfo, boolean update) throws VFSException {
        if (fileInfo.isModified()) {
            fileInfo.setModified(false);
            try { delete(fileInfo.getID()); insert(fileInfo); } catch (VFSException e1) { e1.printStackTrace(); }
        }
    }

    public EIFileInfo save(EIFileInfo fileInfo) {
        try { insert((DBFileInfo) fileInfo); } catch (VFSException e) { e.printStackTrace(); }
        return fileInfo;
    }

    public List<DBFileInfo> loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { getManager().close(ps, rs); freeConnection(c); }

        List<DBFileInfo> dbfiles = new ArrayList<DBFileInfo>();
        ExecutorService service = VFSRoManager.getInstance().getInitPoolservice("VFS_FILE", LoadAllFile.class);
        int page = 0;
        while (page * pageSize < size) { page++; }
        final CountDownLatch latch = new CountDownLatch(page);
        for (int k = 0; k < page; k++) {
            int end = start + pageSize; if (end >= size) { end = size; }
            service.submit(new LoadAllFile(start, start + pageSize, dbfiles, latch)); start = end;
        }
        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        log.info("initVfs load File " + (System.currentTimeMillis() - System.currentTimeMillis()));
        for (EIFileInfo file : dbfiles) { VFSRoManager.getInstance().getFileCache().put(file.getID(), file); }
        return dbfiles;
    }

    public EIFileInfo loadById(String fileId) {
        EIFileInfo fileInfo = null;
        ResultSet rs = null; SqlClause sqlClause; String strSql; Map columnMap;
        sqlClause = config.getQuery("File").getSqlClause("basic"); columnMap = sqlClause.getColumnMappings();
        strSql = sqlClause.getMainClause();
        strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("fileId")).getColumn() + "='" + fileId + "'";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { fileInfo = decodeRow(rs, true); VFSRoManager.getInstance().getFileCache().put(fileInfo.getID(), fileInfo); } }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        return fileInfo;
    }

    public List<EIFileInfo> searchFile(Condition condition, Filter filter) throws JDSException {
        String where = "";
        if (condition != null) { String cs = condition.makeConditionString(); if (cs != null && !cs.equals("")) { where = " where " + cs; } }
        List<EIFileInfo> objList = this.loadByWhere(where);
        List result = (filter == null) ? objList : new ArrayList();
        if (filter == null) { result = objList; } else { for (Iterator it = objList.iterator(); it.hasNext();) { Object inst = it.next(); if (filter.filterObject(inst, OrgConstants.VFSCONFIG_KEY.getType())) { ((ArrayList) result).add(inst); } } }
        return Collections.unmodifiableList(result);
    }

    public List<EIFileInfo> getPersonDeletedFile(String personId) throws VFSException {
        ResultSet rs = null; SqlClause sqlClause; String strSql; Map columnMap; Connection c = null; PreparedStatement ps = null;
        List<EIFileInfo> fileList = new ArrayList<EIFileInfo>();
        sqlClause = config.getQuery("File").getSqlClause("basic"); columnMap = sqlClause.getColumnMappings();
        strSql = buildSqlForPersonDeletedFile(sqlClause, personId);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { fileList.add(decodeRow(rs, true)); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return fileList;
    }

    private String buildSqlForPersonDeletedFile(SqlClause sc, String pid) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause();
        return s + " WHERE " + ((ColumnMapping) cm.get("personId")).getColumn() + "='" + pid + "' AND " + ((ColumnMapping) cm.get("isRecycled")).getColumn() + "=1";
    }

    public void prepareCache(List ids) {
        SqlClause sc = config.getQuery("File").getSqlClause("basic");
        Map cm = sc.getColumnMappings();
        StringBuffer sw = new StringBuffer(" WHERE " + ((ColumnMapping) cm.get("fileId")).getColumn() + " IN (");
        int ac = 0;
        Cache<String, EIFileInfo> cache = VFSRoManager.getInstance().getFileCache();
        for (int i = 0; i < ids.size(); i++) {
            String uuid = (String) ids.get(i);
            if (!cache.containsKey(uuid)) { if (ac > 0) sw.append(","); sw.append("'" + uuid + "'"); ac++; }
        }
        if (ac > 0) { sw.append(") "); try { List list = loadByWhere(sw.toString()); for (int i = 0; i < list.size(); i++) { EIFileInfo pd = (EIFileInfo) list.get(i); cache.put(pd.getID(), pd); } } catch (JDSException e) { log.error(e.getMessage()); } }
    }

    public void loadLinks(DBFileInfo fileInfo) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("File-Link").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, fileInfo.getID());
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { fileInfo.addFileLink(getString(rs, cm.get("linkId"))); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void deleteFileIds(String... fileIds) throws VFSException {
        Query q = config.getQuery("File"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        String _sql = sc.getDeleteClause() + " WHERE " + ((ColumnMapping) cm.get("fileId")).getColumn() + " in (" + String.join(",", Arrays.stream(fileIds).map(s -> "'" + s + "'").toArray(String[]::new)) + ")";
        Connection c = null; PreparedStatement ps = null;
        try { c = this.getConnection(); ps = c.prepareStatement(_sql); ps.executeUpdate(); ps.close(); for (String id : fileIds) { VFSRoManager.getInstance().getFileCache().remove(id); } }
        catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public List<FileVersion> loadVersion(EIFileInfo info) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("FileVersion").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, info.getID());
        List<FileVersion> versions = new ArrayList<>();
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { info.addFileVersion(getString(rs, cm.get("versionId"))); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
        return versions;
    }

    public void addRight(DBFileRight right) throws VFSException {
        Connection c = null; PreparedStatement ps = null;
        Query q = config.getQuery("File-BrowserRight"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where ");
        _sql.append(cm.get("fileId")).append("='" + right.getFileId() + "'").append(" AND ").append(cm.get("roleId")).append("='" + right.getRoleId() + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); ps.close(); }
        catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void deleteRight(String fileId, String roleId) throws VFSException {
        Connection c = null; PreparedStatement ps = null;
        Query q = config.getQuery("File-BrowserRight"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where ");
        _sql.append(cm.get("fileId")).append("='" + fileId + "'").append(" AND ").append(cm.get("roleId")).append("='" + roleId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); ps.close(); }
        catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private List<EIFileInfo> loadByWhere(String where) throws JDSException {
        Connection c = null; PreparedStatement ps = null; List<EIFileInfo> objList = new ArrayList<EIFileInfo>(); ResultSet rs = null; SqlClause sc; String strSql;
        sc = config.getQuery("File").getSqlClause("basic"); strSql = buildSql(sc); strSql = strSql + " " + where;
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery(); while (rs.next()) { objList.add(decodeRow(rs, false)); }
        } catch (SQLException e) { throw new JDSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return objList;
    }

    private DBFileInfo decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc; Map cm; sc = config.getQuery("File").getSqlClause("basic"); cm = sc.getColumnMappings();
        DBFileInfo fi = new DBFileInfo(); fi.setID(getString(rs, cm.get("fileId")));
        if (all != null && all) {
            fi.setName(getString(rs, cm.get("name")));
            fi.setFolderId(getString(rs, cm.get("folderId")));
            fi.setFileType(getInt(rs, cm.get("fileType")));
            fi.setPersonId(getString(rs, cm.get("personId")));
            fi.setCreateTime(getLong(rs, cm.get("createTime")));
            fi.setDescrition(getString(rs, cm.get("descrition")));
            fi.setUpdateTime(getLong(rs, cm.get("updateTime")));
            fi.setIsRecycled(getInt(rs, cm.get("isRecycled")));
            fi.setIsLocked(getInt(rs, cm.get("isLocked")));
            if (cm.containsKey("right")) { fi.setRight(getString(rs, cm.get("right"))); }
            if (cm.containsKey("activityInstId")) { fi.setActivityInstId(getString(rs, cm.get("activityInstId"))); }
            fi.setModified(false); fi.setInitialized(true);
        }
        return fi;
    }

    private void insert(EIFileInfo fileInfo) throws VFSException {
        Query q = config.getQuery("File"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings(); Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "("); int dc = 0;
        _sql.append(cm.get("fileId")).append(","); dc++;
        _sql.append(cm.get("name")).append(","); dc++;
        _sql.append(cm.get("folderId")).append(","); dc++;
        _sql.append(cm.get("fileType")).append(","); dc++;
        _sql.append(cm.get("personId")).append(","); dc++;
        _sql.append(cm.get("descrition")).append(","); dc++;
        _sql.append(cm.get("createTime")).append(","); dc++;
        _sql.append(cm.get("updateTime")).append(","); dc++;
        _sql.append(cm.get("isRecycled")).append(","); dc++;
        _sql.append(cm.get("isLocked")).append(","); dc++;
        if (cm.containsKey("activityInstId")) { _sql.append(cm.get("activityInstId")).append(","); dc++; }
        if (cm.containsKey("right")) { _sql.append(cm.get("right")).append(","); dc++; }
        _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, fileInfo.getID()); ps.setString(++dc, fileInfo.getName()); ps.setString(++dc, fileInfo.getFolderId());
            ps.setInt(++dc, fileInfo.getFileType()); ps.setString(++dc, fileInfo.getPersonId());
            ps.setString(++dc, fileInfo.getDescrition()); ps.setLong(++dc, fileInfo.getCreateTime());
            ps.setLong(++dc, fileInfo.getUpdateTime()); ps.setInt(++dc, fileInfo.getIsRecycled()); ps.setInt(++dc, fileInfo.getIsLocked());
            if (cm.containsKey("activityInstId")) { ps.setString(++dc, fileInfo.getActivityInstId()); }
            if (cm.containsKey("right")) { ps.setString(++dc, fileInfo.getRight()); }
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    class LoadAllFile implements Callable<List<DBFileInfo>> {
        CountDownLatch latch; List<DBFileInfo> files; Integer start, end;
        LoadAllFile(Integer start, Integer end, List<DBFileInfo> f, CountDownLatch l) { this.start = start; this.end = end; this.files = f; this.latch = l; }
        @Override
        public List<DBFileInfo> call() throws Exception {
            List<DBFileInfo> cf = new ArrayList<DBFileInfo>(); ResultSet rs = null; SqlClause sc; String strSql; PreparedStatement ps = null; Connection c = null;
            sc = config.getQuery("File").getSqlClause("basic"); strSql = buildSql(sc, start, end);
            try { c = getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { DBFileInfo fi = decodeRow(rs, true); cf.add(fi); VFSRoManager.getInstance().getFileCache().put(fi.getID(), fi); } rs.close(); } }
            catch (SQLException e) { log.error(e.getMessage()); } finally { getManager().close(ps, rs); freeConnection(c); }
            files.addAll(cf); latch.countDown(); return cf;
        }
    }
}
