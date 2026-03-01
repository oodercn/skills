package net.ooder.skill.document.service;

import net.ooder.skill.document.model.Document;
import net.ooder.skill.document.model.DocumentChunk;
import net.ooder.skill.document.model.ProcessingResult;

import java.util.List;

public interface DocumentProcessor {
    
    ProcessingResult process(Document document);
    
    List<DocumentChunk> chunk(String content, ChunkConfig config);
    
    String extractText(byte[] data, String fileType);
    
    String extractTitle(String content);
    
    List<String> extractKeywords(String content);
    
    int estimateTokens(String content);
    
    class ChunkConfig {
        private int chunkSize = 500;
        private int chunkOverlap = 50;
        private boolean respectSentenceBoundary = true;
        private int minChunkSize = 100;
        
        public int getChunkSize() { return chunkSize; }
        public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }
        public int getChunkOverlap() { return chunkOverlap; }
        public void setChunkOverlap(int chunkOverlap) { this.chunkOverlap = chunkOverlap; }
        public boolean isRespectSentenceBoundary() { return respectSentenceBoundary; }
        public void setRespectSentenceBoundary(boolean respectSentenceBoundary) { this.respectSentenceBoundary = respectSentenceBoundary; }
        public int getMinChunkSize() { return minChunkSize; }
        public void setMinChunkSize(int minChunkSize) { this.minChunkSize = minChunkSize; }
    }
}
