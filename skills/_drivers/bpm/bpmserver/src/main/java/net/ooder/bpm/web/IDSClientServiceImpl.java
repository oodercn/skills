package net.ooder.bpm.web;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.service.IDSClientService;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.ReturnType;
import net.ooder.common.util.StringUtility;
import net.ooder.config.ActivityDefImpl;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.esd.annotation.RouteToType;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@EsbBeanAnnotation(id = "IDSClientService", name = "IDSClientService", expressionArr = "IDSClientServiceImpl()", desc = "IDSClientService")
public class IDSClientServiceImpl implements IDSClientService {
    @Override
    public ResultModel<ActivityInst> getActivityInst(String activityInstID) {
        ResultModel<ActivityInst> result = new ResultModel<ActivityInst>();
        try {

            ActivityInst activityInst = this.getClient().getActivityInst(activityInstID);
            result.setData(activityInst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ResultModel<ProcessDef> getProcessDef(String processDefID) {
        ResultModel<ProcessDef> result = new ResultModel<ProcessDef>();
        try {

            ProcessDef processDef = this.getClient().getProcessDef(processDefID);
            result.setData(processDef);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ActivityDef> getActivityDef(String activityDefID) {
        ResultModel<ActivityDef> result = new ResultModel<ActivityDef>();
        try {

            ActivityDef activityDef = this.getClient().getActivityDef(activityDefID);
            result.setData(activityDef);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            e.printStackTrace();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<RouteDef> getRouteDef(String routeDefId) {
        ResultModel<RouteDef> result = new ResultModel<RouteDef>();
        try {

            RouteDef activityDef = this.getClient().getRouteDef(routeDefId);
            result.setData(activityDef);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ActivityInst> newProcess(String processDefId, String processInstName) {
        ResultModel<ActivityInst> result = new ResultModel<ActivityInst>();
        try {
            ProcessInst processInst = this.getClient().newProcess(processDefId, processInstName, null, null);
            ActivityInst activityInst = processInst.getActivityInstList().get(0);
            result.setData(activityInst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;

    }

    @Override
    public ResultModel<ReturnType> updateProcessInstName(String processInstId, String name) {

        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {

            ReturnType returnType = this.getClient().updateProcessInstName(processInstId, name);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ProcessDefVersion> getProcessDefVersion(String processDefVersionID) {
        ResultModel<ProcessDefVersion> result = new ResultModel<ProcessDefVersion>();
        try {

            ProcessDefVersion processDefVersion = this.getClient().getProcessDefVersion(processDefVersionID);
            result.setData(processDefVersion);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ProcessInst> getProcessInst(String processInstID) {
        ResultModel<ProcessInst> result = new ResultModel<ProcessInst>();
        try {

            ProcessInst processInst = this.getClient().getProcessInst(processInstID);
            result.setData(processInst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> updateProcessInstUrgency(String processInstId, String urgency) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> routeto(RouteBean routeBean) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        result.setData(new ReturnType(ReturnType.MAINCODE_FAIL));
        try {
            List<Map<RightCtx, Object>> ctxs = new ArrayList<Map<RightCtx, Object>>();
            Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
            String activityInstId = routeBean.getActivityInstId();
            ActivityInst activityInst = this.getClient().getActivityInst(activityInstId);

            RouteToType routeToType = (routeBean.getAction());

            switch (routeToType) {
                case ReSend:
                    String activityInstHistoryId = routeBean.getActivityInstHistoryId();
                    if ((activityInstHistoryId == null || activityInstHistoryId.equals("")) && (this.getClient().getLastActivityInstHistoryListByActvityInst(activityInstId, null).size() > 0)) {

                        ActivityInstHistory activityInstHistory = this.getClient().getLastActivityInstHistoryListByActvityInst(activityInstId, null).get(0);
                        activityInstHistoryId = activityInstHistory.getActivityHistoryId();
                    }
                    if (activityInstHistoryId == null && (this.getClient().getLastActivityInstHistoryListByActvityInst(activityInstId, null).size() > 0)) {
                        ActivityInst hisactivityInst = this.getClient().copyActivityInstByHistory(activityInstHistoryId, ctx);
                        RouteBean hisrouteBean = new RouteBean();
                        hisrouteBean.getPerforms().setPerforms(this.getClient().getConnectInfo().getUserID());
                        hisrouteBean.setNextActivityDefId(hisactivityInst.getActivityDefId());
                        hisrouteBean.setActivityInstId(hisactivityInst.getActivityInstId());
                        this.routeto(hisrouteBean);
                    } else {
                        RouteBean hisrouteBean = new RouteBean();
                        hisrouteBean.getPerforms().setPerforms(this.getClient().getConnectInfo().getUserID());
                        hisrouteBean.setNextActivityDefId(activityInst.getActivityDefId());
                        hisrouteBean.setAction(RouteToType.RouteTo);
                        hisrouteBean.setActivityInstId(activityInst.getActivityInstId());
                        this.routeto(hisrouteBean);
                    }

                    break;
                case RouteToEnd:
                    getClient().completeProcessInst(activityInst.getProcessInstId(), ctx);
                    break;
                case RouteBack:
                    activityInstHistoryId = routeBean.getActivityInstHistoryId();
                    this.getClient().routeBack(activityInstId, activityInstHistoryId, null);
                    break;
                case SignReceive:
                    getClient().signReceive(activityInst.getActivityInstId(), ctx);
                    break;
                case EndRead:
                    activityInst.endRead();
                    break;
                case SaveOnly:
                    break;
                case EndTask:
                    this.getClient().endTask(activityInst.getActivityInstId(), ctx);
                    break;
                case RouteTo:
                    ActivityDef activityDef = getClient().getActivityDef(routeBean.getNextActivityDefId());
                    List<String> activityDefIds = new ArrayList<String>();
                    activityDefIds.add(routeBean.getNextActivityDefId());

                    // 设备节点则先执行
                    if (activityDef.getImplementation().equals(ActivityDefImpl.Device) || activityDef.getImplementation().equals(ActivityDefImpl.Service)) {

                        ctxs.add(ctx);
                        getClient().routeTo(activityInstId, activityDefIds, ctxs);
                        activityDef = activityInst.getNextRoutes().get(0).getToActivityDef();
                        RouteBean deviceRouteBean = new RouteBean();
                        deviceRouteBean.setActivityInstId(activityInstId);
                        deviceRouteBean.setNextActivityDefId(activityDef.getActivityDefId());
                        deviceRouteBean.getPerforms().setPerforms(this.getClient().getConnectInfo().getUserID());
                        deviceRouteBean.setAction(routeBean.getAction());
                        result = routeto(deviceRouteBean);

                    } else {
                        // 办理类型
                        ActivityDefPerformtype performType = activityDef.getRightAttribute().getPerformType();

                        ActivityDefPerformSequence performSequence = activityDef.getRightAttribute().getPerformSequence();

                        List<String> performList = new ArrayList<String>();

                        List<String> readList = new ArrayList<String>();

                        if (performType.equals(ActivityDefPerformtype.NOSELECT) || performType.equals(ActivityDefPerformtype.NEEDNOTSELECT)) {

                            List<Person> persons = activityDef.getRightAttribute().getPerFormPersons();
                            for (Person person : persons) {
                                performList.add(person.getID());
                            }

                            List<Person> readpersons = activityDef.getRightAttribute().getReaderPersons();
                            for (Person person : readpersons) {
                                readList.add(person.getID());
                            }
                            ctx.put(RightCtx.PERFORMERS, performList);
                            ctx.put(RightCtx.READERS, readList);

                        } else {
                            String[] performArr = StringUtility.split(routeBean.getPerforms().getPerforms(), ";");
                            performList = Arrays.asList(performArr);
                            ctx.put(RightCtx.PERFORMERS, performList);
                            String[] readArr = StringUtility.split(routeBean.getReaders().getReaders(), ";");
                            ctx.put(RightCtx.READERS, Arrays.asList(readArr));
                        }

                        ctxs.add(ctx);
                        getClient().routeTo(activityInstId, activityDefIds, ctxs);

                        // 当设定条件为单人办理且类型为自动签收时自动进入连续办理界面
                        if (performSequence.equals(ActivityDefPerformSequence.AUTOSIGN) && activityInst.isCanSignReceive() && performType.equals(ActivityDefPerformtype.SINGLE)) {
                            result.setData(new ReturnType(ReturnType.MAINCODE_SUCCESS));
                        }

                    }
                    break;
                default:
                    break;
            }

        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> mrouteto(RouteToBean routeToBean) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        result.setData(new ReturnType(ReturnType.MAINCODE_FAIL));
        try {

            String activityInstId = routeToBean.getActivityInstId();
            List<String> activityDefIds = new ArrayList<String>();

            Map<String, PerformBean> performSelect = routeToBean.getMultiSelect();

            Set<String> keySet = performSelect.keySet();
            List<Map<RightCtx, Object>> ctxs = new ArrayList<Map<RightCtx, Object>>();

            for (String activityDefId : keySet) {
                RouteBean routeBean = performSelect.get(activityDefId).getPerformSelect();
                Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
                activityDefIds.add(routeBean.getNextActivityDefId());

                String[] performArr = StringUtility.split(routeBean.getPerforms().getPerforms(), ";");
                String[] readArr = StringUtility.split(routeBean.getReaders().getReaders(), ";");
                List<String> performList = Arrays.asList(performArr);
                ctx.put(RightCtx.PERFORMERS, performList);
                ctx.put(RightCtx.READERS, Arrays.asList(readArr));
                ctxs.add(ctx);
            }
            getClient().canSignReceive(activityInstId, null);
            ReturnType returnType = getClient().routeTo(activityInstId, activityDefIds, ctxs);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<ActivityInst> copyActivityInstByHistory(String activityHistoryInstId, Boolean isnew) {
        ResultModel<ActivityInst> result = new ResultModel<ActivityInst>();
        try {

            ActivityInst activityInst = this.getClient().copyActivityInstByHistory(activityHistoryInstId, null, isnew);
            result.setData(activityInst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ActivityInst> newActivityInstByActivityDefId(String processInstId, String activityDefId) {
        return null;
    }

    @Override
    public ResultModel<ReturnType> copyTo(String activityHistoryInstId, @RequestBody String[] readers) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().copyTo(activityHistoryInstId, Arrays.asList(readers));
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> canTakeBack(String activityInstID) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            Boolean returnType = this.getClient().canTakeBack(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> takeBack(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().takeBack(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> canEndRead(String activityInstID) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            Boolean returnType = this.getClient().canEndRead(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> endRead(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().endRead(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> endTask(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().endTask(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> abortedTask(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().abortedTask(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> routeBack(String fromActivityInstID, String toActivityInstHistoryID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().routeBack(fromActivityInstID, toActivityInstHistoryID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> canPerform(String activityInstID) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            Boolean returnType = this.getClient().canPerform(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> canSignReceive(String activityInstID) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            Boolean returnType = this.getClient().canSignReceive(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<ReturnType> signReceive(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().signReceive(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> suspendActivityInst(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().suspendActivityInst(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> resumeActivityInst(String activityInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().resumeActivityInst(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> suspendProcessInst(String processInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().suspendProcessInst(processInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> resumeProcessInst(String processInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().resumeProcessInst(processInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> abortProcessInst(String processInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().abortProcessInst(processInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> completeProcessInst(String processInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().completeProcessInst(processInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> deleteProcessInst(String processInstID) {
        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {
            ReturnType returnType = this.getClient().deleteProcessInst(processInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<ReturnType> display(String activityInstId) {

        ResultModel<ReturnType> result = new ResultModel<ReturnType>();
        try {

            ReturnType returnType = this.getClient().display(activityInstId);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<ActivityInstHistory> getActivityInstHistory(String activityInstHistoryID) {
        ResultModel<ActivityInstHistory> result = new ResultModel<ActivityInstHistory>();
        try {
            ActivityInstHistory activityInstHistory = this.getClient().getActivityInstHistory(activityInstHistoryID);
            result.setData(activityInstHistory);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<RouteInst> getRouteInst(String routeInstId) {
        ResultModel<RouteInst> result = new ResultModel<RouteInst>();
        try {
            RouteInst routeInst = this.getClient().getRouteInst(routeInstId);
            result.setData(routeInst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> canRouteBack(String activityInstID) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            Boolean returnType = this.getClient().canRouteBack(activityInstID, new HashMap());
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    /**
     * @return
     */
    public WorkflowClientService getClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }

}


