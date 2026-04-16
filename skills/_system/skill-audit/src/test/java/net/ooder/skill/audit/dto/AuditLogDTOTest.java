package net.ooder.skill.audit.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogDTOTest {

    @Nested
    @DisplayName("EventType 枚举")
    class EventTypeTests {

        @Test
        void shouldHaveAllExpectedTypes() {
            assertEquals(7, AuditLogDTO.EventType.values().length);
        }

        @Test
        void authEventShouldHaveChineseName() {
            assertEquals("认证事件", AuditLogDTO.EventType.AUTH.getName());
        }

        @Test
        void sceneEventShouldHaveChineseName() {
            assertEquals("场景事件", AuditLogDTO.EventType.SCENE.getName());
        }

        @Test
        void capabilityEventShouldHaveChineseName() {
            assertEquals("能力事件", AuditLogDTO.EventType.CAPABILITY.getName());
        }

        @Test
        void agentEventShouldHaveChineseName() {
            assertEquals("代理事件", AuditLogDTO.EventType.AGENT.getName());
        }

        @Test
        void systemEventShouldHaveChineseName() {
            assertEquals("系统事件", AuditLogDTO.EventType.SYSTEM.getName());
        }

        @Test
        void userEventShouldHaveChineseName() {
            assertEquals("用户事件", AuditLogDTO.EventType.USER.getName());
        }

        @Test
        void dataEventShouldHaveChineseName() {
            assertEquals("数据事件", AuditLogDTO.EventType.DATA.getName());
        }
    }

    @Nested
    @DisplayName("Result 枚举")
    class ResultTests {

        @Test
        void shouldHaveThreeResults() {
            assertEquals(3, AuditLogDTO.Result.values().length);
        }

        @Test
        void successShouldHaveChineseName() {
            assertEquals("成功", AuditLogDTO.Result.SUCCESS.getName());
        }

        @Test
        void failureShouldHaveChineseName() {
            assertEquals("失败", AuditLogDTO.Result.FAILURE.getName());
        }

        @Test
        void errorShouldHaveChineseName() {
            assertEquals("错误", AuditLogDTO.Result.ERROR.getName());
        }
    }

    @Nested
    @DisplayName("属性完整读写")
    class PropertyTests {

        @Test
        void shouldSupportAllProperties() {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setRecordId("rec-001");
            dto.setEventType(AuditLogDTO.EventType.AUTH);
            dto.setResult(AuditLogDTO.Result.SUCCESS);
            dto.setTimestamp(System.currentTimeMillis());
            dto.setUserId("user-001");
            dto.setAgentId("agent-001");
            dto.setResourceType("Tenant");
            dto.setResourceId("tenant-abc");
            dto.setAction("create_tenant");
            dto.setDetail("创建租户操作详情");
            dto.setIpAddress("192.168.1.100");
            dto.setSessionId("sess-xyz");
            dto.setUserAgent("Mozilla/5.0");
            dto.setRequestId("req-123");
            dto.setDuration(150L);

            assertEquals("rec-001", dto.getRecordId());
            assertEquals(AuditLogDTO.EventType.AUTH, dto.getEventType());
            assertEquals(AuditLogDTO.Result.SUCCESS, dto.getResult());
            assertEquals("user-001", dto.getUserId());
            assertEquals("agent-001", dto.getAgentId());
            assertEquals("Tenant", dto.getResourceType());
            assertEquals("tenant-abc", dto.getResourceId());
            assertEquals("create_tenant", dto.getAction());
            assertEquals("创建租户操作详情", dto.getDetail());
            assertEquals("192.168.1.100", dto.getIpAddress());
            assertEquals("sess-xyz", dto.getSessionId());
            assertEquals("Mozilla/5.0", dto.getUserAgent());
            assertEquals("req-123", dto.getRequestId());
            assertEquals(150L, dto.getDuration());
        }

        @Test
        void shouldAllowNullValuesForOptionalFields() {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setAction("test");

            assertNull(dto.getUserId());
            assertNull(dto.getDetail());
            assertNull(dto.getIpAddress());
            assertNull(dto.getUserAgent());
            assertEquals("test", dto.getAction());
        }

        @Test
        void timestampShouldAcceptZeroAndNegative() {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setTimestamp(0L);
            assertEquals(0L, dto.getTimestamp());

            dto.setTimestamp(-1L);
            assertEquals(-1L, dto.getTimestamp());
        }

        @Test
        void durationShouldAcceptLargeValues() {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setDuration(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, dto.getDuration());
        }
    }

    @Nested
    @DisplayName("枚举一致性验证")
    class EnumConsistencyTests {

        @Test
        void eventTypeValuesShouldBeUnique() {
            long distinctCount = java.util.Arrays.stream(AuditLogDTO.EventType.values())
                    .map(AuditLogDTO.EventType::getName)
                    .distinct()
                    .count();
            assertEquals(AuditLogDTO.EventType.values().length, distinctCount);
        }

        @Test
        void resultValuesShouldBeUnique() {
            long distinctCount = java.util.Arrays.stream(AuditLogDTO.Result.values())
                    .map(AuditLogDTO.Result::getName)
                    .distinct()
                    .count();
            assertEquals(AuditLogDTO.Result.values().length, distinctCount);
        }
    }
}
