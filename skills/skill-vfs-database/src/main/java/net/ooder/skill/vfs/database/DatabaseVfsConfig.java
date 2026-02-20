package net.ooder.skill.vfs.database;

import net.ooder.common.CommonConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.conf.Query;
import net.ooder.vfs.adapter.FileAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseVfsConfig {

    private static final Log log = LogFactory.getLog("vfs", DatabaseVfsConfig.class);

    private static DatabaseVfsConfig instance;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String filePath;
    private FileAdapter fileAdapter;
    private Map<String, Query> queryMap = new HashMap<>();

    private DatabaseVfsConfig() {
        init();
    }

    public static synchronized DatabaseVfsConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseVfsConfig();
        }
        return instance;
    }

    private void init() {
        dbUrl = getConfigValue("vfs.db.url", "jdbc:mysql://localhost:3306/vfs");
        dbUsername = getConfigValue("vfs.db.username", "root");
        dbPassword = getConfigValue("vfs.db.password", "");
        filePath = getConfigValue("vfs.file.path", "./data/vfs-files");

        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        fileAdapter = new DatabaseFileAdapter(filePath);
        initQueries();

        log.info("DatabaseVfsConfig initialized: dbUrl=" + dbUrl + ", filePath=" + filePath);
    }

    private void initQueries() {
        Query fileObjectQuery = new Query();
        fileObjectQuery.setName("FileObject");
        
        Query.SqlClause basicClause = new Query.SqlClause();
        basicClause.setName("basic");
        basicClause.setMainClause("SELECT * FROM VFS_FILE_OBJECT");
        basicClause.setInsertClause("INSERT INTO VFS_FILE_OBJECT");
        basicClause.setDeleteClause("DELETE FROM VFS_FILE_OBJECT");
        
        Map<String, Query.ColumnMapping> columnMappings = new HashMap<>();
        columnMappings.put("objId", new Query.ColumnMapping("OBJ_ID"));
        columnMappings.put("name", new Query.ColumnMapping("NAME"));
        columnMappings.put("hash", new Query.ColumnMapping("HASH"));
        columnMappings.put("path", new Query.ColumnMapping("PATH"));
        columnMappings.put("length", new Query.ColumnMapping("LENGTH"));
        columnMappings.put("rootPath", new Query.ColumnMapping("ROOT_PATH"));
        columnMappings.put("adapter", new Query.ColumnMapping("ADAPTER"));
        columnMappings.put("createTime", new Query.ColumnMapping("CREATE_TIME"));
        basicClause.setColumnMappings(columnMappings);
        
        Map<String, Query.SqlClause> clauses = new HashMap<>();
        clauses.put("basic", basicClause);
        fileObjectQuery.setSqlClauses(clauses);
        
        queryMap.put("FileObject", fileObjectQuery);
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = CommonConfig.getValue(key);
        if (value == null) {
            value = System.getProperty(key.replace(".", "_"), System.getenv(key.replace(".", "_").toUpperCase()));
        }
        return value != null ? value : defaultValue;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getFilePath() {
        return filePath;
    }

    public FileAdapter getFileAdapter() {
        return fileAdapter;
    }

    public FileAdapter getFileAdapter(String customRootPath) {
        if (customRootPath == null || customRootPath.isEmpty()) {
            return fileAdapter;
        }
        return new DatabaseFileAdapter(customRootPath);
    }

    public Query getQuery(String name) {
        return queryMap.get(name);
    }

    public static void setDbUrl(String url) {
        getInstance().dbUrl = url;
    }

    public static void setDbUsername(String username) {
        getInstance().dbUsername = username;
    }

    public static void setDbPassword(String password) {
        getInstance().dbPassword = password;
    }

    public static void setFilePath(String path) {
        getInstance().filePath = path;
        getInstance().fileAdapter = new DatabaseFileAdapter(path);
    }
}
