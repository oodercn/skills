package net.ooder.skill.tenant.service.impl;

import net.ooder.skill.tenant.entity.Tenant;
import net.ooder.skill.tenant.entity.TenantMember;
import net.ooder.skill.tenant.repository.TenantMemberRepository;
import net.ooder.skill.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TenantServiceImplTest {

    private TenantServiceImpl tenantService;
    private TenantRepository mockTenantRepo;
    private TenantMemberRepository mockMemberRepo;

    @BeforeEach
    void setUp() {
        mockTenantRepo = mock(TenantRepository.class);
        mockMemberRepo = mock(TenantMemberRepository.class);
        tenantService = new TenantServiceImpl(mockTenantRepo, mockMemberRepo);
    }

    private Tenant createSampleTenant(String name) {
        return Tenant.builder()
                .name(name)
                .code(name.toLowerCase().replace(" ", "-"))
                .planType("FREE")
                .build();
    }

    @Nested
    @DisplayName("createTenant - 创建租户")
    class CreateTenantTests {

        @Test
        void shouldCreateTenantWithOwnerMembership() {
            Tenant input = createSampleTenant("测试公司");
            when(mockTenantRepo.save(any(Tenant.class))).thenAnswer(inv -> {
                Tenant t = inv.getArgument(0);
                return t;
            });
            when(mockMemberRepo.save(any(TenantMember.class))).thenAnswer(inv -> inv.getArgument(0));

            Tenant result = tenantService.createTenant(input, "owner-001");

            assertNotNull(result.getId());
            assertEquals("owner-001", result.getCreatedBy());
            verify(mockTenantRepo).save(any(Tenant.class));
            verify(mockMemberRepo).save(argThat(m ->
                "owner-001".equals(m.getUserId()) &&
                "owner".equals(m.getRole()) &&
                "system".equals(m.getInvitedBy())
            ));
        }

        @Test
        void shouldPreserveInputFields() {
            Tenant input = createSampleTenant("高级企业版");
            input.setMaxUsers(100);
            input.setMaxStorageMB(50000L);
            input.setPlanType("ENTERPRISE");
            input.setDescription("企业级多租户方案");

            when(mockTenantRepo.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mockMemberRepo.save(any(TenantMember.class))).thenReturn(new TenantMember());

            Tenant result = tenantService.createTenant(input, "admin-01");

            assertEquals(100, result.getMaxUsers());
            assertEquals(50000L, result.getMaxStorageMB());
            assertEquals("ENTERPRISE", result.getPlanType());
            assertEquals("企业级多租户方案", result.getDescription());
        }
    }

    @Nested
    @DisplayName("listTenants - 查询用户所属租户列表")
    class ListTenantsTests {

        @Test
        void shouldListActiveTenantsForUser() {
            TenantMember m1 = new TenantMember(); m1.setTenantId("t1"); m1.setUserId("u1"); m1.setStatus("ACTIVE");
            TenantMember m2 = new TenantMember(); m2.setTenantId("t2"); m2.setUserId("u1"); m2.setStatus("ACTIVE");
            TenantMember m3 = new TenantMember(); m3.setTenantId("t3"); m3.setUserId("u1"); m3.setStatus("LEFT");

            when(mockMemberRepo.findByUserId("u1")).thenReturn(List.of(m1, m2, m3));

            Tenant t1 = createSampleTenant("租户A"); t1.setId("t1"); t1.setStatus("ACTIVE");
            Tenant t2 = createSampleTenant("租户B"); t2.setId("t2"); t2.setStatus("ACTIVE");
            Tenant t3 = createSampleTenant("租户C"); t3.setId("t3"); t3.setStatus("DELETED");

            when(mockTenantRepo.findAllById(Set.of("t1", "t2"))).thenReturn(new java.util.ArrayList<>(List.of(t1, t2, t3)));

            List<Tenant> result = tenantService.listTenants("u1");

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(t -> "t1".equals(t.getId())));
            assertTrue(result.stream().anyMatch(t -> "t2".equals(t.getId())));
        }

        @Test
        void shouldReturnEmptyListForUserWithNoMemberships() {
            when(mockMemberRepo.findByUserId("new-user")).thenReturn(new ArrayList<>());

            List<Tenant> result = tenantService.listTenants("new-user");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getTenant - 获取单个租户")
    class GetTenantTests {

        @Test
        void shouldReturnTenantById() {
            Tenant expected = createSampleTenant("存在租户");
            expected.setId("existing-id");
            when(mockTenantRepo.findById("existing-id")).thenReturn(Optional.of(expected));

            Tenant result = tenantService.getTenant("existing-id");

            assertEquals("existing-id", result.getId());
            assertEquals("存在租户", result.getName());
        }

        @Test
        void shouldThrowWhenTenantNotFound() {
            when(mockTenantRepo.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> tenantService.getTenant("nonexistent"));
        }
    }

    @Nested
    @DisplayName("updateTenant - 更新租户")
    class UpdateTenantTests {

        @Test
        void shouldUpdateProvidedFieldsOnly() {
            Tenant existing = createSampleTenant("原始名称");
            existing.setId("update-target");
            existing.setMaxUsers(10);

            Tenant updates = new Tenant();
            updates.setName("新名称");
            updates.setDescription("新描述");

            when(mockTenantRepo.findById("update-target")).thenReturn(Optional.of(existing));
            when(mockTenantRepo.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

            Tenant result = tenantService.updateTenant("update-target", updates);

            assertEquals("新名称", result.getName());
            assertEquals("新描述", result.getDescription());
            assertEquals(10, result.getMaxUsers());
        }

        @Test
        void shouldSkipNullFields() {
            Tenant existing = createSampleTenant("保持不变");
            existing.setId("skip-test");
            existing.setDomain("old-domain.com");

            Tenant updates = new Tenant();
            updates.setName(null);
            updates.setDomain(null);

            when(mockTenantRepo.findById("skip-test")).thenReturn(Optional.of(existing));
            when(mockTenantRepo.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

            Tenant result = tenantService.updateTenant("skip-test", updates);

            assertEquals("保持不变", result.getName());
            assertEquals("old-domain.com", result.getDomain());
        }
    }

    @Nested
    @DisplayName("deleteTenant - 删除(软删除)租户")
    class DeleteTenantTests {

        @Test
        void shouldSoftDeleteAndDeactivateMembers() {
            Tenant tenant = createSampleTenant("待删除");
            tenant.setId("delete-me");
            tenant.setStatus("ACTIVE");

            TenantMember activeMember = new TenantMember();
            activeMember.setId("m1"); activeMember.setTenantId("delete-me");
            activeMember.setStatus("ACTIVE");

            when(mockTenantRepo.findById("delete-me")).thenReturn(Optional.of(tenant));
            when(mockMemberRepo.findByTenantIdAndStatus("delete-me", "ACTIVE"))
                    .thenReturn(List.of(activeMember));
            when(mockMemberRepo.save(any(TenantMember.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mockTenantRepo.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

            tenantService.deleteTenant("delete-me");

            verify(mockTenantRepo).save(argThat(t -> "DELETED".equals(t.getStatus())));
            verify(mockMemberRepo).save(argThat(m -> "LEFT".equals(m.getStatus())));
        }
    }

    @Nested
    @DisplayName("switchTenant - 租户切换")
    class SwitchTenantTests {

        @Test
        void shouldSwitchWhenActiveMembershipExists() {
            Tenant tenant = createSampleTenant("目标租户");
            tenant.setId("target-id");

            TenantMember membership = new TenantMember();
            membership.setTenantId("target-id");
            membership.setUserId("switching-user");
            membership.setStatus("ACTIVE");

            when(mockMemberRepo.findByTenantIdAndUserId("target-id", "switching-user"))
                    .thenReturn(Optional.of(membership));
            when(mockTenantRepo.findById("target-id")).thenReturn(Optional.of(tenant));

            Tenant result = tenantService.switchTenant("target-id", "switching-user");

            assertEquals("target-id", result.getId());
            assertEquals("target-id", net.ooder.skill.tenant.context.TenantContext.getTenantId());

            net.ooder.skill.tenant.context.TenantContext.clear();
        }

        @Test
        void shouldThrowWhenNoActiveMembership() {
            when(mockMemberRepo.findByTenantIdAndUserId("blocked-id", "unauthorized"))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                () -> tenantService.switchTenant("blocked-id", "unauthorized"));
        }
    }

    @Nested
    @DisplayName("getQuota - 配额查询")
    class GetQuotaTests {

        @Test
        void shouldReturnDefaultQuotaWhenNotSet() {
            Tenant tenant = createSampleTenant("默认配额");
            tenant.setId("quota-default");
            tenant.setMaxUsers(null);
            tenant.setMaxStorageMB(null);
            tenant.setPlanType(null);

            when(mockTenantRepo.findById("quota-default")).thenReturn(Optional.of(tenant));
            when(mockMemberRepo.countByTenantIdAndStatus("quota-default", "ACTIVE")).thenReturn(5L);

            Map<String, Object> quota = tenantService.getQuota("quota-default");

            assertEquals(50, quota.get("maxUsers"));
            assertEquals(10240L, quota.get("maxStorageMB"));
            assertEquals("FREE", quota.get("planType"));
            assertEquals(5L, quota.get("activeMembers"));
        }

        @Test
        void shouldReturnCustomQuotaWhenSet() {
            Tenant tenant = createSampleTenant("自定义配额");
            tenant.setId("quota-custom");
            tenant.setMaxUsers(200);
            tenant.setMaxStorageMB(100000L);
            tenant.setPlanType("PRO");

            when(mockTenantRepo.findById("quota-custom")).thenReturn(Optional.of(tenant));
            when(mockMemberRepo.countByTenantIdAndStatus("quota-custom", "ACTIVE")).thenReturn(42L);

            Map<String, Object> quota = tenantService.getQuota("quota-custom");

            assertEquals(200, quota.get("maxUsers"));
            assertEquals(100000L, quota.get("maxStorageMB"));
            assertEquals("PRO", quota.get("planType"));
            assertEquals(42L, quota.get("activeMembers"));
        }
    }

    @Nested
    @DisplayName("addMember / removeMember - 成员管理")
    class MemberManagementTests {

        @Test
        void shouldAddNewMemberSuccessfully() {
            Tenant tenant = createSampleTenant("成员管理");
            tenant.setId("member-tenant");
            when(mockTenantRepo.findById("member-tenant")).thenReturn(Optional.of(tenant));
            when(mockMemberRepo.existsByTenantIdAndUserId("member-tenant", "new-member"))
                    .thenReturn(false);
            when(mockMemberRepo.save(any(TenantMember.class))).thenAnswer(inv -> inv.getArgument(0));

            net.ooder.skill.tenant.context.TenantContext.setUserId("inviter-01");

            TenantMember member = tenantService.addMember("member-tenant", "new-member", "MEMBER");

            assertEquals("member-tenant", member.getTenantId());
            assertEquals("new-member", member.getUserId());
            assertEquals("MEMBER", member.getRole());
            assertEquals("inviter-01", member.getInvitedBy());

            net.ooder.skill.tenant.context.TenantContext.clear();
        }

        @Test
        void shouldRejectDuplicateMember() {
            when(mockTenantRepo.findById("dup-tenant")).thenReturn(Optional.of(createSampleTenant("D")));
            when(mockMemberRepo.existsByTenantIdAndUserId("dup-tenant", "existing-user"))
                    .thenReturn(true);

            assertThrows(RuntimeException.class,
                () -> tenantService.addMember("dup-tenant", "existing-user", "ADMIN"));
        }

        @Test
        void shouldRemoveMemberBySettingStatusRemoved() {
            TenantMember existing = new TenantMember();
            existing.setId("rm-member");
            existing.setTenantId("rm-tenant");
            existing.setUserId("leaving-user");
            existing.setStatus("ACTIVE");

            when(mockMemberRepo.findByTenantIdAndUserId("rm-tenant", "leaving-user"))
                    .thenReturn(Optional.of(existing));
            when(mockMemberRepo.save(any(TenantMember.class))).thenAnswer(inv -> inv.getArgument(0));

            tenantService.removeMember("rm-tenant", "leaving-user");

            verify(mockMemberRepo).save(argThat(m -> "REMOVED".equals(m.getStatus())));
        }
    }
}
