package net.ooder.spi.vector;

import java.util.HashMap;
import java.util.Map;

public class VectorStoreConfig {
    
    private String providerType;
    private String collectionName;
    private int dimension;
    private String metricType;
    private String dataDir;
    private Map<String, Object> parameters = new HashMap<>();
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getProviderType() {
        return providerType;
    }
    
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }
    
    public String getCollectionName() {
        return collectionName;
    }
    
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    public String getMetricType() {
        return metricType;
    }
    
    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
    
    public String getDataDir() {
        return dataDir;
    }
    
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public static class Builder {
        private VectorStoreConfig config = new VectorStoreConfig();
        
        public Builder providerType(String providerType) {
            config.setProviderType(providerType);
            return this;
        }
        
        public Builder collectionName(String collectionName) {
            config.setCollectionName(collectionName);
            return this;
        }
        
        public Builder dimension(int dimension) {
            config.setDimension(dimension);
            return this;
        }
        
        public Builder metricType(String metricType) {
            config.setMetricType(metricType);
            return this;
        }
        
        public Builder dataDir(String dataDir) {
            config.setDataDir(dataDir);
            return this;
        }
        
        public Builder parameter(String key, Object value) {
            config.getParameters().put(key, value);
            return this;
        }
        
        public VectorStoreConfig build() {
            return config;
        }
    }
}
