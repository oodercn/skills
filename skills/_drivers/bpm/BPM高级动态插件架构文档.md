# BPM高级动态插件架构文档

## 文档信息
- **版本**: 1.0
- **创建日期**: 2026-04-09
- **来源**: 基于Swing XPDL设计器源码分析 (E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio)
- **目的**: 建立JS版本设计器的高级动态插件架构

---

## 目录
1. [扩展面板远程数据接口分析](#1-扩展面板远程数据接口分析)
2. [Table List集合操作类完整实现](#2-table-list集合操作类完整实现)
3. [集合类数据存储规则](#3-集合类数据存储规则)
4. [高级动态插件架构设计](#4-高级动态插件架构设计)
5. [实现路线图](#5-实现路线图)

---

## 1. 扩展面板远程数据接口分析

### 1.1 权限表达式面板 (XMLExpressionEditPanel)

**文件位置**: `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\XMLExpressionEditPanel.java`

#### 1.1.1 远程数据接口

| 接口类型 | 接口/服务 | 用途 | 数据来源 |
|---------|----------|------|---------|
| **表达式验证** | `FormulaService` | 验证表达式语法 | 本地公式引擎 |
| **变量获取** | `ExpressionParameter` | 获取可用变量列表 | BPM引擎 |
| **表达式模板** | `ResourceManager.getLanguageDependentString()` | 获取本地化表达式模板 | 配置文件 |

#### 1.1.2 关键代码分析

```java
// 表达式编辑面板初始化
public XMLExpressionEditPanel(XMLElement myOwner, int layout,
        boolean isVertical, boolean bigPanel, boolean wrapLines,
        boolean miniDimension) {
    
    // 创建文本编辑区域
    JTextArea jta = new JTextArea();
    
    // 双击打开表达式编辑对话框
    jta.addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent e) {
             XMLElementDialog ed = new XMLElementDialog(
                 (JFrame) BPD.getInstance().getPackageEditor().getWindow(), 
                 "编辑"
             );
         }
    });
    
    // 设置初始值
    jta.setText(myOwner.toValue().toString());
}
```

#### 1.1.3 表达式类型与实现类映射

| 表达式类型 | 实现类 | 适用场景 |
|-----------|--------|---------|
| Expression | `DefaultProcessListenerExpressionPar` | 流程监听器表达式 |
| Expression | `DefaultActivityListenerExpressionPar` | 活动监听器表达式 |
| Script | `DefaultClientProcessListenerExpressionPar` | 客户端流程脚本 |
| Script | `DefaultClientActivityListenerExpressionPar` | 客户端活动脚本 |
| Listener | 自定义类名 | 标准监听器 |

---

### 1.2 监听器面板 (Listeners/Listener)

**文件位置**: 
- `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\Listeners.java`
- `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\Listener.java`

#### 1.2.1 远程数据接口

| 接口 | 服务类 | 数据内容 |
|-----|--------|---------|
| `getProcessListeners()` | `WorkflowProcess` | 流程监听器配置列表 |
| `getActivityListeners()` | `WorkflowProcess` | 活动监听器配置列表 |
| `ResourceManager.getLanguageDependentString()` | 资源管理器 | 本地化显示文本 |

#### 1.2.2 监听器数据结构

```java
// 监听器属性定义
public class Listener extends XMLCollectionElement {
    public static final String PROCESS_TYPE = "Process";
    public static final String ACTIVITY_TYPE = "Activity";
    
    // 监听器名称
    private XMLAttribute listenerName = new XMLAttribute("Name");
    
    // 表达式事件类型
    private XMLAttribute expressionEventType = new XMLAttribute("ExpressionEventType");
    
    // 表达式监听器类型 (Expression/Script/Listener)
    private XMLAttribute expressionListenerType = new XMLAttribute("ExpressionListenerType");
    
    // 监听器事件
    private XMLAttribute listenerEvent = new XMLAttribute("ListenerEvent");
    
    // 表达式内容
    private XMLAttribute expressionStr = new XMLAttribute("expressionStr");
    
    // 实现类
    private XMLAttribute realizeClass = new XMLAttribute("RealizeClass");
}
```

#### 1.2.3 监听器类型切换逻辑

```java
public void typeChange(String textname) {
    if (ExpressionTypeEnums.Expression.getType().equals(textname)) {
        // 表达式模式
        realizeClass.setValue("com.ds.bpm.engine.event.DefaultProcessListenerExpressionPar");
        listenerName.setReadOnly(true);
        expressionStr.setReadOnly(false);
        expressionStr.setRequired(true);
    } else if (ExpressionTypeEnums.Listener.getType().equals(textname)) {
        // 监听器模式
        listenerName.setReadOnly(false);
        expressionStr.setReadOnly(true);
        expressionStr.setRequired(false);
    } else if (ExpressionTypeEnums.Script.getType().equals(textname)) {
        // 脚本模式
        realizeClass.setValue("com.ds.bpm.engine.event.DefaultClientProcessListenerExpressionPar");
        listenerName.setReadOnly(true);
        expressionStr.setReadOnly(false);
        expressionStr.setRequired(true);
    }
}
```

---

### 1.3 组织机构面板 (XMLOrgPanel/XMLPersonsPanel)

**文件位置**:
- `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\formula\org\XMLOrgPanel.java`
- `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\formula\org\XMLPersonsPanel.java`

#### 1.3.1 远程数据接口详解

| 服务接口 | 方法 | 用途 | 返回数据 |
|---------|------|------|---------|
| `JDSServer.getClusterClient()` | `getSystem()` | 获取当前系统 | `SubSystem` |
| `OrgManagerFactory.getClientOrgManager()` | - | 获取组织管理器 | `OrgManager` |
| `OrgManager` | `getTopOrgs(sysId)` | 获取顶级组织 | `List<Org>` |
| `OrgManager` | `getPersonByID(personId)` | 获取人员信息 | `Person` |
| `OrgManager` | `getOrgByID(orgId)` | 获取组织信息 | `Org` |
| `Org` | `getChildrenList()` | 获取子组织 | `List<Org>` |
| `Org` | `getPersonList()` | 获取组织人员 | `List<Person>` |

#### 1.3.2 远程数据获取代码

```java
// 获取系统配置
SubSystem system = JDSServer.getClusterClient()
    .getSystem(JDSServer.getInstance().getCurrServerBean().getId());

// 获取组织管理器
OrgManager manager = OrgManagerFactory.getClientOrgManager(system.getConfigname());

// 获取顶级组织
OrgTree orgTree = new OrgTree(manager.getTopOrgs(system.getSysId()));
JTree allOrgTree = new JTree(orgTree.getRoot());

// 懒加载子节点
allOrgTree.addTreeWillExpandListener(new TreeWillExpandListener() {
    public void treeWillExpand(TreeExpansionEvent tee) {
        TreePath selPath = tee.getPath();
        OrgTreeNode node = (OrgTreeNode) selPath.getLastPathComponent();
        Org org = (Org) node.getUserObject();
        
        // 动态增加子节点
        if (node.getChildren().length == 0) {
            List<Org> childrens = org.getChildrenList();
            for (Org corg : childrens) {
                node.insert(new OrgTreeNode(corg));
            }
        }
        
        // 更新人员列表
        allPersonList.setListData(org.getPersonList().toArray());
    }
});
```

#### 1.3.3 组织机构面板类型

| 面板类 | 用途 | 选择模式 |
|-------|------|---------|
| `XMLOrgPanel` | 组织机构选择 | 单选/多选 |
| `XMLPersonsPanel` | 人员选择 | 单选/多选 |
| `XMLPersonPanel` | 单人员选择 | 单选 |
| `XMLPersonRolePanel` | 人员角色选择 | 多选 |
| `XMLPersonLevelPanel` | 人员级别选择 | 多选 |
| `XMLPersonGroupPanel` | 人员组选择 | 多选 |
| `XMLPersonDutyPanel` | 人员职务选择 | 多选 |
| `XMLRightOrgPanel` | 权限组织选择 | 多选 |

---

### 1.4 其他扩展面板远程接口

#### 1.4.1 IoT设备面板

| 面板类 | 远程服务 | 数据类型 |
|-------|---------|---------|
| `XMLIOTAreaPanel` | IoT区域服务 | 区域信息 |
| `XMLIOTPlacePanel` | IoT位置服务 | 位置信息 |
| `XMLIOTGatewaysPanel` | 网关服务 | 网关设备 |
| `XMLIOTSensorsPanel` | 传感器服务 | 传感器数据 |
| `XMLIOTDeviceTypePanel` | 设备类型服务 | 设备类型 |
| `XMLIOTServiceTreePanel` | IoT服务树 | 服务列表 |

#### 1.4.2 ESD组件面板

| 面板类 | 远程服务 | 数据类型 |
|-------|---------|---------|
| `XMLESDModulesPanel` | 模块服务 | ESD模块 |
| `XMLESDModulePanel` | 单模块服务 | 模块详情 |
| `XMLESDComponentPanel` | 组件服务 | ESD组件 |
| `XMLESDApisPanel` | API服务 | 接口列表 |
| `XMLComTreePanel` | 组件树服务 | 组件树 |

#### 1.4.3 数据库面板

| 面板类 | 远程服务 | 数据类型 |
|-------|---------|---------|
| `XMLESDTablePanel` | 表服务 | 数据库表 |
| `XMLDBFieldsPanel` | 字段服务 | 表字段 |
| `XMLDBFieldPanel` | 单字段服务 | 字段详情 |

---

## 2. Table List集合操作类完整实现

### 2.1 XMLTablePanel - 基础表格面板

**文件位置**: `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\XMLTablePanel.java`

#### 2.1.1 核心功能

```java
public class XMLTablePanel extends XMLPanel {
    // 表格尺寸定义
    protected static Dimension miniTableDimension = new Dimension(300, 75);
    protected static Dimension smallTableDimension = new Dimension(300, 150);
    protected static Dimension mediumTableDimension = new Dimension(600, 200);
    protected static Dimension largeTableDimension = new Dimension(800, 200);
    
    public XMLTablePanel(XMLCollection myOwner, String title,
            boolean hasBorder, boolean automaticWidth, boolean miniDimension) {
        
        super(myOwner, 2, title, XMLPanel.BOX_LAYOUT, true, hasBorder);
        
        // 创建表格模型
        Vector columnNames = new Vector();
        columnNames.add("Object");  // 隐藏列，存储对象引用
        
        // 从集合元素结构获取列名
        Collection c = myOwner.getElementStructure();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            columnNames.add(el.toLabel());
        }
        
        // 创建不可编辑表格
        JTable allItems = new JTable(new Vector(), columnNames) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        // 填充数据
        DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
        it = myOwner.getTableElements().iterator();
        while (it.hasNext()) {
            XMLComplexElement elem = (XMLComplexElement) it.next();
            Vector v = new Vector(elem.toComplexTypeValues());
            v.add(0, elem);  // 第一列存储对象
            dtm.addRow(v);
        }
    }
}
```

#### 2.1.2 交互功能

| 操作 | 触发方式 | 功能 |
|-----|---------|------|
| 编辑 | 双击行 / Enter键 | 打开编辑对话框 |
| 删除 | Delete键 | 删除选中行 |
| 取消 | Escape键 | 关闭对话框 |

```java
// 双击编辑
allItems.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() > 1) {
            editElementDialog();
        }
    }
});

// 键盘快捷键
allItems.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "edit");
allItems.getActionMap().put("edit", new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
        editElementDialog();
    }
});
```

---

### 2.2 XMLTableControlPanel - 表格控制面板

**文件位置**: `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\XMLTableControlPanel.java`

#### 2.2.1 标准按钮配置

| 按钮 | 功能 | 图标 |
|-----|------|------|
| 添加 | 添加新元素 | AddSmall |
| 删除 | 删除选中元素 | RemoveSmall |
| 上移 | 调整顺序上移 | UpSmall |
| 下移 | 调整顺序下移 | DownSmall |

---

### 2.3 Listeners表格实现

**文件位置**: `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\Listeners.java`

```java
public class Listeners extends XMLCollection {
    
    public XMLElement generateNewElement() {
        Listener l = new Listener(this);
        l.setRequired(true);
        return l;
    }
    
    public XMLPanel getPanel() {
        // 创建表格面板
        controlledPanel = new XMLTablePanel(this, "", false, false, true);
        // 创建控制面板
        controlPanel = new XMLTableControlPanel(this, "", true, false);
        // 组合面板
        return new XMLGroupPanel(this, new XMLPanel[] { 
            controlledPanel, controlPanel 
        }, toLabel(), XMLPanel.BOX_LAYOUT, false, true);
    }
    
    // 隐藏第一列(ID列)
    public int[] getInvisibleTableFieldOrdinals() {
        return new int[] { 0 };
    }
}
```

---

## 3. 集合类数据存储规则

### 3.1 分隔符定义

**文件位置**: `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\formula\FormulaParameter.java`

```java
public class FormulaParameter extends XMLComplexElement {
    // 多值分隔符 - 用于存储集合数据
    public static final String DELIMITER_MULTIPLE = ":";
}
```

### 3.2 存储格式规则

#### 3.2.1 多值存储格式

| 数据类型 | 存储格式 | 示例 |
|---------|---------|------|
| 单值 | `value` | `user001` |
| 多值 | `value1:value2:value3` | `dept001:dept002:dept003` |
| 键值对 | `name1=value1:name2=value2` | `org=dept001:person=user001` |

#### 3.2.2 代码实现

```java
// 存储多值数据
public void setElements() {
    DefaultListModel model = (DefaultListModel) getList().getModel();
    String val = "";
    for (int i = 0; i < model.size(); i++) {
        Object obj = model.get(i);
        val = val + FormulaParameter.DELIMITER_MULTIPLE + ((XMLSelectOption) obj).getText();
    }
    if (!val.equals("")) {
        val = val.substring(FormulaParameter.DELIMITER_MULTIPLE.length());
    }
    getOwner().setValue(val);
}

// 读取多值数据
if (myOwner.toValue() != null && !myOwner.toValue().toString().equals("")) {
    String val = myOwner.toValue().toString();
    String[] vals = Utils.tokenize(val, FormulaParameter.DELIMITER_MULTIPLE);
    for (int i = 0; i < vals.length; i++) {
        String person_id = vals[i];
        if (!person_id.trim().equals("")) {
            String person_name = manager.getPersonByID(person_id).getName();
            if (person_name != null) {
                elements.add(new XMLSelectOption(person_name, person_id));
            }
        }
    }
}
```

### 3.3 XML存储格式

#### 3.3.1 Listeners XML格式

```xml
<itjds:Listeners xmlns:itjds="http://www.itjds.com/bpm">
    <itjds:Listener 
        Id="listener001"
        Name="流程启动监听"
        ListenerEvent="Process"
        RealizeClass="com.ds.bpm.engine.event.DefaultProcessListener"
        ExpressionEventType="processStart"
        ExpressionListenerType="Listener"
        expressionStr=""/>
    <itjds:Listener 
        Id="listener002"
        Name="表达式监听"
        ListenerEvent="Process"
        RealizeClass="com.ds.bpm.engine.event.DefaultProcessListenerExpressionPar"
        ExpressionEventType="processEnd"
        ExpressionListenerType="Expression"
        expressionStr="${process.status == 'completed'}"/>
</itjds:Listeners>
```

#### 3.3.2 权限组 XML格式

```xml
<itjds:RightGroups xmlns:itjds="http://www.itjds.com/bpm">
    <itjds:RightGroup Id="group001" Name="审批组">
        <itjds:Member Type="Person" Id="user001"/>
        <itjds:Member Type="Person" Id="user002"/>
        <itjds:Member Type="Org" Id="dept001"/>
    </itjds:RightGroup>
</itjds:RightGroups>
```

### 3.4 数据库存储映射

| 扩展属性 | 属性名 | 存储值格式 | 说明 |
|---------|-------|-----------|------|
| Listeners | `Listeners` | XML字符串 | 监听器配置 |
| RightGroups | `RightGroups` | XML字符串 | 权限组配置 |
| Performers | `Performers` | `id1:id2:id3` | 办理人ID列表 |
| Departments | `Departments` | `id1:id2:id3` | 部门ID列表 |
| Expression | `Expression` | 表达式字符串 | 权限表达式 |

---

## 4. 高级动态插件架构设计

### 4.1 架构总览

```
┌─────────────────────────────────────────────────────────────┐
│                    高级动态插件架构                           │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Expression │  │  Listener   │  │   Org/Per   │         │
│  │    Panel    │  │    Panel    │  │    Panel    │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                │
│         └────────────────┼────────────────┘                │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              DynamicPluginManager                   │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │  │
│  │  │  Plugin  │ │  Plugin  │ │  Plugin  │ ...       │  │
│  │  │ Registry │ │  Loader  │ │ Renderer │           │  │
│  │  └──────────┘ └──────────┘ └──────────┘           │  │
│  └─────────────────────────────────────────────────────┘  │
│                          │                                 │
│         ┌────────────────┼────────────────┐               │
│         ▼                ▼                ▼               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │   Formula   │  │    Org      │  │    IoT      │       │
│  │   Service   │  │   Service   │  │   Service   │       │
│  └─────────────┘  └─────────────┘  └─────────────┘       │
│                                                          │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              DataAdapter Layer                      │  │
│  │  - XPDL to JSON Converter                           │  │
│  │  - Remote Data Fetcher                              │  │
│  │  - Cache Manager                                    │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 插件接口定义

```typescript
// 高级动态插件接口
interface IAdvancedPlugin {
    // 插件ID
    id: string;
    
    // 插件名称
    name: string;
    
    // 插件类型
    type: 'expression' | 'listener' | 'org' | 'iot' | 'esd' | 'database';
    
    // 是否支持远程数据
    remoteData: boolean;
    
    // 远程服务配置
    remoteConfig?: {
        serviceUrl: string;
        method: 'GET' | 'POST';
        params?: Record<string, any>;
        cacheKey?: string;
    };
    
    // 渲染面板
    render(container: HTMLElement, context: any): void;
    
    // 获取远程数据
    fetchRemoteData(): Promise<any>;
    
    // 序列化数据
    serialize(): string | object;
    
    // 反序列化数据
    deserialize(data: string | object): void;
}
```

### 4.3 远程数据服务接口

```typescript
// 组织机构服务接口
interface IOrgService {
    // 获取顶级组织
    getTopOrgs(sysId: string): Promise<Org[]>;
    
    // 获取子组织
    getChildren(orgId: string): Promise<Org[]>;
    
    // 获取组织人员
    getPersons(orgId: string): Promise<Person[]>;
    
    // 根据ID获取人员
    getPersonById(personId: string): Promise<Person>;
    
    // 根据ID获取组织
    getOrgById(orgId: string): Promise<Org>;
}

// 公式/表达式服务接口
interface IFormulaService {
    // 验证表达式
    validateExpression(expression: string): Promise<ValidationResult>;
    
    // 获取可用变量
    getVariables(context: string): Promise<Variable[]>;
    
    // 获取表达式模板
    getTemplates(type: string): Promise<Template[]>;
}

// 监听器服务接口
interface IListenerService {
    // 获取流程监听器列表
    getProcessListeners(): Promise<ListenerConfig[]>;
    
    // 获取活动监听器列表
    getActivityListeners(): Promise<ListenerConfig[]>;
}
```

### 4.4 数据存储适配器

```typescript
// 集合数据存储适配器
class CollectionDataAdapter {
    // 分隔符
    static DELIMITER = ':';
    
    // 序列化多值数据
    static serialize(values: Array<{id: string, name?: string}>): string {
        return values.map(v => v.id).join(this.DELIMITER);
    }
    
    // 反序列化多值数据
    static deserialize(data: string): string[] {
        if (!data) return [];
        return data.split(this.DELIMITER).filter(id => id.trim());
    }
    
    // 序列化为XML
    static toXML(collection: any[], elementName: string): string {
        const xml = collection.map(item => {
            const attrs = Object.entries(item)
                .map(([key, val]) => `${key}="${val}"`)
                .join(' ');
            return `    <${elementName} ${attrs}/>`;
        }).join('\n');
        return `<itjds:${elementName}s>\n${xml}\n</itjds:${elementName}s>`;
    }
    
    // 从XML反序列化
    static fromXML(xml: string): any[] {
        // XML解析逻辑
    }
}
```

---

## 5. 实现路线图

### 5.1 第一阶段：基础框架

| 任务 | 优先级 | 说明 |
|-----|-------|------|
| DynamicPluginManager | P0 | 插件管理器核心 |
| IAdvancedPlugin接口 | P0 | 插件标准接口 |
| RemoteDataService | P0 | 远程数据服务基类 |

### 5.2 第二阶段：表达式插件

| 任务 | 优先级 | 依赖 |
|-----|-------|------|
| ExpressionPanel | P1 | FormulaService |
| 表达式验证 | P1 | 后端API |
| 表达式模板 | P2 | 配置文件 |

### 5.3 第三阶段：监听器插件

| 任务 | 优先级 | 依赖 |
|-----|-------|------|
| ListenerPanel | P1 | TableListPanel |
| 监听器类型切换 | P1 | - |
| 表达式/脚本编辑 | P2 | ExpressionPanel |

### 5.4 第四阶段：组织机构插件

| 任务 | 优先级 | 依赖 |
|-----|-------|------|
| OrgTreePanel | P1 | OrgService |
| PersonSelectionPanel | P1 | OrgService |
| 懒加载实现 | P2 | - |

### 5.5 第五阶段：其他扩展插件

| 任务 | 优先级 | 说明 |
|-----|-------|------|
| IoTDevicePanel | P3 | IoT服务 |
| ESDComponentPanel | P3 | ESD服务 |
| DatabasePanel | P3 | DB服务 |

---

## 附录

### A. 相关文件清单

| 类别 | 文件路径 | 说明 |
|-----|---------|------|
| 表达式面板 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\XMLExpressionEditPanel.java` | 表达式编辑 |
| 监听器集合 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\Listeners.java` | 监听器管理 |
| 监听器元素 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\Listener.java` | 监听器定义 |
| 表格面板 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\XMLTablePanel.java` | 表格基础 |
| 组织面板 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\formula\org\XMLOrgPanel.java` | 组织选择 |
| 人员面板 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\panels\formula\org\XMLPersonsPanel.java` | 人员选择 |
| 公式参数 | `E:\仓库备份\jds-dev\jds-esd-dev\jds-esdstudio\src\main\java\com\ds\bpm\bpd\xml\elements\formula\FormulaParameter.java` | 分隔符定义 |

### B. 远程服务URL映射

| 服务 | Swing实现 | JS实现建议 |
|-----|----------|-----------|
| 组织管理 | `OrgManagerFactory.getClientOrgManager()` | `/api/org/manager` |
| 顶级组织 | `manager.getTopOrgs(sysId)` | `GET /api/org/top?sysId={sysId}` |
| 子组织 | `org.getChildrenList()` | `GET /api/org/{orgId}/children` |
| 人员列表 | `org.getPersonList()` | `GET /api/org/{orgId}/persons` |
| 人员详情 | `manager.getPersonByID(id)` | `GET /api/person/{id}` |
| 表达式验证 | `FormulaService.validate()` | `POST /api/formula/validate` |

---

**文档结束**
