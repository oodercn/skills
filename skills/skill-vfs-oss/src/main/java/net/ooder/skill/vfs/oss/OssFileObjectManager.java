package net.ooder.skill.vfs.oss;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.VFSConstants;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.store.manager.FileObjectManager;

import java.util.concurrent.ConcurrentHashMap;

@EsbBeanAnnotation(id = "OssFileObjectManager", name = "OSS File Object Manager", expressionArr = "OssFileObjectManager()", desc = "OSS based file object manager")
public class OssFileObjectManager implements FileObjectManager {

    private static final Log log = LogFactory.getLog(VFSConstants.CONFIG_KEY, OssFileObjectManager.class);

    private final ConcurrentHashMap<String, OssFileObject> idCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> hashToIdCache = new ConcurrentHashMap<>();
    private final Cache<String, FileObject> fileObjectCache;
    private final Cache<String, String> fileHashCache;

    public OssFileObjectManager() {
        fileObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "ossFileObjectCache");
        fileHashCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "ossFileHashCache");
    }

    @Override
    public void save(FileObject file) {
        if (file == null || file.getID() == null) {
            return;
        }
        OssFileObject ossFile = (OssFileObject) file;
        ossFile.setModified(true);
        idCache.put(ossFile.getID(), ossFile);
        fileObjectCache.put(ossFile.getID(), ossFile);
        if (ossFile.getHash() != null) {
            hashToIdCache.put(ossFile.getHash(), ossFile.getID());
            fileHashCache.put(ossFile.getHash(), ossFile.getID());
        }
        notifyCacheSync("save", ossFile.getID());
    }

    @Override
    public FileObject loadByHash(String hash) {
        if (hash == null) {
            return null;
        }
        String id = hashToIdCache.get(hash);
        if (id != null) {
            return loadById(id);
        }
        id = fileHashCache.get(hash);
        if (id != null) {
            return loadById(id);
        }
        return null;
    }

    @Override
    public FileObject loadById(String objId) {
        if (objId == null) {
            return null;
        }
        OssFileObject cached = idCache.get(objId);
        if (cached != null) {
            return cached;
        }
        FileObject fromCache = fileObjectCache.get(objId);
        if (fromCache != null) {
            idCache.put(objId, (OssFileObject) fromCache);
            return fromCache;
        }
        return null;
    }

    @Override
    public void delete(String ID) throws VFSException {
        if (ID == null) {
            return;
        }
        OssFileObject file = idCache.remove(ID);
        fileObjectCache.remove(ID);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
        notifyCacheSync("delete", ID);
    }

    @Override
    public FileObject createFileObject(String ID) {
        OssFileObject file = new OssFileObject();
        file.setID(ID != null ? ID : java.util.UUID.randomUUID().toString());
        file.setRootPath(OssVfsConfig.getInstance().getBucket());
        file.setAdapter("net.ooder.skill.vfs.oss.OssFileAdapter");
        file.setCreateTime(System.currentTimeMillis());
        return file;
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        return idCache.size();
    }

    @Override
    public void commit(FileObject file) throws VFSException {
        if (file == null) {
            return;
        }
        OssFileObject ossFile = (OssFileObject) file;
        if (ossFile.isModified()) {
            save(ossFile);
            ossFile.setModified(false);
        }
    }

    private void notifyCacheSync(String action, String fileId) {
        try {
            OssCacheSyncService syncService = OssCacheSyncService.getInstance();
            if (syncService != null) {
                syncService.broadcastCacheInvalidation(action, fileId);
            }
        } catch (Exception e) {
            log.error("Failed to notify cache sync for: " + fileId, e);
        }
    }

    public void clearCache() {
        idCache.clear();
        hashToIdCache.clear();
        fileObjectCache.clear();
        fileHashCache.clear();
    }

    public void removeFromCache(String id) {
        OssFileObject file = idCache.remove(id);
        fileObjectCache.remove(id);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
    }
}
