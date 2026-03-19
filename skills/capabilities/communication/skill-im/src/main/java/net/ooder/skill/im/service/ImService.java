package net.ooder.skill.im.service;

import net.ooder.skill.im.dto.*;

import java.util.List;
import java.util.Map;

public interface ImService {
    List<Conversation> getConversationList(String userId);
    Conversation createConversation(String type, String targetId, String name);
    boolean markConversationRead(String conversationId, String userId);
    UnreadSummary getUnreadSummary(String userId);
    boolean deleteConversation(String conversationId, String userId);
    List<Contact> getContactList(String userId, String group);
    List<Contact> searchContacts(String userId, String keyword);
    Contact addContact(Map<String, Object> params);
    boolean updateContact(String contactId, Map<String, Object> params);
    boolean deleteContact(String contactId);
    Map<String, List<Contact>> getContactsByDepartment(String userId);
}
