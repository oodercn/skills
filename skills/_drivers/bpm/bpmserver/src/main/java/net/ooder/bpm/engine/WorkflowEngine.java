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
import net.ooder.common.ReturnType;
import net.ooder.common.Filter;

import java.util.List;
import java.util.Map;

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
public interface WorkflowEngine {


    /**
     * 取得符合条件的流程定义版本列表。
     *
     * @param condition
     *            查询条件，例如根据流程定义的名称进行查询。
     * @param filter
     *            扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    public List<EIProcessDefVersion> getProcessDefVersionList(BPMCondition condition, Filter filter) throws BPMException;

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
    public List<EIProcessDef> getProcessDefList(BPMCondition condition, Filter filter) throws BPMException;

    /**
     * 取得符合条件的流程实例列表
     *
     * @param condition
     *            查询条件，例如根据流程状态，流程类型，流程定义 ID或用户ID进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @return 所有符合条件的ProcessInst列表
     * @throws BPMException
     */
    public List<EIProcessDef> getProcessInstList(BPMCondition condition, Filter filter) throws BPMException;

    /**
     * 取得符合条件的所有活动历史。
     *
     * @param condition
     *            查询条件，例如根据活动办理情况或所属流程实例进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @return 所有符合条件的EIActivityInstHistory列表
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryList(BPMCondition condition, Filter filter) throws BPMException;

    /**
     * 取得符合条件的所有活动实例。
     *
     * @param condition
     *            查询条件，例如根据活动状态(待办或在办的 活动)或所属流程实例进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @return 所有符合条件的ActivityInst列表
     * @throws BPMException
     */
    public List<EIActivityInst> getActivityInstList(BPMCondition condition, Filter filter) throws BPMException;

    public EIProcessInst createProcessInst(String defId, String instName, String urgency) throws BPMException;

    public EIProcessInst createProcessInst(String defId, String instName, String urgency, String initType) throws BPMException;

