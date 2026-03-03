# 场景与能力可视化功能三闭环分析报告

## 文档信息

| 属性 | 值 |
|------|-----|
| **报告版本** | 1.0 |
| **生成日期** | 2026-03-02 |
| **分析范围** | skill-scene 可视化功能、LLM功能 |
| **分析维度** | 发现闭环、执行闭环、配置闭环 |

---

## 一、三闭环定义

### 1.1 发现闭环

```
用户选择模板/技能 → 依赖解析 → 安装执行 → 状态反馈 → 用户确认
        ↑                                              ↓
        ←←←←←←←←←←←← 失败回滚/重试 ←←←←←←←←←←←←←←←←←←←←←←
```

**关键节点**：
- 模板/技能发现
- 依赖图构建
- 拓扑排序安装
- 安装进度反馈
- 失败回滚机制

### 1.2 执行闭环

```
能力调用请求 → 路由解析 → Provider执行 → 结果返回 → 状态更新
      ↑                                              ↓
      ←←←←←←←←←←←← 故障转移/降级 ←←←←←←←←←←←←←←←←←←←←←
```

**关键节点**：
- 能力路由
- Provider选择
- 执行监控
- 结果处理
- 故障转移

### 1.3 配置闭环

```
配置变更请求 → 参数校验 → 配置应用 → 状态同步 → UI更新
      ↑                                          ↓
      ←←←←←←←←←←← 配置回滚 ←←←←←←←←←←←←←←←←←←←←
```

**关键节点**：
- 配置输入
- 参数验证
- 配置持久化
- 状态同步
- UI反馈

---

## 二、现有实现分析

### 2.1 发现闭环实现分析

| 组件 | 实现文件 | 完成度 | 问题 |
|------|----------|--------|------|
| 模板发现 | SceneTemplateLoader.java | ✅ 100% | - |
| 模板列表UI | template-management.html/js | ✅ 90% | 缺少模板预览 |
| 依赖解析 | SceneDependencyResolverImpl (SDK) | ✅ 100% | - |
| 安装执行 | SceneTemplateService.deployTemplate | ✅ 85% | 缺少回滚机制 |
| 安装进度UI | install.js | ⚠️ 60% | 缺少实时进度条 |
| 失败反馈 | SceneTemplateService | ⚠️ 50% | 缺少详细错误信息 |

**发现闭环完成度：81%**

### 2.2 执行闭环实现分析

| 组件 | 实现文件 | 完成度 | 问题 |
|------|----------|--------|------|
| 能力路由 | CapabilityBindingService | ✅ 90% | - |
| Provider选择 | SelectorController | ✅ 80% | 缺少负载均衡 |
| 执行监控 | execution.js | ⚠️ 50% | 缺少实时监控 |
| 结果处理 | ApiClient | ✅ 90% | - |
| 故障转移 | CapabilityServiceImpl | ⚠️ 40% | 缺少自动降级 |
| LLM执行 | LlmController | ✅ 95% | 完整实现 |

**执行闭环完成度：74%**

### 2.3 配置闭环实现分析

| 组件 | 实现文件 | 完成度 | 问题 |
|------|----------|--------|------|
| 场景配置 | scene-detail.js | ✅ 85% | 缺少配置校验 |
| 能力绑定 | capability-binding.js | ✅ 90% | - |
| 参数验证 | 后端DTO校验 | ✅ 80% | - |
| 状态同步 | 各页面JS | ⚠️ 60% | 缺少WebSocket推送 |
| UI反馈 | 通用组件 | ✅ 85% | - |
| 配置回滚 | - | ❌ 0% | 未实现 |

**配置闭环完成度：67%**

---

## 三、三闭环缺口详细分析

### 3.1 发现闭环缺口

#### 缺口1：安装进度实时反馈

**现状**：安装过程缺少实时进度展示

**问题代码** (SceneTemplateService.java):
```java
for (String skillId : installOrder) {
    // 安装过程中没有进度回调
    skillPackageManager.installWithDependencies(skillId, mode).get();
}
```

**建议修改**：
```java
// 添加进度回调接口
public interface InstallProgressCallback {
    void onSkillStart(String skillId, int current, int total);
    void onSkillComplete(String skillId, boolean success, String message);
    void onDependencyStart(String depId);
    void onDependencyComplete(String depId, boolean success);
}

// 在installWithDependencies中调用回调
for (String skillId : installOrder) {
    if (callback != null) {
        callback.onSkillStart(skillId, current++, installOrder.size());
    }
    // ... 安装逻辑
}
```

#### 缺口2：安装失败回滚

**现状**：安装失败后不会回滚已安装的依赖

