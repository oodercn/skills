package net.ooder.skill.management.dto;

public class SkillRateRequest {
    
    private double rating;
    private String comment;
    private String userId;

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
