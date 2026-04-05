/**
 * $RCSfile: EIListener.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import net.ooder.bpm.webservice.XMLParse;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
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
public interface EIListener<T> {
	/**
	 * Getter method for listenerId
	 * 
	 * @return the value of listenerId
	 */
	public abstract String getListenerId();

	/**
	 * Setter method for listenerId
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenerId
	 */
	public abstract void setListenerId(String newVal);

	/**
	 * Getter method for listenername
	 * 
	 * @return the value of listenername
	 */
	public abstract String getListenerName();

	/**
	 * Setter method for listenername
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenername
	 */
	public abstract void setListenerName(String newVal);

	/**
	 * Getter method for listenerregistevent
	 * 
	 * @return the value of listenerregistevent
	 */
	public abstract String getListenerEvent();

	/**
	 * Setter method for listenerregistevent
	 * 
	 * @param newVal
	 *            The new value to be assigned to listenerregistevent
	 */
	public abstract void setListenerEvent(String newVal);

	/**
	 * Getter method for realizeclass
	 * 
	 * @return the value of realizeclass
	 */
	public abstract String getRealizeClass();

	/**
	 * Setter method for realizeclass
	 * 
	 * @param newVal
	 *            The new value to be assigned to realizeclass
	 */
	public abstract void setRealizeClass(String newVal);
	
	/**
	 * 
	 * @param newVal
	 */
	public abstract void setExpressionEventType(String newVal);
	
	/**
	 * 
	 * @param newVal
	 */
	public abstract void setExpressionListenerType(String newVal);
	
	/**
	 * 
	 * @param newVal
	 */
	public abstract void setExpressionStr(String newVal);
		
	
	public  abstract String getExpressionEventType();
	
	
	public abstract String getExpressionListenerType();
	
	
	public abstract String getExpressionStr();
	
	
	
	
	
	
	
	
	
	
	
	
}
