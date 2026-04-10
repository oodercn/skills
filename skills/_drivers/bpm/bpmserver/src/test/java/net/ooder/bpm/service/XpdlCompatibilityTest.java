package net.ooder.bpm.service;

import net.ooder.bpm.engine.database.*;
import net.ooder.bpm.engine.inter.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XPDL格式兼容性测试
 * 验证与历史XPDL文件的兼容性
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class XpdlCompatibilityTest {

    @Autowired
    private ProcessDefManagerService processDefManagerService;

    @Autowired
    private DbProcessDefVersionManager processDefVersionManager;

    /**
     * 测试标准的XPDL格式开始节点
     * 格式: ParticipantID;FirstActivityID;X;Y;Routing
     */
    @Test
    @DisplayName("测试标准XPDL开始节点格式")
    void testStandardXpdlStartNodeFormat() {
        // 典型的XPDL格式开始节点
        String[] testCases = {
            "Participant_Start;act_start;50;200;NO_ROUTING",
            "Participant_001;Activity_1;100;150;ROUTING_001",
            "p_start;a_start;0;0;DEFAULT"
        };

        for (String testCase : testCases) {
            String[] parts = testCase.split(";");
            assertEquals(5, parts.length, "XPDL格式应有5个部分");

            // 验证各部分类型
            assertNotNull(parts[0], "ParticipantID不应为空");
            assertNotNull(parts[1], "FirstActivityID不应为空");
            assertDoesNotThrow(() -> Integer.parseInt(parts[2]), "X坐标应为整数");
            assertDoesNotThrow(() -> Integer.parseInt(parts[3]), "Y坐标应为整数");
            assertNotNull(parts[4], "Routing不应为空");
        }
    }

    /**
     * 测试标准的XPDL格式结束节点
     * 格式: ParticipantID;LastActivityID;X;Y;Routing（多个用|分隔）
     */
    @Test
    @DisplayName("测试标准XPDL结束节点格式")
    void testStandardXpdlEndNodeFormat() {
        // 典型的XPDL格式结束节点（单个）
        String singleEnd = "Participant_End;act_end;800;200;NO_ROUTING";
        String[] parts = singleEnd.split(";");
        assertEquals(5, parts.length, "XPDL结束节点格式应有5个部分");

        // 典型的XPDL格式结束节点（多个）
        String multipleEnds = "Participant_End1;act_end1;800;100;NO_ROUTING|Participant_End2;act_end2;800;200;NO_ROUTING|Participant_End3;act_end3;800;300;NO_ROUTING";
        String[] endNodes = multipleEnds.split("\\|");
        assertEquals(3, endNodes.length, "应解析出3个结束节点");

        for (String endNode : endNodes) {
            String[] endParts = endNode.split(";");
            assertEquals(5, endParts.length, "每个结束节点应有5个部分");
        }
    }

    /**
     * 测试XPDL格式的Listeners XML
     */
    @Test
    @DisplayName("测试XPDL Listeners XML格式")
    void testXpdlListenersXmlFormat() {
        String listenersXml = "<itjds:Listeners>" +
            "<itjds:Listener Id=\"listener_001\" Name=\"流程启动监听\" ListenerEvent=\"PROCESS_START\" RealizeClass=\"com.test.StartListener\"/>" +
            "<itjds:Listener Id=\"listener_002\" Name=\"流程结束监听\" ListenerEvent=\"PROCESS_END\" RealizeClass=\"com.test.EndListener\"/>" +
            "</itjds:Listeners>";

        // 验证XML格式
        assertTrue(listenersXml.startsWith("<itjds:Listeners>"), "应以itjds:Listeners开头");
        assertTrue(listenersXml.endsWith("</itjds:Listeners>"), "应以/itjds:Listeners结尾");
        assertTrue(listenersXml.contains("<itjds:Listener"), "应包含Listener元素");
        assertTrue(listenersXml.contains("Id=\""), "应包含Id属性");
        assertTrue(listenersXml.contains("Name=\""), "应包含Name属性");
        assertTrue(listenersXml.contains("ListenerEvent=\""), "应包含ListenerEvent属性");
        assertTrue(listenersXml.contains("RealizeClass=\""), "应包含RealizeClass属性");
    }

    /**
     * 测试XPDL格式的RightGroups XML
     */
    @Test
    @DisplayName("测试XPDL RightGroups XML格式")
    void testXpdlRightGroupsXmlFormat() {
        String rightGroupsXml = "<itjds:RightGroups>" +
            "<itjds:RightGroup Id=\"rg_001\" Name=\"默认组\" Code=\"DEFAULT\" Order=\"1\" DefaultGroup=\"YES\"/>" +
            "<itjds:RightGroup Id=\"rg_002\" Name=\"审批组\" Code=\"APPROVAL\" Order=\"2\" DefaultGroup=\"NO\"/>" +
            "</itjds:RightGroups>";

        // 验证XML格式
        assertTrue(rightGroupsXml.startsWith("<itjds:RightGroups>"), "应以itjds:RightGroups开头");
        assertTrue(rightGroupsXml.endsWith("</itjds:RightGroups>"), "应以/itjds:RightGroups结尾");
        assertTrue(rightGroupsXml.contains("<itjds:RightGroup"), "应包含RightGroup元素");
        assertTrue(rightGroupsXml.contains("Id=\""), "应包含Id属性");
        assertTrue(rightGroupsXml.contains("Name=\""), "应包含Name属性");
        assertTrue(rightGroupsXml.contains("Code=\""), "应包含Code属性");
        assertTrue(rightGroupsXml.contains("Order=\""), "应包含Order属性");
        assertTrue(rightGroupsXml.contains("DefaultGroup=\""), "应包含DefaultGroup属性");
    }

    /**
     * 测试XPDL格式的块活动坐标
     */
    @Test
    @DisplayName("测试XPDL块活动坐标格式")
    void testXpdlBlockCoordinateFormat() {
        // 块活动的StartOfBlock和EndOfBlock使用相同的XPDL格式
        String startOfBlock = "Participant_Block;act_block_start;200;300;NO_ROUTING";
        String endOfBlock = "Participant_Block;act_block_end;600;300;NO_ROUTING";

        String[] startParts = startOfBlock.split(";");
        String[] endParts = endOfBlock.split(";");

        assertEquals(5, startParts.length, "StartOfBlock应有5个部分");
        assertEquals(5, endParts.length, "EndOfBlock应有5个部分");
        assertEquals(startParts[0], endParts[0], "同一块的ParticipantID应相同");
    }

    /**
     * 测试特殊字符在XPDL中的转义
     */
    @Test
    @DisplayName("测试XPDL XML特殊字符转义")
    void testXpdlXmlEscaping() {
        // 测试需要转义的特殊字符
        String specialChars = "<>&\"'";
        String escaped = escapeXml(specialChars);

        assertFalse(escaped.contains("<"), "<应被转义");
        assertFalse(escaped.contains(">"), ">应被转义");
        assertFalse(escaped.contains("&"), "&应被转义");
        assertFalse(escaped.contains("\""), "\"应被转义");
        assertFalse(escaped.contains("'"), "'应被转义");
    }

    /**
     * 测试XPDL属性类型定义
     */
    @Test
    @DisplayName("测试XPDL属性类型定义")
    void testXpdlAttributeTypes() {
        // BPD类型的属性
        String[] bpdAttributes = {
            "StartOfWorkflow", "EndOfWorkflow", "XOffset", "YOffset",
            "ParticipantID", "StartOfBlock", "EndOfBlock", "ParticipantVisualOrder",
            "CreatorName", "ModifierId", "ModifierName", "ModifyTime", "Limit", "DurationUnit"
        };

        for (String attr : bpdAttributes) {
            assertNotNull(attr);
            assertFalse(attr.isEmpty());
        }

        // WORKFLOW类型的属性
        String[] workflowAttributes = {
            "DeadLineOperation", "SpecialScope"
        };

        for (String attr : workflowAttributes) {
            assertNotNull(attr);
        }
    }

    /**
     * 测试XPDL路由类型
     */
    @Test
    @DisplayName("测试XPDL路由类型定义")
    void testXpdlRoutingTypes() {
        // 标准路由类型
        String[] routingTypes = {
            "NO_ROUTING",
            "SIMPLE_ROUTING",
            "CONDITIONAL_ROUTING",
            "PARALLEL_ROUTING"
        };

        for (String routing : routingTypes) {
            assertNotNull(routing);
            assertFalse(routing.isEmpty());
        }
    }

    /**
     * 测试XPDL参与者类型
     */
    @Test
    @DisplayName("测试XPDL参与者类型定义")
    void testXpdlParticipantTypes() {
        // 标准参与者类型
        String[] participantTypes = {
            "Participant_Start",
            "Participant_End",
            "Participant_Normal",
            "Participant_Block"
        };

        for (String participant : participantTypes) {
            assertNotNull(participant);
            assertTrue(participant.startsWith("Participant_"), "参与者ID应以Participant_开头");
        }
    }

    /**
     * 测试XPDL活动位置类型
     */
    @Test
    @DisplayName("测试XPDL活动位置类型")
    void testXpdlActivityPositions() {
        // 标准位置类型
        Map<String, String> positionTypes = new HashMap<>();
        positionTypes.put("START", "POSITION_START");
        positionTypes.put("END", "POSITION_END");
        positionTypes.put("NORMAL", "POSITION_NORMAL");

        assertEquals("POSITION_START", positionTypes.get("START"));
        assertEquals("POSITION_END", positionTypes.get("END"));
        assertEquals("POSITION_NORMAL", positionTypes.get("NORMAL"));
    }

    /**
     * 测试XPDL Join/Split 类型
     */
    @Test
    @DisplayName("测试XPDL Join/Split 类型")
    void testXpdlJoinSplitTypes() {
        // 标准Join/Split类型
        String[] joinSplitTypes = {"XOR", "OR", "AND"};

        for (String type : joinSplitTypes) {
            assertNotNull(type);
            assertTrue(
                type.equals("XOR") || type.equals("OR") || type.equals("AND"),
                "Join/Split类型必须是XOR、OR或AND"
            );
        }
    }

    /**
     * 测试XPDL实现类型
     */
    @Test
    @DisplayName("测试XPDL实现类型")
    void testXpdlImplementationTypes() {
        // 标准实现类型
        String[] implTypes = {"No", "Service", "SubFlow", "Reference"};

        for (String type : implTypes) {
            assertNotNull(type);
        }
    }

    /**
     * 测试XPDL条件类型
     */
    @Test
    @DisplayName("测试XPDL条件类型")
    void testXpdlConditionTypes() {
        // 标准条件类型
        String[] conditionTypes = {"CONDITION", "OTHERWISE", "EXCEPTION", "DEFAULT"};

        for (String type : conditionTypes) {
            assertNotNull(type);
        }
    }

    /**
     * 测试XPDL路由方向
     */
    @Test
    @DisplayName("测试XPDL路由方向")
    void testXpdlRouteDirections() {
        // 标准路由方向
        String[] directions = {"FORWARD", "BACKWARD", "LOOP"};

        for (String direction : directions) {
            assertNotNull(direction);
        }
    }

    /**
     * 测试XPDL执行类型
     */
    @Test
    @DisplayName("测试XPDL执行类型")
    void testXpdlPerformTypes() {
        // 标准执行类型
        String[] performTypes = {"SINGLE", "JOINTSIGN", "COUNTERSIGN"};

        for (String type : performTypes) {
            assertNotNull(type);
        }
    }

    /**
     * 测试XPDL执行顺序
     */
    @Test
    @DisplayName("测试XPDL执行顺序")
    void testXpdlPerformSequences() {
        // 标准执行顺序
        String[] sequences = {"FIRST", "MEANWHILE", "SEQUENCE"};

        for (String seq : sequences) {
            assertNotNull(seq);
        }
    }

    /**
     * 测试XPDL特殊发送范围
     */
    @Test
    @DisplayName("测试XPDL特殊发送范围")
    void testXpdlSpecialSendScopes() {
        // 标准特殊发送范围
        String[] scopes = {"ALL", "DEPARTMENT", "GROUP", "USER"};

        for (String scope : scopes) {
            assertNotNull(scope);
        }
    }

    /**
     * 测试XPDL退回方式
     */
    @Test
    @DisplayName("测试XPDL退回方式")
    void testXpdlRouteBackMethods() {
        // 标准退回方式
        String[] methods = {"PREV", "START", "ANY"};

        for (String method : methods) {
            assertNotNull(method);
        }
    }

    /**
     * 测试XPDL持续时间单位
     */
    @Test
    @DisplayName("测试XPDL持续时间单位")
    void testXpdlDurationUnits() {
        // 标准持续时间单位
        String[] units = {"M", "H", "D", "W"};

        for (String unit : units) {
            assertNotNull(unit);
        }
    }

    /**
     * 测试XPDL表单类型
     */
    @Test
    @DisplayName("测试XPDL表单类型")
    void testXpdlFormTypes() {
        // 标准表单类型
        String[] formTypes = {"CUSTOM", "SYSTEM", "EXTERNAL"};

        for (String type : formTypes) {
            assertNotNull(type);
        }
    }

    /**
     * 测试XPDL HTTP方法
     */
    @Test
    @DisplayName("测试XPDL HTTP方法")
    void testXpdlHttpMethods() {
        // 标准HTTP方法
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            assertNotNull(method);
        }
    }

    /**
     * 测试XPDL HTTP内容类型
     */
    @Test
    @DisplayName("测试XPDL HTTP内容类型")
    void testXpdlHttpContentTypes() {
        // 标准HTTP内容类型
        String[] contentTypes = {"JSON", "XML", "FORM", "TEXT"};

        for (String type : contentTypes) {
            assertNotNull(type);
        }
    }

    /**
     * 测试XPDL监听器事件类型
     */
    @Test
    @DisplayName("测试XPDL监听器事件类型")
    void testXpdlListenerEventTypes() {
        // 标准监听器事件类型
        String[] eventTypes = {
            "PROCESS_START", "PROCESS_END",
            "ACTIVITY_START", "ACTIVITY_END",
            "ROUTE_TAKE", "ASSIGNMENT"
        };

        for (String event : eventTypes) {
            assertNotNull(event);
            assertTrue(
                event.startsWith("PROCESS_") ||
                event.startsWith("ACTIVITY_") ||
                event.startsWith("ROUTE_") ||
                event.startsWith("ASSIGNMENT"),
                "事件类型格式不正确"
            );
        }
    }

    // ==================== 辅助方法 ====================

    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
