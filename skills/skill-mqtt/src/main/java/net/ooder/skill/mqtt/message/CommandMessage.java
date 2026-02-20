package net.ooder.skill.mqtt.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令消息 - 设备命令/控制消息模型
 */
public class CommandMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String commandId;
    private String command;
    private String targetId;
    private String targetType;
    private String sourceId;
    private String sourceType;
    private String systemCode;
    private Map<String, Object> params = new HashMap<String, Object>();
    private long createTime;
    private long executeTime;
    private int delayTime;
    private int timeout = 30000;
    private int status;
    private Object result;
    
    public CommandMessage() {
        this.createTime = System.currentTimeMillis();
    }
    
    public CommandMessage(String command) {
        this();
        this.command = command;
    }
    
    public String getCommandId() {
        return commandId;
    }
    
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public String getSystemCode() {
        return systemCode;
    }
    
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParam(String key, Object value) {
        params.put(key, value);
    }
    
    public Object getParam(String key) {
        return params.get(key);
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getExecuteTime() {
        return executeTime;
    }
    
    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }
    
    public int getDelayTime() {
        return delayTime;
    }
    
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public static CommandMessage create(String command) {
        return new CommandMessage(command);
    }
}
