package net.ooder.skill.im.gateway;

import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.im.gateway.dto.GatewayHealthDTO;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/gateway/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private MessageGateway messageGateway;

    @PostMapping("/feishu")
    @Auditable(action = "webhook_feishu", resourceType = "WebhookEvent")
    public ResponseEntity<String> feishuCallback(@RequestBody Map<String, Object> payload) {
        String tenantId = extractTenantFromFeishu(payload);
        if (tenantId != null) TenantContext.setTenantId(tenantId);

        log.info("[Webhook] Feishu event type: {}, tenant={}", payload.get("event_type"), tenantId);
        messageGateway.handleInbound("feishu", payload);

        TenantContext.clear();
        return ResponseEntity.ok("{\"code\":0}");
    }

    @PostMapping("/wecom")
    @Auditable(action = "webhook_wecom", resourceType = "WebhookEvent")
    public ResponseEntity<String> wecomCallback(@RequestBody Map<String, Object> payload) {
        String tenantId = extractTenantFromGeneric(payload);
        if (tenantId != null) TenantContext.setTenantId(tenantId);

        log.info("[Webhook] WeCom msgtype: {}, tenant={}", payload.get("MsgType"), tenantId);
        messageGateway.handleInbound("wecom", payload);

        TenantContext.clear();
        return ResponseEntity.ok("success");
    }

    @PostMapping("/dingtalk")
    @Auditable(action = "webhook_dingtalk", resourceType = "WebhookEvent")
    public ResponseEntity<String> dingtalkCallback(@RequestBody Map<String, Object> payload) {
        String tenantId = extractTenantFromDingTalk(payload);
        if (tenantId != null) TenantContext.setTenantId(tenantId);

        log.info("[Webhook] DingTalk msgtype: {}, tenant={}", payload.get("msgtype"), tenantId);
        messageGateway.handleInbound("dingtalk", payload);

        TenantContext.clear();
        return ResponseEntity.ok("{\"success\":true}");
    }

    @PostMapping("/mqtt/publish")
    @Auditable(action = "webhook_mqtt_publish", resourceType = "WebhookEvent")
    public ResponseEntity<String> mqttPublish(@RequestBody Map<String, Object> payload) {
        String tenantId = extractTenantFromGeneric(payload);
        if (tenantId != null) TenantContext.setTenantId(tenantId);

        log.info("[Webhook] MQTT bridge publish to topic: {}, tenant={}", payload.get("topic"), tenantId);
        messageGateway.handleInbound("mqtt", payload);

        TenantContext.clear();
        return ResponseEntity.ok("{\"published\":true}");
    }

    @GetMapping("/health")
    @Auditable(action = "gateway_health_check", resourceType = "System")
    public ResponseEntity<GatewayHealthDTO> health() {
        GatewayHealthDTO status = new GatewayHealthDTO();
        status.setStatus("UP");
        status.setChannels(new java.util.ArrayList<>(messageGateway.getAvailableChannels()));
        status.setCurrentTenant(TenantContext.getTenantId());
        status.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    private String extractTenantFromFeishu(Map<String, Object> payload) {
        Object appId = payload.get("app_id");
        if (appId instanceof String) {
            return "feishu-" + ((String)appId).substring(0, Math.min(8, ((String)appId).length()));
        }
        Object tenant = payload.get("tenant_id");
        return tenant instanceof String ? (String)tenant : null;
    }

    private String extractTenantFromDingTalk(Map<String, Object> payload) {
        Object corpId = payload.get("corp_id");
        if (corpId instanceof String) {
            return "dt-" + ((String)corpId).substring(0, Math.min(8, ((String)corpId).length()));
        }
        Object tenant = payload.get("tenant_id");
        return tenant instanceof String ? (String)tenant : null;
    }

    private String extractTenantFromGeneric(Map<String, Object> payload) {
        Object tenant = payload.get("tenant_id");
        if (tenant instanceof String && !((String)tenant).isEmpty()) return (String)tenant;
        tenant = payload.get("tenantId");
        if (tenant instanceof String && !((String)tenant).isEmpty()) return (String)tenant;
        return null;
    }
}
