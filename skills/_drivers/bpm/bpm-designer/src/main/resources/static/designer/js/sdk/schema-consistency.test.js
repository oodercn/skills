/**
 * Schema一致性测试
 * 验证ActivityPanelSchema与PanelSchema的枚举选项保持同步
 *
 * 创建日期: 2026-04-09
 */

const testCases = {
    activityType: {
        description: '活动类型选项应一致',
        getFromActivityPanel: function() {
            const schema = ActivityPanelSchema.getActivitySchema({
                implementation: 'IMPL_NO',
                activityCategory: 'HUMAN',
                activityType: 'TASK'
            });
            if (!schema || !schema.fields || !schema.fields.basic) return null;
            const field = schema.fields.basic.find(f => f.name === 'activityType');
            return field ? field.options.map(o => o.value) : null;
        },
        getFromPanelSchema: function() {
            const fields = PanelSchema._getBasicFields('IMPL_NO');
            if (!fields) return null;
            const field = fields.find(f => f.name === 'activityType');
            return field ? field.options.map(o => o.value) : null;
        }
    },
    implementation: {
        description: '实现方式选项应一致',
        getFromActivityPanel: function() {
            const schema = ActivityPanelSchema.getActivitySchema({
                implementation: 'IMPL_NO',
                activityCategory: 'HUMAN',
                activityType: 'TASK'
            });
            if (!schema || !schema.fields || !schema.fields.basic) return null;
            const field = schema.fields.basic.find(f => f.name === 'implementation');
            return field ? field.options.map(o => o.value) : null;
        },
        getFromPanelSchema: function() {
            const fields = PanelSchema._getBasicFields('IMPL_NO');
            if (!fields) return null;
            const field = fields.find(f => f.name === 'implementation');
            return field ? field.options.map(o => o.value) : null;
        }
    },
    fieldNameConsistency: {
        description: '字段命名应统一使用implementation而非impl',
        validate: function() {
            const fields = PanelSchema._getBasicFields('IMPL_NO');
            if (!fields) return { pass: false, message: 'PanelSchema._getBasicFields返回null' };
            const implField = fields.find(f => f.name === 'impl');
            if (implField) {
                return { pass: false, message: 'PanelSchema中仍使用impl字段名，应改为implementation' };
            }
            const implementationField = fields.find(f => f.name === 'implementation');
            if (!implementationField) {
                return { pass: false, message: 'PanelSchema中未找到implementation字段' };
            }
            return { pass: true, message: '字段命名已统一为implementation' };
        }
    },
    activityPropertyAccess: {
        description: 'PanelSchema应通过activity.implementation访问属性',
        validate: function() {
            const testActivity = { implementation: 'IMPL_TOOL' };
            try {
                const schema = PanelSchema.getActivitySchema(testActivity);
                if (schema === null && testActivity.implementation) {
                    return { pass: false, message: 'PanelSchema.getActivitySchema返回null，可能仍使用activity.impl访问' };
                }
                return { pass: true, message: 'PanelSchema正确使用activity.implementation访问' };
            } catch (e) {
                return { pass: false, message: '访问异常: ' + e.message };
            }
        }
    }
};

function runSchemaConsistencyTests() {
    console.log('========== Schema一致性测试开始 ==========\n');

    let passCount = 0;
    let failCount = 0;

    for (const [testName, testCase] of Object.entries(testCases)) {
        console.log(`测试: ${testCase.description}`);

        if (testCase.validate) {
            const result = testCase.validate();
            if (result.pass) {
                console.log(`  ✓ ${result.message}`);
                passCount++;
            } else {
                console.log(`  ✗ ${result.message}`);
                failCount++;
            }
        } else if (testCase.getFromActivityPanel && testCase.getFromPanelSchema) {
            const activityPanelOptions = testCase.getFromActivityPanel();
            const panelSchemaOptions = testCase.getFromPanelSchema();

            if (!activityPanelOptions) {
                console.log(`  ✗ ActivityPanelSchema中未找到${testName}选项`);
                failCount++;
                continue;
            }
            if (!panelSchemaOptions) {
                console.log(`  ✗ PanelSchema中未找到${testName}选项`);
                failCount++;
                continue;
            }

            const missingInActivityPanel = panelSchemaOptions.filter(o => !activityPanelOptions.includes(o));
            const missingInPanelSchema = activityPanelOptions.filter(o => !panelSchemaOptions.includes(o));

            if (missingInActivityPanel.length === 0 && missingInPanelSchema.length === 0) {
                console.log(`  ✓ ${testName}选项完全一致 (${activityPanelOptions.length}项)`);
                passCount++;
            } else {
                if (missingInActivityPanel.length > 0) {
                    console.log(`  ✗ ActivityPanelSchema缺少: ${missingInActivityPanel.join(', ')}`);
                    failCount++;
                }
                if (missingInPanelSchema.length > 0) {
                    console.log(`  ✗ PanelSchema缺少: ${missingInPanelSchema.join(', ')}`);
                    failCount++;
                }
            }
        }

        console.log('');
    }

    console.log('========== 测试结果 ==========');
    console.log(`通过: ${passCount}`);
    console.log(`失败: ${failCount}`);
    console.log(`总计: ${passCount + failCount}`);
    console.log(`通过率: ${((passCount / (passCount + failCount)) * 100).toFixed(2)}%`);
    console.log('==============================\n');

    return failCount === 0;
}

if (typeof window !== 'undefined') {
    window.addEventListener('DOMContentLoaded', () => {
        if (typeof ActivityPanelSchema !== 'undefined' && typeof PanelSchema !== 'undefined') {
            runSchemaConsistencyTests();
        } else {
            console.error('ActivityPanelSchema 或 PanelSchema 未定义');
        }
    });
} else if (typeof module !== 'undefined' && module.exports) {
    module.exports = { runSchemaConsistencyTests };
}
