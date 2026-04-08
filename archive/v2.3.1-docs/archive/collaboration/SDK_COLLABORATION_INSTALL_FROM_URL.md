# SDK 安装功能增强协同任务

## 任务编号
SDK-COOP-2026-002

## 优先级
P0 - 高优先级

## 任务背景

### 当前问题

场景驱动安装闭环已实现，但 SDK 缺少从 URL 安装技能的功能：

1. **安装流程不完整**
   - 当前实现：`SkillPackageManager.installWithDependencies(skillId, mode)`
   - 问题：SDK 找不到技能清单，因为没有下载 URL 信息

2. **缺少 URL 安装方法**
   - `skill-index.yaml` 已包含下载 URL：
     - `downloadUrl` - GitHub 下载地址
     - `giteeDownloadUrl` - Gitee 下载地址
   - SDK 没有方法使用这些 URL 进行安装

3. **用户体验差**
   - 用户点击安装按钮后失败
   - 错误信息：`Skill manifest not found: skill-xxx`

### 现有资源

`skill-index.yaml` 已包含完整的技能下载信息：

```yaml
skills:
  - skillId: skill-a2ui
    name: A2UI Skill
    version: "0.7.3"
    downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.3/skill-a2ui-0.7.3.zip
    giteeDownloadUrl: https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-a2ui-0.7.3.zip
```

## 需求方案

### 方案一：添加 installFromUrl 方法（推荐）

**接口定义**：

```java
public interface SkillPackageManager {
    
    // 现有方法
    CompletableFuture<InstallResult> installSkill(InstallRequest request);
    CompletableFuture<InstallResultWithDependencies> installWithDependencies(String skillId, InstallMode mode);
    
    // 新增方法
    CompletableFuture<InstallResult> installFromUrl(String downloadUrl, InstallFromUrlOptions options);
    CompletableFuture<InstallResultWithDependencies> installFromUrlWithDependencies(String downloadUrl, InstallFromUrlOptions options);
}
```

**选项类**：

```java
public class InstallFromUrlOptions {
    private String skillId;           // 可选，用于验证
    private String version;           // 可选，用于验证
    private boolean verifyChecksum;   // 是否校验 checksum
    private String checksum;          // 校验值
    private boolean installDependencies; // 是否安装依赖
    private String mirrorUrl;         // 镜像地址（备用）
}
```

### 方案二：扩展 InstallRequest

**接口定义**：

```java
public class InstallRequest {
    private String skillId;
    private InstallMode mode;
    private String version;
    private String source;
    private String repoUrl;
    
    // 新增字段
    private String downloadUrl;       // 直接下载地址
    private String giteeDownloadUrl;  // Gitee 镜像地址
    private String checksum;          // 校验值
}
```

## 任务分解

### 任务 1：添加 installFromUrl 接口（SDK 团队）

**文件位置**：`skills-api` 模块

**修改内容**：

```java
// SkillPackageManager.java
public interface SkillPackageManager {
    
    /**
     * 从 URL 安装技能
     * @param downloadUrl 技能包下载地址
     * @param options 安装选项
     * @return 安装结果
     */
    CompletableFuture<InstallResult> installFromUrl(String downloadUrl, InstallFromUrlOptions options);
    
    /**
     * 从 URL 安装技能及其依赖
     * @param downloadUrl 技能包下载地址
     * @param options 安装选项
     * @return 安装结果（包含依赖信息）
     */
    CompletableFuture<InstallResultWithDependencies> installFromUrlWithDependencies(
        String downloadUrl, InstallFromUrlOptions options);
}
```

### 任务 2：实现 installFromUrl 方法（SDK 团队）

**文件位置**：`skills-core` 模块

**实现逻辑**：

```java
public class SkillPackageManagerImpl implements SkillPackageManager {
    
    @Override
    public CompletableFuture<InstallResult> installFromUrl(String downloadUrl, InstallFromUrlOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 下载技能包
                Path tempFile = downloadSkillPackage(downloadUrl);
                
                // 2. 校验 checksum（如果提供）
                if (options != null && options.isVerifyChecksum() && options.getChecksum() != null) {
                    verifyChecksum(tempFile, options.getChecksum());
                }
                
                // 3. 解压并安装
                SkillPackage pkg = extractAndInstall(tempFile);
                
                // 4. 清理临时文件
                Files.deleteIfExists(tempFile);
                
                // 5. 返回结果
                InstallResult result = new InstallResult();
                result.setSuccess(true);
                result.setSkillId(pkg.getSkillId());
                result.setVersion(pkg.getVersion());
                return result;
                
            } catch (Exception e) {
                InstallResult result = new InstallResult();
                result.setSuccess(false);
                result.setError(e.getMessage());
                return result;
            }
        });
    }
    
    private Path downloadSkillPackage(String url) throws IOException {
        // 下载逻辑
    }
    
    private void verifyChecksum(Path file, String expectedChecksum) throws IOException {
        // 校验逻辑
    }
    
    private SkillPackage extractAndInstall(Path file) throws IOException {
        // 解压安装逻辑
    }
}
```

### 任务 3：添加 InstallFromUrlOptions 类（SDK 团队）

**文件位置**：`skills-api` 模块

