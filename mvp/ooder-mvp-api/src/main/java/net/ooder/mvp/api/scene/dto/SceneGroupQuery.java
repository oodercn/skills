package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class SceneGroupQuery implements Serializable {
    private String templateId;
    private String creatorId;
    private String status;
    private String keyword;
    private int pageNum = 1;
    private int pageSize = 10;
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
