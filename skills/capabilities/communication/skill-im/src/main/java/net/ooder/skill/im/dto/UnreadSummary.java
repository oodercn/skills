package net.ooder.skill.im.dto;

public class UnreadSummary {
    private int totalUnread;
    private int conversationUnread;
    private int groupUnread;
    private int systemUnread;
    private long updateTime;

    public UnreadSummary() {
        this.updateTime = System.currentTimeMillis();
    }

    public int getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(int totalUnread) {
        this.totalUnread = totalUnread;
    }

    public int getConversationUnread() {
        return conversationUnread;
    }

    public void setConversationUnread(int conversationUnread) {
        this.conversationUnread = conversationUnread;
    }

    public int getGroupUnread() {
        return groupUnread;
    }

    public void setGroupUnread(int groupUnread) {
        this.groupUnread = groupUnread;
    }

    public int getSystemUnread() {
        return systemUnread;
    }

    public void setSystemUnread(int systemUnread) {
        this.systemUnread = systemUnread;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
