/**
 * $RCSfile: DbParticipantSelectManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.expression;

import net.ooder.annotation.FormulaParams;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.common.FormulaType;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.esd.manager.formula.ParticipantSelect;

import java.io.Serializable;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统人员选择公式管理
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
public class DbParticipantSelectManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbParticipantSelectManager.class);

    public static final int ID_PARTICIPANT_SELECT_ID = 0;

    public static final int TYPE_PARTICIPANT_SELECT_ID = Types.VARCHAR;

    public static final String NAME_PARTICIPANT_SELECT_ID = "PARTICIPANT_SELECT_ID";

    public static final int ID_SELECTNAME = 1;

    public static final int TYPE_SELECTNAME = Types.VARCHAR;

    public static final String NAME_SELECTNAME = "SELECTNAME";

    public static final int ID_SELECTENNAME = 2;

    public static final int TYPE_SELECTENNAME = Types.VARCHAR;

    public static final String NAME_SELECTENNAME = "SELECTENNAME";

    public static final int ID_FORMULA = 3;

    public static final int TYPE_FORMULA = Types.VARCHAR;

    public static final String NAME_FORMULA = "FORMULA";


    public static final int ID_TYPE = 4;

    public static final int TYPE_TYPE = Types.VARCHAR;

    public static final String NAME_TYPE = "TYPE";

    public static final int ID_SELECTDESC = 5;

    public static final int TYPE_SELECTDESC = Types.VARCHAR;

    public static final String NAME_SELECTDESC = "SELECTDESC";

    private static final String TABLE_NAME = "RT_PARTICIPANT_SELECT";

    /**
     * create an array of string containing all the fields of the
     * RT_PARTICIPANT_SELECT table.
     */
    private static final String[] FIELD_NAMES = {
            "RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID",
            "RT_PARTICIPANT_SELECT.SELECTNAME",
            "RT_PARTICIPANT_SELECT.SELECTENNAME",
            "RT_PARTICIPANT_SELECT.FORMULA",
            "RT_PARTICIPANT_SELECT.TYPE", "RT_PARTICIPANT_SELECT.SELECTDESC"};

    /**
     * create an array of string containing all the fields of the
     * RT_PARTICIPANT_SELECT table.
     */
    private static final String[] TABLEFIELD_NAMES = {"PARTICIPANT_SELECT_ID",
            "SELECTNAME", "SELECTENNAME", "FORMULA", "TYPE", "SELECTDESC"};

    /**
     * Field that contains the comma separated fields of the
     * RT_PARTICIPANT_SELECT table
     */
    private static final String ALL_FIELDS = "RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID"
            + ",RT_PARTICIPANT_SELECT.SELECTNAME"
            + ",RT_PARTICIPANT_SELECT.SELECTENNAME"
            + ",RT_PARTICIPANT_SELECT.FORMULA"
            + ",RT_PARTICIPANT_SELECT.TYPE"
            + ",RT_PARTICIPANT_SELECT.SELECTDESC";

    private static DbParticipantSelectManager singleton = new DbParticipantSelectManager();

    /**
     * Get the RtParticipantSelectManager singleton
     *
     * @return RtParticipantSelectManager
     */
    synchronized public static DbParticipantSelectManager getInstance() {
        return singleton;
    }

    /**
     * Set your own RtParticipantSelectManager instance, this is optional. By
     * default we provide it for you.
     */
    synchronized public static void setInstance(
            DbParticipantSelectManager instance) {
        singleton = instance;
    }

    /**
     * cache
     */
    Cache cache = null; // 流程定义cache

    /**
     * cache是否可用
     */
    boolean cacheEnabled;

    public DbParticipantSelectManager() {
        cache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY,
                "ParticipantSelectCache");
        cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(
                BPMConstants.CONFIG_KEY).isCacheEnabled();
        ;
    }

    /**
     * Creates a new RtParticipantSelectBean instance.
     *
     * @return the new RtParticipantSelectBean
     */
    public DbParticipantSelect createParticipantSelect() {
        return new DbParticipantSelect();
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
     * @param c the connection to release
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
     * Load a RtParticipantSelectBean from the RT_PARTICIPANT_SELECT using its
     * key fields.
     *
     * @return a unique RtParticipantSelectBean
     */
    public DbParticipantSelect loadByKey(String participantSelectId)
            throws SQLException {
        DbParticipantSelect participant = (DbParticipantSelect) cache
                .get(participantSelectId);
        if (participant != null) {
            return participant;
        }

        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            if (log.isDebugEnabled())
                log
                        .debug("SELECT "
                                + ALL_FIELDS
                                + " FROM RT_PARTICIPANT_SELECT WHERE RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID=?");
            ps = c
                    .prepareStatement(
                            "SELECT "
                                    + ALL_FIELDS
                                    + " FROM RT_PARTICIPANT_SELECT WHERE RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID=?",
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, participantSelectId);
            DbParticipantSelect pReturn[] = loadByPreparedStatement(ps);
            if (pReturn.length < 1)
                return null;
            else {
                participant = pReturn[0];
                putToCache(participantSelectId, participant);
                return participant;
            }
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    /**
     * Load all the rows from RT_PARTICIPANT_SELECT.
     *
     * @return an array of RtParticipantSelectManager pObject
     */
    public DbParticipantSelect[] loadAll() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            if (log.isDebugEnabled())
                log.debug("SELECT " + ALL_FIELDS
                        + " FROM RT_PARTICIPANT_SELECT");
            ps = c.prepareStatement("SELECT " + ALL_FIELDS
                            + " FROM RT_PARTICIPANT_SELECT",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            return loadByPreparedStatement(ps);
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    /**
     * Load all the elements using a preparedstatement.
     *
     * @param ps the PreparedStatement to be used
     * @return an array of RtParticipantSelectBean
     */
    public DbParticipantSelect[] loadByPreparedStatement(PreparedStatement ps)
            throws SQLException {
        return loadByPreparedStatement(ps, null);
    }

    /**
     * Load all the elements using a preparedstatement specifying a list of
     * fields to be retreived.
     *
     * @param ps        the PreparedStatement to be used
     * @param fieldList table of the field's associated constants
     * @return an array of RtParticipantSelectBean
     */
    public DbParticipantSelect[] loadByPreparedStatement(PreparedStatement ps,
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
            return (DbParticipantSelect[]) v
                    .toArray(new DbParticipantSelect[0]);
        } finally {
            if (v != null) {
                v.clear();
                v = null;
            }
            getManager().close(rs);
        }
    }

    /**
     * retreive an array of RtParticipantSelectBean given a sql where clausis.
     *
     * @param where the sql where clausis
     * @return the resulting RtParticipantSelectBean table
     */
    public List<ParticipantSelect> loadByWhere(String where) throws SQLException {
        DbParticipantSelect[] participants = loadByWhere(where,
                new int[]{ID_PARTICIPANT_SELECT_ID});
        List result = new DbParticipantSelectList(participants);
        return Collections.unmodifiableList(result);
    }

    /**
     * retreive an array of RtParticipantSelectBean given a sql where clausis,
     * and a list of fields.
     *
     * @param where     the sql where clausis
     * @param fieldList table of the field's associated constants
     * @return the resulting RtParticipantSelectBean table
     */
    public DbParticipantSelect[] loadByWhere(String where, int[] fieldList)
            throws SQLException {
        String sql = null;
        if (fieldList == null)
            sql = "select " + ALL_FIELDS + " from RT_PARTICIPANT_SELECT "
                    + where;
        else {
            StringBuffer buff = new StringBuffer(128);
            buff.append("select ");
            for (int i = 0; i < fieldList.length; i++) {
                if (i != 0)
                    buff.append(",");
                buff.append(FIELD_NAMES[fieldList[i]]);
            }
            buff.append(" from RT_PARTICIPANT_SELECT ");
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

            return (DbParticipantSelect[]) v
                    .toArray(new DbParticipantSelect[0]);
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
    public int deleteByKey(String participantSelectId) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            if (log.isDebugEnabled())
                log
                        .debug("DELETE from RT_PARTICIPANT_SELECT WHERE RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID=?");
            ps = c
                    .prepareStatement(
                            "DELETE from RT_PARTICIPANT_SELECT WHERE RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID=?",
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, participantSelectId);
            return ps.executeUpdate();
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    /**
     * Delete rows according to an object
     *
     * @param pObject the RtParticipantSelectBean object(s) to be deleted
     * @return the number of deleted objects
     */
    public int delete(DbParticipantSelect pObject) throws SQLException {
        if (pObject.isParticipantSelectIdInitialized() == true)
            return deleteByKey(pObject.getParticipantSelectId());
        Connection c = null;
        PreparedStatement ps = null;
        StringBuffer sql = null;
        try {
            sql = new StringBuffer("DELETE FROM RT_PARTICIPANT_SELECT WHERE ");
            int _dirtyAnd = 0;
            if (pObject.isParticipantSelectIdInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("PARTICIPANT_SELECT_ID").append("=?");
                _dirtyAnd++;
            }
            if (pObject.isSelectnameInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("SELECTNAME").append("=?");
                _dirtyAnd++;
            }
            if (pObject.isSelectennameInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("SELECTENNAME").append("=?");
                _dirtyAnd++;
            }
            if (pObject.isFormulaInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("FORMULA").append("=?");
                _dirtyAnd++;
            }
            if (pObject.isTypeInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("TYPE").append("=?");
                _dirtyAnd++;
            }
            if (pObject.isSelectdescInitialized()) {
                if (_dirtyAnd > 0)
                    sql.append(" AND ");
                sql.append("SELECTDESC").append("=?");
                _dirtyAnd++;
            }
            if (log.isDebugEnabled())
                log.debug(sql.toString());
            c = getConnection();
            ps = c.prepareStatement(sql.toString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int _dirtyCount = 0;
            if (pObject.isParticipantSelectIdInitialized())
                ps.setString(++_dirtyCount, pObject.getParticipantSelectId());
            if (pObject.isSelectnameInitialized())
                ps.setString(++_dirtyCount, pObject.getSelectName());
            if (pObject.isSelectennameInitialized())
                ps.setString(++_dirtyCount, pObject.getSelectenName());
            if (pObject.isFormulaInitialized())
                ps.setString(++_dirtyCount, pObject.getFormula());
            if (pObject.isTypeInitialized())
                ps.setString(++_dirtyCount, pObject.getFormulaType().getType());
            if (pObject.isSelectdescInitialized())
                ps.setString(++_dirtyCount, pObject.getSelectDesc());
            int _rows = ps.executeUpdate();
            return _rows;
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    /**
     * Delete rows from RT_PARTICIPANT_SELECT table by where clausis
     *
     * @param where the clausis
     * @return the number of deleted rows
     */
    public int deleteByWhere(String where) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            String delByWhereSQL = "DELETE FROM RT_PARTICIPANT_SELECT " + where;
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
     * Save into the database an array of RtParticipantSelectBean pObjects
     *
     * @param pObjects the RtParticipantSelectBean pObject table to be saved
     * @return the Objects to be saved
     */
    public DbParticipantSelect[] save(DbParticipantSelect[] pObjects)
            throws SQLException {
        for (int iIndex = 0; iIndex < pObjects.length; iIndex++) {
            save(pObjects[iIndex]);
        }
        return pObjects;
    }

    /**
     * before the save of the RtParticipantSelectBean pObject.
     *
     * @param pObject the RtParticipantSelectBean pObject to be saved
     */
    void beforeInsert(DbParticipantSelect pObject) throws SQLException {
    }

    /**
     * after the save of the RtParticipantSelectBean pObject
     *
     * @param pObject the RtParticipantSelectBean pObject to be saved
     */
    void afterInsert(DbParticipantSelect pObject) throws SQLException {
    }

    /**
     * before the update of the RtParticipantSelectBean pObject.
     *
     * @param pObject the RtParticipantSelectBean pObject to be updated
     */
    void beforeUpdate(DbParticipantSelect pObject) throws SQLException {
    }

    /**
     * after the update of the RtParticipantSelectBean pObject.
     *
     * @param pObject the RtParticipantSelectBean pObject to be updated
     */
    void afterUpdate(DbParticipantSelect pObject) throws SQLException {
    }

    /**
     * Save into the database the RtParticipantSelectBean pObject
     *
     * @param pObject the RtParticipantSelectBean pObject to be saved
     */
    public DbParticipantSelect save(DbParticipantSelect pObject)
            throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        StringBuffer _sql = null;
        try {
            c = getConnection();
            if (pObject.isNew()) { // SAVE
                beforeInsert(pObject);
                int _dirtyCount = 0;
                _sql = new StringBuffer("INSERT into RT_PARTICIPANT_SELECT (");
                if (pObject.isParticipantSelectIdModified()) {
                    _sql.append("PARTICIPANT_SELECT_ID").append(",");
                    _dirtyCount++;
                }
                if (pObject.isSelectnameModified()) {
                    _sql.append("SELECTNAME").append(",");
                    _dirtyCount++;
                }
                if (pObject.isSelectennameModified()) {
                    _sql.append("SELECTENNAME").append(",");
                    _dirtyCount++;
                }
                if (pObject.isFormulaModified()) {
                    _sql.append("FORMULA").append(",");
                    _dirtyCount++;
                }
                if (pObject.isTypeModified()) {
                    _sql.append("TYPE").append(",");
                    _dirtyCount++;
                }
                if (pObject.isSelectdescModified()) {
                    _sql.append("SELECTDESC").append(",");
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
                if (pObject.isParticipantSelectIdModified())
                    ps.setString(++_dirtyCount, pObject
                            .getParticipantSelectId());
                if (pObject.isSelectnameModified())
                    ps.setString(++_dirtyCount, pObject.getSelectName());
                if (pObject.isSelectennameModified())
                    ps.setString(++_dirtyCount, pObject.getSelectenName());
                if (pObject.isFormulaModified())
                    ps.setString(++_dirtyCount, pObject.getFormula());
                if (pObject.isTypeModified())
                    ps.setString(++_dirtyCount, pObject.getFormulaType().getType());
                if (pObject.isSelectdescModified())
                    ps.setString(++_dirtyCount, pObject.getSelectDesc());
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
                _sql = new StringBuffer("UPDATE RT_PARTICIPANT_SELECT SET ");
                if (pObject.isParticipantSelectIdModified())
                    _sql.append("PARTICIPANT_SELECT_ID").append("=?,");
                if (pObject.isSelectnameModified())
                    _sql.append("SELECTNAME").append("=?,");
                if (pObject.isSelectennameModified())
                    _sql.append("SELECTENNAME").append("=?,");
                if (pObject.isFormulaModified())
                    _sql.append("FORMULA").append("=?,");
                if (pObject.isTypeModified())
                    _sql.append("TYPE").append("=?,");
                if (pObject.isSelectdescModified())
                    _sql.append("SELECTDESC").append("=?,");
                _sql.setLength(_sql.length() - 1);
                _sql.append(" WHERE ");
                _sql.append("RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID=?");
                if (log.isDebugEnabled())
                    log.debug(_sql.toString());
                ps = c.prepareStatement(_sql.toString(),
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                int _dirtyCount = 0;
                if (pObject.isParticipantSelectIdModified()) {
                    if (pObject.getParticipantSelectId() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject
                                .getParticipantSelectId());
                }
                if (pObject.isSelectnameModified()) {
                    if (pObject.getSelectName() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject.getSelectName());
                }
                if (pObject.isSelectennameModified()) {
                    if (pObject.getSelectenName() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject.getSelectenName());
                }
                if (pObject.isFormulaModified()) {
                    if (pObject.getFormula() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject.getFormula());
                }
                if (pObject.isTypeModified()) {
                    if (pObject.getFormulaType() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject.getFormulaType().getType());
                }
                if (pObject.isSelectdescModified()) {
                    if (pObject.getSelectDesc() == null)
                        ps.setNull(++_dirtyCount, 12);
                    else
                        ps.setString(++_dirtyCount, pObject.getSelectDesc());
                }
                if (_dirtyCount != 0) {

                    ps.setString(++_dirtyCount, pObject
                            .getParticipantSelectId());
                    ps.executeUpdate();
                    pObject.resetIsModified();
                    // postupdate+

                    // postupdate-
                    afterUpdate(pObject);
                }
            }
            saveParameter(pObject, c);
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
        return pObject;
    }

    /**
     * Transform a ResultSet iterating on the RT_PARTICIPANT_SELECT on a
     * RtParticipantSelectBean pObject
     *
     * @param rs the resultset to be transformed
     * @return pObject resulting RtParticipantSelectBean pObject
     */
    public DbParticipantSelect decodeRow(ResultSet rs) throws SQLException {
        DbParticipantSelect pObject = createParticipantSelect();
        if (rs.getObject(1) != null)
            pObject.setParticipantSelectId(rs.getString(1));
        if (rs.getObject(2) != null)
            pObject.setSelectName(rs.getString(2));
        if (rs.getObject(3) != null)
            pObject.setSelectenName(rs.getString(3));
        if (rs.getObject(4) != null)
            pObject.setFormula(rs.getString(4));
        if (rs.getObject(5) != null)
            pObject.setFormulaType(FormulaType.fromType(rs.getString(5)));
        if (rs.getObject(6) != null)
            pObject.setSelectDesc(rs.getString(6));
        pObject.setIsNew(false);
        pObject.resetIsModified();

        return pObject;
    }

    /**
     * Transform a ResultSet iterating on the RT_PARTICIPANT_SELECT on a
     * RtParticipantSelectBean pObject according to a list of fields
     *
     * @param rs        the resultset to be transformed
     * @param fieldList table of the field's associated constants
     * @return pObject resulting RtParticipantSelectBean pObject
     */
    public DbParticipantSelect decodeRow(ResultSet rs, int[] fieldList)
            throws SQLException {
        DbParticipantSelect pObject = createParticipantSelect();
        int pos = 0;
        for (int i = 0; i < fieldList.length; i++) {
            switch (fieldList[i]) {
                case ID_PARTICIPANT_SELECT_ID:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setParticipantSelectId(rs.getString(pos));
                    }
                    break;
                case ID_SELECTNAME:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setSelectName(rs.getString(pos));
                    }
                    break;
                case ID_SELECTENNAME:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setSelectenName(rs.getString(pos));
                    }
                    break;
                case ID_FORMULA:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setFormula(rs.getString(pos));
                    }
                    break;
                case ID_TYPE:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setFormulaType(FormulaType.fromType(rs.getString(pos)));
                    }
                    break;
                case ID_SELECTDESC:
                    ++pos;
                    if (rs.getObject(pos) != null) {
                        pObject.setSelectDesc(rs.getString(pos));
                    }
                    break;
            }
        }
        pObject.setIsNew(false);
        pObject.resetIsModified();

        return pObject;
    }

    /**
     * load a unique RtParticipantSelectBean pObject from an template one giving
     * a c
     *
     * @param pObject the RtParticipantSelectBean pObject to look for
     * @return the pObject matching the template
     */
    public DbParticipantSelect loadObject(DbParticipantSelect pObject)
            throws SQLException {
        DbParticipantSelect[] pReturn = loadObjects(pObject);
        if (pReturn.length == 0)
            return null;
        if (pReturn.length > 1)
            throw new SQLException("More than one element !!");
        return pReturn[0];
    }

    /**
     * load an array of RtParticipantSelectBean from a template one
     *
     * @param pObject the RtParticipantSelectBean template to look for
     * @return all the RtParticipantSelectBean matching the template
     */
    public DbParticipantSelect[] loadObjects(DbParticipantSelect pObject)
            throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        StringBuffer where = new StringBuffer("");
        StringBuffer _sql = new StringBuffer("SELECT " + ALL_FIELDS
                + " from RT_PARTICIPANT_SELECT WHERE ");
        StringBuffer _sqlWhere = new StringBuffer("");
        try {
            int _dirtyCount = 0;
            if (pObject.isParticipantSelectIdModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("PARTICIPANT_SELECT_ID= ?");
            }
            if (pObject.isSelectnameModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("SELECTNAME= ?");
            }
            if (pObject.isSelectennameModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("SELECTENNAME= ?");
            }
            if (pObject.isFormulaModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("FORMULA= ?");
            }
            if (pObject.isTypeModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("TYPE= ?");
            }
            if (pObject.isSelectdescModified()) {
                _dirtyCount++;
                _sqlWhere.append((_sqlWhere.length() == 0) ? " " : " AND ")
                        .append("SELECTDESC= ?");
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
            if (pObject.isParticipantSelectIdModified())
                ps.setString(++_dirtyCount, pObject.getParticipantSelectId());
            if (pObject.isSelectnameModified())
                ps.setString(++_dirtyCount, pObject.getSelectName());
            if (pObject.isSelectennameModified())
                ps.setString(++_dirtyCount, pObject.getSelectenName());
            if (pObject.isFormulaModified())
                ps.setString(++_dirtyCount, pObject.getFormula());
            if (pObject.isTypeModified())
                ps.setString(++_dirtyCount, pObject.getFormulaType().getType());
            if (pObject.isSelectdescModified())
                ps.setString(++_dirtyCount, pObject.getSelectDesc());
            ps.executeQuery();
            return loadByPreparedStatement(ps);
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    private static String LOAD_EXPRESSION_PARAMETER = "SELECT "
            + " PARAMETER_ID, " + " PARAMETERCODE, " + " PARAMETERNAME, " + " PARAMETERENNAME, "
            + " PARAMETERDESC, " + " PARAMETERVALUE, " + " PARAMETERTYPE, " + "PARTICIPANT_SELECT_ID, " + "SINGLE"
            + " FROM RT_EXPRESSION_PARAMETER "
            + " WHERE PARTICIPANT_SELECT_ID = ? ";

    private static String LOAD_EXPRESSION_PARAMETERBY_ID = "SELECT "
            + " PARAMETER_ID, " + " PARAMETERCODE, " + " PARAMETERNAME, " + " PARAMETERENNAME, "
            + " PARAMETERDESC, " + " PARAMETERVALUE, " + " PARAMETERTYPE, " + "PARTICIPANT_SELECT_ID, " + "SINGLE"
            + " FROM RT_EXPRESSION_PARAMETER "
            + " WHERE PARAMETER_ID = ? ";

    private static String DELETE_EXPRESSION_PARAMETER = " DELETE FROM "
            + " RT_EXPRESSION_PARAMETER "
            + " WHERE  PARTICIPANT_SELECT_ID = ? ";

    private static String SAVE_EXPROCESS_PARAMETER = "INSERT INTO"
            + " RT_EXPRESSION_PARAMETER ( " + " PARAMETER_ID, "
            + " PARTICIPANT_SELECT_ID, " + " PARAMETERCODE, "
            + " PARAMETERNAME, " + " PARAMETERENNAME, " + " PARAMETERDESC, " + " PARAMETERVALUE, " + " PARAMETERTYPE ," + " SINGLE "
            + " ) VALUES ( " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, " + " ?, "
            + " ? ," + " ? " + " ) ";

    public void loadParameter(DbParticipantSelect participant) {
        Connection c = null;
        PreparedStatement ps = null;
        String _sql = LOAD_EXPRESSION_PARAMETER;
        List parameterList = participant.getParameterList();
        try {
            if (log.isDebugEnabled())
                log.debug(_sql);
            c = getConnection();
            ps = c.prepareStatement(_sql.toString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, participant.getParticipantSelectId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String parameterId = rs.getString("PARAMETER_ID");
                String parameterCode = rs.getString("PARAMETERCODE");
                String parameterName = rs.getString("PARAMETERNAME");
                String parameterenName = rs.getString("PARAMETERENNAME");
                String parameterDesc = rs.getString("PARAMETERDESC");
                String parameterValue = rs.getString("PARAMETERVALUE");
                String parameterType = rs.getString("PARAMETERTYPE");
                Integer single = rs.getInt("SINGLE");


                DbExpressionParameter parameter = new DbExpressionParameter();
                parameter.setParameterId(parameterId);
                parameter.setParameterCode(parameterCode);
                parameter.setParameterName(parameterName);
                parameter.setParameterenName(parameterenName);
                parameter.setParameterDesc(parameterDesc);
                parameter.setParameterValue(parameterValue);
                parameter.setParticipantSelectId(participant.getParticipantSelectId());
                parameter.setParameterType(FormulaParams.fromType(parameterType));
                if (single == null || single == 0) {
                    parameter.setSingle(false);
                }

                parameterList.add(parameter);
            }
            participant.setParameterList(parameterList);
        } catch (SQLException e) {
            log.error("load participant expression parameter from "
                    + participant.getParticipantSelectId() + " failed!", e);
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
    }

    public DbExpressionParameter loadParameterById(String parameterId) {
        Connection c = null;
        PreparedStatement ps = null;
        String _sql = LOAD_EXPRESSION_PARAMETERBY_ID;
        DbExpressionParameter parameter = null;
        //	List parameterList = participant.getParameterList();
        try {
            if (log.isDebugEnabled())
                log.debug(_sql);
            c = getConnection();
            ps = c.prepareStatement(_sql.toString(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, parameterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                parameter = new DbExpressionParameter();
                //	String parameterId = rs.getString("PARAMETER_ID");
                String parameterCode = rs.getString("PARAMETERCODE");
                String parameterName = rs.getString("PARAMETERNAME");
                String parameterenName = rs.getString("PARAMETERENNAME");
                String parameterDesc = rs.getString("PARAMETERDESC");
                String parameterType = rs.getString("PARAMETERTYPE");
                String parameterValue = rs.getString("PARAMETERVALUE");
                String participantId = rs.getString("PARTICIPANT_SELECT_ID");
                Integer single = rs.getInt("SINGLE");

                parameter.setParameterId(parameterId);
                parameter.setParameterCode(parameterCode);
                parameter.setParameterName(parameterName);
                parameter.setParameterenName(parameterenName);
                parameter.setParameterDesc(parameterDesc);
                parameter.setParticipantSelectId(participantId);
                parameter.setParameterValue(parameterValue);
                parameter.setParameterType(FormulaParams.fromType(parameterType));
                if (single == null || single == 0) {
                    parameter.setSingle(false);
                }

            }

        } catch (SQLException e) {
            log.error("load participant expression parameter from  failed!", e);
        } finally {
            getManager().close(ps);
            freeConnection(c);
        }
        return parameter;
    }


    private void saveParameter(DbParticipantSelect participant, Connection c) {
        if (participant.parameterList == null) {
            return;
        }

        PreparedStatement ps = null;

        try {
            // delete all parameter first
            String sql = DELETE_EXPRESSION_PARAMETER;
            if (log.isDebugEnabled())
                log.debug(sql);
            ps = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, participant.getParticipantSelectId());
            ps.executeUpdate();

            // insert it one by one
            getManager().close(ps);
            sql = SAVE_EXPROCESS_PARAMETER;
            if (log.isDebugEnabled())
                log.debug(sql);
            ps = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            List parameterList = participant.parameterList;
            for (int i = 0; i < parameterList.size(); i++) {
                DbExpressionParameter parameter = (DbExpressionParameter) parameterList
                        .get(i);
                ps.setString(1, parameter.getParameterId());
                ps.setString(2, participant.getParticipantSelectId());
                ps.setString(3, parameter.getParameterCode());
                ps.setString(4, parameter.getParameterName());
                ps.setString(5, parameter.getParameterenName());
                ps.setString(6, parameter.getParameterDesc());
                ps.setString(7, parameter.getParameterValue());
                FormulaParams formulaParams = parameter.getParameterType();
                if (formulaParams == null) {
                    formulaParams = FormulaParams.UNKNOW;
                }
                ps.setString(8, formulaParams.getType());
                ps.setInt(9, parameter.getSingle() ? 1 : 0);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("save participant expression parameter from "
                    + participant.getParticipantSelectId() + " failed!", e);
        } finally {
            getManager().close(ps);
        }

    }

    /**
     * @param participantSelectId
     * @param participant
     */
    private void putToCache(String participantSelectId,
                            DbParticipantSelect participant) {
        synchronized (participantSelectId.intern()) {
            if (cache.get(participantSelectId) == null) {
                cache.put(participantSelectId, participant);
            }
        }

    }

    /**
     * @param ids
     */
    public void prepareCache(List ids) {
        StringBuffer sqlWhere = new StringBuffer(
                " WHERE PARTICIPANT_SELECT_ID IN (");
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
            DbParticipantSelect[] list = null;
            try {
                list = loadByWhere(sqlWhere.toString(), null);
            } catch (SQLException e) {
                log.error("prefetch the process instance fail! Where Sql: "
                        + sqlWhere.toString());
                log.error("SqlException is ", e);
                return;
            }
            for (int i = 0; i < list.length; i++) {
                DbParticipantSelect ps = (DbParticipantSelect) list[i];
                putToCache(ps.getParticipantSelectId(), ps);
            }
        }

    }
}


