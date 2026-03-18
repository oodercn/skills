package net.ooder.skill.media.xiaohongshu;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.media.MediaPublishProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class XiaohongshuMediaProvider implements MediaPublishProvider {
    
    private String appId;
    private String appSecret;
    private String accessToken;
    
    public XiaohongshuMediaProvider() {
        this.appId = System.getenv("XHS_APP_ID");
        this.appSecret = System.getenv("XHS_APP_SECRET");
        this.accessToken = System.getenv("XHS_ACCESS_TOKEN");
    }
    
    @Override
    public String getProviderType() {
        return "xiaohongshu";
    }
    
    @Override
    public List<String> getSupportedContentTypes() {
        return Arrays.asList("note", "video", "image");
    }
    
    @Override
    public PublishResult publish(PublishRequest request) {
        log.info("Xiaohongshu publish: title={}", request.getTitle());
        
        PublishResult result = new PublishResult();
        String articleId = "XHS" + System.currentTimeMillis();
        
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setArticleUrl("https://www.xiaohongshu.com/explore/" + articleId);
        result.setStatus("published");
        result.setPublishedAt(System.currentTimeMillis());
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("noteId", articleId);
        extra.put("type", request.getContentType());
        extra.put("topicIds", new ArrayList<>());
        extra.put("atUsers", new ArrayList<>());
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PublishResult update(String articleId, PublishRequest request) {
        log.info("Xiaohongshu update: articleId={}", articleId);
        
        PublishResult result = new PublishResult();
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setStatus("updated");
        
        return result;
    }
    
    @Override
    public boolean delete(String articleId) {
        log.info("Xiaohongshu delete: articleId={}", articleId);
        return true;
    }
    
    @Override
    public ArticleResult getArticle(String articleId) {
        log.info("Xiaohongshu getArticle: articleId={}", articleId);
        
        ArticleResult result = new ArticleResult();
        result.setArticleId(articleId);
        result.setTitle("Mock Xiaohongshu Note");
        result.setContent("Mock content");
        result.setStatus("published");
        result.setArticleUrl("https://www.xiaohongshu.com/explore/" + articleId);
        
        return result;
    }
    
    @Override
    public List<ArticleResult> listArticles(int page, int pageSize) {
        log.info("Xiaohongshu listArticles: page={}, pageSize={}", page, pageSize);
        return new ArrayList<>();
    }
    
    @Override
    public StatsResult getStats(String articleId) {
        log.info("Xiaohongshu getStats: articleId={}", articleId);
        
        StatsResult result = new StatsResult();
        result.setArticleId(articleId);
        result.setViewCount(8000);
        result.setLikeCount(300);
        result.setCommentCount(80);
        result.setCollectCount(150);
        result.setShareCount(40);
        
        return result;
    }
    
    public void setAppId(String appId) { this.appId = appId; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
