package net.ooder.skill.context.service;

import net.ooder.skill.context.ContextUpdate;
import net.ooder.skill.context.GlobalContextConfig;

import java.util.List;
import java.util.Map;

public interface MultiLevelContextManager {

    void initializeGlobalContext(GlobalContextConfig config);

    Map<String, Object> getCurrentContext();

    void reloadContextForPage(String pageId);

    void reloadContextForSkill(String skillId);

    String getContextLevel();

    void pushContextUpdate(ContextUpdate update);

    List<Map<String, Object>> getConversationHistory(String sessionId);

    void addMessage(String sessionId, Map<String, Object> message);

    void clearSession(String sessionId);

    void clearAllSessions();
}