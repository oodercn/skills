package net.ooder.skills.bpm.demo.worklist;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.data.SearchData;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.enums.activityinst.ActivityInstDealMethod;
import net.ooder.bpm.enums.activityinst.ActivityInstRunStatus;
import net.ooder.bpm.enums.activityinst.ActivityInstStatus;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.common.JDSException;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/bpm/demo/worklist/")
public class WorkListServiceDemo {

    @RequestMapping(method = RequestMethod.POST, value = "ProcessDefVersionList")
    @ResponseBody
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(String projectName) {
        ListResultModel result = new ListResultModel();
        try {
            BPMCondition condition = new BPMCondition(BPMConditionKey.PROCESSDEF_VERSION_PUBLICATIONSTATUS, 
                Operator.EQUALS, ProcessDefVersionStatus.RELEASED.getType());
            ListResultModel<List<ProcessDefVersion>> versionList = getClient().getProcessDefVersionList(condition, null, null);
            result.setData(versionList.get());
        } catch (Exception e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel<List<ProcessDefVersion>>) result).setErrdes(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "NotStartActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getNotStartActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_CURRENTWORK_NOTSTART);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "WaitedActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getWaitedActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_WAITEDWORK);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "MyActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getMyActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_MYWORKNOTREAD);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "CompletedActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getCompletedActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_COMPLETEDWORK);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "CompletedProcessInstList")
    @ResponseBody
    public ListResultModel<List<ProcessInst>> getCompletedProcessInstList(@RequestBody SearchData data) {
        ListResultModel<List<ProcessInst>> result = new ListResultModel<List<ProcessInst>>();
        try {
            BPMCondition condition = new BPMCondition(BPMConditionKey.PROCESSINST_STATE,
                    Operator.EQUALS, ProcessInstStatus.completed);
            ListResultModel<List<ProcessInst>> processList = getClient().getProcessInstList(condition, 
                RightConditionEnums.CONDITION_COMPLETEDWORK, null, null);
            result.setData(processList.get());
        } catch (Exception e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel<List<ProcessInst>>) result).setErrdes(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "ReadActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getReadActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_READ);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "EndReadActivityInstList")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> getEndReadActivityInstList(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        data.setConditionEnums(RightConditionEnums.CONDITION_ENDREAD);
        result = searchActivityInst(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "searchActivityInst")
    @ResponseBody
    public ListResultModel<List<ActivityInst>> searchActivityInst(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        BPMCondition condition = null;
        RightConditionEnums conditionEnums = data.getConditionEnums();
        String processDefId = data.getProcessDefId();
        if (conditionEnums == null) {
            conditionEnums = RightConditionEnums.CONDITION_WAITEDWORK;
        }
        switch (conditionEnums) {
            case CONDITION_WAITEDWORK:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                        Operator.EQUALS, ActivityInstStatus.notStarted);
                break;
            case CONDITION_CURRENTWORK_NOTSTART:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_RUNSTATUS,
                        Operator.EQUALS, ActivityInstRunStatus.PROCESSNOTSTARTED);
                break;
            case CONDITION_CURRENTWORK:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                        Operator.EQUALS, ActivityInstStatus.running);
                break;
            case CONDITION_READ:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                        Operator.EQUALS, ActivityInstStatus.READ);
                break;
            case CONDITION_ENDREAD:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                        Operator.EQUALS, ActivityInstStatus.ENDREAD);
                break;
            default:
                condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_STATE,
                        Operator.NOT_EQUAL, ActivityInstStatus.aborted);
                break;
        }

        if (processDefId != null) {
            BPMCondition ccondition = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSDEF_ID, 
                Operator.EQUALS, processDefId);
            condition.addCondition(ccondition, JoinOperator.JOIN_AND);
        }

        if (data.getStartTime() != null && data.getStartTime() != 0) {
            BPMCondition startcondition = new BPMCondition(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME,
                    Operator.GREATER_THAN, data.getStartTime());
            condition.addCondition(startcondition, JoinOperator.JOIN_AND);
        }

        if (data.getEndTime() != null && data.getEndTime() != 0) {
            BPMCondition endcondition = new BPMCondition(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME,
                    Operator.LESS_THAN, data.getEndTime());
            condition.addCondition(endcondition, JoinOperator.JOIN_AND);
        }
        
        if (data.getTitle() != null && !data.getTitle().equals("")) {
            BPMCondition endcondition = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSINST_ID,
                    Operator.IN, "select PROCESSINST_ID from BPM_PROCESSINSTANCE where " + 
                    BPMConditionKey.PROCESSINST_NAME + " like '%" + data.getTitle() + "%'");
            condition.addCondition(endcondition, JoinOperator.JOIN_AND);
        }

        condition.addOrderBy(new Order(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME, false));
        try {
            ListResultModel<List<ActivityInst>> activityInstList = getClient().getActivityInstList(
                condition, conditionEnums, null, null);
            result.setData(activityInstList.get());
        } catch (Exception e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel<List<ActivityInst>>) result).setErrdes(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "searchActivityHistory")
    @ResponseBody
    public ListResultModel<List<ActivityInstHistory>> searchActivityHistory(@RequestBody SearchData data) {
        ListResultModel<List<ActivityInstHistory>> result = new ListResultModel<List<ActivityInstHistory>>();
        BPMCondition condition = null;
        RightConditionEnums conditionEnums = data.getConditionEnums();
        String processDefId = data.getProcessDefId();
        if (conditionEnums == null) {
            conditionEnums = RightConditionEnums.CONDITION_WAITEDWORK;
        }

        condition = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_DEALMETHOD,
                Operator.EQUALS, ActivityInstDealMethod.DEALMETHOD_NORMAL);

        if (processDefId != null) {
            BPMCondition ccondition = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_PROCESSDEF_ID, 
                Operator.EQUALS, processDefId);
            condition.addCondition(ccondition, JoinOperator.JOIN_AND);
        }

        if (data.getEndTime() != null && data.getEndTime() != 0) {
            BPMCondition endcondition = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_ARRIVEDTIME,
                    Operator.LESS_THAN, data.getEndTime());
            condition.addCondition(endcondition, JoinOperator.JOIN_AND);
        }
        if (data.getTitle() != null && !data.getTitle().equals("")) {
            BPMCondition endcondition = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_PROCESSINST_ID,
                    Operator.IN, "select PROCESSINST_ID from BPM_PROCESSINSTANCE where " + 
                    BPMConditionKey.PROCESSINST_NAME + " like '%" + data.getTitle() + "%'");
            condition.addCondition(endcondition, JoinOperator.JOIN_AND);
        }

        condition.addOrderBy(new Order(BPMConditionKey.ACTIVITYHISTORY_ARRIVEDTIME, false));
        try {
            ListResultModel<List<ActivityInstHistory>> activityInstList = getClient().getActivityInstHistoryList(
                condition, conditionEnums, null, null);
            result.setData(activityInstList.get());
        } catch (Exception e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel<List<ActivityInstHistory>>) result).setErrdes(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private WorkflowClientService getClient() {
        WorkflowClientService client = EsbUtil.parExpression("$BPMC", WorkflowClientService.class);
        return client;
    }
}
