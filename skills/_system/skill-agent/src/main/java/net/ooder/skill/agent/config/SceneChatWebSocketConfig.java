package net.ooder.skill.agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class SceneChatWebSocketConfig implements WebSocketConfigurer {

    private final SceneChatWebSocketHandler sceneChatHandler;

    public SceneChatWebSocketConfig(SceneChatWebSocketHandler sceneChatHandler) {
        this.sceneChatHandler = sceneChatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sceneChatHandler, "/ws/scene-groups/{sceneGroupId}/chat")
            .setAllowedOrigins("*");
    }
}
