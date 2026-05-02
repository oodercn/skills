package net.ooder.bpm.event.test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BpmTestEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventType;
    private BpmEventDomain domain;
    private long timestamp;
    private String source;
    private HashMap<String, String> metadata;
    private String payload;
    private String sceneEventCode;

    public BpmTestEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }

    public BpmTestEvent(String source, BpmEventDomain domain, String eventType, String payload) {
        this();
        this.source = source;
        this.domain = domain;
        this.eventType = eventType;
        this.payload = payload;
    }

    public static BpmTestEvent processEvent(String source, String processInstId, String state) {
        BpmTestEvent event = new BpmTestEvent(source, BpmEventDomain.PROCESS, "ProcessEvent", state);
        event.metadata.put("processInstId", processInstId);
        event.metadata.put("state", state);
        return event;
    }

    public static BpmTestEvent activityEvent(String source, String activityInstId, String agentId, String action) {
        BpmTestEvent event = new BpmTestEvent(source, BpmEventDomain.ACTIVITY, "ActivityEvent", action);
        event.metadata.put("activityInstId", activityInstId);
        event.metadata.put("agentId", agentId);
        event.metadata.put("action", action);
        return event;
    }

    public static BpmTestEvent skillFlowEvent(String source, String skillFlowId, BpmSkillFlowState fromState, BpmSkillFlowState toState) {
        BpmTestEvent event = new BpmTestEvent(source, BpmEventDomain.SKILLFLOW, "SkillFlowEvent", fromState.getCode() + "->" + toState.getCode());
        event.metadata.put("skillFlowId", skillFlowId);
        event.metadata.put("fromState", fromState.getCode());
        event.metadata.put("toState", toState.getCode());
        return event;
    }

    public static BpmTestEvent agentEvent(String source, String agentId, String messageType, String taskDesc) {
        BpmTestEvent event = new BpmTestEvent(source, BpmEventDomain.AGENT, "AgentEvent", messageType);
        event.metadata.put("agentId", agentId);
        event.metadata.put("messageType", messageType);
        event.metadata.put("taskDesc", taskDesc);
        return event;
    }

    public static BpmTestEvent sceneBridgeEvent(String source, String sceneEventCode, Map<String, String> scenePayload) {
        BpmTestEvent event = new BpmTestEvent(source, BpmEventDomain.SCENE_BRIDGE, "SceneBridgeEvent", sceneEventCode);
        event.sceneEventCode = sceneEventCode;
        if (scenePayload != null) {
            event.metadata.putAll(scenePayload);
        }
        event.metadata.put("sceneEventCode", sceneEventCode);
        return event;
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static BpmTestEvent deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (BpmTestEvent) ois.readObject();
        }
    }

    public String serializeToJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"eventId\":\"").append(escapeJson(eventId)).append("\",");
        sb.append("\"eventType\":\"").append(escapeJson(eventType)).append("\",");
        sb.append("\"domain\":\"").append(domain != null ? domain.name() : "").append("\",");
        sb.append("\"timestamp\":").append(timestamp).append(",");
        sb.append("\"source\":\"").append(escapeJson(source)).append("\",");
        sb.append("\"payload\":\"").append(escapeJson(payload)).append("\",");
        sb.append("\"sceneEventCode\":\"").append(escapeJson(sceneEventCode != null ? sceneEventCode : "")).append("\",");
        sb.append("\"metadata\":{");
        if (metadata != null && !metadata.isEmpty()) {
            List<String> keys = new ArrayList<>(metadata.keySet());
            Collections.sort(keys);
            boolean first = true;
            for (String key : keys) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(key)).append("\":\"").append(escapeJson(metadata.get(key))).append("\"");
                first = false;
            }
        }
        sb.append("}}");
        return sb.toString();
    }

    public static BpmTestEvent deserializeFromJson(String json) {
        BpmTestEvent event = new BpmTestEvent();
        Map<String, String> parsed = parseJsonFlat(json);
        event.eventId = parsed.get("eventId");
        event.eventType = parsed.get("eventType");
        event.domain = parsed.containsKey("domain") && !parsed.get("domain").isEmpty()
            ? BpmEventDomain.valueOf(parsed.get("domain")) : null;
        event.timestamp = parsed.containsKey("timestamp") ? Long.parseLong(parsed.get("timestamp")) : 0;
        event.source = parsed.get("source");
        event.payload = parsed.get("payload");
        event.sceneEventCode = parsed.get("sceneEventCode");
        event.metadata = new HashMap<>();
        for (Map.Entry<String, String> entry : parsed.entrySet()) {
            if (entry.getKey().startsWith("meta.")) {
                event.metadata.put(entry.getKey().substring(5), entry.getValue());
            }
        }
        return event;
    }

    private static Map<String, String> parseJsonFlat(String json) {
        Map<String, String> result = new HashMap<>();
        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);
        int metaStart = content.indexOf("\"metadata\":{");
        if (metaStart >= 0) {
            String beforeMeta = content.substring(0, metaStart);
            String metaSection = content.substring(metaStart + "\"metadata\":".length());
            parseKeyValuePairs(beforeMeta, result);
            parseMetadataSection(metaSection, result);
        } else {
            parseKeyValuePairs(content, result);
        }
        return result;
    }

    private static void parseKeyValuePairs(String section, Map<String, String> result) {
        String[] parts = section.split(",");
        for (String part : parts) {
            int colonIdx = part.indexOf(':');
            if (colonIdx < 0) continue;
            String key = part.substring(0, colonIdx).trim().replace("\"", "");
            String value = part.substring(colonIdx + 1).trim().replace("\"", "");
            if (!key.isEmpty()) result.put(key, value);
        }
    }

    private static void parseMetadataSection(String metaSection, Map<String, String> result) {
        int depth = 0;
        int start = -1;
        for (int i = 0; i < metaSection.length(); i++) {
            if (metaSection.charAt(i) == '{') { if (depth == 0) start = i; depth++; }
            if (metaSection.charAt(i) == '}') { depth--; if (depth == 0 && start >= 0) break; }
        }
        if (start >= 0) {
            String inner = metaSection.substring(start + 1);
            int endBrace = inner.lastIndexOf('}');
            if (endBrace >= 0) inner = inner.substring(0, endBrace);
            String[] pairs = inner.split(",");
            for (String pair : pairs) {
                int colonIdx = pair.indexOf(':');
                if (colonIdx < 0) continue;
                String key = pair.substring(0, colonIdx).trim().replace("\"", "");
                String value = pair.substring(colonIdx + 1).trim().replace("\"", "");
                if (!key.isEmpty()) result.put("meta." + key, value);
            }
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public BpmEventDomain getDomain() { return domain; }
    public void setDomain(BpmEventDomain domain) { this.domain = domain; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public HashMap<String, String> getMetadata() { return metadata; }
    public void setMetadata(HashMap<String, String> metadata) { this.metadata = metadata; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getSceneEventCode() { return sceneEventCode; }

    @Override
    public String toString() {
        return String.format("BpmTestEvent[id=%s, domain=%s, type=%s, source=%s, payload=%s]",
            eventId, domain, eventType, source, payload);
    }
}
