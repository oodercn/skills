package net.ooder.skill.conversation.service.impl;

import net.ooder.skill.conversation.model.Conversation;
import net.ooder.skill.conversation.model.CreateConversationRequest;
import net.ooder.skill.conversation.model.Message;
import net.ooder.skill.conversation.repository.ConversationRepository;
import net.ooder.skill.conversation.repository.MessageRepository;
import net.ooder.skill.conversation.service.ConversationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {
    
    private static final Logger log = LoggerFactory.getLogger(ConversationServiceImpl.class);
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    @Transactional
    public Conversation createConversation(CreateConversationRequest request) {
        Conversation conversation = new Conversation();
        conversation.setId(UUID.randomUUID().toString());
        conversation.setUserId(request.getUserId());
        conversation.setSceneId(request.getSceneId());
        conversation.setTitle(request.getTitle());
        conversation.setSystemPrompt(request.getSystemPrompt());
        conversation.setProvider(request.getProvider());
        conversation.setModel(request.getModel());
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
            Message systemMessage = new Message(Message.MessageRole.SYSTEM, request.getSystemPrompt());
            conversation.addMessage(systemMessage);
        }
        
        return conversationRepository.save(conversation);
    }
    
    @Override
    public Optional<Conversation> getConversation(String id) {
        return conversationRepository.findById(id);
    }
    
    @Override
    public Optional<Conversation> getConversation(String id, String userId) {
        return conversationRepository.findByIdAndUserId(id, userId);
    }
    
    @Override
    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByUserIdAndStatus(userId, Conversation.ConversationStatus.ACTIVE);
    }
    
    @Override
    public List<Conversation> getSceneConversations(String sceneId) {
        return conversationRepository.findBySceneIdOrderByUpdateTimeDesc(sceneId);
    }
    
    @Override
    @Transactional
    public Conversation updateConversation(String id, Conversation updates) {
        Optional<Conversation> optional = conversationRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("Conversation not found: " + id);
        }
        
        Conversation conversation = optional.get();
        
        if (updates.getTitle() != null) {
            conversation.setTitle(updates.getTitle());
        }
        if (updates.getSystemPrompt() != null) {
            conversation.setSystemPrompt(updates.getSystemPrompt());
        }
        if (updates.getProvider() != null) {
            conversation.setProvider(updates.getProvider());
        }
        if (updates.getModel() != null) {
            conversation.setModel(updates.getModel());
        }
        
        conversation.setUpdateTime(System.currentTimeMillis());
        
        return conversationRepository.save(conversation);
    }
    
    @Override
    @Transactional
    public void deleteConversation(String id) {
        conversationRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void archiveConversation(String id) {
        Optional<Conversation> optional = conversationRepository.findById(id);
        if (optional.isPresent()) {
            Conversation conversation = optional.get();
            conversation.setStatus(Conversation.ConversationStatus.ARCHIVED);
            conversation.setUpdateTime(System.currentTimeMillis());
            conversationRepository.save(conversation);
        }
    }
    
    @Override
    @Transactional
    public Message addMessage(String conversationId, String role, String content, Integer tokenCount, String metadata) {
        Optional<Conversation> optional = conversationRepository.findById(conversationId);
        if (!optional.isPresent()) {
            throw new RuntimeException("Conversation not found: " + conversationId);
        }
        
        Conversation conversation = optional.get();
        
        Message.MessageRole messageRole;
        try {
            messageRole = Message.MessageRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            messageRole = Message.MessageRole.USER;
        }
        
        Message message = new Message(messageRole, content);
        message.setId(UUID.randomUUID().toString());
        message.setTokenCount(tokenCount);
        message.setMetadata(metadata);
        
        conversation.addMessage(message);
        
        if (tokenCount != null && tokenCount > 0) {
            conversation.incrementTokens(tokenCount);
        }
        
        conversationRepository.save(conversation);
        
        return message;
    }
    
    @Override
    public List<Message> getMessages(String conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }
    
    @Override
    public List<Message> getRecentMessages(String conversationId, int limit) {
        List<Message> allMessages = messageRepository.findByConversationId(conversationId);
        
        if (allMessages.size() <= limit) {
            return allMessages;
        }
        
        return allMessages.subList(allMessages.size() - limit, allMessages.size());
    }
    
    @Override
    public List<Map<String, Object>> getConversationContext(String conversationId, int maxTokens) {
        List<Message> messages = getMessages(conversationId);
        List<Map<String, Object>> context = new ArrayList<>();
        
        int totalTokens = 0;
        
        for (Message message : messages) {
            int tokens = message.getTokenCount() != null ? message.getTokenCount() : estimateTokens(message.getContent());
            
            if (totalTokens + tokens > maxTokens && !context.isEmpty()) {
                break;
            }
            
            context.add(message.toMap());
            totalTokens += tokens;
        }
        
        return context;
    }
    
    @Override
    @Transactional
    public void clearMessages(String conversationId) {
        messageRepository.deleteByConversationId(conversationId);
        
        Optional<Conversation> optional = conversationRepository.findById(conversationId);
        if (optional.isPresent()) {
            Conversation conversation = optional.get();
            conversation.setMessages(new ArrayList<>());
            conversation.setMessageCount(0);
            conversation.setTotalTokens(0);
            conversation.setUpdateTime(System.currentTimeMillis());
            conversationRepository.save(conversation);
        }
    }
    
    @Override
    public long getConversationCount(String userId) {
        return conversationRepository.countByUserId(userId);
    }
    
    @Override
    public long getTotalTokens(String userId) {
        Long total = conversationRepository.sumTokensByUserId(userId);
        return total != null ? total : 0;
    }
    
    private int estimateTokens(String content) {
        if (content == null) return 0;
        
        int chineseChars = 0;
        int englishWords = 0;
        int otherChars = 0;
        
        StringBuilder currentWord = new StringBuilder();
        
        for (char c : content.toCharArray()) {
            if (Character.toString(c).matches("[\\u4e00-\\u9fa5]")) {
                if (currentWord.length() > 0) {
                    englishWords++;
                    currentWord = new StringBuilder();
                }
                chineseChars++;
            } else if (Character.isLetter(c)) {
                currentWord.append(c);
            } else {
                if (currentWord.length() > 0) {
                    englishWords++;
                    currentWord = new StringBuilder();
                }
                otherChars++;
            }
        }
        
        if (currentWord.length() > 0) {
            englishWords++;
        }
        
        return (int) Math.ceil(chineseChars * 0.5 + englishWords * 1.3 + otherChars * 0.3);
    }
}
