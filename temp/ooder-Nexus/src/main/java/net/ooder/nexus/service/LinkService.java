package net.ooder.nexus.service;

import java.util.List;
import java.util.Map;

/**
 * P2P链路管理服务接口
 * 基于SDK NetworkService实现
 *
 * @author ooder Team
 * @version 0.7.0
 * @since 0.7.0
 */
public interface LinkService {

    /**
     * 创建链路
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param type 链路类型 (DIRECT, ROUTED, TUNNEL, P2P, RELAY)
     * @return 链路信息
     */
    Map<String, Object> createLink(String sourceId, String targetId, String type);

    /**
     * 获取所有链路
     * @return 链路列表
     */
    List<Map<String, Object>> getAllLinks();

    /**
     * 获取链路详情
     * @param linkId 链路ID
     * @return 链路信息
     */
    Map<String, Object> getLink(String linkId);

    /**
     * 获取两个节点之间的链路
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @return 链路信息
     */
    Map<String, Object> getLinkBetween(String sourceId, String targetId);

    /**
     * 移除链路
     * @param linkId 链路ID
     * @return 是否成功
     */
    boolean removeLink(String linkId);

    /**
     * 获取链路质量
     * @param linkId 链路ID
     * @return 链路质量信息
     */
    Map<String, Object> getLinkQuality(String linkId);

    /**
     * 更新链路质量
     * @param linkId 链路ID
     * @param latency 延迟(ms)
     * @param packetLoss 丢包率(0.0-1.0)
     */
    void updateLinkQuality(String linkId, int latency, double packetLoss);

    /**
     * 查找最优路径
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @return 路径上的链路列表
     */
    List<Map<String, Object>> findOptimalPath(String sourceId, String targetId);

    /**
     * 查找所有路径
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param maxPaths 最大路径数
     * @return 所有路径
     */
    List<List<Map<String, Object>>> findAllPaths(String sourceId, String targetId, int maxPaths);

    /**
     * 获取从指定节点出发的链路
     * @param sourceId 源节点ID
     * @return 链路列表
     */
    List<Map<String, Object>> getLinksFrom(String sourceId);

    /**
     * 获取到达指定节点的链路
     * @param targetId 目标节点ID
     * @return 链路列表
     */
    List<Map<String, Object>> getLinksTo(String targetId);

    /**
     * 获取网络统计信息
     * @return 网络统计
     */
    Map<String, Object> getNetworkStats();

    /**
     * 启用质量监控
     * @param intervalMs 监控间隔(毫秒)
     */
    void enableQualityMonitor(long intervalMs);

    /**
     * 禁用质量监控
     */
    void disableQualityMonitor();

    /**
     * 检查质量监控是否启用
     * @return 是否启用
     */
    boolean isQualityMonitorEnabled();

    /**
     * 获取链路数量
     * @return 链路总数
     */
    int getLinkCount();

    /**
     * 检查链路是否存在
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @return 是否存在
     */
    boolean hasLink(String sourceId, String targetId);
}
