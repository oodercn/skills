/**
 * $RCSfile: EIActivityInst.java,v $
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
 * Description: 活动实例接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public interface EIActivityInst {
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
	 * Getter method for processdefId
	 * 
	 * @return the value of processdefId
	 */
	public abstract String getProcessDefId();

	public abstract EIProcessDef getProcessDef() throws BPMException;

	/**
	 * Setter method for processdefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefId
	 */
	public abstract void setProcessDefId(String newVal);

	/**
	 * Getter method for activityinstState
	 * 
	 * @return the value of activityinstState
	 */
	public abstract String getState();

	/**
	 * Setter method for activityinstState
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstState
	 */
	public abstract void setState(String newVal);

	/**
	 * Getter method for urgencytype
	 * 
	 * @return the value of urgencytype
	 */
	public abstract String getUrgency();

	/**
	 * Setter method for urgencytype
	 * 
	 * @param newVal
	 *            The new value to be assigned to urgencytype
	 */
	public abstract void setUrgency(String newVal);

	/**
	 * Getter method for arrivedtime
	 * 
	 * @return the value of arrivedtime
	 */
	public abstract Date getArrivedTime();

	/**
	 * Setter method for arrivedtime
	 * 
	 * @param newVal
	 *            The new value to be assigned to arrivedtime
	 */
	public abstract void setArrivedTime(Date newVal);

	/**
	 * Getter method for limittime
	 * 
	 * @return the value of limittime
	 */
	public abstract Date getLimitTime();
	
	/**
	 * Getter method for alerttime
	 * 
	 * @return the value of alerttime
	 */
	public abstract Date getAlertTime();

	/**
	 * Setter method for limittime
	 * 
	 * @param newVal
	 *            The new value to be assigned to limittime
	 */
	public abstract void setLimitTime(Date newVal);

	
	/**
	 * Setter method for alerttime
	 * 
	 * @param newVal
	 *            The new value to be assigned to alerttime
	 */
	public abstract void setAlertTime(Date newVal);

	/**
	 * Getter method for starttime
	 * 
	 * @return the value of starttime
	 */
	public abstract Date getStartTime();

	/**
	 * Setter method for starttime
	 * 
	 * @param newVal
	 *            The new value to be assigned to starttime
	 */
	public abstract void setStartTime(Date newVal);

	/**
	 * Getter method for recievestate
	 * 
	 * @return the value of recievestate
	 */
	public abstract String getReceiveMethod();

	/**
	 * Setter method for recievestate
	 * 
	 * @param newVal
	 *            The new value to be assigned to recievestate
	 */
	public abstract void setReceiveMethod(String newVal);

	/**
	 * Getter method for dealstate
	 * 
	 * @return the value of dealstate
	 */
	public abstract String getDealMethod();

	/**
	 * Setter method for dealstate
	 * 
	 * @param newVal
	 *            The new value to be assigned to dealstate
	 */
	public abstract void setDealMethod(String newVal);

	/**
	 * Getter method for runstate
	 * 
	 * @return the value of runstate
	 */
	public abstract String getRunStatus();

	/**
	 * Setter method for runstate
	 * 
	 * @param newVal
	 *            The new value to be assigned to runstate
	 */
	public abstract void setRunStatus(String newVal);

	public abstract String getCanTakeBack();

	public abstract void setCanTakeBack(String can);

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
	public void setAttribute(String name, EIAttributeInst attr)
			throws BPMException;
	
	

	// 删除属性
	public void removeAttribute(EIAttributeInst attr) throws BPMException;

	public EIAttributeInst getAttribute(String name);

	/**
	 * 取得当前活动所对应的流程实例
	 * 
	 * @return 流程实例
	 * @throws BPMException
	 *             有异常发生
	 */
	public EIProcessInst getProcessInst() throws BPMException;

	/**
	 * 取得与该活动实例相对应的流程定义
	 * 
	 * @return 流程定义
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException;

	/**
	 * 取得与该活动实例相对应的活动定义
	 * 
	 * @return 活动定义
	 */
	public EIActivityDef getActivityDef() throws BPMException;

}
