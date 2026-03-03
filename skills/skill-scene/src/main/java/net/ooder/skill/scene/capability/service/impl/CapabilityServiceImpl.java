package net.ooder.skill.scene.capability.service.impl;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityStatus;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.registry.CapabilityRegistry;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.storage.JsonStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CapabilityServiceImpl implements CapabilityService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityServiceImpl.class);
    private static final String STORAGE_KEY = "capabilities";

    private final CapabilityRegistry registry;
    private final JsonStorageService storageService;

    public CapabilityServiceImpl(JsonStorageService storageService) {
        this.storageService = storageService;
        this.registry = new CapabilityRegistry();
        loadFromStorage();
        initDefaultCapabilities();
    }

    private void loadFromStorage() {
        try {
            List<Capability> stored = storageService.loadList(STORAGE_KEY, Capability.class);
            if (stored != null) {
                for (Capability cap : stored) {
                    registry.register(cap);
                }
                log.info("Loaded {} capabilities from storage", stored.size());
            }
        } catch (Exception e) {
            log.warn("Failed to load capabilities from storage: {}", e.getMessage());
        }
    }

    private void saveToStorage() {
        try {
            storageService.saveList(STORAGE_KEY, registry.findAll());
        } catch (Exception e) {
            log.error("Failed to save capabilities to storage: {}", e.getMessage());
        }
    }

    private void initDefaultCapabilities() {
        if (registry.size() == 0) {
            registerDefaultCapabilities();
        }
    }

    private void registerDefaultCapabilities() {
        Capability dailyLogScene = new Capability();
        dailyLogScene.setCapabilityId("daily-log-scene");
        dailyLogScene.setName("日志汇报场景");
        dailyLogScene.setDescription("完整的日志汇报场景能力，包含提醒、提交、汇总、分析等闭环流程");
        dailyLogScene.setType(CapabilityType.SCENE);
        dailyLogScene.getSupportedSceneTypes().add("daily-report");
        dailyLogScene.setVersion("2.3.0");
        dailyLogScene.setIcon("ri-file-list-3-line");
        java.util.Map<String, Object> dailyLogMeta = new java.util.HashMap<>();
        dailyLogMeta.put("category", "办公协作");
        dailyLogMeta.put("provider", "ooder");
        dailyLogScene.setMetadata(dailyLogMeta);
        dailyLogScene.setDependencies(java.util.Arrays.asList("report-remind", "report-submit", "report-aggregate"));
        dailyLogScene.setOptionalCapabilities(java.util.Arrays.asList("report-analyze", "email-send"));
        register(dailyLogScene);

        Capability knowledgeQAScene = new Capability();
        knowledgeQAScene.setCapabilityId("knowledge-qa-scene");
        knowledgeQAScene.setName("知识问答场景");
        knowledgeQAScene.setDescription("基于知识库的智能问答场景，支持文档导入、索引构建、语义搜索");
        knowledgeQAScene.setType(CapabilityType.SCENE);
        knowledgeQAScene.getSupportedSceneTypes().add("knowledge-qa");
        knowledgeQAScene.setVersion("2.3.0");
        knowledgeQAScene.setIcon("ri-book-open-line");
        java.util.Map<String, Object> knowledgeMeta = new java.util.HashMap<>();
        knowledgeMeta.put("category", "知识管理");
        knowledgeMeta.put("provider", "ooder");
        knowledgeQAScene.setMetadata(knowledgeMeta);
        knowledgeQAScene.setDependencies(java.util.Arrays.asList("kb-management", "kb-search"));
        knowledgeQAScene.setOptionalCapabilities(java.util.Arrays.asList("llm-chat"));
        register(knowledgeQAScene);

        Capability llmWorkspaceScene = new Capability();
        llmWorkspaceScene.setCapabilityId("llm-workspace-scene");
        llmWorkspaceScene.setName("LLM工作空间");
        llmWorkspaceScene.setDescription("大语言模型工作空间，支持多轮对话、上下文管理、模型切换");
        llmWorkspaceScene.setType(CapabilityType.SCENE);
        llmWorkspaceScene.getSupportedSceneTypes().add("llm-workspace");
        llmWorkspaceScene.setVersion("2.3.0");
        llmWorkspaceScene.setIcon("ri-robot-line");
        java.util.Map<String, Object> llmMeta = new java.util.HashMap<>();
        llmMeta.put("category", "AI助手");
        llmMeta.put("provider", "ooder");
        llmWorkspaceScene.setMetadata(llmMeta);
        llmWorkspaceScene.setDependencies(java.util.Arrays.asList("llm-chat", "conversation-manage"));
        register(llmWorkspaceScene);

        Capability reportRemind = new Capability();
        reportRemind.setCapabilityId("report-remind");
        reportRemind.setName("日志提醒");
        reportRemind.setDescription("定时提醒员工提交日志");
        reportRemind.setType(CapabilityType.COMMUNICATION);
        reportRemind.getSupportedSceneTypes().add("daily-report");
        register(reportRemind);

        Capability reportSubmit = new Capability();
        reportSubmit.setCapabilityId("report-submit");
        reportSubmit.setName("日志提交");
        reportSubmit.setDescription("员工提交工作日志");
        reportSubmit.setType(CapabilityType.SERVICE);
        reportSubmit.getSupportedSceneTypes().add("daily-report");
        register(reportSubmit);

        Capability reportAggregate = new Capability();
        reportAggregate.setCapabilityId("report-aggregate");
        reportAggregate.setName("日志汇总");
        reportAggregate.setDescription("汇总所有员工日志");
        reportAggregate.setType(CapabilityType.SERVICE);
        reportAggregate.getSupportedSceneTypes().add("daily-report");
        register(reportAggregate);

        Capability reportAnalyze = new Capability();
        reportAnalyze.setCapabilityId("report-analyze");
        reportAnalyze.setName("日志分析");
        reportAnalyze.setDescription("AI分析日志内容");
        reportAnalyze.setType(CapabilityType.AI);
        reportAnalyze.getSupportedSceneTypes().add("daily-report");
        register(reportAnalyze);

        Capability emailSend = new Capability();
        emailSend.setCapabilityId("email-send");
        emailSend.setName("邮件发送");
        emailSend.setDescription("发送邮件通知");
        emailSend.setType(CapabilityType.COMMUNICATION);
        emailSend.getSupportedSceneTypes().add("daily-report");
        emailSend.getSupportedSceneTypes().add("notification");
        register(emailSend);

        Capability kbManagement = new Capability();
        kbManagement.setCapabilityId("kb-management");
        kbManagement.setName("知识库管理");
        kbManagement.setDescription("创建、更新、删除知识库，支持BM25搜索");
        kbManagement.setType(CapabilityType.DATA);
        kbManagement.getSupportedSceneTypes().add("knowledge");
        kbManagement.setSkillId("skill-knowledge-base");
        register(kbManagement);

        Capability kbSearch = new Capability();
        kbSearch.setCapabilityId("kb-search");
        kbSearch.setName("知识搜索");
        kbSearch.setDescription("BM25文档搜索能力");
        kbSearch.setType(CapabilityType.AI);
        kbSearch.getSupportedSceneTypes().add("knowledge");
        kbSearch.setSkillId("skill-knowledge-base");
        register(kbSearch);

        Capability userManagement = new Capability();
        userManagement.setCapabilityId("user-management");
        userManagement.setName("用户管理");
        userManagement.setDescription("管理用户和权限配置");
        userManagement.setType(CapabilityType.SECURITY);
        userManagement.getSupportedSceneTypes().add("security");
        userManagement.setSkillId("skill-security");
        register(userManagement);

        Capability permissionControl = new Capability();
        permissionControl.setCapabilityId("permission-control");
        permissionControl.setName("权限控制");
        permissionControl.setDescription("控制用户权限和角色");
        permissionControl.setType(CapabilityType.SECURITY);
        permissionControl.getSupportedSceneTypes().add("security");
        permissionControl.setSkillId("skill-security");
        register(permissionControl);

        Capability securityAudit = new Capability();
        securityAudit.setCapabilityId("security-audit");
        securityAudit.setName("安全审计");
        securityAudit.setDescription("审计安全事件");
        securityAudit.setType(CapabilityType.SECURITY);
        securityAudit.getSupportedSceneTypes().add("security");
        securityAudit.setSkillId("skill-security");
        register(securityAudit);

        Capability llmChat = new Capability();
        llmChat.setCapabilityId("llm-chat");
        llmChat.setName("LLM对话");
        llmChat.setDescription("大语言模型对话能力");
        llmChat.setType(CapabilityType.AI);
        llmChat.getSupportedSceneTypes().add("llm");
        llmChat.setSkillId("skill-llm-conversation");
        register(llmChat);

        Capability conversationManage = new Capability();
        conversationManage.setCapabilityId("conversation-manage");
        conversationManage.setName("会话管理");
        conversationManage.setDescription("多轮对话上下文管理");
        conversationManage.setType(CapabilityType.SERVICE);
        conversationManage.getSupportedSceneTypes().add("llm");
        conversationManage.setSkillId("skill-llm-conversation");
        register(conversationManage);

        log.info("Registered {} default capabilities", registry.size());
    }

    @Override
    public Capability register(Capability capability) {
        if (capability == null || capability.getCapabilityId() == null) {
            throw new IllegalArgumentException("Capability and capabilityId must not be null");
        }

        capability.setUpdateTime(System.currentTimeMillis());
        if (capability.getCreateTime() == 0) {
            capability.setCreateTime(capability.getUpdateTime());
        }
        if (capability.getStatus() == null) {
            capability.setStatus(CapabilityStatus.REGISTERED);
        }

        registry.register(capability);
        saveToStorage();
        log.info("Registered capability: {}", capability.getCapabilityId());
        return capability;
    }

    @Override
    public void unregister(String capabilityId) {
        registry.unregister(capabilityId);
        saveToStorage();
        log.info("Unregistered capability: {}", capabilityId);
    }

    @Override
    public Capability findById(String capabilityId) {
        return registry.findById(capabilityId);
    }

    @Override
    public List<Capability> findAll() {
        return registry.findAll();
    }

    @Override
    public List<Capability> findByType(CapabilityType type) {
        return registry.findByType(type);
    }

    @Override
    public List<Capability> findBySceneType(String sceneType) {
        return registry.findBySceneType(sceneType);
    }

    @Override
    public List<Capability> search(String query) {
        return registry.search(query);
    }

    @Override
    public Capability update(Capability capability) {
        Capability existing = registry.findById(capability.getCapabilityId());
        if (existing == null) {
            throw new IllegalArgumentException("Capability not found: " + capability.getCapabilityId());
        }

        capability.setUpdateTime(System.currentTimeMillis());
        capability.setCreateTime(existing.getCreateTime());

        registry.register(capability);
        saveToStorage();
        log.info("Updated capability: {}", capability.getCapabilityId());
        return capability;
    }

    @Override
    public void updateStatus(String capabilityId, String status) {
        Capability capability = registry.findById(capabilityId);
        if (capability != null) {
            capability.setStatus(CapabilityStatus.valueOf(status));
            capability.setUpdateTime(System.currentTimeMillis());
            saveToStorage();
            log.info("Updated capability status: {} -> {}", capabilityId, status);
        }
    }
}
