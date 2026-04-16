package net.ooder.skill.tenant.interceptor;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class TenantInterceptorTest {

    private TenantInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new TenantInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/tenants");
    }

    @Nested
    @DisplayName("Header 解析优先级最高")
    class HeaderResolutionTests {

        @Test
        void shouldResolveFromX_Tenant_Id_Header() throws Exception {
            request.addHeader("X-Tenant-Id", "header-tenant-001");
            request.addHeader("X-User-Id", "user-header");

            boolean result = interceptor.preHandle(request, response, null);

            assertTrue(result);
            assertEquals("header-tenant-001", net.ooder.skill.tenant.context.TenantContext.getTenantId());
            assertEquals("user-header", net.ooder.skill.tenant.context.TenantContext.getUserId());
        }

        @Test
        void headerShouldOverrideCookie() throws Exception {
            request.addHeader("X-Tenant-Id", "from-header");
            request.setCookies(new Cookie("tenant_id", "from-cookie"));

            interceptor.preHandle(request, response, null);

            assertEquals("from-header", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }

        @Test
        void headerShouldOverrideQueryParam() throws Exception {
            request.addHeader("X-Tenant-Id", "from-header");
            request.setParameter("tenantId", "from-param");

            interceptor.preHandle(request, response, null);

            assertEquals("from-header", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("Cookie 解析 (次优先级)")
    class CookieResolutionTests {

        @Test
        void shouldResolveFromCookieWhenNoHeader() throws Exception {
            request.setCookies(new Cookie("tenant_id", "cookie-tenant-001"));

            interceptor.preHandle(request, response, null);

            assertEquals("cookie-tenant-001", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }

        @Test
        void shouldFindCorrectCookieByName() throws Exception {
            Cookie[] cookies = {
                new Cookie("session_id", "sess-123"),
                new Cookie("tenant_id", "correct-tenant"),
                new Cookie("theme", "dark")
            };
            request.setCookies(cookies);

            interceptor.preHandle(request, response, null);

            assertEquals("correct-tenant", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }

        @Test
        void shouldSkipWhenCookiesArrayIsNull() throws Exception {
            request.setCookies(null);
            request.setParameter("tenantId", "param-value");

            interceptor.preHandle(request, response, null);

            assertEquals("param-value", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("QueryParam 解析 (最低优先级)")
    class QueryParamResolutionTests {

        @Test
        void shouldResolveFromQueryParamWhenNoHeaderOrCookie() throws Exception {
            request.setParameter("tenantId", "param-tenant-001");

            interceptor.preHandle(request, response, null);

            assertEquals("param-tenant-001", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("无租户信息场景")
    class NoTenantScenarioTests {

        @Test
        void shouldPassThroughWhenNoTenantInfo() throws Exception {
            boolean result = interceptor.preHandle(request, response, null);

            assertTrue(result);
            assertNull(net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }

        @Test
        void shouldIgnoreEmptyHeaderValue() throws Exception {
            request.addHeader("X-Tenant-Id", "");
            request.setCookies(new Cookie("tenant_id", "fallback-cookie"));

            interceptor.preHandle(request, response, null);

            assertEquals("fallback-cookie", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("afterCompletion 清理")
    class AfterCompletionTests {

        @Test
        void shouldClearContextAfterCompletion() throws Exception {
            request.addHeader("X-Tenant-Id", "to-be-cleared");
            request.addHeader("X-User-Id", "user-to-clear");

            interceptor.preHandle(request, response, null);
            assertNotNull(net.ooder.skill.tenant.context.TenantContext.getTenantId());

            interceptor.afterCompletion(request, response, null, null);

            assertNull(net.ooder.skill.tenant.context.TenantContext.getTenantId());
            assertNull(net.ooder.skill.tenant.context.TenantContext.getUserId());
        }
    }

    @Nested
    @DisplayName("完整解析链路")
    class FullChainTests {

        @Test
        void headerOverCookieOverParamPriority() throws Exception {
            request.addHeader("X-Tenant-Id", "winner-header");
            request.setCookies(new Cookie("tenant_id", "loser-cookie"));
            request.setParameter("tenantId", "loser-param");
            request.addHeader("X-User-Id", "header-user");

            interceptor.preHandle(request, response, null);

            assertEquals("winner-header", net.ooder.skill.tenant.context.TenantContext.getTenantId());
            assertEquals("header-user", net.ooder.skill.tenant.context.TenantContext.getUserId());
        }

        @Test
        void cookieOverParamWhenNoHeader() throws Exception {
            request.setCookies(new Cookie("tenant_id", "winner-cookie"));
            request.setParameter("tenantId", "loser-param");

            interceptor.preHandle(request, response, null);

            assertEquals("winner-cookie", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }

        @Test
        void paramOnlyWhenNoHeaderOrCookie() throws Exception {
            request.setParameter("tenantId", "only-param");

            interceptor.preHandle(request, response, null);

            assertEquals("only-param", net.ooder.skill.tenant.context.TenantContext.getTenantId());
        }
    }
}
