package net.ooder.skill.llm.config.dto;

import java.util.List;

public class LlmUsageStatsDTO {
    private long totalTokens;
    private long promptTokens;
    private long completionTokens;
    private long totalRequests;
    private List<DailyStats> dailyStats;

    public static class DailyStats {
        private String date;
        private long tokens;
        private long requests;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public long getTokens() { return tokens; }
        public void setTokens(long tokens) { this.tokens = tokens; }
        public long getRequests() { return requests; }
        public void setRequests(long requests) { this.requests = requests; }
    }

    public long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
    public long getPromptTokens() { return promptTokens; }
    public void setPromptTokens(long promptTokens) { this.promptTokens = promptTokens; }
    public long getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(long completionTokens) { this.completionTokens = completionTokens; }
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    public List<DailyStats> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyStats> dailyStats) { this.dailyStats = dailyStats; }
}
