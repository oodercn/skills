/**
 * 枚举映射测试文件
 * 测试EnumMapper类的功能
 * 
 * 创建日期: 2026-04-07
 */

// 测试用例
const testCases = {
    ProcessDefAccess: {
        frontend: ['Public', 'Private', 'Block'],
        backend: ['INDEPENDENT', 'SUBPROCESS', 'BLOCK']
    },
    ProcessDefVersionStatus: {
        frontend: ['UNDER_REVISION', 'RELEASED', 'UNDER_TEST'],
        backend: ['DRAFT', 'PUBLISHED', 'TESTING']
    },
    ActivityDefJoin: {
        frontend: ['JOIN_AND', 'JOIN_XOR', 'DEFAULT'],
        backend: ['AND', 'XOR', 'DEFAULT']
    },
    ActivityDefSplit: {
        frontend: ['SPLIT_AND', 'SPLIT_XOR', 'DEFAULT'],
        backend: ['AND', 'XOR', 'DEFAULT']
    },
    MarkEnum: {
        frontend: ['ProcessInst', 'ActivityInst', 'Person', 'ActivityInstPerson'],
        backend: ['GLOBAL', 'ACTIVITY', 'PERSON', 'ACTIVITY_PERSON']
    },
    LockEnum: {
        frontend: ['Msg', 'Lock', 'Person', 'Last', 'NO'],
        backend: ['MSG', 'LOCK', 'PERSON', 'LAST', 'NO_LOCK']
    },
    ActivityDefPosition: {
        frontend: ['POSITION_NORMAL', 'POSITION_START', 'POSITION_END', 'VIRTUAL_LAST_DEF'],
        backend: ['NORMAL', 'START', 'END', 'VIRTUAL_LAST_DEF']
    },
    ActivityDefImpl: {
        frontend: ['IMPL_NO', 'IMPL_TOOL', 'IMPL_SUBFLOW', 'IMPL_OUTFLOW', 'IMPL_DEVICE', 'IMPL_EVENT', 'IMPL_SERVICE', 'IMPL_AGENT', 'IMPL_ROUTE', 'IMPL_BLOCK'],
        backend: ['NO', 'TOOL', 'SUBFLOW', 'OUTFLOW', 'DEVICE', 'EVENT', 'SERVICE', 'AGENT', 'ROUTE', 'BLOCK']
    },
    ActivityType: {
        frontend: ['TASK', 'SERVICE', 'SCRIPT', 'START', 'END', 'XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY', 'SUBPROCESS', 'LLM_TASK', 'COORDINATOR', 'AGENT_TASK', 'AGENT_TOOL'],
        backend: ['TASK', 'SERVICE', 'SCRIPT', 'START', 'END', 'XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY', 'SUBPROCESS', 'LLM_TASK', 'COORDINATOR', 'AGENT_TASK', 'AGENT_TOOL']
    },
    ActivityCategory: {
        frontend: ['HUMAN', 'AGENT', 'SCENE'],
        backend: ['HUMAN', 'AGENT', 'SCENE']
    },
    ActivityDefSpecialSendScope: {
        frontend: ['ALL', 'SAME_DEPT', 'SPECIFIED'],
        backend: ['ALL', 'SAME_DEPT', 'SPECIFIED']
    },
    ActivityDefRouteBackMethod: {
        frontend: ['PREVIOUS', 'SPECIFIED', 'START'],
        backend: ['PREVIOUS', 'SPECIFIED', 'START']
    },
    ActivityDefPerformtype: {
        frontend: ['SINGLE', 'MULTIPLE', 'JOINTSIGN', 'SENDER'],
        backend: ['SINGLE', 'MULTIPLE', 'JOINTSIGN', 'SENDER']
    },
    ActivityDefPerformSequence: {
        frontend: ['SEQUENTIAL', 'PARALLEL'],
        backend: ['SEQUENTIAL', 'PARALLEL']
    },
    ActivityDefDeadLineOperation: {
        frontend: ['NONE', 'AUTO_COMPLETE', 'AUTO_ABORT', 'NOTIFY'],
        backend: ['NONE', 'AUTO_COMPLETE', 'AUTO_ABORT', 'NOTIFY']
    },
    DurationUnit: {
        frontend: ['MINUTES', 'HOURS', 'DAYS'],
        backend: ['MINUTES', 'HOURS', 'DAYS']
    },
    RightGroupEnums: {
        frontend: ['PERFORMER', 'SPONSOR', 'READER', 'HISTORYPERFORMER', 'HISSPONSOR', 'HISTORYREADER', 'NORIGHT', 'NULL'],
        backend: ['PERFORMER', 'SPONSOR', 'READER', 'HISTORYPERFORMER', 'HISSPONSOR', 'HISTORYREADER', 'NORIGHT', 'NULL']
    },
    AgentGroupEnums: {
        frontend: ['PERFORMER', 'SPONSOR', 'MONITOR', 'COORDINATOR', 'HISTORYPERFORMER', 'HISSPONSOR', 'HISTORYMONITOR', 'NORIGHT', 'NULL'],
        backend: ['PERFORMER', 'SPONSOR', 'MONITOR', 'COORDINATOR', 'HISTORYPERFORMER', 'HISSPONSOR', 'HISTORYMONITOR', 'NORIGHT', 'NULL']
    },
    AgentType: {
        frontend: ['LLM', 'TASK', 'EVENT', 'HYBRID', 'COORDINATOR', 'TOOL'],
        backend: ['LLM', 'TASK', 'EVENT', 'HYBRID', 'COORDINATOR', 'TOOL']
    },
    AgentPerformStatus: {
        frontend: ['WAITING', 'CURRENT', 'FINISH', 'ERROR', 'TIMEOUT', 'DELETE'],
        backend: ['WAITING', 'CURRENT', 'FINISH', 'ERROR', 'TIMEOUT', 'DELETE']
    }
};

