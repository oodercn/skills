/**
 * $RCSfile: EIProcessDefVersionManager.java,v $
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
import net.ooder.bpm.engine.database.DbProcessDefVersionManager;

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
public abstract class EIProcessDefVersionManager {

	private static EIProcessDefVersionManager singleton = new DbProcessDefVersionManager();

	/**
	 * Get the EIProcessDefManager singleton
	 * 
	 * @return EIProcessDefManager
	 */
	synchronized public static EIProcessDefVersionManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own EIProcessDefManager instance, this is optional. By default
	 * we provide it for you.
	 */
	synchronized public static void setInstance(
			EIProcessDefVersionManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new DbProcessDefVersion instance.
	 * 
	 * @return the new DbProcessDefVersion
	 */
	public abstract EIProcessDefVersion createProcessDefVersion();

	/**
	 * Load a DbProcessDefVersion from the BPM_PROCESSDEF_VERSION using its key
	 * fields.
	 * 
	 * @return a unique DbProcessDefVersion
	 */
	public abstract EIProcessDefVersion loadByKey(String processDefVersionId)
			throws BPMException;

	/**
	 * Load a DbProcessDefVersion table from the BPM_PROCESSDEF_VERSION table
	 * using its PROCESSDEF_ID field.
	 * 
	 * @return an array of DbProcessDefVersion
	 */
	public abstract List loadByProcessdefId(String value) throws BPMException;

	/**
	 * Delete from the BPM_PROCESSDEF_VERSION table by PROCESSDEF_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public abstract int deleteByProcessdefId(String value) throws BPMException;

	/**
	 * get the BPMProcessdefBean object from the
	 * BPM_PROCESSDEF_VERSION.PROCESSDEF_ID field.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion
	 * @return the associated BPMProcessdefBean pObject
	 */
	public abstract EIProcessDef getParentProcessDef(EIProcessDefVersion pObject)
			throws BPMException;

	/**
	 * associates the DbProcessDefVersion object to the BPMProcessdefBean
	 * object.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessdefBean object to associate to the
	 *            DbProcessDefVersion
	 * @return the associated BPMProcessdefBean pObject
	 */
	public abstract EIProcessDefVersion setParentProcessDef(
			EIProcessDefVersion pObject, EIProcessDef pObjectToBeSet);

	// ////////////////////////////////////
	public abstract List loadByWhere(String where) throws BPMException;

	// ////////////////////////////////////
	public abstract int deleteByKey(String processdefVersionId)
			throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIProcessDefVersion pObject) throws BPMException;

	/**
	 * Delete rows from BPM_PROCESSDEF_VERSION table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of DbProcessDefVersion pObjects
	 * 
	 * @param pObjects
	 *            the DbProcessDefVersion pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIProcessDefVersion[] save(EIProcessDefVersion[] pObjects)
			throws BPMException;

	/**
	 * Save into the database the DbProcessDefVersion pObject
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be saved
	 */
	public abstract EIProcessDefVersion save(EIProcessDefVersion pObject)
			throws BPMException;

	/**
	 * load a unique DbProcessDefVersion pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to look for
	 * @return the pObject matching the template
	 */
	public abstract EIProcessDefVersion loadObject(EIProcessDefVersion pObject)
			throws BPMException;

	/**
	 * load an array of DbProcessDefVersion from a template one
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion template to look for
	 * @return all the DbProcessDefVersion matching the template
	 */
	public abstract List loadObjects(EIProcessDefVersion pObject)
			throws BPMException;

	/**
	 * load the activied version in a Process Definition
	 * 
	 * @param processDefId
	 *            the Process Definition UUID
	 * @return active version in this Process or null if no version is actived
	 */
	public abstract EIProcessDefVersion getActiveProcessDefVersion(
			String processDefId) throws BPMException;

	/**
	 * 取得流程定义版本的第一个活动定义
	 * 
	 * @param processDefVersionId
	 * @return
	 * @throws BPMException
	 */
	public abstract EIActivityDef getFirstActivityDef(String processDefVersionId)
			throws BPMException;
}

