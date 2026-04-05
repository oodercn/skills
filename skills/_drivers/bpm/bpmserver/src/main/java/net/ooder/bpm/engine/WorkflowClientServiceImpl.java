
package net.ooder.bpm.engine;

import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.database.DbRouteInst;
import net.ooder.bpm.engine.event.BPMCoreEventControl;
import net.ooder.bpm.engine.event.EIActivityEvent;
import net.ooder.bpm.engine.event.EIProcessEvent;
import net.ooder.bpm.engine.event.EIRightEvent;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.*;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.engine.query.FilterChain;
import net.ooder.bpm.engine.util.UtilTimer;
import net.ooder.bpm.enums.activitydef.ActivityDefJoin;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryStatus;
import net.ooder.bpm.enums.event.ActivityEventEnums;
import net.ooder.bpm.enums.event.ProcessEventEnums;
import net.ooder.bpm.enums.event.RightEventEnums;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.bpm.enums.process.ProcessInstStartType;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightPermission;
import net.ooder.bpm.routefitle.RouteConditionFilter;
import net.ooder.command.Command;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.Condition;
import net.ooder.common.Filter;
import net.ooder.config.ActivityDefImpl;
import net.ooder.config.ListResultModel;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: WorkflowClientService客户端服务接口的实现。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0O
 */
