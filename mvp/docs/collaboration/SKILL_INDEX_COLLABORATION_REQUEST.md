# Skills 团队协作请求：修复 skill-index.yaml 格式

## 一、问题描述

### 1.1 现象
MVP 项目集成 SE SDK 2.3.1 后，Gitee 发现功能返回 0 个技能：

```json
{
    "code": 200,
    "status": "success",
    "data": {
        "capabilities": [],
        "total": 0,
        "source": "gitee",
        "repoUrl": "https://gitee.com/ooderCN/skills",
        "branch": "main"
    }
}
```

### 1.2 根本原因

**问题1：apiVersion 格式不匹配**

SE SDK 期望的格式：
```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndex
```

实际文件格式：
```yaml
apiVersion: ooder.io/v1
kind: SkillIndex
```

**问题2：skills-path 配置**

当前配置 `skills-path: ""`（空字符串），但技能实际位于 `skills/` 子目录。

### 1.3 验证方式
```bash
# 检查索引文件内容
curl -s "https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml" | head -10

# 输出显示 apiVersion: ooder.io/v1（错误）
# 应该是 apiVersion: skill.ooder.net/v1
```

---

## 二、请求内容

### 2.1 需要添加的文件

在 Gitee 仓库 `ooderCN/skills` 根目录创建 `skill-index.yaml` 文件。

### 2.2 文件格式规范

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndex

metadata:
  name: ooder-skills-index
  version: "1.0.0"
  description: Ooder Agent Platform 技能库索引
  lastUpdated: 2026-03-21

spec:
  skills:
    # 系统管理类
    - id: skill-network
      name: 网络管理服务
      category: sys
      path: skills/skill-network
      manifest: skill.yaml
      
    - id: skill-security
      name: 安全管理服务
      category: sys
      path: skills/skill-security
      manifest: skill.yaml
      
    - id: skill-hosting
      name: 托管服务
      category: sys
      path: skills/skill-hosting
      manifest: skill.yaml
      
    - id: skill-monitor
      name: 监控服务
      category: sys
      path: skills/skill-monitor
      manifest: skill.yaml
      
    - id: skill-health
      name: 健康检查服务
      category: sys
      path: skills/skill-health
      manifest: skill.yaml
      
    - id: skill-agent
      name: 代理管理服务
      category: sys
      path: skills/skill-agent
      manifest: skill.yaml
      
    - id: skill-openwrt
      name: OpenWrt路由器驱动
      category: sys
      path: skills/skill-openwrt
      manifest: skill.yaml
      
    - id: skill-audit
      name: 审计日志服务
      category: sys
      path: skills/skill-audit
      manifest: skill.yaml
      
    - id: skill-access-control
      name: 访问控制服务
      category: sys
      path: skills/skill-access-control
      manifest: skill.yaml
      
    - id: skill-remote-terminal
      name: 远程终端服务
      category: sys
      path: skills/skill-remote-terminal
      manifest: skill.yaml

    # 消息通讯类
    - id: skill-im
      name: 即时通讯服务
      category: msg
      path: skills/skill-im
      manifest: skill.yaml
      
    - id: skill-group
      name: 群组管理服务
      category: msg
      path: skills/skill-group
      manifest: skill.yaml
      
    - id: skill-msg
      name: 消息服务
      category: msg
      path: skills/skill-msg
      manifest: skill.yaml
      
    - id: skill-mqtt
      name: MQTT服务
      category: msg
      path: skills/skill-mqtt
      manifest: skill.yaml

    # 组织管理类
    - id: skill-org-dingding
      name: 钉钉组织集成
      category: org
      path: skills/skill-org-dingding
      manifest: skill.yaml
      
    - id: skill-org-feishu
      name: 飞书组织集成
      category: org
      path: skills/skill-org-feishu
      manifest: skill.yaml
      
    - id: skill-user-auth
      name: 用户认证服务
      category: org
      path: skills/skill-user-auth
      manifest: skill.yaml

    # UI生成类
    - id: skill-a2ui
      name: A2UI生成服务
      category: ui
      path: skills/skill-a2ui
      manifest: skill.yaml

    # 工具类
    - id: skill-scheduler-quartz
      name: Quartz调度器
      category: util
      path: skills/skill-scheduler-quartz
      manifest: skill.yaml
      
    - id: skill-k8s
      name: Kubernetes管理
      category: util
      path: skills/skill-k8s
      manifest: skill.yaml
      
    - id: skill-vfs-local
      name: 本地文件系统
      category: util
      path: skills/skill-vfs-local
      manifest: skill.yaml
```

### 2.3 文件位置

```
ooderCN/skills/
├── skill-index.yaml          # 新增：索引文件
├── README.md
├── skills/
│   ├── skill-network/
│   │   └── skill.yaml
│   ├── skill-security/
│   │   └── skill.yaml
│   └── ...
```

---

## 三、技术背景

### 3.1 SE SDK 发现机制

SE SDK 2.3.1 的 `UnifiedDiscoveryService` 工作流程：

1. 解析仓库 URL，识别平台类型（GitHub/Gitee）
2. 调用平台 API 获取 `skill-index.yaml` 文件内容
3. 解析索引文件，获取技能列表
4. 返回 `List<SkillPackage>` 给调用方

### 3.2 相关接口

```java
// UnifiedDiscoveryService.java
public interface UnifiedDiscoveryService {
    /**
     * 发现指定地址的Skills
     * 自动识别平台类型（GitHub/Gitee），使用缓存机制
     */
    CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl);
}
```

### 3.3 MVP 集成代码

```java
// SdkConfiguration.java
@Bean
public UnifiedDiscoveryService unifiedDiscoveryService() {
    UnifiedDiscoveryServiceImpl service = new UnifiedDiscoveryServiceImpl();
    service.configureGitee(giteeToken, "ooderCN", "skills", "main", "");
    return service;
}

// DiscoveryController.java
@PostMapping("/gitee")
public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(...) {
    CompletableFuture<List<SkillPackage>> future = 
        unifiedDiscoveryService.discoverSkills("https://gitee.com/ooderCN/skills");
    List<SkillPackage> packages = future.get(60, TimeUnit.SECONDS);
    // ...
}
```

---

## 四、验证步骤

### 4.1 添加索引文件后验证

```bash
# 1. 检查文件存在
curl -s "https://gitee.com/api/v5/repos/ooderCN/skills/contents/skill-index.yaml"

# 2. 检查文件内容
curl -s "https://gitee.com/ooderCN/skills/raw/master/skill-index.yaml"
```

### 4.2 MVP 项目验证

1. 启动 MVP 服务
2. 访问能力发现页面：`http://localhost:8084/console/pages/capability-discovery.html`
3. 选择 "Gitee仓库" 发现方式
4. 确认返回技能列表不为空

---

## 五、优先级

**高优先级** - 影响 Gitee 发现功能的核心功能

---

## 六、相关文档

- [Gitee 仓库地址](https://gitee.com/ooderCN/skills)
- [SE SDK Gitee 发现器文档](file:///E:/github/ooder-sdk/scene-engine/docs/v2.3.1/10-integration/04-gitee-discovery.md)
- [MVP Gitee 发现集成文档](file:///e:/github/ooder-skills/mvp/docs/features/GITEE_DISCOVERY_INTEGRATION.md)

---

## 七、联系方式

- MVP 团队：此协作请求由 MVP 项目发起
- Skills 团队：请确认并在 Gitee 仓库添加索引文件

---

**创建时间**: 2026-03-21  
**状态**: 待处理
