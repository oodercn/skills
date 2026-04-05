/**
 * $RCSfile: DbActivityHistoryPersonManager.java,v $
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

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动历史相关人员管理
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
public class DbActivityHistoryPersonManager implements Serializable{
	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityHistoryPersonManager.class);

	public static final int ID_ACTIVITYINST_HISTORY_ID = 0;

	public static final int TYPE_ACTIVITYINST_HISTORY_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_HISTORY_ID = "ACTIVITYINST_HISTORY_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_PERSON_ID = 2;

	public static final int TYPE_PERSON_ID = Types.VARCHAR;

	public static final String NAME_PERSON_ID = "PERSON_ID";

	public static final int ID_PERSONNAME = 3;

	public static final int TYPE_PERSONNAME = Types.VARCHAR;

	public static final String NAME_PERSONNAME = "PERSONNAME";

	public static final int ID_RIGHT_GRP_CODE = 4;

	public static final int TYPE_RIGHT_GRP_CODE = Types.VARCHAR;

	public static final String NAME_RIGHT_GRP_CODE = "RIGHT_GRP_CODE";

	private static final String TABLE_NAME = "RT_ACTIVITYHISTORY_PERSON";

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITYHISTORY_PERSON table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_ACTIVITYHISTORY_PERSON.ACTIVITYINST_HISTORY_ID",
			"RT_ACTIVITYHISTORY_PERSON.PROCESSINST_ID",
			"RT_ACTIVITYHISTORY_PERSON.PERSON_ID",
			"RT_ACTIVITYHISTORY_PERSON.PERSONNAME",
			"RT_ACTIVITYHISTORY_PERSON.RIGHT_GRP_CODE" };

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITYHISTORY_PERSON table.
	 */
	private static final String[] TABLEFIELD_NAMES = {
			"ACTIVITYINST_HISTORY_ID", "PROCESSINST_ID", "PERSON_ID",
			"PERSONNAME", "RIGHT_GRP_CODE" };

	/**
	 * Field that contains the comma separated fields of the
	 * RT_ACTIVITYHISTORY_PERSON table
	 */
	private static final String ALL_FIELDS = "RT_ACTIVITYHISTORY_PERSON.ACTIVITYINST_HISTORY_ID"
			+ ",RT_ACTIVITYHISTORY_PERSON.PROCESSINST_ID"
			+ ",RT_ACTIVITYHISTORY_PERSON.PERSON_ID"
			+ ",RT_ACTIVITYHISTORY_PERSON.PERSONNAME"
			+ ",RT_ACTIVITYHISTORY_PERSON.RIGHT_GRP_CODE";

	private static DbActivityHistoryPersonManager singleton = new DbActivityHistoryPersonManager();

	/**
	 * Get the RtActivityhistoryPersonManager singleton
	 * 
	 * @return RtActivityhistoryPersonManager
	 */
	synchronized public static DbActivityHistoryPersonManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtActivityhistoryPersonManager instance, this is optional.
	 * By default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbActivityHistoryPersonManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtActivityhistoryPersonBean instance.
	 * 
	 * @return the new RtActivityhistoryPersonBean
	 */
	public DbActivityHistoryPerson createActivityHistoryPerson() {
		return new DbActivityHistoryPerson();
	}

	/**
	 * Creates a new RtActivityhistoryPersonBean instance.
	 * 
	 * @return the new RtActivityhistoryPersonBean
	 */
	public DbActivityHistoryPerson createActivityHistoryPerson(
			DbActivityInstPerson instPerson) {
		DbActivityHistoryPerson historyPerson = createActivityHistoryPerson();
		historyPerson.setPersonId(instPerson.getPersonId());
		historyPerson.setPersonName(instPerson.getPersonName());
		historyPerson.setProcessInstId(instPerson.getProcessInstId());
		historyPerson.setRightGrpCode(instPerson.getRightGrpCode());

		return historyPerson;

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

	// ////////////////////////////////////
	// PREPARED STATEMENTRESTRICTION LOADER
	// ////////////////////////////////////

	/**
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of RtActivityhistoryPersonBean
	 */
	public DbActivityHistoryPerson[] loadByPreparedStatement(
			PreparedStatement ps) throws SQLException {
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
	 * @return an array of RtActivityhistoryPersonBean
	 */
	public DbActivityHistoryPerson[] loadByPreparedStatement(
			PreparedStatement ps, int[] fieldList) throws SQLException {
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
			return (DbActivityHistoryPerson[]) v
					.toArray(new DbActivityHistoryPerson[0]);
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
	 * retreive an array of RtActivityhistoryPersonBean given a sql where
	 * clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RtActivityhistoryPersonBean table
	 */
	// 49
	public DbActivityHistoryPerson[] loadByWhere(String where)
			throws SQLException {
		return loadByWhere(where, null);
	}

	/**
	 * retreive an array of RtActivityhistoryPersonBean given a sql where
	 * clausis, and a list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting RtActivityhistoryPersonBean table
	 */
	// 51
	public DbActivityHistoryPerson[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_ACTIVITYHISTORY_PERSON "
					+ where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_ACTIVITYHISTORY_PERSON ");
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

			return (DbActivityHistoryPerson[]) v
					.toArray(new DbActivityHistoryPerson[0]);
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
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(DbActivityHistoryPerson pObject) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer(
					"DELETE FROM RT_ACTIVITYHISTORY_PERSON WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityinstHistoryIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_HISTORY_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSON_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonnameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSONNAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRightGrpCodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RIGHT_GRP_CODE").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityinstHistoryIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstHistoryId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isPersonIdInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			if (pObject.isPersonnameInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonName());
			if (pObject.isRightGrpCodeInitialized())
				ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
			int _rows = ps.executeUpdate();
			return _rows;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_ACTIVITYHISTORY_PERSON table by where clausis
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
			String delByWhereSQL = "DELETE FROM RT_ACTIVITYHISTORY_PERSON "
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
	 * Save into the database an array of RtActivityhistoryPersonBean pObjects
	 * 
	 * @param pObjects
	 *            the RtActivityhistoryPersonBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public DbActivityHistoryPerson[] save(DbActivityHistoryPerson[] pObjects)
			throws SQLException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the RtActivityhistoryPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to be saved
	 */
	void beforeInsert(DbActivityHistoryPerson pObject) throws SQLException {
		

		
	}

	/**
	 * after the save of the RtActivityhistoryPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to be saved
	 */
	void afterInsert(DbActivityHistoryPerson pObject) throws SQLException {
	}

	/**
	 * before the update of the RtActivityhistoryPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to be updated
	 */
	void beforeUpdate(DbActivityHistoryPerson pObject) throws SQLException {
	}

	/**
	 * after the update of the RtActivityhistoryPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to be updated
	 */
	void afterUpdate(DbActivityHistoryPerson pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtActivityhistoryPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to be saved
	 */
	public DbActivityHistoryPerson save(DbActivityHistoryPerson pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
	
		try {
			c = getConnection();
			
			
			
			
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				//
				
				StringBuffer where = new StringBuffer();
				where.append("  where ACTIVITYINST_HISTORY_ID='");
				where.append(pObject.getActivityInstHistoryId()+"' AND PERSON_ID='"+pObject.getPersonId()+"'");
				where.append(" AND RIGHT_GRP_CODE='"+pObject.getRightGrpCode()+"'");
		
				DbActivityHistoryPerson[] dbActivityHistoryPersons=this.loadByWhere(where.toString());
				if (dbActivityHistoryPersons.length>0){
					pObject= dbActivityHistoryPersons[0];
				}else{
					
					int _dirtyCount = 0;
					_sql = new StringBuffer(
							"INSERT into RT_ACTIVITYHISTORY_PERSON (");
					if (pObject.isActivityinstHistoryIdModified()) {
						_sql.append("ACTIVITYINST_HISTORY_ID").append(",");
						_dirtyCount++;
					}
					if (pObject.isProcessinstIdModified()) {
						_sql.append("PROCESSINST_ID").append(",");
						_dirtyCount++;
					}
					if (pObject.isPersonIdModified()) {
						_sql.append("PERSON_ID").append(",");
						_dirtyCount++;
					}
					if (pObject.isPersonnameModified()) {
						_sql.append("PERSONNAME").append(",");
						_dirtyCount++;
					}
					if (pObject.isRightGrpCodeModified()) {
						_sql.append("RIGHT_GRP_CODE").append(",");
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
					if (pObject.isActivityinstHistoryIdModified())
						ps.setString(++_dirtyCount, pObject
								.getActivityInstHistoryId());
					if (pObject.isProcessinstIdModified())
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
					if (pObject.isPersonIdModified())
						ps.setString(++_dirtyCount, pObject.getPersonId());
					if (pObject.isPersonnameModified())
						ps.setString(++_dirtyCount, pObject.getPersonName());
					if (pObject.isRightGrpCodeModified())
						ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
					ps.executeUpdate();
					// postinsert+

					// postinsert-
					pObject.setIsNew(false);
					pObject.resetIsModified();
					afterInsert(pObject);
				}
			
			} else { // UPDATE
			// preupdate+

				// preupdate-
				beforeUpdate(pObject);
				_sql = new StringBuffer("UPDATE RT_ACTIVITYHISTORY_PERSON SET ");
				if (pObject.isActivityinstHistoryIdModified())
					_sql.append("ACTIVITYINST_HISTORY_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isPersonIdModified())
					_sql.append("PERSON_ID").append("=?,");
				if (pObject.isPersonnameModified())
					_sql.append("PERSONNAME").append("=?,");
				if (pObject.isRightGrpCodeModified())
					_sql.append("RIGHT_GRP_CODE").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append("");
				_sql.append("  where ACTIVITYINST_HISTORY_ID='");
				_sql.append(pObject.getActivityInstHistoryId()+"' AND PERSON_ID='"+pObject.getPersonId()+"'");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isActivityinstHistoryIdModified()) {
					if (pObject.getActivityInstHistoryId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getActivityInstHistoryId());
				}
				if (pObject.isProcessinstIdModified()) {
					if (pObject.getProcessInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessInstId());
				}
				if (pObject.isPersonIdModified()) {
					if (pObject.getPersonId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getPersonId());
				}
				if (pObject.isPersonnameModified()) {
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
				if (_dirtyCount == 0)
					return pObject;
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
	 * Transform a ResultSet iterating on the RT_ACTIVITYHISTORY_PERSON on a
	 * RtActivityhistoryPersonBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtActivityhistoryPersonBean pObject
	 */
	public DbActivityHistoryPerson decodeRow(ResultSet rs) throws SQLException {
		DbActivityHistoryPerson pObject = createActivityHistoryPerson();
		if (rs.getObject(1) != null)
			pObject.setActivityInstHistoryId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setPersonId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setPersonName(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setRightGrpCode(RightGroupEnums.fromType(rs.getString(5)));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_ACTIVITYHISTORY_PERSON on a
	 * RtActivityhistoryPersonBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtActivityhistoryPersonBean pObject
	 */
	public DbActivityHistoryPerson decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityHistoryPerson pObject = createActivityHistoryPerson();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYINST_HISTORY_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstHistoryId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_PERSON_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPersonId(rs.getString(pos));
				}
				break;
			case ID_PERSONNAME:
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
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique RtActivityhistoryPersonBean pObject from an template one
	 * giving a c
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean pObject to look for
	 * @return the pObject matching the template
	 */
	public DbActivityHistoryPerson loadObject(DbActivityHistoryPerson pObject)
			throws SQLException {
		DbActivityHistoryPerson[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new SQLException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of RtActivityhistoryPersonBean from a template one
	 * 
	 * @param pObject
	 *            the RtActivityhistoryPersonBean template to look for
	 * @return all the RtActivityhistoryPersonBean matching the template
	 */
	public DbActivityHistoryPerson[] loadObjects(DbActivityHistoryPerson pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_ACTIVITYHISTORY_PERSON WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityinstHistoryIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_HISTORY_ID= ?");
			}
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isPersonIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSON_ID= ?");
			}
			if (pObject.isPersonnameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSONNAME= ?");
			}
			if (pObject.isRightGrpCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RIGHT_GRP_CODE= ?");
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
			if (pObject.isActivityinstHistoryIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstHistoryId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isPersonIdModified())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			if (pObject.isPersonnameModified())
				ps.setString(++_dirtyCount, pObject.getPersonName());
			if (pObject.isRightGrpCodeModified())
				ps.setString(++_dirtyCount, pObject.getRightGrpCode().getType());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}
}


