/**
 * $RCSfile: AdminService.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:54 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */

package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.inter.EIActivityInstHistory;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.activitydef.ActivityDefRightAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.process.ProcessDefVersionAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.Condition;
import net.ooder.common.Filter;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.AttributeName;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: BPM管理监控接口。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang
 * @version 2.0
 */
public interface AdminService {

	// --------------------------------------------- 登陆注销操作

	/**
	 * @param client
	 *            The client to set.
	 */
	public void setClientService(WorkflowClientService client);

	/**
	 * @param rightEngine
	 *            The rightEngine to set.
	 */
	public void setRightEngine(RightEngine rightEngine);
	

	/**
	 * @param dataEngine
	 *            The dataEngine to set.
	 */
	public void setDataEngine(DataEngine dataEngine);

	/**
	 * @param workflowEngine
	 *            The workflowEngine to set.
	 */
	public void setWorkflowEngine(WorkflowEngine workflowEngine);

	/**
	 * @param workflowServer
	 *            The workflowServer to set.
	 */
	public void setWorkflowServer(BPMServer workflowServer);

	/**
	 * @param sessionHandle
	 *            The sessionHandle to set.
	 */
	public void setSessionHandle(JDSSessionHandle sessionHandle);

	/**
	 * @param connInfo
	 *            The connInfo to set.
	 */
	public void setConnInfo(ConnectInfo connInfo);

	/**
	 * @param systemCode
	 *            The systemCode to set.
	 */
	public void setSystemCode(String systemCode);

	/**
	 * 登陆
	 * 
	 * @param connInfo
	 *            登陆连接信息
	 * @return 结果标识
	 * @throws BPMException
	 */
	public void connect(ConnectInfo connInfo) throws JDSException;

	/**
	 * 注销
	 * 
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType disconnect() throws JDSException;

	/**
	 * 取得登录人信息
	 * 
	 * @return
	 */
	public ConnectInfo getConnectInfo();

