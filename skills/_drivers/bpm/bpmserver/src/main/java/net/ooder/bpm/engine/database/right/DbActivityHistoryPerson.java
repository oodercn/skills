/**
 * $RCSfile: DbActivityHistoryPerson.java,v $
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

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动历史相关人员
 * </p>
 * <p>
 * The java object mapped on the Relation database table RT_ACTIVITYHISTORY_PERSON
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
public class DbActivityHistoryPerson implements Serializable {
    private String activityInstHistoryId;

    private boolean activityinstHistoryId_is_modified = false;

    private boolean activityinstHistoryId_is_initialized = false;

    private String processInstId;

    private boolean processinstId_is_modified = false;

    private boolean processinstId_is_initialized = false;

    private String personId;

    private boolean personId_is_modified = false;

    private boolean personId_is_initialized = false;

    private String personName;

    private boolean personname_is_modified = false;

    private boolean personname_is_initialized = false;

    private String rightGrpCode;

    private boolean rightGrpCode_is_modified = false;

    private boolean rightGrpCode_is_initialized = false;

    private boolean _isNew = true;

    /**
     */
    DbActivityHistoryPerson() {
    }

    /**
     * Getter method for activityinstHistoryId
     * 
     * @return the value of activityinstHistoryId
     */
    public String getActivityInstHistoryId() {
	return activityInstHistoryId;
    }

    /**
     * Setter method for activityinstHistoryId
     * 
     * @param newVal
     *            The new value to be assigned to activityinstHistoryId
     */
    public void setActivityInstHistoryId(String newVal) {
	if ((newVal != null && newVal.equals(this.activityInstHistoryId) == true) || (newVal == null && this.activityInstHistoryId == null))
	    return;
	this.activityInstHistoryId = newVal;
	activityinstHistoryId_is_modified = true;
	activityinstHistoryId_is_initialized = true;
    }

    /**
     * Determine if the activityinstHistoryId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isActivityinstHistoryIdModified() {
	return activityinstHistoryId_is_modified;
    }

    /**
     * Determine if the activityinstHistoryId has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isActivityinstHistoryIdInitialized() {
	return activityinstHistoryId_is_initialized;
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
	if ((newVal != null && newVal.equals(this.processInstId) == true) || (newVal == null && this.processInstId == null))
	    return;
	this.processInstId = newVal;
	processinstId_is_modified = true;
	processinstId_is_initialized = true;
    }

    /**
     * Determine if the processinstId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
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
	if ((newVal != null && newVal.equals(this.personId) == true) || (newVal == null && this.personId == null))
	    return;
	this.personId = newVal;
	personId_is_modified = true;
	personId_is_initialized = true;
    }

    /**
     * Determine if the personId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
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
     * Getter method for personname
     * 
     * @return the value of personname
     */
    public String getPersonName() {
	return personName;
    }

    /**
     * Setter method for personname
     * 
     * @param newVal
     *            The new value to be assigned to personname
     */
    public void setPersonName(String newVal) {
	if ((newVal != null && newVal.equals(this.personName) == true) || (newVal == null && this.personName == null))
	    return;
	this.personName = newVal;
	personname_is_modified = true;
	personname_is_initialized = true;
    }

    /**
     * Determine if the personname is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isPersonnameModified() {
	return personname_is_modified;
    }

    /**
     * Determine if the personname has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isPersonnameInitialized() {
	return personname_is_initialized;
    }

    /**
     * Getter method for rightGrpCode
     * 
     * @return the value of rightGrpCode
     */
    public RightGroupEnums getRightGrpCode() {
	return RightGroupEnums.fromType(rightGrpCode);
    }
    
    public String getRightGrpCodeStr() {
	return rightGrpCode;
    }

    public void setRightGrpCodeStr(String newVal) {
	if ((newVal != null && newVal.equals(this.rightGrpCode) == true) || (newVal == null && this.rightGrpCode == null))
	    return;
	this.rightGrpCode = newVal;
	rightGrpCode_is_modified = true;
	rightGrpCode_is_initialized = true;
    }

    /**
     * Setter method for rightGrpCode
     * 
     * @param newVal
     *            The new value to be assigned to rightGrpCode
     */
    public void setRightGrpCode(RightGroupEnums groups) {

	String newVal = groups.getType();
	setRightGrpCodeStr(newVal);
    }

    /**
     * Determine if the rightGrpCode is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
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
     * Determine if the object has been modified since the last time this method was called or since the creation of the
     * object
     * 
     * @return true if the object has been modified, false if the object has not been modified
     */
    public boolean isModified() {
	return activityinstHistoryId_is_modified || processinstId_is_modified || personId_is_modified || personname_is_modified || rightGrpCode_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
	activityinstHistoryId_is_modified = false;
	processinstId_is_modified = false;
	personId_is_modified = false;
	personname_is_modified = false;
	rightGrpCode_is_modified = false;
    }

    /**
     * Copy the passed bean into the current bean
     * 
     * @param bean
     *            the bean to copy into the current bean
     */
    public void copy(DbActivityHistoryPerson bean) {
	setActivityInstHistoryId(bean.getActivityInstHistoryId());
	setProcessInstId(bean.getProcessInstId());
	setPersonId(bean.getPersonId());
	setPersonName(bean.getPersonName());
	setRightGrpCode(bean.getRightGrpCode());
    }

    /**
     * Return the object string representation
     * 
     * @return the object as a string
     */
    public String toString() {
	return "\n[RT_ACTIVITYHISTORY_PERSON] " + "\n - RT_ACTIVITYHISTORY_PERSON.ACTIVITYINST_HISTORY_ID = " + (activityinstHistoryId_is_initialized ? ("[" + activityInstHistoryId.toString() + "]") : "not initialized") + ""
		+ "\n - RT_ACTIVITYHISTORY_PERSON.PROCESSINST_ID = " + (processinstId_is_initialized ? ("[" + processInstId.toString() + "]") : "not initialized") + "" + "\n - RT_ACTIVITYHISTORY_PERSON.PERSON_ID = "
		+ (personId_is_initialized ? ("[" + personId.toString() + "]") : "not initialized") + "" + "\n - RT_ACTIVITYHISTORY_PERSON.PERSONNAME = "
		+ (personname_is_initialized ? ("[" + personName.toString() + "]") : "not initialized") + "" + "\n - RT_ACTIVITYHISTORY_PERSON.RIGHT_GRP_CODE = "
		+ (rightGrpCode_is_initialized ? ("[" + rightGrpCode.toString() + "]") : "not initialized") + "";
    }

}

