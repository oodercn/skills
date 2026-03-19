# 字典表规范与范例 v1.0

> **文档版本**: v1.0  
> **发布日期**: 2026-03-01  
> **适用范围**: Ooder 全模块开发  
> **文档状态**: 正式发布

---

## 一、概述

### 1.1 背景

在 Ooder 系统中，前端页面存在大量静态选择项数据（如能力类型、参与者类型、状态等）。这些数据硬编码在前端代码中，存在以下问题：

1. **维护困难**：数据分散在多个文件中，修改时需要逐一更新
2. **不可控**：前端数据与后端枚举可能不一致
3. **扩展性差**：新增选项需要修改前端代码并重新部署

### 1.2 解决方案

建立统一的字典表机制：
- 后端使用枚举类定义字典数据，通过注解标识元信息
- 提供统一的字典 API 供前端调用
- 前端使用字典缓存工具获取和展示字典数据

---

## 二、字典架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        字典表架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   后端枚举层                                                     │
│   ─────────                                                     │
│   @Dict(code="xxx", name="XXX")                                 │
│   public enum XxxType implements DictItem {                     │
│       VALUE1("code1", "名称1", "描述1", "icon1", 1),            │
│       VALUE2("code2", "名称2", "描述2", "icon2", 2);            │
│   }                                                             │
│                                                                 │
│   服务层                                                        │
│   ──────                                                        │
│   DictService                                                   │
│   ├── 自动扫描并注册所有 @Dict 枚举                              │
│   ├── 提供字典查询接口                                          │
│   └── 支持缓存刷新                                              │
│                                                                 │
│   API层                                                         │
│   ─────                                                         │
│   DictController                                                │
│   ├── GET /api/v1/dicts          获取所有字典                   │
│   ├── GET /api/v1/dicts/{code}   获取指定字典                   │
│   └── GET /api/v1/dicts/{code}/items/{itemCode}/name            │
│                                                                 │
│   前端层                                                        │
│   ──────                                                        │
│   DictCache 工具                                                │
│   ├── 初始化时预加载所有字典                                     │
│   ├── 提供同步/异步查询接口                                     │
│   └── 支持下拉框自动渲染                                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件

| 组件 | 位置 | 说明 |
|------|------|------|
| `@Dict` | 后端注解 | 标识枚举类为字典类型 |
| `DictItem` | 后端接口 | 字典项接口定义 |
| `DictDTO` | 后端DTO | 字典数据传输对象 |
| `DictItemDTO` | 后端DTO | 字典项数据传输对象 |
| `DictService` | 后端服务 | 字典管理服务 |
| `DictController` | 后端控制器 | 字典API控制器 |
| `DictCache` | 前端工具 | 字典缓存工具 |

---

## 三、后端实现规范

### 3.1 字典注解定义

```java
package net.ooder.skill.scene.dto.dict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    
    String code();
    
    String name() default "";
    
    String description() default "";
    
    boolean cacheable() default true;
}
```

### 3.2 字典项接口定义

```java
package net.ooder.skill.scene.dto.dict;

public interface DictItem {
    
    String getCode();
    
    String getName();
    
    String getDescription();
    
    String getIcon();
    
    int getSort();
}
```

### 3.3 枚举实现范例

#### 范例一：场景组状态

```java
package net.ooder.skill.scene.dto.scene;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "scene_group_status", name = "场景组状态", description = "场景组的运行状态")
public enum SceneGroupStatus implements DictItem {
    
    DRAFT("DRAFT", "草稿", "场景组草稿状态", "ri-draft-line", 1),
    CREATING("CREATING", "创建中", "场景组正在创建", "ri-loader-4-line", 2),
    CONFIGURING("CONFIGURING", "配置中", "场景组正在配置", "ri-settings-4-line", 3),
    PENDING("PENDING", "待激活", "场景组等待激活", "ri-time-line", 4),
    ACTIVE("ACTIVE", "运行中", "场景组正常运行", "ri-play-circle-line", 5),
    SUSPENDED("SUSPENDED", "已暂停", "场景组已暂停", "ri-pause-circle-line", 6),
    SCALING("SCALING", "扩缩容中", "场景组正在扩缩容", "ri-expand-diagonal-line", 7),
    MIGRATING("MIGRATING", "迁移中", "场景组正在迁移", "ri-route-line", 8),
    DESTROYING("DESTROYING", "销毁中", "场景组正在销毁", "ri-delete-bin-line", 9),
    DESTROYED("DESTROYED", "已销毁", "场景组已销毁", "ri-skull-line", 10),
    ERROR("ERROR", "错误", "场景组运行错误", "ri-error-warning-line", 11);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    SceneGroupStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public String getIcon() { return icon; }

    @Override
    public int getSort() { return sort; }
}
```

