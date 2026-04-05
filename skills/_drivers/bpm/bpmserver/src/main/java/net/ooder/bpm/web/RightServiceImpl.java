package net.ooder.bpm.web;

import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.ct.CtActivityDefRight;
import net.ooder.bpm.client.service.RightService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "RightService", name = "RightService", expressionArr = "RightServiceImpl()", desc = "RightService")

public class RightServiceImpl implements RightService {

    @Override
    public ResultModel<ActivityDefRight> getActivityDefRight(String activityDefId) {
        ResultModel<ActivityDefRight> result = new ResultModel<ActivityDefRight>();
        try {
            ActivityDefRight activityDefRight = getClient().getActivityDefRightAttribute(activityDefId);
            result.setData(new CtActivityDefRight(activityDefRight));
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<List<String>> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt group) {
        ResultModel<List<String>> result = new ResultModel<List<String>>();
        List<String> personIds = new ArrayList<String>();
        try {
            List<Person> persons = getClient().getActivityInstPersons(activityInstId, group);

            for (Person person : persons) {

                personIds.add(person.getID());
            }
            result.setData(personIds);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<List<String>> getActivityInstHistoryRightAttribute(String activityInstHistoryId, ActivityInstHistoryAtt group) {
        ResultModel<List<String>> result = new ResultModel<List<String>>();
        List<String> personIds = new ArrayList<String>();
        try {
            List<Person> persons = getClient().getActivityInstHistoryPersons(activityInstHistoryId, group);

            for (Person person : persons) {

                personIds.add(person.getID());
            }
            result.setData(personIds);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<List<RightGroupEnums>> queryAllPermissionToActivityInst(String activityInstHistoryId) {

        return null;
    }


    /**
     * @return
     */
    public WorkflowClientService getClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }
}


