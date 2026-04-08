# MVP 核心拆分与 Skills 模块化协作任务说明

**文档版本**: v1.0  
**创建日期**: 2026-03-12  
**项目代号**: OODER-2.3.1-MVP-SPLIT

---

## 一、项目背景

### 1.1 当前问题

| 问题 | 影响 | 优先级 |
|------|------|:------:|
| skill-scene 模块过大 (~50MB) | 启动慢、内存占用高 | P0 |
| 180+ API 端点耦合 | 维护困难、扩展性差 | P0 |
| LLM/知识库逻辑内嵌 | 无法独立升级替换 | P1 |
| 缺少最小化配置 | 无法按需安装 | P1 |

### 1.2 目标架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MVP Core (最小核心)                           │
│  ┌─────────────────────────────────────────────────────────────────┐│
│  │  skill-common    │  skill-capability  │  skill-protocol         ││
│  │  (认证/组织/配置) │  (能力管理核心)     │  (协议处理)             ││
│  └─────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│                     Skills Layer (技能层)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│  │ skill-llm-*  │  │ skill-knowledge│ │ skill-audit │              │
│  │ LLM驱动系列  │  │ 知识库系列     │  │ 审计技能    │              │
│  └──────────────┘  └──────────────┘  └──────────────┘              │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 预期收益

| 指标 | 当前 | 目标 | 改善 |
|------|------|------|------|
| MVP 核心大小 | ~50MB | ~10MB | -80% |
| 核心 API 端点 | 180+ | 40 | -78% |
| 启动时间 | ~30s | ~5s | -83% |
| 内存占用 | ~512MB | ~128MB | -75% |

---

## 二、团队分工

### 2.1 Skills 团队

**职责范围**:
- 创建 `skill-common` 模块
- 拆分 LLM 驱动系列技能
- 拆分知识库系列技能
- 拆分审计/监控等独立能力
- 定义模块间接口规范

**交付物**:
- skill-common 模块 (可运行)
- skill-llm-base 模块
- skill-llm-openai/ollama/qianwen 模块
- skill-knowledge-base 模块
- skill-audit 模块

### 2.2 MVP 团队

**职责范围**:
- 定义 MVP 最小配置规范
- 创建 Profile 配置文件 (micro/small/large/enterprise)
- 实现按需加载机制
- 编写安装/激活脚本
- 验证最小化启动

**交付物**:
- MVP 配置规范文档
- Profile 配置文件集
- 安装向导更新
- 启动验证脚本

---

## 三、Skills 团队任务清单

### 3.1 Task-SKILL-001: 创建 skill-common 模块

**优先级**: P0  
**预估工时**: 3天  
**负责人**: ________

**任务描述**:
创建 `skill-common` 模块，包含认证、组织、配置等核心 API。

**详细步骤**:

```
Step 1: 创建模块结构
skills/_system/skill-common/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/common/
    ├── api/
    │   ├── AuthApi.java
    │   ├── OrgApi.java
    │   └── ConfigApi.java
    ├── model/
    │   ├── ResultModel.java
    │   ├── UserInfo.java
    │   └── SystemConfig.java
    ├── service/
    │   ├── AuthService.java
    │   ├── OrgService.java
    │   └── ConfigService.java
    └── storage/
        └── JsonStorageService.java
```

**Step 2: 迁移 Auth API**

从 `skill-scene` 迁移以下 Controller:
- `AuthController.java` → `AuthApi.java`
- 相关 Service 和 Model

**迁移 API 列表**:
| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| POST /api/v1/auth/login | 保留 | 登录 |
| POST /api/v1/auth/logout | 保留 | 登出 |
| GET /api/v1/auth/session | 保留 | 会话 |
| GET /api/v1/auth/current-user | 保留 | 当前用户 |
| GET /api/v1/auth/roles | 保留 | 角色列表 |
| GET /api/v1/auth/menu-config | 保留 | 菜单配置 |

