package net.ooder.skill.capability.test;

import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

/**
 * ORG Capability Address Integration Test
 * 
 * <p>Integration tests for ORG (Organization) capability addresses.</p>
 * 
 * <h3>Test Addresses:</h3>
 * <ul>
 *   <li>0x08 - ORG_LOCAL (Local Organization)</li>
 *   <li>0x09 - ORG_DINGDING (DingTalk Organization)</li>
 *   <li>0x0A - ORG_FEISHU (Feishu Organization)</li>
 *   <li>0x0B - ORG_WECOM (WeCom Organization)</li>
 *   <li>0x0C - ORG_LDAP (LDAP Organization)</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 1.0.0
 */
@DisplayName("ORG Capability Address Integration Tests")
class OrgCapabilityAddressIntegrationTest extends CapabilityAddressIntegrationTestBase {

    @Override
    protected void setUpTest() {
    }

    @Override
    protected void tearDownTest() {
    }

    @Override
    protected String getTestCategory() {
        return "ORG";
    }

    @Override
    protected List<String> getTestAddresses() {
        return Arrays.asList(
            "0x08",
            "0x09",
            "0x0A",
            "0x0B",
            "0x0C"
        );
    }
}
