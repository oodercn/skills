package net.ooder.skill.msg.push.channel;

import net.ooder.skill.msg.push.dto.PushRequestDTO;
import net.ooder.skill.msg.push.dto.PushResultDTO;

public interface MessageChannel {
    
    String getChannelName();
    
    boolean isAvailable();
    
    PushResultDTO send(PushRequestDTO request);
    
    PushResultDTO sendToUser(String userId, String title, String content);
    
    PushResultDTO sendToGroup(String groupId, String title, String content);
}
