package net.ooder.skill.vfs.database;

import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.common.util.IOUtility;
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

public class DatabaseFileAdapter extends AbstractFileAdapter {

    String lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    private static final Log logger = LogFactory.getLog(DatabaseFileAdapter.class);

    public DatabaseFileAdapter(String rootPath) {
        super(rootPath);
    }

    @Override
    public void mkdirs(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void delete(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public long write(String vfsPath, MD5InputStream input) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        logger.info("vfsPath:" + vfsPath);

        try {
            FileOutputStream output = new FileOutputStream(file);
            IOUtility.copy(input, output);
            IOUtility.shutdownStream(input);
            IOUtility.shutdownStream(output);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + vfsPath, e);
        } catch (IOException e) {
            logger.error("Failed to write to file: " + vfsPath, e);
        }

        return file.length();
    }

    @Override
    public MD5InputStream getMD5InputStream(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }

        File file = new File(vfsPath);
        try {
            FileInputStream input = new FileInputStream(file);
            return new MD5InputStream(input);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + vfsPath, e);
        }
        return null;
    }

    @Override
    public MD5OutputStream getOutputStream(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + vfsPath, e);
        }

        return new MD5OutputStream(output);
    }

    @Override
    public InputStream getInputStream(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }

        File file = new File(vfsPath);
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + vfsPath, e);
        }

        return input;
    }

    @Override
    public String getMD5Hash(String vfsPath) {
        try {
            if (!vfsPath.startsWith(this.getRootPath())) {
                vfsPath = this.getRootPath() + vfsPath;
            }
            return DigestUtils.md5Hex(getInputStream(vfsPath));
        } catch (IOException e) {
            logger.error("Failed to get MD5 hash for file: " + vfsPath, e);
        }
        return null;
    }

    @Override
    public long write(String vfsPath, InputStream input) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        logger.info("vfsPath:" + vfsPath);
        File file = new File(vfsPath);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream output = new FileOutputStream(file);
            IOUtility.copy(input, output);
            IOUtility.shutdownStream(input);
            IOUtility.shutdownStream(output);
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + vfsPath, e);
        } catch (IOException e) {
            logger.error("Failed to write to file: " + vfsPath, e);
        }

        return file.length();
    }

    @Override
    public boolean exists(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        return file.exists();
    }

    @Override
    public boolean testConnection(String vfsPath) {
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        File file = new File(vfsPath);
        if (file.getFreeSpace() < 2.1 * 1024 * 1024 * 1024) {
            return false;
        }
        return true;
    }

    @Override
    public Integer writeLine(String vfsPath, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }
        int ln = -1;

        try {
            File file = new File(vfsPath);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(raf.length());
            raf.writeBytes(sb.toString());
            raf.writeBytes(lineSeparator);
            raf.read();
            InputStream sbs = new FileInputStream(file);
            LineNumberReader reader = new LineNumberReader(
                    new InputStreamReader(sbs, charsetName), textBufSize);
            reader.skip(raf.length());
            ln = reader.getLineNumber();
            raf.close();
        } catch (Exception e) {
            logger.error("Failed to write line to file: " + vfsPath, e);
        }
        return ln;
    }

    @Override
    public Long getLength(String vfsPath) {
        Long length = 0L;
        if (!vfsPath.startsWith(this.getRootPath())) {
            vfsPath = this.getRootPath() + vfsPath;
        }

        File file = new File(vfsPath);
        if (file != null && file.exists()) {
            length = file.length();
        }

        return length;
    }

    @Override
    public List<String> readLine(String vfsPath, List<Integer> lineNums) {
        List<String> strs = new ArrayList<String>();
        try {
            if (!vfsPath.startsWith(this.getRootPath())) {
                vfsPath = this.getRootPath() + vfsPath;
            }
            File file = new File(vfsPath);

            InputStream sbs = new FileInputStream(file);

            LineNumberReader reader = new LineNumberReader(
                    new InputStreamReader(sbs, charsetName), textBufSize);

            for (Integer num : lineNums) {
                reader.setLineNumber(num);
                strs.add(reader.readLine());
            }

        } catch (Exception e) {
            logger.error("Failed to read lines from file: " + vfsPath, e);
        }
        return strs;
    }

    @Override
    public String createFolderPath() {
        Date date = new Date();
        DateFormat format1 = new SimpleDateFormat(
                File.separator + "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator);
        return getRootPath() + format1.format(date);
    }
}
