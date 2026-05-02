package net.ooder.vfs.event;

import net.ooder.sdk.core.event.CoreEvent;

public class VfsServerEvent extends CoreEvent {

    public enum ServerAction {
        STARTED, STOPPED, CONNECTED, DISCONNECTED, ERROR
    }

    private final ServerAction action;
    private final String serverId;
    private final String message;

    public VfsServerEvent(String source, ServerAction action, String serverId, String message) {
        super(source);
        this.action = action;
        this.serverId = serverId;
        this.message = message;
    }

    public ServerAction getAction() { return action; }
    public String getServerId() { return serverId; }
    public String getMessage() { return message; }

    @Override
    public String getDescription() {
        return String.format("Server %s: id=%s, msg=%s", action, serverId, message);
    }
}
