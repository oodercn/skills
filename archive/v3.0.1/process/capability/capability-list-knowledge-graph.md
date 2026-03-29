# 能力列表功能知识图谱与重构方案

> **文档版本**: v1.0  
> **创建日期**: 2024-03-10  
> **目的**: 统一能力列表功能，解决分类不一致、代码重复等问题

---

## 一、知识图谱

### 1.1 能力分类体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          能力分类体系（统一后）                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     CapabilityType（能力类型）                       │   │
│  │  ─────────────────────────────────────────────────────────────────  │   │
│  │  ATOMIC      │ 原子能力    │ 最小不可分割的能力单元                   │   │
│  │  COMPOSITE   │ 组合能力    │ 多个原子能力组合                        │   │
│  │  SCENE       │ 场景特性    │ 场景特有的能力                          │   │
│  │  DRIVER      │ 驱动能力    │ 驱动场景执行的能力                      │   │
│  │  COLLABORATIVE│ 协作能力   │ 多人协作的能力                          │   │
│  │  SERVICE     │ 服务能力    │ 后台服务类能力                          │   │
│  │  AI          │ AI能力      │ 大模型相关能力                          │   │
│  │  TOOL        │ 工具能力    │ 独立工具类能力                          │   │
│  │  CONNECTOR   │ 连接器      │ 外部系统连接能力                        │   │
│  │  DATA        │ 数据能力    │ 数据处理能力                            │   │
│  │  MANAGEMENT  │ 管理能力    │ 系统管理能力                            │   │
│  │  COMMUNICATION│ 通信能力   │ 通信相关能力                            │   │
│  │  SECURITY    │ 安全能力    │ 安全相关能力                            │   │
│  │  MONITORING  │ 监控能力    │ 监控相关能力                            │   │
│  │  SKILL       │ 技能包      │ 完整的技能包                            │   │
│  │  SCENE_GROUP │ 场景组      │ 场景组能力                              │   │
│  │  CUSTOM      │ 自定义      │ 用户自定义能力                          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   CapabilityOwnership（能力归属）                    │   │
│  │  ─────────────────────────────────────────────────────────────────  │   │
│  │  SCENE_INTERNAL │ SIC │ 场景内部能力 │ 依附于场景，不可独立使用       │   │
│  │  INDEPENDENT    │ IC  │ 独立能力     │ 可独立使用，支持多场景         │   │
│  │  PLATFORM       │ PC  │ 平台能力     │ 平台基础能力，全局可用         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   SceneSkillCategory（场景技能分类）                 │   │
│  │  ─────────────────────────────────────────────────────────────────  │   │
│  │  ABS │ 自驱业务场景 │ hasSelfDrive=true, businessScore>=8           │   │
│  │  ASS │ 自驱系统场景 │ hasSelfDrive=true, businessScore<8            │   │
│  │  TBS │ 触发业务场景 │ hasSelfDrive=false, businessScore>=8          │   │
│  │  OTHER │ 其他      │ 非场景技能                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 分类关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          分类关系图                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                         ┌──────────────────┐                                │
│                         │   Capability     │                                │
│                         └────────┬─────────┘                                │
│                                  │                                          │
│              ┌───────────────────┼───────────────────┐                      │
│              │                   │                   │                      │
│              ▼                   ▼                   ▼                      │
│    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐             │
│    │ CapabilityType  │ │CapabilityOwner- │ │SceneSkillCate-  │             │
│    │    (类型)       │ │    ship(归属)    │ │    gory(分类)   │             │
│    └────────┬────────┘ └────────┬────────┘ └────────┬────────┘             │
│             │                   │                   │                       │
│             │                   │                   │                       │
│    ┌────────┴────────┐ ┌────────┴────────┐ ┌────────┴────────┐             │
│    │ • ATOMIC        │ │ • SCENE_INTERNAL│ │ • ABS           │             │
│    │ • COMPOSITE     │ │   (SIC)         │ │ • ASS           │             │
│    │ • SCENE         │ │ • INDEPENDENT   │ │ • TBS           │             │
│    │ • SERVICE       │ │   (IC)          │ │ • OTHER         │             │
│    │ • TOOL          │ │ • PLATFORM      │ │                 │             │
│    │ • ...           │ │   (PC)          │ │                 │             │
│    └─────────────────┘ └─────────────────┘ └─────────────────┘             │
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐    │
│    │                        映射关系                                  │    │
│    │  ─────────────────────────────────────────────────────────────  │    │
│    │  SCENE_INTERNAL + scene-skill + hasSelfDrive + score>=8 → ABS  │    │
│    │  SCENE_INTERNAL + scene-skill + hasSelfDrive + score<8  → ASS  │    │
│    │  SCENE_INTERNAL + scene-skill + !hasSelfDrive          → TBS  │    │
│    │  其他情况                                              → OTHER │    │
│    └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 数据流向图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据流向图                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                   │
│  │ skill.yaml  │────►│ SkillSync   │────►│ Capability  │                   │
│  │ (文件系统)  │     │ Service     │     │ Registry    │                   │
│  └─────────────┘     └─────────────┘     └──────┬──────┘                   │
│                                                  │                          │
│                                                  ▼                          │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                   │
│  │ Category    │◄────│ Capability  │◄────│ Capability  │                   │
│  │ Detector    │     │ Service     │     │ Controller  │                   │
│  └─────────────┘     └─────────────┘     └──────┬──────┘                   │
│         │                                        │                          │
│         │ 分类计算                               │ REST API                 │
│         ▼                                        ▼                          │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                   │
│  │ Business    │     │ JSON        │     │ 前端页面    │                   │
│  │ Semantics   │     │ Storage     │     │ • my-capabilities.html         │
│  │ Scorer      │     │             │     │ • capability-discovery.html    │
│  └─────────────┘     └─────────────┘     └─────────────┘                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、常见问题与错误

