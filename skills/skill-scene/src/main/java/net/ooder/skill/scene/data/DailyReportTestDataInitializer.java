package net.ooder.skill.scene.data;

import net.ooder.skill.scene.dto.SceneDefinitionDTO;
import net.ooder.skill.scene.dto.scene.*;
import net.ooder.skill.scene.service.SceneService;
import net.ooder.skill.scene.service.SceneGroupService;
import net.ooder.skill.scene.service.SceneTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DailyReportTestDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DailyReportTestDataInitializer.class);

    @Autowired
    private SceneTemplateService templateService;

    @Autowired
    private SceneGroupService sceneGroupService;

    @Autowired
    private SceneService sceneService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Initializing Daily Report test data...");
        
        initDailyReportTemplate();
        initDailyReportSceneGroup();
        initTestScenes();
        
        log.info("Daily Report test data initialized successfully");
    }

    private void initDailyReportTemplate() {
        SceneTemplateDTO template = new SceneTemplateDTO();
        template.setTemplateId("tpl-daily-report");
        template.setName("日志汇报场景模板");
        template.setDescription("日志汇报场景，支持日志提交、提醒、汇总和分析。适用于团队日常日志管理，支持定时提醒、AI分析等功能。");
        template.setVersion("1.0.0");
        template.setCategory("business");
        template.setType("PRIMARY");
        template.setStatus("published");
        template.setCreateTime(System.currentTimeMillis());
        
        List<CapabilityDefDTO> capabilities = new ArrayList<>();
        
        CapabilityDefDTO remindCap = new CapabilityDefDTO();
        remindCap.setCapId("report-remind");
        remindCap.setName("日志提醒");
        remindCap.setDescription("定时提醒员工提交日志");
        remindCap.setCategory("notification");
        capabilities.add(remindCap);
        
        CapabilityDefDTO submitCap = new CapabilityDefDTO();
        submitCap.setCapId("report-submit");
        submitCap.setName("日志提交");
        submitCap.setDescription("员工提交工作日志");
        submitCap.setCategory("data-input");
        capabilities.add(submitCap);
        
        CapabilityDefDTO aggregateCap = new CapabilityDefDTO();
        aggregateCap.setCapId("report-aggregate");
        aggregateCap.setName("日志汇总");
        aggregateCap.setDescription("汇总所有员工日志");
        aggregateCap.setCategory("data-processing");
        capabilities.add(aggregateCap);
        
        CapabilityDefDTO analyzeCap = new CapabilityDefDTO();
        analyzeCap.setCapId("report-analyze");
        analyzeCap.setName("日志分析");
        analyzeCap.setDescription("AI分析日志内容");
        analyzeCap.setCategory("intelligence");
        capabilities.add(analyzeCap);
        
        CapabilityDefDTO uiCap = new CapabilityDefDTO();
        uiCap.setCapId("report-ui-form");
        uiCap.setName("日志填写表单");
        uiCap.setDescription("提供日志填写的UI界面");
        uiCap.setCategory("ui");
        capabilities.add(uiCap);
        
        template.setCapabilities(capabilities);
        
        List<RoleDefinitionDTO> roles = new ArrayList<>();
        
        RoleDefinitionDTO managerRole = new RoleDefinitionDTO();
        managerRole.setName("manager");
        managerRole.setDescription("场景管理者（领导）");
        managerRole.setRequired(true);
        managerRole.setMinCount(1);
        managerRole.setMaxCount(1);
        managerRole.setCapabilities(Arrays.asList("report-remind", "report-aggregate", "report-analyze", "report-submit"));
        roles.add(managerRole);
        
        RoleDefinitionDTO employeeRole = new RoleDefinitionDTO();
        employeeRole.setName("employee");
        employeeRole.setDescription("普通员工");
        employeeRole.setRequired(true);
        employeeRole.setMinCount(1);
        employeeRole.setMaxCount(100);
        employeeRole.setCapabilities(Arrays.asList("report-submit", "report-ui-form"));
        roles.add(employeeRole);
        
        RoleDefinitionDTO llmRole = new RoleDefinitionDTO();
        llmRole.setName("llm-assistant");
        llmRole.setDescription("LLM分析助手");
        llmRole.setRequired(false);
        llmRole.setMinCount(0);
        llmRole.setMaxCount(5);
        llmRole.setCapabilities(Arrays.asList("report-analyze", "report-remind"));
        roles.add(llmRole);
        
        RoleDefinitionDTO coordinatorRole = new RoleDefinitionDTO();
        coordinatorRole.setName("coordinator");
        coordinatorRole.setDescription("协调Agent");
        coordinatorRole.setRequired(false);
        coordinatorRole.setMinCount(0);
        coordinatorRole.setMaxCount(1);
        coordinatorRole.setCapabilities(Arrays.asList("report-remind", "report-aggregate"));
        roles.add(coordinatorRole);
        
        template.setRoles(roles);
        
        WorkflowDefinitionDTO workflow = new WorkflowDefinitionDTO();
        List<TriggerDefinitionDTO> triggers = new ArrayList<>();
        
        TriggerDefinitionDTO trigger1 = new TriggerDefinitionDTO();
        trigger1.setType("schedule");
        trigger1.setCron("0 17 * * 1-5");
        trigger1.setAction("remind-flow");
        triggers.add(trigger1);
        
        TriggerDefinitionDTO trigger2 = new TriggerDefinitionDTO();
        trigger2.setType("schedule");
        trigger2.setCron("0 18 * * 1-5");
        trigger2.setAction("aggregate-flow");
        triggers.add(trigger2);
        
        workflow.setTriggers(triggers);
        
        List<StepDefinitionDTO> steps = new ArrayList<>();
        
        StepDefinitionDTO step1 = new StepDefinitionDTO();
        step1.setId("remind");
        step1.setName("发送提醒");
        step1.setCapability("report-remind");
        step1.setExecutor("coordinator");
        steps.add(step1);
        
        StepDefinitionDTO step2 = new StepDefinitionDTO();
        step2.setId("wait-submit");
        step2.setName("等待提交");
        step2.setType("wait");
        step2.setTimeout(3600000L);
        steps.add(step2);
        
        StepDefinitionDTO step3 = new StepDefinitionDTO();
        step3.setId("aggregate");
        step3.setName("汇总日志");
        step3.setCapability("report-aggregate");
        step3.setExecutor("coordinator");
        step3.setDependsOn(Arrays.asList("wait-submit"));
        steps.add(step3);
        
        StepDefinitionDTO step4 = new StepDefinitionDTO();
        step4.setId("analyze");
        step4.setName("AI分析");
        step4.setCapability("report-analyze");
        step4.setExecutor("llm-assistant");
        step4.setDependsOn(Arrays.asList("aggregate"));
        steps.add(step4);
        
        StepDefinitionDTO step5 = new StepDefinitionDTO();
        step5.setId("notify-manager");
        step5.setName("通知领导");
        step5.setCapability("report-remind");
        step5.setExecutor("coordinator");
        step5.setDependsOn(Arrays.asList("analyze"));
        steps.add(step5);
        
        workflow.setSteps(steps);
        template.setWorkflow(workflow);
        
        templateService.create(template);
        log.info("Created daily report template: {}", template.getTemplateId());
    }

    private void initDailyReportSceneGroup() {
        SceneGroupConfigDTO config = new SceneGroupConfigDTO();
        config.setName("研发部日志汇报组");
        config.setDescription("研发部团队的日常日志汇报场景组");
        config.setCreatorId("user-manager-001");
        config.setCreatorType(ParticipantType.USER);
        config.setMinMembers(2);
        config.setMaxMembers(20);
        
        SceneGroupDTO group = sceneGroupService.create("tpl-daily-report", config);
        
        SceneParticipantDTO manager = new SceneParticipantDTO();
        manager.setParticipantId("user-manager-001");
        manager.setParticipantType(ParticipantType.USER);
        manager.setRole("manager");
        sceneGroupService.join(group.getSceneGroupId(), manager);
        
        SceneParticipantDTO employee1 = new SceneParticipantDTO();
        employee1.setParticipantId("user-employee-001");
        employee1.setParticipantType(ParticipantType.USER);
        employee1.setRole("employee");
        sceneGroupService.join(group.getSceneGroupId(), employee1);
        
        SceneParticipantDTO employee2 = new SceneParticipantDTO();
        employee2.setParticipantId("user-employee-002");
        employee2.setParticipantType(ParticipantType.USER);
        employee2.setRole("employee");
        sceneGroupService.join(group.getSceneGroupId(), employee2);
        
        SceneParticipantDTO llmAgent = new SceneParticipantDTO();
        llmAgent.setParticipantId("agent-llm-001");
        llmAgent.setParticipantType(ParticipantType.AGENT);
        llmAgent.setRole("llm-assistant");
        sceneGroupService.join(group.getSceneGroupId(), llmAgent);
        
        SceneParticipantDTO coordinatorAgent = new SceneParticipantDTO();
        coordinatorAgent.setParticipantId("agent-coordinator-001");
        coordinatorAgent.setParticipantType(ParticipantType.AGENT);
        coordinatorAgent.setRole("coordinator");
        sceneGroupService.join(group.getSceneGroupId(), coordinatorAgent);
        
        CapabilityBindingDTO binding1 = new CapabilityBindingDTO();
        binding1.setCapId("report-remind");
        binding1.setProviderType(CapabilityProviderType.AGENT);
        binding1.setProviderId("agent-coordinator-001");
        binding1.setConnectorType(ConnectorType.INTERNAL);
        binding1.setPriority(1);
        sceneGroupService.bindCapability(group.getSceneGroupId(), binding1);
        
        CapabilityBindingDTO binding2 = new CapabilityBindingDTO();
        binding2.setCapId("report-submit");
        binding2.setProviderType(CapabilityProviderType.SKILL);
        binding2.setProviderId("skill-daily-report");
        binding2.setConnectorType(ConnectorType.HTTP);
        binding2.setPriority(1);
        sceneGroupService.bindCapability(group.getSceneGroupId(), binding2);
        
        CapabilityBindingDTO binding3 = new CapabilityBindingDTO();
        binding3.setCapId("report-aggregate");
        binding3.setProviderType(CapabilityProviderType.AGENT);
        binding3.setProviderId("agent-coordinator-001");
        binding3.setConnectorType(ConnectorType.INTERNAL);
        binding3.setPriority(1);
        sceneGroupService.bindCapability(group.getSceneGroupId(), binding3);
        
        CapabilityBindingDTO binding4 = new CapabilityBindingDTO();
        binding4.setCapId("report-analyze");
        binding4.setProviderType(CapabilityProviderType.AGENT);
        binding4.setProviderId("agent-llm-001");
        binding4.setConnectorType(ConnectorType.INTERNAL);
        binding4.setPriority(1);
        sceneGroupService.bindCapability(group.getSceneGroupId(), binding4);
        
        sceneGroupService.activate(group.getSceneGroupId());
        
        log.info("Created daily report scene group: {}", group.getSceneGroupId());
    }

    private void initTestScenes() {
        SceneDefinitionDTO scene1 = new SceneDefinitionDTO();
        scene1.setSceneId("scene-data-processing");
        scene1.setName("数据处理场景");
        scene1.setDescription("用于数据采集、处理和分析的场景");
        scene1.setType("primary");
        scene1.setVersion("1.0.0");
        scene1.setCreateTime(System.currentTimeMillis());
        scene1.setActive(true);
        sceneService.create(scene1);
        
        SceneDefinitionDTO scene2 = new SceneDefinitionDTO();
        scene2.setSceneId("scene-api-integration");
        scene2.setName("API集成场景");
        scene2.setDescription("用于外部API集成的场景");
        scene2.setType("primary");
        scene2.setVersion("1.0.0");
        scene2.setCreateTime(System.currentTimeMillis());
        scene2.setActive(false);
        sceneService.create(scene2);
        
        log.info("Created test scenes");
    }
}
