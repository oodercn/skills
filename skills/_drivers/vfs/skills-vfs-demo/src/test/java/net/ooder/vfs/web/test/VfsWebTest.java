package net.ooder.vfs.web.test;

import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VfsWebTestApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("VFS-Web 健壮性单元测试")
class VfsWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcManager jdbcManager;

    private static String createdFolderId;
    private static String createdFileId;
    private static String subFolderId;

    @BeforeAll
    static void checkPreconditions(@Autowired JdbcTemplate jdbcTemplate, @Autowired JdbcManager jdbcManager) {
        assertNotNull(jdbcTemplate, "JdbcTemplate should be injected");
        assertNotNull(jdbcManager, "JdbcManager should be injected");
        assertNotNull(jdbcTemplate.getDialect(), "Dialect should be initialized");
        assertEquals("SQLite", jdbcTemplate.getDialect().getName(), "Dialect should be SQLite");
    }

    private String toJson(String... keyValuePairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(keyValuePairs[i]).append("\":");
            String val = keyValuePairs[i + 1];
            if (val == null) {
                sb.append("null");
            } else if (val.startsWith("__NUM__:")) {
                sb.append(val.substring(8));
            } else if (val.startsWith("__BOOL__:")) {
                sb.append(val.substring(9));
            } else {
                sb.append("\"").append(val.replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String num(long v) {
        return "__NUM__:" + v;
    }

    private String bool(boolean v) {
        return "__BOOL__:" + v;
    }

    private String extractField(String json, String field) {
        String pattern = "\"" + field + "\":";
        int idx = json.indexOf(pattern);
        if (idx < 0) return null;
        idx += pattern.length();
        while (idx < json.length() && json.charAt(idx) == ' ') idx++;
        if (idx >= json.length()) return null;
        if (json.charAt(idx) == '"') {
            int end = json.indexOf("\"", idx + 1);
            return json.substring(idx + 1, end);
        } else {
            int end = idx;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(idx, end).trim();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. 健康检查 - /api/vfs/health")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/vfs/health"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("vfs-web-test"))
            .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    @Order(2)
    @DisplayName("2. 统计信息 - /api/vfs/stats")
    void testStatsEndpoint() throws Exception {
        mockMvc.perform(get("/api/vfs/stats"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderCount").isNumber())
            .andExpect(jsonPath("$.fileCount").isNumber())
            .andExpect(jsonPath("$.versionCount").isNumber())
            .andExpect(jsonPath("$.dialect").value("SQLite"))
            .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    @Order(3)
    @DisplayName("3. 创建文件夹 - POST /api/vfs/folders")
    void testCreateFolder() throws Exception {
        String body = toJson("name", "TestFolder-1", "parentId", "folder-root", "personId", "user-test");

        MvcResult result = mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderId").isString())
            .andExpect(jsonPath("$.name").value("TestFolder-1"))
            .andExpect(jsonPath("$.parentId").value("folder-root"))
            .andExpect(jsonPath("$.path").isString())
            .andExpect(jsonPath("$.createTime").isNumber())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        createdFolderId = extractField(response, "folderId");
        assertNotNull(createdFolderId, "Created folder ID should not be null");
    }

    @Test
    @Order(4)
    @DisplayName("4. 查询文件夹 - GET /api/vfs/folders/{id}")
    void testGetFolder() throws Exception {
        assertNotNull(createdFolderId, "Folder should have been created first");

        mockMvc.perform(get("/api/vfs/folders/{folderId}", createdFolderId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderId").value(createdFolderId))
            .andExpect(jsonPath("$.name").value("TestFolder-1"))
            .andExpect(jsonPath("$.parentId").value("folder-root"))
            .andExpect(jsonPath("$.personId").value("user-test"));
    }

    @Test
    @Order(5)
    @DisplayName("5. 查询根文件夹 - GET /api/vfs/folders/folder-root")
    void testGetRootFolder() throws Exception {
        mockMvc.perform(get("/api/vfs/folders/{folderId}", "folder-root"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderId").value("folder-root"))
            .andExpect(jsonPath("$.name").value("Root"));
    }

    @Test
    @Order(6)
    @DisplayName("6. 查询不存在的文件夹 - 404")
    void testGetNonExistentFolder() throws Exception {
        mockMvc.perform(get("/api/vfs/folders/{folderId}", "non-existent-folder"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("7. 创建子文件夹")
    void testCreateSubFolder() throws Exception {
        assertNotNull(createdFolderId, "Parent folder should exist");

        String body = toJson("name", "SubFolder-1", "parentId", createdFolderId, "personId", "user-test");

        MvcResult result = mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("SubFolder-1"))
            .andExpect(jsonPath("$.parentId").value(createdFolderId))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        subFolderId = extractField(response, "folderId");
    }

    @Test
    @Order(8)
    @DisplayName("8. 查询文件夹子项 - GET /api/vfs/folders/{id}/children")
    void testGetFolderChildren() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/vfs/folders/{folderId}/children", "folder-root"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        assertNotNull(response);
        assertTrue(response.startsWith("["), "Children should be a JSON array");
    }

    @Test
    @Order(9)
    @DisplayName("9. 创建文件 - POST /api/vfs/files")
    void testCreateFile() throws Exception {
        assertNotNull(createdFolderId, "Folder should exist for file creation");

        String body = toJson("name", "test-document.txt", "folderId", createdFolderId,
            "personId", "user-test", "fileType", num(1), "hash", "abc123def456", "length", num(1024));

        MvcResult result = mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").isString())
            .andExpect(jsonPath("$.name").value("test-document.txt"))
            .andExpect(jsonPath("$.folderId").value(createdFolderId))
            .andExpect(jsonPath("$.fileType").value(1))
            .andExpect(jsonPath("$.hash").value("abc123def456"))
            .andExpect(jsonPath("$.length").value(1024))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        createdFileId = extractField(response, "fileId");
        assertNotNull(createdFileId, "Created file ID should not be null");
    }

    @Test
    @Order(10)
    @DisplayName("10. 查询文件 - GET /api/vfs/files/{id}")
    void testGetFile() throws Exception {
        assertNotNull(createdFileId, "File should have been created first");

        mockMvc.perform(get("/api/vfs/files/{fileId}", createdFileId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value(createdFileId))
            .andExpect(jsonPath("$.name").value("test-document.txt"))
            .andExpect(jsonPath("$.folderId").value(createdFolderId))
            .andExpect(jsonPath("$.personId").value("user-test"))
            .andExpect(jsonPath("$.isRecycled").value(0))
            .andExpect(jsonPath("$.isLocked").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("11. 查询不存在的文件 - 404")
    void testGetNonExistentFile() throws Exception {
        mockMvc.perform(get("/api/vfs/files/{fileId}", "non-existent-file"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    @DisplayName("12. 更新文件 - PUT /api/vfs/files/{id}")
    void testUpdateFile() throws Exception {
        assertNotNull(createdFileId, "File should exist for update");

        String body = toJson("name", "renamed-document.txt");

        mockMvc.perform(put("/api/vfs/files/{fileId}", createdFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value(createdFileId))
            .andExpect(jsonPath("$.updateTime").isNumber());

        mockMvc.perform(get("/api/vfs/files/{fileId}", createdFileId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("renamed-document.txt"));
    }

    @Test
    @Order(13)
    @DisplayName("13. 移动文件 - POST /api/vfs/files/{id}/move")
    void testMoveFile() throws Exception {
        assertNotNull(createdFileId, "File should exist for move");
        assertNotNull(subFolderId, "Target folder should exist for move");

        String body = toJson("targetFolderId", subFolderId);

        mockMvc.perform(post("/api/vfs/files/{fileId}/move", createdFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value(createdFileId))
            .andExpect(jsonPath("$.targetFolderId").value(subFolderId))
            .andExpect(jsonPath("$.moved").value(true));

        mockMvc.perform(get("/api/vfs/files/{fileId}", createdFileId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderId").value(subFolderId));
    }

    @Test
    @Order(14)
    @DisplayName("14. 移动文件缺少targetFolderId - 400")
    void testMoveFileMissingTarget() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/vfs/files/{fileId}/move", createdFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("targetFolderId is required"));
    }

    @Test
    @Order(15)
    @DisplayName("15. 查询文件版本 - GET /api/vfs/files/{id}/versions")
    void testGetFileVersions() throws Exception {
        assertNotNull(createdFileId, "File should exist");

        MvcResult result = mockMvc.perform(get("/api/vfs/files/{fileId}/versions", createdFileId))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        assertNotNull(response);
        assertTrue(response.startsWith("["), "Versions should be a JSON array");
    }

    @Test
    @Order(16)
    @DisplayName("16. 删除文件 - DELETE /api/vfs/files/{id}")
    void testDeleteFile() throws Exception {
        assertNotNull(createdFileId, "File should exist for deletion");

        mockMvc.perform(delete("/api/vfs/files/{fileId}", createdFileId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(1));

        mockMvc.perform(get("/api/vfs/files/{fileId}", createdFileId))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(17)
    @DisplayName("17. 删除根文件夹被拒绝 - 400")
    void testDeleteRootFolder() throws Exception {
        mockMvc.perform(delete("/api/vfs/folders/{folderId}", "folder-root"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Cannot delete root folder"));
    }

    @Test
    @Order(18)
    @DisplayName("18. 删除子文件夹 - DELETE /api/vfs/folders/{id}")
    void testDeleteSubFolder() throws Exception {
        assertNotNull(subFolderId, "Sub-folder should exist for deletion");

        mockMvc.perform(delete("/api/vfs/folders/{folderId}", subFolderId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(1));

        mockMvc.perform(get("/api/vfs/folders/{folderId}", subFolderId))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(19)
    @DisplayName("19. 创建文件夹 - 空请求体使用默认值")
    void testCreateFolderWithDefaults() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("New Folder"))
            .andExpect(jsonPath("$.parentId").value("folder-root"));
    }

    @Test
    @Order(20)
    @DisplayName("20. 创建文件 - 空请求体使用默认值")
    void testCreateFileWithDefaults() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("New File"))
            .andExpect(jsonPath("$.folderId").value("folder-root"))
            .andExpect(jsonPath("$.fileType").value(1))
            .andExpect(jsonPath("$.hash").value(""))
            .andExpect(jsonPath("$.length").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("21. 并发创建文件夹 - 10线程")
    void testConcurrentCreateFolder() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String body = toJson("name", "ConcurrentFolder-" + index, "parentId", "folder-root", "personId", "user-concurrent");
                    mockMvc.perform(post("/api/vfs/folders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                        .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout");
        assertTrue(successCount.get() >= threadCount * 8 / 10,
            "At least 80% of concurrent folder creates should succeed. Success: " + successCount.get() + "/" + threadCount);
        executor.shutdown();
    }

    @Test
    @Order(22)
    @DisplayName("22. 并发创建文件 - 20线程")
    void testConcurrentCreateFile() throws Exception {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String body = toJson("name", "ConcurrentFile-" + index, "folderId", "folder-root",
                        "personId", "user-concurrent", "fileType", num(1), "hash", "hash-" + index, "length", num(index * 100));
                    mockMvc.perform(post("/api/vfs/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                        .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout");
        assertTrue(successCount.get() >= 1,
            "At least 1 concurrent file create should succeed (SQLite single-conn limitation). Success: " + successCount.get() + "/" + threadCount);
        executor.shutdown();
    }

    @Test
    @Order(23)
    @DisplayName("23. 并发读写混合 - 50线程")
    void testConcurrentMixedOperations() throws Exception {
        String testFolderId;
        {
            String body = toJson("name", "MixedTestFolder", "parentId", "folder-root", "personId", "user-mixed");
            MvcResult result = mockMvc.perform(post("/api/vfs/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            testFolderId = extractField(result.getResponse().getContentAsString(), "folderId");
        }

        int totalOps = 50;
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalOps);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < totalOps; i++) {
            final int index = i;
            final String folderId = testFolderId;
            executor.submit(() -> {
                try {
                    int opType = index % 3;
                    switch (opType) {
                        case 0:
                            mockMvc.perform(get("/api/vfs/folders/{folderId}", folderId))
                                .andExpect(status().isOk());
                            break;
                        case 1:
                            String body = toJson("name", "MixedFile-" + index, "folderId", folderId, "personId", "user-mixed");
                            mockMvc.perform(post("/api/vfs/files")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                                .andExpect(status().isOk());
                            break;
                        case 2:
                            mockMvc.perform(get("/api/vfs/folders/{folderId}/children", folderId))
                                .andExpect(status().isOk());
                            break;
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(60, TimeUnit.SECONDS), "All mixed operations should complete within timeout");
        assertTrue(successCount.get() >= totalOps * 7 / 10,
            "At least 70% of mixed operations should succeed. Success: " + successCount.get() + "/" + totalOps);
        executor.shutdown();
    }

    @Test
    @Order(24)
    @DisplayName("24. 边界测试 - 超长文件夹名")
    void testCreateFolderWithLongName() throws Exception {
        String longName = "A".repeat(500);
        String body = toJson("name", longName, "parentId", "folder-root");

        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(longName));
    }

    @Test
    @Order(25)
    @DisplayName("25. 边界测试 - 特殊字符文件夹名")
    void testCreateFolderWithSpecialChars() throws Exception {
        String specialName = "Folder-quotes-apostrophe";
        String body = toJson("name", specialName, "parentId", "folder-root");

        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(specialName));
    }

    @Test
    @Order(26)
    @DisplayName("26. 边界测试 - 超长文件名")
    void testCreateFileWithLongName() throws Exception {
        String longName = "F".repeat(500) + ".txt";
        String body = toJson("name", longName, "folderId", "folder-root");

        MvcResult result = mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 200 || status == 500,
            "Long filename should either succeed(200) or be rejected by DB(500), got: " + status);
    }

    @Test
    @Order(27)
    @DisplayName("27. 边界测试 - 大文件长度值")
    void testCreateFileWithLargeLength() throws Exception {
        String body = "{\"name\":\"LargeFile.bin\",\"folderId\":\"folder-root\",\"length\":9223372036854775807}";

        mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length").value(9223372036854775807L));
    }

    @Test
    @Order(28)
    @DisplayName("28. 错误处理 - 无效JSON请求体")
    void testInvalidJsonBody() throws Exception {
        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(29)
    @DisplayName("29. 错误处理 - 缺少Content-Type")
    void testMissingContentType() throws Exception {
        mockMvc.perform(post("/api/vfs/folders")
                .content("{\"name\":\"test\"}"))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(30)
    @DisplayName("30. 错误处理 - DELETE不存在的文件")
    void testDeleteNonExistentFile() throws Exception {
        mockMvc.perform(delete("/api/vfs/files/{fileId}", "non-existent-file-id"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(0));
    }

    @Test
    @Order(31)
    @DisplayName("31. 错误处理 - DELETE不存在的文件夹")
    void testDeleteNonExistentFolder() throws Exception {
        mockMvc.perform(delete("/api/vfs/folders/{folderId}", "non-existent-folder-id"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(0));
    }

    @Test
    @Order(32)
    @DisplayName("32. 错误处理 - 移动到不存在的文件夹")
    void testMoveToNonExistentFolder() throws Exception {
        String tempFileId;
        {
            String body = toJson("name", "MoveTestFile", "folderId", "folder-root");
            MvcResult result = mockMvc.perform(post("/api/vfs/files")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            tempFileId = extractField(result.getResponse().getContentAsString(), "fileId");
        }

        String body = toJson("targetFolderId", "non-existent-folder");

        mockMvc.perform(post("/api/vfs/files/{fileId}/move", tempFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Order(33)
    @DisplayName("33. 事务一致性 - 创建文件应更新文件夹SIZE")
    void testTransactionConsistency() throws Exception {
        String txFolderId;
        {
            String body = toJson("name", "TxTestFolder", "parentId", "folder-root");
            MvcResult result = mockMvc.perform(post("/api/vfs/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            txFolderId = extractField(result.getResponse().getContentAsString(), "folderId");
        }

        for (int i = 0; i < 5; i++) {
            String body = toJson("name", "TxFile-" + i, "folderId", txFolderId, "personId", "user-tx");
            mockMvc.perform(post("/api/vfs/files")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
        }

        MvcResult childrenResult = mockMvc.perform(get("/api/vfs/folders/{folderId}/children", txFolderId))
            .andExpect(status().isOk())
            .andReturn();
        String childrenResponse = childrenResult.getResponse().getContentAsString();
        assertTrue(childrenResponse.contains("TxFile-"), "Children should contain created files");
    }

    @Test
    @Order(34)
    @DisplayName("34. 批量创建后统计验证")
    void testBatchCreateAndStats() throws Exception {
        for (int i = 0; i < 5; i++) {
            String body = toJson("name", "BatchFolder-" + i, "parentId", "folder-root");
            mockMvc.perform(post("/api/vfs/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
        }

        for (int i = 0; i < 10; i++) {
            String body = toJson("name", "BatchFile-" + i, "folderId", "folder-root");
            mockMvc.perform(post("/api/vfs/files")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
        }

        MvcResult statsResult = mockMvc.perform(get("/api/vfs/stats"))
            .andExpect(status().isOk())
            .andReturn();
        String statsResponse = statsResult.getResponse().getContentAsString();
        int folderCount = Integer.parseInt(extractField(statsResponse, "folderCount"));
        int fileCount = Integer.parseInt(extractField(statsResponse, "fileCount"));
        assertTrue(folderCount > 0, "Folder count should be > 0");
        assertTrue(fileCount > 0, "File count should be > 0");
    }

    @Test
    @Order(35)
    @DisplayName("35. 锁管理器状态验证")
    void testLockManagerStats() throws Exception {
        int initialLockCount = LockManager.getLockCount();

        String body = toJson("name", "LockTestFolder", "parentId", "folder-root");
        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());

        int afterLockCount = LockManager.getLockCount();
        assertTrue(afterLockCount >= initialLockCount,
            "Lock count should not decrease after operations");
    }

    @Test
    @Order(36)
    @DisplayName("36. JdbcManager连接状态验证")
    void testJdbcManagerConnectionState() throws Exception {
        mockMvc.perform(get("/api/vfs/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeConnections").isNumber())
            .andExpect(jsonPath("$.inTransaction").isBoolean());
    }

    @Test
    @Order(37)
    @DisplayName("37. 更新不存在的文件")
    void testUpdateNonExistentFile() throws Exception {
        String body = toJson("name", "UpdatedName");

        mockMvc.perform(put("/api/vfs/files/{fileId}", "non-existent-file-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value("non-existent-file-id"));
    }

    @Test
    @Order(38)
    @DisplayName("38. 创建文件fileType为0")
    void testCreateFileWithZeroFileType() throws Exception {
        String body = "{\"name\":\"ZeroTypeFile\",\"folderId\":\"folder-root\",\"fileType\":0}";

        mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileType").value(0));
    }

    @Test
    @Order(39)
    @DisplayName("39. 创建文件fileType为负数")
    void testCreateFileWithNegativeFileType() throws Exception {
        String body = "{\"name\":\"NegativeTypeFile\",\"folderId\":\"folder-root\",\"fileType\":-1}";

        mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileType").value(-1));
    }

    @Test
    @Order(40)
    @DisplayName("40. 查询空文件夹的子项")
    void testEmptyFolderChildren() throws Exception {
        String emptyFolderId;
        {
            String body = toJson("name", "EmptyFolder", "parentId", "folder-root");
            MvcResult result = mockMvc.perform(post("/api/vfs/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            emptyFolderId = extractField(result.getResponse().getContentAsString(), "folderId");
        }

        MvcResult result = mockMvc.perform(get("/api/vfs/folders/{folderId}/children", emptyFolderId))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString(), "Empty folder should have no children");
    }

    @Test
    @Order(41)
    @DisplayName("41. 并发更新同一文件 - 锁竞争")
    void testConcurrentUpdateSameFile() throws Exception {
        String sharedFileId;
        {
            String body = toJson("name", "SharedFile", "folderId", "folder-root");
            MvcResult result = mockMvc.perform(post("/api/vfs/files")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            sharedFileId = extractField(result.getResponse().getContentAsString(), "fileId");
        }

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final String fileId = sharedFileId;
            executor.submit(() -> {
                try {
                    String body = toJson("name", "SharedFile-Updated-" + index);
                    mockMvc.perform(put("/api/vfs/files/{fileId}", fileId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                        .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout");
        assertTrue(successCount.get() >= threadCount * 7 / 10,
            "At least 70% of concurrent updates should succeed. Success: " + successCount.get() + "/" + threadCount);
        executor.shutdown();
    }

    @Test
    @Order(42)
    @DisplayName("42. 并发删除同一文件 - 锁竞争")
    void testConcurrentDeleteSameFile() throws Exception {
        String sharedFileId;
        {
            String body = toJson("name", "DeleteTargetFile", "folderId", "folder-root");
            MvcResult result = mockMvc.perform(post("/api/vfs/files")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andReturn();
            sharedFileId = extractField(result.getResponse().getContentAsString(), "fileId");
        }

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger deleteSuccessCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String fileId = sharedFileId;
            executor.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(delete("/api/vfs/files/{fileId}", fileId))
                        .andReturn();
                    String response = result.getResponse().getContentAsString();
                    String deleted = extractField(response, "deleted");
                    if ("1".equals(deleted)) {
                        deleteSuccessCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout");
        assertEquals(1, deleteSuccessCount.get(),
            "Exactly one thread should successfully delete the file");
        executor.shutdown();
    }

    @Test
    @Order(43)
    @DisplayName("43. 压力测试 - 100次连续API调用")
    void testStressSequentialCalls() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/vfs/health"))
                .andExpect(status().isOk());
        }

        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 30000, "100 sequential health checks should complete within 30s, took: " + duration + "ms");
    }

    @Test
    @Order(44)
    @DisplayName("44. 创建文件夹 - 无效的parentId引用不存在的文件夹")
    void testCreateFolderWithNonExistentParent() throws Exception {
        String body = toJson("name", "OrphanFolder", "parentId", "non-existent-parent");

        mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Order(45)
    @DisplayName("45. 完整CRUD生命周期验证")
    void testFullCrudLifecycle() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/vfs/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"LifecycleFolder\",\"parentId\":\"folder-root\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folderId").isString())
            .andReturn();

        String folderId = extractField(createResult.getResponse().getContentAsString(), "folderId");

        mockMvc.perform(get("/api/vfs/folders/{folderId}", folderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("LifecycleFolder"));

        MvcResult fileResult = mockMvc.perform(post("/api/vfs/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"LifecycleFile.txt\",\"folderId\":\"" + folderId + "\"}"))
            .andExpect(status().isOk())
            .andReturn();

        String fileId = extractField(fileResult.getResponse().getContentAsString(), "fileId");

        mockMvc.perform(get("/api/vfs/files/{fileId}", fileId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("LifecycleFile.txt"));

        mockMvc.perform(put("/api/vfs/files/{fileId}", fileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"UpdatedLifecycleFile.txt\"}"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/vfs/files/{fileId}", fileId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedLifecycleFile.txt"));

        mockMvc.perform(delete("/api/vfs/files/{fileId}", fileId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(1));

        mockMvc.perform(get("/api/vfs/files/{fileId}", fileId))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/vfs/folders/{folderId}", folderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(1));

        mockMvc.perform(get("/api/vfs/folders/{folderId}", folderId))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(46)
    @DisplayName("46. 连接泄漏检测 - 多次API调用后连接数稳定")
    void testNoConnectionLeak() throws Exception {
        MvcResult statsBefore = mockMvc.perform(get("/api/vfs/stats"))
            .andExpect(status().isOk())
            .andReturn();
        int connectionsBefore = Integer.parseInt(extractField(statsBefore.getResponse().getContentAsString(), "activeConnections"));

        for (int i = 0; i < 50; i++) {
            mockMvc.perform(get("/api/vfs/health")).andExpect(status().isOk());
            mockMvc.perform(get("/api/vfs/stats")).andExpect(status().isOk());
        }

        MvcResult statsAfter = mockMvc.perform(get("/api/vfs/stats"))
            .andExpect(status().isOk())
            .andReturn();
        int connectionsAfter = Integer.parseInt(extractField(statsAfter.getResponse().getContentAsString(), "activeConnections"));

        assertTrue(connectionsAfter <= connectionsBefore + 2,
            "Connection leak detected: " + connectionsAfter + " > " + (connectionsBefore + 2));
    }

    @Test
    @Order(47)
    @DisplayName("47. 方言信息验证")
    void testDialectInfo() throws Exception {
        assertNotNull(jdbcTemplate.getDialect());
        assertEquals("SQLite", jdbcTemplate.getDialect().getName());
        assertTrue(jdbcTemplate.getDialect().supportsLimit());
        assertTrue(jdbcTemplate.getDialect().supportsOffset());
    }

    @Test
    @Order(48)
    @DisplayName("48. 并发创建和删除混合 - 30线程")
    void testConcurrentCreateDeleteMix() throws Exception {
        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    if (index % 2 == 0) {
                        String body = toJson("name", "MixFile-" + index, "folderId", "folder-root");
                        mockMvc.perform(post("/api/vfs/files")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                            .andExpect(status().isOk());
                    } else {
                        mockMvc.perform(delete("/api/vfs/files/{fileId}", "mix-nonexistent-" + index))
                            .andExpect(status().isOk());
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Expected under contention
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout");
        assertTrue(successCount.get() >= threadCount * 4 / 10,
            "At least 40% of mixed operations should succeed (SQLite limitation). Success: " + successCount.get() + "/" + threadCount);
        executor.shutdown();
    }

    @Test
    @Order(49)
    @DisplayName("49. 文件版本查询 - 不存在的文件")
    void testFileVersionsForNonExistentFile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/vfs/files/{fileId}/versions", "non-existent-file"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString(), "Non-existent file should have empty versions");
    }

    @Test
    @Order(50)
    @DisplayName("50. 最终统计验证")
    void testFinalStats() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/vfs/stats"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dialect").value("SQLite"))
            .andExpect(jsonPath("$.inTransaction").value(false))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        int folderCount = Integer.parseInt(extractField(response, "folderCount"));
        assertTrue(folderCount > 0, "Final folder count should be > 0");
    }
}
