package net.ooder.vfs.manager.dbimpl;

import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.FileCopy;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FileCopyManager;
import net.ooder.vfs.manager.inner.DBFileCopy;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcFileCopyManager implements FileCopyManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileCopyManager.class);
    private static volatile JdbcFileCopyManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final VfsCacheProvider cacheProvider;

    private JdbcFileCopyManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.cacheProvider = dbManager.getCacheProvider();
    }

    public static JdbcFileCopyManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFileCopyManager.class) {
                if (instance == null) {
                    instance = new JdbcFileCopyManager();
                }
            }
        }
        return instance;
    }

    @Override
    public FileCopy createFileCopy() {
        return new DBFileCopy();
    }

    @Override
    public void delete(String copyId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE_COPY WHERE COPY_ID = ?", copyId);
        cacheProvider.getFileCopyCache().remove(copyId);
    }

    @Override
    public void save(FileCopy copy) throws VFSException {
        insert((DBFileCopy) copy);
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        String sql = "SELECT * FROM VFS_FILE_COPY";
        var copies = jdbcTemplate.queryForList(sql, null, this::mapRow);
        for (DBFileCopy copy : copies) {
            cacheProvider.getFileCopyCache().put(copy.getID(), copy);
        }
        return copies.size();
    }

    @Override
    public FileCopy loadById(String copyId) throws VFSException {
        return LockManager.executeWithLock("copy-" + copyId, () -> {
            String sql = "SELECT * FROM VFS_FILE_COPY WHERE COPY_ID = ?";
            DBFileCopy copy = jdbcTemplate.queryForObject(sql, new Object[]{copyId}, this::mapRow);
            if (copy != null) {
                cacheProvider.getFileCopyCache().put(copy.getID(), copy);
            }
            return copy;
        });
    }

    private void insert(DBFileCopy copy) throws VFSException {
        String sql = "INSERT INTO VFS_FILE_COPY (COPY_ID, VERSION_ID, NAME, STATE, RIGHT_VAL, PERSON_ID, FOLDER_ID, FILE_ID, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, copy.getID(), copy.getVersionId(), copy.getName(),
            copy.getState(), copy.getMaxRight(), copy.getPersonId(),
            copy.getFolderId(), copy.getFileId(), copy.getCreateTime());
        cacheProvider.getFileCopyCache().put(copy.getID(), copy);
    }

    private DBFileCopy mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFileCopy copy = new DBFileCopy();
        copy.setID(rs.getString("COPY_ID"));
        copy.setVersionId(rs.getString("VERSION_ID"));
        copy.setName(rs.getString("NAME"));
        copy.setState(rs.getInt("STATE"));
        copy.setMaxRight(rs.getInt("RIGHT_VAL"));
        copy.setPersonId(rs.getString("PERSON_ID"));
        copy.setFolderId(rs.getString("FOLDER_ID"));
        copy.setFileId(rs.getString("FILE_ID"));
        copy.setCreateTime(rs.getLong("CREATE_TIME"));
        return copy;
    }
}
