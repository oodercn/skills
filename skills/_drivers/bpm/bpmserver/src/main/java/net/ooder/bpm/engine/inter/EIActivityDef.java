/**
 * $RCSfile: EIActivityDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.List;

import net.ooder.bpm.engine.BPMException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动定义接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lxl
 * @version 1.0
 */
public interface EIActivityDef {
	
	/**
	 * 活动所属流程UUID
	 * 
	 * @return
	 */
	public String getProcessDefId();

	public void setProcessDefId(String processDefId);

	/**
	 * 活动所属流程版本UUID
	 * 
	 * @return
	 */
	public String getProcessDefVersionId();

	public void setProcessDefVersionId(String processDefVersionId);

	/**
	 * 活动UUID
	 * 
	 * @return
	 */
	public String getActivityDefId();

	public void setActivityDefId(String activityDefId);

	/**
	 * 活动名称
	 * 
	 * @return
	 */
	public String getName();

	public void setName(String name);

	/**
	 * 活动描述
	 * 
	 * @return
	 */
	public String getDescription();

	public void setDescription(String description);

	/**
	 * 活动位置
	 * 
	 * @return
	 * <li> POSITION_NORMAL 一般活动
	 * <li> POSITION_START 起始活动
	 * <li> POSITION_END 结束活动
	 */
	public String getPosition();

	public void setPosition(String position);

	/**
	 * 活动实现方式
	 * 
	 * @return
	 * <li> IMPL_NO 手动活动
	 * <li> IMPL_TOOL 自动活动
	 * <li> IMPL_SUBFLOW 子流程活动
	 * <li> IMPL_OUTFLOW 跳转流程活动
	 */
	public String getImplementation();

	public void setImplementation(String implementation);

	/**
	 * 自动活动实现类
	 * 
	 * @return
	 */
	public String getExecClass();

	public void setExecClass(String execClass);

	/**
	 * 活动时间限制，单位由DurationUnit决定
	 * 
	 * @return
	 */
	public int getLimit();

	public void setLimit(int limit);

	/**
	 * 活动报警时间，单位由DurationUnit决定
	 * 
	 * @return
	 */
	public int getAlertTime();

	public void setAlertTime(int alertTime);

	/**
	 * 时间单位，如果没有设定则使用流程定义
	 * 
	 * @return
	 */
	public String getDurationUnit();

	public void setDurationUnit(String durationUnit);

	/**
	 * 到达时间限制后的操作
	 * 
	 * @return
	 */
	public String getDeadlineOperation();

	public void setDeadlineOperation(String deadlineOperation);

	/**
	 * 是否可以退回
	 * 
	 * @return
	 */
	public String getCanRouteBack();

	public void setCanRouteBack(String canRouteBack);

	/**
	 * 如果可以退回，退回的方法
	 * 
	 * @return
	 */
	public String getRouteBackMethod();

	public void setRouteBackMethod(String takeBackMethod);

	/**
	 * 是否可以特送
	 * 
	 * @return
	 */
	public String getCanSpecialSend();

	public void setCanSpecialSend(String canSpecialSend);

	/**
	 * 到达此活动节点路由的处理方法
	 * 
	 * @return
	 */
	public String getJoin();

	public void setJoin(String join);

	/**
	 * 从此活动节点出发的路由
	 * 
	 * @return
	 */
	public String getSplit();

	public void setSplit(String split);

	/**
	 * 从此活动节点出发的所有路由
	 * 
	 * @return
	 */
	public List<String> getOutRouteIds() throws BPMException;

	/**
	 * 进入从此活动节点的所有路由
	 * 
	 * @return
	 */
	public List<String> getInRouteIds() throws BPMException;

	/**
	 * 取得活动中的扩展属性值，此值已经经过解释<br>
	 * 当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
	 * 例如：<br>
	 * "Form1.field1.readRight" - 取得最顶层属性中名称为form1的下的名称为field1的子属性下名称为value的值
	 * 
	 * @param name
	 *            属性名称,"."隔开，不区分大小写
	 * @return 属性值，此值已经使用AttributeInterpret接口实现类解释后的值
	 */
	public EIAttributeDef getAttribute(String name);

	/**
	 * 取得活动中的扩展属性值，此值已经经过解释<br>
	 * 当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
	 * 例如：<br>
	 * "Form1.field1.readRight" - 取得最顶层属性中名称为form1的下的名称为field1的子属性下名称为value的值
	 * 
	 * @param name
	 *            属性名称,"."隔开，不区分大小写
	 * @return 属性值，此值已经使用AttributeInterpret接口实现类解释后的值
	 */
	public Object getAttributeInterpretedValue(String name);

	/**
	 * 取得活动中的扩展属性值,此值是未经解析的原值，即数据库中储存的值<br>
	 * 当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
	 * 例如：<br>
	 * "Form1.field1.readRight" - 取得最顶层属性中名称为form1的下的名称为field1的子属性下名称为value的值
	 * 
	 * @param name
	 *            属性名称,"."隔开，不区分大小写
	 * @return 属性值，此值是未经解析的原值
	 */
	public String getAttributeValue(String name);

	/**
	 * 取得所有的属性
	 * 
	 * @return
	 */
	public List getAllAttribute();

	/**
	 * 取得最顶层的属性（没有父属性的属性）
	 * 
	 * @return
	 */
	public List getTopAttribute();

	/**
	 * 清空活动定义内的所有扩展属性
	 */
	public void clearAttribute();

	public void setAttribute(String name, EIAttributeDef attDef)
			throws BPMException;

	/**
	 * 取得当前活动上注册的所有活动监听器
	 * 
	 * @return
	 */
	public List getListeners();

	public void setListeners(List list);

	/**
	 * 取得当前活动定义所使用的流程定义版本
	 * 
	 * @return 流程定义版本
	 * @throws BPMException
	 *             有异常发生
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException;

	/**
	 * 取得与该活动定义相对应的流程定义
	 * 
	 * @return 流程定义
	 */
	public EIProcessDef getProcessDef() throws BPMException;
}