### 2.1 问题清单

| 问题ID | 问题描述 | 影响范围 | 严重程度 |
|:------:|----------|----------|:--------:|
| P001 | 前后端分类体系不一致 | 全局 | 🔴 高 |
| P002 | 存在两个CapabilityDTO类 | 后端 | 🟡 中 |
| P003 | 前端硬编码技能列表 | 前端 | 🔴 高 |
| P004 | 分类逻辑重复实现 | 前后端 | 🟡 中 |
| P005 | 缺少分类字段同步 | 数据 | 🔴 高 |
| P006 | API返回数据不完整 | 后端 | 🟡 中 |
| P007 | 过滤器统计不准确 | 前端 | 🟡 中 |
| P008 | 能力发现数量不对 | 前端 | 🔴 高 |

### 2.2 问题详细分析

#### P001: 前后端分类体系不一致

**问题描述**：
```
后端使用:
├── CapabilityOwnership: SCENE_INTERNAL, INDEPENDENT, PLATFORM
└── SceneSkillCategory: ABS, ASS, TBS

前端使用:
├── SIC (场景内部能力)
├── IC (独立能力)
├── PC (平台能力)
└── TOOL (工具能力)

映射关系:
SCENE_INTERNAL ↔ SIC
INDEPENDENT    ↔ IC
PLATFORM       ↔ PC
TOOL           ↔ 无对应（前端自定义）
```

**影响**：
- 前端显示与后端数据不一致
- 新增能力需要同时修改前后端

#### P002: 存在两个CapabilityDTO类

**问题描述**：
```
dto/CapabilityDTO.java (简单版)
├── capId, sceneId, name, description
└── type, category

dto/discovery/CapabilityDTO.java (完整版)
├── id, name, type, description, version
├── source, status, skillId
├── isSceneCapability, category, mainFirst
├── visibility, driverConditions, participants
└── metadata
```

**影响**：
- 代码重复
- 维护成本高
- 字段不一致导致数据丢失

#### P003: 前端硬编码技能列表

