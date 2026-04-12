package net.ooder.skill.scenes;

import net.ooder.scene.workflow.SceneWorkflow;
import net.ooder.scene.workflow.SqlSceneWorkflowManager;
import net.ooder.scene.workflow.WorkflowExecution;
import net.ooder.scene.workflow.WorkflowStep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WorkflowIntegrationTest {

    @Autowired
    private SqlSceneWorkflowManager workflowManager;

    @Test
    public void testWorkflowManagerInjection() {
        assertNotNull(workflowManager, "SqlSceneWorkflowManager should be injected");
    }

    @Test
    public void testCreateWorkflow() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-1",
            "测试工作流",
            "这是一个测试工作流"
        );

        assertNotNull(workflow, "Workflow should not be null");
        assertNotNull(workflow.getWorkflowId(), "Workflow ID should not be null");
        assertEquals("测试工作流", workflow.getName(), "Workflow name should match");
        assertEquals("test-scene-group-1", workflow.getSceneGroupId(), "Scene group ID should match");
    }

    @Test
    public void testGetWorkflow() {
        SceneWorkflow created = workflowManager.createWorkflow(
            "test-scene-group-2",
            "查询测试工作流",
            "测试查询功能"
        );

        SceneWorkflow retrieved = workflowManager.getWorkflow(created.getWorkflowId());

        assertNotNull(retrieved, "Retrieved workflow should not be null");
        assertEquals(created.getWorkflowId(), retrieved.getWorkflowId(), "Workflow ID should match");
    }

    @Test
    public void testUpdateWorkflow() {
        SceneWorkflow created = workflowManager.createWorkflow(
            "test-scene-group-3",
            "更新测试工作流",
            "测试更新功能"
        );

        created.setName("更新后的工作流名称");
        created.setDescription("更新后的描述");

        SceneWorkflow updated = workflowManager.updateWorkflow(created);

        assertNotNull(updated, "Updated workflow should not be null");
        assertEquals("更新后的工作流名称", updated.getName(), "Name should be updated");
        assertEquals("更新后的描述", updated.getDescription(), "Description should be updated");
    }

    @Test
    public void testDeleteWorkflow() {
        SceneWorkflow created = workflowManager.createWorkflow(
            "test-scene-group-4",
            "删除测试工作流",
            "测试删除功能"
        );

        boolean deleted = workflowManager.deleteWorkflow(created.getWorkflowId());
        assertTrue(deleted, "Workflow should be deleted");

        SceneWorkflow retrieved = workflowManager.getWorkflow(created.getWorkflowId());
        assertNull(retrieved, "Deleted workflow should not be found");
    }

    @Test
    public void testAddWorkflowStep() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-5",
            "步骤测试工作流",
            "测试步骤管理"
        );

        WorkflowStep step = new WorkflowStep();
        step.setName("步骤1");
        step.setDescription("第一个步骤");
        step.setStepType("ACTION");
        step.setSequence(1);

        Map<String, Object> config = new HashMap<>();
        config.put("action", "send_notification");
        step.setConfig(config);

        WorkflowStep added = workflowManager.addWorkflowStep(workflow.getWorkflowId(), step);

        assertNotNull(added, "Added step should not be null");
        assertNotNull(added.getStepId(), "Step ID should not be null");
        assertEquals("步骤1", added.getName(), "Step name should match");
    }

    @Test
    public void testGetWorkflowSteps() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-6",
            "步骤列表测试工作流",
            "测试步骤列表查询"
        );

        WorkflowStep step1 = new WorkflowStep();
        step1.setName("步骤1");
        step1.setStepType("ACTION");
        step1.setSequence(1);
        workflowManager.addWorkflowStep(workflow.getWorkflowId(), step1);

        WorkflowStep step2 = new WorkflowStep();
        step2.setName("步骤2");
        step2.setStepType("WAIT");
        step2.setSequence(2);
        workflowManager.addWorkflowStep(workflow.getWorkflowId(), step2);

        List<WorkflowStep> steps = workflowManager.getWorkflowSteps(workflow.getWorkflowId());

        assertNotNull(steps, "Steps should not be null");
        assertEquals(2, steps.size(), "Should have 2 steps");
    }

    @Test
    public void testExecuteWorkflow() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-7",
            "执行测试工作流",
            "测试工作流执行"
        );

        WorkflowStep step = new WorkflowStep();
        step.setName("执行步骤");
        step.setStepType("ACTION");
        step.setSequence(1);
        workflowManager.addWorkflowStep(workflow.getWorkflowId(), step);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("param1", "value1");
        inputData.put("param2", "value2");

        WorkflowExecution execution = workflowManager.executeWorkflow(
            workflow.getWorkflowId(),
            inputData,
            "test-executor"
        );

        assertNotNull(execution, "Execution should not be null");
        assertNotNull(execution.getExecutionId(), "Execution ID should not be null");
        assertEquals(workflow.getWorkflowId(), execution.getWorkflowId(), "Workflow ID should match");
    }

    @Test
    public void testGetExecution() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-8",
            "执行查询测试工作流",
            "测试执行查询"
        );

        WorkflowStep step = new WorkflowStep();
        step.setName("查询测试步骤");
        step.setStepType("ACTION");
        step.setSequence(1);
        workflowManager.addWorkflowStep(workflow.getWorkflowId(), step);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("test", "data");

        WorkflowExecution created = workflowManager.executeWorkflow(
            workflow.getWorkflowId(),
            inputData,
            "test-executor"
        );

        WorkflowExecution retrieved = workflowManager.getExecution(created.getExecutionId());

        assertNotNull(retrieved, "Retrieved execution should not be null");
        assertEquals(created.getExecutionId(), retrieved.getExecutionId(), "Execution ID should match");
    }

    @Test
    public void testSetTrigger() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-9",
            "触发器测试工作流",
            "测试触发器管理"
        );

        Map<String, Object> triggerConfig = new HashMap<>();
        triggerConfig.put("type", "CRON");
        triggerConfig.put("expression", "0 0 9 * * ?");

        boolean result = workflowManager.setTrigger(workflow.getWorkflowId(), triggerConfig);
        assertTrue(result, "Trigger should be set successfully");

        SceneWorkflow updated = workflowManager.getWorkflow(workflow.getWorkflowId());
        assertTrue(updated.isTriggerEnabled(), "Trigger should be enabled");
    }

    @Test
    public void testEnableDisableTrigger() {
        SceneWorkflow workflow = workflowManager.createWorkflow(
            "test-scene-group-10",
            "触发器开关测试工作流",
            "测试触发器开关"
        );

        boolean enabled = workflowManager.enableTrigger(workflow.getWorkflowId());
        assertTrue(enabled, "Trigger should be enabled");

        SceneWorkflow afterEnable = workflowManager.getWorkflow(workflow.getWorkflowId());
        assertTrue(afterEnable.isTriggerEnabled(), "Trigger should be enabled");

        boolean disabled = workflowManager.disableTrigger(workflow.getWorkflowId());
        assertTrue(disabled, "Trigger should be disabled");

        SceneWorkflow afterDisable = workflowManager.getWorkflow(workflow.getWorkflowId());
        assertFalse(afterDisable.isTriggerEnabled(), "Trigger should be disabled");
    }
}
