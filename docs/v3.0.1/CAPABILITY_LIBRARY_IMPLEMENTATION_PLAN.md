# OoderAgent 能力库实施方案 v3.0.1

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**状态**: 待实施  
**基于**: OS与Skills库深度对比分析、5W推理、用户闭环故事

---

## 📋 执行摘要

本方案基于对OS工程和Skills库的深度分析，从5W角度建立了完整的能力库体系，识别了缺失的关键能力，并制定了详细的实施路线图。

### 核心成果

1. ✅ **完成OS与Skills库对比分析** - 识别了6个完全重复技能、7组功能重复技能
2. ✅ **更新分类体系** - 废弃ABS/ASS/TBS，采用SceneType + visibility二维分类
3. ✅ **建立能力库体系** - 从5W角度建立了完整的能力库闭环逻辑
4. ✅ **识别缺失能力** - 识别了4个P0能力、4个P1能力、4个P2能力
5. ✅ **制定实施路线图** - 制定了4个阶段的详细实施计划

---

## 一、核心变更

### 1.1 分类体系升级

**旧版本 (v2.3)**:
```
ABS - 自驱业务场景
ASS - 自驱系统场景
TBS - 触发业务场景
SVC - 服务技能
```

**新版本 (v3.0.1)**:
```
SceneType (场景类型):
├── AUTO - 自驱场景 (自动运行)
└── TRIGGER - 触发场景 (需要触发)

visibility (可见性):
├── public - 公开可见 (用户可发现、可激活)
└── internal - 内部使用 (系统后台运行)

Service Categories (服务分类):
├── org - 组织服务
├── vfs - 存储服务
├── msg - 消息通讯
├── llm - LLM服务
├── knowledge - 知识服务
├── sys - 系统服务
├── payment - 支付服务
├── media - 媒体服务
└── util - 工具服务

Capability Types (能力类型):
├── ATOMIC - 原子能力
├── COMPOSITE - 组合能力
├── SCENE - 场景能力
├── DRIVER - 驱动能力
├── COLLABORATIVE - 协作能力
├── SERVICE - 服务能力
├── AI - AI能力
├── TOOL - 工具能力
├── CONNECTOR - 连接器能力
├── DATA - 数据能力
├── MANAGEMENT - 管理能力
├── COMMUNICATION - 通信能力
├── SECURITY - 安全能力
├── MONITORING - 监控能力
├── SKILL - 技能能力
└── CUSTOM - 自定义能力
```

**映射关系**:
```
ABS → SceneType.AUTO + visibility.public
ASS → SceneType.AUTO + visibility.internal
TBS → SceneType.TRIGGER + visibility.public
SVC → service-skill (保持不变)
```

### 1.2 文件更新清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `skill-classification.yaml` | ✅ 已更新 | 采用新的SceneType + visibility分类体系 |
| `DOCUMENT_MERGE_PLAN.md` | ✅ 已创建 | 文档合并方案 |
| `CAPABILITY_LIBRARY_PLANNING.md` | ✅ 已创建 | 能力库规划报告 |
| `OS_SKILLS_COMPARISON_ANALYSIS.md` | ✅ 已创建 | OS与Skills库深度对比分析 |
| `CAPABILITY_LIBRARY_IMPLEMENTATION_PLAN.md` | ✅ 本文档 | 能力库实施方案 |

---

## 二、OS已实现功能清单

### 2.1 核心页面功能（18个页面）

| 页面分类 | 页面数量 | 核心功能 | 状态 |
|---------|---------|---------|------|
| **能力发现** | 1个 | 雷达扫描、多源发现、结果展示 | ✅ 完整 |
| **能力管理** | 11个 | 注册、安装、调用、卸载、详情、激活、绑定、依赖、日志、权限、统计 | ✅ 完整 |
| **场景管理** | 7个 | 创建、列表、详情、能力绑定、场景组、知识库配置 | ✅ 完整 |

### 2.2 核心API接口（20+个）

