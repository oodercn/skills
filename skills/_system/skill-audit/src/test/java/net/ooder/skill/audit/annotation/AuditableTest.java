package net.ooder.skill.audit.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class AuditableTest {

    @Nested
    @DisplayName("注解元数据验证")
    class AnnotationMetadataTests {

        @Test
        void shouldHaveCorrectTarget() {
            Target target = Auditable.class.getAnnotation(Target.class);
            assertNotNull(target);
            assertArrayEquals(new ElementType[]{ElementType.METHOD, ElementType.TYPE}, target.value());
        }

        @Test
        void shouldHaveRuntimeRetention() {
            Retention retention = Auditable.class.getAnnotation(Retention.class);
            assertNotNull(retention);
            assertEquals(RetentionPolicy.RUNTIME, retention.value());
        }

        @Test
        void shouldBeDocumented() {
            assertTrue(Auditable.class.isAnnotationPresent(java.lang.annotation.Documented.class));
        }

        @Test
        void shouldBeInterface() {
            assertTrue(java.lang.annotation.Annotation.class.isAssignableFrom(Auditable.class));
        }
    }

    @Nested
    @DisplayName("默认属性值")
    class DefaultAttributeTests {

        private Auditable getPlainMethodAnnotation() throws Exception {
            return SampleClass.class.getMethod("plainMethod").getAnnotation(Auditable.class);
        }

        @Test
        void actionDefaultShouldBeEmpty() throws Exception {
            assertEquals("", getPlainMethodAnnotation().action());
        }

        @Test
        void moduleDefaultShouldBeEmpty() throws Exception {
            assertEquals("", getPlainMethodAnnotation().module());
        }

        @Test
        void resourceTypeDefaultShouldBeEmpty() throws Exception {
            assertEquals("", getPlainMethodAnnotation().resourceType());
        }

        @Test
        void logParamsDefaultShouldBeFalse() throws Exception {
            assertFalse(getPlainMethodAnnotation().logParams());
        }

        @Test
        void logResultDefaultShouldBeFalse() throws Exception {
            assertFalse(getPlainMethodAnnotation().logResult());
        }

        @Test
        void logExceptionDefaultShouldBeTrue() throws Exception {
            assertTrue(getPlainMethodAnnotation().logException());
        }
    }

    @Nested
    @DisplayName("自定义属性值")
    class CustomAttributeTests {

        @Test
        void shouldAcceptCustomAction() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("customActionMethod").getAnnotation(Auditable.class);
            assertEquals("创建租户", auditable.action());
        }

        @Test
        void shouldAcceptCustomModule() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("customActionMethod").getAnnotation(Auditable.class);
            assertEquals("tenant", auditable.module());
        }

        @Test
        void shouldAcceptCustomResourceType() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("customActionMethod").getAnnotation(Auditable.class);
            assertEquals("Tenant", auditable.resourceType());
        }

        @Test
        void shouldAcceptLogParamsTrue() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("withParamsLogging", String.class, int.class).getAnnotation(Auditable.class);
            assertTrue(auditable.logParams());
        }

        @Test
        void shouldAcceptLogResultTrue() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("withResultLogging").getAnnotation(Auditable.class);
            assertTrue(auditable.logResult());
        }

        @Test
        void shouldAcceptLogExceptionFalse() throws Exception {
            Auditable auditable = SampleClass.class.getMethod("suppressException").getAnnotation(Auditable.class);
            assertFalse(auditable.logException());
        }
    }

    @Nested
    @DisplayName("类级别注解支持")
    class ClassLevelTests {

        @Test
        void shouldSupportTypeLevelAnnotation() {
            Auditable auditable = AnnotatedService.class.getAnnotation(Auditable.class);
            assertNotNull(auditable);
            assertEquals("SERVICE", auditable.action());
        }
    }

    static class SampleClass {

        @Auditable
        public void plainMethod() {}

        @Auditable(action = "创建租户", module = "tenant", resourceType = "Tenant")
        public String customActionMethod() { return "ok"; }

        @Auditable(logParams = true)
        public void withParamsLogging(String param1, int param2) {}

        @Auditable(logResult = true)
        public String withResultLogging() { return "result-data"; }

        @Auditable(logException = false)
        public void suppressException() throws Exception { throw new RuntimeException("test"); }
    }

    @Auditable(action = "SERVICE", resourceType = "ServiceClass")
    static class AnnotatedService {}
}
