/**
 * $RCSfile: EIProcessInstManager.java,v $
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
import net.ooder.bpm.engine.database.DbProcessInstManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例管理器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public abstract class EIProcessInstManager {

	private static EIProcessInstManager singleton = new DbProcessInstManager();

	/**
	 * Get the EIProcessInstanceManager singleton
	 * 
	 * @return EIProcessInstanceManager
	 */
	synchronized public static EIProcessInstManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own EIProcessInstanceManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(EIProcessInstManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new ProcessInstance instance.
	 * 
	 * @return the new ProcessInstance
	 */
	public abstract EIProcessInst createProcessInstance();

	/**
	 * Load a ProcessInstance from the BPM_PROCESSINSTANCE using its key fields.
	 * 
	 * @return a unique ProcessInstance
	 */
	public abstract EIProcessInst loadByKey(String processinstId)
			throws BPMException;

	/**
	 * Load all process instances.
	 * 
	 * @return
	 * @throws BPMException
	 */
	public abstract List loadAll() throws BPMException;

	public abstract List loadByWhere(String where) throws BPMException;

	/**
	 * retreive an array of ProcessInstance given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting ProcessInstance table
	 */

	public abstract int deleteByKey(String processinstId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the ProcessInstance object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIProcessInst pObject) throws BPMException;

	/**
	 * Delete rows from BPM_PROCESSINSTANCE table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of ProcessInstance pObjects
	 * 
	 * @param pObjects
	 *            the ProcessInstance pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIProcessInst[] save(EIProcessInst[] pObjects)
			throws BPMException;

	/**
	 * Save into the database the ProcessInstance pObject
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be saved
	 */
	public abstract EIProcessInst save(EIProcessInst pObject)
			throws BPMException;
	// /**
	// * load a unique ProcessInstance pObject from an template one giving a c
	// * @param pObject the ProcessInstance pObject to look for
	// * @return the pObject matching the template
	// */
	// public abstract EIProcessInst loadObject(EIProcessInst pObject) throws
	// BPMException;
	// /**
	// * load an array of ProcessInstance from a template one
	// * @param pObject the ProcessInstance template to look for
	// * @return all the ProcessInstance matching the template
	// */
	// public abstract EIProcessInst[] loadObjects(EIProcessInst pObject) throws
	// BPMException;
}
