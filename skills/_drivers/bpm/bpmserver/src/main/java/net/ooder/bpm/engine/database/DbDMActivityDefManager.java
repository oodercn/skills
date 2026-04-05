/**
 * $RCSfile: DbDMActivityDefManager.java,v $
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

public class DbDMActivityDefManager extends EIActivityDefManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbDMActivityDefManager.class);

	public static final int ID_ACTIVITYDEF_ID = 0;

	public static final int TYPE_ACTIVITYDEF_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYDEF_ID = "ACTIVITYDEF_ID";

	public static final int ID_PROCESSDEF_ID = 1;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_PROCESSDEF_VERSION_ID = 2;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_DEFNAME = 3;

	public static final int TYPE_DEFNAME = Types.VARCHAR;

	public static final String NAME_DEFNAME = "DEFNAME";

	public static final int ID_DESCRIPTION = 4;

	public static final int TYPE_DESCRIPTION = Types.VARCHAR;

	public static final String NAME_DESCRIPTION = "DESCRIPTION";

	public static final int ID_POSITION = 5;

	public static final int TYPE_POSITION = Types.VARCHAR;

	public static final String NAME_POSITION = "POSITION";

	public static final int ID_IMPLEMENTATION = 6;

	public static final int TYPE_IMPLEMENTATION = Types.VARCHAR;

	public static final String NAME_IMPLEMENTATION = "IMPLEMENTATION";

	public static final int ID_EXECCLASS = 7;

	public static final int TYPE_EXECCLASS = Types.VARCHAR;

	public static final String NAME_EXECCLASS = "EXECCLASS";

	public static final int ID_LIMIT = 8;

	public static final int TYPE_LIMIT = Types.DECIMAL;

	public static final String NAME_LIMIT = "ACTIVITYLIMIT";

	public static final int ID_ALERTTIME = 9;

	public static final int TYPE_ALERTTIME = Types.DECIMAL;

	public static final String NAME_ALERTTIME = "ALERTTIME";

	public static final int ID_DURATIONUNIT = 10;

	public static final int TYPE_DURATIONUNIT = Types.VARCHAR;

	public static final String NAME_DURATIONUNIT = "DURATIONUNIT";

	public static final int ID_DEADLINEOPERATION = 11;

	public static final int TYPE_DEADLINEOPERATION = Types.VARCHAR;

	public static final String NAME_DEADLINEOPERATION = "DEADLINEOPERATION";

	public static final int ID_CANROUTEBACK = 12;

	public static final int TYPE_CANROUTEBACK = Types.VARCHAR;

	public static final String NAME_CANROUTEBACK = "CANROUTEBACK";

	public static final int ID_ROUTEBACKMETHOD = 13;

	public static final int TYPE_ROUTEBACKMETHOD = Types.VARCHAR;

	public static final String NAME_ROUTEBACKMETHOD = "ROUTEBACKMETHOD";

	public static final int ID_CANSPECIALSEND = 14;

	public static final int TYPE_CANSPECIALSEND = Types.VARCHAR;

	public static final String NAME_CANSPECIALSEND = "CANSPECIALSEND";

	public static final int ID_JOIN = 15;

	public static final int TYPE_JOIN = Types.VARCHAR;

	public static final String NAME_JOIN = "INJOIN";

	public static final int ID_SPLIT = 16;

	public static final int TYPE_SPLIT = Types.VARCHAR;

	public static final String NAME_SPLIT = "SPLIT";

	private static final String TABLE_NAME = "BPM_ACTIVITYDEF";

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYDEF table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_ACTIVITYDEF.ACTIVITYDEF_ID", "BPM_ACTIVITYDEF.PROCESSDEF_ID",
			"BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID", "BPM_ACTIVITYDEF.DEFNAME",
			"BPM_ACTIVITYDEF.DESCRIPTION", "BPM_ACTIVITYDEF.POSITION",
			"BPM_ACTIVITYDEF.IMPLEMENTATION", "BPM_ACTIVITYDEF.EXECCLASS",
			"BPM_ACTIVITYDEF.ACTIVITYLIMIT", "BPM_ACTIVITYDEF.ALERTTIME",
			"BPM_ACTIVITYDEF.DURATIONUNIT",
			"BPM_ACTIVITYDEF.DEADLINEOPERATION",
			"BPM_ACTIVITYDEF.CANROUTEBACK", "BPM_ACTIVITYDEF.ROUTEBACKMETHOD",
			"BPM_ACTIVITYDEF.CANSPECIALSEND", "BPM_ACTIVITYDEF.INJOIN",
			"BPM_ACTIVITYDEF.SPLIT" };

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYDEF table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "ACTIVITYDEF_ID",
			"PROCESSDEF_ID", "PROCESSDEF_VERSION_ID", "DEFNAME", "DESCRIPTION",
			"POSITION", "IMPLEMENTATION", "EXECCLASS", "ACTIVITYLIMIT", "ALERTTIME",
			"DURATIONUNIT", "DEADLINEOPERATION", "CANROUTEBACK",
			"ROUTEBACKMETHOD", "CANSPECIALSEND", "INJOIN", "SPLIT" };

	/**
	 * Field that contains the comma separated fields of the BPM_ACTIVITYDEF
	 * table
	 */
	private static final String ALL_FIELDS = 
		"BPM_ACTIVITYDEF.ACTIVITYDEF_ID"
			+ ",BPM_ACTIVITYDEF.PROCESSDEF_ID"
			+ ",BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID"
			+ ",BPM_ACTIVITYDEF.DEFNAME" + ",BPM_ACTIVITYDEF.DESCRIPTION"
			+ ",BPM_ACTIVITYDEF.POSITION" + ",BPM_ACTIVITYDEF.IMPLEMENTATION"
			+ ",BPM_ACTIVITYDEF.EXECCLASS" + ",BPM_ACTIVITYDEF.ACTIVITYLIMIT"
			+ ",BPM_ACTIVITYDEF.ALERTTIME" + ",BPM_ACTIVITYDEF.DURATIONUNIT"
			+ ",BPM_ACTIVITYDEF.DEADLINEOPERATION"
			+ ",BPM_ACTIVITYDEF.CANROUTEBACK"
			+ ",BPM_ACTIVITYDEF.ROUTEBACKMETHOD"
			+ ",BPM_ACTIVITYDEF.CANSPECIALSEND" + ",BPM_ACTIVITYDEF.INJOIN"
			+ ",BPM_ACTIVITYDEF.SPLIT";

	/** 流程定义cache */
	Cache activityDefCache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbDMActivityDefManager() {
		activityDefCache = CacheManagerFactory.createCache(
				BPMConstants.CONFIG_KEY, "ActivityDefCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new EIActivityDef instance.
	 * 
	 * @return the new EIActivityDef
	 */
	public EIActivityDef createActivityDef() {
		return new DbActivityDef();
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

	/**
	 * Load a BPMActivitydefBean from the BPM_ACTIVITYDEF using its key fields.
	 * 
	 * @return a unique BPMActivitydefBean
	 */
	public EIActivityDef loadByKey(String activitydefId) throws BPMException {
		DbActivityDef dbActivityDef = (DbActivityDef) activityDefCache
				.get(activitydefId);
		if (dbActivityDef != null) {
			return dbActivityDef;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM BPM_ACTIVITYDEF WHERE BPM_ACTIVITYDEF.ACTIVITYDEF_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_ACTIVITYDEF WHERE BPM_ACTIVITYDEF.ACTIVITYDEF_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activitydefId);
			DbActivityDef pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				DbActivityDef act = pReturn[0];
				putToCache(act.getActivityDefId(), act);
				return pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load a DbActivityDef table from the BPM_ACTIVITYDEF table using its
	 * PROCESSDEF_VERSION_ID field.
	 * 
	 * @return an array of DbActivityDef
	 */
	public List loadByProcessDefVersionId(String value) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID=?");
			ps = c
					.prepareStatement(
							"SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			DbActivityDef[] acts = loadByPreparedStatement(ps,
					new int[] { ID_ACTIVITYDEF_ID });
			List list = new DbActivityDefList(acts);
			return Collections.unmodifiableList(list);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete from the BPM_ACTIVITYDEF table by PROCESSDEF_VERSION_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public int deleteByProcessDefVersionId(String value) throws BPMException {
		String where = " WHERE PROCESSDEF_VERSION_ID='" + value + "'";
		return deleteByWhere(where);
	}

	/**
	 * get the BPMProcessdefVersionBean object from the
	 * BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID field.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean
	 * @return the associated BPMProcessdefVersionBean pObject
	 */
	public EIProcessDefVersion getProcessDefVersion(EIActivityDef pObject)
			throws BPMException {
		EIProcessDefVersion other = EIProcessDefVersionManager.getInstance()
				.createProcessDefVersion();
		other.setProcessDefVersionId(pObject.getProcessDefVersionId());
		return DbProcessDefVersionManager.getInstance().loadObject(other);
	}

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
	public EIActivityDef setProcessDefVersion(EIActivityDef pObject,
			EIProcessDefVersion pObjectToBeSet) {
		pObject.setProcessDefVersionId(pObjectToBeSet.getProcessDefVersionId());
		return pObject;
	}

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of BPMActivitydefBean
	 */
	DbActivityDef[] loadByPreparedStatement(PreparedStatement ps)
			throws SQLException {
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
	 * @return an array of BPMActivitydefBean
	 */
	DbActivityDef[] loadByPreparedStatement(PreparedStatement ps,
			int[] fieldList) throws SQLException {
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
			return (DbActivityDef[]) v.toArray(new DbActivityDef[0]);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	/**
	 * retreive an array of BPMActivitydefBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMActivitydefBean table
	 */
	public List loadByWhere(String where) throws BPMException {
		DbActivityDef[] acts;
		try {
			acts = loadByWhere(where, new int[] { ID_ACTIVITYDEF_ID });
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		}
		List list = new DbActivityDefList(acts);
		return Collections.unmodifiableList(list);
	}

	/**
	 * retreive an array of BPMActivitydefBean given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting BPMActivitydefBean table
	 */
	DbActivityDef[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_ACTIVITYDEF " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_ACTIVITYDEF ");
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

			return (DbActivityDef[]) v.toArray(new DbActivityDef[0]);
		} finally {
			if (v != null) {
				v.clear();
			}
			getManager().close(pStatement, rs);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to its keys
	 * 
	 * @return the number of deleted rows
	 */
	public int deleteByKey(String activitydefId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			deleteAttributeByKey(activitydefId);
			deleteListenerByKey(activitydefId);
			if (log.isDebugEnabled())
				log
						.debug("DELETE from BPM_ACTIVITYDEF WHERE BPM_ACTIVITYDEF.ACTIVITYDEF_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_ACTIVITYDEF WHERE BPM_ACTIVITYDEF.ACTIVITYDEF_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activitydefId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		} finally {
			removeFromCache(activitydefId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(EIActivityDef pObject) throws BPMException {
		DbActivityDef activityDef = (DbActivityDef) pObject;

		if (activityDef.isActivitydefIdInitialized() == true)
			return deleteByKey(activityDef.getActivityDefId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_ACTIVITYDEF WHERE ");
			int _dirtyAnd = 0;
			if (activityDef.isActivitydefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isDefnameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEFNAME").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isDescriptionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DESCRIPTION").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isPositionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("POSITION").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isImplementationInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("IMPLEMENTATION").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isExecclassInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("EXECCLASS").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isLimitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYLIMIT").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isAlerttimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ALERTTIME").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isDurationunitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DURATIONUNIT").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isDeadlineoperationInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEADLINEOPERATION").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isCantakebackInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CANROUTEBACK").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isTakebackmethodInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEBACKMETHOD").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isCanspecialsendInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CANSPECIALSEND").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isJoinInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("INJOIN").append("=?");
				_dirtyAnd++;
			}
			if (activityDef.isSplitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SPLIT").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (activityDef.isActivitydefIdInitialized())
				ps.setString(++_dirtyCount, activityDef.getActivityDefId());
			if (activityDef.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, activityDef.getProcessDefId());
			if (activityDef.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, activityDef
						.getProcessDefVersionId());
			if (activityDef.isDefnameInitialized())
				ps.setString(++_dirtyCount, activityDef.getName());
			if (activityDef.isDescriptionInitialized())
				ps.setString(++_dirtyCount, activityDef.getDescription());
			if (activityDef.isPositionInitialized())
				ps.setString(++_dirtyCount, activityDef.getPosition());
			if (activityDef.isImplementationInitialized())
				ps.setString(++_dirtyCount, activityDef.getImplementation());
			if (activityDef.isExecclassInitialized())
				ps.setString(++_dirtyCount, activityDef.getExecClass());
			if (activityDef.isLimitInitialized())
				ps.setInt(++_dirtyCount, activityDef.getLimit());
			if (activityDef.isAlerttimeInitialized())
				ps.setInt(++_dirtyCount, activityDef.getAlertTime());
			if (activityDef.isDurationunitInitialized())
				ps.setString(++_dirtyCount, activityDef.getDurationUnit());
			if (activityDef.isDeadlineoperationInitialized())
				ps.setString(++_dirtyCount, activityDef.getDeadlineOperation());
			if (activityDef.isCantakebackInitialized())
				ps.setString(++_dirtyCount, activityDef.getCanRouteBack());
			if (activityDef.isTakebackmethodInitialized())
				ps.setString(++_dirtyCount, activityDef.getRouteBackMethod());
			if (activityDef.isCanspecialsendInitialized())
				ps.setString(++_dirtyCount, activityDef.getCanSpecialSend());
			if (activityDef.isJoinInitialized())
				ps.setString(++_dirtyCount, activityDef.getJoin());
			if (activityDef.isSplitInitialized())
				ps.setString(++_dirtyCount, activityDef.getSplit());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from BPM_ACTIVITYDEF table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public int deleteByWhere(String where) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			// remove from cache first!
			DbActivityDef[] acts;
			try {
				acts = loadByWhere(where, new int[] { ID_ACTIVITYDEF_ID });
				for (int i = 0; i < acts.length; i++) {
					removeFromCache(acts[i].getActivityDefId());
				}
			} catch (SQLException e) {
				throw new BPMException("", e,
						BPMException.ACTIVITYDEFINITIONERROR);
			}
			// delete it!
			c = getConnection();
			deleteAttributeByWhere(where);
			deleteListenerByWhere(where);
			String delByWhereSQL = "DELETE FROM BPM_ACTIVITYDEF " + where;
			if (log.isDebugEnabled())
				log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of BPMActivitydefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMActivitydefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIActivityDef[] save(EIActivityDef[] pObjects) throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the BPMActivitydefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be saved
	 */
	void beforeInsert(DbActivityDef pObject) throws SQLException {
	}

	/**
	 * after the save of the BPMActivitydefBean pObject
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be saved
	 */
	void afterInsert(DbActivityDef pObject) throws SQLException {
	}

	/**
	 * before the update of the BPMActivitydefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be updated
	 */
	void beforeUpdate(DbActivityDef pObject) throws SQLException {
	}

	/**
	 * after the update of the BPMActivitydefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be updated
	 */
	void afterUpdate(DbActivityDef pObject) throws SQLException {
	}

	/**
	 * Save into the database the BPMActivitydefBean pObject
	 * 
	 * @param pObject
	 *            the BPMActivitydefBean pObject to be saved
	 */
	public EIActivityDef save(EIActivityDef pObject) throws BPMException {
		DbActivityDef activityDef = (DbActivityDef) pObject;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (activityDef.isNew()) { // SAVE
				beforeInsert(activityDef);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_ACTIVITYDEF (");
				if (activityDef.isActivitydefIdModified()) {
					_sql.append("ACTIVITYDEF_ID").append(",");
					_dirtyCount++;
				}
				if (activityDef.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (activityDef.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (activityDef.isDefnameModified()) {
					_sql.append("DEFNAME").append(",");
					_dirtyCount++;
				}
				if (activityDef.isDescriptionModified()) {
					_sql.append("DESCRIPTION").append(",");
					_dirtyCount++;
				}
				if (activityDef.isPositionModified()) {
					_sql.append("POSITION").append(",");
					_dirtyCount++;
				}
				if (activityDef.isImplementationModified()) {
					_sql.append("IMPLEMENTATION").append(",");
					_dirtyCount++;
				}
				if (activityDef.isExecclassModified()) {
					_sql.append("EXECCLASS").append(",");
					_dirtyCount++;
				}
				if (activityDef.isLimitModified()) {
					_sql.append("ACTIVITYLIMIT").append(",");
					_dirtyCount++;
				}
				if (activityDef.isAlerttimeModified()) {
					_sql.append("ALERTTIME").append(",");
					_dirtyCount++;
				}
				if (activityDef.isDurationunitModified()) {
					_sql.append("DURATIONUNIT").append(",");
					_dirtyCount++;
				}
				if (activityDef.isDeadlineoperationModified()) {
					_sql.append("DEADLINEOPERATION").append(",");
					_dirtyCount++;
				}
				if (activityDef.isCantakebackModified()) {
					_sql.append("CANROUTEBACK").append(",");
					_dirtyCount++;
				}
				if (activityDef.isTakebackmethodModified()) {
					_sql.append("ROUTEBACKMETHOD").append(",");
					_dirtyCount++;
				}
				if (activityDef.isCanspecialsendModified()) {
					_sql.append("CANSPECIALSEND").append(",");
					_dirtyCount++;
				}
				if (activityDef.isJoinModified()) {
					_sql.append("INJOIN").append(",");
					_dirtyCount++;
				}
				if (activityDef.isSplitModified()) {
					_sql.append("SPLIT").append(",");
					_dirtyCount++;
				}
				_sql.setLength(_sql.length() - 1);
				_sql.append(") values (");
				for (int i = 0; i < _dirtyCount; i++)
					_sql.append("?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(")");

				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (activityDef.isActivitydefIdModified())
					ps.setString(++_dirtyCount, activityDef.getActivityDefId());
				if (activityDef.isProcessdefIdModified())
					ps.setString(++_dirtyCount, activityDef.getProcessDefId());
				if (activityDef.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, activityDef
							.getProcessDefVersionId());
				if (activityDef.isDefnameModified())
					ps.setString(++_dirtyCount, activityDef.getName());
				if (activityDef.isDescriptionModified())
					ps.setString(++_dirtyCount, activityDef.getDescription());
				if (activityDef.isPositionModified())
					ps.setString(++_dirtyCount, activityDef.getPosition());
				if (activityDef.isImplementationModified())
					ps
							.setString(++_dirtyCount, activityDef
									.getImplementation());
				if (activityDef.isExecclassModified())
					ps.setString(++_dirtyCount, activityDef.getExecClass());
				if (activityDef.isLimitModified())
					ps.setLong(++_dirtyCount, activityDef.getLimit());
				if (activityDef.isAlerttimeModified())
					ps.setLong(++_dirtyCount, activityDef.getAlertTime());
				if (activityDef.isDurationunitModified())
					ps.setString(++_dirtyCount, activityDef.getDurationUnit());
				if (activityDef.isDeadlineoperationModified())
					ps.setString(++_dirtyCount, activityDef
							.getDeadlineOperation());
				if (activityDef.isCantakebackModified())
					ps.setString(++_dirtyCount, activityDef.getCanRouteBack());
				if (activityDef.isTakebackmethodModified())
					ps.setString(++_dirtyCount, activityDef
							.getRouteBackMethod());
				if (activityDef.isCanspecialsendModified())
					ps
							.setString(++_dirtyCount, activityDef
									.getCanSpecialSend());
				if (activityDef.isJoinModified())
					ps.setString(++_dirtyCount, activityDef.getJoin());
				if (activityDef.isSplitModified())
					ps.setString(++_dirtyCount, activityDef.getSplit());
				ps.executeUpdate();
				activityDef.setIsNew(false);
				afterInsert(activityDef);
			} else { // UPDATE
				beforeUpdate(activityDef);
				_sql = new StringBuffer("UPDATE BPM_ACTIVITYDEF SET ");
				if (activityDef.isActivitydefIdModified())
					_sql.append("ACTIVITYDEF_ID").append("=?,");
				if (activityDef.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (activityDef.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (activityDef.isDefnameModified())
					_sql.append("DEFNAME").append("=?,");
				if (activityDef.isDescriptionModified())
					_sql.append("DESCRIPTION").append("=?,");
				if (activityDef.isPositionModified())
					_sql.append("POSITION").append("=?,");
				if (activityDef.isImplementationModified())
					_sql.append("IMPLEMENTATION").append("=?,");
				if (activityDef.isExecclassModified())
					_sql.append("EXECCLASS").append("=?,");
				if (activityDef.isLimitModified())
					_sql.append("ACTIVITYLIMIT").append("=?,");
				if (activityDef.isAlerttimeModified())
					_sql.append("ALERTTIME").append("=?,");
				if (activityDef.isDurationunitModified())
					_sql.append("DURATIONUNIT").append("=?,");
				if (activityDef.isDeadlineoperationModified())
					_sql.append("DEADLINEOPERATION").append("=?,");
				if (activityDef.isCantakebackModified())
					_sql.append("CANROUTEBACK").append("=?,");
				if (activityDef.isTakebackmethodModified())
					_sql.append("ROUTEBACKMETHOD").append("=?,");
				if (activityDef.isCanspecialsendModified())
					_sql.append("CANSPECIALSEND").append("=?,");
				if (activityDef.isJoinModified())
					_sql.append("INJOIN").append("=?,");
				if (activityDef.isSplitModified())
					_sql.append("SPLIT").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_ACTIVITYDEF.ACTIVITYDEF_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (activityDef.isActivitydefIdModified()) {
					if (activityDef.getActivityDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getActivityDefId());
				}
				if (activityDef.isProcessdefIdModified()) {
					if (activityDef.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getProcessDefId());
				}
				if (activityDef.isProcessdefVersionIdModified()) {
					if (activityDef.getProcessDefVersionId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getProcessDefVersionId());
				}
				if (activityDef.isDefnameModified()) {
					if (activityDef.getName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef.getName());
				}
				if (activityDef.isDescriptionModified()) {
					if (activityDef.getDescription() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getDescription());
				}
				if (activityDef.isPositionModified()) {
					if (activityDef.getPosition() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef.getPosition());
				}
				if (activityDef.isImplementationModified()) {
					if (activityDef.getImplementation() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getImplementation());
				}
				if (activityDef.isExecclassModified()) {
					if (activityDef.getExecClass() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef.getExecClass());
				}
				if (activityDef.isLimitModified()) {
					ps.setInt(++_dirtyCount, activityDef.getLimit());
				}
				if (activityDef.isAlerttimeModified()) {
					ps.setDouble(++_dirtyCount, activityDef.getAlertTime());
				}
				if (activityDef.isDurationunitModified()) {
					if (activityDef.getDurationUnit() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getDurationUnit());
				}
				if (activityDef.isDeadlineoperationModified()) {
					if (activityDef.getDeadlineOperation() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getDeadlineOperation());
				}
				if (activityDef.isCantakebackModified()) {
					if (activityDef.getCanRouteBack() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getCanRouteBack());
				}
				if (activityDef.isTakebackmethodModified()) {
					if (activityDef.getRouteBackMethod() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getRouteBackMethod());
				}
				if (activityDef.isCanspecialsendModified()) {
					if (activityDef.getCanSpecialSend() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef
								.getCanSpecialSend());
				}
				if (activityDef.isJoinModified()) {
					if (activityDef.getJoin() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef.getJoin());
				}
				if (activityDef.isSplitModified()) {
					if (activityDef.getSplit() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, activityDef.getSplit());
				}
				if (_dirtyCount != 0) {
					ps.setString(++_dirtyCount, activityDef.getActivityDefId());
					ps.executeUpdate();

					afterUpdate(activityDef);
				}

			}
			saveAttribute(activityDef);
			saveListeners(activityDef);
			activityDef.resetIsModified();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return activityDef;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYDEF on a
	 * BPMActivitydefBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting BPMActivitydefBean pObject
	 */
	DbActivityDef decodeRow(ResultSet rs) throws SQLException {
		DbActivityDef pObject = (DbActivityDef) createActivityDef();
		if (rs.getObject(1) != null)
			pObject.setActivityDefId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setProcessDefVersionId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setName(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setDescription(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setPosition(rs.getString(6));
		if (rs.getObject(7) != null)
			pObject.setImplementation(rs.getString(7));
		if (rs.getObject(8) != null)
			pObject.setExecClass(rs.getString(8));
		if (rs.getObject(9) != null)
			pObject.setLimit(rs.getInt(9));
		if (rs.getObject(10) != null)
			pObject.setAlertTime(rs.getInt(10));
		if (rs.getObject(11) != null)
			pObject.setDurationUnit(rs.getString(11));
		if (rs.getObject(12) != null)
			pObject.setDeadlineOperation(rs.getString(12));
		if (rs.getObject(13) != null)
			pObject.setCanRouteBack(rs.getString(13));
		if (rs.getObject(14) != null)
			pObject.setRouteBackMethod(rs.getString(14));
		if (rs.getObject(15) != null)
			pObject.setCanSpecialSend(rs.getString(15));
		if (rs.getObject(16) != null)
			pObject.setJoin(rs.getString(16));
		if (rs.getObject(17) != null)
			pObject.setSplit(rs.getString(17));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYDEF on a
	 * BPMActivitydefBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting BPMActivitydefBean pObject
	 */
	DbActivityDef decodeRow(ResultSet rs, int[] fieldList) throws SQLException {
		DbActivityDef pObject = (DbActivityDef) createActivityDef();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
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
			case ID_PROCESSDEF_VERSION_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefVersionId(rs.getString(pos));
				}
				break;
			case ID_DEFNAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setName(rs.getString(pos));
				}
				break;
			case ID_DESCRIPTION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDescription(rs.getString(pos));
				}
				break;
			case ID_POSITION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPosition(rs.getString(pos));
				}
				break;
			case ID_IMPLEMENTATION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setImplementation(rs.getString(pos));
				}
				break;
			case ID_EXECCLASS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setExecClass(rs.getString(pos));
				}
				break;
			case ID_LIMIT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLimit(rs.getInt(pos));
				}
				break;
			case ID_ALERTTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setAlertTime(rs.getInt(pos));
				}
				break;
			case ID_DURATIONUNIT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDurationUnit(rs.getString(pos));
				}
				break;
			case ID_DEADLINEOPERATION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDeadlineOperation(rs.getString(pos));
				}
				break;
			case ID_CANROUTEBACK:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCanRouteBack(rs.getString(pos));
				}
				break;
			case ID_ROUTEBACKMETHOD:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteBackMethod(rs.getString(pos));
				}
				break;
			case ID_CANSPECIALSEND:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCanSpecialSend(rs.getString(pos));
				}
				break;
			case ID_JOIN:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setJoin(rs.getString(pos));
				}
				break;
			case ID_SPLIT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setSplit(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	public EIActivityDef getFirstActivityDefInProcess(String value)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID=? AND POSITION='START'");
			ps = c
					.prepareStatement(
							"SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF WHERE PROCESSDEF_VERSION_ID=? AND POSITION='START'",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			DbActivityDef[] acts = loadByPreparedStatement(ps,
					new int[] { ID_ACTIVITYDEF_ID });
			if (acts.length == 0) {
				return null;
			} else if (acts.length > 1) {
				throw new BPMException(
						"more than one start activity in process " + value,
						BPMException.ACTIVITYDEFINITIONERROR);
			} else {
				return loadByKey(acts[0].getActivityDefId());
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ACTIVITYDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " ACTIVITYDEF_ID, " + " PROPNAME, "
			+ " PROPVALUE, " + " PROPCLASS, " + " PROPTYPE, "
			+ " PARENTPROP_ID, " + " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_ACTIVITYDEF_PROPERTY " + "WHERE ACTIVITYDEF_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_EXTATTRIBUTRE = "DELETE FROM BPM_ACTIVITYDEF_PROPERTY "
			+ "WHERE ACTIVITYDEF_ID = ? ";

	private static final String DELETE_EXTATTRIBUTRE_BYWHERE = "DELETE FROM BPM_ACTIVITYDEF_PROPERTY "
			+ " WHERE ACTIVITYDEF_ID IN (SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_ACTIVITYDEF_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " ACTIVITYDEF_ID, "
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

	void loadAttribute(DbActivityDef act) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(LOAD_EXTATTRIBUTRE);
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, act.getActivityDefId());
			ResultSet rs = ps.executeQuery();
			String lastUUID = "";
			DbAttributeDef extAtt = null;
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
					extAtt = (DbAttributeDef) act.attributeIdMap.get(currUUID);
					if (extAtt == null) {
						extAtt = new DbAttributeDef();
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
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void addAttribute(DbActivityDef act, DbAttributeDef extAtt) {
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeDef parent = (DbAttributeDef) act.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeDef();
				act.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeDef att = (DbAttributeDef) act.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeDef();
				att.setName(extAtt.getType());
				att.setType(extAtt.getType());
				act.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);
			// act.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		act.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	/**
	 * @param activityDef
	 */
	void saveAttribute(DbActivityDef activityDef) throws BPMException {
		if (activityDef.isAttributeModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, activityDef.getActivityDefId());
			ps.executeUpdate();

			getManager().close(ps);
			if (log.isDebugEnabled())
				log.debug(INSERT_EXTATTRIBUTRE);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			List list = activityDef.getAllAttribute();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbAttributeDef att = (DbAttributeDef) it.next();
				saveAttribute(ps, activityDef, att);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void saveAttribute(PreparedStatement ps, DbActivityDef activityDef,
			DbAttributeDef attDef) throws SQLException {

		String value = attDef.getValue();
		if (value == null) {
			value = "";
		}
		int block = (value.length() / 500);
		for (int i = 0; i <= block; i++) {
			int begin = i * 500;
			int end = (i == block) ? value.length() : (i + 1) * 500;
			String v = value.substring(begin, end);

			ps.setString(1, attDef.getId());
			ps.setString(2, activityDef.getActivityDefId());
			ps.setString(3, attDef.getName());
			ps.setString(4, v);
			ps.setString(5, attDef.getInterpretClass());
			ps.setString(6, attDef.getType());
			ps.setString(7, attDef.getParentId());
			ps.setInt(8, i);
			ps.setString(9, attDef.getCanInstantiate());
			ps.executeUpdate();
		}
	}

	private void deleteAttributeByKey(String activityDefId) throws BPMException {

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, activityDefId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void deleteAttributeByWhere(String where) throws BPMException {

		Connection c = null;
		PreparedStatement ps = null;
		try {
			String sql = DELETE_EXTATTRIBUTRE_BYWHERE + where + " ) ";
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(sql);
			ps = c.prepareStatement(sql);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}
	
private String expressionEventType;
	
	private String expressionListenerType;
	
	private String expressionStr;


	private static final String LOAD_ACTIVITYDEF_LISTENER = "SELECT LISTENER_ID, "
			+ " ACTIVITYDEF_ID, "
			+ " LISTENERNAME, "
			+ " LISTENERREGISTEVENT, "
			+ " REALIZECLASS, "
			+ " EXPRESSIONSTR, "
			+ " EXPRESSIONLISENTERTYPE, "
			+ " EXPRESSIONEVENTTYPE "
			+ " FROM BPM_ACTIVITYLISTENER " + "WHERE ACTIVITYDEF_ID = ? ";

	private static final String DELETE_ACTIVITYDEF_LISTENER = "DELETE FROM BPM_ACTIVITYLISTENER "
			+ " WHERE ACTIVITYDEF_ID = ? ";

	private static final String DELETE_ACTIVITYDEF_LISTENER_BYWHERE = "DELETE FROM BPM_ACTIVITYLISTENER "
			+ " WHERE ACTIVITYDEF_ID IN (SELECT ACTIVITYDEF_ID FROM BPM_ACTIVITYDEF ";

	private static final String INSERT_ACTIVITYDEF_LISTENER = "INSERT INTO BPM_ACTIVITYLISTENER "
			+ " ( "
			+ " LISTENER_ID, "
			+ " ACTIVITYDEF_ID, "
			+ " LISTENERNAME, "
			+ " LISTENERREGISTEVENT, "
			+ " REALIZECLASS, "
			+ " EXPRESSIONSTR,"
			+ " EXPRESSIONLISENTERTYPE, "
			+ " EXPRESSIONEVENTTYPE "
			+ " ) VALUES ( " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, "+ " ?, "+ " ?, "+ " ? ) ";

	void loadListener(DbActivityDef act) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(LOAD_ACTIVITYDEF_LISTENER);
			ps = c.prepareStatement(LOAD_ACTIVITYDEF_LISTENER);
			ps.setString(1, act.getActivityDefId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DbListener listener = new DbListener();
				listener.setListenerId(rs.getString("LISTENER_ID"));
				listener.setListenerName(rs.getString("LISTENERNAME"));
				listener.setListenerEvent(rs.getString("LISTENERREGISTEVENT"));
				listener.setRealizeClass(rs.getString("REALIZECLASS"));
				listener.setExpressionStr(rs.getString("EXPRESSIONSTR"));
				listener.setExpressionListenerType(rs.getString("EXPRESSIONLISENTERTYPE"));
				listener.setExpressionEventType(rs.getString("EXPRESSIONEVENTTYPE"));
				act.listeners.add(listener);
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void deleteListenerByKey(String activityDefId) throws BPMException {

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_ACTIVITYDEF_LISTENER);
			ps = c.prepareStatement(DELETE_ACTIVITYDEF_LISTENER);
			ps.setString(1, activityDefId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void deleteListenerByWhere(String where) throws BPMException {

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			String sql = DELETE_ACTIVITYDEF_LISTENER_BYWHERE + where + " ) ";
			if (log.isDebugEnabled())
				log.debug(sql);
			ps = c.prepareStatement(sql);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void saveListeners(DbActivityDef activityDef) throws BPMException {
		if (activityDef.isListenersModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_ACTIVITYDEF_LISTENER);
			ps = c.prepareStatement(DELETE_ACTIVITYDEF_LISTENER);
			ps.setString(1, activityDef.getActivityDefId());
			ps.executeUpdate();

			getManager().close(ps);
			if (log.isDebugEnabled())
				log.debug(INSERT_ACTIVITYDEF_LISTENER);
			ps = c.prepareStatement(INSERT_ACTIVITYDEF_LISTENER);
			List list = activityDef.getListeners();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbListener listener = (DbListener) it.next();
				ps.setString(1, listener.getListenerId());
				ps.setString(2, activityDef.getActivityDefId());
				ps.setString(3, listener.getListenerName());
				ps.setString(4, listener.getListenerEvent());
				ps.setString(5, listener.getRealizeClass());
				ps.setString(6, listener.getExpressionStr());
				ps.setString(7, listener.getExpressionListenerType());
				ps.setString(8, listener.getExpressionEventType());
				ps.executeUpdate();
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	/**
	 * put the object to the cache, this method synchronize the uuid, so you
	 * can't push the Objects with same uuid into cache at sametime
	 * 
	 * @param uuid
	 * @param dbProcessDef
	 */
	private void putToCache(String uuid, DbActivityDef dbActivityDef) {
		synchronized (uuid.intern()) {
			if (!activityDefCache.containsKey(uuid)) {
				activityDefCache.put(uuid, dbActivityDef);
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
			if (activityDefCache.containsKey(uuid)) {
				activityDefCache.remove(uuid);
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
		StringBuffer sqlWhere = new StringBuffer(" WHERE PROCESSDEF_ID IN (");
		int andCount = 0;
		// check out all UUIDs not in the cache now
		for (int i = 0; i < ids.size(); i++) {
			String uuid = (String) ids.get(i);
			if (activityDefCache.containsKey(uuid) == false) {
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
			DbActivityDef[] activitys = null;
			try {
				activitys = loadByWhere(sqlWhere.toString(), null);
			} catch (SQLException e) {
				log.error("prefetch the Process Definition fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < activitys.length; i++) {
				DbActivityDef act = activitys[i];
				putToCache(act.getActivityDefId(), act);
			}
		}
	}

}


