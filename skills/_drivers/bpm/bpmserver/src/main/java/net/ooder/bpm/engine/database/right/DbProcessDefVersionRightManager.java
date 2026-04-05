/**
 * $RCSfile: DbProcessDefVersionRightManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.right;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisorManager;

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
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lxl
 * @version 1.0
 */
public class DbProcessDefVersionRightManager implements Serializable{

	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefSupervisorManager.class);

	private static DbProcessDefVersionRightManager singleton = new DbProcessDefVersionRightManager();

	/**
	 * Get the RtProcessdefRightGrpManager singleton
	 * 
	 * @return RtProcessdefRightGrpManager
	 */
	synchronized public static DbProcessDefVersionRightManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtProcessdefRightGrpManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbProcessDefVersionRightManager instance) {
		singleton = instance;
	}

	/** 流程定义cache */
	Cache cache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbProcessDefVersionRightManager() {
		cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ProcessDefVersionRightCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new RtProcessdefRightGrpBean instance.
	 * 
	 * @return the new RtProcessdefRightGrpBean
	 */
	public DbProcessDefVersionRight createProcessDefVersionRight() {
		return new DbProcessDefVersionRight();
	}

	/**
	 * get the manager object used to get connections
	 * 
	 * @return the manager used
	 */
	DbManager getManager() {
		return DbManager.getInstance();
	}

	/**
	 * free connection
	 * 
	 * @param c
	 *            the connection to release
	 */
	void freeConnection(Connection c) {
		getManager().releaseConnection(c); // back to pool
	}

	/**
	 * get connection
	 */
	Connection getConnection() throws SQLException {
		return getManager().getConnection();
	}

	public DbProcessDefVersionRight loadByKey(String processDefVersionId)
			throws BPMException {
		DbProcessDefVersionRight rightDef = (DbProcessDefVersionRight) cache
				.get(processDefVersionId);
		if (rightDef != null) {
			return rightDef;
		}
		DbProcessDefSupervisorManager supervisorManager = (DbProcessDefSupervisorManager) EIProcessDefSupervisorManager
				.getInstance();
		DbProcessDefRightGrpManager grpManager = DbProcessDefRightGrpManager
				.getInstance();

		rightDef = createProcessDefVersionRight();
		// load supervisors
		rightDef.setSupervisors(supervisorManager
				.getSupervisorByProcessDefVersionId(processDefVersionId));
		// load right groups
		rightDef.setRightGroups(grpManager
				.getRightGroupByProcessDefVersionId(processDefVersionId));
		putToCache(processDefVersionId, rightDef);
		return rightDef;

	}

	public void save(DbProcessDefVersionRight rightDef) throws BPMException {
		DbProcessDefSupervisorManager supervisorManager = (DbProcessDefSupervisorManager) EIProcessDefSupervisorManager
				.getInstance();
		DbProcessDefRightGrpManager grpManager = DbProcessDefRightGrpManager
				.getInstance();
		// delete all supervisors
		supervisorManager.deleteByProcessDefVersion(rightDef
				.getProcessDefVersionId());
		// delete all right groups
		grpManager.deleteByProcessDefVersion(rightDef.getProcessDefVersionId());

		try {
			// now insert new supervisors
			supervisorManager.save((DbProcessDefSupervisor[]) rightDef
					.getSupervisors().toArray(new DbProcessDefSupervisor[0]));
			// and insert new right groups
			grpManager.save((DbProcessDefRightGrp[]) rightDef.getRightGroups()
					.toArray(new DbProcessDefRightGrp[0]));
		} catch (SQLException e) {
			throw new BPMException("save right info in process definiton "
					+ rightDef.getProcessDefVersionId(), e);
		}
	}

	/**
	 * @param processDefVersionId
	 * @param rightDef
	 */
	private void putToCache(String processDefVersionId,
			DbProcessDefVersionRight rightDef) {
		synchronized (processDefVersionId.intern()) {
			if (cache.get(processDefVersionId) == null) {
				cache.put(processDefVersionId, rightDef);
			}
		}
	}

}