```
能力发现API:
├── GET  /api/v1/discovery/methods              # 获取发现方法列表
├── POST /api/v1/discovery/local                # 本地发现
├── POST /api/v1/discovery/github               # GitHub发现
├── POST /api/v1/discovery/gitee                # Gitee发现
├── POST /api/v1/discovery/install              # 安装能力
└── GET  /api/v1/discovery/capability/{id}      # 获取能力详情

能力管理API:
├── GET    /api/v1/capabilities                 # 获取能力列表
├── POST   /api/v1/capabilities                 # 注册能力
├── GET    /api/v1/capabilities/{id}            # 获取能力详情
├── DELETE /api/v1/capabilities/{id}            # 删除能力
├── POST   /api/v1/capabilities/{id}/activate   # 激活能力
├── POST   /api/v1/capabilities/{id}/bind       # 绑定能力
└── POST   /api/v1/discovery/capabilities/invoke # 调用能力

场景管理API:
├── POST /api/v1/scenes/list                    # 场景列表
├── POST /api/v1/scenes/create                  # 创建场景
├── POST /api/v1/scenes/delete                  # 删除场景
├── GET  /api/v1/scenes/{id}                    # 场景详情
├── GET  /api/v1/scenes/{id}/capabilities       # 场景能力列表
└── POST /api/v1/scenes/{id}/knowledge          # 场景知识库配置
```

### 2.3 发现途径（7种）

```
1. GitHub - 代码仓库发现
2. Gitee - 代码仓库发现
3. SKILL_CENTER - 技能中心（官方市场）
4. UDP_BROADCAST - UDP广播（局域网发现）
5. LOCAL_FS - 本地文件（本地安装）
6. MDNS_DNS_SD - mDNS（服务发现）
7. DHT_KADEMLIA - DHT（分布式发现）
```

---

## 三、Skills库独有能力

### 3.1 驱动能力（28个）

#### LLM驱动（7个）
```
skill-llm-base        # LLM基础驱动
skill-llm-openai      # OpenAI驱动
skill-llm-deepseek    # DeepSeek驱动（OS也有）
skill-llm-qianwen     # 通义千问驱动
skill-llm-volcengine  # 火山引擎驱动
skill-llm-ollama      # Ollama驱动
skill-llm-baidu       # 百度文心驱动
```

#### IM驱动（3个）
```
skill-im-dingding     # 钉钉IM驱动（OS也有）
skill-im-feishu       # 飞书IM驱动
skill-im-wecom        # 企业微信IM驱动
```

#### 组织驱动（4个）
```
skill-org-base        # 组织基础驱动
skill-org-feishu      # 飞书组织驱动
skill-org-ldap        # LDAP组织驱动
skill-org-wecom       # 企业微信组织驱动
```

#### 存储驱动（6个）
```
skill-vfs-base        # 存储基础驱动
skill-vfs-local       # 本地存储驱动
skill-vfs-database    # 数据库存储驱动
skill-vfs-minio       # Minio存储驱动
skill-vfs-oss         # 阿里云OSS驱动
skill-vfs-s3          # AWS S3驱动
```

#### 支付驱动（3个）
```
skill-payment-alipay   # 支付宝驱动
skill-payment-wechat   # 微信支付驱动
skill-payment-unionpay # 银联支付驱动
```

#### 媒体驱动（5个）
```
skill-media-wechat      # 微信公众号驱动
skill-media-weibo       # 微博驱动
skill-media-zhihu       # 知乎驱动
skill-media-toutiao     # 头条驱动
skill-media-xiaohongshu # 小红书驱动
```

---

## 四、缺失能力识别

### 4.1 P0 - 核心缺失能力（必须补充）

| 能力 | 分类 | 用户故事 | 建议实现 | 优先级 |
|------|------|---------|---------|--------|
| **能力说明书生成** | TOOL | 作为开发者，我希望能自动生成能力说明书，以便快速发布能力 | 基于skill.yaml自动生成README.md | P0 |
| **能力依赖检查** | TOOL | 作为用户，我希望在安装能力前检查依赖是否满足，以便避免安装失败 | 基于依赖声明自动检查 | P0 |
| **能力测试框架** | TOOL | 作为开发者，我希望能测试能力接口，以便确保能力质量 | 提供能力测试工具 | P0 |
| **能力发布流程** | MANAGEMENT | 作为开发者，我希望能发布能力到市场，以便其他用户使用 | 提供能力发布工具 | P0 |

### 4.2 P1 - 重要缺失能力（建议补充）

| 能力 | 分类 | 用户故事 | 建议实现 | 优先级 |
|------|------|---------|---------|--------|
| **能力评分系统** | MANAGEMENT | 作为用户，我希望能对能力评分，以便帮助其他用户选择 | 实现评分功能 | P1 |
| **能力推荐系统** | AI | 作为用户，希望系统能推荐相关能力，以便快速找到需要的能力 | 基于使用记录推荐 | P1 |
| **能力监控告警** | MONITORING | 作为管理员，希望能监控能力运行状态，以便及时发现问题 | 实现告警功能 | P1 |
| **能力日志分析** | MONITORING | 作为管理员，希望能分析能力日志，以便优化能力性能 | 实现日志分析 | P1 |

