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
package net.ooder.bpm.engine.database.event;

import java.io.Serializable;

import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 榛樿鏉冮檺涓殑娲诲姩瀹炰緥涓庤澶囧簲鐢ㄥ叧绯?
 * </p>
 * <p>
 * The java object mapped on the Relation database table DT_ACTIVITY_DEVICE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @version 1.0
 */
public class DbActivityInstEvent implements Serializable {

  

    private String activityInsteEndPointId;

    private boolean activityinstEndPointId_is_modified = false;

    private boolean activityinstEndPointId_is_initialized = false;

  

    private String processInstId;

    private boolean processinstId_is_modified = false;

    private boolean processinstId_is_initialized = false;

    private String activityInstId;

    private boolean activityinstId_is_modified = false;

    private boolean activityinstId_is_initialized = false;

    private String endPointId;

    private boolean endPointId_is_modified = false;

    private boolean endPointId_is_initialized = false;
    
    
    private String personId;

    private boolean personId_is_modified = false;

    private boolean personId_is_initialized = false;

    private String endPointName;

    private boolean endPointName_is_modified = false;

    private boolean endPointName_is_initialized = false;

    private String serviceId;

    private boolean serviceId_is_modified = false;

    private boolean serviceId_is_initialized = false;

    private String eventGrpCode;

    private boolean eventGrpCode_is_modified = false;

    private boolean eventGrpCode_is_initialized = false;

    private String eventActivityState;

    private boolean eventActivityState_is_modified = false;

    private boolean eventActivityState_is_initialized = false;

    private String lastEventGrp;

    private boolean lastEventGrp_is_modified = false;

    private boolean lastEventGrp_is_initialized = false;

    private boolean _isNew = true;

    /**
     */
    DbActivityInstEvent() {
    }

    /**
     * Getter method for activityinstEndPointId
     * 
     * @return the value of activityinstEndPointId
     */
    public String getActivityInstEndPointId() {
	return activityInsteEndPointId;
    }

    /**
     * Setter method for activityinstEndPointId
     * 
     * @param newVal
     *            The new value to be assigned to activityinstEndPointId
     */
    public void setActivityInstEndPointId(String newVal) {
	if ((newVal != null && newVal.equals(this.activityInsteEndPointId) == true) || (newVal == null && this.activityInsteEndPointId == null))
	    return;
	this.activityInsteEndPointId = newVal;
	activityinstEndPointId_is_modified = true;
	activityinstEndPointId_is_initialized = true;
    }

    public boolean isActivityinstEventIdModified() {
	return activityinstEndPointId_is_modified;
    }

    public boolean isActivityinstEventIdInitialized() {
	return activityinstEndPointId_is_initialized;
    }

    public String getPersonId() {
	return personId;
    }

    public void setPersonId(String newVal) {
	if ((newVal != null && newVal.equals(this.personId) == true) || (newVal == null && this.personId == null))
	    return;
	this.personId = newVal;
	personId_is_modified = true;
	personId_is_initialized = true;
    }

    public boolean isPersonIdModified() {
	return personId_is_modified;
    }

    public boolean isPersonIdInitialized() {
	return personId_is_initialized;
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
	if ((newVal != null && newVal.equals(this.activityInstId) == true) || (newVal == null && this.activityInstId == null))
	    return;
	this.activityInstId = newVal;
	activityinstId_is_modified = true;
	activityinstId_is_initialized = true;
    }

    /**
     * Determine if the activityinstId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
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
     * Getter method for activityinstId
     * 
     * @return the value of activityinstId
     */
    public String getServiceId() {
	return serviceId;
    }

    /**
     * Setter method for activityinstId
     * 
     * @param newVal
     *            The new value to be assigned to activityinstId
     */
    public void setServiceId(String newVal) {
	if ((newVal != null && newVal.equals(this.serviceId) == true) || (newVal == null && this.serviceId == null))
	    return;
	this.serviceId = newVal;
	serviceId_is_modified = true;
	serviceId_is_initialized = true;
    }

    /**
     * Determine if the activityinstId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isServiceIdModified() {
	return serviceId_is_modified;
    }

    /**
     * Determine if the activityinstId has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isServiceIdInitialized() {
	return serviceId_is_initialized;
    }

    /**
     * Getter method for endPointId
     * 
     * @return the value of endPointId
     */
    public String getEndPointId() {
	return endPointId;
    }

    /**
     * Setter method for endPointId
     * 
     * @param newVal
     *            The new value to be assigned to endPointId
     */
    public void setEndPointId(String newVal) {
	if ((newVal != null && newVal.equals(this.endPointId) == true) || (newVal == null && this.endPointId == null))
	    return;
	this.endPointId = newVal;
	endPointId_is_modified = true;
	endPointId_is_initialized = true;
    }

    /**
     * Determine if the endPointId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isEndPointIdModified() {
	return endPointId_is_modified;
    }

    /**
     * Determine if the endPointId has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isEndPointIdInitialized() {
	return endPointId_is_initialized;
    }

    /**
     * Getter method for endPointName
     * 
     * @return the value of endPointName
     */
    public String getEndPointName() {
	return endPointName;
    }

    /**
     * Setter method for endPointName
     * 
     * @param newVal
     *            The new value to be assigned to endPointName
     */
    public void setEndPointName(String newVal) {
	if ((newVal != null && newVal.equals(this.endPointName) == true) || (newVal == null && this.endPointName == null))
	    return;
	this.endPointName = newVal;
	endPointName_is_modified = true;
	endPointName_is_initialized = true;
    }

