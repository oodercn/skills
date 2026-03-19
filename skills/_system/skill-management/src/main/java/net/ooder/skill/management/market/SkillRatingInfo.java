package net.ooder.skill.management.market;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SkillRatingInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private List<SkillReview> reviews;
    private double averageRating;
    private int totalRatings;

    public SkillRatingInfo() {
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
        this.totalRatings = 0;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<SkillReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<SkillReview> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        recalculateAverage();
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public int getReviewCount() {
        return reviews.size();
    }

    public void addReview(SkillReview review) {
        if (review != null) {
            reviews.add(review);
            recalculateAverage();
        }
    }

    public void removeReview(String userId) {
        reviews.removeIf(r -> userId.equals(r.getUserId()));
        recalculateAverage();
    }

    private void recalculateAverage() {
        if (reviews.isEmpty()) {
            averageRating = 0.0;
            totalRatings = 0;
            return;
        }
        
        double sum = 0;
        for (SkillReview review : reviews) {
            sum += review.getRating();
        }
        averageRating = sum / reviews.size();
        totalRatings = reviews.size();
    }
}
