package net.ooder.vfs.jdbc.dialect;

public class DB2Dialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "DB2";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        return "SELECT * FROM (SELECT T.*, ROW_NUMBER() OVER() AS RN FROM (" + 
               sql + ") AS T) AS TT WHERE RN BETWEEN " + (offset + 1) + " AND " + (offset + limit);
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ") AS _count_tmp";
    }
    
    @Override
    public String getLimitClause(int offset, int limit) {
        return "FETCH FIRST " + limit + " ROWS ONLY";
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
        return "SELECT NEXTVAL FOR " + sequenceName + " FROM SYSIBM.SYSDUMMY1";
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT CURRENT TIMESTAMP FROM SYSIBM.SYSDUMMY1";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM SYSCAT.TABLES WHERE TABNAME = UPPER('" + tableName + "')";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM SYSCAT.COLUMNS WHERE TABNAME = UPPER('" + tableName + 
               "') AND COLNAME = UPPER('" + columnName + "')";
    }
}
