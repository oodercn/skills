# Skills DTO 转换规范检查列表

## 一、DTO 定义规范

### 1.1 DTO 命名规范
- [ ] DTO 类名以 `DTO` 结尾
- [ ] DTO 类位于 `dto` 包下
- [ ] DTO 类名清晰表达其用途（如 `UserDTO`、`LlmConfigDTO`）
- [ ] 避免使用通用名称（如 `DataDTO`、`InfoDTO`）

### 1.2 DTO 结构规范
- [ ] DTO 类只包含 getter/setter 方法，不包含业务逻辑
- [ ] DTO 类实现 `Serializable` 接口（如需序列化）
- [ ] DTO 类包含所有必要的字段，字段类型使用包装类型而非基本类型
- [ ] DTO 类字段使用正确的类型（避免过度使用 `Map<String, Object>`）
- [ ] DTO 类添加必要的校验注解（如 `@NotNull`、`@NotEmpty`）

### 1.3 DTO 字段规范
- [ ] 字段命名遵循驼峰命名法
- [ ] 字段类型与业务含义匹配
- [ ] 避免使用 `Object` 类型字段
- [ ] 复杂对象字段使用具体的 DTO 类型而非 Map
- [ ] 日期时间字段使用 `LocalDateTime` 或 `Long`（时间戳）

## 二、Entity 到 DTO 转换规范

### 2.1 转换位置规范
- [ ] Entity 到 DTO 的转换应在 Service 层完成
- [ ] Controller 层不应直接操作 Entity
- [ ] 转换逻辑应封装在独立的转换方法中（如 `convertToDTO()`）
- [ ] 避免在多个地方重复编写转换逻辑

### 2.2 转换实现规范
- [ ] 使用统一的转换工具或框架（推荐 MapStruct）
- [ ] 如手动转换，应在 Service 实现类中提供私有转换方法
- [ ] 转换方法命名规范：`convertToDTO(Entity entity)`、`convertToEntity(DTO dto)`
- [ ] 转换方法应处理 null 值情况
- [ ] 转换方法应处理复杂对象的序列化/反序列化

### 2.3 转换工具使用规范
- [ ] 优先使用 MapStruct 进行类型安全的转换
- [ ] 如使用 BeanUtils，需确保字段名称和类型完全一致
- [ ] 避免使用 JSON 序列化/反序列化进行简单对象转换
- [ ] 对于复杂对象（如 Map 字段），可使用 ObjectMapper

### 2.4 转换性能规范
- [ ] 批量转换时使用 Stream API 或循环，避免多次单独转换
- [ ] 避免在循环中重复创建 ObjectMapper 实例
- [ ] 对于频繁转换的对象，考虑使用缓存

## 三、DTO 到 Entity 转换规范

### 3.1 转换时机规范
- [ ] DTO 到 Entity 的转换应在 Service 层完成
- [ ] Controller 层接收 DTO，Service 层转换为 Entity
- [ ] 更新操作时，应先查询 Entity，再更新字段

### 3.2 转换实现规范
- [ ] 创建操作：将 DTO 转换为新的 Entity 实例
- [ ] 更新操作：查询现有 Entity，更新 DTO 中非 null 的字段
- [ ] 转换方法应处理部分更新场景
- [ ] 转换方法应验证必要字段的存在性

### 3.3 字段映射规范
- [ ] DTO 字段与 Entity 字段名称应保持一致
- [ ] 如字段名称不一致，应在转换方法中明确映射
- [ ] 忽略不应映射的字段（如密码确认字段）
- [ ] 处理字段类型转换（如 String 到 LocalDateTime）

## 四、Controller 层 DTO 使用规范

### 4.1 接收参数规范
- [ ] Controller 方法参数使用 DTO 类型，不使用 Entity
- [ ] 使用 `@RequestBody` 接收 POST/PUT 请求的 DTO
- [ ] 使用 `@Valid` 注解触发 DTO 校验
- [ ] 避免使用 `Map<String, Object>` 作为请求参数

### 4.2 返回结果规范
- [ ] Controller 方法返回值使用 DTO 类型
- [ ] 返回统一的结果包装类（如 `ResultModel<DTO>`）
- [ ] 列表查询返回 `List<DTO>` 或分页结果
- [ ] 避免直接返回 Entity 或 Map

### 4.3 Controller 层职责规范
- [ ] Controller 层不进行 DTO 与 Entity 的转换
- [ ] Controller 层不包含业务逻辑
- [ ] Controller 层只负责请求接收、参数校验、结果返回
- [ ] Controller 层调用 Service 层方法处理业务

