package net.ooder.vfs.jdbc.dialect;

public interface DatabaseDialect {
    
    String getName();
    
    String getPagedSql(String sql, int offset, int limit);
    
    String getCountSql(String sql);
    
    String getLimitClause(int offset, int limit);
    
    boolean supportsLimit();
    
    boolean supportsOffset();
    
    String getSequenceNextValSql(String sequenceName);
    
    String getCurrentTimestampSql();
    
    String getTableExistsSql(String tableName);
    
    String getColumnExistsSql(String tableName, String columnName);
}
