package net.ooder.skill.template.controller;

import net.ooder.skill.template.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SceneTemplateServiceImpl implements SceneTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SceneTemplateServiceImpl.class);

    @Override
    public List<SceneTemplateDTO> listTemplates() {
        return Collections.emptyList();
    }

    @Override
    public SceneTemplateDTO getTemplate(String templateId) {
        log.warn("[getTemplate] templateId={}, not implemented", templateId);
        return null;
    }

    @Override
    public DeployResultDTO deployTemplate(String templateId) {
        DeployResultDTO result = new DeployResultDTO();
        result.setSuccess(false);
        result.setMessage("Not implemented");
        return result;
    }

    @Override
    public SseEmitter deployTemplateWithProgress(String templateId) {
        SseEmitter emitter = new SseEmitter(30000L);
        try { emitter.send(SseEmitter.event().name("error").data("Not implemented")); emitter.complete(); }
        catch (Exception e) { emitter.completeWithError(e); }
        return emitter;
    }

    @Override
    public InstallResultDTO installTemplateDependencies(String templateId) {
        InstallResultDTO r = new InstallResultDTO();
        r.setSuccess(false); r.setMessage("Not implemented");
        r.setInstalledSkills(Collections.emptyList());
        return r;
    }

    @Override
    public HealthCheckResultDTO checkDependenciesHealth(String templateId) {
        HealthCheckResultDTO r = new HealthCheckResultDTO();
        r.setAllHealthy(true);
        r.setTotalCount(0); r.setHealthyCount(0);
        return r;
    }

    @Override
    public List<DependencyStatusDTO> getMissingDependencies(String templateId) {
        return Collections.emptyList();
    }

    @Override
    public AutoInstallResultDTO autoInstallDependencies(String templateId, boolean includeOptional) {
        AutoInstallResultDTO r = new AutoInstallResultDTO();
        r.setSuccess(false);
        r.setInstalledSkills(Collections.emptyList());
        r.setSkippedSkills(Collections.emptyList());
        r.setFailedSkills(Collections.emptyList());
        return r;
    }

    @Override
    public AutoInstallResultDTO installMissingRequired(String templateId) {
        AutoInstallResultDTO r = new AutoInstallResultDTO();
        r.setSuccess(false);
        r.setInstalledSkills(Collections.emptyList());
        r.setSkippedSkills(Collections.emptyList());
        r.setFailedSkills(Collections.emptyList());
        return r;
    }
}
