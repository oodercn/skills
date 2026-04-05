/**
 * $RCSfile: DbProcessInstManager.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/09/30 14:58:41 $
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.attribute.InterpreterManager;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例管理器接口的数据库实现
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
public class DbProcessInstManager extends EIProcessInstManager {

	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessInstManager.class);

	public static final int ID_PROCESSINST_ID = 0;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_PROCESSDEF_ID = 1;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_PROCESSDEF_VERSION_ID = 2;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_PROCESSINST_NAME = 3;

	public static final int TYPE_PROCESSINST_NAME = Types.VARCHAR;

	public static final String NAME_PROCESSINST_NAME = "PROCESSINST_NAME";

	public static final int ID_PROCESSINST_URGENCY = 4;

	public static final int TYPE_PROCESSINST_URGENCY = Types.VARCHAR;

	public static final String NAME_PROCESSINST_URGENCY = "PROCESSINST_URGENCY";

	public static final int ID_PROCESSINST_STATE = 5;

	public static final int TYPE_PROCESSINST_STATE = Types.VARCHAR;

	public static final String NAME_PROCESSINST_STATE = "PROCESSINST_STATE";

	public static final int ID_COPYNUMBER = 6;

	public static final int TYPE_COPYNUMBER = Types.DECIMAL;

	public static final String NAME_COPYNUMBER = "COPYNUMBER";

	public static final int ID_STARTTIME = 7;

	public static final int TYPE_STARTTIME = Types.DECIMAL;

	public static final String NAME_STARTTIME = "STARTTIME";

	public static final int ID_LIMITTIME = 8;

	public static final int TYPE_LIMITTIME = Types.DECIMAL;

	public static final String NAME_LIMITTIME = "LIMITTIME";

	public static final int ID_ENDTIME = 9;

	public static final int TYPE_ENDTIME = Types.DECIMAL;

	public static final String NAME_ENDTIME = "ENDTIME";

	public static final int ID_RUNSTATUS = 10;

	public static final int TYPE_RUNSTATUS = Types.VARCHAR;

	public static final String NAME_RUNSTATUS = "RUNSTATUS";

	private static final String TABLE_NAME = "BPM_PROCESSINSTANCE";

	/**
	 * create an array of string containing all the fields of the
	 * BPM_PROCESSINSTANCE table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_PROCESSINSTANCE.PROCESSINST_ID",
			"BPM_PROCESSINSTANCE.PROCESSDEF_ID",
			"BPM_PROCESSINSTANCE.PROCESSDEF_VERSION_ID",
			"BPM_PROCESSINSTANCE.PROCESSINST_NAME",
			"BPM_PROCESSINSTANCE.PROCESSINST_URGENCY",
			"BPM_PROCESSINSTANCE.PROCESSINST_STATE",
			"BPM_PROCESSINSTANCE.COPYNUMBER", "BPM_PROCESSINSTANCE.STARTTIME",
			"BPM_PROCESSINSTANCE.LIMITTIME", "BPM_PROCESSINSTANCE.ENDTIME",
			"BPM_PROCESSINSTANCE.RUNSTATUS" };

	/**
	 * create an array of string containing all the fields of the
	 * BPM_PROCESSINSTANCE table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "PROCESSINST_ID",
			"PROCESSDEF_ID", "PROCESSDEF_VERSION_ID", "PROCESSINST_NAME",
			"PROCESSINST_URGENCY", "PROCESSINST_STATE", "COPYNUMBER",
			"STARTTIME", "LIMITTIME", "ENDTIME", "RUNSTATUS" };

	/**
	 * Field that contains the comma separated fields of the BPM_PROCESSINSTANCE
	 * table
	 */
	private static final String ALL_FIELDS = "BPM_PROCESSINSTANCE.PROCESSINST_ID"
			+ ",BPM_PROCESSINSTANCE.PROCESSDEF_ID"
			+ ",BPM_PROCESSINSTANCE.PROCESSDEF_VERSION_ID"
			+ ",BPM_PROCESSINSTANCE.PROCESSINST_NAME"
			+ ",BPM_PROCESSINSTANCE.PROCESSINST_URGENCY"
			+ ",BPM_PROCESSINSTANCE.PROCESSINST_STATE"
			+ ",BPM_PROCESSINSTANCE.COPYNUMBER"
			+ ",BPM_PROCESSINSTANCE.STARTTIME"
			+ ",BPM_PROCESSINSTANCE.LIMITTIME"
			+ ",BPM_PROCESSINSTANCE.ENDTIME"
			+ ",BPM_PROCESSINSTANCE.RUNSTATUS";

	/** 流程定义cache */
	Cache cache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbProcessInstManager() {
		cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ProcessInstCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new ProcessInstance instance.
	 * 
	 * @return the new ProcessInstance
	 */
	public EIProcessInst createProcessInstance() {
		return new DbProcessInst();
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
	 * Load a ProcessInstance from the BPM_PROCESSINSTANCE using its key fields.
	 * 
	 * @return a unique ProcessInstance
	 */
	// 12
	public EIProcessInst loadByKey(String processInstId) throws BPMException {
		EIProcessInst dbProcessInst = (DbProcessInst) cache.get(processInstId);
		if (dbProcessInst != null) {
			return dbProcessInst;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_PROCESSINSTANCE WHERE BPM_PROCESSINSTANCE.PROCESSINST_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_PROCESSINSTANCE WHERE BPM_PROCESSINSTANCE.PROCESSINST_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processInstId);
			EIProcessInst pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				putToCache(processInstId, (DbProcessInst) pReturn[0]);
				return pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	// ////////////////////////////////////
	// LOAD ALL
	// ////////////////////////////////////

	/**
	 * Load all the rows from BPM_PROCESSINSTANCE.
	 * 
	 * @return an array of ProcessInstanceManager pObject
	 */
	// 38
	public List loadAll() throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager().log(
					"SELECT " + ALL_FIELDS + " FROM BPM_PROCESSINSTANCE");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_PROCESSINSTANCE",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			List list = new DbProcessInstList(loadByPreparedStatement(ps,
					new int[] { ID_PROCESSINST_ID }));
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
	 * @return an array of ProcessInstance
	 */
	// 41
	public DbProcessInst[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of ProcessInstance
	 */
	public DbProcessInst[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbProcessInst[]) v.toArray(new DbProcessInst[0]);
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
	 * retreive an array of ProcessInstance given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting ProcessInstance table
	 */
	// 49
	public List loadByWhere(String where) throws BPMException {
		List result;
		result = new DbProcessInstList(loadByWhere(where,
				new int[] { ID_PROCESSINST_ID }));
		return Collections.unmodifiableList(result);
	}

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
	// 51
	public DbProcessInst[] loadByWhere(String where, int[] fieldList)
			throws BPMException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_PROCESSINSTANCE " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_PROCESSINSTANCE ");
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

			return (DbProcessInst[]) v.toArray(new DbProcessInst[0]);
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
	public int deleteByKey(String processinstId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"DELETE from BPM_PROCESSINSTANCE WHERE BPM_PROCESSINSTANCE.PROCESSINST_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_PROCESSINSTANCE WHERE BPM_PROCESSINSTANCE.PROCESSINST_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processinstId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			removeFromCache(processinstId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the ProcessInstance object(s) to be deleted
	 * @return the number of deleted objects
	 */
	// 63
	public int delete(EIProcessInst processInst) throws BPMException {
		DbProcessInst pObject = (DbProcessInst) processInst;
		if (pObject.isProcessinstIdInitialized() == true)
			return deleteByKey(pObject.getProcessInstId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_PROCESSINSTANCE WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstNameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_NAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstUrgencyInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_URGENCY").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstStateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_STATE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isCopynumberInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("COPYNUMBER").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isStarttimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("STARTTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isTimelimitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LIMITTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isEndtimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ENDTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRunstateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RUNSTATUS").append("=?");
				_dirtyAnd++;
			}
			getManager().log(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isProcessinstNameInitialized())
				ps.setString(++_dirtyCount, pObject.getName());
			if (pObject.isProcessinstUrgencyInitialized())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isProcessinstStateInitialized())
				ps.setString(++_dirtyCount, pObject.getState());
			if (pObject.isCopynumberInitialized())
				ps.setInt(++_dirtyCount, pObject.getCopyNumber());
			if (pObject.isStarttimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isTimelimitInitialized())
				ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isEndtimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getEndTime().getTime());
			if (pObject.isRunstateInitialized())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
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
	 * Delete rows from BPM_PROCESSINSTANCE table by where clausis
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
			DbProcessInst[] processInsts = loadByWhere(where,
					new int[] { ID_PROCESSINST_ID });
			for (int i = 0; i < processInsts.length; i++) {
				removeFromCache(processInsts[i].getProcessInstId());
			}
			// delete it
			c = getConnection();
			String delByWhereSQL = "DELETE FROM BPM_PROCESSINSTANCE " + where;
			getManager().log(delByWhereSQL);
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
	 * Save into the database an array of ProcessInstance pObjects
	 * 
	 * @param pObjects
	 *            the ProcessInstance pObject table to be saved
	 * @return the Objects to be saved
	 */
	// 65
	public EIProcessInst[] save(EIProcessInst[] pObjects) throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the ProcessInstance pObject.
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be saved
	 */
	// 67
	void beforeInsert(DbProcessInst pObject) throws SQLException {

	}

	/**
	 * after the save of the ProcessInstance pObject
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be saved
	 */
	// 68
	void afterInsert(DbProcessInst pObject) throws SQLException {

	}

	/**
	 * before the update of the ProcessInstance pObject.
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be updated
	 */
	// 69
	void beforeUpdate(DbProcessInst pObject) throws SQLException {

	}

	/**
	 * after the update of the ProcessInstance pObject.
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be updated
	 */
	// 70
	void afterUpdate(DbProcessInst pObject) throws SQLException {

	}

	/**
	 * Save into the database the ProcessInstance pObject
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to be saved
	 */
	// 100
	public EIProcessInst save(EIProcessInst processInst) throws BPMException {
		DbProcessInst pObject = (DbProcessInst) processInst;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_PROCESSINSTANCE (");
				if (pObject.isProcessinstIdModified()) {
					_sql.append("PROCESSINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstNameModified()) {
					_sql.append("PROCESSINST_NAME").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstUrgencyModified()) {
					_sql.append("PROCESSINST_URGENCY").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstStateModified()) {
					_sql.append("PROCESSINST_STATE").append(",");
					_dirtyCount++;
				}
				if (pObject.isCopynumberModified()) {
					_sql.append("COPYNUMBER").append(",");
					_dirtyCount++;
				}
				if (pObject.isStarttimeModified()) {
					_sql.append("STARTTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isTimelimitModified()) {
					_sql.append("LIMITTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isEndtimeModified()) {
					_sql.append("ENDTIME").append(",");
					_dirtyCount++;
				}
				if (pObject.isRunstateModified()) {
					_sql.append("RUNSTATUS").append(",");
					_dirtyCount++;
				}
				_sql.setLength(_sql.length() - 1);
				_sql.append(") values (");
				for (int i = 0; i < _dirtyCount; i++)
					_sql.append("?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(")");

				getManager().log(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isProcessdefIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessDefId());
				if (pObject.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, pObject
							.getProcessDefVersionId());
				if (pObject.isProcessinstNameModified())
					ps.setString(++_dirtyCount, pObject.getName());
				if (pObject.isProcessinstUrgencyModified())
					ps.setString(++_dirtyCount, pObject.getUrgency());
				if (pObject.isProcessinstStateModified())
					ps.setString(++_dirtyCount, pObject.getState());
				if (pObject.isCopynumberModified())
					ps.setInt(++_dirtyCount, pObject.getCopyNumber());
				if (pObject.isStarttimeModified())
					ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
				if (pObject.isTimelimitModified())
					ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
				if (pObject.isEndtimeModified())
					ps.setLong(++_dirtyCount, pObject.getEndTime().getTime());
				if (pObject.isRunstateModified())
					ps.setString(++_dirtyCount, pObject.getRunStatus());
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
				_sql = new StringBuffer("UPDATE BPM_PROCESSINSTANCE SET ");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (pObject.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (pObject.isProcessinstNameModified())
					_sql.append("PROCESSINST_NAME").append("=?,");
				if (pObject.isProcessinstUrgencyModified())
					_sql.append("PROCESSINST_URGENCY").append("=?,");
				if (pObject.isProcessinstStateModified())
					_sql.append("PROCESSINST_STATE").append("=?,");
				if (pObject.isCopynumberModified())
					_sql.append("COPYNUMBER").append("=?,");
				if (pObject.isStarttimeModified())
					_sql.append("STARTTIME").append("=?,");
				if (pObject.isTimelimitModified())
					_sql.append("LIMITTIME").append("=?,");
				if (pObject.isEndtimeModified())
					_sql.append("ENDTIME").append("=?,");
				if (pObject.isRunstateModified())
					_sql.append("RUNSTATUS").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_PROCESSINSTANCE.PROCESSINST_ID=?");
				getManager().log(_sql.toString());
				try{
					ps = c.prepareStatement(_sql.toString(),
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					int _dirtyCount = 0;
					if (pObject.isProcessinstIdModified()) {
						if (pObject.getProcessInstId() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getProcessInstId());
					}
					if (pObject.isProcessdefIdModified()) {
						if (pObject.getProcessDefId() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getProcessDefId());
					}
					if (pObject.isProcessdefVersionIdModified()) {
						if (pObject.getProcessDefVersionId() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject
									.getProcessDefVersionId());
					}
					if (pObject.isProcessinstNameModified()) {
						if (pObject.getName() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getName());
					}
					if (pObject.isProcessinstUrgencyModified()) {
						if (pObject.getUrgency() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getUrgency());
					}
					if (pObject.isProcessinstStateModified()) {
						if (pObject.getState() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getState());
					}
					if (pObject.isCopynumberModified()) {
						ps.setInt(++_dirtyCount, pObject.getCopyNumber());
					}
					if (pObject.isStarttimeModified()) {
						if (pObject.getStartTime() == null)
							ps.setNull(++_dirtyCount, Types.DECIMAL);
						else
							ps.setLong(++_dirtyCount, pObject.getStartTime()
									.getTime());
					}
					if (pObject.isTimelimitModified()) {
						ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
					}
					if (pObject.isEndtimeModified()) {
						if (pObject.getEndTime() == null)
							ps.setNull(++_dirtyCount, Types.DECIMAL);
						else
							ps.setLong(++_dirtyCount, pObject.getEndTime()
									.getTime());
					}
					if (pObject.isRunstateModified()) {
						if (pObject.getRunStatus() == null)
							ps.setNull(++_dirtyCount, Types.VARCHAR);
						else
							ps.setString(++_dirtyCount, pObject.getRunStatus());
					}
					if (_dirtyCount != 0) {
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
						ps.executeUpdate();
						pObject.resetIsModified();
						// postupdate+

						// postupdate-
						afterUpdate(pObject);
					}
				}catch (Exception e) {
					
				}
			
			
			}
			// 保存扩展属性
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
	 * Transform a ResultSet iterating on the BPM_PROCESSINSTANCE on a
	 * ProcessInstance pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting ProcessInstance pObject
	 */
	// 72
	public DbProcessInst decodeRow(ResultSet rs) throws SQLException {
		DbProcessInst pObject = (DbProcessInst) createProcessInstance();
		if (rs.getObject(1) != null)
			pObject.setProcessInstId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setProcessDefVersionId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setName(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setUrgency(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setState(rs.getString(6));
		if (rs.getObject(7) != null)
			pObject.setCopyNumber(rs.getInt(7));
		if (rs.getObject(8) != null)
			pObject.setStartTime(new Date(rs.getLong(8)));
		if (rs.getObject(9) != null)
			pObject.setLimitTime(new Date(rs.getLong(9)));
		if (rs.getObject(10) != null)
			pObject.setEndTime(new Date(rs.getLong(10)));
		if (rs.getObject(11) != null)
			pObject.setRunStatus(rs.getString(11));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_PROCESSINSTANCE on a
	 * ProcessInstance pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting ProcessInstance pObject
	 */
	// 73
	public DbProcessInst decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbProcessInst pObject = (DbProcessInst) createProcessInstance();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_PROCESSDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefId(rs.getString(pos));
				}
				break;
			case ID_PROCESSDEF_VERSION_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefVersionId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_NAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setName(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_URGENCY:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setUrgency(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_STATE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setState(rs.getString(pos));
				}
				break;
			case ID_COPYNUMBER:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCopyNumber(rs.getInt(pos));
				}
				break;
			case ID_STARTTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setStartTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_LIMITTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLimitTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_ENDTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEndTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_RUNSTATUS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRunStatus(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique ProcessInstance pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the ProcessInstance pObject to look for
	 * @return the pObject matching the template
	 */
	// 85
	public EIProcessInst loadObject(EIProcessInst pObject) throws BPMException {
		EIProcessInst[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new BPMException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of ProcessInstance from a template one
	 * 
	 * @param pObject
	 *            the ProcessInstance template to look for
	 * @return all the ProcessInstance matching the template
	 */
	// 88
	public EIProcessInst[] loadObjects(EIProcessInst processInst)
			throws BPMException {
		DbProcessInst pObject = (DbProcessInst) processInst;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from BPM_PROCESSINSTANCE WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isProcessdefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (pObject.isProcessdefVersionIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_VERSION_ID= ?");
			}
			if (pObject.isProcessinstNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_NAME= ?");
			}
			if (pObject.isProcessinstUrgencyModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_URGENCY= ?");
			}
			if (pObject.isProcessinstStateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_STATE= ?");
			}
			if (pObject.isCopynumberModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("COPYNUMBER= ?");
			}
			if (pObject.isStarttimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("STARTTIME= ?");
			}
			if (pObject.isTimelimitModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LIMITTIME= ?");
			}
			if (pObject.isEndtimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ENDTIME= ?");
			}
			if (pObject.isRunstateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RUNSTATUS= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			getManager().log(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isProcessdefIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isProcessdefVersionIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isProcessinstNameModified())
				ps.setString(++_dirtyCount, pObject.getName());
			if (pObject.isProcessinstUrgencyModified())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isProcessinstStateModified())
				ps.setString(++_dirtyCount, pObject.getState());
			if (pObject.isCopynumberModified())
				ps.setInt(++_dirtyCount, pObject.getCopyNumber());
			if (pObject.isStarttimeModified())
				ps.setLong(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isTimelimitModified())
				ps.setLong(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isEndtimeModified())
				ps.setLong(++_dirtyCount, pObject.getEndTime().getTime());
			if (pObject.isRunstateModified())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " PROCESSINST_ID, " + " PROPNAME, "
			+ " PROPVALUE, " + " PROPCLASS, " + " PROPTYPE, "
			+ " PARENTPROP_ID, " + " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_PROCESSINST_PROPERTY " + "WHERE PROCESSINST_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_PROCESS_EXTATTRIBUTRE = "DELETE FROM BPM_PROCESSINST_PROPERTY "
			+ "WHERE PROCESSINST_ID = ? ";

	private static final String DELETE_ONE_EXTATTRIBUTRE = "DELETE FROM BPM_PROCESSINST_PROPERTY "
			+ "WHERE PROPERTY_ID = ? ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_PROCESSINST_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " PROCESSINST_ID, "
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

	private static final String UPDATE_EXTATTRIBUTRE = "UPDATE BPM_PROCESSINST_PROPERTY SET "
			+ " PROPNAME=?, "
			+ " PROPVALUE=?, "
			+ " PROPCLASS=?, "
			+ " PROPTYPE=?, "
			+ " ISEXTENSION=?, "
			+ " CANINSTANTIATE=? "
			+ " where PROPERTY_ID=?";

	void loadAttribute(DbProcessInst proc) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, proc.getProcessInstId());
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
						addAttribute(proc, extAtt);
					}
					extAtt = (DbAttributeInst) proc.attributeIdMap
							.get(currUUID);
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
				addAttribute(proc, extAtt);
			}

		} catch (SQLException e) {
			throw new BPMException("", e);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void addAttributeToDb(DbProcessInst processInst, DbAttributeInst attr)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();

			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			saveAttribute(ps, processInst, attr);
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void updateAttributeToDb(DbProcessInst processInst, DbAttributeInst attr)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();

			getManager().close(ps);
			ps = c.prepareStatement(UPDATE_EXTATTRIBUTRE);
			updateAttribute(ps, processInst, attr);
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

	private void addAttribute(DbProcessInst proc, DbAttributeInst extAtt) {
		if (proc.attributeIdMap == null) {
			proc.attributeIdMap = new HashMap();
		}
		if (proc.attributeTopMap == null) {
			proc.attributeTopMap = new HashMap();
		}
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeInst parent = (DbAttributeInst) proc.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeInst();
				proc.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeInst att = (DbAttributeInst) proc.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeInst();
				att.setName(extAtt.getType());
				att.setType(extAtt.getType());

				proc.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);
			// act.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		proc.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	/**
	 * @param activityDef
	 */
	void saveAttribute(DbProcessInst proc) throws BPMException {
		//避免重复操作
		synchronized (proc.getProcessInstId().intern()) {
			if (proc.isAttributeModified() == false) {
				return;
			}
			Connection c = null;
			PreparedStatement ps = null;
			try {
				c = getConnection();
				ps = c.prepareStatement(DELETE_PROCESS_EXTATTRIBUTRE);
				ps.setString(1, proc.getProcessInstId());
				ps.executeUpdate();

				getManager().close(ps);
				ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
				List list = proc.getAllAttribute();
				for (Iterator it = list.iterator(); it.hasNext();) {
					DbAttributeInst att = (DbAttributeInst) it.next();
					saveAttribute(ps, proc, att);
				}

			} catch (SQLException e) {
				throw new BPMException("", e);

			} finally {
				getManager().close(ps);
				freeConnection(c);
			}
		}
		

	}

	private void saveAttribute(PreparedStatement ps, DbProcessInst proc,
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

			ps.setString(1, attInst.getId());
			ps.setString(2, proc.getProcessInstId());
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

	private void updateAttribute(PreparedStatement ps, DbProcessInst proc,
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
	 * @param dbProcessInst
	 */
	private void putToCache(String uuid, DbProcessInst dbProcessInst) {
		synchronized (uuid.intern()) {
			if (!cache.containsKey(uuid)) {

				cache.put(uuid, dbProcessInst);
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
		StringBuffer sqlWhere = new StringBuffer(" WHERE PROCESSINST_ID IN (");
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
			EIProcessInst[] list = null;
			try {
				list = loadByWhere(sqlWhere.toString(), null);
			} catch (BPMException e) {
				log.error("prefetch the process instance fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < list.length; i++) {
				DbProcessInst pd = (DbProcessInst) list[i];
				putToCache(pd.getProcessInstId(), pd);
			}
		}
	}
}


