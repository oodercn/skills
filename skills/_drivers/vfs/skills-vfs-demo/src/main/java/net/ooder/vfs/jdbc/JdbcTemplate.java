package net.ooder.vfs.jdbc;

import net.ooder.vfs.jdbc.dialect.DatabaseDialect;
import net.ooder.vfs.jdbc.dialect.DialectFactory;
import net.ooder.vfs.jdbc.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcTemplate {
    
    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
    
    private final ConnectionProvider connectionProvider;
    private DatabaseDialect dialect;
    private final Map<String, DatabaseDialect> dialectCache = new ConcurrentHashMap<>();
    
    public JdbcTemplate(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    public JdbcTemplate(ConnectionProvider connectionProvider, String driverClassName) {
        this.connectionProvider = connectionProvider;
        this.dialect = DialectFactory.getDialect(driverClassName);
    }
    
    public void setDialect(DatabaseDialect dialect) {
        this.dialect = dialect;
    }
    
    public DatabaseDialect getDialect() {
        return this.dialect;
    }
    
    public void initDialect(Connection connection) throws SQLException {
        if (this.dialect == null) {
            this.dialect = DialectFactory.getDialect(connection);
        }
    }
    
    private boolean isInTransaction() {
        if (connectionProvider instanceof JdbcManager) {
            return ((JdbcManager) connectionProvider).isInTransaction();
        }
        return false;
    }
    
    public <T> T queryForObject(String sql, Object[] params, RowMapper<T> rowMapper) throws VFSException {
        return queryForObject(sql, params, rowMapper, null);
    }
    
    public <T> T queryForObject(String sql, Object[] params, RowMapper<T> rowMapper, Connection conn) throws VFSException {
        List<T> results = queryForList(sql, params, rowMapper, conn);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public <T> List<T> queryForList(String sql, Object[] params, RowMapper<T> rowMapper) throws VFSException {
        return queryForList(sql, params, rowMapper, null);
    }
    
    public <T> List<T> queryForList(String sql, Object[] params, RowMapper<T> rowMapper, Connection conn) throws VFSException {
        List<T> results = new ArrayList<>();
        boolean externalConnection = (conn != null);
        boolean inTransaction = isInTransaction();
        
        try {
            if (conn == null) {
                conn = connectionProvider.getConnection();
            }
            initDialect(conn);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                
                try (ResultSet rs = ps.executeQuery()) {
                    int rowNum = 0;
                    while (rs.next()) {
                        results.add(rowMapper.mapRow(rs, rowNum++));
                    }
                }
            }
        } catch (SQLException e) {
            throw new VFSException("查询失败: " + sql, e);
        } finally {
            if (!externalConnection && !inTransaction && conn != null) {
                connectionProvider.releaseConnection(conn);
            }
        }
        
        return results;
    }
    
    public <T> T query(String sql, Object[] params, ResultSetExtractor<T> extractor) throws VFSException {
        return query(sql, params, extractor, null);
    }
    
    public <T> T query(String sql, Object[] params, ResultSetExtractor<T> extractor, Connection conn) throws VFSException {
        boolean externalConnection = (conn != null);
        boolean inTransaction = isInTransaction();
        
        try {
            if (conn == null) {
                conn = connectionProvider.getConnection();
            }
            initDialect(conn);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                
                try (ResultSet rs = ps.executeQuery()) {
                    return extractor.extractData(rs);
                }
            }
        } catch (SQLException e) {
            throw new VFSException("查询失败: " + sql, e);
        } finally {
            if (!externalConnection && !inTransaction && conn != null) {
                connectionProvider.releaseConnection(conn);
            }
        }
    }
    
    public List<Map<String, Object>> queryForMapList(String sql, Object... params) throws VFSException {
        return queryForList(sql, params, (rs, rowNum) -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                map.put(columnName, value);
            }
            
            return map;
        });
    }
    
    public int update(String sql, Object... params) throws VFSException {
        return update(sql, params, null);
    }
    
    public int update(String sql, Object[] params, Connection conn) throws VFSException {
        boolean externalConnection = (conn != null);
        boolean inTransaction = isInTransaction();
        
        try {
            if (conn == null) {
                conn = connectionProvider.getConnection();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new VFSException("更新失败: " + sql, e);
        } finally {
            if (!externalConnection && !inTransaction && conn != null) {
                connectionProvider.releaseConnection(conn);
            }
        }
    }
    
    public int[] batchUpdate(String sql, List<Object[]> paramsList) throws VFSException {
        return batchUpdate(sql, paramsList, null);
    }
    
    public int[] batchUpdate(String sql, List<Object[]> paramsList, Connection conn) throws VFSException {
        if (paramsList == null || paramsList.isEmpty()) {
            return new int[0];
        }
        
        boolean externalConnection = (conn != null);
        boolean inTransaction = isInTransaction();
        
        try {
            if (conn == null) {
                conn = connectionProvider.getConnection();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Object[] params : paramsList) {
                    setParameters(ps, params);
                    ps.addBatch();
                }
                
                return ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new VFSException("批量更新失败: " + sql, e);
        } finally {
            if (!externalConnection && !inTransaction && conn != null) {
                connectionProvider.releaseConnection(conn);
            }
        }
    }
    
    public int queryForInt(String sql, Object... params) throws VFSException {
        Integer result = queryForObject(sql, params, (rs, rowNum) -> rs.getInt(1));
        return result != null ? result : 0;
    }
    
    public long queryForLong(String sql, Object... params) throws VFSException {
        Long result = queryForObject(sql, params, (rs, rowNum) -> rs.getLong(1));
        return result != null ? result : 0L;
    }
    
    public String queryForString(String sql, Object... params) throws VFSException {
        return queryForObject(sql, params, (rs, rowNum) -> rs.getString(1));
    }
    
    public <T> List<T> queryForPagedList(String sql, Object[] params, RowMapper<T> rowMapper, 
                                         int offset, int limit) throws VFSException {
        if (dialect == null) {
            throw new VFSException("数据库方言未初始化，请先调用initDialect()方法");
        }
        
        String pagedSql = dialect.getPagedSql(sql, offset, limit);
        return queryForList(pagedSql, params, rowMapper);
    }
    
    public int queryForCount(String sql, Object... params) throws VFSException {
        if (dialect == null) {
            throw new VFSException("数据库方言未初始化，请先调用initDialect()方法");
        }
        
        String countSql = dialect.getCountSql(sql);
        return queryForInt(countSql, params);
    }
    
    public String generateInsertSql(String tableName, String[] columnNames) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(columnNames[i]);
        }
        
        sql.append(") VALUES (");
        
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        
        sql.append(")");
        
        return sql.toString();
    }
    
    public String generateUpdateSql(String tableName, String[] columnNames, String whereClause) {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");
        
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(columnNames[i]).append(" = ?");
        }
        
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }
    
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                
                if (param == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Long) {
                    ps.setLong(i + 1, (Long) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else if (param instanceof Float) {
                    ps.setFloat(i + 1, (Float) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else if (param instanceof java.util.Date) {
                    ps.setTimestamp(i + 1, new Timestamp(((java.util.Date) param).getTime()));
                } else if (param instanceof java.sql.Date) {
                    ps.setDate(i + 1, (java.sql.Date) param);
                } else if (param instanceof Timestamp) {
                    ps.setTimestamp(i + 1, (Timestamp) param);
                } else if (param instanceof byte[]) {
                    ps.setBytes(i + 1, (byte[]) param);
                } else if (param instanceof Blob) {
                    ps.setBlob(i + 1, (Blob) param);
                } else if (param instanceof Clob) {
                    ps.setClob(i + 1, (Clob) param);
                } else {
                    ps.setObject(i + 1, param);
                }
            }
        }
    }
    
    public void execute(String sql) throws VFSException {
        execute(sql, null);
    }
    
    public void execute(String sql, Connection conn) throws VFSException {
        boolean externalConnection = (conn != null);
        boolean inTransaction = isInTransaction();
        
        try {
            if (conn == null) {
                conn = connectionProvider.getConnection();
            }
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new VFSException("执行SQL失败: " + sql, e);
        } finally {
            if (!externalConnection && !inTransaction && conn != null) {
                connectionProvider.releaseConnection(conn);
            }
        }
    }
    
    public boolean tableExists(String tableName) throws VFSException {
        if (dialect == null) {
            throw new VFSException("数据库方言未初始化");
        }
        
        String sql = dialect.getTableExistsSql(tableName);
        int count = queryForInt(sql);
        return count > 0;
    }
    
    public boolean columnExists(String tableName, String columnName) throws VFSException {
        if (dialect == null) {
            throw new VFSException("数据库方言未初始化");
        }
        
        String sql = dialect.getColumnExistsSql(tableName, columnName);
        int count = queryForInt(sql);
        return count > 0;
    }
}
