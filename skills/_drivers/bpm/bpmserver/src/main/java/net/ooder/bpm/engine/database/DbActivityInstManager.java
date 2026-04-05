/**
 * $RCSfile: DbActivityInstManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.attribute.InterpreterManager;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIActivityInstManager;
import net.ooder.bpm.engine.inter.EIAttributeInst;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.CommonYesNoEnum;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例管理器接口的数据库实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li, chenjie
 * @version 1.0
 */
public class DbActivityInstManager extends EIActivityInstManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstManager.class);

	public static final int ID_ACTIVITYINST_ID = 0;

	public static final int TYPE_ACTIVITYINST_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_ID = "ACTIVITYINST_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ACTIVITYDEF_ID = 2;

	public static final int TYPE_ACTIVITYDEF_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYDEF_ID = "ACTIVITYDEF_ID";

	public static final int ID_PROCESSDEF_ID = 3;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_ACTIVITYINST_STATE = 4;

	public static final int TYPE_ACTIVITYINST_STATE = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_STATE = "ACTIVITYINST_STATE";

	public static final int ID_URGENCYTYPE = 5;

	public static final int TYPE_URGENCYTYPE = Types.VARCHAR;

	public static final String NAME_URGENCYTYPE = "URGENCYTYPE";

	public static final int ID_ARRIVEDTIME = 6;

	public static final int TYPE_ARRIVEDTIME = Types.DECIMAL;

	public static final String NAME_ARRIVEDTIME = "ARRIVEDTIME";

	public static final int ID_LIMITTIME = 7;
	
	public static final int TYPE_LIMITTIME = Types.DECIMAL;

	public static final String NAME_LIMITTIME = "LIMITTIME";

	public static final int ID_ALERTTIME = 8;
	
	public static final int TYPE_ALERTTIME = Types.DECIMAL;

	public static final String NAME_ALERTTIME = "ALERTTIME";


	public static final int ID_STARTTIME = 9;

	public static final int TYPE_STARTTIME = Types.DECIMAL;

	public static final String NAME_STARTTIME = "STARTTIME";

	public static final int ID_RECEIVEMETHOD = 10;

	public static final int TYPE_RECEIVEMETHOD = Types.VARCHAR;

	public static final String NAME_RECEIVEMETHOD = "RECEIVEMETHOD";

	public static final int ID_DEALMETHOD = 11;

	public static final int TYPE_DEALMETHOD = Types.VARCHAR;

	public static final String NAME_DEALMETHOD = "DEALMETHOD";

	public static final int ID_RUNSTATUS = 12;

	public static final int TYPE_RUNSTATUS = Types.VARCHAR;

	public static final String NAME_RUNSTATUS = "RUNSTATUS";

	public static final int ID_CANTAKEBACK = 13;

	public static final int TYPE_CANTAKEBACK = Types.VARCHAR;

	public static final String NAME_CANTAKEBACK = "CANTAKEBACK";

	private static final String TABLE_NAME = "BPM_ACTIVITYINSTANCE";

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYINSTANCE table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID",
			"BPM_ACTIVITYINSTANCE.PROCESSINST_ID",
			"BPM_ACTIVITYINSTANCE.ACTIVITYDEF_ID",
			"BPM_ACTIVITYINSTANCE.PROCESSDEF_ID",
			"BPM_ACTIVITYINSTANCE.ACTIVITYINST_STATE",
			"BPM_ACTIVITYINSTANCE.URGENCYTYPE",
			"BPM_ACTIVITYINSTANCE.ARRIVEDTIME",
			"BPM_ACTIVITYINSTANCE.LIMITTIME", 
			"BPM_ACTIVITYINSTANCE.ALERTTIME", 
			"BPM_ACTIVITYINSTANCE.STARTTIME",
			"BPM_ACTIVITYINSTANCE.RECEIVEMETHOD",
			"BPM_ACTIVITYINSTANCE.DEALMETHOD",
			"BPM_ACTIVITYINSTANCE.RUNSTATUS",
			"BPM_ACTIVITYINSTANCE.CANTAKEBACK" };

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYINSTANCE table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "ACTIVITYINST_ID",
			"PROCESSINST_ID", "ACTIVITYDEF_ID", "PROCESSDEF_ID",
			"ACTIVITYINST_STATE", "URGENCYTYPE", "ARRIVEDTIME", "LIMITTIME","ALERTTIME",
			"STARTTIME", "RECEIVEMETHOD", "DEALMETHOD", "RUNSTATUS",
			"CANTAKEBACK" };

	/**
	 * Field that contains the comma separated fields of the
	 * BPM_ACTIVITYINSTANCE table
	 */
	private static final String ALL_FIELDS = "BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID"
			+ ",BPM_ACTIVITYINSTANCE.PROCESSINST_ID"
			+ ",BPM_ACTIVITYINSTANCE.ACTIVITYDEF_ID"
			+ ",BPM_ACTIVITYINSTANCE.PROCESSDEF_ID"
			+ ",BPM_ACTIVITYINSTANCE.ACTIVITYINST_STATE"
			+ ",BPM_ACTIVITYINSTANCE.URGENCYTYPE"
			+ ",BPM_ACTIVITYINSTANCE.ARRIVEDTIME"
			+ ",BPM_ACTIVITYINSTANCE.LIMITTIME"
			+ ",BPM_ACTIVITYINSTANCE.ALERTTIME"
			+ ",BPM_ACTIVITYINSTANCE.STARTTIME"
			+ ",BPM_ACTIVITYINSTANCE.RECEIVEMETHOD"
			+ ",BPM_ACTIVITYINSTANCE.DEALMETHOD"
			+ ",BPM_ACTIVITYINSTANCE.RUNSTATUS"
			+ ",BPM_ACTIVITYINSTANCE.CANTAKEBACK";

	/** 流程定义cache */
	Cache cache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbActivityInstManager() {
		cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ActivityInstCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new Activityinstance instance.
	 * 
	 * @return the new Activityinstance
	 */
	public EIActivityInst createActivityInstance() {
		return new DbActivityInst();
	}

	/**
	 * get the manager object used to get connections
	 * 
	 * @return the manager used
	 */
	// 2
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

	/**
	 * Load a Activityinstance from the BPM_ACTIVITYINSTANCE using its key
	 * fields.
	 * 
	 * @return a unique Activityinstance
	 */
	// 12
	public EIActivityInst loadByKey(String activityInstId) throws BPMException {
		EIActivityInst dbActivityInst = (DbActivityInst) cache
				.get(activityInstId);
		if (dbActivityInst != null) {
			return dbActivityInst;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			log
					.debug("SELECT "
							+ ALL_FIELDS
							+ " FROM BPM_ACTIVITYINSTANCE WHERE BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_ACTIVITYINSTANCE WHERE BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityInstId);
			EIActivityInst pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				putToCache(activityInstId, (DbActivityInst) pReturn[0]);
				return (DbActivityInst) pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load a Activityinstance table from the BPM_ACTIVITYINSTANCE table using
	 * its PROCESSINST_ID field.
	 * 
	 * @return an array of Activityinstance
	 */
	// LOAD BY IMPORTED KEY
	public List loadByProcessInstId(String value) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			log.debug("SELECT " + ALL_FIELDS
					+ " FROM BPM_ACTIVITYINSTANCE WHERE PROCESSINST_ID=?");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ACTIVITYINSTANCE WHERE PROCESSINST_ID=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			return Arrays.asList(loadByPreparedStatement(ps));
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete from the BPM_ACTIVITYINSTANCE table by PROCESSINST_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	// DELETE BY IMPORTED KEY
	public int deleteByProcessInstId(String value) throws BPMException {
		String where = "PROCESSINST_ID='" + value + "'";
		return deleteByWhere(where);
	}

	/**
	 * get the Processinstance object from the
	 * BPM_ACTIVITYINSTANCE.PROCESSINST_ID field.
	 * 
	 * @param pObject
	 *            the Activityinstance
	 * @return the associated Processinstance pObject
	 */
	// LOAD IMPORTED
	public EIProcessInst getProcessInstance(EIActivityInst pObject)
			throws BPMException {
		EIProcessInst other = EIProcessInstManager.getInstance()
				.createProcessInstance();
		other.setProcessInstId(pObject.getProcessInstId());
		return ((DbProcessInstManager) DbProcessInstManager.getInstance())
				.loadObject(other);
	}

	/**
	 * associates the ActivityInstance object to the Processinstance object.
	 * 
	 * @param pObject
	 *            the Activityinstance object to use
	 * @param pObjectToBeSet
	 *            the Processinstance object to associate to the
	 *            Activityinstance
	 * @return the associated Processinstance pObject
	 */
	// SET IMPORTED
	public EIActivityInst setProcessInstance(EIActivityInst pObject,
			EIProcessInst pObjectToBeSet) {
		pObject.setProcessInstId(pObjectToBeSet.getProcessInstId());
		return pObject;
	}

	// ////////////////////////////////////
	// LOAD ALL
	// ////////////////////////////////////

	/**
	 * Load all the rows from BPM_ACTIVITYINSTANCE.
	 * 
	 * @return an array of ActivityinstanceManager pObject
	 */
	// 38
	public List loadAll() throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			log.debug("SELECT " + ALL_FIELDS + " FROM BPM_ACTIVITYINSTANCE");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ACTIVITYINSTANCE",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			List list = new DbActivityInstList(loadByPreparedStatement(ps,
					new int[] { ID_ACTIVITYINST_ID }));
			return Collections.unmodifiableList(list);

		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	// ////////////////////////////////////
	// PREPARED STATEMENTRESTRICTION LOADER
	// ////////////////////////////////////

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of Activityinstance
	 */
	// 41
	public EIActivityInst[] loadByPreparedStatement(PreparedStatement ps)
			throws BPMException {
		return loadByPreparedStatement(ps, null);
	}

	/**
	 * Load all the elements using a preparedstatement specifying a list of
	 * fields to be retreived.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return an array of Activityinstance
	 */
	public DbActivityInst[] loadByPreparedStatement(PreparedStatement ps,
			int[] fieldList) throws BPMException {
		ResultSet rs = null;
		java.util.ArrayList v = null;
		try {
			rs = ps.executeQuery();
			v = new java.util.ArrayList();
			while (rs.next()) {
				if (fieldList == null)
					v.add(decodeRow(rs));
				else
					v.add(decodeRow(rs, fieldList));
			}
			return (DbActivityInst[]) v.toArray(new DbActivityInst[0]);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	// ////////////////////////////////////
	// RESTRICTION LOADER
	// ////////////////////////////////////
	/**
	 * retreive an array of Activityinstance given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting Activityinstance table
	 */
	// 49
	public List loadByWhere(String where) throws BPMException {
		List result;
		result = new DbActivityInstList(loadByWhere(where,
				new int[] { ID_ACTIVITYINST_ID }));
		return Collections.unmodifiableList(result);
	}

	/**
	 * retreive an array of Activityinstance given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting Activityinstance table
	 */
	// 51
	private DbActivityInst[] loadByWhere(String where, int[] fieldList)
			throws BPMException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_ACTIVITYINSTANCE "
					+ where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_ACTIVITYINSTANCE ");
			buff.append(where);
			sql = buff.toString();
			buff = null;
		}
		Connection c = null;
		Statement pStatement = null;
		ResultSet rs = null;
		java.util.ArrayList v = null;
		try {
			c = getConnection();
			pStatement = c.createStatement();

			if (log.isDebugEnabled())
				log.debug("Executing SQL: " + sql);
			rs = pStatement.executeQuery(sql);
			v = new java.util.ArrayList();
			while (rs.next()) {
				if (fieldList == null)
					v.add(decodeRow(rs));
				else
					v.add(decodeRow(rs, fieldList));
			}

			return (DbActivityInst[]) v.toArray(new DbActivityInst[0]);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			if (v != null) {
				v.clear();
			}
			getManager().close(pStatement, rs);
			freeConnection(c);
		}
	}

	// ////////////////////////////////////
	// DELETE
	// ////////////////////////////////////
	/**
	 * Delete rows according to its keys
	 * 
	 * @return the number of deleted rows
	 */
	// 60
	public int deleteByKey(String activityinstId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			log
					.debug("DELETE from BPM_ACTIVITYINSTANCE WHERE BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_ACTIVITYINSTANCE WHERE BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			removeFromCache(activityinstId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param activityInst
	 *            the Activityinstance object(s) to be deleted
	 * @return the number of deleted objects
	 */
	// 63
	public int delete(EIActivityInst activityInst) throws BPMException {
		DbActivityInst pObject = (DbActivityInst) activityInst;
		if (pObject.isActivityinstIdInitialized() == true)
			return deleteByKey(pObject.getActivityInstId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_ACTIVITYINSTANCE WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isActivitydefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isActivityinstStateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_STATE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isUrgencytypeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("URGENCYTYPE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isArrivedtimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ARRIVEDTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isLimittimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LIMITTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isAlerttimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ALERTTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isStarttimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("STARTTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRecievestateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RECEIVEMETHOD").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isDealstateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEALMETHOD").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRunstateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RUNSTATUS").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isCantakebackInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CANTAKEBACK").append("=?");
				_dirtyAnd++;
			}
			log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivitydefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityDefId());
			if (pObject.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isActivityinstStateInitialized())
				ps.setString(++_dirtyCount, pObject.getState());
			if (pObject.isUrgencytypeInitialized())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isArrivedtimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getArrivedTime().getTime());
			if (pObject.isLimittimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isAlerttimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getAlertTime().getTime());
			if (pObject.isStarttimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isRecievestateInitialized())
				ps.setString(++_dirtyCount, pObject.getReceiveMethod());
			if (pObject.isDealstateInitialized())
				ps.setString(++_dirtyCount, pObject.getDealMethod());
			if (pObject.isRunstateInitialized())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
			if (pObject.isCantakebackInitialized())
				ps.setString(++_dirtyCount, pObject.getCanTakeBack());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from BPM_ACTIVITYINSTANCE table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public int deleteByWhere(String where) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			// remove it from cache
			DbActivityInst[] activityInsts = loadByWhere(where,
					new int[] { ID_ACTIVITYINST_ID });
			for (int i = 0; i < activityInsts.length; i++) {
				removeFromCache(activityInsts[i].getActivityInstId());
			}
			// delete it
			c = getConnection();
			String delByWhereSQL = "DELETE FROM BPM_ACTIVITYINSTANCE " + where;
			log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of Activityinstance pObjects
	 * 
	 * @param pObjects
	 *            the Activityinstance pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIActivityInst[] save(EIActivityInst[] pObjects) throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the Activityinstance pObject.
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to be saved
	 */
	// 67
	void beforeInsert(EIActivityInst pObject) throws SQLException {
	}

	/**
	 * after the save of the Activityinstance pObject
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to be saved
	 */
	// 68
	void afterInsert(EIActivityInst pObject) throws SQLException {
	}

	/**
	 * before the update of the Activityinstance pObject.
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to be updated
	 */
	// 69
	void beforeUpdate(EIActivityInst pObject) throws SQLException {
	}

	/**
	 * after the update of the Activityinstance pObject.
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to be updated
	 */
	// 70
	void afterUpdate(EIActivityInst pObject) throws SQLException {
	}

	/**
	 * Save into the database the Activityinstance pObject
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to be saved
	 */
	// 100
	public EIActivityInst save(EIActivityInst activityInst) throws BPMException {
		DbActivityInst pObject = (DbActivityInst) activityInst;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_ACTIVITYINSTANCE (");
				if (pObject.isActivityinstIdModified()) {
					_sql.append("ACTIVITYINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstIdModified()) {
					_sql.append("PROCESSINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isActivitydefIdModified()) {
					_sql.append("ACTIVITYDEF_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isActivityinstStateModified()) {
					_sql.append("ACTIVITYINST_STATE").append(",");
					_dirtyCount++;
				}
				if (pObject.isUrgencytypeModified()) {
					_sql.append("URGENCYTYPE").append(",");
					_dirtyCount++;
				}
				if (pObject.isArrivedtimeModified()) {
					_sql.append("ARRIVEDTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isLimittimeModified()) {
					_sql.append("LIMITTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isAlerttimeModified()) {
					_sql.append("ALERTTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isStarttimeModified()) {
					_sql.append("STARTTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isRecievestateModified()) {
					_sql.append("RECEIVEMETHOD").append(",");
					_dirtyCount++;
				}
				if (pObject.isDealstateModified()) {
					_sql.append("DEALMETHOD").append(",");
					_dirtyCount++;
				}
				if (pObject.isRunstateModified()) {
					_sql.append("RUNSTATUS").append(",");
					_dirtyCount++;
				}
				if (pObject.isCantakebackModified()) {
					_sql.append("CANTAKEBACK").append(",");
					_dirtyCount++;
				}
				_sql.setLength(_sql.length() - 1);
				_sql.append(") values (");
				for (int i = 0; i < _dirtyCount; i++)
					_sql.append("?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(")");

				log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (pObject.isActivityinstIdModified())
					ps.setString(++_dirtyCount, pObject.getActivityInstId());
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isActivitydefIdModified())
					ps.setString(++_dirtyCount, pObject.getActivityDefId());
				if (pObject.isProcessdefIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessDefId());
				if (pObject.isActivityinstStateModified())
					ps.setString(++_dirtyCount, pObject.getState());
				if (pObject.isUrgencytypeModified())
					ps.setString(++_dirtyCount, pObject.getUrgency());
				if (pObject.isArrivedtimeModified())
					ps.setLong(++_dirtyCount, pObject.getArrivedTime()
							.getTime());
				if (pObject.isLimittimeModified())
					ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
				if (pObject.isAlerttimeModified())
					ps.setLong(++_dirtyCount, pObject.getAlertTime().getTime());
				if (pObject.isStarttimeModified())
					ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
				if (pObject.isRecievestateModified())
					ps.setString(++_dirtyCount, pObject.getReceiveMethod());
				if (pObject.isDealstateModified())
					ps.setString(++_dirtyCount, pObject.getDealMethod());
				if (pObject.isRunstateModified())
					ps.setString(++_dirtyCount, pObject.getRunStatus());
				if (pObject.isCantakebackModified())
					ps.setString(++_dirtyCount, pObject.getCanTakeBack());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				pObject.setIsNew(false);
				pObject.resetIsModified();
				afterInsert(pObject);
			} else { // UPDATE
				// preupdate+

				// preupdate-
				beforeUpdate(pObject);
				_sql = new StringBuffer("UPDATE BPM_ACTIVITYINSTANCE SET ");
				if (pObject.isActivityinstIdModified())
					_sql.append("ACTIVITYINST_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isActivitydefIdModified())
					_sql.append("ACTIVITYDEF_ID").append("=?,");
				if (pObject.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (pObject.isActivityinstStateModified())
					_sql.append("ACTIVITYINST_STATE").append("=?,");
				if (pObject.isUrgencytypeModified())
					_sql.append("URGENCYTYPE").append("=?,");
				if (pObject.isArrivedtimeModified())
					_sql.append("ARRIVEDTIME").append("=?,");
				if (pObject.isLimittimeModified())
					_sql.append("LIMITTIME").append("=?,");
				if (pObject.isAlerttimeModified())
					_sql.append("ALERTTIME").append("=?,");
				if (pObject.isStarttimeModified())
					_sql.append("STARTTIME").append("=?,");
				if (pObject.isRecievestateModified())
					_sql.append("RECEIVEMETHOD").append("=?,");
				if (pObject.isDealstateModified())
					_sql.append("DEALMETHOD").append("=?,");
				if (pObject.isRunstateModified())
					_sql.append("RUNSTATUS").append("=?,");
				if (pObject.isCantakebackModified())
					_sql.append("CANTAKEBACK").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_ACTIVITYINSTANCE.ACTIVITYINST_ID=?");
				log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isActivityinstIdModified()) {
					if (pObject.getActivityInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, pObject
										.getActivityInstId());
				}
				if (pObject.isProcessinstIdModified()) {
					if (pObject.getProcessInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
				}
				if (pObject.isActivitydefIdModified()) {
					if (pObject.getActivityDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getActivityDefId());
				}
				if (pObject.isProcessdefIdModified()) {
					if (pObject.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessDefId());
				}
				if (pObject.isActivityinstStateModified()) {
					if (pObject.getState() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getState());
				}
				if (pObject.isUrgencytypeModified()) {
					if (pObject.getUrgency() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getUrgency());
				}
				if (pObject.isArrivedtimeModified()) {
					if (pObject.getArrivedTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, pObject.getArrivedTime()
								.getTime());
				}
				if (pObject.isLimittimeModified()) {
					ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
				}
				if (pObject.isAlerttimeModified()) {
					ps.setLong(++_dirtyCount, pObject.getAlertTime().getTime());
				}
				if (pObject.isStarttimeModified()) {
					if (pObject.getStartTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, pObject.getStartTime()
								.getTime());
				}
				if (pObject.isRecievestateModified()) {
					if (pObject.getReceiveMethod() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getReceiveMethod());
				}
				if (pObject.isDealstateModified()) {
					if (pObject.getDealMethod() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getDealMethod());
				}
				if (pObject.isRunstateModified()) {
					if (pObject.getRunStatus() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRunStatus());
				}
				if (pObject.isCantakebackModified()) {
					if (pObject.getCanTakeBack() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getCanTakeBack());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
				ps.executeUpdate();
				pObject.resetIsModified();
				// postupdate+

				// postupdate-
				afterUpdate(pObject);
			}
			// 保存扩展属性
			// TODO
			saveAttribute(pObject);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYINSTANCE on a
	 * Activityinstance pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting Activityinstance pObject
	 */
	// 72
	public DbActivityInst decodeRow(ResultSet rs) throws SQLException {
		DbActivityInst pObject = (DbActivityInst) createActivityInstance();
		if (rs.getObject(1) != null)
			pObject.setActivityInstId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setActivityDefId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setProcessDefId(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setState(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setUrgency(rs.getString(6));
		if (rs.getObject(7) != null)
			pObject.setArrivedTime(new Date(rs.getLong(7)));
		if (rs.getObject(8) != null)
			pObject.setLimitTime(new Date(rs.getLong(8)));
		if (rs.getObject(9) != null)
			pObject.setAlertTime(new Date(rs.getLong(9)));
		if (rs.getObject(10) != null)
			pObject.setStartTime(new Date(rs.getLong(10)));
		if (rs.getObject(11) != null)
			pObject.setReceiveMethod(rs.getString(11));
		if (rs.getObject(12) != null)
			pObject.setDealMethod(rs.getString(12));
		if (rs.getObject(13) != null)
			pObject.setRunStatus(rs.getString(13));
		if (rs.getObject(14) != null)
			pObject.setCanTakeBack(rs.getString(14));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYINSTANCE on a
	 * Activityinstance pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting Activityinstance pObject
	 */
	// 73
	public DbActivityInst decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityInst pObject = (DbActivityInst) createActivityInstance();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_ACTIVITYDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityDefId(rs.getString(pos));
				}
				break;
			case ID_PROCESSDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefId(rs.getString(pos));
				}
				break;
			case ID_ACTIVITYINST_STATE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setState(rs.getString(pos));
				}
				break;
			case ID_URGENCYTYPE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setUrgency(rs.getString(pos));
				}
				break;
			case ID_ARRIVEDTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setArrivedTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_LIMITTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLimitTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_ALERTTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setAlertTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_STARTTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setStartTime(new Date(rs.getLong(pos)));
				}
				break;
			
			case ID_RECEIVEMETHOD:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setReceiveMethod(rs.getString(pos));
				}
				break;
				
			case ID_DEALMETHOD:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDealMethod(rs.getString(pos));
				}
				break;
			case ID_RUNSTATUS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRunStatus(rs.getString(pos));
				}
				break;
			case ID_CANTAKEBACK:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCanTakeBack(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique Activityinstance pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the Activityinstance pObject to look for
	 * @return the pObject matching the template
	 */
	// 85
	public EIActivityInst loadObject(EIActivityInst pObject)
			throws BPMException {
		EIActivityInst[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new BPMException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of Activityinstance from a template one
	 * 
	 * @param pObject
	 *            the Activityinstance template to look for
	 * @return all the Activityinstance matching the template
	 */
	// 88
	public EIActivityInst[] loadObjects(EIActivityInst activityInst)
			throws BPMException {
		DbActivityInst pObject = (DbActivityInst) activityInst;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from BPM_ACTIVITYINSTANCE WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_ID= ?");
			}
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isActivitydefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYDEF_ID= ?");
			}
			if (pObject.isProcessdefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (pObject.isActivityinstStateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_STATE= ?");
			}
			if (pObject.isUrgencytypeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("URGENCYTYPE= ?");
			}
			if (pObject.isArrivedtimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ARRIVEDTIME= ?");
			}
		
			if (pObject.isLimittimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LIMITTIME= ?");
			}
			if (pObject.isAlerttimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ALERTTIME= ?");
			}
			if (pObject.isStarttimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("STARTTIME= ?");
			}
			if (pObject.isRecievestateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RECEIVEMETHOD= ?");
			}
			if (pObject.isDealstateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEALMETHOD= ?");
			}
			if (pObject.isRunstateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RUNSTATUS= ?");
			}
			if (pObject.isCantakebackModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CANTAKEBACK= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			log.debug(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (pObject.isActivityinstIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivitydefIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityDefId());
			if (pObject.isProcessdefIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isActivityinstStateModified())
				ps.setString(++_dirtyCount, pObject.getState());
			if (pObject.isUrgencytypeModified())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isArrivedtimeModified())
				ps.setLong(++_dirtyCount, pObject.getArrivedTime().getTime());
			if (pObject.isLimittimeModified())
				ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isAlerttimeModified())
				ps.setLong(++_dirtyCount, pObject.getAlertTime().getTime());
			if (pObject.isStarttimeModified())
				ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isRecievestateModified())
				ps.setString(++_dirtyCount, pObject.getReceiveMethod());
			if (pObject.isDealstateModified())
				ps.setString(++_dirtyCount, pObject.getDealMethod());
			if (pObject.isRunstateModified())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
			if (pObject.isCantakebackModified())
				ps.setString(++_dirtyCount, pObject.getCanTakeBack());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * 复制一份活动实例，除了活动实例的UUID不同外，其他均相同。
	 * 
	 * @param original
	 *            需要复制的活动实例
	 * @return
	 * @throws BPMException
	 */
	public EIActivityInst copyActivityInst(EIActivityInst original)
			throws BPMException {
		DbActivityInst target = new DbActivityInst();
		target.copy((DbActivityInst) original);
		// 复制后重新设置其UUID
		target.setActivityInstId(UUID.randomUUID().toString());
		save(target);

		// 复制扩展属性，可能带层次关系
		List attributes = new ArrayList(((DbActivityInst) original)
				.getTopAttribute());

		if (attributes != null) {
			for (int i = 0, n = attributes.size(); i < n; i++) {
				EIAttributeInst attr = (EIAttributeInst) attributes.get(i);
				copyChildAttribute(null, attr.getType(), target, attr);
			}
		}

		// 复制后保存
		save(target);
		return target;
	}

	/**
	 * @param string
	 * @param attrId
	 * @param child
	 */
	private void copyChildAttribute(EIAttributeInst parent, String name,
			EIActivityInst target, EIAttributeInst attr) {
		EIAttributeInst newAttr = new DbAttributeInst();
		String attrId = UUID.randomUUID().toString();
		newAttr.setId(attrId);
		newAttr.setInterpretClass(attr.getInterpretClass());
		newAttr.setInterpretedValue(attr.getInterpretedValue());
		newAttr.setName(attr.getName());
		if (parent == null) {
			newAttr.setParentId(null);
		} else {
			newAttr.setParentId(parent.getId());
		}
		newAttr.setType(attr.getType());
		newAttr.setValue(attr.getValue());
		// TODO
		newAttr.setParent(attr.getParent());
		try {
			target.setAttribute(name, newAttr);
		} catch (BPMException e) {
			log.error("", e);
		}
		List childrenAttr = attr.getChildren();
		if (childrenAttr != null) {
			for (Iterator it = childrenAttr.iterator(); it.hasNext();) {
				EIAttributeInst child = (EIAttributeInst) it.next();
				copyChildAttribute(newAttr, newAttr.getName(), target, child);
			}
		}
	}

	/**
	 * 根据活动实例所对应的扩展属性的定义，实例化活动实例的扩展属性。需要先删除活动实例 的扩展属性，然后再根据其定义实例化之。
	 * 
	 * @param inst
	 *            活动实例对象
	 * @throws BPMException
	 */
	public void instantiateExtAttribute(EIActivityInst inst)
			throws BPMException {
		EIActivityDef activityDef = EIActivityDefManager.getInstance()
				.loadByKey(inst.getActivityDefId());
		List attrDefs = activityDef.getAllAttribute();
		for (Iterator iter = attrDefs.iterator(); iter.hasNext();) {
			DbAttributeDef attrDef = (DbAttributeDef) iter.next();
			String canInstantiate = attrDef.getCanInstantiate();
			// 可以实例化的。
			if (canInstantiate != null
					&& canInstantiate.equals(CommonYesNoEnum.YES.getType())) {
				DbAttributeInst attrInst = new DbAttributeInst();
				attrInst.setId(UUID.randomUUID().toString());
				attrInst.setCanInstantiate(attrDef.getCanInstantiate());
				attrInst.setInterpretClass(attrDef.getInterpretClass());
				attrInst.setIsExtension(attrDef.getIsExtension());
				attrInst.setName(attrDef.getName());
				attrInst.setParentId(attrDef.getParentId());
				attrInst.setType(attrDef.getType());
				attrInst.setValue(attrDef.getValue());
				attrInst.setInterpretedValue(attrDef.getInterpretedValue());

				addAttribute((DbActivityInst) inst, attrInst);
			}
		}
	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " ACTIVITYINST_ID, " + " PROPNAME, "
			+ " PROPVALUE, " + " PROPCLASS, " + " PROPTYPE, "
			+ " PARENTPROP_ID, " + " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_ACTIVITYINST_PROPERTY " + "WHERE ACTIVITYINST_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_ACTIVITY_EXTATTRIBUTRE = "DELETE FROM BPM_ACTIVITYINST_PROPERTY "
			+ "WHERE ACTIVITYINST_ID = ? ";

	private static final String DELETE_ONE_EXTATTRIBUTRE = "DELETE FROM BPM_ACTIVITYINST_PROPERTY "
			+ "WHERE PROPERTY_ID = ? ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_ACTIVITYINST_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " ACTIVITYINST_ID, "
			+ " PROPNAME, "
			+ " PROPVALUE, "
			+ " PROPCLASS, "
			+ " PROPTYPE, "
			+ " PARENTPROP_ID, "
			+ " ISEXTENSION, "
			+ " CANINSTANTIATE "
			+ ") VALUES ( "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, " + "?, " + "? " + " ) ";

	private static final String UPDATE_EXTATTRIBUTRE = "UPDATE BPM_ACTIVITYINST_PROPERTY SET "
			+ " PROPNAME=?, "
			+ " PROPVALUE=?, "
			+ " PROPCLASS=?, "
			+ " PROPTYPE=?, "
			+ " ISEXTENSION=?, "
			+ " CANINSTANTIATE=? "
			+ " where PROPERTY_ID=?";

	void loadAttribute(DbActivityInst act) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, act.getActivityInstId());
			ResultSet rs = ps.executeQuery();
			String lastUUID = "";
			DbAttributeInst extAtt = null;
			while (rs.next()) {
				String currUUID = rs.getString("PROPERTY_ID");
				// some attribute value save on two or more record in the db!
				if (currUUID.equals(lastUUID)) {
					// same uuid minds same attribute, combine the values
					String newValue = rs.getString("PROPVALUE");
					newValue = (newValue == null) ? "" : newValue;
					extAtt.value = extAtt.value + newValue;
				} else {
					lastUUID = currUUID;
					if (extAtt != null) {
						addAttribute(act, extAtt);
					}
					extAtt = (DbAttributeInst) act.attributeIdMap.get(currUUID);
					if (extAtt == null) {
						extAtt = new DbAttributeInst();
					}
					extAtt.setId(currUUID);
					extAtt.setName(rs.getString("PROPNAME"));
					extAtt.setValue(rs.getString("PROPVALUE"));
					extAtt.setInterpretClass(rs.getString("PROPCLASS"));
					extAtt.setType(rs.getString("PROPTYPE"));
					extAtt.setParentId(rs.getString("PARENTPROP_ID"));
					extAtt.setCanInstantiate(rs.getString("CANINSTANTIATE"));
				}
			}
			if (extAtt != null) {
				addAttribute(act, extAtt);
			}

		} catch (SQLException e) {
			throw new BPMException("", e);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void addAttribute(DbActivityInst act, DbAttributeInst extAtt) {
		if (act.attributeIdMap == null) {
			act.attributeIdMap = new HashMap();
		}
		if (act.attributeTopMap == null) {
			act.attributeTopMap = new HashMap();
		}
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeInst parent = (DbAttributeInst) act.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeInst();
				act.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeInst att = (DbAttributeInst) act.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeInst();
				att.setName(extAtt.getType());
				att.setType(extAtt.getType());
				act.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);
			// act.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		act.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	void addAttributeToDb(DbActivityInst activityInst, DbAttributeInst attr)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			saveAttribute(ps, activityInst, attr);
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void updateAttributeToDb(DbActivityInst activityInst, DbAttributeInst attr)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();

			getManager().close(ps);
			ps = c.prepareStatement(UPDATE_EXTATTRIBUTRE);
			updateAttribute(ps, activityInst, attr);
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void removeAttributeFromDb(DbAttributeInst attr) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(DELETE_ONE_EXTATTRIBUTRE);
			ps.setString(1, attr.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * @param activityDef
	 */
	void saveAttribute(DbActivityInst activityInst) throws BPMException {
		if (activityInst.isAttributeModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(DELETE_ACTIVITY_EXTATTRIBUTRE);
			log.debug(DELETE_ACTIVITY_EXTATTRIBUTRE);

			ps.setString(1, activityInst.getActivityInstId());
			int k = ps.executeUpdate();
			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			log.debug(INSERT_EXTATTRIBUTRE);
			List list = activityInst.getAllAttribute();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbAttributeInst att = (DbAttributeInst) it.next();
				saveAttribute(ps, activityInst, att);
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void saveAttribute(PreparedStatement ps,
			DbActivityInst activityInst, DbAttributeDef attr)
			throws SQLException {
		DbAttributeInst attrInst = new DbAttributeInst();
		attrInst.setCanInstantiate(attr.getCanInstantiate());
		attrInst.setInterpretClass(attr.getInterpretClass());
		attrInst.setIsExtension(attr.getIsExtension());
		attrInst.setName(attr.getName());
		attrInst.setType(attr.getType());
		attrInst.setId(attr.getId());
		attrInst.setParentId(attr.getParentId());
		attrInst.setValue(attr.getValue());

		saveAttribute(ps, activityInst, attrInst);
	}

	private void saveAttribute(PreparedStatement ps,
			DbActivityInst activityInst, DbAttributeInst attInst)
			throws SQLException {
		AttributeInterpreter interpreter = InterpreterManager.getInstance()
				.getInterpreter(attInst.getInterpretClass());

		String value = interpreter.instantiate(attInst.getInterpretedValue());

		if (value == null) {
			value = "";
		}
		attInst.setValue(value);
		int block = (value.length() / 500);
		for (int i = 0; i <= block; i++) {
			int begin = i * 500;
			int end = (i == block) ? value.length() : (i + 1) * 500;
			String v = value.substring(begin, end);

			ps.setString(1, attInst.getId());
		//	ps.setString(1, UUID.randomUUID().toString());
			ps.setString(2, activityInst.getActivityInstId());
			ps.setString(3, attInst.getName());
			ps.setString(4, v);
			ps.setString(5, attInst.getInterpretClass());
			ps.setString(6, attInst.getType());
			ps.setString(7, attInst.getParentId());
			ps.setInt(8, i);
			ps.setString(9, attInst.getCanInstantiate());
			ps.executeUpdate();
		}
	}

	private void updateAttribute(PreparedStatement ps, DbActivityInst activity,
			DbAttributeInst attInst) throws SQLException {
		AttributeInterpreter interpreter = InterpreterManager.getInstance()
				.getInterpreter(attInst.getInterpretClass());

		String value = interpreter.instantiate(attInst.getInterpretedValue());
		if (value == null) {
			value = "";
		}
		// 将值重设回去。
		attInst.setValue(value);
		int block = (value.length() / 500);
		for (int i = 0; i <= block; i++) {
			int begin = i * 500;
			int end = (i == block) ? value.length() : (i + 1) * 500;
			String v = value.substring(begin, end);

			ps.setString(1, attInst.getName());
			ps.setString(2, v);
			ps.setString(3, attInst.getInterpretClass());
			ps.setString(4, attInst.getType());
			ps.setInt(5, i);
			ps.setString(6, attInst.getCanInstantiate());
			ps.setString(7, attInst.getId());
			ps.executeUpdate();
		}
	}

	/**
	 * put the object to the cache, this method synchronize the uuid, so you
	 * can't push the Objects with same uuid into cache at sametime
	 * 
	 * @param uuid
	 * @param dbProcessDef
	 */
	private void putToCache(String uuid, DbActivityInst dbActivityInst) {
		synchronized (uuid.intern()) {
			if (!cache.containsKey(uuid)) {

				cache.put(uuid, dbActivityInst);
			}
		}
	}

	/**
	 * remove the object from the cache
	 * 
	 * @param uuid
	 */
	private void removeFromCache(String uuid) {
		synchronized (uuid.intern()) {
			if (cache.containsKey(uuid)) {
				cache.remove(uuid);
			}
		}
	}

	/**
	 * remove the object from the cache
	 * 
	 * @param uuid
	 */
	private void removeFromCache(List uuids) {
		for (int i = 0; i < uuids.size(); i++) {
			Object obj = uuids.get(i);
			if (obj instanceof String) {
				String uuid = (String) obj;
				removeFromCache(uuid);
			}
		}
	}

	/**
	 * 批量将可能读取的对象先预读到Cache中，提高效率
	 * 
	 * @param ids
	 *            需要预读的UUID列表
	 */
	void prepareCache(List ids) {
		StringBuffer sqlWhere = new StringBuffer(" WHERE ACTIVITYINST_ID IN (");
		int andCount = 0;
		// check out all UUIDs not in the cache now
		for (int i = 0; i < ids.size(); i++) {
			String uuid = (String) ids.get(i);
			if (cache.containsKey(uuid) == false) {
				if (andCount > 0) {
					sqlWhere.append(",");
				}
				sqlWhere.append(" '" + uuid + "'");
				andCount++;
			}
		}
		// batch load
		if (andCount > 0) {
			sqlWhere.append(") ");
			EIActivityInst[] list = null;
			try {
				list = loadByWhere(sqlWhere.toString(), null);
			} catch (BPMException e) {
				log.error("prefetch the activity instance fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < list.length; i++) {
				DbActivityInst pd = (DbActivityInst) list[i];
				putToCache(pd.getActivityInstId(), pd);
			}
		}
	}
}


