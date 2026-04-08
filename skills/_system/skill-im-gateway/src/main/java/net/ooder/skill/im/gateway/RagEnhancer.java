package net.ooder.skill.im.gateway;

import net.ooder.spi.rag.RagEnhanceDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RagEnhancer {

    private static final Logger log = LoggerFactory.getLogger(RagEnhancer.class);

    @Autowired(required = false)
    private RagEnhanceDriver ragEnhanceDriver;

    public boolean isRagAvailable() {
        return ragEnhanceDriver != null && ragEnhanceDriver.isAvailable();
    }

    public String enanceForIm(String userMessage, String tenantId) {
        if (ragEnhanceDriver == null || !ragEnhanceDriver.isAvailable() ||
                userMessage == null || userMessage.isEmpty()) {
            return null;
        }
        try {
            List<String> kbIds = tenantId != null ? List.of(tenantId + "-kb") : null;
            String enhanced = ragEnhanceDriver.enhancePrompt(userMessage, null, kbIds);
            log.debug("[RagEnhancer] IM message enhanced, original length={}, enhanced length={}",
                    userMessage.length(), enhanced != null ? enhanced.length() : 0);
            return enhanced;
        } catch (Exception e) {
            log.warn("[RagEnhancer] RAG enhancement for IM failed: {}", e.getMessage());
            return null;
        }
    }
}
