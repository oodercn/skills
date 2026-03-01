package net.ooder.skill.llm.context.model;

import java.util.Map;

public class ContextRequest {
    
    private String userId;
    private String sceneId;
    private String pageType;
    private String pageId;
    private String query;
    private Map<String, Object> params;
    private int maxTokens;
    private boolean includeHistory;
    private boolean includeKnowledge;
    private int historyTurns;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public boolean isIncludeHistory() { return includeHistory; }
    public void setIncludeHistory(boolean includeHistory) { this.includeHistory = includeHistory; }
    public boolean isIncludeKnowledge() { return includeKnowledge; }
    public void setIncludeKnowledge(boolean includeKnowledge) { this.includeKnowledge = includeKnowledge; }
    public int getHistoryTurns() { return historyTurns; }
    public void setHistoryTurns(int historyTurns) { this.historyTurns = historyTurns; }
}
