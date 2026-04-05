/**
 * $RCSfile: DbListener.java,v $
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

import net.ooder.bpm.engine.inter.EIListener;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统监听器定义接口数据库实现
 * </p>
 * <p>
 * 流程和活动都可以定义监听器，分别可以监听流程事件和活动事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class DbListener implements EIListener, Cacheable, Serializable {
	private String listenerId;

	private String listenerName;

	private String listenerEvent;

	private String realizeClass;
	
	private String expressionEventType;
	
	private String expressionListenerType;
	
	private String expressionStr;

	private boolean _isNew = true;

	
	DbListener() {
	
	}

	/**
	 * Getter method for listenerId
	 * 
	 * @return the value of listenerId
	 */
	public String getListenerId() {
		return listenerId;
	}

	/**
	 * Setter method for listenerId
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenerId
	 */
	public void setListenerId(String newVal) {
		if ((newVal != null && newVal.equals(this.listenerId) == true)
				|| (newVal == null && this.listenerId == null))
			return;
		this.listenerId = newVal;
	}

	/**
	 * Getter method for listenername
	 * 
	 * @return the value of listenername
	 */
	public String getListenerName() {
		return listenerName;
	}

	/**
	 * Setter method for listenername
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenername
	 */
	public void setListenerName(String newVal) {
		if ((newVal != null && newVal.equals(this.listenerName) == true)
				|| (newVal == null && this.listenerName == null))
			return;
		this.listenerName = newVal;
	}

	/**
	 * Getter method for listenerregistevent
	 * 
	 * @return the value of listenerregistevent
	 */
	public String getListenerEvent() {
		return listenerEvent;
	}

	/**
	 * Setter method for listenerregistevent
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenerregistevent
	 */
	public void setListenerEvent(String newVal) {
		if ((newVal != null && newVal.equals(this.listenerEvent) == true)
				|| (newVal == null && this.listenerEvent == null))
			return;
		this.listenerEvent = newVal;
	}

	/**
	 * Getter method for realizeclass
	 * 
	 * @return the value of realizeclass
	 */
	public String getRealizeClass() {
		return realizeClass;
	}

	/**
	 * Setter method for realizeclass
	 * 
	 * @param newVal
	 *            The new value to be assigned to realizeclass
	 */
	public void setRealizeClass(String newVal) {
		if ((newVal != null && newVal.equals(this.realizeClass) == true)
				|| (newVal == null && this.realizeClass == null))
			return;
		this.realizeClass = newVal;
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
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbListener bean) {
		setListenerId(bean.getListenerId());
		setListenerName(bean.getListenerName());
		setListenerEvent(bean.getListenerEvent());
		setRealizeClass(bean.getRealizeClass());
		this.setExpressionEventType(bean.getExpressionEventType());
		this.setExpressionListenerType(bean.getExpressionListenerType());
		this.setExpressionStr(bean.getExpressionStr());
	
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_ACTIVITYLISTENER] "
				+ "\n - BPM_ACTIVITYLISTENER.LISTENER_ID = "
				+ (listenerId != null ? ("[" + listenerId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYLISTENER.LISTENERNAME = "
				+ (listenerName != null ? ("[" + listenerName.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYLISTENER.LISTENERREGISTEVENT = "
				+ (listenerEvent != null ? ("[" + listenerEvent.toString() + "]")
						: "not initialized")
						+ "\n - BPM_ACTIVITYLISTENER.EXPRESSIONEVENTYPE = "
				+ (listenerEvent != null ? ("[" + expressionEventType.toString() + "]")
						: "not initialized")
						+ "\n - BPM_ACTIVITYLISTENER.EXPRESSIONLISENTERTYPE = "
				+ (listenerEvent != null ? ("[" + expressionListenerType.toString() + "]")
						: "not initialized")
						+ "\n - BPM_ACTIVITYLISTENER.EXPRESSIONSTR = "
				+ (listenerEvent != null ? ("[" + expressionStr.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYLISTENER.REALIZECLASS = "
				+ (realizeClass != null ? ("[" + realizeClass.toString() + "]")
						: "not initialized") + "";
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {

		int size = 0;

		size += CacheSizes.sizeOfString(listenerId);
		size += CacheSizes.sizeOfString(listenerName);
		size += CacheSizes.sizeOfString(listenerEvent);
		size += CacheSizes.sizeOfString(realizeClass);
		size += CacheSizes.sizeOfString(expressionEventType);
		size += CacheSizes.sizeOfString(expressionListenerType);
		size += CacheSizes.sizeOfString(expressionStr);
		size += CacheSizes.sizeOfBoolean();

		return size;
	}

	public String getExpressionEventType() {
		
		return expressionEventType;
	}

	public void setExpressionEventType(String newVal) {
		if ((newVal != null && newVal.equals(this.expressionEventType) == true)
				|| (newVal == null && this.expressionEventType == null))
			return;
		this.expressionEventType = newVal;
	}

	public String getExpressionListenerType() {
		return expressionListenerType;
	}

	public void setExpressionListenerType(String newVal) {
		if ((newVal != null && newVal.equals(this.expressionListenerType) == true)
				|| (newVal == null && this.expressionListenerType == null))
			return;
		
		this.expressionListenerType = newVal;
	}

	public String getExpressionStr() {
		return expressionStr;
	}

	public void setExpressionStr(String newVal) {
		if ((newVal != null && newVal.equals(this.expressionStr) == true)
				|| (newVal == null && this.expressionStr == null))
			return;
		this.expressionStr = newVal;
	}

	



}


