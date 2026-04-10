package net.ooder.spi.document;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档解析结果
 */
public class ParseResult {
    
    private String text;
    private String title;
    private String author;
    private Integer pageCount;
    private Long fileSize;
    private String language;
    private Map<String, Object> metadata = new HashMap<>();
    private boolean success;
    private String errorMessage;
    
    public static ParseResult success(String text) {
        ParseResult result = new ParseResult();
        result.setSuccess(true);
        result.setText(text);
        return result;
    }
    
    public static ParseResult failure(String errorMessage) {
        ParseResult result = new ParseResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Integer getPageCount() {
        return pageCount;
    }
    
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public ParseResult addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}
