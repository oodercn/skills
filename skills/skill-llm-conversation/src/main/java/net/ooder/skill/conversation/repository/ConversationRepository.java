package net.ooder.skill.conversation.repository;

import net.ooder.skill.conversation.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    List<Conversation> findByUserIdOrderByUpdateTimeDesc(String userId);
    
    Page<Conversation> findByUserId(String userId, Pageable pageable);
    
    List<Conversation> findBySceneIdOrderByUpdateTimeDesc(String sceneId);
    
    Optional<Conversation> findByIdAndUserId(String id, String userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.userId = :userId AND c.status = :status ORDER BY c.updateTime DESC")
    List<Conversation> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") Conversation.ConversationStatus status);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.userId = :userId")
    long countByUserId(@Param("userId") String userId);
    
    @Query("SELECT SUM(c.totalTokens) FROM Conversation c WHERE c.userId = :userId")
    Long sumTokensByUserId(@Param("userId") String userId);
    
    List<Conversation> findByStatus(Conversation.ConversationStatus status);
    
    @Query("SELECT c FROM Conversation c WHERE c.expireTime IS NOT NULL AND c.expireTime < :timestamp")
    List<Conversation> findExpiredConversations(@Param("timestamp") Long timestamp);
}
