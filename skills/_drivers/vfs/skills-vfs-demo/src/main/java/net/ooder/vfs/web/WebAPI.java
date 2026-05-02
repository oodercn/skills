package net.ooder.vfs.web;

import net.ooder.common.JDSException;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.store.service.StoreService;
import net.ooder.vfs.store.util.SuffixConts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/VFS/")
public class WebAPI {

    private static final Logger log = LoggerFactory.getLogger(WebAPI.class);

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}.{type}")
    public void get(@PathVariable("hash") String hash, @PathVariable("type") String type,
                    HttpServletResponse response) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = fileObject.downLoad();
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String contentType = SuffixConts.getSuffixMap().get(type);
            response.setContentType(contentType != null ? contentType : "application/octet-stream");
            response.setHeader("Content-Disposition",
                "filename=" + new String((hash + "." + type).getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileObject.getLength()));
            response.setHeader("Accept-Ranges", "bytes");

            streamData(inputStream, response.getOutputStream());
        } catch (JDSException e) {
            log.error("Failed to download file: {}.{}", hash, type, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.error("IO error downloading file: {}.{}", hash, type, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            closeQuietly(inputStream);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}?download")
    public void download(@PathVariable("hash") String hash, HttpServletResponse response) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = fileObject.downLoad();
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "filename=" + hash);
            response.setHeader("Content-Length", String.valueOf(fileObject.getLength()));
            response.setHeader("Accept-Ranges", "bytes");

            streamData(inputStream, response.getOutputStream());
        } catch (JDSException e) {
            log.error("Failed to download file: {}", hash, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.error("IO error downloading file: {}", hash, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            closeQuietly(inputStream);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}?addLine")
    public void writLine(@PathVariable("hash") String hash, String body) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            this.getVFSClient().writeLine(fileObject.getID(), body);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}?read")
    public void read(@PathVariable("hash") String hash, String body) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            this.getVFSClient().writeLine(fileObject.getID(), body);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}?addline")
    public void writLine(@PathVariable("hash") String hash, Integer[] lines, HttpServletResponse response) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            List<String> strings = this.getVFSClient().readLine(fileObject.getID(), Arrays.asList(lines));
            try {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(strings.toString());
            } catch (IOException e) {
                log.error("Failed to write response for readLine: {}", hash, e);
            }
        }
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "put")
    public void put(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            this.getVFSClient().createFileObject(new MD5InputStream(is));
        } catch (IOException e) {
            log.error("Failed to upload file", e);
        }
    }

    private void streamData(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
    }

    private void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    public StoreService getVFSClient() {
        return EsbUtil.parExpression(StoreService.class);
    }
}
