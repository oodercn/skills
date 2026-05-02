package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.Condition;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FileInfoManager;
import net.ooder.vfs.manager.inner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JdbcFileInfoManager implements FileInfoManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileInfoManager.class);
    private static volatile JdbcFileInfoManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final VfsCacheProvider cacheProvider;
    private final ExecutorService executorService;

    private JdbcFileInfoManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.cacheProvider = dbManager.getCacheProvider();
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> { Thread t = new Thread(r, "FileInfoManager-Worker"); t.setDaemon(true); return t; });
    }

    public static JdbcFileInfoManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFileInfoManager.class) {
                if (instance == null) { instance = new JdbcFileInfoManager(); }
            }
        }
        return instance;
    }

    @Override
    public void delete(String fileId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE WHERE FILE_ID = ?", fileId);
        cacheProvider.getFileCache().remove(fileId);
    }

    @Override
    public void commit(EIFileInfo fileInfo, boolean update) throws VFSException {
        if (fileInfo.isModified()) {
            fileInfo.setModified(false);
            dbManager.executeInTransaction(() -> {
                delete(fileInfo.getID());
                insert((DBFileInfo) fileInfo);
                return null;
            });
        }
    }

    @Override
    public EIFileInfo save(EIFileInfo fileInfo) {
        try { insert((DBFileInfo) fileInfo); }
        catch (VFSException e) { log.error("Failed to save file info: {}", fileInfo.getID(), e); }
        return fileInfo;
    }

    @Override
    public List<DBFileInfo> loadAll(Integer pageSize) {
        long startTime = System.currentTimeMillis();
        int totalCount = getTotalFileCount();
        List<DBFileInfo> allFiles = loadAllFilesParallel(totalCount, pageSize);
        for (EIFileInfo file : allFiles) { cacheProvider.getFileCache().put(file.getID(), file); }
        log.info("Loaded {} files in {}ms", allFiles.size(), System.currentTimeMillis() - startTime);
        return allFiles;
    }

    private int getTotalFileCount() { return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE"); }

    private List<DBFileInfo> loadAllFilesParallel(int totalCount, int batchSize) {
        int pageCount = (totalCount + batchSize - 1) / batchSize;
        List<CompletableFuture<List<DBFileInfo>>> futures = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            int offset = i * batchSize; int limit = batchSize;
            futures.add(CompletableFuture.supplyAsync(() -> loadFilesBatch(offset, limit), executorService));
        }
        try {
            List<DBFileInfo> results = new ArrayList<>(totalCount);
            for (CompletableFuture<List<DBFileInfo>> future : futures) { results.addAll(future.get(30, TimeUnit.SECONDS)); }
            return results;
        } catch (Exception e) { futures.forEach(f -> f.cancel(true)); log.error("Failed to load all files in parallel", e); return new ArrayList<>(); }
    }

    private List<DBFileInfo> loadFilesBatch(int offset, int limit) {
        return jdbcTemplate.queryForPagedList("SELECT * FROM VFS_FILE", null, this::mapRow, offset, limit);
    }

    @Override
    public EIFileInfo loadById(String fileId) {
        Map<String, EIFileInfo> cache = cacheProvider.getFileCache();
        EIFileInfo cached = cache.get(fileId);
        if (cached != null) return cached;
        return LockManager.executeWithLock(fileId, () -> {
            EIFileInfo file = cache.get(fileId);
            if (file != null) return file;
            DBFileInfo fileInfo = jdbcTemplate.queryForObject("SELECT * FROM VFS_FILE WHERE FILE_ID = ?", new Object[]{fileId}, this::mapRow);
            if (fileInfo != null) { cache.put(fileId, fileInfo); }
            return fileInfo;
        });
    }

    @Override
    public List<EIFileInfo> searchFile(Condition condition, Filter filter) throws JDSException {
        StringBuilder whereClause = new StringBuilder();
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.isEmpty()) { whereClause.append(" WHERE ").append(conditionStr); }
        }
        List<EIFileInfo> files = jdbcTemplate.queryForList("SELECT * FROM VFS_FILE" + whereClause, null, this::mapRow);
        if (filter != null) { files = files.stream().filter(file -> filter.filterObject(file, null)).collect(Collectors.toList()); }
        return Collections.unmodifiableList(files);
    }

    @Override
    public List<EIFileInfo> getPersonDeletedFile(String personId) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FILE WHERE PERSON_ID = ? AND IS_RECYCLED = 1", new Object[]{personId}, this::mapRow);
    }

    @Override
    public void prepareCache(List ids) {
        if (ids == null || ids.isEmpty()) return;
        List<String> uncachedIds = new ArrayList<>();
        Map<String, EIFileInfo> cache = cacheProvider.getFileCache();
        for (Object id : ids) { String fileId = (String) id; if (!cache.containsKey(fileId)) uncachedIds.add(fileId); }
        if (!uncachedIds.isEmpty()) loadFilesByIds(uncachedIds);
    }

    private void loadFilesByIds(List<String> fileIds) {
        if (fileIds.isEmpty()) return;
        String placeholders = fileIds.stream().map(id -> "?").collect(Collectors.joining(","));
        List<DBFileInfo> files = jdbcTemplate.queryForList("SELECT * FROM VFS_FILE WHERE FILE_ID IN (" + placeholders + ")", fileIds.toArray(), this::mapRow);
        Map<String, EIFileInfo> cache = cacheProvider.getFileCache();
        for (DBFileInfo file : files) cache.put(file.getID(), file);
    }

    @Override
    public void loadLinks(DBFileInfo fileInfo) throws VFSException {
        List<String> linkIds = jdbcTemplate.queryForList("SELECT LINK_ID FROM VFS_FILE_LINK WHERE FILE_ID = ?", new Object[]{fileInfo.getID()},
            (rs, rowNum) -> rs.getString("LINK_ID"));
        for (String linkId : linkIds) fileInfo.addFileLink(linkId);
    }

    @Override
    public void deleteFileIds(String... fileIds) throws VFSException {
        if (fileIds == null || fileIds.length == 0) return;
        String placeholders = Arrays.stream(fileIds).map(id -> "?").collect(Collectors.joining(","));
        jdbcTemplate.update("DELETE FROM VFS_FILE WHERE FILE_ID IN (" + placeholders + ")", (Object[]) fileIds);
        for (String fileId : fileIds) cacheProvider.getFileCache().remove(fileId);
    }

    @Override
    public List<FileVersion> loadVersion(EIFileInfo info) throws VFSException {
        List<String> versionIds = jdbcTemplate.queryForList("SELECT VERSION_ID FROM VFS_FILE_VERSION WHERE FILE_ID = ?", new Object[]{info.getID()},
            (rs, rowNum) -> rs.getString("VERSION_ID"));
        for (String versionId : versionIds) info.addFileVersion(versionId);
        return new ArrayList<>();
    }

    @Override
    public void addRight(DBFileRight right) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE_BROWSER_RIGHT WHERE FILE_ID = ? AND ROLE_ID = ?", right.getFileId(), right.getRoleId());
    }

    @Override
    public void deleteRight(String fileId, String roleId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE_BROWSER_RIGHT WHERE FILE_ID = ? AND ROLE_ID = ?", fileId, roleId);
    }

    private void insert(DBFileInfo fileInfo) throws VFSException {
        jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, DESCRITION, CREATE_TIME, UPDATE_TIME, IS_RECYCLED, IS_LOCKED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            fileInfo.getID(), fileInfo.getName(), fileInfo.getFolderId(), fileInfo.getFileType(),
            fileInfo.getPersonId(), fileInfo.getDescrition(), fileInfo.getCreateTime(),
            fileInfo.getUpdateTime(), fileInfo.getIsRecycled(), fileInfo.getIsLocked());
    }

    private DBFileInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFileInfo file = new DBFileInfo();
        file.setID(rs.getString("FILE_ID")); file.setName(rs.getString("NAME"));
        file.setFolderId(rs.getString("FOLDER_ID")); file.setFileType(rs.getInt("FILE_TYPE"));
        file.setPersonId(rs.getString("PERSON_ID")); file.setCreateTime(rs.getLong("CREATE_TIME"));
        file.setDescrition(rs.getString("DESCRITION")); file.setUpdateTime(rs.getLong("UPDATE_TIME"));
        file.setIsRecycled(rs.getInt("IS_RECYCLED")); file.setIsLocked(rs.getInt("IS_LOCKED"));
        try { file.setRight(rs.getString("RIGHT")); } catch (SQLException ignored) {}
        try { file.setActivityInstId(rs.getString("ACTIVITY_INST_ID")); } catch (SQLException ignored) {}
        file.setModified(false); file.setInitialized(true);
        return file;
    }

    public void shutdown() {
        executorService.shutdown();
        try { if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) executorService.shutdownNow(); }
        catch (InterruptedException e) { executorService.shutdownNow(); Thread.currentThread().interrupt(); }
    }

    public void deleteBatch(List<String> fileIds) throws VFSException {
        if (fileIds == null || fileIds.isEmpty()) return;
        List<Object[]> params = fileIds.stream().map(id -> new Object[]{id}).collect(Collectors.toList());
        jdbcTemplate.batchUpdate("DELETE FROM VFS_FILE WHERE FILE_ID = ?", params);
        for (String fileId : fileIds) cacheProvider.getFileCache().remove(fileId);
    }

    public List<DBFileInfo> getFilesByFolderId(String folderId) throws VFSException {
        return jdbcTemplate.queryForList("SELECT * FROM VFS_FILE WHERE FOLDER_ID = ? ORDER BY CREATE_TIME DESC", new Object[]{folderId}, this::mapRow);
    }

    public List<DBFileInfo> getFilesByFolderIdPaged(String folderId, int page, int pageSize) throws VFSException {
        int offset = (page - 1) * pageSize;
        return jdbcTemplate.queryForPagedList("SELECT * FROM VFS_FILE WHERE FOLDER_ID = ? ORDER BY CREATE_TIME DESC", new Object[]{folderId}, this::mapRow, offset, pageSize);
    }

    public int getFileCountByFolderId(String folderId) throws VFSException {
        return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FOLDER_ID = ?", folderId);
    }

    public void updateFileInfo(DBFileInfo fileInfo) throws VFSException {
        jdbcTemplate.update("UPDATE VFS_FILE SET NAME = ?, FOLDER_ID = ?, FILE_TYPE = ?, PERSON_ID = ?, DESCRITION = ?, UPDATE_TIME = ?, IS_RECYCLED = ?, IS_LOCKED = ? WHERE FILE_ID = ?",
            fileInfo.getName(), fileInfo.getFolderId(), fileInfo.getFileType(),
            fileInfo.getPersonId(), fileInfo.getDescrition(), fileInfo.getUpdateTime(),
            fileInfo.getIsRecycled(), fileInfo.getIsLocked(), fileInfo.getID());
        cacheProvider.getFileCache().put(fileInfo.getID(), fileInfo);
    }

    public void moveFile(String fileId, String targetFolderId) throws VFSException {
        dbManager.executeInTransaction(() -> {
            jdbcTemplate.update("UPDATE VFS_FILE SET FOLDER_ID = ? WHERE FILE_ID = ?", targetFolderId, fileId);
            cacheProvider.getFileCache().remove(fileId);
            return null;
        });
    }

    public void recycleFile(String fileId) throws VFSException {
        jdbcTemplate.update("UPDATE VFS_FILE SET IS_RECYCLED = 1 WHERE FILE_ID = ?", fileId);
        cacheProvider.getFileCache().remove(fileId);
    }

    public void restoreFile(String fileId) throws VFSException {
        jdbcTemplate.update("UPDATE VFS_FILE SET IS_RECYCLED = 0 WHERE FILE_ID = ?", fileId);
        cacheProvider.getFileCache().remove(fileId);
    }
}
