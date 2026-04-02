# OS页面迁移深度分析报告

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**分析范围**: OS中的76个HTML页面、页面依赖关系、迁移策略  
**目的**: 分析页面关联、依赖关系、制定迁移方案

---

## 📊 一、页面总体统计

### 1.1 页面分类统计

| 分类 | 数量 | 说明 |
|------|------|------|
| **能力管理** | 11个 | 能力发现、管理、激活、绑定等 |
| **场景管理** | 7个 | 场景创建、详情、能力绑定等 |
| **系统配置** | 15个 | 系统配置、认证、组织、LLM、VFS等 |
| **知识库** | 5个 | 知识中心、知识库管理、搜索等 |
| **Agent管理** | 8个 | Agent列表、详情、注册、监控等 |
| **组织管理** | 6个 | 组织管理、角色管理等 |
| **模板管理** | 4个 | 模板管理、融合模板等 |
| **流程管理** | 4个 | 执行管理、企业流程等 |
| **用户工作台** | 9个 | 工作台、个人中心、我的场景等 |
| **其他** | 7个 | 技能配置、网络审批等 |
| **总计** | **76个** | - |

---

## 🔗 二、页面依赖关系分析

### 2.1 CSS依赖关系

#### 核心CSS框架

```
所有页面共享的CSS:
├── /console/css/remixicon/remixicon.css    # 图标库
├── /console/css/nexus.css                  # 核心UI框架
├── /console/css/nx-page.css                # 页面布局
└── /console/css/components/                # 组件样式
    ├── llm-enhancement.css                 # LLM增强组件
    ├── toast.css                           # 提示框
    ├── modal.css                           # 模态框
    └── ...
```

#### 页面特定CSS

```
/console/css/pages/
├── capability-discovery.css                # 能力发现页面
├── capability-management.css               # 能力管理页面
├── my-capabilities.css                     # 我的能力页面
├── scene-management.css                    # 场景管理页面
└── ...
```

### 2.2 JavaScript依赖关系

#### 核心JS库

```
所有页面共享的JS:
├── /console/js/nexus.js                    # 核心UI框架
├── /console/js/api-client.js               # API客户端
├── /console/js/utils.js                    # 工具函数
└── /console/js/auth.js                     # 认证模块
```

#### 页面特定JS

```
/console/js/pages/
├── capability-discovery.js                 # 能力发现逻辑
├── capability-management.js                # 能力管理逻辑
├── my-capabilities.js                      # 我的能力逻辑
├── scene-management.js                     # 场景管理逻辑
└── ...
```

#### LLM聊天浮动组件

```
/console/js/llm-chat-float/
├── index.js                                # 入口
├── core.js                                 # 核心逻辑
├── chat.js                                 # 聊天功能
├── utils.js                                # 工具函数
├── core/
│   ├── event-bus.js                        # 事件总线
│   ├── websocket-service.js                # WebSocket服务
│   ├── streaming-service.js                # 流式服务
│   └── markdown-service.js                 # Markdown服务
└── components/
    ├── base-window.js                      # 基础窗口
    ├── float-bar.js                        # 浮动栏
    ├── assistant-window.js                 # 助手窗口
    ├── todo-window.js                      # 待办窗口
    ├── im-window.js                        # IM窗口
    ├── mention-popup.js                    # @提及弹窗
    └── emoji-picker.js                     # 表情选择器
```

### 2.3 页面间导航关系

```
导航菜单结构:
├── 工作台 (workbench.html)
│   ├── 我的场景 (my-scenes.html)
│   ├── 我的待办 (my-todos.html)
│   └── 我的历史 (my-history.html)
│
├── 能力中心
│   ├── 发现能力 (capability-discovery.html)
│   ├── 我的能力 (my-capabilities.html)
│   ├── 能力管理 (capability-management.html)
│   └── 能力详情 (capability-detail.html)
│
├── 场景中心
│   ├── 场景管理 (scene-management.html)
│   ├── 场景详情 (scene-detail.html)
│   ├── 场景组管理 (scene-group-management.html)
│   └── 场景组详情 (scene-group-detail.html)
│
├── 知识中心
│   ├── 知识中心 (knowledge-center.html)
│   ├── 知识库管理 (knowledge-base.html)
│   └── 知识搜索 (knowledge-search.html)
│
├── 系统配置
│   ├── 系统配置 (config-system.html)
│   ├── 认证配置 (auth-config.html)
│   ├── 组织配置 (org-config.html)
│   ├── LLM配置 (llm-config.html)
│   ├── VFS配置 (vfs-config.html)
│   └── 安全配置 (security-config.html)
│
└── Agent管理
    ├── Agent列表 (agent-list.html)
    ├── Agent详情 (agent-detail.html)
    └── Agent监控 (agent-monitor.html)
```

