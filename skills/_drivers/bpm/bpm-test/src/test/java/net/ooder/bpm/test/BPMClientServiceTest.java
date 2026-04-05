package net.ooder.bpm.test;

import net.ooder.bpm.client.*;
import net.ooder.config.ResultModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BPMClientServiceTest {

    @Nested
    @DisplayName("流程定义测试")
    class ProcessDefTests {

        @Test
        @DisplayName("测试 ProcessDef 基本属性")
        void testProcessDefProperties() {
            ProcessDef mockProcessDef = mock(ProcessDef.class);
            when(mockProcessDef.getProcessDefId()).thenReturn("test-process-def");
            when(mockProcessDef.getName()).thenReturn("Test Process");
            when(mockProcessDef.getDescription()).thenReturn("Test Description");
            when(mockProcessDef.getSystemCode()).thenReturn("bpm");

            assertEquals("test-process-def", mockProcessDef.getProcessDefId());
            assertEquals("Test Process", mockProcessDef.getName());
            assertEquals("Test Description", mockProcessDef.getDescription());
            assertEquals("bpm", mockProcessDef.getSystemCode());
        }
    }

    @Nested
    @DisplayName("流程实例测试")
    class ProcessInstTests {

        @Test
        @DisplayName("测试 ProcessInst 基本属性")
        void testProcessInstProperties() {
            ProcessInst mockProcessInst = mock(ProcessInst.class);
            when(mockProcessInst.getProcessInstId()).thenReturn("process-inst-1");
            when(mockProcessInst.getName()).thenReturn("Test Process Instance");
            when(mockProcessInst.getProcessDefId()).thenReturn("test-process-def");

            assertEquals("process-inst-1", mockProcessInst.getProcessInstId());
            assertEquals("Test Process Instance", mockProcessInst.getName());
            assertEquals("test-process-def", mockProcessInst.getProcessDefId());
        }
    }

    @Nested
    @DisplayName("活动实例测试")
    class ActivityInstTests {

        @Test
        @DisplayName("测试 ActivityInst 基本属性")
        void testActivityInstProperties() {
            ActivityInst mockActivityInst = mock(ActivityInst.class);
            when(mockActivityInst.getActivityInstId()).thenReturn("activity-inst-1");
            when(mockActivityInst.getActivityDefId()).thenReturn("activity-def-1");
            when(mockActivityInst.getProcessInstId()).thenReturn("process-inst-1");

            assertEquals("activity-inst-1", mockActivityInst.getActivityInstId());
            assertEquals("activity-def-1", mockActivityInst.getActivityDefId());
            assertEquals("process-inst-1", mockActivityInst.getProcessInstId());
        }
    }

    @Nested
    @DisplayName("路由测试")
    class RouteTests {

        @Test
        @DisplayName("测试 RouteDef 基本属性")
        void testRouteDefProperties() {
            RouteDef mockRouteDef = mock(RouteDef.class);
            when(mockRouteDef.getRouteDefId()).thenReturn("route-def-1");
            when(mockRouteDef.getName()).thenReturn("Test Route");

            assertEquals("route-def-1", mockRouteDef.getRouteDefId());
            assertEquals("Test Route", mockRouteDef.getName());
        }

        @Test
        @DisplayName("测试 RouteInst 基本属性")
        void testRouteInstProperties() {
            RouteInst mockRouteInst = mock(RouteInst.class);
            when(mockRouteInst.getRouteInstId()).thenReturn("route-inst-1");

            assertEquals("route-inst-1", mockRouteInst.getRouteInstId());
        }
    }

    @Nested
    @DisplayName("ResultModel 测试")
    class ResultModelTests {

        @Test
        @DisplayName("测试 ResultModel 成功响应")
        void testResultModelSuccess() {
            ResultModel<String> result = new ResultModel<>();
            result.setData("test-data");

            assertNotNull(result.getData());
            assertEquals("test-data", result.getData());
        }
    }
}
