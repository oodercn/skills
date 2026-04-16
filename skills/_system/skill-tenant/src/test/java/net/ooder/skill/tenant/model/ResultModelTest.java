package net.ooder.skill.tenant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultModelTest {

    @Nested
    @DisplayName("success 工厂方法")
    class SuccessTests {

        @Test
        void successWithDataShouldReturn200() {
            ResultModel<String> r = ResultModel.success("ok-data");
            assertEquals(200, r.getCode());
            assertEquals("success", r.getMessage());
            assertEquals("ok-data", r.getData());
            assertTrue(r.getTimestamp() > 0);
        }

        @Test
        void successWithMessageShouldIncludeCustomMessage() {
            ResultModel<Integer> r = ResultModel.success(99, "自定义消息");
            assertEquals(200, r.getCode());
            assertEquals("自定义消息", r.getMessage());
            assertEquals(99, r.getData());
        }

        @Test
        void successWithNullDataIsValid() {
            ResultModel<Object> r = ResultModel.success(null);
            assertEquals(200, r.getCode());
            assertNull(r.getData());
        }
    }

    @Nested
    @DisplayName("error 工厂方法")
    class ErrorTests {

        @Test
        void errorShouldReturn500() {
            ResultModel<Void> r = ResultModel.error("错误信息");
            assertEquals(500, r.getCode());
            assertEquals("错误信息", r.getMessage());
            assertNull(r.getData());
        }
    }

    @Nested
    @DisplayName("泛型支持验证")
    class GenericSupportTests {

        @Test
        void shouldSupportListData() {
            ResultModel<List<String>> r = ResultModel.success(List.of("a", "b"));
            assertEquals(2, r.getData().size());
        }

        @Test
        void shouldSupportMapData() {
            ResultModel<Map<String, Object>> r = ResultModel.success(Map.of("k", "v"));
            assertEquals("v", r.getData().get("k"));
        }
    }
}
