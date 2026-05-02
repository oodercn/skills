package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.Condition;
import net.ooder.common.ConfigCode;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FolderManager;
import net.ooder.vfs.manager.inner.*;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JdbcFolderManager implements FolderManager {

    private static final Log log = LogFactory.getLog("vfs", JdbcFolderManager.class);
    private static volatile JdbcFolderManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final ExecutorService executorService;

    private JdbcFolderManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            r -> { Thread t = new Thread(r, "FolderManager-Worker"); t.setDaemon(true); return t; });
    }

    public static JdbcFolderManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFolderManager.class) {
                if (instance == null) { instance = new JdbcFolderManager(); }
            }
        }
        return instance;
    }

    @Override
    public List<String> loadTopFolder(ConfigCode sysCode) {
        return jdbcTemplate.queryForList("SELECT FOLDER_ID FROM VFS_FOLDER WHERE PARENT_ID IS NULL", null,
            (rs, rowNum) -> rs.getString("FOLDER_ID"));
    }

    @Override
    public List<DBFolder> loadAll(Integer pageSize) {
        long startTime = System.currentTimeMillis();
        int totalCount = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FOLDER");
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        List<CompletableFuture<List<DBFolder>>> futures = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            int offset = i * pageSize; int limit = pageSize;
            futures.add(CompletableFuture.supplyAsync(() ->
                jdbcTemplate.queryForPagedList("SELECT * FROM VFS_FOLDER", null, this::mapRow, offset, limit), executorService));
        }
        try {
            List<DBFolder> results = new ArrayList<>(totalCount);
            for (CompletableFuture<List<DBFolder>> f : futures) { results.addAll(f.get(30, TimeUnit.SECONDS)); }
            for (EIFolder folder : results) { VFSRoManager.getInstance().getFolderCache().put(folder.getID(), folder); }
            log.info("Loaded " + results.size() + " folders in " + (System.currentTimeMillis() - startTime) + "ms");
            return results;
        } catch (Exception e) { futures.forEach(f -> f.cancel(true)); return new ArrayList<>(); }
    }

    @Override
    public void reLoad(DBFolder folder, Boolean hasLoadBase, Boolean hasLoadChiled, Boolean hasLoadFiles) {
        try {
            if (hasLoadBase) { loadData(folder.getID()); }
            if (hasLoadChiled) { loadChildren(folder); }
            if (hasLoadFiles) { loadFiles(folder); }
        } catch (Exception e) { log.error("reLoad failed", e); }
    }

    @Override
    public List<EIFolder> searchFolder(Condition condition, Filter filter) throws JDSException {
        StringBuilder whereClause = new StringBuilder();
        if (condition != null) {
            String cs = condition.makeConditionString();
            if (cs != null && !cs.isEmpty()) { whereClause.append(" WHERE ").append(cs); }
        }
        List<EIFolder> folders = jdbcTemplate.queryForList("SELECT * FROM VFS_FOLDER" + whereClause, null, this::mapRow);
        if (filter != null) { folders = folders.stream().filter(f -> filter.filterObject(f, null)).collect(Collectors.toList()); }
        return Collections.unmodifiableList(folders);
    }

    @Override
    public EIFolder loadById(String folderId) throws VFSFolderNotFoundException {
        EIFolder cached = VFSRoManager.getInstance().getFolderCache().get(folderId);
        if (cached != null) return cached;
        return LockManager.executeWithLock(folderId, () -> {
            EIFolder f = VFSRoManager.getInstance().getFolderCache().get(folderId);
            if (f != null) return f;
            try { return loadData(folderId); } catch (VFSFolderNotFoundException e) { throw new RuntimeException(e); }
        });
    }

    private DBFolder loadData(String folderId) throws VFSFolderNotFoundException {
        DBFolder folder = jdbcTemplate.queryForObject("SELECT * FROM VFS_FOLDER WHERE FOLDER_ID = ?",
            new Object[]{folderId}, this::mapRow);
        if (folder == null) throw new VFSFolderNotFoundException("Folder not found: " + folderId);
        VFSRoManager.getInstance().getFolderCache().put(folderId, folder);
        return folder;
    }

    @Override
    public List<EIFolder> loadChildren(DBFolder folder) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FOLDER WHERE PARENT_ID = ? ORDER BY CREATE_TIME ASC",
            new Object[]{folder.getID()}, this::mapRow);
    }

    @Override
    public List<EIFileInfo> loadFiles(DBFolder folder) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FILE WHERE FOLDER_ID = ? ORDER BY CREATE_TIME DESC",
            new Object[]{folder.getID()}, (rs, rowNum) -> mapFileInfoRow(rs));
    }

    @Override
    public List<EIFolder> loadByWhere(String where) throws JDSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FOLDER " + where, null, this::mapRow);
    }

    @Override
    public void remove(String folderId) throws VFSFolderNotFoundException {
        int affected = jdbcTemplate.update("DELETE FROM VFS_FOLDER WHERE FOLDER_ID = ?", folderId);
        if (affected == 0) throw new VFSFolderNotFoundException("Folder not found: " + folderId);
        VFSRoManager.getInstance().getFolderCache().remove(folderId);
    }

    @Override
    public void commit(EIFolder folder) throws VFSException {
        if (folder.isModified()) {
            folder.setModified(false);
            dbManager.executeInTransaction(() -> {
                remove(folder.getID());
                insert((DBFolder) folder);
                return null;
            });
        }
    }

    @Override
    public void deleteFolderIds(String... folderIds) throws VFSException {
        if (folderIds == null || folderIds.length == 0) return;
        String placeholders = Arrays.stream(folderIds).map(id -> "?").collect(Collectors.joining(","));
        jdbcTemplate.update("DELETE FROM VFS_FOLDER WHERE FOLDER_ID IN (" + placeholders + ")", (Object[]) folderIds);
        for (String id : folderIds) VFSRoManager.getInstance().getFolderCache().remove(id);
    }

    @Override
    public EIFolder createFolder(DBFolder pfolder, String name, String descrition, String personId) throws VFSFolderNotFoundException {
        DBFolder folder = new DBFolder();
        folder.setID("folder-" + System.currentTimeMillis());
        folder.setName(name);
        folder.setParentId(pfolder.getID());
        folder.setPersonId(personId);
        folder.setCreateTime(System.currentTimeMillis());
        folder.setUpdateTime(System.currentTimeMillis());
        folder.setPath(pfolder.getPath() + "/" + name);
        try { insert(folder); } catch (VFSException e) { throw new VFSFolderNotFoundException(e.getMessage()); }
        return folder;
    }

    @Override
    public EIFileInfo createFile(EIFolder pfolder, String name) {
        DBFileInfo file = new DBFileInfo();
        file.setID("file-" + System.currentTimeMillis());
        file.setName(name);
        file.setFolderId(pfolder.getID());
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        try { JdbcFileInfoManager.getInstance().save(file); } catch (Exception e) { log.error("createFile failed", e); }
        return file;
    }

    @Override
    public void delete(String folderId) {
        try { remove(folderId); } catch (VFSFolderNotFoundException e) { log.error("delete folder failed", e); }
    }

    @Override
    public List<EIFolder> getDeletedChildrenList(String folderID) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FOLDER WHERE PARENT_ID = ? AND IS_RECYCLED = 1", new Object[]{folderID}, this::mapRow);
    }

    @Override
    public List<EIFileInfo> getDeletedFileList(String folderID) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FILE WHERE FOLDER_ID = ? AND IS_RECYCLED = 1", new Object[]{folderID},
            (rs, rowNum) -> mapFileInfoRow(rs));
    }

    @Override
    public void deleteRight(String folderId, String roleId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FOLDER_BROWSER_RIGHT WHERE FOLDER_ID = ? AND ROLE_ID = ?", folderId, roleId);
    }

    @Override
    public void addRight(DBFolderRight right) throws VFSException {
        jdbcTemplate.update("INSERT INTO VFS_FOLDER_BROWSER_RIGHT (FOLDER_ID, ROLE_ID, TYPE) VALUES (?, ?, ?)",
            right.getFolderId(), right.getRoleId(), right.getType());
    }

    @Override
    public void prepareCache(List ids) {
        if (ids == null || ids.isEmpty()) return;
        for (Object id : ids) { try { loadById((String) id); } catch (Exception e) { log.error("prepareCache failed for " + id, e); } }
    }

    @Override
    public List<EIFolder> getPersonDeletedFolder(String personId) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FOLDER WHERE PERSON_ID = ? AND IS_RECYCLED = 1", new Object[]{personId}, this::mapRow);
    }

    public void insert(DBFolder folder) throws VFSException {
        jdbcTemplate.update("INSERT INTO VFS_FOLDER (FOLDER_ID, NAME, PARENT_ID, PERSON_ID, CREATE_TIME, UPDATE_TIME, PATH) VALUES (?, ?, ?, ?, ?, ?, ?)",
            folder.getID(), folder.getName(), folder.getParentId(), folder.getPersonId(),
            folder.getCreateTime(), folder.getUpdateTime(), folder.getPath());
        VFSRoManager.getInstance().getFolderCache().put(folder.getID(), folder);
    }

    public void update(DBFolder folder) throws VFSException {
        jdbcTemplate.update("UPDATE VFS_FOLDER SET NAME = ?, PARENT_ID = ?, PERSON_ID = ?, UPDATE_TIME = ?, PATH = ? WHERE FOLDER_ID = ?",
            folder.getName(), folder.getParentId(), folder.getPersonId(), folder.getUpdateTime(),
            folder.getPath(), folder.getID());
        VFSRoManager.getInstance().getFolderCache().put(folder.getID(), folder);
    }

    private DBFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFolder folder = new DBFolder();
        folder.setID(rs.getString("FOLDER_ID")); folder.setName(rs.getString("NAME"));
        folder.setParentId(rs.getString("PARENT_ID")); folder.setPersonId(rs.getString("PERSON_ID"));
        folder.setCreateTime(rs.getLong("CREATE_TIME")); folder.setUpdateTime(rs.getLong("UPDATE_TIME"));
        folder.setPath(rs.getString("PATH"));
        folder.setModified(false); folder.setInitialized(true);
        return folder;
    }

    private EIFileInfo mapFileInfoRow(ResultSet rs) throws SQLException {
        DBFileInfo file = new DBFileInfo();
        file.setID(rs.getString("FILE_ID")); file.setName(rs.getString("NAME"));
        file.setFolderId(rs.getString("FOLDER_ID")); file.setFileType(rs.getInt("FILE_TYPE"));
        file.setPersonId(rs.getString("PERSON_ID")); file.setCreateTime(rs.getLong("CREATE_TIME"));
        file.setUpdateTime(rs.getLong("UPDATE_TIME")); file.setIsRecycled(rs.getInt("IS_RECYCLED"));
        file.setIsLocked(rs.getInt("IS_LOCKED")); file.setModified(false); file.setInitialized(true);
        return file;
    }

    public void shutdown() {
        executorService.shutdown();
        try { if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) executorService.shutdownNow(); }
        catch (InterruptedException e) { executorService.shutdownNow(); Thread.currentThread().interrupt(); }
    }
}
