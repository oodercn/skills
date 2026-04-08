package net.ooder.skill.template.dto;

import java.util.List;

public class PageResult<T> {
    
    private List<T> items;
    private int total;
    private int pageNum;
    private int pageSize;

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
