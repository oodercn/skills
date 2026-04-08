package net.ooder.skill.driver.config.dto;

import java.util.Map;

public class AllDriversTestResultDTO {
    
    private int total;
    private long configured;
    private Map<String, Boolean> results;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public long getConfigured() { return configured; }
    public void setConfigured(long configured) { this.configured = configured; }
    public Map<String, Boolean> getResults() { return results; }
    public void setResults(Map<String, Boolean> results) { this.results = results; }
}
