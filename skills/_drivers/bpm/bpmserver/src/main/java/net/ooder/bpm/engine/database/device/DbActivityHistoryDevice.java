
package net.ooder.bpm.engine.database.device;

import java.io.Serializable;

import net.ooder.bpm.enums.right.RightGroupEnums;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动历史相关设备
 * </p>
 * <p>
 * The java object mapped on the Relation database table
 * RT_ACTIVITYHISTORY_DEVICE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author 文章
 * @version 1.0
 */
public class DbActivityHistoryDevice implements Serializable {
	private String activityInstHistoryId;

	private boolean activityinstHistoryId_is_modified = false;

	private boolean activityinstHistoryId_is_initialized = false;

	private String processInstId;

	private boolean processinstId_is_modified = false;

	private boolean processinstId_is_initialized = false;

	private String endPointId;

	private boolean endPointId_is_modified = false;

	private boolean endPointId_is_initialized = false;

	private String endPointName;

	private boolean endPointName_is_modified = false;

	private boolean endPointName_is_initialized = false;

	private String deviceGrpCode;

	private boolean deviceGrpCode_is_modified = false;

	private boolean deviceGrpCode_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbActivityHistoryDevice() {
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
		if ((newVal != null && newVal.equals(this.activityInstHistoryId) == true)
				|| (newVal == null && this.activityInstHistoryId == null))
			return;
		this.activityInstHistoryId = newVal;
		activityinstHistoryId_is_modified = true;
		activityinstHistoryId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstHistoryId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
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
		if ((newVal != null && newVal.equals(this.endPointId) == true)
				|| (newVal == null && this.endPointId == null))
			return;
		this.endPointId = newVal;
		endPointId_is_modified = true;
		endPointId_is_initialized = true;
	}

	/**
	 * Determine if the endPointId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
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
		if ((newVal != null && newVal.equals(this.endPointName) == true)
				|| (newVal == null && this.endPointName == null))
			return;
		this.endPointName = newVal;
		endPointName_is_modified = true;
		endPointName_is_initialized = true;
	}

	/**
	 * Determine if the endPointName is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isPersonnameModified() {
		return endPointName_is_modified;
	}

	/**
	 * Determine if the endPointName has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isPersonnameInitialized() {
		return endPointName_is_initialized;
	}

	/**
	 * Getter method for deviceGrpCode
	 * 
	 * @return the value of deviceGrpCode
	 */
	public RightGroupEnums getDeviceGrpCode() {
		return RightGroupEnums.fromType(deviceGrpCode);
	}

	/**
	 * Setter method for deviceGrpCode
	 * 
	 * @param newVal
	 *            The new value to be assigned to deviceGrpCode
	 */
	public void setDeviceGrpCode(RightGroupEnums group) {
	    String newVal=group.getType();
		if ((newVal != null && newVal.equals(this.deviceGrpCode) == true)
				|| (newVal == null && this.deviceGrpCode == null))
			return;
		this.deviceGrpCode = newVal;
		deviceGrpCode_is_modified = true;
		deviceGrpCode_is_initialized = true;
	}

	/**
	 * Determine if the deviceGrpCode is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDeviceGrpCodeModified() {
		return deviceGrpCode_is_modified;
	}

	/**
	 * Determine if the deviceGrpCode has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDeviceGrpCodeInitialized() {
		return deviceGrpCode_is_initialized;
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
		return activityinstHistoryId_is_modified || processinstId_is_modified
				|| endPointId_is_modified || endPointName_is_modified
				|| deviceGrpCode_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		activityinstHistoryId_is_modified = false;
		processinstId_is_modified = false;
		endPointId_is_modified = false;
		endPointName_is_modified = false;
		deviceGrpCode_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbActivityHistoryDevice bean) {
		setActivityInstHistoryId(bean.getActivityInstHistoryId());
		setProcessInstId(bean.getProcessInstId());
		setEndPointId(bean.getEndPointId());
		setEndPointName(bean.getEndPointName());
		setDeviceGrpCode(bean.getDeviceGrpCode());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[RT_ACTIVITYHISTORY_DEVICE] "
				+ "\n - RT_ACTIVITYHISTORY_DEVICE.ACTIVITYINST_HISTORY_ID = "
				+ (activityinstHistoryId_is_initialized ? ("["
						+ activityInstHistoryId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITYHISTORY_DEVICE.PROCESSINST_ID = "
				+ (processinstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - RT_ACTIVITYHISTORY_DEVICE.PERSON_ID = "
				+ (endPointId_is_initialized ? ("[" + endPointId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITYHISTORY_DEVICE.PERSONNAME = "
				+ (endPointName_is_initialized ? ("[" + endPointName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_ACTIVITYHISTORY_DEVICE.RIGHT_GRP_CODE = "
				+ (deviceGrpCode_is_initialized ? ("[" + deviceGrpCode.toString() + "]")
						: "not initialized") + "";
	}

}