**问题描述**：
```javascript
// my-capabilities.js 中硬编码了 50+ 个技能ID
var SIC_SKILLS = ['skill-llm-chat', 'skill-knowledge-qa', ...];
var IC_SKILLS = ['skill-mqtt', 'skill-knowledge-base', ...];
var PC_SKILLS = ['skill-user-auth', 'skill-vfs-local', ...];
var TOOL_SKILLS = ['skill-openwrt', 'skill-trae-solo'];
```

**影响**：
- 新增技能需要修改前端代码
- 与后端数据不同步
- 维护成本极高

#### P005: 缺少分类字段同步

**问题描述**：
```
skill.yaml 中定义:
category: abs
hasSelfDrive: true
businessSemanticsScore: 9

但扫描后 Capability 对象中:
category = null (未同步)
hasSelfDrive = null (未同步)
businessSemanticsScore = null (未同步)
```

**影响**：
- 分类计算依赖默认值
- 导致所有能力被归类为 OTHER 或 TBS

---

## 三、完整解决方案

### 3.1 方案概述

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          解决方案架构                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Step 1: 统一数据模型                                                       │
│  ├── 合并两个 CapabilityDTO                                                │
│  ├── 添加完整的分类字段                                                     │
│  └── 建立字段映射关系                                                       │
│                                                                             │
│  Step 2: 后端分类服务                                                       │
│  ├── 完善 SkillCapabilitySyncService                                       │
│  ├── 实现 SceneSkillCategoryDetector                                       │
│  └── 提供分类计算API                                                        │
│                                                                             │
│  Step 3: 前端重构                                                           │
│  ├── 移除硬编码列表                                                         │
│  ├── 使用API获取分类                                                        │
│  └── 统一过滤和显示逻辑                                                     │
│                                                                             │
│  Step 4: 页面整合                                                           │
│  ├── 合并重复页面                                                           │
│  ├── 统一能力列表组件                                                       │
│  └── 更新菜单配置                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Step 1: 统一数据模型

#### 1.1 合并CapabilityDTO

```java
/**
 * 统一的能力DTO - 替代原有的两个DTO
 */
public class CapabilityDTO {
    
    // ===== 基础信息 =====
    private String id;
    private String capabilityId;
    private String name;
    private String description;
    private String version;
    private String icon;
    
    // ===== 类型与分类 =====
    private String type;                    // CapabilityType
    private String ownership;               // CapabilityOwnership: SIC/IC/PC
    private String category;                // SceneSkillCategory: ABS/ASS/TBS/OTHER
    
    // ===== 场景技能属性 =====
    private boolean isSceneCapability;
    private boolean hasSelfDrive;
    private boolean mainFirst;
    private Integer businessSemanticsScore;
    
    // ===== 来源与状态 =====
    private String source;                  // 来源：LOCAL/GITHUB/GITEE
    private String status;                  // 状态：installed/available
    private String skillId;                 // 所属技能ID
    private String parentScene;             // 父场景ID
    
    // ===== 功能配置 =====
    private List<String> capabilities;
    private List<String> supportedSceneTypes;
    private List<String> dependencies;
    private List<Map<String, Object>> driverConditions;
    private List<Map<String, Object>> participants;
    
    // ===== 元数据 =====
    private String visibility;              // 可见性：public/internal
    private Map<String, Object> metadata;
    private Long createdAt;
    private Long updatedAt;
    
    // ===== 计算属性 =====
    private Boolean installed;              // 是否已安装
    private String installUrl;              // 安装链接
}
```

#### 1.2 skill.yaml字段规范

```yaml
# skill.yaml 标准字段定义
id: skill-document-assistant
name: 智能文档助手
version: 1.0.0
description: AI驱动的文档处理助手
icon: ri-file-text-line

# 类型与分类
type: scene-skill                    # 必填：scene-skill/service-skill/tool-skill等
ownership: SIC                       # 可选：SIC/IC/PC，不填则自动计算
category: ABS                        # 可选：ABS/ASS/TBS，不填则自动计算

# 场景技能属性
isSceneCapability: true              # 是否场景能力
hasSelfDrive: true                   # 是否自驱
mainFirst: true                      # 是否主要优先
businessSemanticsScore: 9            # 业务语义分数 (1-10)

# 可见性
visibility: public                   # public/internal

# 依赖
dependencies:
  - skill-llm-openai
  - skill-knowledge-base

# 能力列表
capabilities:
  - document-summarize
  - document-translate
  - document-extract
```

