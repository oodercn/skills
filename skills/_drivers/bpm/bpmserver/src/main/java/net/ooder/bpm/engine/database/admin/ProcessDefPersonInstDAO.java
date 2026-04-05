package net.ooder.bpm.engine.database.admin;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.jdbc.BaseJdbcDAO;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessDefPersonInstDAO extends BaseJdbcDAO {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, ProcessDefPersonInstDAO.class);

    public static final String UUID = "uuid";
    public static final String PROCESSDEFID = "process_def_id";
    public static final String PERSONID = "person_id";
    public static final String RIGHTCODE = "right_code";

    private ProcessDefPersonInst mapRow(ResultSet rs) throws SQLException {
        ProcessDefPersonInst inst = new ProcessDefPersonInst();
        inst.setUuid(rs.getString("uuid"));
        inst.setProcessDefId(rs.getString("process_def_id"));
        inst.setPersonId(rs.getString("person_id"));
        inst.setRightCode(rs.getString("right_code"));
        return inst;
    }

    public List<ProcessDefPersonInst> findByExample(ProcessDefPersonInst example) {
        log.debug("finding ProcessDefPersonInst instance by example");
        List<ProcessDefPersonInst> results = new ArrayList<>();
        if (example.getProcessDefId() != null) {
            results = findByProperty(PROCESSDEFID, example.getProcessDefId());
        } else if (example.getPersonId() != null) {
            results = findByProperty(PERSONID, example.getPersonId());
        } else if (example.getRightCode() != null) {
            results = findByProperty(RIGHTCODE, example.getRightCode());
        } else {
            results = findAll();
        }
        return results;
    }

    public void update(ProcessDefPersonInst transientInstance) {
        log.debug("updating ProcessDefPersonInst instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE bpm_process_def_person_inst SET process_def_id=?, person_id=?, right_code=? WHERE uuid=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, transientInstance.getProcessDefId());
            ps.setString(2, transientInstance.getPersonId());
            ps.setString(3, transientInstance.getRightCode());
            ps.setString(4, transientInstance.getUuid());
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

    public void delete(ProcessDefPersonInst processDefPersonInst) {
        log.debug("deleting ProcessDefPersonInst instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "DELETE FROM bpm_process_def_person_inst WHERE uuid=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, processDefPersonInst.getUuid());
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

    public ProcessDefPersonInst findById(String id) {
        log.debug("getting ProcessDefPersonInst instance with id: " + id);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcessDefPersonInst instance = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_process_def_person_inst WHERE uuid=?";
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

    public List<ProcessDefPersonInst> findByProperty(String propertyName, Object value) {
        log.debug("finding ProcessDefPersonInst instance with property: " + propertyName + ", value: " + value);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProcessDefPersonInst> list = new ArrayList<>();
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_process_def_person_inst WHERE " + propertyName + "=?";
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

    public List<ProcessDefPersonInst> findAll() {
        log.debug("finding all ProcessDefPersonInst instances");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProcessDefPersonInst> list = new ArrayList<>();
        try {
            conn = getConnection();
            String sql = "SELECT * FROM bpm_process_def_person_inst";
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

    public void insert(ProcessDefPersonInst instance) {
        log.debug("insert ProcessDefPersonInst instance");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO bpm_process_def_person_inst (uuid, process_def_id, person_id, right_code) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, instance.getUuid());
            ps.setString(2, instance.getProcessDefId());
            ps.setString(3, instance.getPersonId());
            ps.setString(4, instance.getRightCode());
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

    public List<ProcessDefPersonInst> findByProcessDefId(String processdefId) {
        return findByProperty(PROCESSDEFID, processdefId);
    }

    public List<ProcessDefPersonInst> findByPersonId(String personId) {
        return findByProperty(PERSONID, personId);
    }

    public List<ProcessDefPersonInst> findByRightCode(String rightCode) {
        return findByProperty(RIGHTCODE, rightCode);
    }
}
