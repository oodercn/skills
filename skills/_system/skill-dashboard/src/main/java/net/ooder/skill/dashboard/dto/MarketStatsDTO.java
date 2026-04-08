package net.ooder.skill.dashboard.dto;

import java.util.List;

public class MarketStatsDTO {
    
    private long totalDownloads;
    private long totalReviews;
    private double avgRating;
    private List<String> trendingSkills;

    public long getTotalDownloads() { return totalDownloads; }
    public void setTotalDownloads(long totalDownloads) { this.totalDownloads = totalDownloads; }
    public long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(long totalReviews) { this.totalReviews = totalReviews; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public List<String> getTrendingSkills() { return trendingSkills; }
    public void setTrendingSkills(List<String> trendingSkills) { this.trendingSkills = trendingSkills; }
}
