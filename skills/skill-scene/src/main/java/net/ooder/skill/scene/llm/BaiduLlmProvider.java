package net.ooder.skill.scene.llm;

import net.ooder.scene.skill.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class BaiduLlmProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(BaiduLlmProvider.class);

    private static final String PROVIDER_TYPE = "baidu";
    private static final String DEFAULT_MODEL = "ernie-bot";
    
    private static final String QIANFAN_API_HOST = "qianfan.baidubce.com";
    private static final String QIANFAN_API_URL = "https://qianfan.baidubce.com/v2/app/conversation/runs";
    
    private String accessKey;
    private String secretKey;
    
    private final List<String> supportedModels = new ArrayList<String>();
    
    public BaiduLlmProvider() {
        supportedModels.add("ernie-bot");
        supportedModels.add("ernie-bot-4");
        supportedModels.add("ernie-bot-turbo");
        supportedModels.add("bce-v3");
    }
    
    public BaiduLlmProvider(String accessKey, String secretKey) {
        this();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
    
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String getProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public List<String> getSupportedModels() {
        return supportedModels;
    }

    @Override
    public boolean supportsStreaming() {
        return true;
    }

    @Override
    public boolean supportsFunctionCalling() {
        return false;
    }

    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        log.info("Baidu LLM chat called with model: {}", model);
        
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
            String apiUrl = QIANFAN_API_URL;
            
            Map<String, Object> requestBody = new HashMap<String, Object>();
            
            StringBuilder contentBuilder = new StringBuilder();
            for (Map<String, Object> msg : messages) {
                String role = (String) msg.get("role");
                String msgContent = (String) msg.get("content");
                if ("user".equals(role)) {
                    contentBuilder.append(msgContent);
                }
            }
            
            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("query", contentBuilder.toString());
            
            Map<String, Object> conversationConfig = new HashMap<String, Object>();
            conversationConfig.put("model", getModelId(model));
            payload.put("conversation_config", conversationConfig);
            
            requestBody.put("payload", payload);
            
            String response = sendRequestWithBceAuth(apiUrl, requestBody);
            
            result.put("content", response);
            result.put("model", model);
            result.put("provider", PROVIDER_TYPE);
            
        } catch (Exception e) {
            log.error("Baidu LLM chat error", e);
            result.put("content", "Error: " + e.getMessage());
            result.put("error", true);
        }
        
        return result;
    }
    
    private String getModelId(String model) {
        if (model == null || model.isEmpty()) {
            model = DEFAULT_MODEL;
        }
        
        switch (model) {
            case "ernie-bot-4":
                return "ernie-4.0-8k";
            case "ernie-bot-turbo":
                return "ernie-3.5-8k";
            case "ernie-bot":
            case "bce-v3":
            default:
                return "ernie-3.5-8k";
        }
    }

    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("Baidu LLM complete called with model: {}", model);
        
        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
        Map<String, Object> userMessage = new HashMap<String, Object>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        
        Map<String, Object> result = chat(model, messages, options);
        return (String) result.get("content");
    }

    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("Baidu LLM translate called");
        
        String prompt = "请将以下文本翻译成" + targetLanguage + "：\n\n" + text;
        if (sourceLanguage != null && !sourceLanguage.isEmpty()) {
            prompt = "请将以下" + sourceLanguage + "文本翻译成" + targetLanguage + "：\n\n" + text;
        }
        
        return complete(model, prompt, null);
    }

    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("Baidu LLM summarize called");
        
        String prompt = "请总结以下内容，总结长度不超过" + maxLength + "字：\n\n" + text;
        return complete(model, prompt, null);
    }

    @Override
    public List<double[]> embed(String model, List<String> texts) {
        log.info("Baidu LLM embed called with model: {}, text count: {}", model, texts.size());
        
        List<double[]> result = new ArrayList<double[]>();
        log.info("Embedding not implemented for BCE auth");
        
        return result;
    }
    
    private String sendRequestWithBceAuth(String apiUrl, Map<String, Object> requestBody) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        
        String timestamp = getTimestamp();
        String jsonBody = mapToJson(requestBody);
        
        String authorization = generateBceAuth("POST", url.getPath(), timestamp, jsonBody);
        
        conn.setRequestProperty("x-bce-date", timestamp);
        conn.setRequestProperty("Authorization", authorization);
        conn.setRequestProperty("Host", QIANFAN_API_HOST);
        
        conn.setConnectTimeout(60000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);
        
        log.debug("Request body: {}", jsonBody);
        log.debug("Authorization: {}", authorization);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        log.info("Baidu API response code: {}", responseCode);
        
        BufferedReader reader;
        if (responseCode == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                reader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            }
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        String responseBody = response.toString();
        log.debug("Response body: {}", responseBody);
        
        if (responseCode != 200) {
            log.error("Baidu API error: {} - {}", responseCode, responseBody);
            return "Error (HTTP " + responseCode + "): " + responseBody;
        }
        
        String content = extractJsonValue(responseBody, "answer");
        if (content == null) {
            content = extractJsonValue(responseBody, "result");
        }
        
        return content != null ? content : responseBody;
    }
    
    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
    
    private String generateBceAuth(String method, String path, String timestamp, String body) {
        try {
            String canonicalUri = path;
            String canonicalQueryString = "";
            
            String canonicalHeaders = "host:" + QIANFAN_API_HOST + "\n" +
                                      "x-bce-date:" + timestamp + "\n";
            String signedHeaders = "host;x-bce-date";
            
            String bodyHash = sha256Hex(body);
            
            String canonicalRequest = method + "\n" +
                                      canonicalUri + "\n" +
                                      canonicalQueryString + "\n" +
                                      canonicalHeaders + "\n" +
                                      signedHeaders + "\n" +
                                      bodyHash;
            
            String authStringPrefix = "bce-auth-v1/" + accessKey + "/" + timestamp + "/3600";
            String signingKey = hmacSha256Hex(secretKey, authStringPrefix);
            String signature = hmacSha256Hex(signingKey, canonicalRequest);
            
            return authStringPrefix + "/" + signedHeaders + "/" + signature;
            
        } catch (Exception e) {
            log.error("Error generating BCE auth", e);
            return "";
        }
    }
    
    private String sha256Hex(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    private byte[] hmacSha256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
    
    private String hmacSha256Hex(String key, String data) throws Exception {
        return bytesToHex(hmacSha256(key, data));
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += searchKey.length();
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\t')) {
            startIndex++;
        }
        
        if (startIndex >= json.length()) {
            return null;
        }
        
        if (json.charAt(startIndex) == '"') {
            startIndex++;
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return null;
            }
            return json.substring(startIndex, endIndex);
        } else {
            int endIndex = startIndex;
            while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
                endIndex++;
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
    
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof List) {
                json.append(listToJson((List<?>) value));
            } else if (value instanceof Map) {
                json.append(mapToJson((Map<String, Object>) value));
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        for (Object item : list) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            if (item instanceof String) {
                json.append("\"").append(escapeJson((String) item)).append("\"");
            } else if (item instanceof Map) {
                json.append(mapToJson((Map<String, Object>) item));
            } else {
                json.append("\"").append(escapeJson(item.toString())).append("\"");
            }
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder escaped = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }
}
