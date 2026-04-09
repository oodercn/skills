package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.service.AgentChatService;
import net.ooder.skill.agent.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.ooder.skill.agent.repository.ChatContextRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentChatServiceImpl implements AgentChatService {

    private static final Logger log = LoggerFactory.getLogger(AgentChatServiceImpl.class);

    @Autowired
    private UnifiedInterfaceAdapter unifiedAdapter;

    private Map<String, List<AgentChatMessageDTO>> messageStore = new ConcurrentHashMap<>();
    @Autowired
    private ChatContextRepository contextRepository;

    private static final int MAX_CONTEXT_MESSAGES = 50;

    private Map<String, SceneChatContextDTO> contextStore = new ConcurrentHashMap<>();
    private Map<String, Set<String>> readStatus = new ConcurrentHashMap<>();

    @Override
    public SceneChatContextDTO getChatContext(String sceneGroupId, String userId) {
        log.debug("[getChatContext] sceneGroupId={}, userId={}", sceneGroupId, userId);
        
        SceneChatContextDTO context = contextStore.get(sceneGroupId);
        if (context == null) {
            context = contextRepository.findBySceneGroupId(sceneGroupId).orElse(null);
        }
        if (context == null) {
            context = new SceneChatContextDTO();
            context.setSceneGroupId(sceneGroupId);
            context.setSceneGroupName("Scene " + sceneGroupId);
            context.setParticipants(new ArrayList<>());
            context.setAgents(new ArrayList<>());
        }
        contextStore.put(sceneGroupId, context);
        context.setCurrentUserId(userId);
        return context;
    }

    @Override
    public PageResult<AgentChatMessageDTO> getMessages(String sceneGroupId, String userId, 
            String type, Long before, Long after, int pageNum, int pageSize) {
        log.debug("[getMessages] sceneGroupId={}, type={}, pageNum={}, pageSize={}", 
            sceneGroupId, type, pageNum, pageSize);
        
        List<AgentChatMessageDTO> messages = messageStore.getOrDefault(sceneGroupId, new ArrayList<>());
        
        List<AgentChatMessageDTO> filtered = messages.stream()
            .filter(m -> type == null || type.isEmpty() || type.equals(m.getMessageType()))
            .filter(m -> before == null || m.getCreateTime() < before)
            .filter(m -> after == null || m.getCreateTime() > after)
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(Collectors.toList());
        
        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<AgentChatMessageDTO> pageData = start < total ? 
            filtered.subList(start, end) : new ArrayList<>();
        
        PageResult<AgentChatMessageDTO> result = new PageResult<>();
        result.setList(pageData);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }

    @Override
    public AgentChatMessageDTO getMessage(String messageId) {
        for (List<AgentChatMessageDTO> messages : messageStore.values()) {
            for (AgentChatMessageDTO message : messages) {
                if (messageId.equals(message.getMessageId())) {
                    return message;
                }
            }
        }
        return null;
    }

    @Override
    public String sendMessage(String sceneGroupId, AgentChatMessageDTO message) {
        log.info("[sendMessage] sceneGroupId={}, type={}", sceneGroupId, message.getMessageType());
        
        String messageId = UUID.randomUUID().toString();
        message.setMessageId(messageId);
        message.setSceneGroupId(sceneGroupId);
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus("SENT");
        
        if (message.getPriority() <= 0) {
            message.setPriority(5);
        }
        
        List<AgentChatMessageDTO> messages = messageStore.computeIfAbsent(sceneGroupId, k -> new ArrayList<>());
        messages.add(message);
        
        if (messages.size() > MAX_CONTEXT_MESSAGES) {
            messages.subList(0, messages.size() - MAX_CONTEXT_MESSAGES).clear();
        }
        
        SceneChatContextDTO context = contextStore.get(sceneGroupId);
        if (context != null) {
            contextRepository.save(context);
        }
        
        return messageId;
    }

    @Override
    public void markAsRead(String sceneGroupId, String userId, String messageId) {
        log.debug("[markAsRead] sceneGroupId={}, userId={}, messageId={}", 
            sceneGroupId, userId, messageId);
        
        String key = sceneGroupId + ":" + messageId;
        readStatus.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(userId);
        
        AgentChatMessageDTO message = getMessage(messageId);
        if (message != null) {
            message.setStatus("READ");
        }
    }

    @Override
    public void markAllAsRead(String sceneGroupId, String userId, String type) {
        log.debug("[markAllAsRead] sceneGroupId={}, userId={}, type={}", 
            sceneGroupId, userId, type);
        
        List<AgentChatMessageDTO> messages = messageStore.getOrDefault(sceneGroupId, new ArrayList<>());
        for (AgentChatMessageDTO message : messages) {
            if (type == null || type.isEmpty() || type.equals(message.getMessageType())) {
                markAsRead(sceneGroupId, userId, message.getMessageId());
            }
        }
    }

    @Override
    public Map<String, Integer> getUnreadCounts(String sceneGroupId, String userId) {
        log.debug("[getUnreadCounts] sceneGroupId={}, userId={}", sceneGroupId, userId);
        
        Map<String, Integer> counts = new HashMap<>();
        List<AgentChatMessageDTO> messages = messageStore.getOrDefault(sceneGroupId, new ArrayList<>());
        
        Map<String, List<AgentChatMessageDTO>> groupedByType = messages.stream()
            .collect(Collectors.groupingBy(AgentChatMessageDTO::getMessageType));
        
        for (Map.Entry<String, List<AgentChatMessageDTO>> entry : groupedByType.entrySet()) {
            String type = entry.getKey();
            int unread = 0;
            for (AgentChatMessageDTO msg : entry.getValue()) {
                String key = sceneGroupId + ":" + msg.getMessageId();
                Set<String> readers = readStatus.get(key);
                if (readers == null || !readers.contains(userId)) {
                    unread++;
                }
            }
            counts.put(type, unread);
        }
        
        return counts;
    }

    @Override
    public Object executeMessageAction(String sceneGroupId, String messageId, String userId,
            String actionId, Map<String, Object> actionData) {
        log.info("[executeMessageAction] messageId={}, actionId={}", messageId, actionId);
        
        AgentChatMessageDTO message = getMessage(messageId);
        if (message == null) {
            return Map.of("error", "Message not found");
        }
        
        return Map.of("success", true, "actionId", actionId, "result", "Action executed");
    }

    @Override
    public List<TodoDTO> getTodos(String sceneGroupId, String userId, String status) {
        log.debug("[getTodos] sceneGroupId={}, userId={}, status={}", sceneGroupId, userId, status);
        return new ArrayList<>();
    }

    @Override
    public boolean acceptTodo(String userId, String todoId) {
        log.info("[acceptTodo] userId={}, todoId={}", userId, todoId);
        return true;
    }

    @Override
    public boolean rejectTodo(String userId, String todoId, String reason) {
        log.info("[rejectTodo] userId={}, todoId={}, reason={}", userId, todoId, reason);
        return true;
    }

    @Override
    public boolean delegateTodo(String userId, String todoId, String toUserId) {
        log.info("[delegateTodo] userId={}, todoId={}, toUserId={}", userId, todoId, toUserId);
        return true;
    }

    @Override
    public boolean completeTodo(String userId, String todoId) {
        log.info("[completeTodo] userId={}, todoId={}", userId, todoId);
        return true;
    }

    @Override
    public void addReaction(String messageId, String userId, String emoji) {
        log.debug("[addReaction] messageId={}, userId={}, emoji={}", messageId, userId, emoji);
        
        AgentChatMessageDTO message = getMessage(messageId);
        if (message != null) {
            AgentChatMessageDTO.MessageReaction reaction = 
                new AgentChatMessageDTO.MessageReaction(emoji, userId);
            message.getReactions().add(reaction);
        }
    }

    @Override
    public void removeReaction(String messageId, String userId, String emoji) {
        log.debug("[removeReaction] messageId={}, userId={}, emoji={}", messageId, userId, emoji);
        
        AgentChatMessageDTO message = getMessage(messageId);
        if (message != null && message.getReactions() != null) {
            message.getReactions().removeIf(r -> 
                r.getUserId().equals(userId) && r.getEmoji().equals(emoji));
        }
    }
}
