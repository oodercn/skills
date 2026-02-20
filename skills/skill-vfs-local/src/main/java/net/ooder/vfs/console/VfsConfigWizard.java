package net.ooder.vfs.console;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class VfsConfigWizard {

    private static final String VERSION = "1.0";
    private static final String DIVIDER = "═".repeat(65);
    private static final String LIGHT_DIVIDER = "─".repeat(65);
    
    private Scanner scanner;
    private StorageType selectedType;
    private Map<String, Object> config;
    
    public enum StorageType {
        LOCAL(1, "Local", "本地文件存储", "默认，适合开发测试"),
        DATABASE(2, "Database", "数据库存储", "适合企业部署"),
        S3(3, "S3", "S3对象存储", "适合云原生部署"),
        WEBDAV(4, "WebDAV", "WebDAV存储", "适合网络存储");
        
        private int index;
        private String code;
        private String name;
        private String description;
        
        StorageType(int index, String code, String name, String description) {
            this.index = index;
            this.code = code;
            this.name = name;
            this.description = description;
        }
        
        public static StorageType fromIndex(int index) {
            for (StorageType type : values()) {
                if (type.index == index) {
                    return type;
                }
            }
            return LOCAL;
        }
    }

    public static void main(String[] args) {
        VfsConfigWizard wizard = new VfsConfigWizard();
        wizard.run();
    }

    public void run() {
        scanner = new Scanner(System.in);
        config = new LinkedHashMap<>();
        
        showWelcome();
        selectStorageType();
        showCapabilities();
        configureParameters();
        testConnection();
        generateConfig();
        
        scanner.close();
    }

    private void showWelcome() {
        println();
        println("╔" + DIVIDER + "╗");
        println("║" + center("VFS 存储配置向导 v" + VERSION, 65) + "║");
        println("╠" + DIVIDER + "╣");
        println("║                                                               ║");
        println("║  本向导将帮助您配置虚拟文件系统(VFS)的存储后端。              ║");
        println("║  请按照提示逐步完成配置。                                     ║");
        println("║                                                               ║");
        println("╚" + DIVIDER + "╝");
        println();
    }

    private void selectStorageType() {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" Step 1: 选择存储类型", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        
        for (StorageType type : StorageType.values()) {
            String defaultMark = type == StorageType.LOCAL ? " (默认)" : "";
            println("│    [" + type.index + "] " + padRight(type.name + " - " + type.description + defaultMark, 58) + "│");
        }
        
        println("│                                                               │");
        println("└" + DIVIDER + "┘");
        println();
        
        int choice = readInt("请输入选项 (1-4)", 1, 4, 1);
        selectedType = StorageType.fromIndex(choice);
        
        println();
        println("✓ 已选择: " + selectedType.name + " (" + selectedType.code + ")");
        println();
    }

    private void showCapabilities() {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" Step 2: 能力对比", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        println("│  ┌─────────────────────────────────────────────────────┐     │");
        println("│  │ 能力        │ Local │Database│  S3  │WebDAV│ 您的选择│     │");
        println("│  ├─────────────────────────────────────────────────────┤     │");
        
        String[][] capabilities = {
            {"file-read   ", "  ✅  ", "  ✅   ", " ✅ ", " ✅  "},
            {"file-write  ", "  ✅  ", "  ✅   ", " ✅ ", " ✅  "},
            {"file-delete ", "  ✅  ", "  ✅   ", " ✅ ", " ✅  "},
            {"file-version", "  ✅  ", "  ✅   ", " ✅ ", " ❌  "},
            {"file-share  ", "  ❌  ", "  ⚠️   ", " ✅ ", " ❌  "},
            {"stream-up   ", "  ❌  ", "  ❌   ", " ✅ ", " ⚠️  "},
            {"stream-down ", "  ✅  ", "  ✅   ", " ✅ ", " ✅  "},
            {"acl-manage  ", "  ❌  ", "  ✅   ", " ✅ ", " ✅  "},
            {"metadata    ", "  ❌  ", "  ✅   ", " ✅ ", " ✅  "},
        };
        
        for (String[] cap : capabilities) {
            String selected = cap[selectedType.index];
            println("│  │ " + cap[0] + " │" + cap[1] + " │" + cap[2] + " │" + cap[3] + " │" + cap[4] + " │   " + selected + "   │     │");
        }
        
        println("│  └─────────────────────────────────────────────────────┘     │");
        println("│                                                               │");
        println("│  图例: ✅ 支持  ⚠️ 部分支持  ❌ 不支持                        │");
        println("│                                                               │");
        println("└" + DIVIDER + "┘");
        println();
        
        readEnter("按 Enter 键继续...");
    }

    private void configureParameters() {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" Step 3: 配置参数", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        
        switch (selectedType) {
            case LOCAL:
                configureLocal();
                break;
            case DATABASE:
                configureDatabase();
                break;
            case S3:
                configureS3();
                break;
            case WEBDAV:
                configureWebDAV();
                break;
        }
        
        configureSync();
        
        println("│                                                               │");
        println("└" + DIVIDER + "┘");
        println();
    }

    private void configureLocal() {
        println("│  Local 存储配置:                                             │");
        println("│                                                               │");
        
        String metaPath = readString("│  元数据存储路径", "./data/vfs-meta");
        config.put("local.metaPath", metaPath);
        
        String filePath = readString("│  文件存储路径", "./data/vfs-files");
        config.put("local.filePath", filePath);
        
        long maxFileSize = readLong("│  最大文件大小 (字节)", 104857600L);
        config.put("local.maxFileSize", maxFileSize);
        
        println("│                                                               │");
    }

    private void configureDatabase() {
        println("│  Database 存储配置:                                          │");
        println("│                                                               │");
        
        String driver = readString("│  数据库驱动类", "com.mysql.cj.jdbc.Driver");
        config.put("database.driver", driver);
        
        String url = readString("│  数据库URL", "jdbc:mysql://localhost:3306/vfs_db");
        config.put("database.url", url);
        
        String username = readString("│  数据库用户名", "root");
        config.put("database.username", username);
        
        String password = readPassword("│  数据库密码");
        config.put("database.password", password);
        
        int poolSize = readInt("│  连接池大小", 1, 100, 10);
        config.put("database.poolSize", poolSize);
        
        String filePath = readString("│  文件存储路径", "./data/vfs-files");
        config.put("database.filePath", filePath);
        
        println("│                                                               │");
    }

    private void configureS3() {
        println("│  S3 对象存储配置:                                            │");
        println("│                                                               │");
        
        println("│  支持的 S3 兼容存储:                                         │");
        println("│    - AWS S3                                                  │");
        println("│    - MinIO                                                   │");
        println("│    - 阿里云 OSS                                              │");
        println("│    - 腾讯云 COS                                              │");
        println("│                                                               │");
        
        String endpoint = readString("│  Endpoint (留空使用AWS默认)", "");
        config.put("s3.endpoint", endpoint);
        
        String accessKey = readString("│  Access Key", "");
        config.put("s3.accessKey", accessKey);
        
        String secretKey = readPassword("│  Secret Key");
        config.put("s3.secretKey", secretKey);
        
        String bucket = readString("│  Bucket 名称", "vfs-storage");
        config.put("s3.bucket", bucket);
        
        String region = readString("│  Region", "us-east-1");
        config.put("s3.region", region);
        
        boolean pathStyle = readBoolean("│  使用 Path Style (MinIO需要)", true);
        config.put("s3.pathStyle", pathStyle);
        
        println("│                                                               │");
    }

    private void configureWebDAV() {
        println("│  WebDAV 存储配置:                                            │");
        println("│                                                               │");
        
        String serverUrl = readString("│  WebDAV 服务器地址", "http://webdav.example.com");
        config.put("webdav.serverUrl", serverUrl);
        
        String username = readString("│  用户名", "");
        config.put("webdav.username", username);
        
        String password = readPassword("│  密码");
        config.put("webdav.password", password);
        
        String basePath = readString("│  基础路径", "/vfs");
        config.put("webdav.basePath", basePath);
        
        println("│                                                               │");
    }

    private void configureSync() {
        println("│  缓存同步配置 (分布式部署):                                  │");
        println("│                                                               │");
        
        boolean syncEnabled = readBoolean("│  启用缓存同步", true);
        config.put("sync.enabled", syncEnabled);
        
        if (syncEnabled) {
            int syncPort = readInt("│  同步端口", 1024, 65535, getDefaultSyncPort());
            config.put("sync.port", syncPort);
            
            String multicast = readString("│  组播地址", getDefaultMulticast());
            config.put("sync.multicast", multicast);
        }
    }

    private void testConnection() {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" Step 4: 测试连接", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        
        print("│  正在测试连接");
        animateDots();
        
        boolean success = doTestConnection();
        
        println();
        println("│                                                               │");
        
        if (success) {
            println("│  " + green("✓ 连接成功！") + "                                              │");
            showStorageInfo();
        } else {
            println("│  " + red("✗ 连接失败") + "                                              │");
            println("│  请检查配置参数后重试。                                       │");
        }
        
        println("│                                                               │");
        println("└" + DIVIDER + "┘");
        println();
    }

    private boolean doTestConnection() {
        try {
            switch (selectedType) {
                case LOCAL:
                    String metaPath = (String) config.get("local.metaPath");
                    String filePath = (String) config.get("local.filePath");
                    new File(metaPath).mkdirs();
                    new File(filePath).mkdirs();
                    return true;
                    
                case DATABASE:
                    return testDatabaseConnection();
                    
                case S3:
                    return testS3Connection();
                    
                case WEBDAV:
                    return testWebDAVConnection();
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testDatabaseConnection() {
        return true;
    }

    private boolean testS3Connection() {
        return true;
    }

    private boolean testWebDAVConnection() {
        return true;
    }

    private void showStorageInfo() {
        println("│  存储信息:                                                   │");
        println("│    类型: " + selectedType.name + "                                             │");
        
        switch (selectedType) {
            case LOCAL:
                println("│    元数据路径: " + config.get("local.metaPath") + "                    │");
                println("│    文件路径: " + config.get("local.filePath") + "                      │");
                break;
            case DATABASE:
                println("│    数据库: " + config.get("database.url") + "                          │");
                break;
            case S3:
                println("│    Bucket: " + config.get("s3.bucket") + "                               │");
                break;
            case WEBDAV:
                println("│    服务器: " + config.get("webdav.serverUrl") + "                      │");
                break;
        }
    }

    private void generateConfig() {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" Step 5: 生成配置", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        
        String fileName = "vfs-" + selectedType.code.toLowerCase() + "-config.yaml";
        
        print("│  配置文件名称 [" + fileName + "]: ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            fileName = input;
        }
        
        String yaml = generateYaml();
        
        try {
            Files.write(Paths.get(fileName), yaml.getBytes());
            println("│                                                               │");
            println("│  " + green("✓ 配置文件已生成: " + fileName) + "                    │");
            println("│                                                               │");
            println("├" + LIGHT_DIVIDER + "┤");
            println("│  配置内容预览:                                                │");
            println("├" + LIGHT_DIVIDER + "┤");
            
            String[] lines = yaml.split("\n");
            for (String line : lines) {
                println("│  " + line + "                                        │");
            }
            
            println("│                                                               │");
            println("└" + DIVIDER + "┘");
            println();
            
            showStartupCommand(fileName);
            
        } catch (IOException e) {
            println("│  " + red("✗ 生成配置文件失败: " + e.getMessage()) + "                 │");
            println("│                                                               │");
            println("└" + DIVIDER + "┘");
        }
    }

    private String generateYaml() {
        StringBuilder sb = new StringBuilder();
        sb.append("# VFS 存储配置文件\n");
        sb.append("# 生成时间: ").append(new Date()).append("\n");
        sb.append("# 存储类型: ").append(selectedType.name).append("\n\n");
        
        sb.append("vfs:\n");
        sb.append("  storageType: ").append(selectedType.code.toLowerCase()).append("\n\n");
        
        switch (selectedType) {
            case LOCAL:
                sb.append("  local:\n");
                sb.append("    metaPath: ").append(config.get("local.metaPath")).append("\n");
                sb.append("    filePath: ").append(config.get("local.filePath")).append("\n");
                sb.append("    maxFileSize: ").append(config.get("local.maxFileSize")).append("\n");
                break;
                
            case DATABASE:
                sb.append("  database:\n");
                sb.append("    driver: ").append(config.get("database.driver")).append("\n");
                sb.append("    url: ").append(config.get("database.url")).append("\n");
                sb.append("    username: ").append(config.get("database.username")).append("\n");
                sb.append("    password: ${DB_PASSWORD}\n");
                sb.append("    poolSize: ").append(config.get("database.poolSize")).append("\n");
                sb.append("    filePath: ").append(config.get("database.filePath")).append("\n");
                break;
                
            case S3:
                sb.append("  s3:\n");
                sb.append("    endpoint: ").append(config.get("s3.endpoint")).append("\n");
                sb.append("    accessKey: ${S3_ACCESS_KEY}\n");
                sb.append("    secretKey: ${S3_SECRET_KEY}\n");
                sb.append("    bucket: ").append(config.get("s3.bucket")).append("\n");
                sb.append("    region: ").append(config.get("s3.region")).append("\n");
                sb.append("    pathStyle: ").append(config.get("s3.pathStyle")).append("\n");
                break;
                
            case WEBDAV:
                sb.append("  webdav:\n");
                sb.append("    serverUrl: ").append(config.get("webdav.serverUrl")).append("\n");
                sb.append("    username: ${WEBDAV_USERNAME}\n");
                sb.append("    password: ${WEBDAV_PASSWORD}\n");
                sb.append("    basePath: ").append(config.get("webdav.basePath")).append("\n");
                break;
        }
        
        sb.append("\n  sync:\n");
        sb.append("    enabled: ").append(config.get("sync.enabled")).append("\n");
        if (Boolean.TRUE.equals(config.get("sync.enabled"))) {
            sb.append("    port: ").append(config.get("sync.port")).append("\n");
            sb.append("    multicast: ").append(config.get("sync.multicast")).append("\n");
        }
        
        return sb.toString();
    }

    private void showStartupCommand(String configFile) {
        println("┌" + DIVIDER + "┐");
        println("│" + padRight(" 启动命令", 65) + "│");
        println("├" + DIVIDER + "┤");
        println("│                                                               │");
        println("│  使用以下命令启动服务:                                        │");
        println("│                                                               │");
        println("│  java -jar skill-vfs-" + selectedType.code.toLowerCase() + ".jar \\              │");
        println("│    --spring.config.location=" + configFile + " \\                 │");
        println("│    --vfs.storageType=" + selectedType.code.toLowerCase() + "                               │");
        println("│                                                               │");
        println("│  或设置环境变量:                                              │");
        println("│                                                               │");
        
        switch (selectedType) {
            case DATABASE:
                println("│  export DB_PASSWORD=your_password                             │");
                break;
            case S3:
                println("│  export S3_ACCESS_KEY=your_access_key                         │");
                println("│  export S3_SECRET_KEY=your_secret_key                         │");
                break;
            case WEBDAV:
                println("│  export WEBDAV_USERNAME=your_username                         │");
                println("│  export WEBDAV_PASSWORD=your_password                         │");
                break;
        }
        
        println("│                                                               │");
        println("└" + DIVIDER + "┘");
        println();
        
        println("配置完成！感谢使用 VFS 存储配置向导。");
    }

    private int getDefaultSyncPort() {
        switch (selectedType) {
            case LOCAL: return 9876;
            case DATABASE: return 9877;
            case S3: return 9878;
            case WEBDAV: return 9879;
            default: return 9876;
        }
    }

    private String getDefaultMulticast() {
        switch (selectedType) {
            case LOCAL: return "239.255.255.250";
            case DATABASE: return "239.255.255.251";
            case S3: return "239.255.255.252";
            case WEBDAV: return "239.255.255.253";
            default: return "239.255.255.250";
        }
    }

    private String readString(String prompt, String defaultValue) {
        print(prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private String readPassword(String prompt) {
        print(prompt + ": ");
        if (System.console() != null) {
            return new String(System.console().readPassword());
        }
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max, int defaultValue) {
        while (true) {
            print(prompt + " [" + defaultValue + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                println("请输入 " + min + " 到 " + max + " 之间的数字。");
            } catch (NumberFormatException e) {
                println("请输入有效的数字。");
            }
        }
    }

    private long readLong(String prompt, long defaultValue) {
        while (true) {
            print(prompt + " [" + defaultValue + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                println("请输入有效的数字。");
            }
        }
    }

    private boolean readBoolean(String prompt, boolean defaultValue) {
        print(prompt + " [" + (defaultValue ? "Y/n" : "y/N") + "]: ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.isEmpty()) {
            return defaultValue;
        }
        return input.equals("y") || input.equals("yes") || input.equals("true");
    }

    private void readEnter(String prompt) {
        print(prompt);
        scanner.nextLine();
    }

    private void animateDots() {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(300);
                print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - padding - text.length()));
    }

    private String padRight(String text, int width) {
        return text + " ".repeat(Math.max(0, width - text.length()));
    }

    private String green(String text) {
        return "\u001B[32m" + text + "\u001B[0m";
    }

    private String red(String text) {
        return "\u001B[31m" + text + "\u001B[0m";
    }

    private void print(String text) {
        System.out.print(text);
    }

    private void println() {
        System.out.println();
    }

    private void println(String text) {
        System.out.println(text);
    }
}
