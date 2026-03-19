package net.ooder.skill.media.wechat;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.media.MediaPublishProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class WechatMediaProvider implements MediaPublishProvider {
    
    private String appId;
    private String appSecret;
    private String accessToken;
    
    public WechatMediaProvider() {
        this.appId = System.getenv("WECHAT_MP_APP_ID");
        this.appSecret = System.getenv("WECHAT_MP_APP_SECRET");
    }
    
    @Override
    public String getProviderType() {
        return "wechat_mp";
    }
    
    @Override
    public List<String> getSupportedContentTypes() {
        return Arrays.asList("article", "image", "video", "audio");
    }
    
    @Override
    public PublishResult publish(PublishRequest request) {
        log.info("WechatMP publish: title={}", request.getTitle());
        
        PublishResult result = new PublishResult();
        String articleId = "WX" + System.currentTimeMillis();
        
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setArticleUrl("https://mp.weixin.qq.com/s/" + articleId);
        result.setStatus("published");
        result.setPublishedAt(System.currentTimeMillis());
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("mediaId", "media_" + articleId);
        extra.put("msgType", request.getContentType());
        extra.put("digest", request.getSummary());
        extra.put("contentSourceUrl", request.getSourceUrl());
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PublishResult update(String articleId, PublishRequest request) {
        log.info("WechatMP update: articleId={}", articleId);
        
        PublishResult result = new PublishResult();
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setStatus("updated");
        result.setPublishedAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public boolean delete(String articleId) {
        log.info("WechatMP delete: articleId={}", articleId);
        return true;
    }
    
    @Override
    public ArticleResult getArticle(String articleId) {
        log.info("WechatMP getArticle: articleId={}", articleId);
        
        ArticleResult result = new ArticleResult();
        result.setArticleId(articleId);
        result.setTitle("Mock Article");
        result.setContent("Mock content for article");
        result.setStatus("published");
        result.setArticleUrl("https://mp.weixin.qq.com/s/" + articleId);
        result.setPublishedAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public List<ArticleResult> listArticles(int page, int pageSize) {
        log.info("WechatMP listArticles: page={}, pageSize={}", page, pageSize);
        return new ArrayList<>();
    }
    
    @Override
    public StatsResult getStats(String articleId) {
        log.info("WechatMP getStats: articleId={}", articleId);
        
        StatsResult result = new StatsResult();
        result.setArticleId(articleId);
        result.setViewCount(1000);
        result.setLikeCount(50);
        result.setCommentCount(10);
        result.setShareCount(20);
        
        return result;
    }
    
    public void setAppId(String appId) { this.appId = appId; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
