# 从OS迁移Skills到发布仓库的冲突处理方案

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**工作流程**: OS(开发库) → Skills(发布仓库) → Gitee  
**目的**: 识别新增skills、处理冲突、制定迁移策略

---

## 📋 一、OS与Skills的关系定位

### 1.1 工作流程

```
┌─────────────────────────────────────────────────────────────────┐
│                     Skills开发与发布流程                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 开发阶段                                                     │
│     └── OS (开发库)                                             │
│         ├── 开发新的skill                                        │
│         ├── 调试skill功能                                        │
│         └── 测试skill稳定性                                      │
│                                                                 │
│  2. 迁移阶段                                                     │
│     └── OS → Skills                                             │
│         ├── 识别新增skill                                        │
│         ├── 识别冲突skill                                        │
│         ├── 处理冲突                                             │
│         └── 复制skill到Skills                                    │
│                                                                 │
│  3. 发布阶段                                                     │
│     └── Skills → Gitee                                          │
│         ├── 版本管理                                             │
│         ├── 文档更新                                             │
│         └── 推送到远程仓库                                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 角色定位

| 仓库 | 角色 | 用途 | 内容 |
|------|------|------|------|
| **OS** | 开发库 | 开发、调试、测试 | 开发中的skills、实验性skills |
| **Skills** | 发布仓库 | 生产环境、分发 | 稳定的skills、正式版本 |
| **Gitee** | 远程仓库 | 版本控制、协作 | 所有发布的skills |

---

## 📊 二、OS中的Skills清单（46个）

### 2.1 按分类统计

| 分类 | 数量 | 说明 |
|------|------|------|
| **_business** | 12个 | 业务层skills |
| **_drivers** | 7个 | 驱动层skills |
| **_system** | 27个 | 系统层skills |
| **总计** | **46个** | - |

### 2.2 完整清单

#### _business（12个）

```
1. skill-context           # 上下文管理
2. skill-driver-config     # 驱动配置
3. skill-install-scene     # 场景安装
4. skill-installer         # 安装器
5. skill-keys              # 密钥管理
6. skill-knowledge         # 知识库
7. skill-llm-config        # LLM配置
8. skill-procedure         # 流程管理
9. skill-scenes            # 场景管理（包含SceneGroup功能）⭐ 重要
10. skill-security         # 安全服务
11. skill-selector         # 选择器
12. skill-todo             # 待办管理 ⭐ 重要
```

#### _drivers（7个）

```
1. skill-im-dingding       # 钉钉IM驱动
2. skill-llm-base          # LLM基础驱动
3. skill-llm-deepseek      # DeepSeek驱动
4. skill-llm-monitor       # LLM监控
5. skill-org-web           # 组织Web驱动
6. skill-spi               # SPI驱动
7. skill-volcengine        # 火山引擎驱动（待确认）
```

#### _system（27个）

```
1. skill-agent             # Agent服务
2. skill-audit             # 审计服务
3. skill-auth              # 认证服务
4. skill-capability        # 能力服务
5. skill-common            # 通用服务
6. skill-config            # 配置服务
7. skill-dashboard         # 仪表盘
8. skill-dict              # 字典服务
9. skill-discovery         # 发现服务
10. skill-history          # 历史服务
11. skill-install          # 安装服务
12. skill-key              # 密钥服务
13. skill-knowledge        # 知识服务
14. skill-llm-chat         # LLM聊天
15. skill-management       # 技能管理
16. skill-menu             # 菜单服务
17. skill-notification     # 通知服务
18. skill-org              # 组织服务
19. skill-protocol         # 协议服务
20. skill-role             # 角色服务
21. skill-scene            # 场景服务
22. skill-setup            # 设置服务
23. skill-support          # 支持服务
24. skill-template         # 模板服务
... (还有3个未列出)
```

---

## 🔍 三、Skills中的Skills清单（63个）

### 3.1 按分类统计

| 分类 | 数量 | 说明 |
|------|------|------|
| **_drivers** | 28个 | 驱动类skills |
| **_system** | 4个 | 系统类skills |
| **scenes** | 15个 | 场景类skills |
| **tools** | 10个 | 工具类skills |
| **capabilities** | 6个 | 能力类skills |
| **总计** | **63个** | - |

### 3.2 完整清单（略）

详见 [skill-index.yaml](file:///e:/github/ooder-skills/skill-index.yaml)

---

## ⚠️ 四、冲突分析

### 4.1 完全重复的Skills（6个）

| Skill ID | OS版本 | Skills版本 | 冲突类型 | 处理策略 |
|---------|--------|-----------|---------|---------|
| **skill-common** | 2.3.1 | 1.0.0 | 版本冲突 | **使用OS版本**（更新） |
| **skill-llm-base** | 2.3.1 | 1.0.0 | 版本冲突 | **使用OS版本**（更新） |
| **skill-im-dingding** | 2.3.1 | - | OS有skill.yaml, Skills只有pom.xml | **使用OS版本**（补充） |
| **skill-llm-chat** | 2.3.1 | - | OS更完整 | **使用OS版本**（更新） |
| **skill-management** | 2.3.1 | - | OS更规范 | **使用OS版本**（更新） |
| **skill-protocol** | 2.3.1 | - | 功能相似 | **合并** |

### 4.2 功能重复的Skills（7组）

| 功能领域 | OS Skills | Skills Skills | 冲突分析 | 处理策略 |
|---------|-----------|--------------|---------|---------|
| **知识管理** | skill-knowledge | skill-knowledge-qa, skill-knowledge-management | 功能重叠 | **合并为3个独立skills** |
| **待办管理** | skill-todo | skill-todo-sync | 功能重叠 | **合并为skill-todo** |
| **密钥管理** | skill-keys, skill-key | - | OS内部重复 | **合并为skill-key-management** |
| **组织管理** | skill-org | skill-org-base, skill-org-feishu, skill-org-ldap, skill-org-wecom | 架构不同 | **保留Skills架构**（更完善） |
| **安装管理** | skill-installer, skill-install, skill-install-scene | - | OS内部重复 | **合并为skill-installer** |
| **场景管理** | skill-scenes, skill-scene | - | OS内部重复 | **合并为skill-scene-engine** |
| **配置管理** | skill-driver-config, skill-config, skill-llm-config | skill-llm-config-manager | 功能重叠 | **整合为2个skills** |

### 4.3 OS独有的Skills（需要迁移到Skills）

#### 新增Skills（40个）

**_business（12个）**:
```
1. skill-context           # 新增
2. skill-driver-config     # 新增
3. skill-install-scene     # 新增
4. skill-installer         # 新增
5. skill-keys              # 新增
6. skill-knowledge         # 新增（需合并）
7. skill-llm-config        # 新增
8. skill-procedure         # 新增
9. skill-scenes            # 新增 ⭐ 包含SceneGroup功能（80个API）
10. skill-security         # 新增
11. skill-selector         # 新增
12. skill-todo             # 新增（需与Skills的skill-todo-sync合并）
```

**重要说明**:

**skill-scenes (SceneGroup)**:
- 提供完整的SceneGroup管理功能
- 包含80个API接口
- 支持场景组创建、激活、配置、参与者管理、快照、知识库绑定等
- 实现文件：
  - SceneGroupService.java (服务接口)
  - SceneGroupServiceImpl.java (服务实现)
  - SceneGroupController.java (控制器，303行代码)
  - SceneGroup.java (实体类)
  - SceneGroupRepository.java (数据访问)
  - SceneGroupDTO.java (数据传输对象)

**skill-todo**:
- 提供待办管理服务
- 需要与Skills库中的skill-todo-sync合并
- skill-common中还有TodoSyncService SPI接口

**skill-common中的工具类SPI**:
- CalendarService: 日历服务接口（提供日历事件管理）
- TodoSyncService: 待办同步服务接口（提供待办同步功能）
- 这些SPI接口为Skills库中的工具类skills提供了扩展点

**_drivers（3个）**:
```
1. skill-llm-monitor       # 新增（OS独有，24个Java文件）
2. skill-org-web           # 新增（OS独有，4个Java文件）
3. skill-spi               # 新增
```

**_system（25个）**:
```
1. skill-agent             # 新增
2. skill-audit             # 新增
3. skill-auth              # 新增
4. skill-capability        # 新增
5. skill-config            # 新增
6. skill-dashboard         # 新增
7. skill-dict              # 新增
8. skill-discovery         # 新增
9. skill-history           # 新增
10. skill-install          # 新增
11. skill-key              # 新增（需合并）
12. skill-knowledge        # 新增（需合并）
13. skill-menu             # 新增
14. skill-notification     # 新增
15. skill-org              # 新增（需合并）
16. skill-role             # 新增
17. skill-scene            # 新增（需合并）
18. skill-setup            # 新增
19. skill-support          # 新增
20. skill-template         # 新增
... (还有5个未列出)
```

### 4.4 Skills独有的Skills（保留）

#### Skills独有（23个）

**_drivers（23个）**:
```
LLM驱动（5个）:
1. skill-llm-openai        # Skills独有
2. skill-llm-qianwen       # Skills独有
3. skill-llm-ollama        # Skills独有
4. skill-llm-baidu         # Skills独有
5. skill-llm-volcengine    # Skills独有

