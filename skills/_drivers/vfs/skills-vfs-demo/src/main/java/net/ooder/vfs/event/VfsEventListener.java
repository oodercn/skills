package net.ooder.vfs.event;

@FunctionalInterface
public interface VfsEventListener {
    void onEvent(Object event);
}