### 3.3 Step 2: 后端分类服务

#### 2.1 完善SkillCapabilitySyncService

```java
@Service
public class SkillCapabilitySyncService {
    
    /**
     * 同步skill.yaml到Capability
     */
    public Capability syncFromSkillYaml(Path skillYamlPath) {
        Map<String, Object> yaml = parseYaml(skillYamlPath);
        
        Capability cap = new Capability();
        
        // 基础信息
        cap.setCapabilityId(getString(yaml, "id"));
        cap.setName(getString(yaml, "name"));
        cap.setDescription(getString(yaml, "description"));
        cap.setVersion(getString(yaml, "version"));
        cap.setIcon(getString(yaml, "icon"));
        
        // 类型
        cap.setType(parseType(getString(yaml, "type")));
        
        // 场景技能属性
        cap.setSceneCapability(getBool(yaml, "isSceneCapability"));
        cap.setHasSelfDrive(getBool(yaml, "hasSelfDrive"));
        cap.setMainFirst(getBool(yaml, "mainFirst"));
        cap.setBusinessSemanticsScore(getInt(yaml, "businessSemanticsScore"));
        
        // 自动计算分类
        cap.setOwnership(calculateOwnership(cap));
        cap.setCategory(calculateCategory(cap));
        
        return cap;
    }
    
    /**
     * 计算能力归属
     */
    private CapabilityOwnership calculateOwnership(Capability cap) {
        // 如果已有明确归属，直接返回
        if (cap.getOwnership() != null) {
            return cap.getOwnership();
        }
        
        // 根据属性计算
        if (cap.getParentSkill() != null && cap.getParentScene() != null) {
            return CapabilityOwnership.SCENE_INTERNAL;
        } else if (cap.getSupportedSceneTypes() != null && !cap.getSupportedSceneTypes().isEmpty()) {
            return CapabilityOwnership.INDEPENDENT;
        } else {
            return CapabilityOwnership.PLATFORM;
        }
    }
    
    /**
     * 计算场景技能分类
     */
    private SceneSkillCategory calculateCategory(Capability cap) {
        // 如果已有明确分类，直接返回
        if (cap.getCategory() != null) {
            return cap.getCategory();
        }
        
        // 非场景能力
        if (!cap.isSceneCapability()) {
            return SceneSkillCategory.OTHER;
        }
        
        boolean hasSelfDrive = cap.isHasSelfDrive();
        int score = cap.getBusinessSemanticsScore() != null ? cap.getBusinessSemanticsScore() : 5;
        
        if (hasSelfDrive && score >= 8) {
            return SceneSkillCategory.ABS;
        } else if (hasSelfDrive && score < 8) {
            return SceneSkillCategory.ASS;
        } else {
            return SceneSkillCategory.TBS;
        }
    }
}
```

#### 2.2 新增分类API

```java
@RestController
@RequestMapping("/api/v1/capabilities")
public class CapabilityController {
    
    /**
     * 获取能力分类统计
     */
    @GetMapping("/stats/by-category")
    public ResultModel<Map<String, Long>> getStatsByCategory() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("ABS", capabilityService.countByCategory(SceneSkillCategory.ABS));
        stats.put("ASS", capabilityService.countByCategory(SceneSkillCategory.ASS));
        stats.put("TBS", capabilityService.countByCategory(SceneSkillCategory.TBS));
        stats.put("OTHER", capabilityService.countByCategory(SceneSkillCategory.OTHER));
        return ResultModel.success(stats);
    }
    
    /**
     * 获取能力归属统计
     */
    @GetMapping("/stats/by-ownership")
    public ResultModel<Map<String, Long>> getStatsByOwnership() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("SIC", capabilityService.countByOwnership(CapabilityOwnership.SCENE_INTERNAL));
        stats.put("IC", capabilityService.countByOwnership(CapabilityOwnership.INDEPENDENT));
        stats.put("PC", capabilityService.countByOwnership(CapabilityOwnership.PLATFORM));
        return ResultModel.success(stats);
    }
    
    /**
     * 获取能力列表（支持分类过滤）
     */
    @GetMapping
    public ResultModel<List<CapabilityDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String ownership,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean installed) {
        
        List<CapabilityDTO> list = capabilityService.findAll(
            CapabilityQuery.builder()
                .type(type)
                .ownership(ownership)
                .category(category)
                .keyword(keyword)
                .installed(installed)
                .build()
        );
        
        return ResultModel.success(list);
    }
}
```