public class WorkflowClientServiceImpl implements WorkflowClientService, Serializable {

    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, WorkflowClientServiceImpl.class);

    private static final int TIMER_LEVEL = 5;

    private JDSServer jdsServer;

    private WorkflowEngine workflowEngine;

    private RightEngine rightEngine;

    private DeviceEngine deviceEngine;

    private ServiceEngine serviceEngine;

    private EventEngine eventEngine;

    private DataEngine dataEngine;

    private FileEngine fileEngine;

    // Org Manager
    public OrgManager orgManager = null;

    private JDSClientService jdsClient;

    /**
     * 根据Session中的JDSSessionHandle信息取得工作流系统客户 接口WorkflowClientService
     *
     * @return 工作流系统客户接口实现
     * @throws JDSException
     */
    WorkflowClientServiceImpl(JDSClientService jdsClient) throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        this.jdsClient = jdsClient;
        String systemCode = jdsClient.getSystemCode();
        this.workflowEngine = WorkflowEngineImpl.getEngine(systemCode);
        this.rightEngine = BPMServer.getRigthEngine(systemCode);
        this.deviceEngine = BPMServer.getDeviceEngine(systemCode);
        this.dataEngine = BPMServer.getDataEngine(systemCode);
        this.fileEngine = BPMServer.getFileEngine(systemCode);
        this.eventEngine = BPMServer.getEventEngine(systemCode);
        this.serviceEngine = BPMServer.getServiceEngine(systemCode);

    }

    WorkflowClientServiceImpl(JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        this.jdsClient = jdsServer.getJDSClientService(sessionHandle, JDSServer.getClusterClient().getSystem(systemCode).getConfigname());
        this.workflowEngine = WorkflowEngineImpl.getEngine(systemCode);
        this.rightEngine = BPMServer.getRigthEngine(systemCode);
        this.dataEngine = BPMServer.getDataEngine(systemCode);
        this.fileEngine = BPMServer.getFileEngine(systemCode);
        this.eventEngine = BPMServer.getEventEngine(systemCode);
        this.serviceEngine = BPMServer.getServiceEngine(systemCode);
        // this(this.jdsClient);

    }

    // ---------------------------------------------事件相关方法，EventControl
    // add by lxl 2004-02-14

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param inst    the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireProcessEvent(EIProcessInst inst, ProcessEventEnums eventID) throws BPMException {
        fireProcessEvent(inst, eventID, null);
    }

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param inst    the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireProcessEvent(EIProcessInst inst, ProcessEventEnums eventID, Map<RightCtx, Object> eventContext) throws BPMException {
        eventContext = fillInUserID(eventContext);
        EIProcessEvent event = new EIProcessEvent(inst, eventID);
        event.setClientService(this);
        event.setContextMap(eventContext);
        try {
            BPMCoreEventControl.dispatchProcessEvent(event, getSystemCode());
        } catch (JDSException e) {
            throw new BPMException(e);
        }
    }

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param inst
     *            the process which fire thie event, the source of event
     * @param eventID
     *            the event type
     */
    // private void fireActivityEvent(EIActivityInst inst, int eventID)
    // throws BPMException {
    // fireActivityEvent(inst, eventID, null);
    // }

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param inst    the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireActivityEvent(EIActivityInst inst, ActivityEventEnums eventID, Map<RightCtx, Object> eventContext) throws BPMException {
        eventContext = fillInUserID(eventContext);
        EIActivityEvent event = new EIActivityEvent(inst, eventID);
        event.setClientService(this);
        event.setContextMap(eventContext);
        try {
            BPMCoreEventControl.dispatchActivityEvent(event, getSystemCode());
        } catch (JDSException e) {
            throw new BPMException(e);
        }
    }

    /**
     * fire a right event and transfer this event to EventControl
     *
     * @param inst    the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireRightEvent(EIActivityInst inst, RightEventEnums eventID, Map<RightCtx, Object> eventContext) throws BPMException {
        eventContext = fillInUserID(eventContext);

        EIRightEvent event = new EIRightEvent(inst, eventID);
        event.setClientService(this);
        event.setContextMap(eventContext);
//        try {
//            BPMCoreEventControl.dispatchRightEvent(event, getSystemCode());
//        } catch (JDSException e) {
//            e.printStackTrace();
//            //throw new BPMException(e);
//        }
    }

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param insts   the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireActivityEvent(EIActivityInst[] insts, ActivityEventEnums eventID) throws BPMException {
        fireActivityEvent(insts, eventID, null);
    }

    /**
     * fire a process event and transfer this event to EventControl
     *
     * @param insts   the process which fire thie event, the source of event
     * @param eventID the event type
     */
    private void fireActivityEvent(EIActivityInst[] insts, ActivityEventEnums eventID, Map<RightCtx, Object> eventContext) throws BPMException {
        eventContext = fillInUserID(eventContext);
        EIActivityEvent event = new EIActivityEvent(insts, eventID);
        event.setClientService(this);
        event.setContextMap(eventContext);
        try {
            BPMCoreEventControl.dispatchActivityEvent(event, getSystemCode());
        } catch (JDSException e) {
            throw new BPMException(e);
        }
    }

    public String getSystemCode() {
        return this.getJdsClient().getSystemCode();
    }

    public JDSSessionHandle getSessionHandle() {
        return this.getJdsClient().getSessionHandle();
    }

    // --------------------------------------------- 登陆注销操作

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#connect(net.ooder.bpm.engine.ConnectInfo)
     */
    public void connect(ConnectInfo connInfo) throws JDSException {

        this.getJdsClient().connect(connInfo);

    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#disconnect()
     */
    public ReturnType disconnect() throws JDSException {
        this.getJdsClient().disconnect();

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public JDSClientService getJdsClient() {
        return jdsClient;
    }

    public void setJdsClient(JDSClientService jdsClient) {
        this.jdsClient = jdsClient;
    }

    public ConnectInfo getConnectInfo() {
        return this.getJdsClient().getConnectInfo();
    }

    // --------------------------------------------- 定义相关方法

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessDefList(java.util.List)
     */
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        try {
            checkLogined();
            BPMCondition resultCon = condition;
            Filter resultFilter = filter;

            // 加入系统判断条件
            // String inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF WHERE
            // BPM_PROCESSDEF.SYSTEMCODE='" + this.getJdsClient().getSystemCode() + "'";

            BPMCondition sysCon = new BPMCondition(BPMConditionKey.PROCESSDEF_VERSION_PUBLICATIONSTATUS, Operator.EQUALS, ProcessDefVersionStatus.RELEASED.getType());
            if (resultCon == null) {
                resultCon = sysCon;
            } else {
                resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
            }

            // 在权限上下文参数中加入当前登陆人员ID
            Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
            // 从权限引擎中获取权限相关的流程定义过滤器
            Filter rightFilter = rightEngine.getProcessDefListFilter(rightCtx);
            if (rightFilter != null) {
                FilterChain filterChain = new FilterChain();
                filterChain.addFilter(resultFilter);
                filterChain.addFilter(rightFilter);
                resultFilter = filterChain;

                // 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
                if (rightFilter instanceof BPMCondition) {
                    resultCon.addCondition((BPMCondition) rightFilter, JoinOperator.JOIN_AND);
                }
            }


            // 获取流程定义列表
            List processDefList = workflowEngine.getProcessDefVersionList(resultCon, resultFilter);
            logger.debug(timer.timerString(TIMER_LEVEL, "getProcessDefList"));

            WorkflowListProxy proxy = new WorkflowListProxy(processDefList, getSystemCode());
            ListResultModel<List<ProcessDefVersion>> resultModel = new ListResultModel<List<ProcessDefVersion>>();
            resultModel.setData(proxy);
            resultModel.setSize(proxy.size());

            return resultModel;
        } catch (Exception e) {
            throw new BPMException("getProcessDefList error.", e, BPMException.GETPROCESSDEFLISTERROR);
        }
    }

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition 查询条件，例如根据流程定义的名称进行查询。
     * @param filter    扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    public ListResultModel<List<ProcessDef>> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        try {
            checkLogined();
            BPMCondition resultCon = condition;
            Filter resultFilter = filter;

            BPMCondition sysCon = new BPMCondition(BPMConditionKey.PROCESSDEF_ACCESSLEVEL, Operator.EQUALS, ProcessDefAccess.Public.getType());
            if (resultCon == null) {
                resultCon = sysCon;
            }

            // 在权限上下文参数中加入当前登陆人员ID
            Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
            // 从权限引擎中获取权限相关的流程定义过滤器
            Filter rightFilter = rightEngine.getProcessDefListFilter(rightCtx);
            if (rightFilter != null) {
                FilterChain filterChain = new FilterChain();
                filterChain.addFilter(resultFilter);
                filterChain.addFilter(rightFilter);
                resultFilter = filterChain;

                // 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
                if (rightFilter instanceof BPMCondition) {
                    resultCon.addCondition((BPMCondition) rightFilter, JoinOperator.JOIN_AND);
                }
            }

            // 获取流程定义列表
            List processDefList = workflowEngine.getProcessDefList(resultCon, resultFilter);
            logger.debug(timer.timerString(TIMER_LEVEL, "getProcessDefList"));


            WorkflowListProxy proxy = new WorkflowListProxy(processDefList, getSystemCode());
            ListResultModel<List<ProcessDef>> resultModel = new ListResultModel<List<ProcessDef>>();
            resultModel.setData(proxy);
            resultModel.setSize(proxy.size());

            return resultModel;

        } catch (Exception e) {
            throw new BPMException("getProcessDefList error.", e, BPMException.GETPROCESSDEFLISTERROR);
        }
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessDef(java.lang.String)
     */
    public ProcessDefVersion getProcessDefVersion(String processDefID) throws BPMException {
        checkLogined();
        EIProcessDefVersion eiProcessDefVersion = EIProcessDefVersionManager.getInstance().loadByKey(processDefID);
        // add by lxl 2004-01-16
        if (eiProcessDefVersion == null) {
            return null;
        }
        return new ProcessDefVersionProxy(eiProcessDefVersion, getSystemCode());
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessDef(java.lang.String)
     */
    public ProcessDef getProcessDef(String processDefID) throws BPMException {
        checkLogined();
        EIProcessDef eiProcessDef = EIProcessDefManager.getInstance().loadByKey(processDefID);
        // add by lxl 2004-01-16
        if (eiProcessDef == null) {
            return null;
        }
        return new ProcessDefProxy(eiProcessDef, getSystemCode());
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getActivityDef(java.lang.String)
     */
    public ActivityDef getActivityDef(String activityDefID) throws BPMException {
        checkLogined();
        EIActivityDef eiActivityDef = EIActivityDefManager.getInstance().loadByKey(activityDefID);
        // add by lxl 2004-01-16
        if (eiActivityDef == null) {
            return null;
        }
        return new ActivityDefProxy(eiActivityDef, this.getSystemCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#getRouteDef(java.lang.String)
     */
    public RouteDef getRouteDef(String routeDefId) throws BPMException {
        checkLogined();
        EIRouteDef eiRouteDef = EIRouteDefManager.getInstance().loadByKey(routeDefId);
        // add by lxl 2004-01-16
        if (eiRouteDef == null) {
            return null;
        }
        return new RouteDefProxy(eiRouteDef, getSystemCode());
    }

    // --------------------------------------------- 实例相关方法

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessInstList(net.ooder.bpm.engine.query.Condition,
     * net.ooder.bpm.engine.query.Filter, java.util.Map)
     */
    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums rightCondition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        try {
            checkLogined();
            BPMCondition resultCon = condition;
            Filter resultFilter = filter;

            // 加入系统判断条件
            String inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.CLASSIFICATION in ('" + this.getJdsClient().getSystemCode() + "','scene')";
            BPMCondition sysCon = new BPMCondition(BPMConditionKey.PROCESSINST_PROCESSDEF_ID, Operator.IN, inSQL);
            if (resultCon == null) {
                resultCon = sysCon;
            } else {
                resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
            }

            Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
            Filter rightFilter = rightEngine.getProcessInstListFilter(rightCondition, rightCtx);
            if (rightFilter != null) {
                FilterChain filterChain = new FilterChain();
                filterChain.addFilter(resultFilter);
                filterChain.addFilter(rightFilter);
                resultFilter = filterChain;

                // 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
                if (rightFilter instanceof BPMCondition) {
                    resultCon.addCondition((BPMCondition) rightFilter, JoinOperator.JOIN_AND);
                }
            }
            List processInstList = workflowEngine.getProcessInstList(resultCon, resultFilter);
            logger.debug(timer.timerString(TIMER_LEVEL, "getProcessInstList"));

            WorkflowListProxy proxy = new WorkflowListProxy(processInstList, getSystemCode());
            ListResultModel<List<ProcessInst>> resultModel = new ListResultModel<List<ProcessInst>>();
            resultModel.setData(proxy);
            resultModel.setSize(proxy.size());

            return resultModel;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BPMException("getProcessInstList error.", e, BPMException.GETPROCESSINSTLISTERROR);
        }
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getActivityInstListbyUserId(java.lang.String, java.lang.String)
     */
    public ListResultModel<List<ActivityInst>> getActivityInstList(BPMCondition condition, RightConditionEnums rightCondition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        // modify by lxl 2004-01-15


        UtilTimer timer = new UtilTimer();
        try {
            checkLogined();
            BPMCondition resultCon = condition;
            Filter resultFilter = filter;

            String inSQL = "SELECT BPM_PROCESSDEF_VERSION.PROCESSDEF_ID FROM BPM_PROCESSDEF, BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF.PROCESSDEF_ID=BPM_PROCESSDEF_VERSION.PROCESSDEF_ID ";


            // 加入系统判断条件
//            String inSQL = "SELECT BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF, BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF.PROCESSDEF_ID=BPM_PROCESSDEF_VERSION.PROCESSDEF_ID AND BPM_PROCESSDEF.SYSTEMCODE in ('"
//                    + this.getJdsClient().getSystemCode() + "','scene')";
            BPMCondition sysCon = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSDEF_ID, Operator.IN, inSQL);

            if (resultCon == null) {
                resultCon = sysCon;
            } else {
                resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
            }

            // 增加分裂过滤 2013-10- 26
            BPMCondition splitCon = new BPMCondition(BPMConditionKey.ACTIVITYINST_DEALMETHOD, Operator.NOT_EQUAL, ActivityInstDealMethod.DEALMETHOD_SPLITED.getType());
            resultCon.addCondition(splitCon, JoinOperator.JOIN_AND);

            Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
            Filter rightFilter = rightEngine.getActivityInstListFilter(rightCondition, rightCtx);

            if (rightFilter != null) {
                FilterChain filterChain = new FilterChain();
                filterChain.addFilter(resultFilter);
                filterChain.addFilter(rightFilter);
                resultFilter = filterChain;

                // 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
                if (rightFilter instanceof BPMCondition) {
                    resultCon.addCondition((BPMCondition) rightFilter, JoinOperator.JOIN_AND);
                }
            }

            List activityInstList = workflowEngine.getActivityInstList(resultCon, resultFilter);
            logger.debug(timer.timerString(TIMER_LEVEL, "getActivityInstList"));


            WorkflowListProxy proxy = new WorkflowListProxy(activityInstList, getSystemCode());
            ListResultModel<List<ActivityInst>> resultModel = new ListResultModel<List<ActivityInst>>();
            resultModel.setData(proxy);
            resultModel.setSize(proxy.size());

            return resultModel;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BPMException("getActivityInstList error.", e, BPMException.GETACTIVITYINSTLISTERROR);
        }

        // checkLogined();
        // // TODO 需要从权限引擎获得Condition和Filter
        //
        // List activityInstList = workflowEngine.getActivityInstList(condition,
        // filter);
        // return new WorkflowListProxy(activityInstList);
    }

    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx, boolean noSplit) throws BPMException {
        checkLogined();
        List list = workflowEngine.getLastActivityInstHistoryListByActvityInst(actvityInstId, noSplit);
        return new WorkflowListProxy(list, getSystemCode());
    }

    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId 活动实例ID
     * @param ctx           权限相关上下文参数
     * @return List<ActivityInstHistory>
     * @throws BPMException
     */
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        List list = workflowEngine.getLastActivityInstHistoryListByActvityInst(actvityInstId);
        return new WorkflowListProxy(list, getSystemCode());
    }

    /**
     * 取得所有活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId 活动实例ID
     * @param ctx           权限相关上下文参数
     * @return List<ActivityInstHistory>
     * @throws BPMException
     */
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        List list = workflowEngine.getActivityInstHistoryListByActvityInst(actvityInstId);
        return new WorkflowListProxy(list, getSystemCode());
    }

    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId 活动实例历史ID
     * @param ctx                   权限相关上下文参数
     * @return List<ActivityInst>
     * @throws BPMException
     */
    public List<ActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId,

                                                                         Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        List routeList = workflowEngine.getActivityInstHistoryOutRoute(activityInstHistoryId);
        List activityInstList = new ArrayList<ActivityInst>();
        for (int k = 0; k < routeList.size(); k++) {
            DbRouteInst routeInst = (DbRouteInst) routeList.get(k);
            ActivityInst inst = getActivityInst(routeInst.getToActivityId());
            if (inst != null) {
                activityInstList.add(inst);
            }
        }
        return activityInstList;
    }

    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @param processInstId 流程实例ID
     * @param ctx           权限相关上下文参数
     * @return
     * @throws BPMException
     */
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        List list = workflowEngine.getActivityInstHistoryListByProcessInst(processInstId);

        WorkflowListProxy proxy = new WorkflowListProxy(list, getSystemCode());
        ListResultModel<List<ActivityInstHistory>> resultModel = new ListResultModel<List<ActivityInstHistory>>();
        resultModel.setData(proxy);
        resultModel.setSize(proxy.size());

        return resultModel;

    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessInstbyId(java.lang.String)
     */
    public ProcessInst getProcessInst(String processInstID) throws BPMException {
        checkLogined();
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        if (eiProcessInst == null) {
            return null;
        }
        return new ProcessInstProxy(eiProcessInst, getSystemCode());
    }

    /**
     * 更新流程实例名称（公文标题）
     *
     * @param name 新名称，长度在100字节以内
     * @return
     * @throws BPMException
     */
    public ReturnType updateProcessInstName(String processInstId, String name) throws BPMException {
        // 判断名称长度
        int length = name.getBytes().length;
        if (length >= 100) {
            return new ReturnType(ReturnType.MAINCODE_FAIL, "名称长度不能超过100个字节！");
        }
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstId);
        eiProcessInst.setName(name);
        EIProcessInstManager.getInstance().save(eiProcessInst);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 更新流程实例紧急程度(兼容旧接口，新接口不再使用)
     *
     * @param processInstId
     * @param urgency       新的紧急程度
     * @return
     * @throws BPMException
     */
    public ReturnType updateProcessInstUrgency(String processInstId, String urgency) throws BPMException {
        // 判断名称长度
        int length = urgency.getBytes().length;
        if (length >= 20) {
            return new ReturnType(ReturnType.MAINCODE_FAIL, "紧急程度长度不能超过20个字节！");
        }
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstId);
        eiProcessInst.setUrgency(urgency);
        EIProcessInstManager.getInstance().save(eiProcessInst);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#getActivityInstbyId(java.lang.String)
     */
    public ActivityInst getActivityInst(String activityInstID) throws BPMException {
        checkLogined();
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);
        if (eiActivityInst == null) {
            return null;
        }
        return new ActivityInstProxy(eiActivityInst, getSystemCode());
    }

    @Override
    public RouteInst getRouteInst(String routeInstId) throws BPMException {
        checkLogined();
        EIRouteInst eiRouteInst = EIRouteInstManager.getInstance().loadByKey(routeInstId);
        if (eiRouteInst == null) {
            return null;
        }
        return new RouteInstProxy(eiRouteInst, getSystemCode());
    }

    /**
     * 按照活动实例历史的ID取得活动实例历史对象
     *
     * @param activityInstHistoryID 活动实例历史ID
     * @return 指定活动实例历史ID的ActivityInst对象
     * @throws BPMException
     */
    public ActivityInstHistory getActivityInstHistory(String activityInstHistoryID) throws BPMException {
        checkLogined();
        EIActivityInstHistory eiActivityInstHistory = EIActivityInstHistoryManager.getInstance().loadByKey(activityInstHistoryID);
        if (eiActivityInstHistory == null) {
            return null;
        }
        return new ActivityInstHistoryProxy(eiActivityInstHistory, getSystemCode());

    }

    // --------------------------------------------- 流程启动相关方法
    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#startProcess(java.lang.String, java.lang.String,
     * java.lang.String, java.util.Map)
     */
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        checkLogined();
        beginTransaction();
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        ProcessInst processInst;
        ActivityInst activityInst;
        ReturnType rt;
        ReturnType rtd;
        ReturnType rtf;
        ReturnType rtdi;
        try {
            // 创建流程实例
            EIProcessInst eiProcessInst = workflowEngine.createProcessInst(processDefId, processInstName, processUrgency);

            // 1.判断是否有权启动
            ReturnType returnType = rightEngine.hasRightToStartProcess(eiProcessInst.getProcessInstId(), rightCtx);
            if (returnType.isSucess() == false) {
                throw new BPMException("Create process instance error.", BPMException.CREATEPROCESSINSTANCEERROR);
            }

            rt = rightEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            deviceEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            serviceEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            eventEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            rtd = dataEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            rtf = fileEngine.createProcessInst(eiProcessInst.getProcessInstId(), rightCtx);
            if (rt.mainCode() == ReturnType.MAINCODE_FAIL || rtd.mainCode() == ReturnType.MAINCODE_FAIL || rtf.mainCode() == ReturnType.MAINCODE_FAIL) {
                rollbackTransaction();
                if (rt.toString() == null) {
                    throw new BPMException("Create process instance error.", BPMException.CREATEPROCESSINSTANCEERROR);
                } else {
                    throw new BPMException(rt.toString(), BPMException.CREATEPROCESSINSTANCEERROR);
                }
            }
            // 启动流程实例
            fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTING); // 触发流程启动事件
            EIActivityInst eiActivityInst = workflowEngine.startProcessInst(eiProcessInst.getProcessInstId());
            rt = rightEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            rtdi = deviceEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            eventEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            serviceEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            rtd = dataEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), ctx);
            rtf = fileEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            if (rt.mainCode() == ReturnType.MAINCODE_FAIL || rtd.mainCode() == ReturnType.MAINCODE_FAIL || rtf.mainCode() == ReturnType.MAINCODE_FAIL) {
                rollbackTransaction();
                if (rt.toString() == null) {
                    throw new BPMException("Start process instance error.", BPMException.STARTPROCESSINSTANCEERROR);
                } else {
                    throw new BPMException(rt.toString(), BPMException.STARTPROCESSINSTANCEERROR);
                }
            }
            // 触发活动初始化事件
            fireActivityEvent(eiActivityInst, ActivityEventEnums.INITED, ctx);
            fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED); // 触发流程启动事件

            commitTransaction();
            processInst = new ProcessInstProxy(eiProcessInst, getSystemCode());
            logger.debug(timer.timerString(TIMER_LEVEL, "newProcess"));
        } catch (BPMException bpme) {
            rollbackTransaction();
            throw bpme;
        } catch (Exception e) {
            e.printStackTrace();
            rollbackTransaction();
            throw new BPMException("newProcess error.", e, BPMException.NEWPROCESSINSTANCEERROR);
        }

        return processInst;
    }

    /**
     * 自动启动流程实例，将定义的流程启动人实例化后作为该起始活动的办理人。
     */
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        checkLogined();
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        ProcessInst processInst;
        ActivityInst activityInst;
        ReturnType rt;
        ReturnType rtd;
        ReturnType rtf;
        ReturnType rtdi;
        try {
            // 创建流程实例
            EIProcessInst eiProcessInst = workflowEngine.createProcessInst(processDefId, processInstName, processUrgency, initType);
            // 如果不是自动启动，需要判断启动权限
            if (!ProcessInstStartType.AUTO.getType().equals(initType)) {
                // 1.判断是否有权启动
                ReturnType returnType = rightEngine.hasRightToStartProcess(eiProcessInst.getProcessInstId(), rightCtx);
                if (returnType.isSucess() == false) {
                    throw new BPMException("Create process instance error.", BPMException.CREATEPROCESSINSTANCEERROR);
                }
            }

            rt = rightEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);
            rtdi = deviceEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);
            eventEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);
            serviceEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);
            rtd = dataEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);

            rtf = fileEngine.createProcessInst(eiProcessInst.getProcessInstId(), initType, rightCtx);
            if (rt.mainCode() == ReturnType.MAINCODE_FAIL || rtd.mainCode() == ReturnType.MAINCODE_FAIL || rtf.mainCode() == ReturnType.MAINCODE_FAIL) {
                rollbackTransaction();
                if (rt.toString() == null) {
                    throw new BPMException("Create process instance error.", BPMException.CREATEPROCESSINSTANCEERROR);
                } else {
                    throw new BPMException(rt.toString(), BPMException.CREATEPROCESSINSTANCEERROR);
                }
            }

            // 启动流程实例
            fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTING); // 触发流程启动事件
            EIActivityInst eiActivityInst = workflowEngine.startProcessInst(eiProcessInst.getProcessInstId());
            // 如果是自动启动，需要实例化办理人（启动人）
            if (ProcessInstStartType.AUTO.getType().equals(initType)) {
                // 传入当前的活动实例id与流程实例id
                Map tempCtx = new HashMap();
                tempCtx.putAll(ctx);
                tempCtx.put(RightCtx.ACTIVITYINST_ID.getType(), eiActivityInst.getActivityInstId());
                tempCtx.put(RightCtx.PROCESSINST_ID.getType(), eiActivityInst.getProcessInstId());
                List users = rightEngine.getPerformerCandidate(eiActivityInst.getActivityDefId(), tempCtx);
                if (users.isEmpty()) {
                    throw new BPMException("Start process instance error, Can't find process starter!");
                }
                rightCtx.put(RightCtx.USERS, users);
            }

            rt = rightEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);

            rtdi = deviceEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);

            serviceEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            eventEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);

            rtd = dataEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), ctx);
            rtf = fileEngine.startProcessInst(eiProcessInst.getProcessInstId(), eiActivityInst.getActivityInstId(), rightCtx);
            if (rt.mainCode() == ReturnType.MAINCODE_FAIL || rtd.mainCode() == ReturnType.MAINCODE_FAIL || rtf.mainCode() == ReturnType.MAINCODE_FAIL) {

                rollbackTransaction();
                if (rt.toString() == null) {
                    throw new BPMException("Start process instance error.", BPMException.STARTPROCESSINSTANCEERROR);
                } else {
                    throw new BPMException(rt.toString(), BPMException.STARTPROCESSINSTANCEERROR);
                }
            }
            // 触发活动初始化事件
            fireActivityEvent(eiActivityInst, ActivityEventEnums.INITED, ctx);
            if (ProcessInstStartType.AUTO.getType().equals(initType)) {
                // 触发流程启动事件，将上下文环境传给监听器。
                fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED, ctx);
            } else {
                fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED); // 触发流程启动事件
            }

            commitTransaction();
            processInst = new ProcessInstProxy(eiProcessInst, getSystemCode());
            logger.debug(timer.timerString(TIMER_LEVEL, "newProcess"));
        } catch (BPMException bpme) {
            rollbackTransaction();
            throw bpme;
        } catch (Exception e) {
            rollbackTransaction();
            throw new BPMException("newProcess error.", e, BPMException.NEWPROCESSINSTANCEERROR);
        }

        return processInst;
    }

    // --------------------------------------------- 路由相关方法

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#getNextRoutes(String startActivityInstID, Condition condition,
     * Filter routeFilter, Map<RightCtx,Object> ctx)
     */
    public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);

        ActivityInst activityInst = getActivityInst(startActivityInstID);
        rightCtx.put(RightCtx.ACTIVITYINST_ID, startActivityInstID);
        rightCtx.put(RightCtx.PROCESSINST_ID, activityInst.getProcessInstId());
        RouteConditionFilter routeConditionFilter = new RouteConditionFilter(rightCtx);
        FilterChain filterChain = new FilterChain();
        if (routeFilter != null) {
            filterChain.addFilter(routeFilter);
        }
        filterChain.addFilter(routeConditionFilter);

        List<EIRouteDef> routeList = workflowEngine.getNextRoutes(startActivityInstID, condition, filterChain);
        return new WorkflowListProxy(routeList, getSystemCode());
    }

    public ReturnType display(String activityInstId) throws BPMException {
        ActivityInst activityInst = getActivityInst(activityInstId);
        // 活动还没有开始运行
        if (ActivityInstStatus.notStarted.equals(activityInst.getState())) {
            // 如果当前办理人只有一个人，而且配置了自动签收，则执行自动签收操作。
            List performers = (List) getActivityInstRightAttribute(activityInstId, ActivityInstRightAtt.PERFORMER, null);
            if (performers != null && performers.size() == 1) {
                ActivityDefPerformSequence performSequence = getActivityDefRightAttribute(activityInst.getActivityDefId()).getPerformSequence();
                // 自动签收还未签收时
                if (ActivityDefPerformSequence.AUTOSIGN.equals(performSequence) && canSignReceive(activityInstId, null)) {
                    signReceive(activityInstId, null);
                }
            }
        }

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstId);
        fireActivityEvent(eiActivityInst, ActivityEventEnums.DISP, null);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return copyActivityInstByHistory(activityHistoryInstId, ctx, false);
    }

    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx, boolean isnew) throws BPMException {

        EIActivityInst inst = workflowEngine.getSplitActivityInst(activityHistoryInstId, isnew);
        // 添加权限
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);

        rightEngine.copyActivityInstByHistory(inst.getActivityInstId(), activityHistoryInstId, rightCtx);

        dataEngine.copyActivityInstByHistory(inst.getActivityInstId(), activityHistoryInstId, rightCtx);
        fileEngine.copyActivityInstByHistory(inst.getActivityInstId(), activityHistoryInstId, rightCtx);

        return new ActivityInstProxy(inst, this.getJdsClient().getSystemCode());
    }

    public ActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {

        ActivityInst activityInst = new ActivityInstProxy(this.workflowEngine.newActivityInstByActivityDefId(processInstId, activityDefId), getSystemCode());
        ctx = fillInUserID(ctx);
        List users = new ArrayList();
        users.add(ctx.get(RightCtx.USERID));

        ctx.put(RightCtx.PERFORMERS, users);
        workflowEngine.routeTo(activityInst.getActivityInstId(), activityDefId);

        dataEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);
        if (this.getActivityDef(activityDefId).getImplementation().equals(ActivityDefImpl.No)) {
            rightEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);
        } else if (this.getActivityDef(activityDefId).getImplementation().equals(ActivityDefImpl.Event)) {
            serviceEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);
        } else if (this.getActivityDef(activityDefId).getImplementation().equals(ActivityDefImpl.Service)) {
            eventEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);
        } else if (this.getActivityDef(activityDefId).getImplementation().equals(ActivityDefImpl.Device)) {
            deviceEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);
        }

        fileEngine.routeTo(activityInst.getActivityInstId(), activityDefId, ctx);

        return activityInst;
    }

    /**
     * @param startActivityInstID
     * @param nextActivityDefID
     * @param ctx
     * @return
     * @throws BPMException
     */
    private EIActivityInst signle(String startActivityInstID, String nextActivityDefID, Map<RightCtx, Object> ctx, Map<RightCtx, Object> eventContext) throws BPMException {

        EIActivityInst inst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);
        fireActivityEvent(inst, ActivityEventEnums.ROUTING, eventContext); // 触发活动路由事件

        EIActivityInst nextInst = inst;

        ctx = fillInUserID(ctx);

        if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.No)) {
            List<String> performers = (List<String>) ctx.get(RightCtx.PERFORMERS);
            rightEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);
        } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Device)) {
            deviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);
        } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Event)) {
            eventEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);
        } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Service)) {
            serviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);
        }
        dataEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);
        fileEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, ctx);

        nextInst = workflowEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID);

        ActivityDef activityDef = this.getActivityDef(nextActivityDefID);

        if (activityDef == null) {
            throw new BPMException("The activity definition not found!");
        }

        ActivityDefJoin join = activityDef.getJoin();

        if (join != null && join.equals(ActivityDefJoin.JOIN_AND)) {
            List<EIActivityInst> activityInsts = workflowEngine.combinableActivityInsts(inst.getActivityInstId());
            if (activityInsts.size() > 1) {
                // 挂起或者合并活动实例
                String suspendOrCombine = workflowEngine.suspendOrCombine(inst.getActivityInstId());

                if (ActivityInstSuSpend.SUSPEND.getType().equals(suspendOrCombine)) {
                    workflowEngine.suspendActivityInst(startActivityInstID);
                    if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
                        rightEngine.suspendActivityInst(startActivityInstID, ctx);
                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
                        deviceEngine.suspendActivityInst(startActivityInstID, ctx);
                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
                        serviceEngine.suspendActivityInst(startActivityInstID, ctx);
                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
                        eventEngine.suspendActivityInst(startActivityInstID, ctx);
                    }

                    fileEngine.suspendActivityInst(startActivityInstID, ctx);

                    dataEngine.suspendActivityInst(startActivityInstID, ctx);
                } else if (ActivityInstSuSpend.COMBINE.getType().equals(suspendOrCombine)) {
                    List<String> activityInstIds = new ArrayList<String>();
                    for (int k = 0; k < activityInsts.size(); k++) {
                        activityInstIds.add(activityInsts.get(k).getActivityInstId());
                    }
                    String[] activityInstIdArr = activityInstIds.toArray(new String[]{});
                    if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

                        rightEngine.combineActivityInsts(activityInstIdArr, ctx);

                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
                        deviceEngine.combineActivityInsts(activityInstIdArr, ctx);
                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
                        serviceEngine.combineActivityInsts(activityInstIdArr, ctx);
                    } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
                        eventEngine.combineActivityInsts(activityInstIdArr, ctx);
                    }

                    fileEngine.combineActivityInsts(activityInstIdArr, ctx);
                    dataEngine.combineActivityInsts(activityInstIdArr, ctx);
                    nextInst = workflowEngine.combineActivityInsts(activityInstIdArr);

                    if (!inst.getActivityDefId().equals(nextInst.getActivityDef())) {
                        // 合并工程并完成分裂流程推进

                        nextInst = workflowEngine.routeTo(nextInst.getActivityInstId(), (String) nextActivityDefID);
                    }

                }
            }
        }

        fireActivityEvent(nextInst, ActivityEventEnums.ROUTED, eventContext);
        return nextInst;
    }

    /**
     * 多设备命令并行分裂活动
     *
     * @param startActivityInstID
     * @param nextActivityDefID
     * @param ctx
     * @param newHistory
     * @return
     * @throws BPMException
     */
    private Map multipleDevice(String startActivityInstID, String nextActivityDefID, Map<RightCtx, Object> ctx, EIActivityInstHistory newHistory, Map<RightCtx, Object> eventContext) throws BPMException {

        List<String> enpointids = new ArrayList((List) ctx.get(RightCtx.sensorieee));
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);
        List copyActivityInsts = workflowEngine.splitActivityInst(startActivityInstID, enpointids.size(), newHistory.getActivityHistoryId());
        if (!eiActivityInst.getReceiveMethod().equals(ActivityInstReceiveMethod.RESEND.getType())) {
            EIActivityInst mainInst = workflowEngine.routeTo(startActivityInstID, (String) nextActivityDefID);
            Map<RightCtx, Object> mainMap = new HashMap<RightCtx, Object>();
            mainMap.putAll(ctx);
            List users = new ArrayList();
            users.add(this.getJdsClient().getConnectInfo().getUserID());
            mainMap.put(RightCtx.PERFORMERS, users);
        }
        String[] ids = new String[copyActivityInsts.size()];
        for (int k = 0; k < copyActivityInsts.size(); k++) {
            EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(k);
            ids[k] = inst.getActivityInstId();
        }

        serviceEngine.splitActivityInst(startActivityInstID, ids, ctx);
        dataEngine.splitActivityInst(startActivityInstID, ids, ctx);
        fileEngine.splitActivityInst(startActivityInstID, ids, ctx);
        for (int i = 0; i < copyActivityInsts.size(); i++) {
            Map<RightCtx, Object> map = new HashMap<RightCtx, Object>();
            map.putAll(ctx);
            map = this.fillInUserID(map);
            List<String> endpointList = new ArrayList<String>();
            endpointList.add(enpointids.get(i));

            EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(i);

            fireActivityEvent(inst, ActivityEventEnums.ROUTING, eventContext); // 触发活动路由事件
            EIActivityInst nextInst = workflowEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID);

            map.put(RightCtx.sensorieee, enpointids);
            map.put(RightCtx.ACTIVITYINST_ID, nextInst.getActivityInstId());
            deviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);

            dataEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
            fileEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
            fireActivityEvent(nextInst, ActivityEventEnums.ROUTED, map);
            // 如果串行处理则将其他活动暂停

        }

        List endpointList = new ArrayList();
        endpointList.add(enpointids.get(0));
        ctx.put(RightCtx.sensorieee, endpointList);

        return ctx;
    }

    /**
     * 多人并行办理分裂活动
     *
     * @param startActivityInstID
     * @param nextActivityDefID
     * @param ctx
     * @param newHistory
     * @return
     * @throws BPMException
     */
    private Map multiple(String startActivityInstID, String nextActivityDefID, Map<RightCtx, Object> ctx, EIActivityInstHistory newHistory, Map<RightCtx, Object> eventContext) throws BPMException {

        List<String> performers = new ArrayList((List) ctx.get(RightCtx.PERFORMERS));

        ActivityDefPerformSequence performSequence = this.getActivityDefRightAttribute(nextActivityDefID).getPerformSequence();

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);

        if (performSequence.equals(ActivityDefPerformSequence.FIRST)) {
            signle(startActivityInstID, nextActivityDefID, ctx, eventContext);
        } else {

            List copyActivityInsts = workflowEngine.splitActivityInst(startActivityInstID, performers.size(), newHistory.getActivityHistoryId());
            if (!eiActivityInst.getReceiveMethod().equals(ActivityInstReceiveMethod.RESEND.getType())) {
                EIActivityInst mainInst = workflowEngine.routeTo(startActivityInstID, (String) nextActivityDefID);
                Map<RightCtx, Object> mainMap = new HashMap<RightCtx, Object>();
                mainMap.putAll(ctx);
                List users = new ArrayList();
                users.add(this.getJdsClient().getConnectInfo().getUserID());
                mainMap.put(RightCtx.PERFORMERS, users);
                // rightEngine.routeTo(startActivityInstID, nextActivityDefID,mainMap);
                // dataEngine.routeTo(startActivityInstID, nextActivityDefID, mainMap);
                // fileEngine.routeTo(startActivityInstID, nextActivityDefID, mainMap);
            }
            String[] ids = new String[copyActivityInsts.size()];
            for (int k = 0; k < copyActivityInsts.size(); k++) {
                EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(k);
                ids[k] = inst.getActivityInstId();

            }
            if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
                rightEngine.splitActivityInst(startActivityInstID, ids, ctx);
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
                serviceEngine.splitActivityInst(startActivityInstID, ids, ctx);
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
                deviceEngine.splitActivityInst(startActivityInstID, ids, ctx);
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
                eventEngine.splitActivityInst(startActivityInstID, ids, ctx);

            }

            dataEngine.splitActivityInst(startActivityInstID, ids, ctx);
            fileEngine.splitActivityInst(startActivityInstID, ids, ctx);
            for (int i = 0; i < copyActivityInsts.size(); i++) {
                Map<RightCtx, Object> map = new HashMap<RightCtx, Object>();
                map.putAll(ctx);
                map = this.fillInUserID(map);
                List<String> personList = new ArrayList<String>();
                personList.add(performers.get(i));

                EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(i);

                fireActivityEvent(inst, ActivityEventEnums.ROUTING, eventContext); // 触发活动路由事件
                EIActivityInst nextInst = workflowEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID);

                map.put(RightCtx.PERFORMERS, personList);
                map.put(RightCtx.ACTIVITYINST_ID, nextInst.getActivityInstId());
                if (this.getActivityInst(inst.getActivityInstId()).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

                    rightEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                } else if (this.getActivityInst(inst.getActivityInstId()).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
                    deviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                } else if (this.getActivityInst(inst.getActivityInstId()).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
                    serviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                } else if (this.getActivityInst(inst.getActivityInstId()).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
                    eventEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                }

                dataEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                fileEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, map);
                fireActivityEvent(nextInst, ActivityEventEnums.ROUTED, map);

                fireRightEvent(eiActivityInst, RightEventEnums.ROUTETO, eventContext);
                // 如果串行处理则将其他活动暂停
                if (performSequence.equals(ActivityDefPerformSequence.SEQUENCE)) {
                    workflowEngine.suspendActivityInst(inst.getActivityInstId());
                }

            }

            List personList = new ArrayList();
            personList.add(performers.get(0));
            ctx.put(RightCtx.PERFORMERS, personList);
        }

        return ctx;
    }

    private Map joinSign(String startActivityInstID, String nextActivityDefID, Map<RightCtx, Object> ctx, EIActivityInstHistory newHistory) throws BPMException {

        Map<RightCtx, Object> eventContext = new HashMap();

        eventContext.put(RightCtx.CONTEXT_ACTIVITYINSTHISTORY, newHistory.getActivityHistoryId());
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);
        eventContext.putAll(ctx);
        fireActivityEvent(eiActivityInst, ActivityEventEnums.SPLITING, eventContext);
        List<String> performers = new ArrayList((List) ctx.get(RightCtx.PERFORMERS));

        List copyActivityInsts = workflowEngine.splitActivityInst(startActivityInstID, performers.size(), newHistory.getActivityHistoryId());
        String[] ids = new String[copyActivityInsts.size()];

        for (int k = 0; k < copyActivityInsts.size(); k++) {
            EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(k);
            ids[k] = inst.getActivityInstId();

        }
        // 不需要分类权限

        fireActivityEvent((EIActivityInst[]) copyActivityInsts.toArray(new EIActivityInst[0]), ActivityEventEnums.SPLITED, eventContext);
        for (int k = 0; k < performers.size(); k++) {

            String orgId = (String) performers.get(k);

            if (orgId.toUpperCase().startsWith("org".toUpperCase())) {
                orgId = orgId.substring(3, orgId.length());
                Map<RightCtx, Object> cctx = this.fillInUserID(new HashMap());
                cctx.put(RightCtx.ORGS, orgId);

                List<Person> personList = this.getActivityDefRightAttribute(nextActivityDefID).getPerFormPersons();
                List personlist = new ArrayList();
                for (int p = 0; p < personList.size(); p++) {
                    personlist.add(personList.get(p).getID());
                }

                Map<RightCtx, Object> multipleMap = this.fillInUserID(new HashMap());
                multipleMap.putAll(ctx);
                multipleMap.put(RightCtx.PERFORMERS, personlist);

                EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(k);
                fireActivityEvent(inst, ActivityEventEnums.ROUTING, ctx); // 触发活动路由事件
                EIActivityInst nextInst = workflowEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID);
                if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.No)) {

                    rightEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Device)) {
                    deviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Service)) {
                    serviceEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                } else if (this.getActivityDef(nextActivityDefID).getImplementation().equals(ActivityDefImpl.Event)) {
                    eventEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                }

                dataEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                fileEngine.routeTo(inst.getActivityInstId(), (String) nextActivityDefID, multipleMap);
                fireActivityEvent(nextInst, ActivityEventEnums.ROUTED, ctx);
            }
        }

        String orgId = (String) performers.get(0);
        if (orgId.toUpperCase().startsWith("org".toUpperCase())) {
            orgId = orgId.substring(3, orgId.length());
        }
        ctx.put(RightCtx.ORGS, orgId);
        List<Person> personList = (List<Person>) this.getActivityDefRightAttribute(nextActivityDefID).getPerFormPersons();
        List personlist = new ArrayList();

        for (int p = 0; p < personList.size(); p++) {
            personlist.add(personList.get(p).getID());
        }

        ctx.put(RightCtx.PERFORMERS, personlist);

        return ctx;
    }

    private ReturnType routeTo2(String startActivityInstID, String nextActivityDefID, Map<RightCtx, Object> ctx, EIActivityInstHistory newHistory, Map<RightCtx, Object> eventContext) throws BPMException {
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);
        eventContext.putAll(ctx);
        if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            ActivityDefPerformtype performType = this.getActivityDefRightAttribute(nextActivityDefID).getPerformType();

            if (performType != null && performType.equals(ActivityDefPerformtype.MULTIPLE)) {
                List<String> performers = new ArrayList((List) ctx.get(RightCtx.PERFORMERS));
                multiple(startActivityInstID, nextActivityDefID, ctx, newHistory, eventContext);
            } else if (performType != null && performType.equals(ActivityDefPerformtype.JOINTSIGN)) {
                joinSign(startActivityInstID, nextActivityDefID, ctx, newHistory);
            } else {
                signle(startActivityInstID, nextActivityDefID, ctx, eventContext);
            }

            fireRightEvent(eiActivityInst, RightEventEnums.ROUTETO, eventContext);
        } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {

            ActivityDefDevicePerformtype performtype = (ActivityDefDevicePerformtype) this.getActivityDefDeviceAttribute(nextActivityDefID).getPerformType();
            if (performtype.equals(ActivityDefDevicePerformtype.MULTIPLE)) {

            } else {
                signle(startActivityInstID, nextActivityDefID, ctx, eventContext);
            }

        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#routeTo(String startActivityInstID, List nextActivityDefIDs, Map
     * ctx)
     */
    public ReturnType routeTo(String startActivityInstID, List<String> nextActivityDefIDs, List<Map<RightCtx, Object>> ctxs) throws BPMException {
        checkLogined();

        for (Map<RightCtx, Object> ctx : ctxs) {
            ctx = fillInUserID(ctx);
        }

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(startActivityInstID);

        if (eiActivityInst == null) {
            throw new BPMException("Activity instance not found:" + startActivityInstID);
        }

        checkCanPerform(eiActivityInst.getActivityInstId(), ctxs.get(0));

        ReturnType ret = null;
        // 1.保存历史
        // 触发活动结束事件

        fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETING, ctxs.get(0));

        EIActivityInstHistory newHistory = null;
        // 触发活动结束事件
        Map<RightCtx, Object> eventContext = new HashMap();

        // 1.1.调用引擎中的保存历史方法 如果是重发就复用以前的历史
        // if (eiActivityInst.getReceiveMethod().equals(ActivityInst.RESEND)){
        // newHistory=workflowEngine.getLastSplitActivityInstHistoryByActvityInst(startActivityInstID).get(0);
        // eventContext.put(EventContext.CONTEXT_ACTIVITYINSTHISTORY, newHistory
        // .getActivityHistoryId());
        // }else{
        // newHistory = workflowEngine.saveActivityHistoryInst(startActivityInstID);
        // }
        newHistory = workflowEngine.saveActivityHistoryInst(startActivityInstID);
        Map<RightCtx, Object> ctx = ctxs.get(0);
        if (ctx.get(RightCtx.READERS) != null) {
            List readers = new ArrayList((List) ctx.get(RightCtx.READERS));
            if (readers.size() > 0) {
                this.copyTo(newHistory.getActivityHistoryId(), readers);
            }

        }


        eventContext.put(RightCtx.CONTEXT_CTXLIST, ctxs);
        eventContext.put(RightCtx.CONTEXT_ACTIVITYINSTHISTORY, newHistory.getActivityHistoryId());
        fireActivityEvent(eiActivityInst, ActivityEventEnums.SAVEING, eventContext);
        // 1.2.调用权限引擎中的保存历史方法
        if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            ret = rightEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            ReturnType retd = deviceEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
            if (!retd.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            ReturnType retd = serviceEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
            if (!retd.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            ReturnType retd = eventEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
            if (!retd.isSucess()) {
                return ret;
            }
        }

        ret = dataEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
        if (!ret.isSucess()) {
            return ret;
        }
        ret = fileEngine.saveActivityHistoryInst(startActivityInstID, newHistory.getActivityHistoryId(), ctxs.get(0));
        if (!ret.isSucess()) {
            return ret;
        }
        fireActivityEvent(eiActivityInst, ActivityEventEnums.SAVEED, eventContext);

        fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETED, eventContext);

        // **如果到达的活动节点是一个虚拟结束节点，则结束流程！**
        if (nextActivityDefIDs.size() == 1 && ActivityDefPosition.VIRTUAL_LAST_DEF.getType().equalsIgnoreCase((String) nextActivityDefIDs.get(0))) {
            return completeProcessInst(eiActivityInst.getProcessInstId(), ctxs.get(0));
        }

        // 2.如果需要的化，将活动实例复制成多份
        List copyActivityInsts = new ArrayList();
        if (nextActivityDefIDs.size() > 1) {
            // 触发活动开始分裂事件
            fireActivityEvent(eiActivityInst, ActivityEventEnums.SPLITING, eventContext);

            // 2.1.调用workflow引擎复制活动实例
            copyActivityInsts = workflowEngine.splitActivityInst(startActivityInstID, nextActivityDefIDs.size(), newHistory.getActivityHistoryId());

            String[] ids = new String[nextActivityDefIDs.size()];

            for (int i = 0; i < copyActivityInsts.size(); i++) {
                EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(i);
                ids[i] = inst.getActivityInstId();
            }
            if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

                // 2.2.调用权限引擎中的复制活动实例
                ret = rightEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
                if (!ret.isSucess()) {
                    return ret;
                }
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
                ReturnType retdevice = deviceEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
                if (!retdevice.isSucess()) {
                    return retdevice;
                }
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
                ReturnType retdevice = serviceEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
                if (!retdevice.isSucess()) {
                    return retdevice;
                }
            } else if (this.getActivityInst(startActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
                ReturnType retdevice = eventEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
                if (!retdevice.isSucess()) {
                    return retdevice;
                }
            }

            ret = dataEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
            if (!ret.isSucess()) {
                return ret;
            }
            ret = fileEngine.splitActivityInst(startActivityInstID, ids, ctxs.get(0));
            if (!ret.isSucess()) {
                return ret;
            }
            for (int i = 0; i < nextActivityDefIDs.size(); i++) {
                ret = routeTo2(ids[i], nextActivityDefIDs.get(i), ctxs.get(i), newHistory, eventContext);
                if (!ret.isSucess()) {
                    return ret;
                }
            }
            // 触发活动分裂完成事件
            fireActivityEvent((EIActivityInst[]) copyActivityInsts.toArray(new EIActivityInst[copyActivityInsts.size()]), ActivityEventEnums.SPLITED, eventContext);

        } else {
            ret = routeTo2(startActivityInstID, nextActivityDefIDs.get(0), ctxs.get(0), newHistory, eventContext);
            if (!ret.isSucess()) {
                return ret;
            }

        }
        // 任务完成后重置任务状态
        if (eiActivityInst.getReceiveMethod().equals(ActivityInstReceiveMethod.RESEND.getType())) {
            eiActivityInst.setReceiveMethod(ActivityInstReceiveMethod.RESEND.getType());
        }

        // 修改流程实例的草稿状态（如果是第一次提交，则将状态改为running）
        EIProcessInst processInst = eiActivityInst.getProcessInst();
        if (processInst.getState().equalsIgnoreCase(ProcessInstStatus.notStarted.getType())) {
            workflowEngine.updateProcessState(eiActivityInst.getProcessInstId(), ProcessInstStatus.running.getType());
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 收回相关方法

    /**
     * 判断是否可以收回 1。调用引擎 2。调用权限
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#canTakeBack(java.lang.String, java.util.Map)
     */
    public boolean canTakeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);

        ActivityDef activityDef = this.getActivityInst(activityInstID).getActivityDef();
        Boolean canTackback = this.getActivityDefRightAttribute(activityDef.getActivityDefId()).isCanReSend();

        if (this.getActivityInst(activityInstID).getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
            return false;
        }
        if (canTackback == null || canTackback) {
            if (workflowEngine.canTakeBack(activityInstID) == false) {
                return false;
            }

        }
        if (activityDef.getImplementation().equals(ActivityDefImpl.No)) {
            if (rightEngine.canTakeBack(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (activityDef.getImplementation().equals(ActivityDefImpl.Device)) {
            if (deviceEngine.canTakeBack(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (activityDef.getImplementation().equals(ActivityDefImpl.Service)) {
            if (serviceEngine.canTakeBack(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (activityDef.getImplementation().equals(ActivityDefImpl.Event)) {
            if (eventEngine.canTakeBack(activityInstID, rightCtx) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 收回活动实例 1。调用权限部分，恢复权限部分数据 2。调用引擎部分，恢复引擎部分数据
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#takeBack(java.lang.String, java.util.Map)
     */
    public ReturnType takeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        if (!canTakeBack(activityInstID, ctx)) {
            this.resumeActivityInst(activityInstID, ctx);
            new ReturnType(ReturnType.MAINCODE_SUCCESS);
            // throw new BPMException("User has no right to perform!");
        }
        checkLogined();
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);

        if (eiActivityInst == null) {
            throw new BPMException("Activity instance not found:" + activityInstID);
        }

        rightCtx.putAll(ctx);
        String suspendOrCombine = workflowEngine.forecastSuspendOrCombine(eiActivityInst.getActivityInstId(), eiActivityInst.getActivityDefId());
        rightCtx.put(RightCtx.SUSPENDORCOMBINE, suspendOrCombine);
        List activityInsts = workflowEngine.combinableActivityInsts(eiActivityInst.getActivityInstId());
        rightCtx.put(RightCtx.COMBINABLEACTIVITYINSTS, activityInsts);
        // String performSequence = getActivityDefRightAttribute(eiActivityInst.getActivityDefId(),
        // ActivityDefRightAtt.PERFORMSEQUENCE, null);
        // rightCtx.put(RightAtt.ATT_PERFORMSEQUENCE.getType(), performSequence);

        // 触发活动收回事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.TAKEBACKING, rightCtx);
        ReturnType rt;
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            // 1.首先调用权限
            rt = rightEngine.tackBack(activityInstID, rightCtx);
            if (!rt.isSucess()) {
                return rt;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            rt = this.deviceEngine.tackBack(activityInstID, rightCtx);
            if (!rt.isSucess()) {
                return rt;
            }

        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            rt = this.serviceEngine.tackBack(activityInstID, rightCtx);
            if (!rt.isSucess()) {
                return rt;
            }

        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            rt = this.eventEngine.tackBack(activityInstID, rightCtx);
            if (!rt.isSucess()) {
                return rt;
            }
        }

        // 2.执行数据引擎
        rt = dataEngine.tackBack(activityInstID, rightCtx);
        if (!rt.isSucess()) {
            return rt;
        }

        // 3.执行文件引擎
        rt = fileEngine.tackBack(activityInstID, rightCtx);
        if (!rt.isSucess()) {
            return rt;
        }

        // 4.调用引擎
        rt = workflowEngine.tackBack(activityInstID, rightCtx);
        if (!rt.isSucess()) {
            return rt;
        }

        // 触发活动收回事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.TAKEBACKED, rightCtx);

        fireRightEvent(eiActivityInst, RightEventEnums.TACKBACK, rightCtx);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 退回相关方法

    /**
     * 判断是否可以退回 1。调用引擎 2。调用权限
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#canRouteBack(java.lang.String, java.util.Map)
     */
    public boolean canRouteBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        // add by lxl 2004-02-08
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        if (workflowEngine.canRouteBack(activityInstID) == false) {
            return false;
        }
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            if (rightEngine.canRouteBack(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            if (deviceEngine.canRouteBack(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            if (eventEngine.canRouteBack(activityInstID, rightCtx) == false) {
                return false;
            }

        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            if (serviceEngine.canRouteBack(activityInstID, rightCtx) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#getRouteBackActivityHistoryInstList(String activityInstID, Filter
     * routeFilter, Map<RightCtx,Object> ctx)
     */
    public List<ActivityInstHistory> getCanTackBackActivityInstList(String activityInstID, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        // add by lxl 2004-02-08
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        // add by lxl 2004-04-15 路由条件的支持
        ActivityInst activityInst = getActivityInst(activityInstID);
        rightCtx.put(RightCtx.ACTIVITYINST_ID, activityInstID);
        rightCtx.put(RightCtx.PROCESSINST_ID, activityInst.getProcessInstId());

        FilterChain filterChain = new FilterChain();
        if (routeFilter != null) {
            filterChain.addFilter(routeFilter);
        }
        // filterChain.addFilter(routeConditionFilter);

        List routeList = workflowEngine.getRouteBacks(activityInstID, filterChain);
        return new WorkflowListProxy(routeList, getSystemCode());

    }

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#getRouteBackActivityHistoryInstList(String activityInstID, Filter
     * routeFilter, Map<RightCtx,Object> ctx)
     */
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstID, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        // add by lxl 2004-02-08
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        // add by lxl 2004-04-15 路由条件的支持
        ActivityInst activityInst = getActivityInst(activityInstID);
        rightCtx.put(RightCtx.ACTIVITYINST_ID, activityInstID);
        rightCtx.put(RightCtx.PROCESSINST_ID, activityInst.getProcessInstId());

        FilterChain filterChain = new FilterChain();
        if (routeFilter != null) {
            filterChain.addFilter(routeFilter);
        }
        // filterChain.addFilter(routeConditionFilter);

        List routeList = workflowEngine.getRouteBacks(activityInstID, filterChain);
        return new WorkflowListProxy(routeList, getSystemCode());

    }

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#routeBack(java.lang.String, java.lang.String, java.util.Map)
     */
    public ReturnType routeBack(String fromActivityInstID, String toActivityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        // add by lxl 2004-02-08
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(fromActivityInstID);
        // added by chenjie 2004-04-13
        if (eiActivityInst == null) {
            throw new BPMException("Activity instance not found:" + fromActivityInstID);
        }
        checkCanPerform(eiActivityInst.getActivityInstId(), rightCtx);

        EIActivityInstHistory history = EIActivityInstHistoryManager.getInstance().loadByKey(toActivityInstHistoryID);
        if (history == null) {
            throw new BPMException("The history activity instance not found!");
        }

        ReturnType ret = null;
        // 1.保存历史
        // 触发活动结束事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETING, ctx);
        // 1.1.调用引擎中的保存历史方法
        EIActivityInstHistory newHistory = workflowEngine.saveActivityHistoryInst(fromActivityInstID);

        if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            // 1.2.调用权限引擎中的保存历史方法
            ret = rightEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), ctx);
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            ret = deviceEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            ret = serviceEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            ret = eventEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        }

        ret = dataEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), rightCtx);
        if (!ret.isSucess()) {
            return ret;
        }
        ret = fileEngine.saveActivityHistoryInst(fromActivityInstID, newHistory.getActivityHistoryId(), rightCtx);
        if (!ret.isSucess()) {
            return ret;
        }

        // 触发活动结束事件
        Map<RightCtx, Object> eventContext = new HashMap();
        eventContext.put(RightCtx.CONTEXT_ACTIVITYINSTHISTORY, newHistory.getActivityHistoryId());
        fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETED, eventContext);

        // 3.分别路由所有的活动实例和复制的活动实例
        // 3.1 判断是否需要合并，如果需要则调用相关方法
        List activityInsts = workflowEngine.forecastCombinableActivityInsts(eiActivityInst.getActivityInstId(), history.getActivityDefId());
        boolean bCombine = false;
        if (activityInsts.size() != 0) {
            String suspendOrCombine = workflowEngine.forecastSuspendOrCombine(eiActivityInst.getActivityInstId(), history.getActivityDefId());
            if (suspendOrCombine.equals(ActivityInstSuSpend.SUSPEND.getType())) {
                // 挂起，不需要处理
            } else if (suspendOrCombine.equals(ActivityInstSuSpend.COMBINE.getType())) {
                // 需要合并
                bCombine = true;
                // 触发合并事件
                fireActivityEvent((EIActivityInst[]) activityInsts.toArray(new EIActivityInst[0]), ActivityEventEnums.JOINING);
            }
        }

        // 触发活动路由事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.ROUTING, ctx);
        ReturnType rt;
        // 1.首先调用权限
        if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            rt = rightEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            rt = deviceEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            rt = eventEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }

        } else if (this.getActivityInst(fromActivityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            rt = serviceEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
            if (!ret.isSucess()) {
                return ret;
            }
        }

        rt = dataEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
        if (!rt.isSucess())
            return rt;

        rt = fileEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, rightCtx);
        if (!rt.isSucess())
            return rt;
        // 2.调用引擎
        EIActivityInst actInst = workflowEngine.routeBack(fromActivityInstID, toActivityInstHistoryID);
        // 触发活动路由事件
        fireActivityEvent(actInst, ActivityEventEnums.ROUTED, ctx);

        // 如果需要合并，触发合并完成事件
        if (bCombine == true) {
            fireActivityEvent(actInst, ActivityEventEnums.JOINED, ctx);
        }
        fireRightEvent(eiActivityInst, RightEventEnums.ROUTBACK, eventContext);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 签收相关方法

    /**
     * 判断是否可以签收 1。调用引擎 2。调用权限
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#canSignReceive e(java.lang.String, java.util.Map)
     */
    public boolean canSignReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        // add by lxl 2004-02-08
        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        if (workflowEngine.canSignReceive(activityInstID) == false) {
            return false;
        }
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            if (rightEngine.canSignReceive(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            if (deviceEngine.canSignReceive(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            if (serviceEngine.canSignReceive(activityInstID, rightCtx) == false) {
                return false;
            }
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            if (eventEngine.canSignReceive(activityInstID, rightCtx) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see net.ooder.bpm.engine.WorkflowClientService#signReceive(java.lang.String, java.util.Map)
     */
    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {

        checkLogined();

        Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
        List performers = (List<Person>) rightEngine.getActivityInstRightAttribute(activityInstID, ActivityInstRightAtt.PERFORMER, rightCtx);

        rightCtx.put(RightCtx.PERFORMERS, performers);

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);
        // added by chenjie 2004-04-13t
        if (eiActivityInst == null) {
            throw new BPMException("Activity instance not found:" + activityInstID);
        }
        if (canSignReceive(eiActivityInst.getActivityInstId(), rightCtx) == false) {
            throw new BPMException("User has no right to perform!");
        }

        // 触发活动激活事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.ACTIVING, ctx);
        ReturnType rt;

        // 可能会出现并发造成失败
        rt = workflowEngine.signReceive(activityInstID);
        if (!rt.isSucess())
            return rt;

        // 1.首先调用权限
        if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No.getType())) {

            rt = rightEngine.signReceive(activityInstID, rightCtx);
            if (!rt.isSucess()) {
                return rt;
            }

        } else if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Device.getType())) {

            rt = deviceEngine.signReceive(activityInstID, rightCtx);
        } else if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Service.getType())) {

            rt = serviceEngine.signReceive(activityInstID, rightCtx);
        } else if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Event.getType())) {

            rt = eventEngine.signReceive(activityInstID, rightCtx);
        }
        if (!rt.isSucess())
            return rt;
        rt = dataEngine.signReceive(activityInstID, rightCtx);
        if (!rt.isSucess())
            return rt;
        rt = fileEngine.signReceive(activityInstID, rightCtx);
        if (!rt.isSucess())
            return rt;
        // 2.调用引擎

        if (!rt.isSucess())
            return rt;
        // 触发活动激活事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.ACTIVED, rightCtx);
        fireRightEvent(eiActivityInst, RightEventEnums.SIGNRECEIVE, rightCtx);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 活动状态转换方法

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#suspendActivityInst(java.lang.String, java.util.Map)
     */
    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);
        // 触发活动恢复事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.SUSPENDING, ctx);
        ReturnType rt;
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            rt = rightEngine.suspendActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            rt = deviceEngine.suspendActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            rt = serviceEngine.suspendActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            rt = eventEngine.suspendActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        }
        rt = dataEngine.suspendActivityInst(activityInstID, ctx);
        if (!rt.isSucess())
            return rt;

        rt = fileEngine.suspendActivityInst(activityInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = workflowEngine.suspendActivityInst(activityInstID);
        if (!rt.isSucess()) {
            return rt;
        }
        // 触发活动恢复事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.SUSPENDED, ctx);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#resumeActivityInst(java.lang.String, java.util.Map)
     */
    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);
        // 触发活动挂起事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.RESUMING, ctx);
        ReturnType rt;
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            rt = rightEngine.resumeActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            rt = serviceEngine.resumeActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            rt = eventEngine.resumeActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        } else if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            rt = deviceEngine.resumeActivityInst(activityInstID, ctx);
            if (!rt.isSucess())
                return rt;
        }

        rt = dataEngine.resumeActivityInst(activityInstID, ctx);
        if (!rt.isSucess())
            return rt;

        rt = fileEngine.resumeActivityInst(activityInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = workflowEngine.resumeActivityInst(activityInstID);
        if (!rt.isSucess())
            return rt;
        // 触发活动挂起事件
        fireActivityEvent(eiActivityInst, ActivityEventEnums.RESUMED, ctx);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 流程状态转换方法

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#suspendProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        // 触发流程实例挂起事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.SUSPENDING);
        ReturnType rt;

        rt = rightEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;

        rt = dataEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = deviceEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = eventEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = serviceEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = fileEngine.suspendProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = workflowEngine.suspendProcessInst(processInstID);
        if (!rt.isSucess())
            return rt;

        // 触发流程实例挂起事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.SUSPENDED);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#resumeProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        // 触发流程实例恢复事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.RESUMING);
        ReturnType rt;
        rt = rightEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = dataEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = deviceEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = serviceEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = eventEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = fileEngine.resumeProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = workflowEngine.resumeProcessInst(processInstID);
        if (!rt.isSucess())
            return rt;

        // 触发流程实例恢复事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.RESUMED);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#abortProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        // 触发流程实例中止事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.ABORTING);
        ReturnType rt;
        rt = rightEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = dataEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = deviceEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = eventEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = serviceEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = fileEngine.abortProcessInst(processInstID, ctx);
        if (!rt.isSucess())
            return rt;
        rt = workflowEngine.abortProcessInst(processInstID);
        if (!rt.isSucess())
            return rt;

        // 触发流程实例中止事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.ABORTED);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#completeProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        // 触发流程实例结束事件

        fireProcessEvent(eiProcessInst, ProcessEventEnums.COMPLETING);

        ReturnType rt;
        List activityInsts = eiProcessInst.getActivityInstList();
        if (activityInsts != null) {
            for (Iterator iter = activityInsts.iterator(); iter.hasNext(); ) {
                EIActivityInst activityInst = (EIActivityInst) iter.next();
                if (!this.canEndRead(activityInst.getActivityInstId(), ctx)) {
                    String activityInstId = activityInst.getActivityInstId();
                    EIActivityInstHistory his = workflowEngine.saveActivityHistoryInst(activityInstId);

                    rt = rightEngine.saveActivityHistoryInst(activityInstId, his.getActivityHistoryId(), ctx);
                    if (!rt.isSucess()) {
                        return rt;
                    }
                    rt = workflowEngine.deleteActivityInst(activityInstId);
                    if (!rt.isSucess()) {
                        return rt;
                    }
                }

            }
        }

        rt = rightEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = deviceEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = eventEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = serviceEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = dataEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = fileEngine.completeProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = workflowEngine.completeProcessInst(processInstID);
        if (!rt.isSucess()) {
            return rt;
        }
        // 触发流程实例结束事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.COMPLETED);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#deleteProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        EIProcessInst eiProcessInst = EIProcessInstManager.getInstance().loadByKey(processInstID);
        // 触发流程实例删除事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.DELETING);
        ReturnType rt;
        rt = rightEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = deviceEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = serviceEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = eventEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }

        rt = dataEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = fileEngine.deleteProcessInst(processInstID, ctx);
        if (!rt.isSucess()) {
            return rt;
        }
        rt = workflowEngine.deleteProcessInst(processInstID);
        if (!rt.isSucess()) {
            return rt;
        }
        // 触发流程实例删除事件
        fireProcessEvent(eiProcessInst, ProcessEventEnums.DELETED);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    // --------------------------------------------- 事务控制方法

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#beginTransaction(java.lang.String, java.util.Map)
     */
    public void beginTransaction() throws BPMException {
        checkLogined();
        try {
            DbManager.getInstance().beginTransaction();

            //  HibernateSessionFactory.getSession().beginTransaction();
        } catch (SQLException sqle) {
            throw new BPMException("Failed to beging transaction of client service.", sqle, BPMException.TRANSACTIONBEGINERROR);
        }
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#commitTransaction(java.lang.String, java.util.Map)
     */
    public void commitTransaction() throws BPMException {
        checkLogined();
        try {
            DbManager.getInstance().endTransaction(true);

            //  HibernateSessionFactory.getSession().getTransaction().commit();
        } catch (SQLException sqle) {
            throw new BPMException("Failed to commit transaction of client service.", sqle, BPMException.TRANSACTIONCOMMITERROR);
        }
    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#rollbackTransaction(java.lang.String, java.util.Map)
     */
    public void rollbackTransaction() throws BPMException {
        checkLogined();
        try {
            DbManager.getInstance().endTransaction(false);
        } catch (SQLException sqle) {
            throw new BPMException("Failed to rollback transaction of client service", sqle, BPMException.TRANSACTIONROLLBACKERROR);
        }
    }

    @Override
    public ActivityDefEvent getActivityDefEventAttribute(String activityDefId) throws BPMException {
        return this.eventEngine.getActivityDefEventAttribute(activityDefId);
    }

    @Override
    public ActivityDefDevice getActivityDefDeviceAttribute(String activityDefId) throws BPMException {
        return this.deviceEngine.getActivityDefDeviceAttribute(activityDefId);
    }

    @Override
    public List<Person> getActivityInstPersons(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
        return this.rightEngine.getActivityInstRightAttribute(activityInstId, attName, null);
    }

    @Override
    public List<DeviceEndPoint> getActivityInstDevices(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
        return this.deviceEngine.getActivityInstDevices(activityInstId, attName);
    }

    @Override
    public List<Command> getActivityInstCommands(String activityInstId) throws BPMException {
        return this.deviceEngine.getCommandFromActivity(activityInstId);
    }

    @Override
    public List<Person> getActivityInstHistoryPersons(String activityInstHistoryId, ActivityInstHistoryAtt attName) throws BPMException {
        return this.rightEngine.getActivityInstHistoryRightAttribute(activityInstHistoryId, attName, null);
    }

    private void checkLogined() throws BPMException {
        if (this.getJdsClient().getConnectInfo() == null || this.getJdsClient().getSessionHandle() == null) {
            throw new BPMException("Not logined error!", BPMException.NOTLOGINEDERROR);
        } else {
            JDSServer.activeSession(this.getJdsClient().getSessionHandle());
        }
    }

    /**
     * 判断当前人是否有办理权限
     *
     * @param activityInstId
     * @param ctx
     * @throws BPMException
     */
    private void checkCanPerform(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // modify by lxl 2004-04-15 改为判断当前的办理人，而不是活动定义的办理候选人
        /*
         * List performers = (List) rightEngine.getActivityInstRightAttribute(activityInstId,
         * IOTRightConstants.ACTIVITYINST_RIGHT_ATT_PERFORMER, ctx); String personId = connInfo.getUserID(); for
         * (Iterator it = performers.iterator(); it.hasNext();) { Person person = (Person) it.next(); if (person != null
         * && personId.equals(person.getID())) { return; } }
         */

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstId);

        if (eiActivityInst.getReceiveMethod().equals(ActivityInstReceiveMethod.RESEND.getType())) {
            return;
        }
        ;

        if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

            String personId = this.getJdsClient().getConnectInfo().getUserID();
            Map tmpCtx = this.fillInUserID(ctx);
            tmpCtx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_PERFORM);
            boolean permission = queryPermissionToActivityInst(activityInstId, tmpCtx);

            fireRightEvent(eiActivityInst, RightEventEnums.CHANGEPERFORMER, tmpCtx);

            if (permission == false) {
                throw new BPMException("User has no right to perform!");
            }
        } else if (eiActivityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {

        }

    }

    private Map<RightCtx, Object> fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put(RightCtx.USERID, this.getJdsClient().getConnectInfo().getUserID());
        return result;
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see net.ooder.bpm.engine.WorkflowClientService#getProcessDefVersionRightAttribute(java.lang.String,
//     * java.lang.String, java.util.Map)
//     */
//    public Object getProcessDefVersionRightAttribute(String processDefVersionId, ProcessDefVersionAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
//	checkLogined();
//	Map ctxRight = fillInUserID(ctx);
//	return rightEngine.getProcessDefVersionRightAttribute(processDefVersionId, attName, ctxRight);
//    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.WorkflowClientService#getActivityDefRightAttribute(java.lang.String, java.lang.String,
     * java.util.Map)
     */
    @Override
    public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException {
        checkLogined();
        return rightEngine.getActivityDefRightAttribute(activityDefId);
    }

    @Override
    public List<AttributeDef> getActivityDefAttributes(String activityDefId) throws BPMException {

        return this.getActivityDef(activityDefId).getAllAttribute();
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see net.ooder.bpm.engine.WorkflowClientService#getRouteDefRightAttribute(java.lang.String, java.lang.String,
//     * java.util.Map)
//     */
//    public Object getRouteDefRightAttribute(String routeDefId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
//	checkLogined();
//	Map ctxRight = fillInUserID(ctx);
//	return rightEngine.getRouteDefRightAttribute(routeDefId, attName, ctxRight);
//    }

    /**
     * 活动实例的权限属性
     *
     * @param activityInstId 活动实例的ID
     * @param attName        属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<Person> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        return rightEngine.getActivityInstRightAttribute(activityInstId, attName, ctx);
    }

    /**
     * 活动实例历史的权限属性
     *
     * @param activityInstHistoryId 活动实例历史的ID
     * @param attName               属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<Person> getActivityInstHistoryRightAttribute(String activityInstHistoryId, ActivityInstHistoryAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        return rightEngine.getActivityInstHistoryRightAttribute(activityInstHistoryId, attName, ctx);
    }

    /**
     * 判断当前人对活动实例的权限
     *
     * @param activityInstId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        Map ctxRight = fillInUserID(ctx);
        return rightEngine.queryPermissionToActivityInst(activityInstId, ctxRight);
    }

    /**
     * 得到当前人对活动实例的所有权限列表
     *
     * @param activityInstId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        checkLogined();
        ctx = fillInUserID(ctx);
        List list = rightEngine.queryAllPermissionToActivityInst(activityInstId, ctx);
        return list;
    }

    public DataEngine getMapDAODataEngine() {
        return dataEngine;
    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateActivityInstFormValues(String activityHistoryID, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, DataMap dataMap) throws BPMException {
        throw new BPMException("服务端不支持此调用！");
    }

    public FileEngine getfileEngine() {
        return fileEngine;
    }

    public void setMapDAODataEngine(DataEngine daoataEngine) {
        this.dataEngine = daoataEngine;
    }

    public void setfileEngine(FileEngine fileEngine) {
        this.fileEngine = fileEngine;
    }

    public boolean canEndRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        ActivityInst activityInst = this.getActivityInst(activityInstID);
        if ((activityInst.getState().equals(ActivityInstStatus.notStarted) || activityInst.getState().equals(ActivityInstStatus.READ))// 兼容数据修改前的旧版本
                && activityInst.getReceiveMethod().equals(ActivityInstReceiveMethod.READ)
            // && activityInst.getProcessInst().getState().equals(ProcessInst.STATE_RUNNING)//
                ) {
            return true;
        }
        ;
        return false;
    }

    public ReturnType endRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        EIActivityInstHistory activityInstHistory = workflowEngine.endRead(activityInstID, ctxRight);

        ActivityInst activityInst = this.getActivityInst(activityInstID);
        ReturnType ret = new ReturnType(ReturnType.MAINCODE_SUCCESS);
        if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            ret = rightEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            ret = deviceEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            ret = serviceEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            ret = eventEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        }

        ReturnType dret = dataEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        ReturnType fret = fileEngine.endRead(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstID);

        fireRightEvent(eiActivityInst, RightEventEnums.ENDREAD, ctx);

        if (ret.isSucess() && dret.isSucess() && fret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType endTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        EIActivityInstHistory activityInstHistory = workflowEngine.endTask(activityInstID, ctxRight);

        ReturnType dret = dataEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        ActivityInst activityInst = this.getActivityInst(activityInstID);
        ReturnType ret = new ReturnType(ReturnType.MAINCODE_SUCCESS);
        if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            ret = rightEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            ret = deviceEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            ret = serviceEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            ret = eventEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        }
        ReturnType fret = fileEngine.endTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        if (ret.isSucess() && dret.isSucess() && fret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType abortedTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        EIActivityInstHistory activityInstHistory = workflowEngine.abortedTask(activityInstID, ctxRight);

        ActivityInst activityInst = this.getActivityInst(activityInstID);
        ReturnType ret = new ReturnType(ReturnType.MAINCODE_SUCCESS);
        if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            ret = rightEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Event)) {
            ret = eventEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Device)) {
            ret = deviceEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        } else if (activityInst.getActivityDef().getImplementation().equals(ActivityDefImpl.Service)) {
            ret = serviceEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);

        }

        ReturnType dret = dataEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        ReturnType fret = fileEngine.abortedTask(activityInstID, activityInstHistory.getActivityHistoryId(), ctxRight);
        if (ret.isSucess() && dret.isSucess() && fret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        String userId = (String) ctxRight.get(RightCtx.USERID);
        ReturnType wret = workflowEngine.deleteHistory(activityInstHistoryID, ctxRight);

        ReturnType rret = rightEngine.deleteHistory(activityInstHistoryID, ctxRight);

        ReturnType dret = dataEngine.deleteHistory(activityInstHistoryID, ctxRight);
        ReturnType fret = fileEngine.deleteHistory(activityInstHistoryID, ctxRight);
        this.getActivityInstHistory(activityInstHistoryID).setPersonAttribute(userId, ActivityInstHistoryStatus.STATUS.getName(), ActivityInstHistoryStatus.DELETE.getType());

        if (wret.isSucess() && rret.isSucess() && dret.isSucess() && fret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        String userId = (String) ctxRight.get(RightCtx.USERID);
        ReturnType wret = workflowEngine.restoreHistory(activityInstHistoryID, ctxRight);

        this.getActivityInstHistory(activityInstHistoryID).setPersonAttribute(userId, ActivityInstHistoryStatus.STATUS.getType(), ActivityInstHistoryStatus.NORMAL.getType());

        ReturnType rret = rightEngine.restoreHistory(activityInstHistoryID, ctxRight);
        if (wret.isSucess() && rret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        String userId = (String) ctxRight.get(RightCtx.USERID);
        ReturnType wret = workflowEngine.clearHistory(activityInstHistoryID, ctxRight);
        ReturnType rret = rightEngine.clearHistory(activityInstHistoryID, ctxRight);
        ReturnType dret = dataEngine.clearHistory(activityInstHistoryID, ctxRight);
        ReturnType fret = fileEngine.clearHistory(activityInstHistoryID, ctxRight);

        this.getActivityInstHistory(activityInstHistoryID).setPersonAttribute(userId, ActivityInstHistoryStatus.STATUS.getType(), ActivityInstHistoryStatus.CLEAR.getType());
        if (wret.isSucess() && rret.isSucess() && dret.isSucess() && fret.isSucess()) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType copyTo(String activityHistoryInstId, List readers) throws BPMException {

        ActivityInstHistory activityInstHistory = this.getActivityInstHistory(activityHistoryInstId);

        // 1.2.调用权限引擎中的保存历史方法
        List<EIActivityInst> activityInstList = workflowEngine.copyActivityInst(activityHistoryInstId, readers.size());
        List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();

        for (EIActivityInst eiActivityInst : activityInstList) {
            activityInsts.add(this.getActivityInst(eiActivityInst.getActivityInstId()));
        }

        ReturnType ret = null;
        ret = dataEngine.copyTo(activityInsts, readers);
        if (!ret.isSucess()) {
            return ret;
        }
        ret = fileEngine.copyTo(activityInsts, readers);
        if (!ret.isSucess()) {
            return ret;
        }
        ret = rightEngine.copyTo(activityInsts, readers);
        if (!ret.isSucess()) {
            return ret;
        }
        Map<RightCtx, Object> ctx = new HashMap();

        ctx.put(RightCtx.CONTEXT_READER, readers);

        EIActivityInst eiActivityInst = EIActivityInstManager.getInstance().loadByKey(activityInstHistory.getActivityInstId());
        fireRightEvent(eiActivityInst, RightEventEnums.COPYTO, ctx);

        return ret;

    }

    public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        return rightEngine.addPersonTagToHistory(activityInstHistoryID, tagName, ctxRight);
    }

    ;

    public ReturnType deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        Map ctxRight = fillInUserID(ctx);
        return rightEngine.deletePersonTagToHistory(activityInstHistoryID, tagName, ctxRight);
    }

    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryList(BPMCondition condition, RightConditionEnums conditionEnums, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        UtilTimer timer = new UtilTimer();
        try {
            checkLogined();
            BPMCondition resultCon = condition;
            Filter resultFilter = filter;

            Map<RightCtx, Object> rightCtx = fillInUserID(ctx);
            Filter rightFilter = rightEngine.getActivityInstHistoryListFilter(conditionEnums, rightCtx);
            if (rightFilter != null) {
                FilterChain filterChain = new FilterChain();
                filterChain.addFilter(resultFilter);
                filterChain.addFilter(rightFilter);
                resultFilter = filterChain;

                // 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
                if (rightFilter instanceof BPMCondition) {

                    if (resultCon == null) {
                        resultCon = (BPMCondition) rightFilter;
                    } else {
                        resultCon.addCondition((BPMCondition) rightFilter, JoinOperator.JOIN_AND);
                    }
                }

            }

            List activityInstHistoryList = workflowEngine.getActivityInstHistoryList(resultCon, resultFilter);
            logger.debug(timer.timerString(TIMER_LEVEL, "getActivityInstHistoryList"));


            WorkflowListProxy proxy = new WorkflowListProxy(activityInstHistoryList, getSystemCode());
            ListResultModel<List<ActivityInstHistory>> resultModel = new ListResultModel<List<ActivityInstHistory>>();
            resultModel.setData(proxy);
            resultModel.setSize(proxy.size());

            return resultModel;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BPMException("getActivityInstList error.", e, BPMException.GETACTIVITYINSTLISTERROR);
        }
    }

    public boolean canPerform(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        boolean isCanPerform = true;
        if (this.getActivityInst(activityInstID).getActivityDef().getImplementation().equals(ActivityDefImpl.No)) {
            ctx = this.fillInUserID(ctx);
            ctx.put(RightCtx.PERMISSION, RightPermission.PERMISSION_PERFORM);
            isCanPerform = this.queryPermissionToActivityInst(activityInstID, ctx);
        }
        return isCanPerform;

    }

    public List<ActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException {
        List<EIActivityInstHistory> activityInstHistoryList = workflowEngine.getAllOutActivityInstHistoryByActvityInstHistory(historyHisroryId, noSplit);
        return new WorkflowListProxy(activityInstHistoryList, getSystemCode());
    }

    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }


    @Override
    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException {
        EIActivityDef eiActivityDef = this.workflowEngine.getFirstActivityDefInProcess(processDefVersionId);

        return this.getActivityDef(eiActivityDef.getActivityDefId());
    }


}


