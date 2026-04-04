package net.ooder.skill.tenant.repository;

import net.ooder.skill.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    Optional<Tenant> findByCode(String code);

    List<Tenant> findByStatus(String status);

    List<Tenant> findByStatusOrderByCreatedAtDesc(String status);

    boolean existsByCode(String code);
}
