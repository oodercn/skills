package net.ooder.skill.common.spi;

public interface SceneServices {
    
    UserService getUserService();
    
    StorageService getStorageService();
    
    ConfigService getConfigService();
    
    OrganizationService getOrganizationService();
    
    PermissionService getPermissionService();
    
    MessageService getMessageService();
    
    AuditService getAuditService();
}
