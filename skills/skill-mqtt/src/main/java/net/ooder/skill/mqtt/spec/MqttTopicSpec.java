package net.ooder.skill.mqtt.spec;

/**
 * MQTT Topic规范 - 定义ooder平台的Topic命名规范
 * 
 * <h3>Topic命名规范�?/h3>
 * <pre>
 * ooder/
 * ├── p2p/                    # 点对点消�? * �?  └── {userId}/           # 用户专属Topic
 * �?      └── inbox           # 收件�? * �? * ├── group/                  # 群组消息
 * �?  └── {groupId}/          # 群组Topic
 * �?      └── broadcast       # 群组广播
 * �? * ├── topic/                  # 主题订阅
 * �?  └── {topicName}/        # 主题名称
 * �?      └── data            # 数据消息
 * �? * ├── broadcast/              # 广播消息
 * �?  └── {channel}/          # 广播频道
 * �? * ├── sensor/                 # 传感器数�? * �?  └── {sensorType}/       # 传感器类�? * �?      └── {sensorId}/     # 传感器ID
 * �?          └── data        # 数据上报
 * �? * ├── command/                # 设备命令
 * �?  └── {deviceType}/       # 设备类型
 * �?      └── {deviceId}/     # 设备ID
 * �?          ├── request     # 命令请求
 * �?          └── response    # 命令响应
 * �? * └── system/                 # 系统消息
 *     ├── alarm               # 告警消息
 *     ├── notification        # 系统通知
 *     └── event               # 系统事件
 * </pre>
 */
public final class MqttTopicSpec {
    
    private MqttTopicSpec() {
    }
    
    public static final String ROOT_PREFIX = "ooder/";
    
    public static final String P2P_PREFIX = "ooder/p2p/";
    public static final String GROUP_PREFIX = "ooder/group/";
    public static final String TOPIC_PREFIX = "ooder/topic/";
    public static final String BROADCAST_PREFIX = "ooder/broadcast/";
    public static final String SENSOR_PREFIX = "ooder/sensor/";
    public static final String COMMAND_PREFIX = "ooder/command/";
    public static final String SYSTEM_PREFIX = "ooder/system/";
    
    public static final String INBOX_SUFFIX = "/inbox";
    public static final String BROADCAST_SUFFIX = "/broadcast";
    public static final String DATA_SUFFIX = "/data";
    public static final String REQUEST_SUFFIX = "/request";
    public static final String RESPONSE_SUFFIX = "/response";
    
    public static final String SYSTEM_ALARM = "ooder/system/alarm";
    public static final String SYSTEM_NOTIFICATION = "ooder/system/notification";
    public static final String SYSTEM_EVENT = "ooder/system/event";
    
    public static String p2pTopic(String userId) {
        return P2P_PREFIX + userId + INBOX_SUFFIX;
    }
    
    public static String groupTopic(String groupId) {
        return GROUP_PREFIX + groupId + BROADCAST_SUFFIX;
    }
    
    public static String topicPath(String topicName) {
        return TOPIC_PREFIX + topicName + DATA_SUFFIX;
    }
    
    public static String broadcastTopic(String channel) {
        return BROADCAST_PREFIX + channel;
    }
    
    public static String sensorTopic(String sensorType, String sensorId) {
        return SENSOR_PREFIX + sensorType + "/" + sensorId + DATA_SUFFIX;
    }
    
    public static String commandRequestTopic(String deviceType, String deviceId) {
        return COMMAND_PREFIX + deviceType + "/" + deviceId + REQUEST_SUFFIX;
    }
    
    public static String commandResponseTopic(String deviceType, String deviceId) {
        return COMMAND_PREFIX + deviceType + "/" + deviceId + RESPONSE_SUFFIX;
    }
    
    public static boolean isP2PTopic(String topic) {
        return topic != null && topic.startsWith(P2P_PREFIX);
    }
    
    public static boolean isGroupTopic(String topic) {
        return topic != null && topic.startsWith(GROUP_PREFIX);
    }
    
    public static boolean isTopicSubscription(String topic) {
        return topic != null && topic.startsWith(TOPIC_PREFIX);
    }
    
    public static boolean isBroadcastTopic(String topic) {
        return topic != null && topic.startsWith(BROADCAST_PREFIX);
    }
    
    public static boolean isSensorTopic(String topic) {
        return topic != null && topic.startsWith(SENSOR_PREFIX);
    }
    
    public static boolean isCommandTopic(String topic) {
        return topic != null && topic.startsWith(COMMAND_PREFIX);
    }
    
    public static boolean isSystemTopic(String topic) {
        return topic != null && topic.startsWith(SYSTEM_PREFIX);
    }
    
    public static String extractUserId(String p2pTopic) {
        if (!isP2PTopic(p2pTopic)) {
            return null;
        }
        String suffix = p2pTopic.substring(P2P_PREFIX.length());
        int slashIndex = suffix.indexOf('/');
        if (slashIndex > 0) {
            return suffix.substring(0, slashIndex);
        }
        return suffix;
    }
    
    public static String extractGroupId(String groupTopic) {
        if (!isGroupTopic(groupTopic)) {
            return null;
        }
        String suffix = groupTopic.substring(GROUP_PREFIX.length());
        int slashIndex = suffix.indexOf('/');
        if (slashIndex > 0) {
            return suffix.substring(0, slashIndex);
        }
        return suffix;
    }
    
    public static String[] extractDeviceInfo(String commandTopic) {
        if (!isCommandTopic(commandTopic)) {
            return null;
        }
        String suffix = commandTopic.substring(COMMAND_PREFIX.length());
        int lastSlash = suffix.lastIndexOf('/');
        if (lastSlash > 0) {
            String devicePath = suffix.substring(0, lastSlash);
            String[] parts = devicePath.split("/");
            if (parts.length >= 2) {
                return new String[]{parts[0], parts[1]};
            }
        }
        return null;
    }
    
    public static String[] extractSensorInfo(String sensorTopic) {
        if (!isSensorTopic(sensorTopic)) {
            return null;
        }
        String suffix = sensorTopic.substring(SENSOR_PREFIX.length());
        String[] parts = suffix.split("/");
        if (parts.length >= 2) {
            return new String[]{parts[0], parts[1]};
        }
        return null;
    }
}
