# Skill Index v2.3.1 执行计划

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **目标版本**: 2.3.1  
> **状态**: 待执行

---

## 一、文档分析结果

### 1.1 参考文档

| 文档 | 路径 | 状态 |
|------|------|:----:|
| PHASE1_BASE_LAYER_CONFIG.md | `E:\github\ooder-sdk\scene-engine\docs\` | ✅ 已读取 |
| SKILL_INDEX_REFACTORING_PROPOSAL.md | `E:\github\ooder-sdk\scene-engine\docs\` | ✅ 已读取 |

### 1.2 当前状态

| 项目 | 状态 | 版本 |
|------|:----:|:----:|
| skill-index.yaml | ✅ 已存在 | 2.3.1 |
| config/schema.yaml | ✅ 已存在 | 2.3.1 |
| config/addresses.yaml | ✅ 已存在 | 2.3.1 |
| config/categories.yaml | ✅ 已存在 | 2.3.1 |

---

## 二、可行性分析

### 2.1 无冲突项

| 项目 | 说明 |
|------|------|
| **版本统一** | 所有文件已更新到 2.3.1 |
| **目录结构** | config/ 目录已创建 |
| **基础层文件** | schema.yaml, addresses.yaml, categories.yaml 已创建 |
| **字段定义** | 与 SE 标准 v1.1.0 一致 |

### 2.2 待执行项

| 项目 | 优先级 | 工作量 |
|------|:------:|:------:|
| skill-index.yaml 字段补全 | P0 | 中 |
| skills 列表字段标准化 | P0 | 高 |
| visibility 枚举值转换 | P1 | 低 |
| 验证脚本编写 | P1 | 中 |

---

## 三、执行计划

### Phase 1: 基础层验证 (已完成 ✅)

```
┌─────────────────────────────────────────────────────────────────┐
│  Phase 1: 基础层验证                                    ✅ 完成  │
├─────────────────────────────────────────────────────────────────┤
│  [✅] 创建 config/ 目录                                          │
│  [✅] 创建 config/schema.yaml (v2.3.1)                          │
│  [✅] 创建 config/addresses.yaml (v2.3.1)                       │
│  [✅] 创建 config/categories.yaml (v2.3.1)                      │
│  [✅] 验证文件格式正确                                           │
└─────────────────────────────────────────────────────────────────┘
```

### Phase 2: skill-index.yaml 字段标准化 (待执行)

```
┌─────────────────────────────────────────────────────────────────┐
│  Phase 2: 字段标准化                                    ⏳ 待执行 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  2.1 字段重命名                                                 │
│  ─────────────────────────────────────────────────────────────  │
│  [✅] skillId → id                                              │
│  [✅] skillType → skillForm                                     │
│  [✅] category → capabilityCategory (skills部分)                │
│                                                                 │
│  2.2 字段新增                                                   │
│  ─────────────────────────────────────────────────────────────  │
│  [⏳] businessCategory - 根据 capabilityCategory 自动分配       │
│  [⏳] sceneType - 根据 mainFirst 映射 (SCENE技能)               │
│  [⏳] capabilityAddresses - 根据 capabilityCategory 生成        │
│  [⏳] category (SE标准) - 8个技术分类枚举                        │
│                                                                 │
│  2.3 枚举值转换                                                 │
│  ─────────────────────────────────────────────────────────────  │
│  [⏳] visibility: PUBLIC → public                               │
│  [⏳] visibility: DEVELOPER → developer                         │
│  [⏳] visibility: ADMIN → internal                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Phase 3: 验证与提交 (待执行)

```
┌─────────────────────────────────────────────────────────────────┐
│  Phase 3: 验证与提交                                    ⏳ 待执行 │
├─────────────────────────────────────────────────────────────────┤
│  [⏳] YAML 格式验证                                             │
│  [⏳] 字段完整性检查                                            │
│  [⏳] 枚举值有效性检查                                          │
│  [⏳] Git 提交                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、字段映射规则

### 4.1 visibility 映射

```yaml
# 当前值 → 目标值
PUBLIC: public
DEVELOPER: developer
ADMIN: internal
```

### 4.2 businessCategory 映射

```yaml
# 根据 capabilityCategory 自动分配
capabilityCategory:
  llm: AI_ASSISTANT
  know: AI_ASSISTANT
  comm: OFFICE_COLLABORATION
  media: MARKETING_OPERATIONS
  mon: SYSTEM_MONITOR
  sec: SECURITY_AUDIT
  iot: INFRASTRUCTURE
  org: INFRASTRUCTURE
  sys: INFRASTRUCTURE
  auth: SECURITY_AUDIT
  net: INFRASTRUCTURE
  vfs: SYSTEM_TOOLS
  db: SYSTEM_TOOLS
  payment: SYSTEM_TOOLS
  search: DATA_PROCESSING
  sched: INFRASTRUCTURE
  util: SYSTEM_TOOLS
