package net.ooder.skill.doc.collab.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocShareDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String shareId;
    private String docId;
    private String shareUrl;
    private String permission;
    private String expireTime;
    private String password;
    private Boolean needPassword;
    private String createTime;
    private String creator;
    private Integer viewCount;
    private Integer downloadCount;
}
