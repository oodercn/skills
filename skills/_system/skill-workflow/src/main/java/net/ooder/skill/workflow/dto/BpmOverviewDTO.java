package net.ooder.skill.workflow.dto;

public class BpmOverviewDTO {
    
    private int waitedCount;
    private int myWorkCount;
    private int completedCount;
    private int readCount;
    private int draftCount;

    public int getWaitedCount() { return waitedCount; }
    public void setWaitedCount(int waitedCount) { this.waitedCount = waitedCount; }
    public int getMyWorkCount() { return myWorkCount; }
    public void setMyWorkCount(int myWorkCount) { this.myWorkCount = myWorkCount; }
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    public int getReadCount() { return readCount; }
    public void setReadCount(int readCount) { this.readCount = readCount; }
    public int getDraftCount() { return draftCount; }
    public void setDraftCount(int draftCount) { this.draftCount = draftCount; }
}
