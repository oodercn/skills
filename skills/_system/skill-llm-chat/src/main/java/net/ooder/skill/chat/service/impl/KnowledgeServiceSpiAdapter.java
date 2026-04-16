package net.ooder.skill.chat.service.impl;

import net.ooder.skill.chat.model.KnowledgeDocument;
import net.ooder.skill.chat.service.KnowledgeService;
import net.ooder.spi.facade.SpiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * KnowledgeService SPI 适配器
 * 将内部 KnowledgeService 实现适配为 SPI 接口
 */
@Component
public class KnowledgeServiceSpiAdapter implements net.ooder.spi.knowledge.KnowledgeService {

    @Autowired
    private KnowledgeService knowledgeService;

    public KnowledgeServiceSpiAdapter() {
        // 构造函数中注册到 SpiServices
        // 注意：此时 knowledgeService 还未注入，实际注册在第一次调用时完成
    }

    private void ensureRegistered() {
        if (SpiServices.getInstance() != null && SpiServices.getInstance().getKnowledgeService() == null) {
            SpiServices.getInstance().setKnowledgeService(this);
        }
    }

    @Override
    public net.ooder.spi.knowledge.KnowledgeDocument uploadDocument(String userId, String title, String content, String dataType) {
        ensureRegistered();
        KnowledgeDocument doc = knowledgeService.uploadDocument(userId, title, content, dataType);
        return toSpiDocument(doc);
    }

    @Override
    public net.ooder.spi.knowledge.KnowledgeDocument getDocument(String docId) {
        KnowledgeDocument doc = knowledgeService.getDocument(docId);
        return doc != null ? toSpiDocument(doc) : null;
    }

    @Override
    public List<String> search(String query, int limit) {
        return knowledgeService.search(query, limit);
    }

    @Override
    public boolean deleteDocument(String docId) {
        knowledgeService.deleteDocument(docId);
        return true;
    }

    @Override
    public net.ooder.spi.knowledge.KnowledgeDocument updateDocument(String docId, String title, String content) {
        // 先删除旧文档，再上传新文档
        deleteDocument(docId);
        return uploadDocument(null, title, content, "text");
    }

    private net.ooder.spi.knowledge.KnowledgeDocument toSpiDocument(KnowledgeDocument doc) {
        if (doc == null) return null;
        net.ooder.spi.knowledge.KnowledgeDocument spiDoc = new net.ooder.spi.knowledge.KnowledgeDocument();
        spiDoc.setDocId(doc.getDocId());
        spiDoc.setTitle(doc.getTitle());
        spiDoc.setContent(doc.getContent());
        spiDoc.setDataType(doc.getType());
        spiDoc.setUserId(doc.getUserId());
        spiDoc.setCreateTime(doc.getCreatedAt() != null ? doc.getCreatedAt().getTime() : 0);
        spiDoc.setUpdateTime(doc.getUpdatedAt() != null ? doc.getUpdatedAt().getTime() : 0);
        spiDoc.setStatus(toSpiStatus(doc.getStatus()));
        return spiDoc;
    }

    private net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus toSpiStatus(KnowledgeDocument.DocumentStatus status) {
        if (status == null) return net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus.ACTIVE;
        switch (status) {
            case PROCESSING:
                return net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus.ACTIVE;
            case READY:
                return net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus.ACTIVE;
            case ERROR:
                return net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus.ARCHIVED;
            default:
                return net.ooder.spi.knowledge.KnowledgeDocument.DocumentStatus.ACTIVE;
        }
    }
}
