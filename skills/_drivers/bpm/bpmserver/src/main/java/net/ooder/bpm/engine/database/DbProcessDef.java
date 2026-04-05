/**
 * $RCSfile: DbProcessDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义接口数据库实现
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_PROCESSDEF
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
public class DbProcessDef implements EIProcessDef, Cacheable, Serializable {
    private String processDefId;

    private boolean processDefId_is_modified = false;

    private boolean processDefId_is_initialized = false;

    private String name;

    private boolean name_is_modified = false;

    private boolean name_is_initialized = false;

    private String description;

    private boolean description_is_modified = false;

    private boolean description_is_initialized = false;

    private String classification;

    private boolean classification_is_modified = false;

    private boolean classification_is_initialized = false;

    private String systemCode;

    private boolean systemCode_is_modified = false;

    private boolean systemCode_is_initialized = false;

    private String accessLevel;

    private boolean accessLevel_is_modified = false;

    private boolean accesslevel_is_initialized = false;

    private boolean _isNew = true;

    /**
     */
    DbProcessDef() {
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
	if ((newVal != null && newVal.equals(this.processDefId) == true) || (newVal == null && this.processDefId == null))
	    return;
	this.processDefId = newVal;
	processDefId_is_modified = true;
	processDefId_is_initialized = true;
    }

    /**
     * Determine if the processdefId is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isProcessDefIdModified() {
	return processDefId_is_modified;
    }

    /**
     * Determine if the processdefId has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isProcessDefIdInitialized() {
	return processDefId_is_initialized;
    }

    /**
     * Getter method for defname
     * 
     * @return the value of defname
     */
    public String getName() {
	return name;
    }

    /**
     * Setter method for name
     * 
     * @param newVal
     *            The new value to be assigned to name
     */
    public void setName(String newVal) {
	if ((newVal != null && newVal.equals(this.name) == true) || (newVal == null && this.name == null))
	    return;
	this.name = newVal;
	name_is_modified = true;
	name_is_initialized = true;
    }

    /**
     * Determine if the name is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isNameModified() {
	return name_is_modified;
    }

    /**
     * Determine if the name has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isNameInitialized() {
	return name_is_initialized;
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
	if ((newVal != null && newVal.equals(this.description) == true) || (newVal == null && this.description == null))
	    return;
	this.description = newVal;
	description_is_modified = true;
	description_is_initialized = true;
    }

    /**
     * Determine if the description is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
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
     * Getter method for classification
     * 
     * @return the value of classification
     */
    public String getClassification() {
	return classification;
    }

    /**
     * Setter method for classification
     * 
     * @param newVal
     *            The new value to be assigned to classification
     */
    public void setClassification(String newVal) {
	if ((newVal != null && newVal.equals(this.classification) == true) || (newVal == null && this.classification == null))
	    return;
	this.classification = newVal;
	classification_is_modified = true;
	classification_is_initialized = true;
    }

    /**
     * Determine if the classification is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isClassificationModified() {
	return classification_is_modified;
    }

    /**
     * Determine if the classification has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isClassificationInitialized() {
	return classification_is_initialized;
    }

    /**
     * Getter method for systemCode
     * 
     * @return the value of systemCode
     */
    public String getSystemCode() {
	return systemCode;
    }

    /**
     * Setter method for systemCode
     * 
     * @param newVal
     *            The new value to be assigned to systemCode
     */
    public void setSystemCode(String newVal) {
	if ((newVal != null && newVal.equals(this.systemCode) == true) || (newVal == null && this.systemCode == null))
	    return;
	this.systemCode = newVal;
	systemCode_is_modified = true;
	systemCode_is_initialized = true;
    }

    /**
     * Determine if the systemcode is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isSystemCodeModified() {
	return systemCode_is_modified;
    }

    /**
     * Determine if the systemcode has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isSystemCodeInitialized() {
	return systemCode_is_initialized;
    }

    /**
     * Getter method for accessLevel
     * 
     * @return the value of accessLevel
     */
    public String getAccessLevel() {
	return accessLevel;
    }

    /**
     * Setter method for accessLevel
     * 
     * @param newVal
     *            The new value to be assigned to accessLevel
     */
    public void setAccessLevel(String newVal) {
	if ((newVal != null && newVal.equals(this.accessLevel) == true) || (newVal == null && this.accessLevel == null))
	    return;
	this.accessLevel = newVal;
	accessLevel_is_modified = true;
	accesslevel_is_initialized = true;
    }

    /**
     * Determine if the accesslevel is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isAccessLevelModified() {
	return accessLevel_is_modified;
    }

    /**
     * Determine if the accesslevel has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isAccessLevelInitialized() {
	return accesslevel_is_initialized;
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
	return processDefId_is_modified || name_is_modified || description_is_modified || classification_is_modified || systemCode_is_modified || accessLevel_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
	processDefId_is_modified = false;
	name_is_modified = false;
	description_is_modified = false;
	classification_is_modified = false;
	systemCode_is_modified = false;
	accessLevel_is_modified = false;
    }

    /**
     * Copy the passed bean into the current bean
     * 
     * @param bean
     *            the bean to copy into the current bean
     */
    public void copy(DbProcessDef bean) {
	setProcessDefId(bean.getProcessDefId());
	setName(bean.getName());
	setDescription(bean.getDescription());
	setClassification(bean.getClassification());
	setSystemCode(bean.getSystemCode());
	setAccessLevel(bean.getAccessLevel());
    }

    /**
     * Return the object string representation
     * 
     * @return the object as a string
     */
    public String toString() {
	return "\n[BPM_PROCESSDEF] " + "\n - BPM_PROCESSDEF.PROCESSDEF_ID = " + (processDefId_is_initialized ? ("[" + processDefId.toString() + "]") : "not initialized") + "" + "\n - BPM_PROCESSDEF.DEFNAME = "
		+ (name_is_initialized ? ("[" + name.toString() + "]") : "not initialized") + "" + "\n - BPM_PROCESSDEF.DESCRIPTION = " + (description_is_initialized ? ("[" + description.toString() + "]") : "not initialized") + ""
		+ "\n - BPM_PROCESSDEF.CLASSIFICATION = " + (classification_is_initialized ? ("[" + classification.toString() + "]") : "not initialized") + "" + "\n - BPM_PROCESSDEF.SYSTEMCODE = "
		+ (systemCode_is_initialized ? ("[" + systemCode.toString() + "]") : "not initialized") + "" + "\n - BPM_PROCESSDEF.ACCESSLEVEL = " + (accesslevel_is_initialized ? ("[" + accessLevel.toString() + "]") : "not initialized")
		+ "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.bpm.engine.inter.EIProcessDef#getProcessDefVersion(int)
     */
    public EIProcessDefVersion getProcessDefVersion(int version) throws BPMException {
	DbProcessDefVersionManager versionMgr = (DbProcessDefVersionManager) EIProcessDefVersionManager.getInstance();
	return versionMgr.getProcessDefVersionByVersion(this.processDefId, version);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.bpm.engine.inter.EIProcessDef#getAllProcessDefVersionIds()
     */
    public List<String> getAllProcessDefVersionIds() throws BPMException {
	List<String> vresionIds = new ArrayList<String>();
	List<EIProcessDefVersion> versions = getAllProcessDefVersions();
	for (EIProcessDefVersion version : versions) {
	    vresionIds.add(version.getProcessDefVersionId());
	}

	return vresionIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.bpm.engine.inter.EIProcessDef#getAllProcessDefVersions()
     */
    public List<EIProcessDefVersion> getAllProcessDefVersions() throws BPMException {
	DbProcessDefVersionManager versionMgr = (DbProcessDefVersionManager) EIProcessDefVersionManager.getInstance();
	return versionMgr.getAllProcessDefVersionByProcessId(this.processDefId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.bpm.engine.inter.EIProcessDef#getActiveProcessDefVersion()
     */
    public EIProcessDefVersion getActiveProcessDefVersion() throws BPMException {
	DbProcessDefVersionManager versionMgr = (DbProcessDefVersionManager) EIProcessDefVersionManager.getInstance();
	return versionMgr.getActiveProcessDefVersion(this.processDefId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {
	int size = 0;

	size += CacheSizes.sizeOfString(processDefId);
	size += CacheSizes.sizeOfString(name);
	size += CacheSizes.sizeOfString(description);
	size += CacheSizes.sizeOfString(classification);
	size += CacheSizes.sizeOfString(systemCode);
	size += CacheSizes.sizeOfString(accessLevel);

	size += CacheSizes.sizeOfBoolean() * 13;
	return size;

    }

    public boolean equals(Object o) {
	String uuid;
	if (o instanceof java.lang.String) {
	    uuid = (String) o;
	} else if (o instanceof DbProcessDef) {
	    uuid = ((DbProcessDef) o).processDefId;
	} else {
	    return false;
	}
	return uuid.equalsIgnoreCase(this.processDefId);
    }

}


