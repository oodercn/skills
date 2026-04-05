package net.ooder.bpm.engine;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSActionContext;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.vfs.Folder;
import net.ooder.vfs.VFSFolderNotFoundException;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;

import java.util.List;
import java.util.Map;

public class VFSFileEngine implements FileEngine {


    private String sysCode;

    VFSFileEngine(String sysCode) {
        this.sysCode = sysCode;
    }

    VFSFileEngine() {

    }

    private WorkflowClientServiceImpl workflowClient = null;

    public void setWorkflowClient(WorkflowClientServiceImpl workflowClient) {
        this.workflowClient = workflowClient;
    }

    public ReturnType abortProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType completeProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType copyTo(List<ActivityInst> eiActivityInst, List<String> personIds) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType endRead(String activityInstId, String activityHistoryInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType resumeActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType resumeProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    private boolean contains(String userId, String[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return false;
        }
        for (String id : userIds) {
            if (userId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
//        WorkflowClientService client = getWorkFlowClient();
//        ActivityInst act = client.getActivityInst(activityInstId);
//        act.setAttribute(ActivityInstHistoryAtt.HISTORYID, activityInstHistoryId);
//        Person person = getPerson();
//        ProcessInst processInst = act.getProcessInst();
//        String processInstFolderId = act.getProcessInst().getAttribute(ProcessInstAtt.ROOTFOLDERID);
//        Folder processInstFolder;
//        try {
//            processInstFolder = (Folder) this.getVfsClient().getFolderById(processInstFolderId);
//            Folder activityInstFolder = (Folder) getVfsClient().getFolderByPath(processInstFolder.getPath() + activityInstId);
//
//            Folder activityInstHisFolder = (Folder) processInstFolder.createChildFolder(activityInstHistoryId, person.getID());
//            getVfsClient().copyFolder(activityInstFolder.getPath(), activityInstHisFolder.getPath());
//        } catch (JDSException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType signReceive(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException {

//        WorkflowClientService client = getWorkFlowClient();
//        ActivityInst act = client.getActivityInst(activityInstId);
//
//        String processInstFolderId = act.getProcessInst().getAttribute(ProcessInstAtt.ROOTFOLDERID);
//
//        try {
//            Folder processInstFolder = getVfsClient().getFolderById(processInstFolderId);
//            Folder activityInstFolder = getVfsClient().getFolderByPath(processInstFolder.getPath() + activityInstId);
//
//            for (int k = 0; k < subActivityInstIds.length; k++) {
//                String sActivityInstId = subActivityInstIds[k];
//                Folder attachmentFolder = (Folder) ((Folder) processInstFolder).createChildFolder(sActivityInstId, client.getConnectInfo().getUserID());
//                attachmentFolder.createChildFolder("embededFolder", client.getConnectInfo().getUserID());
//
//                getVfsClient().copyFolder(activityInstFolder.getPath(), attachmentFolder.getPath());
//
//            }
//        } catch (JDSException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public Person getPerson() {
        WorkflowClientService client = getWorkFlowClient();
        String personId = client.getConnectInfo().getUserID();
        Person person = null;
        try {
            person = client.getOrgManager().getPersonByID(personId);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }
        return person;
    }

    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {

//        WorkflowClientService client = getWorkFlowClient();
//        ActivityInst act = client.getActivityInst(activityInstId);
//
//        Person person = getPerson();
//
//        Folder rootFolder = null;
//        try {
//            rootFolder = (Folder) getCPersonFolder(person);
//        } catch (VFSFolderNotFoundException e) {
//            e.printStackTrace();
//        }
//        Folder processFolder = rootFolder.createChildFolder(processInstId, person.getID());
//
//        Folder activityFolder = (Folder) ((Folder) processFolder).createChildFolder(activityInstId, person.getID());
//
//        Folder embeddedFolder = activityFolder.createChildFolder("document", person.getID());
//
//        List<FormClassBean> formList = act.getActivityDef().getAllDataFormDef();
//        for (int k = 0; formList.size() > k; k++) {
//            FormClassBean formClassBean = formList.get(k);
//            Folder formFolder = activityFolder.createChildFolder(formClassBean.getId(), person.getID());
//
//        }
//
//        ProcessInst processInst = client.getProcessInst(processInstId);
//        processInst.setAttribute(ProcessInstAtt.ROOTFOLDERID, processFolder.getID());

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public Folder getCPersonFolder(Person person) throws VFSFolderNotFoundException {

        String cmailPath = OrgConstants.CMAILROOTPATH;

        Folder cmailFolder = null;
        try {


            if (cmailFolder == null) {
                Folder parentfolder = this.getVfsClient().mkDir(OrgConstants.DISKROOTPATH);
                if (cmailFolder == null) {
                    cmailFolder = ((Folder) parentfolder).createChildFolder(person.getID(), person.getID());
                }

            }
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cmailFolder;
    }

    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType tackBack(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void updateActivityInstHisMapDAO(String activityInstHistoryId, DataMap formdata, String userId) throws BPMException {
    }

    public void updateActivityInstMapDAO(String activityInstId, DataMap formdata, String userId) throws BPMException {
    }

    public void updateProcessInstMapDAO(String processInstId, DataMap formdata, String userId) throws BPMException {

    }

    private WorkflowClientService getWorkFlowClient() {
        WorkflowClientService workflowClient = null;

        JDSSessionFactory sessionFactory = new JDSSessionFactory(JDSActionContext.getActionContext());
        try {
            JDSClientService jdsclient = sessionFactory.getClientService(JDSServer.getClusterClient().getSystem(this.getSystemCode()).getConfigname());
            workflowClient = BPMServer.getInstance().getWorkflowService(jdsclient);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return workflowClient;
    }

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void copyActivityInstByHistory(String activityInstId, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {

        String personId = getWorkFlowClient().getConnectInfo().getUserID();
        WorkflowClientService client = getWorkFlowClient();
        Folder mailFolder = null;
        // mailFolder = this.getVfsClient().getFolderById(activityInstHistoryID);
        // ActivityInst actInsts = client.getActivityInst(activityInstId);
        // ProcessInst processInst = actInsts.getProcessInst();
        // String forward = (String) ctx.get("forward");
        // if (mailFolder != null) {
        // // initFolder(processInst.getAttribute("rootFolderId"), activityInstId, personId);
        // // 回复
        // if ("yes".equals(forward)) {
        // String limitFileIds = (String) ctx.get("limitFileIds");
        // List<String> limitFileIdList = new ArrayList<String>();
        // if (!"".equals(limitFileIds)) {
        // String[] limits = limitFileIds.split(";");
        // for (String fileId : limits) {
        // limitFileIdList.add(fileId);
        // }
        // }
        // try {
        // this.getVfsClient(). copyFolder(activityInstHistoryID, activityInstId, limitFileIdList);
        // } catch (VFSException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // }
    }

    @Override
    public void setWorkflowClient(WorkflowClientService service) {

    }

    public ReturnType endTask(String activityInstID, String activityHistoryId, Map<RightCtx, Object> ctxRight) {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public CtVfsService getVfsClient() {

        CtVfsService vfsClient = CtVfsFactory.getCtVfsService();
        return vfsClient;
    }

    @Override
    public void setSystemCode(String systemCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getSystemCode() {
        // TODO Auto-generated method stub
        return sysCode;
    }
}


