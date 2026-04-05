/**
 * $RCSfile: OAAdminService.java,v $
 * $Revision: 1.2 $
 * $Date: 2015/12/20 15:15:28 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import net.ooder.annotation.JoinOperator;
import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.event.BPMCoreEventControl;
import net.ooder.bpm.engine.event.EIActivityEvent;
import net.ooder.bpm.engine.event.EIProcessEvent;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.*;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.engine.query.FilterChain;
import net.ooder.bpm.engine.util.UtilTimer;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.activitydef.ActivityDefRightAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstSuSpend;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.event.ActivityEventEnums;
import net.ooder.bpm.enums.event.ProcessEventEnums;
import net.ooder.bpm.enums.process.ProcessDefVersionAtt;
import net.ooder.bpm.enums.process.ProcessInstStartType;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.Filter;
import net.ooder.annotation.Operator;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.AttributeName;
import net.ooder.org.Person;
import net.ooder.server.JDSServer;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: BPM工作流管理系统
 * </p>
 * <p>
 * Description: OAAdminService接口的实现。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public class IOTAdminService implements AdminService, Serializable {

	private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, IOTAdminService.class);

	private static final int TIMER_LEVEL = 5;

	private String systemCode;

	private ConnectInfo connInfo;

	private JDSSessionHandle sessionHandle;

	private BPMServer workflowServer;

	private WorkflowEngine workflowEngine;

	private RightEngine rightEngine;

	private WorkflowClientService client;
	
	private DataEngine dataEngine;

	public IOTAdminService() throws BPMException {
		// this.systemCode = SPConstants.APPLICATION_KEY;
	}

	// ---------------------------------------------事件相关方法，EventControl
	
	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param inst
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireProcessEvent(EIProcessInst inst, ProcessEventEnums eventID)
			throws BPMException, JDSException {
		fireProcessEvent(inst, eventID, null);
	}

	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param inst
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireProcessEvent(EIProcessInst inst, ProcessEventEnums eventID,
			Map<RightCtx,Object> eventContext) throws BPMException, JDSException {
		eventContext = fillInUserID(eventContext);
		EIProcessEvent event = new EIProcessEvent(inst, eventID);
		event.setClientService(client);
		event.setContextMap(eventContext);
		BPMCoreEventControl.dispatchProcessEvent(event,client.getSystemCode());
	}

	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param inst
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireActivityEvent(EIActivityInst inst, ActivityEventEnums eventID)
			throws BPMException, JDSException {
		fireActivityEvent(inst, eventID, null);
	}

	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param inst
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireActivityEvent(EIActivityInst inst, ActivityEventEnums eventID,
			Map<RightCtx,Object> eventContext) throws BPMException, JDSException {
		eventContext = fillInUserID(eventContext);
		EIActivityEvent event = new EIActivityEvent(inst, eventID);
		event.setClientService(client);
		event.setContextMap(eventContext);
		BPMCoreEventControl.dispatchActivityEvent(event,client.getSystemCode());
	}

	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param insts
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireActivityEvent(EIActivityInst[] insts, ActivityEventEnums eventID)
			throws BPMException, JDSException {
		fireActivityEvent(insts, eventID, null);
	}

	/**
	 * fire a process event and transfer this event to BPMCoreEventControl
	 * 
	 * @param insts
	 *            the process which fire thie event, the source of event
	 * @param eventID
	 *            the event type
	 * @throws JDSException 
	 */
	private void fireActivityEvent(EIActivityInst[] insts, ActivityEventEnums eventID,
			Map<RightCtx,Object> eventContext) throws BPMException, JDSException {
		eventContext = fillInUserID(eventContext);
		EIActivityEvent event = new EIActivityEvent(insts, eventID);
		event.setClientService(client);
		event.setContextMap(eventContext);
		 BPMCoreEventControl.dispatchActivityEvent(event,client.getSystemCode());
	}

	// --------------------------------------------- 登陆注销操作

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#connect(com.ds.bpm.engine.ConnectInfo)
	 */
	public void connect(ConnectInfo connInfo) throws JDSException {
		this.connInfo = connInfo;
		// workflowServer.connect(client);
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#disconnect()
	 */
	public ReturnType disconnect() {
		// workflowServer.disconnect(connInfo, sessionHandle);
		connInfo = null;
		sessionHandle = null;
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	public ConnectInfo getConnectInfo() {
		return connInfo;
	}

	// --------------------------------------------- 定义相关方法

	/**
	 * 取得可监控疏导的流程实例列表
	 */
	public List getSupervisableProcessInstList(BPMCondition condition,
			Filter filter, Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		BPMCondition resultCon = condition;
		Filter resultFilter = filter;

		// 加入系统判断条件
		String inSQL = "SELECT BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF, BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF.PROCESSDEF_ID=BPM_PROCESSDEF_VERSION.PROCESSDEF_ID AND BPM_PROCESSDEF.SYSTEMCODE='"
				+ systemCode + "'";
		BPMCondition sysCon = new BPMCondition(
				BPMConditionKey.PROCESSINST_PROCESSDEF_VERSION_ID, Operator.IN,
				inSQL);
		// 人员判断条件
		String sql = "SELECT RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_VERSION_ID FROM RT_PROCESSDEF_SUPERVISOR WHERE RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_ID='"
				+ connInfo.getUserID() + "'";
		BPMCondition userCon = new BPMCondition(
				BPMConditionKey.PROCESSINST_PROCESSDEF_VERSION_ID, Operator.IN,
				sql);
		sysCon.addCondition(userCon, JoinOperator.JOIN_AND);

		if (resultCon == null) {
			resultCon = sysCon;
		} else {
			resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
		}

		List processInstList = workflowEngine.getProcessInstList(resultCon,
				resultFilter);
		return new WorkflowListProxy(processInstList,client.getSystemCode());
	}

	public List getProcessDefVersionList(BPMCondition condition, Filter filter,
			Map<RightCtx,Object> ctx) throws BPMException {
		UtilTimer timer = new UtilTimer();
		try {
			checkLogined();
			BPMCondition resultCon = condition;
			Filter resultFilter = filter;

			// 加入系统判断条件
			String inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.SYSTEMCODE='"
					+ systemCode + "'";
			BPMCondition sysCon = new BPMCondition(
					BPMConditionKey.PROCESSDEF_VERSION_PROCESSDEF_ID,
					Operator.IN, inSQL);
			if (resultCon == null) {
				resultCon = sysCon;
			} else {
				resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
			}

			// 在权限上下文参数中加入当前登陆人员ID
			Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
			// 无需用权限过滤
			Filter rightFilter = null; // rightEngine.getProcessDefListFilter(rightCtx);
			if (rightFilter != null) {
				FilterChain filterChain = new FilterChain();
				filterChain.addFilter(resultFilter);
				filterChain.addFilter(rightFilter);
				resultFilter = filterChain;

				// 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
				if (rightFilter instanceof BPMCondition) {
					resultCon.addCondition((BPMCondition) rightFilter,
							JoinOperator.JOIN_AND);
				}
			}

			// 获取流程定义列表
			List processDefList = workflowEngine.getProcessDefVersionList(
					resultCon, resultFilter);
			logger.debug(timer.timerString(TIMER_LEVEL, "getProcessDefList"));
			return new WorkflowListProxy(processDefList,client.getSystemCode());
		} catch (Exception e) {
			throw new BPMException("getProcessDefList error.", e,
					BPMException.GETPROCESSDEFLISTERROR);
		}
	}

	public List getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException {
		UtilTimer timer = new UtilTimer();
		try {
			checkLogined();
			BPMCondition resultCon = condition;
			Filter resultFilter = filter;

			// 加入系统判断条件
			String inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.SYSTEMCODE='"
					+ systemCode + "'";
			BPMCondition sysCon = new BPMCondition(BPMConditionKey.PROCESSDEF_ID,
					Operator.IN, inSQL);
			if (resultCon == null) {
				resultCon = sysCon;
			} else {
				resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
			}

			// 在权限上下文参数中加入当前登陆人员ID
			Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
			// 无需用权限过滤
			Filter rightFilter = null; // rightEngine.getProcessDefListFilter(rightCtx);
			if (rightFilter != null) {
				FilterChain filterChain = new FilterChain();
				filterChain.addFilter(resultFilter);
				filterChain.addFilter(rightFilter);
				resultFilter = filterChain;

				// 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
				if (rightFilter instanceof BPMCondition) {
					resultCon.addCondition((BPMCondition) rightFilter,
							JoinOperator.JOIN_AND);
				}
			}

			// 获取流程定义列表
			List processDefList = workflowEngine.getProcessDefList(resultCon,
					resultFilter);
			logger.debug(timer.timerString(TIMER_LEVEL, "getProcessDefList"));
			return new WorkflowListProxy(processDefList,client.getSystemCode());
		} catch (Exception e) {
			throw new BPMException("getProcessDefList error.", e,
					BPMException.GETPROCESSDEFLISTERROR);
		}
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getProcessDef(java.lang.String)
	 */
	public ProcessDef getProcessDef(String processDefID) throws BPMException {
		checkLogined();
		EIProcessDef eiProcessDef = EIProcessDefManager.getInstance()
				.loadByKey(processDefID);
		// add by lxl 2004-01-16
		if (eiProcessDef == null) {
			return null;
		}
		return new ProcessDefProxy(eiProcessDef,client.getSystemCode());
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getActivityDef(java.lang.String)
	 */
	public ActivityDef getActivityDef(String activityDefID) throws BPMException {
		checkLogined();
		EIActivityDef eiActivityDef = EIActivityDefManager.getInstance()
				.loadByKey(activityDefID);
		// add by lxl 2004-01-16
		if (eiActivityDef == null) {
			return null;
		}
		return new ActivityDefProxy(eiActivityDef,client.getSystemCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#getRouteDef(java.lang.String)
	 */
	public RouteDef getRouteDef(String routeDefId) throws BPMException {
		checkLogined();
		EIRouteDef eiRouteDef = EIRouteDefManager.getInstance().loadByKey(
				routeDefId);
		// add by lxl 2004-01-16
		if (eiRouteDef == null) {
			return null;
		}
		return new RouteDefProxy(eiRouteDef,client.getSystemCode());
	}

	// --------------------------------------------- 实例相关方法

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getProcessInstList(com.ds.bpm.engine.query.Condition,
	 *      com.ds.bpm.engine.query.Filter, java.util.Map)
	 */
	public List getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnums,Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException {
		UtilTimer timer = new UtilTimer();
		try {
			checkLogined();
			BPMCondition resultCon = condition;
			Filter resultFilter = filter;
			String inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.SYSTEMCODE='"
				+ systemCode + "'";
			if (systemCode.equals("system")){
				inSQL = "SELECT BPM_PROCESSDEF.PROCESSDEF_ID FROM BPM_PROCESSDEF";
			}

			// 加入系统判断条件

			BPMCondition sysCon = new BPMCondition(
					BPMConditionKey.PROCESSINST_PROCESSDEF_ID, Operator.IN, inSQL);
			if (resultCon == null) {
				resultCon = sysCon;
			} else {
				resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
			}

			Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
			Filter rightFilter = rightEngine.getProcessInstListFilter(conditionEnums,rightCtx);
			if (rightFilter != null) {
				FilterChain filterChain = new FilterChain();
				filterChain.addFilter(resultFilter);
				filterChain.addFilter(rightFilter);
				resultFilter = filterChain;

				// 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
				if (rightFilter instanceof BPMCondition) {
					resultCon.addCondition((BPMCondition) rightFilter,
							JoinOperator.JOIN_AND);
				}
			}
			List processInstList = workflowEngine.getProcessInstList(resultCon,
					resultFilter);
			logger.debug(timer.timerString(TIMER_LEVEL, "getProcessInstList"));
			return new WorkflowListProxy(processInstList,client.getSystemCode());
		} catch (Exception e) {
			throw new BPMException("getProcessInstList error.", e,
					BPMException.GETPROCESSINSTLISTERROR);
		}
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getActivityInstListbyUserId(java.lang.String,
	 *      java.lang.String)
	 */
	public List getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnums,Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException {
		// modify by lxl 2004-01-15
		UtilTimer timer = new UtilTimer();
		try {
			checkLogined();
			BPMCondition resultCon = condition;
			Filter resultFilter = filter;

			// 加入系统判断条件
			String inSQL = "SELECT BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF, BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF.PROCESSDEF_ID=BPM_PROCESSDEF_VERSION.PROCESSDEF_ID AND BPM_PROCESSDEF.SYSTEMCODE='"
					+ systemCode + "'";
			BPMCondition sysCon = new BPMCondition(
					BPMConditionKey.ACTIVITYINST_PROCESSDEF_ID, Operator.IN,
					inSQL);
			if (resultCon == null) {
				resultCon = sysCon;
			} else {
				resultCon.addCondition(sysCon, JoinOperator.JOIN_AND);
			}

			Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
			Filter rightFilter = rightEngine
					.getActivityInstListFilter(conditionEnums,rightCtx);
			
			if (rightFilter != null) {
				FilterChain filterChain = new FilterChain();
				filterChain.addFilter(resultFilter);
				filterChain.addFilter(rightFilter);
				resultFilter = filterChain;

				// 如果该过滤器继承了Condition，则添加相应权限的SQL查询条件
				if (rightFilter instanceof BPMCondition) {
					resultCon.addCondition((BPMCondition) rightFilter,
							JoinOperator.JOIN_AND);
				}
			}
			List activityInstList = workflowEngine.getActivityInstList(
					resultCon, resultFilter);
			logger.debug(timer.timerString(TIMER_LEVEL, "getActivityInstList"));
			return new WorkflowListProxy(activityInstList,client.getSystemCode());
		} catch (Exception e) {
			throw new BPMException("getActivityInstList error.", e,
					BPMException.GETACTIVITYINSTLISTERROR);
		}

		// checkLogined();
		// // TODO 需要从权限引擎获得Condition和Filter
		//
		// List activityInstList = workflowEngine.getActivityInstList(condition,
		// filter);
		// return new WorkflowListProxy(activityInstList);
	}

	/**
	 * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
	 * 
	 * @param actvityInstId
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return
	 * @throws BPMException
	 */
	public List getActivityInstHistoryListByActvityInst(String actvityInstId,
			Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		List list = workflowEngine
				.getActivityInstHistoryListByActvityInst(actvityInstId);
		return new WorkflowListProxy(list,client.getSystemCode());
	}

	/**
	 * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
	 * 
	 * @param processInstId
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return
	 * @throws BPMException
	 */
	public List getActivityInstHistoryListByProcessInst(String processInstId,
			Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		List list = workflowEngine
				.getActivityInstHistoryListByProcessInst(processInstId);
		return new WorkflowListProxy(list,client.getSystemCode());
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getProcessInstbyId(java.lang.String)
	 */
	public ProcessInst getProcessInst(String processInstID) throws BPMException {
		checkLogined();
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		return new ProcessInstProxy(eiProcessInst,client.getSystemCode());
	}

	/**
	 * 更新流程实例名称（公文标题）
	 * 
	 * @param processInstId
	 * @param name
	 *            新名称，长度在100字节以内
	 * @return
	 * @throws BPMException
	 */
	public ReturnType updateProcessInstName(String processInstId, String name)
			throws BPMException {
		// 判断名称长度
		int length = name.getBytes().length;
		if (length >= 100) {
			return new ReturnType(ReturnType.MAINCODE_FAIL, "名称长度不能超过100个字节！");
		}
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstId);
		eiProcessInst.setName(name);
		EIProcessInstManager.getInstance().save(eiProcessInst);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	/**
	 * 更新流程实例紧急程度
	 * @param processInstId
	 * @param urgency
	 *            新的紧急程度
	 * @return
	 * @throws BPMException
	 */
	public ReturnType updateProcessInstUrgency(String processInstId,
			String urgency) throws BPMException {
		// 判断名称长度
		int length = urgency.getBytes().length;
		if (length >= 20) {
			return new ReturnType(ReturnType.MAINCODE_FAIL, "紧急程度长度不能超过20个字节！");
		}
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstId);
		eiProcessInst.setUrgency(urgency);
		EIProcessInstManager.getInstance().save(eiProcessInst);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#getActivityInstbyId(java.lang.String)
	 */
	public ActivityInst getActivityInst(String activityInstID)
			throws BPMException {
		checkLogined();
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstID);
		return new ActivityInstProxy(eiActivityInst,client.getSystemCode());
	}

	/**
	 * 按照活动实例历史的ID取得活动实例历史对象
	 * 
	 * @param activityInstHistoryID
	 *            活动实例历史ID
	 * @return 指定活动实例历史ID的ActivityInst对象
	 * @throws BPMException
	 */
	public ActivityInstHistory getActivityInstHistory(
			String activityInstHistoryID) throws BPMException {
		checkLogined();
		EIActivityInstHistory eiActivityInstHistory = EIActivityInstHistoryManager
				.getInstance().loadByKey(activityInstHistoryID);
		return new ActivityInstHistoryProxy(eiActivityInstHistory,client.getSystemCode());

	}

	// --------------------------------------------- 流程启动相关方法

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#startProcess(java.lang.String,
	 *      java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInst newProcess(String processDefId, String processInstName,
			String processUrgency, Map<RightCtx,Object> ctx) throws BPMException {
		UtilTimer timer = new UtilTimer();
		checkLogined();
		beginTransaction();
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		ProcessInst processInst;
		ActivityInst activityInst;
		ReturnType rt;
		try {
			// 创建流程实例
			EIProcessInst eiProcessInst = workflowEngine.createProcessInst(
					processDefId, processInstName, processUrgency);
			rt = rightEngine.createProcessInst(
					eiProcessInst.getProcessInstId(), rightCtx);
			if (rt.mainCode() == ReturnType.MAINCODE_FAIL) {
				rollbackTransaction();
				if (rt.toString() == null) {
					throw new BPMException("Create process instance error.",
							BPMException.CREATEPROCESSINSTANCEERROR);
				} else {
					throw new BPMException(rt.toString(),
							BPMException.CREATEPROCESSINSTANCEERROR);
				}
			}
			// 启动流程实例
			fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTING); // 触发流程启动事件
			EIActivityInst eiActivityInst = workflowEngine
					.startProcessInst(eiProcessInst.getProcessInstId());
			rt = rightEngine.startProcessInst(eiProcessInst.getProcessInstId(),
					eiActivityInst.getActivityInstId(), rightCtx);
			if (rt.mainCode() == ReturnType.MAINCODE_FAIL) {
				rollbackTransaction();
				if (rt.toString() == null) {
					throw new BPMException("Start process instance error.",
							BPMException.STARTPROCESSINSTANCEERROR);
				} else {
					throw new BPMException(rt.toString(),
							BPMException.STARTPROCESSINSTANCEERROR);
				}
			}
			fireActivityEvent(eiActivityInst, ActivityEventEnums.INITED); // 触发活动初始化事件
			fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED); // 触发流程启动事件

			commitTransaction();
			processInst = new ProcessInstProxy(eiProcessInst,client.getSystemCode());
			logger.debug(timer.timerString(TIMER_LEVEL, "newProcess"));
		} catch (BPMException bpme) {
			rollbackTransaction();
			throw bpme;
		} catch (Exception e) {
			rollbackTransaction();
			throw new BPMException("newProcess error.", e,
					BPMException.NEWPROCESSINSTANCEERROR);
		}

		return processInst;
	}

	// --------------------------------------------- 路由相关方法

	/**
	 * @see com.ds.bpm.engine.WorkflowClientService#getNextRoutes(java.lang.String,
	 *      java.lang.String, com.ds.bpm.client.filter.RouteFilter,
	 *      java.lang.String)
	 */
	public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition,
										Filter routeFilter, Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		// TODO 从权限引擎获取Condition和Filter

		List<EIRouteDef> routeList = workflowEngine.getNextRoutes(startActivityInstID,
				condition, routeFilter);
		return new WorkflowListProxy<RouteDef>(routeList,client.getSystemCode());
	}

	/**
	 * 取得该活动所对应的流程版本上的所有活动定义。
	 * 
	 * @param activityInstId
	 * @return
	 * @throws BPMException
	 */
	public List getAllActivityDefs(String activityInstId) throws BPMException {
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstId);
		if (eiActivityInst == null) {
			throw new BPMException("The activity instance '" + activityInstId
					+ "' not found!");
		}
		EIProcessDefVersion eiProcessDefVersion = eiActivityInst
				.getProcessDefVersion();
		if (eiProcessDefVersion == null) {
			throw new BPMException(
					"The process definition version for activity instance '"
							+ activityInstId + "' not found!");
		}

		return new WorkflowListProxy(eiProcessDefVersion.getAllActivityDefs(),client.getSystemCode());
	}

	/**
	 * 更换某个活动实例的当前办理人。
	 * 
	 * @param activityInstId
	 *            活动实例的标识
	 * @param ctx
	 *            上下文环境，包含办理人的信息。
	 * @return
	 * @throws BPMException
	 */
	public ReturnType changePerformer(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		ctx = fillInUserID(ctx);

		return rightEngine.changePerformer(activityInstId, ctx);
	}

	/**
	 * @throws JDSException 
	 * @see com.ds.bpm.engine.WorkflowClientService#routeTo(String
	 *      startActivityInstID, List nextActivityDefIDs, Map<RightCtx,Object> ctx)
	 */
	public ReturnType routeTo(String startActivityInstID,
			String[] nextActivityDefIDs, Map[] ctx) throws BPMException, JDSException {
		checkLogined();

		for (int i = 0; i < ctx.length; i++) {
			ctx[i] = fillInUserID(ctx[i]);
		}

		// ------------执行路由---------add by lxl 2004.01.17
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(startActivityInstID);
		ReturnType ret = null;
		// 1.保存历史
		// 触发活动结束事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETING);
		// 1.1.调用引擎中的保存历史方法
		EIActivityInstHistory newHistory = workflowEngine
				.saveActivityHistoryInst(startActivityInstID);
		// 1.2.调用权限引擎中的保存历史方法
		ret = rightEngine.saveActivityHistoryInst(startActivityInstID,
				newHistory.getActivityHistoryId(), ctx[0]);
		if (!ret.isSucess()) {
			return ret;
		}
		// 触发活动结束事件
		Map<RightCtx,Object> eventContext = new HashMap();
		eventContext.put(RightCtx.CONTEXT_ACTIVITYINSTHISTORY, newHistory
				.getActivityHistoryId());
		fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETED, eventContext);

		// **如果到达的活动节点是一个虚拟结束节点，则结束流程！**t
		if (nextActivityDefIDs.length == 1
				&& ActivityDefPosition.VIRTUAL_LAST_DEF.getType()
						.equalsIgnoreCase((String) nextActivityDefIDs[0])) {
			return completeProcessInst(eiActivityInst.getProcessInstId(),
					ctx[0]);
		}

		// 2.如果需要的化，将活动实例复制成多份
		List copyActivityInsts = new ArrayList();
		if (nextActivityDefIDs.length > 1) {
			// 触发活动开始分裂事件
			fireActivityEvent(eiActivityInst, ActivityEventEnums.SPLITING);
			// 2.1.调用workflow引擎复制活动实例
			copyActivityInsts = workflowEngine.splitActivityInst(
					startActivityInstID, nextActivityDefIDs.length, newHistory
							.getActivityHistoryId());
			String[] ids = new String[nextActivityDefIDs.length];
			for (int i = 0; i < copyActivityInsts.size(); i++) {
				EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(i);
				ids[i] = inst.getActivityInstId();
			}
			// 2.2.调用权限引擎中的复制活动实例
			ret = rightEngine.splitActivityInst(startActivityInstID, ids,
					ctx[0]);
			if (!ret.isSucess()) {
				return ret;
			}

			// 触发活动分裂完成事件
			fireActivityEvent((EIActivityInst[]) copyActivityInsts
					.toArray(new EIActivityInst[0]), ActivityEventEnums.SPLITED);
		}

		// 3.分别路由所有的活动实例和复制的活动实例
		// 3.1 判断是否需要合并，如果需要则调用相关方法
		List activityInsts = workflowEngine
				.combinableActivityInsts(eiActivityInst.getActivityInstId());
		boolean bCombine = false;
		if (activityInsts.size() != 0) {
			String suspendOrCombine = workflowEngine
					.suspendOrCombine(eiActivityInst.getActivityInstId());
			if (suspendOrCombine.equals(ActivityInstSuSpend.SUSPEND.getType())) {
				// 挂起，不需要处理
			} else if (suspendOrCombine
					.equals(ActivityInstSuSpend.COMBINE.getType())) {
				// 需要合并
				bCombine = true;
				// 触发合并事件
				fireActivityEvent((EIActivityInst[]) activityInsts
						.toArray(new EIActivityInst[0]), ActivityEventEnums.JOINING);
			}
		}

		// 3.2.路由原来的活动实例
		// 触发活动路由事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.ROUTING);
		// 3.2.1.调用权限引擎路由
		ret = rightEngine.routeTo(startActivityInstID,
				(String) nextActivityDefIDs[0], ctx[0]);
		if (!ret.isSucess()) {
			return ret;
		}
		// 3.2.2.调用workflow引擎路由
		EIActivityInst actInst = workflowEngine.routeTo(startActivityInstID,
				(String) nextActivityDefIDs[0]);
		// 触发活动路由完成事件
		fireActivityEvent(actInst, ActivityEventEnums.ROUTED);
		// 触发合并完成事件
		fireActivityEvent(actInst, ActivityEventEnums.JOINED);

		// 3.2路由复制的活动实例（如果需要的话）
		if (copyActivityInsts.size() > 0) {
			for (int i = 1; i < copyActivityInsts.size(); i++) {
				EIActivityInst inst = (EIActivityInst) copyActivityInsts.get(i);
				fireActivityEvent(inst, ActivityEventEnums.ROUTING); // 触发活动路由事件
				// 3.2.1.调用workflow引擎路由
				workflowEngine.routeTo(inst.getActivityInstId(),
						(String) nextActivityDefIDs[i]);
				// 3.1.2.调用权限引擎路由
				ret = rightEngine.routeTo(inst.getActivityInstId(),
						(String) nextActivityDefIDs[i], ctx[i]);
				if (!ret.isSucess()) {
					return ret;
				}
				// 触发活动路由完成事件
				fireActivityEvent(inst, ActivityEventEnums.ROUTED);
			}
		}

		// 修改流程实例的草稿状态（如果是第一次提交，则将状态改为running）
		EIProcessInst processInst = eiActivityInst.getProcessInst();
		if (processInst.getState().equalsIgnoreCase(
				ProcessInstStatus.notStarted.getType())) {
			workflowEngine.updateProcessState(
					eiActivityInst.getProcessInstId(),
					ProcessInstStatus.running.getType());
		}

		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	// --------------------------------------------- 收回相关方法

	/**
	 * 判断是否可以收回 1。调用引擎 2。调用权限
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#canTakeBack(java.lang.String,
	 *      java.util.Map)
	 */
	public boolean canTakeBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		// add by lxl 2004-02-08
		if (workflowEngine.canTakeBack(activityInstID) == false) {
			return false;
		}
		if (rightEngine.canTakeBack(activityInstID, rightCtx) == false) {
			return false;
		}

		return true;
	}

	/**
	 * 收回活动实例 1。调用权限部分，恢复权限部分数据 2。调用引擎部分，恢复引擎部分数据
	 * @throws JDSException 
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#takeBack(java.lang.String, java.util.Map)
	 */
	public ReturnType takeBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);

		// add by lxl 2004-02-08
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstID);

		// 触发活动收回事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.TAKEBACKING);
		ReturnType rt;
		// 1.首先调用权限
		rt = rightEngine.tackBack(activityInstID, rightCtx);
		if (!rt.isSucess())
			return rt;
		// 2.调用引擎
		rt = workflowEngine.tackBack(activityInstID,rightCtx);
		if (!rt.isSucess())
			return rt;
		// 触发活动收回事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.TAKEBACKED);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	// --------------------------------------------- 退回相关方法

	/**
	 * 判断是否可以退回 1。调用引擎 2。调用权限
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#canRouteBack(java.lang.String,
	 *      java.util.Map)
	 */
	public boolean canRouteBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		// add by lxl 2004-02-08
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		if (workflowEngine.canRouteBack(activityInstID) == false) {
			return false;
		}
		if (rightEngine.canRouteBack(activityInstID, rightCtx) == false) {
			return false;
		}

		return true;
	}

	/**
	 * @see com.ds.bpm.engine.WorkflowClientService#getRouteBackActivityHistoryInstList(java.lang.String,
	 *      com.ds.bpm.query.Filter, java.util.Map)
	 */
	public List getRouteBackActivityHistoryInstList(String activityInstID,
			Filter routeFilter, Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		// add by lxl 2004-02-08
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		// TODO 从权限引擎获取Condition和Filter

		List routeList = workflowEngine.getRouteBacks(activityInstID,
				routeFilter);
		return new WorkflowListProxy(routeList,client.getSystemCode());

	}

	/**
	 * @throws JDSException 
	 * @see com.ds.bpm.engine.WorkflowClientService#routeBack(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	public ReturnType routeBack(String fromActivityInstID,
			String toActivityInstHistoryID, Map<RightCtx,Object> ctx) throws BPMException, JDSException {
		checkLogined();
		// add by lxl 2004-02-08
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(fromActivityInstID);
		ReturnType ret = null;
		// 1.保存历史
		// 触发活动结束事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETING);
		// 1.1.调用引擎中的保存历史方法
		EIActivityInstHistory newHistory = workflowEngine
				.saveActivityHistoryInst(fromActivityInstID);
		// 1.2.调用权限引擎中的保存历史方法
		ret = rightEngine.saveActivityHistoryInst(fromActivityInstID,
				newHistory.getActivityHistoryId(), ctx);
		if (!ret.isSucess()) {
			return ret;
		}
		// 触发活动结束事件
		Map<RightCtx,Object> eventContext = new HashMap<RightCtx,Object>();
		eventContext.put(RightCtx.CONTEXT_ACTIVITYINSTHISTORY, newHistory
				.getActivityHistoryId());
		fireActivityEvent(eiActivityInst, ActivityEventEnums.COMPLETED, eventContext);

		// 触发活动收回事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.ROUTING);
		ReturnType rt;
		// 1.首先调用权限
		rt = rightEngine.routeBack(fromActivityInstID, toActivityInstHistoryID,
				rightCtx);
		if (!rt.isSucess())
			return rt;
		// 2.调用引擎
		EIActivityInst actInst = workflowEngine.routeBack(fromActivityInstID,
				toActivityInstHistoryID);
		// 触发活动收回事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.ROUTED);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	// --------------------------------------------- 签收相关方法

	/**
	 * 判断是否可以签收 1。调用引擎 2。调用权限
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#canSignReceive(java.lang.String,
	 *      java.util.Map)
	 */
	public boolean canSignReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		// add by lxl 2004-02-08
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		if (workflowEngine.canSignReceive(activityInstID) == false) {
			return false;
		}
		if (rightEngine.canSignReceive(activityInstID, rightCtx) == false) {
			return false;
		}

		return true;
	}

	/**
	 * @throws JDSException 
	 * @see com.ds.bpm.engine.WorkflowClientService#signReceive(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType signReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		// add by lxl 2004-02-08
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstID);
		// 触发活动激活事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.ACTIVING);
		ReturnType rt;
		// 1.首先调用权限
		rt = rightEngine.signReceive(activityInstID, rightCtx);
		if (!rt.isSucess())
			return rt;
		// 2.调用引擎
		rt = workflowEngine.signReceive(activityInstID);
		if (!rt.isSucess())
			return rt;
		// 触发活动激活事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.ACTIVED);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	// --------------------------------------------- 活动状态转换方法

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#suspendActivityInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstID);
		// 触发活动恢复事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.SUSPENDING);
		ReturnType rt;
		rt = rightEngine.suspendActivityInst(activityInstID, ctx);
		if (!rt.isSucess())
			return rt;
		rt = workflowEngine.suspendActivityInst(activityInstID);
		if (!rt.isSucess()) {
			return rt;
		}
		// 触发活动恢复事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.SUSPENDED);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#resumeActivityInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIActivityInst eiActivityInst = EIActivityInstManager.getInstance()
				.loadByKey(activityInstID);
		// 触发活动挂起事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.RESUMING);
		ReturnType rt;
		rt = rightEngine.resumeActivityInst(activityInstID, ctx);
		if (!rt.isSucess())
			return rt;
		rt = workflowEngine.resumeActivityInst(activityInstID);
		if (!rt.isSucess())
			return rt;
		// 触发活动挂起事件
		fireActivityEvent(eiActivityInst, ActivityEventEnums.RESUMED);
		return new ReturnType(ReturnType.MAINCODE_SUCCESS);
	}

	// --------------------------------------------- 流程状态转换方法

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#suspendProcessInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType suspendProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		// 触发流程实例挂起事件
		fireProcessEvent(eiProcessInst, ProcessEventEnums.SUSPENDING);
		ReturnType rt;
		rt = rightEngine.suspendProcessInst(processInstID, ctx);
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
	 * @see com.ds.bpm.engine.WorkflowClientService#resumeProcessInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType resumeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		// 触发流程实例恢复事件
		fireProcessEvent(eiProcessInst, ProcessEventEnums.RESUMING);
		ReturnType rt;
		rt = rightEngine.resumeProcessInst(processInstID, ctx);
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
	 * @see com.ds.bpm.engine.WorkflowClientService#abortProcessInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType abortProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		// 触发流程实例中止事件
		fireProcessEvent(eiProcessInst, ProcessEventEnums.ABORTING);
		ReturnType rt;
		rt = rightEngine.abortProcessInst(processInstID, ctx);
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
	 * @see com.ds.bpm.engine.WorkflowClientService#completeProcessInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType completeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		// 触发流程实例结束事件
		fireProcessEvent(eiProcessInst, ProcessEventEnums.COMPLETING);
		ReturnType rt;
		rt = rightEngine.completeProcessInst(processInstID, ctx);
		if (!rt.isSucess()) {
			return rt;
		}
		rt = dataEngine.completeProcessInst(processInstID, ctx);
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
	 * @see com.ds.bpm.engine.WorkflowClientService#deleteProcessInst(java.lang.String,
	 *      java.util.Map)
	 */
	public ReturnType deleteProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException, JDSException {
		checkLogined();
		ctx = fillInUserID(ctx);
		EIProcessInst eiProcessInst = EIProcessInstManager.getInstance()
				.loadByKey(processInstID);
		// 触发流程实例删除事件
		fireProcessEvent(eiProcessInst, ProcessEventEnums.DELETING);
		ReturnType rt;
		rt = rightEngine.deleteProcessInst(processInstID, ctx);
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
	 * @see com.ds.bpm.engine.WorkflowClientService#beginTransaction(java.lang.String,
	 *      java.util.Map)
	 */
	public void beginTransaction() throws BPMException {
		checkLogined();
		try {
			DbManager.getInstance().beginTransaction();
		} catch (SQLException sqle) {
			throw new BPMException(
					"Failed to beging transaction of client service.", sqle,
					BPMException.TRANSACTIONBEGINERROR);
		}
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#commitTransaction(java.lang.String,
	 *      java.util.Map)
	 */
	public void commitTransaction() throws BPMException {
		checkLogined();
		try {
			DbManager.getInstance().endTransaction(true);
		} catch (SQLException sqle) {
			throw new BPMException(
					"Failed to commit transaction of client service.", sqle,
					BPMException.TRANSACTIONCOMMITERROR);
		}
	}

	/*
	 * @see com.ds.bpm.engine.WorkflowClientService#rollbackTransaction(java.lang.String,
	 *      java.util.Map)
	 */
	public void rollbackTransaction() throws BPMException {
		checkLogined();
		try {
			DbManager.getInstance().endTransaction(false);
		} catch (SQLException sqle) {
			throw new BPMException(
					"Failed to rollback transaction of client service", sqle,
					BPMException.TRANSACTIONROLLBACKERROR);
		}
	}

	public Object getProcessDefVersionRightAttribute(String processDefVersionId, ProcessDefVersionAtt attName, Map<RightCtx, Object> ctx) throws JDSException, BPMException {
		return null;
	}

	public Object getActivityDefRightAttribute(String activityDefId, ActivityDefRightAtt attName, Map<RightCtx, Object> ctx) throws JDSException, BPMException {
		return null;
	}

	public Object getRouteDefRightAttribute(String routeDefId, AttributeName attName, Map<RightCtx, Object> ctx) throws JDSException, BPMException {
		return null;
	}


	private void checkLogined() throws BPMException {
		if (sessionHandle == null) {
			throw new BPMException("Not logined error!",
					BPMException.NOTLOGINEDERROR);
		} else {
			JDSServer.activeSession(sessionHandle);
		}
	}

	private Map<RightCtx,Object> fillInUserID(Map<RightCtx,Object> ctx) {
		Map<RightCtx,Object>  result = ctx;
		if (result == null) {
			result = new HashMap<RightCtx,Object>();
		}
		
		String currPersonId=(String) result.get(RightCtx.USERID);
		if (currPersonId==null){
			result.put(RightCtx.USERID, connInfo.getUserID());
		}

		
		return result;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ds.bpm.engine.WorkflowClientService#getActivityDefRightAttribute(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException {

		return rightEngine.getActivityDefRightAttribute(activityDefId);
	}



	/**
	 * 活动实例的权限属性
	 * 
	 * @param activityInstId
	 *            活动实例的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public List<Person> getActivityInstRightAttribute(String activityInstId,
													  ActivityInstRightAtt attName, Map<RightCtx,Object> ctx) throws BPMException {
		checkLogined();
		ctx = fillInUserID(ctx);
		return rightEngine.getActivityInstRightAttribute(activityInstId,
				attName, ctx);
	}

	/**
	 * 活动实例历史的权限属性
	 * 
	 * @param activityInstHistoryId
	 *            活动实例历史的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public List<Person> getActivityInstHistoryRightAttribute(
			String activityInstHistoryId, ActivityInstHistoryAtt attName, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		ctx = fillInUserID(ctx);
		return rightEngine.getActivityInstHistoryRightAttribute(
				activityInstHistoryId, attName, ctx);
	}

	/**
	 * 判断当前人对活动实例的权限
	 * 
	 * @param activityInstId
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		Map<RightCtx,Object> ctxRight = fillInUserID(ctx);
		return rightEngine.queryPermissionToActivityInst(activityInstId,
				ctxRight);
	}

	/**
	 * 得到当前人对活动实例的所有权限列表
	 * 
	 * @param activityInstId
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public  List<RightGroupEnums> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException {
		checkLogined();
		ctx = fillInUserID(ctx);
		List list = rightEngine.queryAllPermissionToActivityInst(
				activityInstId, ctx);
		return list;
	}

	/**
	 * @param client
	 *            The client to set.
	 */
	public void setClientService(WorkflowClientService client) {
		this.client = client;
	}

	/**
	 * @param rightEngine
	 *            The rightEngine to set.
	 */
	public void setRightEngine(RightEngine rightEngine) {
		this.rightEngine = rightEngine;
	}

	/**
	 * @param workflowEngine
	 *            The workflowEngine to set.
	 */
	public void setWorkflowEngine(WorkflowEngine workflowEngine) {
		this.workflowEngine = workflowEngine;
	}

	/**
	 * @param workflowServer
	 *            The workflowServer to set.
	 */
	public void setBPMServer(BPMServer workflowServer) {
		this.workflowServer = workflowServer;
	}

	/**
	 * @param sessionHandle
	 *            The sessionHandle to set.
	 */
	public void setSessionHandle(JDSSessionHandle sessionHandle) {
		this.sessionHandle = sessionHandle;
	}

	/**
	 * @param connInfo
	 *            The connInfo to set.
	 */
	public void setConnInfo(ConnectInfo connInfo) {
		this.connInfo = connInfo;
	}

	/**
	 * @param systemCode
	 *            The systemCode to set.
	 */
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ds.bpm.engine.AdminService#newProcess(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInst newProcess(String processDefId, String processInstName,
			String processUrgency, String initType, Map<RightCtx,Object> ctx)
			throws BPMException {
		UtilTimer timer = new UtilTimer();
		checkLogined();
		Map<RightCtx,Object> rightCtx = fillInUserID(ctx);
		ProcessInst processInst;
		ActivityInst activityInst;
		ReturnType rt;
		try {
			// 创建流程实例
			EIProcessInst eiProcessInst = workflowEngine.createProcessInst(
					processDefId, processInstName, processUrgency, initType);
			// 如果不是自动启动，需要判断启动权限
			if (!ProcessInstStartType.AUTO.equals(initType)) {
				// 1.判断是否有权启动
				ReturnType returnType = rightEngine.hasRightToStartProcess(
						eiProcessInst.getProcessInstId(), rightCtx);
				if (returnType.isSucess() == false) {
					throw new BPMException("Create process instance error.",
							BPMException.CREATEPROCESSINSTANCEERROR);
				}
			}

			rt = rightEngine.createProcessInst(
					eiProcessInst.getProcessInstId(), initType, rightCtx);
			if (rt.mainCode() == ReturnType.MAINCODE_FAIL) {
				rollbackTransaction();
				if (rt.toString() == null) {
					throw new BPMException("Create process instance error.",
							BPMException.CREATEPROCESSINSTANCEERROR);
				} else {
					throw new BPMException(rt.toString(),
							BPMException.CREATEPROCESSINSTANCEERROR);
				}
			}

			// 启动流程实例
			fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTING); // 触发流程启动事件
			EIActivityInst eiActivityInst = workflowEngine
					.startProcessInst(eiProcessInst.getProcessInstId());
			// 如果是自动启动，需要实例化办理人（启动人）
			if (ProcessInstStartType.AUTO.equals(initType)) {
				// 传入当前的活动实例id与流程实例id
				Map tempCtx = new HashMap();
				tempCtx.put(RightCtx.ACTIVITYINST_ID,
						eiActivityInst.getActivityInstId());
				tempCtx.put(RightCtx.PROCESSINST_ID, eiActivityInst
						.getProcessInstId());
				List users = rightEngine.getPerformerCandidate(eiActivityInst
						.getActivityDefId(), tempCtx);
				if (users.isEmpty()) {
					throw new BPMException(
							"Start process instance error, Can't find process starter!");
				}
				rightCtx.put(RightCtx.USERS, users);
			}
			rt = rightEngine.startProcessInst(eiProcessInst.getProcessInstId(),
					eiActivityInst.getActivityInstId(), rightCtx);
			if (rt.mainCode() == ReturnType.MAINCODE_FAIL) {
				rollbackTransaction();
				if (rt.toString() == null) {
					throw new BPMException("Start process instance error.",
							BPMException.STARTPROCESSINSTANCEERROR);
				} else {
					throw new BPMException(rt.toString(),
							BPMException.STARTPROCESSINSTANCEERROR);
				}
			}
			// 触发活动初始化事件
			fireActivityEvent(eiActivityInst, ActivityEventEnums.INITED);
			if (ProcessInstStartType.AUTO.equals(initType)) {
				// 触发流程启动事件，将上下文环境传给监听器。
				fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED, ctx);
			} else {
				fireProcessEvent(eiProcessInst, ProcessEventEnums.STARTED); // 触发流程启动事件
			}

			commitTransaction();
			processInst = new ProcessInstProxy(eiProcessInst,client.getSystemCode());
			logger.debug(timer.timerString(TIMER_LEVEL, "newProcess"));
		} catch (BPMException bpme) {
			rollbackTransaction();
			throw bpme;
		} catch (Exception e) {
			rollbackTransaction();
			throw new BPMException("newProcess error.", e,
					BPMException.NEWPROCESSINSTANCEERROR);
		}

		return processInst;
	}

	public DataEngine getDataEngine() {
		return dataEngine;
	}

	public void setDataEngine(DataEngine dataEngine) {
		this.dataEngine = dataEngine;
	}

	

	public void setWorkflowServer(BPMServer workflowServer) {
	this.workflowServer=workflowServer;
		
	}
}
