/**
 * $RCSfile: DbRouteInst.java,v $
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
import java.util.Date;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityInstHistory;
import net.ooder.bpm.engine.inter.EIActivityInstHistoryManager;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.bpm.engine.inter.EIRouteInst;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

public class DbRouteInst implements EIRouteInst, Cacheable, Serializable {
	private String routeInstId;

	private boolean routeinstId_is_modified = false;

	private boolean routeinstId_is_initialized = false;

	private String processInstId;

	private boolean processinstId_is_modified = false;

	private boolean processinstId_is_initialized = false;

	private String routeName;

	private boolean routename_is_modified = false;

	private boolean routename_is_initialized = false;

	private String description;

	private boolean description_is_modified = false;

	private boolean description_is_initialized = false;

	private String fromActivityId;

	private boolean fromactivityId_is_modified = false;

	private boolean fromactivityId_is_initialized = false;

	private String toActivityId;

	private boolean toactivityId_is_modified = false;

	private boolean toactivityId_is_initialized = false;

	private String routeDirection;

	private boolean routedirection_is_modified = false;

	private boolean routedirection_is_initialized = false;

	private String routeType;

	private boolean routetype_is_modified = false;

	private boolean routetype_is_initialized = false;

	private Date routeTime;

	private boolean routetime_is_modified = false;

	private boolean routetime_is_initialized = false;

	private boolean _isNew = true;

	/**
	 */
	DbRouteInst() {
	}

	/**
	 * Getter method for routeInstId
	 * 
	 * @return the value of routeInstId
	 */
	public String getRouteInstId() {
		return routeInstId;
	}

	/**
	 * Setter method for routeInstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeInstId
	 */
	public void setRouteInstId(String newVal) {
		if ((newVal != null && newVal.equals(this.routeInstId) == true)
				|| (newVal == null && this.routeInstId == null))
			return;
		this.routeInstId = newVal;
		routeinstId_is_modified = true;
		routeinstId_is_initialized = true;
	}

	/**
	 * Determine if the routeInstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRouteinstIdModified() {
		return routeinstId_is_modified;
	}

	/**
	 * Determine if the routeInstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRouteinstIdInitialized() {
		return routeinstId_is_initialized;
	}

	/**
	 * Getter method for processInstId
	 * 
	 * @return the value of processInstId
	 */
	public String getProcessInstId() {
		return processInstId;
	}

	/**
	 * Setter method for processInstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processInstId
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
	 * Determine if the processInstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstIdModified() {
		return processinstId_is_modified;
	}

	/**
	 * Determine if the processInstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstIdInitialized() {
		return processinstId_is_initialized;
	}

	/**
	 * Getter method for routeName
	 * 
	 * @return the value of routeName
	 */
	public String getRouteName() {
		return routeName;
	}

	/**
	 * Setter method for routeName
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeName
	 */
	public void setRouteName(String newVal) {
		if ((newVal != null && newVal.equals(this.routeName) == true)
				|| (newVal == null && this.routeName == null))
			return;
		this.routeName = newVal;
		routename_is_modified = true;
		routename_is_initialized = true;
	}

	/**
	 * Determine if the routeName is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutenameModified() {
		return routename_is_modified;
	}

	/**
	 * Determine if the routeName has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutenameInitialized() {
		return routename_is_initialized;
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
	 * Getter method for fromActivityId
	 * 
	 * @return the value of fromActivityId
	 */
	public String getFromActivityId() {
		return fromActivityId;
	}

	public EIActivityInstHistory getFromActivityHistory() throws BPMException {
		if (fromActivityId == null) {
			return null;
		}
		return EIActivityInstHistoryManager.getInstance().loadByKey(
				fromActivityId);
	}

	/**
	 * Setter method for fromActivityId
	 * 
	 * @param newVal
	 *            The new value to be assigned to fromActivityId
	 */
	public void setFromActivityId(String newVal) {
		if ((newVal != null && newVal.equals(this.fromActivityId) == true)
				|| (newVal == null && this.fromActivityId == null))
			return;
		this.fromActivityId = newVal;
		fromactivityId_is_modified = true;
		fromactivityId_is_initialized = true;
	}

	/**
	 * Determine if the fromActivityId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isFromactivityIdModified() {
		return fromactivityId_is_modified;
	}

	/**
	 * Determine if the fromActivityId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isFromactivityIdInitialized() {
		return fromactivityId_is_initialized;
	}

	/**
	 * Getter method for toActivityId
	 * 
	 * @return the value of toActivityId
	 */
	public String getToActivityId() {
		return toActivityId;
	}

	/**
	 * Setter method for toActivityId
	 * 
	 * @param newVal
	 *            The new value to be assigned to toActivityId
	 */
	public void setToActivityId(String newVal) {
		if ((newVal != null && newVal.equals(this.toActivityId) == true)
				|| (newVal == null && this.toActivityId == null))
			return;
		this.toActivityId = newVal;
		toactivityId_is_modified = true;
		toactivityId_is_initialized = true;
	}

	/**
	 * Determine if the toActivityId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isToactivityIdModified() {
		return toactivityId_is_modified;
	}

	/**
	 * Determine if the toActivityId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isToactivityIdInitialized() {
		return toactivityId_is_initialized;
	}

	/**
	 * Getter method for routeDirection
	 * 
	 * @return the value of routeDirection
	 */
	public RouteDirction getRouteDirection() {
	    
		return RouteDirction.fromType(routeDirection);
	}

	/**
	 * Setter method for routeDirection
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeDirection
	 */
	public void setRouteDirection(RouteDirction routeDirection) {
	    String newVal=routeDirection.getType();
		if ((newVal != null && newVal.equals(this.routeDirection) == true)
				|| (newVal == null && this.routeDirection == null))
			return;
		this.routeDirection = newVal;
		routedirection_is_modified = true;
		routedirection_is_initialized = true;
	}

	/**
	 * Determine if the routeDirection is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutedirectionModified() {
		return routedirection_is_modified;
	}

	/**
	 * Determine if the routeDirection has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutedirectionInitialized() {
		return routedirection_is_initialized;
	}

	/**
	 * Getter method for routeType
	 * 
	 * @return the value of routeType
	 */
	public RouteInstType getRouteType() {
		return RouteInstType.fromType(routeType);
	}

	/**
	 * Setter method for routeType
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeType
	 */
	public void setRouteType(RouteInstType routeType) {
	    String newVal= routeType.getType();
	    
		if ((newVal != null && newVal.equals(this.routeType) == true)
				|| (newVal == null && this.routeType == null))
			return;
		this.routeType = newVal;
		routetype_is_modified = true;
		routetype_is_initialized = true;
	}

	/**
	 * Determine if the routeType is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutetypeModified() {
		return routetype_is_modified;
	}

	/**
	 * Determine if the routeType has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutetypeInitialized() {
		return routetype_is_initialized;
	}

	/**
	 * Getter method for routeTime
	 * 
	 * @return the value of routeTime
	 */
	public Date getRouteTime() {
		return routeTime;
	}

	/**
	 * Setter method for routeTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeTime
	 */
	public void setRouteTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.routeTime) == true)
				|| (newVal == null && this.routeTime == null))
			return;
		this.routeTime = newVal;
		routetime_is_modified = true;
		routetime_is_initialized = true;
	}

	/**
	 * Determine if the routeTime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutetimeModified() {
		return routetime_is_modified;
	}

	/**
	 * Determine if the routeTime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutetimeInitialized() {
		return routetime_is_initialized;
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
		return routeinstId_is_modified || processinstId_is_modified
				|| routename_is_modified || description_is_modified
				|| fromactivityId_is_modified || toactivityId_is_modified
				|| routedirection_is_modified || routetype_is_modified
				|| routetime_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		routeinstId_is_modified = false;
		processinstId_is_modified = false;
		routename_is_modified = false;
		description_is_modified = false;
		fromactivityId_is_modified = false;
		toactivityId_is_modified = false;
		routedirection_is_modified = false;
		routetype_is_modified = false;
		routetime_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbRouteInst bean) {
		setRouteInstId(bean.getRouteInstId());
		setProcessInstId(bean.getProcessInstId());
		setRouteName(bean.getRouteName());
		setDescription(bean.getDescription());
		setFromActivityId(bean.getFromActivityId());
		setToActivityId(bean.getToActivityId());
		setRouteDirection(bean.getRouteDirection());
		setRouteType(bean.getRouteType());
		setRouteTime(bean.getRouteTime());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_ROUTEINST] "
				+ "\n - BPM_ROUTEINST.ROUTEINST_ID = "
				+ (routeinstId_is_initialized ? ("[" + routeInstId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.PROCESSINST_ID = "
				+ (processinstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.ROUTENAME = "
				+ (routename_is_initialized ? ("[" + routeName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.DESCRIPTION = "
				+ (description_is_initialized ? ("[" + description.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.FROMACTIVITY_ID = "
				+ (fromactivityId_is_initialized ? ("["
						+ fromActivityId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.TOACTIVITY_ID = "
				+ (toactivityId_is_initialized ? ("[" + toActivityId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.ROUTEDIRECTION = "
				+ (routedirection_is_initialized ? ("["
						+ routeDirection.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.ROUTETYPE = "
				+ (routetype_is_initialized ? ("[" + routeType.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEINST.ROUTETIME = "
				+ (routetime_is_initialized ? ("[" + routeTime.toString() + "]")
						: "not initialized") + "";
	}

	public int getCachedSize() {
		int size = 0;
		size += CacheSizes.sizeOfString(description); // 路由实例描述
		size += CacheSizes.sizeOfString(fromActivityId);
		size += CacheSizes.sizeOfString(toActivityId);
		size += CacheSizes.sizeOfString(processInstId);
		size += CacheSizes.sizeOfString(routeDirection);
		size += CacheSizes.sizeOfString(routeInstId);
		size += CacheSizes.sizeOfString(routeName);
		size += CacheSizes.sizeOfString(routeType);
		size += CacheSizes.sizeOfDate(); // Route Time

		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIRouteInst#getProcessInst()
	 */
	public EIProcessInst getProcessInst() throws BPMException {
		return EIProcessInstManager.getInstance().loadByKey(processInstId);
	}

}