**建议新增**：
```java
public void rollbackInstall(List<String> installedSkills) {
    Collections.reverse(installedSkills);
    for (String skillId : installedSkills) {
        try {
            skillPackageManager.uninstall(skillId).get();
            log.info("[rollback] Uninstalled: {}", skillId);
        } catch (Exception e) {
            log.error("[rollback] Failed to uninstall: {}", skillId, e);
        }
    }
}
```

#### 缺口3：模板预览功能

**现状**：template-detail.html 缺少依赖树可视化

**建议新增**：
```javascript
// template-detail.js 添加依赖树渲染
renderDependencyTree: function(skills) {
    var container = document.getElementById('dependencyTree');
    var html = '<div class="dep-tree">';
    
    skills.forEach(function(skill) {
        html += '<div class="dep-node" data-skill="' + skill.id + '">';
        html += '<i class="ri-checkbox-circle-line"></i> ';
        html += skill.id;
        if (skill.dependencies) {
            html += '<div class="dep-children">';
            skill.dependencies.forEach(function(dep) {
                html += '<div class="dep-child">└─ ' + dep + '</div>';
            });
            html += '</div>';
        }
        html += '</div>';
    });
    
    html += '</div>';
    container.innerHTML = html;
}
```

### 3.2 执行闭环缺口

#### 缺口1：实时执行监控

**现状**：execution.js 缺少实时状态更新

**建议修改**：
```javascript
// 添加WebSocket连接
var ExecutionMonitor = {
    ws: null,
    
    connect: function(executionId) {
        var wsUrl = 'ws://' + window.location.host + '/ws/execution/' + executionId;
        this.ws = new WebSocket(wsUrl);
        
        this.ws.onmessage = function(event) {
            var data = JSON.parse(event.data);
            ExecutionMonitor.handleUpdate(data);
        };
    },
    
    handleUpdate: function(data) {
        if (data.type === 'status') {
            this.updateStatus(data.executionId, data.status);
        } else if (data.type === 'progress') {
            this.updateProgress(data.executionId, data.progress);
        } else if (data.type === 'result') {
            this.showResult(data.executionId, data.result);
        }
    }
};
```

#### 缺口2：故障自动降级

**现状**：能力执行失败后没有自动降级策略

**建议新增** (后端):
```java
// CapabilityExecutionService.java
public Object executeWithFallback(CapabilityBinding binding, Object input) {
    try {
        return executePrimary(binding, input);
    } catch (Exception e) {
        log.warn("Primary execution failed, trying fallback: {}", e.getMessage());
        
        // 策略1: 尝试备用Provider
        if (binding.getFallbackProvider() != null) {
            return executeFallback(binding, input);
        }
        
        // 策略2: 返回缓存结果
        if (binding.isCacheEnabled()) {
            return getCachedResult(binding);
        }
        
        // 策略3: 返回默认值
        return getDefaultResult(binding);
    }
}
```

#### 缺口3：LLM上下文感知不足

**现状**：LlmAssistant.setContext() 只能设置简单上下文

**建议增强**：
```javascript
// llm-assistant.js 增强上下文收集
collectPageContext: function() {
    var context = {
        page: window.location.pathname,
        title: document.title,
        url: window.location.href,
        timestamp: Date.now()
    };
    
    // 收集场景数据
    if (typeof sceneData !== 'undefined' && sceneData) {
        context.scene = {
            id: sceneData.sceneId,
            name: sceneData.name,
            type: sceneData.type,
            capabilities: sceneData.capabilities
        };
    }
    
    // 收集能力绑定数据
    if (typeof bindings !== 'undefined' && bindings) {
        context.bindings = bindings.map(function(b) {
            return {
                capabilityId: b.capabilityId,
                capabilityName: b.capabilityName,
                status: b.status
            };
        });
    }
    
    return context;
}
```

### 3.3 配置闭环缺口

#### 缺口1：配置变更实时同步

**现状**：配置变更后其他页面不会自动更新

**建议新增**：
```javascript
// config-sync.js 配置同步服务
var ConfigSync = {
    subscribers: {},
    
    subscribe: function(configKey, callback) {
        if (!this.subscribers[configKey]) {
            this.subscribers[configKey] = [];
        }
        this.subscribers[configKey].push(callback);
    },
    
    publish: function(configKey, newValue) {
        if (this.subscribers[configKey]) {
            this.subscribers[configKey].forEach(function(cb) {
                cb(newValue);
            });
        }
    },
    
    // WebSocket监听配置变更
    listen: function() {
        var ws = new WebSocket('ws://' + window.location.host + '/ws/config');
        ws.onmessage = function(event) {
            var data = JSON.parse(event.data);
            ConfigSync.publish(data.key, data.value);
        };
    }
};
```

