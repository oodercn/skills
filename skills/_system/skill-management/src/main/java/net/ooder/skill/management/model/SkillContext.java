package net.ooder.skill.management.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SkillContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String userId;
    private String agentId;
    private String sceneId;
    private String groupId;
    private Map<String, Object> params;
    private Map<String, Object> environment;
    private Map<String, Object> metadata;
    private long timestamp;
    private long timeout;

    public SkillContext() {
        this.params = new HashMap<>();
        this.environment = new HashMap<>();
        this.metadata = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
        this.timeout = 30000L;
    }

    public static SkillContext create() {
        return new SkillContext();
    }

    public static SkillContext create(String userId) {
        SkillContext context = new SkillContext();
        context.setUserId(userId);
        return context;
    }

    public SkillContext sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public SkillContext userId(String userId) {
        this.userId = userId;
        return this;
    }

    public SkillContext agentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public SkillContext sceneId(String sceneId) {
        this.sceneId = sceneId;
        return this;
    }

    public SkillContext groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public SkillContext param(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public SkillContext params(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public SkillContext env(String key, Object value) {
        this.environment.put(key, value);
        return this;
    }

    public SkillContext timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public Map<String, Object> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, Object> environment) {
        this.environment = environment;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
