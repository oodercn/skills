/*
 * Created on 2004-3-5
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.ooder.bpm.engine.inter;

import java.io.Serializable;
import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.RightEngine;
import net.ooder.bpm.engine.database.right.DbProcessDefSupervisorManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006-2004
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public abstract class EIProcessDefSupervisorManager implements Serializable{
	private static EIProcessDefSupervisorManager singleton = new DbProcessDefSupervisorManager();

	synchronized public static EIProcessDefSupervisorManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own EIProcessDefSupervisorManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			EIProcessDefSupervisorManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtProcessdefSupervisorBean instance.
	 * 
	 * @return the new RtProcessdefSupervisorBean
	 */
	public abstract EIProcessDefSupervisor createProcessDefSupervisor();

	/**
	 * retreive an array of RtProcessdefSupervisorBean given a sql where
	 * clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RtProcessdefSupervisorBean table
	 */
	public abstract List loadByWhere(String where) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIProcessDefSupervisor pObject)
			throws BPMException;

	/**
	 * Delete rows from RT_PROCESSDEF_SUPERVISOR table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of RtProcessdefSupervisorBean pObjects
	 * 
	 * @param pObjects
	 *            the RtProcessdefSupervisorBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIProcessDefSupervisor[] save(
			EIProcessDefSupervisor[] pObjects) throws BPMException;

	/**
	 * Save into the database the RtProcessdefSupervisorBean pObject
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be saved
	 */
	public abstract EIProcessDefSupervisor save(EIProcessDefSupervisor pObject)
			throws BPMException;

	public abstract List getSupervisorByProcessDefVersionId(
			String processDefVersionId) throws BPMException;

	public abstract void deleteByProcessDefVersion(String processDefVersionId)
			throws BPMException;
}
