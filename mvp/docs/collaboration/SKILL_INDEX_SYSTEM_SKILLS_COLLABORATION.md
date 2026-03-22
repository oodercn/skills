# 协作任务: skill-index 添加系统技能定义

## 任务编号
COLLAB-2026-003

## 优先级
P0 - 高优先级

## 状态
待处理

## 创建时间
2026-03-22

## 问题描述

### 背景
在 `my-capabilities.html` 页面中，显示总技能数 105，但已安装数量为 0。

### 根本原因
注册表 `./data/installed-skills/registry.properties` 中的技能ID与 `skill-index` 目录中的技能定义不匹配。

**注册表中已安装的技能**:
```
skill-llm.id=skill-llm
skill-management.id=skill-management
skill-capability.id=skill-capability
```

**skill-index 中缺少这些技能的定义**:
- `skill-llm` - 不存在
- `skill-management` - 不存在  
- `skill-capability` - 不存在

## 需要的操作

### 任务 1: 更新 skill-index/skills/sys.yaml

在 `e:\github\ooder-skills\skill-index\skills\sys.yaml` 文件的 `skills:` 列表开头添加以下系统技能定义:

```yaml
    - skillId: skill-common
      name: 通用工具库
      version: "2.3.1"
      capabilityCategory: sys
      subCategory: common
      tags:
        - common
        - auth
        - org
        - config
        - storage
      description: 通用工具库 - 提供认证、组织、配置等核心 API
      sceneId: common
      path: skills/_system/skill-common
      installed: true

    - skillId: skill-llm
      name: LLM基础服务
      version: "2.3.1"
      capabilityCategory: llm
      subCategory: base
      tags:
        - llm
        - base
        - provider
        - chat
      description: LLM基础服务 - 提供LLM Provider注册、配置管理、对话能力
      sceneId: llm-base
      path: skills/_drivers/llm/skill-llm-base
      installed: true

    - skillId: skill-capability
      name: 能力管理服务
      version: "2.3.1"
      capabilityCategory: sys
      subCategory: capability
      tags:
        - capability
        - management
        - register
        - discover
      description: 能力管理服务 - 提供能力注册、发现、绑定管理
      sceneId: capability
      path: skills/_system/skill-capability
      installed: true

    - skillId: skill-management
      name: 技能管理服务
      version: "2.3.1"
      capabilityCategory: sys
      subCategory: management
      tags:
        - skill
        - management
        - lifecycle
        - marketplace
      description: 技能管理服务 - 提供技能注册、生命周期、市场管理
      sceneId: skill-mgmt
      path: skills/_system/skill-management
      installed: true
```

### 任务 2: 更新 metadata.count

将 `metadata.count` 从 `10` 更新为 `14` (新增 4 个系统技能)

### 任务 3: 移除 builtInExcluded 配置

删除以下配置，因为现在这些技能需要在索引中显示:
```yaml
  builtInExcluded:
    - skill-capability
    - skill-scene-management
```

## 验证步骤

1. 更新 `sys.yaml` 文件后，调用刷新接口:
   ```
   POST /api/v1/discovery/refresh
   ```

2. 访问 `http://localhost:8084/console/pages/my-capabilities.html`

3. 验证:
   - 已安装数量应该显示为 3 或更多
   - `skill-llm`, `skill-management`, `skill-capability` 应该显示为已安装状态

## 相关文件

- `e:\github\ooder-skills\skill-index\skills\sys.yaml` - 需要修改
- `e:\github\ooder-skills\mvp\data\installed-skills\registry.properties` - 已安装技能注册表
- `e:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\discovery\MvpSkillIndexLoader.java` - 技能索引加载器

## 技能实际位置

| 技能ID | 实际路径 |
|---|---|
| skill-common | `skills/_system/skill-common/` |
| skill-llm | `skills/_drivers/llm/skill-llm-base/` |
| skill-capability | `skills/_system/skill-capability/` (需要确认) |
| skill-management | `skills/_system/skill-management/` |

## 注意事项

1. `skill-capability` 的实际路径需要确认是否存在
2. 如果技能目录不存在，需要创建或调整路径
3. 更新后需要重启服务或调用刷新接口

## 完成标准

- [ ] sys.yaml 已更新
- [ ] metadata.count 已更新
- [ ] builtInExcluded 已移除
- [ ] 刷新接口调用成功
- [ ] my-capabilities 页面显示正确的已安装数量
