# LLM 任务委派支持说明

## 一、委派概述

本文档说明如何将 Ooder Agent SDK v0.8.0 的开发任务委派给 LLM 协助完成。

---

## 二、委派任务清单

| 任务ID | 任务名称 | 优先级 | 委派类型 | 预计工作量 |
|--------|----------|--------|----------|------------|
| WILL | 意志表达模型 | P0 | 架构设计 | 6人天 |
| ASSET | 数字资产治理 | P1 | 架构设计 | 8人天 |
| REACH | LLM 触达能力 | P1 | 架构设计 | 6人天 |
| IMPL | 业务逻辑实现 | P1 | 代码实现 | 15人天 |
| SKILL-MD | SKILL.md 直接解析支持 | P1 | 架构设计 + 代码实现 | 12人天 |

---

## 三、委派方式

### 3.1 架构设计类任务

**适用任务**：WILL、ASSET、REACH、SKILL-MD

**委派方式**：
1. 提供任务背景和需求描述
2. 提供参考文档和协议规范
3. LLM 输出架构设计和接口定义
4. 人工审核和调整

**输入格式**：
```
任务：{任务名称}
背景：{背景说明}
需求：{详细需求}
参考文档：{参考文档路径}
期望输出：{期望输出内容}
```

**输出格式**：
```java
// 接口定义
public interface XxxApi {
    // 方法定义
}

// 模型定义
public class XxxModel {
    // 属性定义
}
```

### 3.2 代码实现类任务

**适用任务**：IMPL

**委派方式**：
1. 提供接口定义和业务逻辑说明
2. 提供现有代码参考
3. LLM 输出实现代码
4. 人工审核和测试

**输入格式**：
```
任务：{任务名称}
接口定义：{接口代码}
业务逻辑：{业务逻辑说明}
参考代码：{参考代码路径}
期望输出：{期望输出内容}
```

**输出格式**：
```java
// 实现类
public class XxxApiImpl implements XxxApi {
    // 实现代码
}

// 单元测试
public class XxxApiTest {
    // 测试代码
}
```

---

## 四、各任务委派详情

### 4.1 WILL：意志表达模型

**委派类型**：架构设计

**输入材料**：
```
任务：意志表达模型设计
背景：需要支持管理者意志的解析和执行，意志分为战略/战术/执行三个层次
需求：
1. 定义意志表达模型接口
2. 实现意志解析器（语义理解、意图推理、约束识别、优先级判断）
3. 实现意志转化器（目标分解、方案生成、资源规划、风险评估）
4. 实现意志执行监控（任务分配、执行监控、效果评估、反馈调整）
5. 与 SceneAgent 决策机制的集成

参考文档：
- E:\github\super-Agent\protocol-release\v0.8.0\northbound\northbound-protocol-spec.md
- e:\github\ooder-skills\docs\v0.8.0\ARCHITECTURE-V0.8.0.md

期望输出：
1. WillExpression 接口定义
2. WillParser 接口定义
3. WillTransformer 接口定义
4. WillExecutor 接口定义
5. WillManager 接口定义
6. 实现示例代码
```

**输出要求**：
- Java 接口定义
- Javadoc 注释
- 实现示例代码
- 与现有架构的集成说明

---

### 4.2 ASSET：数字资产治理

**委派类型**：架构设计

**输入材料**：
```
任务：数字资产治理设计
背景：需要设计数字资产治理体系，与 Place/Zone/Device 体系对应
需求：
1. 定义数字资产分类（设备/数据/Agent/资源）
2. 实现设备资产管理（与 Place/Zone/Device 对应）
3. 实现数据资产管理
4. 实现 Agent 资产管理
5. 实现资源资产管理
6. 实现资产治理接口

参考文档：
- E:\github\super-Agent\protocol-release\v0.8.0\digital-asset\digital-asset-governance.md
- e:\github\ooder-skills\docs\v0.8.0\ARCHITECTURE-V0.8.0.md

期望输出：
1. DigitalAsset 接口定义
2. AssetGovernance 接口定义
3. 各类型资产管理接口
4. 与 Place/Zone/Device 的映射关系
```

**输出要求**：
- Java 接口定义
- 资产分类模型
- 与现有架构的集成说明

---

### 4.3 REACH：LLM 触达能力

**委派类型**：架构设计

**输入材料**：
```
任务：LLM 触达能力设计
背景：需要实现 LLM 对物理设备和虚拟资源的直接操作能力
需求：
1. 定义触达协议（REACH://device_type/device_id/action?params）
2. 实现物理设备触达（路由器/交换机/防火墙/摄像头/传感器）
3. 实现虚拟资源触达（数据库/文件系统/API/消息队列）
4. 实现触达安全机制（认证/授权/审计）
5. 与 Command 体系的集成

参考文档：
- E:\github\super-Agent\protocol-release\v0.8.0\northbound\northbound-protocol-spec.md
- e:\github\ooder-skills\docs\v0.8.0\CAP-REGISTRY-SPEC.md

期望输出：
1. ReachProtocol 接口定义
2. ReachExecutor 接口定义
3. 各类型触达执行器接口
4. 安全机制设计
```

**输出要求**：
- Java 接口定义
- 触达协议格式
- 安全机制设计

---

### 4.4 IMPL：业务逻辑实现

**委派类型**：代码实现