    /**
     * Determine if the endPointName is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isEndPointNameModified() {
	return endPointName_is_modified;
    }

    /**
     * Determine if the endPointName has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isEndPointNameInitialized() {
	return endPointName_is_initialized;
    }

    /**
     * Getter method for eventGrpCode
     * 
     * @return the value of eventGrpCode
     */
    public RightGroupEnums getEventGrpCode() {
	return RightGroupEnums.fromType(eventGrpCode);
    }

    /**
     * Setter method for eventGrpCode
     * 
     * @param newVal
     *            The new value to be assigned to eventGrpCode
     */
    public void setEventGrpCode(RightGroupEnums group) {
	String newVal=group.getType();
	if ((newVal != null && newVal.equals(this.eventGrpCode) == true) || (newVal == null && this.eventGrpCode == null))
	    return;
	this.eventGrpCode = newVal;
	eventGrpCode_is_modified = true;
	eventGrpCode_is_initialized = true;
    }

    /**
     * Determine if the eventGrpCode is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isEventGrpCodeModified() {
	return eventGrpCode_is_modified;
    }

    /**
     * Determine if the eventGrpCode has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isEventGrpCodeInitialized() {
	return eventGrpCode_is_initialized;
    }

    /**
     * Getter method for eventActivityState
     * 
     * @return the value of eventActivityState
     */
    public RightPerformStatus getEventActivityState() {
	return RightPerformStatus.fromType(eventActivityState);
    }

    /**
     * Setter method for eventActivityState
     * 
     * @param newVal
     *            The new value to be assigned to eventActivityState
     */
    public void setEventActivityState(RightPerformStatus state) {
	String newVal=state.getType();
	if ((newVal != null && newVal.equals(this.eventActivityState) == true) || (newVal == null && this.eventActivityState == null))
	    return;
	this.eventActivityState = newVal;
	eventActivityState_is_modified = true;
	eventActivityState_is_initialized = true;
    }

    /**
     * Determine if the eventActivityState is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isEventActivityStateModified() {
	return eventActivityState_is_modified;
    }

    /**
     * Determine if the eventActivityState has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isEventActivityStateInitialized() {
	return eventActivityState_is_initialized;
    }

    /**
     * Getter method for lastEventGrp
     * 
     * @return the value of lastEventGrp
     */
    public RightGroupEnums getLastEventGrp() {
	return RightGroupEnums.fromType(lastEventGrp);
    }

    /**
     * Setter method for lastEventGrp
     * 
     * @param newVal
     *            The new value to be assigned to lastEventGrp
     */
    public void setLastEventGrp(RightGroupEnums group) {
	String newVal=group.getType();
	if ((newVal != null && newVal.equals(this.lastEventGrp) == true) || (newVal == null && this.lastEventGrp == null))
	    return;
	this.lastEventGrp = newVal;
	lastEventGrp_is_modified = true;
	lastEventGrp_is_initialized = true;
    }

    /**
     * Determine if the lastEventGrp is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isLastEventGrpModified() {
	return lastEventGrp_is_modified;
    }

    /**
     * Determine if the lastEventGrp has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isLastEventGrpInitialized() {
	return lastEventGrp_is_initialized;
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
	return activityinstEndPointId_is_modified || processinstId_is_modified || activityinstId_is_modified || endPointId_is_modified || personId_is_modified || endPointName_is_modified || eventGrpCode_is_modified || eventActivityState_is_modified
		|| lastEventGrp_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
	activityinstEndPointId_is_modified = false;
	processinstId_is_modified = false;
	activityinstId_is_modified = false;
	endPointId_is_modified = false;
	personId_is_modified = false;

	endPointName_is_modified = false;
	eventGrpCode_is_modified = false;
	eventActivityState_is_modified = false;
	lastEventGrp_is_modified = false;
    }

    /**
     * Copy the passed bean into the current bean
     * 
     * @param bean
     *            the bean to copy into the current bean
     */
    public void copy(DbActivityInstEvent bean) {
	setActivityInstEndPointId(bean.getActivityInstEndPointId());
	setProcessInstId(bean.getProcessInstId());
	setActivityInstId(bean.getActivityInstId());
	setEndPointId(bean.getEndPointId());
	setEndPointName(bean.getEndPointName());
	setEventGrpCode(bean.getEventGrpCode());
	setEventActivityState(bean.getEventActivityState());
	setLastEventGrp(bean.getLastEventGrp());
	setPersonId(bean.getPersonId());
    }

    /**
     * Return the object string representation
     * 
     * @return the object as a string
     */
    public String toString() {
	return "\n[DT_ACTIVITY_DEVICE] " + "\n - DT_ACTIVITY_DEVICE.ACTIVITYINST_ENDPOINT_ID = " + (activityinstEndPointId_is_initialized ? ("[" + activityInsteEndPointId.toString() + "]") : "not initialized") + ""
		+ "\n - DT_ACTIVITY_DEVICE.PROCESSINST_ID = " + (processinstId_is_initialized ? ("[" + processInstId.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.ACTIVITYINST_ID = "
		+ (activityinstId_is_initialized ? ("[" + activityInstId.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.ENDPOINT_ID = "
		+ (endPointId_is_initialized ? ("[" + endPointId.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.ENDPOINT_NAME = "
		+ (endPointName_is_initialized ? ("[" + endPointName.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.DEVICE_GRP_CODE = "
		+ (eventGrpCode_is_initialized ? ("[" + eventGrpCode.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.DEVICE_ACTIVITY_STATE = "
		+ (eventActivityState_is_initialized ? ("[" + eventActivityState.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_DEVICE.LAST_DEVICE_GRP = "
		+ (lastEventGrp_is_initialized ? ("[" + lastEventGrp.toString() + "]") : "not initialized") + "";
    }

}

