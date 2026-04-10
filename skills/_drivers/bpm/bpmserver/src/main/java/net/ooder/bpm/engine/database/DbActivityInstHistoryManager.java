/**
 * $RCSfile: DbActivityInstHistoryManager.java,v $
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.attribute.InterpreterManager;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIActivityInstHistory;
import net.ooder.bpm.engine.inter.EIActivityInstHistoryManager;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2003
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public class DbActivityInstHistoryManager extends EIActivityInstHistoryManager {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstHistoryManager.class);

	public static final int ID_ACTIVITYHISTORY_ID = 0;

	public static final int TYPE_ACTIVITYHISTORY_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYHISTORY_ID = "ACTIVITYHISTORY_ID";

	public static final int ID_PROCESSINST_ID = 1;

	public static final int TYPE_PROCESSINST_ID = Types.VARCHAR;

	public static final String NAME_PROCESSINST_ID = "PROCESSINST_ID";

	public static final int ID_ACTIVITYDEF_ID = 2;

	public static final int TYPE_ACTIVITYDEF_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYDEF_ID = "ACTIVITYDEF_ID";

	public static final int ID_ACTIVITYINST_ID = 3;

	public static final int TYPE_ACTIVITYINST_ID = Types.VARCHAR;

	public static final String NAME_ACTIVITYINST_ID = "ACTIVITYINST_ID";

	public static final int ID_URGENCYTYPE = 4;

	public static final int TYPE_URGENCYTYPE = Types.VARCHAR;

	public static final String NAME_URGENCYTYPE = "URGENCYTYPE";

	public static final int ID_ARRIVEDTIME = 5;

	public static final int TYPE_ARRIVEDTIME = Types.DECIMAL;

	public static final String NAME_ARRIVEDTIME = "ARRIVEDTIME";

	public static final int ID_LIMITTIME = 6;

	public static final int TYPE_LIMITTIME = Types.DECIMAL;

	public static final String NAME_LIMITTIME = "LIMITTIME";

	public static final int ID_STARTTIME = 7;

	public static final int TYPE_STARTTIME = Types.DECIMAL;

	public static final String NAME_STARTTIME = "STARTTIME";

	public static final int ID_ENDTIME = 8;

	public static final int TYPE_ENDTIME = Types.DECIMAL;

	public static final String NAME_ENDTIME = "ENDTIME";

	public static final int ID_RECEIVEMETHOD = 9;

	public static final int TYPE_RECEIVEMETHOD = Types.VARCHAR;

	public static final String NAME_RECEIVEMETHOD = "RECEIVEMETHOD";

	public static final int ID_DEALMETHOD = 10;

	public static final int TYPE_DEALMETHOD = Types.VARCHAR;

	public static final String NAME_DEALMETHOD = "DEALMETHOD";

	public static final int ID_RUNSTATUS = 11;

	public static final int TYPE_RUNSTATUS = Types.VARCHAR;

	public static final String NAME_RUNSTATUS = "RUNSTATUS";

	private static final String TABLE_NAME = "BPM_ACTIVITYHISTORY";

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYHISTORY table.
	 */
	private static final String[] FIELD_NAMES = {
			"BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID",
			"BPM_ACTIVITYHISTORY.PROCESSINST_ID",
			"BPM_ACTIVITYHISTORY.ACTIVITYDEF_ID",
			"BPM_ACTIVITYHISTORY.ACTIVITYINST_ID",
			"BPM_ACTIVITYHISTORY.URGENCYTYPE",
			"BPM_ACTIVITYHISTORY.ARRIVEDTIME", "BPM_ACTIVITYHISTORY.LIMITTIME",
			"BPM_ACTIVITYHISTORY.STARTTIME", "BPM_ACTIVITYHISTORY.ENDTIME",
			"BPM_ACTIVITYHISTORY.RECEIVEMETHOD",
			"BPM_ACTIVITYHISTORY.DEALMETHOD", "BPM_ACTIVITYHISTORY.RUNSTATUS" };

	/**
	 * create an array of string containing all the fields of the
	 * BPM_ACTIVITYHISTORY table.
	 */
	private static final String[] TABLEFIELD_NAMES = { "ACTIVITYHISTORY_ID",
			"PROCESSINST_ID", "ACTIVITYDEF_ID", "ACTIVITYINST_ID",
			"URGENCYTYPE", "ARRIVEDTIME", "LIMITTIME", "STARTTIME", "ENDTIME",
			"RECEIVEMETHOD", "DEALMETHOD", "RUNSTATUS" };

	/**
	 * Field that contains the comma separated fields of the BPM_ACTIVITYHISTORY
	 * table
	 */
	private static final String ALL_FIELDS = "BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID"
			+ ",BPM_ACTIVITYHISTORY.PROCESSINST_ID"
			+ ",BPM_ACTIVITYHISTORY.ACTIVITYDEF_ID"
			+ ",BPM_ACTIVITYHISTORY.ACTIVITYINST_ID"
			+ ",BPM_ACTIVITYHISTORY.URGENCYTYPE"
			+ ",BPM_ACTIVITYHISTORY.ARRIVEDTIME"
			+ ",BPM_ACTIVITYHISTORY.LIMITTIME"
			+ ",BPM_ACTIVITYHISTORY.STARTTIME"
			+ ",BPM_ACTIVITYHISTORY.ENDTIME"
			+ ",BPM_ACTIVITYHISTORY.RECEIVEMETHOD"
			+ ",BPM_ACTIVITYHISTORY.DEALMETHOD"
			+ ",BPM_ACTIVITYHISTORY.RUNSTATUS";

	private static DbActivityInstHistoryManager singleton = new DbActivityInstHistoryManager();

	/** 娴佺▼瀹氫箟cache */
	Cache cache = null; // 娴佺▼瀹氫箟cache

	/** 娴佺▼瀹氫箟cache鏄惁鍙敤 */
	boolean cacheEnabled;

	public DbActivityInstHistoryManager() {
		cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
				"ActivityInstHistoryCache");
		cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
				BPMConstants.CONFIG_KEY).isCacheEnabled();
		;
	}

	/**
	 * Creates a new Activityhistory instance.
	 * 
	 * @return the new Activityhistory
	 */
	public EIActivityInstHistory createActivityHistory() {
		return new DbActivityInstHistory();
	}

	/**
	 * Creates a new ActivityHistory instance, use the specify activity instance
	 * as its prototype, save not only basic properties but also extend
	 * attributes
	 * 
	 * @param activityInst
	 *            activity instance to be in history
	 * @return the new ActivityHistory
	 */
	public EIActivityInstHistory saveActivityInstAsHistory(
			EIActivityInst activityInst) throws BPMException {
		DbActivityInstHistory history = new DbActivityInstHistory();
		if (activityInst != null) {
			history.setActivityDefId(activityInst.getActivityDefId());
			history.setActivityHistoryId(UUID.randomUUID().toString());
			history.setActivityInstId(activityInst.getActivityInstId());
			history.setProcessInstId(activityInst.getProcessInstId());

			history.setArrivedTime(activityInst.getArrivedTime());
			history.setDealMethod(activityInst.getDealMethod());
			history.setEndTime(new java.util.Date());
			history.setLimitTime(activityInst.getLimitTime());
			history.setReceiveMethod(activityInst.getReceiveMethod());
			history.setRunStatus(activityInst.getRunStatus());
			history.setStartTime(activityInst.getStartTime());
			history.setUrgency(activityInst.getUrgency());
			save(history);
			// 淇濆瓨鎵╁睍灞炴€?
			Connection c = null;
			PreparedStatement ps = null;
			try {
				c = getConnection();
				ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
				List list = activityInst.getAllAttribute();
				for (Iterator it = list.iterator(); it.hasNext();) {
					DbAttributeInst att = (DbAttributeInst) it.next();
					saveAttribute(ps, history, att);
				}
			} catch (SQLException e) {
				throw new BPMException("", e);
			} finally {
				getManager().close(ps);
				freeConnection(c);
			}
		}
		return history;
	}

	private static final String LOAD_EXTATTRIBUTRE = "SELECT "
			+ " PROPERTY_ID, " + " ACTIVITYHISTORY_ID, " + " PROPNAME, "
			+ " PROPVALUE, " + " PROPCLASS, " + " PROPTYPE, "
			+ " PARENTPROP_ID, " + " ISEXTENSION, " + " CANINSTANTIATE "
			+ "FROM BPM_ACTIVITYHISTORY_PROPERTY "
			+ "WHERE ACTIVITYHISTORY_ID = ? "
			+ "ORDER BY PROPERTY_ID , ISEXTENSION ASC ";

	private static final String DELETE_ONE_EXTATTRIBUTRE = "DELETE FROM BPM_ACTIVITYHISTORY_PROPERTY "
			+ "WHERE PROPERTY_ID = ? ";

	private static final String DELETE_ACTIVITY_EXTATTRIBUTRE = "DELETE FROM BPM_ACTIVITYHISTORY_PROPERTY "
			+ "WHERE ACTIVITYHISTORY_ID = ? ";

	private static final String INSERT_EXTATTRIBUTRE = "INSERT INTO BPM_ACTIVITYHISTORY_PROPERTY "
			+ "( "
			+ " PROPERTY_ID, "
			+ " ACTIVITYHISTORY_ID, "
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

	private static final String UPDATE_EXTATTRIBUTRE = "UPDATE BPM_ACTIVITYHISTORY_PROPERTY SET "
			+ " PROPNAME=?, "
			+ " PROPVALUE=?, "
			+ " PROPCLASS=?, "
			+ " PROPTYPE=?, "
			+ " ISEXTENSION=?, "
			+ " CANINSTANTIATE=? "
			+ " where PROPERTY_ID=?";

	void loadAttribute(DbActivityInstHistory act) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(LOAD_EXTATTRIBUTRE);
			ps.setString(1, act.getActivityHistoryId());
			ResultSet rs = ps.executeQuery();
			String lastUUID = "";
			DbAttributeInst extAtt = null;
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
						addAttribute(act, extAtt);
					}
					extAtt = (DbAttributeInst) act.attributeIdMap.get(currUUID);
					if (extAtt == null) {
						extAtt = new DbAttributeInst();
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
				addAttribute(act, extAtt);
			}

		} catch (SQLException e) {
			throw new BPMException("", e);

		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void addAttributeToDb(DbActivityInstHistory activityInst,
			DbAttributeInst attr) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();

			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			saveAttribute(ps, activityInst, attr);
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void updateAttributeToDb(DbActivityInstHistory activityInst,
			DbAttributeInst attr) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();

			getManager().close(ps);
			ps = c.prepareStatement(UPDATE_EXTATTRIBUTRE);
			updateAttribute(ps, activityInst, attr);
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	void removeAttributeFromDb(DbAttributeInst attr) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(DELETE_ONE_EXTATTRIBUTRE);
			ps.setString(1, attr.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException("", e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	private void addAttribute(DbActivityInstHistory act, DbAttributeInst extAtt) {
		if (act.attributeIdMap == null) {
			act.attributeIdMap = new HashMap();
		}
		if (act.attributeTopMap == null) {
			act.attributeTopMap = new HashMap();
		}
		String parentUUID = extAtt.getParentId();
		if (parentUUID != null && !parentUUID.equals("")) {
			DbAttributeInst parent = (DbAttributeInst) act.attributeIdMap
					.get(parentUUID);
			if (parent == null) {
				// not found the parent attribute, so cateate one first!
				parent = new DbAttributeInst();
				act.attributeIdMap.put(parentUUID, parent);
			}
			parent.addChild(extAtt);
			extAtt.setParent(parent);
		} else { // top attribute (no parent)
			DbAttributeInst att = (DbAttributeInst) act.attributeTopMap
					.get(extAtt.getType());
			if (att == null) {
				att = new DbAttributeInst();
				att.setName(extAtt.getType());
				att.setType(extAtt.getType());
				act.attributeTopMap.put(att.getName(), att);
			}
			att.addChild(extAtt);

			// act.attributeTopMap.put(extAtt.getName(), extAtt);
		}
		act.attributeIdMap.put(extAtt.getId(), extAtt);
	}

	/**
	 * @param activityInstHistory
	 */
	void saveAttribute(DbActivityInstHistory activityInstHistory)
			throws BPMException {
		if (activityInstHistory.isAttributeModified() == false) {
			return;
		}
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			ps = c.prepareStatement(DELETE_ACTIVITY_EXTATTRIBUTRE);
			ps.setString(1, activityInstHistory.getActivityHistoryId());
			int i = ps.executeUpdate();
			getManager().close(ps);
			ps = c.prepareStatement(INSERT_EXTATTRIBUTRE);
			List list = activityInstHistory.getAllAttribute();
			for (Iterator it = list.iterator(); it.hasNext();) {
				DbAttributeInst att = (DbAttributeInst) it.next();
				saveAttribute(ps, activityInstHistory, att);
			}

		} catch (SQLException e) {
			throw new BPMException("", e, BPMException.PROCESSDEFINITIONERROR);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}

	}

	private void saveAttribute(PreparedStatement ps,
			DbActivityInstHistory activityInstHistory, DbAttributeInst attInst)
			throws SQLException {

		String value = attInst.getValue();
		if (value == null) {
			value = "";
		}
		int block = (value.length() / 500);
		for (int i = 0; i <= block; i++) {
			int begin = i * 500;
			int end = (i == block) ? value.length() : (i + 1) * 500;
			String v = value.substring(begin, end);

			ps.setString(1, attInst.getId());
			ps.setString(2, activityInstHistory.getActivityHistoryId());
			ps.setString(3, attInst.getName());
			ps.setString(4, v);
			ps.setString(5, attInst.getInterpretClass());
			ps.setString(6, attInst.getType());
			ps.setString(7, attInst.getParentId());
			ps.setInt(8, i);
			ps.setString(9, attInst.getCanInstantiate());
			ps.executeUpdate();
		}
	}

	private void updateAttribute(PreparedStatement ps,
			DbActivityInstHistory activity, DbAttributeInst attInst)
			throws SQLException {
		AttributeInterpreter interpreter = InterpreterManager.getInstance()
				.getInterpreter(attInst.getInterpretClass());

		String value = interpreter.instantiate(attInst.getInterpretedValue());
		if (value == null) {
			value = "";
		}
		// 灏嗗€奸噸璁惧洖鍘汇€?
		attInst.setValue(value);
		int block = (value.length() / 500);
		for (int i = 0; i <= block; i++) {
			int begin = i * 500;
			int end = (i == block) ? value.length() : (i + 1) * 500;
			String v = value.substring(begin, end);

			ps.setString(1, attInst.getName());
			ps.setString(2, v);
			ps.setString(3, attInst.getInterpretClass());
			ps.setString(4, attInst.getType());
			ps.setInt(5, i);
			ps.setString(6, attInst.getCanInstantiate());
			ps.setString(7, attInst.getId());
			ps.executeUpdate();
		}
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
	 * Load a Activityhistory from the BPM_ACTIVITYHISTORY using its key fields.
	 * 
	 * @return a unique Activityhistory
	 */
	// 12
	public EIActivityInstHistory loadByKey(String activityHistoryId)
			throws BPMException {
		EIActivityInstHistory dbActivityHistoryInst = (DbActivityInstHistory) cache
				.get(activityHistoryId);
		if (dbActivityHistoryInst != null) {
			return dbActivityHistoryInst;
		}

		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			log
					.debug("SELECT "
							+ ALL_FIELDS
							+ " FROM BPM_ACTIVITYHISTORY WHERE BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID=?");
			ps = c
					.prepareStatement(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_ACTIVITYHISTORY WHERE BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityHistoryId);
			DbActivityInstHistory pReturn[] = (DbActivityInstHistory[]) loadByPreparedStatement(ps);
			if (pReturn.length < 1)
				return null;
			else {
				putToCache(activityHistoryId, pReturn[0]);
				return pReturn[0];
			}
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Load a Activityhistory table from the BPM_ACTIVITYHISTORY table using its
	 * PROCESSINST_ID field.
	 * 
	 * @return an array of Activityhistory
	 */
	// LOAD BY IMPORTED KEY
	public EIActivityInstHistory[] loadByProcessInstId(String value)
			throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"SELECT "
									+ ALL_FIELDS
									+ " FROM BPM_ACTIVITYHISTORY WHERE PROCESSINST_ID=?");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ACTIVITYHISTORY WHERE PROCESSINST_ID=?",
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, value);
			return loadByPreparedStatement(ps);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete from the BPM_ACTIVITYHISTORY table by PROCESSINST_ID field.
	 * 
	 * @param value
	 *            the key value to seek
	 * @return the number of rows deleted
	 */
	// DELETE BY IMPORTED KEY
	public int deleteByProcessInstId(String value) throws BPMException {
		String where = "PROCESSINST_ID='" + value + "'";
		return deleteByWhere(where);
	}

	/**
	 * get the Processinstance object from the
	 * BPM_ACTIVITYHISTORY.PROCESSINST_ID field.
	 * 
	 * @param pObject
	 *            the Activityhistory
	 * @return the associated Processinstance pObject
	 */
	// LOAD IMPORTED
	public EIProcessInst getProcessInstance(DbActivityInstHistory pObject)
			throws BPMException {
		EIProcessInst other = EIProcessInstManager.getInstance()
				.createProcessInstance();
		other.setProcessInstId(pObject.getProcessInstId());
		return ((DbProcessInstManager) DbProcessInstManager.getInstance())
				.loadObject(other);
	}

	/**
	 * associates the Activityhistory object to the Processinstance object.
	 * 
	 * @param pObject
	 *            the Activityhistory object to use
	 * @param pObjectToBeSet
	 *            the Processinstance object to associate to the Activityhistory
	 * @return the associated Processinstance pObject
	 */
	// SET IMPORTED
	public EIActivityInstHistory setProcessInstance(
			EIActivityInstHistory pObject, EIProcessInst pObjectToBeSet) {
		pObject.setProcessInstId(pObjectToBeSet.getProcessInstId());
		return pObject;
	}

	// ////////////////////////////////////
	// LOAD ALL
	// ////////////////////////////////////

	/**
	 * Load all the rows from BPM_ACTIVITYHISTORY.
	 * 
	 * @return an array of ActivityhistoryManager pObject
	 */
	// 38
	public List loadAll() throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager().log(
					"SELECT " + ALL_FIELDS + " FROM BPM_ACTIVITYHISTORY");
			ps = c.prepareStatement("SELECT " + ALL_FIELDS
					+ " FROM BPM_ACTIVITYHISTORY",
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			List list = new DbActivityInstHistoryList(loadByPreparedStatement(
					ps, new int[] { ID_ACTIVITYHISTORY_ID }));
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
	 * @return an array of Activityhistory
	 */
	// 41
	public DbActivityInstHistory[] loadByPreparedStatement(PreparedStatement ps)
			throws BPMException {
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
	 * @return an array of Activityhistory
	 */
	public DbActivityInstHistory[] loadByPreparedStatement(
			PreparedStatement ps, int[] fieldList) throws BPMException {
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
			return (DbActivityInstHistory[]) v
					.toArray(new DbActivityInstHistory[0]);
		} catch (SQLException e) {
			throw new BPMException(e);
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
	 * retreive an array of Activityhistory given a sql where clausis.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @return the resulting Activityhistory table
	 */
	// 49
	public List loadByWhere(String where) throws BPMException {
		List result;
		result = new DbActivityInstHistoryList(loadByWhere(where,
				new int[] { ID_ACTIVITYHISTORY_ID }));
		return Collections.unmodifiableList(result);
	}

	/**
	 * retreive an array of Activityhistory given a sql where clausis, and a
	 * list of fields.
	 * 
	 * @param where
	 *            the sql where clausis
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return the resulting Activityhistory table
	 */
	// 51
	private DbActivityInstHistory[] loadByWhere(String where, int[] fieldList)
			throws BPMException {
		String sql = null;
		if (fieldList == null)
			sql = "select " + ALL_FIELDS + " from BPM_ACTIVITYHISTORY " + where;
		else {
			StringBuffer buff = new StringBuffer(128);
			buff.append("select ");
			for (int i = 0; i < fieldList.length; i++) {
				if (i != 0)
					buff.append(",");
				buff.append(FIELD_NAMES[fieldList[i]]);
			}
			buff.append(" from BPM_ACTIVITYHISTORY ");
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

			return (DbActivityInstHistory[]) v
					.toArray(new DbActivityInstHistory[0]);
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
	public int deleteByKey(String activityhistoryId) throws BPMException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = getConnection();
			getManager()
					.log(
							"DELETE from BPM_ACTIVITYHISTORY WHERE BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID=?");
			ps = c
					.prepareStatement(
							"DELETE from BPM_ACTIVITYHISTORY WHERE BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, activityhistoryId);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			removeFromCache(activityhistoryId);
			getManager().close(ps);
			freeConnection(c);
		}
	}

	/**
	 * Delete rows according to an object
	 * 
	 * @param object
	 *            the Activityhistory object(s) to be deleted
	 * @return the number of deleted objects
	 */
	// 63
	public int delete(EIActivityInstHistory object) throws BPMException {
		DbActivityInstHistory pObject = (DbActivityInstHistory) object;
		if (pObject.isActivityhistoryIdInitialized() == true)
			return deleteByKey(pObject.getActivityHistoryId());
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer("DELETE FROM BPM_ACTIVITYHISTORY WHERE ");
			int _dirtyAnd = 0;
			if (pObject.isActivityhistoryIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYHISTORY_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isProcessinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("PROCESSINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isActivitydefIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYDEF_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isActivityinstIdInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ACTIVITYINST_ID").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isUrgencytypeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("URGENCYTYPE").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isArrivedtimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ARRIVEDTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isLimittimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("LIMITTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isStarttimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("STARTTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isEndTimeInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("ENDTIME").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRecievestateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RECEIVEMETHOD").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isDealstateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("DEALMETHOD").append("=?");
				_dirtyAnd++;
			}
			if (pObject.isRunstateInitialized()) {
				if (_dirtyAnd > 0)
					sql.append(" AND ");
				sql.append("RUNSTATUS").append("=?");
				_dirtyAnd++;
			}
			getManager().log(sql.toString());
			c = getConnection();
			ps = c.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int _dirtyCount = 0;
			if (pObject.isActivityhistoryIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityHistoryId());
			if (pObject.isProcessinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivitydefIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityDefId());
			if (pObject.isActivityinstIdInitialized())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isUrgencytypeInitialized())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isArrivedtimeInitialized())
				ps.setDouble(++_dirtyCount, pObject.getArrivedTime().getTime());
			if (pObject.isLimittimeInitialized())
				ps.setDouble(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isStarttimeInitialized())
				ps.setDouble(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isEndTimeInitialized())
				ps.setDouble(++_dirtyCount, pObject.getEndTime().getTime());
			if (pObject.isRecievestateInitialized())
				ps.setString(++_dirtyCount, pObject.getReceiveMethod());
			if (pObject.isDealstateInitialized())
				ps.setString(++_dirtyCount, pObject.getDealMethod());
			if (pObject.isRunstateInitialized())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
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
	 * Delete rows from BPM_ACTIVITYHISTORY table by where clausis
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
			DbActivityInstHistory[] historys = loadByWhere(where,
					new int[] { ID_ACTIVITYHISTORY_ID });
			for (int i = 0; i < historys.length; i++) {
				removeFromCache(historys[i].getActivityHistoryId());
			}
			// delete it
			c = getConnection();
			String delByWhereSQL = "DELETE FROM BPM_ACTIVITYHISTORY " + where;
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
	 * Save into the database an array of Activityhistory pObjects
	 * 
	 * @param pObjects
	 *            the Activityhistory pObject table to be saved
	 * @return the Objects to be saved
	 */
	// 65
	public EIActivityInstHistory[] save(EIActivityInstHistory[] pObjects)
			throws BPMException {
		for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
			save(pObjects[iIndex]);
		}
		return pObjects;
	}

	/**
	 * before the save of the Activityhistory pObject.
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to be saved
	 */
	// 67
	void beforeInsert(EIActivityInstHistory pObject) throws SQLException {
	}

	/**
	 * after the save of the Activityhistory pObject
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to be saved
	 */
	// 68
	void afterInsert(EIActivityInstHistory pObject) throws SQLException {
	}

	/**
	 * before the update of the Activityhistory pObject.
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to be updated
	 */
	// 69
	void beforeUpdate(EIActivityInstHistory pObject) throws SQLException {
	}

	/**
	 * after the update of the Activityhistory pObject.
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to be updated
	 */
	// 70
	void afterUpdate(EIActivityInstHistory pObject) throws SQLException {
	}

	/**
	 * Save into the database the Activityhistory pObject
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to be saved
	 */
	// 100
	public EIActivityInstHistory save(EIActivityInstHistory pObject)
			throws BPMException {
		DbActivityInstHistory history = (DbActivityInstHistory) pObject;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer _sql = null;
		try {
			c = getConnection();
			if (history.isNew()) { // SAVE
				beforeInsert(history);
				int _dirtyCount = 0;
				_sql = new StringBuffer("INSERT into BPM_ACTIVITYHISTORY (");
				if (history.isActivityhistoryIdModified()) {
					_sql.append("ACTIVITYHISTORY_ID").append(",");
					_dirtyCount++;
				}
				if (history.isProcessinstIdModified()) {
					_sql.append("PROCESSINST_ID").append(",");
					_dirtyCount++;
				}
				if (history.isActivitydefIdModified()) {
					_sql.append("ACTIVITYDEF_ID").append(",");
					_dirtyCount++;
				}
				if (history.isActivityinstIdModified()) {
					_sql.append("ACTIVITYINST_ID").append(",");
					_dirtyCount++;
				}
				if (history.isUrgencytypeModified()) {
					_sql.append("URGENCYTYPE").append(",");
					_dirtyCount++;
				}
				if (history.isArrivedtimeModified()) {
					_sql.append("ARRIVEDTIME").append(",");
					_dirtyCount++;
				}
				if (history.isLimittimeModified()) {
					_sql.append("LIMITTIME").append(",");
					_dirtyCount++;
				}
				if (history.isStarttimeModified()) {
					_sql.append("STARTTIME").append(",");
					_dirtyCount++;
				}
				if (history.isEndTimeModified()) {
					_sql.append("ENDTIME").append(",");
					_dirtyCount++;
				}
				if (history.isRecievestateModified()) {
					_sql.append("RECEIVEMETHOD").append(",");
					_dirtyCount++;
				}
				if (history.isDealstateModified()) {
					_sql.append("DEALMETHOD").append(",");
					_dirtyCount++;
				}
				if (history.isRunstateModified()) {
					_sql.append("RUNSTATUS").append(",");
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
				if (history.isActivityhistoryIdModified())
					ps.setString(++_dirtyCount, history.getActivityHistoryId());
				if (history.isProcessinstIdModified())
					ps.setString(++_dirtyCount, history.getProcessInstId());
				if (history.isActivitydefIdModified())
					ps.setString(++_dirtyCount, history.getActivityDefId());
				if (history.isActivityinstIdModified())
					ps.setString(++_dirtyCount, history.getActivityInstId());
				if (history.isUrgencytypeModified())
					ps.setString(++_dirtyCount, history.getUrgency());
				if (history.isArrivedtimeModified())
					ps.setDouble(++_dirtyCount, history.getArrivedTime()
							.getTime());
				if (history.isLimittimeModified())
					ps.setDouble(++_dirtyCount, history.getLimitTime()
							.getTime());
				if (history.isStarttimeModified())
					ps.setDouble(++_dirtyCount, history.getStartTime()
							.getTime());
				if (history.isEndTimeModified())
					ps.setDouble(++_dirtyCount, history.getEndTime().getTime());
				if (history.isRecievestateModified())
					ps.setString(++_dirtyCount, history.getReceiveMethod());
				if (history.isDealstateModified())
					ps.setString(++_dirtyCount, history.getDealMethod());
				if (history.isRunstateModified())
					ps.setString(++_dirtyCount, history.getRunStatus());
				ps.executeUpdate();
				// postinsert+

				// postinsert-
				history.setIsNew(false);
				history.resetIsModified();
				afterInsert(history);
			} else { // UPDATE
				// preupdate+

				// preupdate-
				beforeUpdate(history);
				_sql = new StringBuffer("UPDATE BPM_ACTIVITYHISTORY SET ");
				if (history.isActivityhistoryIdModified())
					_sql.append("ACTIVITYHISTORY_ID").append("=?,");
				if (history.isProcessinstIdModified())
					_sql.append("PROCESSINST_ID").append("=?,");
				if (history.isActivitydefIdModified())
					_sql.append("ACTIVITYDEF_ID").append("=?,");
				if (history.isActivityinstIdModified())
					_sql.append("ACTIVITYINST_ID").append("=?,");
				if (history.isUrgencytypeModified())
					_sql.append("URGENCYTYPE").append("=?,");
				if (history.isArrivedtimeModified())
					_sql.append("ARRIVEDTIME").append("=?,");
				if (history.isLimittimeModified())
					_sql.append("LIMITTIME").append("=?,");
				if (history.isStarttimeModified())
					_sql.append("STARTTIME").append("=?,");
				if (history.isEndTimeModified())
					_sql.append("ENDTIME").append("=?,");
				if (history.isRecievestateModified())
					_sql.append("RECEIVEMETHOD").append("=?,");
				if (history.isDealstateModified())
					_sql.append("DEALMETHOD").append("=?,");
				if (history.isRunstateModified())
					_sql.append("RUNSTATUS").append("=?,");
				_sql.setLength(_sql.length() - 1);
				_sql.append(" WHERE ");
				_sql.append("BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID=?");
				getManager().log(_sql.toString());
				ps = c.prepareStatement(_sql.toString(),
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				int _dirtyCount = 0;
				if (history.isActivityhistoryIdModified()) {
					if (history.getActivityHistoryId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history
								.getActivityHistoryId());
				}
				if (history.isProcessinstIdModified()) {
					if (history.getProcessInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getProcessInstId());
				}
				if (history.isActivitydefIdModified()) {
					if (history.getActivityDefId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getActivityDefId());
				}
				if (history.isActivityinstIdModified()) {
					if (history.getActivityInstId() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps
								.setString(++_dirtyCount, history
										.getActivityInstId());
				}
				if (history.isUrgencytypeModified()) {
					if (history.getUrgency() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getUrgency());
				}
				if (history.isArrivedtimeModified()) {
					if (history.getArrivedTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setDouble(++_dirtyCount, history.getArrivedTime()
								.getTime());
				}
				if (history.isLimittimeModified()) {
					if (history.getLimitTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setDouble(++_dirtyCount, history.getLimitTime()
								.getTime());
				}
				if (history.isStarttimeModified()) {
					if (history.getStartTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setDouble(++_dirtyCount, history.getStartTime()
								.getTime());
				}
				if (history.isEndTimeModified()) {
					if (history.getEndTime() == null)
						ps.setNull(++_dirtyCount, 3);
					else
						ps.setDouble(++_dirtyCount, history.getEndTime()
								.getTime());
				}
				if (history.isRecievestateModified()) {
					if (history.getReceiveMethod() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getReceiveMethod());
				}
				if (history.isDealstateModified()) {
					if (history.getDealMethod() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getDealMethod());
				}
				if (history.isRunstateModified()) {
					if (history.getRunStatus() == null)
						ps.setNull(++_dirtyCount, 12);
					else
						ps.setString(++_dirtyCount, history.getRunStatus());
				}
				if (_dirtyCount == 0)
					return history;
				ps.setString(++_dirtyCount, history.getActivityHistoryId());
				ps.executeUpdate();
				history.resetIsModified();
				// postupdate+

				// postupdate-
				afterUpdate(history);
			}
			// 淇濆瓨鎵╁睍灞炴€?
			saveAttribute(history);
		} catch (SQLException e) {
			throw new BPMException(e);
		} finally {
			getManager().close(ps);
			freeConnection(c);
		}
		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYHISTORY on a
	 * Activityhistory pObject
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @return pObject resulting Activityhistory pObject
	 */
	// 72
	private EIActivityInstHistory decodeRow(ResultSet rs) throws SQLException {
		DbActivityInstHistory pObject = (DbActivityInstHistory) createActivityHistory();
		if (rs.getObject(1) != null)
			pObject.setActivityHistoryId(rs.getString(1));
		if (rs.getObject(2) != null)
			pObject.setProcessInstId(rs.getString(2));
		if (rs.getObject(3) != null)
			pObject.setActivityDefId(rs.getString(3));
		if (rs.getObject(4) != null)
			pObject.setActivityInstId(rs.getString(4));
		if (rs.getObject(5) != null)
			pObject.setUrgency(rs.getString(5));
		if (rs.getObject(6) != null)
			pObject.setArrivedTime(new Date(rs.getLong(6)));
		if (rs.getObject(7) != null)
			pObject.setLimitTime(new Date(rs.getLong(7)));
		if (rs.getObject(8) != null)
			pObject.setStartTime(new Date(rs.getLong(8)));
		if (rs.getObject(9) != null)
			pObject.setEndTime(new Date(rs.getLong(9)));
		if (rs.getObject(10) != null)
			pObject.setReceiveMethod(rs.getString(10));
		if (rs.getObject(11) != null)
			pObject.setDealMethod(rs.getString(11));
		if (rs.getObject(12) != null)
			pObject.setRunStatus(rs.getString(12));
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * Transform a ResultSet iterating on the BPM_ACTIVITYHISTORY on a
	 * Activityhistory pObject according to a list of fields
	 * 
	 * @param rs
	 *            the resultset to be transformed
	 * @param fieldList
	 *            table of the field's associated constants
	 * @return pObject resulting Activityhistory pObject
	 */
	// 73
	private EIActivityInstHistory decodeRow(ResultSet rs, int[] fieldList)
			throws SQLException {
		DbActivityInstHistory pObject = (DbActivityInstHistory) createActivityHistory();
		int pos = 0;
		for (int i = 0; i < fieldList.length; i++) {
			switch (fieldList[i]) {
			case ID_ACTIVITYHISTORY_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityHistoryId(rs.getString(pos));
				}
				break;
			case ID_PROCESSINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setProcessInstId(rs.getString(pos));
				}
				break;
			case ID_ACTIVITYDEF_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityDefId(rs.getString(pos));
				}
				break;
			case ID_ACTIVITYINST_ID:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setActivityInstId(rs.getString(pos));
				}
				break;
			case ID_URGENCYTYPE:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setUrgency(rs.getString(pos));
				}
				break;
			case ID_ARRIVEDTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setArrivedTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_LIMITTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setLimitTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_STARTTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setStartTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_ENDTIME:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setEndTime(new Date(rs.getLong(pos)));
				}
				break;
			case ID_RECEIVEMETHOD:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setReceiveMethod(rs.getString(pos));
				}
				break;
			case ID_DEALMETHOD:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setDealMethod(rs.getString(pos));
				}
				break;
			case ID_RUNSTATUS:
				++pos;
				if (rs.getObject(pos) != null) {
					pObject.setRunStatus(rs.getString(pos));
				}
				break;
			}
		}
		pObject.setIsNew(false);
		pObject.resetIsModified();

		return pObject;
	}

	/**
	 * load a unique Activityhistory pObject from an template one giving a c
	 * 
	 * @param pObject
	 *            the Activityhistory pObject to look for
	 * @return the pObject matching the template
	 */
	// 85
	protected EIActivityInstHistory loadObject(EIActivityInstHistory object)
			throws BPMException {
		DbActivityInstHistory pObject = (DbActivityInstHistory) object;
		EIActivityInstHistory[] pReturn = loadObjects(pObject);
		if (pReturn.length == 0)
			return null;
		if (pReturn.length > 1)
			throw new BPMException("More than one element !!");
		return pReturn[0];
	}

	/**
	 * load an array of Activityhistory from a template one
	 * 
	 * @param pObject
	 *            the Activityhistory template to look for
	 * @return all the Activityhistory matching the template
	 */
	// 88
	protected EIActivityInstHistory[] loadObjects(EIActivityInstHistory object)
			throws BPMException {
		DbActivityInstHistory pObject = (DbActivityInstHistory) object;
		Connection c = null;
		PreparedStatement ps = null;
		StringBuffer where = new StringBuffer("");
		StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
				+ " from BPM_ACTIVITYHISTORY WHERE ");
		StringBuffer _sqlWhere = new StringBuffer("");
		try {
			int _dirtyCount = 0;
			if (pObject.isActivityhistoryIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYHISTORY_ID= ?");
			}
			if (pObject.isProcessinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("PROCESSINST_ID= ?");
			}
			if (pObject.isActivitydefIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYDEF_ID= ?");
			}
			if (pObject.isActivityinstIdModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ACTIVITYINST_ID= ?");
			}
			if (pObject.isUrgencytypeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("URGENCYTYPE= ?");
			}
			if (pObject.isArrivedtimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ARRIVEDTIME= ?");
			}
			if (pObject.isLimittimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("LIMITTIME= ?");
			}
			if (pObject.isStarttimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("STARTTIME= ?");
			}
			if (pObject.isEndTimeModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("ENDTIME= ?");
			}
			if (pObject.isRecievestateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RECEIVEMETHOD= ?");
			}
			if (pObject.isDealstateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("DEALMETHOD= ?");
			}
			if (pObject.isRunstateModified()) {
				_dirtyCount++;
				_sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
						.append("RUNSTATUS= ?");
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
			if (pObject.isActivityhistoryIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityHistoryId());
			if (pObject.isProcessinstIdModified())
				ps.setString(++_dirtyCount, pObject.getProcessInstId());
			if (pObject.isActivitydefIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityDefId());
			if (pObject.isActivityinstIdModified())
				ps.setString(++_dirtyCount, pObject.getActivityInstId());
			if (pObject.isUrgencytypeModified())
				ps.setString(++_dirtyCount, pObject.getUrgency());
			if (pObject.isArrivedtimeModified())
				ps.setDouble(++_dirtyCount, pObject.getArrivedTime().getTime());
			if (pObject.isLimittimeModified())
				ps.setDouble(++_dirtyCount, pObject.getLimitTime().getTime());
			if (pObject.isStarttimeModified())
				ps.setDouble(++_dirtyCount, pObject.getStartTime().getTime());
			if (pObject.isEndTimeModified())
				ps.setDouble(++_dirtyCount, pObject.getEndTime().getTime());
			if (pObject.isRecievestateModified())
				ps.setString(++_dirtyCount, pObject.getReceiveMethod());
			if (pObject.isDealstateModified())
				ps.setString(++_dirtyCount, pObject.getDealMethod());
			if (pObject.isRunstateModified())
				ps.setString(++_dirtyCount, pObject.getRunStatus());
			ps.executeQuery();
			return loadByPreparedStatement(ps);
		} catch (SQLException e) {
			throw new BPMException(e);
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
	 * @param dbActivityInst
	 */
	private void putToCache(String uuid, DbActivityInstHistory dbActivityInst) {
		synchronized (uuid.intern()) {
			if (!cache.containsKey(uuid)) {

				cache.put(uuid, dbActivityInst);
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
		StringBuffer sqlWhere = new StringBuffer(
				" WHERE ACTIVITYHISTORY_ID IN (");
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
			EIActivityInstHistory[] list = null;
			try {
				list = loadByWhere(sqlWhere.toString(), null);
			} catch (BPMException e) {
				log
						.error("prefetch the activity instance histoty fail! Where Sql: "
								+ sqlWhere.toString());
				log.error("SqlException is ", e);
				return;
			}
			for (int i = 0; i < list.length; i++) {
				DbActivityInstHistory pd = (DbActivityInstHistory) list[i];
				putToCache(pd.getActivityHistoryId(), pd);
			}
		}
	}
}


