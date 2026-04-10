/**
 * $RCSfile: DbActivityInstPerson.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.right;

import java.io.Serializable;

import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 榛樿鏉冮檺涓殑娲诲姩瀹炰緥涓庝汉鍛樺叧绯?
 * </p>
 * <p>
 * The java object mapped on the Relation database table RT_ACTIVITY_PERSON
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
public class DbActivityInstPerson implements Serializable {
	

	private String activityInstPersonId;

	private boolean activityinstPersonId_is_modified = false;

	private boolean activityinstPersonId_is_initialized = false;

	private String processInstId;

	private boolean processinstId_is_modified = false;

	private boolean processinstId_is_initialized = false;

	private String activityInstId;

	private boolean activityinstId_is_modified = false;

	private boolean activityinstId_is_initialized = false;

	private String personId;

	private boolean personId_is_modified = false;

	private boolean personId_is_initialized = false;

	private String personName;

	private boolean personName_is_modified = false;

	private boolean personName_is_initialized = false;

	private String rightGrpCode;

	private boolean rightGrpCode_is_modified = false;

	private boolean rightGrpCode_is_initialized = false;

	private String personActivityState;

	private boolean personActivityState_is_modified = false;

	private boolean personActivityState_is_initialized = false;

	private String lastRightGrp;

	private boolean lastRightGrp_is_modified = false;

	private boolean lastRightGrp_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbActivityInstPerson() {
	}

	/**
	 * Getter method for activityinstPersonId
	 * 
	 * @return the value of activityinstPersonId
	 */
	public String getActivityInstPersonId() {
		return activityInstPersonId;
	}

	/**
	 * Setter method for activityinstPersonId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstPersonId
	 */
	public void setActivityInstPersonId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityInstPersonId) == true)
				|| (newVal == null && this.activityInstPersonId == null))
			return;
		this.activityInstPersonId = newVal;
		activityinstPersonId_is_modified = true;
		activityinstPersonId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstPersonId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstPersonIdModified() {
		return activityinstPersonId_is_modified;
	}

	/**
	 * Determine if the activityinstPersonId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstPersonIdInitialized() {
		return activityinstPersonId_is_initialized;
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
		processinstId_is_modified = true;
		processinstId_is_initialized = true;
	}

	/**
	 * Determine if the processinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstIdModified() {
		return processinstId_is_modified;
	}

	/**
	 * Determine if the processinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstIdInitialized() {
		return processinstId_is_initialized;
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
		activityinstId_is_modified = true;
		activityinstId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstIdModified() {
		return activityinstId_is_modified;
	}

	/**
	 * Determine if the activityinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstIdInitialized() {
		return activityinstId_is_initialized;
	}

	/**
	 * Getter method for personId
	 * 
	 * @return the value of personId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * Setter method for personId
	 * 
	 * @param newVal
	 *            The new value to be assigned to personId
	 */
	public void setPersonId(String newVal) {
		if ((newVal != null && newVal.equals(this.personId) == true)
				|| (newVal == null && this.personId == null))
			return;
		this.personId = newVal;
		personId_is_modified = true;
		personId_is_initialized = true;
	}

	/**
	 * Determine if the personId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isPersonIdModified() {
		return personId_is_modified;
	}

	/**
	 * Determine if the personId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isPersonIdInitialized() {
		return personId_is_initialized;
	}

	/**
	 * Getter method for personName
	 * 
	 * @return the value of personName
	 */
	public String getPersonName() {
		return personName;
	}

	/**
	 * Setter method for personName
	 * 
	 * @param newVal
	 *            The new value to be assigned to personName
	 */
	public void setPersonName(String newVal) {
		if ((newVal != null && newVal.equals(this.personName) == true)
				|| (newVal == null && this.personName == null))
			return;
		this.personName = newVal;
		personName_is_modified = true;
		personName_is_initialized = true;
	}

	/**
	 * Determine if the personName is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isPersonNameModified() {
		return personName_is_modified;
	}

	/**
	 * Determine if the personName has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isPersonNameInitialized() {
		return personName_is_initialized;
	}

	/**
	 * Getter method for rightGrpCode
	 * 
	 * @return the value of rightGrpCode
	 */
	public RightGroupEnums getRightGrpCode() {
		return RightGroupEnums.fromType(rightGrpCode);
	}

	/**
	 * Setter method for rightGrpCode
	 * 
	 * @param newVal
	 *            The new value to be assigned to rightGrpCode
	 */
	public void setRightGrpCode(RightGroupEnums group) {
	  //  String newVal=group.getType();
		if ((group != null && group.getType().equals(this.rightGrpCode) == true)
				|| (group == null && this.rightGrpCode == null))
			return;
		this.rightGrpCode = group.getType();
		rightGrpCode_is_modified = true;
		rightGrpCode_is_initialized = true;
	}

	/**
	 * Determine if the rightGrpCode is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRightGrpCodeModified() {
		return rightGrpCode_is_modified;
	}

	/**
	 * Determine if the rightGrpCode has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRightGrpCodeInitialized() {
		return rightGrpCode_is_initialized;
	}

	/**
	 * Getter method for personActivityState
	 * 
	 * @return the value of personActivityState
	 */
	public RightPerformStatus getPersonActivityState() {
	    
		return RightPerformStatus.fromType(personActivityState);
	}

	/**
	 * Setter method for personActivityState
	 * 
	 * @param newVal
	 *            The new value to be assigned to personActivityState
	 */
	public void setPersonActivityState(RightPerformStatus status) {
	    
		    
		if ((status != null && status.getType().equals(this.personActivityState) == true)
				|| (status == null && this.personActivityState == null))
			return;
		this.personActivityState = status.getType();
		personActivityState_is_modified = true;
		personActivityState_is_initialized = true;
	}

	/**
	 * Determine if the personActivityState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isPersonActivityStateModified() {
		return personActivityState_is_modified;
	}

	/**
	 * Determine if the personActivityState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isPersonActivityStateInitialized() {
		return personActivityState_is_initialized;
	}

	/**
	 * Getter method for lastRightGrp
	 * 
	 * @return the value of lastRightGrp
	 */
	public RightGroupEnums getLastRightGrp() {
		return RightGroupEnums.fromType(lastRightGrp);
	}

	/**
	 * Setter method for lastRightGrp
	 * 
	 * @param newVal
	 *            The new value to be assigned to lastRightGrp
	 */
	public void setLastRightGrp(RightGroupEnums group) {
	
		if ((group != null && group.getType().equals(this.lastRightGrp) == true)
				|| (group == null && this.lastRightGrp == null))
			return;
		this.lastRightGrp = group.getType();
		lastRightGrp_is_modified = true;
		lastRightGrp_is_initialized = true;
	}

	/**
	 * Determine if the lastRightGrp is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isLastRightGrpModified() {
		return lastRightGrp_is_modified;
	}

	/**
	 * Determine if the lastRightGrp has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isLastRightGrpInitialized() {
		return lastRightGrp_is_initialized;
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
		return activityinstPersonId_is_modified || processinstId_is_modified
				|| activityinstId_is_modified || personId_is_modified
				|| personName_is_modified || rightGrpCode_is_modified
				|| personActivityState_is_modified || lastRightGrp_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		activityinstPersonId_is_modified = false;
		processinstId_is_modified = false;
		activityinstId_is_modified = false;
		personId_is_modified = false;
		personName_is_modified = false;
		rightGrpCode_is_modified = false;
		personActivityState_is_modified = false;
		lastRightGrp_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbActivityInstPerson bean) {
		setActivityInstPersonId(bean.getActivityInstPersonId());
		setProcessInstId(bean.getProcessInstId());
		setActivityInstId(bean.getActivityInstId());
		setPersonId(bean.getPersonId());
		setPersonName(bean.getPersonName());
		setRightGrpCode(bean.getRightGrpCode());
		setPersonActivityState(bean.getPersonActivityState());
		setLastRightGrp(bean.getLastRightGrp());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[RT_ACTIVITY_PERSON] "
				+ "\n - RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID = "
				+ (activityinstPersonId_is_initialized ? ("["
						+ activityInstPersonId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.PROCESSINST_ID = "
				+ (processinstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.ACTIVITYINST_ID = "
				+ (activityinstId_is_initialized ? ("["
						+ activityInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.PERSON_ID = "
				+ (personId_is_initialized ? ("[" + personId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.PERSON_NAME = "
				+ (personName_is_initialized ? ("[" + personName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.RIGHT_GRP_CODE = "
				+ (rightGrpCode_is_initialized ? ("[" + rightGrpCode.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.PERSON_ACTIVITY_STATE = "
				+ (personActivityState_is_initialized ? ("["
						+ personActivityState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITY_PERSON.LAST_RIGHT_GRP = "
				+ (lastRightGrp_is_initialized ? ("[" + lastRightGrp.toString() + "]")
						: "not initialized") + "";
	}

}