**Step 3: 迁移 Org API**

从 `skill-scene` 迁移以下 Controller:
- `OrgController.java` → `OrgApi.java`

**迁移 API 列表**:
| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| GET /api/v1/org/users/current | 保留 | 当前用户 |
| GET /api/v1/org/users | 保留 | 用户列表 |
| POST /api/v1/org/users | 保留 | 创建用户 |
| GET /api/v1/org/tree | 保留 | 组织树 |

**Step 4: 迁移 Config API**

从 `skill-scene` 迁移以下 Controller:
- `SystemConfigController.java` → `ConfigApi.java`
- `AddressSpaceController.java` 部分内容

**验收标准**:
- [ ] skill-common 可独立编译运行
- [ ] 所有迁移 API 功能正常
- [ ] 单元测试覆盖率 > 80%
- [ ] skill.yaml 配置完整

---

### 3.2 Task-SKILL-002: 创建 skill-capability 模块

**优先级**: P0  
**预估工时**: 2天  
**负责人**: ________

**任务描述**:
创建 `skill-capability` 模块，包含能力管理核心 API。

**详细步骤**:

```
Step 1: 创建模块结构
skills/_system/skill-capability/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/capability/
    ├── api/
    │   ├── CapabilityApi.java
    │   ├── CapabilityDiscoveryApi.java
    │   └── InstallApi.java
    ├── model/
    │   ├── Capability.java
    │   ├── CapabilityStatus.java
    │   └── InstallConfig.java
    ├── service/
    │   ├── CapabilityService.java
    │   ├── CapabilityStateService.java
    │   └── InstallService.java
    └── registry/
        └── CapabilityRegistry.java
```

**Step 2: 迁移能力核心 API**

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| GET /api/v1/capabilities | 保留 | 能力列表 |
| GET /api/v1/capabilities/{id} | 保留 | 能力详情 |
| POST /api/v1/capabilities | 保留 | 创建能力 |
| PUT /api/v1/capabilities/{id} | 保留 | 更新能力 |
| DELETE /api/v1/capabilities/{id} | 保留 | 删除能力 |

**验收标准**:
- [ ] skill-capability 可独立编译运行
- [ ] 能力 CRUD 功能正常
- [ ] 能力状态持久化正常
- [ ] skill.yaml 配置完整

---

### 3.3 Task-SKILL-003: 拆分 LLM 驱动系列

**优先级**: P1  
**预估工时**: 4天  
**负责人**: ________

**任务描述**:
将 LLM 相关逻辑从 skill-scene 拆分为独立技能模块。

**详细步骤**:

**Step 1: 创建 skill-llm-base 模块**

```
skills/_drivers/llm/skill-llm-base/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/llm/
    ├── api/
    │   └── LlmProviderApi.java
    ├── model/
    │   ├── LlmProvider.java
    │   ├── LlmModel.java
    │   └── ChatRequest.java
    ├── service/
    │   ├── LlmProviderService.java
    │   └── LlmBaseService.java
    └── config/
        └── LlmConfig.java
```

**Step 2: 创建 skill-llm-openai 模块**

```
skills/_drivers/llm/skill-llm-openai/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/llm/openai/
    ├── api/
    │   └── OpenAiApi.java
    └── service/
        └── OpenAiService.java
```

**Step 3: 创建其他 LLM 驱动**

- skill-llm-ollama
- skill-llm-qianwen
- skill-llm-deepseek
- skill-llm-volcengine

**验收标准**:
- [ ] skill-llm-base 可独立运行
- [ ] 各 LLM 驱动可独立安装
- [ ] Provider 切换功能正常
- [ ] 地址空间配置正确 (0x30-0x37)

---

### 3.4 Task-SKILL-004: 拆分知识库系列

**优先级**: P1  
**预估工时**: 3天  
**负责人**: ________

