package net.ooder.skill.doc.collab.service;

import net.ooder.skill.doc.collab.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class WeComDocService {

    public DocumentDTO createDocument(DocumentDTO doc) {
        log.info("WeCom: Creating document {}", doc.getTitle());
        String platformDocId = "wecom_doc_" + System.currentTimeMillis();
        doc.setPlatformDocId(platformDocId);
        doc.setEditUrl("https://doc.wecom.qq.com/edit/" + platformDocId);
        doc.setViewUrl("https://doc.wecom.qq.com/view/" + platformDocId);
        return doc;
    }

    public DocumentDTO updateDocument(DocumentDTO doc) {
        log.info("WeCom: Updating document {}", doc.getDocId());
        return doc;
    }

    public boolean deleteDocument(String docId) {
        log.info("WeCom: Deleting document {}", docId);
        return true;
    }
}