**输入材料**：
```
任务：LLM-SDK 业务逻辑实现
背景：LLM-SDK 已定义接口，需要实现具体业务逻辑
需求：
1. MemoryBridgeApi 实现
2. NlpInteractionApi 实现
3. SchedulingApi 实现
4. SecurityApi 实现
5. MonitoringApi 实现
6. MultiLlmAdapterApi 实现
7. CapabilityRequestApi 实现

参考代码：
- E:\github\ooder-sdk\llm-sdk\src\main\java\net\ooder\llm\sdk\api\*.java

期望输出：
1. 各接口的实现类
2. 业务逻辑处理
3. 异常处理
4. 日志记录
5. 单元测试
```

**输出要求**：
- Java 实现类
- 单元测试
- 异常处理
- 日志记录

---

### 4.5 SKILL-MD：SKILL.md 直接解析支持

**委派类型**：架构设计 + 代码实现

**输入材料**：
```
任务：SKILL.md 直接解析支持
背景：业界主流 AI-IDE 已采用 Agent Skills 开放标准，需要兼容这一标准
需求：
1. 定义 SKILL.md 标准格式
2. 实现 SkillMdParser（解析 SKILL.md 文件）
3. 实现 SkillMdRegistry（注册和发现 SKILL.md 技能）
4. 实现 SkillExecutionEngine（执行 SKILL.md 技能）
5. 实现 SkillRouter（路由到不同类型的技能）
6. 与现有架构的集成

参考文档：
- Agent Skills 开放标准：https://agentskills.io
- e:\github\ooder-skills\docs\v0.8.0\ARCHITECTURE-V0.8.0.md

期望输出：
1. SKILL.md 标准格式文档
2. SkillMdParser 接口和实现
3. SkillMdRegistry 接口和实现
4. SkillExecutionEngine 接口和实现
5. SkillRouter 接口和实现
6. 单元测试
7. 使用示例文档
```

**输出要求**：
- SKILL.md 标准格式
- Java 接口和实现
- 单元测试
- 使用示例

---

## 五、委派流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    委派流程                                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 准备输入材料                                                │
│  ├── 整理任务背景和需求                                        │
│  ├── 收集参考文档                                              │
│  └── 明确期望输出                                              │
│                                                                 │
│  2. 委派给 LLM                                                  │
│  ├── 提供完整的输入材料                                        │
│  ├── 说明约束条件                                              │
│  └── 指定输出格式                                              │
│                                                                 │
│  3. LLM 输出                                                    │
│  ├── 架构设计：接口定义、模型定义、集成说明                    │
│  └── 代码实现：实现类、单元测试、异常处理                      │
│                                                                 │
│  4. 人工审核                                                    │
│  ├── 检查设计合理性                                            │
│  ├── 检查代码质量                                              │
│  ├── 检查与现有架构的兼容性                                    │
│  └── 提出修改意见                                              │
│                                                                 │
│  5. 迭代优化                                                    │
│  ├── 根据审核意见修改                                          │
│  ├── 补充缺失内容                                              │
│  └── 完善文档                                                  │
│                                                                 │
│  6. 验证测试                                                    │
│  ├── 编译通过                                                  │
│  ├── 单元测试通过                                              │
│  └── 集成测试通过                                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 六、约束条件

### 6.1 技术约束

| 约束 | 说明 |
|------|------|
| Java 版本 | Java 8+ |
| 构建工具 | Maven |
| 日志框架 | SLF4J |
| 单元测试 | JUnit 5 |
| 代码规范 | Google Java Style |

### 6.2 架构约束

| 约束 | 说明 |
|------|------|
| 包结构 | net.ooder.sdk.{module}.* |
| 接口定义 | 先定义接口，再实现 |
| 模型类 | 不可变对象，使用 Builder 模式 |
| 异常处理 | 自定义异常，不使用 RuntimeException |

### 6.3 文档约束

| 约束 | 说明 |
|------|------|
| 代码注释 | Javadoc 格式 |
| README | 每个模块需要 README.md |
| API 文档 | 使用 Swagger 注解 |

---

## 七、验收标准

### 7.1 架构设计验收

- [ ] 接口定义完整
- [ ] 模型定义合理
- [ ] 与现有架构兼容
- [ ] 文档清晰完整

### 7.2 代码实现验收

- [ ] 编译通过
- [ ] 单元测试覆盖率 > 80%
- [ ] 代码通过 Checkstyle 检查
- [ ] 代码通过 FindBugs 检查
- [ ] 日志记录完整
- [ ] 异常处理合理

---

## 八、时间安排

| 阶段 | 时间 | 任务 |
|------|------|------|
| 第1周 | 2026-02-24 ~ 2026-03-02 | WILL 意志表达模型设计 |
| 第2周 | 2026-03-03 ~ 2026-03-09 | ASSET 数字资产治理设计 |
| 第3周 | 2026-03-10 ~ 2026-03-16 | REACH LLM 触达能力设计 |
| 第4周 | 2026-03-17 ~ 2026-03-23 | SKILL-MD 直接解析支持 |
| 第5-6周 | 2026-03-24 ~ 2026-04-06 | IMPL 业务逻辑实现 + 测试文档 |

---

## 九、联系方式

如有问题，请通过以下方式联系：
- 项目仓库：https://github.com/ooder/ooder-skills
- 问题反馈：GitHub Issues

---

**文档版本**：v1.0  
**创建日期**：2026-02-24  
**最后更新**：2026-02-24
