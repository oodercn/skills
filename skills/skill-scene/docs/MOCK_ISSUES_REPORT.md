# Mock问题分析报告

**分析日期**: 2026-03-12  
**分析范围**: 后端API + 前端JS  

---

## 一、后端Mock检测

### 1.1 GitDiscoveryController.java

| 方法 | Mock位置 | 问题 |
|------|----------|------|
| `discoverFromGitHub` | 第123-125行 | 当capabilities为空且mockEnabled=true时返回Mock数据 |
| `discoverFromGitee` | 第229-231行 | 当capabilities为空且mockEnabled=true时返回Mock数据 |
| `discoverFromGit` | 第254-255行 | 完全依赖Mock， mockEnabled=true时返回Mock数据 |
| `searchGitHub` | 第272-275行 | mockEnabled=false时返回空列表 |
| `searchGitee` | 第315-318行 | mockEnabled=false时返回空列表 |
| `installSkill` | 第345-354行 | **严重**: mockEnabled=true时直接返回成功，跳过真实安装 |

### 1.2 其他Controller

| Controller | 方法 | Mock位置 |
|------------|------|----------|
| `CapabilityController.java` | `getCapabilityDetail` | 第247-249行 |
| `SceneEngineIntegration.java` | `executeCapability` | 第117-118行 |

---

## 二、前端Mock检测

### 2.1 capability-stats.js
| 函数 | 行号 | 问题 |
|------|------|------|
| `loadStats` | 第23-27行 | API失败时调用renderMockStats() |
| `loadCapabilityRank` | 第101-105行 | API失败时调用renderMockCapabilityRank() |
| `loadTypeDistribution` | 第158-162行 | API失败时调用renderMockTypeDistribution() |
| `loadRecentLogs` | 第195-199行 | API失败时调用renderMockLogs() |

### 2.2 其他前端文件
| 文件 | 函数 | 问题 |
|------|------|------|
| `my-history.js` | `loadMockHistory()` | 第98行 - 直接加载Mock数据 |
| `template-detail.js` | `loadMockTemplate()` | 第142行 - 直接加载Mock数据 |
| `template-management.js` | `loadMockData()` | 第33行 - 直接加载Mock数据 |
| `my-todos.js` | `loadMockTodos()` | 第70行 - 直接加载Mock数据 |
| `install.js` | `startInstall()` | 第11-37行 - 完全模拟安装过程 |
| `login.js` | `handleLogin()` | 第21行 - 模拟登录成功 |

---

## 三、闭环检测统计

### 3.1 发现闭环 (Discovery Loop)

```
┌─────────────────────────────────────────────────────────────────┐
│                    发现流程闭环检测                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  前端: startScan()                                                │
│    ↓                                                              │
│  API: POST /api/v1/discovery/local                               │
│    ↓                                                              │
│  后端: GitDiscoveryController.discoverFromLocal()                 │
│    ↓                                                              │
│  检测点1: skillIndexLoader.getSkillsFromEntryFiles() ✅ 已实现     │
│    ↓                                                              │
│  检测点2: checkIfInstalled() ✅ 已实现                              │
│    ↓                                                              │
│  返回: List<CapabilityDTO>                                        │
│    ↓                                                              │
│  前端: renderResults() ✅ 已实现                                   │
│                                                                 │
│  状态: ✅ 完整闭环                                               │
└─────────────────────────────────────────────────────────────────┘
```

**问题**: GitHub/Gitee发现路径存在Mock回退

```
检测点:
1. skillIndexLoader.getSkillsFromIndex() - 使用索引文件
2. gitHubDiscoverer.discoverSkills() - 真实Git发现
3. giteeDiscoverer.discoverSkills() - 真实Gitee发现
4. getMockGitHubCapabilities() - Mock回退 ⚠️
5. getMockGiteeCapabilities() - Mock回退 ⚠️
```

### 3.2 安装闭环 (Install Loop)

