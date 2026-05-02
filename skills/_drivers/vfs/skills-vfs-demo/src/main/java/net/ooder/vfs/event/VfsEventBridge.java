package net.ooder.vfs.event;

import net.ooder.common.JDSException;
import net.ooder.engine.event.EIServerAdapter;
import net.ooder.engine.event.EIServerEvent;
import net.ooder.vfs.event.VfsServerEvent.ServerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VfsEventBridge extends EIServerAdapter {

    private static final Logger log = LoggerFactory.getLogger(VfsEventBridge.class);

    private final VfsEventPublisher publisher;

    public VfsEventBridge() {
        this.publisher = VfsEventPublisher.getInstance();
    }

    @Override
    public void serverStarted(EIServerEvent event) throws JDSException {
        log.info("VFS Server started (ESB bridge): {}", event.getSource());
        publisher.publishServerEvent(
            "VfsEventBridge",
            ServerAction.STARTED,
            event.getSource() != null ? event.getSource().toString() : "unknown",
            "Server started via ESB bridge"
        );
    }

    @Override
    public void serverStopped(EIServerEvent event) throws JDSException {
        log.info("VFS Server stopped (ESB bridge): {}", event.getSource());
        publisher.publishServerEvent(
            "VfsEventBridge",
            ServerAction.STOPPED,
            event.getSource() != null ? event.getSource().toString() : "unknown",
            "Server stopped via ESB bridge"
        );
    }
}
