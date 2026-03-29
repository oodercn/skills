package net.ooder.skill.doc.collab.service;

import net.ooder.skill.doc.collab.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class FeishuDocService {

    public DocumentDTO createDocument(DocumentDTO doc) {
        log.info("Feishu: Creating document {}", doc.getTitle());
        String platformDocId = "feishu_doc_" + System.currentTimeMillis();
        doc.setPlatformDocId(platformDocId);
        doc.setEditUrl("https://feishu.cn/docx/edit/" + platformDocId);
        doc.setViewUrl("https://feishu.cn/docx/" + platformDocId);
        return doc;
    }

    public DocumentDTO updateDocument(DocumentDTO doc) {
        log.info("Feishu: Updating document {}", doc.getDocId());
        return doc;
    }

    public boolean deleteDocument(String docId) {
        log.info("Feishu: Deleting document {}", docId);
        return true;
    }
}
