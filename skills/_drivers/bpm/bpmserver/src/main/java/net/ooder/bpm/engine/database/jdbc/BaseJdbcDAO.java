package net.ooder.bpm.engine.database.jdbc;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.DbManager;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseJdbcDAO {
    
    protected final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, getClass());
    
    protected Connection getConnection() throws SQLException {
        return DbManager.getInstance().getConnection();
    }
    
    protected void releaseConnection(Connection conn) {
        DbManager.getInstance().releaseConnection(conn);
    }
    
    protected void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("Failed to close ResultSet", e);
            }
        }
    }
    
    protected void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.error("Failed to close PreparedStatement", e);
            }
        }
    }
    
    protected void close(ResultSet rs, PreparedStatement ps) {
        close(rs);
        close(ps);
    }
}
