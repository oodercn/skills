package net.ooder.skill.audit.aspect;

import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.audit.dto.AuditLogDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuditableAspectTest {

    private AuditableAspect aspect;
    private ProceedingJoinPoint mockJoinPoint;
    private Signature mockSignature;
    private MethodSignature mockMethodSignature;
    private Auditable mockAnnotation;

    @BeforeEach
    void setUp() {
        aspect = new AuditableAspect();
        mockJoinPoint = mock(ProceedingJoinPoint.class);
        mockSignature = mock(Signature.class);
        mockMethodSignature = mock(MethodSignature.class);
        mockAnnotation = mock(Auditable.class);

        when(mockJoinPoint.getSignature()).thenReturn(mockSignature);
        when(mockSignature.getName()).thenReturn("testMethod");
    }

    @AfterEach
    void cleanRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Nested
    @DisplayName("正常执行 - 成功路径")
    class SuccessPathTests {

        @Test
        void shouldReturnResultWhenMethodSucceeds() throws Throwable {
            Object expected = "success-result";
            when(mockJoinPoint.proceed()).thenReturn(expected);
            when(mockAnnotation.action()).thenReturn("");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertEquals(expected, result);
            verify(mockJoinPoint).proceed();
        }

        @Test
        void shouldUseMethodNameAsDefaultAction() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);

            verify(mockSignature).getName();
        }

        @Test
        void shouldUseCustomActionWhenProvided() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("createTenant");
            when(mockAnnotation.resourceType()).thenReturn("Tenant");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);

            verify(mockAnnotation, atLeast(1)).action();
        }
    }

    @Nested
    @DisplayName("异常处理 - 失败路径")
    class ExceptionPathTests {

        @Test
        void shouldCatchExceptionAndReturnNullWhenLogExceptionTrue() throws Throwable {
            RuntimeException testEx = new RuntimeException("业务异常");
            when(mockJoinPoint.proceed()).thenThrow(testEx);
            when(mockAnnotation.action()).thenReturn("riskyOp");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertNull(result);
        }

        @Test
        void shouldRethrowWhenLogExceptionFalse() throws Throwable {
            RuntimeException testEx = new RuntimeException("不记录的异常");
            when(mockJoinPoint.proceed()).thenThrow(testEx);
            when(mockAnnotation.action()).thenReturn("silentOp");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(false);

            assertThrows(RuntimeException.class,
                () -> aspect.audit(mockJoinPoint, mockAnnotation));
        }

        @Test
        void shouldHandleCheckedException() throws Throwable {
            Exception checkedEx = new Exception("受检异常");
            when(mockJoinPoint.proceed()).thenThrow(checkedEx);
            when(mockAnnotation.action()).thenReturn("ioOp");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("参数日志记录")
    class LogParamsTests {

        @Test
        void shouldSerializeParamsWhenEnabled() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockJoinPoint.getArgs()).thenReturn(new Object[]{"param1", 42, true});
            when(mockAnnotation.action()).thenReturn("withParams");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(true);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);

            verify(mockJoinPoint).getArgs();
        }

        @Test
        void shouldNotAccessParamsWhenDisabled() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("noParams");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);

            verify(mockJoinPoint, never()).getArgs();
        }
    }

    @Nested
    @DisplayName("结果日志记录")
    class LogResultTests {

        @Test
        void shouldRecordResultWhenEnabled() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn(new TestResponse("data-123", "success"));
            when(mockAnnotation.action()).thenReturn("queryData");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(true);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertNotNull(result);
            assertTrue(result instanceof TestResponse);
        }

        @Test
        void shouldNotRecordResultWhenDisabled() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("sensitive-data");
            when(mockAnnotation.action()).thenReturn("secretOp");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertEquals("sensitive-data", result);
        }
    }

    @Nested
    @DisplayName("HTTP上下文提取")
    class HttpContextTests {

        @BeforeEach
        void setupHttpContext() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", "http-user-001");
            request.addHeader("X-Tenant-Id", "tenant-http-001");
            request.addHeader("User-Agent", "TestAgent/1.0");
            request.addHeader("X-Forwarded-For", "10.0.0.1");
            request.setRemoteAddr("192.168.1.50");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        }

        @Test
        void shouldExtractUserIdFromHeader() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("httpTest");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);

            assertNotNull(RequestContextHolder.getRequestAttributes());
        }

        @Test
        void shouldExtractClientIpFromXForwardedFor() throws Throwable {
            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("ipTest");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            aspect.audit(mockJoinPoint, mockAnnotation);
        }

        @Test
        void shouldWorkWithoutHttpRequest() throws Throwable {
            RequestContextHolder.resetRequestAttributes();

            when(mockJoinPoint.proceed()).thenReturn("ok");
            when(mockAnnotation.action()).thenReturn("noHttpTest");
            when(mockAnnotation.resourceType()).thenReturn("");
            when(mockAnnotation.logParams()).thenReturn(false);
            when(mockAnnotation.logResult()).thenReturn(false);
            when(mockAnnotation.logException()).thenReturn(true);

            Object result = aspect.audit(mockJoinPoint, mockAnnotation);

            assertEquals("ok", result);
        }
    }

    @Nested
    @DisplayName("IP地址解析逻辑")
    class IpResolutionTests {

        @Test
        void shouldPreferXForwardedFor() {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader("X-Forwarded-For", "1.2.3.4");
            req.addHeader("X-Real-IP", "5.6.7.8");
            req.setRemoteAddr("9.10.11.12");

            String ip = extractIp(req);
            assertEquals("1.2.3.4", ip);
        }

        @Test
        void shouldFallbackToXRealIp() {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setRemoteAddr("9.10.11.12");

            String ip = extractIp(req);
            assertEquals("9.10.11.12", ip);
        }

        @Test
        void shouldHandleCommaSeparatedIps() {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2, 3.3.3.3");

            String ip = extractIp(req);
            assertEquals("1.1.1.1", ip);
        }

        @Test
        void shouldHandleUnknownHeaderValue() {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader("X-Forwarded-For", "unknown");

            String ip = extractIp(req);
            assertEquals("127.0.0.1", ip);
        }

        private String extractIp(MockHttpServletRequest req) {
            String ip = req.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip != null ? ip : "";
        }
    }

    static class TestResponse {
        private final String id;
        private final String status;
        TestResponse(String id, String status) { this.id = id; this.status = status; }
        public String getId() { return id; }
        public String getStatus() { return status; }
    }
}
