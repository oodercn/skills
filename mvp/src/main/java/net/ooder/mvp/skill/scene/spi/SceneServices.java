package net.ooder.mvp.skill.scene.spi;

public interface SceneServices {
    
    UserService getUserService();
    
    OrganizationService getOrganizationService();
    
    PermissionService getPermissionService();
    
    StorageService getStorageService();
    
    MessageService getMessageService();
    
    ConfigService getConfigService();
    
    AuditService getAuditService();
}