#### 缺口2：配置校验与预览

**现状**：配置保存前没有预览和校验

**建议新增**：
```javascript
// scene-detail.js 添加配置预览
previewConfig: function() {
    var newConfig = this.collectConfigFromForm();
    var currentConfig = sceneData.config || {};
    
    var diff = this.computeDiff(currentConfig, newConfig);
    
    var previewHtml = '<div class="config-preview">' +
        '<h4>配置变更预览</h4>' +
        '<div class="diff-view">';
    
    diff.forEach(function(change) {
        var changeClass = change.type === 'add' ? 'diff-add' : 
                          change.type === 'remove' ? 'diff-remove' : 'diff-modify';
        previewHtml += '<div class="' + changeClass + '">' +
            '<span class="key">' + change.key + '</span>' +
            '<span class="old">' + change.oldValue + '</span>' +
            '<i class="ri-arrow-right-line"></i>' +
            '<span class="new">' + change.newValue + '</span>' +
            '</div>';
    });
    
    previewHtml += '</div></div>';
    
    this.showModal(previewHtml, function() {
        this.saveConfig(newConfig);
    });
}
```

#### 缺口3：配置版本管理

**现状**：配置变更没有版本记录

**建议新增**：
```java
// ConfigVersionService.java
@Service
public class ConfigVersionService {
    
    public void saveVersion(String sceneId, Map<String, Object> config, String operator) {
        ConfigVersion version = new ConfigVersion();
        version.setSceneId(sceneId);
        version.setConfig(config);
        version.setOperator(operator);
        version.setCreateTime(System.currentTimeMillis());
        version.setVersion(generateVersion());
        
        configVersionRepository.save(version);
    }
    
    public Map<String, Object> rollback(String sceneId, String version) {
        ConfigVersion configVersion = configVersionRepository
            .findBySceneIdAndVersion(sceneId, version);
        
        if (configVersion != null) {
            sceneService.updateConfig(sceneId, configVersion.getConfig());
            return configVersion.getConfig();
        }
        
        throw new RuntimeException("Version not found: " + version);
    }
}
```

---

## 四、三闭环完善建议

### 4.1 发现闭环完善清单

| 优先级 | 任务 | 预估工作量 |
|--------|------|------------|
| P0 | 实现安装进度实时反馈 | 2天 |
| P0 | 实现安装失败回滚机制 | 1天 |
| P1 | 添加模板预览依赖树 | 1天 |
| P1 | 增强错误信息展示 | 0.5天 |
| P2 | 添加安装日志详情 | 0.5天 |

### 4.2 执行闭环完善清单

| 优先级 | 任务 | 预估工作量 |
|--------|------|------------|
| P0 | 实现WebSocket执行监控 | 2天 |
| P0 | 实现故障自动降级 | 1天 |
| P1 | 增强LLM上下文感知 | 1天 |
| P1 | 添加执行历史记录 | 1天 |
| P2 | 实现Provider负载均衡 | 2天 |

### 4.3 配置闭环完善清单

| 优先级 | 任务 | 预估工作量 |
|--------|------|------------|
| P0 | 实现配置变更同步 | 2天 |
| P0 | 实现配置版本管理 | 1天 |
| P1 | 添加配置预览功能 | 1天 |
| P1 | 增强配置校验 | 0.5天 |
| P2 | 实现配置回滚UI | 1天 |

---

## 五、总体评估

### 5.1 三闭环完成度

| 闭环 | 完成度 | 评级 |
|------|--------|------|
| 发现闭环 | 81% | B+ |
| 执行闭环 | 74% | B |
| 配置闭环 | 67% | C+ |
| **综合** | **74%** | **B** |

### 5.2 关键差距

1. **实时性不足**：缺少WebSocket推送机制
2. **回滚机制缺失**：安装和配置变更无法回滚
3. **监控不完整**：执行过程缺少实时监控
4. **上下文感知弱**：LLM助手上下文收集不完整

### 5.3 建议优先级

```
第一阶段 (P0):
├── 安装进度实时反馈
├── 安装失败回滚
├── WebSocket执行监控
├── 故障自动降级
└── 配置变更同步

第二阶段 (P1):
├── 模板预览依赖树
├── LLM上下文感知增强
├── 配置版本管理
└── 配置预览功能

第三阶段 (P2):
├── 安装日志详情
├── Provider负载均衡
└── 配置回滚UI
```

---

**报告生成者**: Ooder 开发团队  
**报告日期**: 2026-03-02
