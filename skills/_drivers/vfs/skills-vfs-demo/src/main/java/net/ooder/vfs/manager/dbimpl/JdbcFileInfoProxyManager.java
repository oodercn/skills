package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.manager.FileInfoProxyManager;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.proxy.FileInfoProxy;
import net.ooder.vfs.proxy.VFSListProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcFileInfoProxyManager implements FileInfoProxyManager {

    private static final Logger log = LoggerFactory.getLogger(JdbcFileInfoProxyManager.class);
    private static final Map<ConfigCode, JdbcFileInfoProxyManager> managerMap = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;
    private final ConfigCode subSystemId;

    private JdbcFileInfoProxyManager(ConfigCode subSystemId) {
        this.subSystemId = subSystemId;
        this.jdbcTemplate = new JdbcTemplate(JdbcManager.getInstance());
    }

    public static JdbcFileInfoProxyManager getInstance(ConfigCode sysCode) {
        return managerMap.computeIfAbsent(sysCode, JdbcFileInfoProxyManager::new);
    }

    @Override
    public List<FileInfo> getFileInfoProxyList(List<EIFileInfo> fileInfoList) {
        return new VFSListProxy(fileInfoList);
    }

    @Override
    public FileInfo loadFileInfo(EIFileInfo fileInfo) {
        FileInfoProxy proxy = new FileInfoProxy(fileInfo, subSystemId);
        return loadFromDb(proxy);
    }

    private FileInfoProxy loadFromDb(FileInfoProxy proxy) {
        try {
            if (!proxy.isInitialized()) {
                return proxy;
            }
            loadRoles(proxy);
            proxy.setRoleIdCache_is_initialized(true);
        } catch (Exception e) {
            log.error("Failed to load file info proxy: " + e.getMessage(), e);
        }
        return proxy;
    }

    @Override
    public void loadRoles(FileInfoProxy proxy) throws VFSException {
        String sql = "SELECT ROLE_ID, TYPE FROM VFS_FILE_BROWSER_RIGHT WHERE FILE_ID = ? ORDER BY TYPE";
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
}
