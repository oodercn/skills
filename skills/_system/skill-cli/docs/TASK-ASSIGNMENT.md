# Agent SDK CLI - 可行性分析与任务分工

## 文档信息
- **创建日期**: 2026-04-16
- **开发目录**: `E:\apex\apexos\.ooder\dev\_system\skill-cli`
- **设计文档**: `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\docs\DESIGN-SUMMARY.md`

---

## 一、可行性分析

### 1.1 架构兼容性 ✅

| 评估项 | 状态 | 说明 |
|--------|------|------|
| Skills 框架兼容 | ✅ 完全兼容 | 设计文档已考虑 Skills 框架集成 |
| Ooder SPI 兼容 | ✅ 完全兼容 | 驱动层接口与 ooder-spi-core 设计一致 |
| Spring Boot 集成 | ✅ 支持 | 提供 Spring Boot Starter |
| 可视化组件 | ✅ 支持 | Ooder UI 组件映射已定义 |

### 1.2 技术可行性

```
┌─────────────────────────────────────────────────────────────────┐
│                    技术栈评估                                    │
├─────────────────────────────────────────────────────────────────┤
│  后端技术                                                        │
│  ├── Java 21 ✅         - 与现有项目一致                          │
│  ├── Spring Boot 3.4 ✅ - 与现有项目一致                          │
│  ├── ooder-spi-core ✅  - 已有 SPI 基础设施                       │
│  └── skill-common ✅    - 已有公共组件                            │
│                                                                 │
│  前端技术                                                        │
│  ├── Ooder UI ✅        - 已有组件库                              │
│  ├── ECharts ✅         - 已集成                                  │
│  └── WebSocket ✅       - 已支持                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 风险评估

| 风险项 | 级别 | 缓解措施 |
|--------|------|----------|
| 驱动层实现复杂度 | 中 | 分阶段实现，先 Mock 后真实 |
| 可视化组件开发量 | 中 | 复用现有 Ooder UI 组件 |
| 与 Agent SDK 集成 | 低 | 设计文档已定义适配器模式 |

### 1.4 结论

**可行性评估：✅ 高度可行**

设计文档完整，架构清晰，与现有技术栈完全兼容。建议采用 Skills 模式开发，分阶段实施。

---

## 二、开发工程结构

```
E:\apex\apexos\.ooder\dev\_system\skill-cli\
├── pom.xml                           # Maven 配置
├── skill.yaml                        # Skill 定义
├── docs/                             # 文档目录
│   └── TASK-ASSIGNMENT.md            # 本文档
│
└── src\main\
    ├── java\net\ooder\skill\cli\
    │   ├── api\                      # API 接口定义
    │   │   ├── SkillCliExtension.java
    │   │   ├── CliErrorCode.java
    │   │   └── CliException.java
    │   │
    │   ├── config\                   # 配置类
    │   │   ├── CliAutoConfiguration.java
    │   │   └── CliProperties.java
    │   │
    │   ├── core\                     # 核心实现
    │   │   ├── CliExecutor.java
    │   │   └── CliRegistry.java
    │   │
    │   ├── adapter\                  # 适配器
    │   │   ├── SkillsFrameworkAdapter.java
    │   │   └── AgentSdkAdapter.java
    │   │
    │   ├── command\                  # 命令处理
    │   │   ├── skill\
    │   │   │   └── SkillExecCommand.java
    │   │   ├── scene\
    │   │   │   └── SceneCommand.java
    │   │   └── task\
    │   │       └── TaskCommand.java
    │   │
    │   ├── driver\                   # 驱动层
    │   │   ├── SkillDriver.java
    │   │   ├── SceneDriver.java
    │   │   ├── TaskDriver.java
    │   │   └── ConfigDriver.java
    │   │
    │   ├── service\                  # 应用服务
    │   │   ├── SkillAppService.java
    │   │   ├── SceneAppService.java
    │   │   ├── TaskAppService.java
    │   │   └── ConfigAppService.java
    │   │
    │   ├── model\                    # 领域模型
    │   │   ├── SkillEntity.java
    │   │   ├── SceneEntity.java
    │   │   ├── TaskEntity.java
    │   │   └── ConfigEntity.java
    │   │
    │   ├── dto\                      # 数据传输对象
    │   │   ├── SkillInfoDTO.java
    │   │   ├── SceneInfoDTO.java
    │   │   └── TaskInfoDTO.java
    │   │
    │   └── event\                    # 领域事件
    │       ├── SkillStatusChangedEvent.java
    │       ├── TaskCompletedEvent.java
    │       └── ConfigChangedEvent.java
    │
    └── resources\
        ├── META-INF\
        │   └── spring.factories      # Spring Boot 自动配置
        │
        └── static\console\components\  # 可视化组件
            ├── SkillPanel.class.js
            ├── SceneCanvas.class.js
            ├── TaskMonitor.class.js
            ├── LogViewer.class.js
            ├── CommandInput.class.js
            └── Dashboard.html
