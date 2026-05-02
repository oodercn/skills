package net.ooder.vfs.jdbc;

@FunctionalInterface
public interface ResultSetExtractor<T> {
    T extractData(java.sql.ResultSet rs) throws java.sql.SQLException;
}