**任务描述**:
将知识库相关逻辑从 skill-scene 拆分为独立技能模块。

**详细步骤**:

**Step 1: 创建 skill-knowledge-base 模块**

```
skills/capabilities/knowledge/skill-knowledge-base/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/knowledge/
    ├── api/
    │   └── KnowledgeBaseApi.java
    ├── model/
    │   ├── KnowledgeBase.java
    │   └── Document.java
    └── service/
        └── KnowledgeBaseService.java
```

**Step 2: 创建 skill-rag 模块**

```
skills/capabilities/knowledge/skill-rag/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/rag/
    ├── api/
    │   └── RagApi.java
    └── service/
        └── RagService.java
```

**验收标准**:
- [ ] skill-knowledge-base 可独立运行
- [ ] skill-rag 可独立安装
- [ ] 地址空间配置正确 (0x38-0x3F)

---

### 3.5 Task-SKILL-005: 拆分审计技能

**优先级**: P2  
**预估工时**: 2天  
**负责人**: ________

**任务描述**:
将审计日志逻辑从 skill-scene 拆分为独立技能模块。

**详细步骤**:

```
skills/capabilities/security/skill-audit/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/java/net/ooder/skill/audit/
    ├── api/
    │   └── AuditApi.java
    ├── model/
    │   └── AuditLog.java
    └── service/
        └── AuditService.java
```

**验收标准**:
- [ ] skill-audit 可独立安装
- [ ] 审计日志记录正常
- [ ] 地址空间配置正确 (0x7A)

---

## 四、MVP 团队任务清单

### 4.1 Task-MVP-001: 定义 MVP 配置规范

**优先级**: P0  
**预估工时**: 1天  
**负责人**: ________

**任务描述**:
定义 MVP 最小配置规范文档，包括核心 API、可选技能、Profile 定义。

**详细步骤**:

**Step 1: 创建规范文档**

```yaml
apiVersion: skill.ooder.net/v1
kind: MVPConfig

metadata:
  version: "2.3.1"
  description: "Ooder MVP 最小配置规范"

spec:
  coreApis:
    - path: /api/v1/auth
      module: skill-common
      required: true
      description: "认证核心 API"
    - path: /api/v1/org
      module: skill-common
      required: true
      description: "组织核心 API"
    - path: /api/v1/system
      module: skill-common
      required: true
      description: "系统配置 API"
    - path: /api/v1/config
      module: skill-common
      required: true
      description: "配置中心 API"
    - path: /api/v1/capabilities
      module: skill-capability
      required: true
      description: "能力管理 API"
      
  optionalSkills:
    - id: skill-llm-openai
      category: llm
      address: 0x32
      condition: "llm.enabled == true"
      
    - id: skill-llm-ollama
      category: llm
      address: 0x31
      condition: "llm.enabled == true"
      
    - id: skill-knowledge-base
      category: know
      address: 0x38
      condition: "knowledge.enabled == true"
      
    - id: skill-audit
      category: sec
      address: 0x7A
      condition: "audit.enabled == true"
```

**验收标准**:
- [ ] 规范文档评审通过
- [ ] 与 Skills 团队对齐接口

---

### 4.2 Task-MVP-002: 创建 Profile 配置文件

**优先级**: P0  
**预估工时**: 2天  
**负责人**: ________

**任务描述**:
创建四种 Profile 配置文件，支持不同规模的部署场景。

**详细步骤**:

**Step 1: 创建 micro.json**

```json
{
  "profile": "micro",
  "description": "微型部署 - 仅核心功能",
  "coreApis": true,
  "optionalSkills": [],
  "estimatedMemory": "128MB",
  "startupTime": "5s"
}
```

**Step 2: 创建 small.json**

```json
{
  "profile": "small",
  "description": "小型部署 - 核心功能 + 本地LLM",
  "coreApis": true,
  "optionalSkills": [
    "skill-llm-ollama"
  ],
  "estimatedMemory": "256MB",
  "startupTime": "10s"
}
```

