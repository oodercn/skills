package net.ooder.skill.group.dto;

public class GroupMember {
    private String memberId;
    private String userId;
    private String userName;
    private String avatar;
    private String role;
    private long joinTime;
    private long lastReadTime;
    private int muteStatus;

    public GroupMember() {
        this.role = "member";
        this.joinTime = System.currentTimeMillis();
        this.lastReadTime = System.currentTimeMillis();
        this.muteStatus = 0;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public int getMuteStatus() {
        return muteStatus;
    }

    public void setMuteStatus(int muteStatus) {
        this.muteStatus = muteStatus;
    }
}
