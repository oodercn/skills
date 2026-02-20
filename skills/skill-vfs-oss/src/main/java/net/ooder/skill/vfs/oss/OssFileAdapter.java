package net.ooder.skill.vfs.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.vfs.adapter.AbstractFileAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OssFileAdapter extends AbstractFileAdapter {

    private static final Log logger = LogFactory.getLog(OssFileAdapter.class);

    private final OSS ossClient;
    private final String bucket;

    public OssFileAdapter(OSS ossClient, String bucket) {
        super(bucket);
        this.ossClient = ossClient;
        this.bucket = bucket;
    }

    @Override
    public void mkdirs(String vfsPath) {
        String key = normalizePath(vfsPath);
        if (!key.endsWith("/")) {
            key = key + "/";
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        ossClient.putObject(bucket, key, new ByteArrayInputStream(new byte[0]), metadata);
    }

    @Override
    public void delete(String vfsPath) {
        String key = normalizePath(vfsPath);
        ossClient.deleteObject(bucket, key);
    }

    @Override
    public long write(String vfsPath, MD5InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            PutObjectRequest request = new PutObjectRequest(bucket, key, input, metadata);
            ossClient.putObject(request);
            return ossClient.getObjectMetadata(bucket, key).getContentLength();
        } catch (Exception e) {
            logger.error("Failed to write to OSS: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public MD5InputStream getMD5InputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            OSSObject ossObject = ossClient.getObject(bucket, key);
            return new MD5InputStream(ossObject.getObjectContent());
        } catch (Exception e) {
            logger.error("Failed to get MD5 input stream from OSS: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public MD5OutputStream getOutputStream(String vfsPath) {
        throw new UnsupportedOperationException("MD5OutputStream not supported for OSS storage");
    }

    @Override
    public InputStream getInputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            OSSObject ossObject = ossClient.getObject(bucket, key);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            logger.error("Failed to get input stream from OSS: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public String getMD5Hash(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucket, key);
            String etag = metadata.getETag();
            if (etag != null) {
                etag = etag.replace("\"", "");
            }
            return etag;
        } catch (Exception e) {
            logger.error("Failed to get MD5 hash from OSS: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public long write(String vfsPath, InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            PutObjectRequest request = new PutObjectRequest(bucket, key, input, metadata);
            ossClient.putObject(request);
            return ossClient.getObjectMetadata(bucket, key).getContentLength();
        } catch (Exception e) {
            logger.error("Failed to write to OSS: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public boolean exists(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            return ossClient.doesObjectExist(bucket, key);
        } catch (Exception e) {
            logger.error("Failed to check existence in OSS: " + vfsPath, e);
            return false;
        }
    }

    @Override
    public boolean testConnection(String vfsPath) {
        try {
            return ossClient.doesBucketExist(bucket);
        } catch (Exception e) {
            logger.error("Failed to test OSS connection", e);
            return false;
        }
    }

    @Override
    public Integer writeLine(String vfsPath, String str) {
        throw new UnsupportedOperationException("writeLine not supported for OSS storage");
    }

    @Override
    public Long getLength(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucket, key);
            return metadata.getContentLength();
        } catch (Exception e) {
            logger.error("Failed to get length from OSS: " + vfsPath, e);
            return 0L;
        }
    }

    @Override
    public java.util.List<String> readLine(String vfsPath, java.util.List<Integer> lineNums) {
        throw new UnsupportedOperationException("readLine not supported for OSS storage");
    }

    @Override
    public String createFolderPath() {
        Date date = new Date();
        DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd/");
        return format1.format(date);
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    public String generatePresignedUrl(String key, int expirationMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);
        URL url = ossClient.generatePresignedUrl(bucket, normalizePath(key), expiration);
        return url.toString();
    }

    public void copyObject(String sourceKey, String destKey) {
        ossClient.copyObject(bucket, normalizePath(sourceKey), bucket, normalizePath(destKey));
    }

    public void moveObject(String sourceKey, String destKey) {
        copyObject(sourceKey, destKey);
        delete(sourceKey);
    }
}
