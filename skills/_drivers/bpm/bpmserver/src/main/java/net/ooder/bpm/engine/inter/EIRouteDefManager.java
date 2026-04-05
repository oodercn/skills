/**
 * $RCSfile: EIRouteDefManager.java,v $
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
import net.ooder.bpm.engine.database.DbRouteDefManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
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
public abstract class EIRouteDefManager {

	private static EIRouteDefManager singleton = new DbRouteDefManager();

	/**
	 * Get the BPMRoutedefManager singleton
	 * 
	 * @return BPMRoutedefManager
	 */
	synchronized public static EIRouteDefManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own BPMRoutedefManager instance, this is optional. By default we
	 * provide it for you.
	 */
	synchronized public static void setInstance(EIRouteDefManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new BPMRoutedefBean instance.
	 * 
	 * @return the new BPMRoutedefBean
	 */
	public abstract EIRouteDef createRouteDef();

	/**
	 * Load a BPMRoutedefBean from the BPM_ROUTEDEF using its key fields.
	 * 
	 * @return a unique BPMRoutedefBean
	 */
	public abstract EIRouteDef loadByKey(String routeDefId) throws BPMException;

	/**
	 * Load a BPMRoutedefBean table from the BPM_ROUTEDEF table using its
	 * PROCESSDEF_VERSION_ID field.
	 * 
	 * @return an array of BPMRoutedefBean
	 */
	public abstract List<EIRouteDef> loadByProcessDefVersionId(String value)
			throws BPMException;

	/**
	 * Delete from the BPM_ROUTEDEF table by PROCESSDEF_VERSION_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public abstract int deleteByProcessDefVersionId(String value)
			throws BPMException;

	/**
	 * get the EIProcessdefVersion object from the
	 * BPM_ROUTEDEF.PROCESSDEF_VERSION_ID field.
	 * 
	 * @param pObject
	 *            the EIProcessdefVersion
	 * @return the associated EIProcessdefVersion pObject
	 */
	public abstract EIProcessDefVersion getParentProcessDefVersion(
			EIRouteDef pObject) throws BPMException;

	/**
	 * associates the BPMRoutedefBean object to the BPMProcessdefVersionBean
	 * object.
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessdefVersionBean object to associate to the
	 *            BPMRoutedefBean
	 * @return the associated BPMProcessdefVersionBean pObject
	 */
	public abstract EIRouteDef setParentProcessDefVersion(EIRouteDef pObject,
			EIProcessDefVersion pObjectToBeSet);

	/**
	 * retreive an array of BPMRoutedefBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMRoutedefBean table
	 */
	public abstract List loadByWhere(String where) throws BPMException;

	/**
	 * Delete rows according to its keys
	 * 
	 * @return the number of deleted rows
	 */
	public abstract int deleteByKey(String routedefId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIRouteDef pObject) throws BPMException;

	/**
	 * Delete rows from BPM_ROUTEDEF table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of BPMRoutedefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMRoutedefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIRouteDef[] save(EIRouteDef[] pObjects)
			throws BPMException;

	/**
	 * Save into the database the BPMRoutedefBean pObject
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be saved
	 */
	public abstract EIRouteDef save(EIRouteDef pObject) throws BPMException;

	/**
	 * get all out routes from a activity
	 * 
	 * @param activityDefId
	 *            the activity that routes out from
	 * @return the list of routes that out from a activity
	 * @throws BPMException
	 */
	public abstract List getOutRoutesFromActivity(String activityDefId)
			throws BPMException;

	/**
	 * get all in routes to a activity
	 * 
	 * @param activityDefId
	 *            the activity that routes into
	 * @return the list of routes that into a activity
	 * @throws BPMException
	 */
	public abstract List getInRoutesToActivity(String activityDefId)
			throws BPMException;

}

