package net.ooder.vfs.manager.inner;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.JDSException;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.config.JDSConfig;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.*;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.bigfile.BigFileUtil;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.engine.VFSRoManager;
import net.ooder.vfs.manager.dbimpl.JdbcFileVersionManager;

import java.io.*;
import java.util.*;

public class DBFileVersion implements FileVersion, Cacheable, Serializable {

    private String fileId;
    private String versionID;
    private String versionName;
    private Integer index;
    private String personId;
    private ConfigCode subSystemId = OrgConstants.VFSCONFIG_KEY;
    private String fileObjectId;
    private String sourceId;
    private long createTime;
    private Set<String> fileIdViewList;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public DBFileVersion() {


    }


    @JSONField(serialize = false)
    public boolean fileIdViewList_is_initialized;

    boolean isModified = false;


    @JSONField(serialize = false)
    private static final int BUFFER_SIZE = 1024 * 2;


    @JSONField(serialize = false)
    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }


    public String getVersionID() {
        return versionID;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getIndex() {
        return index;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setFileObjectId(String fileObjectId) {
        this.fileObjectId = fileObjectId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }


    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(fileId);
        size += CacheSizes.sizeOfString(versionID);
        size += CacheSizes.sizeOfString(versionName);
        size += CacheSizes.sizeOfInt();
        size += CacheSizes.sizeOfString(personId);
        size += CacheSizes.sizeOfString(fileObjectId);
        size += CacheSizes.sizeOfString(sourceId);
        size += CacheSizes.sizeOfObject(sourceId);
        size += CacheSizes.sizeOfObject(fileIdViewList);

        return size;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }


    @JSONField(serialize = false)
    private void prepareFileView() {
        if (!fileIdViewList_is_initialized) {
            try {
                JdbcFileVersionManager.getInstance().loadView(this);
            } catch (VFSException e) {
                e.printStackTrace();
            }
        }
    }


    @JSONField(serialize = false)
    public void addFileView(String viewId) {
        if (fileIdViewList == null) {
            fileIdViewList = new LinkedHashSet();
            fileIdViewList_is_initialized = true;
        }
        this.fileIdViewList.add(viewId);
    }


    @JSONField(serialize = false)
    public List<FileView> getViews() {
        prepareFileView();
        if (fileIdViewList == null) {
            return new ArrayList();
        }
        List viewList = new ArrayList();
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        for (String viewId : fileIdViewList) {
            try {
                viewList.add(cacheManager.getFileViewByID(viewId));
            } catch (Exception ex) {
            }
        }
        Collections.sort(viewList, new Comparator<FileView>() {
            public int compare(FileView o1, FileView o2) {
                return o2.getFileIndex() - o1.getFileIndex();
            }
        });
        return viewList;
    }

    public Set<String> getViewIds() {
        prepareFileView();
        if (fileIdViewList == null) {
            fileIdViewList = new LinkedHashSet<String>();
        }

        return fileIdViewList;
    }


    @JSONField(serialize = false)
    public FileObject getFileObject() {
        FileObject fileobj = null;
        VFSRoManager cacheManager = VFSRoManager.getInstance();


        if (getFileObjectId() != null) {
            fileobj = cacheManager.getFileObjectByID(getFileObjectId());
        }

        return fileobj;
    }


    @Override
    public String getPath() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        String path = cacheManager.getFileInfoByID(this.getFileId()).getPath() + VFSConstants.URLVERSION + this.getIndex();
        return path;

    }

    public String getPersonId() {
        return personId;
    }

    public String getFileObjectId() {
        if (fileObjectId == null && versionID != null) {
            try {
                JdbcFileVersionManager.getInstance().loadById(versionID);
            } catch (VFSException e) {
                e.printStackTrace();
            }
        }
        return fileObjectId;
    }

    public Long getCreateTime() {
        return createTime;
    }


    @JSONField(serialize = false)
    public FileView createView(String fileObjectId, Integer fileIndex) {
        FileView view = VFSRoManager.getInstance().createFileView(this, fileObjectId, fileIndex);
        VFSRoManager.getInstance().updateView(view);
        return view;
    }


    @JSONField(serialize = false)
    public MD5InputStream getInputStream() {
        MD5InputStream input = null;
        String tempPath = JDSConfig.Config.tempPath().getPath() + File.separator + "md5hash" + File.separator;
        try {
            if (getViews().size() > 1) {
                List<String> localPaths = new ArrayList<>();
                List<FileView> viewList = getViews();
                for (FileView view : viewList) {
                    String blockPath = tempPath + view.getFileObject().getHash();
                    File file = new File(blockPath);
                    if (!file.exists()) {
                        try {
                            view.getFileObject().downLoad();
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }
                    }
                    localPaths.add(view.getFileObject().getPath());
                }
                BigFileUtil.mergeFiles(localPaths, tempPath + getFileObject().getHash());
                input = new MD5InputStream(new FileInputStream(new File(tempPath + getFileObject().getHash())));
            } else {


                FileObject object = this.getFileObject();
                if (object != null) {

                    input = this.getFileObject().downLoad();

                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return input;
    }


    @JSONField(serialize = false)
    public MD5OutputStream getOutputStream() {
        MD5OutputStream output = null;
        if (this.getFileObject() != null) {
            String vfsPath = this.getFileObject().getPath();
            FileAdapter fileAdapter = getFileAdapter();
            if (vfsPath != null && fileAdapter.exists(vfsPath)) {
                output = fileAdapter.getOutputStream(vfsPath);
            }
        }
        return output;
    }

    public String getFileName() {
        VFSRoManager cacheManager = VFSRoManager.getInstance();
        EIFileInfo file = cacheManager.getFileInfoByID(this.fileId);
        return file.getName();
    }

    public Set<String> getFileIdViewList() {
        return fileIdViewList;
    }

    public void setFileIdViewList(Set<String> fileIdViewList) {
        this.fileIdViewList = fileIdViewList;
    }


    @JSONField(serialize = false)
    public boolean isFileIdViewList_is_initialized() {
        return fileIdViewList_is_initialized;
    }

    public void setFileIdViewList_is_initialized(boolean fileIdViewList_is_initialized) {
        this.fileIdViewList_is_initialized = fileIdViewList_is_initialized;
    }

    public Long getLength() {
        Long length = 0L;
        if (this.getFileObject() == null || this.getFileObject().getPath() == null) {
            return 0L;
        }

        return this.getFileObject().getLength();
    }


    @JSONField(serialize = false)
    public void writeTo(final OutputStream outstream) {
        final MD5InputStream instream = this.getInputStream();
        try {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int l;
            while ((l = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @JSONField(serialize = false)
    private FileAdapter getFileAdapter() {
        FileAdapter fileAdapter = OrgConfig.getInstance(subSystemId).getFileAdapter(this.getFileObject().getRootPath());
        return fileAdapter;

    }


    @JSONField(serialize = false)
    public Integer writeLine(String str) {
        Integer ln = 0;
        try {
            ln = CtVfsFactory.getCtVfsService().writeLine(this.getFileObjectId(), str);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return ln;
    }


    @JSONField(serialize = false)
    public List<String> readLine(List<Integer> lineNums) {
        List<String> dataArr = new ArrayList<String>();
        if (this.getFileObject() != null) {
            String vfsPath = this.getFileObject().getPath();
            if (vfsPath != null && getFileAdapter().exists(vfsPath)) {
                dataArr = getFileAdapter().readLine(vfsPath, lineNums);
            }
        }
        return dataArr;
    }

    public void setIndex(Integer index) {
        this.index = index;

    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;

    }

}
