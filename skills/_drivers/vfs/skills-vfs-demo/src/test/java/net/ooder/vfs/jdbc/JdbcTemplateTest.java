package net.ooder.vfs.jdbc;

import net.ooder.vfs.jdbc.dialect.SQLiteDialect;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JdbcTemplate测试")
class JdbcTemplateTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    private JdbcTemplate jdbcTemplate;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        
        jdbcTemplate = new JdbcTemplate(connectionProvider);
        jdbcTemplate.setDialect(new SQLiteDialect());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("查询单个对象测试")
    void testQueryForObject() throws SQLException {
        String sql = "SELECT * FROM VFS_FILE WHERE FILE_ID = ?";
        String fileId = "test-file-id";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("FILE_ID")).thenReturn(fileId);
        when(resultSet.getString("NAME")).thenReturn("test-file");
        
        TestEntity result = jdbcTemplate.queryForObject(sql, new Object[]{fileId}, (rs, rowNum) -> {
            TestEntity entity = new TestEntity();
            entity.setId(rs.getString("FILE_ID"));
            entity.setName(rs.getString("NAME"));
            return entity;
        });
        
        assertNotNull(result);
        assertEquals(fileId, result.getId());
        assertEquals("test-file", result.getName());
        
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("查询列表测试")
    void testQueryForList() throws SQLException {
        String sql = "SELECT * FROM VFS_FILE WHERE FOLDER_ID = ?";
        String folderId = "test-folder-id";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("FILE_ID")).thenReturn("file1", "file2");
        when(resultSet.getString("NAME")).thenReturn("file1", "file2");
        
        List<TestEntity> results = jdbcTemplate.queryForList(sql, new Object[]{folderId}, (rs, rowNum) -> {
            TestEntity entity = new TestEntity();
            entity.setId(rs.getString("FILE_ID"));
            entity.setName(rs.getString("NAME"));
            return entity;
        });
        
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("file1", results.get(0).getId());
        assertEquals("file2", results.get(1).getId());
        
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("更新操作测试")
    void testUpdate() throws SQLException {
        String sql = "UPDATE VFS_FILE SET NAME = ? WHERE FILE_ID = ?";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        int affected = jdbcTemplate.update(sql, "new-name", "test-file-id");
        
        assertEquals(1, affected);
        verify(preparedStatement).setString(1, "new-name");
        verify(preparedStatement).setString(2, "test-file-id");
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("批量更新测试")
    void testBatchUpdate() throws SQLException {
        String sql = "INSERT INTO VFS_FILE (FILE_ID, NAME) VALUES (?, ?)";
        List<Object[]> paramsList = List.of(
            new Object[]{"file1", "File 1"},
            new Object[]{"file2", "File 2"}
        );
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1});
        
        int[] results = jdbcTemplate.batchUpdate(sql, paramsList);
        
        assertEquals(2, results.length);
        assertEquals(1, results[0]);
        assertEquals(1, results[1]);
        
        verify(preparedStatement, times(2)).addBatch();
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("查询整数测试")
    void testQueryForInt() throws SQLException {
        String sql = "SELECT COUNT(*) FROM VFS_FILE WHERE FOLDER_ID = ?";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(10);
        
        int count = jdbcTemplate.queryForInt(sql, "test-folder-id");
        
        assertEquals(10, count);
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("查询Map列表测试")
    void testQueryForMapList() throws SQLException {
        String sql = "SELECT FILE_ID, NAME FROM VFS_FILE";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("FILE_ID");
        when(metaData.getColumnLabel(2)).thenReturn("NAME");
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn("file1", "file2");
        when(resultSet.getObject(2)).thenReturn("File 1", "File 2");
        
        List<Map<String, Object>> results = jdbcTemplate.queryForMapList(sql);
        
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("file1", results.get(0).get("FILE_ID"));
        assertEquals("File 1", results.get(0).get("NAME"));
        
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("异常处理测试")
    void testExceptionHandling() throws SQLException {
        String sql = "SELECT * FROM VFS_FILE WHERE FILE_ID = ?";
        
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("Connection failed"));
        
        assertThrows(Exception.class, () -> {
            jdbcTemplate.queryForObject(sql, new Object[]{"test-id"}, (rs, rowNum) -> null);
        });
        
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @DisplayName("空结果测试")
    void testEmptyResult() throws SQLException {
        String sql = "SELECT * FROM VFS_FILE WHERE FILE_ID = ?";
        
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        TestEntity result = jdbcTemplate.queryForObject(sql, new Object[]{"non-existent-id"}, 
            (rs, rowNum) -> new TestEntity());
        
        assertNull(result);
        verify(connectionProvider).releaseConnection(connection);
    }

    static class TestEntity {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
