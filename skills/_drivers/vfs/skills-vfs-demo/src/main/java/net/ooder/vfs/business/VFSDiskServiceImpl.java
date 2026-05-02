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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EsbBeanAnnotation(id = "VFSDiskService", name = "VFSDiskService服务", expressionArr = "VFSDiskServiceImpl()", desc = "VFSDiskService服务")
public class VFSDiskServiceImpl implements VFSDiskService {

    private VFSClient vfsClient;

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSDiskServiceImpl.class);
    private static final Logger slfLog = LoggerFactory.getLogger(VFSDiskServiceImpl.class);

    public VFSDiskServiceImpl() {
    }

    private ResultModel<Folder> handleFolder(FolderOperation op) {
        ResultModel<Folder> result = new ResultModel<>();
        try {
            result.setData(op.execute());
        } catch (Throwable e) {
            slfLog.error("VFSDiskService operation failed", e);
            ErrorResultModel err = new ErrorResultModel();
            err.setErrdes(e.getMessage());
            return err;
        }
        return result;
    }

    private <T> ResultModel<T> handleResult(ResultOperation<T> op) {
        ResultModel<T> result = new ResultModel<>();
        try {
            result.setData(op.execute());
        } catch (Throwable e) {
            slfLog.error("VFSDiskService operation failed", e);
            ErrorResultModel err = new ErrorResultModel();
            err.setErrdes(e.getMessage());
            return err;
        }
        return result;
    }

    private ResultModel<Boolean> handleBoolean(BooleanOperation op) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            op.execute();
        } catch (Throwable e) {
            slfLog.error("VFSDiskService operation failed", e);
            result = new ErrorResultModel();
            result.setData(false);
            if (e instanceof VFSException vfsEx) {
                ((ErrorResultModel) result).setErrcode(vfsEx.getErrorCode());
            }
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @FunctionalInterface
    interface FolderOperation { Folder execute() throws Exception; }
    @FunctionalInterface
    interface ResultOperation<T> { T execute() throws Exception; }
    @FunctionalInterface
    interface BooleanOperation { void execute() throws Exception; }

    @Override
    public ResultModel<Folder> mkDir(String path) {
        return handleFolder(() -> getVFSClient().mkDir(path));
    }

    @Override
    public ResultModel<FileInfo> createFile(String path, String name) {
        return handleResult(() -> getVFSClient().createFile(path, name));
    }

    @Override
    public ResultModel<Folder> getFolderByPath(String path) {
        return handleFolder(() -> getVFSClient().getFolderByPath(path));
    }

    @Override
    public ResultModel<FileInfo> getFileInfoByPath(String path) {
        return handleResult(() -> getVFSClient().getFileByPath(path));
    }

    @Override
    public ResultModel<Boolean> delete(String path) {
        return handleBoolean(() -> getVFSClient().delete(path));
    }

    @Override
    public ResultModel<Boolean> cloneFolder(String spath, String tpaht) {
        return handleBoolean(() -> getVFSClient().copyFolder(spath, tpaht, false));
    }

    @Override
    public ResultModel<Boolean> copyFolder(String spath, String tpaht) {
        return handleBoolean(() -> getVFSClient().copyFolder(spath, tpaht));
    }

    @Override
    public ResultModel<FileVersion> getVersionByPath(String path) {
        return handleResult(() -> getVFSClient().getVersionByPath(path));
    }

    @Override
    public ResultModel<Boolean> updateFileInfo(String path, String name, String descrition) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFileInfo(path, name, descrition);
        } catch (VFSException e) {
            slfLog.error("updateFileInfo failed: path={}", path, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> updateFolderInfo(String path, String name, String descrition, FolderType type) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFolderInfo(path, name, descrition, type);
        } catch (VFSException e) {
            slfLog.error("updateFolderInfo failed: path={}", path, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> updateFolderState(String path, FolderState state) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFolderState(path, state);
        } catch (VFSException e) {
            slfLog.error("updateFolderState failed: path={}", path, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> copyFile(String path, String newpath) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getVFSClient().copyFile(path, newpath);
        } catch (VFSException e) {
            slfLog.error("copyFile failed: {} -> {}", path, newpath, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<FileInfo> createFile2(String path, String name, String descrition) {
        ResultModel<FileInfo> result = new ResultModel<FileInfo>();
        try {
            FileInfo fileInfo = getVFSClient().createFile(path, name);
            if (descrition != null && !descrition.equals(name)) {
                getVFSClient().updateFileInfo(fileInfo.getPath(), name, descrition);
            }
            result.setData(fileInfo);
        } catch (Throwable e) {
            slfLog.error("createFile2 failed: path={}", path, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Folder> mkDir2(String path, String descrition, FolderType type) {
        ResultModel<Folder> result = new ResultModel<Folder>();
        try {
            Folder folder = getVFSClient().mkDir(path);
            if ((descrition != null && !descrition.equals(folder.getName()))
                    || (type != null && !folder.getFolderType().equals(type))) {
                getVFSClient().updateFolderInfo(path, folder.getName(), descrition, type);
            }
            result.setData(folder);
        } catch (Throwable e) {
            slfLog.error("mkDir2 failed: path={}", path, e);
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<FileVersion> createFileVersion(String path, String filehash) {
        ResultModel<FileVersion> result = new ResultModel<FileVersion>();
        try {
            FileVersion fileVersion = getVFSClient().createFileVersion(path, filehash);
            if (fileVersion != null) {
                result.setData(fileVersion);
            }
        } catch (Throwable e) {
            slfLog.error("createFileVersion failed: path={}", path, e);
            result = new ErrorResultModel<FileVersion>();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public VFSClient getVFSClient() {
        if (this.vfsClient == null) {
            try {
                JDSClientService client = (JDSClientService) EsbUtil.parExpression("$JDSC");
                vfsClient = VFSServer.getInstance().getVFSService(client);
            } catch (Exception e) {
                slfLog.warn("Failed to get VFSClient from JDSC, falling back to local", e);
            }

            if (vfsClient == null) {
                vfsClient = new VFSClientImpl();
                try {
                    Person person = OrgManagerFactory.getOrgManager().getPersonByAccount(UserBean.getInstance().getUsername());
                    ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
                    vfsClient.connect(connectInfo);
                } catch (JDSException | PersonNotFoundException e) {
                    slfLog.error("Failed to initialize VFSClient", e);
                }
            }
        }
        if (vfsClient == null) {
            this.log.error("vfsClient is null - VFS operations will fail");
        }
        return vfsClient;
    }
}
