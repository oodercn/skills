package net.ooder.skill.agent.cli.service;

import net.ooder.skill.agent.cli.dto.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class NaturalLanguageParser {

    public ParsedCommandDTO parse(String text, String platform) {
        log.info("Parsing text: {} for platform: {}", text, platform);
        
        String command = "unknown";
        List<String> args = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        String intent = "unknown";
        Map<String, Object> entities = new HashMap<>();
        
        if (text.contains("发送消息") || text.contains("发消息")) {
            command = "send-message";
            intent = "send_message";
            entities.put("action", "send");
            entities.put("type", "message");
            
            if (text.contains("给")) {
                int start = text.indexOf("给") + 1;
                int end = text.indexOf(" ", start);
                if (end == -1) end = text.length();
                String recipient = text.substring(start, end).trim();
                entities.put("recipient", recipient);
                args.add(recipient);
            }
            
            if (text.contains("说") || text.contains("内容")) {
                int start = Math.max(
                    text.indexOf("说") != -1 ? text.indexOf("说") + 1 : 0,
                    text.indexOf("内容") != -1 ? text.indexOf("内容") + 2 : 0
                );
                if (start > 0) {
                    String content = text.substring(start).trim();
                    entities.put("content", content);
                    options.put("content", content);
                }
            }
        } else if (text.contains("创建文档") || text.contains("新建文档")) {
            command = "create-doc";
            intent = "create_document";
            entities.put("action", "create");
            entities.put("type", "document");
        } else if (text.contains("查询日程") || text.contains("查看日程")) {
            command = "query-calendar";
            intent = "query_calendar";
            entities.put("action", "query");
            entities.put("type", "calendar");
        } else if (text.contains("创建待办") || text.contains("添加待办")) {
            command = "create-todo";
            intent = "create_todo";
            entities.put("action", "create");
            entities.put("type", "todo");
        } else if (text.contains("同步") && text.contains("组织")) {
            command = "sync-org";
            intent = "sync_organization";
            entities.put("action", "sync");
            entities.put("type", "organization");
        }
        
        return ParsedCommandDTO.builder()
                .originalText(text)
                .platform(platform)
                .command(command)
                .args(args)
                .options(options)
                .confidence(0.85)
                .intent(intent)
                .entities(entities)
                .alternatives(new ArrayList<>())
                .build();
    }
}
