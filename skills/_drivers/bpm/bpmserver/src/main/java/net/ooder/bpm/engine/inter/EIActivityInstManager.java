/**
 * $RCSfile: EIActivityInstManager.java,v $
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
import net.ooder.bpm.engine.database.DbActivityInstManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
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
public abstract class EIActivityInstManager {

	private static EIActivityInstManager singleton = new DbActivityInstManager();

	/**
	 * Get the BPMActivityinstanceManager singleton
	 * 
	 * @return BPMActivityinstanceManager
	 */
	synchronized public static EIActivityInstManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own BPMActivityinstanceManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(EIActivityInstManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new BPMActivityinstance instance.
	 * 
	 * @return the new BPMActivityinstance
	 */
	public abstract EIActivityInst createActivityInstance();

	/**
	 * Load a BPMActivityinstance from the BPM_ACTIVITYINSTANCE using its key
	 * fields.
	 * 
	 * @return a unique BPMActivityinstance
	 */
	public abstract EIActivityInst loadByKey(String activityinstId)
			throws BPMException;

	/**
	 * Load a BPMActivityinstance table from the BPM_ACTIVITYINSTANCE table
	 * using its PROCESSINST_ID field.
	 * 
	 * @return an array of BPMActivityinstance
	 */
	public abstract List loadByProcessInstId(String value) throws BPMException;

	/**
	 * Delete from the BPM_ACTIVITYINSTANCE table by PROCESSINST_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public abstract int deleteByProcessInstId(String value) throws BPMException;

	/**
	 * get the BPMProcessinstance object from the
	 * BPM_ACTIVITYINSTANCE.PROCESSINST_ID field.
	 * 
	 * @param pObject
	 *            the BPMActivityinstance
	 * @return the associated BPMProcessinstance pObject
	 */
	public abstract EIProcessInst getProcessInstance(EIActivityInst pObject)
			throws BPMException;

	/**
	 * associates the BPMActivityinstance object to the BPMProcessinstance
	 * object.
	 * 
	 * @param pObject
	 *            the BPMActivityinstance object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessinstance object to associate to the
	 *            BPMActivityinstance
	 * @return the associated BPMProcessinstance pObject
	 */
	public abstract EIActivityInst setProcessInstance(EIActivityInst pObject,
			EIProcessInst pObjectToBeSet);

	public abstract List loadAll() throws BPMException;

	public abstract List loadByWhere(String where) throws BPMException;

	public abstract int deleteByKey(String activityinstId) throws BPMException;

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMActivityinstance object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public abstract int delete(EIActivityInst pObject) throws BPMException;

	/**
	 * Delete rows from BPM_ACTIVITYINSTANCE table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public abstract int deleteByWhere(String where) throws BPMException;

	/**
	 * Save into the database an array of BPMActivityinstance pObjects
	 * 
	 * @param pObjects
	 *            the BPMActivityinstance pObject table to be saved
	 * @return the Objects to be saved
	 */
	public abstract EIActivityInst[] save(EIActivityInst[] pObjects)
			throws BPMException;

	/**
	 * Save into the database the BPMActivityinstance pObject
	 * 
	 * @param pObject
	 *            the BPMActivityinstance pObject to be saved
	 */
	public abstract EIActivityInst save(EIActivityInst pObject)
			throws BPMException;

	/**
	 * 复制一份活动实例，除了活动实例的UUID不同外，其他均相同。
	 * 
	 * @param original
	 *            需要复制的活动实例
	 * @return
	 * @throws BPMException
	 */
	public abstract EIActivityInst copyActivityInst(EIActivityInst original)
			throws BPMException;

	/**
	 * 根据活动实例所对应的扩展属性的定义，实例化活动实例的扩展属性。需要先删除活动实例 的扩展属性，然后再根据其定义实例化之。
	 * 
	 * @param inst
	 *            活动实例对象
	 * @throws BPMException
	 */
	public abstract void instantiateExtAttribute(EIActivityInst inst)
			throws BPMException;

	// /**
	// * load a unique BPMActivityinstance pObject from an template one giving a
	// c
	// * @param pObject the BPMActivityinstance pObject to look for
	// * @return the pObject matching the template
	// */
	// public abstract EIActivityInst loadObject(EIActivityInst pObject) throws
	// BPMException;
	// /**
	// * load an array of BPMActivityinstance from a template one
	// * @param pObject the BPMActivityinstance template to look for
	// * @return all the BPMActivityinstance matching the template
	// */
	// public abstract EIActivityInst[] loadObjects(EIActivityInst pObject)
	// throws BPMException;
}