```

---

## 三、任务分工

### Phase 4: 驱动层实现 (P1 - 高优先级)

| 任务ID | 任务名称 | 负责模块 | 工作量 | 依赖 |
|--------|----------|----------|--------|------|
| P4-001 | SkillDriver 接口定义 | driver | 2h | 无 |
| P4-002 | SceneDriver 接口定义 | driver | 2h | 无 |
| P4-003 | TaskDriver 接口定义 | driver | 2h | 无 |
| P4-004 | ConfigDriver 接口定义 | driver | 1h | 无 |
| P4-005 | SkillsFrameworkDriverImpl | adapter | 4h | P4-001~004 |
| P4-006 | AgentSdkDriverImpl | adapter | 4h | P4-001~004 |
| P4-007 | MockDriverImpl | adapter | 2h | P4-001~004 |
| P4-008 | DriverFactory | driver | 2h | P4-005~007 |

**Phase 4 预计工作量**: 19小时

### Phase 5: 可视化实现 (P2 - 中优先级)

| 任务ID | 任务名称 | 负责模块 | 工作量 | 依赖 |
|--------|----------|----------|--------|------|
| P5-001 | CLI.SkillPanel 组件 | components | 4h | Phase 4 |
| P5-002 | CLI.SceneCanvas 组件 | components | 6h | Phase 4 |
| P5-003 | CLI.TaskMonitor 组件 | components | 4h | Phase 4 |
| P5-004 | CLI.LogViewer 组件 | components | 3h | Phase 4 |
| P5-005 | CLI.CommandInput 组件 | components | 2h | Phase 4 |
| P5-006 | CLI.ResultPanel 组件 | components | 2h | Phase 4 |
| P5-007 | CLI.ConfigEditor 组件 | components | 3h | Phase 4 |
| P5-008 | CLI.Dashboard 主页面 | components | 4h | P5-001~007 |
| P5-009 | WebSocket 实时推送 | service | 3h | Phase 4 |

**Phase 5 预计工作量**: 31小时

### Phase 6: 测试与优化 (P2 - 中优先级)

| 任务ID | 任务名称 | 负责模块 | 工作量 | 依赖 |
|--------|----------|----------|--------|------|
| P6-001 | 驱动层单元测试 | test | 4h | Phase 4 |
| P6-002 | 应用服务测试 | test | 4h | Phase 4 |
| P6-003 | 可视化组件测试 | test | 3h | Phase 5 |
| P6-004 | 集成测试 | test | 4h | Phase 4, 5 |
| P6-005 | 性能优化 | core | 4h | P6-001~004 |
| P6-006 | 文档完善 | docs | 2h | 全部 |

**Phase 6 预计工作量**: 21小时

---

## 四、开发计划

### 4.1 里程碑

```
Week 1: Phase 4 驱动层实现
├── Day 1-2: 接口定义 (P4-001~004)
├── Day 3-4: 驱动实现 (P4-005~007)
└── Day 5: 工厂和集成 (P4-008)

Week 2: Phase 5 可视化实现
├── Day 1-2: 核心组件 (P5-001~003)
├── Day 3-4: 辅助组件 (P5-004~007)
└── Day 5: Dashboard 和 WebSocket (P5-008~009)

Week 3: Phase 6 测试与优化
├── Day 1-2: 单元测试 (P6-001~003)
├── Day 3: 集成测试 (P6-004)
├── Day 4: 性能优化 (P6-005)
└── Day 5: 文档完善 (P6-006)
```

### 4.2 总工作量

| Phase | 工作量 | 占比 |
|-------|--------|------|
| Phase 4 | 19h | 27% |
| Phase 5 | 31h | 44% |
| Phase 6 | 21h | 29% |
| **总计** | **71h** | **100%** |

---

## 五、接口定义速查

### 5.1 SkillCliExtension

```java
public interface SkillCliExtension {
    String getSkillId();
    String getCommand();
    String getDescription();
    CliResult execute(String[] args, SceneContext context);
}
```

### 5.2 SkillDriver

```java
public interface SkillDriver {
    SkillEntity install(String source, Map<String, Object> config);
    UninstallResult uninstall(String skillId, boolean force);
    StartResult start(String skillId, Map<String, Object> params);
    StopResult stop(String skillId, boolean force);
    List<SkillEntity> getAllSkills();
    SkillEntity getSkill(String skillId);
    SkillStatus getStatus(String skillId);
    Object invoke(String skillId, String capabilityId, Map<String, Object> params);
}
```

### 5.3 SkillAppService

```java
public interface SkillAppService {
    List<SkillEntity> getAllSkills();
    SkillEntity getSkill(String skillId);
    InstallResult installSkill(InstallRequest request);
    InvokeResult invokeCapability(String skillId, String capabilityId, 
                                   Map<String, Object> params);
}
```

---

## 六、相关文件路径

| 文件类型 | 绝对路径 |
|----------|----------|
| 开发工程 | `E:\apex\apexos\.ooder\dev\_system\skill-cli` |
| 设计文档 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\docs\DESIGN-SUMMARY.md` |
| 应用层设计 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\docs\APPLICATION-LAYER-DESIGN.md` |
| 驱动层接口 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\docs\DRIVER-LAYER-INTERFACE.md` |
| 可视化设计 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\docs\VISUALIZATION-DESIGN.md` |
| ooder-spi-core | `E:\apex\apexos\.ooder\dev\_base\ooder-spi-core` |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-04-16  
**维护团队**: ApexOS 开发团队
