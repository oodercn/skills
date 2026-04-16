package net.ooder.spi.document;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DocumentParser {
    
    String getParserName();
    
    List<String> getSupportedMimeTypes();
    
    List<String> getSupportedExtensions();
    
    boolean supports(String mimeType);
    
    ParseResult parse(InputStream inputStream, String mimeType);
    
    ParseResult parseWithMetadata(InputStream inputStream, String mimeType);
    
    default int getPriority() {
        return 100;
    }
}
