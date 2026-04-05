package net.ooder.vfs.manager.inner;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.engine.VFSRoManager;

import java.io.Serializable;

public class DBFileView implements FileView, Cacheable, Serializable, Comparable<DBFileView> {
    private static final Log logger = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), DBFileView.class);
    private static final long serialVersionUID = 8286699801512920515L;
    private String viewId;
    private int fileIndex;
    private String versionId;
    private int fileType;
    private String fileObjectId;
    private String name;
    private ConfigCode subSystemId = OrgConstants.VFSCONFIG_KEY;


    @JSONField(serialize = false)
    boolean isModified = false;


    @JSONField(serialize = false)
    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }

    public DBFileView() {

    }


    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(viewId);
        size += CacheSizes.sizeOfString(versionId);
        size += CacheSizes.sizeOfString(fileObjectId);
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfInt();
        size += CacheSizes.sizeOfInt();
        return size;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileObjectId() {
        return fileObjectId;
    }

    public void setFileObjectId(String fileObjectId) {
        this.fileObjectId = fileObjectId;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }


    @JSONField(serialize = false)
    public FileVersion getFileVersion() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        try {
            return cacheManager.getFileVersionByID(versionId);
        } catch (VFSException e) {
            e.printStackTrace();
        }
        return null;
    }


    public FileObject getFileObject() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        return cacheManager.getFileObjectByID(fileObjectId);
    }

    public int getFileType() {
        return this.fileType;
    }

    public void setID(String id) {
        this.viewId = id;
    }

    public String getID() {
        return viewId;
    }


    public String getPath() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        FileObject object = cacheManager.getFileObjectByID(fileObjectId);
        return object.getPath();
    }

    public int compareTo(DBFileView o) {
        return 0;
    }


    @JSONField(serialize = false)
    public MD5InputStream getInputStream() {
        String vfsPath = this.getFileObject().getPath();
        return getFileAdapter().getMD5InputStream(vfsPath);
    }


    @JSONField(serialize = false)
    private FileAdapter getFileAdapter() {
        FileAdapter fileAdapter = OrgConfig.getInstance(subSystemId).getFileAdapter(this.getFileObject().getRootPath());
        return fileAdapter;

    }


}
