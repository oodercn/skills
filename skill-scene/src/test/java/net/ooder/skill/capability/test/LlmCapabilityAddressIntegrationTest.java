package net.ooder.skill.capability.test;

import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

/**
 * LLM Capability Address Integration Test
 * 
 * <p>Integration tests for LLM (Large Language Model) capability addresses.</p>
 * 
 * <h3>Test Addresses:</h3>
 * <ul>
 *   <li>0x28 - LLM_OLLAMA (Ollama Local Models)</li>
 *   <li>0x29 - LLM_OPENAI (OpenAI GPT)</li>
 *   <li>0x2A - LLM_QIANWEN (Qianwen/Tongyi)</li>
 *   <li>0x2B - LLM_DEEPSEEK (DeepSeek)</li>
 *   <li>0x2C - LLM_VOLCENGINE (VolcEngine/Doubao)</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 1.0.0
 */
@DisplayName("LLM Capability Address Integration Tests")
class LlmCapabilityAddressIntegrationTest extends CapabilityAddressIntegrationTestBase {

    @Override
    protected void setUpTest() {
    }

    @Override
    protected void tearDownTest() {
    }

    @Override
    protected String getTestCategory() {
        return "LLM";
    }

    @Override
    protected List<String> getTestAddresses() {
        return Arrays.asList(
            "0x28",
            "0x29",
            "0x2A",
            "0x2B",
            "0x2C"
        );
    }
}
