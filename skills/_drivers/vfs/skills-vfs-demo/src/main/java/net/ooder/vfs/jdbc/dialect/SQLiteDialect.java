package net.ooder.vfs.jdbc.dialect;

public class SQLiteDialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "SQLite";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ")";
    }
    
    @Override
    public String getLimitClause(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }
    
    @Override
    public boolean supportsLimit() {
        return true;
    }
    
    @Override
    public boolean supportsOffset() {
        return true;
    }
    
    @Override
    public String getSequenceNextValSql(String sequenceName) {
        throw new UnsupportedOperationException("SQLite does not support sequences");
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT datetime('now')";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM pragma_table_info('" + tableName + "') WHERE name='" + columnName + "'";
    }
}