	// --------------------------------------------- 定义相关方法
	/**
	 * 取得可监控疏导的流程实例列表
	 * @throws BPMException 
	 */
	public List<ProcessDefVersion> getSupervisableProcessInstList(BPMCondition condition,
																  Filter filter, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 取得符合条件的流程定义列表。
	 * 
	 * @param condition
	 *            查询条件，例如根据流程定义的名称进行查询。
	 * @param filter
	 *            扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
	 * @return 所有符合条件的ProcessDef列表
	 * @throws BPMException
	 */
	public List<ProcessDef> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException;

	public List getProcessDefVersionList(BPMCondition condition, Filter filter,
			Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 获得指定流程定义ID的流程定义
	 * 
	 * @param processDefID
	 *            流程定义ID
	 * @return 流程定义
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ProcessDef getProcessDef(String processDefID) throws JDSException, BPMException;

	/**
	 * 获得指定活动定义ID的活动定义
	 * 
	 * @param activityDefID
	 *            活动定义ID
	 * @return 活动定义
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ActivityDef getActivityDef(String activityDefID) throws JDSException, BPMException;

	/**
	 * 获得指定路由定义ID的路由定义
	 * 
	 * @param RouteDef
	 *            路由定义ID
	 * @return 路由定义
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public RouteDef getRouteDef(String routeDefId) throws JDSException, BPMException;

	// --------------------------------------------- 实例相关方法

	/**
	 * 取得符合条件的流程实例列表
	 * 
	 * @param condition
	 *            查询条件，例如根据流程状态，流程类型，流程定义 ID或用户ID进行查询。
	 * @param filter
	 *            扩展属性过滤器。
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （当前登陆人做过的流程）的条件参数。
	 * @return 所有符合条件的ProcessInst列表
	 * @throws BPMException
	 */
	public List<ProcessInst> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnums,Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 取得符合条件的所有活动实例。
	 * 
	 * @param condition
	 *            查询条件，例如根据活动状态(待办或在办的 活动)或所属流程实例进行查询。
	 * @param filter
	 *            扩展属性过滤器。
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （当前登陆人做过的活动）的条件参数。
	 * @return 所有符合条件的ActivityInst列表
	 * @throws BPMException
	 */
	public List<ActivityInst> getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnums,Filter filter, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
	 * 
	 * @param actvityInstId
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 活动实例历史（ActivityInstHistory）的列表
	 * @throws BPMException
	 */
	public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String actvityInstId,
			Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
	 * 
	 * @param processInstId
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 活动实例历史（ActivityInstHistory）的列表
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public List<ActivityInstHistory> getActivityInstHistoryListByProcessInst(String processInstId,
			Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 按照流程实例的ID取得流程实例对象
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @return 指定流程实例ID的ProcessInst对象
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ProcessInst getProcessInst(String processInstID) throws JDSException, BPMException;

	/**
	 * 更新流程实例名称（公文标题）
	 * 
	 * @param name
	 *            新名称，长度在100字节以内
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType updateProcessInstName(String processInstId, String name)
			throws BPMException;

	/**
	 * 更新流程实例紧急程度
	 * 
	 * @param urgency
	 *            新的紧急程度
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType updateProcessInstUrgency(String processInstId,
			String urgency) throws JDSException, BPMException;

	/**
	 * 按照活动实例的ID取得活动实例对象
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @return 指定活动实例ID的ActivityInst对象
	 * @throws BPMException
	 */
	public ActivityInst getActivityInst(String activityInstID)
			throws BPMException;

	/**
	 * 取得该活动所对应的流程版本上的所有活动定义
	 * 
	 * @param activityInstId
	 * @return
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public List<ActivityDef> getAllActivityDefs(String activityInstId) throws JDSException, BPMException;

	/**
	 * 按照活动实例历史的ID取得活动实例历史对象
	 * 
	 * @param activityInstHistoryID
	 *            活动实例历史ID
	 * @return 指定活动实例历史ID的ActivityInst对象
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ActivityInstHistory getActivityInstHistory(
			String activityInstHistoryID) throws JDSException, BPMException;

	// --------------------------------------------- 流程启动相关方法

	/**
	 * 开始一个流程
	 * 
	 * @param processDefId
	 *            要创建的流程定义ID
	 * @param processInstName
	 *            流程实例名称
	 * @param processUrgency
	 *            流程紧急程度
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （当前登陆人的ID）的条件参数。
	 * @return 创建的流程实例
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ProcessInst newProcess(String processDefId, String processInstName,
			String processUrgency, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * add by liwenzhang 05/03/22 自动启动流程实例，将定义的流程启动人实例化后作为该起始活动的办理人。
	 * 
	 * @param defId
	 *            要创建的流程定义ID
	 * @param processInstName
	 *            流程实例名称
	 * @param processUrgency
	 *            流程紧急程度
	 * @param initType
	 *            办理类型
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （当前登陆人的ID）的条件参数。
	 * @return 创建的流程实例
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ProcessInst newProcess(String processDefId, String processInstName,
			String processUrgency, String initType, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	// --------------------------------------------- 路由相关方法

	/**
	 * 取得某个活动实例的所有可提交路由的列表
	 * 
	 * @param startActivityInstID
	 *            起始活动实例ID
	 * @param condition
	 *            查询条件。
	 * @param routeFilter
	 *            路由条件过滤器。
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （当前登陆人ID）的条件参数。
	 * @return 路由定义的列表
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition,
                                        Filter routeFilter, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

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
			throws BPMException;

	/**
	 * 将当前用户完成决策的对象转储形成新的实例，本函数完成所有提交后的处理。 ActivityInstWrapper:当前活动实例
	 * 
	 * @param startActivityInstID
	 *            起始活动实例ID
	 * @param nextActivityDefIDs
	 *            要路由到的活动定义ID列表
	 * @param ctx
	 *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
	 *            （selectedDealer或selectedReader）的条件参数。
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType routeTo(String startActivityInstID,
			String[] nextActivityDefIDs, Map[] ctx) throws JDSException, BPMException;

	// --------------------------------------------- 收回相关方法

	/**
	 * 判断当前活动能否收回
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return true-可以收回；false-不能收回。
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public boolean canTakeBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 活动收回操作
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType takeBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	// --------------------------------------------- 退回相关方法

	/**
	 * 判断某活动能否退回
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return true-可以退回；false-不能退回。
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public boolean canRouteBack(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 取得可以退回的所有活动历史的列表
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param routeFilter
	 *            路由过滤器
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 活动实例历史的列表
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public List<EIActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstID,
			Filter routeFilter, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 退回操作
	 * 
	 * @param fromActivityInstID
	 *            需要执行退回操作的活动实例
	 * @param toActivityInstHistoryID
	 *            要退回到达的活动实例历史
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType routeBack(String fromActivityInstID,
			String toActivityInstHistoryID, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	// --------------------------------------------- 签收相关方法

	/**
	 * 能否签收
	 * 
	 * @param activityInstID
	 * @param ctx
	 *            权限相关上下文参数 return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public boolean canSignReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 签收操作
	 * 
	 * @param activityInstID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType signReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	// --------------------------------------------- 活动状态转换方法

	/**
	 * 活动挂起
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 继续活动实例
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	// --------------------------------------------- 流程状态转换方法

	/**
	 * 流程实例挂起
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType suspendProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 继续流程实例
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType resumeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 中止流程实例
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @param ctx
	 * @return
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType abortProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 流程实例完成
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType completeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 删除流程实例
	 * 
	 * @param processInstID
	 *            流程实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public ReturnType deleteProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	// --------------------------------------------- 事务控制方法

	/**
	 * 开始事务操作
	 * @throws BPMException 
	 * 
	 * @throws BPMException
	 */
	public void beginTransaction() throws JDSException, BPMException;

	/**
	 * 提交事务操作
	 * @throws BPMException 
	 * 
	 * @throws BPMException
	 */
	public void commitTransaction() throws JDSException, BPMException;

	/**
	 * 回滚事务操作
	 * @throws BPMException 
	 * 
	 * @throws BPMException
	 */
	public void rollbackTransaction() throws JDSException, BPMException;

	// ---------------------------------------------- 权限相关方法 add by lxl
	// 2004-01-15

	/**
	 * 流程定义相关的权限属性
	 * 
	 * @param processDefVersionId
	 *            流程定义版本的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 属性值
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public Object getProcessDefVersionRightAttribute(
			String processDefVersionId, ProcessDefVersionAtt attName, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 活动定义相关的权限属性
	 * 
	 * @param activityDefId
	 *            活动定义的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 属性值
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public Object getActivityDefRightAttribute(String activityDefId,
			ActivityDefRightAtt attName, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 路由定义相关的权限属性
	 * 
	 * @param routeDefId
	 *            路由定义的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 *            权限相关上下文参数
	 * @return
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public Object getRouteDefRightAttribute(String routeDefId, AttributeName attName,
			Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 活动实例的权限属性
	 * 
	 * @param activityInstId
	 *            活动实例的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 属性值
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public Object getActivityInstRightAttribute(String activityInstId,
			ActivityInstRightAtt attName, Map<RightCtx,Object> ctx) throws JDSException, BPMException;

	/**
	 * 活动实例历史的权限属性
	 * 
	 * @param activityInstHistoryId
	 *            活动实例历史的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 属性值
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public Object getActivityInstHistoryRightAttribute(
			String activityInstHistoryId, ActivityInstHistoryAtt attName, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 判断当前人对活动实例的权限
	 * 
	 * @param activityInstId
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 属性值
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;

	/**
	 * 得到当前人对活动实例的所有权限列表
	 * 
	 * @param activityInstId
	 * @param ctx
	 *            权限相关上下文参数
	 * @return
	 * @throws BPMException 
	 * @throws BPMException
	 */
	public List queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws JDSException, BPMException;
}


