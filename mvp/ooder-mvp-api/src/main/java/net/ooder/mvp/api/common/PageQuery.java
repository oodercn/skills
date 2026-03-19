package net.ooder.mvp.api.common;

import java.io.Serializable;
import java.util.List;

public class PageQuery implements Serializable {
    private int pageNum = 1;
    private int pageSize = 10;
    private String sortBy;
    private String sortOrder = "DESC";
    
    public PageQuery() {}
    
    public PageQuery(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
