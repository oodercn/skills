/**
 * $RCSfile: EIActivityInstHistoryManager.java,v $
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
import net.ooder.bpm.engine.database.DbActivityInstHistory;
import net.ooder.bpm.engine.database.DbActivityInstHistoryManager;

/**
 * <p>
 * Title: BPM
 * </p>
 * <p>
 * Description: 活动实例历史管理器引擎接口
 * </p>
 * <p>
 * Copyright: Copyright itjds (c) 2003
 * </p>
 * <p>
 * Company: itjds
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public abstract class EIActivityInstHistoryManager {
	private static EIActivityInstHistoryManager singleton = new DbActivityInstHistoryManager();

	/**
	 * Get the ActivityinstanceManager singleton
	 * 
	 * @return ActivityinstanceManager
	 */
	synchronized public static EIActivityInstHistoryManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own BPMActivityinstanceHistoryManager instance, this is
	 * optional. By default we provide it for you.
	 */
	synchronized public static void setInstance(
			EIActivityInstHistoryManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new ActivityHistory instance.
	 * 
	 * @return the new ActivityHistory
	 */
	public abstract EIActivityInstHistory createActivityHistory();

	/**
	 * Creates a new ActivityHistory instance, use the specify activity instance
	 * as its prototype
	 * 
	 * @param activityInst
	 *            activity instance to be in history
	 * @return the new ActivityHistory
	 */
	public abstract EIActivityInstHistory saveActivityInstAsHistory(
			EIActivityInst activityInst) throws BPMException;

	/**
	 * Load a ActivityHistory from the BPM_ACTIVITYHISTORY using its key fields.
	 * 
	 * @return a unique ActivityHistory
	 */
	public abstract EIActivityInstHistory loadByKey(String activityHistoryId)
			throws BPMException;

	/**
	 * Load a ActivityHistory table from the BPM_ACTIVITYHISTORY table using its
	 * PROCESSINST_ID field.
	 * 
	 * @return an array of ActivityHistory
	 */
	public abstract EIActivityInstHistory[] loadByProcessInstId(String value)
			throws BPMException;

	/**
	 * Delete from the BPM_ACTIVITYHISTORY table by PROCESSINST_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public abstract int deleteByProcessInstId(String value) throws BPMException;

	/**
	 * get the Processinstance object from the
	 * BPM_ACTIVITYHISTORY.PROCESSINST_ID field.
	 * 
	 * @param pObject
	 *            the ActivityHistory
	 * @return the associated Processinstance pObject
	 */
	public abstract EIProcessInst getProcessInstance(
			DbActivityInstHistory pObject) throws BPMException;

	/**
	 * associates the ActivityHistory object to the Processinstance object.
	 * 
	 * @param pObject
	 *            the ActivityHistory object to use
	 * @param pObjectToBeSet
	 *            the Processinstance object to associate to the ActivityHistory
	 * @return the associated Processinstance pObject
	 */
	public abstract EIActivityInstHistory setProcessInstance(
			EIActivityInstHistory pObject, EIProcessInst pObjectToBeSet);

	// ////////////////////////////////////
	public abstract List loadAll() throws BPMException;

	/**
	 * retreive an array of ActivityHistory given a sql where clausis
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting ActivityHistory table
	 */
	public abstract List loadByWhere(String where) throws BPMException;

	// ////////////////////////////////////
	public abstract int deleteByKey(String activityhistoryId)
			throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the ActivityHistory object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIActivityInstHistory object)
			throws BPMException;

	/**
	 * Delete rows from BPM_ACTIVITYHISTORY table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of ActivityHistory pObjects
	 * 
	 * @param pObjects
	 *            the ActivityHistory pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIActivityInstHistory[] save(
			EIActivityInstHistory[] pObjects) throws BPMException;

	/**
	 * Save into the database the ActivityHistory pObject
	 * 
	 * @param pObject
	 *            the ActivityHistory pObject to be saved
	 */
	public abstract EIActivityInstHistory save(EIActivityInstHistory pObject)
			throws BPMException;
}
