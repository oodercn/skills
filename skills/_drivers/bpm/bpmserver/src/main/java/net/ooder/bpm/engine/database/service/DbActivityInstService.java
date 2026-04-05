/**
 * $RCSfile: DbActivityInstMethod.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.service;

import java.io.Serializable;

import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认权限中的活动实例与设备应用关系
 * </p>
 * <p>
 * The java object mapped on the Relation database table DT_ACTIVITY_SERVICE
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @version 1.0
 */
public class DbActivityInstService implements Serializable {

    private String activityInstServiceId;

    private boolean activityinstService_is_modified = false;

    private boolean activityinstService_is_initialized = false;

    private String processInstId;

    private boolean processinstId_is_modified = false;

    private boolean processinstId_is_initialized = false;

    private String activityInstId;

    private boolean activityinstId_is_modified = false;

    private boolean activityinstId_is_initialized = false;

    private String requestParams;

    private boolean requestParams_is_modified = false;

    private boolean requestParams_is_initialized = false;

  

    private String url;

    private boolean url_is_modified = false;

    private boolean url_is_initialized = false;

    private String serviceId;

    private boolean serviceId_is_modified = false;

    private boolean serviceId_is_initialized = false;

    private String serviceGrpCode;

    private boolean serviceGrpCode_is_modified = false;

    private boolean serviceGrpCode_is_initialized = false;

    private String serviceActivityState;

    private boolean serviceActivityState_is_modified = false;

    private boolean serviceActivityState_is_initialized = false;

    private String lastServiceGrp;

    private boolean lastServiceGrp_is_modified = false;

    private boolean lastServiceGrp_is_initialized = false;

    private boolean _isNew = true;

    /**
     */
    DbActivityInstService() {
    }

    /**
     * Getter method for activityinstService
     * 
     * @return the value of activityinstService
     */
    public String getActivityInstServiceId() {
	return activityInstServiceId;
    }

    /**
     * Setter method for activityinstService
     * 
     * @param newVal
     *            The new value to be assigned to activityinstService
     */
    public void setActivityInstServiceId(String newVal) {
	if ((newVal != null && newVal.equals(this.activityInstServiceId) == true) || (newVal == null && this.activityInstServiceId == null))
	    return;
	this.activityInstServiceId = newVal;
	activityinstService_is_modified = true;
	activityinstService_is_initialized = true;
    }

    public boolean isActivityinstServiceIdModified() {
	return activityinstService_is_modified;
    }

