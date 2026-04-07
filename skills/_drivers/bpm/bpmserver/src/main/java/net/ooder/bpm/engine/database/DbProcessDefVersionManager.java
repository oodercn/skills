/**
 * $RCSfile: DbProcessDefVersionManager.java,v $
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
import java.util.Iterator;
import java.util.List;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
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
 * Description: 流程定义版本数据库实�?
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
import org.springframework.stereotype.Component;

@Component
public class DbProcessDefVersionManager extends EIProcessDefVersionManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefVersionManager.class);

	public static final int ID_PROCESSDEF_VERSION_ID = 0;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_PROCESSDEF_ID = 1;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_VERSION = 2;

	public static final int TYPE_VERSION = Types.DECIMAL;

	public static final String NAME_VERSION = "VERSION";

	public static final int ID_DESCRIPTION = 3;

	public static final int TYPE_DESCRIPTION = Types.VARCHAR;

	public static final String NAME_DESCRIPTION = "DESCRIPTION";

	public static final int ID_PUBLICATIONSTATUS = 4;

	public static final int TYPE_PUBLICATIONSTATUS = Types.VARCHAR;

	public static final String NAME_PUBLICATIONSTATUS = "PUBLICATIONSTATUS";

	public static final int ID_ACTIVETIME = 5;

	public static final int TYPE_ACTIVETIME = Types.DECIMAL;

	public static final String NAME_ACTIVETIME = "ACTIVETIME";

	public static final int ID_FREEZETIME = 6;

	public static final int TYPE_FREEZETIME = Types.DECIMAL;

	public static final String NAME_FREEZETIME = "FREEZETIME";

	public static final int ID_CREATORID = 7;

	public static final int TYPE_CREATORID = Types.VARCHAR;

	public static final String NAME_CREATORID = "CREATORID";

	public static final int ID_CREATORNAME = 8;

	public static final int TYPE_CREATORNAME = Types.VARCHAR;

	public static final String NAME_CREATORNAME = "CREATORNAME";

	public static final int ID_CREATED = 9;

	public static final int TYPE_CREATED = Types.DECIMAL;

	public static final String NAME_CREATED = "CREATED";

	public static final int ID_MODIFIERID = 10;

	public static final int TYPE_MODIFIERID = Types.VARCHAR;

	public static final String NAME_MODIFIERID = "MODIFIERID";

	public static final int ID_MODIFIERNAME = 11;

	public static final int TYPE_MODIFIERNAME = Types.VARCHAR;

	public static final String NAME_MODIFIERNAME = "MODIFIERNAME";

	public static final int ID_MODIFYTIME = 12;

	public static final int TYPE_MODIFYTIME = Types.DECIMAL;

	public static final String NAME_MODIFYTIME = "MODIFYTIME";

	public static final int ID_LIMIT = 13;

	public static final int TYPE_LIMIT = Types.DECIMAL;

	public static final String NAME_LIMIT = "LIMITTIME";

	public static final int ID_DURATIONUNIT = 14;

	public static final int TYPE_DURATIONUNIT = Types.VARCHAR;

	public static final String NAME_DURATIONUNIT = "DURATIONUNIT";

	private static final String TABLE_NAME = "BPM_PROCESSDEF_VERSION";

	/**
	 * create an array of string containing all the fields of the
	 * BPM_PROCESSDEF_VERSION table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID",
			"BPM_PROCESSDEF_VERSION.PROCESSDEF_ID",
			"BPM_PROCESSDEF_VERSION.VERSION",
			"BPM_PROCESSDEF_VERSION.DESCRIPTION",
			"BPM_PROCESSDEF_VERSION.PUBLICATIONSTATUS",
			"BPM_PROCESSDEF_VERSION.ACTIVETIME",
			"BPM_PROCESSDEF_VERSION.FREEZETIME",
			"BPM_PROCESSDEF_VERSION.CREATORID",
			"BPM_PROCESSDEF_VERSION.CREATORNAME",
			"BPM_PROCESSDEF_VERSION.CREATED",
			"BPM_PROCESSDEF_VERSION.MODIFIERID",
			"BPM_PROCESSDEF_VERSION.MODIFIERNAME",
			"BPM_PROCESSDEF_VERSION.MODIFYTIME",
			"BPM_PROCESSDEF_VERSION.LIMITTIME",
			"BPM_PROCESSDEF_VERSION.DURATIONUNIT" };

	/**
	 * create an array of string containing all the fields of the
	 * BPM_PROCESSDEF_VERSION table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "PROCESSDEF_VERSION_ID",
			"PROCESSDEF_ID", "VERSION", "DESCRIPTION", "PUBLICATIONSTATUS",
			"ACTIVETIME", "FREEZETIME", "CREATORID", "CREATORNAME", "CREATED",
			"MODIFIERID", "MODIFIERNAME", "MODIFYTIME", "LIMITTIME", "DURATIONUNIT" };

	/**
	 * Field that contains the comma separated fields of the
	 * BPM_PROCESSDEF_VERSION table
	 */
	private static final String ALL_FIELDS = "BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID"
			+ ",BPM_PROCESSDEF_VERSION.PROCESSDEF_ID"
			+ ",BPM_PROCESSDEF_VERSION.VERSION"
			+ ",BPM_PROCESSDEF_VERSION.DESCRIPTION"
			+ ",BPM_PROCESSDEF_VERSION.PUBLICATIONSTATUS"
			+ ",BPM_PROCESSDEF_VERSION.ACTIVETIME"
			+ ",BPM_PROCESSDEF_VERSION.FREEZETIME"
			+ ",BPM_PROCESSDEF_VERSION.CREATORID"
			+ ",BPM_PROCESSDEF_VERSION.CREATORNAME"
			+ ",BPM_PROCESSDEF_VERSION.CREATED"
			+ ",BPM_PROCESSDEF_VERSION.MODIFIERID"
			+ ",BPM_PROCESSDEF_VERSION.MODIFIERNAME"
			+ ",BPM_PROCESSDEF_VERSION.MODIFYTIME"
			+ ",BPM_PROCESSDEF_VERSION.LIMITTIME"
			+ ",BPM_PROCESSDEF_VERSION.DURATIONUNIT";

	/** 流程定义版本cache */
	Cache processDefVersionCache = null; // 流程定义cache

	/** 流程定义版本cache是否可用 */
	boolean cacheEnabled;

	public DbProcessDefVersionManager() {
		processDefVersionCache = CacheManagerFactory.createCache(
				BPMConstants.CONFIG_KEY, "ProcessDefVersionCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new DbProcessDefVersion instance.
	 * 
	 * @return the new DbProcessDefVersion
	 */
	public EIProcessDefVersion createProcessDefVersion() {
		return new DbProcessDefVersion();
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
	 * Load a DbProcessDefVersion from the BPM_PROCESSDEF_VERSION using its key
	 * fields.
	 * 
	 * @return a unique DbProcessDefVersion
	 */
	public EIProcessDefVersion loadByKey(String processDefVersionId)
			throws BPMException {
		// check cache first
		DbProcessDefVersion dbProcessDefVersion = (DbProcessDefVersion) processDefVersionCache
				.get(processDefVersionId);
		if (dbProcessDefVersion != null) {
			return dbProcessDefVersion;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processDefVersionId);
			DbProcessDefVersion pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				putToCache(processDefVersionId, pReturn[0]);
				return pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load a DbProcessDefVersion table from the BPM_PROCESSDEF_VERSION table
	 * using its PROCESSDEF_ID field.
	 * 
	 * @return an array of DbProcessDefVersion
	 */
	public List loadByProcessdefId(String value) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=?");
			ps = c
					.prepareStatement(
							"SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			DbProcessDefVersion[] versions = loadByPreparedStatement(ps,
					new int[] { ID_PROCESSDEF_VERSION_ID });
			List list = null;
			list = new DbProcessDefVersionList(versions);
			return Collections.unmodifiableList(list);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete from the BPM_PROCESSDEF_VERSION table by PROCESSDEF_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	public int deleteByProcessdefId(String value) throws BPMException {
		String sql = " WHERE PROCESSDEF_ID=?";
		return deleteByWhere(sql);
	}

	/**
	 * get the BPMProcessdefBean object from the
	 * BPM_PROCESSDEF_VERSION.PROCESSDEF_ID field.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion
	 * @return the associated BPMProcessdefBean pObject
	 */
	public EIProcessDef getParentProcessDef(EIProcessDefVersion pObject)
			throws BPMException {
		DbProcessDef other = (DbProcessDef) DbProcessDefManager.getInstance()
				.createProcessDef();
		other.setProcessDefId(pObject.getProcessDefId());
		return EIProcessDefManager.getInstance().loadObject(other);
	}

	/**
	 * associates the DbProcessDefVersion object to the BPMProcessdefBean
	 * object.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion object to use
	 * @param pObjectToBeSet
	 *            the BPMProcessdefBean object to associate to the
	 *            DbProcessDefVersion
	 * @return the associated BPMProcessdefBean pObject
	 */
	public EIProcessDefVersion setParentProcessDef(EIProcessDefVersion pObject,
			EIProcessDef pObjectToBeSet) {
		pObject.setProcessDefId(pObjectToBeSet.getProcessDefId());
		return pObject;
	}

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of DbProcessDefVersion
	 */
	DbProcessDefVersion[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of DbProcessDefVersion
	 */
	DbProcessDefVersion[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbProcessDefVersion[]) v
					.toArray(new DbProcessDefVersion[0]);
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
	 * retreive an array of DbProcessDefVersion given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting DbProcessDefVersion table
	 */
	public List loadByWhere(String where) throws BPMException {
		List result = null;
		try {
			result = new DbProcessDefVersionList(loadByWhere(where,
					new int[] { ID_PROCESSDEF_VERSION_ID }));
		} catch (SQLException e) {
			e.printStackTrace();
			result = new ArrayList();
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * retreive an array of DbProcessDefVersion given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting DbProcessDefVersion table
	 */
	DbProcessDefVersion[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_PROCESSDEF_VERSION "
					+ where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_PROCESSDEF_VERSION ");
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

			return (DbProcessDefVersion[]) v
					.toArray(new DbProcessDefVersion[0]);
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
	public int deleteByKey(String processdefVersionId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			deleteAttributeByKey(processdefVersionId);
			deleteListenerByKey(processdefVersionId);
			if (log.isDebugEnabled())
				log
						.debug("DELETE from BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_PROCESSDEF_VERSION WHERE BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processdefVersionId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			removeFromCache(processdefVersionId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(EIProcessDefVersion pObject) throws BPMException {
		DbProcessDefVersion processDefVersion = (DbProcessDefVersion) pObject;
		if (processDefVersion.isProcessdefVersionIdInitialized() == true)
			return deleteByKey(processDefVersion.getProcessDefVersionId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_PROCESSDEF_VERSION WHERE ");
			int _dirtyAnd = 0;
			if (processDefVersion.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isVersionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("VERSION").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isDescriptionInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DESCRIPTION").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isPublicationstatusInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PUBLICATIONSTATUS").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isActivetimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVETIME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isFreezetimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("FREEZETIME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isCreatoridInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CREATORID").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isCreatornameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CREATORNAME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isCreatedInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("CREATED").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isModifieridInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("MODIFIERID").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isModifiernameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("MODIFIERNAME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isModifytimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("MODIFYTIME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isLimitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LIMITTIME").append("=?");
				_dirtyAnd++;
			}
			if (processDefVersion.isDurationunitInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DURATIONUNIT").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (processDefVersion.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, processDefVersion
						.getProcessDefVersionId());
			if (processDefVersion.isProcessdefIdInitialized())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getProcessDefId());
			if (processDefVersion.isVersionInitialized())
				ps.setInt(++_dirtyCount, processDefVersion.getVersion());
			if (processDefVersion.isDescriptionInitialized())
				ps.setString(++_dirtyCount, processDefVersion.getDescription());
			if (processDefVersion.isPublicationstatusInitialized())
				ps.setString(++_dirtyCount, processDefVersion
						.getPublicationStatus());
			if (processDefVersion.isActivetimeInitialized())
				ps.setLong(++_dirtyCount, processDefVersion.getActiveTime()
						.getTime());
			if (processDefVersion.isFreezetimeInitialized())
				ps.setLong(++_dirtyCount, processDefVersion.getFreezeTime()
						.getTime());
			if (processDefVersion.isCreatoridInitialized())
				ps.setString(++_dirtyCount, processDefVersion.getCreatorId());
			if (processDefVersion.isCreatornameInitialized())
				ps.setString(++_dirtyCount, processDefVersion.getCreatorName());
			if (processDefVersion.isCreatedInitialized())
				ps.setLong(++_dirtyCount, processDefVersion.getCreated()
						.getTime());
			if (processDefVersion.isModifieridInitialized())
				ps.setString(++_dirtyCount, processDefVersion.getModifierId());
			if (processDefVersion.isModifiernameInitialized())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getModifierName());
			if (processDefVersion.isModifytimeInitialized())
				ps.setLong(++_dirtyCount, processDefVersion.getModifyTime()
						.getTime());
			if (processDefVersion.isLimitInitialized())
				ps.setInt(++_dirtyCount, processDefVersion.getLimit());
			if (processDefVersion.isDurationunitInitialized())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getDurationUnit());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from BPM_PROCESSDEF_VERSION table by where clausis
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
			DbProcessDefVersion[] processDefVersions;
			try {
				processDefVersions = loadByWhere(where,
						new int[] { ID_PROCESSDEF_VERSION_ID });
				for (int i = 0; i < processDefVersions.length; i++) {
					removeFromCache(processDefVersions[i]
							.getProcessDefVersionId());
				}
			} catch (SQLException e) {
				throw new BPMException("", e,
						BPMException.PROCESSDEFINITIONERROR);
			}
			// delete it!
			c = getConnection();
			deleteAttributeByWhere(where);
			deleteListenerByWhere(where);
			String delByWhereSQL = "DELETE FROM BPM_PROCESSDEF_VERSION "
					+ where;
			if (log.isDebugEnabled())
				log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of DbProcessDefVersion pObjects
	 * 
	 * @param pObjects
	 *            the DbProcessDefVersion pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIProcessDefVersion[] save(EIProcessDefVersion[] pObjects)
			throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the DbProcessDefVersion pObject.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be saved
	 */
	void beforeInsert(DbProcessDefVersion pObject) throws SQLException {

	}

	/**
	 * after the save of the DbProcessDefVersion pObject
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be saved
	 */
	void afterInsert(DbProcessDefVersion pObject) throws SQLException {

	}

	/**
	 * before the update of the DbProcessDefVersion pObject.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be updated
	 */
	void beforeUpdate(DbProcessDefVersion pObject) throws SQLException {
	}

	/**
	 * after the update of the DbProcessDefVersion pObject.
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be updated
	 */
	void afterUpdate(DbProcessDefVersion pObject) throws SQLException {
	}

	/**
	 * Save into the database the DbProcessDefVersion pObject
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to be saved
	 */
	public EIProcessDefVersion save(EIProcessDefVersion pObject)
			throws BPMException {
		DbProcessDefVersion processDefVersion = (DbProcessDefVersion) pObject;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (processDefVersion.isNew()) { // SAVE
				beforeInsert(processDefVersion);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_PROCESSDEF_VERSION (");
				if (processDefVersion.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isVersionModified()) {
					_sql.append("VERSION").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isDescriptionModified()) {
					_sql.append("DESCRIPTION").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isPublicationstatusModified()) {
					_sql.append("PUBLICATIONSTATUS").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isActivetimeModified()) {
					_sql.append("ACTIVETIME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isFreezetimeModified()) {
					_sql.append("FREEZETIME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isCreatoridModified()) {
					_sql.append("CREATORID").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isCreatornameModified()) {
					_sql.append("CREATORNAME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isCreatedModified()) {
					_sql.append("CREATED").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isModifieridModified()) {
					_sql.append("MODIFIERID").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isModifiernameModified()) {
					_sql.append("MODIFIERNAME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isModifytimeModified()) {
					_sql.append("MODIFYTIME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isLimitModified()) {
					_sql.append("LIMITTIME").append(",");
					_dirtyCount++;
				}
				if (processDefVersion.isDurationunitModified()) {
					_sql.append("DURATIONUNIT").append(",");
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
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				_dirtyCount = 0;
				if (processDefVersion.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getProcessDefVersionId());
				if (processDefVersion.isProcessdefIdModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getProcessDefId());
				if (processDefVersion.isVersionModified())
					ps.setInt(++_dirtyCount, processDefVersion.getVersion());
				if (processDefVersion.isDescriptionModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getDescription());
				if (processDefVersion.isPublicationstatusModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getPublicationStatus());
				if (processDefVersion.isActivetimeModified())
					ps.setLong(++_dirtyCount, processDefVersion.getActiveTime()
							.getTime());
				if (processDefVersion.isFreezetimeModified())
					ps.setLong(++_dirtyCount, processDefVersion.getFreezeTime()
							.getTime());
				if (processDefVersion.isCreatoridModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getCreatorId());
				if (processDefVersion.isCreatornameModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getCreatorName());
				if (processDefVersion.isCreatedModified())
					ps.setLong(++_dirtyCount, processDefVersion.getCreated()
							.getTime());
				if (processDefVersion.isModifieridModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getModifierId());
				if (processDefVersion.isModifiernameModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getModifierName());
				if (processDefVersion.isModifytimeModified())
					ps.setLong(++_dirtyCount, processDefVersion.getModifyTime()
							.getTime());
				if (processDefVersion.isLimitModified())
					ps.setInt(++_dirtyCount, processDefVersion.getLimit());
				if (processDefVersion.isDurationunitModified())
					ps.setString(++_dirtyCount, processDefVersion
							.getDurationUnit());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				processDefVersion.setIsNew(false);
				afterInsert(processDefVersion);
			} else { // UPDATE
			// preupdate+

				// preupdate-
				beforeUpdate(processDefVersion);
				_sql = new StringBuffer("UPDATE BPM_PROCESSDEF_VERSION SET ");
				if (processDefVersion.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (processDefVersion.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (processDefVersion.isVersionModified())
					_sql.append("VERSION").append("=?,");
				if (processDefVersion.isDescriptionModified())
					_sql.append("DESCRIPTION").append("=?,");
				if (processDefVersion.isPublicationstatusModified())
					_sql.append("PUBLICATIONSTATUS").append("=?,");
				if (processDefVersion.isActivetimeModified())
					_sql.append("ACTIVETIME").append("=?,");
				if (processDefVersion.isFreezetimeModified())
					_sql.append("FREEZETIME").append("=?,");
				if (processDefVersion.isCreatoridModified())
					_sql.append("CREATORID").append("=?,");
				if (processDefVersion.isCreatornameModified())
					_sql.append("CREATORNAME").append("=?,");
				if (processDefVersion.isCreatedModified())
					_sql.append("CREATED").append("=?,");
				if (processDefVersion.isModifieridModified())
					_sql.append("MODIFIERID").append("=?,");
				if (processDefVersion.isModifiernameModified())
					_sql.append("MODIFIERNAME").append("=?,");
				if (processDefVersion.isModifytimeModified())
					_sql.append("MODIFYTIME").append("=?,");
				if (processDefVersion.isLimitModified())
					_sql.append("LIMITTIME").append("=?,");
				if (processDefVersion.isDurationunitModified())
					_sql.append("DURATIONUNIT").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_PROCESSDEF_VERSION.PROCESSDEF_VERSION_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (processDefVersion.isProcessdefVersionIdModified()) {
					if (processDefVersion.getProcessDefVersionId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getProcessDefVersionId());
				}
				if (processDefVersion.isProcessdefIdModified()) {
					if (processDefVersion.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getProcessDefId());
				}
				if (processDefVersion.isVersionModified()) {
					ps.setInt(++_dirtyCount, processDefVersion.getVersion());
				}
				if (processDefVersion.isDescriptionModified()) {
					if (processDefVersion.getDescription() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getDescription());
				}
				if (processDefVersion.isPublicationstatusModified()) {
					if (processDefVersion.getPublicationStatus() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getPublicationStatus());
				}
				if (processDefVersion.isActivetimeModified()) {
					if (processDefVersion.getActiveTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, processDefVersion
								.getActiveTime().getTime());
				}
				if (processDefVersion.isFreezetimeModified()) {
					if (processDefVersion.getFreezeTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, processDefVersion
								.getFreezeTime().getTime());
				}
				if (processDefVersion.isCreatoridModified()) {
					if (processDefVersion.getCreatorId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getCreatorId());
				}
				if (processDefVersion.isCreatornameModified()) {
					if (processDefVersion.getCreatorName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getCreatorName());
				}
				if (processDefVersion.isCreatedModified()) {
					if (processDefVersion.getCreated() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setLong(++_dirtyCount, processDefVersion
								.getCreated().getTime());
				}
				if (processDefVersion.isModifieridModified()) {
					if (processDefVersion.getModifierId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getModifierId());
				}
				if (processDefVersion.isModifiernameModified()) {
					if (processDefVersion.getModifierName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getModifierName());
				}
				if (processDefVersion.isModifytimeModified()) {
					if (processDefVersion.getModifyTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setDouble(++_dirtyCount, processDefVersion
								.getModifyTime().getTime());
				}
				if (processDefVersion.isLimitModified()) {
					ps.setLong(++_dirtyCount, processDefVersion.getLimit());
				}
				if (processDefVersion.isDurationunitModified()) {
					if (processDefVersion.getDurationUnit() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, processDefVersion
								.getDurationUnit());
				}
				if (_dirtyCount != 0) {
					ps.setString(++_dirtyCount, processDefVersion
							.getProcessDefVersionId());
					ps.executeUpdate();
					afterUpdate(processDefVersion);
				}
			}
			saveAttribute(processDefVersion);
			saveListeners(processDefVersion);
			processDefVersion.resetIsModified();
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return processDefVersion;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_PROCESSDEF_VERSION on a
	 * DbProcessDefVersion pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting DbProcessDefVersion pObject
	 */
	EIProcessDefVersion decodeRow(ResultSet rs) throws SQLException {
		DbProcessDefVersion pObject = (DbProcessDefVersion) createProcessDefVersion();
		if (rs.getObject(1) != null)
			pObject.setProcessDefVersionId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setVersion(rs.getInt(3));
		if (rs.getObject(4) != null)
			pObject.setDescription(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setPublicationStatus(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setActiveTime(new java.util.Date(rs.getLong(6)));
		if (rs.getObject(7) != null)
			pObject.setFreezeTime(new java.util.Date(rs.getLong(7)));
		if (rs.getObject(8) != null)
			pObject.setCreatorId(rs.getString(8));
		if (rs.getObject(9) != null)
			pObject.setCreatorName(rs.getString(9));
		if (rs.getObject(10) != null)
			pObject.setCreated(new java.sql.Date(rs.getLong(10)));
		if (rs.getObject(11) != null)
			pObject.setModifierId(rs.getString(11));
		if (rs.getObject(12) != null)
			pObject.setModifierName(rs.getString(12));
		if (rs.getObject(13) != null)
			pObject.setModifyTime(new java.sql.Date(rs.getLong(13)));
		if (rs.getObject(14) != null)
			pObject.setLimit(rs.getInt(14));
		if (rs.getObject(15) != null)
			pObject.setDurationUnit(rs.getString(15));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_PROCESSDEF_VERSION on a
	 * DbProcessDefVersion pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting DbProcessDefVersion pObject
	 */
	EIProcessDefVersion decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbProcessDefVersion pObject = (DbProcessDefVersion) createProcessDefVersion();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_PROCESSDEF_VERSION_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefVersionId(rs.getString(pos));
				}
				break;
			case ID_PROCESSDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessDefId(rs.getString(pos));
				}
				break;
			case ID_VERSION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setVersion(rs.getInt(pos));
				}
				break;
			case ID_DESCRIPTION:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDescription(rs.getString(pos));
				}
				break;
			case ID_PUBLICATIONSTATUS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPublicationStatus(rs.getString(pos));
				}
				break;
			case ID_ACTIVETIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActiveTime(new java.util.Date(rs.getLong(pos)));
				}
				break;
			case ID_FREEZETIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setFreezeTime(new java.util.Date(rs.getLong(pos)));
				}
				break;
			case ID_CREATORID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCreatorId(rs.getString(pos));
				}
				break;
			case ID_CREATORNAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCreatorName(rs.getString(pos));
				}
				break;
			case ID_CREATED:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setCreated(new java.util.Date(rs.getLong(pos)));
				}
				break;
			case ID_MODIFIERID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setModifierId(rs.getString(pos));
				}
				break;
			case ID_MODIFIERNAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setModifierName(rs.getString(pos));
				}
				break;
			case ID_MODIFYTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setModifyTime(new java.util.Date(rs.getLong(pos)));
				}
				break;
			case ID_LIMIT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLimit(rs.getInt(pos));
				}
				break;
			case ID_DURATIONUNIT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDurationUnit(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique DbProcessDefVersion pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion pObject to look for
	 * @return the pObject matching the template
	 */
	public EIProcessDefVersion loadObject(EIProcessDefVersion pObject)
			throws BPMException {
		List pReturn = loadObjects(pObject);
		if (pReturn.size() == 0)
			return null;
		if (pReturn.size() > 1)
			throw new BPMException("More than one element !!");
		return (EIProcessDefVersion) pReturn.get(0);
	}

	/**
	 * load an array of DbProcessDefVersion from a template one
	 * 
	 * @param pObject
	 *            the DbProcessDefVersion template to look for
	 * @return all the DbProcessDefVersion matching the template
	 */
	public List loadObjects(EIProcessDefVersion pObject) throws BPMException {
		DbProcessDefVersion processDefVersion = (DbProcessDefVersion) pObject;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer(
				"SELECT PROCESSDEF_VERSION_ID from BPM_PROCESSDEF_VERSION WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (processDefVersion.isProcessdefVersionIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_VERSION_ID= ?");
			}
			if (processDefVersion.isProcessdefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (processDefVersion.isVersionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("VERSION= ?");
			}
			if (processDefVersion.isDescriptionModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DESCRIPTION= ?");
			}
			if (processDefVersion.isPublicationstatusModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PUBLICATIONSTATUS= ?");
			}
			if (processDefVersion.isActivetimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVETIME= ?");
			}
			if (processDefVersion.isFreezetimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("FREEZETIME= ?");
			}
			if (processDefVersion.isCreatoridModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CREATORID= ?");
			}
			if (processDefVersion.isCreatornameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CREATORNAME= ?");
			}
			if (processDefVersion.isCreatedModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("CREATED= ?");
			}
			if (processDefVersion.isModifieridModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("MODIFIERID= ?");
			}
			if (processDefVersion.isModifiernameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("MODIFIERNAME= ?");
			}
			if (processDefVersion.isModifytimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("MODIFYTIME= ?");
			}
			if (processDefVersion.isLimitModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LIMITTIME= ?");
			}
			if (processDefVersion.isDurationunitModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DURATIONUNIT= ?");
			}
			if (_dirtyCount == 0)
				throw new BPMException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			if (log.isDebugEnabled())
				log.debug(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (processDefVersion.isProcessdefVersionIdModified())
				ps.setString(++_dirtyCount, processDefVersion
						.getProcessDefVersionId());
			if (processDefVersion.isProcessdefIdModified())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getProcessDefId());
			if (processDefVersion.isVersionModified())
				ps.setInt(++_dirtyCount, processDefVersion.getVersion());
			if (processDefVersion.isDescriptionModified())
				ps.setString(++_dirtyCount, processDefVersion.getDescription());
			if (processDefVersion.isPublicationstatusModified())
				ps.setString(++_dirtyCount, processDefVersion
						.getPublicationStatus());
			if (processDefVersion.isActivetimeModified())
				ps.setLong(++_dirtyCount, processDefVersion.getActiveTime()
						.getTime());
			if (processDefVersion.isFreezetimeModified())
				ps.setLong(++_dirtyCount, processDefVersion.getFreezeTime()
						.getTime());
			if (processDefVersion.isCreatoridModified())
				ps.setString(++_dirtyCount, processDefVersion.getCreatorId());
			if (processDefVersion.isCreatornameModified())
				ps.setString(++_dirtyCount, processDefVersion.getCreatorName());
			if (processDefVersion.isCreatedModified())
				ps.setLong(++_dirtyCount, processDefVersion.getCreated()
						.getTime());
			if (processDefVersion.isModifieridModified())
				ps.setString(++_dirtyCount, processDefVersion.getModifierId());
			if (processDefVersion.isModifiernameModified())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getModifierName());
			if (processDefVersion.isModifytimeModified())
				ps.setLong(++_dirtyCount, processDefVersion.getModifyTime()
						.getTime());
			if (processDefVersion.isLimitModified())
				ps.setInt(++_dirtyCount, processDefVersion.getLimit());
			if (processDefVersion.isDurationunitModified())
				ps
						.setString(++_dirtyCount, processDefVersion
								.getDurationUnit());
			ps.executeQuery();
			DbProcessDefVersion[] versions = loadByPreparedStatement(ps,
					new int[] { ID_PROCESSDEF_VERSION_ID });
			List list = new DbProcessDefVersionList(versions);
			return Collections.unmodifiableList(list);
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * 取得流程定义版本的第一个活动定�?
	 * 
	 * @param processDefVersionId
	 * @return
	 * @throws BPMException
	 */
	public EIActivityDef getFirstActivityDef(String processDefVersionId)
			throws BPMException {
		return EIActivityDefManager.getInstance().getFirstActivityDefInProcess(
				processDefVersionId);
	}

	/**
	 * get the active version
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersionManager#getActiveProcessDefVersion(java.lang.String)
	 */
	public EIProcessDefVersion getActiveProcessDefVersion(String value)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=? AND PUBLICATIONSTATUS='"
								+ ProcessDefVersionStatus.RELEASED.getType() + "'");
			ps = c
					.prepareStatement(
							"SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=? AND PUBLICATIONSTATUS='"
									+ ProcessDefVersionStatus.RELEASED.getType() + "'",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			DbProcessDefVersion[] versions = loadByPreparedStatement(ps,
					new int[] { ID_PROCESSDEF_VERSION_ID });
			if (versions.length == 0) {
				return null;
			} else if (versions.length > 1) {
				throw new BPMException(
						"more than one active version in process " + value);
			} else {
				return loadByKey(versions[0].getProcessDefVersionId());
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public EIProcessDefVersion getProcessDefVersionByVersion(
			String processDefId, int version) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=? AND VERSION=?");
			ps = c
					.prepareStatement(
							"SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION WHERE PROCESSDEF_ID=? AND VERSION=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, processDefId);
			ps.setInt(2, version);
			DbProcessDefVersion[] versions = loadByPreparedStatement(ps,
					new int[] { ID_PROCESSDEF_VERSION_ID });
			if (versions.length == 0) {
				return null;
			} else if (versions.length > 1) {
				throw new BPMException(
						"more than one active version in process "
								+ processDefId);
			} else {
				return loadByKey(versions[0].getProcessDefVersionId());
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public List getAllProcessDefVersionByProcessId(String processDefId)
			throws BPMException {
		String where = " WHERE PROCESSDEF_ID='" + processDefId
				+ "' ORDER BY VERSION ASC ";
		return loadByWhere(where);
	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " PROCESSDEF_ID, "
			+ " PROCESSDEF_VERSION_ID, " + " PROPNAME, " + " PROPVALUE, "
			+ " PROPCLASS, " + " PROPTYPE, " + " PARENTPROP_ID, "
			+ " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_PROCESSDEF_PROPERTY "
			+ "WHERE PROCESSDEF_VERSION_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_EXTATTRIBUTRE = "DELETE FROM BPM_PROCESSDEF_PROPERTY "
			+ "WHERE PROCESSDEF_VERSION_ID = ? ";

	private static final String DELETE_EXTATTRIBUTRE_BYWHERE = "DELETE FROM BPM_PROCESSDEF_PROPERTY "
			+ "WHERE PROCESSDEF_VERSION_ID IN ( SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_PROCESSDEF_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " PROCESSDEF_ID, "
			+ " PROCESSDEF_VERSION_ID, "
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
			+ "?, "
			+ "?, "
			+ "?, "
			+ "? " + " ) ";

	void loadAttribute(DbProcessDefVersion version) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(LOAD_EXTATTRIBUTRE);
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, version.getProcessDefVersionId());
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
						addAttribute(version, extAtt);
					}
					extAtt = (DbAttributeDef) version.attributeIdMap
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
				addAttribute(version, extAtt);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void addAttribute(DbProcessDefVersion version, DbAttributeDef extAtt) {
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeDef parent = (DbAttributeDef) version.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeDef();
				version.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeDef att = (DbAttributeDef) version.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeDef();
				att.setName((extAtt.getType()));
				version.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);
			// version.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		version.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	/**
	 * @param activityDef
	 */
	void saveAttribute(DbProcessDefVersion version) throws BPMException {
		if (version.isAttributeModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, version.getProcessDefVersionId());
			ps.executeUpdate();

			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			List list = version.getAllAttribute();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbAttributeDef att = (DbAttributeDef) it.next();
				saveAttribute(ps, version, att);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void saveAttribute(PreparedStatement ps,
			DbProcessDefVersion version, DbAttributeDef attDef)
			throws SQLException {

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
			ps.setString(2, version.getProcessDefId());
			ps.setString(3, version.getProcessDefVersionId());
			ps.setString(4, attDef.getName());
			ps.setString(5, v);
			ps.setString(6, attDef.getInterpretClass());
			ps.setString(7, attDef.getType());
			ps.setString(8, attDef.getParentId());
			ps.setInt(9, i);
			ps.setString(10, attDef.getCanInstantiate());
			ps.executeUpdate();
		}
	}

	void deleteAttributeByKey(String versionId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_EXTATTRIBUTRE);
			ps = c.prepareStatement(DELETE_EXTATTRIBUTRE);
			ps.setString(1, versionId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	void deleteAttributeByWhere(String where) throws BPMException {
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

	private static final String LOAD_PROCESSDEF_LISTENER = "SELECT LISTENER_ID, "
			+ " PROCESSDEF_ID, "
			+ " PROCESSDEF_VERSION_ID, "
			+ " LISTENERNAME, "
			+ " LISTENERREGISTEVENT, "
			+ " REALIZECLASS, "
			+ " EXPRESSIONSTR,"
			+ " EXPRESSIONLISENTERTYPE, "
			+ " EXPRESSIONEVENTTYPE "
			+ " FROM BPM_PROCESSLISTENER " + "WHERE PROCESSDEF_VERSION_ID = ? ";

	private static final String DELETE_PROCESSDEF_LISTENER = "DELETE FROM BPM_PROCESSLISTENER "
			+ " WHERE PROCESSDEF_VERSION_ID = ? ";

	private static final String DELETE_PROCESSDEF_LISTENER_BYWHERE = "DELETE FROM BPM_PROCESSLISTENER "
			+ " WHERE PROCESSDEF_VERSION_ID IN (SELECT PROCESSDEF_VERSION_ID FROM BPM_PROCESSDEF_VERSION ";

	private static final String INSERT_PROCESSDEF_LISTENER = "INSERT INTO BPM_PROCESSLISTENER "
			+ " ( "
			+ " LISTENER_ID, "
			+ " PROCESSDEF_ID, "
			+ " PROCESSDEF_VERSION_ID, "
			+ " LISTENERNAME, "
			+ " LISTENERREGISTEVENT, "
			+ " REALIZECLASS, "
			+ " EXPRESSIONSTR,"
			+ " EXPRESSIONLISENTERTYPE, "
			+ " EXPRESSIONEVENTTYPE "
			+ " ) VALUES ( " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, "+ " ?, "+ " ?, "+ " ?, "+ " ? ) ";

	void loadListener(DbProcessDefVersion version) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(LOAD_PROCESSDEF_LISTENER);
			ps = c.prepareStatement(LOAD_PROCESSDEF_LISTENER);
			ps.setString(1, version.getProcessDefVersionId());
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
				
				
				version.listeners.add(listener);
			}
		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void saveListeners(DbProcessDefVersion version) throws BPMException {
		if (version.isListenersModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_PROCESSDEF_LISTENER);
			ps = c.prepareStatement(DELETE_PROCESSDEF_LISTENER);
			ps.setString(1, version.getProcessDefVersionId());
			ps.executeUpdate();

			getManager().close(ps);
			if (log.isDebugEnabled())
				log.debug(INSERT_PROCESSDEF_LISTENER);
			ps = c.prepareStatement(INSERT_PROCESSDEF_LISTENER);
			List list = version.getListeners();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbListener listener = (DbListener) it.next();
				ps.setString(1, listener.getListenerId());
				ps.setString(2, version.getProcessDefId());
				ps.setString(3, version.getProcessDefVersionId());
				ps.setString(4, listener.getListenerName());
				ps.setString(5, listener.getListenerEvent());
				ps.setString(6, listener.getRealizeClass());
				ps.setString(7, listener.getExpressionStr());
				ps.setString(8, listener.getExpressionListenerType());
				ps.setString(9, listener.getExpressionEventType());
				ps.executeUpdate();
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void deleteListenerByKey(String versonId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log.debug(DELETE_PROCESSDEF_LISTENER);
			ps = c.prepareStatement(DELETE_PROCESSDEF_LISTENER);
			ps.setString(1, versonId);
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
			String sql = DELETE_PROCESSDEF_LISTENER_BYWHERE + where + " ) ";
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

	/**
	 * @param processDefVersionId
	 * @param version
	 */
	private void putToCache(String uuid, DbProcessDefVersion version) {
		synchronized (uuid.intern()) {
			if (!processDefVersionCache.containsKey(uuid)) {
				processDefVersionCache.put(uuid, version);
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
			if (processDefVersionCache.containsKey(uuid)) {
				processDefVersionCache.remove(uuid);
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
		StringBuffer sqlWhere = new StringBuffer(
				" WHERE PROCESSDEF_VERSION_ID IN (");
		int andCount = 0;
		// check out all UUIDs not in the cache now
		for (int i = 0; i < ids.size(); i++) {
			String uuid = (String) ids.get(i);
			if (processDefVersionCache.containsKey(uuid) == false) {
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
			DbProcessDefVersion[] versions = null;
			try {
				versions = loadByWhere(sqlWhere.toString(), null);
			} catch (SQLException e) {
				log.error("prefetch the Process Definition fail! Where Sql: "
						+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < versions.length; i++) {
				DbProcessDefVersion pd = versions[i];
				putToCache(pd.getProcessDefVersionId(), pd);
			}
		}
	}

}