```

### 4.3 sceneType 映射

```yaml
# 仅 SCENE 技能需要
skillForm: SCENE:
  mainFirst: true → sceneType: AUTO
  mainFirst: false → sceneType: TRIGGER
  无 mainFirst → sceneType: AUTO (默认)
```

### 4.4 category (SE标准) 映射

```yaml
# 根据 capabilityCategory 映射到 SE 标准 8 分类
capabilityCategory:
  llm: LLM
  know: KNOWLEDGE
  util: TOOL
  sched: WORKFLOW
  db: DATA
  search: DATA
  vfs: DATA
  org: SERVICE
  comm: SERVICE
  media: SERVICE
  mon: SERVICE
  iot: SERVICE
  payment: SERVICE
  sec: SERVICE
  auth: SERVICE
  net: SERVICE
  sys: SERVICE
```

### 4.5 capabilityAddresses 生成规则

```yaml
# 根据 capabilityCategory 自动生成
llm:
  required:
    - address: 0x30
      name: LLM_PROVIDER
      description: "LLM服务"
      
know:
  required:
    - address: 0x38
      name: KNOWLEDGE_BASE
      description: "知识库服务"
      
comm:
  required:
    - address: 0x50
      name: COMM_MESSAGING
      description: "通讯服务"
      
vfs:
  required:
    - address: 0x20
      name: VFS_STORAGE
      description: "文件存储服务"
```

---

## 五、执行命令

### 5.1 字段更新 (手动执行)

由于文件较大，建议使用脚本批量更新：

```powershell
# 更新 visibility 枚举值
(Get-Content skill-index.yaml) `
  -replace 'visibility: PUBLIC', 'visibility: public' `
  -replace 'visibility: DEVELOPER', 'visibility: developer' `
  -replace 'visibility: ADMIN', 'visibility: internal' |
  Set-Content skill-index.yaml
```

### 5.2 验证命令

```powershell
# YAML 格式验证
python -c "import yaml; yaml.safe_load(open('skill-index.yaml'))"

# 字段完整性检查
python scripts/validate_skill_index.py
```

### 5.3 提交命令

```bash
git add skill-index.yaml skills/config/
git commit -m "feat: update skill-index to v2.3.1

- Update version to 2.3.1
- Add businessCategory, sceneType, capabilityAddresses fields
- Convert visibility enum values to lowercase
- Align with SE standard v1.1.0

Refs: PHASE1_BASE_LAYER_CONFIG.md, SKILL_INDEX_REFACTORING_PROPOSAL.md"
```

---

## 六、风险与回滚

### 6.1 风险评估

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|:------:|:----:|----------|
| 字段映射错误 | 中 | 高 | 逐个验证，保留备份 |
| YAML 格式错误 | 低 | 中 | 使用验证工具 |
| 版本不兼容 | 低 | 高 | 分阶段发布 |

### 6.2 回滚方案

```bash
# 快速回滚
git checkout -- skill-index.yaml
git checkout -- skills/config/
```

---

## 七、任务清单

| ID | 任务 | 优先级 | 状态 | 负责人 |
|----|------|:------:|:----:|:------:|
| T01 | 创建 config 目录 | P0 | ✅ 完成 | - |
| T02 | 创建 schema.yaml | P0 | ✅ 完成 | - |
| T03 | 创建 addresses.yaml | P0 | ✅ 完成 | - |
| T04 | 创建 categories.yaml | P0 | ✅ 完成 | - |
| T05 | 更新版本到 2.3.1 | P0 | ✅ 完成 | - |
| T06 | 更新 visibility 枚举值 | P1 | ⏳ 待执行 | Skills Team |
| T07 | 新增 businessCategory 字段 | P0 | ⏳ 待执行 | Skills Team |
| T08 | 新增 sceneType 字段 | P0 | ⏳ 待执行 | Skills Team |
| T09 | 新增 capabilityAddresses 字段 | P0 | ⏳ 待执行 | Skills Team |
| T10 | 验证与提交 | P0 | ⏳ 待执行 | Skills Team |

---

## 八、结论

### 8.1 可行性结论

**✅ 可行，无冲突**

- 基础层配置文件已创建完成
- 字段定义与 SE 标准一致
- 版本已统一到 2.3.1

### 8.2 下一步行动

1. **立即执行**: 更新 visibility 枚举值
2. **短期执行**: 为所有 skills 添加新字段
3. **验证提交**: 运行验证脚本并提交

---

**文档状态**: 待执行  
**预计完成时间**: 1-2 天  
**阻塞项**: 无
