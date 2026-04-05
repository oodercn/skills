package net.ooder.vfs.business;

import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.config.UserBean;
import net.ooder.engine.ConnectInfo;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.OrgManagerFactory;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.FileVersion;
import net.ooder.vfs.Folder;
import net.ooder.vfs.VFSException;
import net.ooder.vfs.engine.VFSServer;
import net.ooder.vfs.service.VFSClient;
import net.ooder.vfs.service.VFSClientService;
import net.ooder.vfs.service.VFSDiskService;
import net.ooder.vfs.service.impl.VFSClientImpl;

@EsbBeanAnnotation(id = "VFSDiskService", name = "VFSDiskService服务", expressionArr = "VFSDiskServiceImpl()", desc = "VFSDiskService服务")
public class VFSDiskServiceImpl implements VFSDiskService {

    private VFSClient vfsClient;

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSDiskServiceImpl.class);

    public VFSDiskServiceImpl() {

    }


    @Override
    public ResultModel<Folder> mkDir(String path) {
        ResultModel<Folder> userStatusInfo = new ResultModel<Folder>();
        try {
            Folder folder = getVFSClient().mkDir(path);
            userStatusInfo.setData(folder);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<FileInfo> createFile(String path, String name) {
        ResultModel<FileInfo> userStatusInfo = new ResultModel<FileInfo>();

        try {
            FileInfo fileInfo = getVFSClient().createFile(path, name);

            userStatusInfo.setData(fileInfo);

        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Folder> getFolderByPath(String path) {
        ResultModel<Folder> userStatusInfo = new ResultModel<Folder>();

        try {
            Folder folder = getVFSClient().getFolderByPath(path);
            userStatusInfo.setData(folder);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<FileInfo> getFileInfoByPath(String path) {
        ResultModel<FileInfo> userStatusInfo = new ResultModel<FileInfo>();

        try {
            FileInfo fileInfo = getVFSClient().getFileByPath(path);
            userStatusInfo.setData(fileInfo);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Boolean> delete(String path) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {
            getVFSClient().delete(path);

        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            userStatusInfo.setData(false);
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }


    @Override
    public ResultModel<Boolean> cloneFolder(String spath, String tpaht) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().copyFolder(spath, tpaht, false);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }


    @Override
    public ResultModel<Boolean> copyFolder(String spath, String tpaht) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {
            getVFSClient().copyFolder(spath, tpaht);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<FileVersion> getVersionByPath(String path) {
        ResultModel<FileVersion> userStatusInfo = new ResultModel<FileVersion>();

        try {
            FileVersion version = getVFSClient().getVersionByPath(path);
            userStatusInfo.setData(version);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Boolean> updateFileInfo(String path, String name, String descrition) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFileInfo(path, name, descrition);
        } catch (VFSException e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }


    @Override
    public ResultModel<Boolean> updateFolderInfo(String path, String name, String descrition, FolderType type) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFolderInfo(path, name, descrition, type);
        } catch (VFSException e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Boolean> updateFolderState(String path, FolderState state) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFolderState(path, state);
        } catch (VFSException e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Boolean> copyFile(String path, String newpath) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().copyFile(path, newpath);
        } catch (VFSException e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<FileInfo> createFile2(String path, String name, String descrition) {
        ResultModel<FileInfo> userStatusInfo = new ResultModel<FileInfo>();

        try {


            FileInfo fileInfo = getVFSClient().createFile(path, name);
            if (descrition != null && !descrition.equals(name)) {
                getVFSClient().updateFileInfo(fileInfo.getPath(), name, descrition);
            }
            userStatusInfo.setData(fileInfo);

        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Folder> mkDir2(String path, String descrition, FolderType type) {
        ResultModel<Folder> userStatusInfo = new ResultModel<Folder>();
        try {
            Folder folder = getVFSClient().mkDir(path);
            if ((descrition != null && !descrition.equals(folder.getName()))
                    || (type != null && !folder.getFolderType().equals(type))) {
                getVFSClient().updateFolderInfo(path, folder.getName(), descrition, type);
            }

            userStatusInfo.setData(folder);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }


    @Override
    public ResultModel<FileVersion> createFileVersion(String path, String filehash) {
        ResultModel<FileVersion> userStatusInfo = new ResultModel<FileVersion>();
        try {
            FileVersion fileVersion = getVFSClient().createFileVersion(path, filehash);

            if (fileVersion != null) {
                userStatusInfo.setData(fileVersion);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel<FileVersion>();
            //   ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public VFSClient getVFSClient() {

        if (this.vfsClient == null) {
            try {
                JDSClientService client = (JDSClientService) EsbUtil.parExpression("$JDSC");
                vfsClient = VFSServer.getInstance().getVFSService(client);
            } catch (Exception e) {
                e.printStackTrace();
                // throw new Throwable("not login!", 1005);
            }

            if (vfsClient == null) {
                vfsClient = new VFSClientImpl();

                try {
                    Person person = OrgManagerFactory.getOrgManager().getPersonByAccount(UserBean.getInstance().getUsername());
                    ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
                    vfsClient.connect(connectInfo);
                } catch (JDSException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (PersonNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (vfsClient == null) {
            this.log.error("vfsClient [" + vfsClient + "]");
        }

        return vfsClient;
    }
}
