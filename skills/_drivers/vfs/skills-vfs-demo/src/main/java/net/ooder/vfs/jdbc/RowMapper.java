package net.ooder.vfs.jdbc;

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException;
}
