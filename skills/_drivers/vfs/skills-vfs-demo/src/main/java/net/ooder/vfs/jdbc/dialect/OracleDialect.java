package net.ooder.vfs.jdbc.dialect;

public class OracleDialect implements DatabaseDialect {
    
    @Override
    public String getName() {
        return "Oracle";
    }
    
    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        int end = offset + limit;
        return "SELECT * FROM (SELECT ROWNUM AS RN, T.* FROM (" + sql + 
               ") T WHERE ROWNUM <= " + end + ") WHERE RN > " + offset;
    }
    
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ")";
    }
    
    @Override
    public String getLimitClause(int offset, int limit) {
        return "ROWNUM BETWEEN " + (offset + 1) + " AND " + (offset + limit);
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
        return "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
    }
    
    @Override
    public String getCurrentTimestampSql() {
        return "SELECT SYSDATE FROM DUAL";
    }
    
    @Override
    public String getTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = UPPER('" + tableName + "')";
    }
    
    @Override
    public String getColumnExistsSql(String tableName, String columnName) {
        return "SELECT COUNT(*) FROM USER_TAB_COLUMNS WHERE TABLE_NAME = UPPER('" + tableName + 
               "') AND COLUMN_NAME = UPPER('" + columnName + "')";
    }
}
