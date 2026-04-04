package net.ooder.skill.protocol.builder;

import net.ooder.skill.protocol.model.AiBridgeMessage;
import net.ooder.skill.protocol.model.ErrorInfo;
import net.ooder.skill.protocol.model.Metadata;
import net.ooder.skill.protocol.model.Extension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AiBridgeMessageBuilder {
    
    private AiBridgeMessage message;
    
    public AiBridgeMessageBuilder() {
        this.message = new AiBridgeMessage();
        this.message.setId(UUID.randomUUID().toString());
        this.message.setTimestamp(System.currentTimeMillis());
    }
    
    public static AiBridgeMessageBuilder create() {
        return new AiBridgeMessageBuilder();
    }
    
    public AiBridgeMessageBuilder version(String version) {
        this.message.setVersion(version);
        return this;
    }
    
    public AiBridgeMessageBuilder id(String id) {
        this.message.setId(id);
        return this;
    }
    
    public AiBridgeMessageBuilder timestamp(long timestamp) {
        this.message.setTimestamp(timestamp);
        return this;
    }
    
    public AiBridgeMessageBuilder type(String type) {
        this.message.setType(type);
        return this;
    }
    
    public AiBridgeMessageBuilder command(String command) {
        this.message.setCommand(command);
        return this;
    }
    
    public AiBridgeMessageBuilder params(Map<String, Object> params) {
        this.message.setParams(params);
        return this;
    }
    
    public AiBridgeMessageBuilder param(String key, Object value) {
        if (this.message.getParams() == null) {
            this.message.setParams(new HashMap<>());
        }
        this.message.getParams().put(key, value);
        return this;
    }
    
    public AiBridgeMessageBuilder metadata(Metadata metadata) {
        this.message.setMetadata(metadata);
        return this;
    }
    
    public AiBridgeMessageBuilder source(String source) {
        this.message.setSource(source);
        return this;
    }
    
    public AiBridgeMessageBuilder target(String target) {
        this.message.setTarget(target);
        return this;
    }
    
    public AiBridgeMessageBuilder responseTo(String responseTo) {
        this.message.setResponseTo(responseTo);
        return this;
    }
    
    public AiBridgeMessageBuilder status(String status) {
        this.message.setStatus(status);
        return this;
    }
    
    public AiBridgeMessageBuilder result(Object result) {
        this.message.setResult(result);
        return this;
    }
    
    public AiBridgeMessageBuilder error(ErrorInfo error) {
        this.message.setError(error);
        return this;
    }
    
    public AiBridgeMessageBuilder error(int code, String message) {
        this.message.setError(new ErrorInfo(code, message));
        return this;
    }
    
    public AiBridgeMessageBuilder error(int code, String message, String details) {
        this.message.setError(new ErrorInfo(code, message, details));
        return this;
    }
    
    public AiBridgeMessageBuilder extension(Extension extension) {
        this.message.setExtension(extension);
        return this;
    }
    
    public AiBridgeMessage build() {
        return this.message;
    }
    
    public static AiBridgeMessage successResponse(AiBridgeMessage request, Object result) {
        return create()
                .id(UUID.randomUUID().toString())
                .responseTo(request.getId())
                .command(request.getCommand())
                .status("success")
                .result(result)
                .build();
    }
    
    public static AiBridgeMessage errorResponse(AiBridgeMessage request, int code, String message) {
        return create()
                .id(UUID.randomUUID().toString())
                .responseTo(request.getId())
                .command(request.getCommand())
                .status("error")
                .error(code, message)
                .build();
    }
    
    public static AiBridgeMessage errorResponse(AiBridgeMessage request, int code, String message, String details) {
        return create()
                .id(UUID.randomUUID().toString())
                .responseTo(request.getId())
                .command(request.getCommand())
                .status("error")
                .error(code, message, details)
                .build();
    }
}
