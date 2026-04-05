package net.ooder.skills.bpm.demo.plugins;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bpm/demo/plugins/attachment/")
public class AttachmentServiceDemo {

    @RequestMapping(method = RequestMethod.POST, value = "AttachmentList")
    @ResponseBody
    public ListResultModel<List<FileInfo>> getAttachmentList(String activityInstId, String formname) {
        ListResultModel<List<FileInfo>> resultModel = new ListResultModel<List<FileInfo>>();
        try {
            if (activityInstId != null) {
                List<FileInfo> attachMents = new ArrayList<>();
                ActivityInst inst = getClient().getActivityInst(activityInstId);
                String processInstId = inst.getProcessInstId();
                Folder processInstFolder = this.getProcessFolder(processInstId);
                
                Folder activityInstFolder = getVfsClient().mkDir(
                    processInstFolder.getPath() + activityInstId + "/" + formname + "/attachment");
                attachMents.addAll(activityInstFolder.getFileList());
                resultModel.setData(attachMents);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "UploadAttachment")
    @ResponseBody
    public ResultModel<FileInfo> uploadAttachment(String activityInstId, String formname, MultipartFile file) {
        ResultModel<FileInfo> resultModel = new ResultModel<FileInfo>();
        try {
            ActivityInst inst = getClient().getActivityInst(activityInstId);
            Folder processInstFolder = this.getProcessFolder(inst.getProcessInstId());
            Folder attachmentFolder = getVfsClient().mkDir(
                processInstFolder.getPath() + activityInstId + "/" + formname + "/attachment");
            
            FileInfo fileInfo = attachmentFolder.createFile(file.getOriginalFilename(), 
                getClient().getConnectInfo().getUserID());
            String content = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            getVfsClient().saveFileAsContent(fileInfo.getPath(), content, null);
            resultModel.setData(fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "DeleteAttachment")
    @ResponseBody
    public ResultModel<Boolean> deleteAttachment(String fileId) {
        ResultModel<Boolean> resultModel = new ResultModel<Boolean>();
        try {
            getVfsClient().deleteFile(fileId);
            resultModel.setData(true);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    private Folder getProcessFolder(String processInstId) throws JDSException {
        ProcessInst processInst = getClient().getProcessInst(processInstId);
        String processInstFolderId = processInst.getAttribute(ProcessInstAtt.ROOTFOLDERID.getType());
        Folder processInstFolder = null;
        if (processInstFolderId != null) {
            processInstFolder = (Folder) this.getVfsClient().getFolderById(processInstFolderId);
        }
        return processInstFolder;
    }

    private CtVfsService getVfsClient() {
        return CtVfsFactory.getCtVfsService();
    }

    private WorkflowClientService getClient() {
        return EsbUtil.parExpression("$BPMC", WorkflowClientService.class);
    }
}
