package net.ooder.skill.vfs.s3;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EsbBeanAnnotation(id = "S3FileObjectManager", name = "S3 File Object Manager", expressionArr = "S3FileObjectManager()", desc = "S3 based file object manager")
public class S3FileObjectManager implements FileObjectManager {

    private static final Log log = LogFactory.getLog(VFSConstants.CONFIG_KEY, S3FileObjectManager.class);

    private final ConcurrentHashMap<String, S3FileObject> idCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> hashToIdCache = new ConcurrentHashMap<>();
    private final Cache<String, FileObject> fileObjectCache;
    private final Cache<String, String> fileHashCache;

    public S3FileObjectManager() {
        fileObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "s3FileObjectCache");
        fileHashCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "s3FileHashCache");
    }

    @Override
    public void save(FileObject file) {
        if (file == null || file.getID() == null) {
            return;
        }
        S3FileObject s3File = (S3FileObject) file;
        s3File.setModified(true);
        idCache.put(s3File.getID(), s3File);
        fileObjectCache.put(s3File.getID(), s3File);
        if (s3File.getHash() != null) {
            hashToIdCache.put(s3File.getHash(), s3File.getID());
            fileHashCache.put(s3File.getHash(), s3File.getID());
        }
        notifyCacheSync("save", s3File.getID());
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
        S3FileObject cached = idCache.get(objId);
        if (cached != null) {
            return cached;
        }
        FileObject fromCache = fileObjectCache.get(objId);
        if (fromCache != null) {
            idCache.put(objId, (S3FileObject) fromCache);
            return fromCache;
        }
        return null;
    }

    @Override
    public void delete(String ID) throws VFSException {
        if (ID == null) {
            return;
        }
        S3FileObject file = idCache.remove(ID);
        fileObjectCache.remove(ID);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
        notifyCacheSync("delete", ID);
    }

    @Override
    public FileObject createFileObject(String ID) {
        S3FileObject file = new S3FileObject();
        file.setID(ID != null ? ID : UUID.randomUUID().toString());
        file.setRootPath(S3VfsConfig.getInstance().getBucket());
        file.setAdapter("net.ooder.skill.vfs.s3.S3FileAdapter");
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
        S3FileObject s3File = (S3FileObject) file;
        if (s3File.isModified()) {
            save(s3File);
            s3File.setModified(false);
        }
    }

    private void notifyCacheSync(String action, String fileId) {
        try {
            S3CacheSyncService syncService = S3CacheSyncService.getInstance();
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
        S3FileObject file = idCache.remove(id);
        fileObjectCache.remove(id);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
            fileHashCache.remove(file.getHash());
        }
    }
}
