package net.ooder.skill.procedure.model;

import java.util.List;

public class PageResult<T> {
    private List<T> data;
    private long total;
    private int pageNum;
    private int pageSize;
    private int totalPages;

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
    
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
