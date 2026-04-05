package net.ooder.skills.bpm.demo.h5;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.config.TreeListResultModel;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;
import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/bpm/demo/h5/")
public class H5WorkServiceDemo {

    @RequestMapping(method = RequestMethod.POST, value = "Main")
    @ResponseBody
    public ResultModel<Object> getMain(String projectId) {
        ResultModel<Object> result = new ResultModel<Object>();
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "NewProcess")
    @ResponseBody
    public TreeListResultModel<List<Map<String, Object>>> newProcess(String processDefVersionId, String domainId, String projectName) {
        TreeListResultModel<List<Map<String, Object>>> formTabsTree = new TreeListResultModel<>();
        Map<String, Object> baseMap = new HashMap<>();
        try {
            ProcessDefVersion version = getClient().getProcessDefVersion(processDefVersionId);
            ProcessInst processInst = getClient().newProcess(version.getProcessDefId(), null, null, null);
            ActivityInst activityInst = processInst.getActivityInstList().get(0);
            String activityInstId = activityInst.getActivityInstId();
            baseMap.put("activityInstId", activityInstId);
            baseMap.put("processInstId", processInst.getProcessInstId());
            baseMap.put("processDefVersionId", processDefVersionId);
            
            List<Map<String, Object>> formTabs = new ArrayList<>();
            ProcessDefVersion processDefVersion = activityInst.getProcessDefVersion();
            ProcessDefForm processDefForm = processDefVersion.getFormDef();
            List<String> tableNams = processDefForm.getTableNames();
            
            for (String tableName : tableNams) {
                Map<String, Object> tableFormTab = new HashMap<>();
                tableFormTab.put("activityInstId", activityInstId);
                tableFormTab.put("tableName", tableName);
                tableFormTab.put("processInstId", processInst.getProcessInstId());
                tableFormTab.put("processDefVersionId", processDefVersion.getProcessDefVersionId());
                formTabs.add(tableFormTab);
            }

            if (formTabs.size() > 0) {
                formTabsTree.setData(formTabs);
            }
            formTabsTree.setCtx(baseMap);

        } catch (BPMException e) {
            e.printStackTrace();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return formTabsTree;
    }

    @RequestMapping(method = RequestMethod.POST, value = "WaiteWrokList")
    @ResponseBody
    public ResultModel<List<ActivityInst>> getWaiteWrok(String processDefId) {
        ResultModel<List<ActivityInst>> result = new ResultModel<List<ActivityInst>>();
        try {
            BPMCondition ccondition = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSDEF_ID, Operator.EQUALS, processDefId);
            ccondition.addOrderBy(new Order(BPMConditionKey.ACTIVITYINST_ARRIVEDTIME, false));
            ListResultModel<List<ActivityInst>> activityInstList = getClient().getActivityInstList(
                ccondition, RightConditionEnums.CONDITION_WAITEDWORK, null, null);
            result.setData(activityInstList.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private WorkflowClientService getClient() {
        WorkflowClientService client = EsbUtil.parExpression("$BPMC", WorkflowClientService.class);
        return client;
    }
}