---

## 🔌 三、页面与API依赖关系

### 3.1 能力管理页面API依赖

#### capability-discovery.html

```javascript
依赖的API:
├── GET  /api/v1/discovery/methods              # 获取发现方法列表
├── POST /api/v1/discovery/local                # 本地发现
├── POST /api/v1/discovery/github               # GitHub发现
├── POST /api/v1/discovery/gitee                # Gitee发现
├── POST /api/v1/discovery/install              # 安装能力
├── GET  /api/v1/discovery/capability/{id}      # 获取能力详情
├── GET  /api/v1/discovery/categories/user-facing # 获取用户可见分类
└── GET  /api/v1/capabilities                   # 获取能力列表
```

**对应的Skill**: `skill-discovery` (OS的_system中)

#### capability-management.html

```javascript
依赖的API:
├── GET    /api/v1/capabilities                   # 获取能力列表
├── POST   /api/v1/capabilities                   # 注册能力
├── GET    /api/v1/capabilities/{id}              # 获取能力详情
├── PUT    /api/v1/capabilities/{id}              # 更新能力
├── DELETE /api/v1/capabilities/{id}              # 删除能力
├── POST   /api/v1/capabilities/{id}/activate     # 激活能力
├── POST   /api/v1/capabilities/{id}/deactivate   # 停用能力
├── POST   /api/v1/capabilities/{id}/bind         # 绑定能力
├── POST   /api/v1/capabilities/{id}/unbind       # 解绑能力
├── GET    /api/v1/capabilities/{id}/dependencies # 获取依赖关系
└── POST   /api/v1/discovery/capabilities/invoke  # 调用能力
```

**对应的Skill**: `skill-capability` (OS的_system中)

#### my-capabilities.html

```javascript
依赖的API:
├── GET  /api/v1/capabilities                   # 获取能力列表
├── GET  /api/v1/capabilities/{id}              # 获取能力详情
├── POST /api/v1/capabilities/{id}/activate     # 激活能力
└── POST /api/v1/capabilities/{id}/deactivate   # 停用能力
```

**对应的Skill**: `skill-capability` (OS的_system中)

### 3.2 场景管理页面API依赖

#### scene-management.html

```javascript
依赖的API:
├── POST /api/v1/scenes/list                    # 场景列表
├── POST /api/v1/scenes/create                  # 创建场景
├── POST /api/v1/scenes/update                  # 更新场景
├── POST /api/v1/scenes/delete                  # 删除场景
├── GET  /api/v1/scenes/{id}                    # 场景详情
└── GET  /api/v1/scenes/{id}/capabilities       # 场景能力列表
```

**对应的Skill**: `skill-scene` (OS的_system中)

#### scene-group-management.html

```javascript
依赖的API:
├── GET  /api/v1/scene-groups/my/created        # 我创建的场景组
├── GET  /api/v1/scene-groups/my/led            # 我领导的场景组
├── GET  /api/v1/scene-groups/my/participated   # 我参与的场景组
├── POST /api/v1/scene-groups                   # 创建场景组
├── GET  /api/v1/scene-groups/{id}              # 场景组详情
├── PUT  /api/v1/scene-groups/{id}              # 更新场景组
├── DELETE /api/v1/scene-groups/{id}            # 删除场景组
├── POST /api/v1/scene-groups/{id}/activate     # 激活场景组
├── POST /api/v1/scene-groups/{id}/deactivate   # 停用场景组
├── GET  /api/v1/scene-groups/{id}/capabilities # 获取能力列表
├── POST /api/v1/scene-groups/{id}/capabilities # 添加能力
├── DELETE /api/v1/scene-groups/{id}/capabilities/{capId} # 删除能力
├── GET  /api/v1/scene-groups/{id}/participants # 获取参与者
├── POST /api/v1/scene-groups/{id}/participants # 添加参与者
├── PUT  /api/v1/scene-groups/{id}/participants/{participantId}/role # 更新角色
├── DELETE /api/v1/scene-groups/{id}/participants/{participantId} # 删除参与者
├── GET  /api/v1/scene-groups/{id}/snapshots    # 获取快照
├── POST /api/v1/scene-groups/{id}/snapshots    # 创建快照
├── POST /api/v1/scene-groups/{id}/snapshots/{snapshotId}/restore # 恢复快照
├── DELETE /api/v1/scene-groups/{id}/snapshots/{snapshotId} # 删除快照
├── GET  /api/v1/scene-groups/{id}/knowledge    # 获取知识库
├── POST /api/v1/scene-groups/{id}/knowledge    # 添加知识库
├── DELETE /api/v1/scene-groups/{id}/knowledge/{kbId} # 删除知识库
├── GET  /api/v1/scene-groups/{id}/llm/config   # 获取LLM配置
├── POST /api/v1/scene-groups/{id}/llm/config   # 更新LLM配置
├── GET  /api/v1/scene-groups/{id}/event-log    # 获取事件日志
├── POST /api/v1/scene-groups/{id}/workflow/start # 启动工作流
├── POST /api/v1/scene-groups/from-fusion       # 从融合模板创建
├── GET  /api/v1/scene-groups/{id}/capabilities/{capId} # 获取能力详情
├── GET  /api/v1/scene-groups/{id}/knowledge/config # 获取知识配置
├── POST /api/v1/scene-groups/{id}/knowledge/config # 更新知识配置
├── GET  /api/v1/scene-groups/{id}/llm/providers/{providerId}/models # 获取模型列表
├── POST /api/v1/scene-groups/{id}/llm/reset    # 重置LLM配置
├── POST /api/v1/scene-groups/{id}/{action}     # 执行动作
└── GET  /api/v1/scene-groups/my/led/members    # 获取我领导的成员
```

