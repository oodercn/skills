package net.ooder.bpm.web;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.service.ProcessDefService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.common.JDSException;
import net.ooder.common.Condition;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;

import java.util.ArrayList;
import java.util.List;
@EsbBeanAnnotation(id = "ProcessDefService", name = "ProcessDefService", expressionArr = "ProcessDefServiceImpl()", desc = "ProcessDefService")

public class ProcessDefServiceImpl implements ProcessDefService {
    @Override
    public ListResultModel<List<String>> getProcessDefVersionIdList(BPMCondition condition) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        Integer pageIndex = condition.getPage().getPageIndex();
        Integer pageSize = condition.getPage().getPageSize();
        try {

            ListResultModel<List<ProcessDefVersion>> processListRsult =  getClient().getProcessDefVersionList(condition, null, null);
            List<ProcessDefVersion> processList=processListRsult.get();
            int size=processListRsult.getSize();

            if (pageIndex<1){
                pageIndex=1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > size) {
                end =size ;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(processList.get(k).getProcessDefVersionId());
            }
            result.setSize(size);
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public ListResultModel<List<String>> getProcessDefIdList(BPMCondition condition) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        Integer pageIndex = condition.getPage().getPageIndex();
        Integer pageSize = condition.getPage().getPageSize();
        try {

            ListResultModel<List<ProcessDef>> processListRsult = getClient().getProcessDefList(condition, null, null);
            List<ProcessDef> processList=processListRsult.get();
            int size=processListRsult.getSize();


            if (pageIndex<1){
                pageIndex=1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end >size) {
                end = size ;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(processList.get(k).getProcessDefId());
            }
            result.setSize(size);
            result.setData(resultList);
        } catch (BPMException e) {
            result = new ErrorListResultModel();
            ((ErrorListResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(String[] processDefVersionIds) {
        ListResultModel<List<ProcessDefVersion>> result = new ListResultModel<List<ProcessDefVersion>>();
        try {
            List<ProcessDefVersion> resultList = new ArrayList<ProcessDefVersion>();
            for (String  processDefVersionId: processDefVersionIds) {
                ProcessDefVersion processDefVersion = getClient().getProcessDefVersion(processDefVersionId);
                resultList.add(processDefVersion);
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
    public ListResultModel<List<ProcessDef>> getProcessDefList(String[] processDefIds) {
        ListResultModel<List<ProcessDef>> result = new ListResultModel<List<ProcessDef>>();
        try {
            List<ProcessDef> resultList = new ArrayList<ProcessDef>();
            for (String  processDefId: processDefIds) {
                ProcessDef processDef = getClient().getProcessDef(processDefId);
                resultList.add(processDef);
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
    public ListResultModel<List<AttributeDef>> loadProcessDefArrtibutes(String processDefVersionId) {
        ListResultModel<List<AttributeDef>> result = new ListResultModel<List<AttributeDef>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<AttributeDef> activityInstList  =  getClient().getProcessDefVersion(processDefVersionId).getAllAttribute();
            if (pageIndex<1){
                pageIndex=1;
            }
            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }
            List<AttributeDef> resultList = new ArrayList<AttributeDef>();
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
    public ListResultModel<List<AttributeDef>> loadActivityDefArrtibutes(String activityDefId) {
        ListResultModel<List<AttributeDef>> result = new ListResultModel<List<AttributeDef>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<AttributeDef> activityInstList  =  getClient().getActivityDef(activityDefId).getAllAttribute();
            if (pageIndex<1){
                pageIndex=1;
            }
            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }
            List<AttributeDef> resultList = new ArrayList<AttributeDef>();
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
    public ListResultModel<List<AttributeDef>> loadRouteDefArrtibutes(String routeDefId) {
        ListResultModel<List<AttributeDef>> result = new ListResultModel<List<AttributeDef>>();
        int pageIndex=0;
        int pageSize=1000;
        try {
            List<AttributeDef> activityInstList  =  getClient().getRouteDef(routeDefId).getAllAttribute();
            if (pageIndex<1){
                pageIndex=1;
            }
            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex ;

            if (end > activityInstList.size()) {
                end = activityInstList.size() ;
            }
            List<AttributeDef> resultList = new ArrayList<AttributeDef>();
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
    public ListResultModel<List<Listener>> getActivityListeners(String activityDefId) {
        return null;
    }

    @Override
    public ResultModel<String> getFirstActivityDefInProcess(String processDefVersionId) {
        ResultModel<String> result = new ResultModel<String>();
        try {


            result.setData(getClient().getFirstActivityDefInProcess(processDefVersionId).getActivityDefId());
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


