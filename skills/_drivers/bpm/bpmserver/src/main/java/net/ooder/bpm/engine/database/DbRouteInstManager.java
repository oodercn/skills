/**
 * $RCSfile: DbRouteInstManager.java,v $
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIRouteInst;
import net.ooder.bpm.engine.inter.EIRouteInstManager;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

public class DbRouteInstManager extends EIRouteInstManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbRouteInstManager.class);

	public static final int ID_ROUTEINST_ID = 0;

	public static final int TYPE_ROUTEINST_ID = Types.VARCHAR;

	public static final String NAME_ROUTEINST_ID = "ROUTEINST_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ROUTENAME = 2;

	public static final int TYPE_ROUTENAME = Types.VARCHAR;

	public static final String NAME_ROUTENAME = "ROUTENAME";

	public static final int ID_DESCRIPTION = 3;

	public static final int TYPE_DESCRIPTION = Types.VARCHAR;

	public static final String NAME_DESCRIPTION = "DESCRIPTION";

	public static final int ID_FROMACTIVITY_ID = 4;

	public static final int TYPE_FROMACTIVITY_ID = Types.VARCHAR;

	public static final String NAME_FROMACTIVITY_ID = "FROMACTIVITY_ID";

	public static final int ID_TOACTIVITY_ID = 5;

	public static final int TYPE_TOACTIVITY_ID = Types.VARCHAR;

	public static final String NAME_TOACTIVITY_ID = "TOACTIVITY_ID";

	public static final int ID_ROUTEDIRECTION = 6;

	public static final int TYPE_ROUTEDIRECTION = Types.VARCHAR;

	public static final String NAME_ROUTEDIRECTION = "ROUTEDIRECTION";

	public static final int ID_ROUTETYPE = 7;

	public static final int TYPE_ROUTETYPE = Types.VARCHAR;

	public static final String NAME_ROUTETYPE = "ROUTETYPE";

	public static final int ID_ROUTETIME = 8;

	public static final int TYPE_ROUTETIME = Types.DECIMAL;

	public static final String NAME_ROUTETIME = "ROUTETIME";

	private static final String TABLE_NAME = "BPM_ROUTEINST";

	/**
	 * create an array of string containing all the fields of the BPM_ROUTEINST
	 * table.
	 */
	private static final String[] FIELD_NAMES = { "BPM_ROUTEINST.ROUTEINST_ID",
			"BPM_ROUTEINST.PROCESSINST_ID", "BPM_ROUTEINST.ROUTENAME",
			"BPM_ROUTEINST.DESCRIPTION", "BPM_ROUTEINST.FROMACTIVITY_ID",
			"BPM_ROUTEINST.TOACTIVITY_ID", "BPM_ROUTEINST.ROUTEDIRECTION",
			"BPM_ROUTEINST.ROUTETYPE", "BPM_ROUTEINST.ROUTETIME" };

	/**
	 * create an array of string containing all the fields of the BPM_ROUTEINST
	 * table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "ROUTEINST_ID",
			"PROCESSINST_ID", "ROUTENAME", "DESCRIPTION", "FROMACTIVITY_ID",
			"TOACTIVITY_ID", "ROUTEDIRECTION", "ROUTETYPE", "ROUTETIME" };

	/** Field that contains the comma separated fields of the BPM_ROUTEINST table */
	private static final String ALL_FIELDS = "BPM_ROUTEINST.ROUTEINST_ID"
			+ ",BPM_ROUTEINST.PROCESSINST_ID" + ",BPM_ROUTEINST.ROUTENAME"
			+ ",BPM_ROUTEINST.DESCRIPTION" + ",BPM_ROUTEINST.FROMACTIVITY_ID"
			+ ",BPM_ROUTEINST.TOACTIVITY_ID" + ",BPM_ROUTEINST.ROUTEDIRECTION"
			+ ",BPM_ROUTEINST.ROUTETYPE" + ",BPM_ROUTEINST.ROUTETIME";

	/** 娴佺▼瀹氫箟cache */
	Cache cache = null; // 娴佺▼瀹氫箟cache

	/** 娴佺▼瀹氫箟cache鏄惁鍙敤 */
	boolean cacheEnabled;

	public DbRouteInstManager() {
		cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ProcessInstCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new RouteInst instance.
	 * 
	 * @return the new RouteInst
	 */
	public EIRouteInst createRouteInst() {
		return new DbRouteInst();
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
	 * Load a RouteInst from the BPM_ROUTEINST using its key fields.
	 * 
	 * @return a unique RouteInst
	 */
	// 12
	public EIRouteInst loadByKey(String routeInstId) throws BPMException {
		DbRouteInst dbRouteInst = (DbRouteInst) cache.get(routeInstId);
		if (dbRouteInst != null) {
			return dbRouteInst;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_ROUTEINST WHERE BPM_ROUTEINST.ROUTEINST_ID=?");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ROUTEINST WHERE BPM_ROUTEINST.ROUTEINST_ID=?",
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, routeInstId);
			DbRouteInst pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				putToCache(routeInstId, (DbRouteInst) pReturn[0]);
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
	 * Load all the rows from BPM_ROUTEINST.
	 * 
	 * @return an array of DbRouteInstManager pObject
	 */
	// 38
	public List loadAll() throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager().log("SELECT " + ALL_FIELDS + " FROM BPM_ROUTEINST");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ROUTEINST", ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			List list = new DbRouteInstList(loadByPreparedStatement(ps,
					new int[] { ID_ROUTEINST_ID }));
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
	 * @return an array of RouteInst
	 */
	// 41
	public DbRouteInst[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of RouteInst
	 */
	public DbRouteInst[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbRouteInst[]) v.toArray(new DbRouteInst[0]);
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
	 * retreive an array of RouteInst given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RouteInst table
	 */
	// 49
	public List loadByWhere(String where) throws BPMException {
		List result;
		result = new DbRouteInstList(loadByWhere(where,
				new int[] { ID_ROUTEINST_ID }));
		return Collections.unmodifiableList(result);
	}

	/**
	 * retreive an array of RouteInst given a sql where clausis, and a list of
	 * fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting RouteInst table
	 */
	// 51
	public DbRouteInst[] loadByWhere(String where, int[] fieldList)
			throws BPMException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_ROUTEINST " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_ROUTEINST ");
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

			return (DbRouteInst[]) v.toArray(new DbRouteInst[0]);
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
	public int deleteByKey(String routeinstId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"DELETE from BPM_ROUTEINST WHERE BPM_ROUTEINST.ROUTEINST_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_ROUTEINST WHERE BPM_ROUTEINST.ROUTEINST_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, routeinstId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			removeFromCache(routeinstId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RouteInst object(s) to be deleted
	 * @return the number of deleted objects
	 */
	// 63
	public int delete(EIRouteInst object) throws BPMException {
		DbRouteInst pObject = (DbRouteInst) object;
		if (pObject.isRouteinstIdInitialized() == true)
			return deleteByKey(pObject.getRouteInstId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_ROUTEINST WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isRouteinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRoutenameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTENAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isDescriptionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DESCRIPTION").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isFromactivityIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("FROMACTIVITY_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isToactivityIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("TOACTIVITY_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRoutedirectionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEDIRECTION").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRoutetypeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTETYPE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRoutetimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTETIME").append("=?");
				_dirtyAnd++;
			}
			getManager().log(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isRouteinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getRouteInstId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isRoutenameInitialized())
				ps.setString(++_dirtyCount, pObject.getRouteName());
			if (pObject.isDescriptionInitialized())
				ps.setString(++_dirtyCount, pObject.getDescription());
			if (pObject.isFromactivityIdInitialized())
				ps.setString(++_dirtyCount, pObject.getFromActivityId());
			if (pObject.isToactivityIdInitialized())
				ps.setString(++_dirtyCount, pObject.getToActivityId());
			if (pObject.isRoutedirectionInitialized())
				ps.setString(++_dirtyCount, pObject.getRouteDirection().getType());
			if (pObject.isRoutetypeInitialized())
				ps.setString(++_dirtyCount, pObject.getRouteType().getType());
			if (pObject.isRoutetimeInitialized())
				ps.setLong(++_dirtyCount, pObject.getRouteTime().getTime());
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
	 * Delete rows from BPM_ROUTEINST table by where clausis
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
			DbRouteInst[] routeInsts = loadByWhere(where,
					new int[] { ID_ROUTEINST_ID });
			for (int i = 0; i < routeInsts.length; i++) {
				removeFromCache(routeInsts[i].getRouteInstId());
			}
			// delete it
			c = getConnection();
			String delByWhereSQL = "DELETE FROM BPM_ROUTEINST " + where;
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
	 * Save into the database an array of RouteInst pObjects
	 * 
	 * @param pObjects
	 *            the RouteInst pObject table to be saved
	 * @return the Objects to be saved
	 */
	// 65
	public EIRouteInst[] save(EIRouteInst[] pObjects) throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the RouteInst pObject.
	 * 
	 * @param pObject
	 *            the RouteInst pObject to be saved
	 */
	// 67
	void beforeInsert(DbRouteInst pObject) throws SQLException {
	}

	/**
	 * after the save of the RouteInst pObject
	 * 
	 * @param pObject
	 *            the RouteInst pObject to be saved
	 */
	// 68
	void afterInsert(DbRouteInst pObject) throws SQLException {
	}

	/**
	 * before the update of the RouteInst pObject.
	 * 
	 * @param pObject
	 *            the RouteInst pObject to be updated
	 */
	// 69
	void beforeUpdate(DbRouteInst pObject) throws SQLException {
	}

	/**
	 * after the update of the RouteInst pObject.
	 * 
	 * @param pObject
	 *            the RouteInst pObject to be updated
	 */
	// 70
	void afterUpdate(DbRouteInst pObject) throws SQLException {
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ROUTEINST on a RouteInst
	 * pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RouteInst pObject
	 */
	// 72
	public DbRouteInst decodeRow(ResultSet rs) throws SQLException {
		DbRouteInst pObject = (DbRouteInst) createRouteInst();
		if (rs.getObject(1) != null)
			pObject.setRouteInstId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setRouteName(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setDescription(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setFromActivityId(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setToActivityId(rs.getString(6));
		if (rs.getObject(7) != null)
			pObject.setRouteDirection(RouteDirction.fromType(rs.getString(7)));
		if (rs.getObject(8) != null)
			pObject.setRouteType(RouteInstType.fromType(rs.getString(8)));
		if (rs.getObject(9) != null)
			pObject.setRouteTime(new Date(rs.getLong(9)));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ROUTEINST on a RouteInst
	 * pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RouteInst pObject
	 */
	// 73
	public DbRouteInst decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbRouteInst pObject = (DbRouteInst) createRouteInst();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ROUTEINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteInstId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_ROUTENAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteName(rs.getString(pos));
				}
				break;
			case ID_DESCRIPTION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDescription(rs.getString(pos));
				}
				break;
			case ID_FROMACTIVITY_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setFromActivityId(rs.getString(pos));
				}
				break;
			case ID_TOACTIVITY_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setToActivityId(rs.getString(pos));
				}
				break;
			case ID_ROUTEDIRECTION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteDirection(RouteDirction.fromType(rs.getString(pos)));
				}
				break;
			case ID_ROUTETYPE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteType(RouteInstType.fromType(rs.getString(pos)));
				}
				break;
			case ID_ROUTETIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteTime(new Date(rs.getLong(pos)));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique RouteInst pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the RouteInst pObject to look for
	 * @return the pObject matching the template
	 */
	// 85
	public EIRouteInst loadObject(EIRouteInst pObject) throws SQLException {
		EIRouteInst[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new SQLException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of RouteInst from a template one
	 * 
	 * @param pObject
	 *            the RouteInst template to look for
	 * @return all the RouteInst matching the template
	 */
	// 88
	public EIRouteInst[] loadObjects(EIRouteInst object) throws SQLException {
		DbRouteInst pObject = (DbRouteInst) object;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from BPM_ROUTEINST WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isRouteinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ROUTEINST_ID= ?");
			}
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isRoutenameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ROUTENAME= ?");
			}
			if (pObject.isDescriptionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DESCRIPTION= ?");
			}
			if (pObject.isFromactivityIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("FROMACTIVITY_ID= ?");
			}
			if (pObject.isToactivityIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("TOACTIVITY_ID= ?");
			}
			if (pObject.isRoutedirectionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ROUTEDIRECTION= ?");
			}
			if (pObject.isRoutetypeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ROUTETYPE= ?");
			}
			if (pObject.isRoutetimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ROUTETIME= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			getManager().log(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (pObject.isRouteinstIdModified())
				ps.setString(++_dirtyCount, pObject.getRouteInstId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isRoutenameModified())
				ps.setString(++_dirtyCount, pObject.getRouteName());
			if (pObject.isDescriptionModified())
				ps.setString(++_dirtyCount, pObject.getDescription());
			if (pObject.isFromactivityIdModified())
				ps.setString(++_dirtyCount, pObject.getFromActivityId());
			if (pObject.isToactivityIdModified())
				ps.setString(++_dirtyCount, pObject.getToActivityId());
			if (pObject.isRoutedirectionModified())
				ps.setString(++_dirtyCount, pObject.getRouteDirection().getType());
			if (pObject.isRoutetypeModified())
				ps.setString(++_dirtyCount, pObject.getRouteType().getType());
			if (pObject.isRoutetimeModified())
				ps.setLong(++_dirtyCount, pObject.getRouteTime().getTime());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	// class+
	/**
	 * Save into the database the RouteInst pObject
	 * 
	 * @param pObject
	 *            the RouteInst pObject to be saved
	 */
	// 100
	public EIRouteInst save(EIRouteInst object) throws BPMException {
		DbRouteInst pObject = (DbRouteInst) object;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_ROUTEINST (");
				if (pObject.isRouteinstIdModified()) {
					_sql.append("ROUTEINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstIdModified()) {
					_sql.append("PROCESSINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isRoutenameModified()) {
					_sql.append("ROUTENAME").append(",");
					_dirtyCount++;
				}
				if (pObject.isDescriptionModified()) {
					_sql.append("DESCRIPTION").append(",");
					_dirtyCount++;
				}
				if (pObject.isFromactivityIdModified()) {
					_sql.append("FROMACTIVITY_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isToactivityIdModified()) {
					_sql.append("TOACTIVITY_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isRoutedirectionModified()) {
					_sql.append("ROUTEDIRECTION").append(",");
					_dirtyCount++;
				}
				if (pObject.isRoutetypeModified()) {
					_sql.append("ROUTETYPE").append(",");
					_dirtyCount++;
				}
				if (pObject.isRoutetimeModified()) {
					_sql.append("ROUTETIME").append(",");
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
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (pObject.isRouteinstIdModified())
					ps.setString(++_dirtyCount, pObject.getRouteInstId());
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isRoutenameModified())
					ps.setString(++_dirtyCount, pObject.getRouteName());
				if (pObject.isDescriptionModified())
					ps.setString(++_dirtyCount, pObject.getDescription());
				if (pObject.isFromactivityIdModified())
					ps.setString(++_dirtyCount, pObject.getFromActivityId());
				if (pObject.isToactivityIdModified())
					ps.setString(++_dirtyCount, pObject.getToActivityId());
				if (pObject.isRoutedirectionModified())
					ps.setString(++_dirtyCount, pObject.getRouteDirection().getType());
				if (pObject.isRoutetypeModified())
					ps.setString(++_dirtyCount, pObject.getRouteType().getType());
				if (pObject.isRoutetimeModified())
					ps.setLong(++_dirtyCount, pObject.getRouteTime().getTime());
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
				_sql = new StringBuffer("UPDATE BPM_ROUTEINST SET ");
				if (pObject.isRouteinstIdModified())
					_sql.append("ROUTEINST_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isRoutenameModified())
					_sql.append("ROUTENAME").append("=?,");
				if (pObject.isDescriptionModified())
					_sql.append("DESCRIPTION").append("=?,");
				if (pObject.isFromactivityIdModified())
					_sql.append("FROMACTIVITY_ID").append("=?,");
				if (pObject.isToactivityIdModified())
					_sql.append("TOACTIVITY_ID").append("=?,");
				if (pObject.isRoutedirectionModified())
					_sql.append("ROUTEDIRECTION").append("=?,");
				if (pObject.isRoutetypeModified())
					_sql.append("ROUTETYPE").append("=?,");
				if (pObject.isRoutetimeModified())
					_sql.append("ROUTETIME").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_ROUTEINST.ROUTEINST_ID=?");
				getManager().log(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isRouteinstIdModified()) {
					if (pObject.getRouteInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRouteInstId());
				}
				if (pObject.isProcessinstIdModified()) {
					if (pObject.getProcessInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
				}
				if (pObject.isRoutenameModified()) {
					if (pObject.getRouteName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRouteName());
				}
				if (pObject.isDescriptionModified()) {
					if (pObject.getDescription() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getDescription());
				}
				if (pObject.isFromactivityIdModified()) {
					if (pObject.getFromActivityId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, pObject
										.getFromActivityId());
				}
				if (pObject.isToactivityIdModified()) {
					if (pObject.getToActivityId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getToActivityId());
				}
				if (pObject.isRoutedirectionModified()) {
					if (pObject.getRouteDirection() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, pObject
										.getRouteDirection().getType());
				}
				if (pObject.isRoutetypeModified()) {
					if (pObject.getRouteType() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRouteType().getType());
				}
				if (pObject.isRoutetimeModified()) {
					if (pObject.getRouteTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, pObject.getRouteTime()
								.getTime());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getRouteInstId());
				ps.executeUpdate();
				pObject.resetIsModified();

				afterUpdate(pObject);
			}
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * put the object to the cache, this method synchronize the uuid, so you
	 * can't push the Objects with same uuid into cache at sametime
	 * 
	 * @param uuid
	 * @param dbProcessInst
	 */
	private void putToCache(String uuid, DbRouteInst routeInst) {
		synchronized (uuid.intern()) {
			if (!cache.containsKey(uuid)) {

				cache.put(uuid, routeInst);
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
	 * 鎵归噺灏嗗彲鑳借鍙栫殑瀵硅薄鍏堥璇诲埌Cache涓紝鎻愰珮鏁堢巼
	 * 
	 * @param ids
	 *            闇€瑕侀璇荤殑UUID鍒楄〃
	 */
	void prepareCache(List ids) {
		StringBuffer sqlWhere = new StringBuffer(" WHERE ROUTEINST_ID IN (");
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
			EIRouteInst[] list = null;
			try {
				list = loadByWhere(sqlWhere.toString(), null);
			} catch (BPMException e) {
				log.error("prefetch the route instance fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < list.length; i++) {
				DbRouteInst pd = (DbRouteInst) list[i];
				putToCache(pd.getRouteInstId(), pd);
			}
		}
	}

	/**
	 * 鍙栧緱鎸囧悜璇ユ椿鍔ㄥ疄渚嬬殑鎵€鏈夎矾鐢卞疄渚?
	 */
	public List getRouteInsts(EIActivityInst activityInst) throws BPMException {
		String where = " where TOACTIVITY_ID='"
				+ activityInst.getActivityInstId() + "'";
		List result = loadByWhere(where);
		return result;
	}
}

