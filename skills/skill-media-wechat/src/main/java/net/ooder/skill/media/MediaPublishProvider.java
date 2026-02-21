package net.ooder.skill.media;

import java.util.List;
import java.util.Map;

public interface MediaPublishProvider {
    
    String getProviderType();
    
    List<String> getSupportedContentTypes();
    
    PublishResult publish(PublishRequest request);
    
    PublishResult update(String articleId, PublishRequest request);
    
    boolean delete(String articleId);
    
    ArticleResult getArticle(String articleId);
    
    List<ArticleResult> listArticles(int page, int pageSize);
    
    StatsResult getStats(String articleId);
    
    public static class PublishRequest {
        private String title;
        private String content;
        private String contentType;
        private List<String> tags;
        private String coverImage;
        private String summary;
        private String author;
        private String sourceUrl;
        private boolean original;
        private Map<String, Object> metadata;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getCoverImage() { return coverImage; }
        public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getSourceUrl() { return sourceUrl; }
        public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
        public boolean isOriginal() { return original; }
        public void setOriginal(boolean original) { this.original = original; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class PublishResult {
        private boolean success;
        private String articleId;
        private String articleUrl;
        private String status;
        private String errorCode;
        private String errorMessage;
        private long publishedAt;
        private Map<String, Object> extra;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }
        public String getArticleUrl() { return articleUrl; }
        public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getPublishedAt() { return publishedAt; }
        public void setPublishedAt(long publishedAt) { this.publishedAt = publishedAt; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }
    
    public static class ArticleResult {
        private String articleId;
        private String title;
        private String content;
        private String status;
        private String coverImage;
        private List<String> tags;
        private String author;
        private long publishedAt;
        private long updatedAt;
        private String articleUrl;
        private Map<String, Object> extra;
        
        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCoverImage() { return coverImage; }
        public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public long getPublishedAt() { return publishedAt; }
        public void setPublishedAt(long publishedAt) { this.publishedAt = publishedAt; }
        public long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
        public String getArticleUrl() { return articleUrl; }
        public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }
    
    public static class StatsResult {
        private String articleId;
        private long viewCount;
        private long likeCount;
        private long commentCount;
        private long shareCount;
        private long collectCount;
        private long forwardCount;
        private Map<String, Object> extra;
        
        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }
        public long getViewCount() { return viewCount; }
        public void setViewCount(long viewCount) { this.viewCount = viewCount; }
        public long getLikeCount() { return likeCount; }
        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
        public long getCommentCount() { return commentCount; }
        public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
        public long getShareCount() { return shareCount; }
        public void setShareCount(long shareCount) { this.shareCount = shareCount; }
        public long getCollectCount() { return collectCount; }
        public void setCollectCount(long collectCount) { this.collectCount = collectCount; }
        public long getForwardCount() { return forwardCount; }
        public void setForwardCount(long forwardCount) { this.forwardCount = forwardCount; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }
}
