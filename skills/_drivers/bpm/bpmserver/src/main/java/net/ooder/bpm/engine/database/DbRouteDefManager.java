/**
 * $RCSfile: DbRouteDefManager.java,v $
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
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.inter.EIRouteDefManager;
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
 * Description: 路由定义管理器接口数据库实现
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
public class DbRouteDefManager extends EIRouteDefManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbRouteDefManager.class);

	public static final int ID_ROUTEDEF_ID = 0;

	public static final int TYPE_ROUTEDEF_ID = Types.VARCHAR;

	public static final String NAME_ROUTEDEF_ID = "ROUTEDEF_ID";

	public static final int ID_PROCESSDEF_ID = 1;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_PROCESSDEF_VERSION_ID = 2;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_ROUTENAME = 3;

	public static final int TYPE_ROUTENAME = Types.VARCHAR;

	public static final String NAME_ROUTENAME = "ROUTENAME";

	public static final int ID_DESCRIPTION = 4;

	public static final int TYPE_DESCRIPTION = Types.VARCHAR;

	public static final String NAME_DESCRIPTION = "DESCRIPTION";

	public static final int ID_FROMACTIVITYDEF_ID = 5;

	public static final int TYPE_FROMACTIVITYDEF_ID = Types.VARCHAR;

	public static final String NAME_FROMACTIVITYDEF_ID = "FROMACTIVITYDEF_ID";

	public static final int ID_TOACTIVITYDEF_ID = 6;

	public static final int TYPE_TOACTIVITYDEF_ID = Types.VARCHAR;

	public static final String NAME_TOACTIVITYDEF_ID = "TOACTIVITYDEF_ID";

	public static final int ID_ROUTEORDER = 7;

	public static final int TYPE_ROUTEORDER = Types.DECIMAL;

	public static final String NAME_ROUTEORDER = "ROUTEORDER";

	public static final int ID_ROUTEDIRECTION = 8;

	public static final int TYPE_ROUTEDIRECTION = Types.VARCHAR;

	public static final String NAME_ROUTEDIRECTION = "ROUTEDIRECTION";

	public static final int ID_ROUTECONDITION = 9;

	public static final int TYPE_ROUTECONDITION = Types.VARCHAR;

	public static final String NAME_ROUTECONDITION = "ROUTECONDITION";
	
	public static final int ID_ROUTECONDITIONTYPE = 10;

	public static final int TYPE_ROUTECONDITIONTYPE = Types.VARCHAR;

	public static final String NAME_ROUTECONDITIONTYPE = "ROUTECONDITIONTYPE";

	private static final String TABLE_NAME = "BPM_ROUTEDEF";

	/**
	 * create an array of string containing all the fields of the BPM_ROUTEDEF
	 * table.
	 */
	private static final String[] FIELD_NAMES = { "BPM_ROUTEDEF.ROUTEDEF_ID",
			"BPM_ROUTEDEF.PROCESSDEF_ID", "BPM_ROUTEDEF.PROCESSDEF_VERSION_ID",
			"BPM_ROUTEDEF.ROUTENAME", "BPM_ROUTEDEF.DESCRIPTION",
			"BPM_ROUTEDEF.FROMACTIVITYDEF_ID", "BPM_ROUTEDEF.TOACTIVITYDEF_ID",
			"BPM_ROUTEDEF.ROUTEORDER", "BPM_ROUTEDEF.ROUTEDIRECTION",
			"BPM_ROUTEDEF.ROUTECONDITION" ,"BPM_ROUTEDEF.ROUTECONDITIONTYPE"};

	/**
	 * create an array of string containing all the fields of the BPM_ROUTEDEF
	 * table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "ROUTEDEF_ID",
			"PROCESSDEF_ID", "PROCESSDEF_VERSION_ID", "ROUTENAME",
			"DESCRIPTION", "FROMACTIVITYDEF_ID", "TOACTIVITYDEF_ID",
			"ROUTEORDER", "ROUTEDIRECTION", "ROUTECONDITION","ROUTECONDITIONTYPE" };

	/** Field that contains the comma separated fields of the BPM_ROUTEDEF table */
	private static final String ALL_FIELDS = "BPM_ROUTEDEF.ROUTEDEF_ID"
			+ ",BPM_ROUTEDEF.PROCESSDEF_ID"
			+ ",BPM_ROUTEDEF.PROCESSDEF_VERSION_ID" + ",BPM_ROUTEDEF.ROUTENAME"
			+ ",BPM_ROUTEDEF.DESCRIPTION" + ",BPM_ROUTEDEF.FROMACTIVITYDEF_ID"
			+ ",BPM_ROUTEDEF.TOACTIVITYDEF_ID" + ",BPM_ROUTEDEF.ROUTEORDER"
			+ ",BPM_ROUTEDEF.ROUTEDIRECTION" + ",BPM_ROUTEDEF.ROUTECONDITION"+ ",BPM_ROUTEDEF.ROUTECONDITIONTYPE";

	/** 流程定义cache */
	Cache routeDefCache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbRouteDefManager() {
		routeDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"RouteDefCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new BPMRoutedefBean instance.
	 * 
	 * @return the new BPMRoutedefBean
	 */
	public EIRouteDef createRouteDef() {
		return new DbRouteDef();
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
	 * Load a BPMRoutedefBean from the BPM_ROUTEDEF using its key fields.
	 * 
	 * @return a unique BPMRoutedefBean
	 */
	public EIRouteDef loadByKey(String routeDefId) throws BPMException {
		DbRouteDef route = (DbRouteDef) routeDefCache.get(routeDefId);
		if (route != null) {
			return route;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM BPM_ROUTEDEF WHERE BPM_ROUTEDEF.ROUTEDEF_ID=?");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ROUTEDEF WHERE BPM_ROUTEDEF.ROUTEDEF_ID=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, routeDefId);
			DbRouteDef pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				route = pReturn[0];
				putToCache(routeDefId, route);
				return pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load a BPMRoutedefBean table from the BPM_ROUTEDEF table using its
	 * PROCESSDEF_VERSION_ID field.
	 * 
	 * @return an array of BPMRoutedefBean
	 */
	public List loadByProcessDefVersionId(String value) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug("SELECT " + ALL_FIELDS
						+ " FROM BPM_ROUTEDEF WHERE PROCESSDEF_VERSION_ID=?");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ROUTEDEF WHERE PROCESSDEF_VERSION_ID=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			DbRouteDef[] routes = loadByPreparedStatement(ps);
			List list = new DbRouteDefList(routes);
			return Collections.unmodifiableList(list);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete from the BPM_ROUTEDEF table by PROCESSDEF_VERSION_ID field.
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
	 * get the EIProcessdefVersion object from the
	 * BPM_ROUTEDEF.PROCESSDEF_VERSION_ID field.
	 * 
	 * @param pObject
	 *            the EIProcessdefVersion
	 * @return the associated EIProcessdefVersion pObject
	 */
	public EIProcessDefVersion getParentProcessDefVersion(EIRouteDef pObject)
			throws BPMException {
		return EIProcessDefVersionManager.getInstance().loadByKey(
				pObject.getProcessDefVersionId());
	}

	/**
	 * associates the BPMRoutedefBean object to the BPMProcessdefVersionBean
	 * object.
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessdefVersionBean object to associate to the
	 *            BPMRoutedefBean
	 * @return the associated BPMProcessdefVersionBean pObject
	 */
	public EIRouteDef setParentProcessDefVersion(EIRouteDef pObject,
			EIProcessDefVersion pObjectToBeSet) {
		pObject.setProcessDefVersionId(pObjectToBeSet.getProcessDefVersionId());
		return pObject;
	}

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of BPMRoutedefBean
	 */
	DbRouteDef[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of BPMRoutedefBean
	 */
	DbRouteDef[] loadByPreparedStatement(PreparedStatement ps, int[] fieldList)
			throws SQLException {
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
			return (DbRouteDef[]) v.toArray(new DbRouteDef[0]);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	/**
	 * retreive an array of BPMRoutedefBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMRoutedefBean table
	 */
	public List loadByWhere(String where) throws BPMException {
		DbRouteDef[] routes = null;
		try {
			routes = loadByWhere(where, null);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		}
		List list = new DbRouteDefList(routes);
		return Collections.unmodifiableList(list);
	}

	/**
	 * retreive an array of BPMRoutedefBean given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting BPMRoutedefBean table
	 */
	DbRouteDef[] loadByWhere(String where, int[] fieldList) throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_ROUTEDEF " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_ROUTEDEF ");
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

			return (DbRouteDef[]) v.toArray(new DbRouteDef[0]);
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
	public int deleteByKey(String routedefId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			deleteAttributeByKey(routedefId);
			if (log.isDebugEnabled())
				log
						.debug("DELETE from BPM_ROUTEDEF WHERE BPM_ROUTEDEF.ROUTEDEF_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_ROUTEDEF WHERE BPM_ROUTEDEF.ROUTEDEF_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, routedefId);
			return ps.executeUpdate();
		} catch (SQLException e) {

			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		} finally {
			removeFromCache(routedefId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(EIRouteDef pObject) throws BPMException {
		DbRouteDef routeDef = (DbRouteDef) pObject;

		if (routeDef.isRoutedefIdInitialized() == true)
			return deleteByKey(routeDef.getRouteDefId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_ROUTEDEF WHERE ");
			int _dirtyAnd = 0;
			if (routeDef.isRoutedefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isRoutenameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTENAME").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isDescriptionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DESCRIPTION").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isFromactivitydefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("FROMACTIVITYDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isToactivitydefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("TOACTIVITYDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isRouteorderInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEORDER").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isRoutedirectionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTEDIRECTION").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isRouteconditionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTECONDITION").append("=?");
				_dirtyAnd++;
			}
			if (routeDef.isRouteconditionTypeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ROUTECONDITIONTYPE").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (routeDef.isRoutedefIdInitialized())
				ps.setString(++_dirtyCount, routeDef.getRouteDefId());
			if (routeDef.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, routeDef.getProcessDefId());
			if (routeDef.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, routeDef.getProcessDefVersionId());
			if (routeDef.isRoutenameInitialized())
				ps.setString(++_dirtyCount, routeDef.getName());
			if (routeDef.isDescriptionInitialized())
				ps.setString(++_dirtyCount, routeDef.getDescription());
			if (routeDef.isFromactivitydefIdInitialized())
				ps.setString(++_dirtyCount, routeDef.getFromActivityDefId());
			if (routeDef.isToactivitydefIdInitialized())
				ps.setString(++_dirtyCount, routeDef.getToActivityDefId());
			if (routeDef.isRouteorderInitialized())
				ps.setInt(++_dirtyCount, routeDef.getRouteOrder());
			if (routeDef.isRoutedirectionInitialized())
				ps.setString(++_dirtyCount, routeDef.getRouteDirection());
			if (routeDef.isRouteconditionInitialized())
				ps.setString(++_dirtyCount, routeDef.getRouteCondition());
			if (routeDef.isRouteconditionTypeInitialized())
				ps.setString(++_dirtyCount, routeDef.getRouteConditionType());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from BPM_ROUTEDEF table by where clausis
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
			DbRouteDef[] acts;
			try {
				acts = loadByWhere(where, new int[] { ID_ROUTEDEF_ID });
				for (int i = 0; i < acts.length; i++) {
					removeFromCache(acts[i].getRouteDefId());
				}
			} catch (SQLException e) {
				throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);
			}
			// delete it!
			c = getConnection();
			deleteAttributeByWhere(where);
			String delByWhereSQL = "DELETE FROM BPM_ROUTEDEF " + where;
			if (log.isDebugEnabled())
				log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of BPMRoutedefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMRoutedefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIRouteDef[] save(EIRouteDef[] pObjects) throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the BPMRoutedefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be saved
	 */
	void beforeInsert(DbRouteDef pObject) throws SQLException {
	}

	/**
	 * after the save of the BPMRoutedefBean pObject
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be saved
	 */
	void afterInsert(DbRouteDef pObject) throws SQLException {
	}

	/**
	 * before the update of the BPMRoutedefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be updated
	 */
	void beforeUpdate(DbRouteDef pObject) throws SQLException {
	}

	/**
	 * after the update of the BPMRoutedefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be updated
	 */
	void afterUpdate(DbRouteDef pObject) throws SQLException {
	}

	/**
	 * Save into the database the BPMRoutedefBean pObject
	 * 
	 * @param pObject
	 *            the BPMRoutedefBean pObject to be saved
	 */
	public EIRouteDef save(EIRouteDef pObject) throws BPMException {
		DbRouteDef routeDef = (DbRouteDef) pObject;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (routeDef.isNew()) { // SAVE
				beforeInsert(routeDef);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_ROUTEDEF (");
				if (routeDef.isRoutedefIdModified()) {
					_sql.append("ROUTEDEF_ID").append(",");
					_dirtyCount++;
				}
				if (routeDef.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (routeDef.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (routeDef.isRoutenameModified()) {
					_sql.append("ROUTENAME").append(",");
					_dirtyCount++;
				}
				if (routeDef.isDescriptionModified()) {
					_sql.append("DESCRIPTION").append(",");
					_dirtyCount++;
				}
				if (routeDef.isFromactivitydefIdModified()) {
					_sql.append("FROMACTIVITYDEF_ID").append(",");
					_dirtyCount++;
				}
				if (routeDef.isToactivitydefIdModified()) {
					_sql.append("TOACTIVITYDEF_ID").append(",");
					_dirtyCount++;
				}
				if (routeDef.isRouteorderModified()) {
					_sql.append("ROUTEORDER").append(",");
					_dirtyCount++;
				}
				if (routeDef.isRoutedirectionModified()) {
					_sql.append("ROUTEDIRECTION").append(",");
					_dirtyCount++;
				}
				if (routeDef.isRouteconditionModified()) {
					_sql.append("ROUTECONDITION").append(",");
					_dirtyCount++;
				}
				if (routeDef.isRouteconditionTypeModified()) {
					_sql.append("ROUTECONDITIONTYPE").append(",");
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
				if (routeDef.isRoutedefIdModified())
					ps.setString(++_dirtyCount, routeDef.getRouteDefId());
				if (routeDef.isProcessdefIdModified())
					ps.setString(++_dirtyCount, routeDef.getProcessDefId());
				if (routeDef.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, routeDef
							.getProcessDefVersionId());
				if (routeDef.isRoutenameModified())
					ps.setString(++_dirtyCount, routeDef.getName());
				if (routeDef.isDescriptionModified())
					ps.setString(++_dirtyCount, routeDef.getDescription());
				if (routeDef.isFromactivitydefIdModified())
					ps
							.setString(++_dirtyCount, routeDef
									.getFromActivityDefId());
				if (routeDef.isToactivitydefIdModified())
					ps.setString(++_dirtyCount, routeDef.getToActivityDefId());
				if (routeDef.isRouteorderModified())
					ps.setInt(++_dirtyCount, routeDef.getRouteOrder());
				if (routeDef.isRoutedirectionModified())
					ps.setString(++_dirtyCount, routeDef.getRouteDirection());
				if (routeDef.isRouteconditionModified())
					ps.setString(++_dirtyCount, routeDef.getRouteCondition());
				if (routeDef.isRouteconditionTypeModified())
					ps.setString(++_dirtyCount, routeDef.getRouteConditionType());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				routeDef.setIsNew(false);
				afterInsert(routeDef);
			} else { // UPDATE
			// preupdate+

				// preupdate-
				beforeUpdate(routeDef);
				_sql = new StringBuffer("UPDATE BPM_ROUTEDEF SET ");
				if (routeDef.isRoutedefIdModified())
					_sql.append("ROUTEDEF_ID").append("=?,");
				if (routeDef.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (routeDef.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (routeDef.isRoutenameModified())
					_sql.append("ROUTENAME").append("=?,");
				if (routeDef.isDescriptionModified())
					_sql.append("DESCRIPTION").append("=?,");
				if (routeDef.isFromactivitydefIdModified())
					_sql.append("FROMACTIVITYDEF_ID").append("=?,");
				if (routeDef.isToactivitydefIdModified())
					_sql.append("TOACTIVITYDEF_ID").append("=?,");
				if (routeDef.isRouteorderModified())
					_sql.append("ROUTEORDER").append("=?,");
				if (routeDef.isRoutedirectionModified())
					_sql.append("ROUTEDIRECTION").append("=?,");
				if (routeDef.isRouteconditionModified())
					_sql.append("ROUTECONDITION").append("=?,");
				if (routeDef.isRouteconditionTypeModified())
					_sql.append("ROUTECONDITIONTYPE").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_ROUTEDEF.ROUTEDEF_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (routeDef.isRoutedefIdModified()) {
					if (routeDef.getRouteDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef.getRouteDefId());
				}
				if (routeDef.isProcessdefIdModified()) {
					if (routeDef.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef.getProcessDefId());
				}
				if (routeDef.isProcessdefVersionIdModified()) {
					if (routeDef.getProcessDefVersionId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getProcessDefVersionId());
				}
				if (routeDef.isRoutenameModified()) {
					if (routeDef.getName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef.getName());
				}
				if (routeDef.isDescriptionModified()) {
					if (routeDef.getDescription() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef.getDescription());
				}
				if (routeDef.isFromactivitydefIdModified()) {
					if (routeDef.getFromActivityDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getFromActivityDefId());
				}
				if (routeDef.isToactivitydefIdModified()) {
					if (routeDef.getToActivityDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getToActivityDefId());
				}
				if (routeDef.isRouteorderModified()) {
					ps.setInt(++_dirtyCount, routeDef.getRouteOrder());
				}
				if (routeDef.isRoutedirectionModified()) {
					if (routeDef.getRouteDirection() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getRouteDirection());
				}
				if (routeDef.isRouteconditionModified()) {
					if (routeDef.getRouteCondition() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getRouteCondition());
				}
				if (routeDef.isRouteconditionTypeModified()) {
					if (routeDef.getRouteConditionType() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, routeDef
								.getRouteConditionType());
				}
				if (_dirtyCount != 0) {
					ps.setString(++_dirtyCount, routeDef.getRouteDefId());
					ps.executeUpdate();
					afterUpdate(routeDef);
				}
			}
			saveAttribute(routeDef);
			saveListeners(routeDef);
			routeDef.resetIsModified();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return routeDef;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ROUTEDEF on a BPMRoutedefBean
	 * pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting BPMRoutedefBean pObject
	 */
	DbRouteDef decodeRow(ResultSet rs) throws SQLException {
		DbRouteDef pObject = (DbRouteDef) createRouteDef();
		if (rs.getObject(1) != null)
			pObject.setRouteDefId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setProcessDefVersionId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setName(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setDescription(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setFromActivityDefId(rs.getString(6));
		if (rs.getObject(7) != null)
			pObject.setToActivityDefId(rs.getString(7));
		if (rs.getObject(8) != null)
			pObject.setRouteOrder(rs.getInt(8));
		if (rs.getObject(9) != null)
			pObject.setRouteDirection(rs.getString(9));
		if (rs.getObject(10) != null)
			pObject.setRouteCondition(rs.getString(10));
		if (rs.getObject(11) != null)
			pObject.setRouteConditionType(rs.getString(11));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ROUTEDEF on a BPMRoutedefBean
	 * pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting BPMRoutedefBean pObject
	 */
	DbRouteDef decodeRow(ResultSet rs, int[] fieldList) throws SQLException {
		DbRouteDef pObject = (DbRouteDef) createRouteDef();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ROUTEDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteDefId(rs.getString(pos));
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
			case ID_ROUTENAME:
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
			case ID_FROMACTIVITYDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setFromActivityDefId(rs.getString(pos));
				}
				break;
			case ID_TOACTIVITYDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setToActivityDefId(rs.getString(pos));
				}
				break;
			case ID_ROUTEORDER:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteOrder(rs.getInt(pos));
				}
				break;
			case ID_ROUTEDIRECTION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteDirection(rs.getString(pos));
				}
				break;
			case ID_ROUTECONDITION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRouteCondition(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " ROUTEDEF_ID, " + " PROPNAME, "
			+ " PROPVALUE, " + " PROPCLASS, " + " PROPTYPE, "
			+ " PARENTPROP_ID, " + " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_ROUTEDEF_PROPERTY " + "WHERE ROUTEDEF_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_EXTATTRIBUTRE = "DELETE FROM BPM_ROUTEDEF_PROPERTY "
			+ "WHERE ROUTEDEF_ID = ? ";

	private static final String DELETE_EXTATTRIBUTRE_BYWHERE = "DELETE FROM BPM_ROUTEDEF_PROPERTY "
			+ "WHERE ROUTEDEF_ID IN (SELECT ROUTEDEF_ID FROM BPM_ROUTEDEF ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_ROUTEDEF_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " ROUTEDEF_ID, "
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

	void loadAttribute(DbRouteDef route) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(LOAD_EXTATTRIBUTRE);
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, route.getRouteDefId());
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
						addAttribute(route, extAtt);
					}
					extAtt = (DbAttributeDef) route.attributeIdMap
							.get(currUUID);
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
				addAttribute(route, extAtt);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void addAttribute(DbRouteDef route, DbAttributeDef extAtt) {
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeDef parent = (DbAttributeDef) route.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeDef();
				route.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeDef att = (DbAttributeDef) route.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeDef();
				att.setName(extAtt.getType());
				route.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);

			// route.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		route.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	/**
	 * @param activityDef
	 */
	void saveAttribute(DbRouteDef route) throws BPMException {
		if (route.isAttributeModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, route.getRouteDefId());
			ps.executeUpdate();

			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			List list = route.getAllAttribute();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbAttributeDef att = (DbAttributeDef) it.next();
				saveAttribute(ps, route, att);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void saveAttribute(PreparedStatement ps, DbRouteDef route,
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
			ps.setString(2, route.getRouteDefId());
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

	private void deleteAttributeByKey(String routeDefId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, routeDefId);
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
			c = getConnection();
			String sql = DELETE_EXTATTRIBUTRE_BYWHERE + where + " ) ";
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


		private static final String LOAD_ROUTEDEF_LISTENER = "SELECT LISTENER_ID, "
				+ " ROUTEDEF_ID, "
				+ " LISTENERNAME, "
				+ " LISTENERREGISTEVENT, "
				+ " REALIZECLASS, "
				+ " EXPRESSIONSTR, "
				+ " EXPRESSIONLISENTERTYPE, "
				+ " EXPRESSIONEVENTTYPE "
				+ " FROM BPM_ROUTELISTENER " + "WHERE ROUTEDEF_ID = ? ";

		private static final String DELETE_ROUTEDEF_LISTENER = "DELETE FROM BPM_ROUTELISTENER "
				+ " WHERE ROUTEDEF_ID = ? ";

		private static final String DELETE_ROUTEDEF_LISTENER_BYWHERE = "DELETE FROM BPM_ROUTELISTENER "
				+ " WHERE ROUTEDEF_ID IN (SELECT ROUTEDEF_ID FROM BPM_ROUTEDEF ";

		private static final String INSERT_ROUTEDEF_LISTENER = "INSERT INTO BPM_ROUTELISTENER "
				+ " ( "
				+ " LISTENER_ID, "
				+ " ROUTEDEF_ID, "
				+ " LISTENERNAME, "
				+ " LISTENERREGISTEVENT, "
				+ " REALIZECLASS, "
				+ " EXPRESSIONSTR,"
				+ " EXPRESSIONLISENTERTYPE, "
				+ " EXPRESSIONEVENTTYPE "
				+ " ) VALUES ( " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, "+ " ?, "+ " ?, "+ " ? ) ";

		void loadListener(DbRouteDef act) throws BPMException {
			Connection c = null;
			PreparedStatement ps = null;
			try {
				c = getConnection();
				if (log.isDebugEnabled())
					log.debug(LOAD_ROUTEDEF_LISTENER);
				ps = c.prepareStatement(LOAD_ROUTEDEF_LISTENER);
				ps.setString(1, act.getRouteDefId());
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
					log.debug(DELETE_ROUTEDEF_LISTENER);
				ps = c.prepareStatement(DELETE_ROUTEDEF_LISTENER);
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
				String sql = DELETE_ROUTEDEF_LISTENER_BYWHERE + where + " ) ";
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

		private void saveListeners(DbRouteDef routeDef) throws BPMException {
			if (routeDef.isListenersModified() == false) {
				return;
			}
			Connection c = null;
			PreparedStatement ps = null;
			try {
				c = getConnection();
				if (log.isDebugEnabled())
					log.debug(DELETE_ROUTEDEF_LISTENER);
				ps = c.prepareStatement(DELETE_ROUTEDEF_LISTENER);
				ps.setString(1, routeDef.getRouteDefId());
				ps.executeUpdate();

				getManager().close(ps);
				if (log.isDebugEnabled())
					log.debug(INSERT_ROUTEDEF_LISTENER);
				ps = c.prepareStatement(INSERT_ROUTEDEF_LISTENER);
				List list = routeDef.getListeners();
				for (Iterator it = list.iterator(); it.hasNext();) {
					DbListener listener = (DbListener) it.next();
					ps.setString(1, listener.getListenerId());
					ps.setString(2, routeDef.getRouteDefId());
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

	
	
	

	public List getOutRoutesFromActivity(String activityDefId)
			throws BPMException {
		List list = null;
		String sqlWhere = " WHERE FROMACTIVITYDEF_ID = '" + activityDefId + "'";
		return loadByWhere(sqlWhere);
	}

	public List getInRoutesToActivity(String activityDefId) throws BPMException {
		List list = null;
		String sqlWhere = " WHERE TOACTIVITYDEF_ID = '" + activityDefId + "'";
		return loadByWhere(sqlWhere);
	}

	/**
	 * put the object to the cache, this method synchronize the uuid, so you
	 * can't push the Objects with same uuid into cache at sametime
	 * 
	 * @param uuid
	 * @param dbProcessDef
	 */
	private void putToCache(String uuid, DbRouteDef dbRouteDef) {
		synchronized (uuid.intern()) {
			if (!routeDefCache.containsKey(uuid)) {
				routeDefCache.put(uuid, dbRouteDef);
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
			if (routeDefCache.containsKey(uuid)) {
				routeDefCache.remove(uuid);
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
		StringBuffer sqlWhere = new StringBuffer(" WHERE ROUTEDEF_ID IN (");
		int andCount = 0;
		// check out all UUIDs not in the cache now
		for (int i = 0; i < ids.size(); i++) {
			String uuid = (String) ids.get(i);
			if (routeDefCache.containsKey(uuid) == false) {
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
			DbRouteDef[] routes = null;
			try {
				routes = loadByWhere(sqlWhere.toString(), null);
			} catch (SQLException e) {
				log.error("prefetch the Route Definition fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < routes.length; i++) {
				DbRouteDef r = (DbRouteDef) routes[i];
				putToCache(r.getRouteDefId(), r);
			}
		}
	}
}


