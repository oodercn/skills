/**
 * $RCSfile: DefaultDataEngine.java,v $
 * $Revision: 1.3 $
 * $Date: 2016/01/23 16:29:55 $
 * Copyright: Copyright (c) 2008
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
package net.ooder.bpm.engine;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessDefForm;

import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.ReturnType;
import net.ooder.common.Filter;

import java.util.List;
import java.util.Map;


public class DefaultDataEngine implements DataEngine {

    public DefaultDataEngine(String systemCode) {

    }

    public ReturnType abortProcessInst(String processInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType clearHistory(String activityInstHistoryID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType combineActivityInsts(String[] activityInstIds, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType completeProcessInst(String processInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map ctx) throws BPMException {
        // TODO Auto-generated method stub

    }

    @Override
    public ProcessDefForm getProcessDefForm(String processDefVersionID, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, String initType, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteProcessInst(String processInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType endRead(String activityInstID, String activityInstHistoryID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public List<FormClassBean> getActivityDefAllDataFormDef(String activityDefId, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormClassBean getActivityDefMainForm(String activityDefId, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataMap getActivityInstHistoryMapDAOMap(String activityInstHistoryId, String userId) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter getActivityInstListFilter(Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataMap getActivityInstMapDAOMap(String activityInstID, String userId) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FormClassBean> getProcessDefAllDataFormDef(String processDefID, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter getProcessDefListFilter(Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormClassBean getProcessDefMainForm(String processDefID, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter getProcessInstListFilter(Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataMap getProcessInstMapDAOMap(String processInstID) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSystemCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityInstFormValues(String activityInstID, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public void setWorkflowClient(WorkflowClientService service) {

    }

    public ReturnType resumeActivityInst(String activityInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType resumeProcessInst(String processInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeTo(String activityInstId, String activityDefId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void setSystemCode(String systemCode) {
        // TODO Auto-generated method stub

    }

    public ReturnType signReceive(String activityInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startActivityInst(String activityInstId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startProcessInst(String processInstId, String activityInstId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType suspendActivityInst(String activityInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType suspendProcessInst(String processInstID, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType tackBack(String activityInstId, Map ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void updateActivityInstHisMapDAO(String activityInstHistoryId, DataMap formdata, String userId) throws BPMException {
        // TODO Auto-generated method stub

    }

    public void updateActivityInstMapDAO(String activityInstId, DataMap formdata, String userId) throws BPMException {
        // TODO Auto-generated method stub

    }

    public void updateProcessInstMapDAO(String processInstId, DataMap formdata, String userId) throws BPMException {
        // TODO Auto-generated method stub

    }

    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map ctx) throws BPMException {
        // TODO Auto-generated method stub
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

}


