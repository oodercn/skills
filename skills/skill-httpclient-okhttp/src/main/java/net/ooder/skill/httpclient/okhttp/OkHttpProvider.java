package net.ooder.skill.httpclient.okhttp;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.HttpClientProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OkHttpProvider implements HttpClientProvider {
    
    private int connectTimeout = 30000;
    private int readTimeout = 30000;
    private int writeTimeout = 30000;
    
    @Override
    public String getProviderType() {
        return "okhttp";
    }
    
    @Override
    public HttpResponse get(String url, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("GET", url, null, headers, options);
    }
    
    @Override
    public HttpResponse post(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("POST", url, body, headers, options);
    }
    
    @Override
    public HttpResponse put(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("PUT", url, body, headers, options);
    }
    
    @Override
    public HttpResponse delete(String url, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("DELETE", url, null, headers, options);
    }
    
    @Override
    public HttpResponse head(String url, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("HEAD", url, null, headers, options);
    }
    
    @Override
    public HttpResponse patch(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        return executeRequest("PATCH", url, body, headers, options);
    }
    
    private HttpResponse executeRequest(String method, String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("OkHttp {} request: {}", method, url);
            
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put("Content-Type", "application/json");
            responseHeaders.put("X-Provider", "okhttp");
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("method", method);
            responseBody.put("url", url);
            responseBody.put("message", "Mock response - configure OkHttp for real requests");
            responseBody.put("timestamp", System.currentTimeMillis());
            
            long duration = System.currentTimeMillis() - startTime;
            
            return new HttpResponse(200, responseHeaders, responseBody, duration);
        } catch (Exception e) {
            log.error("OkHttp request failed: {} {}", method, url, e);
            long duration = System.currentTimeMillis() - startTime;
            return new HttpResponse(500, new HashMap<>(), e.getMessage(), duration);
        }
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
