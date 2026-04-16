package net.ooder.skill.agent.repository;

import net.ooder.skill.agent.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    List<ChatMessage> findBySceneGroupIdOrderByCreateTimeDesc(String sceneGroupId);

    Page<ChatMessage> findBySceneGroupIdOrderByCreateTimeDesc(String sceneGroupId, Pageable pageable);

    List<ChatMessage> findBySceneGroupIdAndMessageTypeOrderByCreateTimeDesc(
        String sceneGroupId, String messageType);

    Page<ChatMessage> findBySceneGroupIdAndMessageTypeOrderByCreateTimeDesc(
        String sceneGroupId, String messageType, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.sceneGroupId = :sceneGroupId " +
           "AND (:messageType IS NULL OR m.messageType = :messageType) " +
           "AND (:after IS NULL OR m.createTime > :after) " +
           "AND (:before IS NULL OR m.createTime < :before) " +
           "ORDER BY m.createTime DESC")
    Page<ChatMessage> findMessages(
        @Param("sceneGroupId") String sceneGroupId,
        @Param("messageType") String messageType,
        @Param("after") LocalDateTime after,
        @Param("before") LocalDateTime before,
        Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sceneGroupId = :sceneGroupId " +
           "AND m.messageType = :messageType AND m.status != 'READ'")
    Long countUnreadBySceneGroupAndType(
        @Param("sceneGroupId") String sceneGroupId, 
        @Param("messageType") String messageType);

    @Query("SELECT m.messageType, COUNT(m) FROM ChatMessage m " +
           "WHERE m.sceneGroupId = :sceneGroupId AND m.status != 'READ' " +
           "GROUP BY m.messageType")
    List<Object[]> countUnreadByType(@Param("sceneGroupId") String sceneGroupId);

    Optional<ChatMessage> findFirstBySceneGroupIdOrderByCreateTimeDesc(String sceneGroupId);

    void deleteBySceneGroupId(String sceneGroupId);

    List<ChatMessage> findBySceneGroupIdAndCreateTimeAfterOrderByCreateTimeAsc(
        String sceneGroupId, LocalDateTime after);

    List<ChatMessage> findBySceneGroupIdAndMessageTypeAndStatusNot(
        String sceneGroupId, String messageType, String status);

    List<ChatMessage> findBySceneGroupIdAndStatusNot(
        String sceneGroupId, String status);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sceneGroupId = :sceneGroupId " +
           "AND m.status NOT IN :statuses AND m.senderId != :senderId")
    long countBySceneGroupIdAndStatusNotInAndSenderIdNot(
        @Param("sceneGroupId") String sceneGroupId,
        @Param("statuses") List<String> statuses,
        @Param("senderId") String senderId);
}
