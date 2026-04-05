/**
 * $RCSfile: DbActivityInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.*;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例接口的数据库实现
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_ACTIVITYINSTANCE
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
public class DbActivityInst implements EIActivityInst, Cacheable, Serializable {
	private static Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInst.class);

	private String activityInstId;

	private boolean activityInstId_is_modified = false;

	private boolean activityInstId_is_initialized = false;

	private String processInstId;

	private boolean processInstId_is_modified = false;

	private boolean processInstId_is_initialized = false;

	private String activityDefId;

	private boolean activityDefId_is_modified = false;

	private boolean activityDefId_is_initialized = false;

	private String processDefId;

	private boolean processDefId_is_modified = false;

	private boolean processDefId_is_initialized = false;

	private String state;

	private boolean activityinstState_is_modified = false;

	private boolean activityinstState_is_initialized = false;

	private String urgency;

	private boolean urgency_is_modified = false;

	private boolean urgency_is_initialized = false;

	private Date arrivedTime;

	private boolean arrivedTime_is_modified = false;

	private boolean arrivedTime_is_initialized = false;

	private Date limitTime;

	private boolean limitTime_is_modified = false;

	private boolean limitTime_is_initialized = false;
	
	private Date alertTime;

	private boolean alertTime_is_modified = false;

	private boolean alertTime_is_initialized = false;


	private Date startTime;

	private boolean startTime_is_modified = false;

	private boolean startTime_is_initialized = false;

	private String recieveState;

	private boolean recieveState_is_modified = false;

	private boolean recieveState_is_initialized = false;

	private String dealState;

	private boolean dealState_is_modified = false;

	private boolean dealState_is_initialized = false;

	private String runState;

	private boolean runState_is_modified = false;

	private boolean runState_is_initialized = false;

	private String canTakeBack;

	private boolean cantakeback_is_modified = false;

	private boolean cantakeback_is_initialized = false;

	private boolean _isNew = true;

	private boolean _isAttributeModified = true;

	Map attributeTopMap = null; // store the top level attribute

	Map attributeIdMap = null; // store all attribute in this activity
								// definition

	/**
	 */
	DbActivityInst() {
	}

	/**
	 * Getter method for activityinstId
	 * 
	 * @return the value of activityinstId
	 */
	public String getActivityInstId() {
		return activityInstId;
	}

	/**
	 * Setter method for activityinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstId
	 */
	public void setActivityInstId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityInstId) == true)
				|| (newVal == null && this.activityInstId == null))
			return;
		this.activityInstId = newVal;
		activityInstId_is_modified = true;
		activityInstId_is_initialized = true;
	}

	/**
	 * @return
	 */
	public boolean isAttributeModified() {
		if (attributeIdMap == null && attributeTopMap == null) {
			return false;
		}
		return activityInstId_is_modified || _isAttributeModified;
	}

	/**
	 * Determine if the activityinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstIdModified() {
		return activityInstId_is_modified;
	}

	/**
	 * Determine if the activityinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstIdInitialized() {
		return activityInstId_is_initialized;
	}

	/**
	 * Getter method for processinstId
	 * 
	 * @return the value of processinstId
	 */
	public String getProcessInstId() {
		return processInstId;
	}

	/**
	 * Setter method for processinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstId
	 */
	public void setProcessInstId(String newVal) {
		if ((newVal != null && newVal.equals(this.processInstId) == true)
				|| (newVal == null && this.processInstId == null))
			return;
		this.processInstId = newVal;
		processInstId_is_modified = true;
		processInstId_is_initialized = true;
	}

	/**
	 * Determine if the processinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstIdModified() {
		return processInstId_is_modified;
	}

	/**
	 * Determine if the processinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstIdInitialized() {
		return processInstId_is_initialized;
	}

	/**
	 * Getter method for activitydefId
	 * 
	 * @return the value of activitydefId
	 */
	public String getActivityDefId() {
		return activityDefId;
	}

	/**
	 * Setter method for activitydefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activitydefId
	 */
	public void setActivityDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityDefId) == true)
				|| (newVal == null && this.activityDefId == null))
			return;
		this.activityDefId = newVal;
		activityDefId_is_modified = true;
		activityDefId_is_initialized = true;
	}

	/**
	 * Determine if the activitydefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivitydefIdModified() {
		return activityDefId_is_modified;
	}

	/**
	 * Determine if the activitydefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivitydefIdInitialized() {
		return activityDefId_is_initialized;
	}

	/**
	 * Getter method for processdefId
	 * 
	 * @return the value of processdefId
	 */
	public String getProcessDefId() {

			return this.processDefId;

	}

	public EIProcessDef getProcessDef() throws BPMException {
		return EIProcessDefManager.getInstance().loadByKey(this.getProcessDefId());
	}

	/**
	 * Setter method for processdefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefId
	 */
	public void setProcessDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.processDefId) == true)
				|| (newVal == null && this.processDefId == null))
			return;
		this.processDefId = newVal;
		processDefId_is_modified = true;
		processDefId_is_initialized = true;
	}

	/**
	 * Determine if the processdefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefIdModified() {
		return processDefId_is_modified;
	}

	/**
	 * Determine if the processdefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefIdInitialized() {
		return processDefId_is_initialized;
	}

	/**
	 * Getter method for activityinstState
	 * 
	 * @return the value of activityinstState
	 */
	public String getState() {
		return state;
	}

	/**
	 * Setter method for activityinstState
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstState
	 */
	public void setState(String newVal) {
		if ((newVal != null && newVal.equals(this.state) == true)
				|| (newVal == null && this.state == null))
			return;
		this.state = newVal;
		activityinstState_is_modified = true;
		activityinstState_is_initialized = true;
	}

	/**
	 * Determine if the activityinstState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstStateModified() {
		return activityinstState_is_modified;
	}

	/**
	 * Determine if the activityinstState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstStateInitialized() {
		return activityinstState_is_initialized;
	}

	/**
	 * Getter method for urgencytype
	 * 
	 * @return the value of urgencytype
	 */
	public String getUrgency() {
		return urgency;
	}

	/**
	 * Setter method for urgencytype
	 * 
	 * @param newVal
	 *            The new value to be assigned to urgencytype
	 */
	public void setUrgency(String newVal) {
		if ((newVal != null && newVal.equals(this.urgency) == true)
				|| (newVal == null && this.urgency == null))
			return;
		this.urgency = newVal;
		urgency_is_modified = true;
		urgency_is_initialized = true;
	}

	/**
	 * Determine if the urgencytype is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isUrgencytypeModified() {
		return urgency_is_modified;
	}

	/**
	 * Determine if the urgencytype has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isUrgencytypeInitialized() {
		return urgency_is_initialized;
	}

	/**
	 * Getter method for arrivedtime
	 * 
	 * @return the value of arrivedtime
	 */
	public Date getArrivedTime() {
		return arrivedTime;
	}

	/**
	 * Setter method for arrivedtime
	 * 
	 * @param newVal
	 *            The new value to be assigned to arrivedtime
	 */
	public void setArrivedTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.arrivedTime) == true)
				|| (newVal == null && this.arrivedTime == null))
			return;
		this.arrivedTime = newVal;
		arrivedTime_is_modified = true;
		arrivedTime_is_initialized = true;
	}

	/**
	 * Determine if the arrivedtime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isArrivedtimeModified() {
		return arrivedTime_is_modified;
	}

	/**
	 * Determine if the arrivedtime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isArrivedtimeInitialized() {
		return arrivedTime_is_initialized;
	}

	
	
	/**
	 * Getter method for limittime
	 * 
	 * @return the value of limittime
	 */
	public Date getLimitTime() {
		return limitTime;
	}

	/**
	 * Setter method for limittime
	 * 
	 * @param newVal
	 *            The new value to be assigned to limittime
	 */
	public void setLimitTime(Date newVal) {
		if (newVal == this.limitTime)
			return;
		this.limitTime = newVal;
		limitTime_is_modified = true;
		limitTime_is_initialized = true;
	}
	

	/**
	 * Determine if the limittime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isLimittimeModified() {
		return limitTime_is_modified;
	}

	/**
	 * Determine if the limittime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isLimittimeInitialized() {
		return limitTime_is_initialized;
	}


	/**
	 * Getter method for alerttime
	 * 
	 * @return the value of alerttime
	 */
	public Date getAlertTime() {
		return alertTime;
	}

	/**
	 * Setter method for alerttime
	 * 
	 * @param newVal
	 *            The new value to be assigned to alerttime
	 */
	public void setAlertTime(Date newVal) {
		if (newVal == this.alertTime)
			return;
		this.alertTime = newVal;
		alertTime_is_modified = true;
		alertTime_is_initialized = true;
	}

	/**
	 * Determine if the alerttime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isAlerttimeModified() {
		return alertTime_is_modified;
	}

	
	
	
	
	/**
	 * Determine if the alerttime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isAlerttimeInitialized() {
		return alertTime_is_initialized;
	}
	
	
	/**
	 * Getter method for starttime
	 * 
	 * @return the value of starttime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Setter method for starttime
	 * 
	 * @param newVal
	 *            The new value to be assigned to starttime
	 */
	public void setStartTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.startTime) == true)
				|| (newVal == null && this.startTime == null))
			return;
		this.startTime = newVal;
		startTime_is_modified = true;
		startTime_is_initialized = true;
	}

	/**
	 * Determine if the starttime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isStarttimeModified() {
		return startTime_is_modified;
	}

	/**
	 * Determine if the starttime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isStarttimeInitialized() {
		return startTime_is_initialized;
	}

	/**
	 * Getter method for recievestate
	 * 
	 * @return the value of recievestate
	 */
	public String getReceiveMethod() {
		return recieveState;
	}

	/**
	 * Setter method for recievestate
	 * 
	 * @param newVal
	 *            The new value to be assigned to recievestate
	 */
	public void setReceiveMethod(String newVal) {
		if ((newVal != null && newVal.equals(this.recieveState) == true)
				|| (newVal == null && this.recieveState == null))
			return;
		this.recieveState = newVal;
		recieveState_is_modified = true;
		recieveState_is_initialized = true;
	}

	/**
	 * Determine if the recievestate is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRecievestateModified() {
		return recieveState_is_modified;
	}

	/**
	 * Determine if the recievestate has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRecievestateInitialized() {
		return recieveState_is_initialized;
	}

	/**
	 * Getter method for dealstate
	 * 
	 * @return the value of dealstate
	 */
	public String getDealMethod() {
		return dealState;
	}

	/**
	 * Setter method for dealstate
	 * 
	 * @param newVal
	 *            The new value to be assigned to dealstate
	 */
	public void setDealMethod(String newVal) {
		if ((newVal != null && newVal.equals(this.dealState) == true)
				|| (newVal == null && this.dealState == null))
			return;
		this.dealState = newVal;
		dealState_is_modified = true;
		dealState_is_initialized = true;
	}

	/**
	 * Determine if the dealstate is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDealstateModified() {
		return dealState_is_modified;
	}

	/**
	 * Determine if the dealstate has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDealstateInitialized() {
		return dealState_is_initialized;
	}

	/**
	 * Getter method for runstate
	 * 
	 * @return the value of runstate
	 */
	public String getRunStatus() {
		return runState;
	}

	/**
	 * Setter method for runstate
	 * 
	 * @param newVal
	 *            The new value to be assigned to runstate
	 */
	public void setRunStatus(String newVal) {
		if ((newVal != null && newVal.equals(this.runState) == true)
				|| (newVal == null && this.runState == null))
			return;
		this.runState = newVal;
		runState_is_modified = true;
		runState_is_initialized = true;
	}

	/**
	 * Determine if the runstate is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRunstateModified() {
		return runState_is_modified;
	}

	/**
	 * Determine if the runstate has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRunstateInitialized() {
		return runState_is_initialized;
	}

	/**
	 * Determine if the current object is new or not
	 * 
	 * @return true if the current object is new, false if the object is not new
	 */
	public boolean isNew() {
		return _isNew;
	}

	/**
	 * Specify to the object if he has to been set as new or not
	 * 
	 * @param isNew
	 *            the boolean value to be assigned to the isNew field
	 */
	public void setIsNew(boolean isNew) {
		this._isNew = isNew;
	}

	/**
	 * Determine if the object has been modified since the last time this method
	 * was called or since the creation of the object
	 * 
	 * @return true if the object has been modified, false if the object has not
	 *         been modified
	 */
	public boolean isModified() {
		return activityInstId_is_modified || processInstId_is_modified
				|| activityDefId_is_modified || processDefId_is_modified
				|| activityinstState_is_modified || urgency_is_modified
				|| arrivedTime_is_modified || limitTime_is_modified
				|| alertTime_is_modified	|| startTime_is_modified
				|| recieveState_is_modified || dealState_is_modified
				|| runState_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		activityInstId_is_modified = false;
		processInstId_is_modified = false;
		activityDefId_is_modified = false;
		processDefId_is_modified = false;
		activityinstState_is_modified = false;
		urgency_is_modified = false;
		arrivedTime_is_modified = false;
		limitTime_is_modified = false;
		alertTime_is_modified = false;
		startTime_is_modified = false;
		recieveState_is_modified = false;
		dealState_is_modified = false;
		runState_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbActivityInst bean) {
		setActivityInstId(bean.getActivityInstId());
		setProcessInstId(bean.getProcessInstId());
		setActivityDefId(bean.getActivityDefId());
		setProcessDefId(bean.getProcessDefId());
		setState(bean.getState());
		setUrgency(bean.getUrgency());
		setArrivedTime(bean.getArrivedTime());
		setLimitTime(bean.getLimitTime());
		setAlertTime(bean.getAlertTime());
		setStartTime(bean.getStartTime());
		setReceiveMethod(bean.getReceiveMethod());
		setDealMethod(bean.getDealMethod());
		setRunStatus(bean.getRunStatus());
		setCanTakeBack(bean.getCanTakeBack());
	}

	/**
	 * 取得属性值，当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
	 * 例如：<br>
	 * "Form1.field1.value" - 取得最顶层属性中名称为form1的
	 * 
	 * @param name
	 * @return
	 */
	public EIAttributeInst getAttribute(String name) {
		if (name != null) {
			name = name.toUpperCase();
		}

		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance attribute error!");
			return null;
		}

		StringTokenizer st = new StringTokenizer(name, ".");
		DbAttributeInst subAtt = null;
		while (st.hasMoreTokens()) {
			String subname = st.nextToken();
			if (subAtt == null) { // top level
				subAtt = (DbAttributeInst) attributeTopMap.get(subname);
			} else {
				subAtt = (DbAttributeInst) subAtt.getChild(subname);
			}

			if (subAtt == null) {
				return null; // not found
			}
		}
		return subAtt;
	}

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
	public Object getAttributeInterpretedValue(String name) {
		EIAttributeInst attInst = getAttribute(name);
		if (attInst == null) {
			return null;
		}
		return attInst.getInterpretedValue();
	}

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
	public String getAttributeValue(String name) {
		EIAttributeInst attInst = getAttribute(name);
		if (attInst == null) {
			return null;
		}
		return attInst.getValue();
	}

	/**
	 * 取得所有扩展属性实例
	 * 
	 * @return
	 */
	public List getAllAttribute() {
		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance attribute error!");
			return new ArrayList(0);
		}
		return new ArrayList(attributeIdMap.values());
	}

	/**
	 * 取得所有的属性类型
	 * 
	 * @return
	 */
	public List getTopAttribute() {
		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance attribute error!");
			return new ArrayList(0);
		}

		List list = new ArrayList(attributeTopMap.values());
		List result = new ArrayList();
		for (Iterator it = list.iterator(); it.hasNext();) {
			EIAttributeInst attr = (EIAttributeInst) it.next();
			result.addAll(attr.getChildren());
		}
		return result;
	}

	public void setAttribute(String parentName, EIAttributeInst attr)
			throws BPMException {
		if (parentName != null) {
			parentName = parentName.toUpperCase();
		}
		loadAttributes();
		EIAttributeInst parentAtt = null;

		if (parentName != null && !parentName.equals("")) {
			parentAtt = getAttribute(parentName);
			if (parentAtt == null) {
				if (parentName.indexOf(".") == -1) { // top level
					parentAtt = new DbAttributeInst();
					parentAtt.setName(parentName);
					parentAtt.setType(parentName);
					setAttribute(null, parentAtt);
				} else {
					// error: parentAtt not in this activity definition!
					throw new BPMException(
							"parentAtt not in this activity definition! parentAtt:"
									+ parentName + ", attName:"
									+ attr.getName());
				}
			}
			attr.setType(parentAtt.getType());
		}

		if (parentAtt != null) {
			// sub attribute
			EIAttributeInst oldAtt = (EIAttributeInst) parentAtt.getChild(attr
					.getName());
			if (oldAtt != null) { // exist same name attribute in this tree
				// 更新属性，并执行数据库删除操作
				removeAttribute(oldAtt);
				//return;
			}
			parentAtt.addChild(attr); // change the new attribute definition!
			attributeIdMap.put(attr.getId(), attr);
			// 执行数据库插入操作
			try {
				((DbActivityInstManager) EIActivityInstManager.getInstance())
						.addAttributeToDb(this, (DbAttributeInst) attr);
			} catch (BPMException e) {
				// 保存失败，将标记置为true
				_isAttributeModified = true;
			}
		} else {
			// top level add to top map
			attributeTopMap.put(attr.getName(), attr);
		}
	}

	/**
	 * 
	 */
	private void loadAttributes() throws BPMException {
		// load first
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbActivityInstManager manager = (DbActivityInstManager) EIActivityInstManager
					.getInstance();

			manager.loadAttribute(this);
		}
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_ACTIVITYINSTANCE] "
				+ "\n - BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID = "
				+ (activityInstId_is_initialized ? ("["
						+ activityInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.PROCESSINST_ID = "
				+ (processInstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.ACTIVITYDEF_ID = "
				+ (activityDefId_is_initialized ? ("["
						+ activityDefId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.PROCESSDEF_ID = "
				+ (processDefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.ACTIVITYINST_STATE = "
				+ (activityinstState_is_initialized ? ("[" + state.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.URGENCYTYPE = "
				+ (urgency_is_initialized ? ("[" + urgency.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.ARRIVEDTIME = "
				+ (arrivedTime_is_initialized ? ("[" + arrivedTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.LIMITTIME = "
				+ (limitTime_is_initialized ? ("[" + limitTime + "]")
						: "not initialized")
						+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.ALERTTIME = "
				+ (alertTime_is_initialized ? ("[" + alertTime + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.STARTTIME = "
				+ (startTime_is_initialized ? ("[" + startTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.RECEIVEMETHOD = "
				+ (recieveState_is_initialized ? ("[" + recieveState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.DEALMETHOD = "
				+ (dealState_is_initialized ? ("[" + dealState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYINSTANCE.RUNSTATUS = "
				+ (runState_is_initialized ? ("[" + runState.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {
		int size = 0;
		size += CacheSizes.sizeOfString(activityDefId);
		size += CacheSizes.sizeOfString(activityInstId);
		if (arrivedTime != null) {
			size += CacheSizes.sizeOfDate(); // arrivedTime
		}
		if (limitTime != null) {
			size += CacheSizes.sizeOfInt(); // limitTime
		}
		if (alertTime != null) {
			size += CacheSizes.sizeOfInt(); // alertTime
		}
		if (startTime != null) {
			size += CacheSizes.sizeOfDate(); // startTime
		}
		size += CacheSizes.sizeOfString(dealState);
		size += CacheSizes.sizeOfString(processDefId);
		size += CacheSizes.sizeOfString(processInstId);
		size += CacheSizes.sizeOfString(recieveState);
		size += CacheSizes.sizeOfString(runState);
		size += CacheSizes.sizeOfString(state);
		size += CacheSizes.sizeOfString(urgency);

		return size;
	}

	public boolean equals(Object o) {
		String uuid;
		if (o instanceof java.lang.String) {
			uuid = (String) o;
		} else if (o instanceof DbActivityInst) {
			uuid = ((DbActivityInst) o).activityInstId;
		} else {
			return false;
		}
		return uuid.equalsIgnoreCase(this.activityInstId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#getCanTakeBack()
	 */
	public String getCanTakeBack() {
		return canTakeBack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#setCanTakeBack(java.lang.String)
	 */
	public void setCanTakeBack(String newVal) {
		if ((newVal != null && newVal.equals(this.canTakeBack) == true)
				|| (newVal == null && this.canTakeBack == null))
			return;
		this.canTakeBack = newVal;
		cantakeback_is_modified = true;
		cantakeback_is_initialized = true;
	}

	/**
	 * Determine if the cantakeback is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCantakebackModified() {
		return cantakeback_is_modified;
	}

	/**
	 * Determine if the cantakeback has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCantakebackInitialized() {
		return cantakeback_is_initialized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#getActivityDef()
	 */
	public EIActivityDef getActivityDef() throws BPMException {
		return EIActivityDefManager.getInstance().loadByKey(activityDefId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#getProcessDef()
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException {
		return EIProcessDefVersionManager.getInstance().getActiveProcessDefVersion(this.getProcessDefId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#getProcessInst()
	 */
	public EIProcessInst getProcessInst() throws BPMException {
		return EIProcessInstManager.getInstance().loadByKey(processInstId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInst#removeAttribute(net.ooder.bpm.engine.inter.EIAttributeInst)
	 */
	public void removeAttribute(EIAttributeInst attr) throws BPMException {
		List children = attr.getChildren();
		// 如果没有子属性，则直接从map及数据库中删除
		if (children == null || children.size() == 0) {
			attributeIdMap.remove(attr.getId()); // remove it from all
													// attribute map
			try {
				// 执行数据库删除操作
				((DbActivityInstManager) EIActivityInstManager.getInstance())
						.removeAttributeFromDb((DbAttributeInst) attr);
			} catch (BPMException e) {
				// 保存失败，将标记置为true
				_isAttributeModified = true;
				throw new BPMException("", e);
			}
		} else {
			// 如果有子属性，则递归删除子属性
			for (Iterator iter = children.iterator(); iter.hasNext();) {
				removeAttribute((EIAttributeInst) iter.next());
			}
			// 然后将attr的子属性清除
			attr.clearChild();
			// 再删除自己
			removeAttribute(attr);
		}
	}

	/**
	 * @param attr
	 */
	private void updateAttribute(EIAttributeInst attr) throws BPMException {
		attributeIdMap.put(attr.getId(), attr);
		try {
			// 执行数据库删除操作
			((DbActivityInstManager) EIActivityInstManager.getInstance())
					.updateAttributeToDb(this, (DbAttributeInst) attr);
		} catch (BPMException e) {
			// 保存失败，将标记置为true
			_isAttributeModified = true;
			throw new BPMException("error occured while removing attribute.", e);
		}
	}

	
}


