package net.ooder.bpm.webservice;

import net.ooder.common.logging.Log;

import net.ooder.bpm.client.event.BPDEvent;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.event.BPMEventControl;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.proxy.ProcessDefVersionProxy;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.engine.subflow.ActRefPdClient;
import net.ooder.bpm.engine.subflow.ActRefPdClientImpl;
import net.ooder.bpm.enums.event.BPDEventEnums;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.webservice.XPDLBean.ProcessDefBean;
import net.ooder.bpm.webservice.XPDLBean.ProcessDefVersionBean;
import net.ooder.bpm.webservice.XPDLBean.SubProcessDefUtil;
import net.ooder.common.database.DBAgent;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.Condition;
import net.ooder.annotation.Operator;
import net.ooder.common.util.DateUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.org.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.sql.SQLException;
import java.util.*;

public class XPDLProcessDef {

    // 获得Log日志对象
    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, XPDLProcessDef.class);
    private Person person;
    private Map eventContext;
    private BPMEventControl eventControl;

    public XPDLProcessDef(Person person) {
        this.person = person;
        this.eventContext = new HashMap();
        this.eventControl = BPMEventControl.getInstance();
        eventContext = (HashMap) fillInUserID(eventContext);
        this.person = person;
    }

    /**
     * 将PDT定义的流程保存到数据库中
     *
     * @param xpdlString XPDL文件的字符串
     * @return boolean
     */
    public boolean saveProcessDefToDB(String xpdlString) throws Exception {
        try {
            Document xpdlDocument = XMLParse.parseXMLDocument(xpdlString);
            Node pkgNode = xpdlDocument.getDocumentElement();
            Node wpsNode = (Node) (XMLParse.getChildNodesByName(pkgNode, "WorkflowProcesses")).get(0);
            List wpNodeList = XMLParse.getChildNodesByName(wpsNode, "WorkflowProcess");
            // 将要保存的流程版本ID存入List中
            List versionIdList = new ArrayList();
            for (int i = 0; i < wpNodeList.size(); i++) {
                Node wpNode = (Node) wpNodeList.get(i);
                ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(wpNode);
                versionIdList.add(processDefVersionBean.getProcessDefVersionId());
            }
            // 查询数据库中是否存在XPDL文件中流程版本ID
            Condition condition = new Condition(BPMConditionKey.PROCESSDEF_VERSION_VERSION_ID, Operator.IN, versionIdList);
            EIProcessDefVersionManager processDefVersionManager = EIProcessDefVersionManager.getInstance();
            List processDefVersionList = processDefVersionManager.loadByWhere(" WHERE " + condition.makeConditionString());
            // 数据库中已经存在的流程版本ID集合
            List dbVersionIdList = new ArrayList();
            for (int i = 0; i < processDefVersionList.size(); i++) {
                EIProcessDefVersion processDefVersion = (EIProcessDefVersion) processDefVersionList.get(i);
                dbVersionIdList.add(processDefVersion.getProcessDefVersionId());
            }
            // 保存流程信息到数据库
            for (int i = 0; i < wpNodeList.size(); i++) {
                Node wpNode = (Node) wpNodeList.get(i);
                ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(wpNode);
                if (dbVersionIdList.contains(processDefVersionBean.getProcessDefVersionId())) {
                    if (!processDefVersionBean.updateDateFromDB()) {
                        return false;
                    }
                } else {
                    ProcessDefBean processDefBean = new ProcessDefBean(wpNode);

                    // 流程已存在则不插入流程管理信息
                    if (!processDefBean.processIsExist()) {
                        String insertSQLString = "INSERT INTO ADMIN_PROCESSDEF_PERSON_INST(UUID,PROCESSDEF_ID,PERSON_ID,RIGHTCODE) " + " VALUES('"+
                        UUID.randomUUID().toString()+"','" + processDefVersionBean.getProcessDefId() + "'" + ",'" + processDefVersionBean.getCreatorid()
                                + "'" + ",'OWNER'" + ")";
                        if (!oprateProcessDefRightsOnDB(insertSQLString)) {
                            return false;
                        }
                    }
                    EIProcessDefVersion processDefVersion = (EIProcessDefVersion) processDefVersionManager.loadByKey(processDefVersionBean.getProcessDefVersionId());
                    if (processDefVersion != null) {
                        BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFUPDATE, this.eventContext);
                        eventControl.dispatchEvent(event);
                    }


                    if (!processDefVersionBean.insertDataToDB()) {
                        return false;
                    }

                }
                // 活动块操作
                saveBolckDefToDB(wpNode);
            }

            return true;
        } catch (Exception e) {
            logger.error("Fail to save processdefs.", e);
            throw e;
        }
    }

    public boolean saveBolckDefToDB(Node wpNode) throws Exception {
        try {
            // Document xpdlDocument = XMLParse.parseXMLDocument(xpdlString);
            // Node wpNode = xpdlDocument.getDocumentElement();

            // 处理活动块
            Node bolckNodes = (Node) (XMLParse.getChildNodesByName(wpNode, "ActivitySets")).get(0);
            List<Node> bolckNodeList = XMLParse.getChildNodesByName(bolckNodes, "ActivitySet");

            // 将要保存的流程版本ID存入List中
            List<String> bolckIdList = new ArrayList<String>();
            for (int i = 0; i < bolckNodeList.size(); i++) {
                Node bolckNode = (Node) bolckNodeList.get(i);
                ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(bolckNode, true);
                bolckIdList.add(processDefVersionBean.getProcessDefVersionId());
            }
            // 查询数据库中是否存在XPDL文件中流程版本ID
            Condition condition = new Condition(BPMConditionKey.PROCESSDEF_VERSION_VERSION_ID, Operator.IN, bolckIdList);
            EIProcessDefVersionManager processDefVersionManager = EIProcessDefVersionManager.getInstance();

            List processDefVersionList = new ArrayList();
            if (bolckIdList.size() > 0) {
                processDefVersionList = processDefVersionManager.loadByWhere(" WHERE " + condition.makeConditionString());
            }
            // 数据库中已经存在的流程版本ID集合

            List dbVersionIdList = new ArrayList();
            for (int i = 0; i < processDefVersionList.size(); i++) {
                EIProcessDefVersion processDefVersion = (EIProcessDefVersion) processDefVersionList.get(i);
                dbVersionIdList.add(processDefVersion.getProcessDefVersionId());
            }
            // 保存流程信息到数据库
            for (int i = 0; i < bolckNodeList.size(); i++) {
                Node blockNode = (Node) bolckNodeList.get(i);
                ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(blockNode, true);
                if (processDefVersionBean.getProcessDefId() != null && !processDefVersionBean.getProcessDefId().equals("")) {
                    if (dbVersionIdList.contains(processDefVersionBean.getProcessDefVersionId())) {
                        if (!processDefVersionBean.updateDateFromDB()) {
                            return false;
                        }
                    } else {
                        if (!processDefVersionBean.insertDataToDB()) {
                        }
                    }
                }

            }
            return true;
        } catch (Exception e) {
            logger.error("Fail to save processdefs.", e);
            throw e;
        }
    }

    /**
     * 从数据库中删除PDT定义的流程
     *
     * @param versionIdsString 流程版本ID的字符串，以逗号分割
     * @return boolean
     */
    public boolean deleteProcessDefFromDB(String versionIdsString) throws Exception {
        try {
            // 将流程版本ID的字符串分割，存为数组
            String[] versionIds = StringUtility.split(versionIdsString, ",");
            for (int i = 0; i < versionIds.length; i++) {
                // 拼写流程ID字符串

                SubProcessDefUtil sp = new SubProcessDefUtil();
                // conn.close();
                List subUersionIds = sp.getSubPorcessXPDL(versionIds[i]);
                List subOutIds = sp.getOutPorcessXPDL(versionIds[i]);
                for (int k = 0; subOutIds.size() > k; k++) {
                    String blockId = (String) subOutIds.get(k);
                    if (!subUersionIds.contains(blockId)) {
                        subUersionIds.add(blockId);
                    }
                }
                // subUersionIds.addAll(sp.getActivitySetsXPDL(versionIds[i]));
                if (subUersionIds == null || subUersionIds.size() == 0) {
                    subUersionIds = new ArrayList();

                }
                if (!subUersionIds.contains(versionIdsString)) {
                    subUersionIds.add(versionIdsString);
                }

                for (int j = 0; subUersionIds.size() > j; j++) {
                    String processDefVersionId = (String) subUersionIds.get(j);
                    EIProcessDefVersion processDefVersion = EIProcessDefVersionManager.getInstance().loadByKey(processDefVersionId);
                    if (processDefVersion != null) {


                        String processDefId = processDefVersion.getProcessDefId();

                        // 获得XPDL文件的流程版本对象
                        ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(processDefVersionId);
                        processDefVersionBean.deleteDateFromDB();


                        BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFDELETED, this.eventContext);
                        eventControl.dispatchEvent(event);

                        // 如果流程已经被删除，那么删除流程所具有的权限
                        List processDefVersionList = EIProcessDefVersionManager.getInstance().loadByProcessdefId(processDefId);
                        // 删除流程的子流程关系

                        ActRefPdClient actRefPdClientImpl = new ActRefPdClientImpl();
                        actRefPdClientImpl.delete(processDefVersionId);
                        if (processDefVersionList.size() == 0) {
                            // 删除流程信息
                            ProcessDefBean processDefBean = new ProcessDefBean(processDefId);
                            if (!processDefBean.deleteDateFromDB()) {
                                return false;
                            }
                            // 删除流程权限信息
                            String deleteSQLString = "DELETE FROM ADMIN_PROCESSDEF_PERSON_INST " + " WHERE ADMIN_PROCESSDEF_PERSON_INST.PROCESSDEF_ID = '" + processDefId + "'";
                            if (!oprateProcessDefRightsOnDB(deleteSQLString)) {
                                return false;
                            }
                        }
                    }

                }

            }
            return true;
        } catch (Exception e) {
            logger.error("Fail to delete processdefs.", e);
            throw e;
        }
    }

    /**
     * 执行流程权限操作
     *
     * @param querySQL SQL语句
     * @return boolean
     * @throws Exception
     */
    private boolean oprateProcessDefRightsOnDB(String querySQL) throws Exception {
        DBAgent dba = new DBAgent(BPMConstants.CONFIG_KEY);
        try {
            if (dba.execute(querySQL) == -1)
                throw new SQLException("查询ADMIN_PROCESSDEF_PERSON_INST表时有错误发生。");
            return true;
        } catch (Exception e) {
            logger.error("Fail to insert rights of processdef.", e);
            throw e;
        } finally {
            dba.close();
        }
    }

    /**
     * 获得工作流集合的XPDL节点字符串
     *
     * @param versionIdsString 流程版本ID的字符串，以逗号分割
     * @return String
     */
    public String getWorkflowProcessesFromDB(String versionIdsString) {
        // modify by lxl 2004-03-12 临时修改，加上了try-catch
        try {
            // 将流程版本ID的字符串分割，存为数组

            // String versionIdsString1=versionIdsString;
            String[] versionIds = StringUtility.split(versionIdsString, ",");

            StringBuffer xpdlString = new StringBuffer();
            xpdlString.append("<WorkflowProcesses>");
            for (int i = 0; i < versionIds.length; i++) {

                SubProcessDefUtil sp = new SubProcessDefUtil();

                List<String> subUersionIds = sp.getSubPorcessXPDL(versionIds[i]);

                StringBuffer subxpdlString = new StringBuffer();
                if (subUersionIds != null) {
                    for (int k = 0; k < subUersionIds.size(); k++) {
                        String versionId = sp.getProcessDefVersionId((String) subUersionIds.get(k));
                        if (versionId != null) {
                            ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(versionId);
                            String workflowprocess = XMLParse.toString(processDefVersionBean.appendBeanToXPDL());
                            int index = workflowprocess.indexOf("WorkflowProcess");
                            String WorkflowBlockString = getWorkflowBlockFromDB((String) subUersionIds.get(k));

                            workflowprocess = workflowprocess.substring(index - 1);

                            if (WorkflowBlockString != null) {
                                int indexBlock = workflowprocess.indexOf("</ActivitySets>");
                                if (indexBlock == -1) {
                                    indexBlock = workflowprocess.indexOf("<Activities>");
                                    if (workflowprocess != null && workflowprocess.length() < 0) {
                                        String indexString = workflowprocess.substring(0, indexBlock);

                                        String endString = workflowprocess.substring(indexBlock);
                                        workflowprocess = indexString + WorkflowBlockString + endString;
                                    }

                                } else {
                                    String indexString = workflowprocess.substring(0, indexBlock);
                                    String endString = workflowprocess.substring(indexBlock + "</ActivitySets>".length());
                                    workflowprocess = indexString + WorkflowBlockString + endString;
                                }
                            }
                            subxpdlString.append(workflowprocess);
                        }
                    }
                }

                ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(versionIds[i]);
                String workflowprocess = XMLParse.toString(processDefVersionBean.appendBeanToXPDL());
                int index = workflowprocess.indexOf("WorkflowProcess");
                String WorkflowBlockString = getWorkflowBlockFromDB(versionIds[i]);
                workflowprocess = workflowprocess.substring(index - 1);
                if (WorkflowBlockString != null && !WorkflowBlockString.equals("<ActivitySets></ActivitySets>")) {
                    int indexBlock = workflowprocess.indexOf("</ActivitySets>");
                    if (indexBlock == -1) {
                        indexBlock = workflowprocess.indexOf("<Activities>");
                        String indexString = workflowprocess.substring(0, indexBlock);
                        String endString = workflowprocess.substring(indexBlock);
                        workflowprocess = indexString + WorkflowBlockString + endString;
                    } else {
                        String indexString = workflowprocess.substring(0, indexBlock);
                        String endString = workflowprocess.substring(indexBlock + "</ActivitySets>".length());
                        workflowprocess = indexString + WorkflowBlockString + endString;
                    }
                }
                xpdlString.append(workflowprocess);
                // 添加子流程串
                xpdlString.append(subxpdlString);
            }

            xpdlString.append("</WorkflowProcesses>");
            // 添加活动块字符串

            return xpdlString.toString();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 获得工作流集合的XPDL节点字符串
     *
     * @param versionIdsString 流程版本ID的字符串，以逗号分割
     * @return String
     */
    public String getWorkflowBlockFromDB(String versionIdsString) {
        try {
            String[] versionIds = StringUtility.split(versionIdsString, ",");
            StringBuffer xpdlString = new StringBuffer();
            xpdlString.append("<ActivitySets>");
            for (int i = 0; i < versionIds.length; i++) {

                SubProcessDefUtil sp = new SubProcessDefUtil();
                // conn.close();
                List subUersionIds = sp.getActivitySetsXPDL(versionIds[i]);

                StringBuffer subxpdlString = new StringBuffer();
                if (subUersionIds != null && subUersionIds.size() > 0) {
                    for (int k = 0; k < subUersionIds.size(); k++) {
                        ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean((String) subUersionIds.get(k), true);
                        String workflowprocess = XMLParse.toString(processDefVersionBean.appendBeanToXPDL());
                        int index = workflowprocess.indexOf("ActivitySet");
                        workflowprocess = workflowprocess.substring(index - 1);
                        subxpdlString.append(workflowprocess);
                    }

                    // ProcessDefVersionBean processDefVersionBean = new ProcessDefVersionBean(
                    // versionIds[i],true);
                    // String workflowprocess = XMLParse
                    // .toString(processDefVersionBean.appendBeanToXPDL());
                    // int index = workflowprocess.indexOf("ActivitySet");
                    // workflowprocess = workflowprocess.substring(index - 1);
                    // xpdlString.append(workflowprocess);
                    // 添加子流程串
                    xpdlString.append(subxpdlString);
                }
            }

            xpdlString.append("</ActivitySets>");
            return xpdlString.toString();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 激活流程版本
     *
     * @param versionId 版本ID
     * @return boolean
     */
    public boolean activateProcessDefVersion(String versionId) throws Exception {
        try {
            EIProcessDefVersionManager processDefVersionManager = EIProcessDefVersionManager.getInstance();
            EIProcessDefVersion processDefVersion = (EIProcessDefVersion) processDefVersionManager.loadByKey(versionId);

            // 分发事件

            // ================冻结以前的激活版本===============
            // 获得选择版本的流程ID
            String processDefId = processDefVersion.getProcessDefId();
            // 获得激活版本对象
            EIProcessDefVersion activeProcessDefVersion = processDefVersionManager.getActiveProcessDefVersion(processDefId);
            if (activeProcessDefVersion != null) {
                activeProcessDefVersion.setPublicationStatus(ProcessDefVersionStatus.UNDER_REVISION.getType());
                activeProcessDefVersion.setFreezeTime(DateUtility.constructTimestamp(DateUtility.getCurrentTime()));
                processDefVersionManager.save(activeProcessDefVersion);
            }
            // ================激活当前选择的版本================
            processDefVersion.setPublicationStatus(ProcessDefVersionStatus.RELEASED.getType());
            processDefVersion.setActiveTime(DateUtility.constructTimestamp(DateUtility.getCurrentTime()));
            processDefVersionManager.save(processDefVersion);
            // 分发事件

            BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFACTIVATED, this.eventContext);
            eventControl.dispatchEvent(event);


            return true;
        } catch (Exception e) {
            logger.error("Fail to activate processdefversion.", e);
            throw e;
        }
    }

    private Map<RightCtx, Object> fillInUserID(Map<RightCtx, Object> ctx) {
        Map<RightCtx, Object> result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put(RightCtx.USERID, person.getID());
        return result;
    }

    /**
     * 冻结流程版本
     *
     * @param versionId 版本ID
     * @return boolean
     */
    public boolean freezeProcessDefVersion(String versionId) throws Exception {

        try {
            EIProcessDefVersionManager processDefVersionManager = EIProcessDefVersionManager.getInstance();
            EIProcessDefVersion processDefVersion = (EIProcessDefVersion) processDefVersionManager.loadByKey(versionId);


            // ================冻结当前选择的版本================
            processDefVersion.setPublicationStatus(ProcessDefVersionStatus.UNDER_REVISION.getType());
            processDefVersion.setFreezeTime(DateUtility.constructTimestamp(DateUtility.getCurrentTime()));
            processDefVersionManager.save(processDefVersion);
            // 分发事件
            BPDEvent event = new BPDEvent(new ProcessDefVersionProxy(processDefVersion, processDefVersion.getSystemCode()), BPDEventEnums.PROCESSDEFFREEZED, this.eventContext);
            eventControl.dispatchEvent(event);

            return true;
        } catch (Exception e) {
            logger.error("Fail to freeze processdefversion.", e);
            throw e;
        }
    }

    public void main(String[] args) {
        // String XMLString =
        // getWorkflowProcessXPDLFromDB("6BB88670-5876-11D8-8670-C2BF14E83C63");
        // System.out.println(XMLString);
        String filename = "c:/cc.xpdl";
        Document document = XMLParse.loadXMLDocumentFromFile(filename);
        Node node = document.getDocumentElement();
        String xpdlString = XMLParse.toString(node);
        DbManager dbManager = DbManager.getInstance();
        // begine transation
        try {
            dbManager.beginTransaction();
            boolean result = saveProcessDefToDB(xpdlString);
            // deleteProcessDefFromDB("6BB88670-5876-11D8-8670-C2BF14E83C63");
            dbManager.endTransaction(result);
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