### 3.4 Step 3: 前端重构

#### 3.1 统一能力服务

```javascript
// capability-service.js - 统一的能力服务

var CapabilityService = {
    
    // 缓存
    _cache: {
        all: null,
        byOwnership: {},
        byCategory: {}
    },
    
    /**
     * 获取所有能力
     */
    getAll: function(forceRefresh) {
        if (this._cache.all && !forceRefresh) {
            return Promise.resolve(this._cache.all);
        }
        
        return ApiClient.get('/api/v1/capabilities')
            .then(function(result) {
                CapabilityService._cache.all = result.data || [];
                return CapabilityService._cache.all;
            });
    },
    
    /**
     * 按归属获取能力
     */
    getByOwnership: function(ownership) {
        return this.getAll().then(function(list) {
            return list.filter(function(cap) {
                return cap.ownership === ownership;
            });
        });
    },
    
    /**
     * 按分类获取能力
     */
    getByCategory: function(category) {
        return this.getAll().then(function(list) {
            return list.filter(function(cap) {
                return cap.category === category;
            });
        });
    },
    
    /**
     * 获取分类统计
     */
    getCategoryStats: function() {
        return ApiClient.get('/api/v1/capabilities/stats/by-category');
    },
    
    /**
     * 获取归属统计
     */
    getOwnershipStats: function() {
        return ApiClient.get('/api/v1/capabilities/stats/by-ownership');
    },
    
    /**
     * 搜索能力
     */
    search: function(keyword, filters) {
        var params = { keyword: keyword };
        if (filters) {
            Object.assign(params, filters);
        }
        return ApiClient.get('/api/v1/capabilities', params);
    },
    
    /**
     * 清除缓存
     */
    clearCache: function() {
        this._cache = { all: null, byOwnership: {}, byCategory: {} };
    }
};
```

#### 3.2 统一能力列表组件

