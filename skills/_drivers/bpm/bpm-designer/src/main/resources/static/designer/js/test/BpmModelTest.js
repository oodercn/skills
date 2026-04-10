/**
 * BPM 模型测试
 * 验证 ActivityDefNew 和 ProcessDefNew 的读写闭环
 */

class BpmModelTest {
    constructor() {
        this.results = [];
        this.passed = 0;
        this.failed = 0;
    }

    /**
     * 运行所有测试
     */
    runAllTests() {
        console.log('========================================');
        console.log('BPM 模型读写闭环测试');
        console.log('========================================');
        
        // ActivityDef 测试
        this.testActivityDefBasic();
        this.testActivityDefRightGroup();
        this.testActivityDefFormGroup();
        this.testActivityDefServiceGroup();
        this.testActivityDefWorkflowGroup();
        this.testActivityDefValidation();
        this.testActivityDefClone();
        
        // ProcessDef 测试
        this.testProcessDefBasic();
        this.testProcessDefStartNode();
        this.testProcessDefEndNodes();
        this.testProcessDefListeners();
        this.testProcessDefRightGroups();
        this.testProcessDefFullRoundTrip();
        this.testProcessDefValidation();
        this.testProcessDefClone();
        
        // 打印结果
        this.printResults();
        
        return {
            total: this.passed + this.failed,
            passed: this.passed,
            failed: this.failed,
            success: this.failed === 0
        };
    }

