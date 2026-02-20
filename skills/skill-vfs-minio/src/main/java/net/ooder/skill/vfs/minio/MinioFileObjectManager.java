package net.ooder.skill.vfs.minio;

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

@EsbBeanAnnotation(id = "MinioFileObjectManager", name = "MinIO File Object Manager", expressionArr = "MinioFileObjectManager()", desc = "MinIO based file object manager")
public class MinioFileObjectManager implements FileObjectManager {

    private static final Log log = LogFactory.getLog(VFSConstants.CONFIG_KEY, MinioFileObjectManager.class);

    private final ConcurrentHashMap<String, MinioFileObject> idCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> hashToIdCache = new ConcurrentHashMap<>();
    private final Cache<String, FileObject> fileObjectCache;
    private final Cache<String, String> fileHashCache;

    public MinioFileObjectManager() {
        fileObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "minioFileObjectCache");
        fileHashCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "minioFileHashCache");
    }

    @Override
    public void save(FileObject file) {
        if (file == null || file.getID() == null) {
            return;
        }
        MinioFileObject minioFile = (MinioFileObject) file;
        minioFile.setModified(true);
        idCache.put(minioFile.getID(), minioFile);
        fileObjectCache.put(minioFile.getID(), minioFile);
        if (minioFile.getHash() != null) {
            hashToIdCache.put(minioFile.getHash(), minioFile.getID());
            fileHashCache.put(minioFile.getHash(), minioFile.getID());
        }
        notifyCacheSync("save", minioFile.getID());
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
        MinioFileObject cached = idCache.get(objId);
        if (cached != null) {
            return cached;
        }
        FileObject fromCache = fileObjectCache.get(objId);
        if (fromCache != null) {
            idCache.put(objId, (MinioFileObject) fromCache);
            return fromCache;
        }
        return null;
    }

    @Override
    public void delete(String ID) throws VFSException {
        if (ID == null) {
            return;
        }
        MinioFileObject file = idCache.remove(ID);
        fileObjectCache.remove(ID);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
        notifyCacheSync("delete", ID);
    }

    @Override
    public FileObject createFileObject(String ID) {
        MinioFileObject file = new MinioFileObject();
        file.setID(ID != null ? ID : java.util.UUID.randomUUID().toString());
        file.setRootPath(MinioVfsConfig.getInstance().getBucket());
        file.setAdapter("net.ooder.skill.vfs.minio.MinioFileAdapter");
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
        MinioFileObject minioFile = (MinioFileObject) file;
        if (minioFile.isModified()) {
            save(minioFile);
            minioFile.setModified(false);
        }
    }

    private void notifyCacheSync(String action, String fileId) {
        try {
            MinioCacheSyncService syncService = MinioCacheSyncService.getInstance();
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
        MinioFileObject file = idCache.remove(id);
        fileObjectCache.remove(id);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
    }
}
