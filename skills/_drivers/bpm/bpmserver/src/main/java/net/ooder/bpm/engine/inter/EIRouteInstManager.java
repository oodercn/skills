/**
 * $RCSfile: EIRouteInstManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */

package net.ooder.bpm.engine.inter;

import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbRouteInstManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由实例引擎内部管理器
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
public abstract class EIRouteInstManager {
	private static EIRouteInstManager singleton = new DbRouteInstManager();

	/**
	 * Get the EIRouteInstManager singleton
	 * 
	 * @return EIRouteInstManager
	 */
	synchronized public static EIRouteInstManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own EIRouteInstManager instance, this is optional. By default we
	 * provide it for you.
	 */
	synchronized public static void setInstance(EIRouteInstManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RouteInst instance.
	 * 
	 * @return the new RouteInst
	 */
	public abstract EIRouteInst createRouteInst();

	/**
	 * Load a RouteInst from the BPM_ROUTEINST using its key fields.
	 * 
	 * @return a unique RouteInst
	 */
	public abstract EIRouteInst loadByKey(String routeinstId)
			throws BPMException;

	public abstract List loadAll() throws BPMException;

	public abstract List loadByWhere(String where) throws BPMException;

	public abstract int deleteByKey(String routeinstId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RouteInst object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIRouteInst pObject) throws BPMException;

	/**
	 * Delete rows from BPM_ROUTEINST table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of RouteInst pObjects
	 * 
	 * @param pObjects
	 *            the RouteInst pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIRouteInst[] save(EIRouteInst[] pObjects)
			throws BPMException;

	// class+
	public abstract EIRouteInst save(EIRouteInst pObject) throws BPMException;

	/**
	 * 取得指向该活动实例的所有路由实例
	 * 
	 * @param from
	 * @return
	 */
	public abstract List getRouteInsts(EIActivityInst activityInst)
			throws BPMException;
}
