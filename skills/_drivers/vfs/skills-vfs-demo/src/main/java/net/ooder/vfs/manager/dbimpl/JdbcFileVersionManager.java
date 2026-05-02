package net.ooder.vfs.manager.dbimpl;

import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FileVersionManager;
import net.ooder.vfs.manager.inner.DBFileVersion;
import net.ooder.vfs.manager.inner.EIFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcFileVersionManager implements FileVersionManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileVersionManager.class);
    private static volatile JdbcFileVersionManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final VfsCacheProvider cacheProvider;

    private JdbcFileVersionManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.cacheProvider = dbManager.getCacheProvider();
    }

    public static JdbcFileVersionManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFileVersionManager.class) {
                if (instance == null) {
                    instance = new JdbcFileVersionManager();
                }
            }
        }
        return instance;
    }

    @Override
    public List<DBFileVersion> loadAll(Integer pageSize) {
        String sql = "SELECT * FROM VFS_FILE_VERSION ORDER BY CREATE_TIME ASC";
        return jdbcTemplate.queryForList(sql, null, this::mapRow);
    }

    @Override
    public DBFileVersion loadById(String versionId) throws VFSException {
        return LockManager.executeWithLock("version-" + versionId, () -> {
            String sql = "SELECT * FROM VFS_FILE_VERSION WHERE VERSION_ID = ?";
            DBFileVersion version = jdbcTemplate.queryForObject(sql, new Object[]{versionId}, this::mapRow);
            if (version != null) {
                cacheProvider.getFileVersionCache().put(version.getVersionID(), version);
            }
            return version;
        });
    }

    @Override
    public DBFileVersion createFileVersion(EIFileInfo info) {
        DBFileVersion version = new DBFileVersion();
        version.setFileId(info.getID());
        version.setPersonId(info.getPersonId());
        version.setVersionID(UUID.randomUUID().toString());
        version.setVersionName(info.getName());
        version.setCreateTime(System.currentTimeMillis());
        try {
            save(version);
        } catch (Exception e) {
            log.error("Failed to create file version: " + e.getMessage(), e);
        }
        return version;
    }

    @Override
    public void commit(DBFileVersion fileVersion) throws VFSException {
        if (fileVersion.isModified()) {
            dbManager.executeInTransaction(() -> {
                delete(fileVersion.getVersionID());
                insert(fileVersion);
                fileVersion.setModified(false);
                return null;
            });
        }
    }

    @Override
    public FileVersion save(DBFileVersion fileVersion) {
        try { insert(fileVersion); } catch (VFSException e) { log.error("save failed", e); }
        return fileVersion;
    }

    @Override
    public List<FileVersion> getVersionByObjectId(String fileObjectId) {
        String sql = "SELECT * FROM VFS_FILE_VERSION WHERE FILE_OBJECT_ID = ? ORDER BY CREATE_TIME DESC";
        List<DBFileVersion> versions = jdbcTemplate.queryForList(sql, new Object[]{fileObjectId}, this::mapRow);
        return new ArrayList<>(versions);
    }

    @Override
    public FileVersion loadByIndex(String fileId, Integer index) {
        try {
            String sql = "SELECT * FROM VFS_FILE_VERSION WHERE FILE_ID = ? AND VERSION_INDEX = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{fileId, index}, this::mapRow);
        } catch (Exception e) { return null; }
    }

    @Override
    public List<FileView> loadView(DBFileVersion version) throws VFSException {
        String sql = "SELECT VIEW_ID FROM VFS_FILE_VIEW WHERE VERSION_ID = ?";
        List<String> viewIds = jdbcTemplate.queryForList(sql, new Object[]{version.getVersionID()},
            (rs, rowNum) -> rs.getString("VIEW_ID"));
        for (String viewId : viewIds) {
            version.addFileView(viewId);
        }
        version.setFileIdViewList_is_initialized(true);
        return new ArrayList<>();
    }

    @Override
    public boolean delete(String versionId) {
        try {
            jdbcTemplate.update("DELETE FROM VFS_FILE_VERSION WHERE VERSION_ID = ?", versionId);
            cacheProvider.getFileVersionCache().remove(versionId);
            return true;
        } catch (Exception e) { return false; }
    }

    private void insert(DBFileVersion version) throws VFSException {
        String sql = "INSERT INTO VFS_FILE_VERSION (VERSION_ID, FILE_ID, VERSION_INDEX, PERSON_ID, " +
                     "CREATE_TIME, FILE_OBJECT_ID, VERSION_NAME) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            version.getVersionID(), version.getFileId(), version.getIndex(),
            version.getPersonId(), version.getCreateTime(), version.getFileObjectId(),
            version.getVersionName());
        cacheProvider.getFileVersionCache().put(version.getVersionID(), version);
    }

    private DBFileVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFileVersion version = new DBFileVersion();
        version.setVersionID(rs.getString("VERSION_ID"));
        version.setFileId(rs.getString("FILE_ID"));
        version.setIndex(rs.getInt("VERSION_INDEX"));
        version.setPersonId(rs.getString("PERSON_ID"));
        version.setCreateTime(rs.getLong("CREATE_TIME"));
        version.setFileObjectId(rs.getString("FILE_OBJECT_ID"));
        try { version.setVersionName(rs.getString("VERSION_NAME")); } catch (SQLException ignored) {}
        version.setModified(false);
        return version;
    }
}
