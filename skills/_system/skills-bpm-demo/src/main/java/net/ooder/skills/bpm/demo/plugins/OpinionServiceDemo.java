package net.ooder.skills.bpm.demo.plugins;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.ct.CtBPMCacheManager;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.common.JDSException;
import net.ooder.common.util.StringUtility;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bpm/demo/plugins/opinion/")
public class OpinionServiceDemo {

    @RequestMapping(method = RequestMethod.POST, value = "OpinionsList")
    @ResponseBody
    public ListResultModel<List<FileInfo>> getOpinionsList(String activityInstId, String activityInstHistoryId, String formname) {
        ListResultModel<List<FileInfo>> resultModel = new ListResultModel<List<FileInfo>>();
        try {
            if (activityInstId != null) {
                List<FileInfo> attachMents = new ArrayList<>();
                ActivityInst inst = getClient().getActivityInst(activityInstId);
                String processInstId = null;
                if (inst != null) {
                    processInstId = inst.getProcessInstId();
                }
                Folder processInstFolder = this.getProcessFolder(processInstId);
                if (!inst.isCanPerform()) {
                    if (activityInstHistoryId != null) {
                        Folder activityInstHisFolder = (Folder) getVfsClient().getFolderByPath(
                            processInstFolder.getPath() + activityInstId + "/" + activityInstHistoryId + "/" + formname + "/opinion");
                        if (activityInstHisFolder != null) {
                            List<FileInfo> fileInfos = activityInstHisFolder.getFileList();
                            attachMents.addAll(fileInfos);
                        }
                    }
                } else {
                    Folder activityInstFolder = getVfsClient().mkDir(processInstFolder.getPath() + activityInstId + "/" + formname + "/opinion");
                    attachMents.addAll(activityInstFolder.getFileList());
                    FileInfo fileInfo = getVfsClient().getFileByPath(activityInstFolder.getPath() + getClient().getConnectInfo().getLoginName());
                    if (fileInfo == null) {
                        fileInfo = getVfsClient().createFile(activityInstFolder.getPath() + getClient().getConnectInfo().getLoginName());
                        attachMents.add(fileInfo);
                    }
                }
                resultModel.setData(attachMents);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "saveOpinion")
    @ResponseBody
    public ResultModel<Boolean> saveOpinion(String path, String content) {
        ResultModel<Boolean> resultModel = new ResultModel<Boolean>();
        try {
            FileInfo fileInfo = CtVfsFactory.getCtVfsService().getFileByPath(path);
            CtVfsFactory.getCtVfsService().saveFileAsContent(fileInfo.getPath(), content, null);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "AddOpinion")
    @ResponseBody
    public ResultModel<FileInfo> addOpinion(String activityInstId, String formname) {
        ResultModel<FileInfo> resultModel = new ResultModel<FileInfo>();
        try {
            ActivityInst inst = CtBPMCacheManager.getInstance().getActivityInst(activityInstId);
            String path = getProcessFolder(inst.getProcessInstId()).getPath() + inst.getActivityInstId() + "/" + formname + "/opinion";
            Folder folder = CtVfsFactory.getCtVfsService().mkDir(path);
            FileInfo fileInfo = folder.createFile(getClient().getConnectInfo().getLoginName(), getClient().getConnectInfo().getUserID());
            resultModel.setData(fileInfo);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return resultModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "delOpinion")
    @ResponseBody
    public ResultModel<Boolean> delOpinion(String paths) {
        ResultModel<Boolean> resultModel = new ResultModel<Boolean>();
        String[] pathArr = StringUtility.split(paths, ";");
        try {
            for (String path : pathArr) {
                FileInfo file = CtVfsFactory.getCtVfsService().getFileByPath(path);
                CtVfsFactory.getCtVfsService().deleteFile(file.getID());
            }
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
        CtVfsService vfsClient = CtVfsFactory.getCtVfsService();
        return vfsClient;
    }

    private WorkflowClientService getClient() {
        WorkflowClientService client = EsbUtil.parExpression("$BPMC", WorkflowClientService.class);
        return client;
    }
}
