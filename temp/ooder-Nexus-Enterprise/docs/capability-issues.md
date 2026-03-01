# 能力管理问题文档

## 发现日期
2026-02-25

## 问题概述
当前系统缺少完整的Skill生命周期管理后端实现，前端页面调用的API返回404。

---

## 一、已确认的问题清单

### 1.1 API缺失问题
| 期望API | 方法 | 实际状态 | 影响 |
|---------|------|----------|------|
| /api/skillcenter/sync/list | GET | 404 Not Found | 无法获取Skill列表 |
| /api/skillcenter/sync/upload | POST | 404 Not Found | 无法上传Skill |
| /api/skillcenter/sync/download/{id} | GET | 404 Not Found | 无法下载Skill |
| /api/skillcenter/sync/{id} | DELETE | 404 Not Found | 无法删除Skill |

### 1.2 数据不一致问题
- **Skill YAML配置** 与 **运行时返回数据** 完全不匹配
- SceneRuntimeService返回的是硬编码mock数据
- 运行时能力ID与skill.yaml定义的能力ID无关联

### 1.3 生命周期管理缺失
- 无SkillLifecycleListener监听器
- 无SkillRegistry注册中心
- 无SkillLoader动态加载器
- 无健康检查机制

### 1.4 存储问题
- SceneService使用内存存储(HashMap)，重启数据丢失
- 无持久化机制

---

## 二、需要SDK支持的观察者能力

### 2.1 必需的事件类型
```java
// Skill生命周期事件
SKILL_CREATED          // Skill被创建
SKILL_VALIDATED        // Skill验证通过
SKILL_LOADED           // Skill加载完成
SKILL_REGISTERED       // Skill注册成功
SKILL_STARTED          // Skill启动
SKILL_STOPPED          // Skill停止
SKILL_UNLOADED         // Skill卸载
SKILL_DELETED          // Skill删除
SKILL_ERROR            // Skill发生错误
SKILL_HEALTH_CHANGED   // 健康状态变化
```

### 2.2 必需的观察者接口
```java
public interface SkillLifecycleObserver {
    void onSkillEvent(SkillEvent event);
}

public interface SkillHealthObserver {
    void onHealthCheck(SkillHealthStatus status);
}
```

### 2.3 必需的查询能力
```java
// 获取Skill当前状态
SkillStatus getSkillStatus(String skillId);

// 获取Skill历史事件
List<SkillEvent> getSkillHistory(String skillId);

// 订阅Skill事件
void subscribe(String skillId, SkillLifecycleObserver observer);

// 取消订阅
void unsubscribe(String skillId, SkillLifecycleObserver observer);
```

---

## 三、黑盒探测计划

### 3.1 探测目标
1. SDK是否提供事件发布/订阅机制
2. SDK是否提供JMX或Actuator端点
3. SDK是否提供WebSocket或SSE事件流
4. SDK是否提供回调接口或钩子

### 3.2 探测方法
1. 扫描类路径查找Listener/Observer/Subscriber相关类
2. 检查Spring Event发布
3. 检查Actuator端点
4. 尝试动态代理拦截

---

## 四、状态
- [x] 问题发现
- [x] 问题验证
- [ ] SDK能力探测
- [ ] 协作请求（如需要）