**对应的Skill**: `skill-scenes` (OS的_business中)

### 3.3 系统配置页面API依赖

#### config-system.html

```javascript
依赖的API:
├── GET /api/v1/system/config                  # 获取系统配置
├── PUT /api/v1/system/config                  # 更新系统配置
├── GET /api/v1/system/health                  # 健康检查
└── GET /api/v1/system/check                   # 检查系统
```

**对应的Skill**: `skill-config` (OS的_system中)

#### llm-config.html

```javascript
依赖的API:
├── GET /api/v1/llm/config                     # 获取LLM配置
├── PUT /api/v1/llm/config                     # 更新LLM配置
├── GET /api/v1/llm/providers                  # 获取LLM提供商列表
└── GET /api/v1/llm/models                     # 获取模型列表
```

**对应的Skill**: `skill-llm-config` (OS的_business中)

#### org-config.html

```javascript
依赖的API:
├── GET /api/v1/org/config                     # 获取组织配置
├── PUT /api/v1/org/config                     # 更新组织配置
├── GET /api/v1/org/users                      # 获取用户列表
├── GET /api/v1/org/departments                # 获取部门列表
└── GET /api/v1/org/tree                       # 获取组织树
```

**对应的Skill**: `skill-org` (OS的_system中)

---

## 📦 四、页面与Skill依赖关系

### 4.1 页面与Skill映射表

| 页面 | 依赖的Skill | Skill位置 | 迁移优先级 |
|------|------------|----------|-----------|
| capability-discovery.html | skill-discovery | OS/_system | P0 |
| capability-management.html | skill-capability | OS/_system | P0 |
| my-capabilities.html | skill-capability | OS/_system | P0 |
| scene-management.html | skill-scene | OS/_system | P0 |
| scene-group-management.html | skill-scenes | OS/_business | P0 |
| config-system.html | skill-config | OS/_system | P0 |
| llm-config.html | skill-llm-config | OS/_business | P0 |
| org-config.html | skill-org | OS/_system | P0 |
| knowledge-center.html | skill-knowledge | OS/_business | P0 |
| agent-list.html | skill-agent | OS/_system | P0 |
| workbench.html | skill-common | OS/_system | P0 |
| my-profile.html | skill-auth | OS/_system | P0 |
| my-todos.html | skill-todo | OS/_business | P1 |
| my-history.html | skill-history | OS/_system | P1 |

### 4.2 Skill依赖链

```
页面依赖链:
capability-discovery.html
└── skill-discovery
    └── skill-common
        └── skill-protocol

capability-management.html
└── skill-capability
    ├── skill-common
    ├── skill-auth
    └── skill-org

scene-group-management.html
└── skill-scenes
    ├── skill-common
    ├── skill-capability
    ├── skill-knowledge
    └── skill-llm-config

llm-config.html
└── skill-llm-config
    ├── skill-common
    └── skill-llm-base
```

---

## 🚀 五、页面迁移策略

### 5.1 迁移原则

