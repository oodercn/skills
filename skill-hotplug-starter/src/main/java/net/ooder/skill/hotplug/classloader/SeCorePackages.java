package net.ooder.skill.hotplug.classloader;

import java.util.Arrays;
import java.util.List;

/**
 * SE 核心包配置工具类
 * 
 * <p>用于判断类是否属于 SE 核心包，需要从父类加载器加载</p>
 * 
 * <p>此配置需要与 SE 的 PluginClassLoader.SE_CORE_PACKAGES 保持同步</p>
 * 
 * @author ooder Team
 * @since 2.3.1
 */
public final class SeCorePackages {

    private SeCorePackages() {
    }

    /**
     * SE 核心包列表 - 必须从主应用加载，确保类型一致性
     * 
     * <p>此列表需要与 net.ooder.scene.core.plugin.PluginClassLoader.SE_CORE_PACKAGES 保持同步</p>
     */
    public static final List<String> PACKAGES = Arrays.asList(
            // SPI 接口
            "net.ooder.scene.spi.",
            // 对话服务
            "net.ooder.scene.skill.conversation.",
            // 存储服务
            "net.ooder.scene.skill.conversation.storage.",
            // 知识库服务
            "net.ooder.scene.skill.knowledge.",
            // 工具服务
            "net.ooder.scene.skill.tool.",
            // 上下文
            "net.ooder.scene.llm.context.",
            // 基础接口
            "net.ooder.scene.skill."
    );

    /**
     * 检查类是否属于 SE 核心包
     *
     * @param className 类名
     * @return 是否属于 SE 核心包
     */
    public static boolean isSeCoreClass(String className) {
        if (className == null) {
            return false;
        }
        for (String pkg : PACKAGES) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 SE 核心包列表（用于日志或调试）
     *
     * @return SE 核心包列表
     */
    public static List<String> getPackages() {
        return PACKAGES;
    }
}
