package net.ooder.skill.vfs.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.ooder.skill.common.storage.JsonStorage;
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

public class LocalStoreServiceImpl implements StoreService {

    private static final Logger log = LoggerFactory.getLogger(LocalStoreServiceImpl.class);

    private FileObjectManager fileObjectManager;
    private JsonStorage jsonStorage;
    private String basePath;

    public void setBasePath(String basePath) {
        this.basePath = basePath;
        this.jsonStorage = new JsonStorage(basePath + "/vfs");
    }

    public void setFileObjectManager(FileObjectManager fileObjectManager) {
        this.fileObjectManager = fileObjectManager;
    }

    @Override
    public FileObject createFileObject(String fileName, Long fileSize, String mimeType) {
        String fileId = "file-" + UUID.randomUUID().toString().substring(0, 8);
        String hash = "temp-" + System.currentTimeMillis();
        
        FileObject fileObject = new FileObject();
        fileObject.setFileId(fileId);
        fileObject.setFileName(fileName);
        fileObject.setFileSize(fileSize);
        fileObject.setMimeType(mimeType);
        fileObject.setHash(hash);
        fileObject.setCreateTime(System.currentTimeMillis());
        fileObject.setUpdateTime(System.currentTimeMillis());
        
        log.debug("Created file object: {} -> {}", fileName, fileId);
        return fileObject;
    }

    @Override
    public FileObject getFileObjectByHash(String hash) {
        if (jsonStorage == null) {
                    return null;
        }
        return jsonStorage.load(hash, FileObject.class);
    }

    @Override
    public FileObject getFileObjectByID(String fileObjectId) {
        if (jsonStorage == null) {
                    return null;
        }
        return jsonStorage.load(fileObjectId, FileObject.class);
    }

    @Override
    public boolean deleteFileObject(String fileObjectId) {
        if (jsonStorage == null) {
                    return false;
        }
        jsonStorage.delete(fileObjectId);
        log.debug("Deleted file object: {}", fileObjectId);
        return true;
    }

    @Override
    public boolean updateFileObject(FileObject fileObject) {
        if (jsonStorage == null || fileObject == null) {
            return false;
        }
        jsonStorage.save(fileObject.getFileId(), fileObject);
        log.debug("Updated file object: {}", fileObject.getFileId());
        return true;
    }

    @Override
    public Integer writeLine(String fileObjectId, String line) {
        log.debug("WriteLine called for: {}", fileObjectId);
        return 0;
    }

    @Override
    public String readLine(String fileObjectId, Integer lineIndex) {
        log.debug("ReadLine called for: {}", fileObjectId);
        return null;
    }

    @Override
    public FileAdapter getFileAdapter() {
        return new LocalFileAdapter(basePath);
    }
}
