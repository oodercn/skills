package net.ooder.vfs.manager.dbimpl;

import net.ooder.vfs.FileView;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.manager.FileViewManager;
import net.ooder.vfs.manager.inner.DBFileView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcFileViewManager implements FileViewManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileViewManager.class);
    private static volatile JdbcFileViewManager instance;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcManager dbManager;
    private final VfsCacheProvider cacheProvider;

    private JdbcFileViewManager() {
        this.dbManager = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(dbManager);
        this.cacheProvider = dbManager.getCacheProvider();
    }

    public static JdbcFileViewManager getInstance() {
        if (instance == null) {
            synchronized (JdbcFileViewManager.class) {
                if (instance == null) { instance = new JdbcFileViewManager(); }
            }
        }
        return instance;
    }

    @Override
    public DBFileView createView(String versionId, Integer fileIndex) {
        DBFileView view = new DBFileView();
        view.setID(UUID.randomUUID().toString());
        view.setVersionId(versionId);
        view.setFileIndex(fileIndex);
        try { save(view); } catch (Exception e) { log.error("createView failed", e); }
        return view;
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        String sql = "SELECT * FROM VFS_FILE_VIEW";
        var views = jdbcTemplate.queryForList(sql, null, this::mapRow);
        for (DBFileView view : views) { cacheProvider.getFileViewCache().put(view.getID(), view); }
        return views.size();
    }

    @Override
    public DBFileView loadById(String viewId) {
        return LockManager.executeWithLock("view-" + viewId, () -> {
            String sql = "SELECT * FROM VFS_FILE_VIEW WHERE VIEW_ID = ?";
            DBFileView view = jdbcTemplate.queryForObject(sql, new Object[]{viewId}, this::mapRow);
            if (view != null) { cacheProvider.getFileViewCache().put(view.getID(), view); }
            return view;
        });
    }

    @Override
    public void commit(DBFileView view) throws VFSException {
        if (view.isModified()) {
            dbManager.executeInTransaction(() -> {
                delete(view.getID());
                insert(view);
                view.setModified(false);
                return null;
            });
        }
    }

    @Override
    public FileView save(DBFileView view) {
        try { insert(view); } catch (VFSException e) { log.error("save view failed", e); }
        return view;
    }

    @Override
    public void delete(String viewId) throws VFSException {
        jdbcTemplate.update("DELETE FROM VFS_FILE_VIEW WHERE VIEW_ID = ?", viewId);
        cacheProvider.getFileViewCache().remove(viewId);
    }

    private void insert(DBFileView view) throws VFSException {
        if (view.getID() == null || view.getID().isEmpty()) { view.setID(UUID.randomUUID().toString()); }
        String sql = "INSERT INTO VFS_FILE_VIEW (VIEW_ID, FILE_INDEX, VERSION_ID, FILE_TYPE, FILE_OBJECT_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, view.getID(), view.getFileIndex(), view.getVersionId(),
            view.getFileType(), view.getFileObjectId());
        cacheProvider.getFileViewCache().put(view.getID(), view);
    }

    private DBFileView mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBFileView view = new DBFileView();
        view.setID(rs.getString("VIEW_ID"));
        view.setFileIndex(rs.getInt("FILE_INDEX"));
        view.setVersionId(rs.getString("VERSION_ID"));
        view.setFileType(rs.getInt("FILE_TYPE"));
        view.setFileObjectId(rs.getString("FILE_OBJECT_ID"));
        try { view.setName(rs.getString("NAME")); } catch (SQLException ignored) {}
        view.setModified(false);
        return view;
    }
}
