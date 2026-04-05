package net.ooder.vfs.manager.dbimpl;

import net.ooder.common.database.ConnectionManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbManager {
	private static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(),
			DbManager.class);

	private static DbManager manager_instance = new DbManager();

	private static InheritableThreadLocal trans_conn = new InheritableThreadLocal();

	public static DbManager getInstance() {
		return manager_instance;
	}

	public synchronized Connection getConnection() throws SQLException {
		Connection tc = (Connection) trans_conn.get();
		if (tc != null) {
			return tc;
		}
		return ConnectionManagerFactory.getConnection(OrgConstants.VFSCONFIG_KEY.getType());
	}

	public synchronized void releaseConnection(Connection c) {
		Connection tc = (Connection) trans_conn.get();
		if (tc != null) {
			return;
		}

		try {
			if (c != null) {
				c.setAutoCommit(true);
				c.close();
			}
		} catch (SQLException x) {
			log.error("Could not release the connection: ", x);
		}
	}

	public Connection beginTransaction() throws SQLException {
		Connection c = getConnection();
		c.setAutoCommit(false);
		trans_conn.set(c);
		return c;
	}

	public void endTransaction(boolean commit) throws SQLException {
		Connection c = (Connection) trans_conn.get();
		if (c == null) {
			return;
		}

		try {
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

	public void log(String message) {
		log.trace(message);
	}

	public void close(Statement s) {
		try {
			if (s != null)
				s.close();
		} catch (SQLException x) {
			log.error("Could not close statement!: ", x);
		}
		;
	}

	public void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException x) {
			log.error("Could not close result set!: ", x);
		}
		;
	}

	public void close(Statement s, ResultSet rs) {
		close(rs);
		close(s);
	}

}
