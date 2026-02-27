package net.ooder.skill.org.base;

import java.util.List;

/**
 * PageResult 鍒嗛〉缁撴灉
 * 
 * @author Ooder Team
 * @version 2.3
 */
public class PageResult<T> {

    private List<T> items;
    private long total;
    private int pageNum;
    private int pageSize;
    private int totalPages;

    public PageResult() {
    }

    public PageResult(List<T> items, long total, int pageNum, int pageSize) {
        this.items = items;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> of(List<T> items, long total, int pageNum, int pageSize) {
        return new PageResult<T>(items, total, pageNum, pageSize);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
