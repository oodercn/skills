package net.ooder.skill.scenes.spi;

import net.ooder.skill.common.spi.storage.PageResult;
import net.ooder.skill.common.spi.storage.SceneGroupData;
import net.ooder.skill.common.spi.storage.SceneGroupStorage;
import net.ooder.skill.scenes.entity.SceneGroup;
import net.ooder.skill.scenes.repository.SceneGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaSceneGroupStorage implements SceneGroupStorage {

    private static final Logger log = LoggerFactory.getLogger(JpaSceneGroupStorage.class);

    private final SceneGroupRepository repository;

    public JpaSceneGroupStorage(SceneGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public SceneGroupData save(SceneGroupData data) {
        log.debug("[JpaSceneGroupStorage] Saving scene group: {}", data.getId());
        SceneGroup entity = toEntity(data);
        SceneGroup saved = repository.save(entity);
        return toData(saved);
    }

    @Override
    public Optional<SceneGroupData> findById(String id) {
        log.debug("[JpaSceneGroupStorage] Finding by id: {}", id);
        return repository.findById(id).map(this::toData);
    }

    @Override
    public PageResult<SceneGroupData> findByOwnerId(String ownerId, int pageNum, int pageSize) {
        log.debug("[JpaSceneGroupStorage] Finding by ownerId: {}, page: {}, size: {}", ownerId, pageNum, pageSize);
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<SceneGroup> page = repository.findByOwnerId(ownerId, pageable);
        
        PageResult<SceneGroupData> result = new PageResult<>();
        result.setList(page.getContent().stream()
                .map(this::toData)
                .collect(Collectors.toList()));
        result.setTotal(page.getTotalElements());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public List<SceneGroupData> findByStatus(String status) {
        log.debug("[JpaSceneGroupStorage] Finding by status: {}", status);
        return repository.findByStatus(status).stream()
                .map(this::toData)
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneGroupData> findAll() {
        log.debug("[JpaSceneGroupStorage] Finding all");
        return repository.findAll().stream()
                .map(this::toData)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        log.debug("[JpaSceneGroupStorage] Deleting by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    public long count() {
        log.debug("[JpaSceneGroupStorage] Counting all");
        return repository.count();
    }

    @Override
    public boolean existsById(String id) {
        log.debug("[JpaSceneGroupStorage] Checking exists by id: {}", id);
        return repository.existsById(id);
    }

    private SceneGroup toEntity(SceneGroupData data) {
        SceneGroup entity = new SceneGroup();
        entity.setId(data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setOwnerId(data.getOwnerId());
        entity.setOwnerName(data.getOwnerName());
        entity.setStatus(data.getStatus() != null ? data.getStatus() : "active");
        
        if (data.getCreatedAt() != null) {
            entity.setCreateTime(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(data.getCreatedAt()), 
                    ZoneId.systemDefault()));
        }
        if (data.getUpdatedAt() != null) {
            entity.setUpdateTime(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(data.getUpdatedAt()), 
                    ZoneId.systemDefault()));
        }
        return entity;
    }

    private SceneGroupData toData(SceneGroup entity) {
        SceneGroupData data = new SceneGroupData();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setDescription(entity.getDescription());
        data.setOwnerId(entity.getOwnerId());
        data.setOwnerName(entity.getOwnerName());
        data.setStatus(entity.getStatus());
        
        if (entity.getCreateTime() != null) {
            data.setCreatedAt(entity.getCreateTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli());
        }
        if (entity.getUpdateTime() != null) {
            data.setUpdatedAt(entity.getUpdateTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli());
        }
        return data;
    }
}
