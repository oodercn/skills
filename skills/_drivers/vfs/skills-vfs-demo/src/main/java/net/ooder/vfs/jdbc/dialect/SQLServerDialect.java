package net.ooder.vfs.jdbc.dialect;

public class SQLServerDialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "SQLServer";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        return "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY (SELECT 0)) AS RN FROM (" + 
               sql + ") AS T) AS TT WHERE RN BETWEEN " + (offset + 1) + " AND " + (offset + limit);
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ") AS _count_tmp";
    }
    
    @Override
    public String getLimitClause(int offset, int limit) {
        return "OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
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
        return "SELECT NEXT VALUE FOR " + sequenceName;
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT GETDATE()";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableName + "'";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tableName + 
               "' AND COLUMN_NAME = '" + columnName + "'";
    }
}
