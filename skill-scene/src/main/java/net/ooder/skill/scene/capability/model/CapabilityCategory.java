package net.ooder.skill.scene.capability.model;

import net.ooder.skill.scene.dto.dict.Dict;
import net.ooder.skill.scene.dto.dict.DictItem;
import java.util.HashMap;
import java.util.Map;

@Dict(code = "capability_category", name = "能力地址分类", description = "能力地址空间分类")
public enum CapabilityCategory implements DictItem {

    SYS("sys", "系统核心", 0x00, "系统级能力", "ri-settings-4-line"),
    ORG("org", "组织服务", 0x08, "组织、用户管理", "ri-team-line"),
    AUTH("auth", "认证服务", 0x10, "认证、授权", "ri-shield-user-line"),
    VFS("vfs", "文件存储", 0x18, "虚拟文件系统", "ri-folder-line"),
    DB("db", "数据库", 0x20, "数据库操作", "ri-database-2-line"),
    LLM("llm", "大语言模型", 0x28, "LLM调用", "ri-brain-line"),
    KNOW("know", "知识库", 0x30, "知识检索", "ri-book-open-line"),
    PAYMENT("payment", "支付服务", 0x38, "支付、计费", "ri-bank-card-line"),
    MEDIA("media", "媒体服务", 0x40, "音视频处理", "ri-movie-line"),
    COMM("comm", "通讯服务", 0x48, "消息、通知", "ri-message-3-line"),
    MON("mon", "监控服务", 0x50, "监控、告警", "ri-pulse-line"),
    IOT("iot", "物联网", 0x58, "设备管理", "ri-router-line"),
    SEARCH("search", "搜索服务", 0x60, "搜索引擎", "ri-search-line"),
    SCHED("sched", "调度服务", 0x68, "定时任务", "ri-timer-line"),
    SEC("sec", "安全服务", 0x70, "安全审计", "ri-lock-line"),
    NET("net", "网络服务", 0x78, "网络管理", "ri-wifi-line"),
    UTIL("util", "工具服务", 0x08, "通用工具", "ri-tools-line");

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
        CODE_MAPPING.put("msg", "comm");
        CODE_MAPPING.put("nexus-ui", "util");
        CODE_MAPPING.put("ui", "util");
        CODE_MAPPING.put("business", "util");
        CODE_MAPPING.put("scheduler", "sched");
        CODE_MAPPING.put("infrastructure", "sys");
        CODE_MAPPING.put("collaboration", "comm");
        CODE_MAPPING.put("system", "sys");
        CODE_MAPPING.put("communication", "comm");
        CODE_MAPPING.put("scene", "util");
    }

    public static CapabilityCategory fromCode(String code) {
        if (code == null) return UTIL;
        
        String normalizedCode = code.toLowerCase().trim();
        
        if (CODE_MAPPING.containsKey(normalizedCode)) {
            normalizedCode = CODE_MAPPING.get(normalizedCode);
        }
        
        for (CapabilityCategory cat : values()) {
            if (cat.code.equalsIgnoreCase(normalizedCode)) {
                return cat;
            }
        }
        return UTIL;
    }

    public static CapabilityCategory fromAddress(int address) {
        for (CapabilityCategory cat : values()) {
            if (address >= cat.baseAddress && address < cat.baseAddress + 8) {
                return cat;
            }
        }
        return UTIL;
    }
}
