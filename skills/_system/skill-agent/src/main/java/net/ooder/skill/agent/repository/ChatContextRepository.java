package net.ooder.skill.agent.repository;

import net.ooder.skill.agent.dto.SceneChatContextDTO;
import java.util.Optional;

public interface ChatContextRepository {

    SceneChatContextDTO save(SceneChatContextDTO context);

    Optional<SceneChatContextDTO> findBySceneGroupId(String sceneGroupId);

    Optional<SceneChatContextDTO> findBySessionId(String sessionId);

    void deleteBySceneGroupId(String sceneGroupId);

    long count();
}
