/**
 * $RCSfile: DbProcessInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityInstHistoryManager;
import net.ooder.bpm.engine.inter.EIActivityInstManager;
import net.ooder.bpm.engine.inter.EIAttributeInst;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例接口的数据库实现
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_PROCESSINSTANCE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li, chenjie
 * @version 1.0
 */
public class DbProcessInst implements EIProcessInst, Cacheable, Serializable {

	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessInst.class);

	private String processInstId;

	private boolean processInstId_is_modified = false;

	private boolean processInstId_is_initialized = false;

	private String processDefId;

	private boolean processDefId_is_modified = false;

	private boolean processDefId_is_initialized = false;

	private String processDefVersionId;

	private boolean processDefVersionId_is_modified = false;

	private boolean processDefVersionId_is_initialized = false;

	private String name;

	private boolean name_is_modified = false;

	private boolean name_is_initialized = false;

	private String urgency;

	private boolean urgency_is_modified = false;

	private boolean urgency_is_initialized = false;

	private String state;

	private boolean state_is_modified = false;

	private boolean state_is_initialized = false;

	private int copyNumber;

	private boolean copyNumber_is_modified = false;

	private boolean copyNumber_is_initialized = false;

	private Date startTime;

	private boolean startTime_is_modified = false;

	private boolean startTime_is_initialized = false;

	private Date limitTime;

	private boolean limitTime_is_modified = false;

	private boolean limitTime_is_initialized = false;

	private Date endTime;

	private boolean endTime_is_modified = false;

	private boolean endTime_is_initialized = false;

	private String runState;

	private boolean runState_is_modified = false;

	private boolean runState_is_initialized = false;

	private boolean _isNew = true;

	private boolean _isAttributeModified = true;

	Map<String,Object> attributeTopMap = null; // store the top level attribute

	Map<String,Object> attributeIdMap = null; // store all attribute in this activity
								// definition

	/**
	 */
	DbProcessInst() {
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
	 * Getter method for processdefId
	 * 
	 * @return the value of processdefId
	 */
	public String getProcessDefId() {
		return processDefId;
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
	 * Getter method for processdefVersionId
	 * 
	 * @return the value of processdefVersionId
	 */
	public String getProcessDefVersionId() {
		return processDefVersionId;
	}

	/**
	 * Setter method for processdefVersionId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefVersionId
	 */
	public void setProcessDefVersionId(String newVal) {
		if ((newVal != null && newVal.equals(this.processDefVersionId) == true)
				|| (newVal == null && this.processDefVersionId == null))
			return;
		this.processDefVersionId = newVal;
		processDefVersionId_is_modified = true;
		processDefVersionId_is_initialized = true;
	}

	/**
	 * Determine if the processdefVersionId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefVersionIdModified() {
		return processDefVersionId_is_modified;
	}

	/**
	 * Determine if the processdefVersionId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefVersionIdInitialized() {
		return processDefVersionId_is_initialized;
	}

	/**
	 * @return
	 */
	public boolean isAttributeModified() {
		if (attributeIdMap == null && attributeTopMap == null) {
			return false;
		}
		return processInstId_is_modified || _isAttributeModified;
	}

	/**
	 * Getter method for processinstName
	 * 
	 * @return the value of processinstName
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for processinstName
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstName
	 */
	public void setName(String newVal) {
		if ((newVal != null && newVal.equals(this.name) == true)
				|| (newVal == null && this.name == null))
			return;
		this.name = newVal;
		name_is_modified = true;
		name_is_initialized = true;
	}

	/**
	 * Determine if the processinstName is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstNameModified() {
		return name_is_modified;
	}

	/**
	 * Determine if the processinstName has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstNameInitialized() {
		return name_is_initialized;
	}

	/**
	 * Getter method for processinstUrgency
	 * 
	 * @return the value of processinstUrgency
	 */
	public String getUrgency() {
		return urgency;
	}

	/**
	 * Setter method for processinstUrgency
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstUrgency
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
	 * Determine if the processinstUrgency is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstUrgencyModified() {
		return urgency_is_modified;
	}

	/**
	 * Determine if the processinstUrgency has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstUrgencyInitialized() {
		return urgency_is_initialized;
	}

	/**
	 * Getter method for processinstState
	 * 
	 * @return the value of processinstState
	 */
	public String getState() {
		return state;
	}

	/**
	 * Setter method for processinstState
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstState
	 */
	public void setState(String newVal) {
		if ((newVal != null && newVal.equals(this.state) == true)
				|| (newVal == null && this.state == null))
			return;
		this.state = newVal;
		state_is_modified = true;
		state_is_initialized = true;
	}

	/**
	 * Determine if the processinstState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstStateModified() {
		return state_is_modified;
	}

	/**
	 * Determine if the processinstState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstStateInitialized() {
		return state_is_initialized;
	}

	/**
	 * Getter method for copynumber
	 * 
	 * @return the value of copynumber
	 */
	public int getCopyNumber() {
		return copyNumber;
	}

	/**
	 * Setter method for copynumber
	 * 
	 * @param newVal
	 *            The new value to be assigned to copynumber
	 */
	public void setCopyNumber(int newVal) {
		if (newVal == this.copyNumber)
			return;
		this.copyNumber = newVal;
		copyNumber_is_modified = true;
		copyNumber_is_initialized = true;
	}

	/**
	 * Determine if the copynumber is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCopynumberModified() {
		return copyNumber_is_modified;
	}

	/**
	 * Determine if the copynumber has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCopynumberInitialized() {
		return copyNumber_is_initialized;
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
	 * Getter method for timelimit
	 * 
	 * @return the value of timelimit
	 */
	public Date getLimitTime() {
		return limitTime;
	}

	/**
	 * Setter method for timelimit
	 * 
	 * @param newVal
	 *            The new value to be assigned to timelimit
	 */
	public void setLimitTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.limitTime) == true)
				|| (newVal == null && this.limitTime == null))
			return;
		this.limitTime = newVal;
		limitTime_is_modified = true;
		limitTime_is_initialized = true;
	}

	/**
	 * Determine if the timelimit is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isTimelimitModified() {
		return limitTime_is_modified;
	}

	/**
	 * Determine if the timelimit has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isTimelimitInitialized() {
		return limitTime_is_initialized;
	}

	/**
	 * Getter method for endtime
	 * 
	 * @return the value of endtime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Setter method for endtime
	 * 
	 * @param newVal
	 *            The new value to be assigned to endtime
	 */
	public void setEndTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.endTime) == true)
				|| (newVal == null && this.endTime == null))
			return;
		this.endTime = newVal;
		endTime_is_modified = true;
		endTime_is_initialized = true;
	}

	/**
	 * Determine if the endtime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isEndtimeModified() {
		return endTime_is_modified;
	}

	/**
	 * Determine if the endtime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isEndtimeInitialized() {
		return endTime_is_initialized;
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

	/*
	 * @see net.ooder.bpm.engine.process.EIProcessInst#getProcessDef()
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException {
		return EIProcessDefVersionManager.getInstance().loadByKey(
				processDefVersionId);
	}

	/**
	 * 取得当前流程实例对应的所有活动实例
	 * 
	 * @return 活动实例列表
	 * @throws BPMException
	 *             有异常发生
	 */
	public List getActivityInstList() throws BPMException {
		String where = " where PROCESSINST_ID='" + processInstId + "'";
		return EIActivityInstManager.getInstance().loadByWhere(where);
	}

	/**
	 * 取得当前流程实例对应的所有活动实例历史
	 * 
	 * @return 活动实例历史列表
	 * @throws BPMException
	 *             有异常发生
	 */
	public List getActivityInstHistoryList() throws BPMException {
		String where = " where PROCESSINST_ID='" + processInstId + "'";
		return EIActivityInstHistoryManager.getInstance().loadByWhere(where);
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
			log.error("get process instance attribute error!");
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
	 * 取得扩展属性值，此值已经经过解释（直接取得相应流程定义版本的扩展属性值）<br>
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
	 * 取得扩展属性值,此值是未经解析的原值，即数据库中储存的值<br>
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
	 * 取得所有扩展属性
	 * 
	 * @return 扩展属性实例列表
	 */
	public List getAllAttribute() {
		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("get process instance attribute error!");
			return new ArrayList(0);
		}
		return new ArrayList(attributeIdMap.values());
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
					// error: parentAtt not in this process instance!
					throw new BPMException(
							"parentAtt not in this process definition! parentAtt:"
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
			List children = null;
			// 该属性已经存在，更新属性
			if (oldAtt != null) {

				//修复更新错误的BUG
				removeAttribute(oldAtt);
				//attr.setId(oldAtt.getId());
				//updateAttribute(attr);
				//return;
			}
			// 为父属性增加子属性
			if (attr.getId() == null) {
				attr.setId(UUID.randomUUID().toString());
			}
			parentAtt.addChild(attr);
			attributeIdMap.put(attr.getId(), attr);
			// 执行数据库插入操作
			try {
				((DbProcessInstManager) EIProcessInstManager.getInstance())
						.addAttributeToDb(this, (DbAttributeInst) attr);
			} catch (BPMException e) {
				// 保存失败，将标记置为true
				_isAttributeModified = true;
				throw new BPMException(
						"error occured while saving attribute to db.", e);
			}

		} else {
			attributeTopMap.put(attr.getName(), attr);
		}

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
		return processInstId_is_modified || processDefId_is_modified
				|| processDefVersionId_is_modified || name_is_modified
				|| urgency_is_modified || state_is_modified
				|| copyNumber_is_modified || startTime_is_modified
				|| limitTime_is_modified || endTime_is_modified
				|| runState_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		processInstId_is_modified = false;
		processDefId_is_modified = false;
		processDefVersionId_is_modified = false;
		name_is_modified = false;
		urgency_is_modified = false;
		state_is_modified = false;
		copyNumber_is_modified = false;
		startTime_is_modified = false;
		limitTime_is_modified = false;
		endTime_is_modified = false;
		runState_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbProcessInst bean) {
		setProcessInstId(bean.getProcessInstId());
		setProcessDefId(bean.getProcessDefId());
		setProcessDefVersionId(bean.getProcessDefVersionId());
		setName(bean.getName());
		setUrgency(bean.getUrgency());
		setState(bean.getState());
		setCopyNumber(bean.getCopyNumber());
		setStartTime(bean.getStartTime());
		setLimitTime(bean.getLimitTime());
		setEndTime(bean.getEndTime());
		setRunStatus(bean.getRunStatus());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_PROCESSINSTANCE] "
				+ "\n - BPM_PROCESSINSTANCE.PROCESSINST_ID = "
				+ (processInstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.PROCESSDEF_ID = "
				+ (processDefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.PROCESSDEF_VERSION_ID = "
				+ (processDefVersionId_is_initialized ? ("["
						+ processDefVersionId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.PROCESSINST_NAME = "
				+ (name_is_initialized ? ("[" + name.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.PROCESSINST_URGENCY = "
				+ (urgency_is_initialized ? ("[" + urgency.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.PROCESSINST_STATE = "
				+ (state_is_initialized ? ("[" + state.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.COPYNUMBER = "
				+ (copyNumber_is_initialized ? ("[" + copyNumber + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.STARTTIME = "
				+ (startTime_is_initialized ? ("[" + startTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.LIMITTIME = "
				+ (limitTime_is_initialized ? ("[" + limitTime + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.ENDTIME = "
				+ (endTime_is_initialized ? ("[" + endTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSINSTANCE.RUNSTATUS = "
				+ (runState_is_initialized ? ("[" + runState.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {
		int size = 0;

		size += CacheSizes.sizeOfString(processInstId);
		size += CacheSizes.sizeOfString(processDefId);
		size += CacheSizes.sizeOfString(processDefVersionId);
		size += CacheSizes.sizeOfString(name);
		size += CacheSizes.sizeOfString(urgency);
		size += CacheSizes.sizeOfString(state);
		size += CacheSizes.sizeOfInt();
		if (startTime == null) {
			size += 4;
		} else {
			size += CacheSizes.sizeOfDate();
		}
		if (endTime == null) {
			size += 4;
		} else {
			size += CacheSizes.sizeOfDate();
		}
		size += CacheSizes.sizeOfInt();
		size += CacheSizes.sizeOfString(runState);

		return size;
	}

	public boolean equals(Object o) {
		String uuid;
		if (o instanceof java.lang.String) {
			uuid = (String) o;
		} else if (o instanceof DbProcessInst) {
			uuid = ((DbProcessInst) o).processInstId;
		} else {
			return false;
		}
		return uuid.equalsIgnoreCase(this.processInstId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessInst#getProcessDef()
	 */
	public EIProcessDef getProcessDef() throws BPMException {
		return EIProcessDefManager.getInstance().loadByKey(processDefId);
	}

	/*
	 * 删除流程实例属性，将递归删除其子属性。
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessInst#removeAttribute(net.ooder.bpm.engine.inter.EIAttributeInst)
	 */
	public void removeAttribute(EIAttributeInst attr) throws BPMException {
		List children = attr.getChildren();
		// 如果没有子属性，则直接从map及数据库中删除
		if (children == null || children.size() == 0) {
			attributeIdMap.remove(attr.getId()); // remove it from all
													// attribute map
			try {
				// 执行数据库删除操作
				((DbProcessInstManager) EIProcessInstManager.getInstance())
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
			((DbProcessInstManager) EIProcessInstManager.getInstance())
					.updateAttributeToDb(this, (DbAttributeInst) attr);
		} catch (BPMException e) {
			// 保存失败，将标记置为true
			_isAttributeModified = true;
			throw new BPMException("error occured while removing attribute.", e);
		}
	}

	/*
	 * private void removeSingleAttribute(EIAttributeInst attr) throws
	 * BPMException { attributeIdMap.remove(attr.getId()); //remove it from all
	 * attribute map try { //执行数据库删除操作 ( (DbProcessInstManager)
	 * EIProcessInstManager .getInstance()) .removeAttributeFromDb(
	 * (DbAttributeInst)attr); } catch (BPMException e) { //保存失败，将标记置为true
	 * _isAttributeModified = true; throw new BPMException("error occured while
	 * removing attribute." , e); } }
	 */
	/*
	 * 测试属性实例的增加删除。
	 */
	public static void main(String[] args) throws BPMException {
		DbProcessInst inst = new DbProcessInst();
		DbAttributeInst attr = new DbAttributeInst();
//		attr.setName("parent");
//		attr.setValue("parent");
//		attr.setId(UUID.randomUUID().toString());
//		inst.setAttribute("", attr);
//
//		DbAttributeInst attr1 = new DbAttributeInst();
//		attr1.setName("level1_1");
//		attr1.setValue("level1_1");
//		String id = UUID.randomUUID().toString();
//		attr1.setId(id);
//		inst.setAttribute("parent", attr1);
//
//		DbAttributeInst attr2 = new DbAttributeInst();
//		attr2.setName("level1_2");
//		attr2.setValue("level1_2");
//		attr2.setId(UUID.randomUUID().toString());
//		inst.setAttribute("parent", attr2);
//
//		attr = new DbAttributeInst();
//		attr.setName("level2_1");
//		attr.setValue("level2_1");
//		attr.setId(UUID.randomUUID().toString());
//		inst.setAttribute("parent.level1_1", attr);
//
//		attr = new DbAttributeInst();
//		attr.setName("level2_2");
//		attr.setValue("level2_2");
//		attr.setId(UUID.randomUUID().toString());
//		inst.setAttribute("parent.level1_1", attr);
//
//		attr1.setInterpretedValue("+++++++++_-------");
//		inst.setAttribute("parent", attr1);
//		inst.removeAttribute(attr1);
//		inst.removeAttribute(attr2);
	}

	private void loadAttributes() throws BPMException {
		// load first
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbProcessInstManager manager = (DbProcessInstManager) EIProcessInstManager
					.getInstance();

			manager.loadAttribute(this);
		}
	}

	

}


