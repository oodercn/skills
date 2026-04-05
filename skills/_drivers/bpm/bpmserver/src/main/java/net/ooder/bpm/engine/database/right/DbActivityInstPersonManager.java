/**
 * $RCSfile: DbActivityInstPersonManager.java,v $
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
import java.util.UUID;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例与人员关系
 * </p>
 * <p>
 * 此数据因为数据量较大，且经常需要更新，所以不使用Cache
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
public class DbActivityInstPersonManager implements Serializable{
	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstPersonManager.class);

	public static final int ID_ACTIVITYINST_PERSON_ID = 0;

	public static final int TYPE_ACTIVITYINST_PERSON_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_PERSON_ID = "ACTIVITYINST_PERSON_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ACTIVITYINST_ID = 2;

	public static final int TYPE_ACTIVITYINST_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_ID = "ACTIVITYINST_ID";

	public static final int ID_PERSON_ID = 3;

	public static final int TYPE_PERSON_ID = Types.VARCHAR;

	public static final String NAME_PERSON_ID = "PERSON_ID";

	public static final int ID_PERSON_NAME = 4;

	public static final int TYPE_PERSON_NAME = Types.VARCHAR;

	public static final String NAME_PERSON_NAME = "PERSON_NAME";

	public static final int ID_RIGHT_GRP_CODE = 5;

	public static final int TYPE_RIGHT_GRP_CODE = Types.VARCHAR;

	public static final String NAME_RIGHT_GRP_CODE = "RIGHT_GRP_CODE";

	public static final int ID_PERSON_ACTIVITY_STATE = 6;

	public static final int TYPE_PERSON_ACTIVITY_STATE = Types.VARCHAR;

	public static final String NAME_PERSON_ACTIVITY_STATE = "PERSON_ACTIVITY_STATE";

	public static final int ID_LAST_RIGHT_GRP = 7;

	public static final int TYPE_LAST_RIGHT_GRP = Types.VARCHAR;

	public static final String NAME_LAST_RIGHT_GRP = "LAST_RIGHT_GRP";

	private static final String TABLE_NAME = "RT_ACTIVITY_PERSON";

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_PERSON table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID",
			"RT_ACTIVITY_PERSON.PROCESSINST_ID",
			"RT_ACTIVITY_PERSON.ACTIVITYINST_ID",
			"RT_ACTIVITY_PERSON.PERSON_ID", "RT_ACTIVITY_PERSON.PERSON_NAME",
			"RT_ACTIVITY_PERSON.RIGHT_GRP_CODE",
			"RT_ACTIVITY_PERSON.PERSON_ACTIVITY_STATE",
			"RT_ACTIVITY_PERSON.LAST_RIGHT_GRP" };

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_PERSON table.
	 */
	private static final String[] TABLEFIELD_NAMES = {
			"ACTIVITYINST_PERSON_ID", "PROCESSINST_ID", "ACTIVITYINST_ID",
			"PERSON_ID", "PERSON_NAME", "RIGHT_GRP_CODE",
			"PERSON_ACTIVITY_STATE", "LAST_RIGHT_GRP" };

	/**
	 * Field that contains the comma separated fields of the RT_ACTIVITY_PERSON
	 * table
	 */
	private static final String ALL_FIELDS = "RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID"
			+ ",RT_ACTIVITY_PERSON.PROCESSINST_ID"
			+ ",RT_ACTIVITY_PERSON.ACTIVITYINST_ID"
			+ ",RT_ACTIVITY_PERSON.PERSON_ID"
			+ ",RT_ACTIVITY_PERSON.PERSON_NAME"
			+ ",RT_ACTIVITY_PERSON.RIGHT_GRP_CODE"
			+ ",RT_ACTIVITY_PERSON.PERSON_ACTIVITY_STATE"
			+ ",RT_ACTIVITY_PERSON.LAST_RIGHT_GRP";

	private static DbActivityInstPersonManager singleton = new DbActivityInstPersonManager();

	/**
	 * Get the RtActivityPersonManager singleton
	 * 
	 * @return RtActivityPersonManager
	 */
	synchronized public static DbActivityInstPersonManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtActivityPersonManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbActivityInstPersonManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtActivityPersonBean instance.
	 * 
	 * @return the new RtActivityPersonBean
	 */
	public DbActivityInstPerson createActivityInstPerson() {
		return new DbActivityInstPerson();
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
	 * Load a RtActivityPersonBean from the RT_ACTIVITY_PERSON using its key
	 * fields.
	 * 
	 * @return a unique RtActivityPersonBean
	 */
	public DbActivityInstPerson loadByKey(String activityinstPersonId)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM RT_ACTIVITY_PERSON WHERE RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM RT_ACTIVITY_PERSON WHERE RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstPersonId);
			DbActivityInstPerson pReturn[] = loadByPreparedStatement(ps);
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
	 * @return an array of RtActivityPersonBean
	 */
	public DbActivityInstPerson[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of RtActivityPersonBean
	 */
	public DbActivityInstPerson[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbActivityInstPerson[]) v
					.toArray(new DbActivityInstPerson[0]);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	/**
	 * retreive an array of RtActivityPersonBean given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RtActivityPersonBean table
	 */
	public DbActivityInstPerson[] loadByWhere(String where) throws SQLException {
		return loadByWhere(where, null);
	}

	/**
	 * retreive an array of RtActivityPersonBean given a sql where clausis, and
	 * a list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting RtActivityPersonBean table
	 */
	public DbActivityInstPerson[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_ACTIVITY_PERSON " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_ACTIVITY_PERSON ");
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
				log.debug(sql);
			rs = pStatement.executeQuery(sql);
			v = new java.util.ArrayList();
			while (rs.next()) {
				if (fieldList == null)
					v.add(decodeRow(rs));
				else
					v.add(decodeRow(rs, fieldList));
			}

			return (DbActivityInstPerson[]) v
					.toArray(new DbActivityInstPerson[0]);
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
	public int deleteByKey(String activityinstPersonId) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("DELETE from RT_ACTIVITY_PERSON WHERE RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from RT_ACTIVITY_PERSON WHERE RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstPersonId);
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
	 *            the RtActivityPersonBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(DbActivityInstPerson pObject) throws SQLException {
		if (pObject.isActivityinstPersonIdInitialized() == true)
			return deleteByKey(pObject.getActivityInstPersonId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM RT_ACTIVITY_PERSON WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityinstPersonIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_PERSON_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isActivityinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSON_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonNameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSON_NAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRightGrpCodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RIGHT_GRP_CODE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonActivityStateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSON_ACTIVITY_STATE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isLastRightGrpInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LAST_RIGHT_GRP").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityinstPersonIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstPersonId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isPersonIdInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			if (pObject.isPersonNameInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonName());
			if (pObject.isRightGrpCodeInitialized())
				ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
			if (pObject.isPersonActivityStateInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonActivityState().getType());
			if (pObject.isLastRightGrpInitialized())
				ps.setString(++_dirtyCount, pObject.getLastRightGrp().getType());
			int _rows = ps.executeUpdate();
			return _rows;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_ACTIVITY_PERSON table by where clausis
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
			String delByWhereSQL = "DELETE FROM RT_ACTIVITY_PERSON " + where;
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
	 * Save into the database an array of RtActivityPersonBean pObjects
	 * 
	 * @param pObjects
	 *            the RtActivityPersonBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public DbActivityInstPerson[] save(DbActivityInstPerson[] pObjects)
			throws SQLException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	void beforeInsert(DbActivityInstPerson pObject) throws SQLException {
	}

	/**
	 * after the save of the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	void afterInsert(DbActivityInstPerson pObject) throws SQLException {
	}

	/**
	 * before the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void beforeUpdate(DbActivityInstPerson pObject) throws SQLException {
	}

	/**
	 * after the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void afterUpdate(DbActivityInstPerson pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	public DbActivityInstPerson save(DbActivityInstPerson pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		
		
		try {
			c = getConnection();
			
					if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into RT_ACTIVITY_PERSON (");
				if (pObject.isActivityinstPersonIdModified()) {
					_sql.append("ACTIVITYINST_PERSON_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessinstIdModified()) {
					_sql.append("PROCESSINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isActivityinstIdModified()) {
					_sql.append("ACTIVITYINST_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isPersonIdModified()) {
					_sql.append("PERSON_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isPersonNameModified()) {
					_sql.append("PERSON_NAME").append(",");
					_dirtyCount++;
				}
				if (pObject.isRightGrpCodeModified()) {
					_sql.append("RIGHT_GRP_CODE").append(",");
					_dirtyCount++;
				}
				if (pObject.isPersonActivityStateModified()) {
					_sql.append("PERSON_ACTIVITY_STATE").append(",");
					_dirtyCount++;
				}
				if (pObject.isLastRightGrpModified()) {
					_sql.append("LAST_RIGHT_GRP").append(",");
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
				if (pObject.isActivityinstPersonIdModified())
					ps.setString(++_dirtyCount, pObject
							.getActivityInstPersonId());
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isActivityinstIdModified())
					ps.setString(++_dirtyCount, pObject.getActivityInstId());
				if (pObject.isPersonIdModified())
					ps.setString(++_dirtyCount, pObject.getPersonId());
				if (pObject.isPersonNameModified())
					ps.setString(++_dirtyCount, pObject.getPersonName());
				if (pObject.isRightGrpCodeModified())
					ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
				if (pObject.isPersonActivityStateModified())
					ps.setString(++_dirtyCount, pObject
							.getPersonActivityState().getType());
				if (pObject.isLastRightGrpModified())
					ps.setString(++_dirtyCount, pObject.getLastRightGrp().getType());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				pObject.setIsNew(false);
				pObject.resetIsModified();
				afterInsert(pObject);
			} else { // UPDATE
			// preupdate+

				// preupdate-
				if (pObject.isModified()){
				
				

				_sql = new StringBuffer("UPDATE RT_ACTIVITY_PERSON SET ");
				if (pObject.isActivityinstPersonIdModified())
					_sql.append("ACTIVITYINST_PERSON_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isActivityinstIdModified())
					_sql.append("ACTIVITYINST_ID").append("=?,");
				if (pObject.isPersonIdModified())
					_sql.append("PERSON_ID").append("=?,");
				if (pObject.isPersonNameModified())
					_sql.append("PERSON_NAME").append("=?,");
				if (pObject.isRightGrpCodeModified())
					_sql.append("RIGHT_GRP_CODE").append("=?,");
				if (pObject.isPersonActivityStateModified())
					_sql.append("PERSON_ACTIVITY_STATE").append("=?,");
				if (pObject.isLastRightGrpModified())
					_sql.append("LAST_RIGHT_GRP").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("RT_ACTIVITY_PERSON.ACTIVITYINST_PERSON_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isActivityinstPersonIdModified()) {
					if (pObject.getActivityInstPersonId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getActivityInstPersonId());
				}
				if (pObject.isProcessinstIdModified()) {
					if (pObject.getProcessInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
				}
				if (pObject.isActivityinstIdModified()) {
					if (pObject.getActivityInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, pObject
										.getActivityInstId());
				}
				if (pObject.isPersonIdModified()) {
					if (pObject.getPersonId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getPersonId());
				}
				if (pObject.isPersonNameModified()) {
					if (pObject.getPersonName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getPersonName());
				}
				if (pObject.isRightGrpCodeModified()) {
					if (pObject.getRightGrpCode() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
				}
				if (pObject.isPersonActivityStateModified()) {
					if (pObject.getPersonActivityState() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getPersonActivityState().getType());
				}
				if (pObject.isLastRightGrpModified()) {
					if (pObject.getLastRightGrp() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getLastRightGrp().getType());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getActivityInstPersonId());
				ps.executeUpdate();
				pObject.resetIsModified();
				// postupdate+

				// postupdate-
				afterUpdate(pObject);
			 }
			}
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_ACTIVITY_PERSON on a
	 * RtActivityPersonBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstPerson decodeRow(ResultSet rs) throws SQLException {
		DbActivityInstPerson pObject = createActivityInstPerson();
		if (rs.getObject(1) != null)
			pObject.setActivityInstPersonId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setActivityInstId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setPersonId(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setPersonName(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setRightGrpCode(RightGroupEnums.fromType(rs.getString(6)));
		if (rs.getObject(7) != null)
			pObject.setPersonActivityState(RightPerformStatus.fromType(rs.getString(7)));
		if (rs.getObject(8) != null)
			pObject.setLastRightGrp(RightGroupEnums.fromType(rs.getString(8)));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_ACTIVITY_PERSON on a
	 * RtActivityPersonBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstPerson decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityInstPerson pObject = createActivityInstPerson();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYINST_PERSON_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstPersonId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_ACTIVITYINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstId(rs.getString(pos));
				}
				break;
			case ID_PERSON_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPersonId(rs.getString(pos));
				}
				break;
			case ID_PERSON_NAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPersonName(rs.getString(pos));
				}
				break;
			case ID_RIGHT_GRP_CODE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRightGrpCode(RightGroupEnums.fromType(rs.getString(pos)));
				}
				break;
			case ID_PERSON_ACTIVITY_STATE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPersonActivityState(RightPerformStatus.fromType(rs.getString(pos)));
				}
				break;
			case ID_LAST_RIGHT_GRP:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLastRightGrp(RightGroupEnums.fromType(rs.getString(pos)));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique RtActivityPersonBean pObject from an template one giving a
	 * c
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to look for
	 * @return the pObject matching the template
	 */
	public DbActivityInstPerson loadObject(DbActivityInstPerson pObject)
			throws SQLException {
		DbActivityInstPerson[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new SQLException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of RtActivityPersonBean from a template one
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean template to look for
	 * @return all the RtActivityPersonBean matching the template
	 */
	public DbActivityInstPerson[] loadObjects(DbActivityInstPerson pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_ACTIVITY_PERSON WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityinstPersonIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_PERSON_ID= ?");
			}
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isActivityinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_ID= ?");
			}
			if (pObject.isPersonIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSON_ID= ?");
			}
			if (pObject.isPersonNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSON_NAME= ?");
			}
			if (pObject.isRightGrpCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RIGHT_GRP_CODE= ?");
			}
			if (pObject.isPersonActivityStateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSON_ACTIVITY_STATE= ?");
			}
			if (pObject.isLastRightGrpModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LAST_RIGHT_GRP= ?");
			}
			if (_dirtyCount == 0)
				throw new SQLException(
						"The pObject to look for is invalid : not initialized !");
			_sql.append(_sqlWhere);
			if (log.isDebugEnabled())
				log.debug(_sql.toString());
			c = getConnection();
			ps = c.prepareStatement(_sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			_dirtyCount = 0;
			if (pObject.isActivityinstPersonIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstPersonId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isPersonIdModified())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			if (pObject.isPersonNameModified())
				ps.setString(++_dirtyCount, pObject.getPersonName());
			if (pObject.isRightGrpCodeModified())
				ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
			if (pObject.isPersonActivityStateModified())
				ps.setString(++_dirtyCount, pObject.getPersonActivityState().getType());
			if (pObject.isLastRightGrpModified())
				ps.setString(++_dirtyCount, pObject.getLastRightGrp().getType());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public void copy(String srcActId, String dstActId) throws SQLException {
		// String sqlInsert = "INSERT INTO RT_ACTIVITY_PERSON " +
		// " ( " + ALL_FIELDS + " ) " +
		// " VALUES ( " +
		// " ?," +
		// " ?," +
		// " ?," +
		// " ?," +
		// " ?," +
		// " ?," +
		// " ?," +
		// " ? " +
		// " ) ";

		String sqlWhere = " WHERE ACTIVITYINST_ID = '" + srcActId + "'";

		DbActivityInstPerson[] actPersons = loadByWhere(sqlWhere, null);
		for (int i = 0; i < actPersons.length; i++) {
			DbActivityInstPerson person = createActivityInstPerson();
			// set it to new person to insert instead of update
			person.copy(actPersons[i]);
			person.setActivityInstId(dstActId);
			person.setActivityInstPersonId(UUID.randomUUID().toString());
			save(person);
		}

	}
}


