package net.ooder.skill.vfs.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import net.ooder.common.CommonConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.adapter.FileAdapter;

public class S3VfsConfig {

    private static final Log log = LogFactory.getLog("vfs", S3VfsConfig.class);

    private static S3VfsConfig instance;

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String region;
    private boolean pathStyleAccess;
    private AmazonS3 s3Client;
    private FileAdapter fileAdapter;

    private S3VfsConfig() {
        init();
    }

    public static synchronized S3VfsConfig getInstance() {
        if (instance == null) {
            instance = new S3VfsConfig();
        }
        return instance;
    }

    private void init() {
        endpoint = getConfigValue("s3.endpoint", "");
        accessKey = getConfigValue("s3.accessKey", "");
        secretKey = getConfigValue("s3.secretKey", "");
        bucket = getConfigValue("s3.bucket", "vfs-storage");
        region = getConfigValue("s3.region", "us-east-1");
        pathStyleAccess = Boolean.parseBoolean(getConfigValue("s3.pathStyle", "true"));

        initS3Client();
        fileAdapter = new S3FileAdapter(s3Client, bucket);

        log.info("S3VfsConfig initialized: endpoint=" + endpoint + ", bucket=" + bucket);
    }

    private void initS3Client() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setConnectionTimeout(10000);
            clientConfig.setSocketTimeout(30000);

            AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withClientConfiguration(clientConfig);

            if (endpoint != null && !endpoint.isEmpty()) {
                builder.withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, region));
                if (pathStyleAccess) {
                    builder.withPathStyleAccessEnabled(true);
                }
            } else {
                builder.withRegion(region);
            }

            s3Client = builder.build();

            if (!s3Client.doesBucketExistV2(bucket)) {
                s3Client.createBucket(bucket);
                log.info("Created S3 bucket: " + bucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize S3 client", e);
            throw new RuntimeException("Failed to initialize S3 client", e);
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

    public String getRegion() {
        return region;
    }

    public AmazonS3 getS3Client() {
        return s3Client;
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
