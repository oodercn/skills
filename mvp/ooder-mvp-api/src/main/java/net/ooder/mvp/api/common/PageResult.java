package net.ooder.mvp.api.common;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
    private List<T> list;
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    
    public PageResult() {}
    
    public PageResult(List<T> list, int pageNum, int pageSize, long total) {
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
    
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}
