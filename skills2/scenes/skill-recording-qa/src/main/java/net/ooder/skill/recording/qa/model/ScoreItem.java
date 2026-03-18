package net.ooder.skill.recording.qa.model;

/**
 * 评分项
 */
public class ScoreItem {
    
    private String id;
    private String name;
    private Integer weight;
    private Integer maxScore;
    private Integer actualScore;
    private String comment;
    
    public ScoreItem() {}
    
    public ScoreItem(String id, String name, Integer weight, Integer maxScore) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.maxScore = maxScore;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public Integer getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
    
    public Integer getActualScore() {
        return actualScore;
    }
    
    public void setActualScore(Integer actualScore) {
        this.actualScore = actualScore;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}
