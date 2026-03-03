package net.ooder.skill.scene.capability.install;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InstallServiceImpl implements InstallService {

    private static final Logger log = LoggerFactory.getLogger(InstallServiceImpl.class);

    private Map<String, InstallConfig> installs = new ConcurrentHashMap<String, InstallConfig>();
    private Map<String, InstallProgress> progressMap = new ConcurrentHashMap<String, InstallProgress>();

    @Override
    public InstallConfig createInstall(CreateInstallRequest request) {
        log.info("[createInstall] Creating install for capability: {}", request.getCapabilityId());
        
        String installId = "install-" + System.currentTimeMillis();
        InstallConfig config = new InstallConfig();
        config.setInstallId(installId);
        config.setCapabilityId(request.getCapabilityId());
        config.setDriverCondition(request.getDriverCondition());
        config.setOptionalCapabilities(request.getOptionalCapabilities());
        config.setConfig(request.getConfig());
        config.setPushType(request.getPushType());
        config.setStatus(InstallConfig.InstallStatus.PENDING);
        config.setCreateTime(System.currentTimeMillis());
        
        if (request.getParticipants() != null) {
            InstallConfig.Participants participants = new InstallConfig.Participants();
            if (request.getParticipants().getLeader() != null) {
                participants.setLeader(request.getParticipants().getLeader());
            }
            if (request.getParticipants().getCollaborators() != null) {
                participants.setCollaborators(request.getParticipants().getCollaborators());
            }
            config.setParticipants(participants);
        }
        
        installs.put(installId, config);
        
        InstallProgress progress = new InstallProgress();
        progress.setInstallId(installId);
        progress.setProgress(0);
        progress.setStatus(InstallConfig.InstallStatus.PENDING);
        progressMap.put(installId, progress);
        
        return config;
    }

    @Override
    public InstallConfig getInstall(String installId) {
        return installs.get(installId);
    }

    @Override
    public InstallConfig updateDriverCondition(String installId, String driverCondition) {
        log.info("[updateDriverCondition] Updating driver condition for: {}", installId);
        
        InstallConfig config = installs.get(installId);
        if (config != null) {
            config.setDriverCondition(driverCondition);
        }
        return config;
    }

    @Override
    public InstallConfig addParticipant(String installId, AddParticipantRequest request) {
        log.info("[addParticipant] Adding participant to: {}", installId);
        
        InstallConfig config = installs.get(installId);
        if (config != null && request.getParticipant() != null) {
            InstallConfig.Participants participants = config.getParticipants();
            if (participants == null) {
                participants = new InstallConfig.Participants();
            }
            List<InstallConfig.Participant> collaborators = participants.getCollaborators();
            collaborators.add(request.getParticipant());
            participants.setCollaborators(collaborators);
            config.setParticipants(participants);
        }
        return config;
    }

    @Override
    public InstallConfig removeParticipant(String installId, String userId) {
        log.info("[removeParticipant] Removing participant from: {}", installId);
        
        InstallConfig config = installs.get(installId);
        if (config != null && config.getParticipants() != null) {
            InstallConfig.Participants participants = config.getParticipants();
            List<InstallConfig.Participant> collaborators = participants.getCollaborators();
            collaborators.removeIf(p -> p.getUserId().equals(userId));
            participants.setCollaborators(collaborators);
        }
        return config;
    }

    @Override
    public InstallConfig updateOptionalCapabilities(String installId, List<String> capabilities) {
        log.info("[updateOptionalCapabilities] Updating capabilities for: {}", installId);
        
        InstallConfig config = installs.get(installId);
        if (config != null) {
            config.setOptionalCapabilities(capabilities);
        }
        return config;
    }

    @Override
    public CompletableFuture<InstallConfig> executeInstall(String installId) {
        log.info("[executeInstall] Executing install: {}", installId);
        
        return CompletableFuture.supplyAsync(() -> {
            InstallConfig config = installs.get(installId);
            InstallProgress progress = progressMap.get(installId);
            
            if (config == null) {
                return null;
            }
            
            config.setStatus(InstallConfig.InstallStatus.INSTALLING);
            if (progress != null) {
                progress.setStatus(InstallConfig.InstallStatus.INSTALLING);
                progress.setCurrentAction("正在安装依赖...");
                progress.setProgress(20);
            }
            
            try {
                Thread.sleep(1000);
                
                List<InstallConfig.DependencyInfo> deps = new ArrayList<InstallConfig.DependencyInfo>();
                InstallConfig.DependencyInfo dep1 = new InstallConfig.DependencyInfo();
                dep1.setCapabilityId("kb-management");
                dep1.setName("知识库管理");
                dep1.setStatus(InstallConfig.DependencyInfo.DependencyStatus.INSTALLED);
                deps.add(dep1);
                
                InstallConfig.DependencyInfo dep2 = new InstallConfig.DependencyInfo();
                dep2.setCapabilityId("kb-search");
                dep2.setName("知识检索");
                dep2.setStatus(InstallConfig.DependencyInfo.DependencyStatus.INSTALLED);
                deps.add(dep2);
                
                config.setDependencies(deps);
                config.setStatus(InstallConfig.InstallStatus.INSTALLED);
                
                if (progress != null) {
                    progress.setStatus(InstallConfig.InstallStatus.INSTALLED);
                    progress.setProgress(100);
                    progress.setCurrentAction("安装完成");
                    progress.setDependencies(deps);
                    progress.setInstalledCapabilities(Arrays.asList("kb-management", "kb-search"));
                    progress.setMessage("安装成功");
                }
            } catch (InterruptedException e) {
                config.setStatus(InstallConfig.InstallStatus.FAILED);
                if (progress != null) {
                    progress.setStatus(InstallConfig.InstallStatus.FAILED);
                    progress.setMessage("安装失败: " + e.getMessage());
                }
            }
            
            return config;
        });
    }

    @Override
    public InstallProgress getInstallProgress(String installId) {
        return progressMap.get(installId);
    }

    @Override
    public InstallConfig pushToParticipants(String installId, PushRequest request) {
        log.info("[pushToParticipants] Pushing install {} to participants", installId);
        
        InstallConfig config = installs.get(installId);
        if (config != null) {
            config.setPushed(true);
            config.setPushTime(System.currentTimeMillis());
        }
        return config;
    }

    @Override
    public List<InstallConfig> listMyInstalls(String userId) {
        log.info("[listMyInstalls] Listing installs for user: {}", userId);
        
        List<InstallConfig> result = new ArrayList<InstallConfig>();
        for (InstallConfig config : installs.values()) {
            InstallConfig.Participants participants = config.getParticipants();
            if (participants != null) {
                if (participants.getLeader() != null && participants.getLeader().getUserId().equals(userId)) {
                    result.add(config);
                } else {
                    for (InstallConfig.Participant p : participants.getCollaborators()) {
                        if (p.getUserId().equals(userId)) {
                            result.add(config);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<InstallConfig> listPendingActivations(String userId) {
        log.info("[listPendingActivations] Listing pending activations for user: {}", userId);
        
        List<InstallConfig> result = new ArrayList<InstallConfig>();
        for (InstallConfig config : installs.values()) {
            if (config.getStatus() == InstallConfig.InstallStatus.PENDING) {
                result.add(config);
            }
        }
        return result;
    }
}
