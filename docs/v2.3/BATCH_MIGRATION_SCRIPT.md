# Skills批量迁移脚本

## 迁移策略

由于12个Skills的Java代码都需要迁移，采用以下批量处理方案�?
### 1. 创建通用API模板

每个Skill需要创建：
- `{SkillName}Api.java` - API接口
- `{SkillName}ApiImpl.java` - API实现
- `model/` - 模型类（从provider.model迁移�?
### 2. 批量迁移命令

```bash
#!/bin/bash

# 迁移skill-protocol
echo "Migrating skill-protocol..."
cd skills/skill-protocol
# 创建api目录
mkdir -p src/main/java/net/ooder/skill/protocol/api
mkdir -p src/main/java/net/ooder/skill/protocol/model

# 迁移模型�?mv src/main/java/net/ooder/skill/protocol/provider/model/protocol/* src/main/java/net/ooder/skill/protocol/model/ 2>/dev/null || true

# 删除旧provider目录
rm -rf src/main/java/net/ooder/skill/protocol/provider

cd ../..

# 对其他Skills重复相同操作...
```

### 3. Import替换规则

```bash
# 批量替换Import
find skills/ -name "*.java" -exec sed -i \
    -e 's/net\.ooder\.scene\.core\.Result/net.ooder.sdk.infra.utils.Result/g' \
    -e 's/net\.ooder\.scene\.core\.SceneEngine/net.ooder.sdk.api.scene.SkillContext/g' \
    -e 's/net\.ooder\.scene\.provider\./net.ooder.skill./g' \
    -e 's/net\.ooder\.scene\.provider\.model\./net.ooder.skill./g' \
    {} \;
```

### 4. 手动处理清单

以下文件需要手动迁移：

| Skill | 文件 | 操作 |
|-------|------|------|
| skill-protocol | ProtocolProviderImpl.java | 改为ProtocolApiImpl.java |
| skill-health | HealthProviderImpl.java | 改为HealthApiImpl.java |
| skill-hosting | HostingServiceImpl.java | 改为HostingApiImpl.java |
| skill-monitor | MonitorServiceImpl.java | 改为MonitorApiImpl.java |
| skill-network | NetworkServiceImpl.java | 改为NetworkApiImpl.java |
| skill-openwrt | OpenWrtDriverImpl.java | 改为OpenWrtApiImpl.java |
| skill-remote-terminal | RemoteTerminalServiceImpl.java | 改为RemoteTerminalApiImpl.java |
| skill-access-control | AccessControlServiceImpl.java | 改为AccessControlApiImpl.java |
| skill-audit | AuditServiceImpl.java | 改为AuditApiImpl.java |
| skill-search | ElasticSearchProvider.java | 改为SearchApiImpl.java |
| skill-report | DefaultReportProvider.java | 改为ReportApiImpl.java |
| skill-cmd-service | Command.java | 改为CmdApi.java |

## 实施建议

考虑到工作量和时间，建议�?
1. **分阶段实�?*�?   - Week 1: 迁移4个核心Skills (protocol, health, hosting, monitor)
   - Week 2: 迁移4个重要Skills (network, openwrt, remote-terminal, access-control)
   - Week 3: 迁移4个一般Skills (audit, search, report, cmd-service)

2. **团队协作**�?   - 2人并行开�?   - 每人负责2个Skills/�?
3. **质量保障**�?   - 每个Skill迁移后必须编译通过
   - 每个Skill迁移后必须通过单元测试

## 预计工时

- 12个Skills × 2�?= 24�?- 2人并�?= 12�?- 缓冲时间 = 3�?- **总计 = 15�?(3�?**
