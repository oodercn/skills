# Nexus-UI Skill 完整技术规范

> **版本**: 1.0.0  
> **适用 Nexus 版本**: >= 2.3.0  
> **最后更新**: 2026-02-25

---

## 目录

1. [架构概述](#1-架构概述)
2. [生命周期管理](#2-生命周期管理)
3. [发现与分发](#3-发现与分发)
4. [安装与部署](#4-安装与部署)
5. [卸载与清理](#5-卸载与清理)
6. [安全模型](#6-安全模型)
7. [菜单集成](#7-菜单集成)
8. [API 规范](#8-api-规范)
9. [任务拆分与实现](#9-任务拆分与实现)

---

## 1. 架构概述

### 1.1 核心架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Nexus 平台                                      │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Nexus-UI Skill 容器                           │   │
│  │                                                                      │   │
│  │   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐         │   │
│  │   │   生命周期    │◄──►│   安全沙箱    │◄──►│   菜单管理    │         │   │
│  │   │   管理器      │    │              │    │              │         │   │
│  │   └──────────────┘    └──────────────┘    └──────────────┘         │   │
│  │          ▲                   ▲                   ▲                  │   │
│  │          │                   │                   │                  │   │
│  │   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐         │   │
│  │   │   资源加载    │    │   API 代理    │    │   配置管理    │         │   │
│  │   │   管理器      │    │              │    │              │         │   │
│  │   └──────────────┘    └──────────────┘    └──────────────┘         │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ▲                                         │
│                                    │                                         │
│  ┌─────────────────┐    ┌─────────┴──────────┐    ┌─────────────────┐      │
│  │   Web 服务器     │◄──►│   Skill 实例       │◄──►│   API 服务      │      │
│  │  (静态资源服务)  │    │  (HTML/CSS/JS)    │    │  (Java/Node/..) │      │
│  └─────────────────┘    └────────────────────┘    └─────────────────┘      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
           ▲                                    ▲
           │                                    │
    ┌──────┴──────────┐              ┌─────────┴──────────┐
    │   Skill 市场     │              │   API 来源系统     │
    │  (发现/分发)    │              │  (Nexus/第三方)    │
    └─────────────────┘              └────────────────────┘
```

### 1.2 核心组件说明

| 组件 | 职责 | 关键技术 |
|------|------|----------|
| 生命周期管理器 | 管理 Skill 的创建、启动、停止、销毁 | 状态机、事件驱动 |
| 安全沙箱 | 隔离 Skill 运行环境，限制权限 | iframe、CSP、API 白名单 |
| 菜单管理器 | 动态注册/注销菜单项 | 配置驱动、热更新 |
| 资源加载管理器 | 加载静态资源，处理 CDN 回退 | 缓存策略、懒加载 |
| API 代理 | 转发 API 请求，统一鉴权 | 反向代理、请求拦截 |
| 配置管理器 | 管理 Skill 配置，支持热更新 | 配置中心、监听机制 |

---

## 2. 生命周期管理

### 2.1 状态机定义

```
                    ┌─────────────┐
                    │   CREATED   │
                    │   (已创建)   │
                    └──────┬──────┘
                           │ install()
                           ▼
                    ┌─────────────┐
                    │  INSTALLED  │
                    │   (已安装)   │
                    └──────┬──────┘
                           │ validate()
                           ▼
              ┌────────────────────────┐
              │      VALIDATING        │
              │      (验证中)          │
              └───────────┬────────────┘
                          │
              ┌───────────┴───────────┐
              │ valid                 │ invalid
              ▼                       ▼
       ┌─────────────┐          ┌─────────────┐
       │   INVALID   │          │   PENDING   │
       │   (无效)    │          │  (待启动)   │
       └─────────────┘          └──────┬──────┘
                                       │ start()
                                       ▼
                                ┌─────────────┐
                                │  STARTING   │
                                │   (启动中)   │
                                └──────┬──────┘
                                       │
                              ┌────────┴────────┐
                              │ success         │ failed
                              ▼                 ▼
                       ┌─────────────┐   ┌─────────────┐
                       │    ACTIVE   │   │    ERROR    │
                       │   (运行中)   │   │   (错误)    │
                       └──────┬──────┘   └──────┬──────┘
                              │                 │
                              │ stop()          │ retry()
                              ▼                 ▼
                       ┌─────────────┐   ┌─────────────┐
                       │  STOPPING   │   │  RESTARTING │
                       │   (停止中)   │   │   (重启中)   │
                       └──────┬──────┘   └──────┬──────┘
                              │                 │
                              ▼                 ▼
                       ┌─────────────┐   ┌─────────────┐
                       │   STOPPED   │   │   ACTIVE    │
                       │   (已停止)   │   │  (恢复运行)  │
                       └──────┬──────┘   └─────────────┘
                              │
                              │ uninstall()
                              ▼
                       ┌─────────────┐
                       │  UNINSTALL  │
                       │   (卸载中)   │
                       └──────┬──────┘
                              │
                              ▼
                       ┌─────────────┐
                       │  DESTROYED  │
                       │   (已销毁)   │
                       └─────────────┘
```

### 2.2 生命周期事件

```java
public interface NexusUiSkillLifecycle {
    
    // 安装阶段
    void onInstalling(SkillContext context);      // 开始安装
    void onInstalled(SkillContext context);       // 安装完成
    void onValidationFailed(SkillContext context, ValidationError error); // 验证失败
    
    // 启动阶段
    void onStarting(SkillContext context);        // 开始启动
    void onStarted(SkillContext context);         // 启动完成
    void onStartFailed(SkillContext context, Throwable error); // 启动失败
    
    // 运行阶段
    void onActivated(SkillContext context);       // 激活（菜单可见）
    void onDeactivated(SkillContext context);     // 失活（菜单隐藏）
    
    // 停止阶段
    void onStopping(SkillContext context);        // 开始停止
    void onStopped(SkillContext context);         // 停止完成
    
    // 卸载阶段
    void onUninstalling(SkillContext context);    // 开始卸载
    void onUninstalled(SkillContext context);     // 卸载完成
    
    // 错误处理
    void onError(SkillContext context, Throwable error); // 发生错误
}
```

### 2.3 生命周期钩子配置

```yaml
# skill.yaml
spec:
  lifecycle:
    hooks:
      # 安装前检查
      preInstall:
        script: "scripts/pre-install.sh"
        timeout: 30s
        
      # 安装后初始化
      postInstall:
        script: "scripts/post-install.sh"
        
      # 启动前检查
      preStart:
        healthCheck:
          endpoint: "/health"
          interval: 5s
          retries: 3
          
      # 停止后清理
      postStop:
        cleanup:
          - temp_files
          - cache
          
      # 卸载前备份
      preUninstall:
        backup:
          enabled: true
          destination: "/backup/skills/{skill-id}"
```

---

## 3. 发现与分发

### 3.1 发现机制

```
┌─────────────────────────────────────────────────────────────────┐
│                      Skill 发现架构                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  官方市场    │    │  私有仓库    │    │  本地目录    │         │
│  │ (ooder.net) │    │ (企业内部)   │    │ (开发测试)   │         │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘         │
│         │                  │                  │                │
│         └──────────────────┼──────────────────┘                │
│                            ▼                                    │
│                   ┌─────────────────┐                          │
│                   │   发现服务       │                          │
│                   │  (Discovery)    │                          │
│                   └────────┬────────┘                          │
│                            │                                    │
│         ┌──────────────────┼──────────────────┐                │
│         ▼                  ▼                  ▼                │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  搜索接口    │    │  分类浏览    │    │  推荐系统    │         │
│  │  (Search)   │    │ (Category)  │    │(Recommendation)        │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 发现 API 规范

```yaml
# 发现服务 API
openapi: 3.0.0
info:
  title: Nexus-UI Skill Discovery API
  version: 1.0.0

paths:
  /api/skills/discover:
    get:
      summary: 发现 Skill
      parameters:
        - name: type
          in: query
          schema:
            type: string
            enum: [nexus-ui, all]
          description: Skill 类型筛选
        - name: category
          in: query
          schema:
            type: string
          description: 分类筛选
        - name: keyword
          in: query
          schema:
            type: string
          description: 关键词搜索
        - name: source
          in: query
          schema:
            type: string
            enum: [official, private, local, all]
          description: 来源筛选
      responses:
        '200':
          description: Skill 列表
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                    example: success
                  data:
                    type: object
                    properties:
                      skills:
                        type: array
                        items:
                          $ref: '#/components/schemas/SkillMetadata'
                      total:
                        type: integer
                      page:
                        type: integer
                      pageSize:
                        type: integer

  /api/skills/discover/{skillId}:
    get:
      summary: 获取 Skill 详情
      parameters:
        - name: skillId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Skill 详情
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SkillDetail'

components:
  schemas:
    SkillMetadata:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
        version:
          type: string
        description:
          type: string
        type:
          type: string
          enum: [nexus-ui, backend, hybrid]
        category:
          type: string
        author:
          type: string
        icon:
          type: string
        rating:
          type: number
        downloadCount:
          type: integer
        source:
          type: string
          enum: [official, private, local]
        compatibility:
          type: object
          properties:
            nexusVersion:
              type: string
            dependencies:
              type: array
              items:
                type: string
        
    SkillDetail:
      allOf:
        - $ref: '#/components/schemas/SkillMetadata'
        - type: object
          properties:
            screenshots:
              type: array
              items:
                type: string
            readme:
              type: string
            changelog:
              type: string
            size:
              type: integer
            checksum:
              type: string
            signatures:
              type: object
```

### 3.3 多源配置

```yaml
# nexus-config.yaml
skills:
  discovery:
    sources:
      # 官方市场
      - name: official
        type: remote
        url: https://skills.ooder.net/api/v1
        enabled: true
        priority: 1
        auth:
          type: apikey
          keyHeader: X-API-Key
        
      # 私有仓库
      - name: enterprise
        type: remote
        url: https://skills.company.com/api/v1
        enabled: true
        priority: 2
        auth:
          type: oauth2
          clientId: ${SKILL_REGISTRY_CLIENT_ID}
          clientSecret: ${SKILL_REGISTRY_CLIENT_SECRET}
        
      # 本地目录
      - name: local
        type: local
        path: /opt/nexus/skills/local
        enabled: true
        priority: 3
        watch: true  # 监听文件变化
        
      # Git 仓库
      - name: git-repo
        type: git
        url: https://github.com/company/nexus-skills.git
        enabled: false
        branch: main
        path: skills/
        
    # 缓存配置
    cache:
      enabled: true
      ttl: 3600  # 1小时
      maxSize: 100MB
      
    # 同步配置
    sync:
      interval: 300  # 5分钟
      onStartup: true
```

---

## 4. 安装与部署

### 4.1 安装流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      安装流程图                                  │
└─────────────────────────────────────────────────────────────────┘

[用户发起安装]
       │
       ▼
[1. 下载 Skill 包]
       │
       ├──► 从官方市场下载
       ├──► 从私有仓库下载
       └──► 从本地上传
       │
       ▼
[2. 验证包完整性]
       │
       ├──► 检查文件结构
       ├──► 验证 checksum
       └──► 验证数字签名
       │
       ▼
[3. 解析元数据]
       │
       ├──► 读取 skill.yaml
       ├──► 验证必填字段
       └──► 检查版本兼容性
       │
       ▼
[4. 依赖检查]
       │
       ├──► 检查 Nexus 版本
       ├──► 检查依赖 Skill
       └──► 检查系统资源
       │
       ▼
[5. 安全扫描]
       │
       ├──► 静态代码分析
       ├──► 恶意代码检测
       └──► 权限审查
       │
       ▼
[6. 安装文件]
       │
       ├──► 解压到安装目录
       ├──► 复制静态资源
       └──► 注册 API 路由
       │
       ▼
[7. 注册菜单]
       │
       ├──► 解析菜单配置
       ├──► 验证菜单权限
       └──► 添加到菜单系统
       │
       ▼
[8. 启动服务]
       │
       ├──► 启动后端服务（如有）
       ├──► 初始化数据库（如有）
       └──► 执行 post-install 钩子
       │
       ▼
[9. 完成安装]
       │
       ├──► 更新 Skill 列表
       ├──► 记录安装日志
       └──► 通知用户
```

### 4.2 安装 API

```yaml
openapi: 3.0.0
info:
  title: Nexus-UI Skill Installation API
  version: 1.0.0

paths:
  /api/skills/install:
    post:
      summary: 安装 Skill
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: Skill 包文件 (.skill)
                source:
                  type: string
                  description: 远程源 URL（可选）
                options:
                  type: object
                  properties:
                    force:
                      type: boolean
                      description: 强制重新安装
                    skipValidation:
                      type: boolean
                      description: 跳过验证
      responses:
        '200':
          description: 安装成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                    example: success
                  data:
                    type: object
                    properties:
                      skillId:
                        type: string
                      installPath:
                        type: string
                      menuPath:
                        type: string
                      installedAt:
                        type: string
                        format: date-time

  /api/skills/install/{skillId}/status:
    get:
      summary: 获取安装状态
      parameters:
        - name: skillId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 安装状态
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                  data:
                    type: object
                    properties:
                      state:
                        type: string
                        enum: [pending, downloading, validating, installing, completed, failed]
                      progress:
                        type: integer
                        description: 进度百分比
                      message:
                        type: string
                      errors:
                        type: array
                        items:
                          type: string
```

### 4.3 部署配置

```yaml
# skill.yaml - 部署配置
spec:
  deployment:
    # 部署模式
    mode: embedded  # embedded | standalone | external
    
    # 资源需求
    resources:
      cpu:
        min: "100m"
        max: "500m"
      memory:
        min: "128Mi"
        max: "512Mi"
      storage:
        size: "100Mi"
        type: ephemeral  # ephemeral | persistent
    
    # 网络配置
    network:
      # 静态资源路径
      staticPath: /ui/{skill-id}
      
      # API 代理配置
      apiProxy:
        enabled: true
        path: /api/skills/{skill-id}
        target: http://localhost:{dynamic-port}
        
      # 跨域配置
      cors:
        enabled: true
        origins: ["*"]
        methods: ["GET", "POST", "PUT", "DELETE"]
        headers: ["Authorization", "Content-Type"]
    
    # 健康检查
    healthCheck:
      enabled: true
      endpoint: /health
      interval: 30s
      timeout: 5s
      retries: 3
      
    # 自动扩缩容（仅 standalone 模式）
    autoScaling:
      enabled: false
      minReplicas: 1
      maxReplicas: 3
      targetCPU: 70
```

---

## 5. 卸载与清理

### 5.1 卸载流程

```
[用户发起卸载]
       │
       ▼
[1. 前置检查]
       │
       ├──► 检查是否有依赖此 Skill 的其他 Skill
       ├──► 检查是否有正在进行的操作
       └──► 确认用户权限
       │
       ▼
[2. 执行 pre-uninstall 钩子]
       │
       ├──► 备份数据（如配置）
       ├──► 通知其他组件
       └──► 停止相关服务
       │
       ▼
[3. 注销菜单]
       │
       ├──► 从菜单系统移除
       ├──► 清理路由映射
       └──► 更新权限缓存
       │
       ▼
[4. 停止服务]
       │
       ├──► 停止后端服务
       ├──► 关闭数据库连接
       └──► 释放端口
       │
       ▼
[5. 清理资源]
       │
       ├──► 删除静态资源
       ├──► 删除安装目录
       ├──► 清理缓存
       └──► 删除配置数据
       │
       ▼
[6. 执行 post-uninstall 钩子]
       │
       ├──► 清理外部依赖
       └──► 发送卸载统计
       │
       ▼
[7. 完成卸载]
       │
       ├──► 更新 Skill 列表
       ├──► 记录卸载日志
       └──► 通知用户
```

### 5.2 卸载 API

```yaml
paths:
  /api/skills/{skillId}/uninstall:
    post:
      summary: 卸载 Skill
      parameters:
        - name: skillId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                force:
                  type: boolean
                  description: 强制卸载（忽略依赖检查）
                keepData:
                  type: boolean
                  description: 保留数据（用于重新安装）
                backup:
                  type: boolean
                  description: 卸载前备份
      responses:
        '200':
          description: 卸载成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                  data:
                    type: object
                    properties:
                      skillId:
                        type: string
                      uninstalledAt:
                        type: string
                      backupPath:
                        type: string
```

### 5.3 清理策略

```yaml
spec:
  uninstall:
    # 数据保留策略
    dataRetention:
      # 用户数据
      userData:
        action: backup  # delete | backup | keep
        backupPath: /backup/skills/{skill-id}/data
        retentionDays: 30
      
      # 配置文件
      config:
        action: backup
        backupPath: /backup/skills/{skill-id}/config
      
      # 缓存数据
      cache:
        action: delete
      
      # 日志文件
      logs:
        action: archive
        archivePath: /archive/skills/{skill-id}/logs
    
    # 依赖处理
    dependencies:
      # 当其他 Skill 依赖此 Skill 时
      onDependentExists:
        action: prompt  # prompt | force | block
        message: "以下 Skill 依赖此 Skill: {dependencies}"
    
    # 清理钩子
    hooks:
      preUninstall:
        - name: backup-data
          script: scripts/backup.sh
        - name: notify-users
          http:
            url: /api/notifications
            method: POST
            body:
              type: skill-uninstalling
              skillId: "{skill-id}"
      
      postUninstall:
        - name: cleanup-external
          script: scripts/cleanup.sh
```

---

## 6. 安全模型

### 6.1 安全架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      安全模型架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    安全边界                              │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │              Skill 安全沙箱                      │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐              │   │   │
│  │  │  │  代码隔离    │  │  资源限制    │              │   │   │
│  │  │  │  (iframe)   │  │  (CSP/Quota)│              │   │   │
│  │  │  └─────────────┘  └─────────────┘              │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐              │   │   │
│  │  │  │  API 白名单  │  │  数据隔离    │              │   │   │
│  │  │  │             │  │             │              │   │   │
│  │  │  └─────────────┘  └─────────────┘              │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│  ┌─────────────────────────┼───────────────────────────────┐   │
│  │                         ▼                               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │   认证层    │  │   授权层    │  │   审计层    │     │   │
│  │  │  (AuthN)   │  │  (AuthZ)   │  │  (Audit)   │     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 权限模型

```yaml
# 权限定义
permissions:
  # Skill 管理权限
  skill:manage:
    description: 管理所有 Skill
    scope: global
    
  skill:install:
    description: 安装 Skill
    scope: global
    
  skill:uninstall:
    description: 卸载 Skill
    scope: skill  # 针对特定 Skill
    
  skill:configure:
    description: 配置 Skill
    scope: skill
    
  # UI 访问权限
  skill:ui:view:
    description: 查看 Skill 页面
    scope: skill
    
  skill:ui:interact:
    description: 与 Skill 交互
    scope: skill
    
  # API 调用权限
  skill:api:call:
    description: 调用 Skill API
    scope: skill
    
  skill:api:admin:
    description: 管理 Skill API
    scope: skill

# 默认角色
roles:
  skill-admin:
    permissions:
      - skill:manage
      - skill:install
      - skill:uninstall
      
  skill-user:
    permissions:
      - skill:ui:view
      - skill:ui:interact
      - skill:api:call
```

### 6.3 安全策略配置

```yaml
spec:
  security:
    # 沙箱配置
    sandbox:
      enabled: true
      type: iframe  # iframe | shadow-dom | worker
      
      # 内容安全策略
      csp:
        default-src: "'self'"
        script-src: 
          - "'self'"
          - "https://gitee.com"
          - "https://cdn.jsdelivr.net"
        style-src:
          - "'self'"
          - "'unsafe-inline'"
          - "https://gitee.com"
          - "https://cdn.jsdelivr.net"
        img-src:
          - "'self'"
          - "data:"
          - "https:"
        connect-src:
          - "'self'"
          - "/api/skills/{skill-id}"
      
      # 资源限制
      resourceLimits:
        maxMemory: "100MB"
        maxStorage: "50MB"
        maxCpu: "10%"
        maxNetwork: "1MB/s"
    
    # API 访问控制
    apiAccess:
      # 白名单模式
      whitelist:
        enabled: true
        apis:
          - path: "/api/data"
            methods: ["GET"]
            rateLimit: "100/min"
          - path: "/api/config"
            methods: ["GET", "POST"]
            requireAuth: true
      
      # 黑名单
      blacklist:
        - path: "/api/admin/*"
        - path: "/api/internal/*"
    
    # 数据隔离
    dataIsolation:
      # 存储隔离
      storage:
        type: isolated  # isolated | shared
        path: "/data/skills/{skill-id}"
      
      # 会话隔离
      session:
        isolated: true
        cookiePrefix: "skill_{skill_id}_"
      
      # 缓存隔离
      cache:
        isolated: true
        namespace: "skill:{skill-id}"
    
    # 审计日志
    audit:
      enabled: true
      events:
        - skill.install
        - skill.uninstall
        - skill.start
        - skill.stop
        - skill.api.call
        - skill.ui.access
      retention: 90d
```

### 6.4 代码签名验证

```yaml
spec:
  security:
    signature:
      # 签名要求
      required: true
      
      # 信任的证书
      trustedCertificates:
        - type: official
          issuer: "Ooder Official"
          url: "https://certs.ooder.net/official.pem"
        - type: enterprise
          issuer: "Enterprise CA"
          url: "https://certs.company.com/ca.pem"
      
      # 验证策略
      verification:
        # 安装时验证
        onInstall: strict  # strict | warn | none
        
        # 运行时验证
        onRuntime: warn
        
        # 证书过期处理
        onCertExpired: block  # block | warn | allow
```

---

## 7. 菜单集成

### 7.1 菜单架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      菜单集成架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    菜单配置中心                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  静态配置    │  │  动态配置    │  │  运行时配置  │     │   │
│  │  │(menu.json) │  │(Skill YAML) │  │ (API)       │     │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │   │
│  │         └─────────────────┼─────────────────┘            │   │
│  │                           ▼                             │   │
│  │                  ┌─────────────────┐                   │   │
│  │                  │   菜单合并引擎   │                   │   │
│  │                  │  (Menu Merger)  │                   │   │
│  │                  └────────┬────────┘                   │   │
│  │                           ▼                             │   │
│  │                  ┌─────────────────┐                   │   │
│  │                  │   权限过滤器    │                   │   │
│  │                  │ (Auth Filter)  │                   │   │
│  │                  └────────┬────────┘                   │   │
│  │                           ▼                             │   │
│  │                  ┌─────────────────┐                   │   │
│  │                  │   菜单渲染器    │                   │   │
│  │                  │ (Renderer)     │                   │   │
│  │                  └─────────────────┘                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 菜单配置规范

```yaml
# skill.yaml - 菜单配置
spec:
  nexusUi:
    menu:
      # 菜单位置
      position: sidebar  # sidebar | header | dropdown | toolbar
      
      # 菜单分类
      category: custom
      
      # 排序权重（越小越靠前）
      order: 100
      
      # 菜单项定义
      items:
        # 主菜单项
        - id: main
          title: 我的仪表盘
          icon: ri-dashboard-3-line
          page: index.html
          permissions:
            - skill:ui:view
          
        # 子菜单
        - id: data
          title: 数据管理
          icon: ri-database-2-line
          children:
            - id: data-list
              title: 数据列表
              icon: ri-list-check
              page: pages/data-list.html
              permissions:
                - skill:ui:view
                
            - id: data-import
              title: 数据导入
              icon: ri-upload-cloud-line
              page: pages/data-import.html
              permissions:
                - skill:ui:interact
                
        # 分隔线
        - type: divider
        
        # 外部链接
        - id: docs
          title: 帮助文档
          icon: ri-question-line
          external: true
          url: https://docs.example.com
          
      # 动态菜单（从 API 获取）
      dynamic:
        enabled: true
        endpoint: /api/skills/{skill-id}/menu
        refreshInterval: 300  # 5分钟
```

### 7.3 菜单 API

```yaml
paths:
  /api/menu:
    get:
      summary: 获取完整菜单
      responses:
        '200':
          description: 菜单列表
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                  data:
                    type: object
                    properties:
                      sidebar:
                        type: array
                        items:
                          $ref: '#/components/schemas/MenuItem'
                      header:
                        type: array
                        items:
                          $ref: '#/components/schemas/MenuItem'

  /api/skills/{skillId}/menu:
    get:
      summary: 获取 Skill 动态菜单
      parameters:
        - name: skillId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 动态菜单项
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/MenuItem'

components:
  schemas:
    MenuItem:
      type: object
      properties:
        id:
          type: string
        title:
          type: string
        icon:
          type: string
        type:
          type: string
          enum: [item, divider, group]
        page:
          type: string
        external:
          type: boolean
        url:
          type: string
        children:
          type: array
          items:
            $ref: '#/components/schemas/MenuItem'
        permissions:
          type: array
          items:
            type: string
        visible:
          type: boolean
        badge:
          type: object
          properties:
            text:
              type: string
            type:
              type: string
              enum: [info, success, warning, danger]
```

### 7.4 菜单权限控制

```javascript
// 菜单权限过滤器
class MenuPermissionFilter {
    
    filter(menuItems, userPermissions) {
        return menuItems.filter(item => {
            // 检查权限
            if (item.permissions) {
                const hasPermission = item.permissions.some(p => 
                    userPermissions.includes(p)
                );
                if (!hasPermission) return false;
            }
            
            // 递归过滤子菜单
            if (item.children) {
                item.children = this.filter(item.children, userPermissions);
                // 如果子菜单都被过滤掉了，且当前菜单没有 page，则隐藏
                if (item.children.length === 0 && !item.page) {
                    return false;
                }
            }
            
            // 检查可见性
            if (item.visible === false) return false;
            
            return true;
        });
    }
}
```

---

## 8. API 规范

### 8.1 API 来源类型

```
┌─────────────────────────────────────────────────────────────────┐
│                      API 来源架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    API 网关                              │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  路由管理    │  │  协议转换    │  │  统一鉴权    │     │   │
│  │  │  (Router)  │  │(Protocol)  │  │  (Auth)    │     │   │
│  │  └──────┬──────┘  └─────────────┘  └─────────────┘     │   │
│  │         │                                              │   │
│  │         ▼                                              │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │              API 来源适配器                      │   │   │
│  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐          │   │   │
│  │  │  │  Nexus  │ │  第三方  │ │  Skill  │          │   │   │
│  │  │  │ 内部API │ │  系统   │ │ 内部API │          │   │   │
│  │  │  └────┬────┘ └────┬────┘ └────┬────┘          │   │   │
│  │  │       └───────────┼───────────┘                │   │   │
│  │  │                   ▼                            │   │   │
│  │  │         ┌─────────────────┐                    │   │   │
│  │  │         │   统一响应格式   │                    │   │   │
│  │  │         │ (JSON Format)   │                    │   │   │
│  │  │         └─────────────────┘                    │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 API 来源配置

```yaml
# skill.yaml - API 来源配置
spec:
  apis:
    # 来源 1：Nexus 内部 API
    - id: nexus-internal
      type: nexus
      description: Nexus 内部服务 API
      baseUrl: /api/v1
      endpoints:
        - path: /users
          method: GET
          description: 获取用户列表
          responseSchema: UserList
          
        - path: /users/{id}
          method: GET
          description: 获取用户详情
          parameters:
            - name: id
              in: path
              required: true
              type: string
          responseSchema: User
    
    # 来源 2：第三方系统 API
    - id: external-crm
      type: external
      description: CRM 系统 API
      baseUrl: https://crm.company.com/api
      auth:
        type: oauth2
        clientId: ${CRM_CLIENT_ID}
        clientSecret: ${CRM_CLIENT_SECRET}
        tokenUrl: https://crm.company.com/oauth/token
      endpoints:
        - path: /customers
          method: GET
          description: 获取客户列表
          cache:
            enabled: true
            ttl: 300
            
    # 来源 3：Skill 内部 API
    - id: skill-internal
      type: internal
      description: Skill 内部服务 API
      baseUrl: /api/skills/{skill-id}
      implementation:
        language: java
        mainClass: com.example.skill.ApiController
      endpoints:
        - path: /data
          method: GET
          description: 获取业务数据
          handler: getData
          
        - path: /config
          method: POST
          description: 更新配置
          handler: updateConfig
          requestSchema: ConfigRequest
```

### 8.3 统一 API 响应格式

```json
{
  "status": "success",
  "message": "操作成功",
  "data": {
    // 业务数据
  },
  "code": null,
  "timestamp": 1709000000000,
  "requestId": "req_abc123",
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "total": 100,
    "totalPages": 5
  }
}
```

### 8.4 API 规格描述

```yaml
# api/spec/openapi.yaml
openapi: 3.0.0
info:
  title: Nexus-UI Skill API Specification
  version: 1.0.0
  description: |
    本 API 规范定义了 Nexus-UI Skill 的接口标准。
    所有 API 必须遵循以下规范：
    1. 使用统一的响应格式
    2. 支持分页查询
    3. 包含完整的错误处理
    4. 提供详细的字段说明

servers:
  - url: /api/skills/{skill-id}
    variables:
      skill-id:
        default: skill-example-nexus-ui

paths:
  /data:
    get:
      summary: 获取数据列表
      description: |
        获取业务数据列表，支持分页和筛选。
        适用于数据表格展示场景。
      tags:
        - 数据管理
      parameters:
        - name: page
          in: query
          description: 页码，从 1 开始
          schema:
            type: integer
            default: 1
        - name: pageSize
          in: query
          description: 每页数量
          schema:
            type: integer
            default: 20
            maximum: 100
        - name: keyword
          in: query
          description: 搜索关键词
          schema:
            type: string
        - name: sort
          in: query
          description: 排序字段
          schema:
            type: string
        - name: order
          in: query
          description: 排序方向
          schema:
            type: string
            enum: [asc, desc]
            default: desc
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                    example: success
                  data:
                    type: object
                    properties:
                      list:
                        type: array
                        items:
                          $ref: '#/components/schemas/DataItem'
                      pagination:
                        $ref: '#/components/schemas/Pagination'
                  message:
                    type: string
        '400':
          description: 请求参数错误
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '401':
          description: 未授权
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '500':
          description: 服务器错误
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /data/{id}:
    get:
      summary: 获取数据详情
      description: 根据 ID 获取单条数据详情
      tags:
        - 数据管理
      parameters:
        - name: id
          in: path
          required: true
          description: 数据 ID
          schema:
            type: string
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                  data:
                    $ref: '#/components/schemas/DataItem'
        '404':
          description: 数据不存在

    put:
      summary: 更新数据
      description: 更新指定 ID 的数据
      tags:
        - 数据管理
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/DataItem'
      responses:
        '200':
          description: 更新成功

    delete:
      summary: 删除数据
      description: 删除指定 ID 的数据
      tags:
        - 数据管理
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 删除成功

components:
  schemas:
    DataItem:
      type: object
      description: 数据项模型
      required:
        - id
        - name
      properties:
        id:
          type: string
          description: 唯一标识
          example: "data_001"
        name:
          type: string
          description: 名称
          example: "示例数据"
        status:
          type: string
          description: 状态
          enum: [active, inactive, pending]
          example: "active"
        createdAt:
          type: string
          format: date-time
          description: 创建时间
        updatedAt:
          type: string
          format: date-time
          description: 更新时间
        metadata:
          type: object
          description: 扩展元数据
          additionalProperties: true

    Pagination:
      type: object
      description: 分页信息
      properties:
        page:
          type: integer
          description: 当前页码
        pageSize:
          type: integer
          description: 每页数量
        total:
          type: integer
          description: 总记录数
        totalPages:
          type: integer
          description: 总页数

    ErrorResponse:
      type: object
      description: 错误响应
      properties:
        status:
          type: string
          example: error
        message:
          type: string
          description: 错误信息
        code:
          type: string
          description: 错误码
        details:
          type: object
          description: 详细错误信息
        timestamp:
          type: string
          format: date-time
```

---

## 9. 任务拆分与实现

### 9.1 开发任务清单

```yaml
tasks:
  # Phase 1: 基础架构
  - id: T1
    title: 生命周期管理器
    priority: P0
    dependencies: []
    deliverables:
      - 状态机实现
      - 生命周期钩子机制
      - 事件发布订阅
    
  - id: T2
    title: 资源加载管理器
    priority: P0
    dependencies: [T1]
    deliverables:
      - CDN 资源加载
      - 本地资源加载
      - 缓存策略
      - 错误回退
    
  - id: T3
    title: 安全沙箱实现
    priority: P0
    dependencies: [T1]
    deliverables:
      - iframe 隔离
      - CSP 策略
      - API 白名单
      - 权限验证
    
  # Phase 2: 发现与分发
  - id: T4
    title: 发现服务
    priority: P1
    dependencies: [T1]
    deliverables:
      - 多源发现机制
      - 搜索接口
      - 分类浏览
      - 缓存同步
    
  - id: T5
    title: 包管理服务
    priority: P1
    dependencies: [T4]
    deliverables:
      - 包下载
      - 完整性验证
      - 签名验证
      - 版本管理
    
  # Phase 3: 安装部署
  - id: T6
    title: 安装引擎
    priority: P1
    dependencies: [T2, T3, T5]
    deliverables:
      - 安装流程
      - 依赖检查
      - 安全扫描
      - 回滚机制
    
  - id: T7
    title: 部署管理器
    priority: P1
    dependencies: [T6]
    deliverables:
      - 静态资源部署
      - API 服务部署
      - 配置管理
      - 健康检查
    
  # Phase 4: 菜单集成
  - id: T8
    title: 菜单管理器
    priority: P1
    dependencies: [T7]
    deliverables:
      - 菜单注册
      - 权限过滤
      - 动态更新
      - 多级菜单
    
  # Phase 5: API 管理
  - id: T9
    title: API 网关
    priority: P2
    dependencies: [T3]
    deliverables:
      - 路由管理
      - 协议转换
      - 统一鉴权
      - 限流熔断
    
  - id: T10
    title: API 适配器
    priority: P2
    dependencies: [T9]
    deliverables:
      - Nexus 内部 API 适配
      - 第三方 API 适配
      - Skill 内部 API 适配
      - 响应格式统一
    
  # Phase 6: 卸载清理
  - id: T11
    title: 卸载引擎
    priority: P2
    dependencies: [T7, T8]
    deliverables:
      - 卸载流程
      - 数据备份
      - 依赖检查
      - 清理策略
    
  # Phase 7: 监控运维
  - id: T12
    title: 监控体系
    priority: P3
    dependencies: [T1]
    deliverables:
      - 指标收集
      - 日志管理
      - 健康检查
      - 告警机制
    
  - id: T13
    title: CLI 工具
    priority: P3
    dependencies: [T4, T6, T11]
    deliverables:
      - skill create 命令
      - skill install 命令
      - skill uninstall 命令
      - skill validate 命令
```

### 9.2 接口定义汇总

```yaml
# 核心接口定义
interfaces:
  # 生命周期接口
  LifecycleManager:
    methods:
      - install(skillPackage): InstallResult
      - start(skillId): StartResult
      - stop(skillId): StopResult
      - uninstall(skillId, options): UninstallResult
      - getStatus(skillId): SkillStatus
      - registerHook(event, callback): void
      
  # 发现接口
  DiscoveryService:
    methods:
      - search(query, filters): SkillList
      - getDetail(skillId): SkillDetail
      - getCategories(): CategoryList
      - sync(source): SyncResult
      
  # 菜单接口
  MenuManager:
    methods:
      - register(skillId, menuConfig): MenuRegistration
      - unregister(skillId): void
      - update(skillId, menuConfig): void
      - getMenu(userPermissions): MenuTree
      
  # 安全接口
  SecurityManager:
    methods:
      - validatePackage(package): ValidationResult
      - createSandbox(skillId): Sandbox
      - checkPermission(user, permission): boolean
      - audit(event, context): void
      
  # API 接口
  ApiGateway:
    methods:
      - registerRoute(skillId, routeConfig): void
      - unregisterRoute(skillId): void
      - proxy(request): Response
      - transform(sourceFormat, targetFormat): Transformer
```

### 9.3 数据模型

```yaml
# 核心数据模型
models:
  Skill:
    id: string
    name: string
    version: string
    type: enum[nexus-ui, backend, hybrid]
    status: enum[created, installed, active, stopped, error, destroyed]
    metadata: SkillMetadata
    config: SkillConfig
    permissions: Permission[]
    
  SkillInstance:
    skillId: string
    instanceId: string
    state: LifecycleState
    installPath: string
    staticUrl: string
    apiUrl: string
    startedAt: datetime
    health: HealthStatus
    
  MenuItem:
    id: string
    skillId: string
    parentId: string
    title: string
    icon: string
    type: enum[item, divider, group]
    page: string
    url: string
    external: boolean
    children: MenuItem[]
    permissions: string[]
    order: int
    visible: boolean
    
  ApiEndpoint:
    id: string
    skillId: string
    source: enum[nexus, external, internal]
    path: string
    method: enum[GET, POST, PUT, DELETE]
    targetUrl: string
    authRequired: boolean
    rateLimit: RateLimit
    cache: CacheConfig
```

---

## 10. 总结

本文档完整定义了 Nexus-UI Skill 的技术规范，涵盖：

1. **生命周期管理**：从创建到销毁的完整状态机
2. **发现与分发**：多源发现、搜索、分类
3. **安装与部署**：验证、依赖检查、安全扫描
4. **卸载与清理**：数据保留、依赖处理、清理策略
5. **安全模型**：沙箱隔离、权限控制、代码签名
6. **菜单集成**：动态注册、权限过滤、多级菜单
7. **API 规范**：多来源适配、统一格式、完整规格
8. **任务拆分**：13个开发任务，分6个阶段实施

通过这套规范，可以实现：
- 用户通过 LLM 快速生成自定义页面
- 简单的打包上传即可完成部署
- 完整的生命周期管理和安全隔离
- 灵活的菜单集成和 API 适配
