package net.ooder.skill.mqtt.spec;

/**
 * MQTT Topic鐟欏嫯瀵?- 鐎规矮绠無oder楠炲啿褰撮惃鍑緊pic閸涜棄鎮曠憴鍕瘱
 * 
 * <h3>Topic閸涜棄鎮曠憴鍕瘱閿?/h3>
 * <pre>
 * ooder/
 * 閳规壕鏀㈤埞鈧?p2p/                    # 閻愮懓顕悙瑙勭Х閹? * 閳?  閳规柡鏀㈤埞鈧?{userId}/           # 閻劍鍩涙稉鎾崇潣Topic
 * 閳?      閳规柡鏀㈤埞鈧?inbox           # 閺€鏈垫缁? * 閳? * 閳规壕鏀㈤埞鈧?group/                  # 缂囥倗绮嶅☉鍫熶紖
 * 閳?  閳规柡鏀㈤埞鈧?{groupId}/          # 缂囥倗绮峊opic
 * 閳?      閳规柡鏀㈤埞鈧?broadcast       # 缂囥倗绮嶉獮鎸庢尡
 * 閳? * 閳规壕鏀㈤埞鈧?topic/                  # 娑撳顣界拋銏ゆ
 * 閳?  閳规柡鏀㈤埞鈧?{topicName}/        # 娑撳顣介崥宥囆?
 * 閳?      閳规柡鏀㈤埞鈧?data            # 閺佺増宓佸☉鍫熶紖
 * 閳? * 閳规壕鏀㈤埞鈧?broadcast/              # 楠炴寧鎸卞☉鍫熶紖
 * 閳?  閳规柡鏀㈤埞鈧?{channel}/          # 楠炴寧鎸辨０鎴︿壕
 * 閳? * 閳规壕鏀㈤埞鈧?sensor/                 # 娴肩姵鍔呴崳銊︽殶閹? * 閳?  閳规柡鏀㈤埞鈧?{sensorType}/       # 娴肩姵鍔呴崳銊ц閸? * 閳?      閳规柡鏀㈤埞鈧?{sensorId}/     # 娴肩姵鍔呴崳鈫朌
 * 閳?          閳规柡鏀㈤埞鈧?data        # 閺佺増宓佹稉濠冨Г
 * 閳? * 閳规壕鏀㈤埞鈧?command/                # 鐠佹儳顦崨鎴掓姢
 * 閳?  閳规柡鏀㈤埞鈧?{deviceType}/       # 鐠佹儳顦猾璇茬€?
 * 閳?      閳规柡鏀㈤埞鈧?{deviceId}/     # 鐠佹儳顦琁D
 * 閳?          閳规壕鏀㈤埞鈧?request     # 閸涙垝鎶ょ拠閿嬬湴
 * 閳?          閳规柡鏀㈤埞鈧?response    # 閸涙垝鎶ら崫宥呯安
 * 閳? * 閳规柡鏀㈤埞鈧?system/                 # 缁崵绮哄☉鍫熶紖
 *     閳规壕鏀㈤埞鈧?alarm               # 閸涘﹨顒熷☉鍫熶紖
 *     閳规壕鏀㈤埞鈧?notification        # 缁崵绮洪柅姘辩叀
 *     閳规柡鏀㈤埞鈧?event               # 缁崵绮烘禍瀣╂
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
