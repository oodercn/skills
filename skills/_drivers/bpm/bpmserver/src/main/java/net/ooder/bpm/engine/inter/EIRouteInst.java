/**
 * $RCSfile: EIRouteInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */

package net.ooder.bpm.engine.inter;

import java.util.Date;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由实例引擎内部接口
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2003
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public interface EIRouteInst {
	/**
	 * Getter method for routeInstId
	 * 
	 * @return the value of routeInstId
	 */
	public abstract String getRouteInstId();

	/**
	 * Setter method for routeInstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeInstId
	 */
	public abstract void setRouteInstId(String newVal);

	/**
	 * Getter method for processInstId
	 * 
	 * @return the value of processInstId
	 */
	public abstract String getProcessInstId();

	/**
	 * Setter method for processInstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processInstId
	 */
	public abstract void setProcessInstId(String newVal);

	/**
	 * Getter method for routeName
	 * 
	 * @return the value of routeName
	 */
	public abstract String getRouteName();

	/**
	 * Setter method for routeName
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeName
	 */
	public abstract void setRouteName(String newVal);

	/**
	 * Getter method for description
	 * 
	 * @return the value of description
	 */
	public abstract String getDescription();

	/**
	 * Setter method for description
	 * 
	 * @param newVal
	 *            The new value to be assigned to description
	 */
	public abstract void setDescription(String newVal);

	/**
	 * Getter method for fromActivityId
	 * 
	 * @return the value of fromActivityId
	 */
	public abstract String getFromActivityId();

	public abstract EIActivityInstHistory getFromActivityHistory()
			throws BPMException;

	/**
	 * Setter method for fromActivityId
	 * 
	 * @param newVal
	 *            The new value to be assigned to fromActivityId
	 */
	public abstract void setFromActivityId(String newVal);

	/**
	 * Getter method for toActivityId
	 * 
	 * @return the value of toActivityId
	 */
	public abstract String getToActivityId();

	/**
	 * Setter method for toActivityId
	 * 
	 * @param newVal
	 *            The new value to be assigned to toActivityId
	 */
	public abstract void setToActivityId(String newVal);

	/**
	 * Getter method for routeDirection
	 * 
	 * @return the value of routeDirection
	 */
	public abstract RouteDirction getRouteDirection();

	/**
	 * Setter method for routeDirection
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeDirection
	 */
	public abstract void setRouteDirection(RouteDirction newVal);

	/**
	 * Getter method for routeType
	 * 
	 * @return the value of routeType
	 */
	public abstract RouteInstType getRouteType();

	/**
	 * Setter method for routeType
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeType
	 */
	public abstract void setRouteType(RouteInstType newVal);

	/**
	 * Getter method for routeTime
	 * 
	 * @return the value of routeTime
	 */
	public abstract Date getRouteTime();

	/**
	 * Setter method for routeTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeTime
	 */
	public abstract void setRouteTime(Date newVal);

	/**
	 * 取得与该路由实例相对应的流程实例
	 * 
	 * @return 流程实例
	 */
	public EIProcessInst getProcessInst() throws BPMException;
}
