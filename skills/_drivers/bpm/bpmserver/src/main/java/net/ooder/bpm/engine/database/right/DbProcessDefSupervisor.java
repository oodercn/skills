/**
 * $RCSfile: DbProcessDefSupervisor.java,v $
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

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisor;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 娴佺▼鐩戞帶浜?
 * </p>
 * <p>
 * The java object mapped on the Relation database table
 * RT_PROCESSDEF_SUPERVISOR
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
public class DbProcessDefSupervisor implements Cacheable, Serializable,
		EIProcessDefSupervisor {
	private String processDefId;

	private boolean processdefId_is_modified = false;

	private boolean processdefId_is_initialized = false;

	private String processDefVersionId;

	private boolean processdefVersionId_is_modified = false;

	private boolean processdefVersionId_is_initialized = false;

	private String supervisorId;

	private boolean supervisorId_is_modified = false;

	private boolean supervisorId_is_initialized = false;

	private String supervisorName;

	private boolean supervisorName_is_modified = false;

	private boolean supervisorName_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbProcessDefSupervisor() {
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
		processdefId_is_modified = true;
		processdefId_is_initialized = true;
	}

	/**
	 * Determine if the processdefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefIdModified() {
		return processdefId_is_modified;
	}

	/**
	 * Determine if the processdefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefIdInitialized() {
		return processdefId_is_initialized;
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
		processdefVersionId_is_modified = true;
		processdefVersionId_is_initialized = true;
	}

	/**
	 * Determine if the processdefVersionId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefVersionIdModified() {
		return processdefVersionId_is_modified;
	}

	/**
	 * Determine if the processdefVersionId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefVersionIdInitialized() {
		return processdefVersionId_is_initialized;
	}

	/**
	 * Getter method for supervisorId
	 * 
	 * @return the value of supervisorId
	 */
	public String getSupervisorId() {
		return supervisorId;
	}

	/**
	 * Setter method for supervisorId
	 * 
	 * @param newVal
	 *            The new value to be assigned to supervisorId
	 */
	public void setSupervisorId(String newVal) {
		if ((newVal != null && newVal.equals(this.supervisorId) == true)
				|| (newVal == null && this.supervisorId == null))
			return;
		this.supervisorId = newVal;
		supervisorId_is_modified = true;
		supervisorId_is_initialized = true;
	}

	/**
	 * Determine if the supervisorId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isSupervisorIdModified() {
		return supervisorId_is_modified;
	}

	/**
	 * Determine if the supervisorId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isSupervisorIdInitialized() {
		return supervisorId_is_initialized;
	}

	/**
	 * Getter method for supervisorName
	 * 
	 * @return the value of supervisorName
	 */
	public String getSupervisorName() {
		return supervisorName;
	}

	/**
	 * Setter method for supervisorName
	 * 
	 * @param newVal
	 *            The new value to be assigned to supervisorName
	 */
	public void setSupervisorName(String newVal) {
		if ((newVal != null && newVal.equals(this.supervisorName) == true)
				|| (newVal == null && this.supervisorName == null))
			return;
		this.supervisorName = newVal;
		supervisorName_is_modified = true;
		supervisorName_is_initialized = true;
	}

	/**
	 * Determine if the supervisorName is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isSupervisorNameModified() {
		return supervisorName_is_modified;
	}

	/**
	 * Determine if the supervisorName has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isSupervisorNameInitialized() {
		return supervisorName_is_initialized;
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
		return processdefId_is_modified || processdefVersionId_is_modified
				|| supervisorId_is_modified || supervisorName_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		processdefId_is_modified = false;
		processdefVersionId_is_modified = false;
		supervisorId_is_modified = false;
		supervisorName_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(EIProcessDefSupervisor bean) {
		setProcessDefId(bean.getProcessDefId());
		setProcessDefVersionId(bean.getProcessDefVersionId());
		setSupervisorId(bean.getSupervisorId());
		setSupervisorName(bean.getSupervisorName());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[RT_PROCESSDEF_SUPERVISOR] "
				+ "\n - RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_ID = "
				+ (processdefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_VERSION_ID = "
				+ (processdefVersionId_is_initialized ? ("["
						+ processDefVersionId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_ID = "
				+ (supervisorId_is_initialized ? ("[" + supervisorId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_NAME = "
				+ (supervisorName_is_initialized ? ("["
						+ supervisorName.toString() + "]") : "not initialized")
				+ "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {
		int size = 0;

		size += CacheSizes.sizeOfString(processDefId);
		size += CacheSizes.sizeOfString(processDefVersionId);
		size += CacheSizes.sizeOfString(supervisorId);
		size += CacheSizes.sizeOfString(supervisorName);

		size += CacheSizes.sizeOfBoolean() * 5;

		return size;
	}
}


