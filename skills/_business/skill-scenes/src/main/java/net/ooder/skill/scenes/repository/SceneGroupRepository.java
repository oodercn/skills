package net.ooder.skill.scenes.repository;

import net.ooder.skill.scenes.entity.SceneGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SceneGroupRepository extends JpaRepository<SceneGroup, String> {

    Page<SceneGroup> findByOwnerId(String ownerId, Pageable pageable);

    List<SceneGroup> findByStatus(String status);

    Page<SceneGroup> findByNameContaining(String name, Pageable pageable);
}
