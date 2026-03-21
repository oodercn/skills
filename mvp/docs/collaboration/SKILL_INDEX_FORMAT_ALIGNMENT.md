# Skills 团队协作请求：skill-index.yaml 格式对齐

> **文档版本**: v2.3.1  
> **更新日期**: 2026-03-21  
> **状态**: 已升级为正式协作申请  
> **详细文档**: [SE_SDK_INCLUDES_SUPPORT_REQUEST.md](./SE_SDK_INCLUDES_SUPPORT_REQUEST.md)

---

## 一、问题描述

### 1.1 现象
MVP 项目集成 SE SDK 2.3.1 后，Gitee 发现功能始终返回 0 个技能。

### 1.2 根本原因

**`skill-index.yaml` 格式不兼容**

**SE SDK 期望的格式**（直接的 `skills` 列表）：
```yaml
skills:
  - id: skill-network
    name: 网络管理服务
    category: sys
    version: "2.3.1"
  - id: skill-security
    name: 安全管理服务
    category: sys
    version: "2.3.1"
```

**Gitee 仓库实际格式**（使用 `includes` 引用其他文件）：
```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"

spec:
  includes:
    - categories.yaml
    - scene-drivers.yaml
    - skills/*.yaml
    - scenes/*.yaml
  
  statistics:
    totalSkills: 63
    totalScenes: 50
```

---

## 二、解决方案

### ✅ 方案 B：SE SDK 完全支持 includes 格式（推荐）

**详细协作申请已提交**: [SE_SDK_INCLUDES_SUPPORT_REQUEST.md](./SE_SDK_INCLUDES_SUPPORT_REQUEST.md)

SE SDK 需要实现 `includes` 字段解析功能，自动加载引用的文件。

**理由**:
1. 符合 v2.3.1 标准格式规范
2. 支持模块化文件组织
3. 一次实现，永久解决
4. 本地加载器已支持目录结构，远程发现应统一

---

## 三、临时方案（不推荐）

### 方案 A：在 skill-index.yaml 中添加 skills 字段

在保留现有 `includes` 结构的同时，添加 SE SDK 兼容的 `skills` 字段。

**缺点**:
1. 文件冗余，维护成本高
2. 容易出现不一致
3. 治标不治本

---

## 四、验证方式

### 4.1 修改后验证

```bash
# 检查索引文件包含 skills 字段
curl -s "https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml" | grep -A 5 "skills:"
```

### 4.2 MVP 集成测试

1. 清除缓存：删除 `.ooder/cache/discovery/` 目录
2. 重启 MVP 服务
3. 访问能力发现页面
4. 选择 Gitee 发现
5. 确认返回技能列表不为空

---

## 五、相关文档

- **[SE SDK Includes 支持协作申请](./SE_SDK_INCLUDES_SUPPORT_REQUEST.md)** - 详细技术方案
- [SE SDK UnifiedDiscoveryServiceImpl.java](file:///E:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/discovery/impl/UnifiedDiscoveryServiceImpl.java)
- [Gitee skill-index.yaml](https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml)
- [MVP Gitee 发现集成文档](file:///e:/github/ooder-skills/mvp/docs/features/GITEE_DISCOVERY_INTEGRATION.md)

---

**创建时间**: 2026-03-21  
**状态**: 已升级为正式协作申请  
**指派**: SE SDK 团队
