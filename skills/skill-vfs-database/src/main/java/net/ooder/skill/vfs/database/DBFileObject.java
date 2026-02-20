package net.ooder.skill.vfs.database;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.JDSException;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.adapter.FileAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DBFileObject implements FileObject, Cacheable, Serializable {

    private static final long serialVersionUID = 5284347777800353504L;

    private String ID;
    private String hash;
    private String name;
    private long length;
    private String adapter;
    private String rootPath;
    private String path;
    private long createTime;

    private transient boolean isModified = false;

    public DBFileObject() {
    }

    public String getID() {
        return ID;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public Long getLength() {
        return length;
    }

    @Override
    public void setLength(Long length) {
        this.length = length;
        this.isModified = true;
    }

    public String getPath() {
        return path;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public void setHash(String hash) {
        this.hash = hash;
        this.isModified = true;
    }

    public void setName(String name) {
        this.name = name;
        this.isModified = true;
    }

    public void setLength(long length) {
        this.length = length;
        this.isModified = true;
    }

    public void setPath(String path) {
        this.path = path;
        this.isModified = true;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public MD5InputStream downLoad() throws JDSException {
        MD5InputStream input = null;
        String vfsPath = getPath();
        FileAdapter fileAdapter = getFileAdapter();
        if (vfsPath != null && fileAdapter.exists(vfsPath)) {
            input = fileAdapter.getMD5InputStream(vfsPath);
        }
        return input;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }

    @JSONField(serialize = false)
    public Integer writeLine(String str) {
        Integer ln = -1;
        FileAdapter fileAdapter = getFileAdapter();
        String vfsPath = getPath();
        if (vfsPath != null && fileAdapter.exists(vfsPath)) {
            ln = fileAdapter.writeLine(vfsPath, str);
            setLength(fileAdapter.getLength(vfsPath));
            setHash(fileAdapter.getMD5Hash(vfsPath));
        }
        return ln;
    }

    @JSONField(serialize = false)
    public List<String> readLine(List<Integer> lineNums) {
        List<String> dataArr = new ArrayList<String>();
        String vfsPath = getPath();
        if (vfsPath != null && getFileAdapter().exists(vfsPath)) {
            dataArr = getFileAdapter().readLine(vfsPath, lineNums);
        }
        return dataArr;
    }

    @JSONField(serialize = false)
    private FileAdapter getFileAdapter() {
        return DatabaseVfsConfig.getInstance().getFileAdapter(rootPath);
    }

    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(ID);
        size += CacheSizes.sizeOfString(hash);
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfString(rootPath);
        size += CacheSizes.sizeOfString(adapter);
        size += CacheSizes.sizeOfObject(length);
        size += CacheSizes.sizeOfString(path);
        size += CacheSizes.sizeOfObject(createTime);
        return size;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }
}
