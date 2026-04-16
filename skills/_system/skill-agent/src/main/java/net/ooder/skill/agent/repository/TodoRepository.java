package net.ooder.skill.agent.repository;

import net.ooder.skill.agent.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findBySceneGroupIdOrderByCreateTimeDesc(String sceneGroupId);

    List<Todo> findBySceneGroupIdAndStatusOrderByCreateTimeDesc(
        String sceneGroupId, String status);

    List<Todo> findByAssigneeOrderByCreateTimeDesc(String assignee);

    List<Todo> findByAssigneeAndStatusOrderByCreateTimeDesc(
        String assignee, String status);

    List<Todo> findByToUserOrderByCreateTimeDesc(String toUser);

    List<Todo> findByToUserAndStatusOrderByCreateTimeDesc(
        String toUser, String status);

    @Query("SELECT t FROM Todo t WHERE t.sceneGroupId = :sceneGroupId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:userId IS NULL OR t.toUser = :userId OR t.assignee = :userId) " +
           "ORDER BY t.createTime DESC")
    List<Todo> findTodos(
        @Param("sceneGroupId") String sceneGroupId,
        @Param("status") String status,
        @Param("userId") String userId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.sceneGroupId = :sceneGroupId " +
           "AND t.status = 'PENDING'")
    Long countPendingBySceneGroup(@Param("sceneGroupId") String sceneGroupId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.assignee = :assignee " +
           "AND t.status IN ('PENDING', 'ACCEPTED')")
    Long countActiveByAssignee(@Param("assignee") String assignee);
}
