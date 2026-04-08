package net.ooder.skill.im.gateway;

import net.ooder.spi.im.ImDeliveryDriver;
import net.ooder.spi.im.handler.InboundHandler;
import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.im.dto.MultiChannelMessageDTO;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessageGateway implements ImDeliveryDriver {

    private static final Logger log = LoggerFactory.getLogger(MessageGateway.class);

    @Autowired(required = false)
    private Map<String, ImService> imServices;

    @Autowired(required = false)
    private MqttChannelAdapter mqttAdapter;

    private final ExecutorService executor = Executors.newFixedThreadPool(8,
        r -> { Thread t = new Thread(r, "msg-gw-"); t.setDaemon(true); return t; });

    private final Map<String, InboundHandler> inboundHandlers = new ConcurrentHashMap<>();

    public enum Channel {
        WEBSOCKET("websocket"), DINGTALK("dingding"), FEISHU("feishu"),
        WECOM("wecom"), MQTT("mqtt");
        private final String code;
        Channel(String code) { this.code = code; }
        public String getCode() { return code; }
        public static Channel fromCode(String code) {
            for (Channel c : values()) if (c.code.equals(code)) return c;
            return null;
        }
    }

    @Override
    public net.ooder.spi.im.model.SendResult sendToUser(String platform, String userId,
            net.ooder.spi.im.model.MessageContent content) {
        ImService svc = imServices != null ? imServices.get(platform) : null;
        if (svc == null) return net.ooder.spi.im.model.SendResult.failure("无可用通道: " + platform);
        net.ooder.skill.common.spi.im.SendResult cr = svc.sendToUser(platform, userId, toCommon(content));
        return toSpiResult(cr);
    }

    @Override
    public net.ooder.spi.im.model.SendResult sendToGroup(String platform, String groupId,
            net.ooder.spi.im.model.MessageContent content) {
        ImService svc = imServices != null ? imServices.get(platform) : null;
        if (svc == null) return net.ooder.spi.im.model.SendResult.failure("无可用通道: " + platform);
        net.ooder.skill.common.spi.im.SendResult cr = svc.sendToGroup(platform, groupId, toCommon(content));
        return toSpiResult(cr);
    }

    @Override
    public net.ooder.spi.im.model.SendResult sendDing(String userId, String title, String content) {
        if (imServices == null) return net.ooder.spi.im.model.SendResult.failure("钉钉发送失败: 无服务");
        for (Map.Entry<String, ImService> entry : imServices.entrySet()) {
            try {
                net.ooder.skill.common.spi.im.SendResult r = entry.getValue().sendDing(userId, title, content);
                if (r.isSuccess()) return toSpiResult(r);
            } catch (Exception ignored) {}
        }
        return net.ooder.spi.im.model.SendResult.failure("钉钉发送失败");
    }

    @Override
    public net.ooder.spi.im.model.SendResult sendMarkdown(String platform, String userId,
            String title, String markdown) {
        ImService svc = imServices != null ? imServices.get(platform) : null;
        if (svc == null) return net.ooder.spi.im.model.SendResult.failure("无可用通道: " + platform);
        net.ooder.skill.common.spi.im.SendResult cr = svc.sendMarkdown(platform, userId, title, markdown);
        return toSpiResult(cr);
    }

    @Override
    public List<String> getAvailablePlatforms() {
        return imServices != null ? new ArrayList<>(imServices.keySet()) : new ArrayList<>();
    }

    @Override
    public boolean isPlatformAvailable(String platform) {
        return imServices != null && imServices.containsKey(platform);
    }

    @Override
    public String getPlatformName(String platform) { return platform; }

    @Auditable(action = "im_send", resourceType = "IMMessage", logParams = true, logResult = true)
    @Override
    public CompletableFuture<net.ooder.spi.im.model.SendResult> sendAsync(
            net.ooder.spi.im.model.MessageContent content, DeliveryContext ctx) {
        MultiChannelMessageDTO msg = new MultiChannelMessageDTO();
        msg.setChannel(ctx.channel());
        msg.setMsgType(content.getType() != null ? content.getType().name().toLowerCase() : "text");
        msg.setReceiver(ctx.receiver());
        msg.setTitle(content.getTitle());
        msg.setContent(content.getContent() != null ? content.getContent() : content.getText());
        if (ctx.extra() != null) msg.setExtra(new HashMap<>(ctx.extra()));
        return send(msg);
    }

    @Auditable(action = "im_broadcast", resourceType = "IMMessage")
    @Override
    public Map<String, net.ooder.spi.im.model.SendResult> broadcast(
            DeliveryTemplate template, List<String> channels) {
        MultiChannelMessageDTO dto = new MultiChannelMessageDTO();
        dto.setMsgType(template.msgType()); dto.setContent(template.content());
        dto.setTitle(template.title());
        if (template.extra() != null) dto.setExtra(new HashMap<>(template.extra()));
        Map<String, net.ooder.spi.im.model.SendResult> results = new ConcurrentHashMap<>();
        for (String ch : channels) {
            MultiChannelMessageDTO copy = deepCopy(dto); copy.setChannel(ch);
            try { results.put(ch, send(copy).get()); }
            catch (Exception e) { results.put(ch, net.ooder.spi.im.model.SendResult.failure("[" + ch + "] " + e.getMessage())); }
        }
        return results;
    }

    @Override
    public Set<String> getAvailableChannels() {
        Set<String> ch = new HashSet<>();
        if (imServices != null) ch.addAll(imServices.keySet());
        if (mqttAdapter != null) ch.add("mqtt");
        return ch;
    }

    @Auditable(action = "im_register_handler", resourceType = "IMHandler")
    @Override
    public void registerInboundHandler(String channel, InboundHandler handler) {
        inboundHandlers.put(channel, handler);
        log.info("[MessageGateway] Registered handler: {}", channel);
    }

    @Auditable(action = "im_handle_inbound", resourceType = "IMInbound")
    @Override
    public void handleInbound(String channel, Map<String, Object> rawMessage) {
        String tid = extractTenantFromPayload(rawMessage);
        if (tid != null) TenantContext.setTenantId(tid);
        InboundHandler h = inboundHandlers.get(channel);
        if (h != null) {
            executor.submit(() -> { try { h.handle(channel, rawMessage); } catch (Exception e) { log.error("[Inbound] {}: {}", channel, e.getMessage()); } finally { TenantContext.clear(); } });
        } else { log.warn("[MessageGateway] No handler: {}", channel); TenantContext.clear(); }
    }

    public CompletableFuture<net.ooder.spi.im.model.SendResult> send(MultiChannelMessageDTO message) {
        String channel = message.getChannel();
        String tenantId = resolveTenantId(message);
        try {
            if (tenantId != null) TenantContext.setTenantId(tenantId);
            if (message.getExtra() != null && message.getExtra().get("userId") != null)
                TenantContext.setUserId((String) message.getExtra().get("userId"));
            ImService svc = imServices != null ? imServices.get(channel) : null;
            if (svc == null && mqttAdapter != null && "mqtt".equals(channel)) return sendViaMqtt(message);
            if (svc == null) return CompletableFuture.completedFuture(net.ooder.spi.im.model.SendResult.failure("无通道: " + channel));
            final ImService fsvc = svc;
            return CompletableFuture.supplyAsync(() -> {
                net.ooder.skill.common.spi.im.MessageContent c = toCommonDto(message);
                net.ooder.skill.common.spi.im.SendResult result;
                try {
                    result = "group".equals(message.getMsgType()) ?
                        fsvc.sendToGroup(channel, message.getReceiver(), c) :
                        fsvc.sendToUser(channel, message.getReceiver(), c);
                } catch (Exception e) {
                    log.error("[Send] {} error: {}", channel, e.getMessage());
                    result = net.ooder.skill.common.spi.im.SendResult.failure("失败: " + e.getMessage());
                }
                log.info("[Send] via {}: {} (tenant={})", channel, result.isSuccess() ? "OK" : "FAIL", TenantContext.getTenantId());
                return toSpiResult(result);
            }, executor);
        } finally { TenantContext.clear(); }
    }

    private CompletableFuture<net.ooder.spi.im.model.SendResult> sendViaMqtt(MultiChannelMessageDTO m) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                net.ooder.skill.common.spi.im.MessageContent c = toCommonDto(m);
                return toSpiResult("group".equals(m.getMsgType()) ?
                    mqttAdapter.sendToGroup("mqtt", m.getReceiver(), c) : mqttAdapter.sendToUser("mqtt", m.getReceiver(), c));
            } catch (Exception e) { return net.ooder.spi.im.model.SendResult.failure("MQTT: " + e.getMessage()); }
        }, executor);
    }

    private net.ooder.skill.common.spi.im.MessageContent toCommon(net.ooder.spi.im.model.MessageContent c) {
        switch (c.getType()) {
            case MARKDOWN: return net.ooder.skill.common.spi.im.MessageContent.markdown(c.getTitle(),
                c.getContent() != null ? c.getContent() : c.getText());
            case LINK: return net.ooder.skill.common.spi.im.MessageContent.link(c.getTitle(),
                c.getContent() != null ? c.getContent() : c.getText(), c.getUrl());
            default: return net.ooder.skill.common.spi.im.MessageContent.text(
                c.getContent() != null ? c.getContent() : c.getText());
        }
    }

    private net.ooder.skill.common.spi.im.MessageContent toCommonDto(MultiChannelMessageDTO d) {
        String t = d.getMsgType() != null ? d.getMsgType() : "text";
        return "markdown".equals(t) ?
            net.ooder.skill.common.spi.im.MessageContent.markdown(d.getTitle(), d.getContent()) :
            net.ooder.skill.common.spi.im.MessageContent.text(d.getContent());
    }

    private net.ooder.spi.im.model.SendResult toSpiResult(net.ooder.skill.common.spi.im.SendResult cr) {
        if (cr == null) return net.ooder.spi.im.model.SendResult.failure("未知错误");
        return new net.ooder.spi.im.model.SendResult(cr.isSuccess(), cr.getMessageId(), null, null);
    }

    private MultiChannelMessageDTO deepCopy(MultiChannelMessageDTO s) {
        MultiChannelMessageDTO c = new MultiChannelMessageDTO();
        c.setMsgType(s.getMsgType()); c.setReceiver(s.getReceiver()); c.setReceiverId(s.getReceiverId());
        c.setTitle(s.getTitle()); c.setContent(s.getContent());
        c.setReceiverIds(s.getReceiverIds() != null ? new ArrayList<>(s.getReceiverIds()) : null);
        c.setExtra(s.getExtra() != null ? new HashMap<>(s.getExtra()) : null);
        return c;
    }

    private String resolveTenantId(MultiChannelMessageDTO m) {
        if (m.getExtra() != null) { Object t = m.getExtra().get("tenantId"); if (t instanceof String s && !s.isEmpty()) return s; }
        return TenantContext.getTenantId();
    }

    private String extractTenantFromPayload(Map<String, Object> p) {
        if (p == null) return null;
        Object t = p.get("tenant_id"); if (!(t instanceof String)) t = p.get("tenantId");
        if (t instanceof String s && !s.isEmpty()) return s;
        if (p.containsKey("headers")) { Object h = p.get("headers"); if (h instanceof Map) { Object ht = ((Map<?,?>)h).get("X-Tenant-Id"); if (ht instanceof String) return (String)ht; } }
        return null;
    }

    @PreDestroy
    public void shutdown() { log.info("[MessageGateway] Shutdown"); executor.shutdown(); inboundHandlers.clear(); }
}
