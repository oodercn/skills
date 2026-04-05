package net.ooder.vfs.web;

import net.ooder.common.JDSException;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.vfs.FileObject;
import net.ooder.vfs.store.service.StoreService;
import net.ooder.vfs.store.util.SuffixConts;
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
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}.{type}")
    public void get(@PathVariable("hash") String hash, @PathVariable("type") String type) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            InputStream inputStream = null;
            try {
                inputStream = fileObject.downLoad();
                HttpServletResponse response = (HttpServletResponse) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("ServletResponse");

                if (inputStream != null) {
                    response.setContentType(SuffixConts.getSuffixMap().get(type));
                    response.setHeader("Content-disposition", "filename=" + new String((hash + "." + type).getBytes("utf-8"), "ISO8859-1"));
                    response.setHeader("Content-Length", String.valueOf(fileObject.getLength()));
                    long downloadedLength = 0l;
                    OutputStream os = response.getOutputStream();
                    byte[] b = new byte[2048];
                    int length;
                    while ((length = inputStream.read(b)) > 0) {
                        os.write(b, 0, length);
                        downloadedLength += b.length;
                    }
                    os.close();
                    inputStream.close();
                }
            } catch (JDSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "{hash}?download")
    public void download(@PathVariable("hash") String hash) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            InputStream inputStream = null;
            try {
                inputStream = fileObject.downLoad();
                if (inputStream != null) {
                    HttpServletResponse response = (HttpServletResponse) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("ServletResponse");
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition", "filename=" + hash);
                    response.setHeader("Content-Length", String.valueOf(fileObject.getLength()));
                    long downloadedLength = 0l;
                    OutputStream os = response.getOutputStream();
                    byte[] b = new byte[2048];
                    int length;
                    while ((length = inputStream.read(b)) > 0) {
                        os.write(b, 0, length);
                        downloadedLength += b.length;
                    }
                    os.close();
                    inputStream.close();
                }
            } catch (JDSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


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
    public void writLine(@PathVariable("hash") String hash, Integer[] lines) {
        FileObject fileObject = this.getVFSClient().getFileObjectByHash(hash);
        if (fileObject != null) {
            List<String> strings = this.getVFSClient().readLine(fileObject.getID(), Arrays.asList(lines));
            HttpServletResponse response = (HttpServletResponse) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("ServletResponse");
            try {
                response.getWriter().write(strings.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    response.getWriter().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(method = { RequestMethod.POST}, value = "put")
    void put(@RequestParam("file") MultipartFile file) {
        try {
            this.getVFSClient().createFileObject(new MD5InputStream(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public StoreService getVFSClient() {
        return EsbUtil.parExpression(StoreService.class);
    }

}
