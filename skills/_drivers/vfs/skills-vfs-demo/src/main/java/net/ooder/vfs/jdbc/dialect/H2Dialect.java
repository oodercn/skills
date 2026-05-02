package net.ooder.vfs.jdbc.dialect;

public class H2Dialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "H2";
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
        return "SELECT NEXTVAL('" + sequenceName + "')";
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT CURRENT_TIMESTAMP()";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = UPPER('" + tableName + "')";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = UPPER('" + tableName + 
               "') AND COLUMN_NAME = UPPER('" + columnName + "')";
    }
}
