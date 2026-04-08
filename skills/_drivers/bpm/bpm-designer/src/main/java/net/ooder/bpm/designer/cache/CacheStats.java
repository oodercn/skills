package net.ooder.bpm.designer.cache;

public class CacheStats {
    
    private final int currentSize;
    private final int maxSize;
    
    public CacheStats(int currentSize, int maxSize) {
        this.currentSize = currentSize;
        this.maxSize = maxSize;
    }
    
    public int getCurrentSize() {
        return currentSize;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public double getUsagePercent() {
        return maxSize > 0 ? (currentSize * 100.0 / maxSize) : 0;
    }
    
    @Override
    public String toString() {
        return String.format("CacheStats{currentSize=%d, maxSize=%d, usage=%.1f%%}", 
            currentSize, maxSize, getUsagePercent());
    }
}
