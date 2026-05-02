package net.ooder.vfs.jdbc.dialect;

public class PostgreSQLDialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "PostgreSQL";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ") AS _count_tmp";
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
        return "SELECT CURRENT_TIMESTAMP";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = '" + tableName + 
               "' AND column_name = '" + columnName + "'";
    }
}