## 五、Service 层 DTO 使用规范

### 5.1 Service 接口规范
- [ ] Service 接口方法参数和返回值使用 DTO 类型
- [ ] Service 接口不暴露 Entity 类型
- [ ] Service 接口定义清晰的业务方法

### 5.2 Service 实现规范
- [ ] Service 实现类负责 DTO 与 Entity 的转换
- [ ] Service 实现类调用 Repository 操作 Entity
- [ ] Service 实现类包含必要的业务逻辑
- [ ] Service 实现类处理事务边界

### 5.3 转换方法规范
- [ ] 提供私有 `convertToDTO(Entity entity)` 方法
- [ ] 提供私有 `convertToEntity(DTO dto)` 方法
- [ ] 提供私有 `convertToDTOList(List<Entity> entities)` 方法
- [ ] 转换方法应处理 null 检查

## 六、特殊场景处理规范

### 6.1 嵌套对象转换规范
- [ ] 嵌套对象也应使用 DTO 类型
- [ ] 提供嵌套对象的转换方法
- [ ] 避免在 DTO 中嵌套 Entity

### 6.2 集合转换规范
- [ ] 使用 Stream API 进行集合转换
- [ ] 提供批量转换方法
- [ ] 处理空集合情况

### 6.3 枚举类型转换规范
- [ ] DTO 中使用枚举类型或 String 类型
- [ ] Entity 中使用枚举类型或存储枚举的 name()
- [ ] 转换时正确处理枚举类型

### 6.4 日期时间转换规范
- [ ] DTO 中使用 `LocalDateTime` 或时间戳（Long）
- [ ] Entity 中使用 `LocalDateTime`
- [ ] 转换时处理时区问题
- [ ] JSON 序列化时使用统一格式

### 6.5 敏感信息处理规范
- [ ] DTO 中不包含密码等敏感信息
- [ ] 如需传输敏感信息，应加密
- [ ] 返回前端前清除敏感字段
- [ ] 日志中不打印敏感信息

## 七、Map<String, Object> 使用规范

### 7.1 使用场景限制
- [ ] 仅在动态字段场景使用 `Map<String, Object>`
- [ ] 已知结构的对象应使用强类型 DTO
- [ ] API 响应避免使用 `Map<String, Object>`

### 7.2 类型安全规范
- [ ] 使用 Map 时添加注释说明字段类型
- [ ] 取值时进行类型检查和转换
- [ ] 提供工具方法处理 Map 类型转换

### 7.3 文档规范
- [ ] 使用 Map 的方法应添加 JavaDoc 说明字段结构
- [ ] API 文档中明确 Map 字段的结构
- [ ] 提供示例说明 Map 字段的用法

## 八、异常处理规范

### 8.1 转换异常处理
- [ ] 转换方法应捕获并处理异常
- [ ] 转换失败应记录日志
- [ ] 转换失败应抛出明确的业务异常
- [ ] 不应吞掉转换异常

### 8.2 空值处理规范
- [ ] 转换方法应处理 null 输入
- [ ] 明确 null 字段的语义（不更新 vs 设置为 null）
- [ ] 使用 Optional 处理可能为 null 的值

## 九、测试规范

### 9.1 转换方法测试
- [ ] 为转换方法编写单元测试
- [ ] 测试正常转换场景
- [ ] 测试 null 值处理
- [ ] 测试字段类型转换

### 9.2 集成测试规范
- [ ] 测试 Controller 到 Service 的完整流程
- [ ] 测试 DTO 校验是否生效
- [ ] 测试异常情况的处理

## 十、代码质量规范

### 10.1 代码重复规范
- [ ] 避免在多处重复编写转换逻辑
- [ ] 提取公共转换方法到工具类
- [ ] 使用转换框架减少样板代码

### 10.2 可维护性规范
- [ ] 转换逻辑清晰易懂
- [ ] 添加必要的注释说明
- [ ] 字段变更时同步更新转换方法

### 10.3 性能优化规范
- [ ] 避免不必要的对象创建
- [ ] 批量操作使用批量转换
- [ ] 缓存频繁使用的 ObjectMapper 实例

---

## 检查结果统计

- 总检查项：XX 项
- 通过项：XX 项
- 不通过项：XX 项
- 不适用项：XX 项
- 通过率：XX%

## 问题汇总

| 序号 | 模块 | 文件 | 问题描述 | 严重程度 | 建议方案 |
|------|------|------|----------|----------|----------|
| 1    |      |      |          |          |          |

## 改进建议

1. 
2. 
3. 

---

**检查人：**
**检查日期：**
**版本：**
