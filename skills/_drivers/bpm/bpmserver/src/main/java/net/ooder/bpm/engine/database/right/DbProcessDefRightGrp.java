/**
 * $RCSfile: DbProcessDefRightGrp.java,v $
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

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 娴佺▼鏉冮檺缁?
 * </p>
 * <p>
 * The java object mapped on the Relation database table RT_PROCESSDEF_RIGHT_GRP
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
public class DbProcessDefRightGrp implements Cacheable, Serializable {
	
	
	/** 鏃犳潈闄愮粍 */
	public static final String RIGHTGRP_TYPE = "RIGHTGRPTYPE";

	private String rightGroupId;

	private boolean rightGroupId_is_modified = false;

	private boolean rightGroupId_is_initialized = false;

	private String processDefVersionId;

	private boolean processdefVersionId_is_modified = false;

	private boolean processdefVersionId_is_initialized = false;

	private String processDefId;

	private boolean processdefId_is_modified = false;

	private boolean processdefId_is_initialized = false;

	private String groupName;

	private boolean groupname_is_modified = false;

	private boolean groupname_is_initialized = false;

	private String groupCode;

	private boolean groupcode_is_modified = false;

	private boolean groupcode_is_initialized = false;

	private int groupOrder;

	private boolean grouporder_is_modified = false;

	private boolean grouporder_is_initialized = false;

	private String defaultRight;

	private boolean defaultright_is_modified = false;

	private boolean defaultright_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbProcessDefRightGrp() {
	}

	/**
	 * Getter method for rightGroupId
	 * 
	 * @return the value of rightGroupId
	 */
	public String getRightGroupId() {
		return rightGroupId;
	}

	/**
	 * Setter method for rightGroupId
	 * 
	 * @param newVal
	 *            The new value to be assigned to rightGroupId
	 */
	public void setRightGroupId(String newVal) {
		if ((newVal != null && newVal.equals(this.rightGroupId) == true)
				|| (newVal == null && this.rightGroupId == null))
			return;
		this.rightGroupId = newVal;
		rightGroupId_is_modified = true;
		rightGroupId_is_initialized = true;
	}

	/**
	 * Determine if the rightGroupId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRightGroupIdModified() {
		return rightGroupId_is_modified;
	}

	/**
	 * Determine if the rightGroupId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRightGroupIdInitialized() {
		return rightGroupId_is_initialized;
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
	 * Getter method for groupname
	 * 
	 * @return the value of groupname
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Setter method for groupname
	 * 
	 * @param newVal
	 *            The new value to be assigned to groupname
	 */
	public void setGroupName(String newVal) {
		if ((newVal != null && newVal.equals(this.groupName) == true)
				|| (newVal == null && this.groupName == null))
			return;
		this.groupName = newVal;
		groupname_is_modified = true;
		groupname_is_initialized = true;
	}

	/**
	 * Determine if the groupname is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isGroupnameModified() {
		return groupname_is_modified;
	}

	/**
	 * Determine if the groupname has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isGroupnameInitialized() {
		return groupname_is_initialized;
	}

	/**
	 * Getter method for groupcode
	 * 
	 * @return the value of groupcode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * Setter method for groupcode
	 * 
	 * @param newVal
	 *            The new value to be assigned to groupcode
	 */
	public void setGroupCode(String newVal) {
		if ((newVal != null && newVal.equals(this.groupCode) == true)
				|| (newVal == null && this.groupCode == null))
			return;
		this.groupCode = newVal;
		groupcode_is_modified = true;
		groupcode_is_initialized = true;
	}

	/**
	 * Determine if the groupcode is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isGroupcodeModified() {
		return groupcode_is_modified;
	}

	/**
	 * Determine if the groupcode has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isGroupcodeInitialized() {
		return groupcode_is_initialized;
	}

	/**
	 * Getter method for grouporder
	 * 
	 * @return the value of grouporder
	 */
	public int getGroupOrder() {
		return groupOrder;
	}

	/**
	 * Setter method for grouporder
	 * 
	 * @param newVal
	 *            The new value to be assigned to grouporder
	 */
	public void setGroupOrder(int newVal) {
		if (newVal == this.groupOrder)
			return;
		this.groupOrder = newVal;
		grouporder_is_modified = true;
		grouporder_is_initialized = true;
	}

	/**
	 * Determine if the grouporder is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isGrouporderModified() {
		return grouporder_is_modified;
	}

	/**
	 * Determine if the grouporder has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isGrouporderInitialized() {
		return grouporder_is_initialized;
	}

	/**
	 * Getter method for defaultright
	 * 
	 * @return the value of defaultright
	 */
	public String getDefaultRight() {
		return defaultRight;
	}

	/**
	 * Setter method for defaultright
	 * 
	 * @param newVal
	 *            The new value to be assigned to defaultright
	 */
	public void setDefaultRight(String newVal) {
		if ((newVal != null && newVal.equals(this.defaultRight) == true)
				|| (newVal == null && this.defaultRight == null))
			return;
		this.defaultRight = newVal;
		defaultright_is_modified = true;
		defaultright_is_initialized = true;
	}

	/**
	 * Determine if the defaultright is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDefaultrightModified() {
		return defaultright_is_modified;
	}

	/**
	 * Determine if the defaultright has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDefaultrightInitialized() {
		return defaultright_is_initialized;
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
		return rightGroupId_is_modified || processdefVersionId_is_modified
				|| processdefId_is_modified || groupname_is_modified
				|| groupcode_is_modified || grouporder_is_modified
				|| defaultright_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		rightGroupId_is_modified = false;
		processdefVersionId_is_modified = false;
		processdefId_is_modified = false;
		groupname_is_modified = false;
		groupcode_is_modified = false;
		grouporder_is_modified = false;
		defaultright_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbProcessDefRightGrp bean) {
		setRightGroupId(bean.getRightGroupId());
		setProcessDefVersionId(bean.getProcessDefVersionId());
		setProcessDefId(bean.getProcessDefId());
		setGroupName(bean.getGroupName());
		setGroupCode(bean.getGroupCode());
		setGroupOrder(bean.getGroupOrder());
		setDefaultRight(bean.getDefaultRight());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[RT_PROCESSDEF_RIGHT_GRP] "
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID = "
				+ (rightGroupId_is_initialized ? ("[" + rightGroupId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_VERSION_ID = "
				+ (processdefVersionId_is_initialized ? ("["
						+ processDefVersionId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_ID = "
				+ (processdefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.GROUPNAME = "
				+ (groupname_is_initialized ? ("[" + groupName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.GROUPCODE = "
				+ (groupcode_is_initialized ? ("[" + groupCode.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.GROUPORDER = "
				+ (grouporder_is_initialized ? ("[" + groupOrder + "]")
						: "not initialized")
				+ ""
				+ "\n - RT_PROCESSDEF_RIGHT_GRP.DEFAULTRIGHT = "
				+ (defaultright_is_initialized ? ("[" + defaultRight.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {

		int size = 0;

		size += CacheSizes.sizeOfString(rightGroupId);
		size += CacheSizes.sizeOfString(processDefVersionId);
		size += CacheSizes.sizeOfString(processDefId);
		size += CacheSizes.sizeOfString(groupName);
		size += CacheSizes.sizeOfString(groupCode);
		size += CacheSizes.sizeOfString(defaultRight);

		size += CacheSizes.sizeOfBoolean() * 17;

		return size;
	}

}


