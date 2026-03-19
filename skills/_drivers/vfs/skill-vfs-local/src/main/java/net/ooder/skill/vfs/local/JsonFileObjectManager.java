package net.ooder.skill.vfs.local;

import net.ooder.annotation.EsbBeanAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.store.manager.FileObjectManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EsbBeanAnnotation(id = "JsonFileObjectManager", name = "JSON File Object Manager", expressionArr = "JsonFileObjectManager()", desc = "JSON based file object manager for local storage")
public class JsonFileObjectManager implements FileObjectManager {

    private static final Logger log = LoggerFactory.getLogger(JsonFileObjectManager.class);

    private final String metaPath;
    private final String filePath;
    private final FileAdapter fileAdapter;

    private final ConcurrentHashMap<String, JsonFileObject> idCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> hashToIdCache = new ConcurrentHashMap<>();

    public JsonFileObjectManager() {
        this.metaPath = LocalVfsConfig.getMetaPath();
        this.filePath = LocalVfsConfig.getFilePath();
        this.fileAdapter = LocalVfsConfig.getFileAdapter();
        initDirectories();
    }

    public JsonFileObjectManager(String metaPath, String filePath, FileAdapter fileAdapter) {
        this.metaPath = metaPath;
        this.filePath = filePath;
        this.fileAdapter = fileAdapter;
        initDirectories();
    }

    private void initDirectories() {
        File metaDir = new File(metaPath);
        if (!metaDir.exists()) {
            metaDir.mkdirs();
        }
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }

    @Override
    public void save(FileObject file) {
        if (file == null || file.getID() == null) {
            return;
        }
        JsonFileObject jsonFile = (JsonFileObject) file;
        jsonFile.setModified(true);
        idCache.put(jsonFile.getID(), jsonFile);
        if (jsonFile.getHash() != null) {
            hashToIdCache.put(jsonFile.getHash(), jsonFile.getID());
        }
        saveToJson(jsonFile);
        notifyCacheSync("save", jsonFile.getID());
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
        File hashFile = new File(metaPath + "/hash/" + hash + ".json");
        if (hashFile.exists()) {
            JsonFileObject file = loadFromJson(hashFile);
            if (file != null) {
                idCache.put(file.getID(), file);
                hashToIdCache.put(hash, file.getID());
                return file;
            }
        }
        return null;
    }

    @Override
    public FileObject loadById(String objId) {
        if (objId == null) {
            return null;
        }
        JsonFileObject cached = idCache.get(objId);
        if (cached != null) {
            return cached;
        }
        File metaFile = new File(metaPath + "/objects/" + objId + ".json");
        if (metaFile.exists()) {
            JsonFileObject file = loadFromJson(metaFile);
            if (file != null) {
                idCache.put(objId, file);
                if (file.getHash() != null) {
                    hashToIdCache.put(file.getHash(), objId);
                }
                return file;
            }
        }
        return null;
    }

    @Override
    public void delete(String ID) throws VFSException {
        if (ID == null) {
            return;
        }
        JsonFileObject file = idCache.remove(ID);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
        }
        File metaFile = new File(metaPath + "/objects/" + ID + ".json");
        if (metaFile.exists()) {
            metaFile.delete();
        }
        if (file != null && file.getHash() != null) {
            File hashFile = new File(metaPath + "/hash/" + file.getHash() + ".json");
            if (hashFile.exists()) {
                hashFile.delete();
            }
        }
        notifyCacheSync("delete", ID);
    }

    @Override
    public FileObject createFileObject(String ID) {
        JsonFileObject file = new JsonFileObject();
        file.setID(ID != null ? ID : UUID.randomUUID().toString());
        file.setRootPath(filePath);
        file.setAdapter("net.ooder.skill.vfs.local.LocalFileAdapter");
        file.setCreateTime(System.currentTimeMillis());
        return file;
    }

    @Override
    public Integer loadAll(Integer pageSize) {
        File objectsDir = new File(metaPath + "/objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();
            return 0;
        }
        File[] files = objectsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return 0;
        }
        int count = 0;
        for (File metaFile : files) {
            try {
                JsonFileObject file = loadFromJson(metaFile);
                if (file != null) {
                    idCache.put(file.getID(), file);
                    if (file.getHash() != null) {
                        hashToIdCache.put(file.getHash(), file.getID());
                    }
                    count++;
                }
            } catch (Exception e) {
                log.error("Failed to load file object from: {}", metaFile.getPath(), e);
            }
        }
        log.info("Loaded {} file objects from JSON storage", count);
        return count;
    }

    @Override
    public void commit(FileObject file) throws VFSException {
        if (file == null) {
            return;
        }
        JsonFileObject jsonFile = (JsonFileObject) file;
        if (jsonFile.isModified()) {
            save(jsonFile);
            jsonFile.setModified(false);
        }
    }

    private void saveToJson(JsonFileObject file) {
        try {
            File objectsDir = new File(metaPath + "/objects");
            if (!objectsDir.exists()) {
                objectsDir.mkdirs();
            }
            File hashDir = new File(metaPath + "/hash");
            if (!hashDir.exists()) {
                hashDir.mkdirs();
            }
            String json = file.toJson();
            File metaFile = new File(objectsDir, file.getID() + ".json");
            try (FileOutputStream fos = new FileOutputStream(metaFile)) {
                fos.write(json.getBytes("UTF-8"));
            }
            if (file.getHash() != null) {
                File hashFile = new File(hashDir, file.getHash() + ".json");
                try (FileOutputStream fos = new FileOutputStream(hashFile)) {
                    fos.write(json.getBytes("UTF-8"));
                }
            }
        } catch (IOException e) {
            log.error("Failed to save file object to JSON: {}", file.getID(), e);
            throw new VFSException("Failed to save file object", e);
        }
    }

    private JsonFileObject loadFromJson(File file) {
        try {
            if (!file.exists()) {
                return null;
            }
            byte[] data = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(data);
            }
            String json = new String(data, "UTF-8");
            return JsonFileObject.fromJson(json);
        } catch (IOException e) {
            log.error("Failed to load file object from JSON: {}", file.getPath(), e);
            return null;
        }
    }

    private void notifyCacheSync(String action, String fileId) {
        try {
            VfsCacheSyncService syncService = VfsCacheSyncService.getInstance();
            if (syncService != null) {
                syncService.broadcastCacheInvalidation(action, fileId);
            }
        } catch (Exception e) {
            log.error("Failed to notify cache sync for: {}", fileId, e);
        }
    }

    public void clearCache() {
        idCache.clear();
        hashToIdCache.clear();
    }

    public void removeFromCache(String id) {
        JsonFileObject file = idCache.remove(id);
        if (file != null && file.getHash() != null) {
            hashToIdCache.remove(file.getHash());
        }
    }
}
