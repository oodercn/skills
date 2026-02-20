package net.ooder.skill.vfs.database;

import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.database.ConnectionManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.conf.Query;
import net.ooder.org.conf.Query.ColumnMapping;
import net.ooder.org.conf.Query.SqlClause;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.store.manager.FileObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class DBFileObjectManager implements FileObjectManager {

    private static final Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileObjectManager.class);

    private Cache<String, FileObject> fileObjectCache;
    private Cache<String, String> fileHashCache;

    private static Map<String, ScheduledExecutorService> pathServiceMap = new HashMap<String, ScheduledExecutorService>();

    private DatabaseVfsConfig config;

    public DBFileObjectManager() {
        this.config = DatabaseVfsConfig.getInstance();
        fileObjectCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY.getType(), "fileObjectCache");
        fileHashCache = CacheManagerFactory.createCache(OrgConstants.VFSCONFIG_KEY_STORE, "fileHashCache");
    }

    @Override
    public FileObject createFileObject(String ID) {
        DBFileObject object = new DBFileObject();
        object.setID(ID);
        object.setRootPath(config.getFilePath());
        object.setAdapter("net.ooder.skill.vfs.database.adapter.DBFileAdapter");
        object.setCreateTime(System.currentTimeMillis());
        return object;
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        ResultSet rs = null;
        Integer size = 0;
        Integer start = 0;
        String strSql = "select count(*) AS ROWSIZE from VFS_FILE_OBJECT";

        PreparedStatement ps = null;
        Connection c = null;
        try {
            c = getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();

            if (rs.next()) {
                size = rs.getInt("ROWSIZE");
            }
        } catch (SQLException e) {
            logger.error("Failed to get total rows count for file objects", e);
        } finally {
            close(ps, rs);
            freeConnection(c);
        }

        ExecutorService service = getInitPoolservice("VFS_OBJ", LoadAllFileObj.class);

        int page = 0;
        while (page * pageSize < size) {
            page++;
        }

        final CountDownLatch latch = new CountDownLatch(page);
        List<LoadAllFileObj<List<DBFileObject>>> tasks = new ArrayList<LoadAllFileObj<List<DBFileObject>>>();

        for (int k = 0; k < page; k++) {
            int end = start + pageSize;
            if (end >= size) {
                end = size;
            }
            tasks.add(new LoadAllFileObj(start, end, latch));
            start = end;
        }

        try {
            service.invokeAll(tasks);
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Failed to invoke all file object loading tasks", e);
        }

        return size;
    }

    class LoadAllFileObj<T extends List<DBFileObject>> implements Callable<T> {
        private Integer start;
        private Integer end;
        private CountDownLatch latch;

        public LoadAllFileObj(Integer start, Integer end, CountDownLatch latch) {
            this.start = start;
            this.end = end;
            this.latch = latch;
        }

        @Override
        public T call() {
            T objSet = loadObj(start, end);
            logger.info("end check FileObject start" + start + " end=" + (start + objSet.size()));
            return objSet;
        }

        private T loadObj(Integer start, Integer end) {
            ResultSet rs = null;
            SqlClause sqlClause;
            String strSql;
            PreparedStatement ps = null;
            Connection c = null;
            T objs = (T) new ArrayList<DBFileObject>();
            try {
                sqlClause = config.getQuery("FileObject").getSqlClause("basic");
                strSql = buildSql(sqlClause, start, end);

                c = getConnection();
                ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int k = 0;
                if (ps.execute()) {
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        DBFileObject fileObj = decodeRow(rs, true);
                        fileObjectCache.put(fileObj.getID(), fileObj);
                        fileHashCache.put(fileObj.getHash(), fileObj.getID());
                        k++;
                        objs.add(fileObj);
                    }
                    rs.close();
                }
                logger.info("initVfs load FileOBJECT end" + start + "=========end " + end + " k=" + k);
            } catch (SQLException e) {
                logger.error("Failed to load file objects from database", e);
            } finally {
                latch.countDown();
                close(ps, rs);
                freeConnection(c);
            }
            return objs;
        }
    }

    @Override
    public void save(FileObject file) {
        DBFileObject dbFile = (DBFileObject) file;
        dbFile.setModified(true);
        try {
            FileAdapter fileAdapter = config.getFileAdapter();
            if (file.getPath() != null && fileAdapter.exists(file.getPath())) {
                String fileHash = fileAdapter.getMD5Hash(file.getPath());
                if (!fileHash.equals(file.getHash())) {
                    file.setHash(fileHash);
                }
            }
            commit(file);
        } catch (VFSException e) {
            logger.error("Failed to save file object: " + file.getID(), e);
        }
    }

    @Override
    public FileObject loadByHash(String hash) {
        if (hash != null) {
            try {
                return loadByHashFromDb(hash);
            } catch (VFSException e) {
                logger.error("Failed to load file object by hash: " + hash, e);
            }
        }
        return null;
    }

    @Override
    public FileObject loadById(String objId) {
        if (objId != null) {
            try {
                return loadData(objId);
            } catch (VFSException e) {
                logger.error("Failed to load file object by ID: " + objId, e);
            }
        }
        return null;
    }

    @Override
    public void delete(String ID) throws VFSException {
        deleteFileObject(ID);
        fileObjectCache.remove(ID);
        notifyCacheSync("delete", ID);
    }

    @Override
    public void commit(FileObject file) throws VFSException {
        DBFileObject dbFile = (DBFileObject) file;
        if (dbFile.isModified()) {
            if (dbFile.getID() != null) {
                deleteFileObject(dbFile.getID());
            } else {
                dbFile.setID(UUID.randomUUID().toString());
            }
            insert(dbFile);
            dbFile.setModified(false);
            notifyCacheSync("save", dbFile.getID());
        }
    }

    private DBFileObject loadByHashFromDb(String hash) throws VFSException {
        ResultSet rs = null;
        SqlClause sqlClause;
        String strSql;
        Map columnMap;
        Connection c = null;
        PreparedStatement ps = null;
        sqlClause = config.getQuery("FileObject").getSqlClause("basic");
        columnMap = sqlClause.getColumnMappings();
        strSql = buildHashSql(sqlClause, hash);
        DBFileObject file = null;
        try {
            c = getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            if (rs.next()) {
                file = decodeRow(rs, true);
                fileObjectCache.put(file.getID(), file);
                fileHashCache.put(hash, file.getID());
            }
        } catch (SQLException e) {
            throw new VFSException(e);
        } finally {
            close(ps, rs);
            freeConnection(c);
        }
        return file;
    }

    private DBFileObject loadData(String ID) throws VFSException {
        ResultSet rs = null;
        SqlClause sqlClause;
        String strSql;
        Map columnMap;
        Connection c = null;
        PreparedStatement ps = null;
        sqlClause = config.getQuery("FileObject").getSqlClause("basic");
        columnMap = sqlClause.getColumnMappings();
        strSql = buildSql(sqlClause, ID);
        DBFileObject file = null;
        try {
            c = getConnection();
            ps = c.prepareStatement(strSql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            if (rs.next()) {
                file = decodeRow(rs, true);
                fileObjectCache.put(file.getID(), file);
                fileHashCache.put(file.getHash(), file.getID());
            }
        } catch (SQLException e) {
            throw new VFSException(e);
        } finally {
            close(ps, rs);
            freeConnection(c);
        }
        return file;
    }

    private synchronized DBFileObject decodeRow(ResultSet rs, Boolean all) {
        SqlClause sqlClause;
        Map columnMap;
        sqlClause = config.getQuery("FileObject").getSqlClause("basic");
        columnMap = sqlClause.getColumnMappings();

        DBFileObject file = new DBFileObject();

        if (all != null && !all) {
            String id = getString(rs, columnMap.get("objId"));
            file.setID(id);
        } else {
            String name = getString(rs, columnMap.get("name"));
            String fileId = getString(rs, columnMap.get("objId"));
            String path = getString(rs, columnMap.get("path"));
            String hash = getString(rs, columnMap.get("hash"));
            String rootPath = getString(rs, columnMap.get("rootPath"));
            String adapter = getString(rs, columnMap.get("adapter"));
            file.setName(name);
            file.setID(fileId);
            file.setPath(path);
            file.setHash(hash);
            file.setRootPath(rootPath);
            file.setAdapter(adapter);
            if (columnMap.containsKey("length")) {
                String lengthStr = getString(rs, columnMap.get("length"));
                if (lengthStr == null || "".equals(lengthStr)) {
                    lengthStr = "0";
                }
                long length = Long.valueOf(lengthStr);
                file.setLength(length);
            }
            if (columnMap.containsKey("createTime")) {
                long createTime = getLong(rs, columnMap.get("createTime"));
                file.setCreateTime(createTime);
            }
        }
        return file;
    }

    public void insert(DBFileObject file) throws VFSException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            Query query = config.getQuery("FileObject");
            SqlClause sqlClause = query.getSqlClause("basic");
            Map columnMap = sqlClause.getColumnMappings();
            StringBuffer _sql = new StringBuffer(sqlClause.getInsertClause() + "(");
            int _dirtyCount = 0;
            _sql.append(columnMap.get("objId")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("name")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("hash")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("path")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("length")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("rootPath")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("adapter")).append(",");
            _dirtyCount++;
            _sql.append(columnMap.get("createTime")).append(",");
            _dirtyCount++;

            _sql.setLength(_sql.length() - 1);
            _sql.append(") values (");
            for (int i = 0; i < _dirtyCount; i++)
                _sql.append("?,");
            _sql.setLength(_sql.length() - 1);
            _sql.append(")");
            ps = c.prepareStatement(_sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            _dirtyCount = 0;
            ps.setString(++_dirtyCount, file.getID());
            ps.setString(++_dirtyCount, file.getName());
            ps.setString(++_dirtyCount, file.getHash());
            ps.setString(++_dirtyCount, file.getPath());
            ps.setString(++_dirtyCount, file.getLength().toString());
            ps.setString(++_dirtyCount, file.getRootPath());
            ps.setString(++_dirtyCount, file.getAdapter());
            ps.setLong(++_dirtyCount, file.getCreateTime());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new VFSException(e);
        } finally {
            close(ps);
            freeConnection(c);
        }
    }

    public void deleteFileObject(String ID) throws VFSException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            Query query = config.getQuery("FileObject");
            SqlClause sqlClause = query.getSqlClause("basic");
            Map columnMap = sqlClause.getColumnMappings();
            StringBuffer _sql = new StringBuffer(sqlClause.getDeleteClause() + " where ");
            _sql.append(columnMap.get("objId")).append("='" + ID + "'");
            ps = c.prepareStatement(_sql.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VFSException(e);
        } finally {
            close(ps);
            freeConnection(c);
        }
    }

    private String buildSql(SqlClause sqlClause, String ID) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("objId")).getColumn() + "='" + ID + "'";
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("objId")).getColumn() + "='" + ID + "'";
        }
        return strSql;
    }

    private String buildHashSql(SqlClause sqlClause, String hash) {
        Map columnMap = sqlClause.getColumnMappings();
        String strSql = sqlClause.getMainClause();
        String strWhere = sqlClause.getWhereClause();
        if (strWhere != null && !strWhere.equals("")) {
            strSql = strSql + " " + strWhere + " AND " + ((ColumnMapping) columnMap.get("hash")).getColumn() + "='" + hash + "'";
        } else {
            strSql = strSql + " WHERE " + ((ColumnMapping) columnMap.get("hash")).getColumn() + "='" + hash + "'";
        }
        return strSql;
    }

    private String buildSql(SqlClause sqlClause, Integer start, Integer end) {
        String strSql = sqlClause.getMainClause();
        strSql = strSql + " LIMIT " + start + "," + (end - start);
        return strSql;
    }

    private String getString(ResultSet rs, Object columnMapping) {
        try {
            String column = ((ColumnMapping) columnMapping).getColumn();
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private long getLong(ResultSet rs, Object columnMapping) {
        try {
            String column = ((ColumnMapping) columnMapping).getColumn();
            return rs.getLong(column);
        } catch (SQLException e) {
            return 0;
        }
    }

    private Connection getConnection() throws SQLException {
        return ConnectionManagerFactory.getConnection(OrgConstants.VFSCONFIG_KEY.getType());
    }

    private void freeConnection(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                logger.error("Failed to close connection", e);
            }
        }
    }

    private void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            logger.error("Failed to close statement", e);
        }
    }

    private void close(PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            logger.error("Failed to close statement", e);
        }
    }

    public ScheduledExecutorService getInitPoolservice(String path, Class clazz) {
        synchronized (path.intern()) {
            String key = clazz.getName() + "[" + path + "]";
            ScheduledExecutorService service = pathServiceMap.get(key);
            if (service == null || service.isShutdown()) {
                service = Executors.newScheduledThreadPool(10);
                pathServiceMap.put(key, service);
            }
            return service;
        }
    }

    private void notifyCacheSync(String action, String fileId) {
        try {
            DatabaseCacheSyncService syncService = DatabaseCacheSyncService.getInstance();
            if (syncService != null) {
                syncService.broadcastCacheInvalidation(action, fileId);
            }
        } catch (Exception e) {
            logger.error("Failed to notify cache sync for: " + fileId, e);
        }
    }
}
