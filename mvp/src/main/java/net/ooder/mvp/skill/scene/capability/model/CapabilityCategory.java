package net.ooder.mvp.skill.scene.capability.model;

import net.ooder.mvp.skill.scene.dto.dict.Dict;
import net.ooder.mvp.skill.scene.dto.dict.DictItem;
import java.util.HashMap;
import java.util.Map;

@Dict(code = "capability_category", name = "能力地址分类", description = "能力地址空间分类")
public enum CapabilityCategory implements DictItem {

    ORG("org", "组织服务", 0x00, "企业组织架构、用户认证相关服务", "ri-team-line"),
    VFS("vfs", "存储服务", 0x08, "文件存储、对象存储相关服务", "ri-database-2-line"),
    LLM("llm", "LLM服务", 0x10, "大语言模型服务、对话、配置、上下文管理", "ri-brain-line"),
    KNOWLEDGE("knowledge", "知识服务", 0x18, "知识库、RAG、向量存储、文档处理", "ri-book-line"),
    SYS("sys", "系统管理", 0x20, "系统监控、网络管理、安全审计", "ri-settings-3-line"),
    MSG("msg", "消息通讯", 0x28, "消息队列、通讯协议服务", "ri-message-3-line"),
    UI("ui", "UI生成", 0x30, "界面生成、设计转代码服务", "ri-palette-line"),
    PAYMENT("payment", "支付服务", 0x38, "支付渠道、退款管理、交易处理", "ri-bank-card-line"),
    MEDIA("media", "媒体发布", 0x40, "自媒体文章发布、内容管理、数据分析", "ri-edit-line"),
    UTIL("util", "工具服务", 0x48, "通用工具、辅助服务、业务工具", "ri-tools-line"),
    NEXUS_UI("nexus-ui", "Nexus界面", 0x50, "Nexus管理界面、仪表盘、监控页面", "ri-layout-line");

    private final String code;
    private final String name;
    private final int baseAddress;
    private final String description;
    private final String icon;

    CapabilityCategory(String code, String name, int baseAddress, String description, String icon) {
        this.code = code;
        this.name = name;
        this.baseAddress = baseAddress;
        this.description = description;
        this.icon = icon;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBaseAddress() { return baseAddress; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getIcon() { return icon; }

    @Override
    public int getSort() { return baseAddress; }

    private static final Map<String, String> CODE_MAPPING = new HashMap<>();
    static {
        // 废弃的场景技能类型 -> knowledge
        CODE_MAPPING.put("abs", "knowledge");
        CODE_MAPPING.put("tbs", "knowledge");
        CODE_MAPPING.put("ass", "knowledge");
        
        // 大写格式 -> 小写
        CODE_MAPPING.put("ORG", "org");
        CODE_MAPPING.put("VFS", "vfs");
        CODE_MAPPING.put("LLM", "llm");
        CODE_MAPPING.put("KNOWLEDGE", "knowledge");
        CODE_MAPPING.put("SYS", "sys");
        CODE_MAPPING.put("MSG", "msg");
        CODE_MAPPING.put("UI", "ui");
        CODE_MAPPING.put("PAYMENT", "payment");
        CODE_MAPPING.put("MEDIA", "media");
        CODE_MAPPING.put("UTIL", "util");
        CODE_MAPPING.put("NEXUS-UI", "nexus-ui");
        CODE_MAPPING.put("SYSTEM", "sys");
        CODE_MAPPING.put("COMMUNICATION", "msg");
        CODE_MAPPING.put("COLLABORATION", "util");
        
        // 未定义分类 -> 标准分类
        CODE_MAPPING.put("business", "util");
        CODE_MAPPING.put("infrastructure", "sys");
        CODE_MAPPING.put("scheduler", "sys");
        CODE_MAPPING.put("auth", "org");
        CODE_MAPPING.put("db", "vfs");
        CODE_MAPPING.put("know", "knowledge");
        CODE_MAPPING.put("comm", "msg");
        CODE_MAPPING.put("mon", "sys");
        CODE_MAPPING.put("search", "sys");
        CODE_MAPPING.put("sched", "sys");
        CODE_MAPPING.put("sec", "sys");
        CODE_MAPPING.put("iot", "sys");
        CODE_MAPPING.put("net", "sys");
        CODE_MAPPING.put("service", "util");
        CODE_MAPPING.put("scene", "util");
        CODE_MAPPING.put("nexus-ui", "nexus-ui");
    }

    public static CapabilityCategory fromCode(String code) {
        if (code == null) return SYS;
        
        String normalizedCode = code.toLowerCase().trim();
        
        // 先检查原始code（大写格式）
        if (CODE_MAPPING.containsKey(code)) {
            String mappedValue = CODE_MAPPING.get(code);
            // 不再转换为小写，直接使用映射后的值
            for (CapabilityCategory cat : values()) {
                if (cat.code.equalsIgnoreCase(mappedValue)) {
                    return cat;
                }
            }
        } else if (CODE_MAPPING.containsKey(normalizedCode)) {
            String mappedValue = CODE_MAPPING.get(normalizedCode);
            for (CapabilityCategory cat : values()) {
                if (cat.code.equalsIgnoreCase(mappedValue)) {
                    return cat;
                }
            }
        }
        
        // 直接匹配枚举值
        for (CapabilityCategory cat : values()) {
            if (cat.code.equalsIgnoreCase(normalizedCode)) {
                return cat;
            }
        }
        
        return SYS;
    }

    public static CapabilityCategory fromAddress(int address) {
        for (CapabilityCategory cat : values()) {
            if (address >= cat.baseAddress && address < cat.baseAddress + 8) {
                return cat;
            }
        }
        return SYS;
    }
}