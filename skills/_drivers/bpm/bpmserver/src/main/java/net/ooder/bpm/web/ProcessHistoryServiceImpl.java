package net.ooder.bpm.web;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.client.service.ProcessHistoryService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.jds.core.esb.EsbUtil;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "ProcessHistoryService", name = "ProcessHistoryService", expressionArr = "ProcessHistoryServiceImpl()", desc = "ProcessHistoryService")
public class ProcessHistoryServiceImpl implements ProcessHistoryService {
    @Override
    public ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {
            List<ActivityInst> activityInstList = getClient().getActivityInstListByOutActvityInstHistory(activityInstHistoryId, null);
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > activityInstList.size()) {
                end = activityInstList.size();
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
    public ListResultModel<List<String>> getActivityInstHistoryListByActvityInst(String actvityInstId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {
            List<ActivityInstHistory> activityInstList = getClient().getActivityInstHistoryListByActvityInst(actvityInstId, null);
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > activityInstList.size()) {
                end = activityInstList.size();
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
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
    public ListResultModel<List<String>> getActivityInstHistoryList(WebRightCondition webcondition) {

        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        Integer pageIndex = webcondition.getPage().getPageIndex();
        Integer pageSize = webcondition.getPage().getPageSize();
        try {
            ListResultModel<List<ActivityInstHistory>> activityInstListRsult = getClient().getActivityInstHistoryList(webcondition.getCondition(), webcondition.getRightCondition(), webcondition.getFilter(), webcondition.getCtx());
            List<ActivityInstHistory> activityInstList=activityInstListRsult.get();
            int size=activityInstListRsult.getSize();
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;
            int end = pageSize * pageIndex;

            if (end > size) {
                end = size;
            }

            List<String> resultList = new ArrayList<String>();
            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
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
    public ListResultModel<List<String>> getLastActivityInstHistoryListByActvityInst(String actvityInstId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {
            List<ActivityInstHistory> activityInstList = getClient().getLastActivityInstHistoryListByActvityInst(actvityInstId, null);
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > activityInstList.size()) {
                end = activityInstList.size();
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
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
    public ListResultModel<List<String>> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, Boolean noSplit) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {
            List<ActivityInstHistory> activityInstList = getClient().getAllOutActivityInstHistoryByActvityInstHistory(historyHisroryId, noSplit);
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > activityInstList.size()) {
                end = activityInstList.size();
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
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
    public ListResultModel<List<String>> getActivityInstHistoryListByProcessInst(String processInstId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {

            ListResultModel<List<ActivityInstHistory>> activityInstListRsult = getClient().getActivityInstHistoryListByProcessInst(processInstId, null);
            List<ActivityInstHistory> activityInstList=activityInstListRsult.get();
            int size=activityInstListRsult.getSize();

            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > size) {
                end = size;
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
            }
            result.setSize(activityInstList.size());
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
    public ResultModel<ReturnType> deleteHistory(String activityInstHistoryID) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> restoreHistory(String activityInstHistoryID) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> clearHistory(String activityInstHistoryID) {
        return null;
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> loadHistoryActivityInstHistoryList(String[] activityInstHistoryIds) {
        ListResultModel<List<ActivityInstHistory>> result = new ListResultModel<List<ActivityInstHistory>>();
        try {
            List<ActivityInstHistory> resultList = new ArrayList<ActivityInstHistory>();
            for (String activityInstHistoryId : activityInstHistoryIds) {
                ActivityInstHistory activityInstHistory = getClient().getActivityInstHistory(activityInstHistoryId);
                resultList.add(activityInstHistory);
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
    public ListResultModel<List<String>> loadHistoryAttribute(String activityInstHistoryId) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName) {
        return null;
    }

    @Override
    public ListResultModel<List<String>> loadActivityInstHistoryArrtibuteIds(String historyId) {
        return null;
    }

    @Override
    public ListResultModel<List<AttributeInst>> loadActivityInstHistoryArrtibutes(String[] attributeIds) {
        return null;
    }

    @Override
    public ListResultModel<List<String>> getRouteBackActivityHistoryInstList(String activityInstId) {
        ListResultModel<List<String>> result = new ListResultModel<List<String>>();
        int pageIndex = 0;
        int pageSize = 1000;
        try {
            List<ActivityInstHistory> activityInstList = getClient().getRouteBackActivityHistoryInstList(activityInstId, null, null);
            if (pageIndex < 1) {
                pageIndex = 1;
            }

            int start = (pageIndex - 1) * pageSize;

            int end = pageSize * pageIndex;

            if (end > activityInstList.size()) {
                end = activityInstList.size();
            }

            List<String> resultList = new ArrayList<String>();

            for (int k = start; k < end; k++) {
                resultList.add(activityInstList.get(k).getActivityHistoryId());
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
    public ResultModel<Boolean> setActivityHistoryAttribute(String activityDefId, String name, String value) {
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


