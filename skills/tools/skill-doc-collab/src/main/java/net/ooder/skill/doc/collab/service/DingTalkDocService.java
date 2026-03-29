package net.ooder.skill.doc.collab.service;

import net.ooder.skill.doc.collab.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class DingTalkDocService {

    public DocumentDTO createDocument(DocumentDTO doc) {
        log.info("DingTalk: Creating document {}", doc.getTitle());
        String platformDocId = "dingtalk_doc_" + System.currentTimeMillis();
        doc.setPlatformDocId(platformDocId);
        doc.setEditUrl("https://doc.dingtalk.com/edit/" + platformDocId);
        doc.setViewUrl("https://doc.dingtalk.com/view/" + platformDocId);
        return doc;
    }

    public DocumentDTO updateDocument(DocumentDTO doc) {
        log.info("DingTalk: Updating document {}", doc.getDocId());
        return doc;
    }

    public boolean deleteDocument(String docId) {
        log.info("DingTalk: Deleting document {}", docId);
        return true;
    }
}
