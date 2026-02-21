package net.ooder.skill.im.service.impl;

import net.ooder.skill.im.dto.*;
import net.ooder.skill.im.service.ImService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ImServiceImpl implements ImService {

    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, Contact> contacts = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> conversationUnread = new ConcurrentHashMap<>();

    @Override
    public List<Conversation> getConversationList(String userId) {
        return new ArrayList<>(conversations.values());
    }

    @Override
    public Conversation createConversation(String type, String targetId, String name) {
        Conversation conversation = new Conversation();
        conversation.setConversationId("conv-" + UUID.randomUUID().toString().substring(0, 8));
        conversation.setConversationType(type != null ? type : "private");
        conversation.setTargetId(targetId);
        conversation.setName(name);
        conversation.setTargetType("private".equals(type) ? "user" : "group");
        conversation.setMemberCount(1);
        conversations.put(conversation.getConversationId(), conversation);
        return conversation;
    }

    @Override
    public boolean markConversationRead(String conversationId, String userId) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.setUnreadCount(0);
            conversation.setUpdateTime(System.currentTimeMillis());
            Map<String, Integer> userUnread = conversationUnread.computeIfAbsent(userId, k -> new HashMap<>());
            userUnread.put(conversationId, 0);
            return true;
        }
        return false;
    }

    @Override
    public UnreadSummary getUnreadSummary(String userId) {
        UnreadSummary summary = new UnreadSummary();
        int total = 0;
        int convUnread = 0;
        int groupUnread = 0;
        
        for (Conversation conv : conversations.values()) {
            int unread = conv.getUnreadCount();
            total += unread;
            if ("private".equals(conv.getConversationType())) {
                convUnread += unread;
            } else {
                groupUnread += unread;
            }
        }
        
        summary.setTotalUnread(total);
        summary.setConversationUnread(convUnread);
        summary.setGroupUnread(groupUnread);
        summary.setSystemUnread(0);
        return summary;
    }

    @Override
    public boolean deleteConversation(String conversationId, String userId) {
        return conversations.remove(conversationId) != null;
    }

    @Override
    public List<Contact> getContactList(String userId, String group) {
        if (group != null && !group.isEmpty()) {
            return contacts.values().stream()
                    .filter(c -> group.equals(c.getDepartment()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(contacts.values());
    }

    @Override
    public List<Contact> searchContacts(String userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        String lowerKeyword = keyword.toLowerCase();
        return contacts.values().stream()
                .filter(c -> (c.getName() != null && c.getName().toLowerCase().contains(lowerKeyword))
                        || (c.getDepartment() != null && c.getDepartment().toLowerCase().contains(lowerKeyword))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    @Override
    public Contact addContact(Map<String, Object> params) {
        Contact contact = new Contact();
        contact.setContactId("contact-" + UUID.randomUUID().toString().substring(0, 8));
        if (params.get("userId") != null) {
            contact.setUserId((String) params.get("userId"));
        }
        if (params.get("name") != null) {
            contact.setName((String) params.get("name"));
        }
        if (params.get("avatar") != null) {
            contact.setAvatar((String) params.get("avatar"));
        }
        if (params.get("department") != null) {
            contact.setDepartment((String) params.get("department"));
        }
        if (params.get("position") != null) {
            contact.setPosition((String) params.get("position"));
        }
        if (params.get("email") != null) {
            contact.setEmail((String) params.get("email"));
        }
        if (params.get("phone") != null) {
            contact.setPhone((String) params.get("phone"));
        }
        if (params.get("remark") != null) {
            contact.setRemark((String) params.get("remark"));
        }
        contacts.put(contact.getContactId(), contact);
        return contact;
    }

    @Override
    public boolean updateContact(String contactId, Map<String, Object> params) {
        Contact contact = contacts.get(contactId);
        if (contact == null) {
            return false;
        }
        if (params.get("name") != null) {
            contact.setName((String) params.get("name"));
        }
        if (params.get("avatar") != null) {
            contact.setAvatar((String) params.get("avatar"));
        }
        if (params.get("department") != null) {
            contact.setDepartment((String) params.get("department"));
        }
        if (params.get("position") != null) {
            contact.setPosition((String) params.get("position"));
        }
        if (params.get("email") != null) {
            contact.setEmail((String) params.get("email"));
        }
        if (params.get("phone") != null) {
            contact.setPhone((String) params.get("phone"));
        }
        if (params.get("remark") != null) {
            contact.setRemark((String) params.get("remark"));
        }
        return true;
    }

    @Override
    public boolean deleteContact(String contactId) {
        return contacts.remove(contactId) != null;
    }

    @Override
    public Map<String, List<Contact>> getContactsByDepartment(String userId) {
        Map<String, List<Contact>> result = new HashMap<>();
        for (Contact contact : contacts.values()) {
            String dept = contact.getDepartment() != null ? contact.getDepartment() : "未分组";
            result.computeIfAbsent(dept, k -> new ArrayList<>()).add(contact);
        }
        return result;
    }
}
