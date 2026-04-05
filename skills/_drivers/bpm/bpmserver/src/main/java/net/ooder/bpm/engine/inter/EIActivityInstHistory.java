/**
 * $RCSfile: EIActivityInstHistory.java,v $
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
 * 活动实例的历史数据访问接口
 */
public interface EIActivityInstHistory {
	/**
	 * Getter method for activityhistoryId
	 * 
	 * @return the value of activityhistoryId
	 */
	public abstract String getActivityHistoryId();

	/**
	 * Setter method for activityhistoryId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityhistoryId
	 */
	public abstract void setActivityHistoryId(String newVal);

	/**
	 * Getter method for processinstId
	 * 
	 * @return the value of processinstId
	 */
	public abstract String getProcessInstId();

	/**
	 * Setter method for processinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstId
	 */
	public abstract void setProcessInstId(String newVal);

	/**
	 * Getter method for activitydefId
	 * 
	 * @return the value of activitydefId
	 */
	public abstract String getActivityDefId();

	/**
	 * Setter method for activitydefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activitydefId
	 */
	public abstract void setActivityDefId(String newVal);

	/**
	 * Getter method for activityinstId
	 * 
	 * @return the value of activityinstId
	 */
	public abstract String getActivityInstId();

	/**
	 * Setter method for activityinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstId
	 */
	public abstract void setActivityInstId(String newVal);

	/**
	 * Getter method for urgencyType
	 * 
	 * @return the value of urgencyType
	 */
	public abstract String getUrgency();

	/**
	 * Setter method for urgencyType
	 * 
	 * @param newVal
	 *            The new value to be assigned to urgencyType
	 */
	public abstract void setUrgency(String newVal);

	/**
	 * Getter method for arrivedTime
	 * 
	 * @return the value of arrivedTime
	 */
	public abstract Date getArrivedTime();

	/**
	 * Setter method for arrivedTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to arrivedTime
	 */
	public abstract void setArrivedTime(Date newVal);

	/**
	 * Getter method for limitTime
	 * 
	 * @return the value of limitTime
	 */
	public abstract Date getLimitTime();

	/**
	 * Setter method for limitTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to limitTime
	 */
	public abstract void setLimitTime(Date newVal);

	/**
	 * Getter method for startTime
	 * 
	 * @return the value of startTime
	 */
	public abstract Date getStartTime();

	/**
	 * Setter method for startTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to startTime
	 */
	public abstract void setStartTime(Date newVal);

	/**
	 * Getter method for endTime
	 * 
	 * @return the value of endTime
	 */
	public abstract Date getEndTime();

	/**
	 * Setter method for endTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to endTime
	 */
	public abstract void setEndTime(Date newVal);

	/**
	 * Getter method for recieveState
	 * 
	 * @return the value of recieveState
	 */
	public abstract String getReceiveMethod();

	/**
	 * Setter method for recieveState
	 * 
	 * @param newVal
	 *            The new value to be assigned to recieveState
	 */
	public abstract void setReceiveMethod(String newVal);

	/**
	 * Getter method for dealState
	 * 
	 * @return the value of dealState
	 */
	public abstract String getDealMethod();

	/**
	 * Setter method for dealState
	 * 
	 * @param newVal
	 *            The new value to be assigned to dealState
	 */
	public abstract void setDealMethod(String newVal);

	/**
	 * Getter method for runState
	 * 
	 * @return the value of runState
	 */
	public abstract String getRunStatus();

	/**
	 * Setter method for runState
	 * 
	 * @param newVal
	 *            The new value to be assigned to runState
	 */
	public abstract void setRunStatus(String newVal);

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public abstract void copy(EIActivityInstHistory bean);

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
	 * 取得所有扩展属性实例
	 * 
	 * @return
	 */
	public List getAllAttribute();

	/* 属性操作 */
	// 设置属性
	public void setAttribute(String type, EIAttributeInst attr)
			throws BPMException;

	// 删除属性
	public void removeAttribute(EIAttributeInst attr) throws BPMException;

	/**
	 * 取得当前活动历史所对应的流程实例
	 * 
	 * @return 流程实例
	 * @throws BPMException
	 *             有异常发生
	 */
	public EIProcessInst getProcessInst() throws BPMException;

	/**
	 * 取得与该活动历史对应的活动实例
	 * 
	 * @return 活动实例
	 */
	public EIActivityInst getActivityInst() throws BPMException;

	/**
	 * 取得与该活动历史相对应的活动定义
	 * 
	 * @return 活动定义
	 */
	public EIActivityDef getActivityDef() throws BPMException;

	
}

