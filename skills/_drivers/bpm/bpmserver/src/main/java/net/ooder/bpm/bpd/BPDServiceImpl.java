
package net.ooder.bpm.bpd;

import net.ooder.common.logging.Log;

import com.alibaba.fastjson.JSONArray;
import net.ooder.bpm.bpd.service.BPDService;
import net.ooder.bpm.bpd.service.BPDWebService;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.database.admin.ProcessDefPersonInst;
import net.ooder.bpm.engine.database.admin.ProcessDefPersonInstDAO;
import net.ooder.bpm.webservice.XPDLProcessDef;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.OrgManagerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EsbBeanAnnotation(id = "BPDService", name = "BPD服务", expressionArr = "BPDServiceImpl()", desc = "BPD服务")

public class BPDServiceImpl implements BPDWebService {

    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, BPDService.class);


    @Override
    public ResultModel<Boolean> saveProcessDefListToDB(String xpdlString, String personId) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();

        boolean cresult = true;
        if (xpdlString == null || xpdlString.trim().equals("")) {
            // 流程定义版本ID字符串不能为空！

            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
            ((ErrorResultModel<Boolean>) result).setErrdes("流程定义版本ID字符串不能为空！");
            return result;
        }

        DbManager dbManager = DbManager.getInstance();

        try {
            dbManager.beginTransaction();
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            XPDLProcessDef xpdProcessDef = new XPDLProcessDef(person);
            cresult = xpdProcessDef.saveProcessDefToDB(xpdlString);
            result.setData(cresult);

        } catch (BPMException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());

        } catch (Exception e) {
            logger.error("", e);
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());
        } finally {
            try {
                dbManager.endTransaction(cresult);
            } catch (SQLException sqle) {
                logger.error("", sqle);
                result = new ErrorResultModel<Boolean>();
                ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
                ((ErrorResultModel<Boolean>) result).setErrdes(sqle.getMessage());
            }
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> deleteProcessDefListToDB(String versionIdsString, String personId) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        boolean cresult = true;
        if (versionIdsString == null || versionIdsString.trim().equals("")) {
            // 流程定义版本ID字符串不能为空！
            ((ErrorResultModel<Boolean>) result).setErrdes("流程定义版本ID字符串不能为空！");
            return result;
        }
        DbManager dbManager = DbManager.getInstance();

        try {
            dbManager.beginTransaction();
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            XPDLProcessDef xpdProcessDef = new XPDLProcessDef(person);
            cresult = xpdProcessDef.deleteProcessDefFromDB(versionIdsString);
            result.setData(cresult);

        } catch (BPMException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());

        } catch (Exception e) {
            logger.error("", e);
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());
        } finally {
            try {
                dbManager.endTransaction(cresult);
            } catch (SQLException sqle) {
                logger.error("", sqle);
                result = new ErrorResultModel<Boolean>();
                ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
                ((ErrorResultModel<Boolean>) result).setErrdes(sqle.getMessage());
            }
        }
        return result;
    }

    @Override
    public ResultModel<String> getProcessDefListFromDB(String versionIdsString, String personId) {
        ResultModel<String> result = new ResultModel<String>();
        if (versionIdsString == null || versionIdsString.trim().equals("")) {
            // 流程定义版本ID字符串不能为空！
            ((ErrorResultModel<String>) result).setErrdes("流程定义版本ID字符串不能为空！");
            return result;
        }
        try {
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            XPDLProcessDef xpdProcessDef = new XPDLProcessDef(person);
            String xpdlStr = xpdProcessDef.getWorkflowProcessesFromDB(versionIdsString);
            result.setData(xpdlStr);
        } catch (Exception e) {
            result = new ErrorResultModel<String>();

            ((ErrorResultModel<String>) result).setErrdes(e.getMessage());

        }
        return result;
    }

    @Override
    public ResultModel<Boolean> activateProcessDefVersion(String versionId, String personId) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();
        boolean cresult = true;
        if (versionId == null || versionId.trim().equals("")) {
            // 流程定义版本ID字符串不能为空！
            ((ErrorResultModel<Boolean>) result).setErrdes("流程定义版本ID字符串不能为空！");
            return result;
        }
        DbManager dbManager = DbManager.getInstance();

        try {
            dbManager.beginTransaction();
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            XPDLProcessDef xpdProcessDef = new XPDLProcessDef(person);
            cresult = xpdProcessDef.activateProcessDefVersion(versionId);
            result.setData(cresult);
        } catch (BPMException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());

        } catch (Exception e) {
            logger.error("", e);
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());
        } finally {
            try {
                dbManager.endTransaction(cresult);
            } catch (SQLException sqle) {
                logger.error("", sqle);
                result = new ErrorResultModel<Boolean>();
                ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
                ((ErrorResultModel<Boolean>) result).setErrdes(sqle.getMessage());
            }
        }
        return result;

    }


    @Override
    public ResultModel<Boolean> freezeProcessDefVersion(String processDefVersionId, String personId) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();
        boolean cresult = true;
        if (processDefVersionId == null || processDefVersionId.trim().equals("")) {
            // 流程定义版本ID字符串不能为空！
            ((ErrorResultModel<Boolean>) result).setErrdes("流程定义版本ID字符串不能为空！");
            return result;
        }
        DbManager dbManager = DbManager.getInstance();

        try {
            dbManager.beginTransaction();
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            XPDLProcessDef xpdProcessDef = new XPDLProcessDef(person);
            cresult = xpdProcessDef.freezeProcessDefVersion(processDefVersionId);
            result.setData(cresult);
        } catch (BPMException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());

        } catch (Exception e) {
            logger.error("", e);
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());
        } finally {
            try {
                dbManager.endTransaction(cresult);
            } catch (SQLException sqle) {
                logger.error("", sqle);
                result = new ErrorResultModel<Boolean>();
                ((ErrorResultModel<Boolean>) result).setErrcode(BPMException.PROCESSDEFINITIONERROR);
                ((ErrorResultModel<Boolean>) result).setErrdes(sqle.getMessage());
            }
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> saveCommission(String processId, String group, String personIds) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();


        ProcessDefPersonInstDAO dao = new ProcessDefPersonInstDAO();
        ProcessDefPersonInst finddao = new ProcessDefPersonInst();
        finddao.setProcessDefId(processId);
        finddao.setRightCode(group);

        List<ProcessDefPersonInst> insts = dao.findByExample(finddao);

        for (ProcessDefPersonInst inst : insts) {
            dao.delete(inst);
        }

        List<String> personIdArr = JSONArray.parseArray(personIds, String.class);

        for (String personId : personIdArr) {
            ProcessDefPersonInst rightInst = new ProcessDefPersonInst();
            rightInst.setUuid(UUID.randomUUID().toString());
            rightInst.setPersonId(personId);
            rightInst.setProcessDefId(processId);
            rightInst.setRightCode(group);
            dao.insert(rightInst);
        }
        result.setData(true);


        return result;

    }

    @Override
    public ResultModel<List<Person>> getCommissions(String processId, String group) {
        ResultModel<List<Person>> result = new ResultModel<List<Person>>();

        List<Person> persons = new ArrayList<Person>();

        ProcessDefPersonInstDAO dao = new ProcessDefPersonInstDAO();
        ProcessDefPersonInst finddao = new ProcessDefPersonInst();
        finddao.setProcessDefId(processId);
        finddao.setRightCode(group);

        List<ProcessDefPersonInst> insts = dao.findByExample(finddao);

        OrgManager accountManager = OrgManagerFactory.getOrgManager();

        for (ProcessDefPersonInst inst : insts) {
            try {
                persons.add(accountManager.getPersonByID(inst.getPersonId()));
            } catch (PersonNotFoundException e) {
                logger.error(e);
            }
        }

        result.setData(persons);

        return result;
    }


}