```javascript
// capability-list-component.js - 统一的能力列表组件

var CapabilityListComponent = {
    
    render: function(container, options) {
        var config = Object.assign({
            showFilter: true,
            showSearch: true,
            showStats: true,
            filters: ['all', 'SIC', 'IC', 'PC', 'ABS', 'TBS', 'new', 'installed'],
            onItemClick: null,
            onInstall: null
        }, options);
        
        var html = '<div class="capability-list-container">' +
            this._renderHeader(config) +
            this._renderFilters(config) +
            this._renderList(config) +
            this._renderPagination(config) +
            '</div>';
        
        container.innerHTML = html;
        this._bindEvents(container, config);
    },
    
    _renderFilters: function(config) {
        if (!config.showFilter) return '';
        
        return '<div class="capability-filters">' +
            '<span class="filter-chip active" data-filter="all">全部 <span class="filter-count" id="countAll">0</span></span>' +
            '<span class="filter-chip" data-filter="SIC">场景能力 <span class="filter-count" id="countSIC">0</span></span>' +
            '<span class="filter-chip" data-filter="IC">独立能力 <span class="filter-count" id="countIC">0</span></span>' +
            '<span class="filter-chip" data-filter="PC">平台能力 <span class="filter-count" id="countPC">0</span></span>' +
            '<span class="filter-chip" data-filter="ABS">ABS <span class="filter-count" id="countABS">0</span></span>' +
            '<span class="filter-chip" data-filter="TBS">TBS <span class="filter-count" id="countTBS">0</span></span>' +
            '<span class="filter-chip" data-filter="new">新能力 <span class="filter-count" id="countNew">0</span></span>' +
            '<span class="filter-chip" data-filter="installed">已安装 <span class="filter-count" id="countInstalled">0</span></span>' +
            '</div>';
    },
    
    _renderList: function(config) {
        return '<div class="capability-list" id="capabilityList">' +
            '<div class="loading">加载中...</div>' +
            '</div>';
    },
    
    loadData: function() {
        var self = this;
        
        CapabilityService.getAll().then(function(list) {
            self._data = list;
            self._updateStats();
            self._renderItems();
        });
    },
    
    _updateStats: function() {
        var stats = {
            all: this._data.length,
            SIC: 0, IC: 0, PC: 0,
            ABS: 0, ASS: 0, TBS: 0,
            new: 0, installed: 0
        };
        
        this._data.forEach(function(cap) {
            // 归属统计
            if (cap.ownership === 'SCENE_INTERNAL') stats.SIC++;
            else if (cap.ownership === 'INDEPENDENT') stats.IC++;
            else if (cap.ownership === 'PLATFORM') stats.PC++;
            
            // 分类统计
            if (cap.category === 'ABS') stats.ABS++;
            else if (cap.category === 'ASS') stats.ASS++;
            else if (cap.category === 'TBS') stats.TBS++;
            
            // 安装状态
            if (cap.installed) stats.installed++;
            else stats.new++;
        });
        
        // 更新UI
        Object.keys(stats).forEach(function(key) {
            var el = document.getElementById('count' + key.charAt(0).toUpperCase() + key.slice(1));
            if (el) el.textContent = stats[key];
        });
    },
    
    filter: function(filterType) {
        var filtered = this._data;
        
        switch (filterType) {
            case 'SIC':
                filtered = this._data.filter(function(c) { return c.ownership === 'SCENE_INTERNAL'; });
                break;
            case 'IC':
                filtered = this._data.filter(function(c) { return c.ownership === 'INDEPENDENT'; });
                break;
            case 'PC':
                filtered = this._data.filter(function(c) { return c.ownership === 'PLATFORM'; });
                break;
            case 'ABS':
                filtered = this._data.filter(function(c) { return c.category === 'ABS'; });
                break;
            case 'TBS':
                filtered = this._data.filter(function(c) { return c.category === 'TBS'; });
                break;
            case 'new':
                filtered = this._data.filter(function(c) { return !c.installed; });
                break;
            case 'installed':
                filtered = this._data.filter(function(c) { return c.installed; });
                break;
        }
        
        this._filteredData = filtered;
        this._renderItems();
    }
};
```

### 3.5 Step 4: 页面整合

#### 4.1 合并后的页面结构

```
原页面结构:
├── my-capabilities.html (我的能力)
├── capability-discovery.html (发现能力)
├── capability-management.html (能力管理)
├── scene-capabilities.html (场景能力)
├── installed-scene-capabilities.html (已安装场景能力)
└── capability-binding.html (能力绑定)

合并后页面结构:
├── capability-center.html (能力中心 - 统一入口)
│   ├── Tab: 技能市场 (发现能力)
│   ├── Tab: 我的能力 (已安装能力)
│   └── Tab: 能力绑定 (绑定管理)
└── capability-detail.html (能力详情)
```

#### 4.2 能力中心页面

