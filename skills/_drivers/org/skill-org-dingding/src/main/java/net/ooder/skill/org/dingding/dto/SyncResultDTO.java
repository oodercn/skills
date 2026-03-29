package net.ooder.skill.org.dingding.dto;

import java.io.Serializable;
import java.util.List;

public class SyncResultDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private int totalUsers;
    private int totalDepartments;
    private int createdUsers;
    private int updatedUsers;
    private int createdDepartments;
    private int updatedDepartments;
    private List<String> errors;
    private long syncTime;
    private String message;
    
    public static SyncResultDTO success(int users, int depts) {
        SyncResultDTO result = new SyncResultDTO();
        result.setSuccess(true);
        result.setTotalUsers(users);
        result.setTotalDepartments(depts);
        result.setSyncTime(System.currentTimeMillis());
        result.setMessage("同步成功");
        return result;
    }
    
    public static SyncResultDTO fail(String message) {
        SyncResultDTO result = new SyncResultDTO();
        result.setSuccess(false);
        result.setMessage(message);
        result.setSyncTime(System.currentTimeMillis());
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public int getTotalDepartments() {
        return totalDepartments;
    }
    
    public void setTotalDepartments(int totalDepartments) {
        this.totalDepartments = totalDepartments;
    }
    
    public int getCreatedUsers() {
        return createdUsers;
    }
    
    public void setCreatedUsers(int createdUsers) {
        this.createdUsers = createdUsers;
    }
    
    public int getUpdatedUsers() {
        return updatedUsers;
    }
    
    public void setUpdatedUsers(int updatedUsers) {
        this.updatedUsers = updatedUsers;
    }
    
    public int getCreatedDepartments() {
        return createdDepartments;
    }
    
    public void setCreatedDepartments(int createdDepartments) {
        this.createdDepartments = createdDepartments;
    }
    
    public int getUpdatedDepartments() {
        return updatedDepartments;
    }
    
    public void setUpdatedDepartments(int updatedDepartments) {
        this.updatedDepartments = updatedDepartments;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public long getSyncTime() {
        return syncTime;
    }
    
    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