**Step 3: 创建 large.json**

```json
{
  "profile": "large",
  "description": "大型部署 - 核心功能 + LLM + 知识库",
  "coreApis": true,
  "optionalSkills": [
    "skill-llm-openai",
    "skill-knowledge-base",
    "skill-audit"
  ],
  "estimatedMemory": "512MB",
  "startupTime": "15s"
}
```

**Step 4: 创建 enterprise.json**

```json
{
  "profile": "enterprise",
  "description": "企业部署 - 全功能",
  "coreApis": true,
  "optionalSkills": [
    "skill-llm-openai",
    "skill-llm-qianwen",
    "skill-knowledge-base",
    "skill-rag",
    "skill-audit",
    "skill-monitor"
  ],
  "estimatedMemory": "1GB",
  "startupTime": "30s"
}
```

**验收标准**:
- [ ] 四个 Profile 文件创建完成
- [ ] 配置验证脚本通过

---

### 4.3 Task-MVP-003: 实现按需加载机制

**优先级**: P1  
**预估工时**: 3天  
**负责人**: ________

**任务描述**:
实现根据 Profile 配置按需加载技能的机制。

**详细步骤**:

**Step 1: 创建 ProfileLoader**

```java
public class ProfileLoader {
    
    public MVPConfig loadProfile(String profileName) {
        // 加载 profile 配置
    }
    
    public List<String> getRequiredSkills(MVPConfig config) {
        // 获取需要加载的技能列表
    }
    
    public void validateDependencies(MVPConfig config) {
        // 验证依赖关系
    }
}
```

**Step 2: 创建 SkillLoader**

```java
public class SkillLoader {
    
    public void loadSkill(String skillId) {
        // 动态加载技能
    }
    
    public void unloadSkill(String skillId) {
        // 卸载技能
    }
    
    public boolean isLoaded(String skillId) {
        // 检查是否已加载
    }
}
```

**验收标准**:
- [ ] ProfileLoader 可正确加载配置
- [ ] SkillLoader 可动态加载/卸载技能
- [ ] 依赖验证功能正常

---

### 4.4 Task-MVP-004: 更新安装向导

**优先级**: P1  
**预估工时**: 2天  
**负责人**: ________

**任务描述**:
更新安装向导，支持 Profile 选择和技能安装。

**详细步骤**:

**Step 1: 更新 capability-discovery.html**

- 添加 Profile 选择步骤
- 显示可选技能列表
- 显示预估资源占用

**Step 2: 更新安装流程**

```javascript
// 安装流程
const installFlow = {
    steps: [
        { id: 'profile', name: '选择部署规模' },
        { id: 'skills', name: '选择技能组件' },
        { id: 'config', name: '配置参数' },
        { id: 'install', name: '执行安装' },
        { id: 'verify', name: '验证安装' }
    ]
};
```

**验收标准**:
- [ ] Profile 选择功能正常
- [ ] 技能选择功能正常
- [ ] 安装流程完整

---

### 4.5 Task-MVP-005: 创建启动验证脚本

**优先级**: P1  
**预估工时**: 1天  
**负责人**: ________

**任务描述**:
创建启动验证脚本，验证 MVP 核心功能正常。

**详细步骤**:

**Step 1: 创建验证脚本**

```bash
#!/bin/bash
# mvp-verify.sh

echo "=== MVP 核心验证 ==="

# 验证核心 API
echo "1. 验证 Auth API..."
curl -s http://localhost:8084/api/v1/auth/session

echo "2. 验证 Org API..."
curl -s http://localhost:8084/api/v1/org/users/current

echo "3. 验证 Config API..."
curl -s http://localhost:8084/api/v1/system/config

echo "4. 验证 Capability API..."
curl -s http://localhost:8084/api/v1/capabilities

# 验证可选技能
echo "5. 验证已安装技能..."
curl -s http://localhost:8084/api/v1/capabilities?installed=true

echo "=== 验证完成 ==="
```

