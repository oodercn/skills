/**
 * $RCSfile: DbProcessDefVersion.java,v $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.*;
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
 * Description: 流程定义版本接口数据库实�?
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_PROCESSDEF_VERSION
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
public class DbProcessDefVersion implements EIProcessDefVersion, Cacheable,
		Serializable {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefVersion.class);

	private String processDefVersionId;

	private boolean processDefVersionId_is_modified = false;

	private boolean processDefVersionId_is_initialized = false;

	private String processDefId;

	private boolean processDefId_is_modified = false;

	private boolean processDefId_is_initialized = false;

	private int version;

	private boolean version_is_modified = false;

	private boolean version_is_initialized = false;

	private String description;

	private boolean description_is_modified = false;

	private boolean description_is_initialized = false;

	private String publicationStatus;

	private boolean publicationStatus_is_modified = false;

	private boolean publicationStatus_is_initialized = false;

	private java.util.Date activeTime;

	private boolean activeTime_is_modified = false;

	private boolean activeTime_is_initialized = false;

	private java.util.Date freezeTime;

	private boolean freezeTime_is_modified = false;

	private boolean freezeTime_is_initialized = false;

	private String creatorId;

	private boolean creatorId_is_modified = false;

	private boolean creatorId_is_initialized = false;

	private String creatorName;

	private boolean creatorName_is_modified = false;

	private boolean creatorName_is_initialized = false;

	private java.util.Date created;

	private boolean created_is_modified = false;

	private boolean created_is_initialized = false;

	private String modifierId;

	private boolean modifierId_is_modified = false;

	private boolean modifierId_is_initialized = false;

	private String modifierName;

	private boolean modifierName_is_modified = false;

	private boolean modifierName_is_initialized = false;

	private java.util.Date modifyTime;

	private boolean modifyTime_is_modified = false;

	private boolean modifyTime_is_initialized = false;

	private int limit;

	private boolean limit_is_modified = false;

	private boolean limit_is_initialized = false;

	private String durationUnit;

	private boolean durationUnit_is_modified = false;

	private boolean durationUnit_is_initialized = false;

	private boolean _isNew = true;

	Map attributeTopMap = null;

	Map attributeIdMap = null;

	List listeners = null;

	private boolean _isAttributeModified = true;

	/**
	 */
	DbProcessDefVersion() {
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
	 * Getter method for version
	 * 
	 * @return the value of version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Setter method for version
	 * 
	 * @param newVal
	 *            The new value to be assigned to version
	 */
	public void setVersion(int newVal) {
		if (newVal == this.version) {
			return;
		}
		this.version = newVal;
		version_is_modified = true;
		version_is_initialized = true;
	}

	/**
	 * Determine if the version is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isVersionModified() {
		return version_is_modified;
	}

	/**
	 * Determine if the version has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isVersionInitialized() {
		return version_is_initialized;
	}

	/**
	 * Getter method for description
	 * 
	 * @return the value of description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter method for description
	 * 
	 * @param newVal
	 *            The new value to be assigned to description
	 */
	public void setDescription(String newVal) {
		if ((newVal != null && newVal.equals(this.description) == true)
				|| (newVal == null && this.description == null))
			return;
		this.description = newVal;
		description_is_modified = true;
		description_is_initialized = true;
	}

	/**
	 * Determine if the description is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDescriptionModified() {
		return description_is_modified;
	}

	/**
	 * Determine if the description has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDescriptionInitialized() {
		return description_is_initialized;
	}

	/**
	 * Getter method for publicationstate
	 * 
	 * @return the value of publicationstate
	 */
	public String getPublicationStatus() {
		return publicationStatus;
	}

	/**
	 * Setter method for publicationstate
	 * 
	 * @param newVal
	 *            The new value to be assigned to publicationstate
	 */
	public void setPublicationStatus(String newVal) {
	 
		if ((newVal != null && newVal.equals(this.publicationStatus) == true)
				|| (newVal == null && this.publicationStatus == null))
			return;
		this.publicationStatus = newVal;
		publicationStatus_is_modified = true;
		publicationStatus_is_initialized = true;
	}

	/**
	 * Determine if the publicationstate is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isPublicationstatusModified() {
		return publicationStatus_is_modified;
	}

	/**
	 * Determine if the publicationstate has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isPublicationstatusInitialized() {
		return publicationStatus_is_initialized;
	}

	/**
	 * Getter method for activetime
	 * 
	 * @return the value of activetime
	 */
	public java.util.Date getActiveTime() {
		return activeTime;
	}

	/**
	 * Setter method for activetime
	 * 
	 * @param newVal
	 *            The new value to be assigned to activetime
	 */
	public void setActiveTime(java.util.Date newVal) {
		if ((newVal != null && newVal.equals(this.activeTime) == true)
				|| (newVal == null && this.activeTime == null))
			return;
		this.activeTime = newVal;
		activeTime_is_modified = true;
		activeTime_is_initialized = true;
	}

	/**
	 * Determine if the activetime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivetimeModified() {
		return activeTime_is_modified;
	}

	/**
	 * Determine if the activetime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivetimeInitialized() {
		return activeTime_is_initialized;
	}

	/**
	 * Getter method for freezetime
	 * 
	 * @return the value of freezetime
	 */
	public java.util.Date getFreezeTime() {
		return freezeTime;
	}

	/**
	 * Setter method for freezetime
	 * 
	 * @param newVal
	 *            The new value to be assigned to freezetime
	 */
	public void setFreezeTime(java.util.Date newVal) {
		if ((newVal != null && newVal.equals(this.freezeTime) == true)
				|| (newVal == null && this.freezeTime == null))
			return;
		this.freezeTime = newVal;
		freezeTime_is_modified = true;
		freezeTime_is_initialized = true;
	}

	/**
	 * Determine if the freezetime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isFreezetimeModified() {
		return freezeTime_is_modified;
	}

	/**
	 * Determine if the freezetime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isFreezetimeInitialized() {
		return freezeTime_is_initialized;
	}

	/**
	 * Getter method for creatorid
	 * 
	 * @return the value of creatorid
	 */
	public String getCreatorId() {
		return creatorId;
	}

	/**
	 * Setter method for creatorid
	 * 
	 * @param newVal
	 *            The new value to be assigned to creatorid
	 */
	public void setCreatorId(String newVal) {
		if ((newVal != null && newVal.equals(this.creatorId) == true)
				|| (newVal == null && this.creatorId == null))
			return;
		this.creatorId = newVal;
		creatorId_is_modified = true;
		creatorId_is_initialized = true;
	}

	/**
	 * Determine if the creatorid is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCreatoridModified() {
		return creatorId_is_modified;
	}

	/**
	 * Determine if the creatorid has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCreatoridInitialized() {
		return creatorId_is_initialized;
	}

	/**
	 * Getter method for creatorname
	 * 
	 * @return the value of creatorname
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * Setter method for creatorname
	 * 
	 * @param newVal
	 *            The new value to be assigned to creatorname
	 */
	public void setCreatorName(String newVal) {
		if ((newVal != null && newVal.equals(this.creatorName) == true)
				|| (newVal == null && this.creatorName == null))
			return;
		this.creatorName = newVal;
		creatorName_is_modified = true;
		creatorName_is_initialized = true;
	}

	/**
	 * Determine if the creatorname is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCreatornameModified() {
		return creatorName_is_modified;
	}

	/**
	 * Determine if the creatorname has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCreatornameInitialized() {
		return creatorName_is_initialized;
	}

	/**
	 * Getter method for created
	 * 
	 * @return the value of created
	 */
	public java.util.Date getCreated() {
		return created;
	}

	/**
	 * Setter method for created
	 * 
	 * @param newVal
	 *            The new value to be assigned to created
	 */
	public void setCreated(java.util.Date newVal) {
		if ((newVal != null && newVal.equals(this.created) == true)
				|| (newVal == null && this.created == null))
			return;
		this.created = newVal;
		created_is_modified = true;
		created_is_initialized = true;
	}

	/**
	 * Determine if the created is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isCreatedModified() {
		return created_is_modified;
	}

	/**
	 * Determine if the created has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isCreatedInitialized() {
		return created_is_initialized;
	}

	/**
	 * Getter method for modifierid
	 * 
	 * @return the value of modifierid
	 */
	public String getModifierId() {
		return modifierId;
	}

	/**
	 * Setter method for modifierid
	 * 
	 * @param newVal
	 *            The new value to be assigned to modifierid
	 */
	public void setModifierId(String newVal) {
		if ((newVal != null && newVal.equals(this.modifierId) == true)
				|| (newVal == null && this.modifierId == null))
			return;
		this.modifierId = newVal;
		modifierId_is_modified = true;
		modifierId_is_initialized = true;
	}

	/**
	 * Determine if the modifierid is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isModifieridModified() {
		return modifierId_is_modified;
	}

	/**
	 * Determine if the modifierid has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isModifieridInitialized() {
		return modifierId_is_initialized;
	}

	/**
	 * Getter method for modifiername
	 * 
	 * @return the value of modifiername
	 */
	public String getModifierName() {
		return modifierName;
	}

	/**
	 * Setter method for modifiername
	 * 
	 * @param newVal
	 *            The new value to be assigned to modifiername
	 */
	public void setModifierName(String newVal) {
		if ((newVal != null && newVal.equals(this.modifierName) == true)
				|| (newVal == null && this.modifierName == null))
			return;
		this.modifierName = newVal;
		modifierName_is_modified = true;
		modifierName_is_initialized = true;
	}

	/**
	 * Determine if the modifiername is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isModifiernameModified() {
		return modifierName_is_modified;
	}

	/**
	 * Determine if the modifiername has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isModifiernameInitialized() {
		return modifierName_is_initialized;
	}

	/**
	 * Getter method for modifytime
	 * 
	 * @return the value of modifytime
	 */
	public java.util.Date getModifyTime() {
		return modifyTime;
	}

	/**
	 * Setter method for modifytime
	 * 
	 * @param newVal
	 *            The new value to be assigned to modifytime
	 */
	public void setModifyTime(java.util.Date newVal) {
		if ((newVal != null && newVal.equals(this.modifyTime) == true)
				|| (newVal == null && this.modifyTime == null))
			return;
		this.modifyTime = newVal;
		modifyTime_is_modified = true;
		modifyTime_is_initialized = true;
	}

	/**
	 * Determine if the modifytime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isModifytimeModified() {
		return modifyTime_is_modified;
	}

	/**
	 * Determine if the modifytime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isModifytimeInitialized() {
		return modifyTime_is_initialized;
	}

	/**
	 * Getter method for limit
	 * 
	 * @return the value of limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Setter method for limit
	 * 
	 * @param newVal
	 *            The new value to be assigned to limit
	 */
	public void setLimit(int newVal) {
		if (newVal == this.limit) {
			return;
		}
		this.limit = newVal;
		limit_is_modified = true;
		limit_is_initialized = true;
	}

	/**
	 * Determine if the limit is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isLimitModified() {
		return limit_is_modified;
	}

	/**
	 * Determine if the limit has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isLimitInitialized() {
		return limit_is_initialized;
	}

	/**
	 * Getter method for durationunit
	 * 
	 * @return the value of durationunit
	 */
	public String getDurationUnit() {
		return durationUnit;
	}

	/**
	 * Setter method for durationunit
	 * 
	 * @param newVal
	 *            The new value to be assigned to durationunit
	 */
	public void setDurationUnit(String newVal) {
		if ((newVal != null && newVal.equals(this.durationUnit) == true)
				|| (newVal == null && this.durationUnit == null))
			return;
		this.durationUnit = newVal;
		durationUnit_is_modified = true;
		durationUnit_is_initialized = true;
	}

	/**
	 * Determine if the durationunit is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDurationunitModified() {
		return durationUnit_is_modified;
	}

	/**
	 * Determine if the durationunit has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDurationunitInitialized() {
		return durationUnit_is_initialized;
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
		return processDefVersionId_is_modified || processDefId_is_modified
				|| version_is_modified || description_is_modified
				|| publicationStatus_is_modified || activeTime_is_modified
				|| freezeTime_is_modified || creatorId_is_modified
				|| creatorName_is_modified || created_is_modified
				|| modifierId_is_modified || modifierName_is_modified
				|| modifyTime_is_modified || limit_is_modified
				|| durationUnit_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		processDefVersionId_is_modified = false;
		processDefId_is_modified = false;
		version_is_modified = false;
		description_is_modified = false;
		publicationStatus_is_modified = false;
		activeTime_is_modified = false;
		freezeTime_is_modified = false;
		creatorId_is_modified = false;
		creatorName_is_modified = false;
		created_is_modified = false;
		modifierId_is_modified = false;
		modifierName_is_modified = false;
		modifyTime_is_modified = false;
		limit_is_modified = false;
		durationUnit_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbProcessDefVersion bean) {
		setProcessDefVersionId(bean.getProcessDefVersionId());
		setProcessDefId(bean.getProcessDefId());
		setVersion(bean.getVersion());
		setDescription(bean.getDescription());
		setPublicationStatus(bean.getPublicationStatus());
		setActiveTime(bean.getActiveTime());
		setFreezeTime(bean.getFreezeTime());
		setCreatorId(bean.getCreatorId());
		setCreatorName(bean.getCreatorName());
		setCreated(bean.getCreated());
		setModifierId(bean.getModifierId());
		setModifierName(bean.getModifierName());
		setModifyTime(bean.getModifyTime());
		setLimit(bean.getLimit());
		setDurationUnit(bean.getDurationUnit());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_PROCESSDEF_VERSION] "
				+ "\n - BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID = "
				+ (processDefVersionId_is_initialized ? ("["
						+ processDefVersionId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.PROCESSDEF_ID = "
				+ (processDefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.VERSION = "
				+ (version_is_initialized ? ("[" + version + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.DESCRIPTION = "
				+ (description_is_initialized ? ("[" + description.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.PUBLICATIONSTATE = "
				+ (publicationStatus_is_initialized ? ("["
						+ publicationStatus.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.ACTIVETIME = "
				+ (activeTime_is_initialized ? ("[" + activeTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.FREEZETIME = "
				+ (freezeTime_is_initialized ? ("[" + freezeTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.CREATORID = "
				+ (creatorId_is_initialized ? ("[" + creatorId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.CREATORNAME = "
				+ (creatorName_is_initialized ? ("[" + creatorName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.CREATED = "
				+ (created_is_initialized ? ("[" + created.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.MODIFIERID = "
				+ (modifierId_is_initialized ? ("[" + modifierId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.MODIFIERNAME = "
				+ (modifierName_is_initialized ? ("[" + modifierName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.MODIFYTIME = "
				+ (modifyTime_is_initialized ? ("[" + modifyTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.LIMIT = "
				+ (limit_is_initialized ? ("[" + limit + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_PROCESSDEF_VERSION.DURATIONUNIT = "
				+ (durationUnit_is_initialized ? ("[" + durationUnit.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getProcessDefName()
	 */
	public String getProcessDefName() {
		try {
			return EIProcessDefManager.getInstance().loadByKey(
					this.processDefId).getName();
		} catch (BPMException e) {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getClassification()
	 */
	public String getClassification() {
		try {
			return EIProcessDefManager.getInstance().loadByKey(
					this.processDefId).getClassification();
		} catch (BPMException e) {
			return "";
		}
	}

	public String getDefDescription() {
		try {
			return EIProcessDefManager.getInstance().loadByKey(
					this.processDefId).getDescription();
		} catch (BPMException e) {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getSystemCode()
	 */
	public String getSystemCode() {
		try {
			return EIProcessDefManager.getInstance().loadByKey(
					this.processDefId).getSystemCode();
		} catch (BPMException e) {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAccessLevel()
	 */
	public String getAccessLevel() {
		try {
			return EIProcessDefManager.getInstance().loadByKey(
					this.processDefId).getAccessLevel();
		} catch (BPMException e) {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAllActivityDefs()
	 */
	public List<EIActivityDef> getAllActivityDefs() {
		EIActivityDefManager manager = EIActivityDefManager.getInstance();
		try {
			return manager.loadByProcessDefVersionId(this.processDefVersionId);
		} catch (BPMException e) {
			return null;
		}
	}

	/**
	 * 取得当前版本中包含所有路由的对象
	 * 
	 * @return 返回的List是只�?
	 */
	public List<EIRouteDef> getAllRouteDefs()  {
		EIRouteDefManager manager = EIRouteDefManager.getInstance();
		try {
			return manager.loadByProcessDefVersionId(this.processDefVersionId);
		} catch (BPMException e) {
			return new ArrayList<EIRouteDef>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
	 */
	public String getAttributeValue(String name) {
		EIAttributeDef attDef = getAttribute(name);
		if (attDef == null) {
			return null;
		}
		return attDef.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
	 */
	public Object getAttributeInterpretedValue(String name) {
		EIAttributeDef attDef = getAttribute(name);
		if (attDef == null) {
			return null;
		}
		return attDef.getInterpretedValue();
	}

	/**
	 * 取得属性值，当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
	 * 例如�?br>
	 * "Form1.field1.value" - 取得最顶层属性中名称为form1�?
	 * 
	 * @param name
	 * @return
	 */
	public EIAttributeDef getAttribute(String name) {
//		if (name != null) {
//			name = name.toUpperCase();
//		}

		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbProcessDefVersionManager manager = (DbProcessDefVersionManager) EIProcessDefVersionManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in process definition "
						+ this.processDefVersionId + " failed!", e);
				return null;
			}
		}

		StringTokenizer st = new StringTokenizer(name, ".");
		DbAttributeDef subAtt = null;
		while (st.hasMoreTokens()) {
			String subname = st.nextToken();
			if (subAtt == null) { // top level
				subAtt = (DbAttributeDef) attributeTopMap.get(subname);
			} else {
				subAtt = (DbAttributeDef) subAtt.getChild(subname);
			}

			if (subAtt == null) {
				return null; // not found
			}
		}
		return subAtt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityDef#getAllAttribute()
	 */
	public List getAllAttribute() {
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbProcessDefVersionManager manager = (DbProcessDefVersionManager) EIProcessDefVersionManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in process definition "
						+ this.processDefVersionId + " failed!", e);
				return new ArrayList();
			}
		}
		return new ArrayList(attributeIdMap.values());
	}

	/**
	 * 取得最顶层的属性（没有父属性的属性）
	 * 
	 * @return
	 */
	public List getTopAttribute() {
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbProcessDefVersionManager manager = (DbProcessDefVersionManager) EIProcessDefVersionManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in process definition "
						+ this.processDefVersionId + " failed!", e);
				return new ArrayList();
			}
		}

		List list = new ArrayList(attributeTopMap.values());
		List result = new ArrayList();
		for (Iterator it = list.iterator(); it.hasNext();) {
			EIAttributeDef attr = (EIAttributeDef) it.next();
			result.addAll(attr.getChildren());
		}
		return result;

	}

	public void clearAttribute() {

		if (attributeIdMap == null && attributeTopMap == null) {
			// 不读取数据库，直接清�?
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
		}
		attributeIdMap.clear();
		attributeTopMap.clear();

		_isAttributeModified = true;
	}

	public void setAttribute(String parentName, EIAttributeDef attDef)
			throws BPMException {
//		if (parentName != null) {
//			parentName = parentName.toUpperCase();
//		}
		// load first
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbProcessDefVersionManager manager = (DbProcessDefVersionManager) EIProcessDefVersionManager
					.getInstance();

			manager.loadAttribute(this);
		}

		EIAttributeDef parentAtt = null;

		if (parentName != null && !parentName.equals("")) {
			parentAtt = getAttribute(parentName);
			if (parentAtt == null) {
				if (parentName.indexOf(".") == -1) { // top level
					parentAtt = new DbAttributeDef();
					parentAtt.setName(parentName);
					parentAtt.setType(parentName);
					setAttribute(null, parentAtt);
				} else {
					// error: parentAtt not in this process definition!
					throw new BPMException(
							"parentAtt not in this process definition! parentAtt:"
									+ parentName + ", attName:"
									+ attDef.getName());
				}
			}
			attDef.setType(parentAtt.getType());
		}

		if (parentAtt != null) {
			// sub attribute
			EIAttributeDef oldAtt = (EIAttributeDef) parentAtt.getChild(attDef
					.getName());
			if (oldAtt != null) { // exist same name attribute in this tree
				attributeIdMap.remove(oldAtt.getId()); // remove it from all
														// attribute map
			}
			parentAtt.addChild(attDef); // change the new attribute definition!
			attributeIdMap.put(attDef.getId(), attDef);
		} else {
			// top level add to top map
			// EIAttributeDef oldAtt = (EIAttributeDef)
			// attributeTopMap.get(attDef.getName());
			// if(oldAtt != null) { //exist same name attribute in this tree
			// attributeIdMap.remove(oldAtt.getId()); //remove it from all
			// attribute map
			// }

			attributeTopMap.put(attDef.getName(), attDef);
		}

		_isAttributeModified = true;
	}

	public boolean isAttributeModified() {
		if (attributeIdMap == null && attributeTopMap == null) {
			return false;
		}
		return processDefVersionId_is_modified || _isAttributeModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getListeners()
	 */
	public List getListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
			DbProcessDefVersionManager manager = (DbProcessDefVersionManager) EIProcessDefVersionManager
					.getInstance();
			try {
				manager.loadListener(this);
			} catch (BPMException e) {
				return null;
			}
		}
		return new ArrayList(listeners);
	}

	public void setListeners(List list) {
		listeners = list;
	}

	/**
	 * @return
	 */
	public boolean isListenersModified() {
		if (listeners == null) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {
		int size = 0;
		size += CacheSizes.sizeOfString(processDefVersionId);
		size += CacheSizes.sizeOfString(processDefId);
		size += CacheSizes.sizeOfInt(); // version
		size += CacheSizes.sizeOfString(description);
		size += CacheSizes.sizeOfString(publicationStatus);
		if (activeTime == null) {
			size += CacheSizes.sizeOfObject();
		} else {
			size += CacheSizes.sizeOfDate();
		}
		if (freezeTime == null) {
			size += CacheSizes.sizeOfObject();
		} else {
			size += CacheSizes.sizeOfDate();
		}

		size += CacheSizes.sizeOfString(creatorId);
		size += CacheSizes.sizeOfString(creatorName);
		if (created == null) {
			size += CacheSizes.sizeOfObject();
		} else {
			size += CacheSizes.sizeOfDate();
		}

		size += CacheSizes.sizeOfString(modifierId);
		size += CacheSizes.sizeOfString(modifierName);
		if (modifyTime == null) {
			size += CacheSizes.sizeOfObject();
		} else {
			size += CacheSizes.sizeOfDate();
		}

		size += CacheSizes.sizeOfInt(); // LIMIT
		size += CacheSizes.sizeOfString(durationUnit);

		return size;

	}

}


