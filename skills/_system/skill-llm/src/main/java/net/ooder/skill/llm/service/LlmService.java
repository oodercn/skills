package net.ooder.skill.llm.service;

import net.ooder.skill.llm.model.ChatRequest;
import net.ooder.skill.llm.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface LlmService {

    ChatResponse chat(ChatRequest request);

    SseEmitter streamChat(ChatRequest request);

    List<Map<String, Object>> listAvailableModels();
}