    public boolean isActivityinstServiceIdInitialized() {
	return activityinstService_is_initialized;
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
     * Getter method for requestParams
     * 
     * @return the value of requestParams
     */
    public String getRequestParams() {
	return requestParams;
    }

    /**
     * Setter method for requestParams
     * 
     * @param newVal
     *            The new value to be assigned to requestParams
     */
    public void setRequestParams(String newVal) {
	if ((newVal != null && newVal.equals(this.requestParams) == true) || (newVal == null && this.requestParams == null))
	    return;
	this.requestParams = newVal;
	requestParams_is_modified = true;
	requestParams_is_initialized = true;
    }

    /**
     * Determine if the requestParams is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isRequestParamsModified() {
	return requestParams_is_modified;
    }

    /**
     * Determine if the requestParams has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isRequestParamsInitialized() {
	return requestParams_is_initialized;
    }

    /**
     * Getter method for url
     * 
     * @return the value of url
     */
    public String getUrl() {
	return url;
    }

    /**
     * Setter method for url
     * 
     * @param newVal
     *            The new value to be assigned to url
     */
    public void setUrl(String newVal) {
	if ((newVal != null && newVal.equals(this.url) == true) || (newVal == null && this.url == null))
	    return;
	this.url = newVal;
	url_is_modified = true;
	url_is_initialized = true;
    }

    /**
     * Determine if the url is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isUrlModified() {
	return url_is_modified;
    }

    /**
     * Determine if the url has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isUrlInitialized() {
	return url_is_initialized;
    }

    /**
     * Getter method for serviceGrpCode
     * 
     * @return the value of serviceGrpCode
     */
    public RightGroupEnums getServiceGrpCode() {
	return RightGroupEnums.fromType(serviceGrpCode);
    }

    /**
     * Setter method for serviceGrpCode
     * 
     * @param newVal
     *            The new value to be assigned to serviceGrpCode
     */
    public void setServiceGrpCode(RightGroupEnums group) {
	String newVal = group.getType();
	if ((newVal != null && newVal.equals(this.serviceGrpCode) == true) || (newVal == null && this.serviceGrpCode == null))
	    return;
	this.serviceGrpCode = newVal;
	serviceGrpCode_is_modified = true;
	serviceGrpCode_is_initialized = true;
    }

    /**
     * Determine if the serviceGrpCode is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isServiceGrpCodeModified() {
	return serviceGrpCode_is_modified;
    }

    /**
     * Determine if the serviceGrpCode has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isServiceGrpCodeInitialized() {
	return serviceGrpCode_is_initialized;
    }

    /**
     * Getter method for serviceActivityState
     * 
     * @return the value of serviceActivityState
     */
    public RightPerformStatus getServiceActivityState() {
	return RightPerformStatus.fromType(serviceActivityState);
    }

    /**
     * Setter method for serviceActivityState
     * 
     * @param newVal
     *            The new value to be assigned to serviceActivityState
     */
    public void setServiceActivityState(RightPerformStatus state) {
	String newVal = state.getType();
	if ((newVal != null && newVal.equals(this.serviceActivityState) == true) || (newVal == null && this.serviceActivityState == null))
	    return;
	this.serviceActivityState = newVal;
	serviceActivityState_is_modified = true;
	serviceActivityState_is_initialized = true;
    }

    /**
     * Determine if the serviceActivityState is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isServiceActivityStateModified() {
	return serviceActivityState_is_modified;
    }

    /**
     * Determine if the serviceActivityState has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isServiceActivityStateInitialized() {
	return serviceActivityState_is_initialized;
    }

    /**
     * Getter method for lastServiceGrp
     * 
     * @return the value of lastServiceGrp
     */
    public RightGroupEnums getLastServiceGrp() {
	return RightGroupEnums.fromType(lastServiceGrp);
    }

    /**
     * Setter method for lastServiceGrp
     * 
     * @param newVal
     *            The new value to be assigned to lastServiceGrp
     */
    public void setLastServiceGrp(RightGroupEnums group) {
	String newVal = group.getType();
	if ((newVal != null && newVal.equals(this.lastServiceGrp) == true) || (newVal == null && this.lastServiceGrp == null))
	    return;
	this.lastServiceGrp = newVal;
	lastServiceGrp_is_modified = true;
	lastServiceGrp_is_initialized = true;
    }

    /**
     * Determine if the lastServiceGrp is modified or not
     * 
     * @return true if the field has been modified, false if the field has not been modified
     */
    public boolean isLastServiceGrpModified() {
	return lastServiceGrp_is_modified;
    }

    /**
     * Determine if the lastServiceGrp has been initialized or not
     * 
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isLastServiceGrpInitialized() {
	return lastServiceGrp_is_initialized;
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
	return activityinstService_is_modified || processinstId_is_modified || activityinstId_is_modified || requestParams_is_modified ||  url_is_modified || serviceGrpCode_is_modified
		|| serviceActivityState_is_modified || lastServiceGrp_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
	activityinstService_is_modified = false;
	processinstId_is_modified = false;
	activityinstId_is_modified = false;
	requestParams_is_modified = false;
	url_is_modified = false;
	serviceGrpCode_is_modified = false;
	serviceActivityState_is_modified = false;
	lastServiceGrp_is_modified = false;
    }

    /**
     * Copy the passed bean into the current bean
     * 
     * @param bean
     *            the bean to copy into the current bean
     */
    public void copy(DbActivityInstService bean) {
	setActivityInstServiceId(bean.getActivityInstServiceId());
	setProcessInstId(bean.getProcessInstId());
	setActivityInstId(bean.getActivityInstId());
	setRequestParams(bean.getRequestParams());
	setUrl(bean.getUrl());
	setServiceGrpCode(bean.getServiceGrpCode());
	setServiceActivityState(bean.getServiceActivityState());
	setLastServiceGrp(bean.getLastServiceGrp());
	this.setRequestParams(bean.getRequestParams());
	
    }

    /**
     * Return the object string representation
     * 
     * @return the object as a string
     */
    public String toString() {
	return "\n[DT_ACTIVITY_SERVICE] " + "\n - DT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID = " + (activityinstService_is_initialized ? ("[" + activityInstServiceId.toString() + "]") : "not initialized") + ""
		+ "\n - DT_ACTIVITY_SERVICE.PROCESSINST_ID = " + (processinstId_is_initialized ? ("[" + processInstId.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.ACTIVITYINST_ID = "
		+ (activityinstId_is_initialized ? ("[" + activityInstId.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.SERVICE_ID = "
		+ (requestParams_is_initialized ? ("[" + requestParams.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.URL = "
		+ (url_is_initialized ? ("[" + url.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.SERVICE_GRP_CODE = "
		+ (serviceGrpCode_is_initialized ? ("[" + serviceGrpCode.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.SERVICE_ACTIVITY_STATE = "
		+ (serviceActivityState_is_initialized ? ("[" + serviceActivityState.toString() + "]") : "not initialized") + "" + "\n - DT_ACTIVITY_SERVICE.LAST_SERVICE_GRP = "
		+ (lastServiceGrp_is_initialized ? ("[" + lastServiceGrp.toString() + "]") : "not initialized") + "";
    }

}