#### 范例二：参与者类型

```java
package net.ooder.skill.scene.dto.scene;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;

@Dict(code = "participant_type", name = "参与者类型", description = "场景参与者的类型")
public enum ParticipantType implements DictItem {
    
    USER("USER", "用户", "人类用户参与者", "ri-user-line", 1),
    AGENT("AGENT", "Agent", "智能代理参与者", "ri-robot-line", 2),
    SUPER_AGENT("SUPER_AGENT", "超级Agent", "超级智能代理参与者", "ri-robot-2-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ParticipantType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public String getIcon() { return icon; }

    @Override
    public int getSort() { return sort; }
}
```

### 3.4 字典服务实现

```java
package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictDTO;
import net.ooder.skill.scene.dto.dict.DictItem;
import net.ooder.skill.scene.dto.dict.DictItemDTO;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DictService {

    private final Map<String, DictDTO> dictCache = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends Enum<?>>> dictEnumRegistry = new ConcurrentHashMap<>();

    public DictService() {
        init();
    }

    private synchronized void init() {
        registerDictEnum(net.ooder.skill.scene.capability.model.CapabilityType.class);
        registerDictEnum(net.ooder.skill.scene.dto.scene.ParticipantType.class);
        registerDictEnum(net.ooder.skill.scene.dto.scene.SceneGroupStatus.class);
    }

    public void registerDictEnum(Class<? extends Enum<?>> enumClass) {
        Dict dictAnnotation = enumClass.getAnnotation(Dict.class);
        if (dictAnnotation == null) {
            return;
        }

        String code = dictAnnotation.code();
        String name = dictAnnotation.name().isEmpty() ? enumClass.getSimpleName() : dictAnnotation.name();
        String description = dictAnnotation.description();

        DictDTO dictDTO = new DictDTO(code, name, description);
        List<DictItemDTO> items = new ArrayList<>();

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant instanceof DictItem) {
                DictItem dictItem = (DictItem) enumConstant;
                DictItemDTO itemDTO = new DictItemDTO(
                    dictItem.getCode(),
                    dictItem.getName(),
                    dictItem.getDescription(),
                    dictItem.getIcon(),
                    dictItem.getSort()
                );
                items.add(itemDTO);
            }
        }

        items.sort(Comparator.comparingInt(DictItemDTO::getSort));
        dictDTO.setItems(items);

        if (dictAnnotation.cacheable()) {
            dictCache.put(code, dictDTO);
        }
        dictEnumRegistry.put(code, enumClass);
    }

    public DictDTO getDict(String code) {
        return dictCache.get(code);
    }

    public List<DictItemDTO> getDictItems(String code) {
        DictDTO dict = dictCache.get(code);
        return dict != null ? dict.getItems() : new ArrayList<>();
    }

    public String getDictItemName(String code, String itemCode) {
        for (DictItemDTO item : getDictItems(code)) {
            if (item.getCode().equals(itemCode)) {
                return item.getName();
            }
        }
        return itemCode;
    }
}
```

### 3.5 字典控制器实现

```java
package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.dict.DictDTO;
import net.ooder.skill.scene.dto.dict.DictItemDTO;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.service.DictService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dicts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping
    public ResultModel<List<DictDTO>> getAllDicts() {
        return ResultModel.success(dictService.getAllDicts());
    }

    @GetMapping("/{code}")
    public ResultModel<DictDTO> getDict(@PathVariable String code) {
        DictDTO dict = dictService.getDict(code);
        if (dict == null) {
            return ResultModel.notFound("字典不存在: " + code);
        }
        return ResultModel.success(dict);
    }

    @GetMapping("/{code}/items")
    public ResultModel<List<DictItemDTO>> getDictItems(@PathVariable String code) {
        return ResultModel.success(dictService.getDictItems(code));
    }

    @GetMapping("/{code}/items/{itemCode}/name")
    public ResultModel<String> getDictItemName(
            @PathVariable String code,
            @PathVariable String itemCode) {
        return ResultModel.success(dictService.getDictItemName(code, itemCode));
    }
}
```

---

## 四、前端实现规范

### 4.1 字典缓存工具

