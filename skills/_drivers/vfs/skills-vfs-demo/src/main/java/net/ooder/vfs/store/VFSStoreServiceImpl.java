package net.ooder.vfs.store;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.conf.OrgConstants;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.adapter.FileAdapter;
import net.ooder.vfs.service.VFSClientService;
import net.ooder.vfs.service.VFSStoreService;
import net.ooder.vfs.store.service.StoreService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "VFSStoreService", name = "VFSStoreServiceImpl服务", expressionArr = "VFSStoreServiceImpl()", desc = "VFSStoreServiceImpl服务")
public class VFSStoreServiceImpl implements VFSStoreService {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, VFSStoreServiceImpl.class);


    private StoreService vfsClient;

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSClientService.class);

    public VFSStoreServiceImpl() {

    }

    @Override
    public ResultModel<FileObject> getFileObjectByHash(String hash) {
        long time=System.currentTimeMillis();
        ResultModel<FileObject> userStatusInfo = new ResultModel<FileObject>();
        try {
            FileObject file = getVFSClient().getFileObjectByHash(hash);
            userStatusInfo.setData(file);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }


    @Override
    public ResultModel<FileAdapter> getFileAdapter() {
        ResultModel<FileAdapter> userStatusInfo = new ResultModel<FileAdapter>();
        try {
            FileAdapter adapter = getVFSClient().getFileAdapter();
            userStatusInfo.setData(adapter);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;

    }

    @Override
    public ResultModel<Boolean> deleteFileObject(String ID) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().deleteFileObject(ID);
        } catch (Throwable e) {
            userStatusInfo = new ErrorResultModel();
            e.printStackTrace();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    public ResultModel<Boolean> updateFileObject(FileObject fileObject) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getVFSClient().updateFileObject(fileObject);
        } catch (Throwable e) {
            userStatusInfo = new ErrorResultModel();
            e.printStackTrace();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<Integer> writeLine(String fileId, String json) {
        ResultModel<Integer> userStatusInfo = new ResultModel<Integer>();

        try {
            Integer linenum = getVFSClient().writeLine(fileId, json);
            userStatusInfo.setData(linenum);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<List<String>> readLine(String fileObjectId, List<Integer> lines) {
        ResultModel<List<String>> userStatusInfo = new ResultModel<List<String>>();
        try {
            FileObject object = this.getVFSClient().getFileObjectByID(fileObjectId);
            List<String> linenum = object.readLine(lines);
            userStatusInfo.setData(linenum);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<FileObject> getFileObjectByID(String id) {
        ResultModel<FileObject> userStatusInfo = new ResultModel<FileObject>();

        try {
            FileObject fileObject = getVFSClient().getFileObjectByID(id);
            userStatusInfo.setData(fileObject);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();

            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;

    }


    @Override
    public ResultModel<FileObject> createFileObject(MultipartFile file) {
        ResultModel<FileObject> userStatusInfo = new ResultModel<FileObject>();
        try {
            FileObject fileObject = getVFSClient().createFileObject(new MD5InputStream(file.getInputStream()));
            userStatusInfo.setData(fileObject);
        } catch (Throwable e) {
            userStatusInfo = new ErrorResultModel();
            e.printStackTrace();
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }


    @Override
    public ListResultModel<List<FileObject>> loadFileObjectList(String[] ids) {
        ListResultModel<List<FileObject>> userStatusInfo = new ListResultModel<List<FileObject>>();
        try {
            List<FileObject> objects = new ArrayList<FileObject>();
            for (String id : ids) {
                try {
                    FileObject object = getVFSClient().getFileObjectByID(id);
                    if (object != null) {
                        objects.add(object);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
            userStatusInfo.setData(objects);
        } catch (Throwable e) {
            e.printStackTrace();
            userStatusInfo = new ErrorListResultModel();

            ((ErrorListResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    @Override
    public ResultModel<InputStream> downLoadByHash(String hash) {
        ResultModel<InputStream> userStatusInfo = new ResultModel<InputStream>();
        InputStream inputStream = null;
        try {
            FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
            if (fileObject != null) {
                inputStream = fileObject.downLoad();
                if (inputStream != null) {

                    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    HttpServletRequest request = attr.getRequest();
                    HttpServletResponse response = attr.getResponse();


                    if (response==null){
                        response = (HttpServletResponse) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("ServletResponse");
                    }
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition", "filename=" + hash);
                    response.setHeader("Content-Length", String.valueOf(fileObject.getLength()));
                    long downloadedLength = 0l;
                    OutputStream os = response.getOutputStream();
                    byte[] b = new byte[2048];
                    int length;
                    while ((length = inputStream.read(b)) > 0) {
                        os.write(b, 0, length);
                        downloadedLength += b.length;
                    }
                    os.close();
                    inputStream.close();
                    this.log.info("end down [" + hash + "]");
                }


            } else {
                userStatusInfo.setData(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(JDSException.FORMNOTFONUD);
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());

        }
        return userStatusInfo;
    }


    public StoreService getVFSClient() {
        return EsbUtil.parExpression(StoreService.class);
    }

}
