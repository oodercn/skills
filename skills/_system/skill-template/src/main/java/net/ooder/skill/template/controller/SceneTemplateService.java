package net.ooder.skill.template.controller;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import net.ooder.skill.template.dto.*;

public interface SceneTemplateService {
    
    List<SceneTemplateDTO> listTemplates();
    
    SceneTemplateDTO getTemplate(String templateId);
    
    DeployResultDTO deployTemplate(String templateId);
    
    SseEmitter deployTemplateWithProgress(String templateId);
    
    InstallResultDTO installTemplateDependencies(String templateId);
    
    HealthCheckResultDTO checkDependenciesHealth(String templateId);
    
    List<DependencyStatusDTO> getMissingDependencies(String templateId);
    
    AutoInstallResultDTO autoInstallDependencies(String templateId, boolean includeOptional);
    
    AutoInstallResultDTO installMissingRequired(String templateId);
}
