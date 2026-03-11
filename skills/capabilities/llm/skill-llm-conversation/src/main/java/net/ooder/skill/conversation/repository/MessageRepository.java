package net.ooder.skill.conversation.repository;

import net.ooder.skill.conversation.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    
    List<Message> findByConversationIdOrderByCreateTimeAsc(String conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createTime ASC")
    List<Message> findByConversationId(@Param("conversationId") String conversationId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId")
    long countByConversationId(@Param("conversationId") String conversationId);
    
    @Query(value = "SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createTime DESC")
    List<Message> findRecentMessages(@Param("conversationId") String conversationId);
    
    void deleteByConversationId(String conversationId);
}
