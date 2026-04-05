/**
 * $RCSfile: VfsDataEngine.java,v $
 * $Revision: 1.4 $
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

import net.ooder.bpm.client.data.DataConstants;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.engine.data.FormClassFactory;
import net.ooder.bpm.engine.data.LuceneDataMap;
import net.ooder.bpm.engine.database.form.DbProcessDefForm;
import net.ooder.bpm.engine.database.form.DbProcessDefFormManager;
import net.ooder.bpm.engine.database.right.DbActivityDefRightManager;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.ProcessDefFormProxy;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.Filter;
import net.ooder.common.util.StringUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VfsDataEngine implements DataEngine {

    private static VfsDataEngine mapDAODataEngine;
    public EIProcessDefManager processDefMgr = null;

    public EIProcessDefVersionManager processDefVerMgr = null;

    public EIProcessDefVersionManager processDefVersionManager = null;

    public DbProcessDefFormManager processDefFormManager = null;

    public EIActivityDefManager activityDefMgr = null;

    public DbActivityDefRightManager activityRightMgr = null;

    public EIRouteDefManager routeDefMgr = null;

    public EIProcessInstManager processInstMgr = null;

    public EIActivityInstManager activityInstMgr = null;

    public EIActivityInstHistoryManager activityInstHistoryMgr = null;

    public EIRouteInstManager routeInstMgr = null;
    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, VfsDataEngine.class);

    private String systemCode;

    public VfsDataEngine(String systemCode) throws BPMException {
        this.systemCode = systemCode;
        processDefMgr = EIProcessDefManager.getInstance();
        processDefVerMgr = EIProcessDefVersionManager.getInstance();
        activityDefMgr = EIActivityDefManager.getInstance();
        routeDefMgr = EIRouteDefManager.getInstance();
        processInstMgr = EIProcessInstManager.getInstance();
        activityInstMgr = EIActivityInstManager.getInstance();
        activityInstHistoryMgr = EIActivityInstHistoryManager.getInstance();
        routeInstMgr = EIRouteInstManager.getInstance();
        processDefFormManager = DbProcessDefFormManager.getInstance();

    }

    public static VfsDataEngine getInstance(String systemCode) throws BPMException {

        if (mapDAODataEngine == null) {
            synchronized (VfsDataEngine.class) {
                if (mapDAODataEngine == null) {
                    mapDAODataEngine = new VfsDataEngine(systemCode);
                }
            }
        }

        return mapDAODataEngine;
    }

    private boolean hasId(List<FormClassBean> activityFormList, String id) {

        boolean hasId = false;
        for (int k = 0; k < activityFormList.size(); k++) {
            FormClassBean activityForm = activityFormList.get(k);
            String formId = activityForm.getId();
            if (formId.equals(id)) {
                return true;
            }
        }
        return hasId;

    }

    private List compForm(List<FormClassBean> activityFormList, List<FormClassBean> prcoessFormList) {
        List formList = activityFormList;

        for (int k = 0; k < prcoessFormList.size(); k++) {
            FormClassBean processForm = prcoessFormList.get(k);
            String processFormId = processForm.getId();
            boolean hasId = hasId(activityFormList, processFormId);
            if (!hasId) {
                formList.add(processForm);
            }
        }
        return formList;

    }

    public List<FormClassBean> getActivityDefAllDataFormDef(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {

        List<FormClassBean> formList = (List<FormClassBean>) JDSActionContext.getActionContext().getContext().get("visForm[" + activityDefId + "]");

        if (formList == null) {
            EIActivityDef activityDef = activityDefMgr.loadByKey(activityDefId);
            EIProcessDef pdf = activityDef.getProcessDef();
            formList = new ArrayList<FormClassBean>();
            //    String path = OrgConstants.WORKFLOWBASEPATH+ OrgConstants.WORKFLOWFORMPATH+ activityDef.getProcessDefVersionId() + "/" + activityDef.getActivityDefId() + "/";

            //  formList = getVfsFormClassBeanListByPath(path, formList);
            JDSActionContext.getActionContext().getContext().put("visForm[" + activityDefId + "]", formList);

            List processDefForm = this.getProcessDefAllDataFormDef(activityDef.getProcessDefId(), ctx);
            formList = compForm(formList, processDefForm);
        }

        return formList;
    }

    private String formatFilePath(String path) {
        if (!"/".equals(File.separator)) {
            path = StringUtility.replace(path, "/", File.separator);
        } else {
            path = StringUtility.replace(path, "\\", File.separator);
        }
        return path;
    }

    private List<FormClassBean> getVfsFormClassBeanListByPath(String path, List<FormClassBean> formList) {


        try {
            Folder folder = getVfsClient().getFolderByPath(path);

            if (folder != null) {
                List<FileInfo> files = folder.getFileList();
                for (int k = 0; k < files.size(); k++) {
                    FileInfo fileInfo = files.get(k);
                    FormClassBean formClassBean = null;
                    try {
                        formClassBean = FormClassFactory.getInstance().vfsfile2FormClassbean(fileInfo.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formClassBean != null) {
                        formList.add(formClassBean);
                    }

                }

            }
        } catch (JDSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return formList;
    }

    private Integer indexOf(List<FormClassBean> formList, FormClassBean formClassBean) {
        for (int k = 0; k < formList.size(); k++) {
            FormClassBean form = formList.get(k);
            if (form.getName().equals(formClassBean.getName())) {
                return k;
            }
        }
        return -1;

    }

    public FormClassBean getActivityDefMainForm(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {

        FormClassBean formClassBean = new FormClassBean();

        EIActivityDef activityDef = activityDefMgr.loadByKey(activityDefId);

        String mdforms = null;
        if ((activityDef.getAttributeInterpretedValue("APPLICATION.VFSFORM")) != null) {
            mdforms = activityDef.getAttributeInterpretedValue("APPLICATION.VFSFORM").toString();
        }
        String[] formsArr = null;
        if (mdforms != null) {
            formsArr = mdforms.split(":");
            for (int k = 0; formsArr.length > k; k++) {

                try {
                    String value = activityDef.getAttributeInterpretedValue("APPLICATION.VFSFORM." + formsArr[k]).toString();
                    formClassBean.setId(formsArr[k]);
                    formClassBean.setName(value.split(";")[0]);
                    formClassBean.setExperss(value.split(";")[1]);

                } catch (Exception e) {
                    throw new BPMException(e);
                }

            }
        }

        // String mainFormId = this.getActivityDefMaoDaoMainFormId(activityDefId, ctx);
        // FormClassBean formClassBean = null;
        // List<FormClassBean> formList = (List<FormClassBean>) getActivityDefAllDataFormDef(activityDefId, null);
        // if (formList.size() > 0) {
        // formClassBean = formList.get(0);
        // for (int k = 0; k < formList.size(); k++) {
        // FormClassBean classBean = formList.get(k);
        // if (classBean.getId().equals(mainFormId)) {
        // return classBean;
        // }
        // }
        // }

        return formClassBean;

    }

    @Override
    public ProcessDefForm getProcessDefForm(String processDefVersionID, Map<RightCtx, Object> ctx) throws BPMException {
        DbProcessDefForm dbDefForm = processDefFormManager.loadByKey(processDefVersionID);
        ProcessDefForm defForm = new ProcessDefFormProxy(dbDefForm, this.systemCode);
        return defForm;
    }

    public FormClassBean getProcessDefMainForm(String processDefID, Map<RightCtx, Object> ctx) throws BPMException {
        FormClassBean formClassBean = null;
        EIProcessDef processDef = processDefMgr.loadByKey(processDefID);
        String mainFormId = null;
        if ((processDef.getActiveProcessDefVersion().getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.MAINBEAN)) != null) {
            mainFormId = processDef.getActiveProcessDefVersion().getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.MAINBEAN).toString();
        }

        List<FormClassBean> formList = (List<FormClassBean>) this.getProcessDefAllDataFormDef(processDefID, null);

        if (formList.size() > 0) {
            formClassBean = formList.get(0);
            for (int k = 0; k < formList.size(); k++) {
                FormClassBean classBean = formList.get(k);
                if (classBean.getId().equals(mainFormId)) {
                    return classBean;
                }
            }
        }

        return formClassBean;

    }

    private DataMap getNewDataMap(Object source, String userId) throws BPMException {
        LuceneDataMap dataMap = new LuceneDataMap(source, userId, this.systemCode);
        return dataMap;
    }

    private DataMap getActivityInstMapDAOMapFormDb(String activityInstID, String userId) throws BPMException {
        EIActivityInst eiActivityInst = this.activityInstMgr.loadByKey(activityInstID);
        return this.getNewDataMap(eiActivityInst, userId);
    }

    private DataMap getActivityInstHistoryMapDAOMapFormDb(String activityInstHistoryId, String userId) throws BPMException {
        EIActivityInstHistory his = activityInstHistoryMgr.loadByKey(activityInstHistoryId);
        return this.getNewDataMap(his, userId);
    }

    private DataMap getProcessInstMapDAOMapFormDb(String processInstId, String userId) throws BPMException {
        EIProcessInst eiProcessInst = processInstMgr.loadByKey(processInstId);
        return this.getNewDataMap(eiProcessInst, null);
    }

    public List<FormClassBean> getProcessDefAllDataFormDef(String processDefID, Map<RightCtx, Object> ctx) throws BPMException {

        List<FormClassBean> formList = new ArrayList<FormClassBean>();
        String mdforms = null;
        EIProcessDef processDef = this.processDefMgr.loadByKey(processDefID);
        EIProcessDefVersion processDefVersion = processDef.getActiveProcessDefVersion();
        if (processDefVersion != null) {
            if ((processDefVersion.getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.JDSFORM)) != null) {
                mdforms = processDefVersion.getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.JDSFORM).toString();
            }
            String[] formsArr = null;
            if (mdforms != null) {
                formsArr = mdforms.split(":");
                for (int k = 0; formsArr.length > k; k++) {
                    FormClassBean formClassBean = null;
                    try {

                        formClassBean = FormClassFactory.getInstance().getFormClassBeanInst(formsArr[k]);

                    } catch (Exception e) {
                        throw new BPMException(e);
                    }
                    if (formClassBean != null) {
                        formList.add(formClassBean);
                    }

                }
            }
        }


//        String path = OrgConstants.WORKFLOWBASEPATH + OrgConstants.WORKFLOWFORMPATH + processDefVersion.getProcessDefVersionId() + "/";
//        formList = getVfsFormClassBeanListByPath(path, formList);
        return formList;
    }

    public void updateProcessInstMapDAO(String processInstID, DataMap formdata, String userId) throws BPMException {

        LuceneDataMap datamap = (LuceneDataMap) formdata;
        if (datamap.getSource() instanceof EIProcessInst) {
            datamap.getDataFactory().update(datamap);
        } else {
            LuceneDataMap processDatamap = (LuceneDataMap) this.getProcessInstMapDAOMap(processInstID);
            processDatamap.getDataFactory().update(datamap);

        }

    }

    public void updateActivityInstMapDAO(String activityInstID, DataMap formdata, String userId) throws BPMException {

        LuceneDataMap datamap = (LuceneDataMap) formdata;
        datamap.getDataFactory().update(datamap);

    }

    public void updateActivityInstHisMapDAO(String activityInstHistoryId, DataMap formdata, String userId) throws BPMException {

        LuceneDataMap datamap = (LuceneDataMap) formdata;
        LuceneDataMap hismap = (LuceneDataMap) this.getActivityInstHistoryMapDAOMap(activityInstHistoryId, userId);
        hismap.getDataFactory().update(datamap);

    }

    /**
     * 为兼容就有MAPDAO程序处理
     *
     * @param activityDefId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<FormClassBean> getActivityDefAllMapDaoDataFormDef(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        List<FormClassBean> formList = new ArrayList<FormClassBean>();
        String mdforms = null;
        EIActivityDef activityDef = this.activityDefMgr.loadByKey(activityDefId);
        if ((activityDef.getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.JDSFORM)) != null) {
            mdforms = activityDef.getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.JDSFORM).toString();
        }
        String[] formsArr = null;
        if (mdforms != null) {
            formsArr = mdforms.split(":");
            for (int k = 0; formsArr.length > k; k++) {
                FormClassBean formClassBean = null;
                try {
                    formClassBean = FormClassFactory.getInstance().getFormClassBeanInst(formsArr[k]);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BPMException(e);
                }

            }
        }

        return formList;
    }

    /**
     * @param activityDefId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public String getActivityDefMaoDaoMainFormId(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        EIActivityDef activityDef = this.activityDefMgr.loadByKey(activityDefId);
        String mainFormId = "MAINFORM";
        if ((activityDef.getAttributeValue("APPLICATION" + "." + DataConstants.MAINBEAN)) != null) {
            mainFormId = activityDef.getAttributeInterpretedValue("APPLICATION" + "." + DataConstants.MAINBEAN).toString();
        }

        return mainFormId;

    }

    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);
        EIActivityInstHistory[] activityInstHistorys = activityInstHistoryMgr.loadByProcessInstId(processInstID);
        if (ctx != null && ctx.get(RightCtx.CONTEXT_ACTIVITYINSTHISTORY) != null) {
            LuceneDataMap datamap = (LuceneDataMap) this.getActivityInstHistoryMapDAOMap((String) ctx.get(RightCtx.CONTEXT_ACTIVITYINSTHISTORY), userId);
            this.updateProcessInstMapDAO(processInstID, datamap, userId);
        }

        // LuceneDataMap datamap=(LuceneDataMap)getProcessInstMapDAOMapFormDb( processInstID, userId);

        // try {
        // if (datamap.getSource() instanceof EIProcessInst){
        // datamap.getDataFactory().update(datamap);
        // }else{
        // LuceneDataMap processDatamap= (LuceneDataMap) this.getProcessInstMapDAOMap(processInstID);
        // processDatamap.getDataFactory().update(datamap);
        // }
        // } catch (MapDAOException e) {
        // e.printStackTrace();
        // new BPMException(e);
        // }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    //
    // public String getTableName(String processInstId) throws BPMException{
    // EIProcessInst eiProcessInst=this.processInstMgr.loadByKey(processInstId);
    // String tableName=eiProcessInst.getAttributeValue(Attribute.TYPE_APPLICATION+"."+"MAPDAOTABLENAME");
    //
    // if (tableName==null ||tableName.equals("")){
    //
    // tableName= new MapDaoDataDAO().getTableName();
    // EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
    // .createAttributeInst();
    // eiAttributeInst.setId(UUIDGenerator.genUUID());
    // eiAttributeInst.setName("MAPDAOTABLENAME");
    // eiAttributeInst.setInterpretedValue(tableName);
    // eiProcessInst.setAttribute(Attribute.TYPE_APPLICATION, eiAttributeInst);
    //
    // }
    // return tableName;
    //
    // }

    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        try {

        } catch (Exception e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL, e.getMessage());
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType endRead(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public Filter getActivityInstListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Filter getProcessDefListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Object getProcessDefVersionDataAttribute(String processDefVersionId, String attName, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Filter getProcessInstListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Object getRouteDefDataAttribute(String routeDefId, String attName, Map<RightCtx, Object> ctx) throws BPMException {

        return null;
    }

    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        EIActivityInst eiActivityInst = this.activityInstMgr.loadByKey(activityInstId);
        DataMap dataMap = getActivityInstMapDAOMap(activityInstId, userId);

        this.updateActivityInstMapDAO(activityInstId, dataMap, userId);
        IOTRightEngine rightEngine = null;
        try {
            rightEngine = (IOTRightEngine) BPMServer.getRigthEngine(this.systemCode);
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ActivityDefPerformtype performType = rightEngine.getActivityDefRightAttribute(activityDefId).getPerformType();
        ActivityDefPerformSequence performSeqence = rightEngine.getActivityDefRightAttribute(activityDefId).getPerformSequence();

        if (eiActivityInst.getProcessInst().getCopyNumber() <= 1) {// 没有分裂发生时应该更新流程数据
            // &&!this.getProcessInstMapDAOMap(eiActivityInst.getProcessInstId()).isEmpty()
            this.updateProcessInstMapDAO(eiActivityInst.getProcessInstId(), dataMap, null);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        String processInstId = this.activityInstMgr.loadByKey(activityInstId).getProcessInstId();
        DataMap dataMap = getActivityInstMapDAOMap(activityInstId, userId);
        this.updateActivityInstHisMapDAO(activityInstHistoryId, dataMap, userId);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        // this.updateActivityInstMapDAO(activityInstID,
        // this.activityInstMgr.loadByKey(activityInstID).getProcessInstId(),
        // this.getProcessInstMapDAOMap(this.activityInstMgr.loadByKey(activityInstID).getProcessInstId()).clone(),userId);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);
        EIActivityInst eiActivityInst = this.activityInstMgr.loadByKey(activityInstId);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
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

    public DataMap getActivityInstMapDAOMap(String activityInstID, String userId) throws BPMException {

        EIActivityInst eiActivityInst = this.activityInstMgr.loadByKey(activityInstID);

        Map userMap = (Map) JDSActionContext.getActionContext().getContext().get(activityInstID);
        if (userMap == null) {
            userMap = new HashMap();
        }
        DataMap dataMap = (DataMap) userMap.get(userId);

        if (dataMap == null) {
            // 没考虑好先采取兼容性设计
            // dataMap=this.getActivityInstMapDAOMapFormDb(activityInstID,userId);
            dataMap = this.getActivityInstMapDAOMapFormDb(activityInstID, null);

            userMap.put(userId, dataMap);
            JDSActionContext.getActionContext().getContext().put(activityInstID, userMap);
        }

        return dataMap;
    }

    public DataMap getActivityInstHistoryMapDAOMap(String activityInstyHistoryId, String userId) throws BPMException {

        Map userMap = (Map) JDSActionContext.getActionContext().getContext().get(activityInstyHistoryId);
        if (userMap == null) {
            userMap = new HashMap();
        }
        DataMap dataMap = (DataMap) userMap.get(userId);
        if (dataMap == null) {
            dataMap = this.getActivityInstHistoryMapDAOMapFormDb(activityInstyHistoryId, userId);
            userMap.put(userId, dataMap);
            JDSActionContext.getActionContext().getContext().put(activityInstyHistoryId, userMap);
        }
        return dataMap;
    }

    public DataMap getProcessInstMapDAOMap(String processInstId) throws BPMException {

        DataMap dataMap = (DataMap) JDSActionContext.getActionContext().getContext().get(processInstId);
        if (dataMap == null) {
            dataMap = this.getProcessInstMapDAOMapFormDb(processInstId, null);
            JDSActionContext.getActionContext().getContext().put(processInstId, dataMap);
        }
        return dataMap;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;

    }

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub

    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID, String userId) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, String userId, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID, String userId) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateActivityInstFormValues(String activityInstID, String userId, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId, String userId) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, String userId, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void setWorkflowClient(WorkflowClientService service) {

    }

    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public CtVfsService getVfsClient() {

        CtVfsService vfsClient = CtVfsFactory.getCtVfsService();
        return vfsClient;
    }
}


