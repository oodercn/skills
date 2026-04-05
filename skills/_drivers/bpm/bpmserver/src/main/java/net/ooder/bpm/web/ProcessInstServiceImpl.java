package net.ooder.bpm.web;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.service.ProcessInstService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.common.JDSException;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EsbBeanAnnotation(id = "ProcessInstService", name = "ProcessInstService", expressionArr = "ProcessInstServiceImpl()", desc = "ProcessInstService")

public class ProcessInstServiceImpl implements ProcessInstService {
    @Override
    public ListResultModel<List<String>> getProcessInstList(WebRightCondition webcondition) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        Integer pageIndex = webcondition.getPage().getPageIndex();
        Integer pageSize = webcondition.getPage().getPageSize();

        try {

            ListResultModel<List<ProcessInst>> processListRsult =   getClient().getProcessInstList(webcondition.getCondition(),webcondition.getRightCondition(),webcondition.getFilter(),webcondition.getCtx());
            List<ProcessInst> processList=processListRsult.get();
            int size=processListRsult.getSize();

               if (pageIndex<1){
                pageIndex=1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > size) {
                end = size ;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(processList.get(k).getProcessInstId());
            }
            result.setSize(size);
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        } catch (JDSException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ListResultModel<List<ProcessInst>> loadProcessInstLists(String[] processInstIds) {
        ListResultModel<List<ProcessInst>> result = new ListResultModel<List<ProcessInst>>();
        try {
            List<ProcessInst> resultList = new ArrayList<ProcessInst>();
            for (String  processInstId: processInstIds) {
                ProcessInst activityInst = getClient().getProcessInst(processInstId);
                resultList.add(activityInst);
            }
            result.setSize(resultList.size());
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ListResultModel<List<String>> getActivityInstList(WebRightCondition webcondition) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        Integer pageIndex = webcondition.getPage().getPageIndex();
        Integer pageSize = webcondition.getPage().getPageSize();
        try {


            ListResultModel<List<ActivityInst>> activityInstListRsult =  getClient().getActivityInstList(webcondition.getCondition(),webcondition.getRightCondition(),webcondition.getFilter(),webcondition.getCtx());
            List<ActivityInst> activityInstList=activityInstListRsult.get();
            int size=activityInstListRsult.getSize();
           if (pageIndex<1){
                pageIndex=1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > size) {
                end = size ;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityInstId());
            }
            result.setSize(size);
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        } catch (JDSException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ListResultModel<List<ActivityInst>> loadActivityInstList(String[] activityInstIds) {
        ListResultModel<List<ActivityInst>> result = new ListResultModel<List<ActivityInst>>();
        try {
            List<ActivityInst> resultList = new ArrayList<ActivityInst>();
            for (String  activityInstId: activityInstIds) {
                ActivityInst activityInst = getClient().getActivityInst(activityInstId);
                if (activityInst!=null){
                    resultList.add(activityInst);
                }

            }
            result.setSize(resultList.size());
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<ActivityInst> activityInstList  =  getClient().getActivityInstListByOutActvityInstHistory(activityInstHistoryId,new HashMap<>());
            if (pageIndex<1){
                pageIndex=1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityInstId());
            }
            result.setSize(activityInstList.size());
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ListResultModel<List<String>> getNextRoutes(String startActivityInstID) {
        return null;
    }

    @Override
    public ListResultModel<List<AttributeInst>> loadProcessInstArrtibutes(String processInstId) {
        ListResultModel<List<AttributeInst>> result = new ListResultModel<List<AttributeInst>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<AttributeInst> activityInstList  =  getClient().getProcessInst(processInstId).getAllAttribute();
            if (pageIndex<1){
                pageIndex=1;
            }
            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }
            List<AttributeInst> resultList = new ArrayList<AttributeInst>();
            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k));
            }
            result.setSize(activityInstList.size());
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }


    @Override
    public ListResultModel<List<AttributeInst>> loadActivityInstArrtibutes(String activityInstId) {
        ListResultModel<List<AttributeInst>> result = new ListResultModel<List<AttributeInst>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<AttributeInst> activityInstList  =  getClient().getActivityInst(activityInstId).loadAllAttribute();
            if (pageIndex<1){
                pageIndex=1;
            }
            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }
            List<AttributeInst> resultList = new ArrayList<AttributeInst>();
            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k));
            }
            result.setSize(activityInstList.size());
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ResultModel<Boolean> setActivityInstAttribute(String activityInstId, String name, String value) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getClient().getActivityInst(activityInstId).setAttribute(name,value);

        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ResultModel<Boolean> setProcessInstAttribute(String processInstId, String name, String value) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getClient().getProcessInst(processInstId).setAttribute(name,value);

        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    /**
     *
     *
     * @return
     */
    public WorkflowClientService getClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }
}


