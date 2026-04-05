package net.ooder.vfs.manager.inner;

import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.annotation.MethodChinaName;
import net.ooder.vfs.FileLink;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.FileView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public interface EIFileInfo {

    @MethodChinaName(cname = "取得文件标识")
    public String getID();

    @MethodChinaName(cname = "取得文件名称")
    public String getName();

    @MethodChinaName(cname = "取得文件路径")
    public String getPath();

    @MethodChinaName(cname = "获取文件上传者")
    public String getPersonId();

    @MethodChinaName(cname = "获取文件类型")
    public int getFileType();

    @MethodChinaName(cname = "设置文件标识")
    public void setID(String uid);

    @MethodChinaName(cname = "设置文件名称")
    public void setName(String name);

    @MethodChinaName(cname = "设置文件路径")
    public void setPath(String path);

    @MethodChinaName(cname = "设置文件上传者")
    public void setPersonId(String personId);

    @MethodChinaName(cname = "设置文件类型")
    public void setFileType(int fileType);

    @MethodChinaName(cname = "文件创建时间")
    public Long getCreateTime();

    public void setCreateTime(long createTime);

    @MethodChinaName(cname = "文件描述")
    public String getDescrition();

    public void setDescrition(String descrition);

    public void setOldFolderId(String oldFolderId);

    public void setRight(String maxRight);

    public String getRight();

    @MethodChinaName(cname = "取得该文件所属所有文件夹")
    public EIFolder getFolder();

    public void setFolderId(String folderId);

    public String getFolderId();


    @MethodChinaName(cname = "取得文件所有版本信息")
    public List<FileVersion> getVersionList();

    @MethodChinaName(cname = "取得文件所有版本信息")
    public List<String> getVersionIds();

    @MethodChinaName(cname = "取得当前版本")
    public FileVersion getCurrentVersion();

    public String getCurrentVersonId();

    @MethodChinaName(cname = "取得当前版本文件hash")
    public String getCurrentVersonFileHash();

    @MethodChinaName(cname = "取得当前版本文件长度")
    public Long getCurrentVersonFileLength();

    @MethodChinaName(cname = "取得当前版本文件path")
    public Long getCurrentVersonFileCreateTime();

    @MethodChinaName(cname = "取得当前版本文件输入流")
    public MD5InputStream getCurrentVersonInputStream();

    @MethodChinaName(cname = "取得当前版本文件输出流")
    public MD5OutputStream getCurrentVersonOutputStream();

    @MethodChinaName(cname = "系统盘ID")
    public String getSysid();

    @MethodChinaName(cname = "取得当前活动实例Id")
    public String getActivityInstId();

    public void setActivityInstId(String ActivityInstId);

    @MethodChinaName(cname = "取得当前活动历史Id")
    public String getHistroyId();

    public void setHistroytId(String histroyId);

    @MethodChinaName(cname = "取得当前视图")
    public List<FileView> getCurrentViews();

    @MethodChinaName(cname = "获取文件链接")
    public List<FileLink> getLinks();


    public void addFileVersion(String versonId);

    public void addFileLink(String linkId);


    public int getIsRecycled();


    public void setIsRecycled(int isRecycled);

    public FileVersion createFileVersion();

    public void writeTo(final OutputStream outstream) throws IOException;


    public void setUpdateTime(long updateTime);

    public long getUpdateTime();

    public void setModified(boolean b);

    public void setIsLocked(int isLocked);

    public void setFileId(String fileId);

    public boolean isModified();

    public boolean isInitialized();

    public void setInitialized(boolean initialized);

    public boolean isFileIdLinkList_is_initialized();


    public void setFileIdLinkList_is_initialized(
            boolean fileIdLinkList_is_initialized);

    public boolean isFileIdVersionList_is_initialized();


    public void setFileIdVersionList_is_initialized(
            boolean fileIdVersionList_is_initialized);


    public int getIsLocked();

}
