/**
 * $RCSfile: EIActivityDefManager.java,v $
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
import net.ooder.bpm.engine.database.DbActivityDefManager;

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
public abstract class EIActivityDefManager {

	private static EIActivityDefManager singleton = new DbActivityDefManager();

	/**
	 * Get the BPMActivitydefManager singleton
	 * 
	 * @return BPMActivitydefManager
	 */
	synchronized public static EIActivityDefManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own BPMActivitydefManager instance, this is optional. By default
	 * we provide it for you.
	 */
	synchronized public static void setInstance(EIActivityDefManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new EIActivityDef instance.
	 * 
	 * @return the new EIActivityDef
	 */
	public abstract EIActivityDef createActivityDef();

	/**
	 * Load a BPMActivitydefBean from the BPM_ACTIVITYDEF using its key fields.
	 * 
	 * @return a unique BPMActivitydefBean
	 */
	public abstract EIActivityDef loadByKey(String activitydefId)
			throws BPMException;

	/**
	 * Load a DbActivityDef table from the BPM_ACTIVITYDEF table using its
	 * PROCESSDEF_VERSION_ID field.
	 * 
	 * @return an array of DbActivityDef
	 */
	public abstract List<EIActivityDef> loadByProcessDefVersionId(String value)
			throws BPMException;

	/**
	 * Delete from the BPM_ACTIVITYDEF table by PROCESSDEF_VERSION_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public abstract int deleteByProcessDefVersionId(String value)
			throws BPMException;

	/**
	 * get the BPMProcessdefVersionBean object from the
	 * BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID field.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean
	 * @return the associated BPMProcessdefVersionBean pObject
	 */
	public abstract EIProcessDefVersion getProcessDefVersion(
			EIActivityDef pObject) throws BPMException;

	/**
	 * associates the BPMActivitydefBean object to the BPMProcessdefVersionBean
	 * object.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessdefVersionBean object to associate to the
	 *            BPMActivitydefBean
	 * @return the associated BPMProcessdefVersionBean pObject
	 */
	public abstract EIActivityDef setProcessDefVersion(EIActivityDef pObject,
			EIProcessDefVersion pObjectToBeSet);

	/**
	 * retreive an array of BPMActivitydefBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMActivitydefBean table
	 */
	public abstract List loadByWhere(String where) throws BPMException;

	/**
	 * Delete rows according to its keys
	 * 
	 * @return the number of deleted rows
	 */
	public abstract int deleteByKey(String activitydefId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIActivityDef pObject) throws BPMException;

	/**
	 * Delete rows from BPM_ACTIVITYDEF table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of BPMActivitydefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMActivitydefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIActivityDef[] save(EIActivityDef[] pObjects)
			throws BPMException;

	/**
	 * Save into the database the BPMActivitydefBean pObject
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be saved
	 */
	public abstract EIActivityDef save(EIActivityDef pObject)
			throws BPMException;

	/**
	 * get fisrt activity in a process
	 * 
	 * @param value
	 *            the process version UUID
	 * @return the first activity definiution or null if can't found the start
	 *         activity in this process
	 * @throws BPMException
	 *             throw a BPMException when there is more than one start
	 *             activity in this process, that is a definiton error please
	 *             check you process definition.
	 */
	public abstract EIActivityDef getFirstActivityDefInProcess(String value)
			throws BPMException;

}