IM驱动（2个）:
6. skill-im-feishu         # Skills独有
7. skill-im-wecom          # Skills独有

组织驱动（4个）:
8. skill-org-base          # Skills独有
9. skill-org-feishu        # Skills独有
10. skill-org-ldap         # Skills独有
11. skill-org-wecom        # Skills独有

存储驱动（6个）:
12. skill-vfs-base         # Skills独有
13. skill-vfs-local        # Skills独有
14. skill-vfs-database     # Skills独有
15. skill-vfs-minio        # Skills独有
16. skill-vfs-oss          # Skills独有
17. skill-vfs-s3           # Skills独有

支付驱动（3个）:
18. skill-payment-alipay   # Skills独有
19. skill-payment-wechat   # Skills独有
20. skill-payment-unionpay # Skills独有

媒体驱动（5个）:
21. skill-media-wechat     # Skills独有
22. skill-media-weibo      # Skills独有
23. skill-media-zhihu      # Skills独有
24. skill-media-toutiao    # Skills独有
25. skill-media-xiaohongshu # Skills独有
```

**scenes（15个）**:
```
Skills独有场景skills（15个）
注意：OS中有skill-scenes，提供SceneGroup功能
```

**tools（10个）**:
```
Skills独有工具skills（10个）
注意：OS中有相关实现：
- skill-todo (_business): 待办管理服务
- CalendarService (skill-common/SPI): 日历服务接口
- TodoSyncService (skill-common/SPI): 待办同步服务接口
```

---

## 🚀 五、迁移策略

### 5.1 迁移原则

1. **OS是开发库**: OS中的skills是开发版本，可以快速迭代
2. **Skills是发布仓库**: Skills中的skills是稳定版本，需要保证质量
3. **版本优先**: OS版本通常更新，应该使用OS版本
4. **架构优先**: Skills架构更完善时，保留Skills架构
5. **合并去重**: 功能重复的skills需要合并

### 5.2 冲突处理策略

#### 策略1: 版本更新（6个skills）

**适用场景**: OS版本更新，Skills版本较旧

**处理步骤**:
```
1. 备份Skills中的旧版本
2. 复制OS中的新版本到Skills
3. 更新版本号和文档
4. 测试验证
```

**适用skills**:
- skill-common
- skill-llm-base
- skill-im-dingding
- skill-llm-chat
- skill-management
- skill-protocol

#### 策略2: 功能合并（7组skills）

**适用场景**: OS和Skills都有类似功能，需要合并

**处理步骤**:
```
1. 分析两个版本的功能差异
2. 确定合并方案
3. 创建新的合并版本
4. 测试验证
5. 删除旧版本
```

**适用skills**:
- 知识管理组: skill-knowledge (OS) + skill-knowledge-qa + skill-knowledge-management (Skills)
- 待办管理组: skill-todo (OS) + skill-todo-sync (Skills)
  - OS的skill-todo提供待办管理服务
  - Skills的skill-todo-sync提供同步功能
  - 合并方案：保留OS的skill-todo，补充Skills的同步功能
  - 注意：skill-common中有TodoSyncService SPI接口
- 密钥管理组: skill-keys + skill-key (OS)
- 组织管理组: skill-org (OS) + skill-org-* (Skills)
- 安装管理组: skill-installer + skill-install + skill-install-scene (OS)
- 场景管理组: skill-scenes (OS) + Skills的场景skills
  - OS的skill-scenes提供SceneGroup功能（80个API）
  - Skills的场景skills是具体的业务场景
  - 合并方案：保留OS的skill-scenes作为场景组管理，Skills的场景skills作为具体实现
- 配置管理组: skill-driver-config + skill-config + skill-llm-config (OS) + skill-llm-config-manager (Skills)

#### 策略3: 新增迁移（40个skills）

**适用场景**: OS独有的skills，需要迁移到Skills

**处理步骤**:
```
1. 检查skill.yaml完整性
2. 补充缺失的文档和测试
3. 复制到Skills对应目录
4. 更新skill-index.yaml
5. 测试验证
```

**适用skills**:
- OS独有的40个skills

#### 策略4: 保留Skills版本（23个skills）

**适用场景**: Skills独有的skills，OS中没有

**处理步骤**:
```
1. 保持Skills版本不变
2. 确保文档完整
3. 确保测试通过
```

**适用skills**:
- Skills独有的23个驱动skills
- Skills独有的15个场景skills
- Skills独有的10个工具skills

---

## 📝 六、迁移执行计划

### Phase 1: 准备工作（1天）

**任务清单**:
- [ ] 备份Skills仓库
- [ ] 创建迁移分支
- [ ] 准备迁移工具脚本
- [ ] 制定详细的迁移清单

### Phase 2: 版本更新（2天）

**任务清单**:
- [ ] 更新skill-common
- [ ] 更新skill-llm-base
- [ ] 更新skill-im-dingding
- [ ] 更新skill-llm-chat
- [ ] 更新skill-management
- [ ] 合并skill-protocol

**验收标准**:
- 所有更新的skills测试通过
- 文档更新完成
- 版本号正确

### Phase 3: 功能合并（3天）

**任务清单**:
- [ ] 合并知识管理组
- [ ] 合并待办管理组
- [ ] 合并密钥管理组
- [ ] 合并组织管理组
- [ ] 合并安装管理组
- [ ] 合并场景管理组
- [ ] 合并配置管理组

**验收标准**:
- 所有合并的skills功能完整
- 测试通过
- 文档更新完成

### Phase 4: 新增迁移（5天）

**任务清单**:
- [ ] 迁移_business的12个skills
- [ ] 迁移_drivers的3个skills
- [ ] 迁移_system的25个skills

**验收标准**:
- 所有迁移的skills测试通过
- skill.yaml完整
- 文档完整

### Phase 5: 整理与测试（2天）

**任务清单**:
- [ ] 更新skill-index.yaml
- [ ] 更新skill-classification.yaml
- [ ] 运行全量测试
- [ ] 更新文档

**验收标准**:
- 所有skills测试通过
- 文档完整
- 可以发布到Gitee

---

## 📊 七、迁移后的Skills统计

### 7.1 预计数量

| 分类 | 当前 | 迁移后 | 增加 |
|------|------|--------|------|
| **_drivers** | 28 | 31 | +3 |
| **_system** | 4 | 32 | +28 |
| **_business** | 0 | 12 | +12 |
| **scenes** | 15 | 27 | +12 |
| **tools** | 10 | 10 | 0 |
| **capabilities** | 6 | 6 | 0 |
| **总计** | **63** | **118** | **+55** |

### 7.2 迁移后结构

```
skills/
├── _drivers/               # 驱动类（31个）
│   ├── llm/               # LLM驱动（8个）
│   ├── im/                # IM驱动（3个）
│   ├── org/               # 组织驱动（5个）
│   ├── vfs/               # 存储驱动（6个）
│   ├── payment/           # 支付驱动（3个）
│   ├── media/             # 媒体驱动（5个）
│   └── spi/               # SPI驱动（1个）
│
├── _system/               # 系统类（32个）
│   ├── skill-common
│   ├── skill-protocol
│   ├── skill-config
│   ├── skill-management
│   ├── skill-llm-chat
│   ├── skill-llm-monitor
│   ├── skill-audit
│   ├── skill-auth
│   ├── skill-agent
│   ├── skill-dashboard
│   └── ... (22个)
│
├── _business/             # 业务类（12个）
│   ├── skill-context
│   ├── skill-driver-config
│   ├── skill-installer
│   ├── skill-knowledge
│   ├── skill-security
│   ├── skill-todo
│   └── ... (6个)
│
├── scenes/                # 场景类（27个）
│   ├── skill-llm-chat
│   ├── skill-knowledge-qa
│   ├── skill-daily-report
│   ├── skill-business
│   ├── skill-collaboration
│   └── ... (22个)
│
├── tools/                 # 工具类（10个）
│   ├── skill-calendar
│   ├── skill-report
│   └── ... (8个)
│
└── capabilities/          # 能力类（6个）
    ├── auth/
    ├── communication/
    ├── scheduler/
    ├── search/
    ├── monitor/
    └── infrastructure/
