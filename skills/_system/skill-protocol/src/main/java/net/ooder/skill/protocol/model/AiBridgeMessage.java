package net.ooder.skill.protocol.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiBridgeMessage {
    
    @JsonProperty("version")
    private String version = "0.6.0";
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("command")
    private String command;
    
    @JsonProperty("params")
    private Map<String, Object> params;
    
    @JsonProperty("metadata")
    private Metadata metadata;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("target")
    private String target;
    
    @JsonProperty("response_to")
    private String responseTo;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private ErrorInfo error;
    
    @JsonProperty("extension")
    private Extension extension;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.messageId = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
        this.id = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
        if (metadata != null) {
            this.source = metadata.getSenderId();
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        if (metadata == null) {
            metadata = new Metadata();
        }
        metadata.setSenderId(source);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(String responseTo) {
        this.responseTo = responseTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
