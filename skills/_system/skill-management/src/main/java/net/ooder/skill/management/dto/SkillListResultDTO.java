package net.ooder.skill.management.dto;

import java.util.List;

public class SkillListResultDTO {
    
    private String status;
    private List<?> data;
    private int total;
    private int page;
    private int size;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<?> getData() { return data; }
    public void setData(List<?> data) { this.data = data; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
