
package net.ooder.bpm.engine.database.service;

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
 * Description: 活动实例与设备命令关系
 * </p>
 * <p>
 * 此数据因为数据量较大，且经常需要更新，所以不使用Cache
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author 李文章
 * @version 1.0
 */
public class DbActivityInstServiceManager implements Serializable{
	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstServiceManager.class);

	public static final int ID_ACTIVITYINST_SERVICE_ID = 0;

	public static final int TYPE_ACTIVITYINST_SERVICE_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_SERVICE_ID = "ACTIVITYINST_SERVICE_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ACTIVITYINST_ID = 2;

	public static final int TYPE_ACTIVITYINST_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_ID = "ACTIVITYINST_ID";

	public static final int ID_ENDPOINT_ID = 3;

	public static final int TYPE_ENDPOINT_ID = Types.VARCHAR;

	public static final String NAME_ENDPOINT_ID = "ENDPOINT_ID";

	public static final int ID_URL = 4;

	public static final int TYPE_URL = Types.VARCHAR;

	public static final String NAME_URL = "URL";

	public static final int ID_DEVICE_GRP_CODE = 5;

	public static final int TYPE_DEVICE_GRP_CODE = Types.VARCHAR;

	public static final String NAME_DEVICE_GRP_CODE = "DEVICE_GRP_CODE";

	public static final int ID_SERVICE_ACTIVITY_STATE = 6;

	public static final int TYPE_SERVICE_ACTIVITY_STATE = Types.VARCHAR;

	public static final String NAME_SERVICE_ACTIVITY_STATE = "EVENT_ACTIVITY_STATE";

	public static final int ID_LAST_SERVICE_GRP = 7;

	public static final int TYPE_LAST_SERVICE_GRP = Types.VARCHAR;

	public static final String NAME_LAST_SERVICE_GRP = "LAST_SERVICE_GRP";
	
	public static final int ID_SERVICE_ID = 8;

	public static final int TYPE_SERVICE_ID = Types.VARCHAR;

	public static final String NAME_SERVICE_ID = "SERVICE_ID";
	
	
	public static final int ID_REQUESTPARAMS = 9;

	public static final int TYPE_REQUESTPARAMS = Types.VARCHAR;

	public static final String NAME_REQUESTPARAMS = "REQUESTPARAMS";

	private static final String TABLE_NAME = "RT_ACTIVITY_SERVICE";

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_SERVICE table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID",
			"RT_ACTIVITY_SERVICE.PROCESSINST_ID",
			"RT_ACTIVITY_SERVICE.ACTIVITYINST_ID",
			"RT_ACTIVITY_SERVICE.ENDPOINT_ID", 
			"RT_ACTIVITY_SERVICE.URL",
			"RT_ACTIVITY_SERVICE.SERVICE_GRP_CODE",
			"RT_ACTIVITY_SERVICE.SERVICE_ACTIVITY_STATE",
			"RT_ACTIVITY_SERVICE.LAST_SERVICE_GRP" ,
			"RT_ACTIVITY_SERVICE.SERVICE_ID",
			"RT_ACTIVITY_SERVICE.REQUESTPARAMS"
			};

	/**
	 * create an array of string containing all the fields of the
	 * RT_ACTIVITY_SERVICE table.
	 */
	private static final String[] TABLEFIELD_NAMES = {
			"ACTIVITYINST_SERVICE_ID", "PROCESSINST_ID", "ACTIVITYINST_ID",
			"ENDPOINT_ID", "URL", "SERVICE_GRP_CODE",
			"SERVICE_ACTIVITY_STATE", "LAST_SERVICE_GRP","SERVICE_ID","REQUESTPARAMS" };

	/**
	 * Field that contains the comma separated fields of the RT_ACTIVITY_SERVICE
	 * table
	 */
	private static final String ALL_FIELDS = "RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID"
			+ ",RT_ACTIVITY_SERVICE.PROCESSINST_ID"
			+ ",RT_ACTIVITY_SERVICE.ACTIVITYINST_ID"
			+ ",RT_ACTIVITY_SERVICE.ENDPOINT_ID"
			+ ",RT_ACTIVITY_SERVICE.URL"
			+ ",RT_ACTIVITY_SERVICE.SERVICE_GRP_CODE"
			+ ",RT_ACTIVITY_SERVICE.SERVICE_ACTIVITY_STATE"
			+ ",RT_ACTIVITY_SERVICE.LAST_SERVICE_GRP"
	                + ",RT_ACTIVITY_SERVICE.SERVICE_ID"
	                + ",RT_ACTIVITY_SERVICE.REQUESTPARAMS";

	private static DbActivityInstServiceManager singleton = new DbActivityInstServiceManager();

	/**
	 * Get the RtActivityPersonManager singleton
	 * 
	 * @return RtActivityPersonManager
	 */
	synchronized public static DbActivityInstServiceManager getInstance() {
		return singleton;
	}

	/**
	 * Set your own RtActivityPersonManager instance, this is optional. By
	 * default we provide it for you.
	 */
	synchronized public static void setInstance(
			DbActivityInstServiceManager instance) {
		singleton = instance;
	}

	/**
	 * Creates a new RtActivityPersonBean instance.
	 * 
	 * @return the new RtActivityPersonBean
	 */
	public DbActivityInstService createActivityInstService() {
		return new DbActivityInstService();
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
	 * Load a RtActivityPersonBean from the RT_ACTIVITY_SERVICE using its key
	 * fields.
	 * 
	 * @return a unique RtActivityPersonBean
	 */
	public DbActivityInstService loadByKey(String activityinstServiceId)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("SELECT "
								+ ALL_FIELDS
								+ " FROM RT_ACTIVITY_SERVICE WHERE RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM RT_ACTIVITY_SERVICE WHERE RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstServiceId);
			DbActivityInstService pReturn[] = loadByPreparedStatement(ps);
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
	public DbActivityInstService[] loadByPreparedStatement(PreparedStatement ps)
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
	public DbActivityInstService[] loadByPreparedStatement(PreparedStatement ps,
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
			return (DbActivityInstService[]) v
					.toArray(new DbActivityInstService[0]);
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
	public DbActivityInstService[] loadByWhere(String where) throws SQLException {
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
	public DbActivityInstService[] loadByWhere(String where, int[] fieldList)
			throws SQLException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_ACTIVITY_SERVICE " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_ACTIVITY_SERVICE ");
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

			return (DbActivityInstService[]) v
					.toArray(new DbActivityInstService[0]);
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
	public int deleteByKey(String activityinstServiceId) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			if (log.isDebugEnabled())
				log
						.debug("DELETE from RT_ACTIVITY_SERVICE WHERE RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from RT_ACTIVITY_SERVICE WHERE RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityinstServiceId);
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
	public int delete(DbActivityInstService pObject) throws SQLException {
		if (pObject.isActivityinstServiceIdInitialized() == true)
			return deleteByKey(pObject.getActivityInstServiceId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM RT_ACTIVITY_SERVICE WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityinstServiceIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_SERVICE_ID").append("=?");
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
			if (pObject.isServiceIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ENDPOINT_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isUrlInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("URL").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isServiceGrpCodeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SERVICEE_GRP_CODE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isServiceActivityStateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SERVICE_ACTIVITY_STATE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isLastServiceGrpInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LAST_SERVICE_GRP").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isServiceIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SERVICE_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRequestParamsInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("REQUESTPARAMS").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityinstServiceIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstServiceId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isServiceIdInitialized())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isUrlInitialized())
				ps.setString(++_dirtyCount, pObject.getUrl());
			if (pObject.isServiceGrpCodeInitialized())
				ps.setString(++_dirtyCount, pObject.getServiceGrpCode().getType());
			if (pObject.isServiceActivityStateInitialized())
				ps.setString(++_dirtyCount, pObject.getServiceActivityState().getType());
			if (pObject.isLastServiceGrpInitialized())
				ps.setString(++_dirtyCount, pObject.getLastServiceGrp().getType());
			if (pObject.isServiceIdInitialized())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isRequestParamsInitialized())
				ps.setString(++_dirtyCount, pObject.getRequestParams());
			int _rows = ps.executeUpdate();
			return _rows;
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_ACTIVITY_SERVICE table by where clausis
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
			String delByWhereSQL = "DELETE FROM RT_ACTIVITY_SERVICE " + where;
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
	public DbActivityInstService[] save(DbActivityInstService[] pObjects)
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
	void beforeInsert(DbActivityInstService pObject) throws SQLException {
	}

	/**
	 * after the save of the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	void afterInsert(DbActivityInstService pObject) throws SQLException {
	}

	/**
	 * before the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void beforeUpdate(DbActivityInstService pObject) throws SQLException {
	}

	/**
	 * after the update of the RtActivityPersonBean pObject.
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be updated
	 */
	void afterUpdate(DbActivityInstService pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtActivityPersonBean pObject
	 * 
	 * @param pObject
	 *            the RtActivityPersonBean pObject to be saved
	 */
	public DbActivityInstService save(DbActivityInstService pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		
		
		try {
			c = getConnection();
			
					if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into RT_ACTIVITY_SERVICE (");
				if (pObject.isActivityinstServiceIdModified()) {
					_sql.append("ACTIVITYINST_SERVICE_ID").append(",");
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
				if (pObject.isServiceIdModified()) {
					_sql.append("ENDPOINT_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isUrlModified()) {
					_sql.append("URL").append(",");
					_dirtyCount++;
				}
				if (pObject.isServiceGrpCodeModified()) {
					_sql.append("SERVICE_GRP_CODE").append(",");
					_dirtyCount++;
				}
				if (pObject.isServiceActivityStateModified()) {
					_sql.append("SERVICE_ACTIVITY_STATE").append(",");
					_dirtyCount++;
				}
				if (pObject.isLastServiceGrpModified()) {
					_sql.append("LAST_SERVICE_GRP").append(",");
					_dirtyCount++;
				}
				if (pObject.isServiceIdModified()) {
					_sql.append("SERVICE_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isRequestParamsModified()) {
					_sql.append("REQUESTPARAMS").append(",");
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
				if (pObject.isActivityinstServiceIdModified())
					ps.setString(++_dirtyCount, pObject
							.getActivityInstServiceId());
				if (pObject.isProcessinstIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessInstId());
				if (pObject.isActivityinstIdModified())
					ps.setString(++_dirtyCount, pObject.getActivityInstId());
				if (pObject.isServiceIdModified())
					ps.setString(++_dirtyCount, pObject.getServiceId());
				if (pObject.isUrlModified())
					ps.setString(++_dirtyCount, pObject.getUrl());
				if (pObject.isServiceGrpCodeModified())
					ps.setString(++_dirtyCount, pObject.getServiceGrpCode().getType());
				if (pObject.isServiceActivityStateModified())
					ps.setString(++_dirtyCount, pObject
							.getServiceActivityState().getType());
				if (pObject.isLastServiceGrpModified())
					ps.setString(++_dirtyCount, pObject.getLastServiceGrp().getType());
				if (pObject.isServiceIdModified())
					ps.setString(++_dirtyCount, pObject.getServiceId());
				if (pObject.isRequestParamsModified())
					ps.setString(++_dirtyCount, pObject.getRequestParams());
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
				
				

				_sql = new StringBuffer("UPDATE RT_ACTIVITY_SERVICE SET ");
				if (pObject.isActivityinstServiceIdModified())
					_sql.append("ACTIVITYINST_SERVICE_ID").append("=?,");
				if (pObject.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (pObject.isActivityinstIdModified())
					_sql.append("ACTIVITYINST_ID").append("=?,");
				if (pObject.isServiceIdModified())
					_sql.append("ENDPOINT_ID").append("=?,");
				if (pObject.isUrlModified())
					_sql.append("URL").append("=?,");
				if (pObject.isServiceGrpCodeModified())
					_sql.append("DEVICE_GRP_CODE").append("=?,");
				if (pObject.isServiceActivityStateModified())
					_sql.append("EVENT_ACTIVITY_STATE").append("=?,");
				if (pObject.isLastServiceGrpModified())
					_sql.append("LAST_SERVICE_GRP").append("=?,");
				if (pObject.isServiceIdModified())
					_sql.append("SERVICE_ID").append("=?,");
				if (pObject.isServiceIdModified())
					_sql.append("REQUESTPARAMS").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("RT_ACTIVITY_SERVICE.ACTIVITYINST_SERVICE_ID=?");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isActivityinstServiceIdModified()) {
					if (pObject.getActivityInstServiceId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getActivityInstServiceId());
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
				if (pObject.isServiceIdModified()) {
					if (pObject.getServiceId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getServiceId());
				}
				if (pObject.isUrlModified()) {
					if (pObject.getUrl() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getUrl());
				}
				if (pObject.isServiceGrpCodeModified()) {
					if (pObject.getServiceGrpCode() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getServiceGrpCode().getType());
				}
				if (pObject.isServiceActivityStateModified()) {
					if (pObject.getServiceActivityState() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getServiceActivityState().getType());
				}
				if (pObject.isLastServiceGrpModified()) {
					if (pObject.getLastServiceGrp() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getLastServiceGrp().getType());
				}
				if (pObject.isServiceIdModified()) {
					if (pObject.getLastServiceGrp() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getServiceId());
				}
				if (pObject.isRequestParamsModified()) {
					if (pObject.getRequestParams()== null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getRequestParams());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.setString(++_dirtyCount, pObject.getActivityInstServiceId());
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
	 * Transform a ResultSet iterating on the RT_ACTIVITY_SERVICE on a
	 * RtActivityPersonBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstService decodeRow(ResultSet rs) throws SQLException {
		DbActivityInstService pObject = createActivityInstService();
		if (rs.getObject(1) != null)
			pObject.setActivityInstServiceId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setActivityInstId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setServiceId(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setUrl(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setServiceGrpCode( RightGroupEnums.fromType(rs.getString(6)));
		if (rs.getObject(7) != null)
			pObject.setServiceActivityState(RightPerformStatus.fromType(rs.getString(7)));
		if (rs.getObject(8) != null)
			pObject.setLastServiceGrp( RightGroupEnums.fromType(rs.getString(8)));
		if (rs.getObject(9) != null)
			pObject.setServiceId(rs.getString(9));
		if (rs.getObject(10) != null)
			pObject.setRequestParams(rs.getString(10));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_ACTIVITY_SERVICE on a
	 * RtActivityPersonBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtActivityPersonBean pObject
	 */
	public DbActivityInstService decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityInstService pObject = createActivityInstService();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYINST_SERVICE_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstServiceId(rs.getString(pos));
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
					pObject.setServiceId(rs.getString(pos));
				}
				break;
			case ID_URL:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setUrl(rs.getString(pos));
				}
				break;
			case ID_DEVICE_GRP_CODE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setServiceGrpCode( RightGroupEnums.fromType(rs.getString(pos)));
				}
				break;
			case ID_SERVICE_ACTIVITY_STATE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setServiceActivityState(RightPerformStatus.fromType(rs.getString(pos)));
				}
				break;
			case ID_LAST_SERVICE_GRP:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLastServiceGrp( RightGroupEnums.fromType(rs.getString(pos)));
				}
			case ID_SERVICE_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setServiceId(rs.getString(pos));
				}
				break;
			case ID_REQUESTPARAMS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRequestParams(rs.getString(pos));
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
	public DbActivityInstService loadObject(DbActivityInstService pObject)
			throws SQLException {
		DbActivityInstService[] pReturn = loadObjects(pObject);
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
	public DbActivityInstService[] loadObjects(DbActivityInstService pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_ACTIVITY_SERVICE WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityinstServiceIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_SERVICE_ID= ?");
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
			if (pObject.isServiceIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ENDPOINT_ID= ?");
			}
			if (pObject.isUrlModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("URL= ?");
			}
			if (pObject.isServiceGrpCodeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEVICE_GRP_CODE= ?");
			}
			if (pObject.isServiceActivityStateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("EVENT_ACTIVITY_STATE= ?");
			}
			if (pObject.isLastServiceGrpModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LAST_SERVICE_GRP= ?");
			}
			if (pObject.isServiceIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SERVICE_ID= ?");
			}
			if (pObject.isRequestParamsModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("REQUESTPARAMS= ?");
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
			if (pObject.isActivityinstServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstServiceId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivityinstIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isUrlModified())
				ps.setString(++_dirtyCount, pObject.getUrl());
			if (pObject.isServiceGrpCodeModified())
				ps.setString(++_dirtyCount, pObject.getServiceGrpCode().getType());
			if (pObject.isServiceActivityStateModified())
				ps.setString(++_dirtyCount, pObject.getServiceActivityState().getType());
			if (pObject.isLastServiceGrpModified())
				ps.setString(++_dirtyCount, pObject.getLastServiceGrp().getType());
			if (pObject.isServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isServiceIdModified())
				ps.setString(++_dirtyCount, pObject.getServiceId());
			if (pObject.isRequestParamsModified())
				ps.setString(++_dirtyCount, pObject.getRequestParams());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public void copy(String srcActId, String dstActId) throws SQLException {
		// String sqlInsert = "INSERT INTO RT_ACTIVITY_SERVICE " +
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

		DbActivityInstService[] actPersons = loadByWhere(sqlWhere, null);
		for (int i = 0; i < actPersons.length; i++) {
			DbActivityInstService person = createActivityInstService();
			// set it to new person to insert instead of update
			person.copy(actPersons[i]);
			person.setActivityInstId(dstActId);
			person.setActivityInstServiceId(UUID.randomUUID().toString());
			save(person);
		}

	}
}