1. **页面与Skill分离**: 页面保留在主工程，Skill提供API
2. **依赖优先**: 先迁移被依赖的Skill，再迁移依赖它的页面
3. **分组迁移**: 按功能分组迁移，确保功能完整
4. **测试验证**: 每迁移一组，立即测试验证

### 5.2 迁移分组

#### Group 1: 核心基础（P0）

**Skills**:
```
1. skill-common          # 通用服务（所有页面依赖）
2. skill-protocol        # 协议服务
3. skill-auth            # 认证服务
4. skill-config          # 配置服务
```

**页面**:
```
1. login.html            # 登录页面
2. workbench.html        # 工作台
3. my-profile.html       # 个人中心
4. config-system.html    # 系统配置
```

**迁移顺序**:
```
Step 1: 迁移skill-common
Step 2: 迁移skill-protocol
Step 3: 迁移skill-auth
Step 4: 迁移skill-config
Step 5: 复制页面到Skills
Step 6: 测试验证
```

#### Group 2: 能力管理（P0）

**Skills**:
```
1. skill-discovery       # 发现服务
2. skill-capability      # 能力服务
3. skill-install         # 安装服务
```

**页面**:
```
1. capability-discovery.html      # 能力发现
2. capability-management.html     # 能力管理
3. my-capabilities.html           # 我的能力
4. capability-detail.html         # 能力详情
5. capability-activation.html     # 能力激活
6. capability-binding.html        # 能力绑定
```

**迁移顺序**:
```
Step 1: 迁移skill-discovery
Step 2: 迁移skill-capability
Step 3: 迁移skill-install
Step 4: 复制页面到Skills
Step 5: 更新页面API路径
Step 6: 测试验证
```

#### Group 3: 场景管理（P0）

**Skills**:
```
1. skill-scene           # 场景服务
2. skill-scenes          # 场景组服务（包含SceneGroup）
```

**页面**:
```
1. scene-management.html          # 场景管理
2. scene-detail.html              # 场景详情
3. scene-capabilities.html        # 场景能力
4. scene-group-management.html    # 场景组管理
5. scene-group-detail.html        # 场景组详情
```

**迁移顺序**:
```
Step 1: 迁移skill-scene
Step 2: 迁移skill-scenes（包含SceneGroup的80个API）
Step 3: 复制页面到Skills
Step 4: 更新页面API路径
Step 5: 测试验证
```

#### Group 4: 知识库（P0）

**Skills**:
```
1. skill-knowledge       # 知识服务
```

**页面**:
```
1. knowledge-center.html          # 知识中心
2. knowledge-base.html            # 知识库管理
3. knowledge-search.html          # 知识搜索
4. business-knowledge.html        # 业务知识库
```

**迁移顺序**:
```
Step 1: 迁移skill-knowledge
Step 2: 复制页面到Skills
Step 3: 更新页面API路径
Step 4: 测试验证
```

#### Group 5: Agent管理（P1）

**Skills**:
```
1. skill-agent           # Agent服务
```

**页面**:
```
1. agent-list.html                # Agent列表
2. agent-detail.html              # Agent详情
3. agent-register.html            # Agent注册
4. agent-monitor.html             # Agent监控
```

**迁移顺序**:
```
Step 1: 迁移skill-agent
Step 2: 复制页面到Skills
Step 3: 更新页面API路径
Step 4: 测试验证
```

#### Group 6: 组织管理（P1）

**Skills**:
```
1. skill-org             # 组织服务
2. skill-menu            # 菜单服务
3. skill-role            # 角色服务
```

**页面**:
```
1. org-management.html            # 组织管理
2. role-admin.html                # 管理员角色
3. role-user.html                 # 用户角色
4. menu-auth.html                 # 菜单权限
```

**迁移顺序**:
```
Step 1: 迁移skill-org
Step 2: 迁移skill-menu
Step 3: 迁移skill-role
Step 4: 复制页面到Skills
Step 5: 更新页面API路径
Step 6: 测试验证
```

---

## 📝 六、页面迁移详细步骤

### 6.1 单个页面迁移步骤

#### Step 1: 分析页面依赖

```bash
# 检查页面引用的CSS
grep -o 'href="[^"]*\.css"' capability-discovery.html

# 检查页面引用的JS
grep -o 'src="[^"]*\.js"' capability-discovery.html

# 检查页面调用的API
grep -o '/api/v1/[^"'\'']*' capability-discovery.html
```

#### Step 2: 确认依赖的Skill已迁移

