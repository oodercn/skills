package net.ooder.skill.document.service.impl;

import net.ooder.skill.document.model.Document;
import net.ooder.skill.document.model.DocumentChunk;
import net.ooder.skill.document.model.ProcessingResult;
import net.ooder.skill.document.service.DocumentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentProcessorImpl implements DocumentProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentProcessorImpl.class);
    
    private static final double CHARS_PER_TOKEN = 1.5;
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[。！？.!?]\\s*");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("\\n\\s*\\n");
    private static final Pattern TITLE_PATTERN = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "的", "是", "在", "了", "和", "与", "或", "有", "这", "那", "我", "你", "他",
        "the", "a", "an", "is", "are", "was", "were", "be", "been", "being"
    ));
    
    @Override
    public ProcessingResult process(Document document) {
        long startTime = System.currentTimeMillis();
        
        ProcessingResult result = new ProcessingResult();
        result.setDocId(document.getId());
        result.setSuccess(false);
        
        try {
            String content = document.getContent();
            if (content == null || content.isEmpty()) {
                result.setErrorMessage("Document content is empty");
                return result;
            }
            
            if (document.getTitle() == null || document.getTitle().isEmpty()) {
                document.setTitle(extractTitle(content));
            }
            
            ChunkConfig config = new ChunkConfig();
            List<DocumentChunk> chunks = chunk(content, config);
            
            int totalTokens = 0;
            for (DocumentChunk chunk : chunks) {
                totalTokens += chunk.getTokenCount();
            }
            
            result.setSuccess(true);
            result.setChunkCount(chunks.size());
            result.setTotalTokens(totalTokens);
            result.setChunks(chunks);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("contentLength", content.length());
            statistics.put("avgChunkSize", chunks.isEmpty() ? 0 : content.length() / chunks.size());
            statistics.put("keywords", extractKeywords(content));
            result.setStatistics(statistics);
            
            log.info("Document processed: id={}, chunks={}, tokens={}", 
                document.getId(), chunks.size(), totalTokens);
            
        } catch (Exception e) {
            log.error("Failed to process document: {}", document.getId(), e);
            result.setErrorMessage(e.getMessage());
        }
        
        result.setProcessingTime(System.currentTimeMillis() - startTime);
        return result;
    }
    
    @Override
    public List<DocumentChunk> chunk(String content, ChunkConfig config) {
        List<DocumentChunk> chunks = new ArrayList<>();
        
        if (content == null || content.isEmpty()) {
            return chunks;
        }
        
        String[] paragraphs = PARAGRAPH_PATTERN.split(content);
        
        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;
        int startPosition = 0;
        
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;
            
            if (currentChunk.length() + paragraph.length() > config.getChunkSize() 
                && currentChunk.length() >= config.getMinChunkSize()) {
                
                DocumentChunk chunk = createChunk(
                    currentChunk.toString().trim(), 
                    chunkIndex++, 
                    startPosition
                );
                chunks.add(chunk);
                
                String overlap = getOverlapText(currentChunk.toString(), config.getChunkOverlap());
                currentChunk = new StringBuilder(overlap);
                startPosition += currentChunk.length();
            }
            
            currentChunk.append(paragraph).append("\n\n");
        }
        
        if (currentChunk.length() >= config.getMinChunkSize()) {
            DocumentChunk chunk = createChunk(
                currentChunk.toString().trim(), 
                chunkIndex, 
                startPosition
            );
            chunks.add(chunk);
        }
        
        return chunks;
    }
    
    private DocumentChunk createChunk(String content, int index, int startPosition) {
        DocumentChunk chunk = new DocumentChunk();
        chunk.setId(UUID.randomUUID().toString());
        chunk.setChunkIndex(index);
        chunk.setContent(content);
        chunk.setStartPosition(startPosition);
        chunk.setEndPosition(startPosition + content.length());
        chunk.setTokenCount(estimateTokens(content));
        return chunk;
    }
    
    private String getOverlapText(String text, int overlapSize) {
        if (text.length() <= overlapSize) {
            return text;
        }
        
        String overlap = text.substring(text.length() - overlapSize);
        
        Matcher matcher = SENTENCE_PATTERN.matcher(overlap);
        int firstSentenceEnd = -1;
        if (matcher.find()) {
            firstSentenceEnd = matcher.end();
        }
        
        if (firstSentenceEnd > 0 && firstSentenceEnd < overlap.length()) {
            return overlap.substring(firstSentenceEnd).trim();
        }
        
        return overlap;
    }
    
    @Override
    public String extractText(byte[] data, String fileType) {
        if (data == null || data.length == 0) {
            return "";
        }
        
        String lowerType = fileType.toLowerCase();
        
        switch (lowerType) {
            case ".txt":
            case ".md":
            case ".json":
            case ".yaml":
            case ".yml":
            case ".xml":
            case ".csv":
                return new String(data, StandardCharsets.UTF_8);
                
            case ".html":
            case ".htm":
                return extractTextFromHtml(new String(data, StandardCharsets.UTF_8));
                
            default:
                return new String(data, StandardCharsets.UTF_8);
        }
    }
    
    private String extractTextFromHtml(String html) {
        return html.replaceAll("<[^>]+>", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    @Override
    public String extractTitle(String content) {
        if (content == null || content.isEmpty()) {
            return "Untitled";
        }
        
        Matcher matcher = TITLE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        String[] lines = content.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() < 100) {
                return line;
            }
        }
        
        return content.substring(0, Math.min(50, content.length()));
    }
    
    @Override
    public List<String> extractKeywords(String content) {
        Map<String, Integer> wordFreq = new HashMap<>();
        
        String[] words = content.toLowerCase()
            .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
            .split("\\s+");
        
        for (String word : words) {
            if (word.length() < 2 || STOP_WORDS.contains(word)) {
                continue;
            }
            wordFreq.merge(word, 1, Integer::sum);
        }
        
        return wordFreq.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(10)
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public int estimateTokens(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil(content.length() / CHARS_PER_TOKEN);
    }
}
