package net.ooder.spi.messaging;

import net.ooder.spi.messaging.model.UnifiedMessage;

public interface MessageStreamHandler {

    void onMessage(UnifiedMessage message);

    void onChunk(String chunk);

    void onComplete();

    void onError(Throwable error);
}
