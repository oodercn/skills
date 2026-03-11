package net.ooder.skill.security.dto;

import lombok.Data;

@Data
public class SecurityConfig {
    
    private boolean enableAuth;
    private boolean enableEncryption;
    private boolean enableAudit;
    private int sessionTimeout;
    private int maxLoginAttempts;
    private int keyRotationDays;
    private boolean enableFirewall;
    private String firewallMode;
    private boolean enableAgentAuth;
    private boolean enableAgentEncryption;
    private boolean enableAgentIsolation;
    private int llmRateLimit;
    private int costAlertThreshold;
    
    public SecurityConfig() {
        this.enableAuth = true;
        this.enableEncryption = true;
        this.enableAudit = true;
        this.sessionTimeout = 30;
        this.maxLoginAttempts = 5;
        this.keyRotationDays = 90;
        this.enableFirewall = false;
        this.firewallMode = "active";
        this.enableAgentAuth = true;
        this.enableAgentEncryption = true;
        this.enableAgentIsolation = true;
        this.llmRateLimit = 60;
        this.costAlertThreshold = 100;
    }
}
