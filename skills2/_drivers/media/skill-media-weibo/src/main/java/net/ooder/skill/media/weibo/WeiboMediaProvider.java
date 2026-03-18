package net.ooder.skill.media.weibo;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.media.MediaPublishProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class WeiboMediaProvider implements MediaPublishProvider {
    
    private String appKey;
    private String appSecret;
    private String accessToken;
    
    public WeiboMediaProvider() {
        this.appKey = System.getenv("WEIBO_APP_KEY");
        this.appSecret = System.getenv("WEIBO_APP_SECRET");
        this.accessToken = System.getenv("WEIBO_ACCESS_TOKEN");
    }
    
    @Override
    public String getProviderType() {
        return "weibo";
    }
    
    @Override
    public List<String> getSupportedContentTypes() {
        return Arrays.asList("article", "status", "image", "video");
    }
    
    @Override
    public PublishResult publish(PublishRequest request) {
        log.info("Weibo publish: title={}", request.getTitle());
        
        PublishResult result = new PublishResult();
        String articleId = "WB" + System.currentTimeMillis();
        
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setArticleUrl("https://weibo.com/article/" + articleId);
        result.setStatus("published");
        result.setPublishedAt(System.currentTimeMillis());
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("mid", articleId);
        extra.put("source", appKey);
        extra.put("picIds", new ArrayList<>());
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PublishResult update(String articleId, PublishRequest request) {
        log.info("Weibo update: articleId={}", articleId);
        
        PublishResult result = new PublishResult();
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setStatus("updated");
        
        return result;
    }
    
    @Override
    public boolean delete(String articleId) {
        log.info("Weibo delete: articleId={}", articleId);
        return true;
    }
    
    @Override
    public ArticleResult getArticle(String articleId) {
        log.info("Weibo getArticle: articleId={}", articleId);
        
        ArticleResult result = new ArticleResult();
        result.setArticleId(articleId);
        result.setTitle("Mock Weibo Article");
        result.setContent("Mock content");
        result.setStatus("published");
        result.setArticleUrl("https://weibo.com/article/" + articleId);
        
        return result;
    }
    
    @Override
    public List<ArticleResult> listArticles(int page, int pageSize) {
        log.info("Weibo listArticles: page={}, pageSize={}", page, pageSize);
        return new ArrayList<>();
    }
    
    @Override
    public StatsResult getStats(String articleId) {
        log.info("Weibo getStats: articleId={}", articleId);
        
        StatsResult result = new StatsResult();
        result.setArticleId(articleId);
        result.setViewCount(5000);
        result.setLikeCount(100);
        result.setCommentCount(30);
        result.setForwardCount(50);
        
        return result;
    }
    
    public void setAppKey(String appKey) { this.appKey = appKey; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
