/**
 * $RCSfile: WorkflowEngine.java,v $
 * $Revision: 1.4 $
 * $Date: 2016/01/23 16:29:52 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.util.TimeUtility;
import net.ooder.bpm.enums.activitydef.ActivityDefJoin;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.activitydef.ActivityDefRouteBackMethod;
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryStatus;
import net.ooder.bpm.enums.process.ProcessInstRunStatus;
import net.ooder.bpm.enums.process.ProcessInstStartType;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.bpm.routefitle.RouteConditionFilter;
import net.ooder.common.ReturnType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.Filter;
import net.ooder.config.ActivityDefImpl;
import net.ooder.engine.event.EventControl;
import net.ooder.common.CommonYesNoEnum;

import java.util.*;

//import net.ooder.common.Condition;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统核心引擎!
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
public class WorkflowEngineImpl implements WorkflowEngine {
    private Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, WorkflowEngineImpl.class);

    EIProcessDefManager processDefMgr = null;

    EIProcessDefVersionManager processDefVerMgr = null;

    EIActivityDefManager activityDefMgr = null;

    EIRouteDefManager routeDefMgr = null;

    EIProcessInstManager processInstMgr = null;

    EIActivityInstManager activityInstMgr = null;

    EIRouteInstManager routeInstMgr = null;

    EIActivityInstHistoryManager activityInstHistoryMgr = null;

    EventControl eventControl = null;

    private String systemCode;

    public static WorkflowEngine getEngine(String systemCode) {

        if (engine == null) {
            synchronized (WorkflowEngineImpl.class) {
                if (engine == null) {
                    engine = new WorkflowEngineImpl(systemCode);
                }
            }
        }
        return engine;
    }

    /**
     * @link
     * @shapeType PatternLink
     * @pattern Singleton
     * @supplierRole Singleton factory
     */
    /* # private ProcessEngine _processEngine; */
    private static WorkflowEngineImpl engine = null;

    protected WorkflowEngineImpl(String systemCode) {
        processDefMgr = EIProcessDefManager.getInstance();
        processDefVerMgr = EIProcessDefVersionManager.getInstance();
        activityDefMgr = EIActivityDefManager.getInstance();
        routeDefMgr = EIRouteDefManager.getInstance();
        processInstMgr = EIProcessInstManager.getInstance();
        activityInstMgr = EIActivityInstManager.getInstance();
        routeInstMgr = EIRouteInstManager.getInstance();
        activityInstHistoryMgr = EIActivityInstHistoryManager.getInstance();
        eventControl = EventControl.getInstance();
        this.systemCode = systemCode;

    }

    /**
     * 取得符合条件的流程定义版本列表。
     *
     * @param condition 查询条件，例如根据流程定义的名称进行查询。
     * @param filter    扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    public List<EIProcessDefVersion> getProcessDefVersionList(BPMCondition condition, Filter filter) throws BPMException {
        String where = "";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {
                where = " where " + conditionStr;
            }
        }

        List processDefList = processDefVerMgr.loadByWhere(where);
        Iterator it = processDefList.iterator();
        List result;
        if (filter == null) {
            result = processDefList;
        } else {
            result = new ArrayList();
            for (; it.hasNext(); ) {
                EIProcessDefVersion ver = (EIProcessDefVersion) it.next();
                if (filter.filterObject(ver, systemCode)) {
                    result.add(ver);
                }
            }
        }
        return result;
    }

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition 查询条件，例如根据流程定义的名称进行查询。
     * @param filter    扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    public List<EIProcessDef> getProcessDefList(BPMCondition condition, Filter filter) throws BPMException {
        String where = "";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {
                where = " where " + conditionStr;
            }
        }

        List processDefList = processDefMgr.loadByWhere(where);
        Iterator it = processDefList.iterator();
        List result;
        if (filter == null) {
            result = processDefList;
        } else {
            result = new ArrayList();
            for (; it.hasNext(); ) {
                EIProcessDef def = (EIProcessDef) it.next();
                if (filter.filterObject(def, systemCode)) {
                    result.add(def);
                }
            }
        }
        return result;
    }

    /**
     * 取得符合条件的流程实例列表
     *
     * @param condition 查询条件，例如根据流程状态，流程类型，流程定义 ID或用户ID进行查询。
     * @param filter    扩展属性过滤器。
     * @return 所有符合条件的ProcessInst列表
     * @throws BPMException
     */
    public List<EIProcessDef> getProcessInstList(BPMCondition condition, Filter filter) throws BPMException {
        String where = "";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {

                where = " where " + conditionStr;
            }
        }

        List processInstList = processInstMgr.loadByWhere(where);
        Iterator it = processInstList.iterator();
        List result;

        if (filter == null) {
            result = processInstList;
        } else {
            result = new ArrayList<EIProcessInst>();
            for (; it.hasNext(); ) {
                EIProcessInst inst = (EIProcessInst) it.next();
                if (filter.filterObject(inst, systemCode)) {
                    result.add(inst);
                }
            }
        }
        return result;
    }

    /**
     * 取得符合条件的所有活动历史。
     *
     * @param condition 查询条件，例如根据活动办理情况或所属流程实例进行查询。
     * @param filter    扩展属性过滤器。
     * @return 所有符合条件的EIActivityInstHistory列表
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryList(BPMCondition condition, Filter filter) throws BPMException {
        String where = "";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {
                where = " where " + conditionStr;
            }
        }

        List activityInstHistoryList = activityInstHistoryMgr.loadByWhere(where);
        Iterator it = activityInstHistoryList.iterator();
        List result;

        if (filter == null) {
            result = activityInstHistoryList;
        } else {
            result = new ArrayList();
            for (; it.hasNext(); ) {
                EIActivityInstHistory inst = (EIActivityInstHistory) it.next();
                if (filter.filterObject(inst, systemCode)) {
                    result.add(inst);
                }
            }
        }
        return result;
    }

    /**
     * 取得符合条件的所有活动实例。
     *
     * @param condition 查询条件，例如根据活动状态(待办或在办的 活动)或所属流程实例进行查询。
     * @param filter    扩展属性过滤器。
     * @return 所有符合条件的ActivityInst列表
     * @throws BPMException
     */
    public List<EIActivityInst> getActivityInstList(BPMCondition condition, Filter filter) throws BPMException {
        String where = "";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {
                where = " where " + conditionStr;
            }
        }

        List activityInstList = activityInstMgr.loadByWhere(where);
        Iterator it = activityInstList.iterator();
        List result;

        if (filter == null) {
            result = activityInstList;
        } else {
            result = new ArrayList();
            for (; it.hasNext(); ) {
                EIActivityInst inst = (EIActivityInst) it.next();
                if (filter.filterObject(inst, systemCode)) {
                    result.add(inst);
                }
            }
        }
        return result;
    }

    public EIProcessInst createProcessInst(String defId, String instName, String urgency) throws BPMException {
        // 默认为人工启动
        return createProcessInst(defId, instName, urgency, ProcessInstStartType.MANUAL.getType());
    }

    public EIProcessInst createProcessInst(String defId, String instName, String urgency, String initType) throws BPMException {
        // get the process definition
        log.debug("creating process instance, defId=" + defId);
        EIProcessDefVersion defVer = processDefVerMgr.getActiveProcessDefVersion(defId);
        if (defVer == null) {
            throw new BPMException("can't found the Process Definition " + defId);
        }
        // create instance from manager
        EIProcessInst inst = processInstMgr.createProcessInstance();
        inst.setProcessDefId(defId);
        inst.setProcessDefVersionId(defVer.getProcessDefVersionId());
        inst.setProcessInstId(UUID.randomUUID().toString());
        inst.setName(instName);
        inst.setCopyNumber(0);
        inst.setRunStatus(ProcessInstRunStatus.NORMAL.getType());
        // 对于自动启动流程，流程实例状态设为running
        if (ProcessInstStartType.AUTO.getType().equalsIgnoreCase(initType)) {
            inst.setState(ProcessInstStatus.running.getType());
        } else {
            inst.setState(ProcessInstStatus.notStarted.getType());
        }
        inst.setUrgency(urgency);
        // 保存
        processInstMgr.save(inst);
        // add by lxl 2004-04-30 解决新建的实例和Cache中的不是同一个得问题
        inst = processInstMgr.loadByKey(inst.getProcessInstId());
        return inst;

    }

    /**
     * 新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     *
     * @param processInstId
     * @param activityDefId
     * @return
     * @throws BPMException
     */
    public EIActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId) throws BPMException {
        EIActivityInst actInst = activityInstMgr.createActivityInstance();
        EIProcessInst inst = processInstMgr.loadByKey(processInstId);
        String verId = inst.getProcessDefVersionId();
        // get the first activity definition
        EIActivityDef actDef = this.activityDefMgr.loadByKey(activityDefId);
        if (actDef == null) {
            return null;
        }
        // set activity instance attribute;
        actInst.setActivityInstId(UUID.randomUUID().toString());
        actInst.setActivityDefId(actDef.getActivityDefId());
        actInst.setProcessDefId(actDef.getProcessDefId());
        actInst.setProcessInstId(processInstId);
        actInst.setArrivedTime(new java.util.Date());
        actInst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
        actInst.setReceiveMethod(actDef.getRouteBackMethod());
        actInst.setState(ActivityInstStatus.notStarted.getType());
        actInst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
        activityInstMgr.save(actInst);
        return actInst;

    }

    /**
     * 根据流程的开始活动节点，创建活动实例，但未启动活动实例
     *
     * @param processInstId
     * @return
     * @throws BPMException
     */
    public EIActivityInst startProcessInst(String processInstId) throws BPMException {
        // fireProcessEvent(inst, EIProcessEvent.PROCESS_STARTING);
        log.debug("starting process instance " + processInstId);

        EIProcessInst inst = processInstMgr.loadByKey(processInstId);
        if (inst == null) {
            throw new BPMException("The process instance not found, Id is " + processInstId);
        }
        EIActivityInst actInst = activityInstMgr.createActivityInstance();
        String verId = inst.getProcessDefVersionId();
        // get the first activity definition
        EIActivityDef actDef = this.getFirstActivityDefInProcess(verId);
        if (actDef == null) {
            return null;
        }

        // set activity instance attribute;
        actInst.setActivityInstId(UUID.randomUUID().toString());
        actInst.setActivityDefId(actDef.getActivityDefId());
        actInst.setProcessDefId(actDef.getProcessDefId());
        actInst.setProcessInstId(inst.getProcessInstId());
        actInst.setArrivedTime(new java.util.Date());
        actInst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
        actInst.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
        actInst.setState(ActivityInstStatus.notStarted.getType());
        actInst.setRunStatus(ActivityInstRunStatus.PROCESSNOTSTARTED.getType());

        activityInstMgr.save(actInst);

        // 设置流程启动时间，时间限制，更新流程实例状态
        Date start = new Date(System.currentTimeMillis());
        EIProcessDefVersion procDefVer = inst.getProcessDefVersion();
        inst.setStartTime(start);
        if (procDefVer.getLimit() != 0) {
            inst.setLimitTime(TimeUtility.roll(start, procDefVer.getLimit(), procDefVer.getDurationUnit()));
        }
        processInstMgr.save(inst);
        // add by lxl 2004-04-30 解决新建的实例和Cache中的不是同一个得问题
        actInst = activityInstMgr.loadByKey(actInst.getActivityInstId());
        return actInst;
    }

    /**
     * 取得流程第一个活动对象
     *
     * @param processDefVersionId
     * @return
     * @throws BPMException
     */
    public EIActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = this.processDefVerMgr.loadByKey(processDefVersionId);
        RouteConditionFilter routeConditionFilter = new RouteConditionFilter(new HashMap());
        if (eiProcessDefVersion == null) {
            throw new BPMException("The eiProcessDefVersion  not found, Id is " + processDefVersionId);
        }
        // condition here
        EIActivityDef eiActivityDef = activityDefMgr.getFirstActivityDefInProcess(eiProcessDefVersion.getProcessDefVersionId());

        if (eiActivityDef.getImplementation().equals(ActivityDefImpl.Tool)) {
            String where = " where FROMACTIVITYDEF_ID = '" + eiActivityDef.getActivityDefId() + "' and ROUTEDIRECTION='" + RouteDirction.FORWARD.getType() + "'";

            // 路由顺序
            where += " order by ROUTEORDER";
            List routeList = routeDefMgr.loadByWhere(where);
            // filter here
            Iterator it = routeList.iterator();
            List<EIRouteDef> result = new ArrayList<EIRouteDef>();

            if (routeConditionFilter == null) {
                result = routeList;
            } else {
                result = new ArrayList();
                for (; it.hasNext(); ) {
                    EIRouteDef routeDef = (EIRouteDef) it.next();
                    if (routeConditionFilter.filterObject(routeDef, systemCode)) {
                        result.add(routeDef);
                    }
                }
            }

            if (result.size() == 0) {
                throw new BPMException("The startActivity  not found, the express par no ActivityDef can start");
            }
            eiActivityDef = result.get(0).getToActivityDef();
        }

        return eiActivityDef;
    }

    /**
     * 启动活动实例
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public ReturnType startActivityInst(String activityInstId) throws BPMException {
        log.debug("starting Activity " + activityInstId);
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstId);
        if (actInst == null) {
            throw new BPMException("The activity instance not found, Id is " + activityInstId);
        }
        actInst.setStartTime(new java.util.Date());
        // 设置启动时间，时间限制，更新流程实例状态
        Date start = new Date(System.currentTimeMillis());
        EIActivityDef actDef = actInst.getActivityDef();
        if (actDef == null) {
            throw new BPMException("The activity definition not found.");
        }
        actInst.setStartTime(start);
        if (actDef.getLimit() != 0) {
            actInst.setLimitTime(TimeUtility.roll(start, actDef.getLimit(), actDef.getDurationUnit()));
        }

        if (actDef.getAlertTime() != 0) {
            actInst.setAlertTime(TimeUtility.roll(start, actDef.getLimit() - actDef.getAlertTime(), actDef.getDurationUnit()));
        }
        actInst.setState(ActivityInstStatus.running.getType());

        // 实例化扩展属性
        activityInstMgr.instantiateExtAttribute(actInst);
        activityInstMgr.save(actInst);

        EIProcessInst procInst = actInst.getProcessInst();
        if (procInst != null && ProcessInstStatus.notStarted.equals(procInst.getState())) {
            updateProcessState(procInst.getProcessInstId(), ProcessInstStatus.running.getType());
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 更新流程实例的状态。
     *
     * @param processInstId 流程实例的标识
     * @param state         新的流程实例状态
     * @return
     * @throws BPMException
     */
    public ReturnType updateProcessState(String processInstId, String state) throws BPMException {
        EIProcessInst inst = processInstMgr.loadByKey(processInstId);
        if (inst == null) {
            throw new BPMException("The specified process instance not found: " + processInstId);
        }
        String oldProcessState = inst.getState();
        if (state != null && !state.equals(oldProcessState)) {
            inst.setState(state);
            processInstMgr.save(inst);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 保存活动历史。 <br>
     * 1. 将活动实例变为历史活动并存入数据库； <br>
     * 2. 并修改与该活动实例相对应的路由实例的信息； <br>
     * 3. 重新设置该活动实例的信息。 <br>
     * 4. 创建路由实例，并将路由实例的出发节点指向该历史实例，到达节点指向该活动实例。
     *
     * @param activityInstId 活动实例的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInstHistory saveActivityHistoryInst(String activityInstId) throws BPMException {

        EIActivityInst from = activityInstMgr.loadByKey(activityInstId);
        if (from == null) {
            throw new BPMException("The activity instance not found, Id is " + activityInstId);
        }
        EIActivityInstHistory history = null;
        // 新建历史活动并保存
        history = activityInstHistoryMgr.saveActivityInstAsHistory(from);
        // 修改与该实例活动相对应的路由实例信息
        List routeInsts = routeInstMgr.getRouteInsts(from);
        for (int i = 0; i < routeInsts.size(); i++) {
            EIRouteInst routeInst = (EIRouteInst) routeInsts.get(i);
            if (routeInst != null) {
                routeInst.setToActivityId(history.getActivityHistoryId());
                String direction = from.getReceiveMethod();
                if (direction != null) {
                    if (direction.equals(ActivityInstReceiveMethod.SEND.getType())) {
                        routeInst.setRouteDirection(RouteDirction.FORWARD);
                    } else if (direction.equals(ActivityInstReceiveMethod.BACK.getType())) {
                        routeInst.setRouteDirection(RouteDirction.BACK);
                    } else if (direction.equals(ActivityInstReceiveMethod.SPECIAL.getType())) {
                        routeInst.setRouteDirection(RouteDirction.SPECIAL);
                    }
                }
                routeInst.setRouteType(RouteInstType.HISTORY);
                routeInst.setRouteTime(new Date(System.currentTimeMillis()));
                // 保存
                routeInstMgr.save(routeInst);
            }
        }

        // 重设该活动实例的信息
        if (!from.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
            from.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
            from.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
            from.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
            from.setArrivedTime(new java.util.Date());
            from.setState(ActivityInstStatus.notStarted.getType());
            activityInstMgr.save(from);
        }

        // 创建新的路由实例
        EIRouteInst routeInst;
        routeInst = routeInstMgr.createRouteInst();
        routeInst.setRouteInstId(UUID.randomUUID().toString());
        routeInst.setProcessInstId(from.getProcessInstId());
        routeInst.setFromActivityId(history.getActivityHistoryId());
        routeInst.setToActivityId(from.getActivityInstId());
        routeInst.setRouteType(RouteInstType.ACTIVITY);
        routeInstMgr.save(routeInst);

        return history;
    }

    public EIActivityInst getSplitActivityInst(String activityHistoryInstId, boolean isnew) throws BPMException {
        EIActivityInstHistory his = activityInstHistoryMgr.loadByKey(activityHistoryInstId);
        // 取得最后一次分裂的历史
        if (!his.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
            List<EIActivityInstHistory> historyList = getLastActivityInstHistoryListByActvityInst(his.getActivityInstId(), true);
            for (int i = 0; i < historyList.size(); i++) {
                EIActivityInstHistory ahis = historyList.get(i);
                if (his.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType()))
                    ;
                activityHistoryInstId = his.getActivityHistoryId();
                his = ahis;
                continue;
            }
        }
        EIActivityInst inst = null;

        if (isnew) {
            inst = this.copyActivityInst(activityHistoryInstId, 1).get(0);
        } else {
            inst = this.activityInstMgr.loadByKey(his.getActivityInstId());
            inst.setCanTakeBack(CommonYesNoEnum.NO.getType());
            inst.setState(ActivityInstStatus.notStarted.getType());
            inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
            inst.setReceiveMethod(ActivityInstReceiveMethod.RESEND.getType());
            inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
        }

        return inst;
    }

    /**
     * 分裂活动实例为不同的几个活动实例，活动分裂后将不能够收回
     *
     * @param activityInstId        需要分裂的活动实例标识
     * @param count                 分裂的数量
     * @param activityHistoryInstId 路由目标节点的活动定义标识的列表
     *                              路由目标节点的活动定义标识的列表
     * @return 分裂后的活动实例列表，其中一个活动实例重用了原活动实例，其他的活动实例 为新创建的
     * @throws BPMException
     */
    public List<EIActivityInst> splitActivityInst(String activityInstId, int count, String activityHistoryInstId) throws BPMException {
        List result = new ArrayList();

        if (count > 0) {
            EIActivityInst original = activityInstMgr.loadByKey(activityInstId);
            if (original == null) {
                throw new BPMException("The activity instance not found, Id is " + activityInstId);
            }

            // result.add(original);
            for (int i = 0; i < count; i++) {
                EIActivityInst inst = activityInstMgr.copyActivityInst(original);
                result.add(inst);
                // 创建新的路由实例
                EIRouteInst routeInst;
                routeInst = routeInstMgr.createRouteInst();
                routeInst.setRouteInstId(UUID.randomUUID().toString());
                routeInst.setProcessInstId(inst.getProcessInstId());
                routeInst.setFromActivityId(activityHistoryInstId);
                routeInst.setToActivityId(inst.getActivityInstId());
                routeInst.setRouteType(RouteInstType.ACTIVITY);
                routeInstMgr.save(routeInst);
            }
            // 将是否可收回标志置为“NO”

            EIActivityInstHistory history = this.activityInstHistoryMgr.loadByKey(activityHistoryInstId);
            history.setDealMethod(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType());
            activityInstHistoryMgr.save(history);

            original.setCanTakeBack(CommonYesNoEnum.NO.getType());
            // saveActivityHistoryInst(activityInstId);
            original.setDealMethod(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType());
            original.setState(ActivityInstStatus.suspended.getType());
            activityInstMgr.save(original);
            // 更新流程实例的copy数。
            EIProcessInst procInst = original.getProcessInst();
            procInst.setCopyNumber(count);
            processInstMgr.save(procInst);

        } else {

            throw new BPMException("The copy number should be bigger than 0!");
        }
        return result;
    }

    /**
     * 为抄送需求分裂活动实例为不同的几个活动实例，活动分裂后将不能够收回，状态转变为抄送状态
     *
     * @param activityHistoryInstId 路由目标节点的活动定义标识的列表
     * @return 分裂后的活动实例列表，其中一个活动实例重用了原活动实例，其他的活动实例 为新创建的
     * @throws BPMException
     */
    public List<EIActivityInst> copyActivityInst(String activityHistoryInstId, int count) throws BPMException {
        List result = new ArrayList();
        if (count > 0) {
            EIActivityInstHistory hisOriginal = activityInstHistoryMgr.loadByKey(activityHistoryInstId);
            EIActivityInst original = hisOriginal.getActivityInst();
            if (original == null) {
                throw new BPMException("The activity instance not found, Id is " + activityHistoryInstId);
            }
            boolean splited = false;
            if (original.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
                splited = true;
            }

            // 将是否可收回标志置为“NO”
            original.setCanTakeBack(CommonYesNoEnum.NO.getType());
            original.setState(ActivityInstStatus.notStarted.getType());
            original.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
            if (!splited) {
                original.setReceiveMethod(ActivityInstReceiveMethod.READ.getType());
            } else {
                original.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
            }
            original.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
            // activityInstMgr.save(original);
            // result.add(original);

            // 新建n-1份
            for (int i = 0; i < count; i++) {
                EIActivityInst inst = activityInstMgr.copyActivityInst(original);

                if (splited) {
                    inst.setCanTakeBack(CommonYesNoEnum.NO.getType());
                    inst.setState(ActivityInstStatus.notStarted.getType());
                    inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
                    inst.setReceiveMethod(ActivityInstReceiveMethod.RESEND.getType());
                    inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
                } else {
                    inst.setCanTakeBack(CommonYesNoEnum.NO.getType());
                    inst.setState(ActivityInstStatus.READ.getType());
                    inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
                    inst.setReceiveMethod(ActivityInstReceiveMethod.READ.getType());
                    inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
                }
                activityInstMgr.save(inst);
                result.add(inst);

                // 创建新的路由实例
                EIRouteInst routeInst;
                routeInst = routeInstMgr.createRouteInst();
                routeInst.setRouteInstId(UUID.randomUUID().toString());
                routeInst.setProcessInstId(inst.getProcessInstId());
                routeInst.setFromActivityId(activityHistoryInstId);
                routeInst.setToActivityId(inst.getActivityInstId());
                routeInst.setRouteType(RouteInstType.ACTIVITY);
                routeInstMgr.save(routeInst);
            }
            // 如果是补发则需要恢复原路有设置
            if (splited) {
                original.setCanTakeBack(CommonYesNoEnum.NO.getType());
                original.setState(ActivityInstStatus.suspended.getType());
                original.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
                original.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
                original.setDealMethod(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType());

            }
            activityInstMgr.save(original);
        } else {
            throw new BPMException("The copy number should be bigger than 0!");
        }
        return result;
    }

    /**
     * 取得所有可提交的前进路由的列表
     *
     * @param activityInstID 路由起始活动实例ID
     * @param condition      查询条件。
     * @param routeFilter    路由条件过滤器。
     * @return 路由列表
     * @throws BPMException
     */
    public List<EIRouteDef> getNextRoutes(String activityInstID, BPMCondition condition, Filter routeFilter) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found, Id is " + activityInstID);
        }
        // condition here
        String where = " where FROMACTIVITYDEF_ID = '" + actInst.getActivityDefId() + "' and ROUTEDIRECTION='" + RouteDirction.FORWARD.getType() + "'";
        if (condition != null) {
            String conditionStr = condition.makeConditionString();
            if (conditionStr != null && !conditionStr.equals("")) {
                where += " and " + conditionStr;
            }
        }
        // 路由顺序
        where += " order by ROUTEORDER";
        List routeList = routeDefMgr.loadByWhere(where);
        // filter here
        Iterator it = routeList.iterator();
        List result = new ArrayList();

        if (routeFilter == null) {
            result = routeList;
        } else {
            result = new ArrayList();
            for (; it.hasNext(); ) {
                EIRouteDef routeDef = (EIRouteDef) it.next();
                if (routeFilter.filterObject(routeDef, systemCode)) {
                    result.add(routeDef);
                }
            }
        }

        // if (result == null || result.size() == 0) {
        // throw new BPMException("No route found!");
        // }
        return result;
    }

    /**
     * 预测某活动实例可以合并的活动实例标识的数组， 如果该流程只有一份活动实例或者该活动实例节点不需要合并的则返回一个空List， 否则该流程实例所有的活动实例的标识列表。
     *
     * @return
     * @throws BPMException
     */
    public List<EIActivityInst> forecastCombinableActivityInsts(String actInstId, String actDefId) throws BPMException {
        EIActivityInst activityInst = activityInstMgr.loadByKey(actInstId);
        if (activityInst == null) {
            throw new BPMException("The activity instance not found! Id is:" + actInstId);
        }
        EIProcessInst processInst = processInstMgr.loadByKey(activityInst.getProcessInstId());
        if (processInst == null) {
            throw new BPMException("The process instance not found!");
        }
        List activityInsts = null;
        int copies = processInst.getCopyNumber();
        if (copies <= 1) {
            return new ArrayList();
        }
        EIActivityDef actDef = activityDefMgr.loadByKey(actDefId);
        if (actDef == null) {
            throw new BPMException("The activity definition not found!");
        }
        // if (actDef.getAttributeValue(IOTRightConstants.ACTIVITYDEF_RIGHT_ATT_PERFORMSEQUENCE)
        // .equals(IOTRightConstants.PERFORMSEQUENCE_FIRST)){
        //
        // }
        //

        String join = actDef.getJoin();
        if (join != null && !join.equals(ActivityDefJoin.JOIN_AND)) {
            return new ArrayList();
        }
        String where = " WHERE PROCESSINST_ID='" + processInst.getProcessInstId() + "' order by ACTIVITYINST_ID";
        return activityInstMgr.loadByWhere(where);

    }

    /**
     * 取得某活动实例可以合并的活动实例标识的数组， 如果该流程只有一份活动实例或者该活动实例节点不需要合并的则返回一个空List， 否则该流程实例所有的活动实例的标识列表。
     *
     * @return
     * @throws BPMException
     */
    public List combinableActivityInsts(String actInstId) throws BPMException {
        EIActivityInst activityInst = activityInstMgr.loadByKey(actInstId);

        if (activityInst == null) {
            throw new BPMException("The activity instance not found! Id is:" + actInstId);
        }
        EIProcessInst processInst = processInstMgr.loadByKey(activityInst.getProcessInstId());
        if (processInst == null) {
            throw new BPMException("The process instance not found!");
        }
        List activityInsts = null;

        EIActivityDef actDef = activityInst.getActivityDef();
        if (actDef == null) {
            throw new BPMException("The activity definition not found!");
        }

        String join = actDef.getJoin();

        if (join != null && !join.equals(ActivityDefJoin.JOIN_AND.getType())) {
            return new ArrayList();
        }

        List<EIActivityInstHistory> hisList = getLastSplitActivityInstHistoryByActvityInst(actInstId);
        // 取得最近一次的分裂 通常情况下只有一个
        // 如果出现多次分类则只合并当前分支的，不支持二次分裂合并
        if (hisList.size() == 0) {
            activityInsts = new ArrayList();
        } else {
            activityInsts = getActivityInstListByOutActvityInstHistory(hisList.get(hisList.size() - 1).getActivityHistoryId());
        }

        return activityInsts;
        // String where = " where PROCESSINST_ID='"
        // + processInst.getProcessInstId() + "' and ACTIVITYINST_STATE='"
        // + ActivityInstStatus.SUSPENDED + "' order by ACTIVITYINST_ID";
        // return activityInstMgr.loadByWhere(where);
    }

    /**
     * 预测某活动实例到达指定活动节点后是否可以合并或者需要挂起 1.判断所有的副本是否都到达<br>
     * 2.如果所有的副本都已到达，则合并，否则挂起<br>
     *
     * @return
     * @throws BPMException 此方法用于clientservice在路由前预测是否会发生合并操作
     */
    @Deprecated
    public String forecastSuspendOrCombine(String actInstId, String actDefId) throws BPMException {

        return suspendOrCombine(actInstId, actDefId);
    }

    /**
     * 某活动实例是否可以合并或者需要挂起 1.判断所有的副本是否都到达<br>
     * 2.如果所有的副本都已到达，则合并，否则挂起<br>
     *
     * @return
     * @throws BPMException
     */
    public String suspendOrCombine(String actInstId) throws BPMException {
        return suspendOrCombine(actInstId, null);
    }

    public String suspendOrCombine(String actInstId, String activityDefId) throws BPMException {
        EIActivityInst activityInst = activityInstMgr.loadByKey(actInstId);
        if (activityInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        EIProcessInst processInst = processInstMgr.loadByKey(activityInst.getProcessInstId());
        if (processInst == null) {
            throw new BPMException("The process instance not found!");
        }
        if (activityDefId == null) {
            activityDefId = activityInst.getActivityDefId();
        }

        List<EIActivityInstHistory> hisList = getLastSplitActivityInstHistoryByActvityInst(actInstId);
        List activityInsts = new ArrayList();

        // 取得最近一次的分裂 通常情况下只有一个
        // 如果出现多次分类则只合并当前分支的，不支持二次分裂合并
        if (hisList.size() > 0) {
            activityInsts = getActivityInstListByOutActvityInstHistory(hisList.get(hisList.size() - 1).getActivityHistoryId());
        }

        for (Iterator iter = activityInsts.iterator(); iter.hasNext(); ) {
            EIActivityInst inst = (EIActivityInst) iter.next();
            if (!inst.getActivityDefId().equals(activityDefId) && !inst.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
                return ActivityInstSuSpend.SUSPEND.getType();
            }

        }
        return ActivityInstSuSpend.COMBINE.getType();
    }

    /**
     * 从某个活动实例路由到另一活动实例。 不重新创建实例，而是重复使用该实例。<br>
     * 另外，分裂了的活动需要在需合并的节点挂起等待或进行合并<br>
     * 活动有多个副本的情况：<br>
     * 1.判断该节点是否是合并节点<br>
     * 2.判断所有的副本是否都到达<br>
     * 3.如果所有的副本都已到达，则合并，否则挂起<br>
     * 副本合并时需要执行以下操作：<br>
     *
     * @param fromActivityInstId 路由出发点活动实例（或分裂后的实例）的标识
     * @param toActivityDefId    路由目标节点活动定义的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInst routeTo(String fromActivityInstId, String toActivityDefId) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(fromActivityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        EIActivityDef toDef = activityDefMgr.loadByKey(toActivityDefId);
        if (toDef == null) {
            throw new BPMException("The activity definition not found!");
        }

        // 结束节点，直接调用流程结束方法
        if (ActivityDefPosition.VIRTUAL_LAST_DEF.getType().equals(toActivityDefId)) {
            // activityInstMgr.deleteByKey(fromActivityInstId);
            completeProcessInst(inst.getProcessInstId());
            return null;
        } else {

            EIProcessInst processInst = inst.getProcessInst();
            inst.setActivityDefId(toActivityDefId);
            inst.setArrivedTime(new java.util.Date());
            if (!inst.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {

                inst.setActivityDefId(toActivityDefId);
                inst.setArrivedTime(new java.util.Date());
                inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
                inst.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
                inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
                inst.setState(ActivityInstStatus.notStarted.getType());
            }
            if (processInst == null) {
                throw new BPMException("The process instance not found!");
            }
            // if (processInst.getCopyNumber() > 1) {
            // EIActivityDef actDef = inst.getActivityDef();
            // if (actDef == null) {
            // throw new BPMException("The activity definition not found!");
            // }
            // String join = actDef.getJoin();
            //
            // if (join != null && join.equals(ActivityDef.JOIN_AND)) {
            // inst.setState(ActivityInstStatus.SUSPENDED);
            // activityInstMgr.save(inst);
            // }
            // }

            activityInstMgr.save(inst);
        }

        return inst;
    }

    /**
     * 是否可以退回。
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public boolean canRouteBack(String activityInstId) throws BPMException {
        boolean can = false;
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        // 如果不在运行状态，返回false
        if (!ActivityInstStatus.running.getType().equals(inst.getState())) {
            return false;
        }
        EIActivityDef def = inst.getActivityDef();
        String canRouteBack = def.getCanRouteBack();

        if (CommonYesNoEnum.YES.getType().equals(canRouteBack)) {
            can = true;
        }

        return can;
    }

    /**
     * 取得活动实例可以退回的活动历史实例的列表，可退回到的节点由 “ROUTEBACKMETHOD”字段的值确定
     *
     * @param activityInstId
     * @param routeFilter
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getRouteBacks(String activityInstId, Filter routeFilter) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        EIActivityDef def = inst.getActivityDef();
        if (def == null) {
            throw new BPMException("The activity definition not found!");
        }

        List result = new ArrayList();
        String routeBackMethod = def.getRouteBackMethod();
        if (routeBackMethod == null) {
            routeBackMethod = ActivityDefRouteBackMethod.ANY.getType();
        }

        // 退回方法为：回到上一节点(可能有多个)
        if (ActivityDefRouteBackMethod.LAST.getType().equals(routeBackMethod)) {
            List routeInsts = routeInstMgr.getRouteInsts(inst);
            for (Iterator iter = routeInsts.iterator(); iter.hasNext(); ) {
                EIRouteInst routeInst = (EIRouteInst) iter.next();
                String activityInstHistoryId = routeInst.getFromActivityId();
                try {
                    EIActivityInstHistory history = activityInstHistoryMgr.loadByKey(activityInstHistoryId);
                    result.add(history);
                } catch (BPMException e) {
                }
            }
        } else {
            // 退回方法为：回到任一节点
            // 一个节点可能有多个活动历史的情况
            if (ActivityDefRouteBackMethod.ANY.getType().equals(routeBackMethod)) {
                // 取得该活动的历史
                result = getActivityInstHistoryListByActvityInst(activityInstId);
                // Map map = new HashMap();
                // List historys = activityInstHistoryMgr.loadByWhere(" where
                // PROCESSINST_ID='" + inst.getProcessInstId() + "' order by
                // ENDTIME");
                // for (Iterator iter = historys.iterator(); iter.hasNext();) {
                // EIActivityInstHistory history =
                // (EIActivityInstHistory)iter.next();
                // if (!map.containsKey(history.getActivityDefId())) {
                // map.put(history.getActivityDefId(), history);
                // }
                // }
                // List historyList = new ArrayList(map.values());
                // Collections.sort(historyList, new
                // ActivityInstHistoryEndTimeComparator());
                // result.addAll(historyList);
            } else {
                // 退回方法为：回到指定节点(可能有多个)
                if (ActivityDefRouteBackMethod.SPECIFY.getType().equals(routeBackMethod)) {
                    // 根据路由定义取得退回路由的定义列表
                    // TODO 路由条件here
                    List routeDefs = routeDefMgr.loadByWhere(" where FROMACTIVITYDEF_ID='" + def.getActivityDefId() + "' and ROUTEDIRECTION='" + RouteDirction.BACK.getType() + "'");
                    // 可能存在多次退回的情况，一个节点可能有多个活动历史。
                    List historys = getActivityInstHistoryListByActvityInst(activityInstId);
                    Map map = new HashMap();
                    for (Iterator it = routeDefs.iterator(); it.hasNext(); ) {
                        // modify by lxl 2004-04-15 条件路由
                        EIRouteDef routeDef = (EIRouteDef) it.next();
                        if (routeFilter != null) {
                            if (!routeFilter.filterObject(routeDef, systemCode)) {
                                continue;
                            }
                        }

                        String toActivityDefId = routeDef.getToActivityDefId();
                        for (Iterator iter = historys.iterator(); iter.hasNext(); ) {
                            EIActivityInstHistory history = (EIActivityInstHistory) iter.next();
                            if (toActivityDefId.equals(history.getActivityDefId())) {
                                map.put(toActivityDefId, history);
                                break;
                            }
                        }
                    }
                    List historyList = new ArrayList(map.values());
                    // Collections.sort(historyList, new
                    // ActivityInstHistoryEndTimeComparator());
                    result.addAll(historyList);
                }
            }
        }
        return result;
    }

    /**
     * 活动实例退回操作。
     *
     * @param activityInstId        活动实例的标识。
     * @param activityInstHistoryId 退回到某活动实例历史的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInst routeBack(String activityInstId, String activityInstHistoryId) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        EIActivityInstHistory history = activityInstHistoryMgr.loadByKey(activityInstHistoryId);
        if (history == null) {
            throw new BPMException("The history activity instance not found!");
        }
        EIActivityDef toDef = history.getActivityDef();
        if (toDef == null) {
            throw new BPMException("The activity definition not found!");
        }

        inst.setActivityDefId(toDef.getActivityDefId());
        inst.setArrivedTime(new java.util.Date());
        inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
        inst.setReceiveMethod(ActivityInstReceiveMethod.BACK.getType());
        inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
        inst.setState(ActivityInstStatus.notStarted.getType());

        EIProcessInst processInst = inst.getProcessInst();
        if (processInst == null) {
            throw new BPMException("The process instance not found!");
        }
        if (processInst.getCopyNumber() > 1) {
            EIActivityDef actDef = inst.getActivityDef();
            if (actDef == null) {
                throw new BPMException("The activity definition not found!");
            }
            String join = actDef.getJoin();
            if (join != null && join.equals(ActivityDefJoin.JOIN_AND.getType())) {
                inst.setState(ActivityInstStatus.suspended.getType());
                activityInstMgr.save(inst);
            }
        }

        // 判断是否需要挂起或合并
        List activityInsts = combinableActivityInsts(inst.getActivityInstId());
        if (activityInsts.size() > 1) {
            // 挂起或者合并活动实例
            String suspendOrCombine = suspendOrCombine(inst.getActivityInstId());
            if (ActivityInstSuSpend.SUSPEND.getType().equals(suspendOrCombine)) {
                inst.setState(ActivityInstStatus.suspended.getType());
            } else if (ActivityInstSuSpend.COMBINE.getType().equals(suspendOrCombine)) {
                return combineActivityInsts(activityInsts);
            }
        }
        activityInstMgr.save(inst);

        return inst;
    }

    /**
     * 是否可以收回。<br>
     * 如果活动实例的状态为"notStarting"，而且活动的“是否可以收回”字段的值不为"NO"时， 则可以收回，否则不能收回。
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public boolean canTakeBack(String activityInstId) throws BPMException {
        if (activityInstId == null || activityInstId.equals("")) {
            return false;
        }
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }

        boolean can = false;
        String canTackBack = inst.getCanTakeBack();

        if (ActivityInstStatus.notStarted.getType().equals(inst.getState()) && !ActivityInstDealMethod.DEALMETHOD_SPLITED.getType().equals(inst.getDealMethod())) {
            // 是否可收回字段的值为空，或者值为ActivityInstArr.CANTAKEBACK_NO
            if (canTackBack == null || !canTackBack.equals(CommonYesNoEnum.NO.getType())) {
                can = true;
            }

        }
        return can;

    }

    /**
     * 收回活动实例，恢复上一节点的状态。<br>
     * 1. 删除从上节点到该节点的路由实例<br>
     * 2. 修改上上节点至上节点的路由实例<br>
     * 3. 修改该活动实例的相应信息，活动实例的扩展属性不变<br>
     * 4. 删除上一节点的活动历史
     *
     * @param activityInstId
     * @return
     */
    public ReturnType tackBack(String activityInstId, Map ctx) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);

        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        // 取得从上节点到该节点的路由实例
        List routeList = routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + activityInstId + "'");
        if (routeList == null || routeList.size() != 1) {
            throw new BPMException("can not found the corresponding route instance!");
        }
        EIRouteInst routeInst = (EIRouteInst) routeList.get(0);
        // 取得前一节点的历史活动ID
        String previousActivityInstHistoryId = routeInst.getFromActivityId();
        // 删除从上节点到该节点的路由实例
        routeInstMgr.deleteByWhere(" where TOACTIVITY_ID='" + activityInstId + "'");

        String performSequence = (String) ctx.get(RightCtx.PERFORMSEQUENCE.getType());
        if (performSequence.equals("MEANWHILE")) {
            // 如果是多人办理并且当前不只一条时则直接删除活动信息
            if (routeInstMgr.loadByWhere(" where FROMACTIVITY_ID='" + previousActivityInstHistoryId + "'").size() > 0) {
                // 删除上一节点的活动历史
                this.deleteActivityInst(activityInstId);
                // this.deleteHistory(previousActivityInstHistoryId, ctx);
            }

        } else {
            // 修改该活动实例的相应信息，根据历史信息还原
            EIActivityInstHistory history = activityInstHistoryMgr.loadByKey(previousActivityInstHistoryId);
            if (history == null) {
                throw new BPMException("The activity history not found!");
            }

            // 取得上上节点至上节点的路由实例
            List previousRouteList = routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + previousActivityInstHistoryId + "'");
            // 修改上上节点至上节点的所有路由实例
            if (previousRouteList != null && previousRouteList.size() != 0) {
                for (Iterator iter = previousRouteList.iterator(); iter.hasNext(); ) {
                    EIRouteInst e = (EIRouteInst) iter.next();
                    e.setToActivityId(history.getActivityInstId());
                    e.setRouteTime(null);
                    routeInstMgr.save(e);
                }
            }

            inst.setActivityDefId(history.getActivityDefId());
            inst.setArrivedTime(history.getArrivedTime());
            inst.setDealMethod(history.getDealMethod());
            inst.setLimitTime(history.getLimitTime());
            inst.setReceiveMethod(history.getReceiveMethod());
            inst.setRunStatus(history.getRunStatus());
            inst.setUrgency(history.getUrgency());
            inst.setState(ActivityInstStatus.running.getType());

            // 如果是分裂后可能产生的后果是将分裂的历史删掉了
            if (routeInstMgr.loadByWhere(" where FROMACTIVITY_ID='" + previousActivityInstHistoryId + "'").size() == 0) {
                // 删除上一节点的活动历史
                activityInstHistoryMgr.deleteByKey(previousActivityInstHistoryId);
            }

            // 判断是否需要合并 addby liwenzhang 就是发给多人办理后其它人都办完了，到同步环节那头，同步环节都挂起，就剩一个人没有办然后这头取回这个人，同步环节还是挂起

            // 如果是从草稿发送出来的仍然返回草稿状体
            EIActivityDef fristActivityDef = this.getFirstActivityDefInProcess(history.getProcessInst().getProcessDefVersionId());
            List<EIActivityInst> activityInsts = (List) ctx.get("combinableActivityInsts");

            String suspendOrCombine = (String) ctx.get("suspendOrCombine");

            if (activityInsts.size() != 0) {
                if (suspendOrCombine.equals(ActivityInstSuSpend.SUSPEND.getType())) {
                    // 挂起，不需要处理
                    inst.setState(ActivityInstStatus.suspended.getType());
                    inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
                } else if (suspendOrCombine.equals(ActivityInstSuSpend.COMBINE.getType())) {
                    inst = combineActivityInsts(activityInsts);
                }
            } else {
                if (history.getActivityDefId().equals(fristActivityDef.getActivityDefId())) {
                    this.updateProcessState(history.getProcessInstId(), ProcessInstStatus.notStarted.getType());
                }
            }

            activityInstMgr.save(inst);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 活动阅毕操作
     *
     * @param activityInstID 活动实例ID
     * @param ctx            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory endRead(String activityInstID, Map ctx) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        actInst.setState(ActivityInstStatus.ENDREAD.getType());
        actInst.setReceiveMethod(ActivityInstReceiveMethod.READ.getType());
        activityInstMgr.save(actInst);
        EIActivityInstHistory activityInstHistory = activityInstHistoryMgr.saveActivityInstAsHistory(actInst);

        return activityInstHistory;
    }

    ;

    /**
     * 活动结束操作
     *
     * @param activityInstID 活动实例ID
     * @param ctx            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory endTask(String activityInstID, Map ctx) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        actInst.setState(ActivityInstStatus.completed.getType());
        actInst.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
        activityInstMgr.save(actInst);
        EIActivityInstHistory activityInstHistory = activityInstHistoryMgr.saveActivityInstAsHistory(actInst);
        return activityInstHistory;
    }

    ;

    /**
     * 任务执行失败操作
     *
     * @param activityInstID 活动实例ID
     * @param ctx            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory abortedTask(String activityInstID, Map ctx) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        actInst.setState(ActivityInstStatus.terminated.getType());
        actInst.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());

        activityInstMgr.save(actInst);
        EIActivityInstHistory activityInstHistory = activityInstHistoryMgr.saveActivityInstAsHistory(actInst);
        return activityInstHistory;
    }

    ;

    /**
     * 彻底删除历史操作
     *
     * @param activityInstHistoryID 活动实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType clearHistory(String activityInstHistoryID, Map ctx) throws BPMException {
        EIActivityInstHistory history = this.activityInstHistoryMgr.loadByKey(activityInstHistoryID);
        history.setRunStatus(ActivityInstHistoryStatus.CLEAR.getType());
        activityInstHistoryMgr.save(history);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 删除历史操作
     *
     * @param activityInstHistoryID 活动实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType deleteHistory(String activityInstHistoryID, Map ctx) throws BPMException {
        // String userId = (String) ctx.get(RightEngine.CTX_USER_ID);
        //
        // EIActivityInstHistory history = this.activityInstHistoryMgr
        // .loadByKey(activityInstHistoryID);
        // EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
        // .createAttributeInst();
        // eiAttributeInst.setId(UUID.randomUUID().toString());
        // eiAttributeInst.setName(ActivityInstHistory.PERSON_STATS);
        // eiAttributeInst.setInterpretedValue(ActivityInstHistory.DELETEMETHOD_NORMAL);
        // history.setAttribute(userId, eiAttributeInst);
        // activityInstHistoryMgr.save(history);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 还原历史操作
     *
     * @param activityInstHistoryID 活动实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType restoreHistory(String activityInstHistoryID, Map ctx) throws BPMException {
        // EIActivityInstHistory history = this.activityInstHistoryMgr
        // .loadByKey(activityInstHistoryID);
        // history.setRunStatus(ActivityInstHistory.DEALMETHOD_NORMAL);
        // activityInstHistoryMgr.save(history);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 查询是否能签收
     *
     * @param activityInstID
     * @return
     */
    public boolean canSignReceive(String activityInstID) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstID);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        boolean can = false;
        if (ActivityInstStatus.notStarted.getType().equals(inst.getState()) && !ActivityInstDealMethod.DEALMETHOD_SPLITED.getType().equals(inst.getDealMethod())) {
            can = true;
        }
        return can;
    }

    /**
     * 签收活动实例，启动该活动实例。
     *
     * @param activityInstID 活动实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType signReceive(String activityInstID) throws BPMException {
        return startActivityInst(activityInstID);
    }

    /**
     * @param activityInsts
     * @return
     * @throws BPMException
     */
    public EIActivityInst combineActivityInsts(List<EIActivityInst> activityInsts) throws BPMException {
        List<String> activityInstIds = new ArrayList<String>();
        for (int k = 0; k < activityInsts.size(); k++) {
            activityInstIds.add(activityInsts.get(k).getActivityInstId());
        }
        String[] activityInstIdArr = activityInstIds.toArray(new String[]{});
        return combineActivityInsts(activityInstIdArr);
    }

    /**
     * 合并分裂的活动实例,保留第一个活动实例<br>
     * 1.需要合并的内容包括：扩展属性，活动的其他属性重新初始化<br>
     *
     * @param activityInsts 活动实例标识的数组
     * @return
     * @throws BPMException
     */
    public EIActivityInst combineActivityInsts(String[] activityInsts) throws BPMException {
        if (activityInsts == null || activityInsts.length < 1) {
            throw new BPMException("can not combine activity instances!");
        }
        EIActivityInst inst = (EIActivityInst) this.activityInstMgr.loadByKey(activityInsts[0]);

        if (inst == null) {
            throw new BPMException("Activity instance not found!");
        }
        if (!inst.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
            return inst;
            // throw new BPMException("Activity instance is not a split activityInst");
        }
        EIProcessInst processInst = inst.getProcessInst();
        if (processInst == null) {
            throw new BPMException("The process instance not found!");
        }
        int index = 0; // activityInsts.indexOf(inst);

        inst.setArrivedTime(new java.util.Date());
        inst.setDealMethod(ActivityInstDealMethod.DEALMETHOD_NORMAL.getType());
        inst.setReceiveMethod(ActivityInstReceiveMethod.SEND.getType());
        inst.setRunStatus(ActivityInstRunStatus.NORMAL.getType());
        inst.setState(ActivityInstStatus.notStarted.getType());
        // 将是否可以收回置为YES
        inst.setCanTakeBack(CommonYesNoEnum.YES.getType());

        // 将其他活动实例的扩展属性添加到第一个活动实例中，如果扩展属性与第一个相同则更新之
        for (int i = 0, n = activityInsts.length; i < n; i++) {
            if (i != index) {
                EIActivityInst tempInst = (EIActivityInst) this.activityInstMgr.loadByKey(activityInsts[i]);
                List attributes = tempInst.getAllAttribute();
                for (Iterator iter = attributes.iterator(); iter.hasNext(); ) {
                    EIAttributeInst attribute = (EIAttributeInst) iter.next();
                    inst.setAttribute(attribute.getName(), attribute);
                }

                // 删除活动实例，并将该活动实例所对应的路由实例挂接到第一个活动实例上
                deleteActivityInstAndResetRoute(tempInst.getActivityInstId(), inst.getActivityInstId());
            }

        }
        activityInstMgr.save(inst);
        // 将活动copy数置为1
        EIActivityDef fristActivityDef = this.getFirstActivityDefInProcess(inst.getProcessInst().getProcessDefVersionId());

        if (inst.getActivityDefId().equals(fristActivityDef.getActivityDefId())) {
            this.updateProcessState(inst.getProcessInstId(), ProcessInstStatus.notStarted.getType());
        }
        processInst.setCopyNumber(1);
        processInstMgr.save(processInst);
        return inst;
    }

    /**
     * 活动挂起
     *
     * @param activityInstID
     * @return
     * @throws BPMException
     */
    public ReturnType suspendActivityInst(String activityInstID) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        actInst.setState(ActivityInstStatus.suspended.getType());
        activityInstMgr.save(actInst);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 继续活动实例
     */
    public ReturnType resumeActivityInst(String activityInstID) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
        if (actInst == null) {
            throw new BPMException("The activity instance not found!");
        }
        actInst.setState(ActivityInstStatus.running.getType());
        activityInstMgr.save(actInst);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 流程实例挂起
     */
    public ReturnType suspendProcessInst(String processInstID) throws BPMException {
        return updateProcessState(processInstID, ProcessInstStatus.suspended.getType());
    }

    /**
     * 继续流程实例
     */
    public ReturnType resumeProcessInst(String processInstID) throws BPMException {
        return updateProcessState(processInstID, ProcessInstStatus.running.getType());
    }

    /**
     * 中止流程实例，将对应的活动实例存为历史，然后将其删除。
     *
     * @param processInstID
     * @return
     * @throws BPMException
     */
    public ReturnType abortProcessInst(String processInstID) throws BPMException {
        List activityInsts = activityInstMgr.loadByProcessInstId(processInstID);
        if (activityInsts == null) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
        for (Iterator iter = activityInsts.iterator(); iter.hasNext(); ) {
            EIActivityInst activityInst = (EIActivityInst) iter.next();
            String activityInstId = activityInst.getActivityInstId();
            saveActivityHistoryInst(activityInstId);
            deleteActivityInst(activityInstId);
        }

        return updateProcessState(processInstID, ProcessInstStatus.aborted.getType());
    }

    /**
     * 流程实例完成。将如果还存在对应的活动实例，则将其删除。
     *
     * @param processInstID 流程实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType completeProcessInst(String processInstID) throws BPMException {
        // List activityInsts = activityInstMgr.loadByProcessInstId(processInstID);
        // if (activityInsts != null) {
        // for (Iterator iter = activityInsts.iterator(); iter.hasNext();) {
        // EIActivityInst activityInst = (EIActivityInst) iter.next();
        // if (!activityInst.getState().equals(ActivityInst.STATUS_READ)){
        // String activityInstId = activityInst.getActivityInstId();
        // // saveActivityHistoryInst(activityInstId);
        // deleteActivityInst(activityInstId);
        // }
        //
        // }
        // }
        EIProcessInst procInst = processInstMgr.loadByKey(processInstID);
        if (procInst == null) {
            throw new BPMException("The process instance '" + processInstID + "' not found!");
        }
        procInst.setEndTime(new Date(System.currentTimeMillis()));
        return updateProcessState(processInstID, ProcessInstStatus.completed.getType());
    }

    /**
     * 删除流程实例。<br>
     * 删除的内容包括：<br>
     * 1. 流程实例信息；<br>
     * 2. 相应的活动实例信息；<br>
     * 3. 相应的活动实例历史的信息；<br>
     * 4. 相应的路由实例信息。
     *
     * @param processInstID 流程实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType deleteProcessInst(String processInstID) throws BPMException {
        String where = " where PROCESSINST_ID='" + processInstID + "'";
        activityInstMgr.deleteByWhere(where);
        activityInstHistoryMgr.deleteByWhere(where);
        routeInstMgr.deleteByWhere(where);

        processInstMgr.deleteByKey(processInstID);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * @param result
     * @param history
     */
    private void addActivityInstOutHistory(Map historyMap, EIActivityInstHistory history) throws BPMException {
        if (history != null) {
            historyMap.put(history.getActivityHistoryId(), history);
            List routes = routeInstMgr.loadByWhere("where FROMACTIVITY_ID='" + history.getActivityHistoryId() + "' and ROUTETYPE='HISTORY'");
            for (Iterator it = routes.iterator(); it.hasNext(); ) {
                EIRouteInst routeInst = (EIRouteInst) it.next();
                EIActivityInstHistory his = activityInstHistoryMgr.loadByKey(routeInst.getToActivityId());
                addActivityInstOutHistory(historyMap, his);
            }
        }
    }

    /**
     * 根据指定历史分裂节点获分裂出去的所有历史
     *
     * @param historyHisroryId
     * @param noSplit//是否包含分裂的节点
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException {
        List<EIRouteInst> outRouteInsts = this.routeInstMgr.loadByWhere("where FROMACTIVITY_ID='" + historyHisroryId + "' and ROUTETYPE='HISTORY'");
        Map historyMap = new HashMap();
        for (Iterator it = outRouteInsts.iterator(); it.hasNext(); ) {
            EIRouteInst routeInst = (EIRouteInst) it.next();
            String dealMethod = routeInst.getFromActivityHistory().getDealMethod();
            if (noSplit || !dealMethod.equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
                EIActivityInstHistory his = this.activityInstHistoryMgr.loadByKey(routeInst.getToActivityId());
                addActivityInstOutHistory(historyMap, his);
            }
        }
        List result = new ArrayList(historyMap.values());
        Collections.sort(result, new ActivityInstHistoryEndTimeComparator());
        return result;
    }

    /**
     * 根据指定活动获取所有曾经分裂过的历史节点
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getLastSplitActivityInstHistoryByActvityInst(String activityInstId) throws BPMException {
        List<EIActivityInstHistory> hisList = getLastActivityInstHistoryListByActvityInst(activityInstId, true);
        List<EIActivityInstHistory> splitList = new ArrayList<EIActivityInstHistory>();
        for (int i = 0; i < hisList.size(); i++) {
            EIActivityInstHistory his = hisList.get(i);
            if (his.getDealMethod().equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
                splitList.add(his);
            }
        }
        return splitList;
    }

    public List<EIActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String activityInstId) throws BPMException {
        return getLastActivityInstHistoryListByActvityInst(activityInstId, false);
    }

    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列 该方法会递归查找当前活动上所有步骤包括分列和未分裂的
     *
     * @param activityInstId 活动实例ID
     * @param noSplit        活动实例ID
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String activityInstId, boolean noSplit) throws BPMException {
        EIActivityInst actInst = activityInstMgr.loadByKey(activityInstId);
        if (actInst == null) {
            throw new BPMException("The activity instance '" + activityInstId + "' not found!");
        }

        List routeInsts = routeInstMgr.getRouteInsts(actInst);
        Map historyMap = new HashMap();
        for (Iterator it = routeInsts.iterator(); it.hasNext(); ) {
            EIRouteInst routeInst = (EIRouteInst) it.next();
            String dealMethod = routeInst.getFromActivityHistory().getDealMethod();
            if (noSplit || !dealMethod.equals(ActivityInstDealMethod.DEALMETHOD_SPLITED.getType())) {
                addActivityInstHistory(historyMap, routeInst.getFromActivityHistory());
            }
        }

        List result = new ArrayList(historyMap.values());
        Collections.sort(result, new ActivityInstHistoryEndTimeComparator());
        return result;

        // return activityInstHistoryMgr.loadByWhere(" where ACTIVITYINST_ID='"
        // + activityInstId + "' order by ENDTIME");
    }

    /**
     * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param activityInstId 活动实例ID
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryListByActvityInst(String activityInstId) throws BPMException {
        return activityInstHistoryMgr.loadByWhere(" where ACTIVITYINST_ID='" + activityInstId + "'  order by ENDTIME");
        // return activityInstHistoryMgr.loadByWhere(" where ACTIVITYINST_ID='"
        // + activityInstId + "' and DEALMETHOD!='"+ActivityInstDealMethod.DEALMETHOD_SPLITED+"' order by ENDTIME");
    }

    /**
     * @param result
     * @param history
     */
    private void addActivityInstHistory(Map historyMap, EIActivityInstHistory history) throws BPMException {
        if (history != null) {
            historyMap.put(history.getActivityHistoryId(), history);
            List routes = routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + history.getActivityHistoryId() + "'");
            for (Iterator it = routes.iterator(); it.hasNext(); ) {
                EIRouteInst routeInst = (EIRouteInst) it.next();
                addActivityInstHistory(historyMap, routeInst.getFromActivityHistory());
            }
        }
    }

    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @param processInstId 流程实例ID
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryListByProcessInst(String processInstId) throws BPMException {
        // EIProcessInst procInst = processInstMgr.loadByKey(processInstId);
        // if (procInst == null) {
        // throw new BPMException("The prcoess instance not found, ID is:" +
        // processInstId);
        // }
        // return activityInstHistoryMgr.loadByWhere(" where PROCESSINST_ID='"
        // + processInstId + "' order by ENDTIME desc");
        return activityInstHistoryMgr.loadByWhere(" where PROCESSINST_ID='" + processInstId + "'  order by ENDTIME desc");
    }

    /**
     * 取得该流程实例的所有路由实例
     *
     * @param processInstId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getRouteInsts(String processInstId) throws BPMException {
        return routeInstMgr.loadByWhere(" where PROCESSINST_ID='" + processInstId + "' order by ROUTETIME");
    }

    // /**
    // * 取得路由实例对应的路由定义
    // *
    // * @param routeInstId
    // * @return 路由定义
    // * @throws BPMException
    // */
    // public RouteDef getRouteDefByEIRouInst(String routeInstId) throws BPMException {
    //
    // EIRouteInst routeInst=routeInstMgr.loadByKey(routeInstId);
    //
    // List routes=this.routeDefMgr.loadByWhere(" where FROMACTIVITYDEF_ID='"+routeInst.getFromActivityId()+"' AND
    // TOACTIVITYDEF_ID='"+routeInst.getToActivityId()+"'" );
    // if (routes.isEmpty()){
    // return null;
    // }
    // EIRouteDef eiRouteDef =(EIRouteDef) routes.get(0);
    // // add by lxl 2004-01-16
    // if (eiRouteDef == null) {
    // return null;
    // }
    // return new RouteDefProxy(eiRouteDef);
    // }

    /**
     * 取得该实例的到达路由
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getActivityInstInRoute(String activityInstId) throws BPMException {
        return routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + activityInstId + "' order by ROUTETIME");
    }

    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId 活动实例历史ID
     * @return List<EIActivityInst>
     * @throws BPMException
     */
    public List<EIActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) throws BPMException {
        List routeList = getActivityInstHistoryOutRoute(activityInstHistoryId);
        List activityInstList = new ArrayList<EIActivityInst>();

        for (int k = 0; k < routeList.size(); k++) {
            EIRouteInst routeInst = (EIRouteInst) routeList.get(k);
            if (routeInst.getRouteType().equals(RouteInstType.HISTORY)) {
                EIActivityInstHistory history = activityInstHistoryMgr.loadByKey(routeInst.getToActivityId());
                List<EIActivityInst> instList = getActivityInstListByOutActvityInstHistory(history.getActivityHistoryId());
                activityInstList.addAll(instList);
                EIActivityInst inst = history.getActivityInst();
                if (inst != null && !activityInstList.contains(inst)) {
                    activityInstList.add(inst);
                }

            } else {
                EIActivityInst inst = activityInstMgr.loadByKey(routeInst.getToActivityId());
                if (inst != null) {
                    activityInstList.add(inst);
                }
            }
        }
        return activityInstList;
    }

    /**
     * 取得从该实例历史发出的实例
     *
     * @param activityInstHistoryId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getActivityInstHistoryOutRoute(String activityInstHistoryId) throws BPMException {
        return routeInstMgr.loadByWhere(" where FROMACTIVITY_ID='" + activityInstHistoryId + "' order by ROUTETIME");
    }

    /**
     * 删除活动实例。将同时删除路由实例。
     *
     * @param activityInstID
     * @return
     * @throws BPMException
     */
    public ReturnType deleteActivityInst(String activityInstID) throws BPMException {
        // 删除与该活动实例相应的路由实例
        String where = " where TOACTIVITY_ID='" + activityInstID + "' and ROUTETYPE='" + RouteInstType.ACTIVITY + "'";
        routeInstMgr.deleteByWhere(where);

        activityInstMgr.deleteByKey(activityInstID);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    private void deleteActivityInstAndResetRoute(String deleteActivityInstId, String actInstId) throws BPMException {
        String where = " where TOACTIVITY_ID='" + deleteActivityInstId + "' and ROUTETYPE='" + RouteInstType.ACTIVITY + "'";
        List routeInsts = routeInstMgr.loadByWhere(where);
        for (Iterator it = routeInsts.iterator(); it.hasNext(); ) {
            EIRouteInst routeInst = (EIRouteInst) it.next();
            routeInst.setToActivityId(actInstId);
            routeInstMgr.save(routeInst);
        }

        activityInstMgr.deleteByKey(deleteActivityInstId);
    }

    private class ActivityInstHistoryEndTimeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Date time1 = ((EIActivityInstHistory) o1).getEndTime();
            Date time2 = ((EIActivityInstHistory) o2).getEndTime();
            if (time1 == null || time2 == null) {
                return 0;
            }
            if (time1.getTime() == time2.getTime()) {
                return 0;
            }
            if (time1.getTime() > time2.getTime()) {
                return 1;
            }
            return -1;
        }
    }
}


