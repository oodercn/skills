package net.ooder.skill.mqtt.handler;

import net.ooder.skill.mqtt.context.MqttContext;
import net.ooder.skill.mqtt.message.CommandMessage;
import net.ooder.skill.mqtt.message.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 命令消息处理�?- 处理设备命令/控制消息
 */
public class CommandMessageHandler implements MqttMessageHandler {
    
    private static final Logger log = LoggerFactory.getLogger(CommandMessageHandler.class);
    
    public static final String COMMAND_PREFIX = "ooder/command/";
    public static final String COMMAND_REQUEST_SUFFIX = "/request";
    public static final String COMMAND_RESPONSE_SUFFIX = "/response";
    
    @Override
    public String getHandlerId() {
        return "command-handler";
    }
    
    @Override
    public boolean canHandle(String topic) {
        return topic != null && topic.startsWith(COMMAND_PREFIX);
    }
    
    @Override
    public void handle(MqttContext context, MqttMessage message) throws Exception {
        String topic = message.getTopic();
        
        log.info("Command message received: topic={}, from={}", topic, context.getClientId());
        
        CommandMessage cmdMsg = parseCommandMessage(message, context);
        
        if (topic.endsWith(COMMAND_REQUEST_SUFFIX)) {
            handleCommandRequest(context, cmdMsg);
        } else if (topic.endsWith(COMMAND_RESPONSE_SUFFIX)) {
            handleCommandResponse(context, cmdMsg);
        } else {
            handleCommand(context, cmdMsg);
        }
    }
    
    @Override
    public int getOrder() {
        return 300;
    }
    
    protected CommandMessage parseCommandMessage(MqttMessage message, MqttContext context) {
        CommandMessage cmdMsg = new CommandMessage();
        cmdMsg.setCommandId(message.getMessageId());
        cmdMsg.setSourceId(context.getUserId());
        cmdMsg.setSourceId(context.getClientId());
        cmdMsg.setSystemCode(context.getSystemCode());
        
        String topic = message.getTopic();
        String devicePath = extractDevicePath(topic);
        if (devicePath != null) {
            String[] parts = devicePath.split("/");
            if (parts.length >= 2) {
                cmdMsg.setTargetType(parts[0]);
                cmdMsg.setTargetId(parts[1]);
            }
        }
        
        String payload = message.getPayloadAsString();
        if (payload != null && payload.startsWith("{")) {
            cmdMsg.setParam("payload", payload);
        } else {
            cmdMsg.setCommand(payload);
        }
        
        return cmdMsg;
    }
    
    protected void handleCommandRequest(MqttContext context, CommandMessage command) {
        log.info("Command request: commandId={}, target={}/{}, command={}", 
            command.getCommandId(), command.getTargetType(), command.getTargetId(), command.getCommand());
        
        try {
            Object result = executeCommand(command);
            command.setResult(result);
            command.setStatus(1);
            
            sendCommandResponse(context, command);
        } catch (Exception e) {
            log.error("Command execution failed: {}", e.getMessage());
            command.setStatus(-1);
            command.setResult(e.getMessage());
        }
    }
    
    protected void handleCommandResponse(MqttContext context, CommandMessage command) {
        log.info("Command response: commandId={}, status={}, result={}", 
            command.getCommandId(), command.getStatus(), command.getResult());
    }
    
    protected void handleCommand(MqttContext context, CommandMessage command) {
        handleCommandRequest(context, command);
    }
    
    protected Object executeCommand(CommandMessage command) throws Exception {
        String cmd = command.getCommand();
        log.debug("Executing command: {}", cmd);
        
        switch (cmd) {
            case "ping":
                return "pong";
            case "status":
                return "ok";
            case "reconnect":
                return handleReconnectCommand(command);
            default:
                return handleCustomCommand(command);
        }
    }
    
    protected Object handleReconnectCommand(CommandMessage command) {
        log.info("Reconnect command received for target: {}", command.getTargetId());
        return "reconnect_initiated";
    }
    
    protected Object handleCustomCommand(CommandMessage command) {
        log.info("Custom command: {}", command.getCommand());
        return "executed";
    }
    
    protected void sendCommandResponse(MqttContext context, CommandMessage command) {
        log.info("Sending command response: commandId={}", command.getCommandId());
    }
    
    private String extractDevicePath(String topic) {
        String suffix = topic.substring(COMMAND_PREFIX.length());
        int lastSlash = suffix.lastIndexOf('/');
        if (lastSlash > 0) {
            return suffix.substring(0, lastSlash);
        }
        return suffix;
    }
}
