package net.ooder.skill.vfs.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.vfs.adapter.AbstractFileAdapter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class S3FileAdapter extends AbstractFileAdapter {

    private static final Log logger = LogFactory.getLog(S3FileAdapter.class);

    private final AmazonS3 s3Client;
    private final String bucket;

    public S3FileAdapter(AmazonS3 s3Client, String bucket) {
        super(bucket);
        this.s3Client = s3Client;
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
        s3Client.putObject(bucket, key, new ByteArrayInputStream(new byte[0]), metadata);
    }

    @Override
    public void delete(String vfsPath) {
        String key = normalizePath(vfsPath);
        s3Client.deleteObject(bucket, key);
    }

    @Override
    public long write(String vfsPath, MD5InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            PutObjectRequest request = new PutObjectRequest(bucket, key, input, metadata);
            s3Client.putObject(request);
            return s3Client.getObjectMetadata(bucket, key).getContentLength();
        } catch (Exception e) {
            logger.error("Failed to write to S3: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public MD5InputStream getMD5InputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            S3Object s3Object = s3Client.getObject(bucket, key);
            return new MD5InputStream(s3Object.getObjectContent());
        } catch (Exception e) {
            logger.error("Failed to get MD5 input stream from S3: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public MD5OutputStream getOutputStream(String vfsPath) {
        throw new UnsupportedOperationException("MD5OutputStream not supported for S3 storage");
    }

    @Override
    public InputStream getInputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            S3Object s3Object = s3Client.getObject(bucket, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            logger.error("Failed to get input stream from S3: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public String getMD5Hash(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, key);
            String etag = metadata.getETag();
            if (etag != null) {
                etag = etag.replace("\"", "");
            }
            return etag;
        } catch (Exception e) {
            logger.error("Failed to get MD5 hash from S3: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public long write(String vfsPath, InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            PutObjectRequest request = new PutObjectRequest(bucket, key, input, metadata);
            s3Client.putObject(request);
            return s3Client.getObjectMetadata(bucket, key).getContentLength();
        } catch (Exception e) {
            logger.error("Failed to write to S3: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public boolean exists(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            return s3Client.doesObjectExist(bucket, key);
        } catch (Exception e) {
            logger.error("Failed to check existence in S3: " + vfsPath, e);
            return false;
        }
    }

    @Override
    public boolean testConnection(String vfsPath) {
        try {
            return s3Client.doesBucketExistV2(bucket);
        } catch (Exception e) {
            logger.error("Failed to test S3 connection", e);
            return false;
        }
    }

    @Override
    public Integer writeLine(String vfsPath, String str) {
        throw new UnsupportedOperationException("writeLine not supported for S3 storage");
    }

    @Override
    public Long getLength(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, key);
            return metadata.getContentLength();
        } catch (Exception e) {
            logger.error("Failed to get length from S3: " + vfsPath, e);
            return 0L;
        }
    }

    @Override
    public List<String> readLine(String vfsPath, List<Integer> lineNums) {
        throw new UnsupportedOperationException("readLine not supported for S3 storage");
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
        return s3Client.generatePresignedUrl(bucket, normalizePath(key), expiration).toString();
    }
}
