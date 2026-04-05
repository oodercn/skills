/**
 * $RCSfile: EIProcessDefManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:58 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbProcessDefManager;

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
public abstract class EIProcessDefManager {

	private static EIProcessDefManager singleton = new DbProcessDefManager();

	/**
	 * Get the EIProcessDefManager singleton
	 * 
	 * @return EIProcessDefManager
	 */
	synchronized public static EIProcessDefManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own EIProcessDefManager instance, this is optional. By default
	 * we provide it for you.
	 */
	synchronized public static void setInstance(EIProcessDefManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new DbProcessDef instance.
	 * 
	 * @return the new DbProcessDef
	 */
	public abstract EIProcessDef createProcessDef();

	/**
	 * Load a DbProcessDef from the BPM_PROCESSDEF using its key fields.
	 * 
	 * @return a unique DbProcessDef
	 */
	public abstract EIProcessDef loadByKey(String processDefId)
			throws BPMException;

	// ////////////////////////////////////
	public abstract List loadAll();

	/**
	 * retreive an array of BPMProcessdefBean given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMProcessdefBean table
	 */
	public abstract List loadByWhere(String where) throws BPMException;

	public abstract int deleteByKey(String processdefId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIProcessDef processDef) throws BPMException;

	/**
	 * Delete rows from BPM_PROCESSDEF table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of BPMProcessdefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMProcessdefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIProcessDef[] save(EIProcessDef[] processDefs)
			throws BPMException;

	/**
	 * Save into the database the BPMProcessdefBean pObject
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be saved
	 */
	public abstract EIProcessDef save(EIProcessDef processDef)
			throws BPMException;

	/**
	 * get the number of rows of the table BPM_PROCESSDEF
	 * 
	 * @return the number of rows returned
	 */
	public abstract int count() throws BPMException;

	/**
	 * get the number of rows of the table BPM_PROCESSDEF with a where clausis
	 * 
	 * @param where
	 *            the restriction clausis
	 * @return the number of rows returned
	 */
	public abstract int countByWhere(String where) throws BPMException;

	/**
	 * look for the number of element of a specific BPMProcessdefBean pObject
	 * given a c
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return the number of rows returned
	 */
	public abstract int count(EIProcessDef processDef) throws BPMException;

	/**
	 * does a specific BPMProcessdefBean pObject exists ?
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return true if an pObject in the database matches the BPMProcessdefBean
	 *         pObject
	 */
	public abstract boolean exists(EIProcessDef processDef) throws BPMException;

	/**
	 * load a unique BPMProcessdefBean pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return the pObject matching the template
	 */
	public abstract EIProcessDef loadObject(EIProcessDef processDef)
			throws BPMException;

	/**
	 * load an array of BPMProcessdefBean from a template one
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean template to look for
	 * @return all the BPMProcessdefBean matching the template
	 */
	public abstract List loadObjects(EIProcessDef processDef)
			throws BPMException;
}
