package net.ooder.skill.market.service.impl;

import net.ooder.skill.market.dto.*;
import net.ooder.skill.market.service.SkillMarketService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillMarketServiceImpl implements SkillMarketService {

    private final Map<String, SkillPackage> skillCache = new HashMap<>();
    private final Set<String> installedSkills = new HashSet<>();

    public SkillMarketServiceImpl() {
        initMockData();
    }

    private void initMockData() {
        addMockSkill("skill-org-feishu", "Feishu Organization Service", 
            "飞书组织数据集成服务", "org", "0.7.3");
        addMockSkill("skill-org-dingding", "DingTalk Organization Service", 
            "钉钉组织数据集成服务", "org", "0.7.3");
        addMockSkill("skill-org-wecom", "WeCom Organization Service", 
            "企业微信组织数据集成服务", "org", "0.7.3");
        addMockSkill("skill-vfs-local", "Local VFS Service", 
            "本地文件系统存储服务", "vfs", "0.7.3");
        addMockSkill("skill-vfs-minio", "MinIO VFS Service", 
            "MinIO存储服务", "vfs", "0.7.3");
        addMockSkill("skill-network", "Network Management Skill", 
            "网络管理技能", "sys", "0.7.3");
        addMockSkill("skill-security", "Security Management Skill", 
            "安全管理技能", "sys", "0.7.3");
        addMockSkill("skill-mqtt", "MQTT Service Skill", 
            "MQTT服务技能", "msg", "0.7.3");
        addMockSkill("skill-a2ui", "A2UI Skill", 
            "A2UI图转代码技能", "ui", "0.7.3");
    }

    private void addMockSkill(String skillId, String name, String description, 
                              String category, String version) {
        SkillPackage pkg = new SkillPackage();
        pkg.setSkillId(skillId);
        pkg.setName(name);
        pkg.setDescription(description);
        pkg.setCategory(category);
        pkg.setVersion(version);
        pkg.setAuthor("Ooder Team");
        pkg.setTags(Arrays.asList(category, "skill"));
        pkg.setDownloadCount((long) (Math.random() * 1000));
        pkg.setInstallCount((long) (Math.random() * 500));
        pkg.setUpdateTime(System.currentTimeMillis());
        
        AuthStatus authStatus = new AuthStatus();
        authStatus.setStatus("verified");
        authStatus.setLevel("standard");
        authStatus.setVerifyTime(System.currentTimeMillis());
        authStatus.setIssuer("Ooder");
        pkg.setAuthStatus(authStatus);
        
        skillCache.put(skillId, pkg);
    }

    @Override
    public List<SkillPackage> listSkills() {
        return new ArrayList<>(skillCache.values());
    }

    @Override
    public PageResult<SkillPackage> searchSkills(SearchRequest request) {
        List<SkillPackage> filtered = skillCache.values().stream()
            .filter(pkg -> {
                if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                    String keyword = request.getKeyword().toLowerCase();
                    return pkg.getName().toLowerCase().contains(keyword) ||
                           pkg.getDescription().toLowerCase().contains(keyword) ||
                           pkg.getSkillId().toLowerCase().contains(keyword);
                }
                return true;
            })
            .filter(pkg -> {
                if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                    return request.getCategory().equals(pkg.getCategory());
                }
                return true;
            })
            .filter(pkg -> {
                if (request.getTags() != null && !request.getTags().isEmpty()) {
                    return pkg.getTags() != null && 
                           pkg.getTags().stream().anyMatch(request.getTags()::contains);
                }
                return true;
            })
            .collect(Collectors.toList());

        int page = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());

        List<SkillPackage> pagedList = start < filtered.size() 
            ? filtered.subList(start, end) 
            : Collections.emptyList();

        return PageResult.of(pagedList, (long) filtered.size(), page, pageSize);
    }

    @Override
    public SkillPackage getSkill(String skillId) {
        return skillCache.get(skillId);
    }

    @Override
    public InstallResult installSkill(String skillId, String version) {
        SkillPackage pkg = skillCache.get(skillId);
        if (pkg == null) {
            return InstallResult.fail("Skill not found: " + skillId);
        }
        
        if (installedSkills.contains(skillId)) {
            return InstallResult.fail("Skill already installed: " + skillId);
        }
        
        installedSkills.add(skillId);
        return InstallResult.success(skillId, version != null ? version : pkg.getVersion());
    }

    @Override
    public InstallResult uninstallSkill(String skillId) {
        if (!installedSkills.contains(skillId)) {
            return InstallResult.fail("Skill not installed: " + skillId);
        }
        
        installedSkills.remove(skillId);
        return InstallResult.success(skillId, null);
    }

    @Override
    public InstallResult updateSkill(String skillId) {
        SkillPackage pkg = skillCache.get(skillId);
        if (pkg == null) {
            return InstallResult.fail("Skill not found: " + skillId);
        }
        
        if (!installedSkills.contains(skillId)) {
            return InstallResult.fail("Skill not installed: " + skillId);
        }
        
        return InstallResult.success(skillId, pkg.getVersion());
    }

    @Override
    public AuthStatus getAuthStatus(String skillId) {
        SkillPackage pkg = skillCache.get(skillId);
        return pkg != null ? pkg.getAuthStatus() : null;
    }

    private SdkConfig sdkConfig;
    private final long startTime = System.currentTimeMillis();

    private void initSdkConfig() {
        sdkConfig = new SdkConfig();
        sdkConfig.setMode("mock");
        sdkConfig.setMockDelay(300);
        sdkConfig.setVersion("0.7.3");
        sdkConfig.setEndpoint("http://localhost:8091");
        sdkConfig.setSettings(new HashMap<>());
        sdkConfig.setUpdateTime(System.currentTimeMillis());
    }

    @Override
    public SdkConfig getSdkConfig() {
        if (sdkConfig == null) {
            initSdkConfig();
        }
        return sdkConfig;
    }

    @Override
    public SdkConfig updateSdkConfig(SdkConfig config) {
        if (config == null) {
            return sdkConfig;
        }
        if (config.getMode() != null) {
            sdkConfig.setMode(config.getMode());
        }
        if (config.getMockDelay() != null) {
            sdkConfig.setMockDelay(config.getMockDelay());
        }
        if (config.getEndpoint() != null) {
            sdkConfig.setEndpoint(config.getEndpoint());
        }
        if (config.getSettings() != null) {
            sdkConfig.setSettings(config.getSettings());
        }
        sdkConfig.setUpdateTime(System.currentTimeMillis());
        return sdkConfig;
    }

    @Override
    public SdkStatus getSdkStatus() {
        SdkStatus status = new SdkStatus();
        status.setStatus("running");
        status.setMode(sdkConfig != null ? sdkConfig.getMode() : "mock");
        status.setVersion("0.7.3");
        status.setHealthy(true);
        status.setUptime(System.currentTimeMillis() - startTime);
        status.setActiveConnections(0);
        
        List<SdkStatus.SdkComponent> components = new ArrayList<>();
        
        SdkStatus.SdkComponent market = new SdkStatus.SdkComponent();
        market.setName("skill-market");
        market.setStatus("running");
        market.setVersion("0.7.3");
        components.add(market);
        
        SdkStatus.SdkComponent storage = new SdkStatus.SdkComponent();
        storage.setName("storage");
        storage.setStatus("healthy");
        storage.setVersion("1.0.0");
        components.add(storage);
        
        status.setComponents(components);
        return status;
    }

    @Override
    public boolean switchMode(String mode) {
        if (mode == null || (!mode.equals("mock") && !mode.equals("real"))) {
            return false;
        }
        if (sdkConfig == null) {
            initSdkConfig();
        }
        sdkConfig.setMode(mode);
        sdkConfig.setUpdateTime(System.currentTimeMillis());
        return true;
    }
}
