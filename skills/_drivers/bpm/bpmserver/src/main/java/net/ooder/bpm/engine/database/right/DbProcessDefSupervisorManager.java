/**
 * $RCSfile: DbProcessDefSupervisorManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.right;

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
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisor;
import net.ooder.bpm.engine.inter.EIProcessDefSupervisorManager;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程监控人管理
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
public class DbProcessDefSupervisorManager extends
		EIProcessDefSupervisorManager {
	Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefSupervisorManager.class);

	public static final int ID_PROCESSDEF_ID = 0;

	public static final int TYPE_PROCESSDEF_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_ID = "PROCESSDEF_ID";

	public static final int ID_PROCESSDEF_VERSION_ID = 1;

	public static final int TYPE_PROCESSDEF_VERSION_ID = Types.VARCHAR;

	public static final String NAME_PROCESSDEF_VERSION_ID = "PROCESSDEF_VERSION_ID";

	public static final int ID_SUPERVISOR_ID = 2;

	public static final int TYPE_SUPERVISOR_ID = Types.VARCHAR;

	public static final String NAME_SUPERVISOR_ID = "SUPERVISOR_ID";

	public static final int ID_SUPERVISOR_NAME = 3;

	public static final int TYPE_SUPERVISOR_NAME = Types.VARCHAR;

	public static final String NAME_SUPERVISOR_NAME = "SUPERVISOR_NAME";

	private static final String TABLE_NAME = "RT_PROCESSDEF_SUPERVISOR";

	/**
	 * create an array of string containing all the fields of the
	 * RT_PROCESSDEF_SUPERVISOR table.
	 */
	private static final String[] FIELD_NAMES = {
			"RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_ID",
			"RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_VERSION_ID",
			"RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_ID",
			"RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_NAME" };

	/**
	 * create an array of string containing all the fields of the
	 * RT_PROCESSDEF_SUPERVISOR table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "PROCESSDEF_ID",
			"PROCESSDEF_VERSION_ID", "SUPERVISOR_ID", "SUPERVISOR_NAME" };

	/**
	 * Field that contains the comma separated fields of the
	 * RT_PROCESSDEF_SUPERVISOR table
	 */
	private static final String ALL_FIELDS = "RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_ID"
			+ ",RT_PROCESSDEF_SUPERVISOR.PROCESSDEF_VERSION_ID"
			+ ",RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_ID"
			+ ",RT_PROCESSDEF_SUPERVISOR.SUPERVISOR_NAME";

	/**
	 * Creates a new RtProcessdefSupervisorBean instance.
	 * 
	 * @return the new RtProcessdefSupervisorBean
	 */
	public EIProcessDefSupervisor createProcessDefSupervisor() {
		return new DbProcessDefSupervisor();
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
	 * Load all the elements using a preparedstatement.
	 * 
	 * @param ps
	 *            the PreparedStatement to be used
	 * @return an array of RtProcessdefSupervisorBean
	 */
	public DbProcessDefSupervisor[] loadByPreparedStatement(PreparedStatement ps)
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
	 * @return an array of RtProcessdefSupervisorBean
	 */
	public DbProcessDefSupervisor[] loadByPreparedStatement(
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
			return (DbProcessDefSupervisor[]) v
					.toArray(new DbProcessDefSupervisor[0]);
		} finally {
			if (v != null) {
				v.clear();
				v = null;
			}
			getManager().close(rs);
		}
	}

	/**
	 * retreive an array of RtProcessdefSupervisorBean given a sql where
	 * clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting RtProcessdefSupervisorBean table
	 */
	public List loadByWhere(String where) throws BPMException {
		return loadByWhere(where, null);
	}

	/**
	 * retreive an array of RtProcessdefSupervisorBean given a sql where
	 * clausis, and a list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting RtProcessdefSupervisorBean table
	 */
	public List loadByWhere(String where, int[] fieldList) throws BPMException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from RT_PROCESSDEF_SUPERVISOR "
					+ where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from RT_PROCESSDEF_SUPERVISOR ");
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

			return new ArrayList(v);
		} catch (SQLException e) {
			log.error("", e);
			throw new BPMException("", e);
		} finally {
			if (v != null) {
				v.clear();
			}
			getManager().close(pStatement, rs);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean object(s) to be deleted
	 * @return the number of deleted objects
	 */
	public int delete(EIProcessDefSupervisor object) throws BPMException {
		DbProcessDefSupervisor pObject = (DbProcessDefSupervisor) object;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer(
					"DELETE FROM RT_PROCESSDEF_SUPERVISOR WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isProcessdefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessdefVersionIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSDEF_VERSION_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isSupervisorIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SUPERVISOR_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isSupervisorNameInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("SUPERVISOR_NAME").append("=?");
				_dirtyAnd++;
			}
			if (log.isDebugEnabled())
				log.debug(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isProcessdefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isProcessdefVersionIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isSupervisorIdInitialized())
				ps.setString(++_dirtyCount, pObject.getSupervisorId());
			if (pObject.isSupervisorNameInitialized())
				ps.setString(++_dirtyCount, pObject.getSupervisorName());
			int _rows = ps.executeUpdate();
			return _rows;
		} catch (SQLException e) {
			log.error("", e);
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows from RT_PROCESSDEF_SUPERVISOR table by where clausis
	 * 
	 * @param where
	 *            the clausis
	 * @return the number of deleted rows
	 */
	public int deleteByWhere(String where) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			String delByWhereSQL = "DELETE FROM RT_PROCESSDEF_SUPERVISOR "
					+ where;
			if (log.isDebugEnabled())
				log.debug(delByWhereSQL);
			ps = c.prepareStatement(delByWhereSQL);
			return ps.executeUpdate();
		} catch (SQLException e) {
			log.error("", e);
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Save into the database an array of RtProcessdefSupervisorBean pObjects
	 * 
	 * @param pObjects
	 *            the RtProcessdefSupervisorBean pObject table to be saved
	 * @return the Objects to be saved
	 */
	public EIProcessDefSupervisor[] save(EIProcessDefSupervisor[] objects)
			throws BPMException {
		DbProcessDefSupervisor[] pObjects = (DbProcessDefSupervisor[]) objects;
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return objects;
	}

	/**
	 * before the save of the RtProcessdefSupervisorBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be saved
	 */
	void beforeInsert(DbProcessDefSupervisor pObject) throws SQLException {
	}

	/**
	 * after the save of the RtProcessdefSupervisorBean pObject
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be saved
	 */
	void afterInsert(DbProcessDefSupervisor pObject) throws SQLException {
	}

	/**
	 * before the update of the RtProcessdefSupervisorBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be updated
	 */
	void beforeUpdate(DbProcessDefSupervisor pObject) throws SQLException {
	}

	/**
	 * after the update of the RtProcessdefSupervisorBean pObject.
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be updated
	 */
	void afterUpdate(DbProcessDefSupervisor pObject) throws SQLException {
	}

	/**
	 * Save into the database the RtProcessdefSupervisorBean pObject
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to be saved
	 */
	public EIProcessDefSupervisor save(EIProcessDefSupervisor object)
			throws BPMException {
		DbProcessDefSupervisor pObject = (DbProcessDefSupervisor) object;

		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (pObject.isNew()) { // SAVE
				beforeInsert(pObject);
				int _dirtyCount = 0;
				_sql = new StringBuffer(
						"INSERT into RT_PROCESSDEF_SUPERVISOR (");
				if (pObject.isProcessdefIdModified()) {
					_sql.append("PROCESSDEF_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isProcessdefVersionIdModified()) {
					_sql.append("PROCESSDEF_VERSION_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isSupervisorIdModified()) {
					_sql.append("SUPERVISOR_ID").append(",");
					_dirtyCount++;
				}
				if (pObject.isSupervisorNameModified()) {
					_sql.append("SUPERVISOR_NAME").append(",");
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
				if (pObject.isProcessdefIdModified())
					ps.setString(++_dirtyCount, pObject.getProcessDefId());
				if (pObject.isProcessdefVersionIdModified())
					ps.setString(++_dirtyCount, pObject
							.getProcessDefVersionId());
				if (pObject.isSupervisorIdModified())
					ps.setString(++_dirtyCount, pObject.getSupervisorId());
				if (pObject.isSupervisorNameModified())
					ps.setString(++_dirtyCount, pObject.getSupervisorName());
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
				_sql = new StringBuffer("UPDATE RT_PROCESSDEF_SUPERVISOR SET ");
				if (pObject.isProcessdefIdModified())
					_sql.append("PROCESSDEF_ID").append("=?,");
				if (pObject.isProcessdefVersionIdModified())
					_sql.append("PROCESSDEF_VERSION_ID").append("=?,");
				if (pObject.isSupervisorIdModified())
					_sql.append("SUPERVISOR_ID").append("=?,");
				if (pObject.isSupervisorNameModified())
					_sql.append("SUPERVISOR_NAME").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append("");
				if (log.isDebugEnabled())
					log.debug(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (pObject.isProcessdefIdModified()) {
					if (pObject.getProcessDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getProcessDefId());
				}
				if (pObject.isProcessdefVersionIdModified()) {
					if (pObject.getProcessDefVersionId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject
								.getProcessDefVersionId());
				}
				if (pObject.isSupervisorIdModified()) {
					if (pObject.getSupervisorId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, pObject.getSupervisorId());
				}
				if (pObject.isSupervisorNameModified()) {
					if (pObject.getSupervisorName() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, pObject
										.getSupervisorName());
				}
				if (_dirtyCount == 0)
					return pObject;
				ps.executeUpdate();
				pObject.resetIsModified();
				// postupdate+

				// postupdate-
				afterUpdate(pObject);
			}
		} catch (SQLException e) {
			log.error("", e);
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_PROCESSDEF_SUPERVISOR on a
	 * RtProcessdefSupervisorBean pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting RtProcessdefSupervisorBean pObject
	 */
	public DbProcessDefSupervisor decodeRow(ResultSet rs) throws SQLException {
		DbProcessDefSupervisor pObject = (DbProcessDefSupervisor) createProcessDefSupervisor();
		if (rs.getObject(1) != null)
			pObject.setProcessDefId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessDefVersionId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setSupervisorId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setSupervisorName(rs.getString(4));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the RT_PROCESSDEF_SUPERVISOR on a
	 * RtProcessdefSupervisorBean pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting RtProcessdefSupervisorBean pObject
	 */
	public DbProcessDefSupervisor decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbProcessDefSupervisor pObject = (DbProcessDefSupervisor) createProcessDefSupervisor();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
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
			case ID_SUPERVISOR_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setSupervisorId(rs.getString(pos));
				}
				break;
			case ID_SUPERVISOR_NAME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setSupervisorName(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique RtProcessdefSupervisorBean pObject from an template one
	 * giving a c
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean pObject to look for
	 * @return the pObject matching the template
	 */
	public DbProcessDefSupervisor loadObject(DbProcessDefSupervisor pObject)
			throws SQLException {
		DbProcessDefSupervisor[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new SQLException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of RtProcessdefSupervisorBean from a template one
	 * 
	 * @param pObject
	 *            the RtProcessdefSupervisorBean template to look for
	 * @return all the RtProcessdefSupervisorBean matching the template
	 */
	public DbProcessDefSupervisor[] loadObjects(DbProcessDefSupervisor pObject)
			throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from RT_PROCESSDEF_SUPERVISOR WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isProcessdefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_ID= ?");
			}
			if (pObject.isProcessdefVersionIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSDEF_VERSION_ID= ?");
			}
			if (pObject.isSupervisorIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SUPERVISOR_ID= ?");
			}
			if (pObject.isSupervisorNameModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("SUPERVISOR_NAME= ?");
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
			if (pObject.isProcessdefIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefId());
			if (pObject.isProcessdefVersionIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessDefVersionId());
			if (pObject.isSupervisorIdModified())
				ps.setString(++_dirtyCount, pObject.getSupervisorId());
			if (pObject.isSupervisorNameModified())
				ps.setString(++_dirtyCount, pObject.getSupervisorName());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	public List getSupervisorByProcessDefVersionId(String processDefVersionId)
			throws BPMException {
		List list = new ArrayList();
		String sqlWhere = " where PROCESSDEF_VERSION_ID= '"
				+ processDefVersionId + "'";
		List supervisors = loadByWhere(sqlWhere);
		if (supervisors == null) {
			return list;
		}
		for (int i = 0; i < supervisors.size(); i++) {
			list.add(supervisors.get(i));
		}
		return list;
	}

	public void deleteByProcessDefVersion(String processDefVersionId)
			throws BPMException {
		String sqlWhere = " where PROCESSDEF_VERSION_ID= '"
				+ processDefVersionId + "'";
		deleteByWhere(sqlWhere);
	}

}


