package net.ooder.skill.vfs.local;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.VFSConstants;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.store.manager.FileObjectManager;
import net.ooder.vfs.store.service.StoreService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@EsbBeanAnnotation(id = "StoreService", name = "Local Store Service", expressionArr = "LocalStoreServiceImpl()", desc = "Local file system store service implementation")
public class LocalStoreServiceImpl implements StoreService {

    private static final Log log = LogFactory.getLog(VFSConstants.CONFIG_KEY, LocalStoreServiceImpl.class);

    private FileObjectManager fileObjectManager;

    private static Cache<String, FileObject> fileObjectCache;
    private static Cache<String, String> fileHashCache;

    public LocalStoreServiceImpl() {
        init();
    }

    private void init() {
        LocalVfsConfig.init();
        fileObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "fileObjectCache");
        fileHashCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_STORE_KEY, "fileHashCache");
        VfsCacheSyncService syncService = VfsCacheSyncService.getInstance();
        syncService.start();
    }

    private FileObjectManager getFileObjectManager() {
        if (fileObjectManager == null) {
            fileObjectManager = new JsonFileObjectManager();
            VfsCacheSyncService.getInstance().setFileObjectManager((JsonFileObjectManager) fileObjectManager);
            fileObjectManager.loadAll(1000);
        }
        return fileObjectManager;
    }

    @Override
    public FileObject createFileObject(MD5InputStream md5InputStream) {
        String hash = "";
        File temp = null;
        FileOutputStream out = null;
        FileInputStream in = null;
        FileObject fileObject = null;

        try {
            temp = File.createTempFile("" + System.currentTimeMillis(), ".temp");
            out = new FileOutputStream(temp);
            in = new FileInputStream(temp);
            IOUtils.copy(md5InputStream, out);
            hash = DigestUtils.md5Hex(in);
            fileObject = getFileObjectByHash(hash);

            if (fileObject != null && fileObject.getLength() == 0) {
                deleteFileObject(fileObject.getID());
                fileObject = null;
            } else if (fileObject != null && fileObject.getPath() != null) {
                FileAdapter adapter = getFileAdapter();
                if (!adapter.exists(fileObject.getPath())) {
                    adapter.mkdirs(fileObject.getPath());
                    adapter.write(fileObject.getPath(), new FileInputStream(temp));
                }
            }

            if (fileObject == null) {
                String[] paths = createFolderPath(null);
                String physicalPath = paths[1].replaceAll("\\\\", "/");

                log.info("physicalPath: " + physicalPath);
                fileObject = this.createFileObject(hash);
                fileObject.setHash(hash);
                fileObject.setPath(physicalPath + hash);

                FileAdapter adapter = getFileAdapter();
                adapter.mkdirs(physicalPath);
                adapter.write(physicalPath + hash, new FileInputStream(temp));
                fileObject.setLength(adapter.getLength(physicalPath + hash));
                updateFileObject(fileObject);
            }

            log.info("end delete.... path=" + temp.getPath());
            temp.deleteOnExit();
        } catch (Exception e) {
            log.error("Failed to create file object", e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                log.error("Failed to close streams when creating file object", e);
            }
        }
        return fileObject;
    }

    @Override
    public FileObject getFileObjectByHash(String hash) {
        if (hash == null) {
            return null;
        }
        String fileObjId = fileHashCache.get(hash);
        FileObject fileObject = null;
        if (fileObjId != null) {
            fileObject = getFileObjectByID(fileObjId);
        } else {
            fileObject = getFileObjectManager().loadByHash(hash);
            if (fileObject != null) {
                fileHashCache.put(hash, fileObject.getID());
            }
        }

        if (fileObject != null) {
            FileAdapter fileAdapter = getFileAdapter();
            String vfsPath = fileObject.getPath();
            if (vfsPath == null || !fileAdapter.exists(vfsPath)) {
                deleteFileObject(fileObject.getID());
                fileObject = null;
            }
        }

        return fileObject;
    }

    @Override
    public FileObject getFileObjectByID(String objId) {
        if (objId == null) {
            return null;
        }

        synchronized (objId.intern()) {
            FileObject fileObject = fileObjectCache.get(objId);
            if (fileObject == null) {
                fileObject = getFileObjectManager().loadById(objId);
                if (fileObject != null) {
                    fileObjectCache.put(objId, fileObject);
                }
            }
            if (fileObject != null) {
                String hash = fileObject.getHash();
                if (hash != null && fileHashCache.get(hash) == null) {
                    fileHashCache.put(hash, fileObject.getID());
                }
            }
            return fileObject;
        }
    }

    @Override
    public Boolean deleteFileObject(String ID) {
        try {
            FileObject fileObject = getFileObjectByID(ID);
            if (fileObject != null) {
                if (fileObject.getHash() != null) {
                    fileHashCache.remove(fileObject.getHash());
                }
                fileObjectCache.remove(ID);
            }
            getFileObjectManager().delete(ID);
        } catch (VFSException e) {
            log.error("Failed to delete file object: " + ID, e);
        }
        return true;
    }

    @Override
    public void updateFileObject(FileObject fileObject) {
        if (fileObject == null) {
            return;
        }
        if (fileObject.getHash() != null) {
            fileHashCache.put(fileObject.getHash(), fileObject.getID());
        }
        fileObjectCache.put(fileObject.getID(), fileObject);
        getFileObjectManager().save(fileObject);
    }

    @Override
    public Integer writeLine(String fileObjectId, String json) {
        Integer ln = -1;
        FileObject object = getFileObjectByID(fileObjectId);
        FileAdapter fileAdapter = getFileAdapter();
        if (object != null) {
            String vfsPath = object.getPath();
            if (vfsPath != null && fileAdapter.exists(vfsPath)) {
                ln = fileAdapter.writeLine(vfsPath, json);
                JsonFileObject fo = (JsonFileObject) object;
                fo.setLength(fileAdapter.getLength(vfsPath));
                fo.setHash(fileAdapter.getMD5Hash(vfsPath));
                fo.setUpdateTime(System.currentTimeMillis());
                updateFileObject(fo);
            }
        }
        return ln;
    }

    @Override
    public List<String> readLine(String fileObjectId, List<Integer> lines) {
        FileAdapter fileAdapter = getFileAdapter();
        FileObject object = getFileObjectByID(fileObjectId);
        List<String> strings = new ArrayList<>();
        if (object != null) {
            String vfsPath = object.getPath();
            if (vfsPath != null && fileAdapter.exists(vfsPath)) {
                strings = fileAdapter.readLine(vfsPath, lines);
            }
        }
        return strings;
    }

    @Override
    public FileAdapter getFileAdapter() {
        return LocalVfsConfig.getFileAdapter();
    }

    private FileObject createFileObject(String ID) {
        return getFileObjectManager().createFileObject(ID);
    }

    private String[] createFolderPath(String path) {
        String temppath = getFileAdapter().createFolderPath();
        return new String[]{temppath, temppath};
    }
}