```java
package net.ooder.skills.api;

public class InstallFromUrlOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String skillId;
    private String version;
    private boolean verifyChecksum = true;
    private String checksum;
    private boolean installDependencies = true;
    private String mirrorUrl;
    
    // 构造函数
    public InstallFromUrlOptions() {}
    
    public static InstallFromUrlOptions defaultOptions() {
        return new InstallFromUrlOptions();
    }
    
    // Builder 模式
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private InstallFromUrlOptions options = new InstallFromUrlOptions();
        
        public Builder skillId(String skillId) {
            options.setSkillId(skillId);
            return this;
        }
        
        public Builder version(String version) {
            options.setVersion(version);
            return this;
        }
        
        public Builder verifyChecksum(boolean verify) {
            options.setVerifyChecksum(verify);
            return this;
        }
        
        public Builder checksum(String checksum) {
            options.setChecksum(checksum);
            return this;
        }
        
        public Builder installDependencies(boolean install) {
            options.setInstallDependencies(install);
            return this;
        }
        
        public Builder mirrorUrl(String url) {
            options.setMirrorUrl(url);
            return this;
        }
        
        public InstallFromUrlOptions build() {
            return options;
        }
    }
    
    // Getter/Setter
    // ...
}
```

### 任务 4：更新 GitDiscoveryController（Skills 团队）

**文件位置**：`skill-scene` 模块

**修改内容**：

```java
@PostMapping("/install")
public ResultModel<InstallResultDTO> installSkill(@RequestBody @Valid InstallSkillRequestDTO request) {
    log.info("[installSkill] skillId: {}, source: {}", request.getSkillId(), request.getSource());
    
    InstallResultDTO result = new InstallResultDTO();
    result.setSkillId(request.getSkillId());
    result.setInstallTime(System.currentTimeMillis());
    
    // 从 skill-index.yaml 获取下载 URL
    String downloadUrl = skillIndexLoader.getDownloadUrl(request.getSkillId());
    
    if (downloadUrl == null) {
        result.setStatus("failed");
        result.setMessage("Download URL not found for skill: " + request.getSkillId());
        return ResultModel.success(result);
    }
    
    if (skillPackageManager != null) {
        try {
            // 使用新方法从 URL 安装
            InstallFromUrlOptions options = InstallFromUrlOptions.builder()
                .skillId(request.getSkillId())
                .installDependencies(true)
                .build();
            
            InstallResultWithDependencies installResult = skillPackageManager
                .installFromUrlWithDependencies(downloadUrl, options)
                .get();
            
            if (installResult != null && installResult.isSuccess()) {
                result.setStatus("installed");
                result.setMessage("Skill installed successfully with " + 
                    installResult.getInstalledDependencies().size() + " dependencies");
                result.setInstalledDependencies(installResult.getInstalledDependencies());
                
                log.info("[installSkill] Skill {} installed successfully", request.getSkillId());
            } else {
                result.setStatus("failed");
                result.setMessage(installResult != null ? installResult.getError() : "Unknown error");
            }
        } catch (Exception e) {
            log.error("[installSkill] Error: {}", e.getMessage());
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }
    } else {
        result.setStatus("unavailable");
        result.setMessage("SkillPackageManager not available");
    }
    
    return ResultModel.success(result);
}
```

## 接口定义

### API 请求格式

```json
POST /api/v1/discovery/install
{
    "skillId": "skill-a2ui",
    "source": "GITEE",
    "version": "0.7.3"
}
```

### API 响应格式

```json
{
    "code": 200,
    "data": {
        "skillId": "skill-a2ui",
        "status": "installed",
        "message": "Skill installed successfully with 2 dependencies",
        "installTime": 1234567890,
        "installedDependencies": [
            "skill-llm-chat",
            "skill-vfs-local"
        ],
        "capabilities": [
            {
                "id": "generate-ui",
                "name": "UI生成",
                "type": "SERVICE"
            }
        ]
    }
}
```

## 验收标准

1. **功能验收**
   - [ ] SDK 支持从 URL 安装技能
   - [ ] 安装成功后技能可用
   - [ ] 依赖自动安装
   - [ ] checksum 校验正常

2. **性能验收**
   - [ ] 下载超时处理（默认 5 分钟）
   - [ ] 断点续传支持（可选）
   - [ ] 镜像地址自动切换

3. **兼容性验收**
   - [ ] 现有安装方法不受影响
   - [ ] 向后兼容

## 时间安排

| 阶段 | 任务 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| 第一阶段 | InstallFromUrlOptions 类 | SDK 团队 | 0.5 天 |
| 第二阶段 | installFromUrl 接口定义 | SDK 团队 | 0.5 天 |
| 第三阶段 | installFromUrl 实现 | SDK 团队 | 2 天 |
| 第四阶段 | GitDiscoveryController 更新 | Skills 团队 | 0.5 天 |
| 第五阶段 | 测试验证 | 双方团队 | 0.5 天 |

## 联系方式

- SDK 团队负责人：[待指定]
- Skills 团队负责人：[待指定]
- 技术评审：[待指定]

## 相关文档

- [skill-index.yaml 规范](../skill-index.yaml)
- [场景驱动安装闭环分析](./SDK_COLLABORATION_GITHUB_DISCOVERY.md)
- [SkillPackageManager 接口规范](../docs/SKILL-SPECIFICATION-V2.3.md)

---

创建时间：2026-03-05
更新时间：2026-03-05
状态：待分配
