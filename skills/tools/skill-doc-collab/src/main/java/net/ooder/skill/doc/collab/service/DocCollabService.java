package net.ooder.skill.doc.collab.service;

import net.ooder.skill.doc.collab.dto.*;
import net.ooder.skill.doc.collab.dict.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class DocCollabService {

    @Autowired
    private FeishuDocService feishuDocService;

    @Autowired
    private DingTalkDocService dingTalkDocService;

    @Autowired
    private WeComDocService weComDocService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DocumentDTO createDocument(DocumentDTO doc) {
        log.info("Creating document: {}", doc.getTitle());
        
        doc.setDocId(UUID.randomUUID().toString());
        doc.setCreateTime(LocalDateTime.now().format(FORMATTER));
        doc.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        doc.setVersion(1);
        
        if (doc.getPlatform() != null) {
            DocumentDTO platformDoc = createOnPlatform(doc);
            if (platformDoc != null) {
                doc.setPlatformDocId(platformDoc.getPlatformDocId());
                doc.setEditUrl(platformDoc.getEditUrl());
                doc.setViewUrl(platformDoc.getViewUrl());
            }
        }
        
        return doc;
    }

    public DocumentDTO getDocument(String docId) {
        log.info("Getting document: {}", docId);
        return DocumentDTO.builder()
                .docId(docId)
                .title("示例文档")
                .docType(DocType.DOCUMENT.getCode())
                .content("这是一个示例文档内容")
                .permission(DocPermission.EDIT.getCode())
                .version(1)
                .build();
    }

    public DocumentDTO updateDocument(String docId, DocumentDTO doc) {
        log.info("Updating document: {}", docId);
        doc.setDocId(docId);
        doc.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        doc.setVersion(doc.getVersion() != null ? doc.getVersion() + 1 : 1);
        
        if (doc.getPlatform() != null && doc.getPlatformDocId() != null) {
            updateOnPlatform(doc);
        }
        
        return doc;
    }

    public boolean deleteDocument(String docId, String platform) {
        log.info("Deleting document: {}", docId);
        
        if (platform != null) {
            deleteFromPlatform(docId, platform);
        }
        
        return true;
    }

    public DocShareDTO shareDocument(String docId, String permission, String expireTime) {
        log.info("Sharing document: {}", docId);
        
        return DocShareDTO.builder()
                .shareId(UUID.randomUUID().toString())
                .docId(docId)
                .shareUrl("https://doc.example.com/share/" + UUID.randomUUID().toString().substring(0, 8))
                .permission(permission)
                .expireTime(expireTime)
                .needPassword(false)
                .createTime(LocalDateTime.now().format(FORMATTER))
                .viewCount(0)
                .downloadCount(0)
                .build();
    }

    public DocCommentDTO addComment(String docId, DocCommentDTO comment) {
        log.info("Adding comment to document: {}", docId);
        
        comment.setCommentId(UUID.randomUUID().toString());
        comment.setDocId(docId);
        comment.setCreateTime(LocalDateTime.now().format(FORMATTER));
        
        return comment;
    }

    public List<DocCommentDTO> getComments(String docId) {
        log.info("Getting comments for document: {}", docId);
        
        List<DocCommentDTO> comments = new ArrayList<>();
        comments.add(DocCommentDTO.builder()
                .commentId(UUID.randomUUID().toString())
                .docId(docId)
                .userId("user1")
                .userName("用户A")
                .content("这是一条评论")
                .createTime(LocalDateTime.now().format(FORMATTER))
                .build());
        
        return comments;
    }

    public byte[] exportDocument(String docId, String format) {
        log.info("Exporting document: {} as {}", docId, format);
        return ("Document content for " + docId).getBytes();
    }

    public List<DocumentDTO> listDocuments(String userId, String folderId) {
        log.info("Listing documents for user: {}, folder: {}", userId, folderId);
        
        List<DocumentDTO> docs = new ArrayList<>();
        docs.add(DocumentDTO.builder()
                .docId(UUID.randomUUID().toString())
                .title("项目文档")
                .docType(DocType.DOCUMENT.getCode())
                .owner(userId)
                .permission(DocPermission.OWNER.getCode())
                .version(3)
                .build());
        docs.add(DocumentDTO.builder()
                .docId(UUID.randomUUID().toString())
                .title("数据表格")
                .docType(DocType.SPREADSHEET.getCode())
                .owner(userId)
                .permission(DocPermission.OWNER.getCode())
                .version(1)
                .build());
        
        return docs;
    }

    private DocumentDTO createOnPlatform(DocumentDTO doc) {
        switch (doc.getPlatform().toUpperCase()) {
            case "FEISHU":
                return feishuDocService.createDocument(doc);
            case "DINGTALK":
                return dingTalkDocService.createDocument(doc);
            case "WECOM":
                return weComDocService.createDocument(doc);
            default:
                return null;
        }
    }

    private void updateOnPlatform(DocumentDTO doc) {
        switch (doc.getPlatform().toUpperCase()) {
            case "FEISHU":
                feishuDocService.updateDocument(doc);
                break;
            case "DINGTALK":
                dingTalkDocService.updateDocument(doc);
                break;
            case "WECOM":
                weComDocService.updateDocument(doc);
                break;
            default:
                break;
        }
    }

    private void deleteFromPlatform(String docId, String platform) {
        switch (platform.toUpperCase()) {
            case "FEISHU":
                feishuDocService.deleteDocument(docId);
                break;
            case "DINGTALK":
                dingTalkDocService.deleteDocument(docId);
                break;
            case "WECOM":
                weComDocService.deleteDocument(docId);
                break;
            default:
                break;
        }
    }
}
