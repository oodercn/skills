package net.ooder.bpm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${bpm.database.init-mode:never}")
    private String initMode;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("开始初始化BPM数据库...");
        log.info("数据库URL: {}", datasourceUrl);
        log.info("初始化模式: {}", initMode);

        // 如果初始化模式为never，则跳过初始化
        if ("never".equalsIgnoreCase(initMode)) {
            log.info("初始化模式为never，跳过数据库初始化");
            verifyData();
            return;
        }

        try {
            // 检查数据库是否已初始化
            if (isDatabaseInitialized()) {
                log.info("数据库已初始化，跳过初始化步骤");
                verifyData();
                return;
            }

            dropOldTables();
            initSchema();
            initData();
            verifyData();
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
        }
    }

    /**
     * 检查数据库是否已初始化
     */
    private boolean isDatabaseInitialized() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BPM_PROCESSDEF", Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            // 表不存在，表示数据库未初始化
            log.info("数据库表不存在，需要初始化");
            return false;
        }
    }

    private void dropOldTables() {
        log.info("删除旧表...");
        String[] tables = {
            "BPM_LISTENER", "BPM_PROCESSDEF_PARTICIPANT", "BPM_ACTIVITYINST_PARTICIPANT", "BPM_USER",
            "BPM_ATTRIBUTEINST", "BPM_ATTRIBUTEDEF",
            "BPM_ROUTEINST", "BPM_ACTIVITYINSTHISTORY", "BPM_ACTIVITYINST",
            "BPM_PROCESSINST", "BPM_ROUTEDEF", "BPM_ACTIVITYDEF",
            "BPM_PROCESSDEF_VERSION", "BPM_PROCESSDEFVERSION", "BPM_PROCESSDEF"
        };
        
        for (String table : tables) {
            try {
                jdbcTemplate.execute("DROP TABLE IF EXISTS " + table);
            } catch (Exception e) {
                log.warn("删除表 {} 失败: {}", table, e.getMessage());
            }
        }
        log.info("旧表删除完成");
    }

    private void initSchema() throws Exception {
        log.info("执行schema.sql...");
        ClassPathResource schemaResource = new ClassPathResource("db/schema.sql");
        String schemaSql = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);
        
        String[] statements = schemaSql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty()) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    log.warn("执行SQL失败: {}", e.getMessage());
                }
            }
        }
        log.info("Schema初始化完成");
    }

    private void initData() throws Exception {
        log.info("初始化数据...");
        
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_USER", Integer.class);
        if (userCount != null && userCount == 0) {
            log.info("初始化用户数据...");
            jdbcTemplate.execute("INSERT INTO BPM_USER (USER_ID, USERNAME, DISPLAYNAME, EMAIL, DEPARTMENT, STATUS) VALUES ('user1', 'zhangsan', '张三', 'zhangsan@example.com', '研发部', 'ACTIVE')");
            jdbcTemplate.execute("INSERT INTO BPM_USER (USER_ID, USERNAME, DISPLAYNAME, EMAIL, DEPARTMENT, STATUS) VALUES ('user2', 'lisi', '李四', 'lisi@example.com', '产品部', 'ACTIVE')");
            jdbcTemplate.execute("INSERT INTO BPM_USER (USER_ID, USERNAME, DISPLAYNAME, EMAIL, DEPARTMENT, STATUS) VALUES ('user3', 'wangwu', '王五', 'wangwu@example.com', '运营部', 'ACTIVE')");
            log.info("用户数据初始化完成");
        }
        
        Integer processDefCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM BPM_PROCESSDEF WHERE PROCESSDEF_ID = 'doc-approval-process'", 
            Integer.class
        );
        
        if (processDefCount != null && processDefCount == 0) {
            log.info("执行data.sql初始化流程定义数据...");
            ClassPathResource dataResource = new ClassPathResource("db/data.sql");
            String dataSql = StreamUtils.copyToString(dataResource.getInputStream(), StandardCharsets.UTF_8);
            
            String[] statements = dataSql.split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("INSERT INTO BPM_USER")) {
                    try {
                        jdbcTemplate.execute(trimmed);
                    } catch (Exception e) {
                        log.warn("插入数据失败: {}", e.getMessage());
                    }
                }
            }
            log.info("流程定义数据加载完成");
        } else {
            log.info("流程定义数据已存在，跳过初始化");
        }
        
        Integer participantCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_PROCESSDEF_PARTICIPANT", Integer.class);
        if (participantCount != null && participantCount == 0) {
            log.info("初始化参与者数据...");
            jdbcTemplate.execute("INSERT INTO BPM_PROCESSDEF_PARTICIPANT (PARTICIPANT_ID, PROCESSDEF_ID, ACTIVITYDEF_ID, USER_ID, ROLE_TYPE, PARTICIPANT_TYPE) VALUES ('participant-draft-1', 'doc-approval-process', 'act-draft', 'user1', 'INITIATOR', 'USER')");
            jdbcTemplate.execute("INSERT INTO BPM_PROCESSDEF_PARTICIPANT (PARTICIPANT_ID, PROCESSDEF_ID, ACTIVITYDEF_ID, USER_ID, ROLE_TYPE, PARTICIPANT_TYPE) VALUES ('participant-approve-1', 'doc-approval-process', 'act-approve', 'user2', 'APPROVER', 'USER')");
            jdbcTemplate.execute("INSERT INTO BPM_PROCESSDEF_PARTICIPANT (PARTICIPANT_ID, PROCESSDEF_ID, ACTIVITYDEF_ID, USER_ID, ROLE_TYPE, PARTICIPANT_TYPE) VALUES ('participant-archive-1', 'doc-approval-process', 'act-archive', 'user3', 'ARCHIVER', 'USER')");
            log.info("参与者数据初始化完成");
        }
        
        Integer propertyCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_ACTIVITYDEF_PROPERTY", Integer.class);
        Integer propertyWithParentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_ACTIVITYDEF_PROPERTY WHERE PARENTPROP_ID IS NOT NULL", Integer.class);
        log.info("属性数据总数: {}, 有父属性的属性数: {}", propertyCount, propertyWithParentCount);
        
        log.info("重新初始化活动属性数据...");
        jdbcTemplate.execute("DELETE FROM BPM_ACTIVITYDEF_PROPERTY");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-draft-1-workflow', 'act-draft', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y')");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-draft-2-pos', 'act-draft', 'positionCoord', '{\"x\":100,\"y\":200}', NULL, 'WORKFLOW', 'prop-draft-1-workflow', 0, 'Y')");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-approve-1-workflow', 'act-approve', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y')");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-approve-2-pos', 'act-approve', 'positionCoord', '{\"x\":300,\"y\":200}', NULL, 'WORKFLOW', 'prop-approve-1-workflow', 0, 'Y')");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-archive-1-workflow', 'act-archive', 'WORKFLOW', NULL, NULL, 'WORKFLOW', NULL, 0, 'Y')");
        jdbcTemplate.execute("INSERT INTO BPM_ACTIVITYDEF_PROPERTY (PROPERTY_ID, ACTIVITYDEF_ID, PROPNAME, PROPVALUE, PROPCLASS, PROPTYPE, PARENTPROP_ID, ISEXTENSION, CANINSTANTIATE) VALUES ('prop-archive-2-pos', 'act-archive', 'positionCoord', '{\"x\":500,\"y\":300}', NULL, 'WORKFLOW', 'prop-archive-1-workflow', 0, 'Y')");
        log.info("活动属性数据初始化完成");
    }

    private void verifyData() {
        log.info("验证数据库数据...");
        
        try {
            Integer processDefCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BPM_PROCESSDEF", Integer.class);
            Integer versionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BPM_PROCESSDEF_VERSION", Integer.class);
            Integer activityCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BPM_ACTIVITYDEF", Integer.class);
            Integer routeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BPM_ROUTEDEF", Integer.class);
            
            log.info("=== 数据库验证结果 ===");
            log.info("流程定义数量: {}", processDefCount);
            log.info("流程版本数量: {}", versionCount);
            log.info("活动定义数量: {}", activityCount);
            log.info("路由定义数量: {}", routeCount);
            
            log.info("\n=== 流程定义详情 ===");
            jdbcTemplate.query(
                "SELECT PROCESSDEF_ID, DEFNAME, DESCRIPTION, SYSTEMCODE FROM BPM_PROCESSDEF",
                (rs, rowNum) -> {
                    log.info("流程定义: {} - {} ({})", 
                        rs.getString("PROCESSDEF_ID"),
                        rs.getString("DEFNAME"),
                        rs.getString("SYSTEMCODE"));
                    return null;
                }
            );
            
            log.info("\n=== 活动定义详情 ===");
            jdbcTemplate.query(
                "SELECT ACTIVITYDEF_ID, DEFNAME, POSITION FROM BPM_ACTIVITYDEF",
                (rs, rowNum) -> {
                    log.info("活动定义: {} - {} [{}]", 
                        rs.getString("ACTIVITYDEF_ID"),
                        rs.getString("DEFNAME"),
                        rs.getString("POSITION"));
                    return null;
                }
            );
            
            log.info("\n=== 路由定义详情 ===");
            jdbcTemplate.query(
                "SELECT ROUTEDEF_ID, ROUTENAME, FROMACTIVITYDEF_ID, TOACTIVITYDEF_ID FROM BPM_ROUTEDEF",
                (rs, rowNum) -> {
                    log.info("路由定义: {} - {} ({} -> {})", 
                        rs.getString("ROUTEDEF_ID"),
                        rs.getString("ROUTENAME"),
                        rs.getString("FROMACTIVITYDEF_ID"),
                        rs.getString("TOACTIVITYDEF_ID"));
                    return null;
                }
            );
            
            log.info("\n数据库初始化验证完成！");
            
        } catch (Exception e) {
            log.error("验证数据失败", e);
        }
    }
}
