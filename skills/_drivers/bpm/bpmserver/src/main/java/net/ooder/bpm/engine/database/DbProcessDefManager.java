/**
 * $RCSfile: DbProcessDefManager.java,v $
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
import java.util.Collections;
import java.util.List;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
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
 * Description: 流程定义管理器接口数据库实现
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
public class DbProcessDefManager extends EIProcessDefManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefManager.class);

	public static final int ID_PROCESSDEF_ID = 0;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_DEFNAME = 1;

	public static final int TYPE_DEFNAME = Types.VARCHAR;

	public static final String NAME_DEFNAME = "DEFNAME";

	public static final int ID_DESCRIPTION = 2;

	public static final int TYPE_DESCRIPTION = Types.VARCHAR;

	public static final String NAME_DESCRIPTION = "DESCRIPTION";

	public static final int ID_CLASSIFICATION = 3;

	public static final int TYPE_CLASSIFICATION = Types.VARCHAR;

	public static final String NAME_CLASSIFICATION = "CLASSIFICATION";

	public static final int ID_SYSTEMCODE = 4;

	public static final int TYPE_SYSTEMCODE = Types.VARCHAR;

	public static final String NAME_SYSTEMCODE = "SYSTEMCODE";

	public static final int ID_ACCESSLEVEL = 5;

	public static final int TYPE_ACCESSLEVEL = Types.VARCHAR;

	public static final String NAME_ACCESSLEVEL = "ACCESSLEVEL";

	private static final String TABLE_NAME = "BPM_PROCESSDEF";

	/**
	 * create an array of string containing all the fields of the BPM_PROCESSDEF
	 * table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_PROCESSDEF.PROCESSDEF_ID", "BPM_PROCESSDEF.DEFNAME",
			"BPM_PROCESSDEF.DESCRIPTION", "BPM_PROCESSDEF.CLASSIFICATION",
			"BPM_PROCESSDEF.SYSTEMCODE", "BPM_PROCESSDEF.ACCESSLEVEL" };

	/**
	 * create an array of string containing all the fields of the BPM_PROCESSDEF
	 * table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "PROCESSDEF_ID",
			"DEFNAME", "DESCRIPTION", "CLASSIFICATION", "SYSTEMCODE",
			"ACCESSLEVEL" };

	/**
	 * Field that contains the comma separated fields of the BPM_PROCESSDEF
	 * table
	 */
	private static final String ALL_FIELDS = "BPM_PROCESSDEF.PROCESSDEF_ID"
			+ ",BPM_PROCESSDEF.DEFNAME" + ",BPM_PROCESSDEF.DESCRIPTION"
			+ ",BPM_PROCESSDEF.CLASSIFICATION" + ",BPM_PROCESSDEF.SYSTEMCODE"
			+ ",BPM_PROCESSDEF.ACCESSLEVEL";

	/** 流程定义cache */
	Cache processDefCache = null; // 流程定义cache

	/** 流程定义cache是否可用 */
	boolean cacheEnabled;

	public DbProcessDefManager() {
		processDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ProcessDefCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new DbProcessDef instance.
	 * 
	 * @return the new DbProcessDef
	 */
	public EIProcessDef createProcessDef() {
		return new DbProcessDef();
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
	 * Load a DbProcessDef from the BPM_PROCESSDEF using its key fields.
	 * 
	 * @return a unique DbProcessDef
	 */
	public EIProcessDef loadByKey(String processDefId) throws BPMException {
		// try to get the process definition from cache
		DbProcessDef processDef = (DbProcessDef) processDefCache
				.get(processDefId);
		if (processDef != null) {
			return processDef;
		}
		// can't get it from cache , now load it from db;
		// synchronized the processDefId only one thread can load the from db
		synchronized (processDefId.intern()) {
			// try again load it from cache
			processDef = (DbProcessDef) processDefCache.get(processDefId);
			if (processDef != null) {
				return processDef;
			}
			// now load it from db
			Connection c = null;
			PreparedStatement ps = null;
			try {
				c = getConnection();
				if (log.isDebugEnabled())
					log
							.debug("SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.PROCESSDEF_ID=?");
				ps = c
						.prepareStatement(
								"SELECT "
										+ ALL_FIELDS
										+ " FROM BPM_PROCESSDEF WHERE BPM_PROCESSDEF.PROCESSDEF_ID=?",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ps.setString(1, processDefId);
				List pReturn = loadByPreparedStatement(ps);
				if (pReturn.size() < 1)
					return null;
				else {
					// put it into cache
					putToCache(processDefId, (DbProcessDef) pReturn.get(0));
					return (DbProcessDef) pReturn.get(0);
				}
			} catch (SQLException e) {
				throw new BPMException("load the process definition "
						+ processDefId + " failed!", e,
						BPMException.PROCESSDEFINITIONERROR);
			} finally {
				getManager().close(ps);
				freeConnection(c);
			}
		}
	}

	// ////////////////////////////////////
	// LOAD ALL
	// ////////////////////////////////////

	/**
	 * Load all the rows from BPM_PROCESSDEF.
	 * 
	 * @return an array of BPMProcessdefManager pObject
	 */
	public List loadAll() {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug("SELECT PROCESSDEF_ID FROM BPM_PROCESSDEF");
			ps = c.prepareStatement("SELECT PROCESSDEF_ID FROM BPM_PROCESSDEF",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			List list = new DbProcessDefList(loadByPreparedStatement(ps,
					new int[] { ID_PROCESSDEF_ID }));
			return Collections.unmodifiableList(list);
		} catch (SQLException e) {
			log.error("load all process definition failed! ", e);
			return new ArrayList();
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
	 * @return an array of BPMProcessdefBean
	 */
	public List loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of BPMProcessdefBean
	 */
	public List loadByPreparedStatement(PreparedStatement ps, int[] fieldList)
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
			return v;
		} finally {
			// if (v != null) { v.clear(); v = null;}
			getManager().close(rs);
		}
	}

	// ////////////////////////////////////
	// RESTRICTION LOADER
	// ////////////////////////////////////
	/**
	 * retreive an array of BPMProcessdefBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting BPMProcessdefBean table
	 */
	public List loadByWhere(String where) throws BPMException {
		List result = null;
		try {
			result = new DbProcessDefList(loadByWhere(where,
					new int[] { ID_PROCESSDEF_ID }));
		} catch (SQLException e) {
			throw new BPMException(e);
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * retreive an array of BPMProcessdefBean given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting BPMProcessdefBean table
	 */
	public DbProcessDef[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_PROCESSDEF " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_PROCESSDEF ");
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

			return (DbProcessDef[]) v.toArray(new DbProcessDef[0]);
		} finally {
			// if (v != null) { v.clear(); }
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
	public int deleteByKey(String processDefId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled()) {
				log
						.debug("DELETE from BPM_PROCESSDEF WHERE BPM_PROCESSDEF.PROCESSDEF_ID=?");
			}
			ps = c
					.prepareStatement(
							"DELETE from BPM_PROCESSDEF WHERE BPM_PROCESSDEF.PROCESSDEF_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processDefId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			log.error("delete process definition : " + processDefId
					+ " failed!", e);
			return -1;
		} finally {
			removeFromCache(processDefId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(EIProcessDef processDef) throws BPMException {
		DbProcessDef dbProcessDef = (DbProcessDef) processDef;
		if (dbProcessDef.isProcessDefIdInitialized() == true)
			return deleteByKey(dbProcessDef.getProcessDefId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_PROCESSDEF WHERE ");
			int _dirtyAnd = 0;
			if (dbProcessDef.isProcessDefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (dbProcessDef.isNameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEFNAME").append("=?");
				_dirtyAnd++;
			}
			if (dbProcessDef.isDescriptionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DESCRIPTION").append("=?");
				_dirtyAnd++;
			}
			if (dbProcessDef.isClassificationInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CLASSIFICATION").append("=?");
				_dirtyAnd++;
			}
			if (dbProcessDef.isSystemCodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SYSTEMCODE").append("=?");
				_dirtyAnd++;
			}
			if (dbProcessDef.isAccessLevelInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACCESSLEVEL").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (dbProcessDef.isProcessDefIdInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getProcessDefId());
			if (dbProcessDef.isNameInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getName());
			if (dbProcessDef.isDescriptionInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getDescription());
			if (dbProcessDef.isClassificationInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getClassification());
			if (dbProcessDef.isSystemCodeInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getSystemCode());
			if (dbProcessDef.isAccessLevelInitialized())
				ps.setString(++_dirtyCount, dbProcessDef.getAccessLevel());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			log.error("delete process definition : " + sql, e);
			return -1;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from BPM_PROCESSDEF table by where clausis
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
			DbProcessDef[] processDefs;
			try {
				processDefs = loadByWhere(where, new int[] { ID_PROCESSDEF_ID });
				for (int i = 0; i < processDefs.length; i++) {
					removeFromCache(processDefs[i].getProcessDefId());
				}
			} catch (SQLException e) {
				throw new BPMException("", e, BPMException.ROUTEDEFINITIONERROR);
			}
			// delete it!
			c = getConnection();
			String delByWhereSQL = "DELETE FROM BPM_PROCESSDEF " + where;
			if (log.isDebugEnabled()) {
				log.debug(delByWhereSQL);
			}
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			log.error("delete ", e);
			return -1;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of BPMProcessdefBean pObjects
	 * 
	 * @param pObjects
	 *            the BPMProcessdefBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIProcessDef[] save(EIProcessDef[] processDefs) throws BPMException {
		for (int iIndex = 0; iIndex < processDefs.length; iIndex++) {
			save(processDefs[iIndex]);
		}
		return processDefs;
	}

	/**
	 * before the save of the BPMProcessdefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be saved
	 */
	void beforeInsert(DbProcessDef pObject) throws SQLException {
	}

	/**
	 * after the save of the BPMProcessdefBean pObject
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be saved
	 */
	void afterInsert(DbProcessDef pObject) throws SQLException {
	}

	/**
	 * before the update of the BPMProcessdefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be updated
	 */
	void beforeUpdate(DbProcessDef pObject) throws SQLException {
	}

	/**
	 * after the update of the BPMProcessdefBean pObject.
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be updated
	 */
	void afterUpdate(DbProcessDef pObject) throws SQLException {
	}

	/**
	 * Save into the database the BPMProcessdefBean pObject
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to be saved
	 */
	public EIProcessDef save(EIProcessDef processDef) throws BPMException {
		DbProcessDef dbProcessDef = (DbProcessDef) processDef;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (dbProcessDef.isNew()) { // SAVE
				beforeInsert(dbProcessDef);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_PROCESSDEF (");
				if (dbProcessDef.isProcessDefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (dbProcessDef.isNameModified()) {
					_sql.append("DEFNAME").append(",");
					_dirtyCount++;
				}
				if (dbProcessDef.isDescriptionModified()) {
					_sql.append("DESCRIPTION").append(",");
					_dirtyCount++;
				}
				if (dbProcessDef.isClassificationModified()) {
					_sql.append("CLASSIFICATION").append(",");
					_dirtyCount++;
				}
				if (dbProcessDef.isSystemCodeModified()) {
					_sql.append("SYSTEMCODE").append(",");
					_dirtyCount++;
				}
				if (dbProcessDef.isAccessLevelModified()) {
					_sql.append("ACCESSLEVEL").append(",");
					_dirtyCount++;
				}
				_sql.setLength(_sql.length() - 1);
				_sql.append(") values (");
				for (int i = 0; i < _dirtyCount; i++)
					_sql.append("?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(")");
				if (log.isDebugEnabled()) {
					log.debug(_sql.toString());
				}
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (dbProcessDef.isProcessDefIdModified())
					ps.setString(++_dirtyCount, dbProcessDef.getProcessDefId());
				if (dbProcessDef.isNameModified())
					ps.setString(++_dirtyCount, dbProcessDef.getName());
				if (dbProcessDef.isDescriptionModified())
					ps.setString(++_dirtyCount, dbProcessDef.getDescription());
				if (dbProcessDef.isClassificationModified())
					ps.setString(++_dirtyCount, dbProcessDef
							.getClassification());
				if (dbProcessDef.isSystemCodeModified())
					ps.setString(++_dirtyCount, dbProcessDef.getSystemCode());
				if (dbProcessDef.isAccessLevelModified())
					ps.setString(++_dirtyCount, dbProcessDef.getAccessLevel());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				dbProcessDef.setIsNew(false);
				dbProcessDef.resetIsModified();
				afterInsert(dbProcessDef);
			} else { // UPDATE
			// preupdate+
				
				
				if (dbProcessDef.isModified()){
//					 preupdate-
					beforeUpdate(dbProcessDef);
					_sql = new StringBuffer("UPDATE BPM_PROCESSDEF SET ");
					if (dbProcessDef.isProcessDefIdModified())
						_sql.append("PROCESSDEF_ID").append("=?,");
					if (dbProcessDef.isNameModified())
						_sql.append("DEFNAME").append("=?,");
					if (dbProcessDef.isDescriptionModified())
						_sql.append("DESCRIPTION").append("=?,");
					if (dbProcessDef.isClassificationModified())
						_sql.append("CLASSIFICATION").append("=?,");
					if (dbProcessDef.isSystemCodeModified())
						_sql.append("SYSTEMCODE").append("=?,");
					if (dbProcessDef.isAccessLevelModified())
						_sql.append("ACCESSLEVEL").append("=?,");
					_sql.setLength(_sql.length() - 1);
					_sql.append(" WHERE ");
					_sql.append("BPM_PROCESSDEF.PROCESSDEF_ID=?");
					if (log.isDebugEnabled()) {
						log.debug(_sql.toString());
					}
					ps = c.prepareStatement(_sql.toString(),
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					int _dirtyCount = 0;
					if (dbProcessDef.isProcessDefIdModified()) {
						if (dbProcessDef.getProcessDefId() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef
									.getProcessDefId());
					}
					if (dbProcessDef.isNameModified()) {
						if (dbProcessDef.getName() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef.getName());
					}
					if (dbProcessDef.isDescriptionModified()) {
						if (dbProcessDef.getDescription() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef
									.getDescription());
					}
					if (dbProcessDef.isClassificationModified()) {
						if (dbProcessDef.getClassification() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef
									.getClassification());
					}
					if (dbProcessDef.isSystemCodeModified()) {
						if (dbProcessDef.getSystemCode() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef
									.getSystemCode());
					}
					if (dbProcessDef.isAccessLevelModified()) {
						if (dbProcessDef.getAccessLevel() == null)
							ps.setNull(++_dirtyCount, 12);
						else
							ps.setString(++_dirtyCount, dbProcessDef
									.getAccessLevel());
					}
					if (_dirtyCount == 0)
						return dbProcessDef;
					ps.setString(++_dirtyCount, dbProcessDef.getProcessDefId());
					ps.executeUpdate();
					dbProcessDef.resetIsModified();
					// postupdate+

					// postupdate-
					afterUpdate(dbProcessDef);
				}

				
			}
		} catch (SQLException e) {
			throw new BPMException("save process definition failed! sql: "
					+ _sql, e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return dbProcessDef;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_PROCESSDEF on a
	 * BPMProcessdefBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting BPMProcessdefBean pObject
	 */
	public DbProcessDef decodeRow(ResultSet rs) throws SQLException {
		DbProcessDef pObject = (DbProcessDef) createProcessDef();
		if (rs.getObject(1) != null)
			pObject.setProcessDefId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setName(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setDescription(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setClassification(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setSystemCode(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setAccessLevel(rs.getString(6));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_PROCESSDEF on a
	 * BPMProcessdefBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting BPMProcessdefBean pObject
	 */
	public DbProcessDef decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbProcessDef pObject = (DbProcessDef) createProcessDef();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_PROCESSDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefId(rs.getString(pos));
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
			case ID_CLASSIFICATION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setClassification(rs.getString(pos));
				}
				break;
			case ID_SYSTEMCODE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setSystemCode(rs.getString(pos));
				}
				break;
			case ID_ACCESSLEVEL:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setAccessLevel(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * get the number of rows of the table BPM_PROCESSDEF
	 * 
	 * @return the number of rows returned
	 */
	public int count() throws BPMException {
		return countByWhere("");
	}

	/**
	 * get the number of rows of the table BPM_PROCESSDEF with a where clausis
	 * 
	 * @param where
	 *            the restriction clausis
	 * @return the number of rows returned
	 */
	public int countByWhere(String where) throws BPMException {
		String sql = "select count(*) as MCOUNT from BPM_PROCESSDEF " + where;
		Connection c = null;
		Statement pStatement = null;
		ResultSet rs = null;
		try {
			int iReturn = -1;
			c = getConnection();
			pStatement = c.createStatement();
			rs = pStatement.executeQuery(sql);
			if (rs.next()) {
				iReturn = rs.getInt("MCOUNT");
			}
			if (iReturn != -1)
				return iReturn;
		} catch (SQLException e) {
			throw new BPMException("count process definition failed, sql: "
					+ sql, e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(pStatement, rs);
			freeConnection(c);
		}
		throw new BPMException("count proces definition failed!",
				BPMException.PROCESSDEFINITIONERROR);
	}

	/**
	 * get the number of rows of the table BPM_PROCESSDEF with a prepared
	 * statement
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return the number of rows returned
	 */
	int countByPreparedStatement(PreparedStatement ps) throws SQLException {
		ResultSet rs = null;
		try {
			int iReturn = -1;
			rs = ps.executeQuery();
			if (rs.next())
				iReturn = rs.getInt("MCOUNT");
			if (iReturn != -1)
				return iReturn;
		} finally {
			getManager().close(rs);
		}
		throw new SQLException("Error in countByPreparedStatement");
	}

	/**
	 * look for the number of element of a specific BPMProcessdefBean pObject
	 * given a c
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return the number of rows returned
	 */
	public int count(EIProcessDef processDef) throws BPMException {
		DbProcessDef dbProcessDef = (DbProcessDef) processDef;
		StringBuffer where = new StringBuffer("");
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		StringBuffer _sqlWhere = null;
		try {
			_sql = new StringBuffer(
					"SELECT count(*) as MCOUNT  from BPM_PROCESSDEF WHERE ");
			_sqlWhere = new StringBuffer("");
			int _dirtyCount = 0;
			if (dbProcessDef.isProcessDefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (dbProcessDef.isNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEFNAME= ?");
			}
			if (dbProcessDef.isDescriptionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DESCRIPTION= ?");
			}
			if (dbProcessDef.isClassificationModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CLASSIFICATION= ?");
			}
			if (dbProcessDef.isSystemCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SYSTEMCODE= ?");
			}
			if (dbProcessDef.isAccessLevelModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACCESSLEVEL= ?");
			}
			if (_dirtyCount == 0)
				throw new BPMException(
						"The pObject to look is unvalid : not initialized !",
						BPMException.PROCESSDEFINITIONERROR);
			_sql.append(_sqlWhere);
			if (log.isDebugEnabled()) {
				log.debug(_sql.toString());
			}
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			_sql = null;
			_dirtyCount = 0;
			if (dbProcessDef.isProcessDefIdModified())
				ps.setString(++_dirtyCount, dbProcessDef.getProcessDefId());
			if (dbProcessDef.isNameModified())
				ps.setString(++_dirtyCount, dbProcessDef.getName());
			if (dbProcessDef.isDescriptionModified())
				ps.setString(++_dirtyCount, dbProcessDef.getDescription());
			if (dbProcessDef.isClassificationModified())
				ps.setString(++_dirtyCount, dbProcessDef.getClassification());
			if (dbProcessDef.isSystemCodeModified())
				ps.setString(++_dirtyCount, dbProcessDef.getSystemCode());
			if (dbProcessDef.isAccessLevelModified())
				ps.setString(++_dirtyCount, dbProcessDef.getAccessLevel());

			return countByPreparedStatement(ps);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * does a specific BPMProcessdefBean pObject exists ?
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return true if an pObject in the database matches the BPMProcessdefBean
	 *         pObject
	 */
	public boolean exists(EIProcessDef processDef) throws BPMException {
		if (count(processDef) > 0)
			return true;
		return false;
	}

	/**
	 * load a unique BPMProcessdefBean pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean pObject to look for
	 * @return the pObject matching the template
	 */
	public EIProcessDef loadObject(EIProcessDef processDef) throws BPMException {

		List pReturn = loadObjects(processDef);
		if (pReturn.size() == 0)
			return null;
		if (pReturn.size() > 1)
			throw new BPMException("More than one element !!",
					BPMException.PROCESSDEFINITIONERROR);
		return (EIProcessDef) pReturn.get(0);
	}

	/**
	 * load an array of BPMProcessdefBean from a template one
	 * 
	 * @param pObject
	 *            the BPMProcessdefBean template to look for
	 * @return all the BPMProcessdefBean matching the template
	 */
	public List loadObjects(EIProcessDef processDef) throws BPMException {
		DbProcessDef dbProcessDef = (DbProcessDef) processDef;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from BPM_PROCESSDEF WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (dbProcessDef.isProcessDefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (dbProcessDef.isNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEFNAME= ?");
			}
			if (dbProcessDef.isDescriptionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DESCRIPTION= ?");
			}
			if (dbProcessDef.isClassificationModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CLASSIFICATION= ?");
			}
			if (dbProcessDef.isSystemCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SYSTEMCODE= ?");
			}
			if (dbProcessDef.isAccessLevelModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACCESSLEVEL= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			if (log.isDebugEnabled()) {
				log.debug(_sql.toString());
			}
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (dbProcessDef.isProcessDefIdModified())
				ps.setString(++_dirtyCount, dbProcessDef.getProcessDefId());
			if (dbProcessDef.isNameModified())
				ps.setString(++_dirtyCount, dbProcessDef.getName());
			if (dbProcessDef.isDescriptionModified())
				ps.setString(++_dirtyCount, dbProcessDef.getDescription());
			if (dbProcessDef.isClassificationModified())
				ps.setString(++_dirtyCount, dbProcessDef.getClassification());
			if (dbProcessDef.isSystemCodeModified())
				ps.setString(++_dirtyCount, dbProcessDef.getSystemCode());
			if (dbProcessDef.isAccessLevelModified())
				ps.setString(++_dirtyCount, dbProcessDef.getAccessLevel());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
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
	private void putToCache(String uuid, DbProcessDef dbProcessDef) {
		synchronized (uuid.intern()) {
			if (!processDefCache.containsKey(uuid)) {
				processDefCache.put(uuid, dbProcessDef);
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
			if (processDefCache.containsKey(uuid)) {
				processDefCache.remove(uuid);
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
			if (processDefCache.containsKey(uuid) == false) {
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
			DbProcessDef[] processDefs = null;
			try {
				processDefs = loadByWhere(sqlWhere.toString(), null);
			} catch (SQLException e) {
				log.error("prefetch the Process Definition fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < processDefs.length; i++) {
				DbProcessDef pd = processDefs[i];
				putToCache(pd.getProcessDefId(), pd);
			}
		}
	}
}


