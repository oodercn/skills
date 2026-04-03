package net.ooder.skill.llm.config.service;

public interface ApiKeyProvider {
    
    String getApiKey(String providerType);
    
    String getApiKey(String providerType, String scopeId);
    
    boolean hasApiKey(String providerType);
    
    void setApiKey(String providerType, String apiKey);
    
    ApiKeySource getApiKeySource(String providerType);
    
    enum ApiKeySource {
        COMMAND_LINE(1, "启动参数"),
        ENVIRONMENT(2, "环境变量"),
        CONFIG_FILE(3, "配置文件"),
        DATABASE(4, "数据库"),
        DEFAULT(5, "默认值");
        
        private final int priority;
        private final String description;
        
        ApiKeySource(int priority, String description) {
            this.priority = priority;
            this.description = description;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
