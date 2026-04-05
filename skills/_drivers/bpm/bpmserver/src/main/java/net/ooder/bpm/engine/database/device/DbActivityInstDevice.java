
package net.ooder.bpm.engine.database.device;

import java.io.Serializable;

import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;

public class DbActivityInstDevice implements Serializable {
	

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

	private String endPointName;

	private boolean endPointName_is_modified = false;

	private boolean endPointName_is_initialized = false;
	
	
	private String commandId;

	private boolean commandId_is_modified = false;

	private boolean commandId_is_initialized = false;

	private String deviceGrpCode;

	private boolean deviceGrpCode_is_modified = false;

	private boolean deviceGrpCode_is_initialized = false;

	private String deviceActivityState;

	private boolean deviceActivityState_is_modified = false;

	private boolean deviceActivityState_is_initialized = false;

	private String lastDeviceGrp;

	private boolean lastDeviceGrp_is_modified = false;

	private boolean lastDeviceGrp_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbActivityInstDevice() {
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
		if ((newVal != null && newVal.equals(this.activityInsteEndPointId) == true)
				|| (newVal == null && this.activityInsteEndPointId == null))
			return;
		this.activityInsteEndPointId = newVal;
		activityinstEndPointId_is_modified = true;
		activityinstEndPointId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstEndPointId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstEndPointIdModified() {
		return activityinstEndPointId_is_modified;
	}

	/**
	 * Determine if the activityinstEndPointId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstEndPointIdInitialized() {
		return activityinstEndPointId_is_initialized;
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
	 * Getter method for activityinstId
	 * 
	 * @return the value of activityinstId
	 */
	public String getCommandId() {
		return commandId;
	}

	/**
	 * Setter method for activityinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstId
	 */
	public void setCommandId(String newVal) {
		if ((newVal != null && newVal.equals(this.commandId) == true)
				|| (newVal == null && this.commandId == null))
			return;
		this.commandId = newVal;
		commandId_is_modified = true;
		commandId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCommandIdModified() {
		return commandId_is_modified;
	}

	/**
	 * Determine if the activityinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCommandIdInitialized() {
		return commandId_is_initialized;
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
	 * Getter method for deviceActivityState
	 * 
	 * @return the value of deviceActivityState
	 */
	public RightPerformStatus getDeviceActivityState() {
		return RightPerformStatus.fromType(deviceActivityState);
	}

	/**
	 * Setter method for deviceActivityState
	 * 
	 * @param newVal
	 *            The new value to be assigned to deviceActivityState
	 */
	public void setDeviceActivityState(RightPerformStatus status) {
	    String newVal=status.getType();
		if ((newVal != null && newVal.equals(this.deviceActivityState) == true)
				|| (newVal == null && this.deviceActivityState == null))
			return;
		this.deviceActivityState = newVal;
		deviceActivityState_is_modified = true;
		deviceActivityState_is_initialized = true;
	}

	/**
	 * Determine if the deviceActivityState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDeviceActivityStateModified() {
		return deviceActivityState_is_modified;
	}

	/**
	 * Determine if the deviceActivityState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDeviceActivityStateInitialized() {
		return deviceActivityState_is_initialized;
	}

	/**
	 * Getter method for lastDeviceGrp
	 * 
	 * @return the value of lastDeviceGrp
	 */
	public String getLastDeviceGrp() {
		return lastDeviceGrp;
	}

	/**
	 * Setter method for lastDeviceGrp
	 * 
	 * @param newVal
	 *            The new value to be assigned to lastDeviceGrp
	 */
	public void setLastDeviceGrp(String newVal) {
		if ((newVal != null && newVal.equals(this.lastDeviceGrp) == true)
				|| (newVal == null && this.lastDeviceGrp == null))
			return;
		this.lastDeviceGrp = newVal;
		lastDeviceGrp_is_modified = true;
		lastDeviceGrp_is_initialized = true;
	}

	/**
	 * Determine if the lastDeviceGrp is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isLastDeviceGrpModified() {
		return lastDeviceGrp_is_modified;
	}

	/**
	 * Determine if the lastDeviceGrp has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isLastDeviceGrpInitialized() {
		return lastDeviceGrp_is_initialized;
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
		return activityinstEndPointId_is_modified || processinstId_is_modified
				|| activityinstId_is_modified || endPointId_is_modified
				|| endPointName_is_modified || deviceGrpCode_is_modified
				|| deviceActivityState_is_modified || lastDeviceGrp_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		activityinstEndPointId_is_modified = false;
		processinstId_is_modified = false;
		activityinstId_is_modified = false;
		endPointId_is_modified = false;
		endPointName_is_modified = false;
		deviceGrpCode_is_modified = false;
		deviceActivityState_is_modified = false;
		lastDeviceGrp_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbActivityInstDevice bean) {
		setActivityInstEndPointId(bean.getActivityInstEndPointId());
		setProcessInstId(bean.getProcessInstId());
		setActivityInstId(bean.getActivityInstId());
		setEndPointId(bean.getEndPointId());
		setEndPointName(bean.getEndPointName());
		setDeviceGrpCode(bean.getDeviceGrpCode());
		setDeviceActivityState(bean.getDeviceActivityState());
		setLastDeviceGrp(bean.getLastDeviceGrp());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[DT_ACTIVITY_DEVICE] "
				+ "\n - DT_ACTIVITY_DEVICE.ACTIVITYINST_ENDPOINT_ID = "
				+ (activityinstEndPointId_is_initialized ? ("["
						+ activityInsteEndPointId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.PROCESSINST_ID = "
				+ (processinstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.ACTIVITYINST_ID = "
				+ (activityinstId_is_initialized ? ("["
						+ activityInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.ENDPOINT_ID = "
				+ (endPointId_is_initialized ? ("[" + endPointId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.ENDPOINT_NAME = "
				+ (endPointName_is_initialized ? ("[" + endPointName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.DEVICE_GRP_CODE = "
				+ (deviceGrpCode_is_initialized ? ("[" + deviceGrpCode.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.DEVICE_ACTIVITY_STATE = "
				+ (deviceActivityState_is_initialized ? ("["
						+ deviceActivityState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - DT_ACTIVITY_DEVICE.LAST_DEVICE_GRP = "
				+ (lastDeviceGrp_is_initialized ? ("[" + lastDeviceGrp.toString() + "]")
						: "not initialized") + "";
	}

}

