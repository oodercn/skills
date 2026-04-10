package net.ooder.skill.document.markdown;

import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.facade.SpiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Markdown 文档解析器自动配置
 */
@AutoConfiguration
@ConditionalOnClass(DocumentParser.class)
public class MarkdownDocumentParserAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(MarkdownDocumentParserAutoConfiguration.class);
    
    @Bean
    @ConditionalOnMissingBean(name = "markdownDocumentParser")
    public DocumentParser markdownDocumentParser() {
        logger.info("Initializing MarkdownDocumentParser");
        return new MarkdownDocumentParser();
    }
    
    @Bean
    public MarkdownParserInitializer markdownParserInitializer(DocumentParser markdownDocumentParser) {
        logger.info("Initializing MarkdownParserInitializer");
        return new MarkdownParserInitializer(markdownDocumentParser);
    }
    
    /**
     * Markdown 解析器初始化器
     */
    public static class MarkdownParserInitializer {
        
        private static final Logger logger = LoggerFactory.getLogger(MarkdownParserInitializer.class);
        
        public MarkdownParserInitializer(DocumentParser documentParser) {
            SpiServices services = SpiServices.getInstance();
            if (services != null) {
                List<DocumentParser> parsers = services.getDocumentParsers();
                if (parsers != null && !parsers.contains(documentParser)) {
                    parsers.add(documentParser);
                    logger.info("MarkdownDocumentParser registered to SpiServices");
                } else if (parsers == null) {
                    services.setDocumentParsers(List.of(documentParser));
                    logger.info("MarkdownDocumentParser registered to SpiServices (new list)");
                }
            } else {
                logger.warn("SpiServices instance not found, DocumentParser not registered");
            }
        }
    }
}
