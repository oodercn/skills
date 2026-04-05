package net.ooder.vfs.manager.dbimpl;

import com.ds.common.Condition;
import com.ds.common.Filter;
import com.ds.common.JDSException;
import com.ds.common.cache.Cache;
import com.ds.common.cache.CacheManagerFactory;
import com.ds.common.logging.Log;
import com.ds.common.logging.LogFactory;
import com.ds.org.conf.OrgConfig;
import com.ds.org.conf.OrgConstants;
import com.ds.org.conf.Query.ColumnMapping;
import com.ds.org.conf.Query.SqlClause;
import com.ds.vfs.VFSConstants;
import com.ds.vfs.VFSException;
import com.ds.vfs.VFSFolderNotFoundException;
import com.ds.vfs.engine.VFSRoManager;
import com.ds.vfs.manager.FolderManager;
import com.ds.vfs.manager.inner.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class DBFolderManager extends DBBaseManager implements FolderManager {
    static FolderManager manager;
    protected static Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFolderManager.class);

    public static FolderManager getInstance() {
        if (manager == null) { manager = new DBFolderManager(); }
        return manager;
    }

    public DBFolderManager() { super(); }

    public List<String> loadTopFolder(ConfigCode sysCode) {
        ResultSet rs = null; SqlClause sc = config.getQuery("Folder").getSqlClause("basic");
        Map cm = sc.getColumnMappings(); String strSql = buildTopFolderSql(sc);
        PreparedStatement ps = null; Connection c = null; List<String> list = new ArrayList<String>();
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            while (rs.next()) { list.add(getString(rs, cm.get("folderId"))); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }
        return list;
    }

    private String buildTopFolderSql(SqlClause sc) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("parentId")).getColumn() + " is null"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("parentId")).getColumn() + " is null"; }
        return s;
    }

    public List<DBFolder> loadAll(Integer pageSize) {
        ResultSet rs = null; Integer size = 0, start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FOLDER";
        PreparedStatement ps = null; Connection c = null;
        try {
            c = this.getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery(); if (rs.next()) { size = rs.getInt("ROWSIZE"); }
        } catch (SQLException e) { e.printStackTrace(); } finally { getManager().close(ps, rs); freeConnection(c); }

        List<DBFolder> dbfolders = new ArrayList<DBFolder>();
        ExecutorService service = VFSRoManager.getInstance().getInitPoolservice("VFS_FOLDER", LoadAllFolder.class);
        int page = 0;
        while (page * pageSize < size) { page++; }
        final CountDownLatch latch = new CountDownLatch(page);
        for (int k = 0; k < page; k++) {
            int end = start + pageSize; if (end >= size) { end = size; }
            service.submit(new LoadAllFolder(start, start + pageSize, dbfolders, latch)); start = end;
        }
        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        logger.info("initVfs load Folder " + (System.currentTimeMillis() - System.currentTimeMillis()));
        for (EIFolder folder : dbfolders) { VFSRoManager.getInstance().getFolderCache().put(folder.getID(), folder); }
        return dbfolders;
    }

    class LoadAllFolder implements Callable<List<DBFolder>> {
        CountDownLatch latch; List<DBFolder> folders; Integer start, end;
        LoadAllFolder(Integer start, Integer end, List<DBFolder> f, CountDownLatch l) { this.start = start; this.end = end; this.folders = f; this.latch = l; }
        @Override
        public List<DBFolder> call() throws Exception {
            List<DBFolder> cf = new ArrayList<DBFolder>(); ResultSet rs = null; SqlClause sc; String strSql; PreparedStatement ps = null; Connection c = null;
            sc = config.getQuery("Folder").getSqlClause("basic"); strSql = buildSql(sc, start, end);
            try { c = getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { DBFolder f = decodeRow(rs, true); cf.add(f); VFSRoManager.getInstance().getFolderCache().put(f.getID(), f); } rs.close(); } }
            catch (SQLException e) { log.error(e.getMessage()); } finally { getManager().close(ps, rs); freeConnection(c); }
            folders.addAll(cf); latch.countDown(); return cf;
        }
    }

    public void reLoad(DBFolder folder, Boolean hasLoadBase, Boolean hasLoadChiled, Boolean hasLoadFiles) throws VFSException {
        if (hasLoadBase) { folder = loadData(folder.getID()); }
        if (hasLoadChiled) { loadChildren(folder); }
        if (hasLoadFiles) { loadFiles(folder); }
    }

    public List<EIFolder> searchFolder(Condition condition, Filter filter) throws JDSException {
        String where = "";
        if (condition != null) { String cs = condition.makeConditionString(); if (cs != null && !cs.equals("")) { where = " where " + cs; } }
        List<EIFolder> objList = this.loadByWhere(where);
        List result = (filter == null) ? objList : new ArrayList();
        if (filter == null) { result = objList; } else { for (Iterator it = objList.iterator(); it.hasNext();) { Object inst = it.next(); if (filter.filterObject(inst, OrgConstants.VFSCONFIG_KEY.getType())) { ((ArrayList) result).add(inst); } } }
        return Collections.unmodifiableList(result);
    }

    public EIFolder loadById(String folderId) throws VFSFolderNotFoundException {
        EIFolder folder = null;
        Cache<String, EIFolder> cache = VFSRoManager.getInstance().getFolderCache();
        if (!cache.containsKey(folderId)) { folder = loadData(folderId); }
        else { folder = (EIFolder) cache.get(folderId); }
        return folder;
    }

    public List<EIFolder> loadChildren(DBFolder folder) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("Folder").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildChildrenSql(sc, folder.getID());
        List<EIFolder> childList = new ArrayList<EIFolder>();
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { childList.add(decodeRow(rs, false)); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return childList;
    }

    public List<EIFileInfo> loadFiles(DBFolder folder) throws VFSException {
        // TODO: implement
        return null;
    }

    public List<EIFolder> loadByWhere(String where) throws JDSException {
        Connection c = null; PreparedStatement ps = null; List<EIFolder> objList = new ArrayList<EIFolder>(); ResultSet rs = null; SqlClause sc; String strSql;
        sc = config.getQuery("Folder").getSqlClause("basic"); strSql = buildSql(sc); strSql = strSql + " " + where;
        try { c = this.getConnection(); ps = c.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); rs = ps.executeQuery(); while (rs.next()) { objList.add(decodeRow(rs, false)); } }
        catch (SQLException e) { throw new JDSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return objList;
    }

    public void remove(String folderId) throws VFSFolderNotFoundException {
        Query q = config.getQuery("Folder"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where "); _sql.append(cm.get("folderId")).append("='" + folderId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); } catch (SQLException e) { throw new VFSFolderNotFoundException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void commit(EIFolder folder) throws VFSException {
        if (((DBFolder)folder).isModified()) { delete(folder.getID()); insert((DBFolder)folder); ((DBFolder)folder).setModified(false); }
    }

    public void deleteFolderIds(String... folderIds) throws VFSException {
        Query q = config.getQuery("Folder"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        String _sql = sc.getDeleteClause() + " WHERE " + ((ColumnMapping)cm.get("folderId")).getColumn() + " in (" + String.join(",", Arrays.stream(folderIds).map(s -> "'" + s + "'").toArray(String[]::new)) + ")";
        Connection c = null; PreparedStatement ps = null;
        try { c = this.getConnection(); ps = c.prepareStatement(_sql); ps.executeUpdate(); for (String id : folderIds) { VFSRoManager.getInstance().getFolderCache().remove(id); } }
        catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public EIFolder createFolder(DBFolder pfolder, String name, String descrition, String personId) throws VFSFolderNotFoundException {
        DBFolder folder = new DBFolder();
        folder.setID(UUID.randomUUID().toString()); folder.setName(name);
        folder.setParentId(pfolder == null ? "" : pfolder.getID());
        folder.setPersonId(personId); folder.setDescrition(descrition);
        folder.setCreateTime(System.currentTimeMillis());
        save(folder); return folder;
    }

    public EIFileInfo createFile(EIFolder pfolder, String name) {
        EIFileInfo fileInfo = VFSRoManager.getInstance().createFile(pfolder, name, "", pfolder.getPersonId());
        pfolder.addFileInfo(fileInfo); return fileInfo;
    }

    public void delete(String folderId) { remove(folderId); }

    public List<EIFolder> getDeletedChildrenList(String folderID) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("Folder").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildDeletedChildrenSql(cm, folderID);
        List<EIFolder> childList = new ArrayList<EIFolder>();
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { childList.add(decodeRow(rs, false)); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return childList;
    }

    public List<EIFileInfo> getDeletedFileList(String folderID) throws VFSException {
        // TODO: implement
        return null;
    }

    public void deleteRight(String folderId, String roleId) throws VFSException {
        Connection c = null; PreparedStatement ps = null;
        Query q = config.getQuery("Folder-BrowserRight"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        StringBuffer _sql = new StringBuffer(sc.getDeleteClause() + " where ");
        _sql.append(cm.get("folderId")).append("='" + folderId + "'").append(" AND ").append(cm.get("roleId")).append("='" + roleId + "'");
        try { c = this.getConnection(); ps = c.prepareStatement(_sql.toString()); ps.executeUpdate(); ps.close(); }
        catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void addRight(DBFolderRight right) throws VFSException {
        Connection c = null; PreparedStatement ps = null;
        Query q = config.getQuery("Folder-BrowserRight"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("folderId")).append(","); dc++; _sql.append(cm.get("roleId")).append(",");
        dc++; _sql.append(cm.get("type")).append(","); dc++; _sql.append(cm.get("colRight")).append(",");
        dc++; _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, right.getFolderId()); ps.setString(++dc, right.getRoleId()); ps.setInt(++dc, right.getType()); ps.setString(++dc, right.getColRight());
            ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    public void prepareCache(List ids) {
        SqlClause sc = config.getQuery("Folder").getSqlClause("basic"); Map cm = sc.getColumnMappings();
        StringBuffer sw = new StringBuffer(" WHERE " + ((ColumnMapping)cm.get("folderId")).getColumn() + " IN (");
        int ac = 0;
        Cache<String, EIFolder> cache = VFSRoManager.getInstance().getFolderCache();
        for (int i = 0; i < ids.size(); i++) {
            String uuid = (String) ids.get(i);
            if (!cache.containsKey(uuid)) { if (ac > 0) sw.append(","); sw.append("'" + uuid + "'"); ac++; }
        }
        if (ac > 0) { sw.append(") "); try { List list = loadByWhere(sw.toString()); for (int i = 0; i < list.size(); i++) { EIFolder pd = (EIFolder) list.get(i); cache.put(pd.getID(), pd); } } catch (JDSException e) { log.error(e.getMessage()); } }
    }

    public List<EIFolder> getPersonDeletedFolder(String personId) throws VFSException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; Connection c = null; PreparedStatement ps = null;
        List<EIFolder> folderList = new ArrayList<EIFolder>();
        sc = config.getQuery("Folder").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSqlForPersonDeletedFolder(sc, personId);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); while (rs.next()) { folderList.add(decodeRow(rs, true)); } }
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        return folderList;
    }

    private String buildSqlForPersonDeletedFolder(SqlClause sc, String pid) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause();
        return s + " WHERE " + ((ColumnMapping)cm.get("personId")).getColumn() + "='" + pid + "' AND " + ((ColumnMapping)cm.get("recycle")).getColumn() + "=1";
    }

    private DBFolder decodeRow(ResultSet rs, Boolean all) {
        SqlClause sc = config.getQuery("Folder").getSqlClause("basic"); Map cm = sc.getColumnMappings(); DBFolder f = new DBFolder();
        f.setID(getString(rs, cm.get("folderId")));
        if (all != null && all) {
            f.setName(getString(rs, cm.get("name"))); f.setParentId(getString(rs, cm.get("parentId")));
            f.setPersonId(getString(rs, cm.get("personId"))); f.setCreateTime(getLong(rs, cm.get("createTime")));
            f.setDescrition(getString(rs, cm.get("descrition"))); f.setRecycle(getInt(rs, cm.get("recycle")));
            f.setState(FolderState.fromType(getInt(rs, cm.get("state"))));
            f.setFolderType(FolderType.fromType(getInt(rs, cm.get("folderType"))));
            f.setOrderNum(getInt(rs, cm.get("orderNum"))); f.setSize(getLong(rs, cm.get("size")));
            f.setActivityInstId(getString(rs, cm.get("activityInstId")));
            f.setUpdateTime(getLong(rs, cm.get("updateTime")));
            f.setModified(false); f.setInitialized(true);
        }
        return f;
    }

    private DBFolder loadData(String ID) throws VFSFolderNotFoundException {
        ResultSet rs = null; SqlClause sc; String strSql; Map cm; PreparedStatement ps = null; Connection c = null;
        sc = config.getQuery("Folder").getSqlClause("basic"); cm = sc.getColumnMappings(); strSql = buildSql(sc, ID);
        try {
            c = this.getConnection(); ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (ps.execute()) { rs = ps.executeQuery(); if (rs.next()) { DBFolder f = decodeRow(rs, true); VFSRoManager.getInstance().getFolderCache().put(f.getID(), f); return f; } }
        } catch (SQLException e) { throw new VFSFolderNotFoundException(e); } finally { getManager().close(ps, rs); freeConnection(c); }
        throw new VFSFolderNotFoundException("folder not found:" + ID);
    }

    private void insert(DBFolder folder) throws VFSException {
        Query q = config.getQuery("Folder"); SqlClause sc = q.getSqlClause("basic"); Map cm = sc.getColumnMappings();
        Connection c = null; PreparedStatement ps = null;
        StringBuffer _sql = new StringBuffer(sc.getInsertClause() + "(");
        int dc = 0; _sql.append(cm.get("folderId")).append(","); dc++;
        _sql.append(cm.get("name")).append(","); dc++; _sql.append(cm.get("parentId")).append(",");
        dc++; _sql.append(cm.get("personId")).append(","); dc++; _sql.append(cm.get("descrition")).append(",");
        dc++; _sql.append(cm.get("createTime")).append(","); dc++; _sql.append(cm.get("recycle")).append(",");
        dc++; _sql.append(cm.get("state")).append(","); dc++; _sql.append(cm.get("folderType")).append(",");
        dc++; _sql.append(cm.get("orderNum")).append(","); dc++; _sql.append(cm.get("size")).append(",");
        dc++; _sql.append(cm.get("activityInstId")).append(","); dc++; _sql.append(cm.get("updateTime")).append(",");
        dc++; _sql.setLength(_sql.length() - 1); _sql.append(") values (");
        for (int i = 0; i < dc; i++) _sql.append("?,");
        _sql.setLength(_sql.length() - 1); _sql.append(")");
        try {
            c = this.getConnection(); ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); dc = 0;
            ps.setString(++dc, folder.getID()); ps.setString(++dc, folder.getName());
            ps.setString(++dc, folder.getParentId()); ps.setString(++dc, folder.getPersonId());
            ps.setString(++dc, folder.getDescrition()); ps.setLong(++dc, folder.getCreateTime());
            ps.setInt(++dc, folder.getRecycle()); ps.setInt(++dc, folder.getState().ordinal());
            ps.setInt(++dc, folder.getFolderType().ordinal()); ps.setInt(++dc, folder.getOrderNum());
            ps.setLong(++dc, folder.getFolderSize()); ps.setString(++dc, folder.getActivityInstId());
            ps.setLong(++dc, folder.getUpdateTime()); ps.executeUpdate();
        } catch (SQLException e) { throw new VFSException(e); } finally { getManager().close(ps); freeConnection(c); }
    }

    private void save(DBFolder folder) throws VFSException { insert(folder); }

    private String buildSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("folderId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("folderId")).getColumn() + "='" + ID + "'"; }
        return s;
    }

    private String buildChildrenSql(SqlClause sc, String ID) {
        Map cm = sc.getColumnMappings(); String s = sc.getMainClause(); String w = sc.getWhereClause();
        if (w != null && !w.equals("")) { s += " " + w + " AND " + ((ColumnMapping)cm.get("parentId")).getColumn() + "='" + ID + "'"; }
        else { s += " WHERE " + ((ColumnMapping)cm.get("parentId")).getColumn() + "='" + ID + "'"; }
        return s;
    }

    private String buildDeletedChildrenSql(Map cm, String ID) {
        return "SELECT * FROM VFS_FOLDER WHERE " + ((ColumnMapping)cm.get("parentId")).getColumn() + "='" + ID + "' AND " + ((ColumnMapping)cm.get("recycle")).getColumn() + "=1";
    }
}
