package net.ooder.skill.msg.push.service;

import net.ooder.skill.msg.push.channel.MessageChannel;
import net.ooder.skill.msg.push.dto.PushRequestDTO;
import net.ooder.skill.msg.push.dto.PushResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsgPushService {
    
    private static final Logger log = LoggerFactory.getLogger(MsgPushService.class);
    
    @Autowired
    private List<MessageChannel> channels;
    
    private Map<String, MessageChannel> channelMap;
    
    private Map<String, MessageChannel> getChannelMap() {
        if (channelMap == null) {
            channelMap = new HashMap<>();
            for (MessageChannel channel : channels) {
                channelMap.put(channel.getChannelName(), channel);
            }
        }
        return channelMap;
    }
    
    public List<String> getAvailableChannels() {
        List<String> available = new ArrayList<>();
        for (MessageChannel channel : channels) {
            if (channel.isAvailable()) {
                available.add(channel.getChannelName());
            }
        }
        return available;
    }
    
    public PushResultDTO send(PushRequestDTO request) {
        log.info("[send] Sending message via channel: {}", request.getChannel());
        
        MessageChannel channel = getChannelMap().get(request.getChannel());
        if (channel == null) {
            return PushResultDTO.fail(request.getChannel(), "不支持的渠道: " + request.getChannel());
        }
        
        if (!channel.isAvailable()) {
            return PushResultDTO.fail(request.getChannel(), "渠道未启用: " + request.getChannel());
        }
        
        return channel.send(request);
    }
    
    public PushResultDTO sendToUser(String channelName, String userId, String title, String content) {
        log.info("[sendToUser] Sending to user {} via {}", userId, channelName);
        
        MessageChannel channel = getChannelMap().get(channelName);
        if (channel == null) {
            return PushResultDTO.fail(channelName, "不支持的渠道: " + channelName);
        }
        
        return channel.sendToUser(userId, title, content);
    }
    
    public PushResultDTO sendToGroup(String channelName, String groupId, String title, String content) {
        log.info("[sendToGroup] Sending to group {} via {}", groupId, channelName);
        
        MessageChannel channel = getChannelMap().get(channelName);
        if (channel == null) {
            return PushResultDTO.fail(channelName, "不支持的渠道: " + channelName);
        }
        
        return channel.sendToGroup(groupId, title, content);
    }
    
    public List<PushResultDTO> broadcast(PushRequestDTO request, List<String> channelNames) {
        log.info("[broadcast] Broadcasting to {} channels", channelNames.size());
        
        List<PushResultDTO> results = new ArrayList<>();
        for (String channelName : channelNames) {
            MessageChannel channel = getChannelMap().get(channelName);
            if (channel != null && channel.isAvailable()) {
                PushRequestDTO channelRequest = new PushRequestDTO();
                channelRequest.setChannel(channelName);
                channelRequest.setMsgType(request.getMsgType());
                channelRequest.setTitle(request.getTitle());
                channelRequest.setContent(request.getContent());
                channelRequest.setReceiver(request.getReceiver());
                channelRequest.setReceiverId(request.getReceiverId());
                results.add(channel.send(channelRequest));
            } else {
                results.add(PushResultDTO.fail(channelName, "渠道不可用"));
            }
        }
        return results;
    }
}
