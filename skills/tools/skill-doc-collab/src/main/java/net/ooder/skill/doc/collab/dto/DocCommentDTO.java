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
public class DocCommentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commentId;
    private String docId;
    private String userId;
    private String userName;
    private String content;
    private String parentId;
    private List<String> replyIds;
    private String createTime;
    private String position;
    private String quotedText;
}
