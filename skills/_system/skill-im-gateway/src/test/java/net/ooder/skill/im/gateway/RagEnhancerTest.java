package net.ooder.skill.im.gateway;

import net.ooder.skill.rag.RagPipeline;
import net.ooder.skill.tenant.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

class RagEnhancerTest {

    private RagEnhancer enhancer;
    private RagPipeline mockRagPipeline;

    @BeforeEach
    void setUp() {
        enhancer = new RagEnhancer();
        mockRagPipeline = mock(RagPipeline.class);
        ReflectionTestUtils.setField(enhancer, "ragPipeline", mockRagPipeline);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("正常增强路径")
    class NormalEnhancementTests {

        @Test
        void shouldReturnEnhancedTextWhenPipelineReturnsResult() {
            String original = "如何部署Kubernetes集群?";
            String expectedEnhanced = "\n## 参考资料\n[1] **K8s部署指南**\n使用kubectl apply...\n\n";

            when(mockRagPipeline.enhancePromptWithRAG(eq(original), isNull(), anyList()))
                    .thenReturn(expectedEnhanced);

            String result = enhancer.enanceForIm(original, "tenant-k8s");

            assertEquals(expectedEnhanced, result);
            verify(mockRagPipeline).enhancePromptWithRAG(eq(original), isNull(), anyList());
        }

        @Test
        void shouldPassTenantIdAsKnowledgeBaseHint() {
            when(mockRagPipeline.enhancePromptWithRAG(anyString(), isNull(), anyList()))
                    .thenReturn("enhanced text");

            enhancer.enanceForIm("query", "specific-tenant-id");

            verify(mockRagPipeline).enhancePromptWithRAG(eq("query"), isNull(), argThat(list ->
                list != null && !list.isEmpty()
            ));
        }
    }

    @Nested
    @DisplayName("降级路径")
    class DegradationTests {

        @Test
        void shouldReturnNullWhenPipelineIsNull() {
            ReflectionTestUtils.setField(enhancer, "ragPipeline", null);

            String result = enhancer.enanceForIm("query", "tenant");

            assertNull(result);
        }

        @Test
        void shouldReturnNullWhenInputIsEmpty() {
            String result = enhancer.enanceForIm("", "tenant");
            assertNull(result);

            result = enhancer.enanceForIm(null, "tenant");
            assertNull(result);
        }

        @Test
        void shouldReturnNullWhenPipelineReturnsNull() {
            when(mockRagPipeline.enhancePromptWithRAG(anyString(), any(), any()))
                    .thenReturn(null);

            String result = enhancer.enanceForIm("query", "tenant");

            assertNull(result);
        }

        @Test
        void shouldReturnNullOnException() {
            when(mockRagPipeline.enhancePromptWithRAG(anyString(), any(), any()))
                    .thenThrow(new RuntimeException("知识库连接超时"));

            String result = enhancer.enanceForIm("query", "tenant");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("无租户场景")
    class NoTenantScenarioTests {

        @Test
        void shouldWorkWithoutTenantId() {
            when(mockRagPipeline.enhancePromptWithRAG(anyString(), isNull(), isNull()))
                    .thenReturn("generic enhanced");

            String result = enhancer.enanceForIm("general question", null);

            assertEquals("generic enhanced", result);
            verify(mockRagPipeline).enhancePromptWithRAG(
                eq("general question"), isNull(), isNull()
            );
        }
    }

    @Nested
    @DisplayName("MultiChannelMessageDTO 辅助测试")
    class DTOTests {

        @Test
        void multiChannelMessageDTOShouldSupportFullPropertyAccess() {
            net.ooder.skill.im.dto.MultiChannelMessageDTO dto =
                new net.ooder.skill.im.dto.MultiChannelMessageDTO();
            dto.setChannel("mqtt");
            dto.setMsgType("text");
            dto.setReceiver("user-001");
            dto.setReceiverId("rid-001");
            dto.setTitle("通知");
            dto.setContent("内容文本");
            dto.setReceiverIds(Arrays.asList("u1", "u2"));
            Map<String, Object> extra = new java.util.HashMap<>();
            extra.put("tenantId", "t-001");
            extra.put("priority", "high");
            dto.setExtra(extra);

            assertEquals("mqtt", dto.getChannel());
            assertEquals("text", dto.getMsgType());
            assertEquals("user-001", dto.getReceiver());
            assertEquals("rid-001", dto.getReceiverId());
            assertEquals("通知", dto.getTitle());
            assertEquals("内容文本", dto.getContent());
            assertEquals(2, dto.getReceiverIds().size());
            assertEquals("t-001", dto.getExtra().get("tenantId"));
            assertEquals("high", dto.getExtra().get("priority"));
        }

        @Test
        void dtoShouldAllowNullFields() {
            net.ooder.skill.im.dto.MultiChannelMessageDTO dto =
                new net.ooder.skill.im.dto.MultiChannelMessageDTO();
            dto.setChannel("ws");

            assertNull(dto.getTitle());
            assertNull(dto.getContent());
            assertNull(dto.getExtra());
            assertNull(dto.getReceiverIds());
        }
    }
}