    /**
     * 新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     *
     * @param processInstId
     * @param activityDefId
     * @return
     * @throws BPMException
     */
    public EIActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId) throws BPMException;

    /**
     * 根据流程的开始活动节点，创建活动实例，但未启动活动实例
     *
     * @param processInstId
     * @return
     * @throws BPMException
     */
    public EIActivityInst startProcessInst(String processInstId) throws BPMException;

    /**
     * 取得流程第一个活动对象
     *
     * @param processDefVersionId
     * @return
     * @throws BPMException
     */
    public EIActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException;

    /**
     * 启动活动实例
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public ReturnType startActivityInst(String activityInstId) throws BPMException;

    /**
     * 更新流程实例的状态。
     *
     * @param processInstId
     *            流程实例的标识
     * @param state
     *            新的流程实例状态
     * @return
     * @throws BPMException
     */
    public ReturnType updateProcessState(String processInstId, String state) throws BPMException;

    /**
     * 保存活动历史。 <br>
     * 1. 将活动实例变为历史活动并存入数据库； <br>
     * 2. 并修改与该活动实例相对应的路由实例的信息； <br>
     * 3. 重新设置该活动实例的信息。 <br>
     * 4. 创建路由实例，并将路由实例的出发节点指向该历史实例，到达节点指向该活动实例。
     *
     * @param activityInstId
     *            活动实例的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInstHistory saveActivityHistoryInst(String activityInstId) throws BPMException;

    public EIActivityInst getSplitActivityInst(String activityHistoryInstId, boolean isnew) throws BPMException;

    /**
     * 分裂活动实例为不同的几个活动实例，活动分裂后将不能够收回
     *
     * @param activityInstId
     *            需要分裂的活动实例标识
     * @param count
     *            分裂的数量
     * @param activityHistoryInstId
     *            路由目标节点的活动定义标识的列表
     * @return 分裂后的活动实例列表，其中一个活动实例重用了原活动实例，其他的活动实例 为新创建的
     * @throws BPMException
     */
    public List<EIActivityInst> splitActivityInst(String activityInstId, int count, String activityHistoryInstId) throws BPMException;

    /**
     * 为抄送需求分裂活动实例为不同的几个活动实例，活动分裂后将不能够收回，状态转变为抄送状态
     *
     * @param activityHistoryInstId
     *            路由目标节点的活动定义标识的列表
     * @return 分裂后的活动实例列表，其中一个活动实例重用了原活动实例，其他的活动实例 为新创建的
     * @throws BPMException
     */
    public List<EIActivityInst> copyActivityInst(String activityHistoryInstId, int count) throws BPMException;

    /**
     * 取得所有可提交的前进路由的列表
     *
     * @param activityInstID
     *            路由起始活动实例ID
     * @param condition
     *            查询条件。
     * @param routeFilter
     *            路由条件过滤器。
     * @return 路由列表
     * @throws BPMException
     */
    public List<EIRouteDef> getNextRoutes(String activityInstID, BPMCondition condition, Filter routeFilter) throws BPMException;

    /**
     * 预测某活动实例可以合并的活动实例标识的数组， 如果该流程只有一份活动实例或者该活动实例节点不需要合并的则返回一个空List， 否则该流程实例所有的活动实例的标识列表。
     *
     * @param actInstId
     * @param actDefId
     * @return
     * @throws BPMException
     *
     */
    public List<EIActivityInst> forecastCombinableActivityInsts(String actInstId, String actDefId) throws BPMException;

    /**
     * 取得某活动实例可以合并的活动实例标识的数组， 如果该流程只有一份活动实例或者该活动实例节点不需要合并的则返回一个空List， 否则该流程实例所有的活动实例的标识列表。
     *
     * @param actInstId
     * @return
     * @throws BPMException
     */
    public List combinableActivityInsts(String actInstId) throws BPMException;

    /**
     * 预测某活动实例到达指定活动节点后是否可以合并或者需要挂起 1.判断所有的副本是否都到达<br>
     * 2.如果所有的副本都已到达，则合并，否则挂起<br>
     *
     * @return
     * @throws BPMException
     *             此方法用于clientservice在路由前预测是否会发生合并操作
     */
    @Deprecated
    public String forecastSuspendOrCombine(String actInstId, String actDefId) throws BPMException;

    /**
     * 某活动实例是否可以合并或者需要挂起 1.判断所有的副本是否都到达<br>
     * 2.如果所有的副本都已到达，则合并，否则挂起<br>
     *
     * @return
     * @throws BPMException
     */
    public String suspendOrCombine(String actInstId) throws BPMException;

    public String suspendOrCombine(String actInstId, String activityDefId) throws BPMException;

    /**
     * 从某个活动实例路由到另一活动实例。 不重新创建实例，而是重复使用该实例。<br>
     * 另外，分裂了的活动需要在需合并的节点挂起等待或进行合并<br>
     * 活动有多个副本的情况：<br>
     * 1.判断该节点是否是合并节点<br>
     * 2.判断所有的副本是否都到达<br>
     * 3.如果所有的副本都已到达，则合并，否则挂起<br>
     * 副本合并时需要执行以下操作：<br>
     *
     * @param fromActivityInstId
     *            路由出发点活动实例（或分裂后的实例）的标识
     * @param toActivityDefId
     *            路由目标节点活动定义的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInst routeTo(String fromActivityInstId, String toActivityDefId) throws BPMException;

    /**
     * 是否可以退回。
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public boolean canRouteBack(String activityInstId) throws BPMException;

    /**
     * 取得活动实例可以退回的活动历史实例的列表，可退回到的节点由 “ROUTEBACKMETHOD”字段的值确定
     *
     * @param activityInstId
     * @param routeFilter
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getRouteBacks(String activityInstId, Filter routeFilter) throws BPMException;

    /**
     * 活动实例退回操作。
     *
     * @param activityInstId
     *            活动实例的标识。
     * @param activityInstHistoryId
     *            退回到某活动实例历史的标识
     * @return
     * @throws BPMException
     */
    public EIActivityInst routeBack(String activityInstId, String activityInstHistoryId) throws BPMException;

    /**
     * 是否可以收回。<br>
     * 如果活动实例的状态为"notStarting"，而且活动的“是否可以收回”字段的值不为"NO"时， 则可以收回，否则不能收回。
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public boolean canTakeBack(String activityInstId) throws BPMException;

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
    public ReturnType tackBack(String activityInstId, Map ctx) throws BPMException;

    /**
     * 活动阅毕操作
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory endRead(String activityInstID, Map ctx) throws BPMException;

    /**
     * 活动结束操作
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory endTask(String activityInstID, Map ctx) throws BPMException;

    /**
     * 任务执行失败操作
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public EIActivityInstHistory abortedTask(String activityInstID, Map ctx) throws BPMException;

    /**
     * 彻底删除历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType clearHistory(String activityInstHistoryID, Map ctx) throws BPMException;

    /**
     * 删除历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType deleteHistory(String activityInstHistoryID, Map ctx) throws BPMException;

    /**
     * 还原历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType restoreHistory(String activityInstHistoryID, Map ctx) throws BPMException;

    /**
     * 查询是否能签收
     *
     * @param activityInstID
     * @return
     */
    public boolean canSignReceive(String activityInstID) throws BPMException;

    /**
     * 签收活动实例，启动该活动实例。
     *
     * @param activityInstID
     *            活动实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType signReceive(String activityInstID) throws BPMException;

    /**
     *
     * @param activityInsts
     * @return
     * @throws BPMException
     */
    public EIActivityInst combineActivityInsts(List<EIActivityInst> activityInsts) throws BPMException;

    /**
     * 合并分裂的活动实例,保留第一个活动实例<br>
     * 1.需要合并的内容包括：扩展属性，活动的其他属性重新初始化<br>
     *
     * @param activityInsts
     *            活动实例标识的数组
     * @return
     * @throws BPMException
     */
    public EIActivityInst combineActivityInsts(String[] activityInsts) throws BPMException;

    /**
     * 活动挂起
     *
     * @param activityInstID
     * @return
     * @throws BPMException
     */
    public ReturnType suspendActivityInst(String activityInstID) throws BPMException;

    /** 继续活动实例 */
    public ReturnType resumeActivityInst(String activityInstID) throws BPMException;

    /** 流程实例挂起 */
    public ReturnType suspendProcessInst(String processInstID) throws BPMException;

    /** 继续流程实例 */
    public ReturnType resumeProcessInst(String processInstID) throws BPMException;

    /**
     * 中止流程实例，将对应的活动实例存为历史，然后将其删除。
     *
     * @param processInstID
     * @return
     * @throws BPMException
     */
    public ReturnType abortProcessInst(String processInstID) throws BPMException;

    /**
     * 流程实例完成。将如果还存在对应的活动实例，则将其删除。
     *
     * @param processInstID
     *            流程实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType completeProcessInst(String processInstID) throws BPMException;

    /**
     * 删除流程实例。<br>
     * 删除的内容包括：<br>
     * 1. 流程实例信息；<br>
     * 2. 相应的活动实例信息；<br>
     * 3. 相应的活动实例历史的信息；<br>
     * 4. 相应的路由实例信息。
     *
     * @param processInstID
     *            流程实例的标识
     * @return
     * @throws BPMException
     */
    public ReturnType deleteProcessInst(String processInstID) throws BPMException;

    /**
     * 根据指定历史分裂节点获分裂出去的所有历史
     *
     * @param historyHisroryId
     * @param noSplit//是否包含分裂的节点
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException;

    /**
     * 根据指定活动获取所有曾经分裂过的历史节点
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getLastSplitActivityInstHistoryByActvityInst(String activityInstId) throws BPMException;

    public List<EIActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String activityInstId) throws BPMException;

    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列 该方法会递归查找当前活动上所有步骤包括分列和未分裂的
     *
     * @param activityInstId
     *            活动实例ID
     * @param noSplit
     *            是否分裂
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String activityInstId, boolean noSplit) throws BPMException;

    ;

    /**
     * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param activityInstId
     *            活动实例ID
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryListByActvityInst(String activityInstId) throws BPMException;

    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @param processInstId
     *            流程实例ID
     * @return
     * @throws BPMException
     */
    public List<EIActivityInstHistory> getActivityInstHistoryListByProcessInst(String processInstId) throws BPMException;

    /**
     * 取得该流程实例的所有路由实例
     *
     * @param processInstId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getRouteInsts(String processInstId) throws BPMException;

    ;

    /**
     * 取得该实例的到达路由
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getActivityInstInRoute(String activityInstId) throws BPMException;

    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId
     *            活动实例历史ID
     * @return List<EIActivityInst>
     * @throws BPMException
     */
    public List<EIActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) throws BPMException;

    /**
     * 取得从该实例历史发出的实例
     *
     * @param activityInstHistoryId
     * @return
     * @throws BPMException
     */
    public List<EIRouteInst> getActivityInstHistoryOutRoute(String activityInstHistoryId) throws BPMException;

    /**
     * 删除活动实例。将同时删除路由实例。
     *
     * @param activityInstID
     * @return
     * @throws BPMException
     */
    public ReturnType deleteActivityInst(String activityInstID) throws BPMException;


}


