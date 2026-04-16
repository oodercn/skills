package net.ooder.skill.tenant.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("TenantId 管理")
    class TenantIdTests {

        @Test
        void shouldSetAndGetTenantId() {
            TenantContext.setTenantId("tenant-001");
            assertEquals("tenant-001", TenantContext.getTenantId());
        }

        @Test
        void shouldReturnNullWhenNotSet() {
            assertNull(TenantContext.getTenantId());
        }

        @Test
        void shouldOverwriteExistingValue() {
            TenantContext.setTenantId("old-tenant");
            TenantContext.setTenantId("new-tenant");
            assertEquals("new-tenant", TenantContext.getTenantId());
        }

        @Test
        void shouldAcceptEmptyStringAsValid() {
            TenantContext.setTenantId("");
            assertEquals("", TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("UserId 管理")
    class UserIdTests {

        @Test
        void shouldSetAndGetUserId() {
            TenantContext.setUserId("user-001");
            assertEquals("user-001", TenantContext.getUserId());
        }

        @Test
        void shouldReturnNullWhenNotSet() {
            assertNull(TenantContext.getUserId());
        }
    }

    @Nested
    @DisplayName("hasTenant 判断")
    class HasTenantTests {

        @Test
        void shouldReturnTrueWhenSet() {
            TenantContext.setTenantId("t1");
            assertTrue(TenantContext.hasTenant());
        }

        @Test
        void shouldReturnFalseWhenNull() {
            assertFalse(TenantContext.hasTenant());
        }

        @Test
        void shouldReturnTrueForEmptyString() {
            TenantContext.setTenantId("");
            assertTrue(TenantContext.hasTenant());
        }
    }

    @Nested
    @DisplayName("clear 清理")
    class ClearTests {

        @Test
        void shouldClearAllContext() {
            TenantContext.setTenantId("t1");
            TenantContext.setUserId("u1");

            TenantContext.clear();

            assertNull(TenantContext.getTenantId());
            assertNull(TenantContext.getUserId());
        }

        @Test
        void shouldBeSafeToCallMultipleTimes() {
            TenantContext.clear();
            TenantContext.clear();
            assertNull(TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("ThreadLocal 隔离性")
    class ThreadIsolationTests {

        @Test
        void differentThreadsShouldHaveIndependentContexts() throws Exception {
            TenantContext.setTenantId("main-thread");

            Thread otherThread = new Thread(() -> {
                TenantContext.setTenantId("other-thread");
                assertEquals("other-thread", TenantContext.getTenantId());
            });

            otherThread.start();
            otherThread.join();

            assertEquals("main-thread", TenantContext.getTenantId());
        }
    }
}
