# Skill Scene v2.3 更新日志

## 发布日期
2026-03-05

## 主要更新

### 1. 场景能力模块重构

#### 新增页面
- **场景能力列表页** (`scene-capabilities.html`)
  - 卡片网格展示场景能力
  - 分类筛选和搜索功能
  - 一键安装/使用操作

- **场景能力详情页** (`scene-capability-detail.html`)
  - 功能介绍、使用指南、依赖技能、配置选项、更新日志
  - 安装/使用/卸载操作

- **已安装场景能力页** (`installed-scene-capabilities.html`)
  - 展示已安装的场景能力包
  - 区分场景能力和技能能力

#### 菜单结构调整
```
🎯 场景能力
   ├── 场景能力列表      → scene-capabilities.html
   └── 已安装场景能力    → installed-scene-capabilities.html

👤 我的工作台
   ├── 我的待办         → my-todos.html
   ├── 我的场景         → my-scenes.html
   └── 已完成场景       → my-history.html
```

### 2. 场景能力定义优化

#### 能力分层架构
| 层级 | 名称 | 定义 | 面向用户 |
|------|------|------|----------|
| 第一层 | 场景能力 | 开箱即用 + 用户交互UI + 导航菜单入口 | 终端用户 |
| 第二层 | 技能能力 | 底层能力组件，提供原子化功能 | 开发者 |
| 第三层 | 基础服务 | 系统级基础设施服务 | 运维 |

#### 场景能力判断逻辑
- 根据 `category` 的 `sceneDriver` 字段判断
- `sceneDriver` 不为 null → 场景能力
- `sceneDriver: null` → 非场景能力

### 3. 数据逻辑优化

#### 已安装状态检查
- 新增 `SkillPackageManager.isInstalled()` 检查
- 动态获取技能安装状态
- 区分 `installed` 和 `available` 状态

#### 测试数据修复
- 修复 `creatorId` 与当前用户 ID 不匹配问题
- 确保"我的场景"页面正确显示数据

### 4. API 优化

#### 新增方法
- `SkillIndexLoader.getSkillInfo(skillId)` - 获取技能信息
- `SkillIndexLoader.getDownloadUrl(skillId)` - 获取下载 URL
- `SkillIndexLoader.checkIfInstalled(skillId)` - 检查安装状态

#### 修复问题
- 修复 API 方法调用（GET → POST）
- 修复 `my-scenes.js` 中的 API 端点

### 5. 协同任务文档

#### SDK 安装功能增强
- 创建协同任务文档 `SDK_COLLABORATION_INSTALL_FROM_URL.md`
- 定义 `installFromUrl()` 接口规范
- 明确任务分工和时间安排

## 技术改进

### 前端
- 统一使用 POST 方法调用 `/api/v1/discovery/gitee`
- 优化页面加载和错误处理
- 改进用户体验和交互设计

### 后端
- 注入 `SkillPackageManager` 到 `SkillIndexLoader`
- 动态检查技能安装状态
- 优化数据返回结构

## 修复问题

1. **场景能力页面无数据** - 修复 API 方法调用
2. **我的场景页面无数据** - 修复 `creatorId` 匹配问题
3. **菜单命名冲突** - 区分"已安装场景能力"和"我的场景"

## 已知问题

1. **安装功能** - 需要 SDK 团队支持 `installFromUrl()` 方法
2. **已安装状态** - 当前没有实际安装的技能包

## 下一步计划

1. 完善"我的待办"功能
2. 实现场景启动和执行流程
3. 添加能力统计数据展示
4. 优化安装流程用户体验

---

**版本**: 2.3  
**发布状态**: 已发布  
**兼容性**: 向后兼容
