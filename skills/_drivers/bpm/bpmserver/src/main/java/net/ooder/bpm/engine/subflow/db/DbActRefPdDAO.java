package net.ooder.bpm.engine.subflow.db;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.jdbc.BaseJdbcDAO;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbActRefPdDAO extends BaseJdbcDAO {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActRefPdDAO.class);

    public static final String ACTIVITYDEFID = "activitydef_id";
    public static final String PROCESSTYPE = "processtype";
    public static final String ISWAITRETURN = "iswaitreturn";
    public static final String MAINPROCESS_VERID = "mainprocess_ver_id";
    public static final String PARENTPROCESS_VERID = "parentprocess_ver_id";
    public static final String DESTPROCESS_VERID = "destprocess_ver_id";

    private ActRefPd mapRow(ResultSet rs) throws SQLException {
        ActRefPd pd = new ActRefPd();
        pd.setActivitydefId(rs.getString("activitydef_id"));
        pd.setProcesstype(rs.getString("processtype"));
        pd.setIswaitreturn(rs.getString("iswaitreturn"));
        pd.setMainprocessVerId(rs.getString("mainprocess_ver_id"));
        pd.setParentprocessVerId(rs.getString("parentprocess_ver_id"));
        pd.setDestprocessVerId(rs.getString("destprocess_ver_id"));
        return pd;
    }

    public void update(ActRefPd transientInstance) {
        log.debug("updating ActRefPd instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE bpm_act_ref_pd SET processtype=?, iswaitreturn=?, mainprocess_ver_id=?, parentprocess_ver_id=?, destprocess_ver_id=? WHERE activitydef_id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, transientInstance.getProcesstype());
            ps.setString(2, transientInstance.getIswaitreturn());
            ps.setString(3, transientInstance.getMainprocessVerId());
            ps.setString(4, transientInstance.getParentprocessVerId());
            ps.setString(5, transientInstance.getDestprocessVerId());
            ps.setString(6, transientInstance.getActivitydefId());
            ps.executeUpdate();
            log.debug("update successful");
        } catch (SQLException e) {
            log.error("update failed", e);
            throw new RuntimeException(e);
        } finally {
            close(ps);
            releaseConnection(conn);
        }
    }

    public void delete(ActRefPd dbActRefPd) {
        log.debug("deleting ActRefPd instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "DELETE FROM bpm_act_ref_pd WHERE activitydef_id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, dbActRefPd.getActivitydefId());
            ps.executeUpdate();
            log.debug("delete successful");
        } catch (SQLException e) {
            log.error("delete failed", e);
            throw new RuntimeException(e);
        } finally {
            close(ps);
            releaseConnection(conn);
        }
    }

    public ActRefPd findById(String id) {
        log.debug("getting ActRefPd instance with id: " + id);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ActRefPd instance = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_act_ref_pd WHERE activitydef_id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                instance = mapRow(rs);
            }
        } catch (SQLException e) {
            log.error("get failed", e);
            throw new RuntimeException(e);
        } finally {
            close(rs, ps);
            releaseConnection(conn);
        }
        return instance;
    }

    public List<ActRefPd> findByProperty(String propertyName, Object value) {
        log.debug("finding ActRefPd instance with property: " + propertyName + ", value: " + value);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ActRefPd> list = new ArrayList<>();
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_act_ref_pd WHERE " + propertyName + "=?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, value);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("find by property name failed", e);
            throw new RuntimeException(e);
        } finally {
            close(rs, ps);
            releaseConnection(conn);
        }
        return list;
    }

    public List<ActRefPd> findAll() {
        log.debug("finding all ActRefPd instances");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ActRefPd> list = new ArrayList<>();
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_act_ref_pd";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("find all failed", e);
            throw new RuntimeException(e);
        } finally {
            close(rs, ps);
            releaseConnection(conn);
        }
        return list;
    }

    public void insert(ActRefPd instance) {
        log.debug("insert ActRefPd instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO bpm_act_ref_pd (activitydef_id, processtype, iswaitreturn, mainprocess_ver_id, parentprocess_ver_id, destprocess_ver_id) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, instance.getActivitydefId());
            ps.setString(2, instance.getProcesstype());
            ps.setString(3, instance.getIswaitreturn());
            ps.setString(4, instance.getMainprocessVerId());
            ps.setString(5, instance.getParentprocessVerId());
            ps.setString(6, instance.getDestprocessVerId());
            ps.executeUpdate();
            log.debug("insert successful");
        } catch (SQLException e) {
            log.error("insert failed", e);
            throw new RuntimeException(e);
        } finally {
            close(ps);
            releaseConnection(conn);
        }
    }

    public List<ActRefPd> findByactivitydefId(String activitydefId) {
        return findByProperty(ACTIVITYDEFID, activitydefId);
    }

    public List<ActRefPd> findByprocesstype(String processtype) {
        return findByProperty(PROCESSTYPE, processtype);
    }

    public List<ActRefPd> findByiswaitreturn(String iswaitreturn) {
        return findByProperty(ISWAITRETURN, iswaitreturn);
    }

    public List<ActRefPd> findBymainprocessVerId(String mainprocessVerId) {
        return findByProperty(MAINPROCESS_VERID, mainprocessVerId);
    }

    public List<ActRefPd> findByparentprocessVerId(String parentprocessVerId) {
        return findByProperty(PARENTPROCESS_VERID, parentprocessVerId);
    }

    public List<ActRefPd> findBydestprocessVerId(String destprocessVerId) {
        return findByProperty(DESTPROCESS_VERID, destprocessVerId);
    }
}