```javascript
var DictCache = (function() {
    var cache = {};
    var loadingPromises = {};

    var DICT_CODES = {
        CAPABILITY_TYPE: 'capability_type',
        PARTICIPANT_TYPE: 'participant_type',
        SCENE_GROUP_STATUS: 'scene_group_status'
    };

    function getDict(code) {
        return new Promise(function(resolve, reject) {
            if (cache[code]) {
                resolve(cache[code]);
                return;
            }

            if (loadingPromises[code]) {
                loadingPromises[code].then(resolve).catch(reject);
                return;
            }

            loadingPromises[code] = fetch('/api/v1/dicts/' + code)
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        cache[code] = result.data;
                        resolve(result.data);
                    } else {
                        reject(new Error('Failed to load dict: ' + code));
                    }
                })
                .finally(function() { delete loadingPromises[code]; });

            loadingPromises[code].then(resolve).catch(reject);
        });
    }

    function getDictItems(code) {
        return getDict(code).then(function(dict) { return dict.items || []; });
    }

    function getDictItemName(code, itemCode) {
        return getDictItems(code).then(function(items) {
            for (var i = 0; i < items.length; i++) {
                if (items[i].code === itemCode) {
                    return items[i].name;
                }
            }
            return itemCode;
        });
    }

    function init() {
        var promises = [];
        for (var key in DICT_CODES) {
            if (DICT_CODES.hasOwnProperty(key)) {
                promises.push(getDict(DICT_CODES[key]));
            }
        }
        return Promise.all(promises);
    }

    return {
        DICT_CODES: DICT_CODES,
        init: init,
        getDict: getDict,
        getDictItems: getDictItems,
        getDictItemName: getDictItemName
    };
})();
```

### 4.2 页面集成范例

#### 替换前（硬编码）

```javascript
const CAPABILITY_TYPES = {
    'DRIVER': { name: '驱动类型', icon: 'ri-hard-drive-2-line' },
    'SERVICE': { name: '服务类型', icon: 'ri-server-line' },
    'AI': { name: 'AI类型', icon: 'ri-brain-line' }
};

function getTypeName(type) {
    return CAPABILITY_TYPES[type]?.name || type;
}
```

#### 替换后（使用字典）

```javascript
let capabilityTypesDict = {};

document.addEventListener('DOMContentLoaded', async function() {
    await initDicts();
});

async function initDicts() {
    if (typeof DictCache !== 'undefined') {
        await DictCache.init();
        const dict = await DictCache.getDict(DictCache.DICT_CODES.CAPABILITY_TYPE);
        if (dict && dict.items) {
            dict.items.forEach(item => {
                capabilityTypesDict[item.code] = { name: item.name, icon: item.icon };
            });
        }
    }
}

function getTypeName(type) {
    return capabilityTypesDict[type]?.name || type;
}
```

---

## 五、字典代码规范

### 5.1 命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 字典代码 | 小写下划线 | `scene_group_status` |
| 字典项代码 | 大写下划线 | `ACTIVE`, `SUSPENDED` |
| 枚举类名 | 大驼峰 | `SceneGroupStatus` |

### 5.2 排序规范

- 使用 `sort` 字段控制显示顺序
- 建议预留排序空间（如 1, 2, 3 或 10, 20, 30）
- 特殊类型可使用较大的排序值（如 99 表示"其他"）

### 5.3 图标规范

- 使用 Remix Icon 图标库
- 图标名称格式：`ri-{name}-line` 或 `ri-{name}-fill`
- 图标应与字典项含义相关

---

## 六、现有字典清单

| 字典代码 | 字典名称 | 枚举类 |
|----------|----------|--------|
| `capability_type` | 能力类型 | CapabilityType |
| `participant_type` | 参与者类型 | ParticipantType |
| `participant_role` | 参与者角色 | ParticipantRole |
| `participant_status` | 参与者状态 | ParticipantStatus |
| `scene_group_status` | 场景组状态 | SceneGroupStatus |
| `scene_type` | 场景类型 | SceneType |
| `connector_type` | 连接器类型 | ConnectorType |
| `capability_provider_type` | 能力提供者类型 | CapabilityProviderType |
| `capability_binding_status` | 能力绑定状态 | CapabilityBindingStatus |
| `template_status` | 模板状态 | TemplateStatus |
| `template_category` | 模板分类 | TemplateCategory |

---

## 七、扩展指南

### 7.1 新增字典步骤

1. 创建枚举类，实现 `DictItem` 接口
2. 添加 `@Dict` 注解
3. 在 `DictService.init()` 中注册枚举类
4. 在前端 `DictCache.DICT_CODES` 中添加字典代码常量

### 7.2 新增字典项步骤

1. 在对应枚举类中添加新的枚举值
2. 设置正确的排序值
3. 重启服务或调用刷新接口

---

## 附录

### A. 相关文档

- [公共技术规范](COMMON_TECHNICAL_SPECIFICATION.md)
- [术语表](GLOSSARY.md)