```
┌─────────────────────────────────────────────────────────────────┐
│                    安装流程闭环检测                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  前端: executeInstall()                                           │
│    ↓                                                              │
│  收集配置: collectInstallConfig() ✅ 已实现                        │
│    ↓                                                              │
│  API: POST /api/v1/discovery/install                              │
│    ↓                                                              │
│  后端: GitDiscoveryController.installSkill()                      │
│    ↓                                                              │
│  检测点1: mockEnabled检查 ⚠️ 存在Mock回退                          │
│    ↓                                                              │
│  检测点2: skillPackageManager.install() ✅ 存在但未验证真实执行   │
│    ↓                                                              │
│  检测点3: skillService.installSkill() ✅ 存在但未验证真实执行      │
│    ↓                                                              │
│  返回: InstallResultDTO                                           │
│    ↓                                                              │
│  前端: updateProgress() ⚠️ 前端模拟进度，非真实反馈              │
│    ↓                                                              │
│  前端: renderCompleteStep() ⚠️ 菜单未真实添加                      │
│                                                                 │
│  状态: ⚠️ 部分闭环 - 存在Mock回退和前端模拟                      │
└─────────────────────────────────────────────────────────────────┘
```

**问题清单**:
1. ❌ 安装进度是前端模拟，非后端真实反馈
2. ❌ 安装完成后未调用菜单添加API
3. ⚠️ mockEnabled=true时跳过所有真实安装逻辑

### 3.3 菜单闭环 (Menu Loop)

```
┌─────────────────────────────────────────────────────────────────┐
│                    菜单添加闭环检测                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  安装完成: renderCompleteStep()                                   │
│    ↓                                                              │
│  检测点1: completeMenuPreview ⚠️ 仅静态展示                       │
│    ↓                                                              │
│  应该调用: POST /api/v1/roles/{roleId}/menus ❌ 未调用             │
│    ↓                                                              │
│  后端: RoleManagementController.addMenuToRole()                   │
│    ↓                                                              │
│  检测点2: MenuRoleConfigService.addMenuToRole() ✅ 已实现          │
│    ↓                                                              │
│  结果: 用户菜单中新增能力入口                                    │
│                                                                 │
│  状态: ❌ 闭环断裂 - 前端未调用后端API                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、Mock问题汇总

| 闭环 | 状态 | 问题数 | 严重程度 |
|------|------|--------|----------|
| 发现闭环 | ✅ 完整 | 0 | - |
| 安装闭环 | ⚠️ 部分 | 3 | 高 |
| 菜单闭环 | ❌ 断裂 | 1 | 高 |
| **总计** | - | **4** | - |

### 详细问题列表

| # | 问题 | 位置 | 影响 | 建议 |
|---|------|------|------|------|
| 1 | 安装进度前端模拟 | capability-discovery.js:861-902 | 用户无法看到真实安装状态 | 实现SSE/WebSocket推送真实进度 |
| 2 | mockEnabled跳过真实安装 | GitDiscoveryController.java:345-354 | 开发环境可能误判安装成功 | 生产环境禁用mockEnabled |
| 3 | 菜单未真实添加 | capability-discovery.js:819-824 | 用户菜单中看不到新安装的能力 | 调用addMenuToRole API |
| 4 | GitHub/Gitee Mock回退 | GitDiscoveryController.java:123-125, 229-231 | 真实Git发现失败时返回假数据 | 移除Mock或明确标注 |

---

## 五、修复优先级

### 高优先级 (必须修复)
1. **安装闭环 - mockEnabled问题**: 生产环境必须禁用mockEnabled
2. **菜单闭环断裂**: 安装完成后必须调用菜单添加API

### 中优先级 (建议修复)
3. **安装进度前端模拟**: 实现真实进度反馈
4. **GitHub/Gitee Mock回退**: 移除或明确标注Mock数据

### 低优先级 (可延后)
5. **前端Mock数据**: 各页面的Mock数据可保留作为开发测试用

---

**报告生成时间**: 2026-03-12  
**报告生成工具**: AI Assistant