    /**
     * 测试 ActivityDef 基本属性
     */
    testActivityDefBasic() {
        this.test('ActivityDef - 基本属性', () => {
            const activity = new ActivityDefNew({
                activityDefId: 'act_test_001',
                name: '测试活动',
                description: '这是一个测试活动',
                position: 'NORMAL',
                activityType: 'TASK',
                positionCoord: { x: 100, y: 200 },
                participantId: 'Participant_Test',
                implementation: 'No',
                join: 'XOR',
                split: 'AND'
            });
            
            // 验证属性
            this.assertEquals(activity.activityDefId, 'act_test_001', 'activityDefId');
            this.assertEquals(activity.name, '测试活动', 'name');
            this.assertEquals(activity.position, 'NORMAL', 'position');
            this.assertEquals(activity.positionCoord.x, 100, 'positionCoord.x');
            this.assertEquals(activity.positionCoord.y, 200, 'positionCoord.y');
            this.assertEquals(activity.participantId, 'Participant_Test', 'participantId');
            
            // 验证 JSON 输出
            const json = activity.toJSON();
            this.assertEquals(json.activityDefId, 'act_test_001', 'JSON activityDefId');
            this.assertEquals(json.positionCoord.x, 100, 'JSON positionCoord.x');
            
            // 验证从 JSON 重建
            const restored = ActivityDefNew.fromJSON(json);
            this.assertEquals(restored.name, '测试活动', 'restored name');
            this.assertEquals(restored.positionCoord.x, 100, 'restored positionCoord.x');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef RIGHT 属性组
     */
    testActivityDefRightGroup() {
        this.test('ActivityDef - RIGHT属性组', () => {
            const activity = new ActivityDefNew({
                name: '权限测试活动',
                RIGHT: {
                    performType: 'JOINTSIGN',
                    performSequence: 'MEANWHILE',
                    canInsteadSign: 'YES',
                    canTakeBack: 'NO',
                    performerSelectedId: 'user_001',
                    movePerformerTo: 'rg_002',
                    surrogateId: 'user_proxy_001',
                    surrogateName: '代理人'
                }
            });
            
            // 验证 RIGHT 属性组
            this.assertEquals(activity.RIGHT.performType, 'JOINTSIGN', 'RIGHT.performType');
            this.assertEquals(activity.RIGHT.canInsteadSign, 'YES', 'RIGHT.canInsteadSign');
            this.assertEquals(activity.getRightProperty('performType'), 'JOINTSIGN', 'getRightProperty');
            
            // 测试设置属性
            activity.setRightProperty('canReSend', 'YES');
            this.assertEquals(activity.RIGHT.canReSend, 'YES', 'setRightProperty');
            
            // 验证 JSON 输出包含 RIGHT
            const json = activity.toJSON();
            this.assertExists(json.RIGHT, 'JSON RIGHT exists');
            this.assertEquals(json.RIGHT.performType, 'JOINTSIGN', 'JSON RIGHT.performType');
            
            // 验证重建
            const restored = ActivityDefNew.fromJSON(json);
            this.assertEquals(restored.RIGHT.performType, 'JOINTSIGN', 'restored RIGHT.performType');
            this.assertEquals(restored.RIGHT.canReSend, 'YES', 'restored RIGHT.canReSend');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef FORM 属性组
     */
    testActivityDefFormGroup() {
        this.test('ActivityDef - FORM属性组', () => {
            const activity = new ActivityDefNew({
                name: '表单测试活动',
                FORM: {
                    formId: 'form_leave_001',
                    formName: '请假申请表',
                    formType: 'CUSTOM',
                    formUrl: '/forms/leave/form.html'
                }
            });
            
            this.assertEquals(activity.FORM.formId, 'form_leave_001', 'FORM.formId');
            this.assertEquals(activity.FORM.formType, 'CUSTOM', 'FORM.formType');
            this.assertEquals(activity.getFormProperty('formName'), '请假申请表', 'getFormProperty');
            
            // 测试修改
            activity.setFormProperty('formType', 'EXTERNAL');
            this.assertEquals(activity.FORM.formType, 'EXTERNAL', 'setFormProperty');
            
            // 验证 JSON
            const json = activity.toJSON();
            this.assertEquals(json.FORM.formId, 'form_leave_001', 'JSON FORM.formId');
            
            // 验证重建
            const restored = ActivityDefNew.fromJSON(json);
            this.assertEquals(restored.FORM.formType, 'EXTERNAL', 'restored FORM.formType');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef SERVICE 属性组
     */
    testActivityDefServiceGroup() {
        this.test('ActivityDef - SERVICE属性组', () => {
            const activity = new ActivityDefNew({
                name: '服务测试活动',
                implementation: 'Service',
                SERVICE: {
                    httpMethod: 'POST',
                    httpUrl: 'https://api.example.com/test',
                    httpRequestType: 'JSON',
                    httpResponseType: 'JSON',
                    httpServiceParams: '{"key": "value"}'
                }
            });
            
            this.assertEquals(activity.SERVICE.httpMethod, 'POST', 'SERVICE.httpMethod');
            this.assertEquals(activity.SERVICE.httpRequestType, 'JSON', 'SERVICE.httpRequestType');
            
            // 验证 JSON
            const json = activity.toJSON();
            this.assertEquals(json.SERVICE.httpMethod, 'POST', 'JSON SERVICE.httpMethod');
            
            // 验证重建
            const restored = ActivityDefNew.fromJSON(json);
            this.assertEquals(restored.SERVICE.httpUrl, 'https://api.example.com/test', 'restored SERVICE.httpUrl');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef WORKFLOW 属性组
     */
    testActivityDefWorkflowGroup() {
        this.test('ActivityDef - WORKFLOW属性组', () => {
            const activity = new ActivityDefNew({
                name: '工作流测试活动',
                WORKFLOW: {
                    deadLineOperation: 'NOTIFY',
                    specialScope: 'DEPARTMENT'
                }
            });
            
            this.assertEquals(activity.WORKFLOW.deadLineOperation, 'NOTIFY', 'WORKFLOW.deadLineOperation');
            this.assertEquals(activity.getWorkflowProperty('specialScope'), 'DEPARTMENT', 'getWorkflowProperty');
            
            // 验证 JSON
            const json = activity.toJSON();
            this.assertEquals(json.WORKFLOW.deadLineOperation, 'NOTIFY', 'JSON WORKFLOW.deadLineOperation');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef 验证
     */
    testActivityDefValidation() {
        this.test('ActivityDef - 数据验证', () => {
            // 有效数据
            const validActivity = new ActivityDefNew({
                name: '有效活动',
                RIGHT: { performType: 'SINGLE' }
            });
            this.assertTrue(validActivity.isValid(), 'valid activity should pass');
            
            // 无效数据 - 错误的 performType
            const invalidActivity = new ActivityDefNew({
                name: '无效活动',
                RIGHT: { performType: 'INVALID_TYPE' }
            });
            const errors = invalidActivity.validate();
            this.assertTrue(errors.length > 0, 'invalid activity should have errors');
            this.assertTrue(errors.some(e => e.includes('performType')), 'should report performType error');
            
            return true;
        });
    }

    /**
     * 测试 ActivityDef 克隆
     */
    testActivityDefClone() {
        this.test('ActivityDef - 克隆', () => {
            const original = new ActivityDefNew({
                name: '原始活动',
                RIGHT: { performType: 'JOINTSIGN' },
                FORM: { formId: 'form_001' }
            });
            
            const cloned = original.clone();
            
            // 验证克隆对象
            this.assertNotEquals(cloned.activityDefId, original.activityDefId, 'cloned ID should be different');
            this.assertTrue(cloned.name.includes('复制'), 'cloned name should include 复制');
            this.assertEquals(cloned.RIGHT.performType, 'JOINTSIGN', 'cloned RIGHT should be preserved');
            this.assertEquals(cloned.FORM.formId, 'form_001', 'cloned FORM should be preserved');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 基本属性
     */
    testProcessDefBasic() {
        this.test('ProcessDef - 基本属性', () => {
            const process = new ProcessDefNew({
                processDefId: 'proc_test_001',
                name: '测试流程',
                description: '这是一个测试流程',
                classification: '办公流程',
                version: 1,
                state: 'DRAFT',
                creatorName: '张三',
                limit: 24,
                durationUnit: 'H'
            });
            
            this.assertEquals(process.processDefId, 'proc_test_001', 'processDefId');
            this.assertEquals(process.name, '测试流程', 'name');
            this.assertEquals(process.version, 1, 'version');
            this.assertEquals(process.limit, 24, 'limit');
            
            // 验证 JSON
            const json = process.toJSON();
            this.assertEquals(json.processDefId, 'proc_test_001', 'JSON processDefId');
            this.assertEquals(json.creatorName, '张三', 'JSON creatorName');
            
            // 验证重建
            const restored = ProcessDefNew.fromJSON(json);
            this.assertEquals(restored.name, '测试流程', 'restored name');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 开始节点
     */
    testProcessDefStartNode() {
        this.test('ProcessDef - 开始节点', () => {
            const process = new ProcessDefNew({
                name: '开始节点测试',
                startNode: {
                    participantId: 'Participant_Start',
                    firstActivityId: 'act_start',
                    positionCoord: { x: 50, y: 200 },
                    routing: 'NO_ROUTING'
                }
            });
            
            this.assertExists(process.startNode, 'startNode exists');
            this.assertEquals(process.startNode.participantId, 'Participant_Start', 'startNode.participantId');
            this.assertEquals(process.startNode.positionCoord.x, 50, 'startNode.positionCoord.x');
            
            // 验证 JSON
            const json = process.toJSON();
            this.assertExists(json.startNode, 'JSON startNode exists');
            this.assertEquals(json.startNode.firstActivityId, 'act_start', 'JSON startNode.firstActivityId');
            
            // 验证重建
            const restored = ProcessDefNew.fromJSON(json);
            this.assertEquals(restored.startNode.positionCoord.x, 50, 'restored startNode.positionCoord.x');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 结束节点
     */
    testProcessDefEndNodes() {
        this.test('ProcessDef - 结束节点', () => {
            const process = new ProcessDefNew({
                name: '结束节点测试',
                endNodes: [
                    {
                        participantId: 'Participant_End1',
                        lastActivityId: 'act_end1',
                        positionCoord: { x: 800, y: 100 },
                        routing: 'NO_ROUTING'
                    },
                    {
                        participantId: 'Participant_End2',
                        lastActivityId: 'act_end2',
                        positionCoord: { x: 800, y: 300 },
                        routing: 'NO_ROUTING'
                    }
                ]
            });
            
            this.assertEquals(process.endNodes.length, 2, 'endNodes count');
            this.assertEquals(process.endNodes[0].participantId, 'Participant_End1', 'endNodes[0].participantId');
            this.assertEquals(process.endNodes[1].positionCoord.y, 300, 'endNodes[1].positionCoord.y');
            
            // 验证 JSON
            const json = process.toJSON();
            this.assertEquals(json.endNodes.length, 2, 'JSON endNodes count');
            
            // 验证重建
            const restored = ProcessDefNew.fromJSON(json);
            this.assertEquals(restored.endNodes[1].lastActivityId, 'act_end2', 'restored endNodes[1].lastActivityId');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 监听器
     */
    testProcessDefListeners() {
        this.test('ProcessDef - 监听器', () => {
            const process = new ProcessDefNew({
                name: '监听器测试'
            });
            
            // 添加监听器
            process.addListener({
                name: '流程启动监听',
                event: 'PROCESS_START',
                realizeClass: 'com.test.StartListener'
            });
            
            process.addListener({
                name: '流程结束监听',
                event: 'PROCESS_END',
                realizeClass: 'com.test.EndListener'
            });
            
            this.assertEquals(process.listeners.length, 2, 'listeners count');
            this.assertEquals(process.listeners[0].event, 'PROCESS_START', 'listeners[0].event');
            this.assertTrue(process.listeners[0].id.startsWith('listener_'), 'listener id format');
            
            // 验证 JSON
            const json = process.toJSON();
            this.assertEquals(json.listeners.length, 2, 'JSON listeners count');
            
            // 验证重建
            const restored = ProcessDefNew.fromJSON(json);
            this.assertEquals(restored.listeners[1].name, '流程结束监听', 'restored listeners[1].name');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 权限组
     */
    testProcessDefRightGroups() {
        this.test('ProcessDef - 权限组', () => {
            const process = new ProcessDefNew({
                name: '权限组测试'
            });
            
            // 添加权限组
            process.addRightGroup({
                name: '默认组',
                code: 'DEFAULT',
                defaultGroup: true
            });
            
            process.addRightGroup({
                name: '审批组',
                code: 'APPROVAL',
                defaultGroup: false
            });
            
            this.assertEquals(process.rightGroups.length, 2, 'rightGroups count');
            this.assertEquals(process.rightGroups[0].order, 1, 'rightGroups[0].order');
            this.assertTrue(process.rightGroups[0].defaultGroup, 'rightGroups[0].defaultGroup');
            
            // 测试获取默认组
            const defaultGroup = process.getDefaultRightGroup();
            this.assertEquals(defaultGroup.code, 'DEFAULT', 'default group code');
            
            // 验证 JSON
            const json = process.toJSON();
            this.assertEquals(json.rightGroups.length, 2, 'JSON rightGroups count');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 完整闭环
     */
    testProcessDefFullRoundTrip() {
        this.test('ProcessDef - 完整读写闭环', () => {
            // 创建完整流程
            const process = new ProcessDefNew({
                processDefId: 'proc_full_test',
                name: '完整测试流程',
                classification: '业务流程',
                version: 1,
                creatorName: '管理员',
                limit: 48,
                durationUnit: 'H',
                startNode: {
                    participantId: 'Participant_Start',
                    firstActivityId: 'act_start',
                    positionCoord: { x: 50, y: 200 },
                    routing: 'NO_ROUTING'
                },
                endNodes: [
                    {
                        participantId: 'Participant_End',
                        lastActivityId: 'act_end',
                        positionCoord: { x: 800, y: 200 },
                        routing: 'NO_ROUTING'
                    }
                ],
                listeners: [
                    { id: 'listener_001', name: '启动监听', event: 'PROCESS_START', realizeClass: 'com.test.Start' }
                ],
                rightGroups: [
                    { id: 'rg_001', name: '默认组', code: 'DEFAULT', order: 1, defaultGroup: true }
                ]
            });
            
            // 添加活动
            const startActivity = ActivityDefNew.createStart(50, 200, '开始');
            startActivity.activityDefId = 'act_start';
            process.addActivity(startActivity);
            
            const taskActivity = new ActivityDefNew({
                activityDefId: 'act_task',
                name: '审批任务',
                position: 'NORMAL',
                positionCoord: { x: 300, y: 200 },
                RIGHT: {
                    performType: 'JOINTSIGN',
                    canInsteadSign: 'YES'
                },
                FORM: {
                    formId: 'form_001',
                    formType: 'CUSTOM'
                }
            });
            process.addActivity(taskActivity);
            
            const endActivity = ActivityDefNew.createEnd(800, 200, '结束');
            endActivity.activityDefId = 'act_end';
            process.addActivity(endActivity);
            
            // 添加路由
            process.addRoute({
                fromActivityDefId: 'act_start',
                toActivityDefId: 'act_task',
                routeDirection: 'FORWARD'
            });
            process.addRoute({
                fromActivityDefId: 'act_task',
                toActivityDefId: 'act_end',
                routeDirection: 'FORWARD'
            });
            
            // 验证数据完整性
            this.assertEquals(process.activities.length, 3, 'activities count');
            this.assertEquals(process.routes.length, 2, 'routes count');
            this.assertExists(process.startNode, 'startNode exists');
            this.assertEquals(process.endNodes.length, 1, 'endNodes count');
            
            // 转换为 JSON
            const json = process.toJSON();
            
            // 验证 JSON 结构
            this.assertEquals(json.processDefId, 'proc_full_test', 'JSON processDefId');
            this.assertEquals(json.activities.length, 3, 'JSON activities count');
            this.assertExists(json.startNode, 'JSON startNode exists');
            this.assertEquals(json.listeners.length, 1, 'JSON listeners count');
            
            // 验证活动属性组
            const taskJson = json.activities.find(a => a.activityDefId === 'act_task');
            this.assertExists(taskJson.RIGHT, 'JSON task RIGHT exists');
            this.assertEquals(taskJson.RIGHT.performType, 'JOINTSIGN', 'JSON RIGHT.performType');
            this.assertExists(taskJson.FORM, 'JSON task FORM exists');
            
            // 从 JSON 重建
            const restored = ProcessDefNew.fromJSON(json);
            
            // 验证重建后的数据完整性
            this.assertEquals(restored.name, '完整测试流程', 'restored name');
            this.assertEquals(restored.activities.length, 3, 'restored activities count');
            this.assertEquals(restored.startNode.positionCoord.x, 50, 'restored startNode.positionCoord.x');
            this.assertEquals(restored.endNodes[0].positionCoord.x, 800, 'restored endNodes[0].positionCoord.x');
            
            // 验证活动属性组
            const restoredTask = restored.getActivity('act_task');
            this.assertExists(restoredTask.RIGHT, 'restored task RIGHT exists');
            this.assertEquals(restoredTask.RIGHT.performType, 'JOINTSIGN', 'restored RIGHT.performType');
            this.assertEquals(restoredTask.FORM.formId, 'form_001', 'restored FORM.formId');
            
            // 验证验证通过
            this.assertTrue(restored.isValid(), 'restored process should be valid');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 验证
     */
    testProcessDefValidation() {
        this.test('ProcessDef - 数据验证', () => {
            // 有效流程
            const validProcess = new ProcessDefNew({
                name: '有效流程',
                startNode: { firstActivityId: 'act_start' },
                endNodes: [{ lastActivityId: 'act_end' }],
                activities: [
                    { activityDefId: 'act_start', name: '开始', position: 'START' },
                    { activityDefId: 'act_end', name: '结束', position: 'END' }
                ]
            });
            this.assertTrue(validProcess.isValid(), 'valid process should pass');
            
            // 无效流程 - 缺少名称
            const invalidProcess = new ProcessDefNew({
                name: '',
                activities: []
            });
            const errors = invalidProcess.validate();
            this.assertTrue(errors.length > 0, 'invalid process should have errors');
            this.assertTrue(errors.some(e => e.includes('名称')), 'should report name error');
            
            return true;
        });
    }

    /**
     * 测试 ProcessDef 克隆
     */
    testProcessDefClone() {
        this.test('ProcessDef - 克隆', () => {
            const original = new ProcessDefNew({
                name: '原始流程',
                startNode: { firstActivityId: 'act_start' },
                endNodes: [{ lastActivityId: 'act_end' }],
                activities: [
                    { activityDefId: 'act_start', name: '开始', position: 'START' },
                    { activityDefId: 'act_end', name: '结束', position: 'END' }
                ],
                listeners: [{ name: '监听', event: 'PROCESS_START', realizeClass: 'com.test' }],
                rightGroups: [{ name: '组', code: 'TEST', order: 1 }]
            });
            
            const cloned = original.clone();
            
            // 验证克隆对象
            this.assertNotEquals(cloned.processDefId, original.processDefId, 'cloned ID should be different');
            this.assertTrue(cloned.name.includes('复制'), 'cloned name should include 复制');
            this.assertEquals(cloned.version, 1, 'cloned version should be 1');
            this.assertEquals(cloned.activities.length, 2, 'cloned activities count');
            this.assertEquals(cloned.listeners.length, 1, 'cloned listeners count');
            
            // 验证活动ID已重新生成
            const originalActivityId = original.activities[0].activityDefId;
            const clonedActivityId = cloned.activities[0].activityDefId;
            this.assertNotEquals(clonedActivityId, originalActivityId, 'cloned activity ID should be different');
            
            return true;
        });
    }

    // ==================== 测试工具方法 ====================

    test(name, fn) {
        try {
            const result = fn();
            if (result) {
                this.passed++;
                this.results.push({ name, status: 'PASSED' });
                console.log(`✓ ${name}`);
            } else {
                this.failed++;
                this.results.push({ name, status: 'FAILED', error: 'Test returned false' });
                console.error(`✗ ${name}`);
            }
        } catch (error) {
            this.failed++;
            this.results.push({ name, status: 'FAILED', error: error.message });
            console.error(`✗ ${name}: ${error.message}`);
        }
    }

    assertEquals(actual, expected, message) {
        if (actual !== expected) {
            throw new Error(`Assertion failed: ${message}. Expected ${expected}, got ${actual}`);
        }
    }

    assertTrue(value, message) {
        if (value !== true) {
            throw new Error(`Assertion failed: ${message}. Expected true, got ${value}`);
        }
    }

    assertExists(value, message) {
        if (value === undefined || value === null) {
            throw new Error(`Assertion failed: ${message}. Value is null or undefined`);
        }
    }

    assertNotEquals(actual, expected, message) {
        if (actual === expected) {
            throw new Error(`Assertion failed: ${message}. Expected different values, both are ${actual}`);
        }
    }

    printResults() {
        console.log('========================================');
        console.log(`测试结果: ${this.passed} 通过, ${this.failed} 失败`);
        console.log('========================================');
        
        if (this.failed > 0) {
            console.log('\n失败的测试:');
            this.results
                .filter(r => r.status === 'FAILED')
                .forEach(r => console.log(`  - ${r.name}: ${r.error}`));
        }
    }
}

// 导出测试类
window.BpmModelTest = BpmModelTest;

// 自动运行测试（如果在浏览器环境中）
if (typeof window !== 'undefined' && window.document) {
    // 等待页面加载完成
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            console.log('BPM Model Test loaded. Run BpmModelTest to execute tests.');
        });
    } else {
        console.log('BPM Model Test loaded. Run BpmModelTest to execute tests.');
    }
}
