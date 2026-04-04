package net.ooder.skill.tenant.model;

import java.util.Map;

public class TenantQuotaDTO {
    private Map<String, Object> quota;

    public Map<String, Object> getQuota() { return quota; }
    public void setQuota(Map<String, Object> quota) { this.quota = quota; }
}
