/**
 * $RCSfile: DbProcessDefRightGrpManager.java,v $
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.RightEngine;
import net.ooder.bpm.engine.database.DbManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程权限组管�?
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
public class DbProcessDefRightGrpManager implements Serializable{

	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefSupervisorManager.class);

	public static final int ID_RIGHT_GROUP_ID = 0;

	public static final int TYPE_RIGHT_GROUP_ID = Types.VARCHAR;

	public static final String NAME_RIGHT_GROUP_ID = "RIGHT_GROUP_ID";

	public static final int ID_PROCESSDEF_VERSION_ID = 1;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_PROCESSDEF_ID = 2;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_GROUPNAME = 3;

	public static final int TYPE_GROUPNAME = Types.VARCHAR;

	public static final String NAME_GROUPNAME = "GROUPNAME";

	public static final int ID_GROUPCODE = 4;

	public static final int TYPE_GROUPCODE = Types.VARCHAR;

	public static final String NAME_GROUPCODE = "GROUPCODE";

	public static final int ID_GROUPORDER = 5;

	public static final int TYPE_GROUPORDER = Types.DECIMAL;

	public static final String NAME_GROUPORDER = "GROUPORDER";

	public static final int ID_DEFAULTRIGHT = 6;

	public static final int TYPE_DEFAULTRIGHT = Types.VARCHAR;

	public static final String NAME_DEFAULTRIGHT = "DEFAULTRIGHT";

	private static final String TABLE_NAME = "RT_PROCESSDEF_RIGHT_GRP";

	/**
	 * create an array of string containing all the fields of the
	 * RT_PROCESSDEF_RIGHT_GRP table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID",
			"RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_VERSION_ID",
			"RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_ID",
			"RT_PROCESSDEF_RIGHT_GRP.GROUPNAME",
			"RT_PROCESSDEF_RIGHT_GRP.GROUPCODE",
			"RT_PROCESSDEF_RIGHT_GRP.GROUPORDER",
			"RT_PROCESSDEF_RIGHT_GRP.DEFAULTRIGHT" };

	/**
	 * create an array of string containing all the fields of the
	 * RT_PROCESSDEF_RIGHT_GRP table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "RIGHT_GROUP_ID",
			"PROCESSDEF_VERSION_ID", "PROCESSDEF_ID", "GROUPNAME", "GROUPCODE",
			"GROUPORDER", "DEFAULTRIGHT" };

	/**
	 * Field that contains the comma separated fields of the
	 * RT_PROCESSDEF_RIGHT_GRP table
	 */
	private static final String ALL_FIELDS = "RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID"
			+ ",RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_VERSION_ID"
			+ ",RT_PROCESSDEF_RIGHT_GRP.PROCESSDEF_ID"
			+ ",RT_PROCESSDEF_RIGHT_GRP.GROUPNAME"
			+ ",RT_PROCESSDEF_RIGHT_GRP.GROUPCODE"
			+ ",RT_PROCESSDEF_RIGHT_GRP.GROUPORDER"
			+ ",RT_PROCESSDEF_RIGHT_GRP.DEFAULTRIGHT";

	private static DbProcessDefRightGrpManager singleton = new DbProcessDefRightGrpManager();

	/**
	 * Get the RtProcessdefRightGrpManager singleton
	 * 
	 * @return RtProcessdefRightGrpManager
	 */
	synchronized public static DbProcessDefRightGrpManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtProcessdefRightGrpManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbProcessDefRightGrpManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtProcessdefRightGrpBean instance.
	 * 
	 * @return the new RtProcessdefRightGrpBean
	 */
	public DbProcessDefRightGrp createProcessDefRightGrp() {
		return new DbProcessDefRightGrp();
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
	 * Load a RtProcessdefRightGrpBean from the RT_PROCESSDEF_RIGHT_GRP using
	 * its key fields.
	 * 
	 * @return a unique RtProcessdefRightGrpBean
	 */
	public DbProcessDefRightGrp loadByKey(String rightGroupId)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM RT_PROCESSDEF_RIGHT_GRP WHERE RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM RT_PROCESSDEF_RIGHT_GRP WHERE RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, rightGroupId);
			DbProcessDefRightGrp pReturn[] = loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else
				return pReturn[0];
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of RtProcessdefRightGrpBean
	 */
	public DbProcessDefRightGrp[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of RtProcessdefRightGrpBean
	 */
	public DbProcessDefRightGrp[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbProcessDefRightGrp[]) v
					.toArray(new DbProcessDefRightGrp[0]);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	/**
	 * retreive an array of RtProcessdefRightGrpBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RtProcessdefRightGrpBean table
	 */
	public DbProcessDefRightGrp[] loadByWhere(String where) throws SQLException {
		return loadByWhere(where, null);
	}

	/**
	 * retreive an array of RtProcessdefRightGrpBean given a sql where clausis,
	 * and a list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting RtProcessdefRightGrpBean table
	 */
	public DbProcessDefRightGrp[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_PROCESSDEF_RIGHT_GRP "
					+ where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_PROCESSDEF_RIGHT_GRP ");
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
			rs = pStatement.executeQuery(sql);
			v = new java.util.ArrayList();
			while (rs.next()) {
				if (fieldList == null)
					v.add(decodeRow(rs));
				else
					v.add(decodeRow(rs, fieldList));
			}

			return (DbProcessDefRightGrp[]) v
					.toArray(new DbProcessDefRightGrp[0]);
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
	public int deleteByKey(String rightGroupId) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("DELETE from RT_PROCESSDEF_RIGHT_GRP WHERE RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from RT_PROCESSDEF_RIGHT_GRP WHERE RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, rightGroupId);
			return ps.executeUpdate();
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(DbProcessDefRightGrp pObject) throws SQLException {
		if (pObject.isRightGroupIdInitialized() == true)
			return deleteByKey(pObject.getRightGroupId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM RT_PROCESSDEF_RIGHT_GRP WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isRightGroupIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RIGHT_GROUP_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isGroupnameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("GROUPNAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isGroupcodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("GROUPCODE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isGrouporderInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("GROUPORDER").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isDefaultrightInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEFAULTRIGHT").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isRightGroupIdInitialized())
				ps.setString(++_dirtyCount, pObject.getRightGroupId());
			if (pObject.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isGroupnameInitialized())
				ps.setString(++_dirtyCount, pObject.getGroupName());
			if (pObject.isGroupcodeInitialized())
				ps.setString(++_dirtyCount, pObject.getGroupCode());
			if (pObject.isGrouporderInitialized())
				ps.setInt(++_dirtyCount, pObject.getGroupOrder());
			if (pObject.isDefaultrightInitialized())
				ps.setString(++_dirtyCount, pObject.getDefaultRight());
			int _rows = ps.executeUpdate();
			return _rows;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_PROCESSDEF_RIGHT_GRP table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public int deleteByWhere(String where) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			String delByWhereSQL = "DELETE FROM RT_PROCESSDEF_RIGHT_GRP "
					+ where;
			if (log.isDebugEnabled())
				log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of RtProcessdefRightGrpBean pObjects
	 * 
	 * @param pObjects
	 *            the RtProcessdefRightGrpBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public DbProcessDefRightGrp[] save(DbProcessDefRightGrp[] pObjects)
			throws SQLException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the RtProcessdefRightGrpBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to be saved
	 */
	void beforeInsert(DbProcessDefRightGrp pObject) throws SQLException {
	}

	/**
	 * after the save of the RtProcessdefRightGrpBean pObject
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to be saved
	 */
	void afterInsert(DbProcessDefRightGrp pObject) throws SQLException {
	}

	/**
	 * before the update of the RtProcessdefRightGrpBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to be updated
	 */
	void beforeUpdate(DbProcessDefRightGrp pObject) throws SQLException {
	}

	/**
	 * after the update of the RtProcessdefRightGrpBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to be updated
	 */
	void afterUpdate(DbProcessDefRightGrp pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtProcessdefRightGrpBean pObject
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to be saved
	 */
	public DbProcessDefRightGrp save(DbProcessDefRightGrp pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into RT_PROCESSDEF_RIGHT_GRP (");
				if (pObject.isRightGroupIdModified()) {
					_sql.append("RIGHT_GROUP_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isGroupnameModified()) {
					_sql.append("GROUPNAME").append(",");
					_dirtyCount++;
				}
				if (pObject.isGroupcodeModified()) {
					_sql.append("GROUPCODE").append(",");
					_dirtyCount++;
				}
				if (pObject.isGrouporderModified()) {
					_sql.append("GROUPORDER").append(",");
					_dirtyCount++;
				}
				if (pObject.isDefaultrightModified()) {
					_sql.append("DEFAULTRIGHT").append(",");
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
				if (pObject.isRightGroupIdModified())
					ps.setString(++_dirtyCount, pObject.getRightGroupId());
				if (pObject.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, pObject
							.getProcessDefVersionId());
				if (pObject.isProcessdefIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessDefId());
				if (pObject.isGroupnameModified())
					ps.setString(++_dirtyCount, pObject.getGroupName());
				if (pObject.isGroupcodeModified())
					ps.setString(++_dirtyCount, pObject.getGroupCode());
				if (pObject.isGrouporderModified())
					ps.setInt(++_dirtyCount, pObject.getGroupOrder());
				if (pObject.isDefaultrightModified())
					ps.setString(++_dirtyCount, pObject.getDefaultRight());
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
				_sql = new StringBuffer("UPDATE RT_PROCESSDEF_RIGHT_GRP SET ");
				if (pObject.isRightGroupIdModified())
					_sql.append("RIGHT_GROUP_ID").append("=?,");
				if (pObject.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (pObject.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (pObject.isGroupnameModified())
					_sql.append("GROUPNAME").append("=?,");
				if (pObject.isGroupcodeModified())
					_sql.append("GROUPCODE").append("=?,");
				if (pObject.isGrouporderModified())
					_sql.append("GROUPORDER").append("=?,");
				if (pObject.isDefaultrightModified())
					_sql.append("DEFAULTRIGHT").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("RT_PROCESSDEF_RIGHT_GRP.RIGHT_GROUP_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isRightGroupIdModified()) {
					if (pObject.getRightGroupId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRightGroupId());
				}
				if (pObject.isProcessdefVersionIdModified()) {
					if (pObject.getProcessDefVersionId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getProcessDefVersionId());
				}
				if (pObject.isProcessdefIdModified()) {
					if (pObject.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessDefId());
				}
				if (pObject.isGroupnameModified()) {
					if (pObject.getGroupName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getGroupName());
				}
				if (pObject.isGroupcodeModified()) {
					if (pObject.getGroupCode() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getGroupCode());
				}
				if (pObject.isGrouporderModified()) {
					ps.setInt(++_dirtyCount, pObject.getGroupOrder());
				}
				if (pObject.isDefaultrightModified()) {
					if (pObject.getDefaultRight() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getDefaultRight());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getRightGroupId());
				ps.executeUpdate();
				pObject.resetIsModified();
				// postupdate+

				// postupdate-
				afterUpdate(pObject);
			}
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_PROCESSDEF_RIGHT_GRP on a
	 * RtProcessdefRightGrpBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtProcessdefRightGrpBean pObject
	 */
	public DbProcessDefRightGrp decodeRow(ResultSet rs) throws SQLException {
		DbProcessDefRightGrp pObject = createProcessDefRightGrp();
		if (rs.getObject(1) != null)
			pObject.setRightGroupId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefVersionId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setProcessDefId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setGroupName(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setGroupCode(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setGroupOrder(rs.getInt(6));
		if (rs.getObject(7) != null)
			pObject.setDefaultRight(rs.getString(7));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_PROCESSDEF_RIGHT_GRP on a
	 * RtProcessdefRightGrpBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtProcessdefRightGrpBean pObject
	 */
	public DbProcessDefRightGrp decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbProcessDefRightGrp pObject = createProcessDefRightGrp();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_RIGHT_GROUP_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRightGroupId(rs.getString(pos));
				}
				break;
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
			case ID_GROUPNAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setGroupName(rs.getString(pos));
				}
				break;
			case ID_GROUPCODE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setGroupCode(rs.getString(pos));
				}
				break;
			case ID_GROUPORDER:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setGroupOrder(rs.getInt(pos));
				}
				break;
			case ID_DEFAULTRIGHT:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDefaultRight(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique RtProcessdefRightGrpBean pObject from an template one
	 * giving a c
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean pObject to look for
	 * @return the pObject matching the template
	 */
	public DbProcessDefRightGrp loadObject(DbProcessDefRightGrp pObject)
			throws SQLException {
		DbProcessDefRightGrp[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new SQLException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of RtProcessdefRightGrpBean from a template one
	 * 
	 * @param pObject
	 *            the RtProcessdefRightGrpBean template to look for
	 * @return all the RtProcessdefRightGrpBean matching the template
	 */
	public DbProcessDefRightGrp[] loadObjects(DbProcessDefRightGrp pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_PROCESSDEF_RIGHT_GRP WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isRightGroupIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RIGHT_GROUP_ID= ?");
			}
			if (pObject.isProcessdefVersionIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_VERSION_ID= ?");
			}
			if (pObject.isProcessdefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (pObject.isGroupnameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("GROUPNAME= ?");
			}
			if (pObject.isGroupcodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("GROUPCODE= ?");
			}
			if (pObject.isGrouporderModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("GROUPORDER= ?");
			}
			if (pObject.isDefaultrightModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEFAULTRIGHT= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			if (log.isDebugEnabled())
				log.debug(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (pObject.isRightGroupIdModified())
				ps.setString(++_dirtyCount, pObject.getRightGroupId());
			if (pObject.isProcessdefVersionIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isProcessdefIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isGroupnameModified())
				ps.setString(++_dirtyCount, pObject.getGroupName());
			if (pObject.isGroupcodeModified())
				ps.setString(++_dirtyCount, pObject.getGroupCode());
			if (pObject.isGrouporderModified())
				ps.setInt(++_dirtyCount, pObject.getGroupOrder());
			if (pObject.isDefaultrightModified())
				ps.setString(++_dirtyCount, pObject.getDefaultRight());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public List getRightGroupByProcessDefVersionId(String processDefVersionId)
			throws BPMException {
		List list = new ArrayList();
		String sqlWhere = " where PROCESSDEF_VERSION_ID= '"
				+ processDefVersionId + "'";
		try {
			DbProcessDefRightGrp[] rightGrps = loadByWhere(sqlWhere);
			if (rightGrps == null) {
				return list;
			}
			for (int i = 0; i < rightGrps.length; i++) {
				list.add(rightGrps[i]);
			}
			return list;
		} catch (SQLException e) {
			throw new BPMException(
					"load right group faild in process definiton "
							+ processDefVersionId, e);
		}
	}

	public void deleteByProcessDefVersion(String processDefVersionId)
			throws BPMException {
		String sqlWhere = " where PROCESSDEF_VERSION_ID= '"
				+ processDefVersionId + "'";
		try {
			deleteByWhere(sqlWhere);
		} catch (SQLException e) {
			throw new BPMException(
					"delete right group faild in process definiton "
							+ processDefVersionId, e);
		}
	}
}


