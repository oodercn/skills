package net.ooder.skill.doc.collab.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String docId;
    private String title;
    private String docType;
    private String content;
    private String platform;
    private String platformDocId;
    private String owner;
    private List<String> editors;
    private List<String> viewers;
    private String permission;
    private String createTime;
    private String updateTime;
    private String editUrl;
    private String viewUrl;
    private Long fileSize;
    private String fileType;
    private Integer version;
    private String folderId;
    private List<String> tags;
}
