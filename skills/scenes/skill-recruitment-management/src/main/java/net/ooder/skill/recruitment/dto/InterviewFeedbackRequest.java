package net.ooder.skill.recruitment.dto;

public class InterviewFeedbackRequest {
    private String result;
    private String feedback;
    private int score;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
