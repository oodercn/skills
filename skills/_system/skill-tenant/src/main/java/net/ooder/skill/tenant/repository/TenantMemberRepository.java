package net.ooder.skill.tenant.repository;

import net.ooder.skill.tenant.entity.TenantMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantMemberRepository extends JpaRepository<TenantMember, String> {

    List<TenantMember> findByTenantIdAndStatus(String tenantId, String status);

    List<TenantMember> findByUserId(String userId);

    Optional<TenantMember> findByTenantIdAndUserId(String tenantId, String userId);

    long countByTenantIdAndStatus(String tenantId, String status);

    void deleteByTenantIdAndUserId(String tenantId, String userId);

    boolean existsByTenantIdAndUserId(String tenantId, String userId);
}
