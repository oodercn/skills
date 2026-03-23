package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MvpSceneServicesImpl implements SceneServices {
    
    private static final Logger log = LoggerFactory.getLogger(MvpSceneServicesImpl.class);
    
    private final MvpUserService userService;
    private final MvpOrganizationService organizationService;
    private final MvpPermissionService permissionService;
    private final MvpStorageService storageService;
    private final MvpMessageService messageService;
    private final MvpConfigService configService;
    private final MvpAuditService auditService;
    
    @Autowired
    public MvpSceneServicesImpl(
            MvpUserService userService,
            MvpOrganizationService organizationService,
            MvpPermissionService permissionService,
            MvpStorageService storageService,
            MvpMessageService messageService,
            MvpConfigService configService,
            MvpAuditService auditService) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.permissionService = permissionService;
        this.storageService = storageService;
        this.messageService = messageService;
        this.configService = configService;
        this.auditService = auditService;
        
        log.info("MvpSceneServicesImpl initialized with all services");
    }
    
    @Override
    public UserService getUserService() {
        return userService;
    }
    
    @Override
    public OrganizationService getOrganizationService() {
        return organizationService;
    }
    
    @Override
    public PermissionService getPermissionService() {
        return permissionService;
    }
    
    @Override
    public StorageService getStorageService() {
        return storageService;
    }
    
    @Override
    public MessageService getMessageService() {
        return messageService;
    }
    
    @Override
    public ConfigService getConfigService() {
        return configService;
    }
    
    @Override
    public AuditService getAuditService() {
        return auditService;
    }
}
