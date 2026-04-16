package net.ooder.skill.org.base;

import java.util.List;

/**
 * PageRequest 鍒嗛〉璇锋眰
 * 
 * @author Ooder Team
 * @version 2.3
 */
public class PageRequest {

    private int pageNum = 1;
    private int pageSize = 20;
    private String sortBy;
    private String sortOrder = "asc";

    public PageRequest() {
    }

    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
