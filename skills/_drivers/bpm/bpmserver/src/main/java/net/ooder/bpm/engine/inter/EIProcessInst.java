/**
 * $RCSfile: EIProcessInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.Date;
import java.util.List;

import net.ooder.bpm.engine.BPMException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface EIProcessInst {

	/*
	 * 取得流程实例UUID @return 流程实例UUID
	 */
	public String getProcessInstId();

	public void setProcessInstId(String processInstId);

	/**
	 * 取得流程定义的UUID
	 * 
	 * @return 流程定义UUID
	 */
	public String getProcessDefId();

	public void setProcessDefId(String processDefId);

	/**
	 * 取得流程定义版本UUID
	 * 
	 * @return 流程定义版本UUID
	 */
	public String getProcessDefVersionId();

	public void setProcessDefVersionId(String processDefVersionId);

	/**
	 * 取得流程实例名称
	 * 
	 * @return 流程实例名称
	 */
	public String getName();

	public void setName(String name);

	/**
	 * 取得紧急程度
	 * 
	 * @return 紧急程度
	 */
	public String getUrgency();

	public void setUrgency(String urgency);

	/**
	 * 取得流程实例状态，有以下几种取值： ProcessInst.STATE_OPEN ProcessInst.STATE_RUNNING
	 * ProcessInst.STATE_NOTRUNNING ProcessInst.STATE_NOTSTARTED
	 * ProcessInst.STATE_SUSPENDED ProcessInst.STATE_CLOSED
	 * ProcessInst.STATE_ABORTED ProcessInst.STATE_TERMINATED
	 * ProcessInst.STATE_COMPLETED
	 * 
	 * @see net.ooder.bpm.client.ProcessInst
	 * @return 流程实例状态
	 */
	public String getState();

	public void setState(String state);

	/**
	 * 取得流程实例副本数量
	 * 
	 * @return 流程实例副本数量
	 */
	public int getCopyNumber();

	public void setCopyNumber(int copyNumber);

	/**
	 * 取得流程实例启动时间
	 * 
	 * @return 流程实例启动时间
	 */
	public Date getStartTime();

	public void setStartTime(Date startTime);

	/**
	 * 取得流程实例办结时间
	 * 
	 * @return 流程实例办结时间
	 */
	public Date getEndTime();

	public void setEndTime(Date endTime);

	/**
	 * 取得流程实例时间限制
	 * 
	 * @return 流程实例时间限制
	 */
	public Date getLimitTime();

	public void setLimitTime(Date limitTime);

	/**
	 * 取得流程实例运行状况，有以下几种取值： ProcessInst.NORMAL --> 正常 ProcessInst.DELAY --> 延期
	 * ProcessInst.URGENCY --> 催办 ProcessInst.ALERT --> 报警
	 * 
	 * @see net.ooder.bpm.client.ProcessInst
	 * @return 流程实例运行状况
	 */
	public String getRunStatus();

	public void setRunStatus(String runState);

	/**
	 * 取得当前流程实例所使用的流程定义版本
	 * 
	 * @return 流程定义版本
	 * @throws BPMException
	 *             有异常发生
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException;

	/**
	 * 取得与该流程实例相对应的流程定义
	 * 
	 * @return 流程定义
	 */
	public EIProcessDef getProcessDef() throws BPMException;

	/**
	 * 取得当前流程实例对应的所有活动实例
	 * 
	 * @return 活动实例列表
	 * @throws BPMException
	 *             有异常发生
	 */
	public List<EIActivityInst> getActivityInstList() throws BPMException;

	/**
	 * 取得当前流程实例对应的所有活动实例历史
	 * 
	 * @return 活动实例历史列表
	 * @throws BPMException
	 *             有异常发生
	 */
	public List<EIActivityInstHistory> getActivityInstHistoryList() throws BPMException;

	/**
	 * 取得扩展属性值，此值已经经过解释<br>
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
	 * 取得扩展属性值,此值是未经解析的原值，即数据库中储存的值<br>
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
	 * 取得所有扩展属性
	 * 
	 * @return 扩展属性列表
	 */
	public List getAllAttribute();

	public EIAttributeInst getAttribute(String name);

	public void setAttribute(String name, EIAttributeInst att)
			throws BPMException;

	
	
	// 删除属性
	public void removeAttribute(EIAttributeInst attr) throws BPMException;
}