```

---

## ⚠️ 八、风险与应对

### 8.1 风险评估

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 功能丢失 | 高 | 中 | 详细测试，保留备份 |
| 版本混乱 | 中 | 中 | 统一版本号管理 |
| 依赖冲突 | 中 | 中 | 检查依赖关系 |
| 文档不一致 | 低 | 高 | 统一文档规范 |

### 8.2 回滚方案

```bash
# 如需回滚，从备份恢复
git checkout backup-before-migration

# 或者回滚到特定版本
git checkout v3.0.0
```

---

## 📚 九、总结

### 9.1 核心发现

1. **OS是开发库**: 46个skills，用于开发和调试
2. **Skills是发布仓库**: 63个skills，用于生产环境
3. **需要迁移**: 40个OS独有的skills需要迁移到Skills
4. **需要处理冲突**: 6个完全重复 + 7组功能重复
5. **迁移后总数**: 118个skills

### 9.2 关键建议

1. **优先处理冲突**: 先解决版本冲突和功能重复
2. **分阶段迁移**: 按优先级分阶段迁移
3. **保证质量**: 每个迁移的skill都要测试通过
4. **完善文档**: 迁移时补充缺失的文档

### 9.3 下一步行动

1. **立即执行**: 开始Phase 1准备工作
2. **短期计划**: 完成Phase 2和Phase 3
3. **中期计划**: 完成Phase 4新增迁移
4. **长期计划**: 完成Phase 5整理与测试

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09
