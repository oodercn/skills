package net.ooder.skill.install.scene.dto;

public class InstallStepDTO {

    private int step;
    private String name;
    private String description;
    private String status;
    private double progress;

    public int getStep() { return step; }
    public void setStep(int step) { this.step = step; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}