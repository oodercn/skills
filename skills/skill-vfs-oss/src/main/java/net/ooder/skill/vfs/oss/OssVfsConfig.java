package net.ooder.skill.vfs.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import net.ooder.common.CommonConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.adapter.FileAdapter;

public class OssVfsConfig {

    private static final Log log = LogFactory.getLog("vfs", OssVfsConfig.class);

    private static OssVfsConfig instance;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private OSS ossClient;
    private FileAdapter fileAdapter;

    private OssVfsConfig() {
        init();
    }

    public static synchronized OssVfsConfig getInstance() {
        if (instance == null) {
            instance = new OssVfsConfig();
        }
        return instance;
    }

    private void init() {
        endpoint = getConfigValue("oss.endpoint", "");
        accessKeyId = getConfigValue("oss.accessKeyId", "");
        accessKeySecret = getConfigValue("oss.accessKeySecret", "");
        bucket = getConfigValue("oss.bucket", "vfs-storage");

        initOssClient();
        fileAdapter = new OssFileAdapter(ossClient, bucket);

        log.info("OssVfsConfig initialized: endpoint=" + endpoint + ", bucket=" + bucket);
    }

    private void initOssClient() {
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            if (!ossClient.doesBucketExist(bucket)) {
                ossClient.createBucket(bucket);
                log.info("Created OSS bucket: " + bucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize OSS client", e);
            throw new RuntimeException("Failed to initialize OSS client", e);
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

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucket() {
        return bucket;
    }

    public OSS getOssClient() {
        return ossClient;
    }

    public FileAdapter getFileAdapter() {
        return fileAdapter;
    }

    public static void setEndpoint(String endpoint) {
        getInstance().endpoint = endpoint;
    }

    public static void setAccessKeyId(String accessKeyId) {
        getInstance().accessKeyId = accessKeyId;
    }

    public static void setAccessKeySecret(String accessKeySecret) {
        getInstance().accessKeySecret = accessKeySecret;
    }

    public static void setBucket(String bucket) {
        getInstance().bucket = bucket;
    }

    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS client shutdown");
        }
    }
}
