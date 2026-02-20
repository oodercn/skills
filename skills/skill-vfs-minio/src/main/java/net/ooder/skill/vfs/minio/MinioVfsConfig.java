package net.ooder.skill.vfs.minio;

import io.minio.MinioClient;
import net.ooder.common.CommonConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.adapter.FileAdapter;

public class MinioVfsConfig {

    private static final Log log = LogFactory.getLog("vfs", MinioVfsConfig.class);

    private static MinioVfsConfig instance;

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private boolean secure;
    private MinioClient minioClient;
    private FileAdapter fileAdapter;

    private MinioVfsConfig() {
        init();
    }

    public static synchronized MinioVfsConfig getInstance() {
        if (instance == null) {
            instance = new MinioVfsConfig();
        }
        return instance;
    }

    private void init() {
        endpoint = getConfigValue("minio.endpoint", "http://localhost:9000");
        accessKey = getConfigValue("minio.accessKey", "");
        secretKey = getConfigValue("minio.secretKey", "");
        bucket = getConfigValue("minio.bucket", "vfs-storage");
        secure = Boolean.parseBoolean(getConfigValue("minio.secure", "false"));

        initMinioClient();
        fileAdapter = new MinioFileAdapter(minioClient, bucket);

        log.info("MinioVfsConfig initialized: endpoint=" + endpoint + ", bucket=" + bucket);
    }

    private void initMinioClient() {
        try {
            String endpointUrl = endpoint;
            if (!endpointUrl.startsWith("http://") && !endpointUrl.startsWith("https://")) {
                endpointUrl = secure ? "https://" + endpointUrl : "http://" + endpointUrl;
            }

            minioClient = MinioClient.builder()
                    .endpoint(endpointUrl)
                    .credentials(accessKey, secretKey)
                    .secure(secure)
                    .build();

            boolean bucketExists = minioClient.bucketExists(
                    io.minio.messages.Bucket.builder()
                            .name(bucket)
                            .build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build()
                );
                log.info("Created MinIO bucket: " + bucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO client", e);
            throw new RuntimeException("Failed to initialize MinIO client", e);
        }
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = CommonConfig.getValue(key);
        if (value == null) {
            value = System.getProperty(key.replace(".", "_"), 
                    System.getenv(key.replace(".", "_").toUpperCase()));
        }
        return value != null ? value : defaultValue;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public boolean isSecure() {
        return secure;
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public FileAdapter getFileAdapter() {
        return fileAdapter;
    }

    public static void setEndpoint(String endpoint) {
        getInstance().endpoint = endpoint;
    }

    public static void setAccessKey(String accessKey) {
        getInstance().accessKey = accessKey;
    }

    public static void setSecretKey(String secretKey) {
        getInstance().secretKey = secretKey;
    }

    public static void setBucket(String bucket) {
        getInstance().bucket = bucket;
    }
}
