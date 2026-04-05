package net.ooder.vfs.manager.inner;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.vfs.VFSFolderNotFoundException;

import java.util.List;
import java.util.Set;

public interface EIFolder {

    @MethodChinaName(cname = "取得文件夹标识")
    public String getID();

    @MethodChinaName(cname = "取得文件夹名称")
    public String getName();

    @MethodChinaName(cname = "取得文件夹资源逻辑地址")
    public String getPath();

    @MethodChinaName(cname = "取得父文件夹的标识")
    public String getParentId();

    @MethodChinaName(cname = "取得文件夹创建人")
    public String getPersonId();

    @MethodChinaName(cname = "取得父文件夹对象")
    public EIFolder getParent();

    @MethodChinaName(cname = "取得当前所有父节点（递归）", display = false)
    public List<EIFolder> getAllParent();

    @MethodChinaName(cname = "取得该文件夹的所有直接子文件夹")
    public List<EIFolder> getChildrenList();

    @MethodChinaName(cname = "取得该文件夹的所有直接子文件夹的标识", display = false)
    public Set<String> getChildIdList();

    @MethodChinaName(cname = "取得该文件夹的所有子文件夹(递归)", display = false)
    public List<EIFolder> getChildrenRecursivelyList();

    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表")
    public List<EIFileInfo> getFileList();

    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表(递归)", display = false)
    public Set<String> getFileIdListRecursively();

    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表(递归)", display = false)
    public List<EIFileInfo> getFileListRecursively();

    @MethodChinaName(cname = "获取文件标示", display = false)
    public Set<String> getFileIdList();

    @MethodChinaName(cname = "设置文件标示", display = false)
    public void setID(String uid);

    @MethodChinaName(cname = "设置文件标示", display = false)
    public void setName(String name);

    @MethodChinaName(cname = "设置父节点", display = false)
    public void setParentId(String parentId);

    @MethodChinaName(cname = "获取人员id", display = false)
    public void setPersonId(String personId);

    @MethodChinaName(cname = "设置文件类型", display = false)
    public void setFolderType(FolderType Type);

    @MethodChinaName(cname = "获取文件类型", display = false)
    public FolderType getFolderType();

    @MethodChinaName(cname = "文件夹大小", display = false)
    public long getFolderSize();

    @MethodChinaName(cname = "设置大小", display = false)
    public void setFolderSize(long size);

    @MethodChinaName(cname = "排序", display = false)
    public int getOrderNum();

    @MethodChinaName(cname = "设置排序", display = false)
    public void setOrderNum(int orderNum);

    @MethodChinaName(cname = "是否删除", display = false)
    public int getRecycle();

    @MethodChinaName(cname = "状态", display = false)
    public void setState(FolderState state);

    @MethodChinaName(cname = "状态", display = false)
    public FolderState getState();

    @MethodChinaName(cname = "创建时间")
    public long getCreateTime();


    @MethodChinaName(cname = "描述")
    public String getDescrition();

    @MethodChinaName(cname = "系统描述")
    public String getSysid();

    public void setDescrition(String descrition);

    @MethodChinaName(cname = "增加子文件")
    public EIFileInfo createFile(String name, String createPersonId);

    @MethodChinaName(cname = "增加子文件")
    public EIFileInfo createFile(String name, String descrition, String createPersonId);

    @MethodChinaName(cname = "增加子文件夹")
    public EIFolder createChildFolder(String name, String descrition, String createPersonId) throws VFSFolderNotFoundException;


    @MethodChinaName(cname = "增加子文件夹")
    public EIFolder createChildFolder(String name, String createPersonId) throws VFSFolderNotFoundException;

    public String getActivityInstId();


    public void setActivityInstId(String activityInstId);

    public String getActivityInstHistoryId();

    public void setActivityInstHistoryId(String activityInstHistoryId);

    public void setIndex(int index);

    public int getIndex();

    public void setUpdateTime(long updateTime);

    public long getUpdateTime();

    public void addChildren(EIFolder folder);

    public boolean isModified();

    public void setModified(boolean b);

    public void setHit(int hit);

    public void addFileInfo(EIFileInfo info);

    public int getHit();

    public Set<String> getChildNameList();

    public void setChildNameList(Set<String> childNameList);

    public void setRecycle(int i);

}