```html
<!-- capability-center.html -->
<!DOCTYPE html>
<html>
<head>
    <title>能力中心</title>
</head>
<body>
    <div class="capability-center">
        <!-- 标签页导航 -->
        <div class="tabs">
            <div class="tab active" data-tab="market">
                <i class="ri-store-2-line"></i> 技能市场
            </div>
            <div class="tab" data-tab="my">
                <i class="ri-apps-line"></i> 我的能力
            </div>
            <div class="tab" data-tab="binding">
                <i class="ri-link"></i> 能力绑定
            </div>
        </div>
        
        <!-- 统一的过滤器和搜索 -->
        <div class="toolbar">
            <div class="filters" id="filters"></div>
            <div class="search">
                <input type="text" placeholder="搜索能力..." id="searchInput">
            </div>
        </div>
        
        <!-- 统计信息 -->
        <div class="stats" id="stats"></div>
        
        <!-- 能力列表 -->
        <div class="capability-list" id="capabilityList"></div>
    </div>
    
    <script src="js/capability-service.js"></script>
    <script src="js/capability-list-component.js"></script>
    <script src="js/pages/capability-center.js"></script>
</body>
</html>
```

---

## 四、实施计划

### 4.1 任务分解

| 任务ID | 任务描述 | 工作量 | 优先级 |
|--------|----------|:------:|:------:|
| T001 | 合并CapabilityDTO类 | 0.5天 | P0 |
| T002 | 完善SkillCapabilitySyncService | 1天 | P0 |
| T003 | 新增分类统计API | 0.5天 | P0 |
| T004 | 创建CapabilityService.js | 0.5天 | P0 |
| T005 | 创建CapabilityListComponent | 1天 | P0 |
| T006 | 创建capability-center.html | 1天 | P1 |
| T007 | 迁移my-capabilities功能 | 0.5天 | P1 |
| T008 | 迁移capability-discovery功能 | 0.5天 | P1 |
| T009 | 更新菜单配置 | 0.5天 | P1 |
| T010 | 删除冗余页面 | 0.5天 | P2 |
| T011 | 更新文档 | 0.5天 | P2 |

### 4.2 时间线

```
Week 1:
├── Day 1-2: T001, T002, T003 (后端重构)
└── Day 3-4: T004, T005 (前端服务层)

Week 2:
├── Day 1-2: T006, T007, T008 (页面整合)
├── Day 3: T009 (菜单配置)
└── Day 4-5: T010, T011 (清理和文档)
```

---

## 五、验收标准

### 5.1 功能验收

| 功能 | 验收标准 |
|------|----------|
| 分类显示 | 所有能力正确显示归属(SIC/IC/PC)和分类(ABS/ASS/TBS) |
| 过滤器 | 过滤器数量统计准确，点击过滤正确 |
| 搜索 | 搜索结果准确，支持模糊匹配 |
| 安装状态 | 已安装能力正确标记，不重复安装 |
| 页面整合 | 三个Tab功能正常，数据共享 |

### 5.2 代码验收

| 指标 | 标准 |
|------|------|
| 重复代码 | 删除所有硬编码技能列表 |
| DTO统一 | 只保留一个CapabilityDTO |
| API完整 | 所有分类相关API可用 |
| 前端组件化 | 能力列表使用统一组件 |

---

## 六、附录

### 6.1 相关文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| dto/CapabilityDTO.java | 保留 | 统一DTO |
| dto/discovery/CapabilityDTO.java | 删除 | 合并到上面 |
| SkillCapabilitySyncService.java | 修改 | 完善同步逻辑 |
| CapabilityController.java | 修改 | 新增API |
| my-capabilities.js | 重构 | 使用统一服务 |
| capability-discovery.js | 重构 | 使用统一服务 |
| my-capabilities.html | 删除 | 合并到capability-center |
| capability-discovery.html | 删除 | 合并到capability-center |
| capability-center.html | 新建 | 统一入口页面 |

### 6.2 API清单

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/capabilities | GET | 获取能力列表 |
| /api/v1/capabilities/{id} | GET | 获取能力详情 |
| /api/v1/capabilities/types | GET | 获取能力类型 |
| /api/v1/capabilities/ownerships | GET | 获取归属类型 |
| /api/v1/capabilities/categories | GET | 获取场景分类 |
| /api/v1/capabilities/stats/by-category | GET | 分类统计 |
| /api/v1/capabilities/stats/by-ownership | GET | 归属统计 |
| /api/v1/capabilities/sync | POST | 同步能力 |