```bash
# 检查Skill是否存在
ls skills/_system/skill-discovery/

# 检查Skill的API是否注册
cat skills/_system/skill-discovery/skill.yaml | grep apis
```

#### Step 3: 复制页面文件

```bash
# 创建目标目录
mkdir -p src/main/resources/static/console/pages

# 复制HTML文件
cp E:/apex/os/src/main/resources/static/console/pages/capability-discovery.html \
   src/main/resources/static/console/pages/

# 复制页面特定的CSS
cp -r E:/apex/os/src/main/resources/static/console/css/pages \
      src/main/resources/static/console/css/

# 复制页面特定的JS
cp -r E:/apex/os/src/main/resources/static/console/js/pages \
      src/main/resources/static/console/js/
```

#### Step 4: 更新页面路径

```html
<!-- 更新CSS路径 -->
<link rel="stylesheet" href="/console/css/nexus.css">
<!-- 保持不变，因为使用绝对路径 -->

<!-- 更新JS路径 -->
<script src="/console/js/pages/capability-discovery.js"></script>
<!-- 保持不变，因为使用绝对路径 -->
```

#### Step 5: 测试验证

```bash
# 启动应用
mvn spring-boot:run

# 访问页面
open http://localhost:8080/console/pages/capability-discovery.html

# 检查API调用
# 打开浏览器开发者工具，查看Network标签
```

### 6.2 批量页面迁移脚本

```bash
#!/bin/bash

# 页面迁移脚本
# 用法: ./migrate-pages.sh <group>

GROUP=$1

case $GROUP in
  "core")
    PAGES=(
      "login.html"
      "workbench.html"
      "my-profile.html"
      "config-system.html"
    )
    SKILLS=(
      "skill-common"
      "skill-protocol"
      "skill-auth"
      "skill-config"
    )
    ;;
  "capability")
    PAGES=(
      "capability-discovery.html"
      "capability-management.html"
      "my-capabilities.html"
      "capability-detail.html"
      "capability-activation.html"
      "capability-binding.html"
    )
    SKILLS=(
      "skill-discovery"
      "skill-capability"
      "skill-install"
    )
    ;;
  "scene")
    PAGES=(
      "scene-management.html"
      "scene-detail.html"
      "scene-capabilities.html"
      "scene-group-management.html"
      "scene-group-detail.html"
    )
    SKILLS=(
      "skill-scene"
      "skill-scenes"
    )
    ;;
  *)
    echo "Usage: $0 {core|capability|scene|knowledge|agent|org}"
    exit 1
    ;;
esac

echo "迁移Skills: ${SKILLS[@]}"
echo "迁移页面: ${PAGES[@]}"

# 迁移Skills
for SKILL in "${SKILLS[@]}"; do
  echo "迁移Skill: $SKILL"
  # 这里添加Skill迁移逻辑
done

# 迁移页面
for PAGE in "${PAGES[@]}"; do
  echo "迁移页面: $PAGE"
  cp "E:/apex/os/src/main/resources/static/console/pages/$PAGE" \
     "src/main/resources/static/console/pages/"
done

echo "迁移完成！"
```

---

## ⚠️ 七、风险与应对

### 7.1 风险评估

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| API路径不一致 | 高 | 中 | 统一API路径规范 |
| CSS样式冲突 | 中 | 低 | 使用命名空间隔离 |
| JS依赖缺失 | 高 | 中 | 确保所有依赖已迁移 |
| 页面跳转失败 | 中 | 中 | 更新所有链接路径 |
| 权限控制失效 | 高 | 低 | 保持权限配置一致 |

### 7.2 回滚方案

```bash
# 如需回滚，从备份恢复
git checkout backup-before-page-migration

# 或者回滚到特定版本
git checkout v3.0.0
```

---

## 📚 八、总结

### 8.1 关键发现

1. **页面总数**: OS中有76个HTML页面
2. **依赖关系**: 页面依赖Skills提供的API
3. **迁移策略**: 页面保留在主工程，Skills提供API
4. **迁移分组**: 按功能分为6个组，按优先级迁移

### 8.2 关键建议

1. **优先迁移Skills**: 先迁移Skills，确保API可用
2. **分组迁移**: 按功能分组迁移，确保功能完整
3. **测试验证**: 每迁移一组，立即测试验证
4. **保持一致**: 确保API路径、权限配置一致

### 8.3 下一步行动

1. **立即执行**: 开始Group 1核心基础迁移
2. **短期计划**: 完成Group 2-4迁移
3. **中期计划**: 完成Group 5-6迁移
4. **长期计划**: 完善文档和测试

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09
