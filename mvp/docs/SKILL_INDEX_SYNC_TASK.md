# 协作任务: skill-index 系统技能同步

## 背景

在 `http://localhost:8084/console/pages/my-capabilities.html` 页面中,显示总技能数 105,但已安装数量为 0. 

## 问题分析

### 数据来源检查

1. **已安装技能注册表** (`./data/installed-skills/registry.properties`):
   ```
   skill-llm.id=skill-llm
   skill-management.id=skill-management
   skill-capability.id=skill-capability
   ```

2. **skill-index 中的技能定义**:
   - `skill-llm` - 不存在
   - `skill-management` - 不存在
   - `skill-capability` - 不存在

### 根本原因

注册表中的技能ID与skill-index中的技能ID不匹配。

| 注册表中的技能ID | skill-index中是否存在 |
|---|---|
| `skill-llm` | 不存在 |
| `skill-management` | 不存在 |
| `skill-capability` | 不存在 |

## 需要的操作

### 任务 1: 更新 skill-index/skills/sys.yaml

在 `e:\github\ooder-skills\skill-index\skills\sys.yaml` 文件的 `skills:` 列表开头添加以下系统技能定义:

