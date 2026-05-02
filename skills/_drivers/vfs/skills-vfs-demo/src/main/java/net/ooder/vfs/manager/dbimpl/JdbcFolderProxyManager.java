package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.ConfigCode;
import net.ooder.vfs.Folder;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.manager.FolderProxyManager;
import net.ooder.vfs.manager.inner.EIFolder;
import net.ooder.vfs.proxy.FolderProxy;
import net.ooder.vfs.proxy.VFSListProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcFolderProxyManager implements FolderProxyManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFolderProxyManager.class);
    private static final Map<ConfigCode, JdbcFolderProxyManager> managerMap = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;
    private final ConfigCode subSystemId;
    private final VfsCacheProvider cacheProvider;

    private JdbcFolderProxyManager(ConfigCode subSystemId) {
        this.subSystemId = subSystemId;
        JdbcManager mgr = JdbcManager.getInstance();
        this.jdbcTemplate = new JdbcTemplate(mgr);
        this.cacheProvider = mgr.getCacheProvider();
    }

    public static JdbcFolderProxyManager getInstance(ConfigCode sysCode) {
        return managerMap.computeIfAbsent(sysCode, JdbcFolderProxyManager::new);
    }

    public static JdbcFolderProxyManager getInstance() {
        return getInstance(ConfigCode.vfs);
    }

    @Override
    public FolderProxy loadFolder(EIFolder folder) {
        return new FolderProxy(folder, subSystemId);
    }

    @Override
    public List<Folder> getFolderProxyList(List<EIFolder> folderList) {
        return new VFSListProxy(folderList);
    }

    @Override
    public void loadRoles(FolderProxy proxy) throws VFSException {
        String sql = "SELECT ROLE_ID, TYPE FROM VFS_FOLDER_BROWSER_RIGHT WHERE FOLDER_ID = ? ORDER BY TYPE";
        List<Map<String, Object>> roles = jdbcTemplate.queryForList(sql, new Object[]{proxy.getID()},
            (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("roleId", rs.getString("ROLE_ID"));
                map.put("type", rs.getInt("TYPE"));
                return map;
            });
        proxy.setRoleIdCache_is_initialized(true);
        proxy.getRoleIdCache().clear();
        for (Map<String, Object> role : roles) {
            proxy.addRole(null, (String) role.get("roleId"));
        }
    }

    @Override
    public void delete(FolderProxy folder) {
        try {
            cacheProvider.getFolderCache().remove(folder.getID());
        } catch (Exception e) {
            log.error("Failed to delete folder proxy: " + e.getMessage(), e);
        }
    }

    @Override
    public void commit(FolderProxy folder) throws VFSException {
        try {
            EIFolder eifolder = cacheProvider.getFolderCache().get(folder.getID());
            if (eifolder != null) {
                cacheProvider.getFolderCache().put(eifolder.getID(), eifolder);
            }
        } catch (Exception e) {
            throw new VFSException(e);
        }
    }
}