### 4.3 P2 - 增强能力（可选补充）

| 能力 | 分类 | 用户故事 | 建议实现 | 优先级 |
|------|------|---------|---------|--------|
| **能力性能优化** | TOOL | 作为开发者，希望能优化能力性能，以便提升用户体验 | 提供性能分析工具 | P2 |
| **能力安全扫描** | SECURITY | 作为管理员，希望能扫描能力安全漏洞，以便保障系统安全 | 提供安全扫描工具 | P2 |
| **能力成本分析** | MANAGEMENT | 作为管理员，希望能分析能力成本，以便优化资源使用 | 提供成本分析工具 | P2 |
| **能力迁移工具** | TOOL | 作为管理员，希望能迁移能力到其他环境，以便快速部署 | 提供迁移工具 | P2 |

---

## 五、实施路线图

### Phase 1: 核心功能迁移（1周）

**目标**: 将OS的核心功能迁移到Skills库

**任务清单**:
- [ ] 迁移能力发现页面和API
  - [ ] capability-discovery.html
  - [ ] skill-discovery.js
  - [ ] /api/v1/discovery/* 接口
  
- [ ] 迁移能力管理页面和API
  - [ ] capability-management.html
  - [ ] my-capabilities.html
  - [ ] capability-detail.html
  - [ ] capability-activation.html
  - [ ] capability-binding.html
  - [ ] /api/v1/capabilities/* 接口
  
- [ ] 迁移场景管理页面和API
  - [ ] scene-management.html
  - [ ] scene-detail.html
  - [ ] /api/v1/scenes/* 接口
  
- [ ] 更新分类体系
  - [x] 更新skill-classification.yaml
  - [ ] 更新相关代码引用

**验收标准**:
- ✅ 所有OS页面可正常访问
- ✅ 所有OS API可正常调用
- ✅ 分类体系统一为SceneType + visibility

**预计完成时间**: 2026-04-09

---

### Phase 2: 驱动能力补充（2周）

**目标**: 将Skills独有的驱动补充到OS

**任务清单**:
- [ ] 补充LLM驱动
  - [ ] skill-llm-openai
  - [ ] skill-llm-qianwen
  - [ ] skill-llm-ollama
  - [ ] skill-llm-baidu
  
- [ ] 补充IM驱动
  - [ ] skill-im-feishu
  - [ ] skill-im-wecom
  
- [ ] 补充组织驱动
  - [ ] skill-org-feishu
  - [ ] skill-org-ldap
  - [ ] skill-org-wecom
  
- [ ] 补充存储驱动
  - [ ] skill-vfs-base
  - [ ] skill-vfs-local
  - [ ] skill-vfs-database
  - [ ] skill-vfs-minio
  - [ ] skill-vfs-oss
  - [ ] skill-vfs-s3
  
- [ ] 补充支付驱动
  - [ ] skill-payment-alipay
  - [ ] skill-payment-wechat
  - [ ] skill-payment-unionpay
  
- [ ] 补充媒体驱动
  - [ ] skill-media-wechat
  - [ ] skill-media-weibo
  - [ ] skill-media-zhihu
  - [ ] skill-media-toutiao
  - [ ] skill-media-xiaohongshu

**验收标准**:
- ✅ 所有驱动可正常安装
- ✅ 所有驱动可正常调用
- ✅ 所有驱动有完整的skill.yaml

**预计完成时间**: 2026-04-23

---

### Phase 3: 缺失能力补充（3周）

**目标**: 补充P0和P1缺失能力

**任务清单**:
- [ ] P0能力实现
  - [ ] 能力说明书生成
  - [ ] 能力依赖检查
  - [ ] 能力测试框架
  - [ ] 能力发布流程
  
- [ ] P1能力实现
  - [ ] 能力评分系统
  - [ ] 能力推荐系统
  - [ ] 能力监控告警
  - [ ] 能力日志分析

**验收标准**:
- ✅ 所有P0能力可用
- ✅ 所有P1能力可用
- ✅ 所有闭环完整

**预计完成时间**: 2026-05-14

---

### Phase 4: 文档与测试（1周）

**目标**: 完善文档和测试

**任务清单**:
- [ ] 编写文档
  - [ ] 能力库使用指南
  - [ ] 能力开发指南
  - [ ] 能力发布指南
  - [ ] 能力API文档
  
- [ ] 编写测试
  - [ ] 能力测试用例
  - [ ] API测试用例
  - [ ] 集成测试用例

**验收标准**:
- ✅ 所有文档完整
- ✅ 所有测试通过
- ✅ 所有API文档完整

**预计完成时间**: 2026-05-21

---

## 六、风险评估与应对

### 6.1 风险矩阵

| 风险 | 影响 | 概率 | 风险等级 | 应对措施 |
|------|------|------|---------|---------|
| API不兼容 | 高 | 中 | 🟡 中 | 建立兼容层，逐步迁移 |
| 依赖冲突 | 中 | 中 | 🟡 中 | 统一依赖版本管理 |
| 功能缺失 | 中 | 低 | 🟢 低 | 详细测试，补充缺失功能 |
| 文档不一致 | 低 | 高 | 🟢 低 | 统一文档规范 |
| 性能下降 | 中 | 低 | 🟢 低 | 性能测试，优化关键路径 |
| 安全漏洞 | 高 | 低 | 🟡 中 | 安全扫描，及时修复 |

### 6.2 回滚方案

```bash
# 如需回滚，从备份恢复
git checkout backup-before-merge

# 或者回滚到特定版本
git checkout v2.3.1
```

---

## 七、成功指标

### 7.1 量化指标

| 指标 | 当前值 | 目标值 | 衡量方式 |
|------|--------|--------|---------|
| **能力总数** | 63 | 94 | skill-index.yaml统计 |
| **驱动能力** | 28 | 31 | 驱动分类统计 |
| **服务能力** | 17 | 32 | 服务分类统计 |
| **场景能力** | 8 | 27 | 场景分类统计 |
| **文档完整率** | 60% | 100% | 文档覆盖率统计 |
| **测试覆盖率** | 40% | 80% | 测试覆盖率统计 |
| **API可用性** | 80% | 99% | API健康检查统计 |

### 7.2 质量指标

| 指标 | 当前值 | 目标值 | 衡量方式 |
|------|--------|--------|---------|
| **代码规范符合率** | 70% | 100% | 代码检查工具 |
| **文档规范符合率** | 60% | 100% | 文档检查工具 |
| **API响应时间** | 500ms | 200ms | 性能监控 |
| **错误率** | 5% | 1% | 错误监控 |

---

## 八、后续维护

### 8.1 文档维护

- **唯一版本**: 所有文档统一在v3.0.1目录维护
- **版本号管理**: 文档版本号与代码版本号同步
- **变更记录**: 每个文档底部维护变更历史
- **审核流程**: 重要文档变更需要审核

### 8.2 能力维护

- **注册规范**: 每个能力必须有完整的skill.yaml
- **依赖管理**: 所有依赖必须显式声明
- **版本管理**: 采用语义化版本控制
- **生命周期**: 建立完整的能力生命周期管理

### 8.3 持续改进

- **定期审核**: 每周审核能力库状态
- **用户反馈**: 收集用户反馈，持续优化
- **性能监控**: 监控能力运行性能
- **安全审计**: 定期进行安全审计

---

## 九、总结

### 9.1 核心成果

1. ✅ **完成深度分析** - 对OS和Skills库进行了全面的对比分析
2. ✅ **升级分类体系** - 采用SceneType + visibility二维分类体系
3. ✅ **建立能力库体系** - 从5W角度建立了完整的能力库闭环逻辑
4. ✅ **识别缺失能力** - 识别了12个缺失的关键能力
5. ✅ **制定实施路线图** - 制定了4个阶段的详细实施计划

### 9.2 关键建议

1. **优先迁移OS核心功能** - 能力发现、能力管理、场景管理是核心，必须优先迁移
2. **补充Skills独有驱动** - Skills的驱动能力是宝贵资产，必须补充到OS
3. **统一分类体系** - 采用SceneType + visibility二维分类体系
4. **完善闭环能力** - 补充缺失的能力，确保每个闭环完整

### 9.3 下一步行动

1. **立即执行**: 开始Phase 1核心功能迁移
2. **短期计划**: 补充Skills独有驱动
3. **中期计划**: 补充P0和P1缺失能力
4. **长期计划**: 完善文档和测试，持续优化

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09  
**预计完成**: 2026-05-21
