package net.ooder.skill.media.zhihu;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.media.MediaPublishProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ZhihuMediaProvider implements MediaPublishProvider {
    
    private String clientId;
    private String clientSecret;
    private String accessToken;
    
    public ZhihuMediaProvider() {
        this.clientId = System.getenv("ZHIHU_CLIENT_ID");
        this.clientSecret = System.getenv("ZHIHU_CLIENT_SECRET");
        this.accessToken = System.getenv("ZHIHU_ACCESS_TOKEN");
    }
    
    @Override
    public String getProviderType() {
        return "zhihu";
    }
    
    @Override
    public List<String> getSupportedContentTypes() {
        return Arrays.asList("article", "answer", "pin", "video");
    }
    
    @Override
    public PublishResult publish(PublishRequest request) {
        log.info("Zhihu publish: title={}", request.getTitle());
        
        PublishResult result = new PublishResult();
        String articleId = "ZH" + System.currentTimeMillis();
        
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setArticleUrl("https://zhuanlan.zhihu.com/p/" + articleId);
        result.setStatus("published");
        result.setPublishedAt(System.currentTimeMillis());
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("columnId", "");
        extra.put("commentPermission", "all");
        extra.put("disclaimerType", request.isOriginal() ? "original" : "reprint");
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PublishResult update(String articleId, PublishRequest request) {
        log.info("Zhihu update: articleId={}", articleId);
        
        PublishResult result = new PublishResult();
        result.setSuccess(true);
        result.setArticleId(articleId);
        result.setStatus("updated");
        
        return result;
    }
    
    @Override
    public boolean delete(String articleId) {
        log.info("Zhihu delete: articleId={}", articleId);
        return true;
    }
    
    @Override
    public ArticleResult getArticle(String articleId) {
        log.info("Zhihu getArticle: articleId={}", articleId);
        
        ArticleResult result = new ArticleResult();
        result.setArticleId(articleId);
        result.setTitle("Mock Zhihu Article");
        result.setContent("Mock content");
        result.setStatus("published");
        result.setArticleUrl("https://zhuanlan.zhihu.com/p/" + articleId);
        
        return result;
    }
    
    @Override
    public List<ArticleResult> listArticles(int page, int pageSize) {
        log.info("Zhihu listArticles: page={}, pageSize={}", page, pageSize);
        return new ArrayList<>();
    }
    
    @Override
    public StatsResult getStats(String articleId) {
        log.info("Zhihu getStats: articleId={}", articleId);
        
        StatsResult result = new StatsResult();
        result.setArticleId(articleId);
        result.setViewCount(3000);
        result.setLikeCount(80);
        result.setCommentCount(20);
        result.setCollectCount(40);
        
        return result;
    }
    
    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