// 测试函数
function runTests() {
    console.log('========== 枚举映射测试开始 ==========\n');
    
    let passCount = 0;
    let failCount = 0;
    
    for (const [enumType, testData] of Object.entries(testCases)) {
        console.log(`测试枚举类型: ${enumType}`);
        
        // 测试前端到后端的映射
        console.log('  测试前端 -> 后端映射:');
        for (let i = 0; i < testData.frontend.length; i++) {
            const frontendValue = testData.frontend[i];
            const expectedBackend = testData.backend[i];
            const actualBackend = EnumMapper.toBackend(enumType, frontendValue);
            
            if (actualBackend === expectedBackend) {
                console.log(`    ✓ ${frontendValue} -> ${actualBackend}`);
                passCount++;
            } else {
                console.log(`    ✗ ${frontendValue} -> ${actualBackend} (期望: ${expectedBackend})`);
                failCount++;
            }
        }
        
        // 测试后端到前端的映射
        console.log('  测试后端 -> 前端映射:');
        for (let i = 0; i < testData.backend.length; i++) {
            const backendValue = testData.backend[i];
            const expectedFrontend = testData.frontend[i];
            const actualFrontend = EnumMapper.toFrontend(enumType, backendValue);
            
            if (actualFrontend === expectedFrontend) {
                console.log(`    ✓ ${backendValue} -> ${actualFrontend}`);
                passCount++;
            } else {
                console.log(`    ✗ ${backendValue} -> ${actualFrontend} (期望: ${expectedFrontend})`);
                failCount++;
            }
        }
        
        console.log('');
    }
    
    // 测试批量转换
    console.log('测试批量转换:');
    
    const testData = {
        accessLevel: 'Public',
        publicationStatus: 'UNDER_REVISION',
        mark: 'ProcessInst',
        lock: 'Lock'
    };
    
    console.log('  原始数据:', testData);
    
    const backendData = EnumMapper.convertToBackend(testData, ProcessDefEnumFields);
    console.log('  转换后端:', backendData);
    
    const frontendData = EnumMapper.convertToFrontend(backendData, ProcessDefEnumFields);
    console.log('  转换前端:', frontendData);
    
    // 验证批量转换
    let batchPass = true;
    for (const [key, value] of Object.entries(testData)) {
        if (frontendData[key] !== value) {
            console.log(`    ✗ 批量转换失败: ${key} 期望 ${value}, 实际 ${frontendData[key]}`);
            batchPass = false;
            failCount++;
        }
    }
    
    if (batchPass) {
        console.log('    ✓ 批量转换测试通过');
        passCount++;
    }
    
    // 测试未知枚举类型
    console.log('\n测试未知枚举类型:');
    const unknownResult = EnumMapper.toBackend('UnknownType', 'SomeValue');
    if (unknownResult === 'SomeValue') {
        console.log('  ✓ 未知枚举类型返回原值');
        passCount++;
    } else {
        console.log('  ✗ 未知枚举类型处理错误');
        failCount++;
    }
    
    // 测试未知枚举值
    console.log('\n测试未知枚举值:');
    const unknownValue = EnumMapper.toBackend('ProcessDefAccess', 'UnknownValue');
    if (unknownValue === 'UnknownValue') {
        console.log('  ✓ 未知枚举值返回原值');
        passCount++;
    } else {
        console.log('  ✗ 未知枚举值处理错误');
        failCount++;
    }
    
    // 输出测试结果
    console.log('\n========== 测试结果 ==========');
    console.log(`通过: ${passCount}`);
    console.log(`失败: ${failCount}`);
    console.log(`总计: ${passCount + failCount}`);
    console.log(`通过率: ${((passCount / (passCount + failCount)) * 100).toFixed(2)}%`);
    console.log('==============================\n');
    
    return failCount === 0;
}

// 运行测试
if (typeof window !== 'undefined') {
    // 浏览器环境
    window.addEventListener('DOMContentLoaded', () => {
        if (typeof EnumMapper !== 'undefined') {
            runTests();
        } else {
            console.error('EnumMapper 未定义，请确保已加载 EnumMapping.js');
        }
    });
} else if (typeof module !== 'undefined' && module.exports) {
    // Node.js 环境
    const { EnumMapper, ProcessDefEnumFields } = require('./EnumMapping.js');
    module.exports = { runTests };
}

// 使用说明
console.log(`
枚举映射测试使用说明:
========================

1. 浏览器环境:
   - 确保已加载 EnumMapping.js
   - 打开浏览器控制台
   - 调用 runTests() 函数

2. Node.js 环境:
   - const { runTests } = require('./enum-mapping.test.js');
   - runTests();

3. 测试内容:
   - 前端到后端的枚举值映射
   - 后端到前端的枚举值映射
   - 批量转换功能
   - 异常情况处理

4. 预期结果:
   - 所有测试用例应该通过
   - 通过率应为 100%
`);
