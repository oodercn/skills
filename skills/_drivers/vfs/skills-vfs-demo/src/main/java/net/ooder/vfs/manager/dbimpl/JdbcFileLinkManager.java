package net.ooder.vfs.manager.dbimpl;

import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FileLinkManager;
import net.ooder.vfs.manager.inner.DBFileLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcFileLinkManager implements FileLinkManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileLinkManager.class);
    private static volatile JdbcFileLinkManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final VfsCacheProvider cacheProvider;

    private JdbcFileLinkManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.cacheProvider = dbManager.getCacheProvider();
    }

    public static JdbcFileLinkManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFileLinkManager.class) {
                if (instance == null) {
                    instance = new JdbcFileLinkManager();
                }
            }
        }
        return instance;
    }

    @Override
    public DBFileLink createLink() {
        return new DBFileLink();
    }

    @Override
    public void save(DBFileLink link) throws VFSException {
        insert(link);
    }

    @Override
    public void delete(String linkId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE_LINK WHERE LINK_ID = ?", linkId);
        cacheProvider.getFileLinkCache().remove(linkId);
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        String sql = "SELECT * FROM VFS_FILE_LINK";
        var links = jdbcTemplate.queryForList(sql, null, this::mapRow);
        for (DBFileLink link : links) {
            cacheProvider.getFileLinkCache().put(link.getID(), link);
        }
        return links.size();
    }

    @Override
    public DBFileLink loadById(String objId) throws VFSException {
        return LockManager.executeWithLock("link-" + objId, () -> {
            String sql = "SELECT * FROM VFS_FILE_LINK WHERE LINK_ID = ?";
            DBFileLink link = jdbcTemplate.queryForObject(sql, new Object[]{objId}, this::mapRow);
            if (link != null) {
                cacheProvider.getFileLinkCache().put(link.getID(), link);
            }
            return link;
        });
    }

    private void insert(DBFileLink link) throws VFSException {
        if (link.getID() == null || link.getID().isEmpty()) {
            link.setID(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO VFS_FILE_LINK (LINK_ID, FILE_ID, PERSON_ID, RIGHT_VAL, STATE, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, link.getID(), link.getFileId(), link.getPersonId(),
            link.getRight(), link.getState(), link.getCreateTime());
        cacheProvider.getFileLinkCache().put(link.getID(), link);
    }

    private DBFileLink mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFileLink link = new DBFileLink();
        link.setID(rs.getString("LINK_ID"));
        link.setFileId(rs.getString("FILE_ID"));
        link.setPersonId(rs.getString("PERSON_ID"));
        link.setRight(rs.getString("RIGHT_VAL"));
        link.setState(rs.getString("STATE"));
        link.setCreateTime(rs.getLong("CREATE_TIME"));
        return link;
    }
}
