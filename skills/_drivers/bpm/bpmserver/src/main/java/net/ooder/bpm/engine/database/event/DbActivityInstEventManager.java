
package net.ooder.bpm.engine.database.event;

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


public class DbActivityInstEventManager implements Serializable{
	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstEventManager.class);

	public static final int ID_ACTIVITYINST_EVENT_ID = 0;

	public static final int TYPE_ACTIVITYINST_EVENT_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_EVENT_ID = "ACTIVITYINST_EVENT_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ACTIVITYINST_ID = 2;

	public static final int TYPE_ACTIVITYINST_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_ID = "ACTIVITYINST_ID";

	public static final int ID_ENDPOINT_ID = 3;

	public static final int TYPE_ENDPOINT_ID = Types.VARCHAR;

	public static final String NAME_ENDPOINT_ID = "ENDPOINT_ID";

	public static final int ID_ENDPOINT_NAME = 4;

	public static final int TYPE_ENDPOINT_NAME = Types.VARCHAR;

	public static final String NAME_ENDPOINT_NAME = "ENDPOINT_NAME";

	public static final int ID_DEVICE_GRP_CODE = 5;

	public static final int TYPE_DEVICE_GRP_CODE = Types.VARCHAR;

	public static final String NAME_DEVICE_GRP_CODE = "DEVICE_GRP_CODE";

	public static final int ID_EVENT_ACTIVITY_STATE = 6;

	public static final int TYPE_EVENT_ACTIVITY_STATE = Types.VARCHAR;

	public static final String NAME_EVENT_ACTIVITY_STATE = "EVENT_ACTIVITY_STATE";

	public static final int ID_LAST_EVENT_GRP = 7;

	public static final int TYPE_LAST_EVENT_GRP = Types.VARCHAR;

	public static final String NAME_LAST_EVENT_GRP = "LAST_EVENT_GRP";
	
	public static final int ID_SERVICE_ID = 8;

	public static final int TYPE_SERVICE_ID = Types.VARCHAR;

	public static final String NAME_SERVICE_ID = "SERVICE_ID";
	
	
	public static final int ID_PERSON_ID = 9;

	public static final int TYPE_PERSON_ID = Types.VARCHAR;

	public static final String NAME_PERSON_ID = "PERSON_ID";

	private static final String TABLE_NAME = "RT_ACTIVITY_EVENT";

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_EVENT table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID",
			"RT_ACTIVITY_EVENT.PROCESSINST_ID",
			"RT_ACTIVITY_EVENT.ACTIVITYINST_ID",
			"RT_ACTIVITY_EVENT.ENDPOINT_ID", "RT_ACTIVITY_EVENT.ENDPOINT_NAME",
			"RT_ACTIVITY_EVENT.DEVICE_GRP_CODE",
			"RT_ACTIVITY_EVENT.EVENT_ACTIVITY_STATE",
			"RT_ACTIVITY_EVENT.LAST_EVENT_GRP" ,
			"RT_ACTIVITY_EVENT.SERVICE_ID",
			"RT_ACTIVITY_EVENT.PERSON_ID"
			};

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_EVENT table.
	 */
	private static final String[] TABLEFIELD_NAMES = {
			"ACTIVITYINST_EVENT_ID", "PROCESSINST_ID", "ACTIVITYINST_ID",
			"ENDPOINT_ID", "ENDPOINT_NAME", "DEVICE_GRP_CODE",
			"EVENT_ACTIVITY_STATE", "LAST_EVENT_GRP","SERVICE_ID","PERSON_ID" };

	/**
	 * Field that contains the comma separated fields of the RT_ACTIVITY_EVENT
	 * table
	 */
	private static final String ALL_FIELDS = "RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID"
			+ ",RT_ACTIVITY_EVENT.PROCESSINST_ID"
			+ ",RT_ACTIVITY_EVENT.ACTIVITYINST_ID"
			+ ",RT_ACTIVITY_EVENT.ENDPOINT_ID"
			+ ",RT_ACTIVITY_EVENT.ENDPOINT_NAME"
			+ ",RT_ACTIVITY_EVENT.DEVICE_GRP_CODE"
			+ ",RT_ACTIVITY_EVENT.EVENT_ACTIVITY_STATE"
			+ ",RT_ACTIVITY_EVENT.LAST_EVENT_GRP"
	                + ",RT_ACTIVITY_EVENT.SERVICE_ID"
	                + ",RT_ACTIVITY_EVENT.PERSON_ID";

	private static DbActivityInstEventManager singleton = new DbActivityInstEventManager();

	/**
	 * Get the RtActivityPersonManager singleton
	 * 
	 * @return RtActivityPersonManager
	 */
	synchronized public static DbActivityInstEventManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtActivityPersonManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbActivityInstEventManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtActivityPersonBean instance.
	 * 
	 * @return the new RtActivityPersonBean
	 */
	public DbActivityInstEvent createActivityInstEvent() {
		return new DbActivityInstEvent();
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
	 * Load a RtActivityPersonBean from the RT_ACTIVITY_EVENT using its key
	 * fields.
	 * 
	 * @return a unique RtActivityPersonBean
	 */
	public DbActivityInstEvent loadByKey(String activityinstEndPointId)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM RT_ACTIVITY_EVENT WHERE RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM RT_ACTIVITY_EVENT WHERE RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstEndPointId);
			DbActivityInstEvent pReturn[] = loadByPreparedStatement(ps);
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
	public DbActivityInstEvent[] loadByPreparedStatement(PreparedStatement ps)
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
	public DbActivityInstEvent[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbActivityInstEvent[]) v
					.toArray(new DbActivityInstEvent[0]);
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
	public DbActivityInstEvent[] loadByWhere(String where) throws SQLException {
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
	public DbActivityInstEvent[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_ACTIVITY_EVENT " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_ACTIVITY_EVENT ");
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

			return (DbActivityInstEvent[]) v
					.toArray(new DbActivityInstEvent[0]);
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
	public int deleteByKey(String activityinstEndPointId) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("DELETE from RT_ACTIVITY_EVENT WHERE RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from RT_ACTIVITY_EVENT WHERE RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstEndPointId);
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
	public int delete(DbActivityInstEvent pObject) throws SQLException {
		if (pObject.isActivityinstEventIdInitialized() == true)
			return deleteByKey(pObject.getActivityInstEndPointId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM RT_ACTIVITY_EVENT WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityinstEventIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_EVENT_ID").append("=?");
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
			if (pObject.isEndPointIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ENDPOINT_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isEndPointNameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ENDPOINT_NAME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isEventGrpCodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEVICE_GRP_CODE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isEventActivityStateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("EVENT_ACTIVITY_STATE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isLastEventGrpInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LAST_EVENT_GRP").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isServiceIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SERVICE_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isPersonIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PERSON_ID").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityinstEventIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstEndPointId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isEndPointIdInitialized())
				ps.setString(++_dirtyCount, pObject.getEndPointId());
			if (pObject.isEndPointNameInitialized())
				ps.setString(++_dirtyCount, pObject.getEndPointName());
			if (pObject.isEventGrpCodeInitialized())
				ps.setString(++_dirtyCount, pObject.getEventGrpCode().getType());
			if (pObject.isEventActivityStateInitialized())
				ps.setString(++_dirtyCount, pObject.getEventActivityState().getType());
			if (pObject.isLastEventGrpInitialized())
				ps.setString(++_dirtyCount, pObject.getLastEventGrp().getType());
			if (pObject.isServiceIdInitialized())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isPersonIdInitialized())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			int _rows = ps.executeUpdate();
			return _rows;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_ACTIVITY_EVENT table by where clausis
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
			String delByWhereSQL = "DELETE FROM RT_ACTIVITY_EVENT " + where;
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
	public DbActivityInstEvent[] save(DbActivityInstEvent[] pObjects)
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
	void beforeInsert(DbActivityInstEvent pObject) throws SQLException {
	}

	/**
	 * after the save of the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	void afterInsert(DbActivityInstEvent pObject) throws SQLException {
	}

	/**
	 * before the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void beforeUpdate(DbActivityInstEvent pObject) throws SQLException {
	}

	/**
	 * after the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void afterUpdate(DbActivityInstEvent pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	public DbActivityInstEvent save(DbActivityInstEvent pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		
		
		try {
			c = getConnection();
			
					if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into RT_ACTIVITY_EVENT (");
				if (pObject.isActivityinstEventIdModified()) {
					_sql.append("ACTIVITYINST_EVENT_ID").append(",");
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
				if (pObject.isEndPointIdModified()) {
					_sql.append("ENDPOINT_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isEndPointNameModified()) {
					_sql.append("ENDPOINT_NAME").append(",");
					_dirtyCount++;
				}
				if (pObject.isEventGrpCodeModified()) {
					_sql.append("DEVICE_GRP_CODE").append(",");
					_dirtyCount++;
				}
				if (pObject.isEventActivityStateModified()) {
					_sql.append("EVENT_ACTIVITY_STATE").append(",");
					_dirtyCount++;
				}
				if (pObject.isLastEventGrpModified()) {
					_sql.append("LAST_EVENT_GRP").append(",");
					_dirtyCount++;
				}
				if (pObject.isServiceIdModified()) {
					_sql.append("SERVICE_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isPersonIdModified()) {
					_sql.append("PERSON_ID").append(",");
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
				if (pObject.isActivityinstEventIdModified())
					ps.setString(++_dirtyCount, pObject
							.getActivityInstEndPointId());
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isActivityinstIdModified())
					ps.setString(++_dirtyCount, pObject.getActivityInstId());
				if (pObject.isEndPointIdModified())
					ps.setString(++_dirtyCount, pObject.getEndPointId());
				if (pObject.isEndPointNameModified())
					ps.setString(++_dirtyCount, pObject.getEndPointName());
				if (pObject.isEventGrpCodeModified())
					ps.setString(++_dirtyCount, pObject.getEventGrpCode().getType());
				if (pObject.isEventActivityStateModified())
					ps.setString(++_dirtyCount, pObject
							.getEventActivityState().getType());
				if (pObject.isLastEventGrpModified())
					ps.setString(++_dirtyCount, pObject.getLastEventGrp().getType());
				if (pObject.isServiceIdModified())
					ps.setString(++_dirtyCount, pObject.getServiceId());
				if (pObject.isPersonIdModified())
					ps.setString(++_dirtyCount, pObject.getPersonId());
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
				
				

				_sql = new StringBuffer("UPDATE RT_ACTIVITY_EVENT SET ");
				if (pObject.isActivityinstEventIdModified())
					_sql.append("ACTIVITYINST_EVENT_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isActivityinstIdModified())
					_sql.append("ACTIVITYINST_ID").append("=?,");
				if (pObject.isEndPointIdModified())
					_sql.append("ENDPOINT_ID").append("=?,");
				if (pObject.isEndPointNameModified())
					_sql.append("ENDPOINT_NAME").append("=?,");
				if (pObject.isEventGrpCodeModified())
					_sql.append("DEVICE_GRP_CODE").append("=?,");
				if (pObject.isEventActivityStateModified())
					_sql.append("EVENT_ACTIVITY_STATE").append("=?,");
				if (pObject.isLastEventGrpModified())
					_sql.append("LAST_EVENT_GRP").append("=?,");
				if (pObject.isServiceIdModified())
					_sql.append("SERVICE_ID").append("=?,");
				if (pObject.isServiceIdModified())
					_sql.append("PERSON_ID").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("RT_ACTIVITY_EVENT.ACTIVITYINST_EVENT_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isActivityinstEventIdModified()) {
					if (pObject.getActivityInstEndPointId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getActivityInstEndPointId());
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
				if (pObject.isEndPointIdModified()) {
					if (pObject.getEndPointId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getEndPointId());
				}
				if (pObject.isEndPointNameModified()) {
					if (pObject.getEndPointName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getEndPointName());
				}
				if (pObject.isEventGrpCodeModified()) {
					if (pObject.getEventGrpCode() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getEventGrpCode().getType());
				}
				if (pObject.isEventActivityStateModified()) {
					if (pObject.getEventActivityState() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getEventActivityState().getType());
				}
				if (pObject.isLastEventGrpModified()) {
					if (pObject.getLastEventGrp() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getLastEventGrp().getType());
				}
				if (pObject.isServiceIdModified()) {
					if (pObject.getLastEventGrp() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getServiceId());
				}
				if (pObject.isPersonIdModified()) {
					if (pObject.getPersonId()== null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getPersonId());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getActivityInstEndPointId());
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
	 * Transform a ResultSet iterating on the RT_ACTIVITY_EVENT on a
	 * RtActivityPersonBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstEvent decodeRow(ResultSet rs) throws SQLException {
		DbActivityInstEvent pObject = createActivityInstEvent();
		if (rs.getObject(1) != null)
			pObject.setActivityInstEndPointId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setActivityInstId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setEndPointId(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setEndPointName(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setEventGrpCode( RightGroupEnums.fromType(rs.getString(6)));
		if (rs.getObject(7) != null)
			pObject.setEventActivityState(RightPerformStatus.fromType(rs.getString(7)));
		if (rs.getObject(8) != null)
			pObject.setLastEventGrp( RightGroupEnums.fromType(rs.getString(8)));
		if (rs.getObject(9) != null)
			pObject.setServiceId(rs.getString(9));
		if (rs.getObject(10) != null)
			pObject.setPersonId(rs.getString(10));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_ACTIVITY_EVENT on a
	 * RtActivityPersonBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstEvent decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityInstEvent pObject = createActivityInstEvent();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYINST_EVENT_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstEndPointId(rs.getString(pos));
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
			case ID_ENDPOINT_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEndPointId(rs.getString(pos));
				}
				break;
			case ID_ENDPOINT_NAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEndPointName(rs.getString(pos));
				}
				break;
			case ID_DEVICE_GRP_CODE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEventGrpCode( RightGroupEnums.fromType(rs.getString(pos)));
				}
				break;
			case ID_EVENT_ACTIVITY_STATE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEventActivityState(RightPerformStatus.fromType(rs.getString(pos)));
				}
				break;
			case ID_LAST_EVENT_GRP:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLastEventGrp( RightGroupEnums.fromType(rs.getString(pos)));
				}
			case ID_SERVICE_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setServiceId(rs.getString(pos));
				}
				break;
			case ID_PERSON_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setPersonId(rs.getString(pos));
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
	public DbActivityInstEvent loadObject(DbActivityInstEvent pObject)
			throws SQLException {
		DbActivityInstEvent[] pReturn = loadObjects(pObject);
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
	public DbActivityInstEvent[] loadObjects(DbActivityInstEvent pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_ACTIVITY_EVENT WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityinstEventIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_EVENT_ID= ?");
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
			if (pObject.isEndPointIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ENDPOINT_ID= ?");
			}
			if (pObject.isEndPointNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ENDPOINT_NAME= ?");
			}
			if (pObject.isEventGrpCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEVICE_GRP_CODE= ?");
			}
			if (pObject.isEventActivityStateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("EVENT_ACTIVITY_STATE= ?");
			}
			if (pObject.isLastEventGrpModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LAST_EVENT_GRP= ?");
			}
			if (pObject.isServiceIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SERVICE_ID= ?");
			}
			if (pObject.isPersonIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PERSON_ID= ?");
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
			if (pObject.isActivityinstEventIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstEndPointId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isEndPointIdModified())
				ps.setString(++_dirtyCount, pObject.getEndPointId());
			if (pObject.isEndPointNameModified())
				ps.setString(++_dirtyCount, pObject.getEndPointName());
			if (pObject.isEventGrpCodeModified())
				ps.setString(++_dirtyCount, pObject.getEventGrpCode().getType());
			if (pObject.isEventActivityStateModified())
				ps.setString(++_dirtyCount, pObject.getEventActivityState().getType());
			if (pObject.isLastEventGrpModified())
				ps.setString(++_dirtyCount, pObject.getLastEventGrp().getType());
			if (pObject.isServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isPersonIdModified())
				ps.setString(++_dirtyCount, pObject.getPersonId());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public void copy(String srcActId, String dstActId) throws SQLException {
		// String sqlInsert = "INSERT INTO RT_ACTIVITY_EVENT " +
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

		DbActivityInstEvent[] actPersons = loadByWhere(sqlWhere, null);
		for (int i = 0; i < actPersons.length; i++) {
			DbActivityInstEvent person = createActivityInstEvent();
			// set it to new person to insert instead of update
			person.copy(actPersons[i]);
			person.setActivityInstId(dstActId);
			person.setActivityInstEndPointId(UUID.randomUUID().toString());
			save(person);
		}

	}
}


