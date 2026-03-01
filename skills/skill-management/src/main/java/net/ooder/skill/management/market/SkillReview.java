package net.ooder.skill.management.market;

import java.io.Serializable;

public class SkillReview implements Serializable {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private String userId;
    private int rating;
    private String comment;
    private long timestamp;

    public SkillReview() {
    }

    public SkillReview(String skillId, String userId, int rating, String comment) {
        this.skillId = skillId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
