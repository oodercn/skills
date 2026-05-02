package net.ooder.vfs.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
    
    Connection getConnection() throws SQLException;
    
    void releaseConnection(Connection connection);
    
    String getDriverClassName();
}
