package net.ooder.skill.vfs.minio;

import io.minio.*;
import io.minio.messages.Item;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.vfs.adapter.AbstractFileAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MinioFileAdapter extends AbstractFileAdapter {

    private static final Log logger = LogFactory.getLog(MinioFileAdapter.class);

    private final MinioClient minioClient;
    private final String bucket;

    public MinioFileAdapter(MinioClient minioClient, String bucket) {
        super(bucket);
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public void mkdirs(String vfsPath) {
        String key = normalizePath(vfsPath);
        if (!key.endsWith("/")) {
            key = key + "/";
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, 0)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to create folder in MinIO: " + vfsPath, e);
        }
    }

    @Override
    public void delete(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to delete from MinIO: " + vfsPath, e);
        }
    }

    @Override
    public long write(String vfsPath, MD5InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(input, -1, 10485760)
                            .build()
            );
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return stat.size();
        } catch (Exception e) {
            logger.error("Failed to write to MinIO: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public MD5InputStream getMD5InputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return new MD5InputStream(stream);
        } catch (Exception e) {
            logger.error("Failed to get MD5 input stream from MinIO: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public MD5OutputStream getOutputStream(String vfsPath) {
        throw new UnsupportedOperationException("MD5OutputStream not supported for MinIO storage");
    }

    @Override
    public InputStream getInputStream(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to get input stream from MinIO: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public String getMD5Hash(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            String etag = stat.etag();
            if (etag != null) {
                etag = etag.replace("\"", "");
            }
            return etag;
        } catch (Exception e) {
            logger.error("Failed to get MD5 hash from MinIO: " + vfsPath, e);
            return null;
        }
    }

    @Override
    public long write(String vfsPath, InputStream input) {
        String key = normalizePath(vfsPath);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(input, -1, 10485760)
                            .build()
            );
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return stat.size();
        } catch (Exception e) {
            logger.error("Failed to write to MinIO: " + vfsPath, e);
            return 0;
        }
    }

    @Override
    public boolean exists(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean testConnection(String vfsPath) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to test MinIO connection", e);
            return false;
        }
    }

    @Override
    public Integer writeLine(String vfsPath, String str) {
        throw new UnsupportedOperationException("writeLine not supported for MinIO storage");
    }

    @Override
    public Long getLength(String vfsPath) {
        String key = normalizePath(vfsPath);
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return stat.size();
        } catch (Exception e) {
            logger.error("Failed to get length from MinIO: " + vfsPath, e);
            return 0L;
        }
    }

    @Override
    public List<String> readLine(String vfsPath, List<Integer> lineNums) {
        throw new UnsupportedOperationException("readLine not supported for MinIO storage");
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
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(normalizePath(key))
                            .expiry(expirationMinutes * 60)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to generate presigned URL", e);
            return null;
        }
    }

    public List<String> listObjects(String prefix) {
        List<String> objects = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(normalizePath(prefix))
                            .build()
            );
            for (Result<Item> result : results) {
                Item item = result.get();
                objects.add(item.objectName());
            }
        } catch (Exception e) {
            logger.error("Failed to list objects from MinIO", e);
        }
        return objects;
    }

    public void copyObject(String sourceKey, String destKey) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(normalizePath(destKey))
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(normalizePath(sourceKey))
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            logger.error("Failed to copy object in MinIO", e);
        }
    }

    public void moveObject(String sourceKey, String destKey) {
        copyObject(sourceKey, destKey);
        delete(sourceKey);
    }
}
