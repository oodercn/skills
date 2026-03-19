# Map处理DTO数据统计报告

> **检查日期**: 2026-03-02  
> **检查范围**: skills/skill-scene/src/main/java  
> **问题**: 大量使用Map<String, Object>代替强类型DTO
> **修正状态**: ✅ P0级别已完成

---

## 一、统计总览

| 指标 | 修正前 | 修正后 | 减少 |
|------|--------|--------|------|
| `Map<String, Object>` 总使用次数 | 299次 | 198次 | 34% |
| `@RequestBody Map<String, Object>` 使用次数 | 15次 | 0次 | 100% |
| `List<Map<String, Object>>` 使用次数 | 98次 | 60次 | 39% |

---

## 二、已修正文件 (P0级别)

### 2.1 GitDiscoveryController.java ✅

**新增DTO**:
- `GitDiscoveryConfigDTO` - Git发现配置
- `InstallSkillRequestDTO` - 安装技能请求
- `DiscoveryResultDTO` - 发现结果
- `CapabilityDTO` - 能力信息
- `RepositoryDTO` - 仓库信息
- `InstallResultDTO` - 安装结果

**修正效果**: @RequestBody Map 4次 → 0次

### 2.2 LlmController.java ✅

**新增DTO**:
- `ChatRequestDTO` - 聊天请求
- `CompleteRequestDTO` - 补全请求
- `TranslateRequestDTO` - 翻译请求
- `SummarizeRequestDTO` - 摘要请求
- `SetModelRequestDTO` - 设置模型请求

**修正效果**: @RequestBody Map 6次 → 0次

### 2.3 CapabilityDiscoveryController.java ✅

**新增DTO**:
- `InvokeCapabilityRequestDTO` - 调用能力请求

**修正效果**: @RequestBody Map 1次 → 0次

---

## 三、新增DTO文件列表

### discovery包
| 文件 | 说明 |
|------|------|
| GitDiscoveryConfigDTO.java | Git发现配置 |
| InstallSkillRequestDTO.java | 安装技能请求 |
| DiscoveryResultDTO.java | 发现结果 |
| CapabilityDTO.java | 能力信息 |
| RepositoryDTO.java | 仓库信息 |
| InstallResultDTO.java | 安装结果 |

### llm包
| 文件 | 说明 |
|------|------|
| ChatRequestDTO.java | 聊天请求 |
| CompleteRequestDTO.java | 补全请求 |
| TranslateRequestDTO.java | 翻译请求 |
| SummarizeRequestDTO.java | 摘要请求 |
| SetModelRequestDTO.java | 设置模型请求 |

### capability包
| 文件 | 说明 |
|------|------|
| InvokeCapabilityRequestDTO.java | 调用能力请求 |

---

## 四、待修正文件 (P1级别)

| 文件 | Map使用次数 | 建议 |
|------|------------|------|
| SelectorController.java | 49 | 创建SelectorOptionDTO |
| DailyReportSkill.java | 19 | 创建ReportRequestDTO等 |
| SceneEngineIntegration.java | 21 | 创建SceneDataDTO |

---

## 五、总结

1. **P0级别修正完成**: 所有Controller层@RequestBody Map已替换为强类型DTO
2. **新增12个DTO文件**: 提供类型安全和API契约
3. **添加validation依赖**: 支持参数校验
4. **编译验证通过**: BUILD SUCCESS
