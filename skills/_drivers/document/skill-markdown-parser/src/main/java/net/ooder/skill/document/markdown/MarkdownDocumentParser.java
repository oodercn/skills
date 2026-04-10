package net.ooder.skill.document.markdown;

import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.document.ParseResult;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 文档解析器
 */
public class MarkdownDocumentParser implements DocumentParser {
    
    private static final Logger logger = LoggerFactory.getLogger(MarkdownDocumentParser.class);
    
    private static final String PARSER_NAME = "Markdown Parser";
    
    private static final List<String> SUPPORTED_MIME_TYPES = Arrays.asList(
        "text/markdown",
        "text/x-markdown",
        "text/plain",
        "application/markdown"
    );
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        ".md",
        ".markdown",
        ".txt",
        ".text"
    );
    
    private static final Pattern TITLE_PATTERN = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern METADATA_PATTERN = Pattern.compile("^---\\s*$", Pattern.MULTILINE);
    
    private final Parser parser;
    private final TextContentRenderer textRenderer;
    
    public MarkdownDocumentParser() {
        this.parser = Parser.builder().build();
        this.textRenderer = TextContentRenderer.builder().build();
    }
    
    @Override
    public String getParserName() {
        return PARSER_NAME;
    }
    
    @Override
    public List<String> getSupportedMimeTypes() {
        return SUPPORTED_MIME_TYPES;
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    @Override
    public boolean supports(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        
        String normalizedMimeType = mimeType.toLowerCase().trim();
        return SUPPORTED_MIME_TYPES.stream()
            .anyMatch(supported -> supported.equalsIgnoreCase(normalizedMimeType));
    }
    
    @Override
    public ParseResult parse(InputStream inputStream, String mimeType) {
        return parseWithMetadata(inputStream, mimeType);
    }
    
    @Override
    public ParseResult parseWithMetadata(InputStream inputStream, String mimeType) {
        try {
            String content = readContent(inputStream, StandardCharsets.UTF_8);
            
            String text = extractText(content);
            
            ParseResult result = ParseResult.success(text);
            
            extractMetadata(content, result);
            
            result.setFileSize((long) content.getBytes(StandardCharsets.UTF_8).length);
            result.addMetadata("mimeType", mimeType);
            result.addMetadata("parser", PARSER_NAME);
            
            logger.debug("Successfully parsed markdown document, text length: {}", text.length());
            
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to parse markdown document: {}", e.getMessage(), e);
            return ParseResult.failure("Failed to parse document: " + e.getMessage());
        }
    }
    
    @Override
    public int getPriority() {
        return 50;
    }
    
    private String readContent(InputStream inputStream, Charset charset) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private String extractText(String markdownContent) {
        try {
            Node document = parser.parse(markdownContent);
            return textRenderer.render(document);
        } catch (Exception e) {
            logger.warn("Failed to parse markdown, returning raw content: {}", e.getMessage());
            return markdownContent;
        }
    }
    
    private void extractMetadata(String content, ParseResult result) {
        Matcher titleMatcher = TITLE_PATTERN.matcher(content);
        if (titleMatcher.find()) {
            String title = titleMatcher.group(1).trim();
            result.setTitle(title);
            result.addMetadata("title", title);
        }
        
        String[] lines = content.split("\n");
        int lineCount = lines.length;
        int wordCount = 0;
        int charCount = content.length();
        
        for (String line : lines) {
            String[] words = line.trim().split("\\s+");
            wordCount += words.length > 0 && !words[0].isEmpty() ? words.length : 0;
        }
        
        result.addMetadata("lineCount", lineCount);
        result.addMetadata("wordCount", wordCount);
        result.addMetadata("charCount", charCount);
        
        if (METADATA_PATTERN.matcher(content).find()) {
            result.addMetadata("hasYamlFrontMatter", true);
        }
        
        int headingCount = countPattern(content, "^#{1,6}\\s+.+$", Pattern.MULTILINE);
        result.addMetadata("headingCount", headingCount);
        
        int codeBlockCount = countPattern(content, "^```", Pattern.MULTILINE) / 2;
        result.addMetadata("codeBlockCount", codeBlockCount);
        
        int linkCount = countPattern(content, "\\[.+?\\]\\(.+?\\)", 0);
        result.addMetadata("linkCount", linkCount);
        
        int imageCount = countPattern(content, "!\\[.*?\\]\\(.+?\\)", 0);
        result.addMetadata("imageCount", imageCount);
    }
    
    private int countPattern(String content, String regex, int flags) {
        Pattern pattern = Pattern.compile(regex, flags);
        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
