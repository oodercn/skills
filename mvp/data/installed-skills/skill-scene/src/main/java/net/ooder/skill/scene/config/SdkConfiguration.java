package net.ooder.skill.scene.config;

import net.ooder.sdk.OoderSdk;
import net.ooder.sdk.OoderSdkBuilder;
import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.sdk.discovery.git.GiteeDiscoverer;
import net.ooder.sdk.discovery.git.GitHubDiscoverer;
import net.ooder.sdk.discovery.git.GitDiscoveryConfig;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.core.impl.SkillPackageManagerImpl;
import net.ooder.skills.core.impl.SkillRegistryImpl;
import net.ooder.skills.core.installer.SkillInstallerImpl;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import net.ooder.skills.api.SkillDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class SdkConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SdkConfiguration.class);

    @Value("${ooder.sdk.enabled:true}")
    private boolean sdkEnabled;

    @Value("${ooder.sdk.node-id:skill-scene}")
    private String nodeId;

    @Value("${ooder.skills.path:./skills}")
    private String skillRootPath;

    @Value("${ooder.gitee.token:}")
    private String giteeToken;

    @Value("${ooder.gitee.owner:ooderCN}")
    private String giteeOwner;

    @Value("${ooder.gitee.skills-repo:skills}")
    private String giteeSkillsRepo;

    @Value("${ooder.github.token:}")
    private String githubToken;

    @Value("${ooder.github.owner:oodercn}")
    private String githubOwner;

    @Value("${ooder.github.skills-repo:skills}")
    private String githubSkillsRepo;

    @Value("${ooder.llm.provider:mock}")
    private String llmProvider;

    @Value("${ooder.llm.model:default}")
    private String llmModel;

    @Value("${ooder.llm.baidu.api-key:}")
    private String baiduApiKey;

    @Value("${ooder.llm.baidu.secret-key:}")
    private String baiduSecretKey;

    private OoderSdk sdk;

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public OoderSdk ooderSDK() {
        log.info("[ooderSDK] Initializing OoderSDK with agentId: {}", nodeId);

        try {
            sdk = OoderSdkBuilder.create()
                .sdkId(nodeId)
                .autoDiscoverDrivers(true)
                .autoStartScenes(true)
                .property("skillRootPath", skillRootPath)
                .property("llm.provider", llmProvider)
                .property("llm.model", llmModel)
                .property("llm.baidu.apiKey", baiduApiKey)
                .property("llm.baidu.secretKey", baiduSecretKey)
                .build();

            log.info("[ooderSDK] OoderSDK initialized successfully");
            return sdk;
        } catch (Exception e) {
            log.error("[ooderSDK] Failed to initialize OoderSDK: {}", e.getMessage(), e);
            return null;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillPackageManager skillPackageManager() {
        log.info("[skillPackageManager] Creating SkillPackageManagerImpl with path: {}", skillRootPath);
        SkillPackageManagerImpl manager = new SkillPackageManagerImpl();
        manager.setSkillRootPath(skillRootPath);
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillInstaller skillInstaller() {
        log.info("[skillInstaller] Creating SkillInstallerImpl");
        return new SkillInstallerImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillRegistry skillRegistry() {
        log.info("[skillRegistry] Creating SkillRegistryImpl");
        return new SkillRegistryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillDiscoverer skillDiscoverer() {
        log.info("[skillDiscoverer] Creating LocalDiscoverer with path: {}", skillRootPath);
        LocalDiscoverer discoverer = new LocalDiscoverer();
        discoverer.setSkillsDirectory(skillRootPath);
        return discoverer;
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillService skillService(SkillPackageManager packageManager) {
        log.info("[skillService] Creating SkillService");
        return new SkillService(packageManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public CapabilityRegistry capabilityRegistry() {
        log.info("[capabilityRegistry] Creating CapabilityRegistry");
        return new CapabilityRegistry();
    }

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public GitHubDiscoverer gitHubDiscoverer() {
        log.info("[gitHubDiscoverer] Creating GitHubDiscoverer bean...");
        try {
            GitDiscoveryConfig config = GitDiscoveryConfig.forGitHub(githubToken, githubOwner, githubSkillsRepo);
            GitHubDiscoverer discoverer = new GitHubDiscoverer(config);
            log.info("[gitHubDiscoverer] GitHubDiscoverer bean created successfully with owner: {}", githubOwner);
            return discoverer;
        } catch (Exception e) {
            log.error("[gitHubDiscoverer] Failed to create GitHubDiscoverer: {}", e.getMessage(), e);
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public GiteeDiscoverer giteeDiscoverer() {
        log.info("[giteeDiscoverer] Creating GiteeDiscoverer bean...");
        try {
            GitDiscoveryConfig config = GitDiscoveryConfig.forGitee(giteeToken, giteeOwner, giteeSkillsRepo);
            GiteeDiscoverer discoverer = new GiteeDiscoverer(config);
            log.info("[giteeDiscoverer] GiteeDiscoverer bean created successfully");
            return discoverer;
        } catch (Exception e) {
            log.error("[giteeDiscoverer] Failed to create GiteeDiscoverer: {}", e.getMessage(), e);
            return null;
        }
    }

    @PreDestroy
    public void shutdown() {
        if (sdk != null) {
            log.info("[shutdown] Shutting down OoderSDK");
            try {
                sdk.shutdown();
            } catch (Exception e) {
                log.warn("[shutdown] Failed to shutdown OoderSDK: {}", e.getMessage());
            }
        }
    }

    public boolean isSdkAvailable() {
        return sdk != null && sdk.isInitialized();
    }
}