**验收标准**:
- [ ] 验证脚本可执行
- [ ] 所有核心 API 验证通过

---

## 五、协作接口定义

### 5.1 模块间接口

**skill-common → skill-capability**:
```java
// 能力状态查询接口
public interface CapabilityStateQuery {
    boolean isInstalled(String capabilityId);
    CapabilityStatus getStatus(String capabilityId);
}
```

**skill-capability → skill-llm-* **:
```java
// LLM 调用接口
public interface LlmInvoker {
    String chat(String prompt, Map<String, Object> options);
    void streamChat(String prompt, Consumer<String> callback);
}
```

**skill-capability → skill-knowledge-* **:
```java
// 知识检索接口
public interface KnowledgeRetriever {
    List<Document> search(String query, int limit);
    void index(Document doc);
}
```

### 5.2 地址空间分配

| 分类 | 地址范围 | 模块 |
|------|---------|------|
| sys | 0x00-0x07 | skill-common |
| org | 0x08-0x0F | skill-common |
| auth | 0x10-0x17 | skill-common |
| vfs | 0x20-0x27 | skill-vfs-* |
| llm | 0x30-0x37 | skill-llm-* |
| know | 0x38-0x3F | skill-knowledge-* |
| sec | 0x78-0x7F | skill-audit |

---

## 六、验收标准

### 6.1 Skills 团队验收标准

| 验收项 | 标准 | 验证方式 |
|--------|------|---------|
| skill-common 编译 | 无错误 | mvn compile |
| skill-common 测试 | 覆盖率 > 80% | mvn test |
| skill-capability 编译 | 无错误 | mvn compile |
| LLM 驱动独立安装 | 可独立运行 | 启动验证 |
| 知识库独立安装 | 可独立运行 | 启动验证 |
| 地址空间配置 | 符合规范 | 配置检查 |

### 6.2 MVP 团队验收标准

| 验收项 | 标准 | 验证方式 |
|--------|------|---------|
| Profile 配置 | 4个文件完整 | 文件检查 |
| micro 启动 | < 5s | 时间测试 |
| small 启动 | < 10s | 时间测试 |
| large 启动 | < 15s | 时间测试 |
| 内存占用 | 符合预估 | 内存监控 |
| 验证脚本 | 全部通过 | 脚本执行 |

---

## 七、时间节点

### 7.1 里程碑

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| M1: 架构设计完成 | Day 3 | 设计文档评审通过 |
| M2: skill-common 完成 | Day 7 | 可独立运行 |
| M3: skill-capability 完成 | Day 10 | 可独立运行 |
| M4: MVP 配置完成 | Day 10 | Profile 文件完成 |
| M5: LLM 驱动拆分完成 | Day 14 | 可独立安装 |
| M6: 知识库拆分完成 | Day 17 | 可独立安装 |
| M7: 集成测试完成 | Day 20 | 全部验收通过 |
| M8: 发布 | Day 21 | v2.3.1 发布 |

### 7.2 每日同步

- **时间**: 每日 10:00
- **形式**: 站会
- **内容**: 进度同步、问题阻塞、协调资源

---

## 八、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 接口定义不一致 | 集成失败 | 提前对齐接口文档 |
| 依赖关系复杂 | 拆分困难 | 依赖分析先行 |
| 测试覆盖不足 | 质量问题 | 增加测试用例 |
| 时间延期 | 发布推迟 | 优先级调整 |

---

## 九、联系方式

**Skills 团队负责人**: ________  
**MVP 团队负责人**: ________  
**项目协调人**: ________

**沟通渠道**:
- 日常沟通: 企业微信群
- 问题跟踪: GitHub Issues
- 文档协作: 腾讯文档

---

**文档状态**: 待评审  
**最后更新**: 2026-03-12
