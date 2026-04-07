/**
 * $RCSfile: DbManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Manager provides connections and manage transaction transparently. The
 * Manager is a singleton, you get its instance with the getInstance() method.
 * It is used by all the XxxxManager to get database connection.
 * 
 * Updated to use Spring DataSource with HikariCP connection pool.
 */
@Component
public class DbManager {
    
    private static final Logger log = LoggerFactory.getLogger(DbManager.class);

    private static DbManager manager_instance;

    @Autowired
    private DataSource dataSource;

    private static final InheritableThreadLocal<Connection> trans_conn = new InheritableThreadLocal<>();

    @PostConstruct
    public void init() {
        manager_instance = this;
    }

    /**
     * Return the manager singleton instance.
     */
    public static DbManager getInstance() {
        if (manager_instance == null) {
            throw new IllegalStateException("DbManager not initialized. Spring context may not be loaded yet.");
        }
        return manager_instance;
    }

    /**
     * Get an auto commit connection. Normally you you not need this method that
     * much ;-)
     * 
     * @return an auto commit connection.
     */
    public synchronized Connection getConnection() throws SQLException {
        Connection tc = trans_conn.get();
        if (tc != null && !tc.isClosed()) {
            return tc;
        }
        return dataSource.getConnection();
    }

    /**
     * Release the connection. Normally you should not need this method ;-)
     */
    public synchronized void releaseConnection(Connection c) {
        Connection tc = trans_conn.get();
        if (tc != null) {
            return;
        }

        try {
            if (c != null && !c.isClosed()) {
                c.setAutoCommit(true);
                c.close();
            }
        } catch (SQLException x) {
            log.error("Could not release the connection: ", x);
        }
    }

    /**
     * When working within a transaction, you should invoke this method first.
     * The connection is returned just in case you need to set the isolation
     * level or else.
     * 
     * @return a non-auto commit connection with the default transaction
     *         isolation level.
     */
    public Connection beginTransaction() throws SQLException {
        Connection c = getConnection();
        c.setAutoCommit(false);
        trans_conn.set(c);
        return c;
    }

    /**
     * Release connection used for the transaction and perform a commit or
     * rollback.
     * 
     * @param commit
     *            tells whether this connection should be committed: true for
     *            commit(), false for rollback()
     */
    public void endTransaction(boolean commit) throws SQLException {
        Connection c = trans_conn.get();

        try {
            if (c == null || c.isClosed()) {
                return;
            }
            if (commit) {
                c.commit();
            } else {
                c.rollback();
            }
        } finally {
            trans_conn.set(null);
            releaseConnection(c);
        }
    }

    // //////////////////////////////////////////////////
    // Utils method
    // //////////////////////////////////////////////////

    /**
     * Log a message using the underlying logwriter, if not null.
     */
    public void log(String message) {
        log.trace(message);
    }

    /**
     * Close the passed Statement.
     */
    public void close(Statement s) {
        try {
            if (s != null)
                s.close();
        } catch (SQLException x) {
            log.error("Could not close statement!: ", x);
        }
    }

    /**
     * Close the passed ResultSet.
     */
    public void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException x) {
            log.error("Could not close result set!: ", x);
        }
    }

    /**
     * Close the passed Statement and ResultSet.
     */
    public void close(Statement s, ResultSet rs) {
        close(rs);
        close(s);
    }
}
