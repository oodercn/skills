package net.ooder.vfs.jdbc.dialect;

public class MySQLDialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "MySQL";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        return sql + " LIMIT " + offset + ", " + limit;
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ") AS _count_tmp";
    }
    
    @Override
    public String getLimitClause(int offset, int limit) {
        return "LIMIT " + offset + ", " + limit;
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
        return "SELECT NEXTVAL(" + sequenceName + ")";
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT NOW()";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_NAME = '" + tableName + "'";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_NAME = '" + tableName + 
               "' AND COLUMN_NAME = '" + columnName + "'";
    }
}
